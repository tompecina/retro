/* PMD.java
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

package cz.pecina.retro.trec;

import java.util.logging.Logger;

import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import cz.pecina.retro.common.Application;

/**
 * PMD format reader/writer.
 * <p>
 * PMD is a binary format used widely by older Tesla PMD 85 emulators. The
 * tape data is stored as a series of bytes, without any indication of the
 * character's position on the tape.  PMD is less versatile than the internal
 * format of PMD85, which can only be truly represented in XML and PMT formats.
 * Therefore, all PMD files can be imported into the PMD85 emulator, export is
 * limited to those tapes that have correct structure of bytes.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class PMD extends TapeProcessor {

  // static logger
  private static final Logger log =
    Logger.getLogger(PMD.class.getName());

  // the tape recorder interface
  private TapeRecorderInterface tapeRecorderInterface;
  
  /**
   * Creates an instance of PMD format reader/writer.
   *
   * @param tape                  the tape to operate on
   * @param tapeRecorderInterface the tape recorder interface object
   */
  public PMD(final Tape tape,
	     final TapeRecorderInterface tapeRecorderInterface) {
    super(tape);
    this.tapeRecorderInterface = tapeRecorderInterface;
    log.fine("New PMD created");
  }

  /**
   * Writes the tape to a file in PMD format.
   *
   * @param file output file
   */
  public void write(final File file) {
    log.fine("Writing PMD-formatted data to a file, file: " + file);
    assert file != null;
    
    // convert tape to byte map
    final TreeMap<Long,Byte> map = PMDUtil.splitTape(tape);
    if (map == null) {
      log.fine("Error, writing failed");
      throw Application.createError(this, "PMDWrite.incompatible");
    }
    log.finer("Byte map created");

    // write the bytes
    try (OutputStream out = new FileOutputStream(file)) {
      for (long pos: map.keySet()) {
	out.write(map.get(pos));
      }
    } catch (final IOException exception) {
      log.fine("Error, writing failed, exception: " + exception);
      throw Application.createError(this, "PMDWrite");
    }

    log.fine("Writing completed");
  }

  /**
   * Reads the tape from a file in PMD format.
   *
   * @param file input file
   */
  public void read(final File file) {
    log.fine("Reading PMD-formatted data from a file, file: " + file);

    // read data into a list
    final List<Byte> list = new ArrayList<>();
    try (InputStream in = new FileInputStream(file)) {
      int b;
      while ((b = in.read()) >= 0) {
	list.add((byte)b);
      }
    } catch (final IOException exception) {
      log.fine("Error, reading failed, exception: " + exception);
      throw Application.createError(this, "PTPRead");
    }

    // clear tape
    tape.clear();

    // for all headers and blocks
    long currPosition = 0;
    int pointer = 0;
    log.finer("List size: " + list.size());
    while (pointer < list.size()) {

      // check if the next block is a header
      boolean isHeader = false;
      PMDBlock block = null;
      try {
	block = PMDUtil.createBlock(list.subList(pointer, pointer + 63));
	isHeader = block instanceof PMDHeader;
      } catch (final TapeException exception) {
	isHeader = false;
      }

      // if not a header, copy the rest of the file as one block
      if (!isHeader) {
	log.finer("Not a header, breaking");
	break;
      }
      
      // write header
      final PMDHeader header = (PMDHeader)block;
      try {
	log.finer("Writing header for block " + header.getFileNumber() +
		  ", name: '" + header.getFileName() + "'");
	currPosition = PMDUtil.longPause(tape,
					 currPosition,
					 tapeRecorderInterface);
	currPosition = PMDUtil.write(tape,
				     currPosition,
				     tapeRecorderInterface,
				     header.getBytes());
	pointer += 63;
	log.finer("New pointer: " + pointer);
      } catch (final TapeException exception) {
	log.fine("Failed, exception: " + exception.getMessage());
	throw new RuntimeException(exception.getMessage());
      }
      log.finer("Header written");

      // check if the next block conforms to the header
      boolean isValid = false;
      final int blockLength = header.getBodyLength() + 1;
      try {
	block = PMDUtil.createBlock(list.subList(pointer, pointer + blockLength));
	isValid = block instanceof PMDValidBlock;
      } catch (final TapeException exception) {
	isValid = false;
      }
      if (block.getLength() != blockLength) {
	isValid = false;
      }

      // if not a header, copy the rest of the file as one block
      if (!isValid) {
	log.finer("Not a valid block, breaking");
	break;
      }

      // write block
      try {
	log.finer("Writing block " + header.getFileNumber() +
		  ", name: '" + header.getFileName() + "'");
	currPosition = PMDUtil.shortPause(tape,
					  currPosition,
					  tapeRecorderInterface);
	currPosition = PMDUtil.write(tape,
				     currPosition,
				     tapeRecorderInterface,
				     list.subList(pointer, pointer + blockLength));
	pointer += blockLength;
	log.finer("New pointer: " + pointer);
      } catch (final TapeException exception) {
	log.fine("Failed, exception: " + exception.getMessage());
	throw new RuntimeException(exception.getMessage());
      }
      log.finer("Block written");
    }

    // if any bytes remain, write them as a custom block
    if (pointer < list.size()) {
      try {
	log.finer("Writing custom block");
	currPosition = PMDUtil.shortPause(tape,
					  currPosition,
					  tapeRecorderInterface);
	currPosition = PMDUtil.write(tape,
				     currPosition,
				     tapeRecorderInterface,
				     list.subList(pointer, list.size()));
	log.finer("Custom block written");
      } catch (final TapeException exception) {
	log.fine("Failed, exception: " + exception.getMessage());
	throw new RuntimeException(exception.getMessage());
      }
    }
    log.finer("Reading completed");
  }
}
