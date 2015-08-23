/* Background.java
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
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.TexturePaint;
import java.awt.Graphics2D;

import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.JComponent;

/**
 * Tiled background image.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Background extends JComponent {

  // static logger
  private static final Logger log =
    Logger.getLogger(Background.class.getName());

  // type of the image
  private String type;

  // color of the image
  private String color;

  // dimensions of the image, in base-size pixels
  private int width, height;

  // paint of the image
  private TexturePaint paint;

  /**
   * Creates an instance of Background of the defined type, color
   * and dimensions.
   *
   * @param type   type of the image
   * @param color  color of the image
   * @param width  width of the image
   * @param height height of the image
   */
  public Background(final String type,
		    final String color,
		    final int width,
		    final int height) {
    log.fine("New background creation started: " + type + ", " + color +
	     " " + width + "x" + height);
    assert type != null;
    assert color != null;
    assert width > 0;
    assert height > 0;
    this.type = type;
    this.color = color;
    this.width = width;
    this.height = height;
    final Icon icon =
      IconCache.get("gui/Background/" + type + "-" + color + ".png");
    final BufferedImage bufferedImage =
      new BufferedImage(icon.getIconWidth(),
			icon.getIconHeight(),
			BufferedImage.TYPE_INT_RGB);
    final Graphics tempGraphics = bufferedImage.createGraphics();
    icon.paintIcon(null, tempGraphics, 0,0);
    tempGraphics.dispose();
    paint = new TexturePaint(bufferedImage,
			     new Rectangle(icon.getIconWidth(),
					   icon.getIconHeight()));
    final Dimension dim = new Dimension(width * GUI.getPixelSize(),
					height * GUI.getPixelSize());
    setMinimumSize(dim);
    setPreferredSize(dim);
    setMaximumSize(dim);
    log.fine("New background created: " + type + ", " + color +
	     " " + width + "x" + height);
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
	      width * pixelSize,
	      height * pixelSize);
    container.add(this);
    log.finer("Background placed");
  }

  // for description see JComponent
  @Override
  protected void paintComponent(final Graphics graphics) {
    log.finest("Repainting background");
    final Graphics2D g2d = (Graphics2D)graphics;
    g2d.setPaint(paint);
    g2d.fill(new Rectangle(width * GUI.getPixelSize(),
			   height * GUI.getPixelSize()));
    log.finest("Background repainted");
  }
}
