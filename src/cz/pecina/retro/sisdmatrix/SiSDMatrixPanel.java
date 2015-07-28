/* SiSDMatrixPanel.java
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

package cz.pecina.retro.sisdmatrix;

import java.util.logging.Logger;
import cz.pecina.retro.gui.BackgroundFixedPane;

/**
 * 4x24 SiSD matrix panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class SiSDMatrixPanel extends BackgroundFixedPane {

  // static logger
  private static final Logger log =
    Logger.getLogger(SiSDMatrixPanel.class.getName());

  // matrix geometry
  private static final int ELEMENT_GRID_X = 12;
  private static final int ELEMENT_GRID_Y = 18;
  private static final int ELEMENT_OFFSET_X = 6;
  private static final int ELEMENT_OFFSET_Y = 6;

  /**
   * Creates a new SiSD matrix panel.
   *
   * @param hardware the SiSD matrix panel hardware object
   */
  public SiSDMatrixPanel(final SiSDMatrixHardware hardware) {
    super("sisdmatrix/SiSDMatrixPanel/mask", "plastic", "darkgray");
    assert hardware != null;

    // set up the element matrix
    for (int line = 0; line < SiSDMatrixHardware.NUMBER_LINES; line++) {
      for (int column = 0;
	   column < SiSDMatrixHardware.NUMBER_COLUMNS;
	   column++) {
	hardware.getElement(line, column).place(
	  this,
	  (column * ELEMENT_GRID_X) + ELEMENT_OFFSET_X,
	  (line * ELEMENT_GRID_Y) + ELEMENT_OFFSET_Y);
      }
    }

    log.fine("SiSD matrix panel set up");
  }
}
