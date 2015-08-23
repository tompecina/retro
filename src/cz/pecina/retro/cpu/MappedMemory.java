/* MappedMemory.java
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

import java.util.logging.Logger;
import java.util.logging.Level;

import org.jdom2.Element;

import cz.pecina.retro.memory.Snapshot;
import cz.pecina.retro.memory.Info;

/**
 * Contiguous block of 64KB RAM with one optional ROM (non-writeable) block
 * and callback for implementing a video-mapped block.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class MappedMemory extends SimpleMemory {

  // dynamic logger, per device
  private Logger log;

  // listeners
  private ReadListener readListener;
  private WriteListener writeListener;
  
  /**
   * Constructor of zero-filled memory block.  The area between
   * <code>startROM</code> and <code>startRAM - 1</code> is non-writeable
   * (all writes are ignored).
   *
   * @param name          device name
   * @param startROM      start of non-writeable memory (in KiB)
   * @param startRAM      start of writeable memory (in KiB)
   * @param writeListener write listener
   */
  public MappedMemory(final String name,
		      final int startROM,
		      final int startRAM,
		      final ReadListener readListener,
		      final WriteListener writeListener) {
    super(name, startROM, startRAM);
    log = Logger.getLogger(getClass().getName() + "." + name);
    this.readListener = readListener;
    this.writeListener = writeListener;
    log.fine(String.format("New MappedMemory created, name: %s", name));
  }

  /**
   * Gets the read listener.
   *
   * @return the read listener or <code>null</code> if none
   */
  public ReadListener getReadListiner() {
    return readListener;
  }

  /**
   * Sets the read listener.
   *
   * @param readListner the read listener or <code>null</code> if none
   */
  public void setReadListener(final ReadListener readListener) {
    this.readListener = readListener;
  }

  /**
   * Gets the write listener.
   *
   * @return the write listener or <code>null</code> if none
   */
  public WriteListener getWriteListiner() {
    return writeListener;
  }

  /**
   * Sets the write listener.
   *
   * @param writeListner the write listener or <code>null</code> if none
   */
  public void setWriteListener(final WriteListener writeListener) {
    this.writeListener = writeListener;
  }

  // for description see AbstractMemory
  @Override
  public int getByte(final int address) {
    assert (address >= 0) && (address < 0x10000);
    if (log.isLoggable(Level.FINEST))
      log.finest(String.format("Memory '%s' read: (%04x) -> %02x",
			       name,
			       address,
			       memory[address] & 0xff));
    int data = super.getByte(address) & 0xff;
    if (readListener != null) {
      data = readListener.getByte(address, data);
    }
    return data;
  }

  // for description see AbstractMemory
  @Override
  public void setByte(final int address, final int data) {
    assert (address >= 0) && (address < 0x10000);
    assert (data >= 0) && (data < 0x100);
    if ((address < (startROM * 0x0400)) ||
	(address >= (startRAM * 0x0400))) {
      final int oldData = memory[address] & 0xff;
      memory[address] = (byte)data;
      if (writeListener != null) {
	writeListener.setByte(address, oldData, data);
      }
      if (log.isLoggable(Level.FINEST)) {
	log.finest(String.format("Memory '%s' written: %02x -> (%04x)",
				 name,
				 (byte)data,
				 address));
      }
    } else if (log.isLoggable(Level.FINER)) {
      log.finer(String.format("Memory '%s' write denied, address: %04x",
			      name,
			      address));
    }
  }
}
