/* DisplayStripe.java
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
import java.awt.Graphics;

import javax.swing.JComponent;

import cz.pecina.retro.gui.GUI;
import cz.pecina.retro.gui.Resizeable;

/**
 * Display stripe object.  These stripes are 16 pixels high and make up the
 * display.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class DisplayStripe extends JComponent implements Resizeable {

  // static logger
  private static final Logger log =
    Logger.getLogger(DisplayStripe.class.getName());

  // the display object
  private Display display;

  // the display hardware object
  private DisplayHardware displayHardware;

  // vertical offset (i.e., the position of the first scan line)
  private int offset;
  
  // pixel data
  private byte[][] pixels;

  /**
   * Creates an instance of a display stripe.
   *
   * @param display the display object
   * @param pixels  pixel matrix containing the whole display area
   * @param offset  the vertical offset (i.e., the position of
   *                the first scan line)
   */
  public DisplayStripe(final Display display,
		       final byte[][] pixels,
		       final int offset) {
    assert display != null;
    assert pixels != null;
    assert (offset >= 0) &&
      (offset <= (Display.DISPLAY_HEIGHT - Display.STRIPE_HEIGHT));
    this.display = display;
    this.pixels = pixels;
    this.offset = offset;
    displayHardware = display.getDisplayHardware();
    redrawOnPixelResize();
    GUI.addResizeable(this);
    log.fine("New display stripe created, offset: " + offset);
  }

  /**
   * Places the display stripe on the panel.
   *
   * @param container container where the bitmap will be placed
   * @param positionX x-coordinate, in base-size pixels
   * @param positionY y-coordinate, in base-size pixels
   */
  public void place(final JComponent container,
		    final int positionX,
		    final int positionY) {
    assert container != null;
    log.fine("Placing display stripe, position: (" +
	      positionX + "," + positionY + ")");

    final int pixelSize = GUI.getPixelSize();
    setBounds(positionX * pixelSize,
	      positionY * pixelSize,
	      Display.DISPLAY_WIDTH * pixelSize,
	      Display.STRIPE_HEIGHT * pixelSize);
    final Dimension dim =
      new Dimension(Display.DISPLAY_WIDTH * pixelSize,
		    Display.STRIPE_HEIGHT * pixelSize);
    setPreferredSize(dim);
    setMaximumSize(dim);
    setMinimumSize(dim);
    container.add(this);
    log.finer("Display stripe placed");
  }

  // paint one cell
  private void paintCell(final int row,
			 final int column,
			 final Graphics graphics) {
    assert (row >= 0) && (row < Display.STRIPE_HEIGHT);
    assert (column >= 0) & (column < Display.DISPLAY_WIDTH_CELLS);
    assert graphics != null;
    if (log.isLoggable(Level.FINER)) {
      log.finer("Painting cell at (" + row + "," + column + ")");
    }
    final int pixelSize = GUI.getPixelSize();
    // if (display.getDisplayHardware().getEnableFlag() &&
	((row + offset) >= (Display.DISPLAY_HEIGHT -
			    display.getDisplayHardware().getScanLines()))) {
      int p = pixels[row + offset][column];
      for (int i = 0; i < 8; i++) {
	graphics.setColor(((p & 0x80) != 0) ? display.color.getColor() : Color.BLACK);
	graphics.fillRect(pixelSize * ((column * 8) + i),
			  pixelSize * (Display.STRIPE_HEIGHT - 1 - row),
			  pixelSize,
			  pixelSize);
	p <<= 1;
      }
    // } else {
    //   graphics.setColor(Color.BLACK);
    //   graphics.fillRect(pixelSize * column * 8,
    // 			pixelSize * (Display.STRIPE_HEIGHT - 1 - row),
    // 			pixelSize * 8,
    // 			pixelSize);
    // }
    log.finest("Cell repainted");
  }

  // for description see JComponent
  @Override
  protected void paintComponent(final Graphics graphics) {
    log.finest("Repainting display stripe");
    for (int row = 0; row < Display.STRIPE_HEIGHT; row++) {
      for (int column = 0; column < Display.DISPLAY_WIDTH_CELLS; column++) {
	paintCell(row, column, graphics);
      }
    }
    log.finest("Display stripe repainted");
  }

  // for description see Resizeable
  @Override
  public void redrawOnPixelResize() {
    log.finest("Display stripe redraw started");
    final int pixelSize = GUI.getPixelSize();
    final Dimension dim =
      new Dimension(Display.DISPLAY_WIDTH * pixelSize,
		    Display.STRIPE_HEIGHT * pixelSize);
    setMinimumSize(dim);
    setPreferredSize(dim);
    setMaximumSize(dim);
    log.finest("Display stripe redraw completed");
  }
}
