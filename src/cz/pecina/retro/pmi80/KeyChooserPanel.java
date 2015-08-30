/* KeyChooserPanel.java
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

package cz.pecina.retro.pmi80;

import java.util.logging.Logger;

import java.util.SortedSet;
import java.util.TreeSet;

import java.awt.Frame;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import cz.pecina.retro.gui.FixedPane;
import cz.pecina.retro.gui.ToggleButton;
import cz.pecina.retro.gui.GUI;
import cz.pecina.retro.gui.IconCache;

/**
 * The key chooser panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class KeyChooserPanel extends FixedPane {

  // static logger
  private static final Logger log =
    Logger.getLogger(KeyChooserPanel.class.getName());

  // button matrix geometry
  private static final int BUTTON_GRID_X = 50;
  private static final int BUTTON_GRID_Y = 50;
  private static final int BUTTON_OFFSET_X = 10;
  private static final int BUTTON_OFFSET_Y = 10;

  // enclosing frame
  private Frame frame;

  // keys on the emulated keyboard
  private ToggleButton buttons[] =
    new ToggleButton[KeyboardLayout.NUMBER_BUTTONS];

  /**
   * Creates a panel containing the mock-up keyboard.
   *
   * @param frame          enclosing frame
   * @param keyboardLayout the keyboard layout
   */
  public KeyChooserPanel(final Frame frame,
			 final KeyboardLayout keyboardLayout) {
    super("pmi80/ComputerPanel/buttonchooser-mask");
    log.fine("New KeyChooserPanel creation started");
    this.frame = frame;
    
    // set up keys
    final int pixelSize = GUI.getPixelSize();
    for (int row = 0; row < KeyboardLayout.NUMBER_BUTTON_ROWS; row++) {
      for (int column = 0;
    	   column < KeyboardLayout.NUMBER_BUTTON_COLUMNS;
    	   column++) {
    	final KeyboardButton key =
    	  keyboardLayout.getButton(row, column);
	final ToggleButton button =
	new ToggleButton("pmi80/KeyboardButton/" + key.getId() + "-%d-%s.png",
			 null,
			 null);
      button.setOnIcon(IconCache.get(String.format(button.getTemplate(),
						   pixelSize,
						   "l")));
      button.place(this,
		   (column * BUTTON_GRID_X) + BUTTON_OFFSET_X,
		   (row * BUTTON_GRID_Y) + BUTTON_OFFSET_Y);
      buttons[KeyboardLayout.getButtonNumber(row, column)] = button;
      log.finest("Button for key '" + key + "' placed");
      }
    }
    log.fine("KeyChooserPanel set up");
  }

  /**
   * Returns a set of all selected keys.
   *
   * @return a set of all selected keys
   */
  public SortedSet<Integer> getKeys() {
    final SortedSet<Integer> set = new TreeSet<>();
    for (int n = 0; n < KeyboardLayout.NUMBER_BUTTONS; n++) {
      if (buttons[n].isPressed()) {
	set.add(n);
      }
    }
    return set;
  }
}
