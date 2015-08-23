/* TapeRecorderButtonsLayout.java
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

package cz.pecina.retro.trec;

import java.util.logging.Logger;

import cz.pecina.retro.gui.GenericButton;

/**
 * Layout of tape recorder buttons.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class TapeRecorderButtonsLayout {

  // static logger
  private static final Logger log =
    Logger.getLogger(TapeRecorderButtonsLayout.class.getName());

  /**
   * Number of tape recorder buttons.
   */
  public static final int NUMBER_BUTTONS = 7;

  /**
   * Position of the RECORD button.
   */
  public static final int BUTTON_POSITION_RECORD = 0;

  /**
   * Position of the PLAY button.
   */
  public static final int BUTTON_POSITION_PLAY = 1;

  /**
   * Position of the REWIND button.
   */
  public static final int BUTTON_POSITION_REWIND = 2;

  /**
   * Position of the FF button.
   */
  public static final int BUTTON_POSITION_FF = 3;

  /**
   * Position of the STOP button.
   */
  public static final int BUTTON_POSITION_STOP = 4;

  /**
   * Position of the PAUSE button.
   */
  public static final int BUTTON_POSITION_PAUSE = 5;

  /**
   * Position of the EJECT button.
   */
  public static final int BUTTON_POSITION_EJECT = 6;

  // button array
  private final GenericButton[] buttons = new GenericButton[NUMBER_BUTTONS];

  /**
   * Creates an instance of the tape recorder buttons layout.
   */
  public TapeRecorderButtonsLayout() {
    buttons[BUTTON_POSITION_RECORD] =
      new TapeRecorderPushOnlyButton("record", "toolTip.record");
    buttons[BUTTON_POSITION_PLAY] =
      new TapeRecorderPushOnlyButton("play", "toolTip.play");
    buttons[BUTTON_POSITION_REWIND] =
      new TapeRecorderPushOnlyButton("rewind", "toolTip.rewind");
    buttons[BUTTON_POSITION_FF] =
      new TapeRecorderPushOnlyButton("ff", "toolTip.ff");
    buttons[BUTTON_POSITION_STOP] =
      new TapeRecorderPushButton("stop", "toolTip.stop");
    buttons[BUTTON_POSITION_PAUSE] =
      new TapeRecorderSwitchButton("pause", "toolTip.pause");
    buttons[BUTTON_POSITION_EJECT] =
      new TapeRecorderPushButton("eject", "toolTip.eject");
  }

  /**
   * Gets the button in position <code>column</code>.
   *
   * @param  column position of the button
   * @return the button in position column
   */
  public GenericButton getButton(final int column) {
    assert (column >= 0) && (column < NUMBER_BUTTONS);
    return buttons[column];
  }
}
