/* Intel8253.java
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
 * Intel 8253 Programmable Interval Timer.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Intel8253 extends Device implements IOElement {

  // dynamic logger, per device
  private Logger log;

  // Read/Load values
  private static final int RL_LATCH = 0;
  private static final int RL_LSB = 1;
  private static final int RL_MSB = 2;
  private static final int RL_LSB_MSB = 3;
      
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
  public Intel8253(final String name, final boolean[] types) {
    super(name);
    log = Logger.getLogger(getClass().getName() + "." + name);
    log.fine("New Intel 8253 creation started, name: " + name);
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
    log.fine("New Intel 8253 creation completed, name: " + name);
  }

  // for description see Device
  @Override
  public void postUnmarshal() {
    log.fine("Post-unmarshal on 8253 completed");
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

    // the current count
    private int count;

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

    // get LSB
    private int lsb(final int data) {
      return (bcd ? (data % 100) : (data & 0xff));
    }
    
    // get MSB
    private int msb(final int data) {
      return (bcd ? (data / 100) : (data >> 8));
    }
    
    // reset counter
    public void reset() {
      CPUScheduler.removeAllScheduledEvents(this);
      clockPin.reset();
      gatePin.reset();
      outPin.reset();
      latched = false;
      log.finer("Counter reset");
    }

    // write one byte to the counter
    public void write(final int data) {
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
      if (reading) {
	reading = false;
	data = msb(count);
      } else {
	switch (rl) {
	  case 2:
	    reading = true;
	  case 0:
	    data = lsb(count);
	    break;
	  case 1:
	    data = msb(count);
	    break;
	}
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
   * @return        the clock pin of counter {@code number}
   */
  public IOPin getClockPin(final int number) {
    return counters[number].clockPin;
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
      log.finer("Attempting to read status register, but 8253 has none");
      return 0xff;
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
