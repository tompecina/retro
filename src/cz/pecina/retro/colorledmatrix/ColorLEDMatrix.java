/* ColorLEDMatrix.java
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

package cz.pecina.retro.colorledmatrix;

import java.util.logging.Logger;

import cz.pecina.retro.peripherals.BasePortPeripheral;

import cz.pecina.retro.gui.GUI;

/**
 * 32x32 color LED matrix panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class ColorLEDMatrix extends BasePortPeripheral {

  // static logger
  private static final Logger log =
    Logger.getLogger(ColorLEDMatrix.class.getName());

  /**
   * The default base port.
   */
  protected static final int DEFAULT_BASE_PORT = 0x1c;

  // hardware
  private ColorLEDMatrixHardware hardware;

  // enclosing frame
  private ColorLEDMatrixFrame frame;

  /**
   * Creates a new color LED matrix panel.
   */
  public ColorLEDMatrix() {
    super("colorledmatrix", DEFAULT_BASE_PORT);
    log.finer("New color LED panel control object created");
  }

  // for description see Peripheral
  @Override
  public void activate() {
    log.fine("Color LED panel activating");
    hardware = new ColorLEDMatrixHardware(getBasePort());
    frame = new ColorLEDMatrixFrame(this, hardware);
    super.activate();
    log.fine("Color LED panel activated");
  }

  // for description see Peripheral
  @Override
  public void deactivate() {
    super.deactivate();
    hardware.deactivate();
    GUI.removeResizeable(frame);
    frame.dispose();
    log.fine("LED panel deactivated");
  }

  // for description see Peripheral
  @Override
  public void implementSettings() {
    super.implementSettings();
    if (isActive())
      hardware.reconnect(getBasePort());
    log.fine("Changed settings implemented for color LED matrix panel");
  }
}
