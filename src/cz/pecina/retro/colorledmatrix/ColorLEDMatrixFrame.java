/* ColorLEDMatrixFrame.java
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

import cz.pecina.retro.peripherals.Peripheral;
import cz.pecina.retro.peripherals.PeripheralFrame;

import cz.pecina.retro.gui.Resizeable;
import cz.pecina.retro.gui.GUI;

/**
 * 32x32 color LED matrix panel frame.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class ColorLEDMatrixFrame extends PeripheralFrame implements Resizeable {

  // static logger
  private static final Logger log =
    Logger.getLogger(ColorLEDMatrixFrame.class.getName());

  // matrix panel
  private ColorLEDMatrixPanel panel;

  // hardware object
  private ColorLEDMatrixHardware hardware;

  /**
   * Creates a new color LED matrix frame.
   *
   * @param peripheral the Peripheral object
   * @param hardware   the LED matrix panel hardware object
   */
  public ColorLEDMatrixFrame(final Peripheral peripheral,
			final ColorLEDMatrixHardware hardware) {
    super(peripheral);
    assert hardware != null;
    this.hardware = hardware;
    panel = new ColorLEDMatrixPanel(hardware);
    add(panel);
    postamble();
    GUI.addResizeable(this);
    log.fine("New color LED matrix frame set up");
  }

  // for description see Resizeable
  @Override
  public void redrawOnPixelResize() {
    log.fine("ColorLEDMatrixFrame redraw started");
    remove(panel);
    panel = new ColorLEDMatrixPanel(hardware);
    add(panel);
    pack();
    log.fine("ColorLEDMatrixFrame redraw completed");
  }
}
