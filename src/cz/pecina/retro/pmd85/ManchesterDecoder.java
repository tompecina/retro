/* ManchesterDecoder.java
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

import cz.pecina.retro.common.Parameters;

import cz.pecina.retro.cpu.IONode;
import cz.pecina.retro.cpu.IOPin;
import cz.pecina.retro.cpu.Device;
import cz.pecina.retro.cpu.Register;
import cz.pecina.retro.cpu.CPUEventOwner;
import cz.pecina.retro.cpu.CPUScheduler;

/**
 * Decoder of Manchester-encoded serial data from the tape recorder.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class ManchesterDecoder extends Device implements CPUEventOwner {

  // dynamic logger, per device
  private Logger log;

  // period of 3/4 tape recorder clock, in CPU clock units
  private static final long SAMPLING_DELAY = 1280;

  // period of 1/2 tape recorder clock, in CPU clock units
  private static final long SAMPLING_DELAY_RESET = 853;

  // number of 0's before inverting signal
  private static final int ZERO_LIMIT = 25;

  // input level
  private int input;

  // clock level
  private int clock;

  // data level
  private int data;

  // counter used by unmarshalling
  private long counter;

  // trigger counter, for resetting on wrong polarity
  private int trigger;
  
  // input pin
  private InPin inPin = new InPin();
  
  // clock pin
  private ClockPin clockPin = new ClockPin();
  
  // data pin
  private DataPin dataPin = new DataPin();
  
  /**
   * Gets the input pin.
   * @return the input pin
   */
  public IOPin getInPin() {
    return inPin;
  }

  /**
   * Gets the clock pin.
   * @return the clock pin
   */
  public IOPin getClockPin() {
    return clockPin;
  }

  /**
   * Gets the data pin.
   *
   * @return the data pin
   */
  public IOPin getDataPin() {
    return dataPin;
  }

  /**
   * Resets the device.
   */
  @Override
  public void reset() {
    CPUScheduler.removeAllEvents(this);
    counter = 0;
    trigger = 0;
    clock = 0;
    clockPin.notifyChangeNode();
    data = 1;
    dataPin.notifyChangeNode();
    log.finer("Manchester decoder reset");
  }

  /**
   * Main constructor.
   *
   * @param name the name of the device
   */
  public ManchesterDecoder(final String name) {
    super(name);
    log = Logger.getLogger(getClass().getName() + "." + name);
    log.fine("New Manchester decoder creation started: " + name);
    this.name = name;

    add(new Register("INPUT") {
	@Override
	public String getValue() {
	  return String.valueOf(input);
	}
	@Override
	public void processValue(final String value) {
	  input = Integer.parseInt(value);
	  log.finer("Input set to: " + input);
	}
      });
    add(new Register("CLOCK") {
	@Override
	public String getValue() {
	  return String.valueOf(clock);
	}
	@Override
	public void processValue(final String value) {
	  clock = Integer.parseInt(value);
	  log.finer("Clock set to: " + clock);
	}
      });
    add(new Register("DATA") {
	@Override
	public String getValue() {
	  return String.valueOf(data);
	}
	@Override
	public void processValue(final String value) {
	  data = Integer.parseInt(value);
	  log.finer("Data set to: " + data);
	}
      });
    add(new Register("COUNTER") {
	@Override
	public String getValue() {
	  return String.valueOf(
	    CPUScheduler.getRemainingTime(ManchesterDecoder.this));
	}
	@Override
	public void processValue(final String value) {
	  counter = Long.parseLong(value);
	  log.finer("Counter set to: " + counter);
	}
      });
    add(new Register("TRIGGER") {
	@Override
	public String getValue() {
	  return String.valueOf(trigger);
	}
	@Override
	public void processValue(final String value) {
	  trigger = Integer.parseInt(value);
	  log.finer("Trigger set to: " + trigger);
	}
      });

    reset();
    log.fine("Manchester decoder creation completed: " + name);
  }

  // for description see Device
  @Override
  public void postUnmarshal() {
    CPUScheduler.removeAllEvents(this);
    CPUScheduler.addEvent(
      this,
      Parameters.systemClockSource.getSystemClock() + counter,
      0);
    clockPin.notifyChangeNode();
    dataPin.notifyChangeNode();
    log.fine("Post-unmarshal on Manchester decoder completed");
  }


  // for description see CPUEventOwner
  @Override
  public void performEvent(final int parameter, final long delay) {
    clock = 0;
    clockPin.notifyChangeNode();
    data = input;
    dataPin.notifyChangeNode();
    if (data == 1) {
      trigger = 0;
    }
    if (trigger == ZERO_LIMIT) {
      log.finer("Too many 0's, inverting");
      trigger = 0;
      CPUScheduler.addEvent(
        this,
	Parameters.systemClockSource.getSystemClock() +	SAMPLING_DELAY_RESET,
	0);
    }
    trigger++;
    log.finest("Event performed, clock is now: " + clock + ", data: " + data);
  }

  // the input pin
  private class InPin extends IOPin {
    @Override
    public void notifyChange() {
      final int newInput = IONode.normalize(queryNode());
      if (newInput != input) {
	log.finest("Change on input pin, new level: " + newInput);
	input = newInput;
	if (CPUScheduler.getRemainingTime(ManchesterDecoder.this) == -1) {
	  log.finest("Pulse ended, edge processed");
	  clock = 1;
	  clockPin.notifyChangeNode();
	  CPUScheduler.addEvent(
	    ManchesterDecoder.this,
	    Parameters.systemClockSource.getSystemClock() + SAMPLING_DELAY,
	    0);
	} else {
	  log.finest("Pulse not yet ended, edge ignored");
	}
      }
    }
  }

  // the clock pin
  private class ClockPin extends IOPin {
    @Override
    public int query() {
      log.finest("Clock: " + clock);
      return clock;
    }
  }

  // the data pin
  private class DataPin extends IOPin {
    @Override
    public int query() {
      log.finest("Data: " + data);
      return data;
    }
  }
}
