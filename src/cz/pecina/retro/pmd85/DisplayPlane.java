/* DisplayPlane.java
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
import java.awt.Graphics;
import javax.swing.JComponent;
import cz.pecina.retro.gui.GUI;
import cz.pecina.retro.gui.Resizeable;

/**
 * Display plane object.  These planes are two in PMD 85 and switching between
 * them implements the effect of blinking pixels.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class DisplayPlane extends JComponent implements Resizeable {

  // static logger
  private static final Logger log =
    Logger.getLogger(DisplayPlane.class.getName());

  // pixel data
  private byte[][] pixels =
    new byte[Display.DISPLAY_HEIGHT][Display.DISPLAY_WIDTH_CELLS];

  // colors
  private Color[][] colors =
    new Color[Display.DISPLAY_HEIGHT][Display.DISPLAY_WIDTH_CELLS];

  /**
   * Creates an instance of a display plane.
   */
  public DisplayPlane() {
    for (int row = 0; row < Display.DISPLAY_HEIGHT; row++) {
      for (int column = 0; column < Display.DISPLAY_WIDTH_CELLS; column++) {
	colors[row][column] = Color.BLACK;
      }
    }
    redrawOnPixelResize();
    GUI.addResizeable(this);
    log.fine("New display plane  created");
  }

  /**
   * Writes one byte of memory-mapped data.
   *
   * @param row        the row, <code>0-0xff</code>
   * @param column     the column, <code>0-0x2f</code>
   * @param pixels     the pixel data; bit 0 is the leftmost, bit 5 the rightmost,
   *                   bits 6 and 7 should be 0
   * @param color      the color to apply
   */
  public void setByte(final int row,
		      final int column,
		      final int pixels,
		      final Color color) {
    log.finest(String.format("Setting byte at (%d,%d) to 0x%02x, color: %s",
			     row, column,
			     pixels,
			     color));
    assert (row >= 0) & (row < 0x100);
    assert (column >= 0) & (column < 0x30);
    if ((this.pixels[row][column] != (byte)pixels) ||
	!colors[row][column].equals(color)) {
      log.finest(String.format(
        "Writing byte, position (%d,%d), data: 0x%02x", row, column, pixels));
      this.pixels[row][column] = (byte)pixels;
      colors[row][column] = color;
    }
  }

  /**
   * Places the display plane on the panel.
   *
   * @param container container where the bitmap will be placed
   * @param positionX x-coordinate, in base-size pixels
   * @param positionY y-coordinate, in base-size pixels
   */
  public void place(final JComponent container,
		    final int positionX,
		    final int positionY) {
    assert container != null;
    log.fine("Placing display plane, position: (" +
	      positionX + "," + positionY + ")");

    final int pixelSize = GUI.getPixelSize();
    setBounds(positionX * pixelSize,
	      positionY * pixelSize,
	      Display.DISPLAY_WIDTH * pixelSize,
	      Display.DISPLAY_HEIGHT * pixelSize);
    final Dimension dim =
      new Dimension(Display.DISPLAY_WIDTH * pixelSize,
		    Display.DISPLAY_HEIGHT * pixelSize);
    setPreferredSize(dim);
    setMaximumSize(dim);
    setMinimumSize(dim);
    container.add(this);
    setVisible(false);
    log.finer("Display plane placed");
  }

  // for description see JComponent
  @Override
  protected void paintComponent(final Graphics graphics) {
    log.finest("Repainting display plane");
    final int pixelSize = GUI.getPixelSize();
    for (int row = 0; row < Display.DISPLAY_HEIGHT; row++) {
      for (int column = 0; column < Display.DISPLAY_WIDTH_CELLS; column++) {
	int p = pixels[row][column];
	final Color c = colors[row][column];
	for (int i = 0; i < 6; i++) {
	  graphics.setColor(((p & 1) == 1) ? c : Color.BLACK);
	  graphics.fillRect(pixelSize * ((column * 6) + i),
			    pixelSize * row,
			    pixelSize,
			    pixelSize);
	  p >>= 1;
	}
      }
    }
    log.finest("Display plane repainted");
  }

  // for description see Resizeable
  @Override
  public void redrawOnPixelResize() {
    log.finest("Display plane redraw started");
    final int pixelSize = GUI.getPixelSize();
    final Dimension dim =
      new Dimension(Display.DISPLAY_WIDTH * pixelSize,
		    Display.DISPLAY_HEIGHT * pixelSize);
    setMinimumSize(dim);
    setPreferredSize(dim);
    setMaximumSize(dim);
    log.finest("Display plane redraw completed");
  }
}
