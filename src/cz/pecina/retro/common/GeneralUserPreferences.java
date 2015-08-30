/* GeneralUserPreferences.java
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

package cz.pecina.retro.common;

import java.util.logging.Logger;

import java.util.Locale;
import java.util.Arrays;

import java.util.prefs.Preferences;
import java.util.prefs.BackingStoreException;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import cz.pecina.retro.common.GeneralConstants;
import cz.pecina.retro.common.Parameters;
import cz.pecina.retro.common.Application;

import cz.pecina.retro.cpu.SimpleMemory;

import cz.pecina.retro.gui.GUI;

/**
 * Static user preferences to be imported on start-up (general).
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public abstract class GeneralUserPreferences {

  // static logger
  private static final Logger log =
    Logger.getLogger(GeneralUserPreferences.class.getName());

  /**
   * String indicating null value in preferences.
   */
  public static final String NULL_STRING = "null";

  // node class
  private static Class nodeClass;
  
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
  
  // gets preferences from the backing store
  protected static void getGeneralPreferences() {
    if (Parameters.preferences == null) {
      assert nodeClass != null;
      Parameters.preferences =
	Preferences.userNodeForPackage(nodeClass);

      try {
	Parameters.preferences.sync();
      } catch (final BackingStoreException | IllegalStateException exception) {
	throw Application.createError(GeneralUserPreferences.class,
				      "backingStore");
      }
      pixelSize = Parameters.preferences.getInt("pixelSize", 0);
      if (pixelSize == 0) {
	// use maximum pixelSize reasonably fitting the screen
	final Rectangle r =
	  GraphicsEnvironment.getLocalGraphicsEnvironment()
	  .getMaximumWindowBounds();
	for (int i: GeneralConstants.PIXEL_SIZES)
	  if (((i * TYPICAL_MASK_WIDTH) > (SIZE_COEFFICIENT * r.width)) ||
	      ((i * TYPICAL_MASK_HEIGHT) > (SIZE_COEFFICIENT * r.height))) {
	    break;
	  } else {
	    pixelSize = i;
	  }
	if (pixelSize == 0) {  // no size fits
	  throw Application.createError(GeneralUserPreferences.class,
					"smallScreen");
	}
	Parameters.preferences.putInt("pixelSize", pixelSize);
      }

      locale = Parameters.preferences.get("locale", null);
      if (locale == null) {
	locale = Locale.getDefault().toLanguageTag();
	if (!Arrays.asList(GeneralConstants.SUPPORTED_LOCALES)
	    .contains(locale)) {
	  locale =
	    GeneralConstants.SUPPORTED_LOCALES[0];  // use the default locale
	}
	Parameters.preferences.put("locale", locale);
      }
    }
    log.finer("General user preferences retrieved");
  }

  /**
   * Sets the node class.
   *
   * @param nodeClass the node class
   */
  public static void setNodeClass(final Class nodeClass) {
    log.fine("Setting node class: " + nodeClass);
    assert nodeClass != null;
    GeneralUserPreferences.nodeClass = nodeClass;
  }

  /**
   * Gets GUI pixel size.
   *
   * @return GUI pixel size
   */
  public static int getPixelSize() {
    getGeneralPreferences();
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
    assert Arrays.asList(GeneralConstants.PIXEL_SIZES).contains(pixelSize);
    getGeneralPreferences();
    if (pixelSize != GeneralUserPreferences.pixelSize) {
      GeneralUserPreferences.pixelSize = pixelSize;
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
    getGeneralPreferences();
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
    assert Arrays.asList(GeneralConstants.SUPPORTED_LOCALES).contains(locale);
    getGeneralPreferences();
    if (locale != GeneralUserPreferences.locale) {
      GeneralUserPreferences.locale = locale;
      Parameters.preferences.put("locale", locale);
      log.fine("Locale in user preferences set to: " + locale);
      Application.setLocale(Locale.forLanguageTag(locale));
      Application.updateLocale();
      Application.redrawAllLocalized();
    } else {
      log.finer("No change in locale, user preferences not updated");
    }
  }
}
