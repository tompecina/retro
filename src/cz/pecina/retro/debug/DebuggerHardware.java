/* DebuggerHardware.java
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

package cz.pecina.retro.debug;

import java.util.logging.Logger;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import cz.pecina.retro.common.Parameters;
import cz.pecina.retro.common.Application;

import cz.pecina.retro.cpu.Intel8080A;

import cz.pecina.retro.gui.BlockModel;
import cz.pecina.retro.gui.PushButton;
import cz.pecina.retro.gui.UniversalPushButton;
import cz.pecina.retro.gui.SiSDBlock;
import cz.pecina.retro.gui.LED;

/**
 * Intel 8080A hardware debugger.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class DebuggerHardware {

  // static logger
  private static final Logger log =
    Logger.getLogger(DebuggerHardware.class.getName());

  /**
   * Number of bytes displayed by the debugger dump line.
   */
  public static final int NUMBER_MEMORY_DATA = 8;

  /**
   * Number of permanent debugger breakpoints.  There is always only one
   * temporary breakpoint (which may be unset).
   */
  public static final int NUMBER_BREAKPOINTS = 4;

  /**
   * Number of SiSD elements in the block displaying the next instruction.
   */
  public static final int NUMBER_DISASSEMBLY = 11;

  /**
   * Block model used for all SiSD blocks.
   */
  public static final BlockModel blockModel = new BlockModel();
  static {
    blockModel.elementType = "small";
    blockModel.elementColor = "red";
    blockModel.buttonType = "small";
    blockModel.buttonColor = "black";
    blockModel.incrementButtonSymbol = "plus";
    blockModel.decrementButtonSymbol = "minus";
    blockModel.gridX = 12;
    blockModel.elementOffsetX = 0;
    blockModel.elementOffsetY = 0;
    blockModel.incrementButtonOffsetX = 3;
    blockModel.incrementButtonOffsetY = -8;
    blockModel.decrementButtonOffsetX = blockModel.incrementButtonOffsetX;
    blockModel.decrementButtonOffsetY = 19;
  }

  /**
   * The CPU object the debugger will operate on.
   */
  public Intel8080A cpu;

  /**
   * SiSD block diplaying the memory dump address.
   */
  public final SiSDBlock memoryAddress = new SiSDBlock(blockModel,
						       4,
						       false,
						       true);

  /**
   * Memory data dump starting address;
   */
  public int memoryAddressValue;

  /**
   * Array of SiSD blocks diplaying the memory data dump.
   */
  public final SiSDBlock memoryData[] = new SiSDBlock[NUMBER_MEMORY_DATA];

  /**
   * SiSD block diplaying the register A.
   */
  public final SiSDBlock registerA = new SiSDBlock(blockModel,
						   2,
						   false,
						   false);

  /**
   * SiSD block diplaying the register pair BC.
   */
  public final SiSDBlock registerBC = new SiSDBlock(blockModel,
						    4,
						    false,
						    false);

  /**
   * SiSD block diplaying the register pair DE.
   */
  public final SiSDBlock registerDE = new SiSDBlock(blockModel,
						    4,
						    false,
						    false);

  /**
   * SiSD block diplaying the register pair HL.
   */
  public final SiSDBlock registerHL = new SiSDBlock(blockModel,
						    4,
						    false,
						    false);

  /**
   * SiSD block diplaying the stack pointer.
   */
  public final SiSDBlock registerSP = new SiSDBlock(blockModel,
						    4,
						    false,
						    false);

  /**
   * SiSD block diplaying the program counter.
   */
  public final SiSDBlock programCounter = new SiSDBlock(blockModel,
							4,
							false,
							true);

  /**
   * SiSD block diplaying the next instruction.
   */
  public final SiSDBlock instructionDisplay = new SiSDBlock(blockModel,
							    NUMBER_DISASSEMBLY,
							    true,
							    false);

  /**
   * Array of SiSD blocks diplaying the breakpoint addresses.
   */
  public final SiSDBlock[] breakpoints = new SiSDBlock[NUMBER_BREAKPOINTS];

  /**
   * List of breakpoints.
   */
  public final List<Integer> breakpointValues = new ArrayList<>();
    
  /**
   * Lookup button for the register pair BC.
   */
  public final PushButton lookUpBCButton =
    new UniversalPushButton(blockModel.buttonType,
			    blockModel.buttonColor,
			    "up",
			    null,
			    null);

  /**
   * Lookup button for the register pair DE.
   */
  public final PushButton lookUpDEButton =
    new UniversalPushButton(blockModel.buttonType,
			    blockModel.buttonColor,
			    "up",
			    null,
			    null);

  /**
   * Lookup button for the register pair HL.
   */
  public final PushButton lookUpHLButton =
    new UniversalPushButton(blockModel.buttonType,
			    blockModel.buttonColor,
			    "up",
			    null,
			    null);

  /**
   * Lookup button for the stack pointer.
   */
  public final PushButton lookUpSPButton =
    new UniversalPushButton(blockModel.buttonType,
			    blockModel.buttonColor,
			    "up",
			    null,
			    null);

  /**
   * LED displaying the S (sign) flag.
   */
  public final LED ledS = new LED("small", "red");

  /**
   * LED displaying the Z (zero) flag.
   */
  public final LED ledZ = new LED("small", "red");

  /**
   * LED displaying the AC (auxiliary carry) flag.
   */
  public final LED ledAC = new LED("small", "red");

  /**
   * LED displaying the P (parity) flag.
   */
  public final LED ledP = new LED("small", "red");

  /**
   * LED displaying the CY (carry) flag.
   */
  public final LED ledCY = new LED("small", "red");

  /**
   * LED displaying the IE (interrupt enable) flag.
   */
  public final LED ledIE = new LED("small", "red");

  /**
   * STEP IN debugger button.
   */
  public final PushButton stepInButton =
    new PushButton("debug/DebuggerButton/stepin-%d-%s.png", null, null);

  /**
   * STEP OVER debugger button.
   */
  public final PushButton stepOverButton =
    new PushButton("debug/DebuggerButton/stepover-%d-%s.png", null, null);

  /**
   * STEP IN debugger button.
   */
  public final PushButton runStopButton =
    new PushButton("debug/DebuggerButton/runstop-%d-%s.png", null, null);

  /**
   * Debugger buttons as an array.
   */
  public final PushButton[] debuggerButtons =
    new PushButton[] {stepInButton, stepOverButton, runStopButton};

  /**
   * Creates a new debugger hardware object.
   *
   * @param cpu the CPU object
   */
  public DebuggerHardware(final Intel8080A cpu) {
    log.fine("New DebuggerHardware creation started");
    this.cpu = cpu;
    memoryAddress.addChangeListener(new UpdateMemoryDump());
    for (int i = 0; i < NUMBER_MEMORY_DATA; i++) {
      memoryData[i] = new SiSDBlock(blockModel, 2, false, false);
    }
    for (int i = 0; i < NUMBER_BREAKPOINTS; i++) {
      breakpoints[i] = new SiSDBlock(blockModel, 4, false, true);
      breakpointValues.add(0xffff);
    }
    lookUpBCButton.addMouseListener(new LookUpBC());
    lookUpDEButton.addMouseListener(new LookUpDE());
    lookUpHLButton.addMouseListener(new LookUpHL());
    lookUpSPButton.addMouseListener(new LookUpSP());
    programCounter.addChangeListener(new UpdateDisassembly());
    log.fine("New DebuggerHardware created");
  }

  /**
   * Gets the breakpoins as a {@code Set}.
   *
   * @return {@code Set} of breakpoints
   */
  public Set<Integer> getBreakpointValues() {
    return new HashSet<Integer>(breakpointValues);
  }

  // converts Long to 16-bit unsigned integer
  private static int longTo16BitInt(final Object l) {
    return (int)((long)l & 0xffff);
  }

  /**
   * Removes any temporary breakpoints.
   */
  public void removeTemporaryBreakpoints() {
    while (breakpointValues.size() > NUMBER_BREAKPOINTS) {
      breakpointValues.remove(NUMBER_BREAKPOINTS);
    }
  }

  /**
   * Activates the debugger display.
   */
  public void activate() {
    memoryAddress.setState(memoryAddressValue);
    memoryAddress.fireStateChanged();
    registerA.setState(cpu.getA());
    registerBC.setState(cpu.getBC());
    registerDE.setState(cpu.getDE());
    registerHL.setState(cpu.getHL());
    registerSP.setState(cpu.getSP());
    ledS.setState(cpu.isSF());
    ledZ.setState(cpu.isZF());
    ledAC.setState(cpu.isACF());
    ledP.setState(cpu.isPF());
    ledCY.setState(cpu.isCF());
    ledIE.setState(cpu.isIE());
    for (int i = 0; i < NUMBER_BREAKPOINTS; i++) {
      breakpoints[i].setState((long)breakpointValues.get(i));
    }
    programCounter.setState(cpu.getPC());
    programCounter.fireStateChanged();
    log.fine("Debugger display activated");
  }

  /**
   * Deactivates (blanks) the debugger display.
   */
  public void deactivate() {
    memoryAddress.setBlank();
    for (SiSDBlock block: memoryData) {
      block.setBlank();
    }
    registerA.setBlank();
    registerBC.setBlank();
    registerDE.setBlank();
    registerHL.setBlank();
    registerSP.setBlank();
    ledS.setState(false);
    ledZ.setState(false);
    ledAC.setState(false);
    ledP.setState(false);
    ledCY.setState(false);
    ledIE.setState(false);
    for (SiSDBlock block: breakpoints) {
      block.setBlank();
    }
    programCounter.setBlank();
    instructionDisplay.setState(Application.getString(this, "running"));
    log.fine("Debugger display deactivated");
  }

  /**
   * Updates the debugger display.
   */
  public void update() {
    memoryAddressValue = longTo16BitInt(memoryAddress.getState());
    for (int i = 0; i < NUMBER_BREAKPOINTS; i++) {
      breakpointValues.set(i, longTo16BitInt(breakpoints[i].getState()));
    }
    cpu.setPC(longTo16BitInt(programCounter.getState()));
    log.fine("Debugger display updated");
  }

  // memory dump listener
  private class UpdateMemoryDump implements ChangeListener {
    @Override
    public void stateChanged(final ChangeEvent event) {
      if (!memoryAddress.isBlank()) {
	final int address = longTo16BitInt(memoryAddress.getState());
	for (int column = 0; column < NUMBER_MEMORY_DATA; column++) {
	  memoryData[column].setState(Parameters.memoryObject
	    .getByte((address + column) & 0xffff));
	}
      }
    }
  }

  // auxiliary look-up method
  private void lookUp(final SiSDBlock block) {
    if (!block.isBlank()) {
      memoryAddress.setState(block.getState());
      memoryAddress.fireStateChanged();
    }
  }

  // look-up BC listener
  private class LookUpBC extends MouseAdapter {
    @Override
    public void mousePressed(final MouseEvent event) {
      lookUp(registerBC);
    }
  }

  // look-up DE listener
  private class LookUpDE extends MouseAdapter {
    @Override
    public void mousePressed(final MouseEvent event) {
      lookUp(registerDE);
    }
  }

  // look-up HL listener
  private class LookUpHL extends MouseAdapter {
    @Override
    public void mousePressed(final MouseEvent event) {
      lookUp(registerHL);
    }
  }

  // look-up SP listener
  private class LookUpSP extends MouseAdapter {
    @Override
    public void mousePressed(final MouseEvent event) {
      lookUp(registerSP);
    }
  }

  // disassembly listener
  private class UpdateDisassembly implements ChangeListener {
    @Override
    public void stateChanged(final ChangeEvent event) {
      if (!programCounter.isBlank()) {
	final int pc = longTo16BitInt(programCounter.getState());
	cpu.setPC(pc);
	instructionDisplay.setState(cpu.getDisassembly(pc).getSimplified());
      }
    }
  }
}
