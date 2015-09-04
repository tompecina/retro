/* Intel8254.java
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

/**
 * Intel 8254 Programmable Interval Timer.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Intel8254 extends Device implements IOElement {

  // dynamic logger, per device
  private Logger log;

  // counters
  private final Counter[] counters = new Counter[3];

  // for description see Device
  @Override
  public void reset() {
    log.finer(String.format("%s: reset", name));
    for (int i = 0; i < 3; i++) {
      counters[i].reset();
    }
  }

  /**
   * Main constructor.
   *
   * @param name  device name
   * @param types  array of counter connection types: if {@code true},
   *               the counter's clock is connected to system clock and
   *               the regular clock pin is disabled, if {@code false},
   *               the clock pin is enabled
   */
  public Intel8254(final String name, final boolean[] types) {
    super(name);
    log = Logger.getLogger(getClass().getName() + "." + name);
    log.fine("New Intel 8254 creation started, name: " + name);
    assert types.length == 3;
    
    // add(new Register("MODE_A") {
    // 	@Override
    // 	public String getValue() {
    // 	  return String.valueOf(modeA);
    // 	}
    // 	@Override
    // 	public void processValue(final String value) {
    // 	  modeA = Integer.parseInt(value);
    // 	  log.finer("Mode A set to: " + modeA);
    // 	}
    //   });

    for (int i = 0; i < 3; i++) {
      counters[i] = new Counter();
      log.finer("Setting up counter: " + i + ", connection type: " +
		(types[i] ? "CPU clock" : "normal"));
      counters[i].type = types[i];
    }
    reset();
    log.fine("New Intel 8254 creation completed, name: " + name);
  }

  // for description see Device
  @Override
  public void postUnmarshal() {
    log.fine("Post-unmarshal on 8254 completed");
  }

  // counter
  private class Counter implements CPUEventOwner {

    // clock connection type
    //  false - the clock pin
    //  true - the CPU clock
    public boolean type;

    // state of the counter
    private int state;

    // state of the latch register
    private boolean latched;

    // true if reading in progress. i.e., LSB read, MSB will follow
    private boolean reading;

    // true if writing in progress. i.e., LSB written, MSB will follow
    private boolean writing;

    // Note: These two flags are probably implemented as one FF in 8253;
    //       in 8254 they are separate.  We will ignore this difference
    //       as this only may be observed if an erroneous sequences is
    //       sent to 8253.

    // the current count
    private int count;

    // the current preset
    private int preset;

    // the new preset
    private int newPreset;

    // the latch register
    private int latch;
    
    // binary/BCD mode
    //  false - binary
    //  true - BCD
    public boolean bcd;

    // RL register - 1
    //  0 - only LSB
    //  1 - only MSB
    //  2 - LSB, then MSB
    public int rl;

    // counter mode, 0-5
    public int mode;

    // the clock pin
    public ClockPin clockPin = new ClockPin();

    // the gate pin
    public GatePin gatePin = new GatePin();

    // the out pin
    public OutPin outPin = new OutPin();

    // set LSB
    private void setLsb(final int data) {
      newPreset = (bcd ?
		   (newPreset - (newPreset % 100) + data) :
		   ((newPreset & 0xff00) | data));
    }

    // set MSB
    private void setMsb(final int data) {
      newPreset = (bcd ?
		   ((newPreset / 100) + (data * 100)) :
		   ((newPreset & 0xff) | (data << 8)));
    }

    // get LSB
    private int getLsb(final int data) {
      return (bcd ? (data % 100) : (data & 0xff));
    }
    
    // get MSB
    private int getMsb(final int data) {
      return (bcd ? (data / 100) : (data >> 8));
    }
    
    // reset counter
    public void reset() {
      CPUScheduler.removeAllScheduledEvents(this);
      clockPin.reset();
      gatePin.reset();
      outPin.reset();
      latched = reading = writing = false;
      newPreset = 0;
      log.finer("Counter reset");
    }

    // write one byte to the counter
    public void write(final int data) {
      switch (rl) {
	case 0:
	  setLsb(data);
	  break;
	case 1:
	  setMsb(data);
	  break;
	case 2:
	  if (writing) {
	    setMsb(data);
	  } else {
	    setLsb(data);
	  }
	  writing = !writing;
	  break;
      }
      if (!writing) {
	preset = newPreset;
	if (preset == 0) {
	  preset = (bcd ? 10000 : 0x10000);
	} else if (((mode == 2) || (mode == 3)) && (preset == 1)) {
	  preset = (bcd ? 10001 : 0x10001);
	  
	  // Note: Preset == 1 is illegal in Mode 1 so we are free
	  //       to do this.
	  
	}
      }
    }

    // read one byte from the counter
    public int read() {
      int data = 0, value;
      if (latched) {
	value = latch;
	if (reading || (rl != 2)) {
	  latched = false;
	}
      } else {
	value = count;
      }
      switch (rl) {
	case 0:
	  data = getLsb(count);
	  break;
	case 1:
	  data = getMsb(count);
	  break;
	case 2:
	  data = (reading ? getMsb(count) : getLsb(count));
	  reading = !reading;
	  break;
      }
      log.finer(String.format("Counter read, value: 0x%02x", data));
      return data;
    }

    // latch the counter value
    public void latch() {
      latch = count;
      latched = true;
      log.finer(String.format("Counter latched, value: 0x%02x", latch));
    }

    // the clock pin
    private class ClockPin extends IOPin {

      private boolean level;

      // reset pin
      public void reset() {
	level = (IONode.normalize(queryNode()) == 1);
      }

      // for description see IOPin
      @Override
      public void notifyChange() {
	if (!type) {
	  final boolean newLevel = (IONode.normalize(queryNode()) == 1);
	  if (newLevel != level) {
	    level = newLevel;
	    if (level) {
	      risingClock();
	    } else {
	      fallingClock();
	    }
	  }
	}
      }
    }

    // the gate pin
    private class GatePin extends IOPin {

      private boolean level;

      // reset pin
      public void reset() {
	level = (IONode.normalize(queryNode()) == 1);
      }
    }

    // the out pin
    private class OutPin extends IOPin {

      private boolean output;
      
      // reset pin
      public void reset() {
	output = (mode != 0);
	notifyChangeNode();
      }

      // for description see IOPin
      @Override
      public int query() {
	return (output ? 1 : 0);
      }
    }

    // for description see CPUEventOwner
    @Override
    public void performScheduledEvent(final int parameter) {
    }
  }

  /**
   * Gets the clock pin.
   *
   * @param  number the counter number
   * @return        the clock pin of counter {@code number} or
   *                {@code null} if the counter is connected to
   *                the system clock
   */
  public IOPin getClockPin(final int number) {
    final Counter counter = counters[number];
    if (counter.type) {
      log.fine("Trying to obtain clock pin on counter " + number +
	       ", which is connected to system clock");
      return null;
    } else {
      return counter.clockPin;
    }
  }
  
  /**
   * Gets the gate pin.
   *
   * @param  number the counter number
   * @return        the gate pin of counter {@code number}
   */
  public IOPin getGatePin(final int number) {
    return counters[number].gatePin;
  }
  
  /**
   * Gets the out pin.
   *
   * @param  number the counter number
   * @return        the out pin of counter {@code number}
   */
  public IOPin getOutPin(final int number) {
    return counters[number].outPin;
  }
  
  // for description see IOElement
  @Override
  public int portInput(final int port) {

    final int number = port & 0x03;
  
    if (number < 3) {
      final int data = counters[number].read();
      log.finer(String.format("Counter %d write: 0x%02x", number, data));
      return data;
    } else {
      return status;
    }
  }

  // for description see IOElement
  @Override
  public void portOutput(final int port, int data) {
    assert (port >= 0) && (port < 0x100);
    assert (data >= 0) && (data < 0x100);

    int number = port & 0x03;
    
    if (number == 0x03) {  // control port
      
      number = data >> 6;
      if (number == 3) {  // illegal counter number, ignore
	log.fine("Illegal counter number in Control Word ignored");
	return;
      }
      final Counter counter = counters[number];

      int rl = (data >> 4) & 0x03;

      if (rl == 0) {

	log.finer("Counter " + number + " latched");
	counter.latch();

      } else {

	counter.reset();
	
	counter.rl = --rl;

	counter.mode = (data >> 1) & 0x07;
	if (counter.mode > 5) {
	  counter.mode -= 4;
	}
      
	counter.bcd = ((data & 1) == 1);

	log.fine("Counter " + number + " programmed: RL: " +
		 (new String[] {"LSB", "MSB", "LSB/MSB"})[rl] +
		 ", mode: " + counter.mode + ", bcd: " + counter.bcd);
      }
    } else {

      counters[port & 0x03].write(data);
      log.finer(String.format("Counter %d write: 0x%02x", number, data));
    }
  }
}
