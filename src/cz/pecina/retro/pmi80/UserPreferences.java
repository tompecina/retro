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
import java.util.Arrays;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.ResourceBundle;

import java.util.prefs.Preferences;
import java.util.prefs.BackingStoreException;

import cz.pecina.retro.common.GeneralUserPreferences;
import cz.pecina.retro.common.Application;
import cz.pecina.retro.common.Parameters;

import cz.pecina.retro.cpu.SimpleMemory;

import cz.pecina.retro.gui.Shortcut;
import cz.pecina.retro.gui.Shortcuts;

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

  // shortcuts prefixes
  private static final String SHORTCUT_PREFIX =
    "keyboard.shortcut.";
  private static final String DEFAULT_SHORTCUT_PREFIX =
    "keyboard.default.shortcut.";
  
  // true if preferences already retrieved
  private static boolean retrieved;
  
  // memory parameters
  private static int startROM, startRAM;

  // keyboard shortcuts
  private static Shortcuts shortcuts;

  // tests if a key is in Parameters.preferences
  private static boolean hasKey(final String key) {
    try {
      return Arrays.asList(Parameters.preferences.keys()).contains(key);
    } catch (final BackingStoreException exception) {
      log.fine("Backing store exception: " + exception.getMessage());
    }
    return false;
  }
  
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

      try {
	for (String key:  Parameters.preferences.keys()) {
	  if (key.startsWith(SHORTCUT_PREFIX)) {
	    if (shortcuts == null) {
	      shortcuts = new Shortcuts();
	    }
	    final String id = key.substring(SHORTCUT_PREFIX.length());
	    final String listString = Parameters.preferences.get(key, null);
	    if ((listString != null) && !listString.isEmpty()) {
	      final NavigableSet<Integer> list = new TreeSet<>();
	      for (String buttonString: listString.split(",")) {
		list.add(Integer.parseInt(buttonString));
	      }
	      shortcuts.put(new Shortcut(id), list);
	    }
	  }
	}
      } catch (final BackingStoreException exception) {
	log.fine("Backing store exception: " + exception.getMessage());
      }
      if (shortcuts == null) {
	shortcuts = getDefaultShortcuts();
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
   * Update the keyboard shortcuts.
   */
  public static void updateShortcuts() {
    log.fine("Updating keyboard shortcuts");
    try {
      for (String key:  Parameters.preferences.keys()) {
	if (key.startsWith(SHORTCUT_PREFIX)) {
	  Parameters.preferences.remove(key);
	}
      }
    } catch (final BackingStoreException exception) {
      log.fine("Backing store exception: " + exception.getMessage());
    }
    for (Shortcut shortcut: shortcuts.keySet()) {
      StringBuilder sb = new StringBuilder();
      boolean first = true;;
      for (int i: shortcuts.get(shortcut)) {
	if (first) {
	  first = false;
	} else {
	  sb.append(",");
	}
	sb.append(String.valueOf(i));
      }
      Parameters.preferences.put(SHORTCUT_PREFIX + shortcut.getId(),
				 sb.toString());
    }
  }

  /**
   * Sets the keyboard shortcuts.
   *
   * @param shortcuts the keyboard shortcuts object
   */
  public static void setShortcuts(final Shortcuts shortcuts) {
    assert shortcuts != null;
    getPreferences();
    UserPreferences.shortcuts = shortcuts;
    updateShortcuts();
    log.finer("Shortcuts in user preferences set");
  }

  /**
   * Gets the keyboard shortcuts.
   *
   * @return the keyboard shortcuts object
   */
  public static Shortcuts getShortcuts() {
    getPreferences();
    return shortcuts;
  }

  /**
   * Gets the default keyboard shortcuts.
   *
   * @return shortcuts the default keyboard shortcuts object
   */
  public static Shortcuts getDefaultShortcuts() {
    final Shortcuts shortcuts = new Shortcuts();
    final ResourceBundle bundle =
      Application.getTextResources().get(UserPreferences.class.getPackage());
    for (String key: bundle.keySet()) {
      if (key.startsWith(DEFAULT_SHORTCUT_PREFIX)) {
	final String listString = bundle.getString(key);
	if ((listString != null) && !listString.isEmpty()) {
	  final NavigableSet<Integer> list = new TreeSet<>();
	  for (String buttonString: listString.split(",")) {
	    list.add(Integer.parseInt(buttonString));
	  }
	  shortcuts.put(new Shortcut(key), list);
	}
      }
    }
    return shortcuts;
  }

  // default constructor disabled
  private UserPreferences() {}
}
