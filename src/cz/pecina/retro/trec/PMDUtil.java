/* PMDUtil.java
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
import java.util.List;
import java.util.ArrayList;

import cz.pecina.retro.common.Application;

/**
 * Common constants and utilities for PMD 85 specific formats.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public final class PMDUtil {

  // static logger
  private static final Logger log =
    Logger.getLogger(PMDUtil.class.getName());

  /**
   * Slot duration in clock cycles.
   */
  public static final long SLOT = 0x6ab;

  /**
   * Calculates a plain one-byte checksum over a part of a list.
   *
   * @param  list the list to be processed
   * @return      the checksum
   */
  public static byte checkSum(final List<Byte> list,
			      final int start,
			      final int length) {
    log.fine("Calculating check sum for a sequence of length: " + length);
    byte s = 0;
    for (int i = 0; i < length; i++) {
      s += list.get(start + i) & 0xff;
    }
    return s;
  }

  /**
   * Splits a tape into bytes.
   *
   * @param  tape the tape to be processed
   * @return      a <code>TreeMap</code> tree of bytes and positions      
   */
  public static TreeMap<Long,Byte> splitTape(final Tape tape) {
    log.fine("Splitting tape into bytes");
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
   * Splits a list of bytes into PMD 85 blocks.
   *
   * @param  map a <code>TreeMap</code> of bytes to be processed
   * @param  ifc the tape recorder interface object
   * @return     a list of PMD 85 blocks
   */
  public static List<PMDBlock> splitBytes(final TreeMap<Long,Byte> map,
					  final TapeRecorderInterface ifc) {
    log.fine("Splitting list into blocks");

    // find gaps between bytes
    final long GAP = ifc.tapeSampleRate / 10;  // 100ms
    final List<List<Long>> lists = new ArrayList<>();
    long curr = Long.MIN_VALUE;
    List<Long> list = null;
    for (long start: map.keySet()) {
      if (start > (curr + GAP)) {
	if (list != null) {
	  lists.add(list);
	}
	list = new ArrayList<Long>();
      }
      list.add(start);
      curr = start;
    }
    if (list != null) {
      lists.add(list);
    }
    log.finer("List split up");

    // check for headers and add extra block if body is shorter than
    // indicated by the header
    for (int i = 0; i < lists.size() - 1; i++) {

      // only process eligible candidates
      if (lists.get(i).size() != 63) {
	continue;
      }

      // check for header
      boolean isHeader = false;
      PMDBlock block = null;
      final List<Byte> headerData = new ArrayList<>();
      for (long l: lists.get(i)) {
	headerData.add(map.get(l));
      }
      try {
	block = createBlock(headerData);
	isHeader = block instanceof PMDHeader;
      } catch (final TapeException exception) {
	isHeader = false;
      }

      // check the size of the following block
      if (isHeader) {
	final PMDHeader header = (PMDHeader)block;
	final List<Long> oldBody = lists.get(i + 1);
	if (oldBody.size() > (header.getBodyLength() + 1)) {
	  final List<Long> newBody =
	    oldBody.subList(0, header.getBodyLength() + 1);
	  final List<Long> newBlock =
	    oldBody.subList(header.getBodyLength() + 1, oldBody.size());
	  lists.remove(i + 1);
	  lists.add(i + 1, newBody);
	  lists.add(i + 2, newBlock);
	  log.finer("Additional block inserted at position " + (i + 1));
	}
      }
    }
    log.finer("All additional blocks inserted");

    // convert lists of bytes into blocks
    final List<PMDBlock> listBlocks = new ArrayList<>();
    for (List<Long> list2: lists) {
      final List<Byte> listBytes = new ArrayList<>();
      for (long l: list2) {
	listBytes.add(map.get(l));
      }
      try {
	listBlocks.add(createBlock(listBytes));
      } catch (final TapeException exception) {
      }  // skip block
    }
    log.finer("List of blocks created, size: " + listBlocks.size());
    return listBlocks;
  }    

  /**
   * Writes a pause to the tape.
   *
   * @param     tape          the tape
   * @param     start         the starting position
   * @param     ifc           the tape recorder interface object
   * @param     length        the pause duration in system clock cycles
   * @return                  the new position
   * @exception TapeException if tape full
   */
  public static long pause(final Tape tape,
			   final long start,
			   final TapeRecorderInterface ifc,
			   final long length
			   ) throws TapeException {
    log.fine("Writing pause of length: " + length);
    final long p = start + ((length * ifc.tapeSampleRate) / 1000); 
    if (p > ifc.getMaxTapeLength()) {
      throw new TapeException(Application.getString(PMDUtil.class,
						    "error.tapeFull"));
    }
    return p;
  }
  
  /**
   * Writes a long pause (2500ms) to the tape.
   *
   * @param     tape          the tape
   * @param     start         the starting position
   * @param     ifc           the tape recorder interface object
   * @return                  the new position
   * @exception TapeException if tape full
   */
  public static long longPause(final Tape tape,
			       final long start,
			       final TapeRecorderInterface ifc
			       ) throws TapeException {
    log.fine("Writing long pause");
    return pause(tape, start, ifc, 2500);
  }
  
  /**
   * Writes a short pause (500ms) to the tape.
   *
   * @param     tape          the tape
   * @param     start         the starting position
   * @param     ifc           the tape recorder interface object
   * @return                  the new position
   * @exception TapeException if tape full
   */
  public static long shortPause(final Tape tape,
				final long start,
				final TapeRecorderInterface ifc
				) throws TapeException {
    log.fine("Writing short pause");
    return pause(tape, start, ifc, 500);
  }
  
  /**
   * Writes a list of bytes to the tape.
   *
   * @param     tape          the tape
   * @param     start         the starting position
   * @param     ifc           the tape recorder interface object
   * @param     bytes         list of bytes to be written
   * @return                  the new position
   * @exception TapeException on error in data
   */
  public static long write(final Tape tape,
			   final long start,
			   final TapeRecorderInterface ifc,
			   final List<Byte> bytes
			   ) throws TapeException {
    log.fine("Writing " + bytes.size() + " byte(s) to tape");
    long pos = start, p = 0;
    for (byte b: bytes) {
      log.finest(String.format("Writing byte: 0x%02x", b));
      if ((pos + (11 * SLOT)) > ifc.getMaxTapeLength()) {
	throw new TapeException(Application.getString(PMDUtil.class,
						      "error.tapeFull"));
      }
      int s = ((b & 0xff) | 0x0300) << 1;
      int l = 1, ctr = 0;
      while (s != 0) {
	final int n = s & 1;
	if (n != l) {
	  if (n == 0) {
	    p = pos;
	  } else {
	    tape.put(p, ctr * SLOT);
	  }
	  ctr = 0;
	  l = n;
	}
	pos += SLOT;
	ctr++;
	s >>= 1;
      }
    }
    return pos;
  }

  /**
   * Factory method for creating a PMD 85 tape block of appropriate type.
   *
   * @param     list          list of input data
   * @exception TapeException on error in data
   */
  public static PMDBlock createBlock(final List<Byte> list
				     ) throws TapeException {
    log.fine("Creating PMD block, data size: " + list.size());
    try {
      return new PMDHeader(list);
    } catch (final TapeException exception1) {
      log.finer("Not a header");
      try {
	return new PMDValidBlock(list);
      } catch (final TapeException exception2) {
	log.finer("Not a valid block");
	return new PMDBlock(list);
      }
    }
  }
    
  // default constructor disabled
  private PMDUtil () {};
}
