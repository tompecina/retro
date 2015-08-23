/* BasicMemoryPlugin.java
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
 * Memory plugin for saving/loading BASIC-G programs.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class BasicMemoryPlugin implements MemoryPlugin {

  // static logger
  private static final Logger log =
    Logger.getLogger(BasicMemoryPlugin.class.getName());

  // fixed start/end address
  private static final int START_ADDRESS = 0x2401;
  private static final int END_ADDRESS = 0xbfff;
  
  // BAS extension filter
  private static FileNameExtensionFilter basFilter =
    new FileNameExtensionFilter(Application.getString(BasicMemoryPlugin.class,
						      "fileFilter.BAS"), "bas");
  
  // for description see MemoryPlugin
  @Override
  public String getSaveDescription() {
    return Application.getString(this, "memory.basic.save.radio");
  }

  // for description see MemoryPlugin
  @Override
  public String getLoadDescription() {
    return Application.getString(this, "memory.basic.load.radio");
  }

  // for description see MemoryPlugin
  @Override
  public FileNameExtensionFilter getFilter() {
    return basFilter;
  }

  // for description see MemoryPlugin
  @Override
  public void read(final Hardware hardware, final File file) {
    log.fine("Reading BASIC-G program to a file: " + file.getName());
    final byte[] ram =
      Parameters.memoryDevice.getBlockByName("RAM").getMemory();
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      Basic.encode(reader, ram, START_ADDRESS, END_ADDRESS);
    } catch (final IOException | BasicException exception) {
      log.fine("Error: " + exception.getMessage());
      throw Application.createError(this, "BASRead");
    }
    log.finer("BASIC-G program read");
  }

  // for description see MemoryPlugin
  @Override
  public void write(final Hardware hardware, final File file) {
    log.fine("Writing BASIC-G program to a file: " + file.getName());
    final byte[] ram =
      Parameters.memoryDevice.getBlockByName("RAM").getMemory();
    if ((ram[START_ADDRESS] | ram[START_ADDRESS + 1]) == (byte)0) {
      throw Application.createError(this, "BAS.noProgram");
    }
    try (PrintWriter writer = new PrintWriter(file)) {
      Basic.decode(ram, writer, START_ADDRESS, END_ADDRESS);
    } catch (final IOException | BasicException exception) {
      log.fine("Error: " + exception.getMessage());
      throw Application.createError(this, "BASWrite");
    }
    log.finer("BASIC-G program written");
  }

  // for description see MemoryPlugin
  @Override
  public String getSuccessfulSaveString() {
    return Application.getString(this, "memory.basic.save.success");
  }

  // for description see MemoryPlugin
  @Override
  public String getSuccessfulLoadString() {
    return Application.getString(this, "memory.basic.load.success");
  }
}
