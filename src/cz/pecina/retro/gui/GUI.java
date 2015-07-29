/* GUI.java
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
import java.util.List;
import java.util.ArrayList;
import java.awt.Image;
import javax.swing.JFrame;

/**
 * Main package class.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class GUI {

  // static logger
  private static final Logger log =
    Logger.getLogger(GUI.class.getName());

  // application-wide pixel size
  private static int pixelSize;

  // list of objects that must be redrawn on pixel size change
  private static final List<Resizeable> resizeables = new ArrayList<>();

  /**
   * Sizes of application icons.
   */
  public static final int[] APPLICATION_ICON_SIZES =
    {16, 24, 32, 48, 64, 96, 128, 192, 256, 384, 512};

  // list of application icons
  private static final List<Image> applicationIcons = new ArrayList<>();

  /**
   * Initializes the package.
   */
  public GUI() {
    log.fine("New GUI created");
  }

  /**
   * Gets application-wide pixel size.
   *
   * @return application-wide pixel size
   */
  public static int getPixelSize() {
    log.finer("Pixel size of " + pixelSize + " retrieved from GUI");
    return pixelSize;
  }

  /**
   * Sets application-wide pixelSize.
   *
   * @param pixelSize application-wide pixel size
   */
  public static void setPixelSize(final int pixelSize) {
    GUI.pixelSize = pixelSize;
    log.finer("Pixel size set to " + pixelSize);
  }

  // for description see Object
  @Override
  public String toString() {
    return "GUI";
  }

  /**
   * Adds an item to the list of objects that must be redrawn
   * on pixel size change.
   *
   * @param item item to be added
   */
  public static void addResizeable(final Resizeable item) {
    resizeables.add(item);
    if (item instanceof JFrame)
      ((JFrame)item).setResizable(false);
    log.finer("Resizeable added: " + item);
  }

  /**
   * Removes an item from the list of objects that must be redrawn
   * on pixel size change.
   *
   * @param item item to be removed
   */
  public static void removeResizeable(final Resizeable item) {
    resizeables.remove(item);
    log.finer("Resizeable removed: " + item);
  }

  /**
   * Redraws all objects in the list of objects that must be redrawn
   * on pixel size change.
   */
  public static void redrawAllResizeables() {
    for (Resizeable item: resizeables)
      item.redrawOnPixelResize();
    log.finer("All resizeables redrawn");
  }

  /**
   *  Adds an image to the list of application icons.
   *
   * @param image the image to be added
   */
  public static void addApplicationIcon(final Image image) {
    applicationIcons.add(image);
  }

  /**
   *  Sets application icons for a frame.
   *
   * @param frame the frame
   */
  public static void setApplicationIcons(final JFrame frame) {
    frame.setIconImages(applicationIcons);
  }
}
