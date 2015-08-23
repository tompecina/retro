/* Raw.java
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

import java.util.logging.Logger;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;

import cz.pecina.retro.cpu.Hardware;

import cz.pecina.retro.common.Application;

/**
 * Raw format reader/writer.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Raw extends MemoryProcessor {

  // static logger
  private static final Logger log =
    Logger.getLogger(Raw.class.getName());

  /**
   * Creates an instance of raw format reader/writer.
   *
   * @param hardware              hardware set
   * @param sourceMemoryBank      source memory bank
   * @param destinationMemoryBank destination memory bank
   */
  public Raw(final Hardware hardware,
	     final String sourceMemoryBank,
	     final String destinationMemoryBank) {
    super(hardware, sourceMemoryBank, destinationMemoryBank);
    log.fine("New Raw created");
  }

  /**
   * Writes a memory range to a file, with wrap-around.
   *
   * @param file         output file
   * @param startAddress starting address
   * @param number       number of bytes
   */
  public void write(final File file,
		    final int startAddress,
		    final int number) {
    log.fine(String.format(
      "Writing raw data to a file, file: %s, start address: %04x," +
      " number of bytes: %d",
      file.getName(),
      startAddress,
      number));
    try (final OutputStream outputStream = new FileOutputStream(file)) {
      for (int i = 0; i < number; i++) {
	final int address = (startAddress + i) & 0xffff;
	final int data = sourceMemory[address] & 0xff;
	outputStream.write(data);
	log.finest(String.format("Write: %04x -> %02x", address, data));
      }
    } catch (Exception exception) {
      log.fine("Error, writing failed, exception: " + exception);
      throw Application.createError(this, "rawWrite");
    }
    log.fine("Raw data write completed");
  }

  /**
   * Reads a memory range from a file, with wrap-around.
   *
   * @param  file               input file
   * @param  destinationAddress destination address
   * @return info info record
   */
  public Info read(final File file, int destinationAddress) {
    log.fine(String.format(
      "Reading raw data from a file, file: %s, destination address: %04x",
      file.getName(),
      destinationAddress));
    final Info info = new Info();
    try (final InputStream inputStream = new FileInputStream(file)) {
      int data;
      while ((data = inputStream.read()) != -1) {
	destinationAddress &= 0xffff;
	if (destinationAddress < info.minAddress) {
	  info.minAddress = destinationAddress;
	}
	if (destinationAddress > info.maxAddress) {
	  info.maxAddress = destinationAddress;
	}
	destinationMemory[destinationAddress] = (byte)data;
	log.finest(String.format("Read: %02x -> (%04x)",
				 data,
				 destinationAddress));
	destinationAddress++;
	info.number++;
      }
    } catch (Exception exception) {
      log.fine("Error, reading failed, exception: " + exception);
      throw Application.createError(this, "rawRead");
    }
    if (info.number == 0) {
      log.fine("Reading completed, with info: number: 0");
    } else {
      log.fine(String.format(
        "Reading completed, with info: number: %d, min: %04x, max: %04x",
	info.number,
	info.minAddress,
	info.maxAddress));
    }
    return info;
  }
}
