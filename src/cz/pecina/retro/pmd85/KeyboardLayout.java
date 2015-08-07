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
    keys.add(new KeyboardKey(keyboardHardware, "K1", 2, 1, 3, 1, 0));
    keys.add(new KeyboardKey(keyboardHardware, "K2", 3, 1, 5, 2, 0));
    keys.add(new KeyboardKey(keyboardHardware, "K3", 4, 1, 7, 3, 0));
    keys.add(new KeyboardKey(keyboardHardware, "K4", 5, 1, 9, 4, 0));
    keys.add(new KeyboardKey(keyboardHardware, "K5", 6, 1, 11, 5, 0));
    keys.add(new KeyboardKey(keyboardHardware, "K6", 7, 1, 13, 6, 0));
    keys.add(new KeyboardKey(keyboardHardware, "K7", 8, 1, 15, 7, 0));
    keys.add(new KeyboardKey(keyboardHardware, "K8", 9, 1, 17, 8, 0));
    keys.add(new KeyboardKey(keyboardHardware, "K9", 10, 1, 19, 9, 0));
    keys.add(new KeyboardKey(keyboardHardware, "K10", 11, 1, 21, 10, 0));
    keys.add(new KeyboardKey(keyboardHardware, "K11", 12, 1, 23, 11, 0));
    keys.add(new KeyboardKey(keyboardHardware, "WRK", 13, 1, 25, 12, 0));
    keys.add(new KeyboardKey(keyboardHardware, "CD", 14, 1, 27, 13, 0));
    keys.add(new KeyboardKey(keyboardHardware, "RCL", 15, 1, 29, 14, 0));
    final KeyboardKey reset =
      new KeyboardKey(keyboardHardware, "RST", 78, 1, 31, -1, -1);
    reset.setReset();
    keys.add(reset);
    keys.add(new KeyboardKey(keyboardHardware, "1", 16, 3, 1, 0, 1));
    keys.add(new KeyboardKey(keyboardHardware, "2", 17, 3, 3, 1, 1));
    keys.add(new KeyboardKey(keyboardHardware, "3", 18, 3, 5, 2, 1));
    keys.add(new KeyboardKey(keyboardHardware, "4", 19, 3, 7, 3, 1));
    keys.add(new KeyboardKey(keyboardHardware, "5", 20, 3, 9, 4, 1));
    keys.add(new KeyboardKey(keyboardHardware, "6", 21, 3, 11, 5, 1));
    keys.add(new KeyboardKey(keyboardHardware, "7", 22, 3, 13, 6, 1));
    keys.add(new KeyboardKey(keyboardHardware, "8", 23, 3, 15, 7, 1));
    keys.add(new KeyboardKey(keyboardHardware, "9", 24, 3, 17, 8, 1));
    keys.add(new KeyboardKey(keyboardHardware, "0", 25, 3, 19, 9, 1));
    keys.add(new KeyboardKey(keyboardHardware, "underscore", 26, 3, 21, 10, 1));
    keys.add(new KeyboardKey(keyboardHardware, "blank", 27, 3, 23, 11, 1));
    keys.add(new KeyboardKey(keyboardHardware, "INS", 28, 3, 25, 12, 1));
    keys.add(new KeyboardKey(keyboardHardware, "DEL", 29, 3, 27, 13, 1));
    keys.add(new KeyboardKey(keyboardHardware, "CLR", 30, 3, 29, 14, 1));
    keys.add(new KeyboardKey(keyboardHardware, "q", 31, 5, 2, 0, 2));
    keys.add(new KeyboardKey(keyboardHardware, "w", 32, 5, 4, 1, 2));
    keys.add(new KeyboardKey(keyboardHardware, "e", 33, 5, 6, 2, 2));
    keys.add(new KeyboardKey(keyboardHardware, "r", 34, 5, 8, 3, 2));
    keys.add(new KeyboardKey(keyboardHardware, "t", 35, 5, 10, 4, 2));
    keys.add(new KeyboardKey(keyboardHardware, "z", 36, 5, 12, 5, 2));
    keys.add(new KeyboardKey(keyboardHardware, "u", 37, 5, 14, 6, 2));
    keys.add(new KeyboardKey(keyboardHardware, "i", 38, 5, 16, 7, 2));
    keys.add(new KeyboardKey(keyboardHardware, "o", 39, 5, 18, 8, 2));
    keys.add(new KeyboardKey(keyboardHardware, "p", 40, 5, 20, 9, 2));
    keys.add(new KeyboardKey(keyboardHardware, "bigat", 41, 5, 22, 10, 2));
    keys.add(new KeyboardKey(keyboardHardware, "backslash", 42, 5, 24, 11, 2));
    keys.add(new KeyboardKey(keyboardHardware, "left", 43, 5, 26, 12, 2));
    keys.add(new KeyboardKey(keyboardHardware, "home", 44, 5, 28, 13, 2));
    keys.add(new KeyboardKey(keyboardHardware, "right", 45, 5, 30, 14, 2));
    keys.add(new KeyboardKey(keyboardHardware, "a", 46, 6, 3, 0, 3));
    keys.add(new KeyboardKey(keyboardHardware, "s", 47, 6, 5, 1, 3));
    keys.add(new KeyboardKey(keyboardHardware, "d", 48, 6, 7, 2, 3));
    keys.add(new KeyboardKey(keyboardHardware, "f", 49, 6, 9, 3, 3));
    keys.add(new KeyboardKey(keyboardHardware, "g", 50, 6, 11, 4, 3));
    keys.add(new KeyboardKey(keyboardHardware, "h", 51, 6, 13, 5, 3));
    keys.add(new KeyboardKey(keyboardHardware, "j", 52, 6, 15, 6, 3));
    keys.add(new KeyboardKey(keyboardHardware, "k", 53, 6, 17, 7, 3));
    keys.add(new KeyboardKey(keyboardHardware, "l", 54, 6, 19, 8, 3));
    keys.add(new KeyboardKey(keyboardHardware, "semicolon", 55, 6, 21, 9, 3));
    keys.add(new KeyboardKey(keyboardHardware, "colon", 56, 6, 23, 10, 3));
    keys.add(new KeyboardKey(keyboardHardware, "revbracket", 57, 6, 25, 11, 3));
    keys.add(new KeyboardKey(keyboardHardware, "left2", 58, 6, 27, 12, 3));
    keys.add(new KeyboardKey(keyboardHardware, "end", 59, 6, 29, 13, 3));
    keys.add(new KeyboardKey(keyboardHardware, "right2", 60, 6, 31, 14, 3));
    final KeyboardKey leftShift =
      new KeyboardKey(keyboardHardware, "shift", 61, 7, 4, -1, -1);
    leftShift.setShift();
    keys.add(leftShift);
    keys.add(new KeyboardKey(keyboardHardware, "y", 62, 7, 6, 1, 4));
    keys.add(new KeyboardKey(keyboardHardware, "x", 63, 7, 8, 2, 4));
    keys.add(new KeyboardKey(keyboardHardware, "c", 64, 7, 10, 3, 4));
    keys.add(new KeyboardKey(keyboardHardware, "v", 65, 7, 12, 4, 4));
    keys.add(new KeyboardKey(keyboardHardware, "b", 66, 7, 14, 5, 4));
    keys.add(new KeyboardKey(keyboardHardware, "n", 67, 7, 16, 6, 4));
    keys.add(new KeyboardKey(keyboardHardware, "m", 68, 7, 18, 7, 4));
    keys.add(new KeyboardKey(keyboardHardware, "comma", 69, 7, 20, 8, 4));
    keys.add(new KeyboardKey(keyboardHardware, "period", 70, 7, 22, 9, 4));
    keys.add(new KeyboardKey(keyboardHardware, "slash", 71, 7, 24, 10, 4));
    final KeyboardKey rightShift =
      new KeyboardKey(keyboardHardware, "shift", 72, 7, 26, -1, -1);
    rightShift.setShift();
    keys.add(rightShift);
    final KeyboardKey stop =
      new KeyboardKey(keyboardHardware, "STOP", 73, 7, 28, -1, -1);
    stop.setStop();
    keys.add(stop);
    keys.add(new KeyboardKey(keyboardHardware, "EOL", 74, 7, 30, 13, 4));
    keys.add(new KeyboardKey(keyboardHardware, "EOL", 75, 7, 32, 14, 4));
    final KeyboardKey space =
      new KeyboardKey(keyboardHardware, "longspace", 79, 7, 13, 0, 4);
    space.setWidth(12);
    keys.add(space);

    log.fine("New keyboard layout created and populated");
  }
  
  /**
   * Modifies the keyboard layout according to the computer model.
   *
   * @param model the computer model
   */
  public modify( final Constants.Model model) {
    if (model != PMD_85_1) {
      keys.getKeyByContact(27).setCap("brace");
      keys.getKeyByContact(41).setCap("at");
      keys.getKeyByContact(57).setCap("bracket");
      keys.getKeyByContact(79).setCap("shortspace");
      keys.getKeyByContact(79).setX(15);
      keys.getKeyByContact(79).setWidth(8);
    }
  }
}
