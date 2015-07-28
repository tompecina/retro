/* BackgroundFixedPane.java
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
import java.awt.Dimension;
import javax.swing.JLayeredPane;
import javax.swing.JLabel;
import javax.swing.Icon;

/**
 * Fixed-size layered pane with background bitmap and
 * tiled underlying texture.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class BackgroundFixedPane extends FixedPane {

  // static logger
  private static final Logger log =
    Logger.getLogger(BackgroundFixedPane.class.getName());
    
  /**
   * Layer used for the tiled background bitmap.
   */
  public static final Integer TILED_BACKGROUND_LAYER =  BACKGROUND_LAYER - 50;

  /**
   * Creates a fixed pane instance.
   *
   * @param prefix prefix of the background bitmap
   * @param type   type of the background texture
   * @param color  color of the background texture
   */
  public BackgroundFixedPane(final String prefix,
			     final String type,
			     final String color) {
    super(prefix);
    assert type != null;
    assert color != null;
    final Background background =
      new Background(type, color, size.width, size.height);
    background.setBounds(0, 0, size.width, size.height);
    add(background, TILED_BACKGROUND_LAYER);
    log.fine("New BackgroundFixedPane created: " + prefix + ", " +
	     type + ", " + color);
  }
}
