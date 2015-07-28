/* LEDMatrixFrame.java
 *
 * Copyright (C) 2014-2015, Tomáš Pecina <tomas@pecina.cz>
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

package cz.pecina.retro.ledmatrix;

import java.util.logging.Logger;
import cz.pecina.retro.peripherals.Peripheral;
import cz.pecina.retro.peripherals.PeripheralFrame;
import cz.pecina.retro.gui.Resizeable;
import cz.pecina.retro.gui.GUI;

/**
 * 32x32 LED matrix panel frame.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class LEDMatrixFrame extends PeripheralFrame implements Resizeable {

  // static logger
  private static final Logger log =
    Logger.getLogger(LEDMatrixFrame.class.getName());

  // matrix panel
  private LEDMatrixPanel panel;

  // hardware object
  private LEDMatrixHardware hardware;

  /**
   * Creates a new LED matrix frame.
   *
   * @param peripheral the Peripheral object
   * @param hardware   the LED matrix panel hardware object
   */
  public LEDMatrixFrame(final Peripheral peripheral,
			final LEDMatrixHardware hardware) {
    super(peripheral);
    assert hardware != null;
    this.hardware = hardware;
    panel = new LEDMatrixPanel(hardware);
    add(panel);
    postamble();
    GUI.addResizeable(this);
    log.fine("New LED matrix frame set up");
  }

  // for description see Resizeable
  @Override
  public void redrawOnPixelResize() {
    log.fine("LEDMatrixFrame redraw started");
    remove(panel);
    panel = new LEDMatrixPanel(hardware);
    add(panel);
    pack();
    log.fine("LEDMatrixFrame redraw completed");
  }
}
