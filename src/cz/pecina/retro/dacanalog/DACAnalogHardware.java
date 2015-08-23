/* DACAnalogHardware.java
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

package cz.pecina.retro.dacanalog;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.awt.Color;

import javax.swing.Timer;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import cz.pecina.retro.cpu.IOElement;

import cz.pecina.retro.common.Parameters;
import cz.pecina.retro.common.Util;

import cz.pecina.retro.gui.GUI;
import cz.pecina.retro.gui.Needle;

/**
 * DAC with analog voltmeter hardware.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class DACAnalogHardware implements IOElement {

  // static logger
  private static final Logger log =
    Logger.getLogger(DACAnalogHardware.class.getName());

  // voltage readout period in msec
  private static final int READOUT_PERIOD = 25;

  // voltage conversion factor
  private static final double VOLTS_PER_STEP = 5.0 / 0xff;

  // nedle angle conversion factor
  private static final double DEGREES_PER_VOLT = 90.0 / 5.0;

  // needle movement coefficients
  private static final double TORQUE_COEFFICIENT = 0.2;
  private static final double DAMPING_COEFFICIENT = 0.5;

  // base port
  private int basePort;

  // needle properties
  private static final int WIDTH = 135;
  private static final int HEIGHT = 72;
  private static final double CENTER_X = 67.0;
  private static final double CENTER_Y = 78.0;
  private static final double LENGTH = 62.5;
  private static final double THICKNESS = 0.75;
  private static final double RATIO = 0.5;
  private static final Color INNER_COLOR= new Color(0xcccccc);
  private static final Color OUTER_COLOR= new Color(0xff4400);
  private static final double INITIAL_ANGLE = -45.0;
	
  // analog needle
  private Needle needle;

  // accumulated voltage readout
  private long accumulator;

  // currently accumulated value
  private int currentValue;

  // start of accumulation
  private long start;

  // time of last change
  private long lastChange;

  // readout timer
  private Timer timer;

  // current voltage
  private double voltage;

  /**
   * Creates the DAC analog hardware object.
   *
   * @param basePort the base port
   */
  public DACAnalogHardware(final int basePort) {
    assert (basePort >= 0) && (basePort < 0x100);
    this.basePort = basePort;
    needle = new Needle(WIDTH,
			HEIGHT,
			CENTER_X,
			CENTER_Y,
			LENGTH,
			THICKNESS,
			RATIO,
			INNER_COLOR,
			OUTER_COLOR,
			INITIAL_ANGLE);
    timer = new Timer(READOUT_PERIOD, new TimerListener());
    start = Parameters.systemClockSource.getSystemClock();
    timer.start();
    connect();
    log.fine("New DAC analog hardware created");
  }

  /**
   * Gets the volmeter needle.
   *
   * @return the Needle object
   */
  public Needle getNeedle() {
    return needle;
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
    } else {
      log.finer("DAC hardware port reconnection not required");
    }
  }

  /**
   * Deactivates hardware.
   */
  public void deactivate() {
    disconnect();
    timer.stop();
    GUI.removeResizeable(needle);
    log.fine("DAC analog hardware deactivated");
  }

  // for description see IOElement
  @Override
  public synchronized void portOutput(final int port, final int data) {
    long time = Parameters.systemClockSource.getSystemClock();
    accumulator += currentValue * (time - lastChange);
    currentValue = data;
    lastChange = time;
  }

  // process timer tick
  private synchronized void timerTick() {
    final long time = Parameters.systemClockSource.getSystemClock();
    assert time >= start;
    if (time != start) {
      accumulator += currentValue * (time - lastChange);
      voltage =
	((double)accumulator / (double)(time - start)) * VOLTS_PER_STEP;
      accumulator = 0;
      start = lastChange = time;
    }
    if (log.isLoggable(Level.FINEST)) {
      log.finest(String.format("DAC timer tick: voltage: %g, accumulator: %d," +
        " currentValue: %d, start: %d, lastChange: %d",
	voltage,
	accumulator,
	currentValue,
	start,
	lastChange));
    }
  }

  // for description see IOElement
  @Override
  public int portInput(final int port) {
    return 0xff;
  }

  // timer listener
  private class TimerListener implements ActionListener {
    private double velocity;

    @Override
    public void actionPerformed(final ActionEvent event) {
      timerTick();
      final double angle = needle.getAngle();
      velocity += (TORQUE_COEFFICIENT *
	(-45.0 + (voltage * DEGREES_PER_VOLT) - angle)) -
	(DAMPING_COEFFICIENT * velocity);
      needle.setAngle(Util.limit(angle + velocity, -45.0, 45.0));
      log.finest("Voltmeter angle: " + angle + ", velocity: " + velocity);
    }
  }
}
