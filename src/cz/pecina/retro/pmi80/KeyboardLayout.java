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

package cz.pecina.retro.pmi80;

import java.util.logging.Logger;
import java.awt.event.KeyEvent;

/**
 * Layout of keyboard buttons.  It contains only physically existing buttons,
 * dummy buttons are added in {@link KeyboardHardware}.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class KeyboardLayout {

  // static logger
  private static final Logger log =
    Logger.getLogger(KeyboardLayout.class.getName());

  /**
   * Number of keyboard rows.
   */
  public static final int NUMBER_BUTTON_ROWS = 5;

  /**
   * Number of keyboard columns.
   */
  public static final int NUMBER_BUTTON_COLUMNS = 5;

  // matrix of buttons
  private final KeyboardButton[][] buttons =
    new KeyboardButton[NUMBER_BUTTON_ROWS][NUMBER_BUTTON_COLUMNS];

  /**
   * Creates the keyboard layout.
   *
   * @param keyboardHardware the keyboard hardware object used for the buttons
   */
  public KeyboardLayout(final KeyboardHardware keyboardHardware) {
    log.fine("New keyboard layout creation started");
    assert keyboardHardware != null;

    buttons[0][0] = new KeyboardButton(keyboardHardware,
				       "RE",
				       -1,
				       -1,
				       KeyEvent.VK_Z,
				       "toolTip.RE");
    buttons[0][0].setReset();
    buttons[0][1] = new KeyboardButton(keyboardHardware,
				       "I",
				       -1,
				       -1,
				       KeyEvent.VK_I,
				       "toolTip.I");
    buttons[0][1].setInterrupt();
    buttons[0][2] = new KeyboardButton(keyboardHardware,
				       "EX",
				       3,
				       2,
				       KeyEvent.VK_X,
				       "toolTip.EX");
    buttons[0][3] = new KeyboardButton(keyboardHardware,
				       "R",
				       3,
				       1,
				       KeyEvent.VK_R,
				       "toolTip.R");
    buttons[0][4] = new KeyboardButton(keyboardHardware,
				       "BR",
				       4,
				       0,
				       KeyEvent.VK_Q,
				       "toolTip.BR");
    buttons[1][0] = new KeyboardButton(keyboardHardware,
				       "C",
				       5,
				       2,
				       KeyEvent.VK_C,
				       null);
    buttons[1][1] = new KeyboardButton(keyboardHardware,
				       "D",
				       4,
				       2,
				       KeyEvent.VK_D,
				       null);
    buttons[1][2] = new KeyboardButton(keyboardHardware,
				       "E",
				       5,
				       1,
				       KeyEvent.VK_E,
				       null);
    buttons[1][3] = new KeyboardButton(keyboardHardware,
				       "F",
				       4,
				       1,
				       KeyEvent.VK_F,
				       null);
    buttons[1][4] = new KeyboardButton(keyboardHardware,
				       "M",
				       5,
				       0,
				       KeyEvent.VK_M,
				       "toolTip.M");
    buttons[2][0] = new KeyboardButton(keyboardHardware,
				       "8",
				       2,
				       2,
				       KeyEvent.VK_NUMPAD8,
				       null);
    buttons[2][1] = new KeyboardButton(keyboardHardware,
				       "9",
				       6,
				       2,
				       KeyEvent.VK_NUMPAD9,
				       null);
    buttons[2][2] = new KeyboardButton(keyboardHardware,
				       "A",
				       2,
				       1,
				       KeyEvent.VK_A,
				       null);
    buttons[2][3] = new KeyboardButton(keyboardHardware,
				       "B",
				       6,
				       1,
				       KeyEvent.VK_B,
				       null);
    buttons[2][4] = new KeyboardButton(keyboardHardware,
				       "L",
				       2,
				       0,
				       KeyEvent.VK_L,
				       "toolTip.L");
    buttons[3][0] = new KeyboardButton(keyboardHardware,
				       "4",
				       1,
				       2,
				       KeyEvent.VK_NUMPAD4,
				       null);
    buttons[3][1] = new KeyboardButton(keyboardHardware,
				       "5",
				       7,
				       2,
				       KeyEvent.VK_NUMPAD5,
				       null);
    buttons[3][2] = new KeyboardButton(keyboardHardware,
				       "6",
				       1,
				       1,
				       KeyEvent.VK_NUMPAD6,
				       null);
    buttons[3][3] = new KeyboardButton(keyboardHardware,
				       "7",
				       7,
				       1,
				       KeyEvent.VK_NUMPAD7, null);
    buttons[3][4] = new KeyboardButton(keyboardHardware,
				       "S",
				       1,
				       0,
				       KeyEvent.VK_S,
				       "toolTip.S");
    buttons[4][0] = new KeyboardButton(keyboardHardware,
				       "0",
				       0,
				       2,
				       KeyEvent.VK_NUMPAD0,
				       null);
    buttons[4][1] = new KeyboardButton(keyboardHardware,
				       "1",
				       8,
				       2,
				       KeyEvent.VK_NUMPAD1,
				       null);
    buttons[4][2] = new KeyboardButton(keyboardHardware,
				       "2",
				       0,
				       1,
				       KeyEvent.VK_NUMPAD2,
				       null);
    buttons[4][3] = new KeyboardButton(keyboardHardware,
				       "3",
				       8,
				       1,
				       KeyEvent.VK_NUMPAD3,
				       null);
    buttons[4][4] = new KeyboardButton(keyboardHardware,
				       "EQ",
				       8,
				       0,
				       KeyEvent.VK_ENTER,
				       "toolTip.EQ");
	
    log.fine("New keyboard layout created and populated");
  }

  /**
   * Gets the button.
   *
   * @param  row    the row
   * @param  column the column
   * @return the button
   */
  public KeyboardButton getButton(final int row, final int column) {
    assert (row >= 0) && (row < NUMBER_BUTTON_ROWS);
    assert (column >= 0) && (column < NUMBER_BUTTON_COLUMNS);
    return buttons[row][column];
  }
}
