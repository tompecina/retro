/* KeyboardLayout.java
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

import java.awt.event.KeyEvent;

import cz.pecina.retro.gui.Shortcut;

/**
 * Layout of keyboard keys.  It contains only physically existing keys,
 * dummy keys are added in {@link KeyboardHardware}.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class KeyboardLayout {

  // static logger
  private static final Logger log =
    Logger.getLogger(KeyboardLayout.class.getName());

  /**
   * Number of keys.
   */
  public static final int NUMBER_KEYS = 37;

  // array of keys
  private KeyboardKey keys[];

  /**
   * Creates the bare keyboard layout.
   *
   * @param keyboardHardware the keyboard hardware object used for the keys
   */
  public KeyboardLayout(final KeyboardHardware keyboardHardware) {
    log.fine("New keyboard layout creation started");
    assert keyboardHardware != null;

    keys = new KeyboardKey[NUMBER_KEYS];

    int n = -1;

    // modifier keys
    keys[++n] = new KeyboardKey(keyboardHardware, "alt", 0, 2, 4, 2);
    keys[++n] = new KeyboardKey(keyboardHardware, "ctrl", 171, 2, 4, 7);
    keys[++n] = new KeyboardKey(keyboardHardware, "shift", 0, 3, 4, 4);
    keys[++n] = new KeyboardKey(keyboardHardware, "num", 38, 3, 1, 4);

    // special modifier key
    keys[++n] = new KeyboardKey(keyboardHardware, "cs", 19, 3, 2, 4);

    // normal keys
    keys[++n] = new KeyboardKey(keyboardHardware, "q", 5, 0, 4, 0);
    keys[++n] = new KeyboardKey(keyboardHardware, "w", 24, 0, 2, 0);
    keys[++n] = new KeyboardKey(keyboardHardware, "e", 43, 0, 1, 0);
    keys[++n] = new KeyboardKey(keyboardHardware, "r", 62, 0, 0, 0);
    keys[++n] = new KeyboardKey(keyboardHardware, "t", 81, 0, 3, 0);
    keys[++n] = new KeyboardKey(keyboardHardware, "y", 100, 0, 3, 6);
    keys[++n] = new KeyboardKey(keyboardHardware, "u", 119, 0, 0, 6);
    keys[++n] = new KeyboardKey(keyboardHardware, "i", 138, 0, 1, 6);
    keys[++n] = new KeyboardKey(keyboardHardware, "o", 157, 0, 2, 6);
    keys[++n] = new KeyboardKey(keyboardHardware, "p", 176, 0, 4, 6);
    keys[++n] = new KeyboardKey(keyboardHardware, "a", 10, 1, 4, 1);
    keys[++n] = new KeyboardKey(keyboardHardware, "s", 29, 1, 2, 1);
    keys[++n] = new KeyboardKey(keyboardHardware, "d", 48, 1, 1, 1);
    keys[++n] = new KeyboardKey(keyboardHardware, "f", 67, 1, 0, 1);
    keys[++n] = new KeyboardKey(keyboardHardware, "g", 86, 1, 3, 1);
    keys[++n] = new KeyboardKey(keyboardHardware, "h", 105, 1, 3, 5);
    keys[++n] = new KeyboardKey(keyboardHardware, "j", 124, 1, 0, 5);
    keys[++n] = new KeyboardKey(keyboardHardware, "k", 143, 1, 1, 5);
    keys[++n] = new KeyboardKey(keyboardHardware, "l", 162, 1, 2, 5);
    keys[++n] = new KeyboardKey(keyboardHardware, "enter", 181, 1, 4, 5);
    keys[++n] = new KeyboardKey(keyboardHardware, "z", 19, 2, 2, 2);
    keys[++n] = new KeyboardKey(keyboardHardware, "x", 38, 2, 1, 2);
    keys[++n] = new KeyboardKey(keyboardHardware, "c", 57, 2, 0, 2);
    keys[++n] = new KeyboardKey(keyboardHardware, "v", 76, 2, 3, 2);
    keys[++n] = new KeyboardKey(keyboardHardware, "b", 95, 2, 3, 7);
    keys[++n] = new KeyboardKey(keyboardHardware, "n", 114, 2, 0, 7);
    keys[++n] = new KeyboardKey(keyboardHardware, "m", 133, 2, 1, 7);
    keys[++n] = new KeyboardKey(keyboardHardware, "up", 152, 2, 2, 7);
    keys[++n] = new KeyboardKey(keyboardHardware, "space", 57, 3, 0, 3);
    keys[++n] = new KeyboardKey(keyboardHardware, "left", 133, 3, 1, 8);
    keys[++n] = new KeyboardKey(keyboardHardware, "down", 152, 3, 2, 8);
    keys[++n] = new KeyboardKey(keyboardHardware, "right", 171, 3, 4, 8);

    assert n == (NUMBER_KEYS - 1);
    
    log.fine("New keyboard layout created and populated");
  }
  /**
   * Gets the array of keys.
   *
   * @return array of keys
   */
  public KeyboardKey[] getKeys() {
    log.finer("Getting the array of keys");
    return keys;
  }

  /**
   * Gets the key by the internal number.
   *
   * @param  number the internal number
   * @return        the key
   */
  public KeyboardKey getKey(final int number) {
    assert (number >= 0) && (number < NUMBER_KEYS);
    log.finer("Getting key by number: " + number);
    return keys[number];
  }
}
