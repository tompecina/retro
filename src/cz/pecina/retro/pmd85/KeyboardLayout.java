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

package cz.pecina.retro.pmd85;

import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;

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

  // array of the keys
  private List<KeyboardKey> keys = new ArrayList<>();

  /**
   * Creates the bare keyboard layout.  It should be further modified
   * according to the computer model by calling {@link #modify}.
   *
   * @param keyboardHardware the keyboard hardware object used for the keys
   */
  public KeyboardLayout(final KeyboardHardware keyboardHardware) {
    log.fine("New keyboard layout creation started");
    assert keyboardHardware != null;

    keys.add(new KeyboardKey(keyboardHardware, "K0", 1, 1, 1, 0, 0));
    keys.add(new KeyboardKey(keyboardHardware, "K1", 2, 3, 1, 0, 1));
    keys.add(new KeyboardKey(keyboardHardware, "K2", 3, 5, 1, 0, 2));
    keys.add(new KeyboardKey(keyboardHardware, "K3", 4, 7, 1, 0, 3));
    keys.add(new KeyboardKey(keyboardHardware, "K4", 5, 9, 1, 0, 4));
    keys.add(new KeyboardKey(keyboardHardware, "K5", 6, 11, 1, 0, 5));
    keys.add(new KeyboardKey(keyboardHardware, "K6", 7, 13, 1, 0, 6));
    keys.add(new KeyboardKey(keyboardHardware, "K7", 8, 15, 1, 0, 7));
    keys.add(new KeyboardKey(keyboardHardware, "K8", 9, 17, 1, 0, 8));
    keys.add(new KeyboardKey(keyboardHardware, "K9", 10, 19, 1, 0, 9));
    keys.add(new KeyboardKey(keyboardHardware, "K10", 11, 21, 1, 0, 10));
    keys.add(new KeyboardKey(keyboardHardware, "K11", 12, 23, 1, 0, 11));
    keys.add(new KeyboardKey(keyboardHardware, "WRK", 13, 25, 1, 0, 12));
    keys.add(new KeyboardKey(keyboardHardware, "CD", 14, 27, 1, 0, 13));
    keys.add(new KeyboardKey(keyboardHardware, "RCL", 15, 29, 1, 0, 14));
    final KeyboardKey reset =
      new KeyboardKey(keyboardHardware, "RST", 78, 31, 1, -1, -1);
    reset.setReset();
    keys.add(reset);
    keys.add(new KeyboardKey(keyboardHardware, "1", 16, 1, 3, 1, 0));
    keys.add(new KeyboardKey(keyboardHardware, "2", 17, 3, 3, 1, 1));
    keys.add(new KeyboardKey(keyboardHardware, "3", 18, 5, 3, 1, 2));
    keys.add(new KeyboardKey(keyboardHardware, "4", 19, 7, 3, 1, 3));
    keys.add(new KeyboardKey(keyboardHardware, "5", 20, 9, 3, 1, 4));
    keys.add(new KeyboardKey(keyboardHardware, "6", 21, 11, 3, 1, 5));
    keys.add(new KeyboardKey(keyboardHardware, "7", 22, 13, 3, 1, 6));
    keys.add(new KeyboardKey(keyboardHardware, "8", 23, 15, 3, 1, 7));
    keys.add(new KeyboardKey(keyboardHardware, "9", 24, 17, 3, 1, 8));
    keys.add(new KeyboardKey(keyboardHardware, "0", 25, 19, 3, 1, 9));
    keys.add(new KeyboardKey(keyboardHardware, "underscore", 26, 21, 3, 1, 10));
    keys.add(new KeyboardKey(keyboardHardware, "blank", 27, 23, 3, 1, 11));
    keys.add(new KeyboardKey(keyboardHardware, "INS", 28, 25, 3, 1, 12));
    keys.add(new KeyboardKey(keyboardHardware, "DEL", 29, 27, 3, 1, 13));
    keys.add(new KeyboardKey(keyboardHardware, "CLR", 30, 29, 3, 1, 14));
    keys.add(new KeyboardKey(keyboardHardware, "q", 31, 2, 5, 2, 0));
    keys.add(new KeyboardKey(keyboardHardware, "w", 32, 4, 5, 2, 1));
    keys.add(new KeyboardKey(keyboardHardware, "e", 33, 6, 5, 2, 2));
    keys.add(new KeyboardKey(keyboardHardware, "r", 34, 8, 5, 2, 3));
    keys.add(new KeyboardKey(keyboardHardware, "t", 35, 10, 5, 2, 4));
    keys.add(new KeyboardKey(keyboardHardware, "z", 36, 12, 5, 2, 5));
    keys.add(new KeyboardKey(keyboardHardware, "u", 37, 14, 5, 2, 6));
    keys.add(new KeyboardKey(keyboardHardware, "i", 38, 16, 5, 2, 7));
    keys.add(new KeyboardKey(keyboardHardware, "o", 39, 18, 5, 2, 8));
    keys.add(new KeyboardKey(keyboardHardware, "p", 40, 20, 5, 2, 9));
    keys.add(new KeyboardKey(keyboardHardware, "bigat", 41, 22, 5, 2, 10));
    keys.add(new KeyboardKey(keyboardHardware, "backslash", 42, 24, 5, 2, 11));
    keys.add(new KeyboardKey(keyboardHardware, "left", 43, 26, 5, 2, 12));
    keys.add(new KeyboardKey(keyboardHardware, "home", 44, 28, 5, 2, 13));
    keys.add(new KeyboardKey(keyboardHardware, "right", 45, 30, 5, 2, 14));
    keys.add(new KeyboardKey(keyboardHardware, "a", 46, 3, 7, 3, 0));
    keys.add(new KeyboardKey(keyboardHardware, "s", 47, 5, 7, 3, 1));
    keys.add(new KeyboardKey(keyboardHardware, "d", 48, 7, 7, 3, 2));
    keys.add(new KeyboardKey(keyboardHardware, "f", 49, 9, 7, 3, 3));
    keys.add(new KeyboardKey(keyboardHardware, "g", 50, 11, 7, 3, 4));
    keys.add(new KeyboardKey(keyboardHardware, "h", 51, 13, 7, 3, 5));
    keys.add(new KeyboardKey(keyboardHardware, "j", 52, 15, 7, 3, 6));
    keys.add(new KeyboardKey(keyboardHardware, "k", 53, 17, 7, 3, 7));
    keys.add(new KeyboardKey(keyboardHardware, "l", 54, 19, 7, 3, 8));
    keys.add(new KeyboardKey(keyboardHardware, "semicolon", 55, 21, 7, 3, 9));
    keys.add(new KeyboardKey(keyboardHardware, "colon", 56, 23, 7, 3, 10));
    keys.add(new KeyboardKey(keyboardHardware, "revbracket", 57, 25, 7, 3, 11));
    keys.add(new KeyboardKey(keyboardHardware, "left2", 58, 27, 7, 3, 12));
    keys.add(new KeyboardKey(keyboardHardware, "end", 59, 29, 7, 3, 13));
    keys.add(new KeyboardKey(keyboardHardware, "right2", 60, 31, 7, 3, 14));
    final KeyboardKey leftShift =
      new KeyboardKey(keyboardHardware, "shift", 61, 4, 9, -1, -1);
    leftShift.setShift();
    keys.add(leftShift);
    keys.add(new KeyboardKey(keyboardHardware, "y", 62, 6, 9, 4, 1));
    keys.add(new KeyboardKey(keyboardHardware, "x", 63, 8, 9, 4, 2));
    keys.add(new KeyboardKey(keyboardHardware, "c", 64, 10, 9, 4, 3));
    keys.add(new KeyboardKey(keyboardHardware, "v", 65, 12, 9, 4, 4));
    keys.add(new KeyboardKey(keyboardHardware, "b", 66, 14, 9, 4, 5));
    keys.add(new KeyboardKey(keyboardHardware, "n", 67, 16, 9, 4, 6));
    keys.add(new KeyboardKey(keyboardHardware, "m", 68, 18, 9, 4, 7));
    keys.add(new KeyboardKey(keyboardHardware, "comma", 69, 20, 9, 4, 8));
    keys.add(new KeyboardKey(keyboardHardware, "period", 70, 22, 9, 4, 9));
    keys.add(new KeyboardKey(keyboardHardware, "slash", 71, 24, 9, 4, 10));
    final KeyboardKey rightShift =
      new KeyboardKey(keyboardHardware, "shift", 72, 26, 9, -1, -1);
    rightShift.setShift();
    keys.add(rightShift);
    final KeyboardKey stop =
      new KeyboardKey(keyboardHardware, "STOP", 73, 28, 9, -1, -1);
    stop.setStop();
    keys.add(stop);
    keys.add(new KeyboardKey(keyboardHardware, "EOL", 74, 30, 9, 4, 13));
    keys.add(new KeyboardKey(keyboardHardware, "EOL", 75, 32, 9, 4, 14));
    keys.add(new KeyboardKey(keyboardHardware, "longspace", 79, 13, 11, 4, 0));

    log.fine("New keyboard layout created and populated");
  }
  
  /**
   * Modifies the keyboard layout according to the computer model.
   *
   * @param model the computer model
   */
  public void modify( final Constants.Model model) {
    log.fine("Modifying keyboard layout for: " + model);
    if (model != Constants.Model.PMD_85_1) {
      getKeyByContact(27).setCap("brace");
      getKeyByContact(41).setCap("at");
      getKeyByContact(57).setCap("bracket");
      getKeyByContact(79).setCap("shortspace");
      getKeyByContact(79).setOffsetX(15);
    }
    log.finer("Keyboard layout modified");
  }

  /**
   * Gets the list of keys.
   *
   * @return the list of keys
   */
  public List<KeyboardKey> getKeys() {
    log.finer("Getting the list of keys");
    return keys;
  }

  /**
   * Gets the key by the contact number.
   *
   * @param contact the contact number
   * @return        the key or <code>null</code> of not found
   */
  public KeyboardKey getKeyByContact(final int contact) {
    log.finer("Getting key by contact: " + contact);
    for (KeyboardKey key: keys) {
      if (key.getContact() == contact) {
	log.finest("Found key: " + key.getCap());
	return key;
      }
    }
    return null;
  }
}
