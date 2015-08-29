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
import java.util.Arrays;

import java.util.prefs.Preferences;
import java.util.prefs.BackingStoreException;

import java.awt.Color;

import cz.pecina.retro.common.GeneralUserPreferences;
import cz.pecina.retro.common.Application;
import cz.pecina.retro.common.Parameters;
import cz.pecina.retro.common.Sound;

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

  // default volume
  private static final int DEFAULT_VOLUME = 67;
  
  // true if preferences already retrieved
  private static boolean retrieved;
  
  // sound mixer parameters
  private static boolean tapeRecorderMute, speakerMute;
  private static int tapeRecorderVolume, speakerVolume;
  
  // the computer model
  private static int model;

  // keyboard shortcuts
  private static Shortcuts shortcuts = new Shortcuts();

  // the color mode
  private static int colorMode;

  // the custom colors
  private static PMDColor[] customColors = new PMDColor[4];

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

      if (hasKey("tapeRecorder.mute")) {
	tapeRecorderMute = Parameters.preferences.getBoolean("tapeRecorder.mute", false);
      } else {
	Parameters.preferences.putBoolean("tapeRecorder.mute", false);
      }
      tapeRecorderVolume = Parameters.preferences.getInt("tapeRecorder.volume", -1);
      if (tapeRecorderVolume == -1) {
	tapeRecorderVolume = DEFAULT_VOLUME;
	Parameters.preferences.putInt("tapeRecorder.volume", tapeRecorderVolume);
      }
      if (hasKey("speaker.mute")) {
	speakerMute = Parameters.preferences.getBoolean("speaker.mute", false);
      } else {
	Parameters.preferences.putBoolean("speaker.mute", false);
      }
      speakerVolume = Parameters.preferences.getInt("speaker.volume", -1);
      if (speakerVolume == -1) {
	speakerVolume = DEFAULT_VOLUME;
	Parameters.preferences.putInt("speaker.volume", speakerVolume);
      }
      
      model = Parameters.preferences.getInt("model", -1);
      if (model == -1) {
	model = Constants.DEFAULT_MODEL;
	Parameters.preferences.putInt("model", model);
      }

      boolean shortcutsDefined;
      for (String key:  Parameters.preferences.keys()) {
	if (key.startsWith("shortcut.")) {
	  final String id = key.substring(9);
	  final String listString = Parameters.preferences.get(key, null);
	  if (listString != null) {
	    shortutsDefined = true;
	    final List<Integer> list = new ArrayList<>();
	    for (String buttonString: string.split(",")) {
	      list.add(Integer.parseInt(buttonString));
	    }
	    shortcuts.put(new Shortcut(id) list);
	  }
	}
      }
      if (!shortcutsDefined) {
	
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
   * Sets the tape recorder mute setting.
   *
   * @param mute {@code true} if the sound from the tape recorder is to be muted
   */
  public static void setTapeRecorderMute(final boolean mute) {
    getPreferences();
    tapeRecorderMute = mute;
    Parameters.preferences.putBoolean("tapeRecorder.mute", mute);
    Parameters.sound.setMute(Sound.TAPE_RECORDER_CHANNEL, mute);
    log.fine("Tape recorder mute set to: " + mute);
  }

  /**
   * Tests the tape recorder mute setting.
   *
   * @return {@code true} if the sound from the tape recorder is muted
   */
  public static boolean isTapeRecorderMute() {
    getPreferences();
    log.finer("Tape recorder mute retrieved from user preferences: " +
	      tapeRecorderMute);
    return tapeRecorderMute;
  }

  /**
   * Sets the tape recorder volume setting.
   *
   * @param volume the volume setting for the sound from the tape recorder
   */
  public static void setTapeRecorderVolume(final int volume) {
    assert (volume >= 0) && (volume <= 100);
    getPreferences();
    tapeRecorderVolume = volume;
    Parameters.preferences.putInt("tapeRecorder.volume", volume);
    Parameters.sound.setVolume(Sound.TAPE_RECORDER_CHANNEL, volume / 100f);
    log.fine("Tape recorder volume set to: " + volume);
  }

  /**
   * Gets the tape recorder volume setting.
   *
   * @return the volume setting for the sound from the tape recorder
   */
  public static int getTapeRecorderVolume() {
    getPreferences();
    log.finer("Tape recorder volume retrieved from user preferences: " +
	      tapeRecorderVolume);
    return tapeRecorderVolume;
  }

  /**
   * Sets the speaker mute setting.
   *
   * @param mute {@code true} if the sound from the speaker is to be muted
   */
  public static void setSpeakerMute(final boolean mute) {
    getPreferences();
    speakerMute = mute;
    Parameters.preferences.putBoolean("speaker.mute", mute);
    Parameters.sound.setMute(Sound.SPEAKER_CHANNEL, mute);
    log.fine("Speaker mute set to: " + mute);
  }

  /**
   * Tests the speaker mute setting.
   *
   * @return {@code true} if the sound from the speaker is muted
   */
  public static boolean isSpeakerMute() {
    getPreferences();
    log.finer("Speaker mute retrieved from user preferences: " +
	      speakerMute);
    return speakerMute;
  }

  /**
   * Sets the speaker volume setting.
   *
   * @param volume the volume setting for the sound from the speaker
   */
  public static void setSpeakerVolume(final int volume) {
    assert (volume >= 0) && (volume <= 100);
    getPreferences();
    speakerVolume = volume;
    Parameters.preferences.putInt("speaker.volume", volume);
    Parameters.sound.setVolume(Sound.SPEAKER_CHANNEL, volume / 100f);
    log.fine("Speaker volume set to: " + volume);
  }

  /**
   * Gets the speaker volume setting.
   *
   * @return the volume setting for the sound from the speaker
   */
  public static int getSpeakerVolume() {
    getPreferences();
    log.finer("Speaker volume retrieved from user preferences: " +
	      speakerVolume);
    return speakerVolume;
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
   * Update the keyboard shortcuts.
   */
  public static void updateShortcuts() {
    for (Shortcut shortcut: shortcuts.keySet()) {
      StringBuilder sb = new StringBuilder;
      boolean next;
      for (int i: shortcuts.get(shortcut)) {
	if (next) {
	  sb.append(",");
	} else {
	  next = true;
	}
	sb.append(Inte
	
  }

  /**
   * Gets the keyboard shortcuts.
   *
   * @return the keyboard shortcuts object
   */
  public static Shortcuts getShortcuts() {
    return shortcuts;
  }

  /**
   * Gets the default keyboard shortcut.
   *
   * @return shortcuts the default keyboard shortcuts object
   */
  public static Shortcut getDefaultShortcuts(final int number) {
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
    computer.getComputerHardware().getMemory().refreshVideoRAM();
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
    computer.getComputerHardware().getMemory().refreshVideoRAM();
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
  private UserPreferences() {}
}
