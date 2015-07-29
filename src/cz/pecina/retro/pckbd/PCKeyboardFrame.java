/* PCKeyboardFrame.java
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
import cz.pecina.retro.peripherals.Peripheral;
import cz.pecina.retro.peripherals.PeripheralFrame;
import cz.pecina.retro.gui.Resizeable;
import cz.pecina.retro.gui.GUI;

/**
 * PC keyboard frame.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class PCKeyboardFrame extends PeripheralFrame implements Resizeable {

  // static logger
  private static final Logger log =
    Logger.getLogger(PCKeyboardFrame.class.getName());

  // keyboard panel
  private PCKeyboardPanel panel;

  // hardware object
  private PCKeyboardHardware hardware;

  /**
   * Creates a new PC keyboard frame.
   *
   * @param peripheral the Peripheral object
   * @param hardware   the PC keyboard hardware object
   */
  public PCKeyboardFrame(final Peripheral peripheral,
			 final PCKeyboardHardware hardware) {
    super(peripheral);
    assert hardware != null;
    this.hardware = hardware;
    panel = new PCKeyboardPanel(hardware);
    add(panel);
    postamble();
    GUI.addResizeable(this);
    log.fine("New PC keyboard frame set up");
  }

  // for description see Resizeable
  @Override
  public void redrawOnPixelResize() {
    log.fine("PC keyboard frame redraw started");
    remove(panel);
    panel = new PCKeyboardPanel(hardware);
    add(panel);
    pack();
    log.fine("PC keyboard frame redraw completed");
  }
}
