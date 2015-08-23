/* LEDMatrixPanel.java
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

package cz.pecina.retro.ledmatrix;

import java.util.logging.Logger;

import cz.pecina.retro.gui.BackgroundFixedPane;

/**
 * 32x32 LED matrix panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class LEDMatrixPanel extends BackgroundFixedPane {

  // static logger
  private static final Logger log =
    Logger.getLogger(LEDMatrixPanel.class.getName());

  // matrix geometry
  private static final int LED_OFFSET_X = 4;
  private static final int LED_OFFSET_Y = 4;

  /**
   * Creates a new LED matrix panel.
   *
   * @param hardware the LED matrix panel hardware object
   */
  public LEDMatrixPanel(final LEDMatrixHardware hardware) {
    super("ledmatrix/LEDMatrixPanel/mask", "plastic", "darkgray");
    assert hardware != null;

    // set up the LED matrix
    hardware.getLEDMatrixElement().place(this, LED_OFFSET_X, LED_OFFSET_Y);

    log.fine("LED matrix panel set up");
  }
}
