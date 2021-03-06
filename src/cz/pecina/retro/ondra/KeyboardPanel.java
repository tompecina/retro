/* KeyboardPanel.java
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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

import cz.pecina.retro.gui.BackgroundFixedPane;
import cz.pecina.retro.gui.Shortcut;
import cz.pecina.retro.gui.LED;
import cz.pecina.retro.gui.VariableLED;

/**
 * The Tesla Ondra SPO 186 keyboard panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class KeyboardPanel extends BackgroundFixedPane {

  // static logger
  private static final Logger log =
    Logger.getLogger(KeyboardPanel.class.getName());

  // key geometry
  private static final int KEY_OFFSET_X = 40;
  private static final int KEY_OFFSET_Y = 12;
  private static final int KEY_GRID_X = 2;
  private static final int KEY_GRID_Y = 28;

  // NMI button position
  private static final int NMI_OFFSET_X = 14;
  private static final int NMI_OFFSET_Y = 16;

  // LED positions
  private static final int YELLOW_LED_OFFSET_X = 18;
  private static final int YELLOW_LED_OFFSET_Y = 76;
  private static final int GREEN_LED_OFFSET_X = YELLOW_LED_OFFSET_X;
  private static final int GREEN_LED_OFFSET_Y = 103;
  
  // enclosing frame
  private JFrame frame;

  // keyboard hardware object
  private KeyboardHardware keyboardHardware;

  /**
   * Creates a layered panel containing the keyboard.
   *
   * @param frame            enclosing frame
   * @param keyboardHardware hardware to operate on
   */
  public KeyboardPanel(final JFrame frame,
		       final KeyboardHardware keyboardHardware) {
    super("ondra/KeyboardPanel/kbdmask", "metal", "black");
    log.fine("New KeyboardPanel creation started");
    this.frame = frame;
    this.keyboardHardware = keyboardHardware;

    // set up keys
    for (KeyboardKey key: keyboardHardware.getKeyboardLayout().getKeys()) {
      key.place(this,
    		KEY_OFFSET_X + (key.getOffsetX() * KEY_GRID_X),
    		KEY_OFFSET_Y + (key.getOffsetY() * KEY_GRID_Y));
      log.finest("Key '" + key + "' added");
    }
    log.finer("Keys set up");

    // set up the NMI button
    keyboardHardware.getNmiButton().place(this,
					  NMI_OFFSET_X,
					  NMI_OFFSET_Y);
    log.finer("NMI button set up");

    // set up keyboard shortcuts
    setShortcuts();

    // set up LEDs
    keyboardHardware.getYellowLED().place(this,
					  YELLOW_LED_OFFSET_X,
					  YELLOW_LED_OFFSET_Y);
    keyboardHardware.getGreenLED().place(this,
					 GREEN_LED_OFFSET_X,
					 GREEN_LED_OFFSET_Y);
    log.finer("LEDs set up");

    log.fine("Keyboard panel set up");
  }

  /**
   * Gets the enclosing frame.
   *
   * @return the enclosing frame
   */
  public JFrame getFrame() {
    return frame;
  }

  /**
   * Gets the keyboard hardware object.
   *
   * @return the keyboard hardware object
   */
  public KeyboardHardware getKeyboardHardware() {
    return keyboardHardware;
  }

  /**
   * Sets up keyboard shortcuts.
   */
  public void setShortcuts() {
    log.finer("Setting up keyboard shortcuts");
    frame.addKeyListener(new ShortcutListener(keyboardHardware));
    log.finer("Keyboard shortcuts set up");
  }
}
