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

package cz.pecina.retro.ondra;

import java.util.logging.Logger;

import java.util.List;
import java.util.ArrayList;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import cz.pecina.retro.cpu.IOPin;
import cz.pecina.retro.cpu.IONode;
import cz.pecina.retro.cpu.NegativeLEDPin;

import cz.pecina.retro.gui.LED;

/**
 * Keyboard of the Tesla Ondra SPO 186 computer.
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
  public static final int NUMBER_MATRIX_COLUMNS = 10;

  // the computer hardware object
  private ComputerHardware computerHardware;

  // matrices of key presses
  private final boolean[][] current =
    new boolean[NUMBER_MATRIX_ROWS][NUMBER_MATRIX_COLUMNS];
  private final boolean[][] next =
    new boolean[NUMBER_MATRIX_ROWS][NUMBER_MATRIX_COLUMNS];
  private final boolean[][] buffer =
    new boolean[NUMBER_MATRIX_ROWS][NUMBER_MATRIX_COLUMNS];

  // matrix of keys
  private final KeyboardKey[][] keys =
    new KeyboardKey[NUMBER_MATRIX_ROWS][NUMBER_MATRIX_COLUMNS];

  // the NMI button
  private final NmiButton nmiButton = new NmiButton();

  // LEDs
  private final LED yellowLED = new LED("small", "yellow");
  private final LED greenLED = new LED("small", "green");

  // LED pins
  private final NegativeLEDPin yellowLEDPin =
    new NegativeLEDPin(yellowLED);
  private final NegativeLEDPin greenLEDPin =
    new NegativeLEDPin(greenLED);

  // keyboard layout
  private KeyboardLayout keyboardLayout;

  /**
   * Creates the keyboard hardware object.
   *
   * @param computerHardware the computer hardware object
   */
  public KeyboardHardware(final ComputerHardware computerHardware) {
    log.fine("New keyboard hardware creation started");
    assert computerHardware != null;
    this.computerHardware = computerHardware;
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
	  log.finer("Dummy key (" + row + "," + column + ") added");
	} else {
	  keys[row][column].addChangeListener(new KeyListener(row, column));
	}
      }
    }
    nmiButton.addChangeListener(new NmiListener());
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

  // NMI listener
  private class NmiListener implements ChangeListener {

    // for description see ChangeListener
    @Override
    public void stateChanged(final ChangeEvent event) {
      log.finer("NMI listener activated");
      if (nmiButton.isPressed()) {
	computerHardware.getCPU().requestNmi();
      }
    }
  }

  /**
   * Gets the current state.
   *
   * @param  column the selected column
   * @return        the state of the keyboard output
   */
  public int getState(final int column) {
    if ((column < 0) || (column >= NUMBER_MATRIX_COLUMNS)) {
      return 0xff;
    }
    int state = 0;
    for (int row = 0; row < NUMBER_MATRIX_ROWS; row++) {
      if (!buffer[row][column]) {
	state |= 1 << row;
      }
    }
    log.finest("State for column: " + column + " = " + state);
    return state;
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
   * Gets the NMI button.
   *
   * @return the NMI button
   */
  public NmiButton getNmiButton() {
    return nmiButton;
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
   * Gets the green LED.
   *
   * @return the green LED
   */
  public LED getGreenLED() {
    return greenLED;
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
   * Gets the green LED.
   *
   * @return the green LED
   */
  public IOPin getGreenLEDPin() {
    return greenLEDPin;
  }
}
