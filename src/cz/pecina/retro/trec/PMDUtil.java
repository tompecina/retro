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

  // halves of slot
  private static final long FIRST_HALFSLOT = SLOT / 2;
  private static final long SECOND_HALFSLOT = SLOT - (SLOT / 2);
  
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


  // process pulse
  //  0 = short, 1 = long, 2 = too long
  private static byte procPulse(final long d) {
    if (d < 1280) {
      return (byte)0;
    } else if (d < 2133) {
      return (byte)1;
    } else {
      return (byte)2;
    }
  }
      
  /**
   * Splits a tape into bytes.
   *
   * @param  tape the tape to be processed
   * @return      a {@code TreeMap} of bytes and positions      
   */
  public static TreeMap<Long,Byte> splitTape(final Tape tape) {
    log.fine("Splitting tape into bytes");
    final TreeMap<Long,Byte> map = new TreeMap<>();
    if (tape.isEmpty()) {
      log.fine("Empty tape");
      return map;
    }

    // convert tape into a map of short/long pulses
    final TreeMap<Long,Byte> pulseMap = new TreeMap<>();
    long lastPulse = -1;
    for (long start: tape.keySet()) {
      if (lastPulse != -1) {
	pulseMap.put(lastPulse, procPulse(start - lastPulse));
      }
      final long duration = tape.get(start);
      pulseMap.put(start, procPulse(duration));
      lastPulse = start + duration;
    }
    log.finer("Pulse map populated");


    // convert pulses into levels
    final TreeMap<Long,Boolean> levelMap = new TreeMap<>();
    int counter = 0;
    boolean level = true, lastShort = false;
    for (long start: pulseMap.keySet()) {
      switch (pulseMap.get(start)) {
	case 0:
	  log.finest("Short pulse at: " + start);
	  if (lastShort) {
	    log.finest("Putting level: " + level + " at: " + start);
	    levelMap.put(start, level);
	    lastShort = false;
	  } else {
	    lastShort = true;
	  }
	  break;
	case 1:
	  log.finest("Long pulse at: " + start);
	  level = !level;
	  log.finest("Putting level: " + level + " at: " + start);
	  levelMap.put(start, level);
	  lastShort = false;
	  break;
	case 2:
	  log.finer("Excessively long pulse, resetting at: " + start);
	  level = true;
	  lastShort = false;
	  break;
      }
      if (level) {
	counter = 0;
      } else {
	if (++counter == 50) {
	  level = !level;
	}
      }
    }
    log.finer("Level map populated");
      
    // process the level map
    final Iterator<Long> keys = levelMap.keySet().iterator();
    int state = 0, bit = 0, buffer = 0;
    for (;;) {
      if (!keys.hasNext()) {
	return map;
      }
      final long start = keys.next();
      level = levelMap.get(start);
      switch (state) {
	case 0:  // searching for start-bit
	  if (!level) {
	    log.finer("Start-bit detected at: " + start);
	    bit = buffer = 0;
	    state++;
	  }
	  break;
	case 1:  // data
	  buffer |= (level ? 1 : 0) << bit;
	  log.finest(String.format(
	    "Data-bit, bit: %d, buffer: %02x", bit, buffer));
	  if (++bit == 8) {
	    state++;
	  }
	  break;
	case 2:  // stop bit 1
	  log.finest("Testing stop-bit 1");
	  if (!level) {
	    log.finest("Framing error at stop-bit 1");
	    state += 2;
	  }
	  state++;
	  break;
	case 3:  // stop bit 2
	  log.finest("Testing stop-bit 2");
	  if (!level) {
	    log.finest("Framing error at stop-bit 2");
	    state++;
	  }
	  log.finer(String.format(
	    "Writing byte, start: %d, value: %02x", start, buffer));
	  map.put(start, (byte)buffer);
	  state = 0;
	  break;
	case 4:  // recovering from framing error
	  log.finest("Recovering from framing error");
	  if (level) {
	    state = 0;
	  }
	  break;
      }
    }
  }

  /**
   * Splits a list of bytes into PMD 85 blocks.
   *
   * @param  map a {@code TreeMap} of bytes to be processed
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
   * <p>
   * The duration of the pause is rounded up to an integer number of slots.
   *
   * @param  tape          the tape
   * @param  start         the starting position
   * @param  length        the pause duration in system clock cycles
   * @return               the new position
   * @throws TapeException if tape full
   */
  public static long pause(final Tape tape,
			   long start,
			   final long length
			   ) throws TapeException {
    log.fine("Writing pause of length: " + length);
    for (long i = (length + SLOT - 1) / SLOT; i >= 0; i--) {
      tape.put(start, FIRST_HALFSLOT);
      start += SLOT;
    }
    return start;
  }
  
  /**
   * Writes a long pause (2500ms) to the tape.
   * <p>
   * The duration of the pause is rounded up to an integer number of slots.
   *
   * @param  tape          the tape
   * @param  start         the starting position
   * @param  ifc           the tape recorder interface object
   * @return               the new position
   * @throws TapeException if tape full
   */
  public static long longPause(final Tape tape,
			       final long start,
			       final TapeRecorderInterface ifc
			       ) throws TapeException {
    log.fine("Writing long pause");
    return pause(tape, start, (2500L * ifc.tapeSampleRate) / 1000L);
  }
  
  /**
   * Writes a short pause (500ms) to the tape.
   * <p>
   * The duration of the pause is rounded up to an integer number of slots.
   *
   * @param  tape          the tape
   * @param  start         the starting position
   * @param  ifc           the tape recorder interface object
   * @return               the new position
   * @throws TapeException if tape full
   */
  public static long shortPause(final Tape tape,
				final long start,
				final TapeRecorderInterface ifc
				) throws TapeException {
    log.fine("Writing short pause");
    return pause(tape, start, (500L * ifc.tapeSampleRate) / 1000L);
  }
  
  /**
   * Writes a list of bytes to the tape.
   *
   * @param  tape          the tape
   * @param  start         the starting position
   * @param  bytes         list of bytes to be written
   * @return               the new position
   * @throws TapeException on error in data
   */
  public static long write(final Tape tape,
			   long start,
			   final List<Byte> bytes
			   ) throws TapeException {
    log.fine("Writing " + bytes.size() + " byte(s) to tape");
    long p = 0, lastPulse = -1;
    for (byte b: bytes) {
      log.finest(String.format("Writing byte: 0x%02x", b));
      int s = ((b & 0xff) | 0x0300) << 1;
      while (s != 0) {
	if ((s & 1) == 0) {
	  if (lastPulse != -1) {
	    tape.put(lastPulse, SECOND_HALFSLOT);
	  }
	  lastPulse = start + FIRST_HALFSLOT;
	} else {
	  if (lastPulse != -1) {
	    tape.put(lastPulse, SLOT);
	  } else {
	    tape.put(start, FIRST_HALFSLOT);
	  }
	  lastPulse = -1;
	}
	start += SLOT;
	s >>= 1;
      }
    }
    return start;
  }

  /**
   * Factory method for creating a PMD 85 tape block of appropriate type.
   *
   * @param  list          list of input data
   * @throws TapeException on error in data
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
