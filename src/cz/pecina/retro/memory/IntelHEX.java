/* IntelHEX.java
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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;

import cz.pecina.retro.common.Application;

import cz.pecina.retro.cpu.Hardware;

/**
 * Intel HEX format reader/writer.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class IntelHEX extends MemoryProcessor {

  // static logger
  private static final Logger log =
    Logger.getLogger(IntelHEX.class.getName());

  // Intex HEX constants
  private static final int INTEL_HEX_MAX_BYTES_PER_LINE = 16;
  private static final int DATA_RECORD = 0x00;
  private static final int END_RECORD = 0x01;
  private static final int START_RECORD = 0x03;
  private static final int MAX_RECORD_TYPE = 0x05;

  /**
   * Creates an instance of IntelHEX format reader/writer.
   *
   * @param hardware              hardware set
   * @param sourceMemoryBank      source memory bank
   * @param destinationMemoryBank destination memory bank
   */
  public IntelHEX(final Hardware hardware,
		  final String sourceMemoryBank,
		  final String destinationMemoryBank) {
    super(hardware, sourceMemoryBank, destinationMemoryBank);
    log.fine("New IntelHEX created");
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
    log.finer("Writing Intel HEX data to a file, with redirection");
    write(file, startAddress, number, startAddress);
  }

  /**
   * Writes a memory range to a file, with wrap-around.
   *
   * @param file               output file
   * @param startAddress       starting address
   * @param number             number of bytes
   * @param destinationAddress destination address
   */
  public void write(final File file,
		    final int startAddress,
		    final int number,
		    final int destinationAddress) {
    log.fine(String.format(
      "Writing Intel HEX data to a file, file: %s, start address: %04x," +
      " number of bytes: %d, destination address: %04x",
      file.getName(),
      startAddress,
      number,
      destinationAddress));
    final int sourceSize = sourceMemory.length;
    try (final PrintWriter writer = new PrintWriter(file)) {
      for (int i = number, address = destinationAddress, count;
	   i > 0;
	   i -= count) {
	count = Math.min(INTEL_HEX_MAX_BYTES_PER_LINE, i);
	writer.printf(":%02X%04X%02X", count, address, DATA_RECORD);
	int checkSum = count + (address & 0xff) + (address >> 8);
	for (int j = 0; j < count; j++) {
	  final int dataByte = sourceMemory[address % sourceSize] & 0xff;
	  checkSum += dataByte;
	  writer.printf("%02X", dataByte);
	  address++;
	}
	writer.printf("%02X%n", (-checkSum) & 0xff);
	log.finest("One data record written, length: " + count);
      }
      writer.printf(":000000%02XFF%n", END_RECORD);
      log.finer("End record written");
    } catch (final Exception exception) {
      log.fine("Error, writing failed, exception: " +
	       exception.getMessage());
      throw Application.createError(this, "HEXWrite");
    }
    log.fine("Intel HEX data write completed");
  }

  /**
   * Reads Intel HEX data from a file and stores it in memory.
   *
   * @param  file input file
   * @return info info record
   */
  public Info read(final File file) {
    log.finer("Reading Intel HEX data from a file, with redirection");
    return read(file, -1);
  }

  /**
   * Reads Intel HEX data from a file and stores it in memory.
   *
   * @param  file               input file
   * @param  destinationAddress destination address ({@code -1} = none)
   * @return info               info record
   */
  public Info read(final File file, int destinationAddress) {
    log.fine(String.format(
      "Reading Intel HEX data from a file, file: %s" +
      ", destination address: %04x",
      file.getName(),
      destinationAddress));
    final int destinationSize = destinationMemory.length;
    final Info info = new Info();
    int number = 0;
    try (final BufferedReader reader =
	 new BufferedReader(new FileReader(file))) {
      int recordType = 0, offset = 0;
      do {
	final String line = reader.readLine();
	if (line.length() == 0) {
	  log.finer("Empty line skipped");
	  continue;
	}
	if (line.charAt(0) != ':') {
	  log.fine("Format error, bad leading character");
	  throw Application.createError(this, "HEX");
	}
	final int recordLength = Integer.parseInt(line.substring(1, 3), 16);
	log.finest("Record length: " + recordLength);
	if (((recordLength * 2) + 11) > line.length()) {
	  log.fine("Format error, record length/line length mismatch");
	  throw Application.createError(this, "HEX");
	}
	final int recordAddress = Integer.parseInt(line.substring(3, 7), 16);
	log.finest(String.format("Record address: %04x", recordAddress));
	recordType = Integer.parseInt(line.substring(7, 9), 16);
	log.finest(String.format("Record type: %02x", recordType));
	if (recordType > MAX_RECORD_TYPE) {
	  log.fine("Illegal record type");
	  throw Application.createError(this, "HEX");
	}
	if (recordType == START_RECORD) {
	  log.finer(String.format("Start record, address: %04x",
				  recordAddress));
	  info.startAddress = recordAddress;
	  continue;
	}
	if ((recordType != DATA_RECORD) && (recordType != END_RECORD)) {
	  log.fine("Unsupported record type");
	  throw Application.createError(this, "unsupportedRecord",
					recordType);
	}
	if ((destinationAddress != -1) && (recordType == DATA_RECORD)) {
	  offset = destinationAddress - recordAddress;
	  destinationAddress = -1;
	  log.finest(String.format("Offset: %04x", offset));
	}
	int checkSum =
	  recordLength + recordAddress + (recordAddress >> 8) + recordType;
	for (int i = 0;; i++) {
	  final int data =
	    Integer.parseInt(line.substring(9 + (i * 2), 11 + (i * 2)), 16);
	  checkSum += data;
	  if (i == recordLength) {
	    break;
	  }
	  final int address = (recordAddress + offset + i) & 0xffff;
	  if (address < info.minAddress) {
	    info.minAddress = address;
	  }
	  if (address > info.maxAddress) {
	    info.maxAddress = address;
	  }
	  destinationMemory[address % destinationSize] = (byte)data;
	  log.finest(String.format("Read: %02x -> (%04x)", data, address));
	  number++;
	}
	if ((checkSum & 0xff) != 0) {
	  log.fine("Bad check sum");
	  throw Application.createError(this, "HEX");
	}
      } while (recordType != END_RECORD);
    } catch (final NumberFormatException exception) {
      log.fine("Error, bad number format, exception: " +
	       exception.getMessage());
      throw Application.createError(this, "HEX");
    } catch (final Exception exception) {
      log.fine("Error, reading failed, exception: " +
	       exception.getMessage());
      throw Application.createError(this, "HEXRead");
    }
    info.number = number;
    if (info.number == 0) {
      log.fine("Reading completed, with info: number: 0");
    } else if (info.startAddress == -1) {
      log.fine(String.format(
        "Reading completed, with info: number: %d, min: %04x, max: %04x," +
	" start: -1",
	info.number,
	info.minAddress,
	info.maxAddress));
    } else {
      log.fine(String.format(
        "Reading completed, with info: number: %d, min: %04x, max: %04x," +
	" start: %04x",
	info.number,
	info.minAddress,
	info.maxAddress,
	info.startAddress));
    }
    return info;
  }
}
