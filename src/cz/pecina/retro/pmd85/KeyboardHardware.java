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

package cz.pecina.retro.pmd85;

import java.util.logging.Logger;
import cz.pecina.retro.gui.LED;
import cz.pecina.retro.cpu.IOPin;
import cz.pecina.retro.cpu.IONode;

/**
 * Keyboard of the Tesla PMD 85 computer.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class KeyboardHardware {

  // static logger
  private static final Logger log =
    Logger.getLogger(KeyboardHardware.class.getName());

  /**
   * Number of keyboard hardware matrix rows.
   */
  public static final int NUMBER_MATRIX_ROWS = 5;

  /**
   * Number of keyboard hardware matrix columns.
   */
  public static final int NUMBER_MATRIX_COLUMNS = 15;

  // select pins
  private final SelectPin[] selectPins = new SelectPin[4];

  // scan pins
  private final ScanPin[] scanPins = new ScanPin[NUMBER_MATRIX_ROWS];

  // currently selected column
  private int select;

  // matrices of key presses
  private final boolean[][] buffer =
    new boolean[NUMBER_MATRIX_ROWS][NUMBER_MATRIX_COLUMNS];
  private final boolean[][] matrix =
    new boolean[NUMBER_MATRIX_ROWS][NUMBER_MATRIX_COLUMNS];

  // matrix of keys
  private final KeyboardKey[][] keys =
    new KeyboardKey[NUMBER_MATRIX_ROWS][NUMBER_MATRIX_COLUMNS];

  // LEDs
  private final LED yellowLED = new LED("small", "yellow");
  private final LED redLED = new LED("small", "red");
  private final LED greenLED = new LED("small", "green");

  // leyout of buttons
  private KeyboardLayout keyboardLayout;

  /**
   * Creates the keyboard hardware object.
   */
  public KeyboardHardware() {
    log.fine("New keyboard hardware creation started");
    keyboardLayout = new KeyboardLayout(this);
    for (KeyboardKey key: keyboardLayout.getKeys()) {
      if (key.getMatrixRow() != -1) {
	keys[key.getMatrixRow()][key.getMatrixColumn()] = key;
      }
    }
    for (int row = 0; row < NUMBER_MATRIX_ROWS; row++) {
      for (int column = 0; column < NUMBER_MATRIX_COLUMNS; column++) {
	if (keys[row][column] == null) {
	  keys[row][column] = new KeyboardKey(row, column);
	}
      }
    }
    for (int row = 0; row < NUMBER_MATRIX_ROWS; row++) {
      scanPins[row] = new ScanPin(row);
    }
    resetBuffer();
    log.fine("New keyboard hardware created");
  }

  // select pins
  private class SelectPin extends IOPin {
    private int number, mask;

    private SelectPin(final int n) {
      super();
      assert (n >= 0) && (n < 4);
      number = n;
      mask = 1 << number;
    }

    @Override
    public void notifyChange() {
      if ((select & mask) != (IONode.normalize(queryNode()) << number)) {
	select ^= mask;
      }
    }
  }

  /**
   * Gets the select pin.
   *
   * @param  n the pin number
   * @return the pin object
   */
  public IOPin getSelectPin(final int n) {
    assert (n >= 0) && (n < 4);
    return selectPins[n];
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
      final int selectedColumn = (select < NUMBER_MATRIX_COLUMNS) ? select : -1;
      return (selectedColumn == -1) ?
	1 :
	(buffer[number][selectedColumn] ? 0 : 1);
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
    copyMatrixToBuffer();
  }

  // prepares matrix of keypresses
  private void prepareMatrix() {
    for (int row = 0; row < NUMBER_MATRIX_ROWS; row++) {
      for (int column = 0; column < NUMBER_MATRIX_COLUMNS; column++) {
	matrix[row][column] = keys[row][column].isPressed();
      }
    }
  }

  // copies matrix of keypresses to buffer
  private void copyMatrixToBuffer() {
    for (int row = 0; row < NUMBER_MATRIX_ROWS; row++) {
      for (int column = 0; column < NUMBER_MATRIX_COLUMNS; column++) {
	buffer[row][column] = matrix[row][column];
      }
    }
  }
	
  // adds matrix of keypresses to buffer
  private void addMatrixToBuffer() {
    for (int row = 0; row < NUMBER_MATRIX_ROWS; row++) {
      for (int column = 0; column < NUMBER_MATRIX_COLUMNS; column++) {
	buffer[row][column] |= matrix[row][column];
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

  /**
   * Gets the yellow LED.
   *
   * @return the yellow LED
   */
  public LED getYellowLED() {
    return yellowLED;
  }

  /**
   * Gets the red LED.
   *
   * @return the red LED
   */
  public LED getRedLED() {
    return redLED;
  }

  /**
   * Gets the green LED.
   *
   * @return the green LED
   */
  public LED getGreenLED() {
    return greenLED;
  }
}
