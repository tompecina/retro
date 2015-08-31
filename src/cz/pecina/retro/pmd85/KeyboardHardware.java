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

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import cz.pecina.retro.cpu.IOPin;
import cz.pecina.retro.cpu.IONode;

import cz.pecina.retro.gui.VariableLED;

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
  private final ShiftPin shiftPin = new ShiftPin();
  private final StopPin stopPin = new StopPin();

  // currently selected column
  private int select;

  // matrices of key presses
  private final boolean[][] current =
    new boolean[NUMBER_MATRIX_ROWS][NUMBER_MATRIX_COLUMNS];
  private final boolean[][] next =
    new boolean[NUMBER_MATRIX_ROWS][NUMBER_MATRIX_COLUMNS];
  private final boolean[][] buffer =
    new boolean[NUMBER_MATRIX_ROWS][NUMBER_MATRIX_COLUMNS];

  // keypresses of shift and stop keys
  private boolean shiftBuffer, shiftCurrent, shiftNext;
  private boolean stopBuffer, stopCurrent, stopNext;
  
  // matrix of keys
  private final KeyboardKey[][] keys =
    new KeyboardKey[NUMBER_MATRIX_ROWS][NUMBER_MATRIX_COLUMNS];

  // LEDs
  private final VariableLED yellowLED = new VariableLED("small", "yellow");
  private final VariableLED redLED = new VariableLED("small", "red");

  // keyboard layout
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
	key.addChangeListener(new ShiftListener(key));
      }
      if (key.isStop()) {
	key.addChangeListener(new StopListener(key));
      }
    }
    for (int row = 0; row < NUMBER_MATRIX_ROWS; row++) {
      for (int column = 0; column < NUMBER_MATRIX_COLUMNS; column++) {
	if (keys[row][column] == null) {
	  keys[row][column] = new KeyboardKey(row, column);
	  log.finer("Dummy key (" + row + "," + column + ") added");
	} else {
	  keys[row][column].addChangeListener(new KeyListener(row, column));
	}
      }
    }
    for (int row = 0; row < NUMBER_MATRIX_ROWS; row++) {
      scanPins[row] = new ScanPin(row);
    }
    for (int i = 0; i < 4; i++) {
      selectPins[i] = new SelectPin(i);
    }
    log.fine("New keyboard hardware created");
  }

  // key listener
  private class KeyListener implements ChangeListener {

    private int row, column;

    public KeyListener(final int row, final int column) {
      super();
      assert (row >= 0) && (row < NUMBER_MATRIX_ROWS);
      assert (column >= 0) && (column < NUMBER_MATRIX_COLUMNS);
      this.row = row;
      this.column = column;
    }

    // for description see ChangeListener
    @Override
    public void stateChanged(final ChangeEvent event) {
      log.finer("Key listener called for (" + row + "," + column + ")");
      final boolean pressed = keys[row][column].isPressed();
      log.finest("Pressed: " + pressed);
      current[row][column] = pressed;
      next[row][column] = next[row][column] || pressed;
    }
  }

  // shift listener
  private class ShiftListener implements ChangeListener {

    private KeyboardKey key;
    
    public ShiftListener(final KeyboardKey key) {
      super();
      assert key != null;
      this.key = key;
    }

    // for description see ChangeListener
    @Override
    public void stateChanged(final ChangeEvent event) {
      log.finer("Shift listener called");
      final boolean pressed = key.isPressed();
      log.finest("Pressed: " + pressed);
      shiftCurrent = pressed;
      shiftNext = shiftNext || pressed;
    }
  }

  // stop listener
  private class StopListener implements ChangeListener {

    private KeyboardKey key;
    
    public StopListener(final KeyboardKey key) {
      super();
      assert key != null;
      this.key = key;
    }

    // for description see ChangeListener
    @Override
    public void stateChanged(final ChangeEvent event) {
      log.finer("Stop listener called");
      final boolean pressed = key.isPressed();
      log.finest("Pressed: " + pressed);
      stopCurrent = pressed;
      stopNext = stopNext || pressed;
    }
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

    // for description see IOPin
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

    // for description see IOPin
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
   * Performs periodic keyboard update.
   */
  public void update() {
    updateBuffer();
  }

  /**
   * Updates the matrix of keyboard presses.
   */
  public void updateBuffer() {
    for (int row = 0; row < NUMBER_MATRIX_ROWS; row++) {
      for (int column = 0; column < NUMBER_MATRIX_COLUMNS; column++) {
	buffer[row][column] = next[row][column];
	next[row][column] = current[row][column];
      }
    }
    shiftBuffer = shiftNext;
    shiftNext = shiftCurrent;
    stopBuffer = stopNext;
    stopNext = stopCurrent;
  }
	
  // shift pin
  private class ShiftPin extends IOPin {

    // for description see IOPin
    @Override
    public int query() {
      return shiftBuffer ? 0 : 1;
    }
  }

  // stop pin
  private class StopPin extends IOPin {

    // for description see IOPin
    @Override
    public int query() {
      return stopBuffer ? 0 : 1;
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
  public VariableLED getYellowLED() {
    return yellowLED;
  }

  /**
   * Gets the red LED.
   *
   * @return the red LED
   */
  public VariableLED getRedLED() {
    return redLED;
  }
}
