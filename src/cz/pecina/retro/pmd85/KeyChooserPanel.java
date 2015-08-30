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

package cz.pecina.retro.pmd85;

import java.util.logging.Logger;

import java.util.NavigableSet;
import java.util.TreeSet;

import java.awt.Frame;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import cz.pecina.retro.gui.BackgroundFixedPane;
import cz.pecina.retro.gui.ToggleButton;
import cz.pecina.retro.gui.GUI;
import cz.pecina.retro.gui.IconCache;

/**
 * The key chooser panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class KeyChooserPanel extends BackgroundFixedPane {

  // static logger
  private static final Logger log =
    Logger.getLogger(KeyChooserPanel.class.getName());

  // base key dimensions
  private static final int BASE_WIDTH = 9;
  private static final int BASE_HEIGHT = BASE_WIDTH;

  // enclosing frame
  private Frame frame;

  // keys on the emulated keyboard
  private ToggleButton buttons[] = new ToggleButton[KeyboardLayout.NUMBER_KEYS];
  
  /**
   * Creates a panel containing the mock-up keyboard.
   *
   * @param frame          enclosing frame
   * @param model          the computer model
   * @param keyboardLayout the keyboard layout
   */
  public KeyChooserPanel(final Frame frame,
			 final int model,
			 final KeyboardLayout keyboardLayout) {
    super("pmd85/KeyboardPanel/" + ((model ==  0) ? "longmask" : "shortmask"),
	  "plastic",
	  "gray");
    log.fine("New KeyChooserPanel creation started");
    this.frame = frame;
    
    // set up keys
    final int pixelSize = GUI.getPixelSize();
    for (int n = 0; n < KeyboardLayout.NUMBER_KEYS; n++) {
      final KeyboardKey key = keyboardLayout.getKeys()[n];
      final ToggleButton button =
	new ToggleButton("pmd85/KeyboardKey/" + key.getCap() + "-%d-%s.png",
			 null,
			 null);
      button.setOnIcon(IconCache.get(String.format(button.getTemplate(),
						   pixelSize,
						   "l")));
      button.place(this,
		   key.getOffsetX() * BASE_WIDTH,
		   key.getOffsetY() * BASE_HEIGHT);
      buttons[n] = button;
      log.finest("Button for key '" + key + "' placed");
    }
    log.fine("KeyChooserPanel set up");
  }

  /**
   * Returns a set of all selected keys.
   *
   * @return a set of all selected keys
   */
  public NavigableSet<Integer> getKeys() {
    final NavigableSet<Integer> set = new TreeSet<>();
    for (int n = 0; n < KeyboardLayout.NUMBER_KEYS; n++) {
      if (buttons[n].isPressed()) {
	set.add(n);
      }
    }
    return set;
  }
}
