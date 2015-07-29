/* ADCPanel.java
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
import cz.pecina.retro.gui.BackgroundFixedPane;

/**
 * 8-channel ADC panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class ADCPanel extends BackgroundFixedPane {

  // static logger
  private static final Logger log =
    Logger.getLogger(ADCPanel.class.getName());

  /**
   * Length of the channel's path, in base-size pixels.
   */
  public static final int LENGTH = 100;

  /**
   * Vertical distance between channels, in base-size pixels.
   */
  public static final int GRID_Y = 25;

  /**
   * Horizontal offset of channels, in base-size pixels.
   */
  public static final int OFFSET_X = 23;

  /**
   * Vertical offset of channels, in base-size pixels.
   */
  public static final int OFFSET_Y = 20;

  /**
   * Creates a new ADC panel.
   *
   * @param hardware the ADC hardware object
   */
  public ADCPanel(final ADCHardware hardware) {
    super("adc/ADCPanel/mask", "plastic", "gray");
    assert hardware != null;

    // set up the channels
    for (int channel = 0; channel < ADCHardware.NUMBER_CHANNELS; channel++) {
      hardware.getChannel(channel).place(this, (channel * GRID_Y) + OFFSET_Y);
    }

    log.fine("ADC panel set up");
  }
}
