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

package cz.pecina.retro.pmd85;

import java.util.logging.Logger;
import java.util.Arrays;
import java.io.InputStream;
import java.io.IOException;
import cz.pecina.retro.common.Parameters;
import cz.pecina.retro.common.Application;
import cz.pecina.retro.cpu.IONode;
import cz.pecina.retro.cpu.Hardware;
import cz.pecina.retro.cpu.Intel8080A;
import cz.pecina.retro.cpu.MappedMemory;
import cz.pecina.retro.trec.TapeRecorderInterface;
import cz.pecina.retro.trec.TapeRecorderHardware;
import cz.pecina.retro.debug.DebuggerHardware;

/**
 * Tesla PMD 85 hardware object.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class ComputerHardware {

  // static logger
  private static final Logger log =
    Logger.getLogger(ComputerHardware.class.getName());

  // the general hardware  private Hardware hardware;
  private Hardware hardware;

  // the memory
  private MappedMemory memory;

  // the CPU
  private Intel8080A cpu;

  // the display hardware
  // private DisplayHardware displayHardware;

  // the keyboard hardware
  // private KeyboardHardware keyboardHardware;

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
    hardware = new Hardware("PMD85");

    // set up memory
    memory = new MappedMemory("MEMORY",
			      0,
			      0,
			      null,
			      null);
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
	 getClass().getResourceAsStream("ROM/monitor-3.bin")) {
      final byte[] buffer = new byte[0x10000];
      final int n = monitor.read(buffer, 0xd000, 0x2000);
      if (n < 1) {
	throw Application.createError(this, "monitorLoad");
      }
      for (int addr = 0; addr < n; addr++) {
	memory.getMemory()[addr] = buffer[addr];
      }
    } catch (final NullPointerException |
	     IOException |
	     IndexOutOfBoundsException exception) {
      log.fine("Error reading monitor");
      throw Application.createError(this, "monitorLoad");
    }

    // set up the display hardware
    // displayHardware = new DisplayHardware();

    // set up the keyboard hardware
    // keyboardHardware = new KeyboardHardware(displayHardware);
    
    // set up the tape recorder hardware
    final TapeRecorderInterface tapeRecorderInterface =
      new TapeRecorderInterface();
    tapeRecorderInterface.tapeSampleRate = Constants.TAPE_SAMPLE_RATE;
    tapeRecorderInterface.tapeFormats =
      Arrays.asList(new String[] {"XML", "PMT", "PTP"});
    tapeRecorderInterface.timerPeriod = Constants.TIMER_PERIOD;
    tapeRecorderHardware = new TapeRecorderHardware(tapeRecorderInterface);

    // set up the debugger hardware
    debuggerHardware = new DebuggerHardware(cpu);

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
  public MappedMemory getMemory() {
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
  // public KeyboardHardware getKeyboardHardware() {
  //   return keyboardHardware;
  // }

  /**
   * Gets the display hardware.
   *
   * @return the display hardware object
   */
  // public DisplayHardware getDisplayHardware() {
  //   return displayHardware;
  // }

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
