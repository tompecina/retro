/* JoystickFrame.java
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

package cz.pecina.retro.ondra;

import java.util.logging.Logger;

import cz.pecina.retro.common.Application;

import cz.pecina.retro.jstick.JoystickPanel;

/**
 * The Joystick frame.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class JoystickFrame extends HidingFrame {

  // static logger
  private static final Logger log =
    Logger.getLogger(JoystickFrame.class.getName());

  // the computer object
  private Computer computer;

  // joystick panel
  private JoystickPanel joystickPanel;

  /**
   * Creates the Joystick frame.
   *
   * @param computer the computer object
   */
  public JoystickFrame(final Computer computer) {
    super(Application.getString(JoystickFrame.class, "joystick.frameTitle"),
	  computer.getIconLayout().getIcon(IconLayout.ICON_POSITION_MEM));
    log.fine("New JoystickFrame creation started");
    assert computer != null;
    this.computer = computer;

    joystickPanel = new JoystickPanel(this, computer);
    add(joystickPanel);
    pack();
    log.fine("JoystickFrame set up");
  }

  // redraw frame
  private void redraw() {
    log.fine("JoystickFrame redraw started");
    super.setTitle(Application.getString(this, "joystick.frameTitle"));
    remove(joystickPanel);
    joystickPanel = new JoystickPanel(this, computer);
    add(joystickPanel);
    pack();
    log.fine("JoystickFrame redraw completed");
  }

  // for description see Localized
  @Override
  public void redrawOnLocaleChange() {
    redraw();
  }
}
