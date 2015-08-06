/* PMDMemory.java
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

package cz.pecina.retro.pmd85;

import java.util.logging.Logger;
import java.util.logging.Level;
import org.jdom2.Element;
import cz.pecina.retro.cpu.Device;
import cz.pecina.retro.cpu.AbstractMemory;
import cz.pecina.retro.memory.Snapshot;
import cz.pecina.retro.memory.Info;

/**
 * Tesla PMD 85 memory.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class PMDMemory extends Device implements AbstractMemory {

  // dynamic logger, per device
  private Logger log;

  /**
   * ROM as an array of bytes.
   */
  protected final byte[] rom;;

  /**
   * RAM as an array of bytes.
   */
  protected final byte[] ram;;

  /**
   * The size of ROM (in KiB).
   */
  protected int sizeROM;

  /**
   * The size of RAM (in KiB).
   */
  protected int sizeRAM;

  /**
   * Constructor of zero-filled memory areas.
   *
   * @param name    device name
   * @param sizeROM size of ROM (in KiB)
   * @param sizeRAM size of RAM (in KiB)
   */
  public PMDMemory(final String name,
		   final int sizeROM,
		   final int sizeRAM) {
    super(name);
    log = Logger.getLogger(getClass().getName() + "." + name);
    assert (sizeROM > 0) && (sizeROM <= 8); 
    assert (sizeRAM > 0) && (sizeRAM <= 64); 
    this.sizeROM = sizeROM;
    this.sizeRAM = sizeRAM;
    rom = new byte[sizeROM * 0x400];
    ram = new byte[sizeRAM * 0x400];
    add(new Register("ROM") {
	@Override
	public String getValue() {
	  return String.valueOf(PMDMemory.this.sizeROM);
	}
	@Override
	public void processValue(final String value) {
	  PMDMemory.this.sizeROM = Integer.parseInt(value);
	}
      });
    add(new Register("RAM") {
	@Override
	public String getValue() {
	  return String.valueOf(PMDMemory.this.sizeRAM);
	}
	@Override
	public void processValue(final String value) {
	  PMDMemory.this.sizeRAM = Integer.parseInt(value);
	}
      });
    add(new Block("ROM") {
	@Override
	public byte[] getROM() {
	  return rom;
	}
	@Override
	public void getContent(final Element block) {
	  Snapshot.buildBlockElement(rom, block, 0, sizeROM * 0x400);
	}
	@Override
	public void processContent(final Element block) {
	  Snapshot.processBlockElement(rom, block, 0);
	}
      });
    add(new Block("RAM") {
	@Override
	public byte[] getRAM() {
	  return ram;
	}
	@Override
	public void getContent(final Element block) {
	  Snapshot.buildBlockElement(ram, block, 0, sizeRAM * 0x400);
	}
	@Override
	public void processContent(final Element block) {
	  Snapshot.processBlockElement(ram, block, 0);
	}
      });
    log.fine(String.format("New PMDMemory created, name: %s", name));
  }

  /**
   * Gets the size of ROM.
   *
   * @return the size of ROM (in KiB)
   */
  public int getSizeROM() {
    return size;
  }

  /**
   * Sets the size of ROM.
   *
   * @param sizeROM the size of ROM (in KiB)
   */
  public void setSizeROM(final int sizeROM) {
    assert (sizeROM > 0) && (sizeROM <= 8);
    this.sizeROM = sizeROM;
  }

  /**
   * Gets the size of RAM.
   *
   * @return the size of RAM (in KiB)
   */
  public int getSizeRAM() {
    return size;
  }

  /**
   * Sets the size of RAM.
   *
   * @param sizeRAM the size of RAM (in KiB)
   */
  public void setSizeRAM(final int sizeRAM) {
    assert (sizeRAM > 0) && (sizeRAM <= 8);
    this.sizeRAM = sizeRAM;
  }

  // for description see AbstractMemory
  @Override
  public String[] getMemoryBanks() {
    log.finer("Providing a list of memory banks");
    return new String[] {"RAM", "ROM"};
  }

  // for description see AbstractMemory
  @Override
  public int getMemoryBankSize(final String bank) {
    log.finer("Size of memory bank '" + bank + "' requested");
    switch (bank) {
      case "ROM":
	return sizeROM * 0x400;
      case "RAM":
	return sizeRAM * 0x400;
      default:
	throw Application.createError(this, "memoryBankDoesNotExist");
    }
  }

  // for description see AbstractMemory
  @Override
  public byte[] getMemoryBank(final String bank) {
    log.finer("Memory bank '" + bank + "' requested");
    switch (bank) {
      case "ROM":
	return rom;
      case "RAM":
	return ram;
      default:
	throw Application.createError(this, "memoryBankDoesNotExist");
    }
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
