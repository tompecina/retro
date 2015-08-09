/* DACDigitalButtonsLayout.java
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

package cz.pecina.retro.dacdigital;

import java.util.logging.Logger;
import cz.pecina.retro.gui.GenericButton;
import cz.pecina.retro.gui.SwitchButton;
import cz.pecina.retro.gui.PushButton;
import cz.pecina.retro.gui.PushOnlyButton;

/**
 * Layout of digital voltmeter buttons.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class DACDigitalButtonsLayout {

  // static logger
  private static final Logger log =
    Logger.getLogger(DACDigitalButtonsLayout.class.getName());

  /**
   * Number of digital voltmeter buttons.
   */
  public static final int NUMBER_BUTTONS = 11;

  /**
   * Position of the AUTO button.
   */
  public static final int BUTTON_POSITION_AUTO = 0;

  /**
   * Position of the MAN button.
   */
  public static final int BUTTON_POSITION_MAN = 1;

  /**
   * Position of the V button.
   */
  public static final int BUTTON_POSITION_V = 2;

  /**
   * Position of the kOhm button.
   */
  public static final int BUTTON_POSITION_KOHM = 3;

  /**
   * Position of the mA button.
   */
  public static final int BUTTON_POSITION_MA = 4;

  /**
   * Position of the AC button.
   */
  public static final int BUTTON_POSITION_AC = 5;

  /**
   * Position of the 0.2 button.
   */
  public static final int BUTTON_POSITION_0_2 = 6;

  /**
   * Position of the 2 button.
   */
  public static final int BUTTON_POSITION_2 = 7;

  /**
   * Position of the 20 button.
   */
  public static final int BUTTON_POSITION_20 = 8;

  /**
   * Position of the 200 button.
   */
  public static final int BUTTON_POSITION_200 = 9;

  /**
   * Position of the 2000 button.
   */
  public static final int BUTTON_POSITION_2000 = 10;

  // button array
  private final GenericButton[] buttons = new GenericButton[NUMBER_BUTTONS];

  /**
   * x-coordinates of digital voltmeter buttons.
   */
  public static final int[] positionX = {0, 1, 0, 1, 2, 3, 4, 5, 6, 7, 8};

  /**
   * y-coordinates of digital voltmeter buttons.
   */
  public static final int[] positionY = {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1};

  /**
   * Creates an instance of the digital voltmeter buttons layout.
   */
  public DACDigitalButtonsLayout() {
    buttons[BUTTON_POSITION_AUTO] =
      new SwitchButton("gui/UniversalButton/round-gray-%d-%s.png",
		       null,
		       null);
    buttons[BUTTON_POSITION_MAN] =
      new PushButton("gui/UniversalButton/round-gray-%d-%s.png",
		     null,
		     null);
    buttons[BUTTON_POSITION_V] =
      new PushOnlyButton("gui/UniversalButton/round-black-%d-%s.png",
			 null,
			 null);
    buttons[BUTTON_POSITION_KOHM] =
      new PushOnlyButton("gui/UniversalButton/round-black-%d-%s.png",
			 null,
			 null);
    buttons[BUTTON_POSITION_MA] =
      new PushOnlyButton("gui/UniversalButton/round-black-%d-%s.png",
			 null,
			 null);
    buttons[BUTTON_POSITION_AC] =
      new SwitchButton("gui/UniversalButton/round-black-%d-%s.png",
		       null,
		       null);
    buttons[BUTTON_POSITION_0_2] =
      new PushOnlyButton("gui/UniversalButton/round-gray-%d-%s.png",
			 null,
			 null);
    buttons[BUTTON_POSITION_2] =
      new PushOnlyButton("gui/UniversalButton/round-gray-%d-%s.png",
			 null,
			 null);
    buttons[BUTTON_POSITION_20] =
      new PushOnlyButton("gui/UniversalButton/round-gray-%d-%s.png",
			 null,
			 null);
    buttons[BUTTON_POSITION_200] =
      new PushOnlyButton("gui/UniversalButton/round-gray-%d-%s.png",
			 null,
			 null);
    buttons[BUTTON_POSITION_2000] =
      new PushOnlyButton("gui/UniversalButton/round-gray-%d-%s.png",
			 null,
			 null);
  }

  /**
   * Gets the <code>n</code>th button
   *
   * @param  n position of the button
   * @return the <code>n</code>th button
   */
  public GenericButton getButton(final int n) {
    assert (n >= 0) && (n < NUMBER_BUTTONS);
    return buttons[n];
  }
}
