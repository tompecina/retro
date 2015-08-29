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
  public static final int NUMBER_KEYS = 77;

  // array of keys
  private KeyboardKey keys[];

  /**
   * Creates the bare keyboard layout.  It should be further modified
   * according to the computer model by calling {@link #modify}.
   *
   * @param keyboardHardware the keyboard hardware object used for the keys
   */
  public KeyboardLayout(final KeyboardHardware keyboardHardware) {
    log.fine("New keyboard layout creation started");
    assert keyboardHardware != null;

    keys = new KeyboardKey[NUMBER_KEYS];

    keys[0] = new KeyboardKey(keyboardHardware, "K0", 1, 1, 0, 0);
    keys[1] = new KeyboardKey(keyboardHardware, "K1", 3, 1, 0, 1);
    keys[2] = new KeyboardKey(keyboardHardware, "K2", 5, 1, 0, 2);
    keys[3] = new KeyboardKey(keyboardHardware, "K3", 7, 1, 0, 3);
    keys[4] = new KeyboardKey(keyboardHardware, "K4", 9, 1, 0, 4);
    keys[5] = new KeyboardKey(keyboardHardware, "K5", 11, 1, 0, 5);
    keys[6] = new KeyboardKey(keyboardHardware, "K6", 13, 1, 0, 6);
    keys[7] = new KeyboardKey(keyboardHardware, "K7", 15, 1, 0, 7);
    keys[8] = new KeyboardKey(keyboardHardware, "K8", 17, 1, 0, 8);
    keys[9] = new KeyboardKey(keyboardHardware, "K9", 19, 1, 0, 9);
    keys[10] = new KeyboardKey(keyboardHardware, "K10", 21, 1, 0, 10);
    keys[11] = new KeyboardKey(keyboardHardware, "K11", 23, 1, 0, 11);
    keys[12] = new KeyboardKey(keyboardHardware, "WRK", 25, 1, 0, 12);
    keys[13] = new KeyboardKey(keyboardHardware, "CD", 27, 1, 0, 13);
    keys[14] = new KeyboardKey(keyboardHardware, "RCL", 29, 1, 0, 14);
    keys[15] = new KeyboardKey(keyboardHardware, "RST", 31, 1, -1, -1);
    keys[15].setReset();
    keys[16] = new KeyboardKey(keyboardHardware, "1", 1, 3, 1, 0);
    keys[17] = new KeyboardKey(keyboardHardware, "2", 3, 3, 1, 1);
    keys[18] = new KeyboardKey(keyboardHardware, "3", 5, 3, 1, 2);
    keys[19] = new KeyboardKey(keyboardHardware, "4", 7, 3, 1, 3);
    keys[20] = new KeyboardKey(keyboardHardware, "5", 9, 3, 1, 4);
    keys[21] = new KeyboardKey(keyboardHardware, "6", 11, 3, 1, 5);
    keys[22] = new KeyboardKey(keyboardHardware, "7", 13, 3, 1, 6);
    keys[23] = new KeyboardKey(keyboardHardware, "8", 15, 3, 1, 7);
    keys[24] = new KeyboardKey(keyboardHardware, "9", 17, 3, 1, 8);
    keys[25] = new KeyboardKey(keyboardHardware, "0", 19, 3, 1, 9);
    keys[26] = new KeyboardKey(keyboardHardware, "underscore", 21, 3, 1, 10);
    keys[27] = new KeyboardKey(keyboardHardware, "blank", 23, 3, 1, 11);
    keys[28] = new KeyboardKey(keyboardHardware, "INS", 25, 3, 1, 12);
    keys[29] = new KeyboardKey(keyboardHardware, "DEL", 27, 3, 1, 13);
    keys[30] = new KeyboardKey(keyboardHardware, "CLR", 29, 3, 1, 14);
    keys[31] = new KeyboardKey(keyboardHardware, "q", 2, 5, 2, 0);
    keys[32] = new KeyboardKey(keyboardHardware, "w", 4, 5, 2, 1);
    keys[33] = new KeyboardKey(keyboardHardware, "e", 6, 5, 2, 2);
    keys[34] = new KeyboardKey(keyboardHardware, "r", 8, 5, 2, 3);
    keys[35] = new KeyboardKey(keyboardHardware, "t", 10, 5, 2, 4);
    keys[36] = new KeyboardKey(keyboardHardware, "z", 12, 5, 2, 5);
    keys[37] = new KeyboardKey(keyboardHardware, "u", 14, 5, 2, 6);
    keys[38] = new KeyboardKey(keyboardHardware, "i", 16, 5, 2, 7);
    keys[39] = new KeyboardKey(keyboardHardware, "o", 18, 5, 2, 8);
    keys[40] = new KeyboardKey(keyboardHardware, "p", 20, 5, 2, 9);
    keys[41] = new KeyboardKey(keyboardHardware, "bigat", 22, 5, 2, 10);
    keys[42] = new KeyboardKey(keyboardHardware, "backslash", 24, 5, 2, 11);
    keys[43] = new KeyboardKey(keyboardHardware, "left", 26, 5, 2, 12);
    keys[44] = new KeyboardKey(keyboardHardware, "home", 28, 5, 2, 13);
    keys[45] = new KeyboardKey(keyboardHardware, "right", 30, 5, 2, 14);
    keys[46] = new KeyboardKey(keyboardHardware, "a", 3, 7, 3, 0);
    keys[47] = new KeyboardKey(keyboardHardware, "s", 5, 7, 3, 1);
    keys[48] = new KeyboardKey(keyboardHardware, "d", 7, 7, 3, 2);
    keys[49] = new KeyboardKey(keyboardHardware, "f", 9, 7, 3, 3);
    keys[50] = new KeyboardKey(keyboardHardware, "g", 11, 7, 3, 4);
    keys[51] = new KeyboardKey(keyboardHardware, "h", 13, 7, 3, 5);
    keys[52] = new KeyboardKey(keyboardHardware, "j", 15, 7, 3, 6);
    keys[53] = new KeyboardKey(keyboardHardware, "k", 17, 7, 3, 7);
    keys[54] = new KeyboardKey(keyboardHardware, "l", 19, 7, 3, 8);
    keys[55] = new KeyboardKey(keyboardHardware, "semicolon", 21, 7, 3, 9);
    keys[56] = new KeyboardKey(keyboardHardware, "colon", 23, 7, 3, 10);
    keys[57] = new KeyboardKey(keyboardHardware, "revbracket", 25, 7, 3, 11);
    keys[58] = new KeyboardKey(keyboardHardware, "left2", 27, 7, 3, 12);
    keys[59] = new KeyboardKey(keyboardHardware, "end", 29, 7, 3, 13);
    keys[60] = new KeyboardKey(keyboardHardware, "right2", 31, 7, 3, 14);
    keys[61] = new KeyboardKey(keyboardHardware, "shift", 4, 9, -1, -1);
    keys[61].setShift();
    keys[62] = new KeyboardKey(keyboardHardware, "y", 6, 9, 4, 1);
    keys[63] = new KeyboardKey(keyboardHardware, "x", 8, 9, 4, 2);
    keys[64] = new KeyboardKey(keyboardHardware, "c", 10, 9, 4, 3);
    keys[65] = new KeyboardKey(keyboardHardware, "v", 12, 9, 4, 4);
    keys[66] = new KeyboardKey(keyboardHardware, "b", 14, 9, 4, 5);
    keys[67] = new KeyboardKey(keyboardHardware, "n", 16, 9, 4, 6);
    keys[68] = new KeyboardKey(keyboardHardware, "m", 18, 9, 4, 7);
    keys[69] = new KeyboardKey(keyboardHardware, "comma", 20, 9, 4, 8);
    keys[70] = new KeyboardKey(keyboardHardware, "period", 22, 9, 4, 9);
    keys[71] = new KeyboardKey(keyboardHardware, "slash", 24, 9, 4, 10);
    keys[72] = new KeyboardKey(keyboardHardware, "shift", 26, 9, -1, -1);
    keys[72].setShift();
    keys[73] = new KeyboardKey(keyboardHardware, "STOP", 28, 9, -1, -1);
    keys[73].setStop();
    keys[74] = new KeyboardKey(keyboardHardware, "EOL", 30, 9, 4, 13);
    keys[75] = new KeyboardKey(keyboardHardware, "EOL", 32, 9, 4, 14);
    keys[76] = new KeyboardKey(keyboardHardware, "longspace", 13, 11, 4, 0);

    log.fine("New keyboard layout created and populated");
  }
  
  /**
   * Modifies the keyboard layout according to the computer model.
   *
   * @param model the computer model
   */
  public void modify( final int model) {
    log.fine("Modifying keyboard layout for: " + model);
    if (model == 0) {
      getKey(27).setCap("blank");
      getKey(41).setCap("bigat");
      getKey(57).setCap("revbracket");
      getKey(76).setCap("longspace");
      getKey(76).setOffsetX(13);
    } else {
      getKey(27).setCap("brace");
      getKey(41).setCap("at");
      getKey(57).setCap("bracket");
      getKey(76).setCap("shortspace");
      getKey(76).setOffsetX(15);
    }
    log.finer("Keyboard layout modified");
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
