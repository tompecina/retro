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
import java.util.Iterator;

/**
 * Common constants and utilities for PMD 85 specific formats.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class PMD {

  // static logger
  private static final Logger log =
    Logger.getLogger(PMD.class.getName());

  /**
   * Splits a tape into bytes.
   *
   * @param  tape the tape to be processed
   * @return a <code>TreeMap</code> tree of bytes and positions      
   */
  public static TreeMap<Long,Byte> splitTape(final Tape tape) {
    log.fine("Splitting tape into bytes");
    final long SLOT = 0x6ab;
    final TreeMap<Long,Byte> map = new TreeMap<>();
    final Iterator<Long> keys = tape.keySet().iterator();
    int state = 0, bit = 0, buffer = 0;
    boolean inPulse = false;
    long start = 0, pos = 0, remains = 0;
    if (!keys.hasNext()) {
      return map;  // empty tape
    }
    long nextPulse = keys.next();
    for (;;) {
      switch (state) {
	case 0:  // start-bit
	  start = nextPulse;
	  remains = tape.get(nextPulse) - (SLOT / 2);
	  if (remains < 0) {
	    log.fine("Start-bit too short, failed");
	    return null;  // start-bit too short
	  }
	  log.finer("Start-bit detected at: " + start);
	  inPulse = true;
	  pos = nextPulse + SLOT + (SLOT / 2);
	  remains -= SLOT;
	  bit = buffer = 0;
	  state++;
	  break;
	case 1:  // data bits
	  buffer |= (inPulse ? 0 : 1) << bit;
	  log.finest(String.format("Data-bit, bit: %d, buffer: %02x", bit, buffer));
	  pos += SLOT;
	  remains -= SLOT;
	  if (++bit == 8) {
	    log.finer(String.format("Writing byte, start: %d, value: %02x", start, buffer));
	    map.put(start, (byte)buffer);
	    state++;
	  }
	  break;
	case 2:  // stop-bit 1
	  log.finest("Testing stop-bit 1");
	  if (inPulse) {
	    log.fine("Framing error, failed");
	    return null;  // framing error
	  }
	  pos += SLOT;
	  remains -= SLOT;
	  state++;
	  break;
	case 3:  // stop-bit 2
	  log.finest("Testing stop-bit 2");
	  if (inPulse) {
	    log.fine("Framing error, failed");
	    return null;  // framing error
	  }
	  if (remains > (Long.MAX_VALUE / 2)) {
	    log.fine("Finished, success");
	    return map;  // done
	  }
	  state = 0;
	  break;
      }
      log.finest(String.format("Next slot, inPulse: %s, pos: %d, remains: %d",
			       inPulse, pos, remains));
      if (remains <= 0) {
	if (inPulse) {
	  if (keys.hasNext()) {
	    nextPulse = keys.next();
	    remains = nextPulse - pos;
	    log.finest(String.format("Next pulse read, starting at: %d, remains: %d",
				     nextPulse, remains));
	    if (remains <= 0) {
	      log.fine("Pulses too close together, failed");
	      return null;  // pulses too close together
	    }
	  } else {
	    remains = Long.MAX_VALUE;
	  }
	} else {
	  remains = tape.get(nextPulse) - pos + nextPulse;
	  if (remains <= 0) {
	    log.fine("Pulse too short, failed");
	    return null;  // pulse to short
	  }
	}
	inPulse = !inPulse;
      }
    }
  }

  /**
   * Writes a pause to the tape.
   *
   * @param  tape       the tape
   * @param  start      the starting position
   * @param  tapeLength the tape length in system clock cycles
   * @param  length     the pause duration in system clock cycles
   * @return            the new position
   */
  public static long longPause(final Tape tape,
			       final long start,
			       final long tapeLength,
			       final long length
			       ) throws TapeException {
    log.fine("Writing long pause");
    final long p = start 
    if (p > tapeLength) {
      throw new TapeException(Application.getString(this, "error.tapeFull"));
  }
  
  /**
   * Writes a long pause (2500ms) to the tape.
   *
   * @param  tape       the tape
   * @param  start      the starting position
   * @param  tapeLength the tape length in system clock cycles
   * @return            the new position
   */
  public static long longPause(final Tape tape,
			       final long start,
			       final long tapeLength
			       ) throws TapeException {
    log.fine("Writing long pause");
    pause(tape, start, tapeLength, 2500);
  }
  
  /**
   * Writes a short pause (500ms) to the tape.
   *
   * @param  tape       the tape
   * @param  start      the starting position
   * @param  tapeLength the tape length in system clock cycles
   * @return            the new position
   */
  public static long shortPause(final Tape tape,
				final long start,
				final long tapeLength
				) throws TapeException {
    log.fine("Writing short pause");
    pause(tape, start, 500);
  }
  
  /**
   * Splits a tape into bytes.
   *
   * @param  tape       the tape
   * @param  start      the starting position
   * @param  tapeLength the tape length in system clock cycles
   * @param  bytes      the array of bytes to be written
   * @return            the new position
   */
  public static long write(final Tape tape,
			   final long start,
			   final long tapeLength,
			   final byte[] bytes
			   ) throws TapeException {
    log.fine("Writing " + bytes.length + "to tape");
  }
  
}
