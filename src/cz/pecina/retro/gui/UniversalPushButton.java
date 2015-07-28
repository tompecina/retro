/* UniversalPushButton.java
 *
 * Copyright (C) 2014-2015, Tomáš Pecina <tomas@pecina.cz>
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
 * Universal push button.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class UniversalPushButton extends PushButton {

  // static logger
  private static final Logger log =
    Logger.getLogger(UniversalPushButton.class.getName());

  /**
   * Creates an instance of the button.
   *
   * @param type     type of the button
   * @param color    color of the button
   * @param symbol   symbol on the button
   * @param shortcut keyboard shortcut for the button (<code>-1</code>
   *                 if none)
   * @param toolTip  tool-tip for the button (<code>null</code> if none)
   */
  public UniversalPushButton(final String type,
			     final String color,
			     final String symbol,
			     final int shortcut,
			     final String toolTip) {
    super("gui/UniversalButton/" + type + "-" + color + "-" + symbol +
	  "-%d-%s.png", shortcut, toolTip);
    log.fine("New UniversalButton created: " + template);
  }
}
