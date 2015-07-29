/* GenericBitmap.java
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
import javax.swing.JLabel;
import javax.swing.JComponent;
import javax.swing.Icon;
import cz.pecina.retro.common.Application;

/**
 * Generic bitmap that can be used on pixel-size driven panels.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class GenericBitmap extends JLabel {

  // static logger
  private static final Logger log =
    Logger.getLogger(GenericBitmap.class.getName());

  /**
   * Creates a GenericBitmap instance with the specified image.
   *
   * @param image image to be displayed
   */
  public GenericBitmap(final Icon image) {
    super(image);
    log.fine("New GenericBitmap created, image: " + image);
  }

  /**
   * Creates a GenericBitmap instance with no image.  The image must
   * be supplied before the bitmap is placed on a panel.
   */
  public GenericBitmap() {
    super();
    log.fine("New GenericBitmap with no image created");
  }

  /**
   * Places the bitmap on the panel.
   *
   * @param container container where the bitmap will be placed
   * @param positionX x-coordinate, in base-size pixels
   * @param positionY y-coordinate, in base-size pixels
   */
  public void place(final JComponent container,
		    final int positionX,
		    final int positionY) {
    assert container != null;
    final Icon icon = getIcon();
    if (icon == null) {
      log.fine("Attempt to place a bitmap with no Icon");
      throw Application.createError(this, "emptyBitmap");
    }
    setBounds(positionX * GUI.getPixelSize(),
	      positionY * GUI.getPixelSize(),
	      icon.getIconWidth(),
	      icon.getIconHeight());
    final Dimension dim =
      new Dimension(icon.getIconWidth(), icon.getIconHeight());
    setPreferredSize(dim);
    setMaximumSize(dim);
    setMinimumSize(dim);
    container.add(this);
    log.finer("GenericBitmap placed");
  }
}
