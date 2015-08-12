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
import cz.pecina.retro.cpu.IOElement;
import cz.pecina.retro.cpu.Register;
import cz.pecina.retro.cpu.Block;
import cz.pecina.retro.cpu.IOPin;
import cz.pecina.retro.memory.Snapshot;
import cz.pecina.retro.memory.Info;

/**
 * Tesla PMD 85 memory.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class PMDMemory
  extends Device
  implements AbstractMemory, IOElement {

  // dynamic logger, per device
  private Logger log;

  // the computer model
  private int model;

  // pins
  private AllRAMPin allRAMPin = new AllRAMPin();
  private MirrorPin mirrorPin = new MirrorPin();

  // flags
  private boolean resetFlag, allRAMFlag, mirrorFlag;
  
  // the display hardware
  private DisplayHardware displayHardware;

  /**
   * ROM as an array of bytes.
   */
  protected final byte[] rom;;

  /**
   * RAM as an array of bytes.
   */
  protected final byte[] ram;;

  /**
   * ROM module as an array of bytes or <code>null</code> if not present.
   */
  protected final byte[] rmm;

  /**
   * The size of ROM (in KiB).
   */
  protected int sizeROM;

  /**
   * The size of RAM (in KiB).
   */
  protected int sizeRAM;

  /**
   * The size of ROM module (in KiB).
   */
  protected int sizeRMM;

  /**
   * Constructor of PMD memory object.
   *
   * @param name    device name
   * @param sizeROM size of ROM (in KiB)
   * @param sizeRAM size of RAM (in KiB)
   * @param sizeRMM size of RMM module (in KiB)
   */
  public PMDMemory(final String name,
		   final int sizeROM,
		   final int sizeRAM,
		   final int sizeRMM,
		   final DisplayHardware displayHardware) {
    super(name);
    log = Logger.getLogger(getClass().getName() + "." + name);
    assert (sizeROM > 0) && (sizeROM <= 8); 
    assert (sizeRAM > 0) && (sizeRAM <= 64); 
    assert (sizeRMM >= 0) && (sizeRMM <= 32); 
    assert displayHardware != null;
    this.sizeROM = sizeROM;
    this.sizeRAM = sizeRAM;
    this.sizeRMM = sizeRMM;
    this.displayHardware = displayHardware;
    rom = new byte[sizeROM * 0x400];
    ram = new byte[sizeRAM * 0x400];
    rmm = (sizeRMM > 0) ? new byte[sizeRMM * 0x400] : null;
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
    add(new Register("RMM") {
	@Override
	public String getValue() {
	  return String.valueOf(PMDMemory.this.sizeRMM);
	}
	@Override
	public void processValue(final String value) {
	  PMDMemory.this.sizeRMM = Integer.parseInt(value);
	}
      });
    add(new Block("ROM") {
	@Override
	public byte[] getMemory() {
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
	public byte[] getMemory() {
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
    if (sizeRMM > 0) {
      add(new Block("RMM") {
	  @Override
	  public byte[] getMemory() {
	    return rmm;
	  }
	  @Override
	  public void getContent(final Element block) {
	    Snapshot.buildBlockElement(rmm, block, 0, sizeRMM * 0x400);
	  }
	  @Override
	  public void processContent(final Element block) {
	    Snapshot.processBlockElement(rmm, block, 0);
	  }
	});
    }
    log.fine(String.format("New PMDMemory created, name: %s", name));
  }

  /**
   * Gets the ROM.
   *
   * @return the ROM as a byte array
   */
  public byte[] getROM() {
    return rom;
  }

  /**
   * Gets the RAM.
   *
   * @return the RAM as a byte array
   */
  public byte[] getRAM() {
    return ram;
  }

  /**
   * Gets the ROM module.
   *
   * @return the ROM module as a byte array or <code>null</code> if not present
   */
  public byte[] getRMM() {
    return rmm;
  }

  /**
   * Gets the size of ROM.
   *
   * @return the size of ROM (in KiB)
   */
  public int getSizeROM() {
    return sizeROM;
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
   * Gets the size of the ROM module.
   *
   * @return the size of the ROM module (in KiB)
   */
  public int getSizeRMM() {
    return sizeRMM;
  }

  /**
   * Gets the size of RAM.
   *
   * @return the size of RAM (in KiB)
   */
  public int getSizeRAM() {
    return sizeRAM;
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

  /**
   * Sets the size of the ROM module.
   *
   * @param sizeRAM the size of the ROM module (in KiB)
   */
  public void setSizeRMM(final int sizeRMM) {
    assert (sizeRMM >= 0) && (sizeRAM <= 32);
    this.sizeRMM = sizeRMM;
  }

  /**
   * Sets the model.
   *
   * @param model the model
   */
  public void setModel(final int model) {
    log.fine("Setting model: " + model);
    this.model = model;
  }

  // for description see Device
  @Override
  public void reset() {
    resetFlag = true;
  }
  
  // for description see IOElement
  @Override
  public void portOutput(final int port, int data) {
    resetFlag = false;
  }

  // for description see IOElement
  @Override
  public int portInput(final int port) {
    return 0xff;
  }

  // AllRAM pin
  private class AllRAMPin extends IOPin {

    private AllRAMPin() {
      super();
    }

    @Override
    public void notifyChange() {
      allRAMFlag = (queryNode() == 0);
    }
  }

  /**
   * Gets the AllRAM pin.
   *
   * @return the pin object
   */
  public IOPin getAllRAMPin() {
    return allRAMPin;
  }

  // mirror pin
  private class MirrorPin extends IOPin {

    private MirrorPin() {
      super();
    }

    @Override
    public void notifyChange() {
      mirrorFlag = (queryNode() == 0);
    }
  }

  /**
   * Gets the mirror pin.
   *
   * @return the pin object
   */
  public IOPin getMirrorPin() {
    return mirrorPin;
  }

  // for description see AbstractMemory
  @Override
  public int getByte(final int address) {
    assert (address >= 0) && (address < 0x10000);
    int  data;
    switch (model) {
      case 0:
      case 1:
	if (resetFlag) {
	  if (address < 0x2000) {
	    data = rom[address];
	  } else if ((address >= 0x8000) && (address < 0xc000)) {
	    data = rom[address - 0x8000];
	  } else {
	    data = 0xff;
	  }
	} else {
	  if ((address >= 0x8000) && (address < 0xc000)) {
	    data = rom[address - 0x8000];
	  } else {
	    data = ram[address];
	  }
	}
	break;
      case 2:
	if (allRAMFlag) {
	  data = ram[address];
	} else if (resetFlag) {
	  if (address < 0x2000) {
	    data = rom[address];
	  } else if ((address >= 0x8000) && (address < 0xc000)) {
	    data = rom[address - 0x8000];
	  } else {
	    data = ram[address];
	  }
	} else {
	  if ((address >= 0x8000) && (address < 0xc000)) {
	    data = rom[address - 0x8000];
	  } else {
	    data = ram[address];
	  }
	}
	break;
      default:
	if (allRAMFlag) {
	  data = ram[address];
	} else if (mirrorFlag) {
	  data = rom[address & 0x1fff];
	} else if (address >= 0xe000) {
	  data = rom[address - 0xe000];
	} else {
	  data = ram[address];
	}
	break;
    }
    if (log.isLoggable(Level.FINEST))
      log.finest(String.format("Memory '%s' read: (%04x) -> %02x",
			       name,
			       address,
			       data & 0xff));
    return data & 0xff;
  }

  // for description see AbstractMemory
  @Override
  public void setByte(final int address, final int data) {
    assert (address >= 0) && (address < 0x10000);
    assert (data >= 0) && (data < 0x100);
    if ((model > 1) || (!resetFlag)) {
      ram[address] = (byte)data;
    }
    if (address >= 0xc000) {
      displayHardware.getDisplay().setByte(address, data);
    }
    if (log.isLoggable(Level.FINEST)) {
      log.finest(String.format("Memory '%s' written: %02x -> (%04x)",
			       name,
			       (byte)data,
			       address));
    }
  }
}
