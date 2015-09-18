/* JoystickPanel.java
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

package cz.pecina.retro.jstick;

import java.util.logging.Logger;

import javax.swing.JFrame;

import cz.pecina.retro.gui.BackgroundFixedPane;

/**
 * Universal joystick panel.  The joystick is represented as a 5-button
 * gamepad.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class JoystickPanel extends BackgroundFixedPane {

  // static logger
  private static final Logger log =
    Logger.getLogger(JoystickPanel.class.getName());

  // button positions
  private static final int NORTH_OFFSET_X = 30;
  private static final int NORTH_OFFSET_Y = 5;
  private static final int EAST_OFFSET_X = 75;
  private static final int EAST_OFFSET_Y = 30;
  private static final int SOUTH_OFFSET_X = NORTH_OFFSET_X;
  private static final int SOUTH_OFFSET_Y = 75;
  private static final int WEST_OFFSET_X = 5;
  private static final int WEST_OFFSET_Y = EAST_OFFSET_Y;
  private static final int FIRE_OFFSET_X = 35;
  private static final int FIRE_OFFSET_Y = 35;

  // the enclosing frame
  private JFrame frame;

  // the joystick hardware
  private JoystickHardware joystickHardware;

  /**
   * Creates the layered panel containing the elements of the
   * joystick panel.
   *
   * @param frame            enclosing frame
   * @param joystickHardware hardware to operate on
   */
  public JoystickPanel(final JFrame frame,
		       final JoystickHardware joystickHardware) {
    super("jstick/JoystickPanel/mask", "metal", "black");
    log.fine("New joystick panel creation started");
    assert frame != null;
    assert joystickHardware != null;
    this.frame = frame;
    this.joystickHardware = joystickHardware;

    // set up buttons
    joystickHardware.northButton.place(this, NORTH_OFFSET_X, NORTH_OFFSET_Y);
    joystickHardware.eastButton.place(this, EAST_OFFSET_X, EAST_OFFSET_Y);
    joystickHardware.southButton.place(this, SOUTH_OFFSET_X, SOUTH_OFFSET_Y);
    joystickHardware.westButton.place(this, WEST_OFFSET_X, WEST_OFFSET_Y);
    joystickHardware.fireButton.place(this, FIRE_OFFSET_X, FIRE_OFFSET_Y);
    log.finer("Buttons set up");

    log.fine("Joystick panel set up");
  }
}
