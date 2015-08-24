/* Needle.java
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
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.RenderingHints;

import java.awt.geom.Line2D;

import javax.swing.JComponent;

/**
 * Two-segment fixed-width meter needle.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Needle extends JComponent implements Resizeable {

  // static logger
  private static final Logger log =
    Logger.getLogger(Needle.class.getName());

  // angle of the meter, in degrees, CW; 0.0 means upright position
  private double angle;

  // dimensions of the component window, in base-size pixels
  private int width, height;

  // coordinates of the needle's axis center, in base-size pixels
  private double centerX, centerY;

  // length of the needle, in base-size pixels
  private double length;

  // thickness of the needle, in base-size pixels
  private double thickness;

  // ratio of the inner segment, between 0.0 (no inner segment)
  // and 1.0 (no outer segment)
  private double ratio;

  // colors of the segments
  private Color innerColor, outerColor;

  /**
   * Creates a new needle object.
   *
   * @param width        width of the component window, in base-size pixels
   * @param height       height width of the component window, in
   *                     base-size pixels
   * @param centerX      x-coordinate of the needle's axis center, in
   *                     base-size pixels
   * @param centerY      y-coordinate of the needle's axis center, in
   *                     base-size pixels
   * @param length       length of the needle, in base-size pixels
   * @param thickness    thickness of the needle, in base-size pixels
   * @param ratio        ratio of the inner segment, between
   *                     {@code 0.0} (no inner segment)
   *                     and {@code 1.0} (no outer segment)
   * @param innerColor   {@code Color} of the inner segment
   * @param outerColor   {@code Color} of the outer segment
   * @param initialAngle initial angle of the needle, in degrees, CW;
   *                     {@code 0.0} means upright position
   */
  public Needle(final int width,
		final int height,
		final double centerX,
		final double centerY,
		final double length,
		final double thickness,
		final double ratio,
		final Color innerColor,
		final Color outerColor,
		final double initialAngle) {
    log.fine("New needle creation started");
    assert width > 0;
    assert height > 0;
    assert length > 0.0;
    assert thickness > 0.0;
    assert (ratio >= 0.0) && (ratio <= 1.0);
    assert innerColor != null;
    assert outerColor != null;
    this.width = width;
    this.height = height;
    this.centerX = centerX;
    this.centerY = centerY;
    this.length = length;
    this.thickness = thickness;
    this.ratio = ratio;
    this.innerColor = innerColor;
    this.outerColor = outerColor;
    angle = initialAngle;
    redrawOnPixelResize();
    GUI.addResizeable(this);
    log.fine("New needle created");
  }
    
  /**
   * Gets the angle of the needle.
   *
   * @return angle of the needle
   */
  public double getAngle() {
    return angle;
  }

  /**
   * Sets the angle of the needle.
   *
   * @param angle new angle of the needle
   */
  public void setAngle(final double angle) {
    if (angle != this.angle) {
      this.angle = angle;
      repaint();
      log.finer("Needle angle changed to: " + angle);
    }
  }

  /**
   * Places the needle on the panel.
   *
   * @param container container where the needle will be placed
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
    log.finer("Needle placed");
  }

  // for description see JComponent
  @Override
  protected void paintComponent(final Graphics graphics) {
    log.finest("Repainting Needle");
    final Graphics2D g2d = (Graphics2D)graphics;
    g2d.setRenderingHints(new RenderingHints(
      RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

    // preliminary calculations
    final int pixelSize = GUI.getPixelSize();
    final double cx = centerX * pixelSize;
    final double cy = centerY * pixelSize;
    final double phi = Math.toRadians(angle);
    final double l = length * pixelSize;
    final double ex = cx + (Math.sin(phi) * l);
    final double ey = cy - (Math.cos(phi) * l);
    final double sx = cx + ratio * (ex - cx);
    final double sy = cy + ratio * (ey - cy);
    final float t = (float)thickness * pixelSize;

    // paint outer segment
    if (ratio != 1.0) {
      final Line2D outerSegment = new Line2D.Double(sx, sy, ex, ey);
      g2d.setPaint(outerColor);
      g2d.setStroke(new BasicStroke(t,
				    BasicStroke.CAP_ROUND,
				    BasicStroke.JOIN_MITER));
      g2d.draw(outerSegment);
    }

    // paint inner segment
    if (ratio != 0.0) {
      final Line2D innerSegment = new Line2D.Double(cx, cy, sx, sy);
      g2d.setPaint(innerColor);
      g2d.setStroke(new BasicStroke(t,
				    BasicStroke.CAP_BUTT,
				    BasicStroke.JOIN_MITER));
      g2d.draw(innerSegment);
    }

    log.finest("Needle repainted");
  }

  // for description see Resizeable
  @Override
  public void redrawOnPixelResize() {
    log.finest("Needle redraw started");
    final int pixelSize = GUI.getPixelSize();
    final Dimension dim =
      new Dimension(width * pixelSize, height * pixelSize);
    setMinimumSize(dim);
    setPreferredSize(dim);
    setMaximumSize(dim);
    repaint();
    log.finest("Needle redraw completed");
  }
}
