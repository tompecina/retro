/* KeyboardKey.java
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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import cz.pecina.retro.common.Localized;
import cz.pecina.retro.common.Application;

import cz.pecina.retro.gui.LockableButton;

/**
 * Key on the PMD 85 keybord.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class KeyboardKey extends LockableButton {

  // static logger
  private static final Logger log =
    Logger.getLogger(KeyboardKey.class.getName());

  // the keyboard hardware object to operate on
  private KeyboardHardware keyboardHardware;

  // cap of the key
  private String cap;

  // key position in the matrix
  private int offsetX, offsetY;

  // position of the key in the hardware matrix
  private int matrixColumn, matrixRow;

  // special functions of the key
  private boolean reset, shift, stop;

  /**
   * Creates an instance of a key.
   *
   * @param keyboardHardware the keyboard hardware object to operate on
   * @param cap              cap of the key
   * @param offsetX          x-offset of the key in 1/2s of the base key width
   * @param offsetY          y-offset of the key in 1/2s of the base key height
   * @param matrixRow        position of the key in the hardware matrix
   *                         (row) or {@code -1} if not connected
   * @param matrixColumn     position of the key in the hardware matrix
   *                         (column) or {@code -1} if not connected
   */
  public KeyboardKey(final KeyboardHardware keyboardHardware,
		     final String cap,
		     final int offsetX,
		     final int offsetY,
		     final int matrixRow,
		     final int matrixColumn) {
    super("pmd85/KeyboardKey/" + cap + "-%d-%s.png", null, null);
    log.fine("New key creation started: " + cap +
	     ", matrix column: " + matrixColumn + ", matrix row: " + matrixRow);
    assert keyboardHardware != null;
    assert cap != null;
    assert (matrixRow >= -1) &&
      (matrixRow < KeyboardHardware.NUMBER_MATRIX_ROWS);
    assert (matrixColumn >= -1) &&
      (matrixColumn < KeyboardHardware.NUMBER_MATRIX_COLUMNS);
    this.keyboardHardware = keyboardHardware;
    this.cap = cap;
    this.offsetX = offsetX;
    this.offsetY = offsetY;
    this.matrixRow = matrixRow;
    this.matrixColumn = matrixColumn;
    addChangeListener(new ChangeListener() {
	@Override
	public void stateChanged(final ChangeEvent event) {
	  KeyboardKey.this.keyboardHardware.updateBuffer();
	}
      });
    log.fine("New keyboard key created: " + cap);
  }
    
  /**
   * Creates an instance of a dummy key.  Such key exists only as a contact
   * in the matrix with no physical switch provided.  It should never be displayed
   * or activated.
   *
   * @param matrixRow    position of the key in the hardware matrix (row)
   * @param matrixColumn position of the key in the hardware matrix (column)
   */
  public KeyboardKey(final int matrixRow, final int matrixColumn) {
    super(null, null, null);
    log.finer("New dummy KeyboardKey creation started for position: " +
	      matrixRow + ", " + matrixColumn);
    assert (matrixColumn >= 0) &&
      (matrixColumn < KeyboardHardware.NUMBER_MATRIX_COLUMNS);
    assert (matrixRow >= 0) &&
      (matrixRow < KeyboardHardware.NUMBER_MATRIX_ROWS);
    this.matrixRow = matrixRow;
    this.matrixColumn = matrixColumn;
    log.fine("New dummy KeyboardKey created for position: " +
	     matrixRow + ", " + matrixColumn);
  }
    
  // for description see Object
  @Override
  public String toString() {
    return cap;
  }

  /**
   * Sets the cap of the key.
   *
   * @param cap the cap of the key
   */
  public void setCap(final String cap) {
    this.cap = cap;
    super.setTemplate("pmd85/KeyboardKey/" + cap + "-%d-%s.png");
    log.fine("New cap set: " + cap);
  }

  /**
   * Gets the cap of the key.
   *
   * @return the cap of the key
   */
  public String getCap() {
    return cap;
  }

  /**
   * Sets the x-offset of the key.
   *
   * @param offsetX x-offset of the key in 1/2s of the base key width
   */
  public void setOffsetX(final int offsetX) {
    this.offsetX = offsetX;
  }

  /**
   * Gets the x-offset of the key.
   *
   * @return x-offset of the key in 1/2s of the base key width
   */
  public int getOffsetX() {
    return offsetX;
  }

  /**
   * Sets the y-offset of the key.
   *
   * @param offsetY y-offset of the key in 1/2s of the base key width
   */
  public void setOffsetY(final int offsetY) {
    this.offsetY = offsetY;
  }

  /**
   * Gets the y-offset of the key.
   *
   * @return y-offset of the key in 1/2s of the base key width
   */
  public int getOffsetY() {
    return offsetY;
  }

  /**
   * Gets the position of the key in the hardware matrix (row).
   *
   * @return the position of the key in the hardware matrix (row)
   */
  public int getMatrixRow() {
    return matrixRow;
  }

  /**
   * Gets the position of the key in the hardware matrix (column).
   *
   * @return the position of the key in the hardware matrix (column)
   */
  public int getMatrixColumn() {
    return matrixColumn;
  }

  /**
   * Connects the key to the RESET signal of the processor.
   */
  public void setReset() {
    reset = true;
  }

  /**
   * Tests if the key is connected to the RESET signal of the processor.
   *
   * @return {@code true} if the key is connected to the RESET signal
   *         of the processor
   */
  public boolean isReset() {
    return reset;
  }

  /**
   * Makes the key a shift.
   */
  public void setShift() {
    shift = true;
  }

  /**
   * Tests if the key is a shift.
   *
   * @return {@code true} if the key is a shift
   */
  public boolean isShift() {
    return shift;
  }

  /**
   * Makes the key STOP.
   */
  public void setStop() {
    stop = true;
  }

  /**
   * Tests if the key is STOP.
   *
   * @return {@code true} if the key is STOP
   */
  public boolean isStop() {
    return stop;
  }
}
