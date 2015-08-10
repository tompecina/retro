/* PeripheralsFrame.java
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

package cz.pecina.retro.pmd85;

import java.util.logging.Logger;
import cz.pecina.retro.common.Application;
import cz.pecina.retro.peripherals.Peripheral;
import cz.pecina.retro.peripherals.PeripheralsPanel;
import cz.pecina.retro.ledmatrix.LEDMatrix;
import cz.pecina.retro.colorledmatrix.ColorLEDMatrix;
import cz.pecina.retro.sisdmatrix.SiSDMatrix;
import cz.pecina.retro.pckbd.PCKeyboard;
import cz.pecina.retro.adc.ADC;
import cz.pecina.retro.dacanalog.DACAnalog;
import cz.pecina.retro.dacdigital.DACDigital;
import cz.pecina.retro.pmi80.iopanel.IOPanel;
import cz.pecina.retro.counter.Counter;
import cz.pecina.retro.dboutput.DebugOutput;

/**
 * The Peripherals frame.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class PeripheralsFrame extends HidingFrame {

  // static logger
  private static final Logger log =
    Logger.getLogger(PeripheralsFrame.class.getName());

  // Periperals panel
  private PeripheralsPanel peripheralsPanel;

  // list of all available peripherals
  private Peripheral peripherals[];

  /**
   * Creates the Peripherals frame.
   *
   * @param computer         the computer control object
   * @param computerHardware the computer hardware object
   */
  public PeripheralsFrame(final Computer computer,
			  final ComputerHardware computerHardware) {
    super(Application.getString(PeripheralsFrame.class,
				"peripherals.frameTitle"),
	  computer.getIconLayout().getIcon(IconLayout.ICON_POSITION_CABLE));
    log.fine("New PeripheralsFrame creation started");
    assert computerHardware != null;
    peripherals = new Peripheral[] {
      new DebugOutput()
    };
    peripheralsPanel = new PeripheralsPanel(this, peripherals);
    add(peripheralsPanel);
    setUp();
    pack();
    log.fine("PeripheralsFrame set up");
  }

  /**
   * Gets the array of available peripherals.
   *
   * @return array of available peripherals
   */
  public Peripheral[] getPeripherals() {
    return peripherals;
  }

  // for description see HidingFrame
  @Override
  public void setUp() {
    peripheralsPanel.setUp();
  }

  // redraw frame
  private void redraw() {
    log.fine("PeripheralsFrame redraw started");
    super.setTitle(Application.getString(this, "peripherals.frameTitle"));
    remove(peripheralsPanel);
    peripheralsPanel = new PeripheralsPanel(this, peripherals);
    add(peripheralsPanel);
    pack();
    log.fine("PeripheralsFrame redraw completed");
  }

  // for description see Localized
  @Override
  public void redrawOnLocaleChange() {
    redraw();
  }
}
