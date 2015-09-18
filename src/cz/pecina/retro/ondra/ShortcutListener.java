/* ShortcutListener.java
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

import java.util.NavigableSet;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import cz.pecina.retro.gui.BackgroundFixedPane;
import cz.pecina.retro.gui.GUI;
import cz.pecina.retro.gui.Shortcut;
import cz.pecina.retro.gui.LED;

/**
 * Keyboard shortcut listener.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class ShortcutListener extends KeyAdapter {

  // static logger
  private static final Logger log =
    Logger.getLogger(ShortcutListener.class.getName());

  // keyboard hardware
  private KeyboardHardware keyboardHardware;
  
  /**
   * Creates the keyboard shortcut listener.
   *
   * @param keyboardHardware the keyboard hardware to operate on
   */
  public ShortcutListener(final KeyboardHardware keyboardHardware) {
    super();
    assert keyboardHardware != null;
    log.fine("New ShortcutListener creation started");

    this.keyboardHardware = keyboardHardware;

    log.finer("ShortcutListener set up");
  }

  // process key event
  private void processKey(final KeyEvent event, final boolean action) {
    log.finer("Processing key event: " + event + ", action: " + action);
    final KeyboardLayout layout = keyboardHardware.getKeyboardLayout();
    final Shortcut shortcut =
      new Shortcut(event.getExtendedKeyCode(), event.getKeyLocation());
    if (UserPreferences.getShortcuts().containsKey(shortcut)) {
      final NavigableSet<Integer> keys =
	UserPreferences.getShortcuts().get(shortcut);
      for (int key: keys) {
	final KeyboardKey button = layout.getKey(key);
	log.finest("Processing key: " + key + ", button: " + button.getCap());
	if (!button.isLocked() && (button.isPressed() != action)) {
	  button.setPressed(action);
	}
      }
      event.consume();
    }
  }

  // for description see KeyListener
  @Override
  public void keyPressed(final KeyEvent event) {
    processKey(event, true);
  }
  
  // for description see KeyListener
  @Override
  public void keyReleased(final KeyEvent event) {
    processKey(event, false);
  }
}
