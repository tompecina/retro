/* KeyboardFrame.java
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
import cz.pecina.retro.gui.Resizeable;
import cz.pecina.retro.gui.GUI;

/**
 * The PMD 85 keyboard frame.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class KeyboardFrame extends HidingFrame implements Resizeable {

  // static logger
  private static final Logger log =
    Logger.getLogger(KeyboardFrame.class.getName());

  // keyboard panel
  private KeyboardPanel keyboardPanel;

  // keyboard hardware object
  private KeyboardHardware keyboardHardware;

  /**
   * Creates the keyboard frame.
   *
   * @param computer         the computer control object
   * @param keyboardHardware hardware to operate on
   */
  public KeyboardFrame(final Computer computer,
		       final KeyboardHardware keyboardHardware) {
    super(Application.getString(KeyboardFrame.class,
      "keyboard.frameTitle"), computer.getIconLayout()
      .getIcon(IconLayout.ICON_POSITION_KEYBOARD));
    log.fine("New KeyboardFrame creation started");
    this.keyboardHardware = keyboardHardware;
    keyboardPanel = new KeyboardPanel(this, keyboardHardware);
    add(keyboardPanel);
    pack();
    GUI.addResizeable(this);
    log.fine("KeyboardFrame set up");
  }

  /**
   * Gets the keyboard panel.
   *
   * @return the keyboard panel
   */
  public KeyboardPanel getKeyboardPanel() {
    return keyboardPanel;
  }
  
  // redraw frame
  private void redraw() {
    log.fine("KeyboardFrame redraw started");
    super.setTitle(Application.getString(this, "keyboard.frameTitle"));
    remove(keyboardPanel);
    keyboardPanel = new KeyboardPanel(this, keyboardHardware);
    add(keyboardPanel);
    pack();
    log.fine("KeyboardFrame redraw completed");
  }

  // for description see Localized
  @Override
  public void redrawOnLocaleChange() {
    redraw();
  }

  // for description see Resizeable
  @Override
  public void redrawOnPixelResize() {
    redraw();
  }
}
