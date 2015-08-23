/* ADC.java
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

package cz.pecina.retro.adc;

import java.util.logging.Logger;

import cz.pecina.retro.peripherals.BasePortPeripheral;

import cz.pecina.retro.gui.GUI;

/**
 * 8-channel ADC.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class ADC extends BasePortPeripheral {

  // static logger
  private static final Logger log =
    Logger.getLogger(ADC.class.getName());

  /**
   * The default base port.
   */
  protected static final int DEFAULT_BASE_PORT = 0x50;

  // hardware
  private ADCHardware hardware;

  // enclosing frame
  private ADCFrame frame;

  /**
   * Creates a new ADC.
   */
  public ADC() {
    super("adc", DEFAULT_BASE_PORT);
    log.fine("New ADC control object created");
  }

  // for description see Peripheral
  @Override
  public void activate() {
    log.fine("ADC activating");
    hardware = new ADCHardware(getBasePort(),
			       ADCPanel.OFFSET_X,
			       ADCPanel.LENGTH);
    frame = new ADCFrame(this, hardware);
    super.activate();
    log.fine("ADC activated");
  }

  // for description see Peripheral
  @Override
  public void deactivate() {
    super.deactivate();
    hardware.deactivate();
    GUI.removeResizeable(frame);
    frame.dispose();
    log.fine("ADC deactivated");
  }

  // for description see Peripheral
  @Override
  public void implementSettings() {
    super.implementSettings();
    if (isActive()) {
      hardware.reconnect(getBasePort());
    }
    log.fine("Changed settings implemented for ADC");
  }
}
