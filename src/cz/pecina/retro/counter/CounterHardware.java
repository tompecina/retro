/* CounterHardware.java
 *
 * Copyright (C) 2015, Tomáš Pecina <tomas@pecina.cz>
 *
 * This file is part of cz.pecina.retro, retro 8-bit computer emulators.
 *
 * This application is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This application is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.         
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cz.pecina.retro.counter;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.Timer;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import cz.pecina.retro.cpu.IOElement;
import cz.pecina.retro.cpu.CPUEventOwner;
import cz.pecina.retro.cpu.CPUScheduler;
import cz.pecina.retro.common.Parameters;
import cz.pecina.retro.common.Util;
import cz.pecina.retro.gui.GUI;
import cz.pecina.retro.gui.ESD;
import cz.pecina.retro.gui.HexESD;
import cz.pecina.retro.gui.LED;
import cz.pecina.retro.gui.BlinkLED;
import cz.pecina.retro.gui.GenericButton;
import cz.pecina.retro.gui.SwitchButton;
import cz.pecina.retro.gui.Knob;

/**
 * RFT G-2002.500 frequency counter hardware.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class CounterHardware implements IOElement, CPUEventOwner {

  // static logger
  private static final Logger log =
    Logger.getLogger(CounterHardware.class.getName());

  /**
   * The number of display elements.
   */
  public static final int NUMBER_DISPLAY_ELEMENTS = 7;

  // number of elements blanked on overflow
  private static final int NUMBER_BLANKED_ELEMENTS = 4;

  // maximum value that can be displayed
  private static final int MAX_VALUE =
    (int)Math.pow(10, NUMBER_DISPLAY_ELEMENTS) - 1;

  // gate time knob positions
  private static final int TIME_0_01 = 2;
  private static final int TIME_0_1 = 3;
  private static final int TIME_1 = 4;
  private static final int TIME_10 = 5;
  private static final int ON_OFF = 6;

  // trigger level knob positions
  private static final int TRIGGER_SINE = 11;
  private static final int TRIGGER_POS = 0;
  private static final int TRIGGER_NEG = 1;

  // attenuator knob positions
  private static final int ATTENUATOR_0 = 8;
  private static final int ATTENUATOR_10 = 9;
  private static final int ATTENUATOR_20 = 10;
  private static final int ATTENUATOR_30 = 11;
  private static final int ATTENUATOR_40 = 0;
  private static final int ATTENUATOR_50 = 1;

  // duration of data being displayed, in msec
  private static final int HOLD_PERIOD = 450;

  // duration between display updates in on/off counter mode, in msec
  private static final int UPDATE_PERIOD = 100;

  // gate LED persistence, in msec
  private static final int GATE_LED_DELAY = 200;

  // durations of gate opening times, in sec
  private static final double DURATION_TIME_0_01 = 0.01;
  private static final double DURATION_TIME_0_1 = 0.1;
  private static final double DURATION_TIME_1 = 1.0;
  private static final double DURATION_TIME_10 = 10.0;

  // CPU scheduler
  private final CPUScheduler scheduler = Parameters.cpu.getCPUScheduler();

  // base port
  private int basePort;

  // trigger bit
  private int bit;

  // states
  private enum State {IDLE, MEASURE, HOLD};

  // state
  private State state = State.IDLE;

  // display elements, right to left
  private final HexESD[] display = new HexESD[NUMBER_DISPLAY_ELEMENTS];

  // gate LED
  private BlinkLED gateLED;

  // buttons
  private GenericButton autoButton, manButton;

  // knobs
  private Knob gateTimeKnob, triggerKnob, attenuatorKnob;

  // counter
  private int counter;

  // displayed counter value
  private int buffer;

  // display timers
  private Timer updateTimer, holdTimer;

  // last data byte from CPU
  private int lastData = 0xff;

  /**
   * Creates the frequency counter hardware object.
   *
   * @param basePort the base port
   * @param bit      the trigger bit
   */
  public CounterHardware(final int basePort, final int bit) {
    log.fine("New frequency counter hardware creation started, base port: " +
	     basePort + ", trigger bit: " + bit);
    assert (basePort >= 0) && (basePort < 0x100);
    assert (bit >= -1) && (bit < 8);
    this.basePort = basePort;
    this.bit = bit;

    // set up timer event
    updateTimer = new Timer(UPDATE_PERIOD, new UpdateTimerListener());
    holdTimer = new Timer(HOLD_PERIOD, new HoldTimerListener());
    holdTimer.setRepeats(false);

    // set up display
    for (int i = 0; i < NUMBER_DISPLAY_ELEMENTS; i++)
      display[i] = new HexESD("big", "lime");

    // set up gate LED
    gateLED = new BlinkLED("small", "lime");

    // set up buttons
    autoButton = new SwitchButton("gui/UniversalButton/round-gray-%d-%s.png",
				  -1,
				  null);
    autoButton.addChangeListener(
      new ChangeListener() {
	@Override
	public void stateChanged(final ChangeEvent event) {
	  if (autoButton.isPressed() && (gateTimeKnob.getState() != ON_OFF)) {
	    startMeasurement();
	  }
	}
      });
    manButton = new SwitchButton("gui/UniversalButton/round-gray-%d-%s.png",
				 -1,
				 null);
    manButton.addChangeListener(
      new ChangeListener() {
	@Override
	public void stateChanged(final ChangeEvent event) {
	  if (!autoButton.isPressed()) {
	    if (manButton.isPressed()) {
	      startMeasurement();
	    } else if (gateTimeKnob.getState() == ON_OFF) {
	      state = State.IDLE;
	      buffer = counter;
	      gateLED.setState(false);
	    }
	  }
	}
      });
    
    // set up knobs
    gateTimeKnob = new Knob("round", "gray", 12, 2, 6);
    gateTimeKnob.addChangeListener(
      new ChangeListener() {
	@Override
	public void stateChanged(final ChangeEvent event) {
	  scheduler.removeAllScheduledEvents(CounterHardware.this);
	  holdTimer.stop();
	  state = State.IDLE;
	  buffer = counter = 0;
	  gateLED.setState(false);
	  if ((autoButton.isPressed() &&
	       (gateTimeKnob.getState() != ON_OFF)) ||
	      (manButton.isPressed() &&
	       (gateTimeKnob.getState() == ON_OFF))) {
	    startMeasurement();
	  }
	}
      });
    triggerKnob = new Knob("round", "gray", 12, 11, 1);
    attenuatorKnob = new Knob("round", "gray", 12, 8, 1);

    // set up initial state of controls
    gateTimeKnob.setState(TIME_1);
    triggerKnob.setState(TRIGGER_SINE);
    attenuatorKnob.setState(ATTENUATOR_0);
    autoButton.setPressed(true);

    // connect to port
    connect();

    // start update timer
    updateTimer.start();

    log.fine("New frequency counter hardware created");
  }

  /**
   * Gets the display element.
   *
   * @param  n the position of the element, zero-based, right to left
   * @return the <code>n</code>-th display element
   */
  public ESD getElement(int n) {
    assert (n >= 0) && (n < NUMBER_DISPLAY_ELEMENTS);
    return display[n];
  }

  /**
   * Gets the gate LED.
   *
   * @return the gate LED
   */
  public LED getGateLED() {
    return gateLED;
  }

  /**
   * Gets the AUTO button.
   *
   * @return the AUTO button
   */
  public GenericButton getAutoButton() {
    return autoButton;
  }

  /**
   * Gets the MAN button.
   *
   * @return the MAN button
   */
  public GenericButton getManButton() {
    return manButton;
  }

  /**
   * Gets the time base knob.
   *
   * @return the time base knob
   */
  public Knob getGateTimeKnob() {
    return gateTimeKnob;
  }

  /**
   * Gets the trigger knob.
   *
   * @return the trigger knob
   */
  public Knob getTriggerKnob() {
    return triggerKnob;
  }

  /**
   * Gets the attenuator knob.
   *
   * @return the attentuator knob
   */
  public Knob getAttenuatorKnob() {
    return attenuatorKnob;
  }

  // connect to port
  private void connect() {
    Parameters.cpu.addIOOutput(basePort, this);
    log.fine("Port connected");
  }

  // disconnect from port
  private void disconnect() {
    Parameters.cpu.removeIOOutput(basePort, this);
    log.fine("Port disconnected");
  }

  /**
   * Reconnects the frequency counter to a new base port
   *
   * @param basePort the new base port
   * @param bit      the interrupt bit
   */
  public void reconnect(final int basePort, final int bit) {
    assert (basePort >= 0) && (basePort < 0x100);
    assert (bit >= -1) && (bit < 8);
    this.bit = bit;
    if (this.basePort != basePort) {
      disconnect();
      this.basePort = basePort;
      connect();
      log.fine(String.format(
        "Frequency counter hardware reconnected to new base port %02x, bit %d",
	basePort,
	bit));
    } else
      log.finer("Frequency counter hardware port reconnection not required");
  }

  /**
   * Deactivates hardware.
   */
  public void deactivate() {
    disconnect();
    scheduler.removeAllScheduledEvents(this);
    updateTimer.stop();
    holdTimer.stop();
    for (HexESD element: display) {
      GUI.removeResizeable(element);
    }
    log.fine("Frequency counter hardware deactivated");
  }

  // for description see IOElement
  @Override
  public synchronized void portOutput(final int port, final int data) {
    log.finest(String.format("Data received on port: %02x -> (%02x)%n",
			     data,
			     port));
    if ((state == State.MEASURE) &&
	((bit == -1) || ((lastData & (~data) & (1 << bit)) != 0))) {
      counter++;
    }
    lastData = data;
  }

  // for description see IOElement
  @Override
  public int portInput(final int port) {
    return 0xff;
  }

  // start measurement
  private synchronized void startMeasurement() {
    gateLED.setState(true);
    scheduler.removeAllScheduledEvents(this);
    counter = 0;
    state = State.MEASURE;
    switch (gateTimeKnob.getState()) {
      case TIME_0_01:
	scheduler.addScheduledEvent(
	  this,
	  (int)Math.round(DURATION_TIME_0_01 * Parameters.CPUFrequency),
	  0);
	break;
      case TIME_0_1:
	scheduler.addScheduledEvent(
	  this,
	  (int)Math.round(DURATION_TIME_0_1 * Parameters.CPUFrequency),
	  0);
	break;
      case TIME_1:
	scheduler.addScheduledEvent(
	  this,
	  (int)Math.round(DURATION_TIME_1 * Parameters.CPUFrequency),
	  0);
	break;
      case TIME_10:
	scheduler.addScheduledEvent(
	  this,
	  (int)Math.round(DURATION_TIME_10 * Parameters.CPUFrequency),
	  0);
	break;
    }
    log.finest("Measurement started");
  }

  // for description see CPUScheduler
  @Override
  public void performScheduledEvent(final int parameter) {
    // state = State.IDLE;
    gateLED.setState(GATE_LED_DELAY);
    buffer = counter;
    if (autoButton.isPressed()) {
      state = State.HOLD;
      holdTimer.start();
    }
  }

  // update timer listener
  private class UpdateTimerListener implements ActionListener {
    @Override
    public void actionPerformed(final ActionEvent event) {
      int counter = (gateTimeKnob.getState() == ON_OFF) ?
	CounterHardware.this.counter :
	buffer;
      final boolean overflow =
	(counter > MAX_VALUE) && (state != State.MEASURE);
      int decimalPoint = -1;
      switch (gateTimeKnob.getState()) {
	case TIME_0_01:
	  decimalPoint = 4;
	  break;
	case TIME_0_1:
	  decimalPoint = 5;
	  break;
	case TIME_1:
	  decimalPoint = 3;
	  break;
	case TIME_10:
	  decimalPoint = 4;
	  break;
      }
      for (int i = 0; i < NUMBER_DISPLAY_ELEMENTS; i++, counter /= 10) {
	display[i].setState((overflow &&
	  (i >= (NUMBER_DISPLAY_ELEMENTS - NUMBER_BLANKED_ELEMENTS))) ?
	  -1 :
	  (int)(counter % 10));
	display[i].setDecimalPoint(i == decimalPoint);
      }
    }
  }

  // hold timer listener
  private class HoldTimerListener implements ActionListener {
    @Override
    public void actionPerformed(final ActionEvent event) {
      if (autoButton.isPressed() && (gateTimeKnob.getState() != ON_OFF)) {
	startMeasurement();
      }
    }
  }
}
