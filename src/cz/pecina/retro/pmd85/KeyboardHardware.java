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
import java.util.List;
import java.util.ArrayList;
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

  // shift pin & stop pin
  private final KeyPin shiftPin = new KeyPin();
  private final KeyPin stopPin = new KeyPin();

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

  // LED pins
  private final LEDPin yellowLEDPin = new LEDPin(yellowLED);
  private final LEDPin redLEDPin = new LEDPin(redLED);
  private final LEDPin greenLEDPin = new LEDPin(greenLED);
  
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
      if (key.isShift()) {
	shiftPin.addKey(key);
      }
      if (key.isStop()) {
	stopPin.addKey(key);
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
    for (int i = 0; i < 4; i++) {
      selectPins[i] = new SelectPin(i);
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
    addMatrixToBuffer();
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

  // shift & stop pins
  private class KeyPin extends IOPin {
    private List<KeyboardKey> keys = new ArrayList<>();

    public void addKey(final KeyboardKey key) {
      assert key != null;
      keys.add(key);
    }
    
    @Override
    public int query() {
      for (KeyboardKey key: keys) {
	if (key.isPressed()) {
	  return 0;
	}
      }
      return 1;
    }
  }

  /**
   * Gets the shift pin.
   *
   * @return the shift pin.
   */
  public IOPin getShiftPin() {
    return shiftPin;
  }

  /**
   * Gets the stop pin.
   *
   * @return the stop pin.
   */
  public IOPin getStopPin() {
    return stopPin;
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

  // LED pins
  private class LEDPin extends IOPin {
    private LED led;

    private LEDPin(final LED led) {
      super();
      assert (led != null);
      this.led = led;
    }

    @Override
    public void notifyChange() {
      led.setState(IONode.normalize(queryNode()) == 1);
    }
  }

  /**
   * Gets the yellow LED pin.
   *
   * @return the yellow LED pin
   */
  public IOPin getYellowLEDPin() {
    return yellowLEDPin;
  }

  /**
   * Gets the red LED pin.
   *
   * @return the red LED pin
   */
  public IOPin getRedLEDPin() {
    return redLEDPin;
  }

  /**
   * Gets the green LED pin.
   *
   * @return the green LED pin
   */
  public IOPin getGreenLEDPin() {
    return greenLEDPin;
  }
}
