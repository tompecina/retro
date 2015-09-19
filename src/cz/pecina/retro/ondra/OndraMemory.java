/* OndraMemory.java
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

package cz.pecina.retro.ondra;

import java.util.logging.Logger;
import java.util.logging.Level;

import org.jdom2.Element;

import cz.pecina.retro.cpu.Device;
import cz.pecina.retro.cpu.AbstractMemory;
import cz.pecina.retro.cpu.IOElement;
import cz.pecina.retro.cpu.Register;
import cz.pecina.retro.cpu.Block;
import cz.pecina.retro.cpu.IOPin;
import cz.pecina.retro.cpu.IONode;

import cz.pecina.retro.memory.Snapshot;
import cz.pecina.retro.memory.Info;

import cz.pecina.retro.jstick.JoystickHardware;

import cz.pecina.retro.trec.TapeRecorderHardware;

/**
 * Tesla Ondra SPO 186 memory.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class OndraMemory
  extends Device
  implements AbstractMemory, IOElement {

  // memory sizes
  private static final int ROM_SIZE = 0x4000;
  private static final int RAM_SIZE = 0x10000;

  // dynamic logger, per device
  private Logger log;

  // the computer model
  private int model;

  // pins
  private AllRAMPin allRAMPin = new AllRAMPin();
  private InPortPin inPortPin = new InPortPin();

  // flags
  private boolean allRAMFlag, inPortFlag;
  
  // the display hardware
  private DisplayHardware displayHardware;
  
  // the keyboard hardware
  private KeyboardHardware keyboardHardware;

  // the joystick hardware
  private JoystickHardware joystickHardware;

  // the tape recorder hardware
  private TapeRecorderHardware tapeRecorderHardware;

  /**
   * ROM as an array of bytes.
   */
  protected final byte[] rom;

  /**
   * RAM as an array of bytes.
   */
  protected final byte[] ram;

  /**
   * Constructor of Ondra memory object.
   *
   * @param name                 device name
   * @param displayHardware      the display hardware object
   * @param keyboardHardware     the keyboard hardware object
   * @param joystickHardware     the joystick hardware object
   * @param tapeRecorderHardware the tape recorder hardware object
   */
  public OndraMemory(final String name,
		     final DisplayHardware displayHardware,
		     final KeyboardHardware keyboardHardware,
		     final JoystickHardware joystickHardware,
		     final TapeRecorderHardware tapeRecorderHardware) {
    super(name);
    log = Logger.getLogger(getClass().getName() + "." + name);
    assert displayHardware != null;
    assert keyboardHardware != null;
    assert joystickHardware != null;
    assert tapeRecorderHardware != null;
    this.displayHardware = displayHardware;
    this.keyboardHardware = keyboardHardware;
    this.joystickHardware = joystickHardware;
    this.tapeRecorderHardware = tapeRecorderHardware;
    rom = new byte[ROM_SIZE];
    ram = new byte[RAM_SIZE];
    add(new Block("RAM") {
	@Override
	public byte[] getMemory() {
	  return ram;
	}
	@Override
	public void getContent(final Element block) {
	  Snapshot.buildBlockElement(ram, block, 0, RAM_SIZE);
	}
	@Override
	public void processContent(final Element block) {
	  Snapshot.processBlockElement(ram, block, 0);
	}
      });
    add(new Block("ROM") {
	@Override
	public byte[] getMemory() {
	  return rom;
	}
	@Override
	public void getContent(final Element block) {
	  Snapshot.buildBlockElement(rom, block, 0, ROM_SIZE);
	}
	@Override
	public void processContent(final Element block) {
	  Snapshot.processBlockElement(rom, block, 0);
	}
      });
    log.fine(String.format("New OndraMemory created, name: %s", name));
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

  // for description see Device
  @Override
  public void reset() {
    allRAMPin.notifyChange();
    inPortPin.notifyChange();
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
      final boolean newAllRAMFlag = (queryNode() == 1);
      if (newAllRAMFlag != allRAMFlag) {
	allRAMFlag = newAllRAMFlag;
	log.finer("allRAMFlag: " + allRAMFlag);
      }
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

  // InPort pin
  private class InPortPin extends IOPin {

    private InPortPin() {
      super();
    }

    @Override
    public void notifyChange() {
      final boolean newInPortFlag = (queryNode() == 1);
      if (newInPortFlag != inPortFlag) {
	inPortFlag = newInPortFlag;
	log.finer("inPortFlag: " + inPortFlag);
      }
    }
  }

  /**
   * Gets the InPort pin.
   *
   * @return the pin object
   */
  public IOPin getInPortPin() {
    return inPortPin;
  }

  /**
   * Refreshes the Video RAM.
   */
  public void refreshVideoRAM() {
    for (int address = Display.START_VIDEO; address < RAM_SIZE; address++) {
      setByte(address, ram[address]);
    }
    log.fine("Video RAM refreshed");
  }

  // for description see AbstractMemory
  @Override
  public int getByte(final int address) {
    assert (address >= 0) && (address < RAM_SIZE);
    int  data;

    if (!allRAMFlag && (address < 0x4000)) {
      data = rom[address];
    } else if (inPortFlag && (address >= 0xe000)) {
      data = tapeRecorderHardware.getOutPin().query() << 7;
      if ((address & 0x0f) == 0x0b) {
	data |=
	  (IONode.normalize(joystickHardware.getNorthPin().query()) << 2) |
	  IONode.normalize(joystickHardware.getEastPin().query()) |
	  (IONode.normalize(joystickHardware.getSouthPin().query()) << 3) |
	  (IONode.normalize(joystickHardware.getWestPin().query()) << 1) |
	  (IONode.normalize(joystickHardware.getFirePin().query()) << 4);
      } else {
	data |= keyboardHardware.getState(address & 0x0f);
      }
    } else {
	data = ram[address];
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
assert (address >= 0) && (address < RAM_SIZE);
    assert (data >= 0) && (data < 0x100);

    if ((allRAMFlag || (address >= 0x4000)) &&
	(!inPortFlag || (address < 0xe000))) {
      ram[address] = (byte)data;
    }

    if (address >= Display.START_VIDEO) {
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
