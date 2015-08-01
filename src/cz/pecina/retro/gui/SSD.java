/* SSD.java
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
 * Seven-segment display element.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class SSD extends JComponent implements Resizeable {

  // static logger
  private static final Logger log =
    Logger.getLogger(SSD.class.getName());

  /**
   * Number of SSD segments.
   */
  public static final int NUMBER_SEGMENTS = 7;

  /**
   * Number of SSD states.
   */
  public static final int NUMBER_STATES = 1 << NUMBER_SEGMENTS;

  /**
   * State of the element.
   */
  protected int state;

  // type of the element
  private String type;

  // color of the element
  private String color;

  // background icon
  private Icon backgroundIcon;

  // segment icons
  private final Icon segmentIcon[] = new Icon[NUMBER_SEGMENTS];

  /**
   * Creates an instance of SSD, initially set to <code>0</code> (blank).
   *
   * @param type  type of the element
   * @param color color of the element
   */
  public SSD(final String type, final String color) {
    assert type != null;
    assert color != null;
    this.type = type;
    this.color = color;
    redrawOnPixelResize();
    GUI.addResizeable(this);
    log.fine("New SSD created: " + type + ", " + color);
  }

  /**
   * Gets the state of the element.
   *
   * @return state of the element
   */
  public int getState() {
    return state;
  }

  /**
   * Sets the state of the element.
   *
   * @param n new state of the element
   */
  public void setState(final int n) {
    assert (n >= 0) && (n <= NUMBER_STATES);
    if (n != state) {
      state = n;
      repaint();
      log.finer("SSD state changed to: " + n);
    }
  }

  /**
   * Places the element on the panel.
   *
   * @param container container where the bitmap will be placed
   * @param positionX x-coordinate, in base-size pixels
   * @param positionY y-coordinate, in base-size pixels
   */
  public void place(final JComponent container,
		    final int positionX,
		    final int positionY) {
    assert container != null;
    setBounds(positionX * GUI.getPixelSize(),
	      positionY * GUI.getPixelSize(),
	      backgroundIcon.getIconWidth(),
	      backgroundIcon.getIconHeight());
    container.add(this);
    log.finer("SSD placed");
  }

  // for description see JComponent
  @Override
  protected void paintComponent(final Graphics graphics) {
    log.finest("Repainting SSD");
    backgroundIcon.paintIcon(this, graphics, 0, 0);
    for (int i = 0, r = state; i < NUMBER_SEGMENTS; i++, r >>= 1) {
      if ((r & 1) != 0) {
	segmentIcon[i].paintIcon(this, graphics, 0, 0);
      }
    }
    log.finest("SSD repainted");
  }

  // for description see Resizeable
  @Override
  public void redrawOnPixelResize() {
    log.finest("SSD redraw started for: " + type + ", " + color + ", " + state);
    final String template = "gui/SSD/%s-%s-%d-%s.png";
    final int pixelSize = GUI.getPixelSize();
    backgroundIcon =
      IconCache.get(String.format(template, type, color, pixelSize, "b"));
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
    log.finest("SSD redraw completed for: " + type + ", " + color +
	       ", " + state);
  }
}
