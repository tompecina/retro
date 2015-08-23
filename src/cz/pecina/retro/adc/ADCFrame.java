/* ADCFrame.java
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

package cz.pecina.retro.adc;

import java.util.logging.Logger;

import cz.pecina.retro.peripherals.Peripheral;
import cz.pecina.retro.peripherals.PeripheralFrame;

import cz.pecina.retro.gui.Resizeable;
import cz.pecina.retro.gui.GUI;

/**
 * 8-channel ADC frame.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class ADCFrame extends PeripheralFrame implements Resizeable {

  // static logger
  private static final Logger log =
    Logger.getLogger(ADCFrame.class.getName());

  // ADC panel
  private ADCPanel panel;

  // hardware object
  private ADCHardware hardware;

  /**
   * Creates a new ADC frame.
   *
   * @param peripheral the Peripheral object
   * @param hardware   the ADC hardware object
   */
  public ADCFrame(final Peripheral peripheral, final ADCHardware hardware) {
    super(peripheral);
    assert hardware != null;
    this.hardware = hardware;
    panel = new ADCPanel(hardware);
    add(panel);
    postamble();
    GUI.addResizeable(this);
    log.fine("New ADC frame set up");
  }

  // for description see Resizeable
  @Override
  public void redrawOnPixelResize() {
    log.fine("ADC frame redraw started");
    remove(panel);
    panel = new ADCPanel(hardware);
    add(panel);
    pack();
    log.fine("ADC frame redraw completed");
  }
}
