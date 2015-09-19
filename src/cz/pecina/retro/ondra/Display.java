/* Display.java
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

package cz.pecina.retro.ondra;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.awt.Color;
import java.awt.Dimension;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * Display of the Tesla Ondra SPO 186 computer.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Display {

  // static logger
  private static final Logger log =
    Logger.getLogger(Display.class.getName());

  /**
   * Width of the display in pixels.
   */
  public static final int DISPLAY_WIDTH = 320;

  /**
   * Height of the display in pixels.
   */
  public static final int DISPLAY_HEIGHT = 256;

  /**
   * Width of the display in 8-pixel cells.
   */
  public static final int DISPLAY_WIDTH_CELLS = DISPLAY_WIDTH / 8;

  /**
   * Number of display stripes.
   */
  public static final int NUMBER_STRIPES = 16;

  /**
   * Height of the display stripe.
   */
  public static final int STRIPE_HEIGHT = 16;

  /**
   * Start of the video RAM.
   */
  public static final int START_VIDEO = 0xd800;

  // the computer hardware object
  private ComputerHardware computerHardware;

  // the color mode
  private int colorMode;

  // custom color
  private OndraColor customColor;

  // active color
  private OndraColor color;

  // display stripes
  private DisplayStripe[] stripes = new DisplayStripe[NUMBER_STRIPES];
  
  // set the active color
  private void setActiveColor() {
    switch (colorMode) {
      case 0:
	color = OndraColor.WOB_COLOR;
	break;
      case 1:
	color = OndraColor.GOB_COLOR;
	break;
      case 2:
	color = OndraColor.DEFAULT_COLOR;
	break;
      default:
	color = customColor;
    }
  }
  
  /**
   * Creates the display control object.
   *
   * @param computerHardware the computer hardware object
   */
  public Display(final ComputerHardware computerHardware) {
    log.fine("Display creation started");
    assert computerHardware != null;
    this.computerHardware = computerHardware;
    for (int stripe = 0; stripe < NUMBER_STRIPES; stripe++) {
      stripes[stripe] = new DisplayStripe();
    }
    log.fine("Display created");
  }

  /**
   * Repaints any changed stripes.
   */
  public void refresh() {
    for (int stripe = 0; stripe < NUMBER_STRIPES; stripe++) {
	stripes[stripe].refresh();
    }
    log.finest("Display refreshed");
  }

  /**
   * Repaints all stripes.
   */
  public void repaint() {
    for (int stripe = 0; stripe < NUMBER_STRIPES; stripe++) {
	stripes[stripe].repaint();
    }
    log.finest("Display repainted");
  }

  /**
   * Writes one byte of memory-mapped data.
   *
   * @param address the raw address
   * @param data    the byte to be written
   */
  public void setByte(final int address, final int data) {
    assert (address >= 0xc000) && (address < 0x10000);
    final int tb = 0xff - (((address << 1) & 0xff) | ((address & 0x80) >> 7));
    final int stripe = tb >> 4;
    final int row = tb & 0x0f;
    final int column = 0xff - (address >> 8);
    if (log.isLoggable(Level.FINEST)) {
      log.finest(String.format(
        "Writing byte, address: 0x%04x, data: 0x%02x", address, data));
    }
    stripes[stripe].setByte(row, column, data);
  }

  /**
   * Sets the color mode.
   *
   * @param colorMode the color mode
   */
  public void setColorMode(final int colorMode) {
    log.fine("Setting color mode: " + colorMode);
    assert (colorMode >= 0) && (colorMode < OndraColor.NUMBER_COLOR_MODES);
    this.colorMode = colorMode;
    setActiveColor();
  }

  /**
   * Gets the color mode.
   *
   * @return the color mode
   */
  public int getColorMode() {
    return colorMode;
  }

  /**
   * Sets the custom color.
   *
   * @param customColor the custom color
   */
  public void setCustomColor(final OndraColor customColor) {
    log.fine("Setting custom color");
    assert customColor != null;
    this.customColor = customColor;
    setActiveColor();
  }

  /**
   * Gets the custom color.
   *
   * @return the custom color
   */
  public OndraColor getCustomColors() {
    return customColor;
  }

  /**
   * Places the display on the panel.
   *
   * @param container container where the bitmap will be placed
   * @param positionX x-coordinate, in base-size pixels
   * @param positionY y-coordinate, in base-size pixels
   */
  public void place(final JComponent container,
		    final int positionX,
		    final int positionY) {
    assert container != null;
    log.fine("Placing display, position: (" +
	      positionX + "," + positionY + ")");
    for (int stripe = 0; stripe < NUMBER_STRIPES; stripe++) {
      stripes[stripe].place(container,
			    positionX,
			    positionY + (STRIPE_HEIGHT * stripe));
    }
    log.finer("Display placed");
  }
}
