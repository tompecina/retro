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
import java.awt.Dimension;
import javax.swing.JComponent;
import cz.pecina.retro.gui.GUI;

/**
 * Display of the Tesla PMD 85 computer.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Display extends JComponent {

  // static logger
  private static final Logger log =
    Logger.getLogger(Display.class.getName());

  // dimensions of the display
  private static final int DISPLAY_WIDTH = 288;
  private static final int DISPLAY_HEIGHT = 256;
  private static final int DISPLAY_WIDTH_CELLS = DISPLAY_WIDTH / 6;

  // pixels
  private byte[][] pixels = new byte [DISPLAY_HEIGHT][DISPLAY_WIDTH_CELLS];
  
  // attributes
  private byte[][] attributes = new byte [DISPLAY_HEIGHT][DISPLAY_WIDTH_CELLS];
  
  // the color mode
  private int colorMode = UserPreferences.getColorMode();

  // custom colors
  private PMDColor[] customColors = UserPreferences.getCustomColors();
  
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
      }
    }
  }

  /**
   * Sets the color mode.
   *
   * @param computer  the computer control object
   * @param colorMode the color mode
   */
  public void setColorMode(final Computer computer, final int colorMode) {
    log.fine("Setting color mode: " + colorMode);
    assert computer != null;
    assert (colorMode >= 0) && (colorMode < PMDColor.NUMBER_COLOR_MODES);
    this.colorMode = colorMode;
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
   * @param computer     the computer control object
   * @param customColors the custom colors
   */
  public void setCustomColors(final Computer computer, final PMDColor[] customColors) {
    log.fine("Setting custom colors");
    assert computer != null;
    assert (customColors != null) && (customColors.length == 4);
    this.customColors = customColors;
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

    final int pixelSize = GUI.getPixelSize();
    setBounds(positionX * pixelSize,
	      positionY * pixelSize,
	      DISPLAY_WIDTH * pixelSize,
	      DISPLAY_HEIGHT * pixelSize);
    final Dimension dim =
      new Dimension(DISPLAY_WIDTH * pixelSize, DISPLAY_HEIGHT * pixelSize);
    setPreferredSize(dim);
    setMaximumSize(dim);
    setMinimumSize(dim);
    container.add(this);
    log.finer("Display placed");
  }
}
