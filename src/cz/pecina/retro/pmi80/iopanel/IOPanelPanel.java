/* IOPanelPanel.java
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

package cz.pecina.retro.pmi80.iopanel;

import java.util.logging.Logger;
import cz.pecina.retro.gui.BackgroundFixedPane;

/**
 * Input/output panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class IOPanelPanel extends BackgroundFixedPane {

  // static logger
  private static final Logger log =
    Logger.getLogger(IOPanelPanel.class.getName());

  // element matrix geometry
  private static final int GRID_X = 30;
  private static final int GRID_Y = 25;
  private static final int OFFSET_X = 30;
  private static final int OFFSET_Y = 40;

  /**
   * Creates a new I/O panel.
   *
   * @param hardware the I/O panel hardware object
   */
  public IOPanelPanel(final IOPanelHardware hardware) {
    super("pmi80/iopanel/IOPanelPanel/mask", "plastic", "gray");
    assert hardware != null;

    // set up the elements
    for (int row = 0; row < IOPanelHardware.NUMBER_ROWS; row++) {
      for (int column = 0; column < IOPanelHardware.NUMBER_COLUMNS; column++) {
	hardware.getElement(row, column).place(this,
					       OFFSET_X + (GRID_X * column),
					       OFFSET_Y + (GRID_Y * row));
      }
    }

    log.fine("I/O panel panel set up");
  }
}
