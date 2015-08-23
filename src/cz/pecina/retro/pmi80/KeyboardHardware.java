/* KeyboardHardware.java
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

import cz.pecina.retro.cpu.IOPin;

/**
 * Keyboard of the Tesla PMI-80 computer consisting of
 * a matrix of 5x5 buttons.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class KeyboardHardware {

  // static logger
  private static final Logger log =
    Logger.getLogger(KeyboardHardware.class.getName());

  /**
   * Number of keyboard hardware matrix columns.
   */
  public static final int NUMBER_MATRIX_COLUMNS = 9;

  /**
   * Number of keyboard hardware matrix rows.
   */
  public static final int NUMBER_MATRIX_ROWS = 3;

  // scan pins
  private final ScanPin[] scanPins = new ScanPin[NUMBER_MATRIX_ROWS];

  // matrices of button presses
  private final boolean[][] buffer =
    new boolean[NUMBER_MATRIX_COLUMNS][NUMBER_MATRIX_ROWS];
  private final boolean[][] matrix =
    new boolean[NUMBER_MATRIX_COLUMNS][NUMBER_MATRIX_ROWS];

  // matrix of buttons
  private final KeyboardButton[][] buttons =
    new KeyboardButton[NUMBER_MATRIX_COLUMNS][NUMBER_MATRIX_ROWS];

  // leyout of buttons
  private KeyboardLayout keyboardLayout;

  // the display hardware object
  private DisplayHardware displayHardware;

  /**
   * Creates the keyboard hardware object.
   *
   * @param displayHardware the display hardware object used (select pins
   *                        are shared between display and keyboard)
   */
  public KeyboardHardware(final DisplayHardware displayHardware) {
    log.fine("New keyboard hardware creation started");
    assert displayHardware != null;
    this.displayHardware = displayHardware;
    keyboardLayout = new KeyboardLayout(this);
    for (int row = 0; row < KeyboardLayout.NUMBER_BUTTON_ROWS; row++) {
      for (int column = 0;
	   column < KeyboardLayout.NUMBER_BUTTON_COLUMNS;
	   column++) {
	final KeyboardButton button = keyboardLayout.getButton(row, column);
	if (keyboardLayout.getButton(row, column).getMatrixColumn() != -1) {
	  buttons[button.getMatrixColumn()][button.getMatrixRow()] = button;
	}
      }
    }
    for (int column = 0; column < NUMBER_MATRIX_COLUMNS; column++) {
      for (int row = 0; row < NUMBER_MATRIX_ROWS; row++) {
	if (buttons[column][row] == null) {
	  buttons[column][row] = new KeyboardButton(column, row);
	}
      }
    }
    for (int row = 0; row < NUMBER_MATRIX_ROWS; row++) {
      scanPins[row] = new ScanPin(row);
    }
    resetBuffer();
    log.fine("New keyboard hardware created");
  }

  // scan pins
  private class ScanPin extends IOPin {
    private int number;

    private ScanPin(final int n) {
      super();
      assert (n >= 0) && (n < NUMBER_MATRIX_ROWS);
      number = n;
    }

    @Override
    public int query() {
      final int s = 15 - displayHardware.getSelect();
      final int selectedColumn = (s < NUMBER_MATRIX_COLUMNS) ? s : -1;
      return (selectedColumn == -1) ?
	1 :
	(buffer[selectedColumn][number] ? 0 : 1);
    }
  }

  /**
   * Gets the scan pin.
   *
   * @param  n the pin number
   * @return the pin object
   */
  public IOPin getScanPin(final int n) {
    assert (n >= 0) && (n < NUMBER_MATRIX_ROWS);
    return scanPins[n];
  }

  /**
   * Resets the matrix of button presses.
   */
  public void resetBuffer() {
    prepareMatrix();
    copyMatrixToBuffer();
  }

  /**
   * Updates the matrix of keyboard presses.
   */
  public void updateBuffer() {
    prepareMatrix();
    addMatrixToBuffer();
  }

  // prepares matrix of button presses
  private void prepareMatrix() {
    for (int column1 = 0; column1 < NUMBER_MATRIX_COLUMNS; column1++) {
      for (int row1 = 0; row1 < NUMBER_MATRIX_ROWS; row1++) {
	matrix[column1][row1] = buttons[column1][row1].isPressed();
      }
    }
    for (int column1 = 0; column1 < NUMBER_MATRIX_COLUMNS; column1++) {
      for (int row1 = 0; row1 < NUMBER_MATRIX_ROWS; row1++) {
	if (matrix[column1][row1]) {
	  for (int row2 = 0; row2 < NUMBER_MATRIX_ROWS; row2++) {
	    if (matrix[column1][row2]) {
	      for (int column2 = 0;
		   column2 < NUMBER_MATRIX_COLUMNS;
		   column2++) {
		if (matrix[column2][row1]) {
		  matrix[column2][row2] = true;
		}
	      }
	    }
	  }
	}
      }
    }
  }

  // copies matrix of button presses to buffer
  private void copyMatrixToBuffer() {
    for (int column = 0; column < NUMBER_MATRIX_COLUMNS; column++) {
      for (int row = 0; row < NUMBER_MATRIX_ROWS; row++) {
	buffer[column][row] = matrix[column][row];
      }
    }
  }
	
  // adds matrix of button presses to buffer
  private void addMatrixToBuffer() {
    for (int column = 0; column < NUMBER_MATRIX_COLUMNS; column++) {
      for (int row = 0; row < NUMBER_MATRIX_ROWS; row++) {
	buffer[column][row] |= matrix[column][row];
      }
    }
  }

  /**
   * Gets the keyboard layout object.
   *
   * @return the keyboard layout object
   */
  public KeyboardLayout getKeyboardLayout() {
    return keyboardLayout;
  }
}
