/* VariableLED.java
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
import java.util.logging.Level;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;

import javax.swing.JComponent;
import javax.swing.Icon;

import cz.pecina.retro.common.Util;

/**
 * LED with variable luminosity, in the range {@code 0.0-1.0}.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class VariableLED extends JComponent implements Resizeable {

  // static logger
  private static final Logger log =
    Logger.getLogger(VariableLED.class.getName());

  // gamma correction exponent
  private static final double LED_GAMMA = 0.2;

  // state of the LED
  private float state;

  // type of the LED
  private String type;

  // color of the LED
  private String color;

  // icons
  private Icon offIcon, onIcon;
    
  /**
   * Creates an instance of a VariableLED, initially set to off.
   *
   * @param type  type of the LED
   * @param color color of the LED
   */
  public VariableLED(final String type, final String color) {
    assert type != null;
    assert color != null;
    this.type = type;
    this.color = color;
    GUI.addResizeable(this);
    redrawOnPixelResize();
    log.fine("New VariableLED created");
  }

  /**
   * Gets the state of the LED.
   *
   * @return state of the LED, {@code 0.0-1.0}
   */
  public float getState() {
    return state;
  }
    
  /**
   * Sets the state of the LED.
   *
   * @param state new state of the LED, {@code 0.0-1.0}
   */
  public void setState(float state) {
    if (log.isLoggable(Level.FINEST)) {
      log.finest("Setting VariableLED state to: " + state);
    }
    state = Util.limit(state, 0f, 1f);
    if (state != this.state) {
      this.state = state;
      repaint();
      if (log.isLoggable(Level.FINER)) {
	final String m = "VariableLED state changed to: " + state;
	if (state > 0) {
	  log.finer(m);
	} else {
	  log.finest(m);
	}
      }
    }
  }


  /**
   * Places the LED on the panel.
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
	      offIcon.getIconWidth(),
	      offIcon.getIconHeight());
    container.add(this);
    log.finer("VariableLED placed");
  }

  // for description see JComponent
  @Override
  protected void paintComponent(final Graphics graphics) {
    log.finest("Repainting VariableLED");
    final Graphics2D graphics2D = (Graphics2D)graphics;
    offIcon.paintIcon(this, graphics2D, 0, 0);
    graphics2D.setComposite(AlphaComposite.getInstance(
      AlphaComposite.SRC_OVER, (float)Math.pow(state, LED_GAMMA)));
    onIcon.paintIcon(this, graphics2D, 0, 0);
    log.finest("VariableLED repainted");
  }

  // for description see Resizeable
  @Override
  public void redrawOnPixelResize() {
    log.finest("VariableLED redraw started for: " + type + ", " + color +
	       ", " + state);
    final String template = "gui/LED/%s-%s-%d-%d.png";
    final int pixelSize = GUI.getPixelSize();
    offIcon =
      IconCache.get(String.format(template, type, color, pixelSize, 0));
    onIcon =
      IconCache.get(String.format(template, type, color, pixelSize, 1));
    final Dimension dim = new Dimension(offIcon.getIconHeight(),
					offIcon.getIconHeight());
    setMinimumSize(dim);
    setPreferredSize(dim);
    setMaximumSize(dim);
    repaint();
    log.finest("VariableLED redraw completed for: " + type + ", " + color +
	       ", " + state);
  }
}
