/* UserPreferences.java
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
import java.util.Locale;
import java.util.prefs.Preferences;
import cz.pecina.retro.common.GeneralUserPreferences;
import cz.pecina.retro.common.Application;
import cz.pecina.retro.common.Parameters;
import cz.pecina.retro.gui.Shortcut;
import cz.pecina.retro.cpu.SimpleMemory;

/**
 * Static user preferences to be imported on start-up (emulator specific).
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public final class UserPreferences extends GeneralUserPreferences {

  // static logger
  private static final Logger log =
    Logger.getLogger(UserPreferences.class.getName());

  // true if preferences already retrieved
  private static boolean retrieved;
  
  // memory parameters
  private static int startROM, startRAM;

  // keyboard shortcuts
  private static Shortcut[][] shortcuts =
    new Shortcut[KeyboardLayout.NUMBER_BUTTON_ROWS]
                [KeyboardLayout.NUMBER_BUTTON_COLUMNS];

  /**
   * Gets preferences from the backing store.
   */
  public static void getPreferences() {
    if (!retrieved) {
      GeneralUserPreferences.getGeneralPreferences();
      Application.setLocale(Locale.forLanguageTag(
        GeneralUserPreferences.getLocale()));
      Application.addModule(UserPreferences.class);
      
      startROM = Parameters.preferences.getInt("startROM", -1);
      if (startROM == -1) {
	startROM = Constants.DEFAULT_START_ROM;
	Parameters.preferences.putInt("startROM", startROM);
      }
      
      startRAM = Parameters.preferences.getInt("startRAM", -1);
      if (startRAM == -1) {
	startRAM = Constants.DEFAULT_START_RAM;
	Parameters.preferences.putInt("startRAM", startRAM);
      }

      for (int row = 0; row < KeyboardLayout.NUMBER_BUTTON_ROWS; row++) {
	for (int column = 0;
	     column < KeyboardLayout.NUMBER_BUTTON_COLUMNS;
	     column++) {
	  final String shortcutString =
	    Parameters.preferences.get("shortcut." + row + "." + column, null);
	  Shortcut shortcut;
	  if (shortcutString == null) {
	    shortcut = getDefaultShortcut(row, column);
	  } else if (shortcutString.equals(NULL_STRING)) {
	    shortcut = null;
	  } else {
	    shortcut = new Shortcut(shortcutString);
	  }
	  shortcuts[row][column] = shortcut;
	  Parameters.preferences.put("shortcut." + row + "." + column,
				     (shortcut != null) ?
				     shortcut.getID() :
				     NULL_STRING);
	}
      }
      retrieved = true;
    }
    log.finer("User preferences retrieved");
  }

  /**
   * Sets start of non-writeable memory.
   *
   * @param startROM start of non-writeable memory (in KiB)
   */
  public static void setStartROM(final int startROM) {
    assert (startROM >= 0) && (startROM <= 64);
    getPreferences();
    UserPreferences.startROM = startROM;
    Parameters.preferences.putInt("startROM", startROM);
    ((SimpleMemory)Parameters.memoryObject).setStartROM(startROM);
    log.fine("Start ROM in user preferences set to: " + startROM);
  }

  /**
   * Gets start of non-writeable memory.
   *
   * @return start of non-writeable memory (in KiB)
   */
  public static int getStartROM() {
    getPreferences();
    log.fine("Start ROM retrieved from user preferences: " + startROM);
    return startROM;
  }

  /**
   * Sets start of writeable memory following the non-writeable block.
   *
   * @param startRAM start of writeable memory (in KiB)
   */
  public static void setStartRAM(final int startRAM) {
    assert (startRAM >= 0) && (startRAM <= 64);
    getPreferences();
    UserPreferences.startRAM = startRAM;
    Parameters.preferences.putInt("startRAM", startRAM);
    ((SimpleMemory)Parameters.memoryObject).setStartRAM(startRAM);
    log.fine("Start RAM in user preferences set to: " + startRAM);
  }

  /**
   * Gets start of writeable memory following the non-writeable block.
   *
   * @return start of writeable memory (in KiB)
   */
  public static int getStartRAM() {
    getPreferences();
    log.fine("Start RAM retrieved from user preferences: " + startROM);
    return startRAM;
  }

  /**
   * Sets the keyboard shortcut.
   *
   * @param keyboardLayout the keyboard layout
   * @param row            the row
   * @param column         the column
   * @param shortcut       the new keyboard shortcut or <code>null</code>
   *                       if none
   */
  public static void setShortcut(final KeyboardLayout keyboardLayout,
				 final int row,
				 final int column,
				 final Shortcut shortcut) {
    assert (row >= 0) && (row < KeyboardLayout.NUMBER_BUTTON_ROWS);
    assert (column >= 0) && (column < KeyboardLayout.NUMBER_BUTTON_COLUMNS);
    getPreferences();
    shortcuts[row][column] = shortcut;
    Parameters.preferences.put("shortcut." + row + "." + column,
			       (shortcut != null) ?
			       shortcut.getID() :
			       NULL_STRING);
    keyboardLayout.getButton(row, column).setShortcut(shortcut);
    log.fine("Shortcut for button (" + row + "," + column +
	     ") in user preferences set to: " +
	     ((shortcut != null) ? shortcut.getID() : "none"));
  }

  /**
   * Gets the keyboard shortcut.
   *
   * @param  row      the row
   * @param  column   the column
   * @return shortcut the keyboard shortcut or <code>null</code>
   *                  if none
   */
  public static Shortcut getShortcut(final int row,
				     final int column) {
    assert (row >= 0) && (row < KeyboardLayout.NUMBER_BUTTON_ROWS);
    assert (column >= 0) && (column < KeyboardLayout.NUMBER_BUTTON_COLUMNS);
    getPreferences();
    final Shortcut shortcut = shortcuts[row][column];
    log.fine("Shortcut for button (" + row + "," + column +
	     ") retrieved from user preferences: " +
	     ((shortcut != null) ? shortcut.getID() : "none"));
    return shortcut;
  }

  /**
   * Gets the default keyboard shortcut.
   *
   * @param  row      the row
   * @param  column   the column
   * @return shortcut the default keyboard shortcut
   */
  public static Shortcut getDefaultShortcut(final int row,
					    final int column) {
    assert (row >= 0) && (row < KeyboardLayout.NUMBER_BUTTON_ROWS);
    assert (column >= 0) && (column < KeyboardLayout.NUMBER_BUTTON_COLUMNS);
    final String shortcutString = Application.getString(
      UserPreferences.class,
      "keyboard.default.shortcut." + row + "." + column);
    return shortcutString.equals(NULL_STRING) ?
           null :
           new Shortcut(shortcutString);
  }

  // default constructor disabled
  private UserPreferences() {};
}
