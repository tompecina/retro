/* PMDTAPE.java
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
import java.io.FileWriter;
import cz.pecina.retro.common.Application;

/**
 * PMDTAPE format reader/writer.
 * <p>
 * PMDTAPE is a proprietary ASCII format developed by Martin Malý for
 * Tesla PMD 85.  The tape data is stored as a series of comma-separated
 * integers indicating the characters, without any indicaiton of the
 * characters' position on the tape.  The series is enclosed in
 *  square brackets.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class PMDTAPE extends TapeProcessor {

  // static logger
  private static final Logger log =
    Logger.getLogger(PMDTAPE.class.getName());

  // the tape recorder interface
  private TapeRecorderInterface tapeRecorderInterface;
  
  /**
   * Creates an instance of PMDTAPE format reader/writer.
   *
   * @param tape                  the tape to operate on
   * @param tapeRecorderInterface the tape recorder interface object
   */
  public PMDTAPE(final Tape tape,
		 final TapeRecorderInterface tapeRecorderInterface) {
    super(tape);
    this.tapeRecorderInterface = tapeRecorderInterface;
    log.fine("New PMDTAPE created");
  }

  /**
   * Writes the tape to a file in PMDTAPE format.
   *
   * @param file output file
   */
  public void write(final File file) {
    log.fine("Writing PMDTAPE-formatted data to a file, file: " + file);
    assert file != null;
    
    final TreeMap<Long,Byte> map = PMD.splitTape(tape);
    if (map == null) {
      log.fine("Error, writing failed");
      throw Application.createError(this, "PMDTAPEWrite.incompatible");
    }
    try (final FileWriter writer = new FileWriter(file)) {
      boolean first = true;
      for (long pos: map.keySet()) {
	writer.write(first ? "[" : ",");
	writer.write(String.format("%d", (map.get(pos) & 0xff)));
	first = false;
      }
      writer.write("]");
    } catch (Exception exception) {
      log.fine("Error, writing failed, exception: " + exception);
      throw Application.createError(this, "PMITAPEWrite");
    }

    log.fine("Writing completed");
  }

  /**
   * Reads the tape from a file in PMDTAPE format.
   *
   * @param file input file
   */
  public void read(final File file) {
    log.fine("Reading PMDTAPE-formatted data from a file, file: " + file);
    final List<Byte> list = new ArrayList<>();
    try (final Scanner scanner =
	 new Scanner(file).useDelimiter("\\s*[\\[\\],]\\s*")) {
      while (scanner.hasNextInt()) {
	final int b = scanner.nextInt();
	log.finest("Reading byte: " + b);
	list.add((byte)b);
      }
    } catch (Exception exception) {
      log.fine("Error, reading failed, exception: " + exception);
      throw Application.createError(this, "PMDTAPERead");
    }

    try {
      final PMDHeader header = new PMDHeader(list, 0, list.size());
    } catch (final TapeException exception) {
      log.fine("Exception: " + exception.getMessage());
      throw new RuntimeException(exception.getMessage());
    }

    System.exit(0);
    
    tape.clear();
    
    
    log.finer("Reading completed");
  }
}
