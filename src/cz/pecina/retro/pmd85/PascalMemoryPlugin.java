/* PascalMemoryPlugin.java
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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.IOException;

import javax.swing.filechooser.FileNameExtensionFilter;

import cz.pecina.retro.common.Parameters;
import cz.pecina.retro.common.Application;

import cz.pecina.retro.cpu.Hardware;

import cz.pecina.retro.memory.MemoryPlugin;

/**
 * Memory plugin for saving/loading Pascal programs.  It is
 * compatible with Pascal-PMD V1.02 and TOM Pascal V2.2.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class PascalMemoryPlugin implements MemoryPlugin {

  // static logger
  private static final Logger log =
    Logger.getLogger(PascalMemoryPlugin.class.getName());

  // PAS extension filter
  private static FileNameExtensionFilter pasFilter =
    new FileNameExtensionFilter(Application.getString(PascalMemoryPlugin.class,
						      "fileFilter.PAS"), "pas");
 
  // for description see MemoryPlugin
  @Override
  public String getSaveDescription() {
    return Application.getString(this, "memory.pascal.save.radio");
  }

  // for description see MemoryPlugin
  @Override
  public String getLoadDescription() {
    return Application.getString(this, "memory.pascal.load.radio");
  }

  // for description see MemoryPlugin
  @Override
  public FileNameExtensionFilter getFilter() {
    return pasFilter;
  }

  // for description see MemoryPlugin
  @Override
  public void read(final Hardware hardware, final File file) {
    log.fine("Reading Pascal program from a file: " + file.getName());
    final byte[] ram =
      Parameters.memoryDevice.getBlockByName("RAM").getMemory();
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      Pascal.encode(reader, ram);
    } catch (final IOException | PascalException exception) {
      log.fine("Error: " + exception.getMessage());
      throw Application.createError(this, "PASRead");
    }
    log.finer("Pascal program read");
  }

  // for description see MemoryPlugin
  @Override
  public void write(final Hardware hardware, final File file) {
    log.fine("Writing Pascal program to a file: " + file.getName());
    final byte[] ram =
      Parameters.memoryDevice.getBlockByName("RAM").getMemory();
    try (PrintWriter writer = new PrintWriter(file)) {
      Pascal.decode(ram, writer);
    } catch (final IOException | PascalException exception) {
      log.fine("Error: " + exception.getMessage());
      throw Application.createError(this, "PASWrite");
    }
    log.finer("Pascal program written");
  }

  // for description see MemoryPlugin
  @Override
  public String getSuccessfulSaveString() {
    return Application.getString(this, "memory.pascal.save.success");
  }

  // for description see MemoryPlugin
  @Override
  public String getSuccessfulLoadString() {
    return Application.getString(this, "memory.pascal.load.success");
  }
}
