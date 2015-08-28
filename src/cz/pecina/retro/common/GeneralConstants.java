/* GeneralConstants.java
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

package cz.pecina.retro.common;

/**
 * General constants common to all emulated devices.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public final class GeneralConstants {
    
  /**
   * Array of pixel sizes for which bitmaps are available.
   */
  public static final Integer[] PIXEL_SIZES = {1, 2, 3, 4};

  /**
   * Array of supported language strings.
   */
  public static final String[] SUPPORTED_LOCALES = {"en-US", "cs-CZ", "sk-SK"};

  public static final int TOOL_TIP_INITIAL_DELAY = 1000;
  public static final int TOOL_TIP_DISMISS_DELAY = 5000;
  public static final int TOOL_TIP_RESHOW_DELAY = 0;

  // default constructor disabled
  private GeneralConstants() {}
}
