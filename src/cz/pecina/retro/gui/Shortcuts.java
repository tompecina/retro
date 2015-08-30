/* Shortcuts.java
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

package cz.pecina.retro.gui;

import java.util.logging.Logger;

import java.util.TreeMap;
import java.util.SortedSet;
import java.util.Comparator;

/**
 * All defined keyboard shortcuts as one object.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Shortcuts extends TreeMap<Shortcut,SortedSet<Integer>> {

  // static logger
  private static final Logger log =
    Logger.getLogger(Shortcuts.class.getName());

  // shortcut comparator
  private static class ShortcutComparator
    implements Comparator<Shortcut> {

    // for description see Comparator
    @Override
    public int compare(final Shortcut shortcut1, final Shortcut shortcut2) {
      final int kc = Integer.compare(shortcut1.getKeyCode(),
				     shortcut2.getKeyCode());
      if (kc != 0) {
	return kc;
      } else {
	return Integer.compare(shortcut1.getKeyLocation(),
			       shortcut2.getKeyLocation());
      }
    }
  }

  /**
   * Creates an instance of keyboard shortcuts.
   */
  public Shortcuts() {
    super(new ShortcutComparator());
    log.fine("New Shortcuts created");
  }
}
