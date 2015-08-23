/* SignESD.java
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
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JComponent;

/**
 * Reduced seven-segment display element with a decimal point.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class SignESD extends ESD {

  // static logger
  private static final Logger log =
    Logger.getLogger(SignESD.class.getName());

  /**
   * Number of SignESD segments.
   */
  public static final int NUMBER_SEGMENTS = 5;

  /**
   * Number of SignESD states.
   */
  public static final int NUMBER_STATES = 1 << NUMBER_SEGMENTS;

  /**
   * Creates an instance of SignESD, initially set to <code>0</code> (blank).
   *
   * @param type  type of the element
   * @param color color of the element
   */
  public SignESD(final String type, final String color) {
    super(type, color);
    log.fine("New SignESD created: " + type + ", " + color);
  }

  // for description see ESD
  @Override
  public void setState(final int n) {
    assert (n >= 0) && (n <= NUMBER_STATES);
    super.setState(n);
  }

  // for description see JComponent
  @Override
  protected void paintComponent(final Graphics graphics) {
    log.finest("Repainting SignESD");
    backgroundIcon.paintIcon(this, graphics, 0, 0);
    for (int i = 0, r = state; i < NUMBER_SEGMENTS; i++, r >>= 1) {
      if ((r & 1) != 0) {
	segmentIcon[i].paintIcon(this, graphics, 0, 0);
      }
    }
    log.finest("SignESD repainted");
  }

  // for description see Resizeable
  @Override
  public void redrawOnPixelResize() {
    log.finest("SignESD redraw started for: " + type + ", " + color +
	       ", " + state);
    final String template = "gui/SignESD/%s-%s-%d-%s.png";
    final int pixelSize = GUI.getPixelSize();
    backgroundIcon = IconCache.get(String.format(template,
						 type,
						 color,
						 pixelSize,
						 "b"));
    for (int i = 0; i < NUMBER_SEGMENTS; i++) {
      segmentIcon[i] = IconCache.get(String.format(template,
						   type,
						   color,
						   pixelSize,
						   String.valueOf(i)));
    }
    final Dimension dim = new Dimension(backgroundIcon.getIconHeight(),
					backgroundIcon.getIconHeight());
    setMinimumSize(dim);
    setPreferredSize(dim);
    setMaximumSize(dim);
    repaint();
    log.finest("SignESD redraw completed for: " + type + ", " + color +
	       ", " + state);
  }
}
