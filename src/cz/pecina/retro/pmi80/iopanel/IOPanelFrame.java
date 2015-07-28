/* IOPanelFrame.java
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

package cz.pecina.retro.pmi80.iopanel;

import java.util.logging.Logger;
import cz.pecina.retro.peripherals.Peripheral;
import cz.pecina.retro.peripherals.PeripheralFrame;
import cz.pecina.retro.gui.Resizeable;
import cz.pecina.retro.gui.GUI;

/**
 * Input/output panel frame.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class IOPanelFrame extends PeripheralFrame implements Resizeable {

  // static logger
  private static final Logger log =
    Logger.getLogger(IOPanelFrame.class.getName());

  // I/O panel
  private IOPanelPanel panel;

  // hardware object
  private IOPanelHardware hardware;

  /**
   * Creates a new I/O panel frame.
   *
   * @param peripheral the Peripheral object
   * @param hardware   the I/O panel hardware object
   */
  public IOPanelFrame(final Peripheral peripheral,
		      final IOPanelHardware hardware) {
    super(peripheral);
    assert hardware != null;
    this.hardware = hardware;
    panel = new IOPanelPanel(hardware);
    add(panel);
    postamble();
    GUI.addResizeable(this);
    log.fine("New I/O panel frame set up");
  }

  // for description see Resizeable
  @Override
  public void redrawOnPixelResize() {
    log.fine("I/O panel frame redraw started");
    remove(panel);
    panel = new IOPanelPanel(hardware);
    add(panel);
    pack();
    log.fine("I/O panel frame redraw completed");
  }
}
