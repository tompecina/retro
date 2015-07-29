/* BlinkPattern.java
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
 * Blinking pattern.
 * <p>
 * The pattern consists of two fileds, <code>timeOn</code> and
 * <code>timeOn</code>.  Both are expressed in milliseconds and may
 * not be negative.  If both <code>timeOn</code> and <code>timeOff</code>
 * are zero, the element is off.  If <code>timeOn</code> is zero and
 * <code>timeOff</code> is non-zero, the element is on.  If
 * <code>timeOn</code> is non-zero and <code>timeOff</code> is zero,
 * the element generates an on-pulse <code>timeOn</code> milliseconds
 * long and then goes off.  If both <code>timeOn</code> and
 * <code>timeOn</code> and non-zero, the element blinks according
 * to the pattern.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class BlinkPattern {

  // static logger
  private static final Logger log =
    Logger.getLogger(BlinkPattern.class.getName());

  // time fields
  private int timeOn, timeOff;

  /**
   * Creates a blinking pattern.
   *
   * @param timeOn  the on parameter of the pattern (in milliseconds)
   * @param timeOff the off parameter of the pattern (in milliseconds)
   */
  public BlinkPattern(final int timeOn, final int timeOff) {
    assert (timeOn >= 0) && (timeOff >= 0);
    this.timeOn = timeOn;
    this.timeOff = timeOff;
    log.fine("New BlinkPattern created");
  }

  /**
   * Creates a pulse pattern.
   *
   * @param timeOn duration of the pulse (in milliseconds)
   */
  public BlinkPattern(final int timeOn) {
    assert timeOn > 0;
    this.timeOn = timeOn;
    this.timeOff = 0;
  }

  /**
   * Creates an on ot off pattern.
   *
   * @param b true generates on pattern, false generates off pattern
   */
  public BlinkPattern(final boolean b) {
    this.timeOn = 0;
    this.timeOff = b ? 1 : 0;
  }

  /**
   * Gets the on parameter of the pattern.
   *
   * @return the on parameter of the pattern (in milliseconds)
   */
  public int getTimeOn() {
    return timeOn;
  }

  /**
   * Gets the off parameter of the pattern.
   *
   * @return the off parameter of the pattern (in milliseconds)
   */
  public int getTimeOff() {
    return timeOff;
  }
}
