/* KeyboardButton.java
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

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import cz.pecina.retro.common.Localized;
import cz.pecina.retro.common.Application;

import cz.pecina.retro.gui.LockableButton;

/**
 * Button on the control panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class KeyboardButton extends LockableButton implements Localized {

  // static logger
  private static final Logger log =
    Logger.getLogger(KeyboardButton.class.getName());

  // the keyboard hardware object to operate on
  private KeyboardHardware keyboardHardware;

  // text identification of the button
  private String id;

  // position of the key in the hardware matrix
  private int matrixColumn, matrixRow;

  // special functions of the key
  private boolean reset, interrupt;

  // tool-tip resource
  private String toolTipResource;

  /**
   * Creates an instance of a control-panel button.
   *
   * @param keyboardHardware the keyboard hardware object to operate on
   * @param id               text identification of the button (used
   *                         when fetching the icons)
   * @param matrixColumn     position of the key in the hardware matrix
   *                         (column) or {@code -1} if not connected
   * @param matrixRow        position of the key in the hardware matrix
   *                         (row) or {@code -1} if not connected
   * @param toolTipResource  tool-tip for the button ({@code null} if none)
   */
  public KeyboardButton(final KeyboardHardware keyboardHardware,
			final String id,
			final int matrixColumn,
			final int matrixRow,
			final String toolTipResource) {
    super("pmi80/KeyboardButton/" + id + "-%d-%s.png",
	  null,
	  (toolTipResource == null) ?
	    null :
	    Application.getString(KeyboardButton.class, toolTipResource));
    log.fine("New keyboard button creation started: id: " + id +
	     ", matrix column: " + matrixColumn + ", matrix row: " + matrixRow);
    assert keyboardHardware != null;
    assert id != null;
    assert (matrixColumn >= -1) &&
      (matrixColumn < KeyboardHardware.NUMBER_MATRIX_COLUMNS);
    assert (matrixRow >= -1) &&
      (matrixRow < KeyboardHardware.NUMBER_MATRIX_ROWS);
    this.keyboardHardware = keyboardHardware;
    this.id = id;
    this.matrixColumn = matrixColumn;
    this.matrixRow = matrixRow;
    this.toolTipResource = toolTipResource;
    Application.addLocalized(this);
    addChangeListener(new ChangeListener() {
	@Override
	public void stateChanged(final ChangeEvent event) {
	  KeyboardButton.this.keyboardHardware.updateBuffer();
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
  public KeyboardButton(final int matrixColumn, final int matrixRow) {
    super(null, null, null);
    log.finer("New dummy KeyboardButton creation started for position: " +
	      matrixColumn + ", " + matrixRow);
    assert (matrixColumn >= 0) &&
      (matrixColumn < KeyboardHardware.NUMBER_MATRIX_COLUMNS);
    assert (matrixRow >= 0) &&
      (matrixRow < KeyboardHardware.NUMBER_MATRIX_ROWS);
    this.matrixColumn = matrixColumn;
    this.matrixRow = matrixRow;
    log.fine("New dummy KeyboardButton created for position: " +
	     matrixColumn + ", " + matrixRow);
  }
    
  // for description see Object
  @Override
  public String toString() {
    return id;
  }

  /**
   * Gets the identification string of the button.
   *
   * @return the identification string of the button
   */
  public String getId() {
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
