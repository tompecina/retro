/* LEDMatrixElement.java
 *
 * Copyright (C) 2014-2015, Tomáš Pecina <tomas@pecina.cz>
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

package cz.pecina.retro.ledmatrix;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.JComponent;
import cz.pecina.retro.gui.Resizeable;
import cz.pecina.retro.gui.GUI;
import cz.pecina.retro.gui.IconCache;

/**
 * 32x32 LED matrix panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class LEDMatrixElement extends JComponent implements Resizeable {

  // static logger
  private static final Logger log =
    Logger.getLogger(LEDMatrixElement.class.getName());

  /**
   * Number of rows.
   */
  public static final int NUMBER_ROWS = 32;

  /**
   * Number of columns.
   */
  public static final int NUMBER_COLUMNS = 32;

  /**
   * State of the element.
   */
  protected int state[][] = new int[NUMBER_ROWS][NUMBER_COLUMNS];

  // graphics
  private Graphics graphics;

  // grid geometry
  private int gridX, gridY;

  // type of the LEDs
  private String type;

  // color of the LEDs
  private String color;

  // icons
  private Icon[] icons = new Icon[2];

  /**
   * Creates an instance of the LED matrix element, initially set to off.
   *
   * @param type  type of the LEDs
   * @param color color of the LEDs
   * @param gridX horizontal distance between LEDs, in pixels
   * @param gridY vertical distance between LEDs, in pixels
   */
  public LEDMatrixElement(final String type,
			  final String color,
			  final int gridX,
			  final int gridY) {
    assert type != null;
    assert color != null;
    assert (gridX > 0) && (gridY > 0);
    log.fine("New LED matrix element creation started: " + type + ", " + color +
	     ", gridX: " + gridX + ", gridY: " + gridY);
    this.type = type;
    this.color = color;
    this.gridX = gridX;
    this.gridY = gridY;
    redrawOnPixelResize();
    GUI.addResizeable(this);
    log.finer("New LED matrix element created: " + type + ", " + color +
	      ", gridX: " + gridX + ", gridY: " + gridY);
  }

  /**
   * Gets the state of a LED.
   *
   * @param  row    row
   * @param  column column
   * @return        state of the LED
   */
  public int getState(final int row, final int column) {
    assert (row >= 0) && (row < NUMBER_ROWS);
    assert (column >= 0) && (column < NUMBER_COLUMNS);
    return state[row][column];
  }

  /**
   * Sets the state of a LED.
   *
   * @param row    row
   * @param column column
   * @param n      new state of the LED
   */
  public void setState(final int row, final int column, final int n) {
    assert (row >= 0) && (row < NUMBER_ROWS);
    assert (column >= 0) && (column < NUMBER_COLUMNS);
    assert (n >= 0) && (n <= 1);
    if (n != state[row][column]) {
      state[row][column] = n;
      repaint();
      log.finer("State of LED at (" + row + "," + column +
		") changed to: " + n);
    }
  }

  /**
   * Sets the state of a LED.
   *
   * @param row    row
   * @param column column
   * @param n      new state of the LED
   */
  public void setState(final int row, final int column, final boolean n) {
    setState(row, column, (n ? 1 : 0));
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
    final int pixelSize = GUI.getPixelSize();
    setBounds(positionX * pixelSize,
    	      positionY * pixelSize,
    	      gridX * NUMBER_COLUMNS * pixelSize,
    	      gridY * NUMBER_ROWS * pixelSize);
    container.add(this);
    log.finer("LED matrix element placed");
  }


  // repaint one LED
  private void repaint(final int row, final int column) {
    icons[state[row][column]].paintIcon(
	this,
	graphics,
	column * gridX * GUI.getPixelSize(),
	row * gridY * GUI.getPixelSize());
    if (log.isLoggable(Level.FINEST)) {
      log.finest("LED at (" + row + "," + column + ") repainted");
    }
  }

  // for description see JComponent
  @Override
  protected void paintComponent(final Graphics graphics) {
    log.finest("Repainting LED matrix");
    this.graphics = graphics;
    for (int row = 0; row < NUMBER_ROWS; row++) {
      for (int column = 0; column < NUMBER_COLUMNS; column++) {
	repaint(row, column);
      }
    }
    log.finer("LED matrix repainted");
  }

  // for description see Resizeable
  @Override
  public void redrawOnPixelResize() {
    log.finest("LED matrix redraw started");
    final String template = "gui/LED/%s-%s-b-%d-%d.png";
    final int pixelSize = GUI.getPixelSize();
    for (int i = 0; i < 2; i++) {
      icons[i] = IconCache.get(String.format(template,
					     type,
					     color,
					     pixelSize,
					     i));
    }
    final Dimension dim = new Dimension(gridX * pixelSize, gridY * pixelSize);
    setMinimumSize(dim);
    setPreferredSize(dim);
    setMaximumSize(dim);
    repaint();
    log.finest("LED element redraw completed");
  }
}
