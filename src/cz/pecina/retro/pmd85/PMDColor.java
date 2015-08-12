/* PMDColor.java
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

package cz.pecina.retro.pmd85;

import java.util.logging.Logger;
import java.awt.Color;

/**
 * PMD 85 color.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class PMDColor {

  // static logger
  private static final Logger log =
    Logger.getLogger(PMDColor.class.getName());


  /**
   * Number of color modes.
   */
  public static int NUMBER_COLOR_MODES = 3;
  
  /**
   * Default color mode.
   */
  public static int DEFAULT_COLOR_MODE = 0;

  /**
   * Number of colors.
   */
  public static int NUMBER_COLORS = 4;

  /**
   * Default color mode.
   */
  public static PMDColor DEFAULT_COLORS[] = {
    new PMDColor(Color.WHITE, false),
    new PMDColor(Color.RED, false),
    new PMDColor(Color.BLUE, false),
    new PMDColor(Color.MAGENTA, false)};
  
  // color
  private Color color;

  // blink flag
  private boolean blinkFlag;

  /**
   * Creates a color object,
   *
   * @param color     color of the pixels
   * @param blinkFlag <code>true</code> if blinking
   */
  public PMDColor(final Color color, final boolean blinkFlag) {
    assert color != null;
    this.color = color;
    this.blinkFlag = blinkFlag;
    log.fine("New PMD color created, color: " + color + ", blink: " + blinkFlag);
  }

  /**
   * Gets the color.
   *
   * @return the color of the pixels
   */
  public Color getColor() {
    return color;
  }


  /**
   * Gets the blinking flag.
   *
   * @return <code>true</code> if blinking
   */
  public boolean getBlinkFlag() {
    return blinkFlag;
  }
}
