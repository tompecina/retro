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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.URISyntaxException;
import cz.pecina.retro.common.Parameters;
import cz.pecina.retro.common.Application;
import cz.pecina.retro.common.Util;
import cz.pecina.retro.cpu.IONode;
import cz.pecina.retro.cpu.Hardware;
import cz.pecina.retro.cpu.Intel8080A;
import cz.pecina.retro.cpu.Intel8255;
import cz.pecina.retro.cpu.LowPin;
import cz.pecina.retro.trec.TapeRecorderInterface;
import cz.pecina.retro.trec.TapeRecorderHardware;
import cz.pecina.retro.debug.DebuggerHardware;
import cz.pecina.retro.gui.LED;
import cz.pecina.retro.gui.Marking;

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

  // the general hardware
  private Hardware hardware;

  // the computer model
  private int model;

  // the memory
  private PMDMemory memory;

  // the CPU
  private Intel8080A cpu;

  // the system 8255 (PIO)
  private Intel8255 systemPIO;

  // the display hardware
  private DisplayHardware displayHardware;

  // the keyboard hardware
  private KeyboardHardware keyboardHardware;

  // the tape recorder hardware
  private TapeRecorderHardware tapeRecorderHardware;

  // the debugger hardware
  private DebuggerHardware debuggerHardware;

  // the LEDs
  private final LED yellowLED = new LED("small", "yellow");
  private final LED redLED = new LED("small", "red");
  private final LED greenLED = new LED("small", "green");

  // the marking
  private final Marking marking =
    new Marking("pmd85/Marking/basic-%d-%d.png",
		Constants.NUMBER_MODELS,
		UserPreferences.getModel());

  /**
   * Creates a new computer hardware object.
   */
  public ComputerHardware() {
    log.fine("New Computer hardware object creation started");

    // create new hardware
    hardware = new Hardware("PMD_85");

    // set up the display hardware
    displayHardware = new DisplayHardware();

    // set up memory
    memory = new PMDMemory("MEMORY", 8, 64, 32, displayHardware);
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
    for (int port: Util.portIterator(0, 0)) {
      cpu.addIOOutput(port, memory);
    }
      
    // set up the system PIO
    systemPIO = new Intel8255("SYSTEM_PIO");
    hardware.add(systemPIO);
    for (int port: Util.portIterator(0x84, 0x8c)) {
      cpu.addIOInput(port, systemPIO); 
      cpu.addIOOutput(port, systemPIO);
    }

    // set up the keyboard hardware
    keyboardHardware = new KeyboardHardware();
    
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

    // connect memory controller
    new IONode().add(systemPIO.getPin(16 + 4)).add(memory.getAllRAMPin());
    new IONode().add(systemPIO.getPin(16 + 5)).add(memory.getMirrorPin());

    // // connect keyboard
    // for (int i = 0; i < 4; i++) {
    //   new IONode().add(systemPIO.getPin(i))
    // 	.add(keyboardHardware.getSelectPin(i));
    // }
    // for (int i = 0; i < 5; i++) {
    //   new IONode().add(systemPIO.getPin(8 + i))
    // 	.add(keyboardHardware.getScanPin(i));
    // }
    // new IONode().add(systemPIO.getPin(8 + 5))
    //   .add(keyboardHardware.getShiftPin(i));
    // new IONode().add(systemPIO.getPin(8 + 6))
    //   .add(keyboardHardware.getStopPin(i));
    // new IONode().add(systemPIO.getPin(16 + 2))
    //   .add(keyboardHardware.getYellowLEDPin(i)).add(yellowLEDPin);
    // new IONode().add(systemPIO.getPin(16 + 3))
    //   .add(keyboardHardware.getRedLEDPin(i)).add(redLEDPin);
    // new IONode().add(new LowPin()).add(keyboardHardware.getGreenLEDPin(i))
    //   .add(greenLEDPin);

    // load any startup images and snapshots
    new CommandLineProcessor(hardware);

    log.fine("New Computer hardware object created");
  }
    
  // loads monitor
  private void loadMonitor() {
    try {
      final byte buffer[] = Files.readAllBytes(Paths.get(getClass()
         .getResource("ROM/monitor-" + model + ".bin").toURI()));
      final int size = buffer.length;
      if (size != ((model < 3) ? 0x1000 : 0x2000)) {
	throw new IOException("Wrong size");
      }
      final byte[] memoryArray =
	Parameters.memoryDevice.getBlockByName("ROM").getMemory();
      for (int addr = 0; addr < size; addr++) {
	memoryArray[addr] = buffer[addr];
	if (model < 3) {
	  memoryArray[addr + 0x1000] = buffer[addr];
	}
      }
    } catch (final NullPointerException |
	     URISyntaxException |
	     IOException |
	     IndexOutOfBoundsException exception) {
      log.fine("Error reading monitor, exception: " + exception);
      throw Application.createError(this, "monitorLoad");
    }
    log.fine("Monitor read");
  }

  // loads Basic
  private void loadBasic() {
    try {
      final byte buffer[] = Files.readAllBytes(Paths.get(getClass()
        .getResource("ROM/basic-" + model + ".bin").toURI()));
      final int size = buffer.length;
      if (size != ((model < 3) ? (9 * 0x400) : (10 * 0x400))) {
	throw new IOException("Wrong size");
      }
      final byte[] memoryArray =
	Parameters.memoryDevice.getBlockByName("RMM").getMemory();
      for (int addr = 0; addr < 0x8000; addr++) {
	memoryArray[addr] = (addr < size) ? buffer[addr] : (byte)0xff;
      }
    } catch (final NullPointerException |
	     URISyntaxException |
	     IOException |
	     IndexOutOfBoundsException exception) {
      log.fine("Error reading Basic, exception: " + exception);
      throw Application.createError(this, "basicLoad");
    }
    log.fine("Basic read");
  }

  /**
   * Sets the model.
   *
   * @param computer the computer control object
   * @param model    the model
   */
  public void setModel(final Computer computer, final int model) {
    log.fine("Setting model: " + model);
    assert (model >= 0) && (model < Constants.NUMBER_MODELS);
    this.model = model;
    marking.setState(model);
    computer.getComputerHardware().getKeyboardHardware()
      .getKeyboardLayout().modify(model);
    computer.getKeyboardFrame().getKeyboardPanel().replaceKeys();
    memory.setModel(model);
    loadMonitor();
    loadBasic();
    hardware.reset();
  }

  /**
   * Gets the model.
   *
   * @return the model
   */
  public int getModel() {
    return model;
  }

  /**
   * Gets the memory.
   *
   * @return the memory
   */
  public PMDMemory getMemory() {
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
   * Gets the system PIO.
   *
   * @return the system PIO
   */
  public Intel8255 getSystemPIO() {
    return systemPIO;
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

  /**
   * Gets the yellow LED.
   *
   * @return the yellow LED
   */
  public LED getYellowLED() {
    return yellowLED;
  }

  /**
   * Gets the red LED.
   *
   * @return the red LED
   */
  public LED getRedLED() {
    return redLED;
  }

  /**
   * Gets the green LED.
   *
   * @return the green LED
   */
  public LED getGreenLED() {
    return greenLED;
  }

  /**
   * Gets the marking.
   *
   * @return the marking
   */
  public Marking getMarking() {
    return marking;
  }
}
