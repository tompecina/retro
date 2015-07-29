/* IOPanel.java
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
 **/

package cz.pecina.retro.pmi80.iopanel;

import java.util.logging.Logger;
import cz.pecina.retro.peripherals.Peripheral;
import cz.pecina.retro.gui.GUI;
import cz.pecina.retro.pmi80.ComputerHardware;

/**
 * Input/output panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class IOPanel extends Peripheral {

  // static logger
  private static final Logger log =
    Logger.getLogger(IOPanel.class.getName());

  // hardware
  private IOPanelHardware hardware;

  // enclosing frame
  private IOPanelFrame frame;

  // the computer hardware object
  private ComputerHardware computerHardware;

  /**
   * Creates a new I/O panel.
   *
   * @param computerHardware the computer hardware object
   */
  public IOPanel(final ComputerHardware computerHardware) {
    super("iopanel");
    assert computerHardware != null;
    this.computerHardware = computerHardware;
    log.fine("New I/O panel control object created");
  }

  // for description see Peripheral
  @Override
  public void activate() {
    log.fine("I/O panel activating");
    hardware = new IOPanelHardware(computerHardware);
    frame = new IOPanelFrame(this, hardware);
    super.activate();
    log.fine("I/O panel activated");
  }

  // for description see Peripheral
  @Override
  public void deactivate() {
    super.deactivate();
    hardware.deactivate();
    GUI.removeResizeable(frame);
    frame.dispose();
    log.fine("I/O panel deactivated");
  }
}
