/* Peripheral.java
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

package cz.pecina.retro.peripherals;

import java.util.logging.Logger;

import java.util.prefs.Preferences;

import javax.swing.JPanel;

import cz.pecina.retro.common.Application;
import cz.pecina.retro.common.Parameters;

/**
 * An abstract peripheral object.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public abstract class Peripheral {

  // static logger
  private static final Logger log =
    Logger.getLogger(Peripheral.class.getName());

  /**
   * Name of the peripheral.
   */
  protected String name;

  // activity flag
  private boolean active;

  /**
   * User preferences for the computer/peripheral combination.
   */
  protected Preferences preferences;

  /**
   * Creates a new Peripheral object.
   *
   * @param name the name of the peripheral
   */
  public Peripheral(final String name) {
    this.name = name;
    preferences = Parameters.preferences.node(name);
    Application.addModule(this);
    log.fine("New peripheral created: " + name);
  }

  /**
   * Activates the peripheral.
   */
  public void activate() {
    active = true;
    log.fine("Peripheral activated: " + name);
  }

  /**
   * Deactivates the peripheral.
   */
  public void deactivate() {
    active = false;
    log.fine("Peripheral deactivated: " + name);
  }

  /**
   * Gets the activity state of the peripheral.
   *
   * @return <code>true</code> if the peripheral is active,
   *         <code>false</code> otherwise
   */
  public boolean isActive() {
    return active;
  }

  /**
   * Gets text to be used in the list of peripherals.
   *
   * @return text to be used in the list of peripherals
   */
  public String getLabelText() {
    return Application.getString(this, "labelText");
  }

  /**
   * Gets text to be used as the title of the frame.
   *
   * @return text to be used as the title of the frame
   */
  public String getFrameTitle() {
    return Application.getString(this, "frameTitle");
  }

  /**
   * Gets text to be used as the title of the settings tab.
   *
   * @return text to be used as the title of the settings tab
   */
  public String getSettingsTitle() {
    return Application.getString(this, "settingsTitle");
  }

  /**
   * Creates the settings panel for the peripheral.
   *
   * @return a <code>JPanel</code> object with the settings widgets of the
   *         peripheral or <code>null</code> if no settings are available
   */
  public JPanel createSettingsPanel() {
    log.fine("Peripheral panel not created for: " + name);
    return null;
  }

  /**
   * Implements changes to the settings.
   */
  public void implementSettings() {
    log.fine("Changed settings not implemented for: " + name);
  }
}
