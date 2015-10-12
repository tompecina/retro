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
import cz.pecina.retro.common.Sound;

import cz.pecina.retro.cpu.IONode;
import cz.pecina.retro.cpu.Hardware;
import cz.pecina.retro.cpu.Intel8080A;
import cz.pecina.retro.cpu.Intel8255A;
import cz.pecina.retro.cpu.Intel8251A;
import cz.pecina.retro.cpu.Intel8253;
import cz.pecina.retro.cpu.Inverter;
import cz.pecina.retro.cpu.NAND;
import cz.pecina.retro.cpu.XOR;
import cz.pecina.retro.cpu.FrequencyGenerator;
import cz.pecina.retro.cpu.FrequencyDivider;
import cz.pecina.retro.cpu.ProportionMeter;

import cz.pecina.retro.trec.TapeRecorderInterface;
import cz.pecina.retro.trec.TapeRecorderHardware;

import cz.pecina.retro.debug.DebuggerHardware;

import cz.pecina.retro.gui.VariableLED;
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

  // the system 8255A (PIO)
  private Intel8255A systemPIO;

  // the general-purpose 8255A (PIO)
  private Intel8255A gPIO;

  // the IMS-2 8255A (PIO)
  private Intel8255A imsPIO;

  // the 8251A (USART)
  private Intel8251A usart;

  // the 8253 (PIT)
  private Intel8253 pit;

  // the tape recorder XOR
  private XOR xor;

  // the 1Hz frequency generator
  private FrequencyGenerator rtcGenerator;

  // the fixed frequency generator (freq = 4000Hz)
  private FrequencyGenerator gen4k;

  // the 1:4 frequency divider
  private FrequencyDivider div;

  // the speaker
  private Speaker speaker;

  // the speaker & yellow LED logic
  private NAND pc0nand, pc1nand, speakerNand;
  private Inverter pc2inv;

  // the tape recorder Manchester decoder
  private ManchesterDecoder decoder;

  // the display hardware
  private DisplayHardware displayHardware;

  // the keyboard hardware
  private KeyboardHardware keyboardHardware;

  // the tape recorder hardware
  private TapeRecorderHardware tapeRecorderHardware;

  // the ROMModule hardware
  private ROMModuleHardware romModuleHardware;

  // the debugger hardware
  private DebuggerHardware debuggerHardware;

  // LEDs
  private final VariableLED yellowLED =
    new VariableLED("small", "yellow");
  private final VariableLED redLED =
    new VariableLED("small", "red");

  // LED meters
  private final ProportionMeter yellowLEDMeter;
  private final ProportionMeter redLEDMeter;

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
    displayHardware = new DisplayHardware(this);

    // set up memory
    memory = new PMDMemory("MEMORY", 8, 64, 32, displayHardware);
    hardware.add(memory);
    Parameters.memoryDevice = memory;
    Parameters.memoryObject = memory;
	
    // set up CPU
    cpu = new PMDIntel8080A("CPU");
    hardware.add(cpu);
    Parameters.systemClockSource = cpu;
    Parameters.cpu = cpu;

    // connect CPU and memory
    cpu.setMemory(memory);
    for (int port: Util.portIterator(0, 0)) {
      cpu.addIOOutput(port, memory);
    }
      
    // set up the system PIO
    systemPIO = new Intel8255A("SYSTEM_PIO");
    hardware.add(systemPIO);
    for (int port: Util.portIterator(0x84, 0x8c)) {
      cpu.addIOInput(port, systemPIO);
      cpu.addIOOutput(port, systemPIO);
    }

    // set up the general-purpose PIO
    gPIO = new Intel8255A("GENERAL_PURPOSE_PIO");
    hardware.add(gPIO);
    for (int port: Util.portIterator(0x4c, 0xfc)) {
      cpu.addIOInput(port, gPIO);
      cpu.addIOOutput(port, gPIO);
    }

    // set up the IMS-2 (HPIB) PIO
    imsPIO = new Intel8255A("IMS_2_PIO");
    hardware.add(imsPIO);
    for (int port: Util.portIterator(0x7c, 0xfc)) {
      cpu.addIOInput(port, imsPIO);
      cpu.addIOOutput(port, imsPIO);
    }

    // set up the keyboard hardware
    keyboardHardware = new KeyboardHardware();
    
    // set up the tape recorder hardware
    final TapeRecorderInterface tapeRecorderInterface =
      new TapeRecorderInterface();
    tapeRecorderInterface.tapeSampleRate = Constants.TAPE_SAMPLE_RATE;
    tapeRecorderInterface.timerPeriod = Constants.TIMER_PERIOD;
    tapeRecorderInterface.tapeFormats = Arrays.asList(new String[]
      {"XML", "PMT", "PTP", "PMD", "PMDTAPE", "WAV"});
    tapeRecorderInterface.vuRecConstant = 150.0;
    tapeRecorderInterface.vuPlayConstant = 80.0;
    tapeRecorderHardware = new TapeRecorderHardware(tapeRecorderInterface);

    // set up the debugger hardware
    debuggerHardware = new DebuggerHardware(cpu);

    // connect memory controller
    new IONode().add(systemPIO.getPin(16 + 4)).add(memory.getAllRAMPin());
    new IONode().add(systemPIO.getPin(16 + 5)).add(memory.getAllROMPin());

    // connect keyboard
    for (int i = 0; i < 4; i++) {
      new IONode().add(systemPIO.getPin(i))
    	.add(keyboardHardware.getSelectPin(i));
    }
    for (int i = 0; i < 5; i++) {
      new IONode().add(systemPIO.getPin(8 + i))
    	.add(keyboardHardware.getScanPin(i));
    }
    new IONode()
      .add(systemPIO.getPin(8 + 5))
      .add(keyboardHardware.getShiftPin());
    new IONode()
      .add(systemPIO.getPin(8 + 6))
      .add(keyboardHardware.getStopPin());
    
    // set up the ROM module hardware
    romModuleHardware = new ROMModuleHardware(this);

    // connect the ROM module
    for (int port: Util.portIterator(0x88, 0x8c)) {
      cpu.addIOInput(port, romModuleHardware.getPIO()); 
      cpu.addIOOutput(port, romModuleHardware.getPIO());
    }
    
    // set up the USART
    usart = new Intel8251A("USART");
    hardware.add(usart);
    for (int port: Util.portIterator(0x1c, 0xfc)) {
      cpu.addIOInput(port, usart);
      cpu.addIOOutput(port, usart);
    }

    // set up the tape recorder XOR
    xor = new XOR("XOR", 2);

    // set up the PIT
    pit = new Intel8253Mod("PIT", new boolean[] {false, true, false});
    hardware.add(pit);
    for (int port: Util.portIterator(0x5c, 0xfc)) {
      cpu.addIOInput(port, pit);
      cpu.addIOOutput(port, pit);
    }

    // set up the 1Hz frequency generator connected to Counter 2
    rtcGenerator = new FrequencyGenerator("RTC_GENERATOR", 1024000, 1024000);
    hardware.add(rtcGenerator);

    // set up the Manchester decoder
    decoder = new ManchesterDecoder("MANCHESTER_DECODER");
    hardware.add(decoder);

    // connect the USART and the tape recorder
    new IONode()
      .add(usart.getCtsPin())
      .add(usart.getRtsPin());
    new IONode()
      .add(usart.getTxdPin())
      .add(xor.getInPin(0));
    new IONode()
      .add(pit.getOutPin(1))
      .add(usart.getTxcPin())
      .add(xor.getInPin(1));
    new IONode()
      .add(xor.getOutPin())
      .add(tapeRecorderHardware.getInPin());
    new IONode()
      .add(tapeRecorderHardware.getOutPin())
      .add(usart.getDsrPin())
      .add(decoder.getInPin());
    new IONode()
      .add(decoder.getClockPin())
      .add(usart.getRxcPin());
    new IONode()
      .add(decoder.getDataPin())
      .add(usart.getRxdPin());

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
    
    // set up fixed frequency source and related logic
    gen4k = new FrequencyGenerator("FREQUENCY_GENERATOR_4KHz", 0x100, 0x100);
    hardware.add(gen4k);
    div = new FrequencyDivider("FREQUENCY_DIVIDER_4KHz_TO_1KHz", 4, false);
    hardware.add(div);
    pc0nand = new NAND("NAND_PC0", 2);
    pc1nand = new NAND("NAND_PC1", 2);
    pc2inv = new Inverter("INVERTER_PC2");
    speakerNand = new NAND("SPEAKER_NAND", 3);

    // connect the 1Hz generator to Counter 2
    new IONode()
      .add(rtcGenerator.getOutPin())
      .add(pit.getClockPin(2));

    // set up LEDs
    yellowLEDMeter = new ProportionMeter("YELLOW_LED");
    redLEDMeter = new ProportionMeter("RED_LED");
    
    // set up the speaker
    speaker = new Speaker("SPEAKER");

    // connect speaker and LEDs
    new IONode()
      .add(gen4k.getOutPin())
      .add(pc1nand.getInPin(1))
      .add(div.getInPin());
    new IONode()
      .add(systemPIO.getPin(16 + 1))
      .add(pc1nand.getInPin(0));
    new IONode()
      .add(div.getOutPin())
      .add(pc0nand.getInPin(1));
    new IONode()
      .add(systemPIO.getPin(16))
      .add(pc0nand.getInPin(0));
    new IONode()
      .add(systemPIO.getPin(16 + 2))
      .add(pc2inv.getInPin());
    new IONode()
      .add(pc0nand.getOutPin())
      .add(speakerNand.getInPin(0));
    new IONode()
      .add(pc1nand.getOutPin())
      .add(speakerNand.getInPin(2));
    new IONode()
      .add(pc2inv.getOutPin())
      .add(speakerNand.getInPin(1));
    new IONode()
      .add(speakerNand.getOutPin())
      .add(yellowLEDMeter.getInPin())
      .add(speaker.getInPin());
    new IONode()
      .add(systemPIO.getPin(16 + 3))
      .add(redLEDMeter.getInPin());
      
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
          .getResource("ROM/monitor-" + model + ".bin").toURI()));
      } else {
	buffer =
	  Files.readAllBytes(Paths.get(CommandLineProcessor.fileNameROM));
      }
      final int size = buffer.length;
      if (size == 0) {
	log.fine("Empty ROM contents");
	return;
      }
      final int modelSize = ((model < 3) ? 0x1000 : 0x2000);
      if (size > modelSize) {
	throw new IOException("Wrong size");
      }
      final byte[] memoryArray =
	Parameters.memoryDevice.getBlockByName("ROM").getMemory();
      for (int addr = 0; addr < modelSize; addr++) {
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
   * Loads the ROM module.
   */
  public void loadRMM() {
    try {
      byte buffer[];
      if (CommandLineProcessor.fileNameRMM == null) {
	buffer = Files.readAllBytes(Paths.get(getClass()
          .getResource("ROM/basic-" + model + ".bin").toURI()));
      } else {
	buffer =
	  Files.readAllBytes(Paths.get(CommandLineProcessor.fileNameRMM));
      }
      final int size = buffer.length;
      if (size == 0) {
	log.fine("Empty RMM contents");
	return;
      }
      if (size > 0x8000) {
	throw new IOException("Wrong size");
      }
      final byte[] memoryArray =
	Parameters.memoryDevice.getBlockByName("RMM").getMemory();
      for (int addr = 0; addr < 0x8000; addr++) {
	memoryArray[addr] = ((addr < size) ? buffer[addr] : (byte)0xff);
      }
      log.fine(String.format("RMM contents read, size: 0x%04x", size));
    } catch (final NullPointerException |
	     URISyntaxException |
	     IOException |
	     IndexOutOfBoundsException exception) {
      log.fine("Error reading RMM, exception: " + exception.getMessage());
      throw Application.createError(this, "RMMLoad");
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
    clearRAM();
    loadROM();
    loadRMM();
    reset();
  }

  /**
   * Resets hardware.
   */
  public void reset() {
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
  public Intel8255A getSystemPIO() {
    return systemPIO;
  }

  /**
   * Gets the general-purpose PIO.
   *
   * @return the general-purpose PIO
   */
  public Intel8255A getGPIO() {
    return gPIO;
  }

  /**
   * Gets the IMS-2 (HPIB) PIO.
   *
   * @return the IMS-2 (HPIB) PIO
   */
  public Intel8255A getImsPIO() {
    return imsPIO;
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
   * Gets the ROM module hardware.
   *
   * @return the ROM module hardware object
   */
  public ROMModuleHardware getROMModuleHardware() {
    return romModuleHardware;
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
  public VariableLED getYellowLED() {
    return yellowLED;
  }

  /**
   * Gets the red LED.
   *
   * @return the red LED
   */
  public VariableLED getRedLED() {
    return redLED;
  }

  /**
   * Gets the yellow LED meter.
   *
   * @return the yellow LED meter
   */
  public ProportionMeter getYellowLEDMeter() {
    return yellowLEDMeter;
  }

  /**
   * Gets the red LED meter.
   *
   * @return the red LED meter
   */
  public ProportionMeter getRedLEDMeter() {
    return redLEDMeter;
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
