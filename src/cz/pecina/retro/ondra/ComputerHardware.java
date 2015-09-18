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

package cz.pecina.retro.ondra;

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
import cz.pecina.retro.common.Sound;

import cz.pecina.retro.cpu.IONode;
import cz.pecina.retro.cpu.Hardware;
import cz.pecina.retro.cpu.ZilogZ80;
import cz.pecina.retro.cpu.OutputLatch;
import cz.pecina.retro.cpu.NegativeLEDPin;

import cz.pecina.retro.jstick.JoystickHardware;

import cz.pecina.retro.trec.TapeRecorderInterface;
import cz.pecina.retro.trec.TapeRecorderHardware;

import cz.pecina.retro.gui.LED;
import cz.pecina.retro.gui.Marking;

/**
 * Tesla Ondra SPO 186 hardware object.
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
  private OndraMemory memory;

  // the CPU
  private ZilogZ80 cpu;

  // the combined output latches
  private OutputLatch primaryLatch, secondaryLatch;

  // the printer latch
  private OutputLatch printerLatch;
  
  // the speaker
  // private Speaker speaker;

  // the display hardware
  private DisplayHardware displayHardware;

  // the keyboard hardware
  private KeyboardHardware keyboardHardware;

  // the joystick hardware
  private JoystickHardware joystickHardware;

  // the tape recorder hardware
  private TapeRecorderHardware tapeRecorderHardware;

  // LEDs
  private final LED yellowLED =
    new LED("small", "yellow");
  private final LED redLED =
    new LED("small", "red");

  // the marking
  private final Marking marking =
    new Marking("ondra/Marking/basic-%d.png");

  /**
   * Creates a new computer hardware object.
   */
  public ComputerHardware() {
    log.fine("New Computer hardware object creation started");

    // create new hardware
    hardware = new Hardware("ONDRA");

    // set up memory
    memory = new OndraMemory("MEMORY", displayHardware);
    hardware.add(memory);
    Parameters.memoryDevice = memory;
    Parameters.memoryObject = memory;
	
    // set up CPU
    cpu = new ZilogZ80("CPU");
    hardware.add(cpu);
    Parameters.systemClockSource = cpu;
    Parameters.cpu = cpu;

    // connect CPU and memory
    cpu.setMemory(memory);

    // set up the output latches
    primaryLatch = new OutputLatch("PRIMARY_LATCH");
    hardware.add(primaryLatch);
    for (int port: Util.portIterator(0, 0x08)) {
      cpu.addIOOutput(port, primaryLatch);
    }
    secondaryLatch = new OutputLatch("SECONDARY_LATCH");
    hardware.add(secondaryLatch);
    for (int port: Util.portIterator(0, 0x01)) {
      cpu.addIOOutput(port, secondaryLatch);
    }
    printerLatch = new OutputLatch("PRINTER_LATCH");
    hardware.add(printerLatch);
    for (int port: Util.portIterator(0, 0x02)) {
      cpu.addIOOutput(port, printerLatch);
    }
    
    // set up the display hardware
    displayHardware = new DisplayHardware("DISPLAY", this);
    hardware.add(displayHardware);
    for (int port: Util.portIterator(0, 0)) {
      cpu.addIOInput(port, displayHardware);
    }

    // set up the keyboard hardware
    keyboardHardware = new KeyboardHardware();
    
    // set up the tape recorder hardware
    final TapeRecorderInterface tapeRecorderInterface =
      new TapeRecorderInterface();
    tapeRecorderInterface.tapeSampleRate = Constants.TAPE_SAMPLE_RATE;
    tapeRecorderInterface.timerPeriod = Constants.TIMER_PERIOD;
    tapeRecorderInterface.tapeFormats = Arrays.asList(new String[]
      {"XML", "PMT", "WAV"});
    tapeRecorderInterface.vuRecConstant = 150.0;
    tapeRecorderInterface.vuPlayConstant = 80.0;
    tapeRecorderHardware = new TapeRecorderHardware(tapeRecorderInterface);

    // connect memory controller
    new IONode().add(primaryLatch.getOutPin(1)).add(memory.getAllRAMPin());
    new IONode().add(primaryLatch.getOutPin(2)).add(memory.getInPortPin());

    // connect display
    new IONode().add(primaryLatch.getOutPin(0)).add(displayHardware.getEnablePin());
    new IONode().add(primaryLatch.getOutPin(4)).add(displayHardware.getAddressPin(0));
    new IONode().add(primaryLatch.getOutPin(5)).add(displayHardware.getAddressPin(1));
    
    // connect the tape recorder
    new IONode()
      .add(primaryLatch.getOutPin(3))
      .add(tapeRecorderHardware.getInPin());

    // set up the sound interface
    new Sound(Constants.SOUND_SAMPLING_RATE, 2);
    Parameters.sound.setVolume(Sound.TAPE_RECORDER_CHANNEL,
      UserPreferences.getTapeRecorderVolume() / 100f);
    Parameters.sound.setMute(Sound.TAPE_RECORDER_CHANNEL,
      UserPreferences.isTapeRecorderMute());
    Parameters.sound.setVolume(Sound.SPEAKER_CHANNEL,
      UserPreferences.getSpeakerVolume() / 100f);
    Parameters.sound.setMute(Sound.SPEAKER_CHANNEL,
      UserPreferences.isSpeakerMute());
    
    // set up LEDs
    yellowLEDPin = new NegativeLEDPin(yellowLed);
    redLEDPin = new NegativeLEDPin(redLed);
    
    // connect LEDs
    new IONode()
      .add(secondaryLatch.getOutPin(1))
      .add(yellowLEDPin);
    new IONode()
      .add(secondaryLatch.getOutPin(0))
      .add(green);
    
    // set up the speaker
    // speaker = new Speaker("SPEAKER");

    // reset the hardware
    hardware.reset();
    
    // load any startup images and snapshots
    new CommandLineProcessor(hardware);

    log.fine("New Computer hardware object created");
  }
    
  /**
   * Loads the ROM contents.
   */
  public void loadROM() {
    try {
      byte buffer[];
      if (CommandLineProcessor.fileNameROM == null) {
	buffer = Files.readAllBytes(Paths.get(getClass()
          .getResource("ROM/monitor.bin").toURI()));
      } else {
	buffer =
	  Files.readAllBytes(Paths.get(CommandLineProcessor.fileNameROM));
      }
      final int size = buffer.length;
      if (size == 0) {
	log.fine("Empty ROM contents");
	return;
      }
      if (size > 0x4000) {
	throw new IOException("Wrong size");
      }
      final byte[] memoryArray =
	Parameters.memoryDevice.getBlockByName("ROM").getMemory();
      for (int addr = 0; addr < 0x4000; addr++) {
	memoryArray[addr] = ((addr < size) ? buffer[addr] : (byte)0xff);
      }
      log.fine(String.format("ROM contents read, size: 0x%04x", size));
    } catch (final NullPointerException |
	     URISyntaxException |
	     IOException |
	     IndexOutOfBoundsException exception) {
      log.fine("Error reading ROM, exception: " + exception.getMessage());
      throw Application.createError(this, "ROMLoad");
    }
  }

  /**
   * Clears the RAM.
   */
  public void clearRAM() {
    Arrays.fill(memory.getRAM(), (byte)0);
    displayHardware.refresh();
  }
  
  /**
   * Resets hardware.
   */
  public void reset() {
    hardware.reset();
    clearRAM();
    loadROM();
    reset();
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
  public OndraMemory getMemory() {
    return memory;
  }

  /**
   * Gets the CPU.
   *
   * @return the CPU
   */
  public ZilogZ80 getCPU() {
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
   * Gets the yellow LED.
   *
   * @return the yellow LED
   */
  public LED getYellowLED() {
    return yellowLED;
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
