/* DACAnalogPanel.java
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

package cz.pecina.retro.dacanalog;

import java.util.logging.Logger;

import cz.pecina.retro.gui.FixedPane;

/**
 * DAC with analog voltmeter panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class DACAnalogPanel extends FixedPane {

  // static logger
  private static final Logger log =
    Logger.getLogger(DACAnalogPanel.class.getName());

  // position of the analog voltmeter
  private static final int OFFSET_X = 0;
  private static final int OFFSET_Y = 0;

  /**
   * Creates a new analog voltmeter panel.
   *
   * @param hardware the DAC hardware object
   */
  public DACAnalogPanel(final DACAnalogHardware hardware) {
    super("dacanalog/DACAnalogPanel/mask");
    assert hardware != null;

    // set up the voltmeter
    hardware.getNeedle().place(this, OFFSET_X, OFFSET_Y);

    log.fine("Analog voltmeter panel set up");
  }
}
