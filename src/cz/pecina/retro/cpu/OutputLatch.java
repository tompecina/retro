/* OutputLatch.java
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
 * 8-bit output buffer/register/latch.
 * <p>
 * This is a register holding a value written to it indefinitely.
 * It can only be accessed by sensing the output pins, reading back the value
 * from a port is not possible.  It roughly corresponds to Intel 8282.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class OutputLatch extends Device implements IOElement {

  // dynamic logger, per device
  private Logger log;

  // register
  private int register;
  
  // output pins
  private final OutPin[] outPins = new OutPin[8];

  /**
   * Gets the output pin.
   *
   * @param  number pin number
   * @return        the output pin
   */
  public IOPin getOutPin(final int number) {
    assert (number >= 0) && (number < 8);
    return outPins[number];
  }

  // notify on all output pins
  private void notifyPins() {
    for (int i = 0; i < 8; i++) {
      outPins[i].notifyChangeNode();
    }
  }

  // for description see Device
  @Override
  public void reset() {
    register = 0;
    notifyPins();
    log.finer(String.format("%s: reset", name));
  }

  /**
   * Main constructor.
   *
   * @param name device name
   */
  public OutputLatch(final String name) {
    super(name);
    log = Logger.getLogger(getClass().getName() + "." + name);
    log.fine("New output latch creation started, name: " + name);

    add(new Register("OUT") {
	@Override
	public String getValue() {
	  return String.format("%02x", register);
	}
	@Override
	public void processValue(final String value) {
	  register = Integer.parseInt(value, 16);
	  log.finer("Register set to: " + register);
	}
      });

    for (int i = 0; i < 8; i++) {
      outPins[i] = new OutPin(i);
    }
    reset();
    log.finer("New output latch creation completed, name: " + name);
  }

  // for description see Device
  @Override
  public void postUnmarshal() {
    notifyPins();
    log.fine("Post-unmarshal on output latch completed");
  }

  // output pin
  private class OutPin extends IOPin {

    private int number;
	
    // main constructor
    private OutPin(final int number) {
      super();
      this.number = number;
    }

    // for description see IOPin
    @Override
    public int query() {
      return (register >> number) & 1;
    }
  }

  // for description see IOElement
  @Override
  public int portInput(final int port) {
    log.finer("Output latch '" + name +
	      "' input port polled, possibly an error");
    return 0xff;
  }

  // for description see IOElement
  @Override
  public void portOutput(final int port, int data) {
    log.finest(String.format("Data written to output latch '%s': 0x%02x",
			     name, data));
    register = data;
    notifyPins();
  }
}
