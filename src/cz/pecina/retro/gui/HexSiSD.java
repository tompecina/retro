/* HexSiSD.java
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

package cz.pecina.retro.gui;

import java.util.logging.Logger;

/**
 * Sixteen-segment display element, subclass for displaying
 * hexadecimal values.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class HexSiSD extends SiSD {

  // static logger
  private static final Logger log =
    Logger.getLogger(HexSiSD.class.getName());

  /**
   * Creates an instance of HexSiSD, initially set to
   * {@code ' '} (blank).
   *
   * @param type  type of the element
   * @param color color of the element
   */
  public HexSiSD(final String type, final String color) {
    super(type, color);
    log.fine("New HexSiSD created");
  }
    
  /**
   * Clears the element.
   *
   * @param ch new state of the element (ignored)
   */
  @Override
  public void setState(final char ch) {
    setState(-1);
    log.finer("HexSSD cleared");
  }

  /**
   * Sets the state of the element.
   *
   * @param n new state of the element
   */
  @Override
  public void setState(final int n) {
    assert (n >= -1) && (n < 16);
    if (n != state) {
      state = n;
      if (n == -1) {
	super.setState((int)' ');
      } else if (n < 10) {
	super.setState((int)'0' + n);
      } else {
	super.setState((int)'A' + n - 10);
      }
      log.finer("HexSSD state changed to: " + n);
    }
  }
}
