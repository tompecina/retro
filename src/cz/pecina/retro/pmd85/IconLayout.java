/* IconLayout.java
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

/**
 * Array of icons displayed on the main control panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class IconLayout {

  // static logger
  private static final Logger log =
    Logger.getLogger(IconLayout.class.getName());

  /**
   * Number of icons.
   */
  public static final int NUMBER_ICONS = 8;

  /**
   * Position of the "wheel" icon.
   */
  public static final int ICON_POSITION_WHEEL = 0;

  /**
   * Position of the "reset" icon.
   */
  public static final int ICON_POSITION_RESET = 1;

  /**
   * Position of the "memory" icon.
   */
  public static final int ICON_POSITION_MEM = 2;

  /**
   * Position of the "keyboard" icon.
   */
  public static final int ICON_POSITION_KEYBOARD = 3;

  /**
   * Position of the "cassette" icon.
   */
  public static final int ICON_POSITION_CASSETTE = 4;

  /**
   * Position of the "cable" icon.
   */
  public static final int ICON_POSITION_CABLE = 5;

  /**
   * Position of the "debugger" icon.
   */
  public static final int ICON_POSITION_DEBUG = 6;

  /**
   * Position of the "info" icon.
   */
  public static final int ICON_POSITION_INFO = 7;

  // the icons
  public IconButton[] icons;

  /**
   * Creates and populates the array of icons.
   *
   * @param computer the computer control object
   */
  public IconLayout(final Computer computer) {
    log.fine("New IconLayout creation started");
    assert computer != null;
    icons = new IconButton[] {
      new IconButton(computer, "wheel", 312, 10, "toolTip.settings"),
      new IconButton(computer, "reset", 313, 44, "toolTip.reset"),
      new IconButton(computer, "mem", 311, 78, "toolTip.memory"),
      new IconButton(computer, "keyboard", 312, 112, "toolTip.keyboard"),
      new IconButton(computer, "cassette", 312, 147, "toolTip.tapeRecorder"),
      new IconButton(computer, "cable", 311, 180, "toolTip.peripherals"),
      new IconButton(computer, "debug", 312, 214, "toolTip.debugger"),
      new IconButton(computer, "info", 312, 245, "toolTip.about")
    };
    log.fine("New IconLayout creation completed");
  }

  /**
   * Gets the icon.
   *
   * @param  n position of the icon, from top, zero-based
   * @return the {@code n}-th icon
   */
  public IconButton getIcon(final int n) {
    return icons[n];
  }
}
