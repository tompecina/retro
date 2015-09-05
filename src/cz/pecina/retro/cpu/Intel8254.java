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

import cz.pecina.retro.common.Util;

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

  /**
   * The counter class.
   */
  protected class Counter implements CPUEventOwner {

    // clock connection type
    //  false - the clock pin
    //  true - the CPU clock
    public boolean type;

    // state of the counter
    private int state;

    // true if reading in progress. i.e., LSB read, MSB will follow
    private boolean reading;

    // true if writing in progress. i.e., LSB written, MSB will follow
    private boolean writing;

    // Note: These two flags are probably implemented as one FF in 8253;
    //       in 8254 they are separate.  We will ignore this difference
    //       as this only may be observed if an erroneous sequences is
    //       sent to 8253.

    // the current count
    private int countingElement;

    // the current initial count
    private int counterRegister;

    // the new initial count
    private int newCounterRegister;

    // the output latch
    private int outputLatch;
    
    // state of the output latch
    private boolean outputLatched;

    // the status latch
    private int statusLatch;
    
    // state of the status latch
    private boolean statusLatched;

    // gate pin trigger detection flag
    private boolean trigger;

    // clock pulse detection flag
    private boolean pulse;

    // binary/BCD mode
    //  false - binary
    //  true - BCD
    public boolean bcd;

    // counting base according to bcd
    private long base;

    // RW register
    //  1 - only LSB
    //  2 - only MSB
    //  3 - LSB, then MSB
    public int rw;

    // counter mode, 0-5
    public int mode;

    // the null count flag
    private boolean nullCount = true;

    // if true counter will be loaded on the next clock pulse
    private boolean load;

    // state of the counting element
    private boolean counting;

    // the clock pin
    public ClockPin clockPin = new ClockPin();

    // the gate pin
    public GatePin gatePin = new GatePin();

    // the out pin
    public OutPin outPin = new OutPin();

    // set LSB
    private void setLsb(final int data) {
      newCounterRegister =
	(bcd ?
	 (newCounterRegister - (newCounterRegister % 100) + data) :
	 ((newCounterRegister & 0xff00) | data));
    }

    // set MSB
    private void setMsb(final int data) {
      newCounterRegister =
	(bcd ?
	 ((newCounterRegister / 100) + (data * 100)) :
	 ((newCounterRegister & 0xff) | (data << 8)));
    }

    // get LSB
    private int getLsb(final int data) {
      return (bcd ? (data % 100) : (data & 0xff));
    }
    
    // get MSB
    private int getMsb(final int data) {
      return (bcd ? ((data / 100) % 100) : ((data >> 8) & 0xff));
    }
    
    // reset counter
    public void reset() {
      CPUScheduler.removeAllScheduledEvents(this);
      clockPin.reset();
      gatePin.reset();
      outPin.reset();
      outputLatched = statusLatched = reading = writing =
	trigger = pulse = counting = load = false;
      nullCount = true;
      newCounterRegister = 0;
      base = (bcd ? 10000 : 0x10000);
      log.finer("Counter reset");
    }

    // latch the counter value
    public void latchOutput() {
      if (!outputLatched) {
	outputLatch = countingElement;
	outputLatched = true;
	log.finer(String.format("Counter latched, value: 0x%02x", outputLatch));
      } else {
	log.finer("Duplicate output latch command ignored");
      }
    }

    // latch status
    public void latchStatus() {
      if (!statusLatched) {
	statusLatch = (outPin.query() << 7) |
	              ((nullCount ? 1 : 0) << 6) |
	              (rw << 4) |
	              (mode << 1) |
	              (bcd ? 1 : 0);
	statusLatched = true;
	log.finer(String.format("Status latched, value: 0x%02x", statusLatch));
      } else {
	log.finer("Duplicate status latch command ignored");
      }
    }

    // write one byte to the counter
    public void write(final int data) {
      switch (rw) {
	case 1:
	  setLsb(data);
	  break;
	case 2:
	  setMsb(data);
	  break;
	case 3:
	  if (writing) {
	    setMsb(data);
	  } else {
	    setLsb(data);
	  }
	  writing = !writing;
	  break;
      }
      if (!writing) {
	counterRegister = newCounterRegister;
	if (counterRegister == 0) {
	  counterRegister = (bcd ? 10000 : 0x10000);
	} else if (((mode == 2) || (mode == 3)) && (counterRegister == 1)) {
	  counterRegister = (bcd ? 10001 : 0x10001);
	  
	  // Note: counterRegister == 1 is illegal in Mode 1 so we are free
	  //       to do this.
	  
	}
	nullCount = true;
	switch (mode) {
	  case 0:
	    outPin.level = false;
	    outPin.notifyChangeNode();
	    gatePin.notifyChange();
	    if (type) {
	      countingElement = counterRegister;
	      if (gatePin.level) {
		CPUScheduler.removeAllScheduledEvents(this);
		counting = true;
		nullCount = false;
		CPUScheduler.addScheduledEvent(this, countingElement + 1, 0);
	      }
	    } else {
	      load = true;
	    }
	    break;
	}
      } else {
	switch (mode) {
	  case 0:
	    if (type) {
	      final long remains = CPUScheduler.getRemainingTime(this);
	      if (remains != -1) {
		CPUScheduler.removeAllScheduledEvents(this);
		countingElement = (int)remains;
	      }
	    }
	    break;
	}
      }
    }

    // read one byte from the counter
    public int read() {
      if (statusLatched) {
	log.finer(String.format("Outputting (latched) status: %02x", statusLatch));
	statusLatched = false;
	return statusLatch;
      }
      int data = 0, value;
      if (outputLatched) {
	log.finest("Outputting latched counter value");
	value = outputLatch;
	if (reading || (rw != 3)) {
	  outputLatched = false;
	}
      } else {
	log.finest("Outputting immediate counter value");
	value = countingElement;
      }
      switch (rw) {
	case 1:
	  data = getLsb(countingElement);
	  break;
	case 2:
	  data = getMsb(countingElement);
	  break;
	case 3:
	  data = (reading ? getMsb(countingElement) : getLsb(countingElement));
	  reading = !reading;
	  break;
      }
      log.finer(String.format("Counter read, value: 0x%02x", data));
      return data;
    }


    // for description see CPUEventOwner
    @Override
    public void performScheduledEvent(final int parameter, final long delay) {
      assert type;
      if (type) {
	switch (mode) {
	  case 0:
	    if (counting) {
	      countingElement = (int)(Util.modulo(base - delay, base));
	      outPin.level = true;
	      outPin.notifyChangeNode();
	    }
	    break;
	}
      }
    }

    // method called on rising clock edge
    private void risingClock() {
      log.finest("Rising clock edge detected");
      assert !type;
      if (!type) {
	switch (mode) {
	  case 0:
	    if (counting) {
	      gatePin.notifyChange();
	      if (!gatePin.level) {
		counting = false;
	      }
	    }
	    break;
	}
      }
    }
    
    // method called on clock pulse
    private void clockPulse() {
      log.finest("Clock pulse detected");
      assert !type;
      if (!type) {
	switch (mode) {
	  case 0:
	    if (load) {
	      countingElement = counterRegister;
	      load = false;
	    } else {
	      countingElement--;
	      if (countingElement == 0) {
	      } else if (countingElement < 0) {
		countingElement = (int)(base - 1);
	      }
	      break;
	    }
	}
      }
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
	    log.finest("New level on clock pin: " + newLevel);
	    level = newLevel;
	    if (level) {
	      risingClock();
	      pulse = true;
	    } else {
	      if (pulse) {
		clockPulse();
		pulse = false;
	      }
	    }
	  }
	}
      }
    }

    // the gate pin
    private class GatePin extends IOPin {

      public boolean level;

      // reset pin
      public void reset() {
	level = (IONode.normalize(queryNode()) == 1);
      }

      // for description see IOPin
      @Override
      public void notifyChange() {
	final boolean newLevel = (IONode.normalize(queryNode()) == 1);
	if (newLevel != level) {
	  level = newLevel;
	  if (level) {
	    trigger = true;
	  }
	}
      }      
    }

    // the out pin
    private class OutPin extends IOPin {

      public boolean level;
      
      // reset pin
      public void reset() {
	level = (mode != 0);
	notifyChangeNode();
      }

      // for description see IOPin
      @Override
      public int query() {
	return (level ? 1 : 0);
      }
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
      log.finer(String.format("Counter %d read: 0x%02x", number, data));
      return data;
    } else {
      log.finer("Attempt to read from non-existent register");
      return 0xff;
    }
  }

  // for description see IOElement
  @Override
  public void portOutput(final int port, final int data) {
    assert (port >= 0) && (port < 0x100);
    assert (data >= 0) && (data < 0x100);

    int number = port & 0x03;
    
    if (number == 0x03) {  // control port
      
      number = data >> 6;
      if (number == 3) {  // read-back
	if ((data & 1) == 1) {
	  log.fine("Illegal read-back command, ignored");
	} else {
	  int p = data;
	  for (int i = 0; i < 3; i++) {
	    p >>= 1;
	    if ((p & 1) == 1) {
	      if (((data >> 5) & 1) == 1) {
		counters[i].latchOutput();
	      }
	      if (((data >> 4) & 1) == 1) {
		counters[i].latchStatus();
	      }	      
	    }
	  log.fine("Read-back command processed");
	  }
	}
      }
      final Counter counter = counters[number];

      int rw = (data >> 4) & 0x03;

      if (rw == 0) {

	log.finer("Counter " + number + " latched");
	counter.latchOutput();

      } else {

	counter.reset();
	
	counter.rw = rw;

	counter.mode = (data >> 1) & 0x07;
	if (counter.mode > 5) {
	  counter.mode -= 4;
	}
      
	counter.bcd = ((data & 1) == 1);

	log.fine("Counter " + number + " programmed: RW: " +
		 (new String[] {"LSB", "MSB", "LSB/MSB"})[rw - 1] +
		 ", mode: " + counter.mode + ", bcd: " + counter.bcd);
      }
    } else {

      counters[port & 0x03].write(data);
      log.finer(String.format("Counter %d write: 0x%02x", number, data));
    }
  }
}
