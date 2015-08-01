/* Digit.java
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
import cz.pecina.retro.common.Util;

/**
 * One counter digit.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Digit extends JComponent implements Resizeable {

  // static logger
  private static final Logger log =
    Logger.getLogger(Digit.class.getName());

  // state of the digit, between 0.0 and 10.0
  private double state;

  // type of the digit
  private String type;

  // color of the digit
  private String color;

  // digits icon
  private Icon digitsIcon;

  /**
   * Createa an instance of a counter digit, initially set to zero.
   *
   * @param type  type of the digit
   * @param color color of the digit
   */
  public Digit(final String type, final String color) {
    log.fine("New Digit creation started, type: " + type +
	     ", color: " + color);
    assert type != null;
    assert color != null;
    this.type = type;
    this.color = color;
    redrawOnPixelResize();
    GUI.addResizeable(this);
    log.fine("New Digit created: " + type + ", " + color);
  }
    
  /**
   * Gets the state of the digit.
   *
   * @return state of the digit
   */
  public double getState() {
    return state;
  }

  /**
   * Set the state of the digit.
   *
   * @param state digit state (<code>0.0</code> - <code>10.0</code>}
   */
  public void setState(double state) {
    state = Util.limit(state, 0.0, 10.0);
    if (Math.abs(state - this.state) > (5.5 / digitsIcon.getIconHeight())) {
      this.state = state;
      repaint();
      log.finer("Digit state changed to: " + state);
    } else
      log.finest("Digit state not changed, remains at " + state);
  }

  /**
   * Places the digit on the panel.
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
	      digitsIcon.getIconWidth(),
	      digitsIcon.getIconHeight() / 11);
    container.add(this);
    log.finer("Digit placed");
  }

  // for description see JComponent
  @Override
  protected void paintComponent(final Graphics graphics) {
    log.finest("Repainting digit");
    digitsIcon.paintIcon(
      this,
      graphics,
      0,
      -(int)Math.round(state * digitsIcon.getIconHeight() / 11));
    log.finest("Digit repainted");
  }

  // for description see Resizeable
  @Override
  public void redrawOnPixelResize() {
    log.finest("Digit redraw started for: " + type + ", " + color +
	       ", " + state);
    final String template = "gui/Digit/%s-%s-%d.png";
    final int pixelSize = GUI.getPixelSize();
    digitsIcon = IconCache.get(String.format(template,
					     type,
					     color,
					     pixelSize));
    final Dimension dim = new Dimension(digitsIcon.getIconHeight(),
					digitsIcon.getIconHeight() / 11);
    setMinimumSize(dim);
    setPreferredSize(dim);
    setMaximumSize(dim);
    repaint();
    log.finest("Digit redraw finished for: " + type + ", " + color +
	       ", " + state);
  }
}
