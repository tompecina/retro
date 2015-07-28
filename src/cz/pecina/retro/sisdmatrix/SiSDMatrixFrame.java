/* SiSDMatrixFrame.java
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

package cz.pecina.retro.sisdmatrix;

import java.util.logging.Logger;
import cz.pecina.retro.peripherals.Peripheral;
import cz.pecina.retro.peripherals.PeripheralFrame;
import cz.pecina.retro.gui.Resizeable;
import cz.pecina.retro.gui.GUI;

/**
 * 4x24 SiSD matrix panel frame.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class SiSDMatrixFrame extends PeripheralFrame implements Resizeable {

  // static logger
  private static final Logger log =
    Logger.getLogger(SiSDMatrixFrame.class.getName());

  // matrix panel
  private SiSDMatrixPanel panel;

  // hardware object
  private SiSDMatrixHardware hardware;

  /**
   * Creates a new SiSD matrix frame.
   *
   * @param peripheral the Peripheral object
   * @param hardware   the SiSD matrix panel hardware object
   */
  public SiSDMatrixFrame(final Peripheral peripheral,
			 final SiSDMatrixHardware hardware) {
    super(peripheral);
    assert hardware != null;
    this.hardware = hardware;
    panel = new SiSDMatrixPanel(hardware);
    add(panel);
    postamble();
    GUI.addResizeable(this);
    log.fine("New SiSD matrix frame set up");
  }

  // for description see Resizeable
  @Override
  public void redrawOnPixelResize() {
    log.fine("SiSDMatrixFrame redraw started");
    remove(panel);
    panel = new SiSDMatrixPanel(hardware);
    add(panel);
    pack();
    log.fine("SiSDMatrixFrame redraw completed");
  }
}
