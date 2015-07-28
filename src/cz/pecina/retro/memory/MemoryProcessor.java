/* MemoryProcessor.java
 *
 * Copyright (C) 2014-2015, Tomáš Pecina <tomas@pecina.cz>
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
import cz.pecina.retro.cpu.Hardware;
import cz.pecina.retro.common.Parameters;

/**
 * Memory processor.  Read/write functions are provided by
 * format subclasses, basic functions such as copy are implemented
 * by this class.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class MemoryProcessor {

  // static logger
  private static final Logger log =
    Logger.getLogger(MemoryProcessor.class.getName());

  /**
   * Hardware set.
   */
  protected Hardware hardware;

  /**
   * Source memory bank.
   */
  protected String sourceMemoryBank;

  /**
   * Destination memory bank.
   */
  protected String destinationMemoryBank;

  /**
   * Source memory array.
   */
  protected byte[] sourceMemory;

  /**
   * Destination memory array.
   */
  protected byte[] destinationMemory;

  /**
   * Creates an instance of memory processor.
   *
   * @param hardware hardware set
   */
  public MemoryProcessor(final Hardware hardware) {
    this.hardware = hardware;
    log.fine("New memory processor created");
  }

  /**
   * Creates an instance of memory processor and sets memory banks.
   *
   * @param hardware              hardware set
   * @param sourceMemoryBank      source memory bank
   * @param destinationMemoryBank destination memory bank
   */
  public MemoryProcessor(final Hardware hardware,
			 final String sourceMemoryBank,
			 final String destinationMemoryBank) {
    this.hardware = hardware;
    this.sourceMemoryBank = sourceMemoryBank;
    this.destinationMemoryBank = destinationMemoryBank;
    sourceMemory =
      Parameters.memoryDevice.getBlockByName(sourceMemoryBank).getMemory();
    destinationMemory =
      Parameters.memoryDevice.getBlockByName(destinationMemoryBank).getMemory();
    log.fine("New memory processor created: " + sourceMemoryBank + " -> " +
	     destinationMemoryBank);
  }

  /**
   * Copy a memory block, with wrap-around.  No protective measures
   * are taken if the source and destination areas overlap so special
   * effects can be achieved. 
   *
   * @param  startAddress       start address
   * @param  endAddress         end address
   * @param  destinationAddress destination address
   * @return                    number of bytes copied
   */
  public int copy(final int startAddress,
		  final int endAddress,
		  final int destinationAddress) {
    log.fine(String.format(
      "Copying memory block: start address: %04x, end address: %04x," +
      " destination address: %04x",
      startAddress,
      endAddress,
      destinationAddress));
    final int number = ((endAddress - startAddress) & 0xffff) + 1;
    for (int i = 0; i < number; i++) {
      final int sourceAddress = (startAddress + i) & 0xffff;
      final int targetAddress = (destinationAddress + i) & 0xffff;
      final int data = sourceMemory[sourceAddress] & 0xff;
      destinationMemory[targetAddress] = (byte)data;
      log.finest(String.format("Copying one byte: (%04x) -> %02x -> (%04x)",
			       sourceAddress, data, targetAddress));
    }
    log.fine("Copying completed, " + number + " byte(s) copied");
    return number;
  }

  /**
   * Fill a memory block with a byte.
   *
   * @param  startAddress start address
   * @param  endAddress   end address
   * @param  data         byte to fill with
   * @return number of bytes filled
   */
  public int fill(final int startAddress,
		  final int endAddress,
		  final int data) {
    log.fine(String.format(
      "Filling memory block: start address: %04x, end address: %04x," +
      " data: %02x",
      startAddress,
      endAddress,
      data));
    assert (data >= 0) && (data < 0x100);
    final int number = ((endAddress - startAddress) & 0xffff) + 1;
    for (int i = 0; i < number; i++) {
      final int targetAddress = (startAddress + i) & 0xffff;
      destinationMemory[targetAddress] = (byte)data;
      log.finest(String.format("Writing one byte: %02x -> (%04x)",
			       data,
			       targetAddress));
    }
    log.fine("Filling completed, " + number + " byte(s) filled");
    return number;
  }

  /**
   * Compary two memory areas.
   *
   * @param  startAddress       start address
   * @param  endAddress         end address
   * @param  destinationAddress destination address
   * @return                    address of the first mismatch or
   *                            <code>-1</code> if equal
   */
  public int compare(final int startAddress,
		     final int endAddress,
		     final int destinationAddress) {
    log.fine(String.format(
      "Comparing memory blocks: start address: %04x, end address: %04x," +
      " destination address: %04x",
      startAddress,
      endAddress,
      destinationAddress));
    final int number = ((endAddress - startAddress) & 0xffff) + 1;
    for (int i = 0; i < number; i++) {
      final int sourceAddress = (startAddress + i) & 0xffff;
      final int targetAddress = (destinationAddress + i) & 0xffff;
      final byte sourceData = sourceMemory[sourceAddress];
      final byte targetData = destinationMemory[targetAddress];
      log.finest(String.format(
        "Comparing one byte: (%04x) = %02x, (%04x) = %02x",
	sourceAddress,
	sourceData,
	targetAddress,
	targetData));
      if (sourceData != targetData) {
	log.fine(String.format("First mismatched at: %04x", sourceAddress));
	return sourceAddress;
      }
    }
    log.fine("Blocks are equal");
    return -1;
  }
}
