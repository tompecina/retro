/* Shortcut.java
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
 * Keyboard shortcut object.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Shortcut {

  // static logger
  private static final Logger log =
    Logger.getLogger(Shortcut.class.getName());

  // code of the key
  private int keyCode;

  // location of the key
  private int keyLocation;

  /**
   * Creates an instance of a keyboard shortcut object.
   *
   * @param keyCode code of the key
   */
  public Shortcut(final int keyCode) {
    this(keyCode, -1);
  }

  /**
   * Creates an instance of a keyboard shortcut object.
   *
   * @param keyCode code of the key
   * @param keyLocation location of the key
   */
  public Shortcut(final int keyCode, final int keyLocation) {
    assert keyCode >= 0;
    assert keyLocation >= -1;
    this.keyCode = keyCode;
    this.keyLocation = keyLocation;
    log.fine("New Shortcut created, key code: " + keyCode +
	     ", location: " + keyLocation);
  }

  /**
   * Sets the code of the key.
   *
   * @return the code of the key
   */
  public int getKeyCode() {
    return keyCode;
  }

  /**
   * Sets the location of the key.
   *
   * @return the location of the key
   */
  public int getKeyLocation() {
    return keyLocation;
  }
}
