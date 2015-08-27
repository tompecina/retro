/* NXOR.java
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

/**
 * NXOR (non-XOR) element.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class NXOR {

  // dynamic logger, per device
  private Logger log;

  // name of the device
  private String name;

  // number of input pins
  private int numberInPins;

  // input pins
  private final InPin[] inPins;

  // output pin
  private OutPin outPin;

  /**
   * Gets the device name.
   *
   * @return the name of the device
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the number of input pins.
   *
   * @return the number of input pins
   */
  public int getNumberInPins() {
    return numberInPins;
  }

  /**
   * Gets input pin.
   *
   * @param  n pin number
   * @return <code>IOPin</code> object
   */
  public IOPin getInPin(final int n) {
    return inPins[n];
  }

  /**
   * Gets output pin.
   *
   * @return <code>IOPin</code> object
   */
  public IOPin getOutPin() {
    return outPin;
  }

  /**
   * Main constructor.
   *
   * @param name         the name of the device
   * @param numberInPins the number of input pins
   */
  public NXOR(final String name, final int numberInPins) {
    assert name != null;
    assert numberInPins > 0;
    log = Logger.getLogger(getClass().getName() + "." + name);
    log.fine("New NXOR creation started: " + name);
    this.name = name;
    this.numberInPins = numberInPins;
    inPins = new InPin[numberInPins];
    for (int i = 0; i < numberInPins; i++) {
      inPins[i] = new InPin(i);
    }
    outPin = new OutPin();
    log.fine("New NXOR creation completed: " + name);
  }

  // input pin class
  private class InPin extends IOPin {

    private int number;
    
    // main constructor
    private InPin(final int number) {
      super();
      assert (number >= 0) && (number < numberInPins);
      this.number = number;
      log.finer("New NXOR input pin created");
    }

    // for description see IOPin
    @Override
    public void notifyChange() {
      if (log.isLoggable(Level.FINEST)) {
	log.finest("Input " + number + " of NXOR '" + name + "' notified");
      }
      outPin.query();
    }
  }

  // output pin class
  private class OutPin extends IOPin {
	
	
    private int output;

    // main constructor
    private OutPin() {
      super();
      log.finer("New NXOR output pin created");
    }

    // for description see IOPin
    @Override
    public int query() {
      int newOutput = 1;
      for (IOPin pin: inPins) {
	newOutput ^= IONode.normalize(pin.queryNode());
      }
      if (newOutput != output) {
	output = newOutput;
	notifyChangeNode();
	if (log.isLoggable(Level.FINER)) {
	  log.finer("Changed output of NXOR '" + name + "': " + output);
	}
      }
      return output;
    }
  }
}
