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

  // the display hardware object
  private DisplayHardware displayHardware;

  // the color mode
  private int colorMode;

  // custom color
  private OndraColor customColor;

  // pixel data
  private byte[][] pixels =
    new byte[Display.DISPLAY_HEIGHT][Display.DISPLAY_WIDTH_CELLS];

  // changed flag
  private boolean changed[] = new boolean[NUMBER_STRIPES];
  
  /**
   * The currently active color.
   */
  public OndraColor color = new OndraColor(Color.WHITE);

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
   * @param displayHardware  the display hardware object
   */
  public Display(final ComputerHardware computerHardware,
		 final DisplayHardware displayHardware) {
    log.fine("Display creation started");
    assert computerHardware != null;
    assert displayHardware != null;
    this.computerHardware = computerHardware;
    this.displayHardware = displayHardware;
    for (int stripe = 0; stripe < NUMBER_STRIPES; stripe++) {
      stripes[stripe] = new DisplayStripe(this, pixels, stripe * STRIPE_HEIGHT);
    }
    repaint();
    log.fine("Display created");
  }

  /**
   * Repaints any changed stripes.
   */
  public void refresh() {
    for (int stripe = 0; stripe < NUMBER_STRIPES; stripe++) {
      if (changed[stripe]) {
	stripes[stripe].repaint();
	changed[stripe] = false;
      }
    }
    log.finest("Display refreshed");
  }

  /**
   * Repaints all stripes.
   */
  public void repaint() {
    for (int stripe = 0; stripe < NUMBER_STRIPES; stripe++) {
      stripes[stripe].repaint();
      changed[stripe] = false;
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
    assert (address >= START_VIDEO) && (address < 0x10000);
    assert (data >= 0) && (data < 0x100);
    final int row = ((address << 1) & 0xfe) | ((address >> 7) & 1);
    final int column = 0xff - (address >> 8);
    if (log.isLoggable(Level.FINEST)) {
      log.finest(String.format(
        "Writing byte, address: 0x%04x, data: 0x%02x, row: %d, column: %d",
	address, data, row, column));
    }
    if (pixels[row][column] != (byte)data) {
      pixels[row][column] = (byte)data;
      changed[row >> 4] = true;
    }
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
  public OndraColor getCustomColor() {
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
      stripes[NUMBER_STRIPES - 1 - stripe].place(
        container,
	positionX,
	positionY + (STRIPE_HEIGHT * stripe));
    }
    log.finer("Display placed");
  }

  /**
   * Gets the display hardware.
   *
   * @return the display hardware object
   */
  public DisplayHardware getDisplayHardware() {
    return displayHardware;
  }
}
