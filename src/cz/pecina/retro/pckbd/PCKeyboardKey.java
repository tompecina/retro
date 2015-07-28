/* PCKeyboardKey.java
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

package cz.pecina.retro.pckbd;

import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import cz.pecina.retro.gui.LockableButton;
import cz.pecina.retro.gui.Shortcut;

/**
 * PC keyboard key.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class PCKeyboardKey extends LockableButton {

  // static logger
  private static final Logger log =
    Logger.getLogger(PCKeyboardKey.class.getName());

  // base key dimensions
  private static final int BASE_WIDTH = 3;
  private static final int BASE_HEIGHT = BASE_WIDTH;

  // hardware to operate on
  private PCKeyboardHardware hardware;

  // text identification of the key
  private String id;

  // key position in the matrix
  private int offsetX, offsetY;

  // scan code of the key 
  private int scanCode;

  // array of shortcuts for the key
  private Shortcut[] shortcuts;

  /**
   * Creates an instance of a PC keyboard key.
   *
   * @param hardware hardware object to operate on
   * @param id text  identification of the key
   * @param offsetX  x-offset of the key in 1/6s of the base key width
   * @param offsetY  y-offset of the key in 1/6s of the base key height
   * @param scanCode scan code of the key
   * @param shortcut shortcut for the key
   */
  public PCKeyboardKey(final PCKeyboardHardware hardware,
		       final String id,
		       final int offsetX,
		       final int offsetY,
		       final int scanCode,
		       final int shortcut) {
    this(hardware,
	 id,
	 offsetX,
	 offsetY,
	 scanCode,
	 new Shortcut[] {new Shortcut(shortcut)});
  }

  /**
   * Creates an instance of a PC keyboard key.
   *
   * @param hardware hardware object to operate on
   * @param id text  identification of the key
   * @param offsetX  x-offset of the key in 1/6s of the base key width
   * @param offsetY  y-offset of the key in 1/6s of the base key height
   * @param scanCode scan code of the key
   * @param shortcut shortcut for the key
   * @param location location of the shortcut key
   */
  public PCKeyboardKey(final PCKeyboardHardware hardware,
		       final String id,
		       final int offsetX,
		       final int offsetY,
		       final int scanCode,
		       final int shortcut,
		       final int location) {
    this(hardware,
	 id,
	 offsetX,
	 offsetY,
	 scanCode,
	 new Shortcut[] {new Shortcut(shortcut, location)});
  }

  /**
   * Creates an instance of a PC keyboard key.
   *
   * @param hardware  hardware object to operate on
   * @param id text   identification of the key
   * @param offsetX   x-offset of the key in 1/6s of the base key width
   * @param offsetY   y-offset of the key in 1/6s of the base key height
   * @param scanCode  scan code of the key
   * @param shortcuts array of shortcuts for the key
   */
  public PCKeyboardKey(final PCKeyboardHardware hardware,
		       final String id,
		       final int offsetX,
		       final int offsetY,
		       final int scanCode,
		       final Shortcut[] shortcuts) {
    super("pckbd/PCKeyboardKey/" + id + "-%d-%s.png", -1, null);
    assert hardware != null;
    assert (id != null) && !id.isEmpty();
    assert offsetX >= 0;
    assert offsetY >= 0;
    assert scanCode > 0;
    this.hardware = hardware;
    this.id = id;
    this.offsetX = offsetX * BASE_WIDTH;
    this.offsetY = offsetY * BASE_HEIGHT;
    this.scanCode = scanCode;
    this.shortcuts = (shortcuts == null) ? new Shortcut[0] : shortcuts;
    addChangeListener(
      new ChangeListener() {
	@Override
	public void stateChanged(final ChangeEvent event) {
	  PCKeyboardKey.this.hardware.sendScanCode(
            PCKeyboardKey.this.scanCode, !pressed);
	}
      });
    log.fine("New PC keyboard key created: " + id);
  }

  /**
   * Gets the x-offset of the key.
   *
   * @return x-offset of the key in pixels of the base size
   */
  public int getOffsetX() {
    return offsetX;
  }
    
  /**
   * Gets the y-offset of the key.
   *
   * @return y-offset of the key in pixels of the base size
   */
  public int getOffsetY() {
    return offsetY;
  }
    
  /**
   * Gets the scan code of the key.
   *
   * @return the scan code of the key
   */
  public int getScanCode() {
    return scanCode;
  }

  /**
   * Gets the array of shortcuts for the key.
   *
   * @return array of shortcuts for the key
   */
  public Shortcut[] getShortcuts() {
    return shortcuts;
  }
}
