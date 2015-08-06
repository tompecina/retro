/* WriteListener.java
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
 * Write listener for memory-mapped devices.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public interface WriteListener {

  /**
   * Writes byte to memory.
   *
   * @param address address in memory
   * @param oldData memory contents before the write
   * @param data    byte to be written
   */
  public abstract void setByte(final int address,
			       final int oldData,
			       final int data);
}
