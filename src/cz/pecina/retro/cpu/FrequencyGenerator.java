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
  
  // CPU scheduler
  private final CPUScheduler scheduler = Parameters.cpu.getCPUScheduler();

  // output pin
  private class OutPin extends IOPin {
    @Override
    public int query() {
      return output;
    }
  }
  
  /**
   * Gets the output pin.
   *
   * @return the output
   */
  public IOPin getOutPin() {
    return outPin;
  }

  /**
   * Resets the device.
   */
  @Override
  public void reset() {
    scheduler.removeAllScheduledEvents(this);
    counter = 0;
    output = 0;
    outPin.notifyChangeNode();
    scheduler.addScheduledEvent(
      this,
      Parameters.systemClockSource.getSystemClock() + offPeriod,
      0);
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
      "New frequency generator started, name: %s, off: %d, on: %d",
      name,
      offPeriod, onPeriod));
    assert offPeriod > 0L;
    assert onPeriod > 0L;

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
	  return String.valueOf(FrequencyGenerator.this.scheduler.getRemainingTime(
	    FrequencyGenerator.this,
	    Parameters.systemClockSource.getSystemClock()));
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
    scheduler.removeAllScheduledEvents(this);
    scheduler.addScheduledEvent(
      this,
      Parameters.systemClockSource.getSystemClock() + counter,
      0);
    outPin.notifyChangeNode();
    log.fine("Post-unmarshal on frequency counter completed");
  }

  // for description see CPUEventOwner
  @Override
  public void performScheduledEvent(final int parameter) {
    output = 1 - output;
    outPin.notifyChangeNode();
    scheduler.addScheduledEvent(
      this,
      Parameters.systemClockSource.getSystemClock() +
        ((output == 0) ? offPeriod : onPeriod),
      0);
    log.finer("Event performed, output is now: " + output);
  }
}
