/* Computer.java
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
import java.util.List;
import java.util.ArrayList;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import cz.pecina.retro.common.Parameters;
import cz.pecina.retro.common.Application;
import cz.pecina.retro.cpu.Processor;
import cz.pecina.retro.cpu.Opcode;
import cz.pecina.retro.gui.GUI;

/**
 * Tesla PMD 85 control object.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Computer {

  // static logger
  private static final Logger log =
    Logger.getLogger(Computer.class.getName());

  // debugger button autorepeat constant
  private static int AUTOREPEAT = Math.round(200f / Constants.TIMER_PERIOD);

  // the computer hardware object
  private ComputerHardware computerHardware;

  // the computer frame
  private ComputerFrame computerFrame;

  // the settings frame
  private SettingsFrame settingsFrame;

  // the memory frame
  private MemoryFrame memoryFrame;

  // the keyboard frame
  private KeyboardFrame keyboardFrame;

  // the peripherals frame
  private PeripheralsFrame peripheralsFrame;

  // the tape recorder frame
  private TapeRecorderFrame tapeRecorderFrame;

  // the debugger frame
  private DebuggerFrame debuggerFrame;

  // the About frame
  // private AboutFrame aboutFrame;

  // the icon layout
  private IconLayout iconLayout;

  // the reset button
  private KeyboardKey resetKey;

  // the shift buttons
  private KeyboardKey leftShiftKey, rightShiftKey;

  // debugger states
  private enum DebuggerState {HIDDEN, RUNNING, STOPPED};

  // debugger state
  private DebuggerState debuggerState = DebuggerState.HIDDEN;

  // state of debugger buttons
  private boolean interruptButtonPressed, runStopButtonPressed;
  private int stepInButtonCounter, stepOverButtonCounter;

  // empty list of breakpoints
  private static final List<Integer> noBreakpoints = new ArrayList<>();

  // true if run() running
  private boolean busy;

  /**
   * Creates a new computer control object.
   */
  public Computer() {
    log.fine("New Computer creation started");

    // set up the computer hardware
    computerHardware = new ComputerHardware();

    // set up the icons
    iconLayout = new IconLayout(this);

    // set up the frames
    computerFrame =
      new ComputerFrame(this,
    			computerHardware.getDisplayHardware(),
    			computerHardware.getKeyboardHardware()
			);
    memoryFrame = new MemoryFrame(this, computerHardware.getHardware());
    keyboardFrame = new KeyboardFrame(this, computerHardware.getKeyboardHardware());
    tapeRecorderFrame = new TapeRecorderFrame(
      this,
      computerHardware.getTapeRecorderHardware());
    debuggerFrame = new DebuggerFrame(
      this,
      computerHardware.getDebuggerHardware());
    peripheralsFrame = new PeripheralsFrame(this, computerHardware);
    settingsFrame = new SettingsFrame(this, peripheralsFrame.getPeripherals());
    // aboutFrame = new AboutFrame(this);

    // find reset and shift keys
    for (KeyboardKey key: computerHardware.getKeyboardHardware()
	   .getKeyboardLayout().getKeys()) {
      if (key.isReset()) {
	resetKey = key;
      }
      if (key.isShift()) {
	if (leftShiftKey == null) {
	  leftShiftKey = key;
	} else {
	  rightShiftKey = key;
	}
      }
    }

    // set the model and reset all stateful hardware
    computerHardware.setModel(this, UserPreferences.getModel());

    // start emulation
    new Timer(Constants.TIMER_PERIOD, new TimerListener()).start();

    log.fine("New Computer created");
  }

  // timer listener
  private class TimerListener implements ActionListener {
    @Override
    public void actionPerformed(final ActionEvent event) {
      run();
    }
  };

  /**
   * Gets the computer hardware.
   *
   * @return the computer hardware
   */
  public ComputerHardware getComputerHardware() {
    return computerHardware;
  }

  /**
   * Gets the icon layout.
   *
   * @return the icon layout
   */
  public IconLayout getIconLayout() {
    return iconLayout;
  }

  /**
   * Gets the computer frame.
   *
   * @return the computer frame
   */
  public ComputerFrame getComputerFrame() {
    return computerFrame;
  }

  /**
   * Gets the keyboard frame.
   *
   * @return the keyboard frame
   */
  public KeyboardFrame getKeyboardFrame() {
    return keyboardFrame;
  }

  /**
   * Stops the debugger.
   */
  public void debuggerStop() {
    debuggerState = DebuggerState.STOPPED;
  }

  /**
   * Hides the debugger.
   */
  public void debuggerHide() {
    debuggerState = DebuggerState.HIDDEN;
  }

  // the main emulation method
  private void run() {
	
    if (busy) {
      log.fine("Processing took too long, timer event dismissed");
      return;
    }
    busy = true;

    switch (debuggerState) {
      case HIDDEN:
    	computerHardware.getDebuggerHardware().removeTemporaryBreakpoints();
    	stepInButtonCounter = stepOverButtonCounter = 0;
    	runStopButtonPressed = false;
    	if (resetKey.isPressed() &&
	    (leftShiftKey.isPressed() || rightShiftKey.isPressed()))  {
    	  // computerHardware.getCPU().requestReset();
    	  // computerHardware.getSystemPIO().reset();
    	  // computerHardware.getMemory().reset();
	  computerHardware.reset();
    	  // computerHardware.getPeripheralPPI().reset();
    	  // computerHardware.getDisplayHardware().display();
    	  break;
    	}
    	// if (interruptButton.isPressed()) {
    	//   if (!interruptButtonPressed) {
    	//     computerHardware.getCPU().requestInterrupt(7);
    	//     interruptButtonPressed = true;
    	//     break;
    	//   }
    	// } else {
    	//   interruptButtonPressed = false;
	// }
    	computerHardware.getCPU().exec(
          Parameters.timerCycles * Parameters.speedUp,
  	  0,
  	  noBreakpoints);
    	// computerHardware.getDisplayHardware().display();
    	break;
      case STOPPED:
    	computerHardware.getDebuggerHardware().removeTemporaryBreakpoints();
    	// computerHardware.getDisplayHardware().displayImmediate();
    	computerHardware.getDebuggerHardware().update();
    	if (resetKey.isPressed() &&
	    (leftShiftKey.isPressed() || rightShiftKey.isPressed()))  {
    	  // computerHardware.getCPU().reset();
    	  // computerHardware.getSystemPIO().reset();
    	  // computerHardware.getMemory().reset();
	  computerHardware.reset();
    	  // computerHardware.getPeripheralPPI().reset();
    	  computerHardware.getDebuggerHardware().activate();
    	  break;
    	}
    	// if (interruptButton.isPressed()) {
    	//   if (!interruptButtonPressed) {
    	//     computerHardware.getCPU().interrupt(7);
    	//     interruptButtonPressed = true;
    	//     computerHardware.getDebuggerHardware().activate();
    	//     break;
    	//   }
    	// } else {
    	//   interruptButtonPressed = false;
    	// }
    	if (computerHardware.getDebuggerHardware().stepInButton.isPressed()) {
    	  if (stepInButtonCounter == 0) {
    	    computerHardware.getCPU().exec(1, 0, noBreakpoints);
    	    computerHardware.getDebuggerHardware().activate();
    	    stepInButtonCounter = AUTOREPEAT;
    	    break;
    	  } else {
    	    stepInButtonCounter--;
    	  }
    	} else {
    	  stepInButtonCounter = 0;
    	}
    	if (computerHardware.getDebuggerHardware().stepOverButton.isPressed()) {
    	  if (stepOverButtonCounter == 0) {
    	    final int pc = computerHardware.getCPU().getPC();
    	    final Opcode opcode = computerHardware.getCPU()
    	      .getOpcode(computerHardware.getMemory().getByte(pc));
    	    if ((opcode.getType() & Processor.INS_CALL) == 0) {
    	      computerHardware.getCPU().exec(1, 0, noBreakpoints);
    	      computerHardware.getDebuggerHardware().activate();
    	    } else {
    	      computerHardware.getDebuggerHardware().getBreakpointValues()
    		.add((pc + opcode.getLength()) & 0xffff);
    	      computerHardware.getDebuggerHardware().deactivate();
    	      debuggerState = DebuggerState.RUNNING;
    	    }
    	    stepOverButtonCounter = AUTOREPEAT;
    	    break;
    	  } else {
    	    stepOverButtonCounter--;
    	  }
    	} else {
    	  stepOverButtonCounter = 0;
    	}
    	if (computerHardware.getDebuggerHardware().runStopButton.isPressed()) {
    	  if (!runStopButtonPressed) {
    	    computerHardware.getDebuggerHardware().deactivate();
    	    debuggerState = DebuggerState.RUNNING;
    	    runStopButtonPressed = true;
    	    break;
    	  }
    	} else {
    	  runStopButtonPressed = false;
    	}
    	break;
      case RUNNING:
    	// computerHardware.getDisplayHardware().reset();
    	if (resetKey.isPressed() &&
	    (leftShiftKey.isPressed() || rightShiftKey.isPressed()))  {
    	  // computerHardware.getCPU().requestReset();
    	  // computerHardware.getSystemPIO().reset();
    	  // computerHardware.getMemory().reset();
	  computerHardware.reset();
    	  // computerHardware.getPeripheralPPI().reset();
    	  // computerHardware.getDisplayHardware().display();
    	  break;
    	}
    	// if (interruptButton.isPressed()) {
    	//   if (!interruptButtonPressed) {
    	//     computerHardware.getCPU().requestInterrupt(7);
    	//     interruptButtonPressed = true;
    	//     break;
    	//   }
    	// } else {
    	//   interruptButtonPressed = false;
    	// }
    	if (computerHardware.getDebuggerHardware().runStopButton.isPressed()) {
    	  if (!runStopButtonPressed) {
    	    computerHardware.getDebuggerHardware().activate();
    	    debuggerState = DebuggerState.STOPPED;
    	    runStopButtonPressed = true;
    	    break;
    	  }
    	} else {
    	  runStopButtonPressed = false;
    	}
    	computerHardware.getCPU().exec(Parameters.timerCycles, 0,
    	  computerHardware.getDebuggerHardware().getBreakpointValues());
    	// computerHardware.getDisplayHardware().display();
    	if (computerHardware.getDebuggerHardware().getBreakpointValues()
    	    .contains(computerHardware.getCPU().getPC())) {
    	  computerHardware.getDebuggerHardware().activate();
    	  debuggerState = DebuggerState.STOPPED;
    	}
    	break;
    }
    computerHardware.getTapeRecorderHardware().process();
    computerHardware.getKeyboardHardware().updateBuffer();
    computerHardware.getDisplayHardware().repaint();
	
    busy = false;
  }
}
