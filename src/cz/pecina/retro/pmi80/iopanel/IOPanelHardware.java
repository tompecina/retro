/* IOPanelHardware.java
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
import cz.pecina.retro.cpu.IONode;
import cz.pecina.retro.pmi80.ComputerHardware;
import cz.pecina.retro.gui.GUI;

/**
 * Input/output panel hardware.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class IOPanelHardware {

  // static logger
  private static final Logger log =
    Logger.getLogger(IOPanelHardware.class.getName());

  /**
   * Number of element rows.
   */
  public static final int NUMBER_ROWS = 8;

  /**
   * Number of element columns.
   */
  public static final int NUMBER_COLUMNS = 4;

  /**
   * Number of elements.
   */
  public static final int NUMBER_ELEMENTS = NUMBER_ROWS * NUMBER_COLUMNS;

  // elements
  private final IOPanelElement[][] elements =
    new IOPanelElement[NUMBER_ROWS][NUMBER_COLUMNS];

  /**
   * Creates the IOPanel hardware object.
   *
   * @param computerHardware the computer hardware object
   */
  public IOPanelHardware(final ComputerHardware computerHardware) {
    log.fine("I/O panel creation started");
    assert computerHardware != null;
    for (int row = 0; row < NUMBER_ROWS; row++) {
      for (int column = 0; column < NUMBER_COLUMNS; column++) {
	final IOPanelElement element = new IOPanelElement();
	elements[row][column] = element;
	switch (column) {
	  case 0:
	    new IONode().add(computerHardware.getSystemPPI()
	      .getPin(8 + row)).add(element.getPin());
	    break;
	  case 1:
	    new IONode().add(computerHardware.getPeripheralPPI()
	      .getPin(row)).add(element.getPin());
	    break;
	  case 2:
	    new IONode().add(computerHardware.getPeripheralPPI()
	      .getPin(8 + row)).add(element.getPin());
	    break;
	  case 3:
	    new IONode().add(computerHardware.getPeripheralPPI()
	      .getPin(16 + row)).add(element.getPin());
	    break;
	}
      }
    }
    log.fine("New IOPanel hardware created");
  }

  /**
   * Gets the element.
   *
   * @param  row   the row of the element
   * @param  column the column of the element
   * @return the element object
   */
  public IOPanelElement getElement(final int row, final int column) {
    assert (row >= 0) && (row < NUMBER_ROWS);
    assert (column >= 0) && (column < NUMBER_COLUMNS);
    return elements[row][column];
  }

  /**
   * Deactivates hardware.
   */
  public void deactivate() {
    for (int row = 0; row < NUMBER_ROWS; row++) {
      for (int column = 0; column < NUMBER_COLUMNS; column++) {
	GUI.removeResizeable(elements[row][column]);
      }
    }
  }
}
