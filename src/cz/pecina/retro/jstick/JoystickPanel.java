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
  private static final int UP_OFFSET_X = 24;
  private static final int UP_OFFSET_Y = 4;
  private static final int RIGHT_OFFSET_X = 60;
  private static final int RIGHT_OFFSET_Y = UP_OFFSET_X;
  private static final int DOWN_OFFSET_X = UP_OFFSET_X;
  private static final int DOWN_OFFSET_Y = RIGHT_OFFSET_X;
  private static final int LEFT_OFFSET_X = UP_OFFSET_Y;
  private static final int LEFT_OFFSET_Y = RIGHT_OFFSET_Y;
  private static final int FIRE_OFFSET_X = 28;
  private static final int FIRE_OFFSET_Y = FIRE_OFFSET_X;

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
    joystickHardware.upButton.place(this, UP_OFFSET_X, UP_OFFSET_Y);
    joystickHardware.rightButton.place(this, RIGHT_OFFSET_X, RIGHT_OFFSET_Y);
    joystickHardware.downButton.place(this, DOWN_OFFSET_X, DOWN_OFFSET_Y);
    joystickHardware.leftButton.place(this, LEFT_OFFSET_X, LEFT_OFFSET_Y);
    joystickHardware.fireButton.place(this, FIRE_OFFSET_X, FIRE_OFFSET_Y);
    log.finer("Buttons set up");

    log.fine("Joystick panel set up");
  }
}
