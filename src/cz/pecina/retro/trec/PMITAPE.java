/* PMITAPE.java
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import cz.pecina.retro.common.Application;

/**
 * PMITAPE format reader/writer.
 * <p>
 * PMITAPE is a proprietary ASCII format developed by Martin Malý for
 * Tesla PMI-80.  The tape data is stored as a series of comma-separated
 * integers indicating time intervals between consecutive edges, measured
 * in CPU cycles.  It starts from the quiescent state of the tape recorder
 * interface.  The series is enclosed in square brackets.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class PMITAPE extends TapeProcessor {

  // static logger
  private static final Logger log =
    Logger.getLogger(PMITAPE.class.getName());

  // the tape recorder interface
  private TapeRecorderInterface tapeRecorderInterface;
  
  /**
   * Creates an instance of PMITAPE format reader/writer.
   *
   * @param tape                  the tape to operate on
   * @param tapeRecorderInterface the tape recorder interface object
   */
  public PMITAPE(final Tape tape,
		 final TapeRecorderInterface tapeRecorderInterface) {
    super(tape);
    this.tapeRecorderInterface = tapeRecorderInterface;
    log.fine("New PMITAPE created");
  }

  /**
   * Writes the tape to a file in PMITAPE format.
   *
   * @param file output file
   */
  public void write(final File file) {
    log.fine("Writing PMITAPE-formatted data to a file, file: " + file);
    long currPos = -1;
    try (final FileWriter writer = new FileWriter(file)) {
      for (long start: tape.navigableKeySet()) {
	final long duration = tape.get(start);
	if ((start > currPos) &&
	    (duration > 0) &&
	    ((start + duration) <= tapeRecorderInterface.getMaxTapeLength())) {
	  writer.write((currPos < 0) ? "[" : ",");
	  final long gap = start - ((currPos < 0) ? 0 : currPos);
	  writer.write(gap + "," + duration);
	  log.finest(String.format("Write: (%d, %d)", gap, duration));
	  currPos = start + duration;
	}	    
      }	
      writer.write("]");
    } catch (final IOException exception) {
      log.fine("Error, writing failed, exception: " + exception);
      throw Application.createError(this, "PMITAPEWrite");
    }
    log.fine("Writing completed");
  }

  /**
   * Reads the tape from a file in PMITAPE format.
   *
   * @param file input file
   */
  public void read(final File file) {
    log.fine("Reading PMITAPE-formatted data from a file, file: " + file);
    long currPos = 0;
    tape.clear();
    try (final Scanner scanner =
	 new Scanner(file).useDelimiter("\\s*[\\[\\],]\\s*")) {
      while (scanner.hasNextLong()) {
	final long start = scanner.nextLong();
	final long duration = scanner.nextLong();
	if ((duration <= 0) ||
	    ((currPos + start + duration) >
	     tapeRecorderInterface.getMaxTapeLength())) {
	  log.fine("Error, reading failed");
	  throw Application.createError(this, "PMITAPE");
	}
	tape.put(start + currPos, duration);
	log.finest(String.format("Read: (%d, %d)",
				 start + currPos,
				 duration));
	currPos += start + duration;
      }
    } catch (final IOException exception) {
      log.fine("Error, reading failed, exception: " + exception);
      throw Application.createError(this, "PMITAPERead");
    }
    log.fine("Reading completed");
  }
}
