/* HexESD.java
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

import cz.pecina.retro.common.Application;

/**
 * Eight-segment display element, subclass for displaying hexadecimal values.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class HexESD extends ESD {

  // static logger
  private static final Logger log =
    Logger.getLogger(HexESD.class.getName());

  // matrix of states
  private static final int[] matrix =
    {0x00, 0x3f, 0x06, 0x5b, 0x4f, 0x66, 0x6d, 0x7d, 0x07,
     0x7f, 0x6f, 0x77, 0x7c, 0x39, 0x5e, 0x79, 0x71};

  /**
   * Creates an instance of HexESD, initially set to
   * <code>' '</code> (blank).
   *
   * @param type  type of the element
   * @param color color of the element
   */
  public HexESD(final String type, final String color) {
    super(type, color);
    log.fine("New HexESD created: " + type + ", " + color);
  }

  /**
   * Gets the state of the decimal point.
   *
   * @return state of the decimal point (<code>true</code> = on,
   *         <code>false</code> = off)
   */
  public boolean getDecimalPoint() {
    return (super.getState() & 0x80) != 0;
  }

  /**
   * Sets the state of the decimal points.  The segments
   * remain unchanged.
   *
   * @param dp new state of the decimal point (<code>true</code> = on,
   *           <code>false</code> = off)
   */
  public void setDecimalPoint(final boolean dp) {
    setState(getState(), dp);
  }

  /**
   * Gets the state of the segments.
   *
   * @return state of the segments (<code>-1</code> - <code>15</code>;
   *         <code>-1</code> means blank)
   */
  @Override
  public int getState() {
    final int n = super.getState() & 0x7f;
    for (int i = 0; i <= NUMBER_STATES; i++) {
      if (matrix[i] == n) {
	return i - 1;
      }
    }
    throw Application.createError(this, "invalidState");
  }

  /**
   * Sets the state of the element.
   *
   * @param n  new state of the segments (<code>-1</code> - <code>15</code>;
   *           <code>-1</code> means blank)
   * @param dp new state of the decimal point (<code>true</code> = on,
   *           <code>false</code> = off)
   */
  public void setState(final int n, final boolean dp) {
    assert (n >= -1) && (n <= 15);
    super.setState(matrix[n + 1] + (dp ? 0x80 : 0));
    log.finer("HexESD state changed to: " + n + ", " + dp);
  }

  /**
   * Sets the state of the segments.  The decimal point remains unchanged.
   *
   * @param n new state of the segments (<code>-1</code> - <code>15</code>;
   *          <code>-1</code> means blank)
   */
  @Override
  public void setState(final int n) {
    setState(n, getDecimalPoint());
  }
}
