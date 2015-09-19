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

package cz.pecina.retro.ondra;

import java.util.logging.Logger;

import java.util.Set;
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

  // key geometry
  private static final int KEY_OFFSET_X = 40;
  private static final int KEY_OFFSET_Y = 12;
  private static final int KEY_GRID_X = 2;
  private static final int KEY_GRID_Y = 28;

  // NMI button position
  private static final int NMI_OFFSET_X = 14;
  private static final int NMI_OFFSET_Y = 16;

  // enclosing frame
  private Frame frame;

  // keys on the emulated keyboard
  private ToggleButton buttons[] = new ToggleButton[KeyboardLayout.NUMBER_KEYS];
  
  /**
   * Creates a panel containing the mock-up keyboard.
   *
   * @param frame          enclosing frame
   * @param keyboardLayout the keyboard layout
   * @param keys           set of key numbers curently assigned to this shortcut
   */
  public KeyChooserPanel(final Frame frame,
			 final KeyboardLayout keyboardLayout,
			 final Set<Integer> keys) {
    super("ondra/KeyboardPanel/kbdmask",
	  "metal",
	  "black");
    log.fine("New KeyChooserPanel creation started");
    this.frame = frame;
    
    // set up keys
    final int pixelSize = GUI.getPixelSize();
    for (int n = 0; n < KeyboardLayout.NUMBER_KEYS; n++) {
      final KeyboardKey key = keyboardLayout.getKeys()[n];
      final ToggleButton button =
	new ToggleButton("ondra/KeyboardKey/" + key.getCap() + "-%d-%s.png",
			 null,
			 null);
      button.setOnIcon(IconCache.get(String.format(button.getTemplate(),
						   pixelSize,
						   "l")));
      button.setPressed(keys.contains(n));
      button.place(this,
		   KEY_OFFSET_X + (key.getOffsetX() * KEY_GRID_X),
		   KEY_OFFSET_Y + (key.getOffsetY() * KEY_GRID_Y));
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
