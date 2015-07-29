/* Application.java
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
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;

/**
 * Main package class.  It includes locale-related stuff and
 * application-wide constants.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public final class Application {

  // static logger
  private static final Logger log =
    Logger.getLogger(Application.class.getName());

  /**
   * XML Schema location prefix.
   */
  public static final String XSD_PREFIX = "http://www.pecina.cz/xsd/";

  // application-wide locale
  private static Locale locale;

  // map of localized modules
  private static final Map<Package, ResourceBundle> textResources =
    new HashMap<>();

  // list of objects that must be redrawn on a locale change
  private static final List<Localized> localized = new ArrayList<>();

  /**
   * Gets application-wide locale.
   *
   * @return application-wide locale
   */
  public static Locale getLocale() {
    log.finer("Locale '" + locale + "' retrieved from Application");
    return locale;
  }

  /**
   * Sets application-wide locale.
   *
   * @param locale application-wide locale
   */
  public static void setLocale(final Locale locale) {
    Application.locale = locale;
    log.finer("Locale set to '" + locale + "'");
  }

  /**
   * Adds a localized module.
   *
   * @param bundle object identifying the resource bundle to be added
   */
  public static void addModule(final Object bundle) {
    assert bundle != null;
    assert locale != null;
    final Package pack = bundle.getClass().getPackage();
    final String name = pack.getName();
    textResources.put(pack, PropertyResourceBundle
		      .getBundle(name + ".TextResources", locale));
    log.fine("New localized module added to Application: " + name);
  }

  /**
   * Adds localized modules.
   *
   * @param bundles objects identifying the resource bundles to be added
   */
  public static void addModules(final Object... bundles) {
    log.fine("Adding new localized modules to Application");
    for (Object bundle: bundles)
      addModule(bundle);
    log.fine("New localized modules added to Application");
  }

  /**
   * Updates all modules on a locale change.
   */
  public static void updateLocale() {
    for (Package pack: textResources.keySet())
      textResources.put(pack, PropertyResourceBundle
			.getBundle(pack.getName() + ".TextResources", locale));
    log.fine("All modules updated on a locale change");
  }

  /**
   * Adds an item to the list of objects that must be redrawn
   * on a locale change.
   *
   * @param item item to be added
   */
  public static void addLocalized(final Localized item) {
    localized.add(item);
    log.finer("Localized added: " + item);
  }

  /**
   * Removes an item from the list of objects that must be redrawn
   * on a locale change.
   *
   * @param item item to be removed
   */
  public static void removeLocalized(final Localized item) {
    localized.remove(item);
    log.finer("Localized removed: " + item);
  }

  /**
   * Redraws all objects in the list of objects that must be redrawn
   * on a locale change.
   */
  public static void redrawAllLocalized() {
    for (Localized item: localized)
      item.redrawOnLocaleChange();
    log.finer("All localized objects redrawn");
  }

  /**
   * Gets a localized text resource.
   *
   * @param  bundle the object identifying the resource bundle
   *                (normally <code>this</code> or a <code>Class</code>
   *                object for static references)
   * @param  key    the key for the desired string
   * @return        the string for the given key
   */
  public static String getString(final Object bundle, final String key) {
    log.finest("Text resource requested: " + key);
    assert bundle != null;
    assert (key != null) && !key.isEmpty();
    final Class cl =
      (bundle instanceof Class) ? (Class)bundle : bundle.getClass();
    final String s = textResources.get(cl.getPackage()).getString(key);
    log.finer("Text resource retrieved: " + key + " -> " + s);
    return s;
  }

  /**
   * Gets a localized error exception.
   *
   * @param  bundle   the object identifying the resource bundle
   *                  (normally <code>this</code> or a <code>Class</code>
   *                  object for static references)
   * @param  errorKey the key of the error message
   * @param  varargs  additional arguments for the message string
   * @return          the <code>RuntimeException</code> corresponding
   *                  to the key
   */
  public static RuntimeException createError(final Object bundle,
					     final String errorKey,
					     final Object... varargs) {
    log.finest("Exceptio requested: " + errorKey);
    assert bundle != null;
    assert (errorKey != null) && !errorKey.isEmpty();
    final Class cl =
      (bundle instanceof Class) ? (Class)bundle : bundle.getClass();
    final String message =
      textResources.get(cl.getPackage()).getString("error." + errorKey);
    return new RuntimeException(String.format(message, varargs));
  }

  // default constructor disabled
  private Application() {};
}
