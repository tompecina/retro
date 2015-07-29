/* IONode.java
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

import java.util.List;
import java.util.ArrayList;

/**
 * Node interconnecting multiple I/O pins.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class IONode {

  /**
   * Constant representing high impendance of a tri-state pin.
   */
  public static final int HIGH_IMPEDANCE = -1;

  /**
   * Converts HIGH_IMPEDANCE to 1.
   *
   * @param  state the input signal level (0, 1 or HIGH_IMPEDANCE)
   * @return the output signal level (0 or 1)
   */
  public static int normalize(final int state) {
    return (state == 0) ? 0 : 1;
  }

  // list of pins connected to the node
  private final List<IOPin> pins = new ArrayList<>();

  /**
   * Adds a new pin to the node.  Supports chaining.
   *
   * @param  pin pin to be added
   * @return this node for chaining
   */
  public IONode add(IOPin pin) {
    assert !pins.contains(pin);
    pins.add(pin);
    pin.setNode(this);
    return this;
  }

  /**
   * Removes a pin from the node.  Supports chaining.
   *
   * @param  pin pin to be removed
   * @return this node for chaining
   */
  public IONode remove(final IOPin pin) {
    assert pins.contains(pin);
    pins.remove(pin);
    return this;
  }

  /**
   * Reports state (signal level) of the node.
   *
   * @return state (signal level) of the node
   */
  public int query() {
    for (IOPin pin: pins) {
      final int response = pin.query();
      if (response != HIGH_IMPEDANCE) {
	return response;
      }
    }
    return HIGH_IMPEDANCE;
  }

  /**
   * Notifies all pins of the changed state (signal level).
   */
  public void notifyChange() {
    for (IOPin pin: pins) {
      pin.notifyChange();
    }
  }
}
