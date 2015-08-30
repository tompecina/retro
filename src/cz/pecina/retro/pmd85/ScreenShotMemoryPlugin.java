/* ScreenShotMemoryPlugin.java
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

import java.io.File;
import java.io.IOException;

import java.awt.Color;
import java.awt.Graphics2D;

import java.awt.image.BufferedImage;

import javax.swing.filechooser.FileNameExtensionFilter;

import javax.imageio.ImageIO;

import cz.pecina.retro.common.Parameters;
import cz.pecina.retro.common.Application;

import cz.pecina.retro.cpu.Hardware;

import cz.pecina.retro.memory.MemoryPlugin;

/**
 * Memory plugin for saving screenshots.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class ScreenShotMemoryPlugin implements MemoryPlugin {

  // static logger
  private static final Logger log =
    Logger.getLogger(ScreenShotMemoryPlugin.class.getName());

  // the computer hardware object
  private ComputerHardware computerHardware;

  // active colors
  private PMDColor[] colors;

  // PNG extension filter
  private static FileNameExtensionFilter pngFilter =
    new FileNameExtensionFilter(Application.getString(ScreenShotMemoryPlugin
      .class, "fileFilter.PNG"), "png");
  
  // for description see MemoryPlugin
  @Override
  public String getSaveDescription() {
    return Application.getString(this, "memory.screenshot.save.radio");
  }

  // for description see MemoryPlugin
  @Override
  public FileNameExtensionFilter getFilter() {
    return pngFilter;
  }

  /**
   * Main constructor.
   *
   * @param computerHardware the computer hardware object
   */
  public ScreenShotMemoryPlugin(final ComputerHardware computerHardware) {
    super();
    log.fine("New ScreenShotMemoryPlugin creation started");
    assert computerHardware != null;
    this.computerHardware = computerHardware;
    log.finer("New ScreenShotMemoryPlugin created");
  }
  
  // for description see MemoryPlugin
  @Override
  public void write(final Hardware hardware, final File file) {
    log.fine("Writing screenshot to a file: " + file.getName());
    final byte[] ram =
      Parameters.memoryDevice.getBlockByName("RAM").getMemory();
    final BufferedImage bi = new BufferedImage(
      Display.DISPLAY_WIDTH,
      Display.DISPLAY_HEIGHT,
      BufferedImage.TYPE_INT_RGB);
    final Graphics2D graphics = bi.createGraphics();
    switch (UserPreferences.getColorMode()) {
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
	  colors = UserPreferences.getCustomColors();
    }
    for (int row = 0; row < Display.DISPLAY_HEIGHT; row++) {
      for (int column = 0; column < Display.DISPLAY_WIDTH_CELLS; column++) {
	int b = ram[Display.START_VIDEO + (row * 64) + column];
	for (int i = 0; i < 6; i++) {
	  graphics.setColor(((b & 1) == 1) ?
			    colors[b >> 6].getColor() :
			    Color.BLACK);
	  graphics.fillRect((column * 6) + i, row, 1, 1);
	  b >>= 1;
	}
      }
    }
    try {
      if (!ImageIO.write(bi, "PNG", file)) {
	throw new IOException("Error writing the screenshot");
      }
    } catch (final IOException exception) {
      log.fine("Error: " + exception.getMessage());
      throw Application.createError(this, "PNGWrite");
    }
    log.finer("Screenshot written");
  }

  // for description see MemoryPlugin
  @Override
  public String getSuccessfulSaveString() {
    return Application.getString(this, "memory.screenshot.save.success");
  }
}
