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

  // internal registers

  // pins

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
   * @param name device name
   */
  public Intel8253(final String name) {
    super(name);
    log = Logger.getLogger(getClass().getName() + "." + name);
    log.fine("New Intel 8253 creation started, name: " + name);

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
    // add(new Register("MODE_B") {
    // 	@Override
    // 	public String getValue() {
    // 	  return String.valueOf(modeB);
    // 	}
    // 	@Override
    // 	public void processValue(final String value) {
    // 	  modeB = Integer.parseInt(value);
    // 	  log.finer("Mode B set to: " + modeB);
    // 	}
    //   });
    // add(new Register("DIR_A") {
    // 	@Override
    // 	public String getValue() {
    // 	  return String.valueOf(directionA);
    // 	}
    // 	@Override
    // 	public void processValue(final String value) {
    // 	  directionA = Integer.parseInt(value);
    // 	  log.finer("Direction A set to: " + directionA);
    // 	}
    //   });
    // add(new Register("DIR_B") {
    // 	@Override
    // 	public String getValue() {
    // 	  return String.valueOf(directionB);
    // 	}
    // 	@Override
    // 	public void processValue(final String value) {
    // 	  directionB = Integer.parseInt(value);
    // 	  log.finer("Direction B set to: " + directionB);
    // 	}
    //   });
    // add(new Register("DIR_CL") {
    // 	@Override
    // 	public String getValue() {
    // 	  return String.valueOf(directionCL);
    // 	}
    // 	@Override
    // 	public void processValue(final String value) {
    // 	  directionCL = Integer.parseInt(value);
    // 	  log.finer("Direction CL set to: " + directionCL);
    // 	}
    //   });
    // add(new Register("DIR_CH") {
    // 	@Override
    // 	public String getValue() {
    // 	  return String.valueOf(directionCH);
    // 	}
    // 	@Override
    // 	public void processValue(final String value) {
    // 	  directionCH = Integer.parseInt(value);
    // 	  log.finer("Direction CH set to: " + directionCH);
    // 	}
    //   });
    // add(new Register("IN") {
    // 	@Override
    // 	public String getValue() {
    // 	  int n = 0;
    // 	  for (int i = 0, r = 1; i < 16; i++, r <<= 1) {
    // 	    if (inputLatch[i] != 0) {
    // 	      n |= r;
    // 	    }
    // 	  }
    // 	  return String.format("%04x", n);
    // 	}
    // 	@Override
    // 	public void processValue(final String value) {
    // 	  for (int i = 0, n = Integer.parseInt(value, 16);
    // 	       i < 16; i++, n >>= 1) {
    // 	    inputLatch[i] = n;
    // 	  }
    // 	  log.finer("Input latch registers set");
    // 	}
    //   });
    // add(new Register("OUT") {
    // 	@Override
    // 	public String getValue() {
    // 	  int n = 0;
    // 	  for (int i = 0, r = 1; i < 24; i++, r <<= 1) {
    // 	    if (outputBuffer[i] != 0) {
    // 	      n |= r;
    // 	    }
    // 	  }
    // 	  return String.format("%06x", n);
    // 	}
    // 	@Override
    // 	public void processValue(final String value) {
    // 	  for (int i = 0, n = Integer.parseInt(value, 16);
    // 	       i < 24; i++, n >>= 1) {
    // 	    outputBuffer[i] = n & 1;
    // 	  }
    // 	  log.finer("Output buffers set");
    // 	}
    //   });
    // add(new Register("STB_A") {
    // 	@Override
    // 	public String getValue() {
    // 	  return String.valueOf(strobeA);
    // 	}
    // 	@Override
    // 	public void processValue(final String value) {
    // 	  strobeA = Integer.parseInt(value);
    // 	  log.finer("Strobe A set to: " + strobeA);
    // 	}
    //   });
    // add(new Register("STB_B") {
    // 	@Override
    // 	public String getValue() {
    // 	  return String.valueOf(strobeB);
    // 	}
    // 	@Override
    // 	public void processValue(final String value) {
    // 	  strobeB = Integer.parseInt(value);
    // 	  log.finer("Strobe B set to: " + strobeB);
    // 	}
    //   });
    // add(new Register("ACK_A") {
    // 	@Override
    // 	public String getValue() {
    // 	  return String.valueOf(acknowledgeA);
    // 	}
    // 	@Override
    // 	public void processValue(final String value) {
    // 	  acknowledgeA = Integer.parseInt(value);
    // 	  log.finer("Acknowledge A set to: " + acknowledgeA);
    // 	}
    //   });
    // add(new Register("ACK_B") {
    // 	@Override
    // 	public String getValue() {
    // 	  return String.valueOf(acknowledgeB);
    // 	}
    // 	@Override
    // 	public void processValue(final String value) {
    // 	  acknowledgeB = Integer.parseInt(value);
    // 	  log.finer("Acknowledge B set to: " + acknowledgeB);
    // 	}
    //   });

    for (int i = 0; i < 3; i++) {
      counters[i] = new Counter();
    }
    reset();
    log.fine("New Intel 8253 creation completed, name: " + name);
  }

  // for description see Device
  @Override
  public void postUnmarshal() {
    log.fine("Post-unmarshal on 8253 completed");
  }

  /**
   * Sets the counter connection type.
   *
   * @param number the counter number
   * @param type   if {@code true}, the counter's clock is connected to system
   *               clock and the clock pin is disabled, if {@code false}, the clock
   *               pin is enabled
   */
  public void setConnection(final int number, final boolean type) {
    log.finer("Counter: " + number + ", connection type: " +
	      (type ? "CPU clock" : "normal"));
    assert (number >= 0) && (number < 3);
    counters[number].type = type;
  }

  // counter
  private class Counter implements CPUEventOwner {

    // clock connection type
    //  false - the clock pin
    //  true - the CPU clock
    public boolean type;

    // state of the counter
    public int state;

    // state of the latch register
    private boolean latched;

    // if true, reading in progress. i.e., LSB read, MSB will follow
    private boolean readInProgress;

    // the current count
    public int count;
    
    // binary/BCD mode
    //  false - binary
    //  true - BCD
    public boolean bcd;

    // RL register - 1
    //  0 - only LSB
    //  1 - only MSB
    //  2 - LSB, then MSB
    public int rl, mode;

    // get LSB
    public int lsb(final int data) {
      return (bcd ? (data % 100) : (data & 0xff));
    }
    
    // get MSB
    public int msb(final int data) {
      return (bcd ? (data / 100) : (data >> 8));
    }
    
    // reset counter
    public void reset() {
      log.finer("Counter reset");
      latchState = 0;
    }

    // write one byte to the counter
    public void write(final int data) {
    }

    // read one byte from the counter
    public int read() {
      int data = 0, value;
      if (latched) {
	value = latch;
	if (readInProgress || (rl != 2)) {
	  latched = false;
	}
      } else {
	value = count;
      }
      if (readInProgress) {
	readInProgress = false;
	data = msb(count);
      } else {
	switch (rl) {
	  case 2:
	    readInProgress = true;
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
      latchState = 1;
      log.finer(String.format("Counter latched, value: 0x%02x", latch));
    }

    // for description see CPUEventOwner
    @Override
    public void performScheduledEvent(final int parameter) {
    }
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

      final int rl = (data >> 4) & 0x03;

      if (rl == 0) {

	log.finer("Counter " + number + " latched");
	counter.latch();

      } else {

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
