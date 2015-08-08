/* FixedPin.java
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

/**
 * I/O pin with fixed signal level (mainly for debugging).
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class FixedPin extends IOPin {

  // the signal level of the pin
  private int level;
  
  /**
   * Main constructor.
   *
   * @param level the fixed level
   */
  public FixedPin(final int level) {
    this.level = level;
  }

  // for description see IOPin
  @Override
  public int query() {
    return level;
  }
}
