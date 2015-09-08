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

  /**
   * The three counters, Counter 0-3.
   */
  protected final Counter[] counters = new Counter[3];

  // for description see Device
  @Override
  public void reset() {
    log.finer(String.format("%s: reset", name));
    for (int i = 0; i < 3; i++) {
      counters[i].reset();
    }
  }

  // counter number, for loop
  private int number;
  
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
    
    for (number = 0; number < 3; number++) {

      add(new Register("COUNTER" + number + "_TYPE") {
	  // for description see Register
	  @Override
	  public String getValue() {
	    return String.valueOf(counters[number].direct ? 1 : 0);
	  }
	  // for description see Register
	  @Override
	  public void processValue(final String value) {
	    counters[number].direct = (Integer.parseInt(value) == 1);
	    log.finer("Type for counter " + number +
		      " set to: " + counters[number].direct);
	  }
        });

      add(new Register("COUNTER" + number + "_MODE") {
	  // for description see Register
	  @Override
	  public String getValue() {
	    return String.valueOf(counters[number].mode);
	  }
	  // for description see Register
	  @Override
	  public void processValue(final String value) {
	    counters[number].mode = Integer.parseInt(value);
	    log.finer("Mode for counter " + number +
		      " set to: " + counters[number].mode);
	  }
        });

      add(new Register("COUNTER" + number + "_CE") {
	  // for description see Register
	  @Override
	  public String getValue() {
	    return String.valueOf(counters[number].countingElement);
	  }
	  // for description see Register
	  @Override
	  public void processValue(final String value) {
	    counters[number].countingElement = Integer.parseInt(value);
	    log.finer("Counting Element for counter " + number +
		      " set to: " + counters[number].countingElement);
	  }
        });
    }

    for (int i = 0; i < 3; i++) {
      counters[i] = new Counter();
      log.finer("Setting up counter: " + number + ", connection type: " +
		(types[i] ? "CPU clock" : "normal"));
      counters[i].direct = types[i];
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

    /**
     * Type of clock connection of the counter. {@code true} if
     * directly connected to the system CPU, {@code false} if
     * the clock pin is used.
     */
    public boolean direct;

    /**
     * {@code true} if reading is in progress. i.e., LSB read, MSB
     * will follow.
     * <p>
     * Flags {@code reading} and {@code writing} are probably implemented
     * as one flip-flop in 8253; in 8254 they are separate.  We may
     * safely ignore the difference as this is only observable when an
     * invalid sequence is written to 8253, and the datasheet does not
     * guarantee any particular response in that case.
     */
    protected boolean reading;

    /**
     * {@code true} if writing is in progress. i.e., LSB written, MSB
     * will follow.
     * <p>
     * Flags {@code reading} and {@code writing} are probably implemented
     * as one flip-flop in 8253; in 8254 they are separate.  We may
     * safely ignore the difference as this is only observable when an
     * invalid sequence is written to 8253, and the datasheet does not
     * guarantee any particular response in that case.
     */
    protected boolean writing;

    /**
     * The current/last known state of the Counting Element.
     */
    protected int countingElement;

    /**
     * The current state of the Counter Register.
     */
    protected int counterRegister;

    /**
     * The new value to be written to the Counter Register.
     */
    protected int newCounterRegister;

    /**
     * The value of the (counter) Output Latch.
     */
    protected int outputLatch;
    
    /**
     * {@code true} if the (counter) Output Latch contains data.
     */
    protected boolean outputLatched;

    /**
     * The value of the Status Latch.
     */
    protected int statusLatch;
    
    /**
     * {@code true} if the Status Latch contains data.
     */
    protected boolean statusLatched;

    /**
     * Gate pin level detection flag.  For normal connection, the gate pin
     * level cannot be used for this purpose as the gate is sampled on every
     * rising edge of the clock.  For direct connection, this is a mere copy
     * of the gate pin level.
     */
    protected boolean gate;

    // gate pin trigger detection flags
    protected boolean trigger, triggered;

    /**
     * The clock pulse detection flag.  {@code true} after a rising and before
     * a falling edge on the clock pin.
     */
    protected boolean pulse;

    /**
     * The BCD flag copied from the Control Word, {@code false}
     * - binary mode, {@code true} - BCD mode.
     */
    public boolean bcd;

    /**
     * The base value of the counter calculated from the BCD flag.
     * Values: {@code 0x10000} if BCD is {@code false}, {@code 10000}
     * if BCD is {@code true}.
     */
    protected int base;

    /**
     * The Read/Write register copied from the Control Word.
     * <p>
     * Meaning: {@code 1} - only LSB, {@code 2} - only MSB,
     * {@code 3} - first LSB, then MSB.
     */
    public int rw;

    /**
     * The counter Mode copied from the Control Word, {@code 0-5}.
     */
    public int mode;

    /**
     * The Null Count flag used in the Status.  {@code true} if the value in
     * the Counter Register has not been written to the Counting Element
     * (please refer to the device datasheet for a more detailed description).
     */
    protected boolean nullCount;

    // if true counter will be loaded on the next clock pulse
    protected boolean loaded;

    // if counter has been reset
    protected boolean reset;

    /**
     * The total accrued delay accrued by the scheduler.
     */
    protected long delay;
    
    /**
     * The clock pin object.
     */
    public ClockPin clockPin = new ClockPin();

    /**
     * The gate pin object.
     */
    public GatePin gatePin = new GatePin();

    /**
     * The output pin object.
     */
    public OutPin outPin = new OutPin();

    /**
     * Gets the LSB (Least Significant Byte).
     *
     * @param  data the input data
     * @return      the LSB (Least Significant Byte) of {@code data}
     */
    protected int lsb(final int data) {
      return (bcd ? (data % 100) : (data & 0xff));
    }
    
    /**
     * Gets the MSB (Most Significant Byte).
     *
     * @param  data the input data
     * @return      the MSB (Most Significant Byte) of {@code data}
     */
    protected int msb(final int data) {
      return (bcd ? ((data / 100) % 100) : ((data >> 8) & 0xff));
    }
    
    /**
     * Resets the counter.
     */
    public void reset() {
      CPUScheduler.removeAllScheduledEvents(this);
      clockPin.reset();
      gatePin.reset();
      outPin.reset();
      outputLatched = statusLatched = reading = writing =
	trigger = triggered = pulse = loaded = false;
      nullCount = reset = true;
      gate = gatePin.level;
      newCounterRegister = 0;
      delay = 0;
      base = (bcd ? 10000 : 0x10000);
      log.finer("Counter reset");
    }
    
    /**
     * Gets the current count of the Counting Element.  For direct connection,
     * this is merely a guess which may be off the actual value up the maximum
     * instruction duration (including any applicable interrupt procedure)
     * minus one.
     *
     * @return the current count of the Counting Element
     */
    protected int getCount() {
      if (direct) {
	if (reset && loaded) {
	  final long remains = CPUScheduler.getRemainingTime(this);
	  if (remains >= 0) {
	    switch (mode) {
	      
	      case 0:
		if (gate) {
		  log.finest("Getting counter state, remains: " + remains);
		  return ((int)remains) % base;
		} else {
		  return countingElement % base;
		}
		
	      case 1:
		log.finest("Getting counter state, remains: " + remains);
		return ((int)remains) % base;
		
	      case 4:
	      case 5:
		
	      case 2:
		if (gate) {
		  return (outPin.level ? ((((int)remains) + 1) % base) : 1);
		} else {
		  return counterRegister % base;
		}
		
	      case 3:
		if (gate) {
		  return ((((int)remains) / 2) + 1) % base;
		} else {
		  return counterRegister % base;
		}
	    }
	  } else {
	    return countingElement % base;
	  }
	} else {
	  return 0;
	}
      } else {
	return countingElement % base;
      }
      return 0;
    }

    /**
     * Latches the current counter value.
     */
    public void latchCounter() {
      if (!outputLatched) {
	outputLatch = getCount();
	outputLatched = true;
	log.finer(String.format("Counter latched, value: 0x%02x", outputLatch));
      } else {
	log.finer("Duplicate output latch command ignored");
      }
    }

    /**
     * Latches the current status.
     */
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

    /**
     * Stops the counter for direct connection type (ignored for normal connection).
     */
    protected void stop() {
      if (direct) {
	final long remains = CPUScheduler.getRemainingTime(this);
	if (remains >= 0) {
	  CPUScheduler.removeAllScheduledEvents(this);
	  switch (mode) {
	    
	    case 0:
	    case 1:
	      countingElement = (int)remains;
	      break;
	    
	    case 2:
	      countingElement = (int)remains + 1;
	      break;
	  }
	}	  
	log.finest("Counting suspended, remains: " + remains);
      }
    }

    /**
     * Writes one byte to the counter.
     *
     * @param data the byte to be written
     */
    public void write(final int data) {
      switch (rw) {
	case 1:
	  newCounterRegister = data;
	  break;
	case 2:
	  newCounterRegister = data * (bcd ? 100 : 0x100); 
	  break;
	case 3:
	  if (writing) {
	    newCounterRegister += data * (bcd ? 100 : 0x100);
	  } else {
	    newCounterRegister = data;
	  }
	  writing = !writing;
	  break;
      }
      if (writing) {
	log.finest("First byte written to counter");
	if (mode == 0) {
	  out(false);
	  stop();
	}
      } else {
	log.finest("Last byte written to counter");
	counterRegister = newCounterRegister;
	if (counterRegister == 0) {
	  counterRegister = base;
	} else if (((mode == 2) || (mode == 3)) && (counterRegister == 1)) {
	  counterRegister = base + 1;
	}
	nullCount = true;
	if (direct) {
	  switch (mode) {
	    case 0:
	      {
		long delta = delay;
		if ((counterRegister - delta) < 1) {
		  delta = counterRegister - 1;
		}
		delay -= delta;
		countingElement = counterRegister - ((int)delta);
		nullCount = false;
		loaded = true;
		if (gate) {
		  CPUScheduler.removeAllScheduledEvents(this);
		  CPUScheduler.addScheduledEvent(this, countingElement + 1, 0);
		  log.finest("Counter started, remains: " + (countingElement + 1));
		}
	      }
	      break;

	    case 1:
	      loaded = true;
	      break;

	    case 2:
	      {
		nullCount = false;
		loaded = true;
		if (gate) {
		  long delta = delay;
		  if ((counterRegister - delta - 1) < 1) {
		    delta = counterRegister - 2;
		  }
		  delay -= delta;
		  countingElement = counterRegister - ((int)delta);
		  CPUScheduler.removeAllScheduledEvents(this);
		  CPUScheduler.addScheduledEvent(this, countingElement, 0);
		  log.finest("Counter started, remains: " + countingElement);
		}
	      }
	      break;
	  }
	} else if ((mode == 2) || (mode == 3)) {
	  if (reset) {
	    loaded = true;
	    reset = false;
	  }
	} else {
	  loaded = true;
	}
      }
    }

    /**
     * Reads one byte from the counter.
     *
     * @return the byte read
     */
    public int read() {
      if (statusLatched) {
	log.finer(String.format("Outputting (latched) status: 0x%02x", statusLatch));
	statusLatched = false;
	return statusLatch;
      }
      int data = 0, value;
      if (outputLatched) {
	value = outputLatch;
	log.finest("Outputting latched counter value: " + value);
	if (reading || (rw != 3)) {
	  outputLatched = false;
	}
      } else {
	value = getCount();
	log.finest("Outputting immediate counter value: " + value);
      }
      switch (rw) {
	case 1:
	  data = lsb(value);
	  break;
	case 2:
	  data = msb(value);
	  break;
	case 3:
	  data = (reading ? msb(value) : lsb(value));
	  reading = !reading;
	  break;
      }
      log.finer(String.format("Counter read, value: 0x%02x", data));
      return data;
    }

    // for description see CPUEventOwner
    @Override
    public void performScheduledEvent(final int parameter, final long newDelay) {
      assert direct;
      if (direct) {
	delay += newDelay;
	switch (mode) {

	  case 0:
	    {
	      long delta = delay;
	      if ((base - 1 - delta) < 1) {
		delta = base - 2;
	      }
	      delay -= delta;
	      countingElement = base - 1 - ((int)delta);
	      if (gate && !writing) {
		out(true);
		CPUScheduler.addScheduledEvent(
	          this,
		  Math.max(countingElement + 1, 1),
		  0);
	      }
	    }
	    break;

	  case 1:
	    {
	      long delta = delay;
	      if ((base - 1 - delta) < 1) {
		delta = base - 2;
	      }
	      delay -= delta;
	      countingElement = base - 1 - ((int)delta);
	      out(true);
	      CPUScheduler.addScheduledEvent(
	        this,
		Math.max(countingElement + 1, 1),
		0);
	    }
	    break;

	  case 2:
	    {
	      if (outPin.level) {
		out(false);
		CPUScheduler.addScheduledEvent(this, 1, 0);
	      } else {
		long delta = delay;
		if ((counterRegister - 1 - delta) < 1) {
		  delta = counterRegister - 2;
		}
		delay -= delta;
		countingElement = counterRegister - 1 - ((int)delta);
		out(true);
		CPUScheduler.addScheduledEvent(
	          this,
		  Math.max(countingElement, 1),
		  0);
	      }
	    }
	    break;
	}
      }
    }

    /**
     * Method called on every rising edge of the clock (only for normal
     * connection).
     */
    protected void risingClock() {
      log.finest("Rising clock edge detected");
      assert !direct;
      if (!direct) {
	gatePin.notifyChange();
	gate = gatePin.level;
	log.finest("Gate level: " + gate);
	if (trigger) {
	  triggered = true;
	  trigger = false;
	}
      }
    }

    /**
     * Sets the level on the output pin.
     *
     * @param level the new level
     */
    protected void out(final boolean level) {
      outPin.level = level;
      outPin.notifyChangeNode();
      log.finest("Output level: " + level);
    }

    /**
     * Loads the Counting Element from the Counter Register
     * and modifies the Control Logic flags accordingly.
     */    
    protected void load() {
      countingElement = counterRegister;
      nullCount = false;
      reset = false;
      triggered = false;
    }

    /**
     * Decrements and reloads the Counting Element in Modes 0 &amp; 1.
     */
    protected void reload01() {
      countingElement--;
      if (countingElement == 0) {
	out(true);
      } else if (countingElement < 0) {
	countingElement = base - 1;
      }
    }
    
    /**
     * Decrements and reloads the Counting Element in Modes 4 &amp; 5.
     */
    protected void reload45() {
      countingElement--;
      if (countingElement == 0) {
	out(false);
      } else if (countingElement < 0) {
	out(true);
	countingElement = base - 1;
      }
    }
    
    /**
     * Method called on every clock pulse (only for normal connection).
     */
    protected void clockPulse() {
      log.finest("Clock pulse detected");
      assert !direct;
      if (!direct) {
	switch (mode) {

	  case 0:
	    if (loaded) {
	      load();
	      loaded = false;
	    } else if (gate && !writing && !reset) {
	      reload01();
	    }
	    break;

	  case 1:
	    if (triggered) {
	      out(false);
	      load();
	    } else if (loaded && !reset) {
	      reload01();
	    }
	    break;

	  case 2:
	    if (loaded || triggered) {
	      out(true);
	      load();
	      loaded = false;
	    } else if (gate && !reset) {
	      countingElement--;
	      if (countingElement == 1) {
		out(false);
	      } else if (countingElement == 0) {
		out(true);
		load();
	      }
	    }
	    break;

	  case 3:
	    if (loaded || triggered) {
	      out(true);
	      load();
	      loaded = false;
	    } else if (gate && !reset) {
	      if (((counterRegister % 2) == 1) &&
		  (countingElement == counterRegister)) {
		countingElement += (outPin.level ? 1 : -1);
	      }
	      countingElement -= 2;
	      if (countingElement == 0) {
		out(!outPin.level);
		load();
	      }
	    }
	    break;

	  case 4:
	    if (loaded) {
	      load();
	      loaded = false;
	    } else if (gate && !reset) {
	      reload45();
	    }
	    break;

	  case 5:
	    if (triggered) {
	      load();
	    } else if (loaded && !reset) {
	      reload45();
	    }
	    break;
	}
      }
    }

    /**
     * The clock pin.
     */
    protected class ClockPin extends IOPin {

      /**
       * The current level of the pin.
       */
      public boolean level;

      /**
       * Resets the pin.
       */
      public void reset() {
	level = (IONode.normalize(queryNode()) == 1);
      }

      // for description see IOPin
      @Override
      public void notifyChange() {
	if (!direct) {
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

    /**
     * The gate pin.
     */
    protected class GatePin extends IOPin {

      /**
       * The current/last known level of the pin.
       */
      public boolean level;

      /**
       * Resets the pin.
       */
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
	  if (direct) {
	    gate = level;
	    switch (mode) {
	      
	      case 0:
		if (level && reset && loaded) {
		  if (!writing) {
		    CPUScheduler.removeAllScheduledEvents(Counter.this);
		    CPUScheduler.addScheduledEvent(
		      Counter.this,
		      Math.max(countingElement, 1),
		      0);
		    log.finest("Counting resumed");
		  }
		} else {
		  stop();
		}
		break;

	      case 1:
		if (level && reset && loaded) {
		  out(false);
		  CPUScheduler.removeAllScheduledEvents(Counter.this);
		  CPUScheduler.addScheduledEvent(
		    Counter.this,
		    counterRegister + 1,
		    0);
		  nullCount = false;
		  log.finest("Counting triggered");
		}
		break;

	      case 2:
		if (level && reset && loaded) {
		  CPUScheduler.removeAllScheduledEvents(Counter.this);
		  CPUScheduler.addScheduledEvent(
		   Counter.this,
		   Math.max(counterRegister, 1),
		   0);
		  log.finest("Counting resumed");
		} else {
		  stop();
		}
		break;
	    }
	  } else if (!level && ((mode == 2) || (mode == 3))) {
	    out(true);
	  }
	}
      }      
    }

    /**
     * The output pin.
     */
    protected class OutPin extends IOPin {

      /**
       * The current level of the pin.
       */
      public boolean level;
      
      /**
       * Resets the pin.
       */
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
    if (counter.direct) {
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
   * Gets the output pin.
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

	  log.fine(String.format("Read-back command: 0x%02x", data));
	  int p = data;
	  for (int i = 0; i < 3; i++) {
	    p >>= 1;
	    if ((p & 1) == 1) {
	      if (((data >> 5) & 1) == 0) {
		counters[i].latchCounter();
	      }
	      if (((data >> 4) & 1) == 0) {
		counters[i].latchStatus();
	      }	      
	    }
	  }
	}
      } else {

	final Counter counter = counters[number];

	int rw = (data >> 4) & 0x03;

	if (rw == 0) {

	  log.finer("Counter " + number + " latched");
	  counter.latchCounter();
	  
	} else {
	  
	  counter.rw = rw;

	  counter.mode = (data >> 1) & 0x07;
	  if (counter.mode > 5) {
	    counter.mode -= 4;
	  }
      
	  counter.bcd = ((data & 1) == 1);

	  counter.reset();
	
	  log.fine("Counter " + number + " programmed: RW: " +
		   (new String[] {"LSB", "MSB", "LSB/MSB"})[rw - 1] +
		   ", mode: " + counter.mode + ", bcd: " + counter.bcd);
	}
      }
    } else {

      log.finer(String.format("Counter %d write: 0x%02x", number, data));
      counters[port & 0x03].write(data);
    }
  }
}
