/* AbstractMemory.java
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
 **/

package cz.pecina.retro.cpu;

/**
 * Memory space to be accessed by CPUs, memory-mapped peripherals and
 * memory-management methods.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public interface AbstractMemory {

  /**
   * Gets an array of memory banks.  The order given will be respected
   * on the Memory frame.
   *
   * @return memory block
   */
  public abstract String[] getMemoryBanks();

  /**
   * Gets the size of a memory banks.
   *
   * @param  bank bank name
   * @return      size of memory bank in bytes
   */
  public abstract int getMemoryBankSize(final String bank);

  /**
   * Gets memory bank as a byte array.
   *
   * @param  bank bank name
   * @return      memory block
   */
  public abstract byte[] getMemoryBank(final String bank);

  /**
   * Reads byte from memory.
   *
   * @param  address address in memory
   * @return         byte in memory
   */
  public abstract int getByte(final int address);

  /**
   * Writes byte to memory.
   *
   * @param address address in memory
   * @param data    byte to be written
   */
  public abstract void setByte(final int address, final int data);
}
