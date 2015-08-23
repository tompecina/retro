/* MemoryPlugin.java
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

package cz.pecina.retro.memory;

import java.io.File;

import javax.swing.filechooser.FileNameExtensionFilter;

import cz.pecina.retro.cpu.Hardware;

/**
 * Plugin for Memory/Save and Memory/Load panels.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public interface MemoryPlugin {

  /**
   * Gets the description of the plugin used on the Save panel.
   */
  public default String getSaveDescription() {
    return null;
  }

  /**
   * Gets the description of the plugin used on the Load panel.
   */
  public default String getLoadDescription() {
    return null;
  }

  /**
   * Gets the file filter.
   */
  public FileNameExtensionFilter getFilter();

  /**
   * Reads the file.
   */
  public default void read(final Hardware hardware, final File file) {
  }

  /**
   * Writes the file.
   */
  public default void write(final Hardware hardware, final File file) {
  }

  /**
   * Gets the string displayed on successful save.
   */
  public default String getSuccessfulSaveString() {
    return null;
  }

  /**
   * Gets the string displayed on successful load.
   */
  public default String getSuccessfulLoadString() {
    return null;
  }
}
