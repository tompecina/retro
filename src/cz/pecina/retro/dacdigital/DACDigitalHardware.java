/* DACDigitalHardware.java
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

package cz.pecina.retro.dacdigital;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.Timer;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import cz.pecina.retro.common.Parameters;
import cz.pecina.retro.common.Util;

import cz.pecina.retro.cpu.IOElement;

import cz.pecina.retro.gui.GUI;
import cz.pecina.retro.gui.ESD;
import cz.pecina.retro.gui.HexESD;
import cz.pecina.retro.gui.SignESD;
import cz.pecina.retro.gui.LED;
import cz.pecina.retro.gui.GenericButton;

/**
 * DAC with digital voltmeter hardware.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class DACDigitalHardware implements IOElement {

  // static logger
  private static final Logger log =
    Logger.getLogger(DACDigitalHardware.class.getName());

  /**
   * The number of display elements.
   */
  public static final int NUMBER_DISPLAY_ELEMENTS = 5;

  // measurement period in msec
  private static final int MEASUREMENT_PERiOD = 200;

  // voltage conversion factor
  private static final double VOLTS_PER_STEP = 5.0 / 0xff;

  // RC constant of AC input filter
  private static final double RC_CONSTANT = 1.0;

  // correction constant so that sine wave yields true RMS
  private static final double CORRECTION_CONSTANT = 1234043.0;

  // base port
  private int basePort;

  // accumulated voltage readout
  private long accumulator;

  // currently accumulated value
  private int currentValue;

  // currently accumulated value for AC measurements
  private double initVoltage;

  // accumulator for AC measurements
  private double altAccumulator;

  // start of accumulation
  private long start;

  // time of last change
  private long lastChange;

  // current voltage
  private double voltage;

  // measurement timer
  private Timer timer;

  // display elements, right to left
  private final ESD[] display = new ESD[NUMBER_DISPLAY_ELEMENTS];

  // power on LED
  private LED powerOnLED;

  // layout of buttons
  private DACDigitalButtonsLayout layout;

  /**
   * Creates the DAC digital hardware object.
   *
   * @param basePort the base port
   */
  public DACDigitalHardware(final int basePort) {
    assert (basePort >= 0) && (basePort < 0x100);
    this.basePort = basePort;

    // set up display
    for (int i = 0; i < (NUMBER_DISPLAY_ELEMENTS - 1); i++)
      display[i] = new HexESD("big", "lime");
    display[NUMBER_DISPLAY_ELEMENTS - 1] = new SignESD("big", "lime");

    // set up power on LED
    powerOnLED = new LED("small", "lime");
    powerOnLED.setState(true);

    // set up buttons
    layout = new DACDigitalButtonsLayout();
    getButton(DACDigitalButtonsLayout.BUTTON_POSITION_AUTO).addChangeListener(
      new ChangeListener() {
	@Override
	public void stateChanged(final ChangeEvent event) {
	  if (((GenericButton)event.getSource()).isPressed()) {
	    startMeasurement();
	    timer.setRepeats(true);
	    timer.start();
	  } else {
	    timer.setRepeats(false);
	    timer.stop();
	  }
	}
      });
    getButton(DACDigitalButtonsLayout.BUTTON_POSITION_MAN).addChangeListener(
      new ChangeListener() {
	@Override
	public void stateChanged(final ChangeEvent event) {
	  if (!DACDigitalHardware.this.getButton(
	    DACDigitalButtonsLayout.BUTTON_POSITION_AUTO).isPressed() &&
	    ((GenericButton)event.getSource()).isPressed()) {
	    startMeasurement();
	    timer.setRepeats(false);
	    timer.start();
	  }
	}
      });
    getButton(DACDigitalButtonsLayout.BUTTON_POSITION_V).addChangeListener(
      new ChangeListener() {
	@Override
	public void stateChanged(final ChangeEvent event) {
	  if (((GenericButton)event.getSource()).isPressed()) {
	    DACDigitalHardware.this.getButton(
	      DACDigitalButtonsLayout.BUTTON_POSITION_KOHM).setPressed(false);
	    DACDigitalHardware.this.getButton(
	      DACDigitalButtonsLayout.BUTTON_POSITION_MA).setPressed(false);
	  }
	}
      });
    getButton(DACDigitalButtonsLayout.BUTTON_POSITION_KOHM).addChangeListener(
      new ChangeListener() {
	@Override
	public void stateChanged(final ChangeEvent event) {
	  if (((GenericButton)event.getSource()).isPressed()) {
	    DACDigitalHardware.this.getButton(
	      DACDigitalButtonsLayout.BUTTON_POSITION_V).setPressed(false);
	    DACDigitalHardware.this.getButton(
	      DACDigitalButtonsLayout.BUTTON_POSITION_MA).setPressed(false);
	  }
	}
      });
    getButton(DACDigitalButtonsLayout.BUTTON_POSITION_MA).addChangeListener(
      new ChangeListener() {
	@Override
	public void stateChanged(final ChangeEvent event) {
	  if (((GenericButton)event.getSource()).isPressed()) {
	    DACDigitalHardware.this.getButton(
	      DACDigitalButtonsLayout.BUTTON_POSITION_V).setPressed(false);
	    DACDigitalHardware.this.getButton(
	      DACDigitalButtonsLayout.BUTTON_POSITION_KOHM).setPressed(false);
	  }
	}
      });
    getButton(DACDigitalButtonsLayout.BUTTON_POSITION_0_2).addChangeListener(
      new ChangeListener() {
	@Override
	public void stateChanged(final ChangeEvent event) {
	  if (((GenericButton)event.getSource()).isPressed()) {
	    DACDigitalHardware.this.getButton(
	      DACDigitalButtonsLayout.BUTTON_POSITION_2).setPressed(false);
	    DACDigitalHardware.this.getButton(
	      DACDigitalButtonsLayout.BUTTON_POSITION_20).setPressed(false);
	    DACDigitalHardware.this.getButton(
	      DACDigitalButtonsLayout.BUTTON_POSITION_200).setPressed(false);
	    DACDigitalHardware.this.getButton(
	      DACDigitalButtonsLayout.BUTTON_POSITION_2000).setPressed(false);
	  }
	}
      });
    getButton(DACDigitalButtonsLayout.BUTTON_POSITION_2).addChangeListener(
      new ChangeListener() {
	@Override
	public void stateChanged(final ChangeEvent event) {
	  if (((GenericButton)event.getSource()).isPressed()) {
	    DACDigitalHardware.this.getButton(
	      DACDigitalButtonsLayout.BUTTON_POSITION_0_2).setPressed(false);
	    DACDigitalHardware.this.getButton(
	      DACDigitalButtonsLayout.BUTTON_POSITION_20).setPressed(false);
	    DACDigitalHardware.this.getButton(
	      DACDigitalButtonsLayout.BUTTON_POSITION_200).setPressed(false);
	    DACDigitalHardware.this.getButton(
	      DACDigitalButtonsLayout.BUTTON_POSITION_2000).setPressed(false);
	  }
	}
      });
    getButton(DACDigitalButtonsLayout.BUTTON_POSITION_20).addChangeListener(
      new ChangeListener() {
	@Override
	public void stateChanged(final ChangeEvent event) {
	  if (((GenericButton)event.getSource()).isPressed()) {
	    DACDigitalHardware.this.getButton(
	      DACDigitalButtonsLayout.BUTTON_POSITION_0_2).setPressed(false);
	    DACDigitalHardware.this.getButton(
	      DACDigitalButtonsLayout.BUTTON_POSITION_2).setPressed(false);
	    DACDigitalHardware.this.getButton(
	      DACDigitalButtonsLayout.BUTTON_POSITION_200).setPressed(false);
	    DACDigitalHardware.this.getButton(
	      DACDigitalButtonsLayout.BUTTON_POSITION_2000).setPressed(false);
	  }
	}
      });
    getButton(DACDigitalButtonsLayout.BUTTON_POSITION_200).addChangeListener(
      new ChangeListener() {
	@Override
	public void stateChanged(final ChangeEvent event) {
	  if (((GenericButton)event.getSource()).isPressed()) {
	    DACDigitalHardware.this.getButton(
	      DACDigitalButtonsLayout.BUTTON_POSITION_0_2).setPressed(false);
	    DACDigitalHardware.this.getButton(
	      DACDigitalButtonsLayout.BUTTON_POSITION_2).setPressed(false);
	    DACDigitalHardware.this.getButton(
	      DACDigitalButtonsLayout.BUTTON_POSITION_20).setPressed(false);
	    DACDigitalHardware.this.getButton(
	      DACDigitalButtonsLayout.BUTTON_POSITION_2000).setPressed(false);
	  }
	}
      });
    getButton(DACDigitalButtonsLayout.BUTTON_POSITION_2000).addChangeListener(
      new ChangeListener() {
	@Override
	public void stateChanged(final ChangeEvent event) {
	  if (((GenericButton)event.getSource()).isPressed()) {
	    DACDigitalHardware.this.getButton(
	      DACDigitalButtonsLayout.BUTTON_POSITION_0_2).setPressed(false);
	    DACDigitalHardware.this.getButton(
	      DACDigitalButtonsLayout.BUTTON_POSITION_2).setPressed(false);
	    DACDigitalHardware.this.getButton(
	      DACDigitalButtonsLayout.BUTTON_POSITION_20).setPressed(false);
	    DACDigitalHardware.this.getButton(
	      DACDigitalButtonsLayout.BUTTON_POSITION_200).setPressed(false);
	  }
	}
      });
    getButton(DACDigitalButtonsLayout.BUTTON_POSITION_V).setPressed(true);
    getButton(DACDigitalButtonsLayout.BUTTON_POSITION_20).setPressed(true);

    // set up timer event
    timer = new Timer(MEASUREMENT_PERiOD, new TimerListener());

    // connect to port
    connect();

    // set up initial state of controls
    getButton(DACDigitalButtonsLayout.BUTTON_POSITION_AUTO).setPressed(true);

    log.fine("New DAC digital hardware created");
  }

  /**
   * Gets the voltmeter display element.
   *
   * @param  n the position of the element, zero-based, right to left
   * @return the {@code n}-th display element
   */
  public ESD getElement(int n) {
    assert (n >= 0) && (n < NUMBER_DISPLAY_ELEMENTS);
    return display[n];
  }

  /**
   * Gets the voltmeter power on LED.
   *
   * @return the power on LED
   */
  public LED getPowerOnLED() {
    return powerOnLED;
  }

  /**
   * Gets the voltmeter button.
   *
   * @param  n the position of the button in the layout
   * @return the {@code n}-th button
   */
  public GenericButton getButton(int n) {
    assert (n >= 0) && (n < DACDigitalButtonsLayout.NUMBER_BUTTONS);
    return layout.getButton(n);
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
   * Reconnects DAC to a new base port
   *
   * @param basePort the new base port
   */
  public void reconnect(final int basePort) {
    assert (basePort >= 0) && (basePort < 0x100);
    if (this.basePort != basePort) {
      disconnect();
      this.basePort = basePort;
      connect();
      log.fine(String.format("DAC hardware reconnected to new base port %02x",
			     basePort));
    } else
      log.finer("DAC hardware port reconnection not required");
  }

  /**
   * Deactivates hardware.
   */
  public void deactivate() {
    disconnect();
    timer.stop();
    for (ESD element: display)
      GUI.removeResizeable(element);
    log.fine("DAC digital hardware deactivated");
  }

  // for description see IOElement
  @Override
  public synchronized void portOutput(final int port, final int data) {
    long time = Parameters.systemClockSource.getSystemClock();
    int q = 0;
    if (getButton(DACDigitalButtonsLayout.BUTTON_POSITION_AC).isPressed()) {
      final double t = (double)(time - lastChange) / Parameters.CPUFrequency;
      final double e = Math.exp(-t / RC_CONSTANT);
      final double c = initVoltage * e;
      altAccumulator += Math.abs(initVoltage * (e - 1.0) * RC_CONSTANT);
      initVoltage = c + ((data - currentValue) * VOLTS_PER_STEP);
    } else {
      accumulator += currentValue * (time - lastChange);
    }
    currentValue = data;
    lastChange = time;
  }

  // start measurement
  private synchronized void startMeasurement() {
    accumulator = 0;
    altAccumulator = 0.0;
    start = lastChange = Parameters.systemClockSource.getSystemClock();
    log.finest("Voltage measurement started");
  }

  // end measurement
  private synchronized void endMeasurement() {
    final long time = Parameters.systemClockSource.getSystemClock();
    assert time >= start;
    if (getButton(DACDigitalButtonsLayout.BUTTON_POSITION_AC).isPressed()) {
      final double t = (double)(time - lastChange) / Parameters.CPUFrequency;
      final double e = Math.exp(-t / RC_CONSTANT);
      final double c = initVoltage * e;
      altAccumulator += Math.abs(initVoltage * (e - 1.0) * RC_CONSTANT);
      initVoltage = c;
      if (time != start) {
	voltage =
	  (altAccumulator / (double)(time - start)) * CORRECTION_CONSTANT;
      }
      altAccumulator = 0.0;
    } else {
      accumulator += currentValue * (time - lastChange);
      if (time != start) {
	voltage =
	  ((double)accumulator / (double)(time - start)) * VOLTS_PER_STEP;
      }
      accumulator = 0;
    }
    start = lastChange = time;
    if (log.isLoggable(Level.FINEST))
      log.finest(String.format("Voltage measurement ended: voltage: %g," +
        " accumulator: %d, currentValue: %d, start: %d, lastChange: %d",
	voltage,
	accumulator,
	currentValue,
	start,
	lastChange));
  }

  // display measurement result
  private void display() {
    int leadingMask = 0;
    for (int i = 0; i < (NUMBER_DISPLAY_ELEMENTS - 1); i++)
      ((HexESD)display[i]).setDecimalPoint(false);
    double coeff;
    if (getButton(DACDigitalButtonsLayout.BUTTON_POSITION_0_2).isPressed()) {
      coeff = 100000.0;
    } else if
	(getButton(DACDigitalButtonsLayout.BUTTON_POSITION_2).isPressed()) {
      coeff = 10000.0;
      leadingMask = 0x10;
    } else if
	(getButton(DACDigitalButtonsLayout.BUTTON_POSITION_20).isPressed()) {
      coeff = 1000.0;
      ((HexESD)display[3]).setDecimalPoint(true);
    } else if
	(getButton(DACDigitalButtonsLayout.BUTTON_POSITION_200).isPressed()) {
      coeff = 100.0;
      ((HexESD)display[2]).setDecimalPoint(true);
    } else {
      coeff = 10.0;
      ((HexESD)display[1]).setDecimalPoint(true);
    }
    int value = getButton(
      DACDigitalButtonsLayout.BUTTON_POSITION_MA).isPressed() ?
      0 :
      (int)Math.round(voltage * coeff);
    if (value < 0) {
      value = -value;
      leadingMask = 0x04;
    }
    if (getButton(DACDigitalButtonsLayout.BUTTON_POSITION_KOHM).isPressed() ||
	(value > 19999)) {
      display[NUMBER_DISPLAY_ELEMENTS - 1].setState(0x03);
      for (int i = 0; i < (NUMBER_DISPLAY_ELEMENTS - 1); i++) {
	display[i].setState(-1);
	((HexESD)display[i]).setDecimalPoint(false);
      }
    } else {
      display[NUMBER_DISPLAY_ELEMENTS - 1]
	.setState(((value > 9999) ? 0x03 : 0x00) | leadingMask);
      for (int i = 0; i < (NUMBER_DISPLAY_ELEMENTS - 1); i++, value /= 10) {
	display[i].setState(value % 10);
      }
    }	    
  }

  // for description see IOElement
  @Override
  public int portInput(final int port) {
    return 0xff;
  }

  // timer listener
  private class TimerListener implements ActionListener {
    @Override
    public void actionPerformed(final ActionEvent event) {
      endMeasurement();
      display();
      if (DACDigitalHardware.this.getButton(
          DACDigitalButtonsLayout.BUTTON_POSITION_AUTO).isPressed()) {
	startMeasurement();
      }
    }
  }
}
