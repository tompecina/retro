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
  public static final int NUMBER_KEYS = 7;

  /**
   * Default keyboard shortcuts.
   */
  public static final Shortcut[] DEFAULT_SHORTCUTS = {
    new Shortcut(KeyEvent.VK_F1, KeyEvent.KEY_LOCATION_STANDARD),         // K0
    new Shortcut(KeyEvent.VK_F2, KeyEvent.KEY_LOCATION_STANDARD),         // K1
    new Shortcut(KeyEvent.VK_F3, KeyEvent.KEY_LOCATION_STANDARD),         // K2
    new Shortcut(KeyEvent.VK_F4, KeyEvent.KEY_LOCATION_STANDARD),         // K3
    new Shortcut(KeyEvent.VK_F5, KeyEvent.KEY_LOCATION_STANDARD),         // K4
    new Shortcut(KeyEvent.VK_F6, KeyEvent.KEY_LOCATION_STANDARD),         // K5
    new Shortcut(KeyEvent.VK_F7, KeyEvent.KEY_LOCATION_STANDARD),         // K6
    new Shortcut(KeyEvent.VK_F8, KeyEvent.KEY_LOCATION_STANDARD),         // K7
    new Shortcut(KeyEvent.VK_F9, KeyEvent.KEY_LOCATION_STANDARD),         // K8
    new Shortcut(KeyEvent.VK_F10, KeyEvent.KEY_LOCATION_STANDARD),        // K9
    new Shortcut(KeyEvent.VK_F11, KeyEvent.KEY_LOCATION_STANDARD),        // K10
    new Shortcut(KeyEvent.VK_F12, KeyEvent.KEY_LOCATION_STANDARD),        // K11
    // new Shortcut(KeyEvent.VK_BACKQUOTE, KeyEvent.KEY_LOCATION_STANDARD),  // WRK
    new Shortcut(KeyEvent.VK_TAB, KeyEvent.KEY_LOCATION_STANDARD),        // C-D
    new Shortcut(KeyEvent.VK_PAGE_UP, KeyEvent.KEY_LOCATION_STANDARD),    // RCL
    new Shortcut(KeyEvent.VK_CONTROL, KeyEvent.KEY_LOCATION_RIGHT),       // RST
    new Shortcut(KeyEvent.VK_1, KeyEvent.KEY_LOCATION_STANDARD),          // 1
    new Shortcut(KeyEvent.VK_2, KeyEvent.KEY_LOCATION_STANDARD),          // 2
    new Shortcut(KeyEvent.VK_3, KeyEvent.KEY_LOCATION_STANDARD),          // 3
    new Shortcut(KeyEvent.VK_4, KeyEvent.KEY_LOCATION_STANDARD),          // 4
    new Shortcut(KeyEvent.VK_5, KeyEvent.KEY_LOCATION_STANDARD),          // 5
    new Shortcut(KeyEvent.VK_6, KeyEvent.KEY_LOCATION_STANDARD),          // 6
    new Shortcut(KeyEvent.VK_7, KeyEvent.KEY_LOCATION_STANDARD),          // 7
    new Shortcut(KeyEvent.VK_8, KeyEvent.KEY_LOCATION_STANDARD),          // 8
    new Shortcut(KeyEvent.VK_9, KeyEvent.KEY_LOCATION_STANDARD),          // 9
    new Shortcut(KeyEvent.VK_0, KeyEvent.KEY_LOCATION_STANDARD),          // 0
    new Shortcut(KeyEvent.VK_UNDERSCORE, KeyEvent.KEY_LOCATION_STANDARD), // _
    // new Shortcut(KeyEvent.VK_BRACE, KeyEvent.KEY_LOCATION_STANDARD),      // }
    null};



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

    keys[0] = new KeyboardKey(keyboardHardware, "K0", 1, 1, 1, 0, 0);
    keys[1] = new KeyboardKey(keyboardHardware, "K1", 2, 3, 1, 0, 1);
    keys[2] = new KeyboardKey(keyboardHardware, "K2", 3, 5, 1, 0, 2);
    keys[3] = new KeyboardKey(keyboardHardware, "K3", 4, 7, 1, 0, 3);
    keys[4] = new KeyboardKey(keyboardHardware, "K4", 5, 9, 1, 0, 4);
    keys[5] = new KeyboardKey(keyboardHardware, "K5", 6, 11, 1, 0, 5);
    keys[6] = new KeyboardKey(keyboardHardware, "K6", 7, 13, 1, 0, 6);
    keys[7] = new KeyboardKey(keyboardHardware, "K7", 8, 15, 1, 0, 7);
    keys[8] = new KeyboardKey(keyboardHardware, "K8", 9, 17, 1, 0, 8);
    keys[9] = new KeyboardKey(keyboardHardware, "K9", 10, 19, 1, 0, 9);
    keys[10] = new KeyboardKey(keyboardHardware, "K10", 11, 21, 1, 0, 10);
    keys[11] = new KeyboardKey(keyboardHardware, "K11", 12, 23, 1, 0, 11);
    keys[12] = new KeyboardKey(keyboardHardware, "WRK", 13, 25, 1, 0, 12);
    keys[13] = new KeyboardKey(keyboardHardware, "CD", 14, 27, 1, 0, 13);
    keys[14] = new KeyboardKey(keyboardHardware, "RCL", 15, 29, 1, 0, 14);
    keys[15] = new KeyboardKey(keyboardHardware, "RST", 78, 31, 1, -1, -1);
    keys[15].setReset();
    keys[16] = new KeyboardKey(keyboardHardware, "1", 16, 1, 3, 1, 0);
    keys[17] = new KeyboardKey(keyboardHardware, "2", 17, 3, 3, 1, 1);
    keys[18] = new KeyboardKey(keyboardHardware, "3", 18, 5, 3, 1, 2);
    keys[19] = new KeyboardKey(keyboardHardware, "4", 19, 7, 3, 1, 3);
    keys[20] = new KeyboardKey(keyboardHardware, "5", 20, 9, 3, 1, 4);
    keys[21] = new KeyboardKey(keyboardHardware, "6", 21, 11, 3, 1, 5);
    keys[22] = new KeyboardKey(keyboardHardware, "7", 22, 13, 3, 1, 6);
    keys[23] = new KeyboardKey(keyboardHardware, "8", 23, 15, 3, 1, 7);
    keys[24] = new KeyboardKey(keyboardHardware, "9", 24, 17, 3, 1, 8);
    keys[25] = new KeyboardKey(keyboardHardware, "0", 25, 19, 3, 1, 9);
    keys[26] = new KeyboardKey(keyboardHardware, "underscore", 26, 21, 3, 1, 10);
    keys[27] = new KeyboardKey(keyboardHardware, "blank", 27, 23, 3, 1, 11);
    keys[28] = new KeyboardKey(keyboardHardware, "INS", 28, 25, 3, 1, 12);
    keys[29] = new KeyboardKey(keyboardHardware, "DEL", 29, 27, 3, 1, 13);
    keys[30] = new KeyboardKey(keyboardHardware, "CLR", 30, 29, 3, 1, 14);
    keys[31] = new KeyboardKey(keyboardHardware, "q", 31, 2, 5, 2, 0);
    keys[32] = new KeyboardKey(keyboardHardware, "w", 32, 4, 5, 2, 1);
    keys[33] = new KeyboardKey(keyboardHardware, "e", 33, 6, 5, 2, 2);
    keys[34] = new KeyboardKey(keyboardHardware, "r", 34, 8, 5, 2, 3);
    keys[35] = new KeyboardKey(keyboardHardware, "t", 35, 10, 5, 2, 4);
    keys[36] = new KeyboardKey(keyboardHardware, "z", 36, 12, 5, 2, 5);
    keys[37] = new KeyboardKey(keyboardHardware, "u", 37, 14, 5, 2, 6);
    keys[38] = new KeyboardKey(keyboardHardware, "i", 38, 16, 5, 2, 7);
    keys[39] = new KeyboardKey(keyboardHardware, "o", 39, 18, 5, 2, 8);
    keys[40] = new KeyboardKey(keyboardHardware, "p", 40, 20, 5, 2, 9);
    keys[41] = new KeyboardKey(keyboardHardware, "bigat", 41, 22, 5, 2, 10);
    keys[42] = new KeyboardKey(keyboardHardware, "backslash", 42, 24, 5, 2, 11);
    keys[43] = new KeyboardKey(keyboardHardware, "left", 43, 26, 5, 2, 12);
    keys[44] = new KeyboardKey(keyboardHardware, "home", 44, 28, 5, 2, 13);
    keys[45] = new KeyboardKey(keyboardHardware, "right", 45, 30, 5, 2, 14);
    keys[46] = new KeyboardKey(keyboardHardware, "a", 46, 3, 7, 3, 0);
    keys[47] = new KeyboardKey(keyboardHardware, "s", 47, 5, 7, 3, 1);
    keys[48] = new KeyboardKey(keyboardHardware, "d", 48, 7, 7, 3, 2);
    keys[49] = new KeyboardKey(keyboardHardware, "f", 49, 9, 7, 3, 3);
    keys[50] = new KeyboardKey(keyboardHardware, "g", 50, 11, 7, 3, 4);
    keys[51] = new KeyboardKey(keyboardHardware, "h", 51, 13, 7, 3, 5);
    keys[52] = new KeyboardKey(keyboardHardware, "j", 52, 15, 7, 3, 6);
    keys[53] = new KeyboardKey(keyboardHardware, "k", 53, 17, 7, 3, 7);
    keys[54] = new KeyboardKey(keyboardHardware, "l", 54, 19, 7, 3, 8);
    keys[55] = new KeyboardKey(keyboardHardware, "semicolon", 55, 21, 7, 3, 9);
    keys[56] = new KeyboardKey(keyboardHardware, "colon", 56, 23, 7, 3, 10);
    keys[57] = new KeyboardKey(keyboardHardware, "revbracket", 57, 25, 7, 3, 11);
    keys[58] = new KeyboardKey(keyboardHardware, "left2", 58, 27, 7, 3, 12);
    keys[59] = new KeyboardKey(keyboardHardware, "end", 59, 29, 7, 3, 13);
    keys[60] = new KeyboardKey(keyboardHardware, "right2", 60, 31, 7, 3, 14);
    keys[61] = new KeyboardKey(keyboardHardware, "shift", 61, 4, 9, -1, -1);
    keys[61].setShift();
    keys[62] = new KeyboardKey(keyboardHardware, "y", 62, 6, 9, 4, 1);
    keys[63] = new KeyboardKey(keyboardHardware, "x", 63, 8, 9, 4, 2);
    keys[64] = new KeyboardKey(keyboardHardware, "c", 64, 10, 9, 4, 3);
    keys[65] = new KeyboardKey(keyboardHardware, "v", 65, 12, 9, 4, 4);
    keys[66] = new KeyboardKey(keyboardHardware, "b", 66, 14, 9, 4, 5);
    keys[67] = new KeyboardKey(keyboardHardware, "n", 67, 16, 9, 4, 6);
    keys[68] = new KeyboardKey(keyboardHardware, "m", 68, 18, 9, 4, 7);
    keys[69] = new KeyboardKey(keyboardHardware, "comma", 69, 20, 9, 4, 8);
    keys[70] = new KeyboardKey(keyboardHardware, "period", 70, 22, 9, 4, 9);
    keys[71] = new KeyboardKey(keyboardHardware, "slash", 71, 24, 9, 4, 10);
    keys[72] = new KeyboardKey(keyboardHardware, "shift", 72, 26, 9, -1, -1);
    keys[72].setShift();
    keys[73] = new KeyboardKey(keyboardHardware, "STOP", 73, 28, 9, -1, -1);
    keys[73].setStop();
    keys[74] = new KeyboardKey(keyboardHardware, "EOL", 74, 30, 9, 4, 13);
    keys[75] = new KeyboardKey(keyboardHardware, "EOL", 75, 32, 9, 4, 14);
    keys[76] = new KeyboardKey(keyboardHardware, "longspace", 79, 13, 11, 4, 0);

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
   * Gets the array of keys.
   *
   * @return the array of keys
   */
  public KeyboardKey[] getKeys() {
    log.finer("Getting the array of keys");
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
