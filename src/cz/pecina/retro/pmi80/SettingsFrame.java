/* SettingsFrame.java
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

import cz.pecina.retro.common.Application;

import cz.pecina.retro.peripherals.Peripheral;

/**
 * The Settings frame.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class SettingsFrame extends HidingFrame {

  // static logger
  private static final Logger log =
    Logger.getLogger(SettingsFrame.class.getName());
    
  // the commputer control object
  private Computer computer;

  // settings panel
  private SettingsPanel settingsPanel;

  // array of available peripherals
  private Peripheral[] peripherals;

  /**
   * Creates the Settings frame.
   *
   * @param computer    the computer control object
   * @param peripherals array of available peripherals
   */
  public SettingsFrame(final Computer computer,
		       final Peripheral[] peripherals) {
    super(Application.getString(SettingsFrame.class, "settings.frameTitle"),
	  computer.getIconLayout().getIcon(IconLayout.ICON_POSITION_WHEEL));
    assert computer != null;
    log.fine("New SettingsFrame creation started");
    this.computer = computer;
    this.peripherals = peripherals;
    settingsPanel = new SettingsPanel(this, computer, peripherals);
    add(settingsPanel);
    pack();
    log.fine("SettingsFrame set up");
  }

  // for description see HidingFrame
  @Override
  public void setUp() {
    settingsPanel.setUp();
  }

  // redraw frame
  private void redraw() {
    log.fine("SettingsFrame redraw started");
    super.setTitle(Application.getString(this, "settings.frameTitle"));
    remove(settingsPanel);
    settingsPanel = new SettingsPanel(this, computer, peripherals);
    add(settingsPanel);
    pack();
    log.fine("SettingsFrame redraw completed");
  }

  // for description see Localized
  @Override
  public void redrawOnLocaleChange() {
    redraw();
  }
}
