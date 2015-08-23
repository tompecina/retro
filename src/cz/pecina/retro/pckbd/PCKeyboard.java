/* PCKeyboard.java
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

package cz.pecina.retro.pckbd;

import java.util.logging.Logger;

import cz.pecina.retro.peripherals.VectorPeripheral;

import cz.pecina.retro.gui.GUI;

/**
 * IBM PC original keyboard.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class PCKeyboard extends VectorPeripheral {

  // static logger
  private static final Logger log =
    Logger.getLogger(PCKeyboard.class.getName());

  /**
   * The default base port.
   */
  protected static final int DEFAULT_BASE_PORT = 0x80;

  /**
   * The default interrupt vector.
   */
  protected static final int DEFAULT_VECTOR = 7;

  // hardware
  private PCKeyboardHardware hardware;

  // enclosing frame
  private PCKeyboardFrame frame;

  /**
   * Creates a new PC Keyboard.
   */
  public PCKeyboard() {
    super("pckbd", DEFAULT_BASE_PORT, DEFAULT_VECTOR);
    log.fine("New PC keyboard control object created");
  }

  // for description see Peripheral
  @Override
  public void activate() {
    log.fine("PC Keyboard activating");
    hardware = new PCKeyboardHardware(getBasePort(), getVector());
    frame = new PCKeyboardFrame(this, hardware);
    hardware.activateShortcuts(frame);
    super.activate();
    log.fine("PC keyboard activated");
  }

  // for description see Peripheral
  @Override
  public void deactivate() {
    super.deactivate();
    hardware.deactivate();
    GUI.removeResizeable(frame);
    frame.dispose();
    log.fine("PC keyboard deactivated");
  }

  // for description see Peripheral
  @Override
  public void implementSettings() {
    super.implementSettings();
    if (isActive()) {
      hardware.reconnect(getBasePort(), getVector());
    }
    log.fine("Changed settings implemented for PC keyboard");
  }
}
