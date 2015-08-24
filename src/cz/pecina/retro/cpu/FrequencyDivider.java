/* FrequencyDivider.java
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
 * Frequency divider.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class FrequencyDivider extends Device {

  // dynamic logger, per device
  private Logger log;

  // dividing ratio
  private int ratio;

  // edge activating the divider (true = rising, false = falling)
  private boolean edge;

  // counter
  private long counter;
  
  // input level
  private boolean input;
  
  // output level
  private boolean output;
  
  // input pin
  private final InPin inPin = new InPin();
  
  // output pin
  private final OutPin outPin = new OutPin();
  

  // input pin
  private class InPin extends IOPin {
	
    // for description see IOPin
    @Override
    public void notifyChange() {
      final boolean newInput = IONode.normalize(queryNode()) == 1;
      if (newInput != input) {
	if (newInput == edge) {
	  if (++counter == ratio) {
	    counter = 0;
	    output = !output;
	    outPin.notifyChangeNode();
	  }
	}
	input = newInput;
      }
    }
  }

  // output pin
  private class OutPin extends IOPin {

    // for description see IOPin
    @Override
    public int query() {
      return output ? 1 : 0;
    }
  }
  
  /**
   * Gets the input pin.
   *
   * @return the input pin
   */
  public IOPin getInPin() {
    return inPin;
  }

  /**
   * Gets the output pin.
   *
   * @return the output pin
   */
  public IOPin getOutPin() {
    return outPin;
  }

  /**
   * Resets the device.
   */
  @Override
  public void reset() {
    counter = 0;
    input = IONode.normalize(inPin.queryNode()) == 1;
    output = false;
    outPin.notifyChangeNode();
    log.finer("Frequency divider reset");
  }

  /**
   * Main constructor.
   *
   * @param name  device name
   * @param ratio the dividing ratio
   * @param edge  {@code true} if the divider is activated by the
   *              rising edge of the input signal, {@code false} if
   *              by the falling edge
   */
  public FrequencyDivider(final String name,
			  final int ratio,
			  final boolean edge) {
    super(name);
    log = Logger.getLogger(getClass().getName() + "." + name);
    log.fine(String.format(
      "New frequency divider creation started, name: %s, ratio: %d, edge: %s",
      name, ratio, edge));
    assert ratio > 1;

    this.ratio = ratio;
    this.edge = edge;
    
    add(new Register("OUTPUT") {
	@Override
	public String getValue() {
	  return output ? "1" : "0";
	}
	@Override
	public void processValue(final String value) {
	  output = Integer.parseInt(value) == 1;
	  log.finer("Output set to: " + output);
	}
      });
    add(new Register("COUNTER") {
	@Override
	public String getValue() {
	  return String.valueOf(counter);
	}
	@Override
	public void processValue(final String value) {
	  counter = Integer.parseInt(value);
	  log.finer("Counter set to: " + counter);
	}
      });

    reset();
    log.fine("New frequency divider creation completed, name: " + name);
  }

  // for description see Device
  @Override
  public void postUnmarshal() {
    input = IONode.normalize(inPin.queryNode()) == 1;
    outPin.notifyChangeNode();
    log.fine("Post-unmarshal on frequency divider completed");
  }
}
