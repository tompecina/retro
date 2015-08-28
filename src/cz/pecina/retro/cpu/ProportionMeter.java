/* ProportionMeter.java
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

package cz.pecina.retro.cpu;

import java.util.logging.Logger;
import java.util.logging.Level;

import cz.pecina.retro.common.Parameters;

/**
 * Proportion meter element, measuring the total proportion of the high or
 * high impedance level on the input pin to the total time of measurement.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class ProportionMeter extends Device {

  // dynamic logger, per device
  private Logger log;

  // counters
  private long offCounter, onCounter;

  // current level
  private boolean level;

  // time of last edge
  private long lastEdgeTime;

  // input pin
  private final InPin inPin = new InPin();

  /**
   * Gets the input pin.
   *
   * @return the input pin
   */
  public IOPin getInPin() {
    return inPin;
  }

  /**
   * Main constructor.
   *
   * @param name         the name of the device
   */
  public ProportionMeter(final String name) {
    super(name);
    log = Logger.getLogger(getClass().getName() + "." + name);
    log.fine("New proportion meter creation started: " + name);
    reset();
    log.fine("New proportion meter creation completed: " + name);
  }

  /**
   * Resets the measurement.
   */
  public void reset() {
    final long time = Parameters.systemClockSource.getSystemClock();
    offCounter = onCounter = 0;
    lastEdgeTime = time;
    level = (IONode.normalize(inPin.queryNode()) == 1);
    if (log.isLoggable(Level.FINER)) {
      log.finer("Proportion meter '" + name  + "' reset at: " + time); 
    }
  }

  /**
   * Gets the current proportion and resets the measurement.
   *
   * @return the current proportion, calculated as {@code onTime / totalTime},
   *         or {@code -1.0} if {@code totalTime == 0} 
   */
  public double getProportionAndReset() {
    
    final long time = Parameters.systemClockSource.getSystemClock();
    if (level) {
      onCounter += time - lastEdgeTime;
    } else {
      offCounter += time - lastEdgeTime;
    }
    final long totalTime = offCounter + onCounter;
    if (totalTime == 0) {
      if (log.isLoggable(Level.FINER)) {
	log.finer("Proportion meter '" + name  +
		  "' provided state -1.0 at: " + time);
      }
      return -1.0;
    }
    final double r = (double)onCounter / (double)totalTime;
    if (log.isLoggable(Level.FINER)) {
      log.finer("Proportion meter '" + name  +
		"' provided state: " + r + " at: " + time);
    }
    reset();
    return r;
  }

  // input pin class
  private class InPin extends IOPin {

    // for description see IOPin
    @Override
    public void notifyChange() {
      final boolean newLevel = (IONode.normalize(inPin.queryNode()) == 1);
      if (newLevel != level) {
	level = newLevel;
	final long time = Parameters.systemClockSource.getSystemClock();
	if (level) {
	  onCounter += time - lastEdgeTime;
	} else {
	  offCounter += time - lastEdgeTime;
	}
	lastEdgeTime = time;
	if (log.isLoggable(Level.FINEST)) {
	  log.finer("Change on '" + name  + "', new level: " + level +
		    " at: " + time); 
	}
      }
    }
  }
}
