/* SAM.java
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
import java.io.FileOutputStream;
import java.io.FileInputStream;

import cz.pecina.retro.common.Application;

/**
 * SAM format reader/writer.
 * <p>
 * SAM is a legacy binary format used in an older MS Windows-based
 * Tesla PMI-80 emulator. It stores samples of the tape recorder interface
 * line as bytes, {@code 0xff} meaning the quiescent state,
 * {@code 0x00} the active state.  The sampling rate was determined
 * experimentally to be 1/23 of the CPU frequency, i.e., one sample is
 * taken every 20.7us.  Due to a lack of any compression, SAM files are
 * rather bulky even for short recordings.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class SAM extends TapeProcessor {

  // static logger
  private static final Logger log =
    Logger.getLogger(SAM.class.getName());

  // SAM format constants
  private static final int SAM_DIVISOR = 23;
  private static final int SAM_INCREMENT = 111;  // 200us
  private static final int SAM_TRAILER_LENGTH = 100000;

  // the tape recorder interface
  private TapeRecorderInterface tapeRecorderInterface;

  /**
   * Creates an instance of SAM format reader/writer.
   *
   * @param tape                  the tape to operate on
   * @param tapeRecorderInterface the tape recorder interface object
   */
  public SAM(final Tape tape,
	     final TapeRecorderInterface tapeRecorderInterface) {
    super(tape);
    this.tapeRecorderInterface = tapeRecorderInterface;
    log.fine("New SAM created");
  }

  /**
   * Writes the tape to a file in SAM format.
   *
   * @param file output file
   */
  public void write(final File file) {
    log.fine("Writing SAM-formatted data to a file, file: " + file);
    long currPos = 0;
    try (final FileOutputStream oStream = new FileOutputStream(file)) {
      for (long start: tape.navigableKeySet()) {
	final long duration =
	  tape.get(start) - tapeRecorderInterface.holdOffPeriod;
	if ((start > currPos) && (duration > 0)) {
	  for (; currPos < start; currPos++) {
	    if ((currPos % SAM_DIVISOR) == 0) {
	      oStream.write(0xff);
	    }
	  }
	  for (; currPos < start + duration; currPos++) {
	    if ((currPos % SAM_DIVISOR) == 0) {
	      oStream.write(((((currPos - start) / SAM_INCREMENT) & 1) == 1) ?
			    0xff :
			    0x00);
	    }
	  }
	}
      }
      for (int i = 0; i < SAM_TRAILER_LENGTH; i++) {
	oStream.write(0xff);
      }
    } catch (final Exception exception) {
      log.fine("Error, writing failed, exception: " + exception.getMessage());
      throw Application.createError(this, "SAMWrite");
    }
    log.fine("Writing completed");
  }

  /**
   * Reads the tape from a file in SAM format.
   *
   * @param file input file
   */
  public void read(final File file) {
    log.fine("Reading SAM-formatted data from a file, file: " + file);
    final byte[] buffer = new byte[1];
    tape.clear();
    try (final FileInputStream iStream = new FileInputStream(file)) {
      long pulseStart = -1, pulseLast = -1;
      while (iStream.read(buffer, 0, 1) == 1) {
	int count = 0;
	if (buffer[0] == 0) {
	  final long currCyc = SAM_DIVISOR * count;
	  if (currCyc > pulseLast) {
	    if (pulseStart != -1) {
	      tape.put(pulseStart, pulseLast - pulseStart);
	    }
	    pulseStart = currCyc;
	  } 
	  pulseLast = currCyc + tapeRecorderInterface.holdOffPeriod;
	}
	count++;
      }
      if (pulseStart != -1) {
	tape.put(pulseStart, pulseLast - pulseStart);
      }
    } catch (final Exception exception) {
      log.fine("Error, reading failed, exception: " + exception.getMessage());
      throw Application.createError(this, "SAMRead");
    }
    log.fine("Reading completed");
  }
}
