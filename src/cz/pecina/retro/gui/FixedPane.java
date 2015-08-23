/* FixedPane.java
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

import java.awt.Dimension;

import javax.swing.JLayeredPane;
import javax.swing.JLabel;
import javax.swing.Icon;

/**
 * Fixed-size layered pane with background bitmap.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class FixedPane extends JLayeredPane {

  // static logger
  private static final Logger log =
    Logger.getLogger(FixedPane.class.getName());
    
  /**
   * Layer used for the background bitmap.
   */
  public static final Integer BACKGROUND_LAYER =
    JLayeredPane.DEFAULT_LAYER - 100;

  /**
   * Dimensions of the pane.
   */
  protected Dimension size;

  /**
   * Creates a fixed pane instance.
   *
   * @param prefix prefix of the background bitmap
   */
  public FixedPane(final String prefix) {
    assert prefix != null;
    setLayout(null);
    final Icon backgroundIcon =
      IconCache.get(prefix + "-" + GUI.getPixelSize() + ".png");
    final JLabel background = new JLabel(backgroundIcon);
    size = new Dimension(backgroundIcon.getIconWidth(),
			 backgroundIcon.getIconHeight());
    setPreferredSize(size);
    setMinimumSize(size);
    setMaximumSize(size);
    background.setBounds(0, 0, size.width, size.height);
    add(background, BACKGROUND_LAYER);
    log.fine("New FixedPane created, background bitmap: " + prefix);
  }
}
