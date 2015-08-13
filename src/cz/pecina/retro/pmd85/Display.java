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

package cz.pecina.retro.pmd85;

import java.util.logging.Logger;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * Display of the Tesla PMD 85 computer.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Display extends Timer {

  // static logger
  private static final Logger log =
    Logger.getLogger(Display.class.getName());

  /**
   * Width of the display in pixels.
   */
  public static final int DISPLAY_WIDTH = 288;

  /**
   * Height of the display in pixels.
   */
  public static final int DISPLAY_HEIGHT = 256;

  /**
   * Width of the display in 6-pixel cells.
   */
  public static final int DISPLAY_WIDTH_CELLS = DISPLAY_WIDTH / 6;

  // blinking period in msec
  private static final int BLINK_PERIOD = 500;

  // the computer hardware object
  private ComputerHardware computerHardware;

  // pixels
  private byte[][] pixels;
  
  // attributes
  private byte[][] attributes;
  
  // the color mode
  private int colorMode;

  // custom colors
  private PMDColor[] customColors;

  // active colors
  private PMDColor[] colors;

  // the display planes
  private DisplayPlane plane[] = new DisplayPlane[2];

  // the display plane switch
  private int planeSwitch;
  
  // set the active colors
  private void setActiveColors() {
    switch (colorMode) {
      case 0:
	if (computerHardware.getModel() < 3) {
	  colors = PMDColor.WOB_COLORS[0];
	} else {
	  colors = PMDColor.WOB_COLORS[1];
	}
	break;
      case 1:
	if (computerHardware.getModel() < 3) {
	  colors = PMDColor.GOB_COLORS[0];
	} else {
	  colors = PMDColor.GOB_COLORS[1];
	}
	break;
      case 2:
	colors = PMDColor.DEFAULT_COLORS;
	break;
      default:
	  colors = customColors;
    }
  }
  
  /**
   * Creates the display control object.
   *
   * @param computerHardware the computer hardware object
   */
  public Display(final ComputerHardware computerHardware) {
    super(BLINK_PERIOD, null);
    log.fine("Display creation started");
    assert computerHardware != null;
    this.computerHardware = computerHardware;
    plane[0] = new DisplayPlane();
    plane[1] = new DisplayPlane();
    pixels = new byte[DISPLAY_HEIGHT][DISPLAY_WIDTH_CELLS];
    attributes = new byte[DISPLAY_HEIGHT][DISPLAY_WIDTH_CELLS];
    colorMode = UserPreferences.getColorMode();
    customColors = UserPreferences.getCustomColors();
    setActiveColors();
    addActionListener(new BlinkListener());
    start();
    log.fine("Display created");
  }

  // blink listener
  private class BlinkListener implements ActionListener {
    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finest("Switching display planes: " + planeSwitch);
      plane[planeSwitch].setVisible(false);
      planeSwitch = 1 - planeSwitch;
      plane[planeSwitch].setVisible(true);
    }
  }

  /**
   * Writes one byte of memory-mapped data.
   *
   * @param address the raw address
   * @param data    the byte to be written
   */
  public void setByte(final int address, final int data) {
    assert (address >= 0xc000) & (address < 0x10000);
    final int row = (address - 0xc000) / 0x40;
    final int column = address % 0x40;
    if (column < 0x30) {
      log.finest(String.format(
        "Writing byte, address: 0x%04x, data: 0x%02x", address, data));
      final byte p = (byte)(data & 0x3f);
      final byte a = (byte)(data >> 6);
      if ((pixels[row][column] != p) || (attributes[row][column] != a)) {
	pixels[row][column] = p;
	attributes[row][column] = a;
	final Color color = colors[a].getColor();
	plane[0].setByte(row, column, p, color);
	plane[1].setByte(row, column,
			 (colors[a].getBlinkFlag() ? 0 : p),
			 color);
      }
    }
  }

  /**
   * Sets the color mode.
   *
   * @param colorMode the color mode
   */
  public void setColorMode(final int colorMode) {
    log.fine("Setting color mode: " + colorMode);
    assert (colorMode >= 0) && (colorMode < PMDColor.NUMBER_COLOR_MODES);
    this.colorMode = colorMode;
    setActiveColors();
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
   * Sets the custom colors.
   *
   * @param customColors the custom colors
   */
  public void setCustomColors(final PMDColor[] customColors) {
    log.fine("Setting custom colors");
    assert (customColors != null) && (customColors.length == 4);
    this.customColors = customColors;
    setActiveColors();
  }

  /**
   * Gets the custom colors.
   *
   * @return the custom colors
   */
  public PMDColor[] getCustomColors() {
    return customColors;
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
    plane[0].place(container, positionX, positionY);
    plane[1].place(container, positionX, positionY);
    log.finer("Display placed");
  }

  /**
   * Repaints the display planes.
   */
  public void repaint() {
    plane[0].repaint();
    plane[1].repaint();
  }
}
