/* PMT.java
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.EOFException;
import cz.pecina.retro.common.Parameters;
import cz.pecina.retro.common.Application;

/**
 * PMT format reader/writer.
 * <p>
 * PMT is a binary format developed by @AUTHOR@ for this application.
 * After an 8-byte preamble, it stores pulse positions and durations
 * as pairs of <code>Long</code>s, using a compression format described
 * in {@link PMTInputStream} and {@link PMTOutputStream}.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class PMT extends TapeProcessor {

  // static logger
  private static final Logger log =
    Logger.getLogger(PMT.class.getName());

  // PMT format signature
  private static final int PMT_SIGNATURE = 0x504d5401;  // includes Subtype 1

  /**
   * Creates an instance of PMT format reader/writer.
   *
   * @param tape the tape to operate on
   */
  public PMT(final Tape tape) {
    super(tape);
    log.fine("New PMT created");
  }

  /**
   * Writes the tape to a file in PMT format.
   *
   * @param file output file
   */
  public void write(final File file) {
    log.fine("Writing PMT-formatted data to a file, file: " + file);
    long currPos = -1;
    try (final PMTOutputStream dStream =
	 new PMTOutputStream(new FileOutputStream(file))) {
      dStream.writeInt(PMT_SIGNATURE);
      dStream.writeInt(Parameters.tapeSampleRate);
      for (long start: tape.navigableKeySet()) {
	final long duration = tape.get(start);
	if ((start > currPos) && (duration > 0) &&
	    ((start + duration) <= TapeRecorder.maxTapeLength)) {
	  dStream.writeLongCompressed(start);
	  dStream.writeLongCompressed(duration);
	  currPos = start + duration;
	}
	log.finest(String.format("Write: (%d, %d)", start, duration));
      }
    } catch (Exception exception) {
      log.fine("Error, writing failed, exception: " + exception);
      throw Application.createError(this, "PMTWrite");
    }
    log.fine("Writing completed");
  }

  /**
   * Reads the tape from a file in PMT format.
   *
   * @param file input file
   */
  public void read(final File file) {
    log.fine("Reading PMT-formatted data from a file, file: " + file);
    long currPos = -1;
    tape.clear();
    try (final PMTInputStream dStream =
	 new PMTInputStream(new FileInputStream(file))) {
      if (dStream.readInt() != PMT_SIGNATURE) {
	Application.createError(this, "PMT");
      }
      if (dStream.readInt() != Parameters.tapeSampleRate) {
	Application.createError(this, "PMTSampleRate");
      }
      try {
	while (true) {
	  final long start = dStream.readLongCompressed(); 
	  final long duration = dStream.readLongCompressed();
	  if ((start <= currPos) ||
	      (duration <= 0) ||
	      ((start + duration) > TapeRecorder.maxTapeLength)) {
	    throw Application.createError(this, "PMT");
	  }
	  tape.put(start, duration);
	  currPos = start;
	  log.finest(String.format("Read: (%d, %d)", start, duration));
	}
      } catch (EOFException exception) {
	log.finer("EOF encountered");
      }
    } catch (Exception exception) {
      log.fine("Error, reading failed, exception: " + exception);
      throw Application.createError(this, "PMTRead");
    }
    log.fine("Reading completed");
  }
}
