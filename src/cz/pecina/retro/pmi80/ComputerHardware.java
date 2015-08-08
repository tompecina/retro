/* ComputerHardware.java
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

package cz.pecina.retro.pmi80;

import java.util.logging.Logger;
import java.util.Arrays;
import java.io.InputStream;
import java.io.IOException;
import cz.pecina.retro.common.Parameters;
import cz.pecina.retro.common.Application;
import cz.pecina.retro.cpu.IONode;
import cz.pecina.retro.cpu.Hardware;
import cz.pecina.retro.cpu.Intel8080A;
import cz.pecina.retro.cpu.Intel8255;
import cz.pecina.retro.cpu.SimpleMemory;
import cz.pecina.retro.cpu.NAND;
import cz.pecina.retro.trec.TapeRecorderInterface;
import cz.pecina.retro.trec.TapeRecorderHardware;
import cz.pecina.retro.debug.DebuggerHardware;

/**
 * Tesla PMI-80 hardware object.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class ComputerHardware {

  // static logger
  private static final Logger log =
    Logger.getLogger(ComputerHardware.class.getName());

  // the general hardware
  private Hardware hardware;

  // the memory
  private SimpleMemory memory;

  // the CPU
  private Intel8080A cpu;

  // the system 8255 (PPI1)
  private Intel8255 systemPPI;

  // the peripheral 8255 (PPI2)
  private Intel8255 peripheralPPI;

  // the display hardware
  private DisplayHardware displayHardware;

  // the keyboard hardware
  private KeyboardHardware keyboardHardware;

  // the tape recorder hardware
  private TapeRecorderHardware tapeRecorderHardware;

  // the debugger hardware
  private DebuggerHardware debuggerHardware;

  /**
   * Creates a new computer hardware object.
   */
  public ComputerHardware() {
    log.fine("New Computer hardware object creation started");

    // create new hardware
    hardware = new Hardware("PMI-80");

    // set up memory
    memory = new SimpleMemory("MEMORY",
			      UserPreferences.getStartROM(),
			      UserPreferences.getStartRAM());
    hardware.add(memory);
    Parameters.memoryDevice = memory;
    Parameters.memoryObject = memory;
	
    // set up CPU
    cpu = new Intel8080A("CPU");
    hardware.add(cpu);
    Parameters.systemClockSource = cpu;
    Parameters.cpu = cpu;

    // connect CPU and memory
    cpu.setMemory(memory);

    // load monitor
    try (final InputStream monitor =
	 getClass().getResourceAsStream("ROM/monitor.bin")) {
      final byte[] buffer = new byte[0x10000];
      final int n = monitor.read(buffer, 0, 0x10000);
      if (n < 1) {
	throw Application.createError(this, "monitorLoad");
      }
      final byte[] memoryArray =
	Parameters.memoryDevice.getBlockByName("COMBINED").getMemory();
      for (int addr = 0; addr < n; addr++) {
	memoryArray[addr] = buffer[addr];
      }
    } catch (final NullPointerException |
	     IOException |
	     IndexOutOfBoundsException exception) {
      log.fine("Error reading monitor");
      throw Application.createError(this, "monitorLoad");
    }

    // set up the system PPI
    systemPPI = new Intel8255("SYSTEM_PPI");  // /CS = A2
    hardware.add(systemPPI);
    for (int port = 0xf8; port < 0xfc; port++) {
      cpu.addIOInput(port, systemPPI); 
      cpu.addIOOutput(port, systemPPI);
    }

    // set up the peripheral PPI
    peripheralPPI = new Intel8255("PERIPHERAL_PPI");  // /CS = A3
    hardware.add(peripheralPPI);
    for (int port = 0xf4; port < 0xf8; port++) {
      cpu.addIOInput(port, peripheralPPI); 
      cpu.addIOOutput(port, peripheralPPI);
    }

    // set up the tape recorder NAND
    final NAND nand = new NAND("TapeRecorderNAND", 2);

    // set up the display hardware
    displayHardware = new DisplayHardware();

    // set up the keyboard hardware
    keyboardHardware = new KeyboardHardware(displayHardware);
    
    // set up the tape recorder hardware
    final TapeRecorderInterface tapeRecorderInterface =
      new TapeRecorderInterface();
    tapeRecorderInterface.tapeSampleRate = Constants.TAPE_SAMPLE_RATE;
    tapeRecorderInterface.tapeFormats =
      Arrays.asList(new String[] {"XML", "PMT", "PMITAPE", "SAM"});
    tapeRecorderInterface.timerPeriod = Constants.TIMER_PERIOD;
    tapeRecorderHardware = new TapeRecorderHardware(tapeRecorderInterface);

    // set up the debugger hardware
    debuggerHardware = new DebuggerHardware(cpu);

    // connect display, keyboard and tape recorder hardware
    for (int i = 0; i < 4; i++) {
      new IONode().add(systemPPI.getPin(16 + i))
	.add(displayHardware.getSelectPin(i));
    }
    for (int i = 0; i < 6; i++) {
      new IONode().add(systemPPI.getPin(i))
	.add(displayHardware.getDataPin(i));
    }
    new IONode().add(systemPPI.getPin(6))
      .add(displayHardware.getDataPin(6)).add(nand.getInPin(0));
    new IONode().add(systemPPI.getPin(7)).add(nand.getInPin(1));
    new IONode().add(nand.getOutPin()).add(tapeRecorderHardware.getOutPin());
    for (int i = 0; i < KeyboardHardware.NUMBER_MATRIX_ROWS; i++) {
      new IONode().add(systemPPI.getPin(20 + i))
	.add(keyboardHardware.getScanPin(i));
    }
    new IONode().add(systemPPI.getPin(23))
      .add(tapeRecorderHardware.getInPin());

    // reset all stateful devices
    hardware.reset();

    // load any startup images and snapshots
    new CommandLineProcessor(hardware);

    log.fine("New Computer hardware object createdo");
  }

  /**
   * Gets the memory.
   *
   * @return the memory
   */
  public SimpleMemory getMemory() {
    return memory;
  }

  /**
   * Gets the CPU.
   *
   * @return the CPU
   */
  public Intel8080A getCPU() {
    return cpu;
  }

  /**
   * Gets the system PPI (PPI1).
   *
   * @return the system PPI (PPI1)
   */
  public Intel8255 getSystemPPI() {
    return systemPPI;
  }

  /**
   * Gets the peripheral PPI (PPI2).
   *
   * @return the system PPI (PPI2)
   */
  public Intel8255 getPeripheralPPI() {
    return peripheralPPI;
  }

  /**
   * Gets the general hardware.
   *
   * @return the general hardware object
   */
  public Hardware getHardware() {
    return hardware;
  }

  /**
   * Gets the keyboard hardware.
   *
   * @return the keyboard hardware object
   */
  public KeyboardHardware getKeyboardHardware() {
    return keyboardHardware;
  }

  /**
   * Gets the display hardware.
   *
   * @return the display hardware object
   */
  public DisplayHardware getDisplayHardware() {
    return displayHardware;
  }

  /**
   * Gets the tape recorder hardware.
   *
   * @return the tape recorder hardware object
   */
  public TapeRecorderHardware getTapeRecorderHardware() {
    return tapeRecorderHardware;
  }

  /**
   * Gets the debugger hardware.
   *
   * @return the debugger hardware object
   */
  public DebuggerHardware getDebuggerHardware() {
    return debuggerHardware;
  }
}
