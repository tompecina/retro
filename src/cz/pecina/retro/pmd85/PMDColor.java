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
  public static int NUMBER_COLOR_MODES = 4;
  
  /**
   * Default color mode.
   */
  public static int DEFAULT_COLOR_MODE = 0;

  /**
   * Number of colors.
   */
  public static int NUMBER_COLORS = 4;

  /**
   * White-on-black color modes.
   */
  public static PMDColor[][] WOB_COLORS= {
    {
      new PMDColor(new Color(0xffffff), false),
      new PMDColor(new Color(0x808080), false),
      new PMDColor(new Color(0xffffff), true),
      new PMDColor(new Color(0x808080), true)},
    {
      new PMDColor(new Color(0xffffff), false),
      new PMDColor(new Color(0xc0c0c0), false),
      new PMDColor(new Color(0x808080), false),
      new PMDColor(new Color(0x404040), false)}};
  
  /**
   * Green-on-black color modes.
   */
  public static PMDColor[][] GOB_COLORS = {
    {
      new PMDColor(new Color(0x80ff80), false),
      new PMDColor(new Color(0x408040), false),
      new PMDColor(new Color(0x80ff80), true),
      new PMDColor(new Color(0x408040), true)},
    {
      new PMDColor(new Color(0x80ff80), false),
      new PMDColor(new Color(0x60c060), false),
      new PMDColor(new Color(0x408040), false),
      new PMDColor(new Color(0x204020), false)}};
  
  /**
   * Default custom color mode.
   */
  public static PMDColor DEFAULT_COLORS[] = {
    new PMDColor(new Color(0x00ff00), false),
    new PMDColor(new Color(0xff0000), false),
    new PMDColor(new Color(0x0000ff), false),
    new PMDColor(new Color(0xff00ff), false)};

  // color
  private Color color;

  // blink flag
  private boolean blinkFlag;

  /**
   * Creates a color object,
   *
   * @param color     color of the pixels
   * @param blinkFlag {@code true} if blinking
   */
  public PMDColor(final Color color, final boolean blinkFlag) {
    assert color != null;
    this.color = color;
    this.blinkFlag = blinkFlag;
    log.fine("New PMD color created, color: " + color +
	     ", blink: " + blinkFlag);
  }

  /**
   * Sets the color.
   *
   * @param color the color of the pixels
   */
  public void setColor(final Color color) {
    this.color = color;
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
   * Sets the blinking flag.
   *
   * @param blinkFlag {@code true} if blinking
   */
  public void setBlinkFlag(final boolean blinkFlag) {
    this.blinkFlag = blinkFlag;
  }

  /**
   * Gets the blinking flag.
   *
   * @return {@code true} if blinking
   */
  public boolean getBlinkFlag() {
    return blinkFlag;
  }
}
