/* DACDigital.java
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
 **/

package cz.pecina.retro.dacdigital;

import java.util.logging.Logger;

import cz.pecina.retro.peripherals.BasePortPeripheral;

import cz.pecina.retro.gui.GUI;

/**
 * Digital-to-analog converter with a digital voltmeter.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class DACDigital extends BasePortPeripheral {

  // static logger
  private static final Logger log =
    Logger.getLogger(DACDigital.class.getName());

  /**
   * The default base port.
   */
  protected static final int DEFAULT_BASE_PORT = 0x40;

  // hardware
  private DACDigitalHardware hardware;

  // enclosing frame
  private DACDigitalFrame frame;

  /**
   * Creates a new DACDigital.
   */
  public DACDigital() {
    super("dacdigital", DEFAULT_BASE_PORT);
    log.fine("New DACDigital control object created");
  }

  // for description see Peripheral
  @Override
  public void activate() {
    log.fine("DACDigital activating");
    hardware = new DACDigitalHardware(getBasePort());
    frame = new DACDigitalFrame(this, hardware);
    super.activate();
    log.fine("DACDigital activated");
  }

  // for description see Peripheral
  @Override
  public void deactivate() {
    super.deactivate();
    hardware.deactivate();
    GUI.removeResizeable(frame);
    frame.dispose();
    log.fine("DACDigital deactivated");
  }

  // for description see Peripheral
  @Override
  public void implementSettings() {
    super.implementSettings();
    if (isActive())
      hardware.reconnect(getBasePort());
    log.fine("Changed settings implemented for DACDigital");
  }
}
