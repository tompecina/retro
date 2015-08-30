/* ShortcutEvent.java
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

/**
 * Keyboard shortcut event.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class ShortcutEvent {

  // static logger
  private static final Logger log =
    Logger.getLogger(ShortcutEvent.class.getName());

  // the key
  private KeyboardKey key;

  // true if pressed, false if released
  private boolean action;
  
  /**
   * Creates a keyboard shortcut event.
   *
   * @param key    the key object
   * @param action {@code true} if pressed, {@code false} if released
   */
  public ShortcutEvent(final KeyboardKey key, final boolean action) {
    assert key != null;
    log.fine("New ShortcutEvent creation started");

    this.key = key;
    this.action = action;

    log.finer("Shortcut event set up");
  }

  /**
   * Gets the key.
   *
   * @return the key object
   */
  public KeyboardKey getKey() {
    return key;
  }


  /**
   * Gets the action.
   *
   * @return {@code true} if pressed, {@code false} if released
   */
  public boolean getAction() {
    return action;
  }
}
