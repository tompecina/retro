/* SimpleMemory.java
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
 * Contiguous block of 64KB RAM with one optional ROM (non-writeable) block.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class SimpleMemory extends Device implements AbstractMemory {

  // dynamic logger, per device
  private Logger log;

  /**
   * Memory as an array of bytes.
   */
  protected final byte[] memory = new byte[0x10000];

  /**
   * The start of non-writeable memory (in KiB).
   */
  protected int startROM;

  /**
   * The start of writeable memory (in KiB).
   */
  protected int startRAM;

  /**
   * Constructor of zero-filled memory block.  The area between
   * <code>startROM</code> and <code>startRAM - 1</code> is non-writeable
   * (all writes are ignored).
   *
   * @param name     device name
   * @param startROM start of non-writeable memory (in KiB)
   * @param startRAM start of writeable memory (in KiB)
   */
  public SimpleMemory(final String name,
		      final int startROM,
		      final int startRAM) {
    super(name);
    log = Logger.getLogger(getClass().getName() + "." + name);
    this.startROM = startROM;
    this.startRAM = startRAM;
    add(new Register("ROM") {
	@Override
	public String getValue() {
	  return String.valueOf(SimpleMemory.this.startROM);
	}
	@Override
	public void processValue(final String value) {
	  SimpleMemory.this.startROM = Integer.parseInt(value);
	}
      });
    add(new Register("RAM") {
	@Override
	public String getValue() {
	  return String.valueOf(SimpleMemory.this.startRAM);
	}
	@Override
	public void processValue(final String value) {
	  SimpleMemory.this.startRAM = Integer.parseInt(value);
	}
      });
    add(new Block("COMBINED") {
	@Override
	public byte[] getMemory() {
	  return memory;
	}
	@Override
	public void getContent(final Element block) {
	  Snapshot.buildBlockElement(memory, block, 0, 0x10000);
	}
	@Override
	public void processContent(final Element block) {
	  Snapshot.processBlockElement(memory, block, 0);
	}
      });
    log.fine(String.format("New SimpleMemory created, name: %s", name));
  }

  /**
   * Gets start of non-writeable memory.
   *
   * @return start of non-writeable memory (in KiB)
   */
  public int getStartROM() {
    return startROM;
  }

  /**
   * Sets start of non-writeable memory.
   *
   * @param startROM start of non-writeable memory (in KiB)
   */
  public void setStartROM(final int startROM) {
    assert (startROM >= 0) && (startROM <= 64);
    this.startROM = startROM;
  }

  /**
   * Gets start of writeable memory.
   *
   * @return start of writeable memory (in KiB)
   */
  public int getStartRAM() {
    return startRAM;
  }

  /**
   * Sets start of writeable memory.
   *
   * @param startRAM start of writeable memory (in KiB)
   */
  public void setStartRAM(final int startRAM) {
    assert (startRAM >= 0) && (startRAM <= 64);
    this.startRAM = startRAM;
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
    return memory[address] & 0xff;
  }

  // for description see AbstractMemory
  @Override
  public void setByte(final int address, final int data) {
    assert (address >= 0) && (address < 0x10000);
    assert (data >= 0) && (data < 0x100);
    if ((address < (startROM * 0x0400)) ||
	(address >= (startRAM * 0x0400))) {
      memory[address] = (byte)data;
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
