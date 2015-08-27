/* DisplayHardware.java
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
 * Display hardware of the Tesla PMD 85 computer.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class DisplayHardware {

  // static logger
  private static final Logger log =
    Logger.getLogger(DisplayHardware.class.getName());

  // the display
  private Display display;

  /**
   * Creates a new display hardware object.
   *
   * @param computerHardware the computer hardware object
   */
  public DisplayHardware(final ComputerHardware computerHardware) {
    assert computerHardware != null;
    display = new Display(computerHardware);
  }

  /**
   * Gets the display.
   */
  public Display getDisplay() {
    return display;
  }

  /**
   * Refreshes the display.
   */
  public void refresh() {
    display.refresh();
  }
}
