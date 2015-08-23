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

package cz.pecina.retro.pmd85;

import java.util.logging.Logger;

import java.util.Locale;

import java.util.prefs.Preferences;

import java.awt.Color;

import cz.pecina.retro.common.GeneralUserPreferences;
import cz.pecina.retro.common.Application;
import cz.pecina.retro.common.Parameters;

import cz.pecina.retro.gui.Shortcut;

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
  
  // the computer model
  private static int model;

  // keyboard shortcuts
  private static Shortcut[] shortcuts =
    new Shortcut[KeyboardLayout.NUMBER_KEYS];

  // the color mode
  private static int colorMode;

  // the custom colors
  private static PMDColor[] customColors = new PMDColor[4];

  /**
   * Gets preferences from the backing store.
   */
  public static void getPreferences() {
    if (!retrieved) {
      GeneralUserPreferences.getGeneralPreferences();
      Application.setLocale(Locale.forLanguageTag(
        GeneralUserPreferences.getLocale()));
      Application.addModule(UserPreferences.class);

      model = Parameters.preferences.getInt("model", -1);
      if (model == -1) {
	model = Constants.DEFAULT_MODEL;
	Parameters.preferences.putInt("model", model);
      }
      
      for (int i = 0; i < KeyboardLayout.NUMBER_KEYS; i++) {
	final String shortcutString =
	  Parameters.preferences.get("shortcut." + i, null);
	Shortcut shortcut;
	if (shortcutString == null) {
	  shortcut = getDefaultShortcut(i);
	} else if (shortcutString.equals(NULL_STRING)) {
	  shortcut = null;
	} else {
	  shortcut = new Shortcut(shortcutString);
	}
	shortcuts[i] = shortcut;
	Parameters.preferences.put("shortcut." + i,
				   (shortcut != null) ?
				   shortcut.getID() :
				   NULL_STRING);
      }
      
      colorMode = Parameters.preferences.getInt("colorMode", -1);
      if (colorMode == -1) {
	colorMode = PMDColor.DEFAULT_COLOR_MODE;
	Parameters.preferences.putInt("colorMode", colorMode);
      }

      for (int i = 0; i < 4; i++) {
	final int color =
	  Parameters.preferences.getInt("customColors.color." + i, -1);
	final boolean blinkFlag = 
	  Parameters.preferences.getBoolean("customColors.blink." + i, false);
	customColors[i] = (color == -1) ?
	                  PMDColor.DEFAULT_COLORS[i] :
	                  new PMDColor(new Color(color), blinkFlag);
	Parameters.preferences.putInt("customColors.color." + i,
				      customColors[i].getColor().getRGB());
	Parameters.preferences.putBoolean("customColors.blink." + i,
					  customColors[i].getBlinkFlag());
      }
      retrieved = true;
    }
    log.finer("User preferences retrieved");
  }

  /**
   * Sets the computer model.
   *
   * @param computer the computer object
   * @param model    the model
   */
  public static void setModel(final Computer computer, final int model) {
    assert computer != null;
    assert (model >= 0) && (model < Constants.NUMBER_MODELS);
    getPreferences();
    UserPreferences.model = model;
    Parameters.preferences.putInt("model", model);
    computer.getComputerHardware().setModel(computer, model);
    log.fine("Model in user preferences set to: " + model);
  }

  /**
   * Gets the computer model.
   *
   * @return the model
   */
  public static int getModel() {
    getPreferences();
    log.finer("Model retrieved from user preferences: " + model);
    return model;
  }

  /**
   * Sets the keyboard shortcut.
   *
   * @param keyboardLayout the keyboard layout
   * @param number         the internal key number
   * @param shortcut       the new keyboard shortcut or <code>null</code>
   *                       if none
   */
  public static void setShortcut(final KeyboardLayout keyboardLayout,
				 final int number,
				 final Shortcut shortcut) {
    assert (number >= 0) && (number < KeyboardLayout.NUMBER_KEYS);
    getPreferences();
    shortcuts[number] = shortcut;
    Parameters.preferences.put("shortcut." + number,
			       (shortcut != null) ?
			       shortcut.getID() :
			       NULL_STRING);
    keyboardLayout.getKey(number).setShortcut(shortcut);
    log.fine("Shortcut for key " + number +
	     " in user preferences set to: " +
	     ((shortcut != null) ? shortcut.getID() : "none"));
  }

  /**
   * Gets the keyboard shortcut.
   *
   * @param  number   the internal key number
   * @return shortcut the keyboard shortcut or <code>null</code>
   *                  if none
   */
  public static Shortcut getShortcut(final int number) {
    assert (number >= 0) && (number < KeyboardLayout.NUMBER_KEYS);
    getPreferences();
    final Shortcut shortcut = shortcuts[number];
    log.fine("Shortcut for key " + number +
	     " retrieved from user preferences: " +
	     ((shortcut != null) ? shortcut.getID() : "none"));
    return shortcut;
  }

  /**
   * Gets the default keyboard shortcut.
   *
   * @param  number   the internal key number
   * @return shortcut the default keyboard shortcut
   */
  public static Shortcut getDefaultShortcut(final int number) {
    assert (number >= 0) && (number < KeyboardLayout.NUMBER_KEYS);
    final String shortcutString = Application.getString(
      UserPreferences.class,
      "keyboard.default.shortcut." + number);
    return shortcutString.equals(NULL_STRING) ?
           null :
           new Shortcut(shortcutString);
  }

  /**
   * Sets the color mode.
   *
   * @param computer  the computer object
   * @param colorMode the color mode
   */
  public static void setColorMode(final Computer computer,
				  final int colorMode) {
    assert computer != null;
    assert (colorMode >= 0) && (colorMode < PMDColor.NUMBER_COLOR_MODES);
    getPreferences();
    UserPreferences.colorMode = colorMode;
    Parameters.preferences.putInt("colorMode", colorMode);
    computer.getComputerHardware().getDisplayHardware().getDisplay()
      .setColorMode(colorMode);
    log.fine("Color mode in user preferences set to: " + colorMode);
  }

  /**
   * Gets the color mode.
   *
   * @return the color mode
   */
  public static int getColorMode() {
    getPreferences();
    log.finer("Color mode retrieved from user preferences: " + colorMode);
    return colorMode;
  }

  /**
   * Sets the custom colors.
   *
   * @param computer     the computer object
   * @param customColors array of custom colors
   */
  public static void setCustomColors(final Computer computer,
				     final PMDColor[] customColors) {
    assert computer != null;
    assert (customColors != null) && (customColors.length == 4);
    getPreferences();
    UserPreferences.customColors = customColors;
    for (int i = 0; i < 4; i++) {
      Parameters.preferences.putInt("customColors.color." + i,
				    customColors[i].getColor().getRGB());
      Parameters.preferences.putBoolean("customColors.blink." + i,
					customColors[i].getBlinkFlag());
    }
    computer.getComputerHardware().getDisplayHardware().getDisplay()
      .setCustomColors(customColors);
    log.fine("Custom colors in user preferences set");
  }

  /**
   * Gets the custom colors.
   *
   * @return array of custom colors
   */
  public static PMDColor[] getCustomColors() {
    getPreferences();
    log.finer("Custom colors retrieved from user preferences");
    return customColors;
  }

  // default constructor disabled
  private UserPreferences() {};
}
