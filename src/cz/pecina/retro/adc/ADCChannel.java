/* ADCChannel.java
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

package cz.pecina.retro.adc;

import java.util.logging.Logger;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JComponent;
import javax.swing.Icon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;
import cz.pecina.retro.gui.Resizeable;
import cz.pecina.retro.gui.GUI;
import cz.pecina.retro.gui.IconCache;
import cz.pecina.retro.common.Util;

/**
 * ADC channel control element.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class ADCChannel extends JLabel implements Resizeable {

  // static logger
  private static final Logger log =
    Logger.getLogger(ADCChannel.class.getName());

  // path offset in base-size pixels
  private int offset;

  // path length in base-size pixels
  private int length;

  // position of the button
  private int screenX, componentX;

  // state of the channel, between 0.0 and 1.0
  private double state;

  /**
   * Creates a horizontal ADC channel control element, initially set
   * to the leftmost position.
   *
   * @param offset the x-offset of the channel's path in base-size pixels
   * @param length the base length of the channel's path in base-size pixels
   */
  public ADCChannel(final int offset, final int length) {
    super(IconCache.get("adc/ADCButton/button-" + GUI.getPixelSize() + ".png"));
    assert offset >= 0;
    assert length > 0;
    this.offset = offset;
    this.length = length;
    GUI.addResizeable(this);
    addMouseListener(new ChannelMouseListener());
    addMouseMotionListener(new ChannelMouseMotionListener());
    log.fine("New ADC channel created, length: " + length);
  }

  // mouse listener
  private class ChannelMouseListener extends MouseAdapter {
    @Override
    public void mousePressed(final MouseEvent event) {
      screenX = event.getXOnScreen();
      componentX = getX();
    }
  }
    
  // mouse motion listener
  private class ChannelMouseMotionListener extends MouseMotionAdapter {
    @Override
    public void mouseDragged(final MouseEvent event) {
      final int pixelSize = GUI.getPixelSize();
      int newX = componentX + event.getXOnScreen() - screenX;
      newX = Util.limit(newX,
			offset * pixelSize,
			(offset + length) * pixelSize);
      state = (newX - (offset * pixelSize)) / (double)(length * pixelSize);
      setLocation(newX, getY());
      repaint();
    }
  }
    
  /**
   * Gets the state of the channel.
   *
   * @return state of the channel, between <code>0.0</code>
   *         and <code>1.0</code>
   */
  public double getState() {
    return state;
  }

  /**
   * Places the channel on the panel.
   *
   * @param container container where the bitmap will be placed
   * @param positionY y-coordinate, in base-size pixels
   */
  public void place(final JComponent container, final int positionY) {
    assert container != null;
    final Icon icon = getIcon();
    assert icon != null;
    final int pixelSize = GUI.getPixelSize();
    int positionX = (int)Math.round((offset + (state * length)) * pixelSize);
    positionX = Util.limit(positionX,
			   offset * pixelSize,
			   (offset + length) * pixelSize);
    setBounds(positionX,
	      positionY * pixelSize,
	      icon.getIconWidth(),
	      icon.getIconHeight());
    final Dimension dim =
      new Dimension(icon.getIconWidth(), icon.getIconHeight());
    setPreferredSize(dim);
    setMaximumSize(dim);
    setMinimumSize(dim);
    container.add(this);
    log.finer("ADC channel placed");
  }

  // for description see Resizeable
  @Override
  public void redrawOnPixelResize() {
    setIcon(IconCache.get("adc/ADCButton/button-" +
			  GUI.getPixelSize() + ".png"));
    log.finer("ADC channel redrawn");
  }
}
