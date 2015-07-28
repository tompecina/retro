/* CounterFrame.java
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

package cz.pecina.retro.counter;

import java.util.logging.Logger;
import cz.pecina.retro.peripherals.Peripheral;
import cz.pecina.retro.peripherals.PeripheralFrame;
import cz.pecina.retro.gui.Resizeable;
import cz.pecina.retro.gui.GUI;

/**
 * RFT G-2002.500 frequency counter frame.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class CounterFrame extends PeripheralFrame implements Resizeable {

  // static logger
  private static final Logger log =
    Logger.getLogger(CounterFrame.class.getName());

  // voltmeter panel
  private CounterPanel panel;

  // hardware object
  private CounterHardware hardware;

  /**
   * Creates a new frequency counter frame.
   *
   * @param peripheral the Peripheral object
   * @param hardware   the Counter hardware object
   */
  public CounterFrame(final Peripheral peripheral,
		      final CounterHardware hardware) {
    super(peripheral);
    assert hardware != null;
    this.hardware = hardware;
    panel = new CounterPanel(hardware);
    add(panel);
    postamble();
    GUI.addResizeable(this);
    log.fine("New frequency counter frame set up");
  }

  // for description see Resizeable
  @Override
  public void redrawOnPixelResize() {
    log.fine("Counter/freqency mater frame redraw started");
    remove(panel);
    panel = new CounterPanel(hardware);
    add(panel);
    pack();
    log.fine("Frequency counter frame redraw completed");
  }
}
