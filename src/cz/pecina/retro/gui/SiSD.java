/* SiSD.java
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
 * Sixteen-segment display element.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class SiSD extends JComponent implements Resizeable {

  // static logger
  private static final Logger log =
    Logger.getLogger(SiSD.class.getName());
 
  /**
   * Minimum supported SiSD state (character code).
   */
  public static final int MIN_VALUE = 0x20;

  /**
   * Maximum supported SiSD state (character code).
   */
  public static final int MAX_VALUE = 0x7f;

  /**
   * Number of SiSD states.
   */
  public static final int NUMBER_SISD_STATES = MAX_VALUE - MIN_VALUE + 1;


  /**
   * Number of SiSD segments.
   */
  public static final int NUMBER_SEGMENTS = 16;

  // segment states for ASCII characers 0x20-0x7f
  private static final int[] segments = new int[] {
    0x0000, 0x000c, 0x0204, 0xaac0, 0xaabb, 0xee99, 0x9371, 0x0200,
    0x1400, 0x4100, 0xff00, 0xaa00, 0x4000, 0x8800, 0x0000, 0x4400,
    0x44ff, 0x040c, 0x8877, 0x083f, 0x888c, 0x90b3, 0x88f9, 0x000f,
    0x88ff, 0x889f, 0x2200, 0x4200, 0x9400, 0x8830, 0x4900, 0x2807,
    0x0cf7, 0x88cf, 0x2a3f, 0x00f3, 0x223f, 0x80f3, 0x80c3, 0x08fb,
    0x88cc, 0x2233, 0x007c, 0x94c0, 0x00f0, 0x05cc, 0x11cc, 0x00ff,
    0x88c7, 0x10ff, 0x98c7, 0x88bb, 0x2203, 0x00fc, 0x44c0, 0x50cc,
    0x5500, 0xa884, 0x4433, 0x2212, 0x1100, 0x2221, 0x5000, 0x0030,
    0x0100, 0xa160, 0xa0e0, 0x8060, 0xa260, 0xc070, 0xaa02, 0xa2a1,
    0xa0c0, 0x2000, 0x2261, 0x3600, 0x2200, 0xa848, 0xa040, 0xa060,
    0x82c1, 0xa281, 0x8040, 0xa0a1, 0xaa10, 0x2060, 0x4040, 0x5048,
    0x5500, 0x2500, 0xc020, 0xa212, 0x00c0, 0x2a21, 0x0500, 0x0003
  };

  /**
   * State of the element.
   */
  protected int state = (int)' ';

  // type of the element
  private String type;

  // color of the element
  private String color;

  // background icon
  private Icon backgroundIcon;

  // segment icons
  private final Icon segmentIcon[] = new Icon[NUMBER_SEGMENTS];

  /**
   * Creates an instance of SiSD, initially set to {@code ' '} (blank).
   *
   * @param type  type of the element
   * @param color color of the element
   */
  public SiSD(final String type, final String color) {
    assert type != null;
    assert color != null;
    this.type = type;
    this.color = color;
    redrawOnPixelResize();
    GUI.addResizeable(this);
    log.fine("New SiSD created: " + type + ", " + color);
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
   * @param ch new state of the element, as {@code char}
   */
  public void setState(final char ch) {
    setState((int)ch);
    log.finer("SiSD state changed to: '" + ch + "'");
  }

  /**
   * Sets the state of the element.
   *
   * @param n new state of the element, as {@code int}
   */
  public void setState(final int n) {
    assert (n >= MIN_VALUE) && (n <= MAX_VALUE);
    if (n != state) {
      state = n;
      repaint();
      log.finer("SiSD state changed to: " + n);
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
    log.finer("SiSD placed");
  }

  // for description see JComponent
  @Override
  protected void paintComponent(final Graphics graphics) {
    log.finest("Repainting SiSD");
    backgroundIcon.paintIcon(this, graphics, 0, 0);
    for (int i = 0, r = segments[state - MIN_VALUE];
	 i < NUMBER_SEGMENTS;
	 i++, r >>= 1) {
      if ((r & 1) != 0) {
	segmentIcon[i].paintIcon(this, graphics, 0, 0);
      }
    }
    log.finest("SiSD repainted");
  }

  // for description see Resizeable
  @Override
  public void redrawOnPixelResize() {
    log.finest("SiSD redraw started for: " + type + ", " + color +
	       ", " + state);
    final String template = "gui/SiSD/%s-%s-%d-%s.png";
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
    log.finest("SiSD redraw completed for: " + type + ", " + color +
	       ", " + state);
  }
}
