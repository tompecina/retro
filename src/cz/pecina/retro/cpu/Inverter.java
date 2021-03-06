/* Inverter.java
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
 * Inverter element.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Inverter extends NAND {

  // dynamic logger, per device
  private Logger log;

  /**
   * Gets input pin.
   *
   * @return {@code IOPin} object
   */
  public IOPin getInPin() {
    return super.getInPin(0);
  }

  /**
   * Main constructor.
   *
   * @param name the name of the device
   */
  public Inverter(final String name) {
    super(name, 1);
    log = Logger.getLogger(getClass().getName() + "." + name);
    log.fine("New inverter creation completed: " + name);
  }
}
