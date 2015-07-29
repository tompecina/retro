/* CounterPanel.java
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

package cz.pecina.retro.counter;

import java.util.logging.Logger;
import cz.pecina.retro.gui.FixedPane;

/**
 * RFT G-2002.500 frequency counter panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class CounterPanel extends FixedPane {

  // static logger
  private static final Logger log =
    Logger.getLogger(CounterPanel.class.getName());

  // display geometry
  private static final int DISPLAY_OFFSET_X = 393;
  private static final int DISPLAY_GRID_X = -24;
  private static final int DISPLAY_OFFSET_Y = 15;

  // gate LED position
  private static final int GATE_LED_OFFSET_X = 222;
  private static final int GATE_LED_OFFSET_Y = 28;

  // buttons positios
  private static final int AUTO_OFFSET_X = 33;
  private static final int AUTO_OFFSET_Y = 31;
  private static final int MAN_OFFSET_X = 61;
  private static final int MAN_OFFSET_Y = AUTO_OFFSET_Y;

  // knob positions
  private static final int GATE_TIME_OFFSET_X = 102;
  private static final int GATE_TIME_OFFSET_Y = 21;
  private static final int TRIGGER_OFFSET_X = 235;
  private static final int TRIGGER_OFFSET_Y = 84;
  private static final int ATTENUATOR_OFFSET_X = 318;
  private static final int ATTENUATOR_OFFSET_Y = TRIGGER_OFFSET_Y;

  /**
   * Creates a new frequency counter panel.
   *
   * @param hardware the counter/frequency panel hardware object
   */
  public CounterPanel(final CounterHardware hardware) {
    super("counter/CounterPanel/mask");
    assert hardware != null;

    // set up the display
    for (int i = 0; i < CounterHardware.NUMBER_DISPLAY_ELEMENTS; i++)
      hardware.getElement(i).place(this,
				   DISPLAY_OFFSET_X + (DISPLAY_GRID_X * i),
				   DISPLAY_OFFSET_Y);

    // set up the gate LED
    hardware.getGateLED().place(this,
				GATE_LED_OFFSET_X,
				GATE_LED_OFFSET_Y);

    // set up buttons
    hardware.getAutoButton().place(this,
				   AUTO_OFFSET_X,
				   AUTO_OFFSET_Y);
    hardware.getManButton().place(this,
				  MAN_OFFSET_X,
				  MAN_OFFSET_Y);

    // set up knob
    hardware.getGateTimeKnob().place(this,
				     GATE_TIME_OFFSET_X,
				     GATE_TIME_OFFSET_Y);
    hardware.getTriggerKnob().place(this,
				    TRIGGER_OFFSET_X,
				    TRIGGER_OFFSET_Y);
    hardware.getAttenuatorKnob().place(this,
				       ATTENUATOR_OFFSET_X,
				       ATTENUATOR_OFFSET_Y);

    log.fine("Frequency counter panel set up");
  }
}
