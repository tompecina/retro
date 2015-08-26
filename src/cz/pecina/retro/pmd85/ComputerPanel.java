/* ComputerPanel.java
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

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import cz.pecina.retro.gui.BackgroundFixedPane;
import cz.pecina.retro.gui.GUI;
import cz.pecina.retro.gui.Shortcut;

/**
 * Main control panel of the computer.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class ComputerPanel extends BackgroundFixedPane {

  // static logger
  private static final Logger log =
    Logger.getLogger(ComputerPanel.class.getName());
    
  // display position
  private static final int DISPLAY_OFFSET_X = 12;
  private static final int DISPLAY_OFFSET_Y = 12;

  // LED positions
  private static final int LED_OFFSET_X = 267;
  private static final int LED_OFFSET_Y = 277;
  private static final int LED_GRID_X = 12;
  
  // brand marking position
  private static final int MARKING_OFFSET_X = 12;
  private static final int MARKING_OFFSET_Y = 278;

  // the enclosing frame
  private ComputerFrame computerFrame;
  
  // the computer control object
  private Computer computer;
  
  // keyboard hardware
  private KeyboardHardware keyboardHardware;
  
  /**
   * Creates the layered panel containing the elements of the main
   * control panel.
   *
   * @param computerFrame    the enclosing frame
   * @param computer         the computer control object
   * @param displayHardware  the display hardware to operate on
   * @param keyboardHardware the keyboard hardware to operate on
   */
  public ComputerPanel(final ComputerFrame computerFrame,
		       final Computer computer,
		       final DisplayHardware displayHardware,
		       final KeyboardHardware keyboardHardware
		       ) {
    super("pmd85/ComputerPanel/mask", "plastic", "gray");
    assert computerFrame != null;
    assert computer != null;
    assert displayHardware != null;
    assert keyboardHardware != null;
    log.fine("New ComputerPanel creation started");

    this.computerFrame = computerFrame;
    this.computer = computer;
    this.keyboardHardware = keyboardHardware;
    
    // set up keyboard shortcuts
    setShortcuts();

    // set up display
    displayHardware.getDisplay().place(this,
				       DISPLAY_OFFSET_X,
				       DISPLAY_OFFSET_Y);
    log.finer("Display set up");

    // set up LEDs
    final ComputerHardware computerHardware = computer.getComputerHardware();
    computerHardware.getYellowLED().place(this,
					  LED_OFFSET_X,
					  LED_OFFSET_Y);
    computerHardware.getRedLED().place(this,
				       LED_OFFSET_X + LED_GRID_X,
				       LED_OFFSET_Y);
    computerHardware.getGreenLED().place(this,
					 LED_OFFSET_X + (2 * LED_GRID_X),
					 LED_OFFSET_Y);
    log.finer("LEDs set up");

    // set up brand marking
    computerHardware.getMarking().place(this, MARKING_OFFSET_X, MARKING_OFFSET_Y);
    log.finer("Brand marking set up");
	
    // set up icons
    for (int i = 0; i < IconLayout.NUMBER_ICONS; i++) {
      final IconButton icon = computer.getIconLayout().getIcon(i);
      icon.place(this, icon.getPositionX(), icon.getPositionY());
    }
    log.finer("Icons set up");

    log.fine("Computer control panel set up");
  }

  /**
   * Sets up keyboard shortcuts.
   */
  public void setShortcuts() {
    log.finer("Setting up keyboard shortcuts");
    computerFrame.addKeyListener(new ShortcutListener());
    log.finer("Keyboard shortcuts set up");
  }

  // shortcut listener
  private class ShortcutListener extends KeyAdapter {
    @Override
    public void keyPressed(final KeyEvent event) {
      final Shortcut shortcut  =
	new Shortcut(event.getExtendedKeyCode(), event.getKeyLocation());
      for (int i = 0; i < KeyboardLayout.NUMBER_KEYS; i++) {
	final KeyboardKey key =
	  keyboardHardware.getKeyboardLayout().getKey(i);
	if (shortcut.equals(key.getShortcut())) {
	  key.setPressed(true);
	  break;
	}
      }
    }
    @Override
    public void keyReleased(final KeyEvent event) {
      final Shortcut shortcut  =
	new Shortcut(event.getExtendedKeyCode(), event.getKeyLocation());
      for (int i = 0; i < KeyboardLayout.NUMBER_KEYS; i++) {
	final KeyboardKey key =
	  keyboardHardware.getKeyboardLayout().getKey(i);
	if (shortcut.equals(key.getShortcut())) {
	  key.setPressed(false);
	  break;
	}
      }
    }
  }
}
