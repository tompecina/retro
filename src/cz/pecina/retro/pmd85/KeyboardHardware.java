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
import java.util.Deque;
import java.util.ArrayDeque;

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
  private final KeyPin shiftPin = new KeyPin();
  private final KeyPin stopPin = new KeyPin();

  // currently selected column
  private int select;

  // matrices of key presses
  private final boolean[][] matrix =
    new boolean[NUMBER_MATRIX_ROWS][NUMBER_MATRIX_COLUMNS];
  private final boolean[][] next =
    new boolean[NUMBER_MATRIX_ROWS][NUMBER_MATRIX_COLUMNS];
  private final boolean[][] buffer =
    new boolean[NUMBER_MATRIX_ROWS][NUMBER_MATRIX_COLUMNS];

  // matrix of keys
  private final KeyboardKey[][] keys =
    new KeyboardKey[NUMBER_MATRIX_ROWS][NUMBER_MATRIX_COLUMNS];

  // LEDs
  private final VariableLED yellowLED = new VariableLED("small", "yellow");
  private final VariableLED redLED = new VariableLED("small", "red");

  // keyboard layout
  private KeyboardLayout keyboardLayout;

  // keyboard shortcut queues
  private Deque<KeyboardKey> presses = new ArrayDeque<>();
  private Deque<KeyboardKey> releases = new ArrayDeque<>();
  
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
      assert (row >= 0) && (row < NUMBER_MATRIX_ROWS);
      assert (column >= 0) && (column < NUMBER_MATRIX_COLUMNS);
      this.row = row;
      this.column = column;
    }

    @Override
    public void stateChanged(final ChangeEvent event) {
      log.finer("Key listener called for (" + row + "," + column + ")");
      final boolean pressed = keys[row][column].isPressed();
      log.finest("Pressed: " + pressed);
      matrix[row][column] = pressed;
      next[row][column] = next[row][column] || pressed;
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
   * Put a keyboard shortcut action in the queue.
   *
   * @param key    the key object
   * @param action {@code true} if pressed, {@code false} if released
   */
  public void pushKey(final KeyboardKey key, final boolean action) {
    log.finer("Pushing key: " + key +
	      ", action: " + (action ? "press" : "release"));
    assert key != null;
    if (action) {
      presses.push(key);
    } else {
      releases.push(key);
    }
  }
  
  /**
   * Performs periodic keyboard update.
   */
  public void update() {
    final KeyboardKey press = presses.pollLast();
    if (press != null) {
      press.setPressed(true);
    } else {
      final KeyboardKey release = releases.pollLast();
      if (release != null) {
	release.setPressed(false);
      }
    }      
    updateBuffer();
  }

  /**
   * Updates the matrix of keyboard presses.
   */
  public void updateBuffer() {
    for (int row = 0; row < NUMBER_MATRIX_ROWS; row++) {
      for (int column = 0; column < NUMBER_MATRIX_COLUMNS; column++) {
	buffer[row][column] = next[row][column];
	next[row][column] = matrix[row][column];
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
