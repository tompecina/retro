/* IconCacha.java
 *
 * Copyright (C) 2014-2015, Tomáš Pecina <tomas@pecina.cz>
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
import java.util.Map;
import java.util.HashMap;
import java.net.URL;
import javax.swing.ImageIcon;
import cz.pecina.retro.Root;
import cz.pecina.retro.common.Application;

/**
 * Cache of image icons.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public final class IconCache {

  // static logger
  private static final Logger log =
    Logger.getLogger(IconCache.class.getName());
    
  // image cache
  private static final Map<String, ImageIcon> cache = new HashMap<>();

  /**
   * Get icon from cache.
   * 
   * @param name icon name (including the prefix)
   * @return icon
   */
  public static ImageIcon get(final String name) {
    log.fine("Requested icon: " + name);
    if (!cache.containsKey(name)) {
      log.finer("Icon '" + name + "' not in cache, fetching from disk");
      final URL icon = Root.class.getResource(name);
      if (icon == null) {
	throw Application.createError(IconCache.class, "imageRead", name);
      }
      cache.put(name, new ImageIcon(icon));
    }
    final ImageIcon icon = cache.get(name);
    log.finer("Icon supplied: " + name + ", " + icon.getIconWidth() +
	      "x" + icon.getIconHeight());
    return icon;
  }
    
  // default constructor disabled
  private IconCache() {};
}
