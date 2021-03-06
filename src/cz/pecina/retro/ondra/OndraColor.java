/* OndraColor.java
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

package cz.pecina.retro.ondra;

import java.util.logging.Logger;

import java.awt.Color;

/**
 * Color constants.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public final class OndraColor {

  // static logger
  private static final Logger log =
    Logger.getLogger(OndraColor.class.getName());

  /**
   * Number of color modes.
   */
  public static int NUMBER_COLOR_MODES = 3;
  
  /**
   * Default color mode.
   */
  public static int DEFAULT_COLOR_MODE = 0;

  /**
   * White-on-black color mode.
   */
  public static Color WOB_COLOR = new Color(0xffffff);
  
  /**
   * Green-on-black color mode.
   */
  public static Color GOB_COLOR = new Color(0x80ff80);

  /**
   * Default custom color mode.
   */
  public static Color DEFAULT_COLOR = new Color(0xffffff);

  // default constructor disabled
  private OndraColor() {}
}
