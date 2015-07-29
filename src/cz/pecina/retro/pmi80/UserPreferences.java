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
import java.util.prefs.Preferences;
import java.util.Locale;
import java.util.Arrays;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import cz.pecina.retro.common.Parameters;
import cz.pecina.retro.common.Application;
import cz.pecina.retro.gui.GUI;
import cz.pecina.retro.cpu.SimpleMemory;

/**
 * Static user preferences to be imported on start-up.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public final class UserPreferences {

  // static logger
  private static final Logger log =
    Logger.getLogger(UserPreferences.class.getName());

  // typical mask dimensions, in base-size pixels
  private static final int TYPICAL_MASK_WIDTH = 500;
  private static final int TYPICAL_MASK_HEIGHT = 400;

  // size coefficient (typical mask is sized so that it takes
  // no less than this constant * screen width/height)
  private static final double SIZE_COEFFICIENT = 0.8;

  // GUI pixel size
  private static int pixelSize;

  // the language tag
  private static String locale;

  // memory parameters
  private static int startROM, startRAM;

  // gets preferences from the backing store
  private static void getPreferences() {
    if (Parameters.preferences == null) {
      Parameters.preferences =
	Preferences.userNodeForPackage(UserPreferences.class);

      try {
	Parameters.preferences.sync();
      } catch (Exception exception) {
	throw Application.createError(UserPreferences.class, "backingStore");
      }
      pixelSize = Parameters.preferences.getInt("pixelSize", 0);
      if (pixelSize == 0) {
	// use maximum pixelSize reasonably fitting the screen
	final Rectangle r =
	  GraphicsEnvironment.getLocalGraphicsEnvironment()
	  .getMaximumWindowBounds();
	for (int i: Constants.PIXEL_SIZES)
	  if (((i * TYPICAL_MASK_WIDTH) > (SIZE_COEFFICIENT * r.width)) ||
	      ((i * TYPICAL_MASK_HEIGHT) > (SIZE_COEFFICIENT * r.height))) {
	    break;
	  } else {
	    pixelSize = i;
	  }
	if (pixelSize == 0) {  // no size fits
	  throw Application.createError(UserPreferences.class, "smallScreen");
	}
	Parameters.preferences.putInt("pixelSize", pixelSize);
      }

      locale = Parameters.preferences.get("locale", null);
      if (locale == null) {
	locale = Locale.getDefault().toLanguageTag();
	if (!Arrays.asList(Constants.SUPPORTED_LOCALES).contains(locale)) {
	  locale = Constants.SUPPORTED_LOCALES[0];  // use the default locale
	}
	Parameters.preferences.put("locale", locale);
      }

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
    }
    log.finer("User preferences retrieved");
  }
    

  /**
   * Gets GUI pixel size.
   *
   * @return GUI pixel size
   */
  public static int getPixelSize() {
    getPreferences();
    log.fine("Pixel size retrieved from user preferences: " + pixelSize);
    return pixelSize;
  }

  /**
   * Sets GUI pixel size.
   *
   * @param pixelSize GUI pixel size
   */
  public static void setPixelSize(final int pixelSize) {
    log.fine("New pixel size requested: " + pixelSize);
    assert Arrays.asList(Constants.PIXEL_SIZES).contains(pixelSize);
    getPreferences();
    if (pixelSize != UserPreferences.pixelSize) {
      UserPreferences.pixelSize = pixelSize;
      Parameters.preferences.putInt("pixelSize", pixelSize);
      log.fine("Pixel size in user preferences set to: " + pixelSize);
      GUI.setPixelSize(pixelSize);
      GUI.redrawAllResizeables();
    } else {
      log.finer("No change in pixel size, user preferences not updated");
    }
    log.fine("Exit from setPixelSize");
  }

  /**
   * Gets language tag.
   *
   * @return language tag
   */
  public static String getLocale() {
    getPreferences();
    log.fine("Locale retrieved from user preferences: " + locale);
    return locale;
  }

  /**
   * Sets language tag.
   *
   * @param locale language tag
   */
  public static void setLocale(final String locale) {
    log.fine("New lcaole requested: " + locale);
    assert Arrays.asList(Constants.SUPPORTED_LOCALES).contains(locale);
    getPreferences();
    if (locale != UserPreferences.locale) {
      UserPreferences.locale = locale;
      Parameters.preferences.put("locale", locale);
      log.fine("Locale in user preferences set to: " + locale);
      Application.setLocale(Locale.forLanguageTag(locale));
      Application.updateLocale();
      Application.redrawAllLocalized();
    } else {
      log.finer("No change in locale, user preferences not updated");
    }
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

  // default constructor disabled
  private UserPreferences() {};
}
