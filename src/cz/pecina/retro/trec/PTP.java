/* PTP.java
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
import java.util.Scanner;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import cz.pecina.retro.common.Application;

/**
 * PTP format reader/writer.
 * <p>
 * PTP is a proprietary binary format developed by Roman and Martin Bórik
 * for their Tesla PMD 85 emulator.  The format is described on the website
 * of their project at {@link http://pmd85.borik.net/wiki/PTP}.  PTP is less
 * versatile than the internal format of PMD85, which can only be truly 
 * represented in XML and PMT formats.  Therefore, all PTP files can be imported
 * into the PMD85 emulator, export is limited to those tapes that have blocks
 * separated by gaps of at least 100ms.  No check is made on the correctness
 * of the blocks so even tapes containing non-standard and erroneous or
 * inconsistent blocks can be saved in PTP.  Export only fails if the byte
 * structure is broken and the tape cannot be converted into bytes.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class PTP extends TapeProcessor {

  // static logger
  private static final Logger log =
    Logger.getLogger(PTP.class.getName());

  // the tape recorder interface
  private TapeRecorderInterface tapeRecorderInterface;
  
  /**
   * Creates an instance of PTP format reader/writer.
   *
   * @param tape                  the tape to operate on
   * @param tapeRecorderInterface the tape recorder interface object
   */
  public PTP(final Tape tape,
		 final TapeRecorderInterface tapeRecorderInterface) {
    super(tape);
    this.tapeRecorderInterface = tapeRecorderInterface;
    log.fine("New PTP created");
  }

  /**
   * Writes the tape to a file in PTP format.
   *
   * @param file output file
   */
  public void write(final File file) {
    log.fine("Writing PTP-formatted data to a file, file: " + file);
    assert file != null;

    // convert tape into byte map
    log.fine("Creating byte map");
    final TreeMap<Long,Byte> map = PMDUtil.splitTape(tape);
    if (map == null) {
      log.fine("Error, writing failed");
      throw Application.createError(this, "PTPWrite.incompatible");
    }
    log.finer("Byte map created");
    
    // split bytes into blocks
    final List<PMDBlock> list = PMDUtil.splitBytes(map, tapeRecorderInterface);
    
    // write blocks
    try (OutputStream out = new FileOutputStream(file)) {
      for (PMDBlock block: list) {
	final int blockLength = block.getLength();
	out.write(blockLength & 0xff);
	out.write(blockLength >> 8);
	for (byte b: block.getBytes()) {
	  out.write(b);
	}
      }
    } catch (final IOException exception) {
      log.fine("Error, writing failed, exception: " + exception);
      throw Application.createError(this, "PTPWrite");
    }
    
    log.fine("Writing completed");
  }

  /**
   * Reads the tape from a file in PTP format.
   *
   * @param file input file
   */
  public void read(final File file) {
    log.fine("Reading PTP-formatted data from a file, file: " + file);

    // read data into a byte array
    final byte[] bytes;
    try {
      bytes = Files.readAllBytes(file.toPath());
    } catch (final IOException exception) {
      log.fine("Error, reading failed, exception: " + exception);
      throw Application.createError(this, "PTPRead");
    }

    // clear tape
    tape.clear();

    // process data block by block
    int pointer = 0;
    long currPosition = 0;
    while (pointer < bytes.length) {

      // get next block length
      if ((pointer + 2) >= bytes.length) {
	log.fine("Invalid PTP file, ends prematurely");
	throw Application.createError(this, "PTP");
      }
      int blockLength =
	((bytes[pointer + 1] & 0xff) << 8) + (bytes[pointer] & 0xff);
      log.finer("Block length: " + blockLength);
      pointer += 2;
      if ((pointer + blockLength) > bytes.length) {
	log.fine("Invalid PTP file, ends prematurely");
	throw Application.createError(this, "PTP");
      }

      // get block data
      final List<Byte> list = new ArrayList<>();
      for (int i = 0; i < blockLength; i++) {
	list.add(bytes[pointer++]);
      }

      // convert into block
      PMDBlock block = null;
      try {
	block = PMDUtil.createBlock(list);
      } catch (final TapeException exception) {
	log.fine("Invalid PTP file, block cannot be created");
	throw Application.createError(this, "PTP");
      }
      
      // check for header
      if (block instanceof PMDHeader) {
	final PMDHeader header = (PMDHeader)block;

	// write header
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
	  log.finer("New pointer: " + pointer);
	} catch (final TapeException exception) {
	  log.fine("Failed, exception: " + exception.getMessage());
	  throw new RuntimeException(exception.getMessage());
	}
	log.finer("Header written");
      
	// get body block size
	if ((pointer + 2) >= bytes.length) {
	  log.fine("Invalid PTP file, ends prematurely");
	  throw Application.createError(this, "PTP");
	}
	blockLength =
	  ((bytes[pointer + 1] & 0xff) << 8) + (bytes[pointer] & 0xff);
	log.finer("Block length: " + blockLength);
	pointer += 2;

	// check against the header
	if (blockLength != (header.getBodyLength() + 1)) {
	  log.fine("Mismatch in block lengths");
	  throw Application.createError(this, "PTP");
	}

	// get body data
	if ((pointer + blockLength) > bytes.length) {
	  log.fine("Invalid PTP file, ends prematurely");
	  throw Application.createError(this, "PTP");
	}
	list.clear();
	for (int i = 0; i < blockLength; i++) {
	  list.add(bytes[pointer++]);
	}
	log.fine("Block read");

	// test the checksum
	if (list.get(blockLength - 1) !=
	    PMDUtil.checkSum(list, 0, blockLength - 1)) {
	  log.fine("Bad checksum");
	  throw Application.createError(this, "PTP");
	}
	log.finer("Checksum ok");

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
				       list);
	  log.finer("New pointer: " + pointer);
	} catch (final TapeException exception) {
	  log.fine("Failed, exception: " + exception.getMessage());
	  throw new RuntimeException(exception.getMessage());
	}
	log.finer("Block written");
	
      } else {

	// write custom type block
	try {
	  log.finer("Writing custom type block");
	  currPosition = PMDUtil.longPause(tape,
					   currPosition,
					   tapeRecorderInterface);
	  currPosition = PMDUtil.write(tape,
				       currPosition,
				       tapeRecorderInterface,
				       list);
	  log.finer("New pointer: " + pointer);
	} catch (final TapeException exception) {
	  log.fine("Failed, exception: " + exception.getMessage());
	  throw new RuntimeException(exception.getMessage());
	}
	log.finer("Custom type block written");
      }
    }
    log.finer("Reading completed");
  }
}
