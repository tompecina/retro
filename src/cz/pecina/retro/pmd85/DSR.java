/* DSR.java
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

package cz.pecina.retro.pmd85;

import java.util.logging.Logger;
import cz.pecina.retro.cpu.IONode;
import cz.pecina.retro.cpu.IOPin;

/**
 * DSR sampling circuit.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class DSR {

  // dynamic logger, per device
  private Logger log;

  // signal level
  private int signal;

  // clock level
  private int level;

  // output level
  private int output;

  // name of the device
  private String name;

  // signal input pin
  private final SignalPin signalPin = new SignalPin();

  // clock input pin
  private final ClockPin clockPin = new ClockPin();

  // output pin
  private OutPin outPin = new OutPin();
  
  /**
   * Gets the device name.
   *
   * @return the name of the device
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the signal pin.
   * @return the signal pin
   */
  public IOPin getSignalPin() {
    return signalPin;
  }

  /**
   * Gets the clock pin.
   * @return the clock pin
   */
  public IOPin getClockPin() {
    return clockPin;
  }

  /**
   * Gets the output pin.
   *
   * @return the output pin
   */
  public IOPin getOutPin() {
    return outPin;
  }

  /**
   * Main constructor.
   *
   * @param name the name of the device
   */
  public DSR(final String name) {
    assert name != null;
    log = Logger.getLogger(getClass().getName() + "." + name);
    log.fine("New DSR creation started: " + name);
    this.name = name;
    log.fine("DSR creation completed: " + name);
  }

  // the signal pin
  private class SignalPin extends IOPin {
  }

  // the clock pin
  private class ClockPin extends IOPin {
    @Override
    public void notifyChange() {
      final int newLevel = IONode.normalize(queryNode());
      if (newLevel != level) {
	level = newLevel;
	if (level == 0) {
	  signal = signalPin.queryNode();
	  output = signal;
	} else {
	  output = 1 - signal;
	}
      }
    }
  }

  // output pin class
  private class OutPin extends IOPin {
    @Override
    public int query() {
      return output;
    }
  }
}
