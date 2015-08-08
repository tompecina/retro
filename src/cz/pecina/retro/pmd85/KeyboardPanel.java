/* TapeRecorderPanel.java
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import cz.pecina.retro.gui.BackgroundFixedPane;

/**
 * The PMD 85 keyboard panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class KeyboardPanel extends BackgroundFixedPane {

  // static logger
  private static final Logger log =
    Logger.getLogger(KeyboardPanel.class.getName());

  // base key dimensions
  private static final int BASE_WIDTH = 9;
  private static final int BASE_HEIGHT = BASE_WIDTH;

  // LED positions
  private static final int YELLOW_LED_OFFSET_X = 101;
  private static final int YELLOW_LED_OFFSET_Y = 102;
  private static final int RED_LED_OFFSET_X = 232;
  private static final int RED_LED_OFFSET_Y = YELLOW_LED_OFFSET_Y;
  private static final int GREEN_LED_OFFSET_X = 272;
  private static final int GREEN_LED_OFFSET_Y = YELLOW_LED_OFFSET_Y;
  
  // enclosing frame
  private JFrame frame;

  // keyboard hardware object
  private KeyboardHardware keyboardHardware;

  /**
   * Creates the layered panel containing the keyboard.
   *
   * @param frame            enclosing frame
   * @param keyboardHardware hardware to operate on
   */
  public KeyboardPanel(final JFrame frame,
		       final KeyboardHardware keyboardHardware) {
    super("pmd85/KeyboardPanel/shortmask", "plastic", "gray");
    log.fine("New KeyboardPanel creation started");
    this.frame = frame;
    this.keyboardHardware = keyboardHardware;

    // set up keys
    for (KeyboardKey key: keyboardHardware.getKeyboardLayout().getKeys()) {
      key.place(this,
    		key.getOffsetX() * BASE_WIDTH,
    		key.getOffsetY() * BASE_HEIGHT);
      log.finest("Key '" + key + "' added");
    }
    log.finer("Keys set up");

    // set up LEDs
    keyboardHardware.getYellowLED().place(this, YELLOW_LED_OFFSET_X, YELLOW_LED_OFFSET_Y);
    keyboardHardware.getRedLED().place(this, RED_LED_OFFSET_X, RED_LED_OFFSET_Y);
    keyboardHardware.getGreenLED().place(this, GREEN_LED_OFFSET_X, GREEN_LED_OFFSET_Y);
    keyboardHardware.getGreenLED().setState(1);
    log.finer("LEDs set up");

    log.fine("Keyboard panel set up");
  }

  /**
   * Get the enclosing frame.
   *
   * @return the enclosing frame
   */
  public JFrame getFrame() {
    return frame;
  }

  /**
   * Get the keyboard hardware object.
   *
   * @return the keyboard hardware object
   */
  public KeyboardHardware getKeyboardHardware() {
    return keyboardHardware;
  }
}
