/* Swatch.java
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
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.BorderFactory;

/**
 * Square fixed-size swatch.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Swatch extends JComponent {

  // static logger
  private static final Logger log =
    Logger.getLogger(Swatch.class.getName());

  // size of the swatch, in pixels
  private int size;

  // color of the swatch
  private Color color;

  /**
   * Creates a swatch,
   *
   * @param type  type of the element
   * @param color color of the element
   */
  public Swatch(final int size, final Color color) {
    assert size > 0;
    assert color != null;
    this.size = size;
    this.color = color;
    final Dimension dim = new Dimension(size, size);
    setMinimumSize(dim);
    setPreferredSize(dim);
    setMaximumSize(dim);
    setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
    repaint();
    log.fine("New swatch created, size: " + size + ", color: " + color);
  }

  /**
   * Sets the color of the swatch.
   *
   * @param color new color of the swatch
   */
  public void setState(final Color color) {
    assert color != null;
    this.color = color;
    repaint();
    log.finer("Swatch color changed to: " + color);
  }

  /**
   * Gets the color of the swatch.
   *
   * @return the color of the swatch
   */
  public Color getColor() {
    return color;
  }

  // for description see JComponent
  @Override
  protected void paintComponent(final Graphics graphics) {
    log.finest("Repainting Swatch");
    graphics.setColor(color);
    graphics.fillRect(0, 0, size, size);
    log.finest("Swatch repainted");
  }
}
