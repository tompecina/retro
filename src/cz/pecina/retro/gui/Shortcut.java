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

import java.awt.event.KeyEvent;

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

  // the extended code of the key
  private int extendedKeyCode;

  // location of the key (-1 = any)
  private int keyLocation;

  /**
   * Creates an instance of a keyboard shortcut object.
   *
   * @param ch character representing the key
   */
  public Shortcut(final char ch) {
    this(KeyEvent.getExtendedKeyCodeForChar(ch),
	 KeyEvent.KEY_LOCATION_STANDARD);
  }

  /**
   * Creates an instance of a keyboard shortcut object.
   *
   * @param extendedKeyCode the extended code of the key
   */
  public Shortcut(final int extendedKeyCode) {
    this(extendedKeyCode, -1);
  }

  /**
   * Creates an instance of a keyboard shortcut object.
   *
   * @param extendedKeyCode the extended code of the key
   * @param keyLocation     the location of the key
   */
  public Shortcut(final int extendedKeyCode, final int keyLocation) {
    assert extendedKeyCode >= 0;
    assert keyLocation >= -1;
    this.extendedKeyCode = extendedKeyCode;
    this.keyLocation = keyLocation;
    log.fine("New Shortcut created, extended key code: " + extendedKeyCode +
	     ", location: " + keyLocation);
  }

  /**
   * Creates an instance of a keyboard shortcut object.
   *
   * @param id the ID of the shortcut
   */
  public Shortcut(final String id) {
    switch (id.substring(0, 1)) {
      case "S":
	keyLocation = KeyEvent.KEY_LOCATION_STANDARD;
	break;
      case "L":
	keyLocation = KeyEvent.KEY_LOCATION_LEFT;
	break;
      case "R":
	keyLocation = KeyEvent.KEY_LOCATION_RIGHT;
	break;
      case "N":
	keyLocation = KeyEvent.KEY_LOCATION_NUMPAD;
	break;
      case "U":
	keyLocation = KeyEvent.KEY_LOCATION_UNKNOWN;
	break;
      default:
	keyLocation = -1;
	break;
    }
    extendedKeyCode = Integer.parseInt(id.substring(1));
  }
  
  /**
   * Gets the extended code of the key.
   *
   * @return the extended code of the key
   */
  public int getExtendedKeyCode() {
    return extendedKeyCode;
  }

  /**
   * Gets the location of the key.
   *
   * @return the location of the key ({@code -1}
   *          means any location)
   */
  public int getKeyLocation() {
    return keyLocation;
  }

  /**
   * Gets the ID of the shortcut.
   *
   * @return the ID of the shortcut
   */
  public String getId() {
    String location;
    if (keyLocation == KeyEvent.KEY_LOCATION_STANDARD) {
      location = "S";
    } else if (keyLocation == KeyEvent.KEY_LOCATION_LEFT) {
      location = "L";
    } else if (keyLocation == KeyEvent.KEY_LOCATION_RIGHT) {
      location = "R";
    } else if (keyLocation == KeyEvent.KEY_LOCATION_NUMPAD) {
      location = "N";
    } else if (keyLocation == KeyEvent.KEY_LOCATION_UNKNOWN) {
      location = "U";
    } else {
      location = "A";
    }
    return location + extendedKeyCode;
  }

  /**
   * Gets a textual description of the shortcut.  As there is no
   * distinction between "standard", "unknown" and "any" locations, the
   * information is incomplete and should never be used for shortcut
   * identification or serialization.  Use {@code #getId} instead.
   *
   * @return the long description of the shortcut
   */
  public String getDesc() {
    String location = "";
    if (keyLocation == KeyEvent.KEY_LOCATION_LEFT) {
      location = "/L";
    } else if (keyLocation == KeyEvent.KEY_LOCATION_RIGHT) {
      location = "/R";
    } else if (keyLocation == KeyEvent.KEY_LOCATION_NUMPAD) {
      location = "/N";
    }
    return KeyEvent.getKeyText(extendedKeyCode) + location;
  }

  // for description see Object
  @Override
  public boolean equals(final Object o) {
    log.finest("Comparing to: " + o);
    return (o != null) &&
           (o instanceof Shortcut) &&
           (((Shortcut)o).extendedKeyCode == extendedKeyCode) &&
           (((Shortcut)o).keyLocation == keyLocation);
  }
}
