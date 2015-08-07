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
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import cz.pecina.retro.gui.LockableButton;
import cz.pecina.retro.common.Localized;
import cz.pecina.retro.common.Application;

/**
 * Button on the control panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class KeyboardKey extends LockableButton implements Localized {

  // static logger
  private static final Logger log =
    Logger.getLogger(KeyboardKey.class.getName());

  // the keyboard hardware object to operate on
  private KeyboardHardware keyboardHardware;

  // cap of the key
  private String cap;

  // position of the key in the hardware matrix
  private int matrixColumn, matrixRow;

  // special functions of the key
  private boolean reset, shift, stop;

  /**
   * Creates an instance of a key.
   *
   * @param keyboardHardware the keyboard hardware object to operate on
   * @param cap              cap of the key
   * @param contact          contact number
   * @param x                x-coordinate of the key on the panel
   * @param y                x-coordinate of the key on the panel
   * @param matrixRow        position of the key in the hardware matrix
   *                         (row) or <code>-1</code> if not connected
   * @param matrixColumn     position of the key in the hardware matrix
   *                         (column) or <code>-1</code> if not connected
   */
  public KeyboardKey(final KeyboardHardware keyboardHardware,
		     final String cap,
		     final int contact,
		     final int x,
		     final int y,
		     final int matrixRow,
		     final int matrixColumn) {
    super("pmd85/KeyboardKey/" + id + "-%d-%s.png", null, null);
    log.fine("New key creation started: " + id +
	     ", matrix column: " + matrixColumn + ", matrix row: " + matrixRow);
    assert keyboardHardware != null;
    assert cap != null;
    assert contact > 0;
    assert (matrixRow >= 0) && (matrixRow < 15);
    assert (matrixRow >= -1) &&
      (matrixRow < KeyboardHardware.NUMBER_MATRIX_ROWS);
    assert (matrixColumn >= -1) &&
      (matrixColumn < KeyboardHardware.NUMBER_MATRIX_COLUMNS);
    this.keyboardHardware = keyboardHardware;
    this.cap = cap;
    this.contact = contact;
    this.x = x;
    this.y = y;
    width = 2;
    height = 2;
    this.matrixRow = matrixRow;
    this.matrixColumn = matrixColumn;
    Application.addLocalized(this);
    addChangeListener(new ChangeListener() {
	@Override
	public void stateChanged(final ChangeEvent event) {
	  KeyboardKey.this.keyboardHardware.updateBuffer();
	}
      });
    log.fine("New keyboard button created: " + id);
  }
    
  /**
   * Creates an instance of a dummy control-panel button.
   * Such button exists only as a contact in the matrix with no
   * physical switch provided.  It should never be displayed or activated.
   *
   * @param matrixColumn position of the key in the hardware matrix (column)
   * @param matrixRow    position of the key in the hardware matrix (row)
   */
  public KeyboardKey(final int matrixColumn, final int matrixRow) {
    super(null, -1, null);
    log.finer("New dummy KeyboardKey creation started for position: " +
	      matrixColumn + ", " + matrixRow);
    assert (matrixColumn >= 0) &&
      (matrixColumn < KeyboardHardware.NUMBER_MATRIX_COLUMNS);
    assert (matrixRow >= 0) &&
      (matrixRow < KeyboardHardware.NUMBER_MATRIX_ROWS);
    this.matrixColumn = matrixColumn;
    this.matrixRow = matrixRow;
    log.fine("New dummy KeyboardKey created for position: " +
	     matrixColumn + ", " + matrixRow);
  }
    
  // for description see Object
  @Override
  public String toString() {
    return id;
  }

  /**
   * Indicates whether the button is connected to the RESET signal
   * of the processor.
   *
   * @return true if the button is connected to the RESET signal
   *         of the processor
   */
  public boolean isReset() {
    return reset;
  }

  /**
   * Connects the button to the RESET signal of the processor.
   */
  public void setReset() {
    reset = true;
  }

  /**
   * Indicates whether the button interrupts the processor.
   *
   * @return true if the button generates interrupt of the processor
   */
  public boolean isInterrupt() {
    return interrupt;
  }

  /**
   * Sets the button to interrupt the processor.
   */
  public void setInterrupt() {
    interrupt = true;
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
   * Gets the position of the key in the hardware matrix (row).
   *
   * @return the position of the key in the hardware matrix (row)
   */
  public int getMatrixRow() {
    return matrixRow;
  }

  // for description see Localized
  @Override
  public void redrawOnLocaleChange() {
    if (toolTipResource != null) {
      setToolTip(Application.getString(this, toolTipResource));
    }
  }
}
