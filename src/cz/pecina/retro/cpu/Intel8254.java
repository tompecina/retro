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
    log.finer(name + ": reset");
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
    
    for (int i = 0; i < 3; i++) {

      add(new CounterRegister("TYPE", i) {
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

      add(new CounterRegister("MODE", i) {
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
      add(new CounterRegister("READING", i) {
	  // for description see Register
	  @Override
	  public String getValue() {
	    return String.valueOf(counters[number].reading ? 1 : 0);
	  }
	  // for description see Register
	  @Override
	  public void processValue(final String value) {
	    counters[number].reading = (Integer.parseInt(value) == 1);
	    log.finer("Reading for counter " + number +
		      " set to: " + counters[number].reading);
	  }
        });

      add(new CounterRegister("WRITING", i) {
	  // for description see Register
	  @Override
	  public String getValue() {
	    return String.valueOf(counters[number].writing ? 1 : 0);
	  }
	  // for description see Register
	  @Override
	  public void processValue(final String value) {
	    counters[number].writing = (Integer.parseInt(value) == 1);
	    log.finer("Writing for counter " + number +
		      " set to: " + counters[number].writing);
	  }
        });

      add(new CounterRegister("COUNTING_ELEMENT", i) {
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

      add(new CounterRegister("COUNTER_REGISTER", i) {
	  // for description see Register
	  @Override
	  public String getValue() {
	    return String.valueOf(counters[number].counterRegister);
	  }
	  // for description see Register
	  @Override
	  public void processValue(final String value) {
	    counters[number].counterRegister = Integer.parseInt(value);
	    log.finer("Counter Register for counter " + number +
		      " set to: " + counters[number].counterRegister);
	  }
        });

      add(new CounterRegister("NEW_COUNTER_REGISTER", i) {
	  // for description see Register
	  @Override
	  public String getValue() {
	    return String.valueOf(counters[number].newCounterRegister);
	  }
	  // for description see Register
	  @Override
	  public void processValue(final String value) {
	    counters[number].newCounterRegister = Integer.parseInt(value);
	    log.finer("New Counter Register for counter " + number +
		      " set to: " + counters[number].newCounterRegister);
	  }
        });

      add(new CounterRegister("OUTPUT_LATCH", i) {
	  // for description see Register
	  @Override
	  public String getValue() {
	    return String.valueOf(counters[number].outputLatch);
	  }
	  // for description see Register
	  @Override
	  public void processValue(final String value) {
	    counters[number].outputLatch = Integer.parseInt(value);
	    log.finer("Output Latch for counter " + number +
		      " set to: " + counters[number].outputLatch);
	  }
        });

      add(new CounterRegister("OUTPUT_LATCHED", i) {
	  // for description see Register
	  @Override
	  public String getValue() {
	    return String.valueOf(counters[number].outputLatched ? 1 : 0);
	  }
	  // for description see Register
	  @Override
	  public void processValue(final String value) {
	    counters[number].outputLatched = (Integer.parseInt(value) == 1);
	    log.finer("Output Latched for counter " + number +
		      " set to: " + counters[number].outputLatched);
	  }
        });

      add(new CounterRegister("STATUS_LATCH", i) {
	  // for description see Register
	  @Override
	  public String getValue() {
	    return String.valueOf(counters[number].statusLatch);
	  }
	  // for description see Register
	  @Override
	  public void processValue(final String value) {
	    counters[number].statusLatch = Integer.parseInt(value);
	    log.finer("Status Latch for counter " + number +
		      " set to: " + counters[number].statusLatch);
	  }
        });

      add(new CounterRegister("STATUS_LATCHED", i) {
	  // for description see Register
	  @Override
	  public String getValue() {
	    return String.valueOf(counters[number].statusLatched ? 1 : 0);
	  }
	  // for description see Register
	  @Override
	  public void processValue(final String value) {
	    counters[number].statusLatched = (Integer.parseInt(value) == 1);
	    log.finer("Status Latched for counter " + number +
		      " set to: " + counters[number].statusLatched);
	  }
        });

      add(new CounterRegister("GATE", i) {
	  // for description see Register
	  @Override
	  public String getValue() {
	    return String.valueOf(counters[number].gate ? 1 : 0);
	  }
	  // for description see Register
	  @Override
	  public void processValue(final String value) {
	    counters[number].gate = (Integer.parseInt(value) == 1);
	    log.finer("Gate for counter " + number +
		      " set to: " + counters[number].gate);
	  }
        });

      add(new CounterRegister("TRIGGER", i) {
	  // for description see Register
	  @Override
	  public String getValue() {
	    return String.valueOf(counters[number].trigger ? 1 : 0);
	  }
	  // for description see Register
	  @Override
	  public void processValue(final String value) {
	    counters[number].trigger = (Integer.parseInt(value) == 1);
	    log.finer("Trigger for counter " + number +
		      " set to: " + counters[number].trigger);
	  }
        });

      add(new CounterRegister("TRIGGERED", i) {
	  // for description see Register
	  @Override
	  public String getValue() {
	    return String.valueOf(counters[number].triggered ? 1 : 0);
	  }
	  // for description see Register
	  @Override
	  public void processValue(final String value) {
	    counters[number].triggered = (Integer.parseInt(value) == 1);
	    log.finer("Triggered for counter " + number +
		      " set to: " + counters[number].triggered);
	  }
        });

      add(new CounterRegister("PULSE", i) {
	  // for description see Register
	  @Override
	  public String getValue() {
	    return String.valueOf(counters[number].pulse ? 1 : 0);
	  }
	  // for description see Register
	  @Override
	  public void processValue(final String value) {
	    counters[number].pulse = (Integer.parseInt(value) == 1);
	    log.finer("Pulse for counter " + number +
		      " set to: " + counters[number].pulse);
	  }
        });

      add(new CounterRegister("BCD", i) {
	  // for description see Register
	  @Override
	  public String getValue() {
	    return String.valueOf(counters[number].bcd ? 1 : 0);
	  }
	  // for description see Register
	  @Override
	  public void processValue(final String value) {
	    counters[number].bcd = (Integer.parseInt(value) == 1);
	    log.finer("BCD for counter " + number +
		      " set to: " + counters[number].bcd);
	  }
        });

      add(new CounterRegister("READ_WRITE", i) {
	  // for description see Register
	  @Override
	  public String getValue() {
	    return String.valueOf(counters[number].rw);
	  }
	  // for description see Register
	  @Override
	  public void processValue(final String value) {
	    counters[number].rw = Integer.parseInt(value);
	    log.finer("Read/Write for counter " + number +
		      " set to: " + counters[number].rw);
	  }
        });

      add(new CounterRegister("NULL_COUNT", i) {
	  // for description see Register
	  @Override
	  public String getValue() {
	    return String.valueOf(counters[number].nullCount ? 1 : 0);
	  }
	  // for description see Register
	  @Override
	  public void processValue(final String value) {
	    counters[number].nullCount = (Integer.parseInt(value) == 1);
	    log.finer("Null Count for counter " + number +
		      " set to: " + counters[number].nullCount);
	  }
        });

      add(new CounterRegister("LOADED", i) {
	  // for description see Register
	  @Override
	  public String getValue() {
	    return String.valueOf(counters[number].loaded ? 1 : 0);
	  }
	  // for description see Register
	  @Override
	  public void processValue(final String value) {
	    counters[number].loaded = (Integer.parseInt(value) == 1);
	    log.finer("Loaded for counter " + number +
		      " set to: " + counters[number].loaded);
	  }
        });

      add(new CounterRegister("RESET", i) {
	  // for description see Register
	  @Override
	  public String getValue() {
	    return String.valueOf(counters[number].reset ? 1 : 0);
	  }
	  // for description see Register
	  @Override
	  public void processValue(final String value) {
	    counters[number].reset = (Integer.parseInt(value) == 1);
	    log.finer("Reset for counter " + number +
		      " set to: " + counters[number].reset);
	  }
        });

      add(new CounterRegister("DELAY", i) {
	  // for description see Register
	  @Override
	  public String getValue() {
	    return String.valueOf(counters[number].delay);
	  }
	  // for description see Register
	  @Override
	  public void processValue(final String value) {
	    counters[number].delay = Long.parseLong(value);
	    log.finer("Delay for counter " + number +
		      " set to: " + counters[number].delay);
	  }
        });

      add(new CounterRegister("CLOCK_PIN_LEVEL", i) {
	  // for description see Register
	  @Override
	  public String getValue() {
	    return String.valueOf(counters[number].clockPin.level ? 1 : 0);
	  }
	  // for description see Register
	  @Override
	  public void processValue(final String value) {
	    counters[number].clockPin.level = (Integer.parseInt(value) == 1);
	    log.finer("Clock pin level for counter " + number +
		      " set to: " + counters[number].clockPin.level);
	  }
        });

      add(new CounterRegister("GATE_PIN_LEVEL", i) {
	  // for description see Register
	  @Override
	  public String getValue() {
	    return String.valueOf(counters[number].gatePin.level ? 1 : 0);
	  }
	  // for description see Register
	  @Override
	  public void processValue(final String value) {
	    counters[number].gatePin.level = (Integer.parseInt(value) == 1);
	    log.finer("Gate pin level for counter " + number +
		      " set to: " + counters[number].gatePin.level);
	  }
        });

      add(new CounterRegister("OUT_PIN_LEVEL", i) {
	  // for description see Register
	  @Override
	  public String getValue() {
	    return String.valueOf(counters[number].outPin.level ? 1 : 0);
	  }
	  // for description see Register
	  @Override
	  public void processValue(final String value) {
	    counters[number].outPin.level = (Integer.parseInt(value) == 1);
	    log.finer("Out pin level for counter " + number +
		      " set to: " + counters[number].outPin.level);
	  }
        });
    }

    for (int i = 0; i < 3; i++) {
      counters[i] = new Counter();
      log.finer("Setting up counter: " + i + ", connection type: " +
		(types[i] ? "CPU clock" : "normal"));
      counters[i].direct = types[i];
    }
    reset();
    log.fine("New Intel 8254 creation completed, name: " + name);
  }

  // for description see Device
  @Override
  public void postUnmarshal() {
    for (int i = 0; i < 3; i++) {
      counters[i].base = counters[i].getBase();
    }
    log.fine("Post-unmarshal on 8254 completed");
  }

  // auxiliary subclass for creating counter registers
  private abstract class CounterRegister extends Register {

    protected int number;

    protected CounterRegister(final String name, final int number) {
      super("COUNTER" + number + "_" + name);
      this.number = number;
    }
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

    /**
     * Trigger event detection flag, relevant only for normal connection.
     * It is set on every rising edge of the gate pin and reset on every
     * rising edge of the clock.
     */
    protected boolean trigger;

    /**
     * Trigger event detection flag, relevant only for normal connection.
     * It is set when the trigger (the rising edge of the gate pin) is detected
     * and reset when it is processed.
     */
    protected boolean triggered;

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

    /**
     * A combined flag with somewhat different meanings in different Modes.
     */
    protected boolean loaded;

    /**
     * {@code true} if the Control Word has been written to the counter.
     */
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
     * Gets the base value the counter calculated from the BCD flag.
     * Values: {@code 0x10000} if BCD is {@code false}, {@code 10000}
     * if BCD is {@code true}.
     *
     * @return the base value of the counter
     */
    protected int getBase() {
      return (bcd ? 10000 : 0x10000);
    }

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
      CPUScheduler.removeAllEvents(this);
      clockPin.reset();
      gatePin.reset();
      outPin.reset();
      outputLatched = statusLatched = reading = writing =
	trigger = triggered = pulse = loaded = reset = false;
      nullCount = true;
      gate = gatePin.level;
      newCounterRegister = 0;
      delay = 0;
      base = getBase();
      log.finer("Counter reset");
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
	  CPUScheduler.removeAllEvents(this);
	  countingElement = (int)remains;
	  if ((mode == 2) || (mode == 3)) {
	    countingElement++;
	  }
	}
	log.finest("Counting suspended, remains: " + remains);
      }
    }

    
    /**
     * Sets the Counting Element to {@code value}, with correction for {@code delay};
     * the resulting value written to the Counting Element is never less than {@code 1}.
     * This method is only applicabla for direct connection.
     *
     * @param value the new value for the Counting Element
     */
    protected void setCountingElement(final int value) {
      assert direct;
      if (direct) {
	long delta = delay;
	if ((value - delta) < 1) {
	  delta = value - 1;
	}
	delay -= delta;
	countingElement = value - ((int)delta);
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
	} else if ((counterRegister == 1) && ((mode == 2) || (mode == 3))) {
	  counterRegister = base + 1;
	}
	
	nullCount = true;

	if (direct) {
	  switch (mode) {

	    case 0:
	      setCountingElement(counterRegister);
	      nullCount = false;
	      loaded = true;
	      if (gate) {
		CPUScheduler.removeAllEvents(this);
		CPUScheduler.addEventRelative(this, countingElement + 1);
		log.finest("Counter started, remains: " + (countingElement + 1));
	      }
	      break;

	    case 1:
	    case 5:
	      loaded = true;
	      break;

	    case 2:
	      if (loaded) {
		nullCount = true;
	      } else {
		nullCount = false;
		loaded = true;
		if (gate) {
		  setCountingElement(counterRegister);
		  CPUScheduler.removeAllEvents(this);
		  CPUScheduler.addEventRelative(this, countingElement);
		  log.finest("Counter started, remains: " + countingElement);
		}
	      }
	      break;

	    case 3:
	      if (loaded) {
		nullCount = true;
	      } else {
		nullCount = false;
		loaded = true;
		if (gate) {
		  setCountingElement(((counterRegister + 1) / 2) + 1);
		  CPUScheduler.removeAllEvents(this);
		  CPUScheduler.addEventRelative(this, countingElement);
		  log.finest("Counter started, remains: " + countingElement);
		}
	      }
	      break;

	    case 4:
	      setCountingElement(counterRegister);
	      nullCount = false;
	      loaded = true;
	      if (gate) {
		CPUScheduler.removeAllEvents(this);
		CPUScheduler.addEventRelative(this, countingElement + 1);
		log.finest("Counter started, remains: " + (countingElement + 1));
	      }
	      break;
	  }
	} else if ((mode == 2) || (mode == 3)) {
	  if (reset) {
	    loaded = true;
	  }
	} else {
	  loaded = true;
	}
      }
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
      if (direct && reset) {
	final long remains = CPUScheduler.getRemainingTime(this);
	log.finest("Getting counter state, remains: " + remains);
	if (remains >= 0) {
	  switch (mode) {
	    
	    case 0:
	      if (gate) {
		return ((int)remains) % base;
	      } else {
		return countingElement % base;
	      }
	      
	    case 1:
	      return ((int)remains) % base;
	      
	    case 2:
	      if (gate) {
		return (outPin.level ? ((((int)remains) + 1) % base) : 1);
	      } else {
		return counterRegister % base;
	      }
	      
	    case 3:
	      if (gate) {
		if (outPin.level) {
		  return Math.min(((int)remains) * 2, counterRegister) % base;
		} else if ((((int)remains) * 2) > (counterRegister - 2)) {
		  return counterRegister % base;
		} else {
		  return (((int)remains) * 2) % base;
		}
	      } else {
		return counterRegister % base;
	      }
	      
	    case 4:
	      if (gate) {
		return (outPin.level ? (((int)remains) % base) : 0);
	      } else {
		return countingElement % base;
	      }
	      
	    case 5:
	      return (outPin.level ? (((int)remains) % base) : 0);
	  }
	}
      }
      return countingElement % base;
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
    public void performEvent(final int parameter, final long newDelay) {
      assert direct;
      if (direct) {
	delay += newDelay;
	switch (mode) {

	  case 0:
	    setCountingElement(base - 1);
	    if (gate && !writing) {
	      out(true);
	      CPUScheduler.addEventRelative(this, countingElement + 1);
	      nullCount = false;
	    }
	    break;

	  case 1:
	    setCountingElement(base - 1);
	    out(true);
	    CPUScheduler.addEventRelative(this, countingElement + 1);
	    break;

	  case 2:
	    if (outPin.level) {
	      out(false);
	      CPUScheduler.addEventRelative(this, 1);
	    } else {
	      out(true);
	      setCountingElement(counterRegister - 1);
	      CPUScheduler.addEventRelative(this, countingElement);
	      nullCount = false;
	    }
	    break;

	  case 3:
	    out(!outPin.level);
	    setCountingElement((counterRegister + (outPin.level ? 1 : 0)) / 2);
	    CPUScheduler.addEventRelative(this, countingElement);
	    nullCount = false;
	    break;

	  case 4:
	    if (outPin.level && loaded) {
	      out(false);
	      CPUScheduler.addEventRelative(this, 1);
	    } else {
	      out(true);
	      loaded = false;
	      setCountingElement(base - 1);
	      CPUScheduler.addEventRelative(this, countingElement);
	    }
	    break;
	    
	  case 5:
	    if (outPin.level && loaded) {
	      out(false);
	      CPUScheduler.addEventRelative(this, 1);
	    } else {
	      out(true);
	      setCountingElement(base - 1);
	      CPUScheduler.addEventRelative(this, countingElement);
	    }
	    break;
	}
      }
    }

    /**
     * Sets the level of the output pin.
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
	      gatePin.notifyChange();
	      gate = gatePin.level;
	      log.finest("Gate level: " + gate);
	      if (trigger) {
		triggered = true;
		trigger = false;
	      }
	      pulse = true;
	    } else {
	      if (pulse) {
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
	  if (direct && reset && loaded) {
	    gate = level;
	    switch (mode) {
	      
	      case 0:
		if (gate) {
		  if (!writing) {
		    CPUScheduler.removeAllEvents(Counter.this);
		    CPUScheduler.addEventRelative(Counter.this, countingElement);
		    log.finest("Counting resumed");
		  }
		} else {
		  stop();
		}
		break;

	      case 1:
		if (gate) {
		  out(false);
		  CPUScheduler.removeAllEvents(Counter.this);
		  CPUScheduler.addEventRelative(Counter.this, counterRegister + 1);
		  nullCount = false;
		  log.finest("Counting triggered");
		}
		break;

	      case 2:
		if (gate) {
		  CPUScheduler.removeAllEvents(Counter.this);
		  CPUScheduler.addEventRelative(Counter.this, counterRegister);
		  log.finest("Counting resumed");
		} else {
		  out(true);
		  stop();
		}
		break;
	      
	      case 3:
		if (gate) {
		  CPUScheduler.removeAllEvents(Counter.this);
		  CPUScheduler.addEventRelative(Counter.this, ((counterRegister + 1) / 2) + 1);
		  log.finest("Counting resumed");
		} else {
		  out(true);
		  stop();
		}
		break;
	      
	      case 4:
		if (gate) {
		  if (!writing) {
		    CPUScheduler.removeAllEvents(Counter.this);
		    CPUScheduler.addEventRelative(Counter.this, countingElement);
		    log.finest("Counting resumed");
		  }
		} else {
		  stop();
		}
		break;

	      case 5:
		if (gate) {
		  out(true);
		  CPUScheduler.removeAllEvents(Counter.this);
		  CPUScheduler.addEventRelative(Counter.this, counterRegister + 1);
		  nullCount = false;
		  log.finest("Counting triggered");
		}
		break;
	    }
	  }
	  if (!level && ((mode == 2) || (mode == 3))) {
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
	  counter.reset = true;
	
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
