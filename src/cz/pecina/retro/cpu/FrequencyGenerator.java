/* FrequencyGenerator.java
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
 * Fixed frequency generator.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class FrequencyGenerator extends Device implements CPUEventOwner {

  // dynamic logger, per device
  private Logger log;

  // timing parameters
  private long offPeriod, onPeriod;

  // counter used by unmarshalling
  private long counter;
  
  // output flag
  private int output;
  
  // output pin
  private final OutPin outPin = new OutPin();
  
  // time of next event
  private long nextEvent;

  // output pin
  private class OutPin extends IOPin {

    // for description see IOPin
    @Override
    public int query() {
      return output;
    }
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
   * Resets the device.
   */
  @Override
  public void reset() {
    CPUScheduler.removeAllEvents(this);
    counter = 0;
    output = 0;
    outPin.notifyChangeNode();
    nextEvent = Parameters.systemClockSource.getSystemClock() + offPeriod;
    CPUScheduler.addEvent(this, nextEvent);
    log.finer("Frequency generator reset");
  }

  /**
   * Main constructor.
   *
   * @param name      device name
   * @param offPeriod off period in clock cycles
   * @param onPeriod  off period in clock cycles
   */
  public FrequencyGenerator(final String name,
			    final long offPeriod,
			    final long onPeriod) {
    super(name);
    log = Logger.getLogger(getClass().getName() + "." + name);
    log.fine(String.format(
      "New frequency generator creation started, name: %s, off: %d, on: %d",
      name,
      offPeriod, onPeriod));
    assert offPeriod > 0;
    assert onPeriod > 0;

    this.offPeriod = offPeriod;
    this.onPeriod = onPeriod;
    
    add(new Register("OFF") {
    	@Override
    	public String getValue() {
    	  return String.valueOf(offPeriod);
    	}
    	@Override
    	public void processValue(final String value) {
    	  FrequencyGenerator.this.offPeriod = Long.parseLong(value);
    	  log.finer("Off period set to: " + FrequencyGenerator.this.offPeriod);
    	}
      });
    add(new Register("ON") {
    	@Override
    	public String getValue() {
    	  return String.valueOf(onPeriod);
    	}
    	@Override
    	public void processValue(final String value) {
    	  FrequencyGenerator.this.onPeriod = Long.parseLong(value);
    	  log.finer("On period set to: " + FrequencyGenerator.this.onPeriod);
    	}
      });
    add(new Register("COUNTER") {
    	@Override
    	public String getValue() {
    	  return String.valueOf(CPUScheduler.getRemainingTime(
	    FrequencyGenerator.this));
    	}
    	@Override
    	public void processValue(final String value) {
    	  counter = Long.parseLong(value);
    	  log.finer("Counter set to: " + counter);
    	}
      });
    add(new Register("OUTPUT") {
    	@Override
    	public String getValue() {
    	  return String.valueOf(output);
    	}
    	@Override
    	public void processValue(final String value) {
    	  output = Integer.parseInt(value);
    	  log.finer("Output set to: " + output);
    	}
      });

    reset();
    log.fine("New frequency counter creation completed, name: " + name);
  }

  // for description see Device
  @Override
  public void postUnmarshal() {
    CPUScheduler.removeAllEvents(this);
    CPUScheduler.addEvent(
      this,
      Parameters.systemClockSource.getSystemClock() + counter);
    outPin.notifyChangeNode();
    log.fine("Post-unmarshal on frequency counter completed");
  }

  // for description see CPUEventOwner
  @Override
  public void performEvent(final int parameter, final long delay) {
    output = 1 - output;
    outPin.notifyChangeNode();
    nextEvent += ((output == 0) ? offPeriod : onPeriod);
    CPUScheduler.addEvent(this, nextEvent);
    if (log.isLoggable(Level.FINEST)) {
      log.finest(
        "Event performed at " + Parameters.systemClockSource.getSystemClock() +
	", parameter: " + parameter + ", output is now: " + output);
      log.finest("New event scheduled for: " + nextEvent);
    }
  }
}
