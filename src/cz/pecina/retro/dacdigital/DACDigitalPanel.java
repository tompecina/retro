/* DACDigitalPanel.java
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

package cz.pecina.retro.dacdigital;

import java.util.logging.Logger;

import cz.pecina.retro.gui.FixedPane;

/**
 * DAC with digial voltmeter panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class DACDigitalPanel extends FixedPane {

  // static logger
  private static final Logger log =
    Logger.getLogger(DACDigitalPanel.class.getName());

  // display geometry
  private static final int DISPLAY_OFFSET_X = 345;
  private static final int DISPLAY_GRID_X = -24;
  private static final int DISPLAY_OFFSET_Y = 15;

  // power on LED position
  private static final int POWER_ON_LED_OFFSET_X = 398;
  private static final int POWER_ON_LED_OFFSET_Y = 35;

  // buttons geometry
  private static final int BUTTONS_OFFSET_X = 155;
  private static final int BUTTONS_GRID_X = 28;
  private static final int BUTTONS_OFFSET_Y = 33;
  private static final int BUTTONS_GRID_Y = 45;

  /**
   * Creates a new digital voltmeter panel.
   *
   * @param hardware the DAC hardware object
   */
  public DACDigitalPanel(final DACDigitalHardware hardware) {
    super("dacdigital/DACDigitalPanel/mask");
    assert hardware != null;

    // set up the display
    for (int i = 0; i < DACDigitalHardware.NUMBER_DISPLAY_ELEMENTS; i++) {
      hardware.getElement(i).place(this,
				   DISPLAY_OFFSET_X + (DISPLAY_GRID_X * i),
				   DISPLAY_OFFSET_Y);
    }

    // set up the power on LED
    hardware.getPowerOnLED().place(this,
				   POWER_ON_LED_OFFSET_X,
				   POWER_ON_LED_OFFSET_Y);

    // set up the buttons
    for (int i = 0; i < DACDigitalButtonsLayout.NUMBER_BUTTONS; i++) {
      hardware.getButton(i).place(
        this,
	BUTTONS_OFFSET_X +
	  (BUTTONS_GRID_X * DACDigitalButtonsLayout.positionX[i]),
	BUTTONS_OFFSET_Y +
	  (BUTTONS_GRID_Y * DACDigitalButtonsLayout.positionY[i]));
    }

    log.fine("Digital voltmeter panel set up");
  }
}
