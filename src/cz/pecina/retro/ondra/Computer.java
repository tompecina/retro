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

package cz.pecina.retro.ondra;

import java.util.logging.Logger;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.Timer;

import cz.pecina.retro.common.Parameters;
import cz.pecina.retro.common.Application;

import cz.pecina.retro.cpu.Processor;
import cz.pecina.retro.cpu.Opcode;

import cz.pecina.retro.gui.GUI;

/**
 * Tesla Ondra SPO 186 control object.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Computer implements Runnable {

  // static logger
  private static final Logger log =
    Logger.getLogger(Computer.class.getName());

  // the computer hardware object
  private ComputerHardware computerHardware;

  // the computer frame
  private ComputerFrame computerFrame;

  // the settings frame
  private SettingsFrame settingsFrame;

  // the reset frame
  private ResetFrame resetFrame;

  // the memory frame
  private MemoryFrame memoryFrame;

  // the keyboard frame
  private KeyboardFrame keyboardFrame;

  // the joystick frame
  private JoystickFrame joystickFrame;

  // the peripherals frame
  private PeripheralsFrame peripheralsFrame;

  // the tape recorder frame
  private TapeRecorderFrame tapeRecorderFrame;

  // the About frame
  // private AboutFrame aboutFrame;

  // the icon layout
  private IconLayout iconLayout;

  // true if run() running
  private boolean busy;

  // the CPU
  private Processor cpu;

  /**
   * Creates a new computer control object.
   */
  public Computer() {
    log.fine("New Computer creation started");
    
    // set up the computer hardware
    computerHardware = new ComputerHardware();
    cpu = computerHardware.getCPU();

    // set up the icons
    iconLayout = new IconLayout(this);

    // set up the frames
    computerFrame =
      new ComputerFrame(this,
    			computerHardware.getDisplayHardware(),
    			computerHardware.getKeyboardHardware()
			);
    // memoryFrame = new MemoryFrame(this, computerHardware.getHardware());
    keyboardFrame =
      new KeyboardFrame(this, computerHardware.getKeyboardHardware());
    joystickFrame =
      new JoystickFrame(this);
    tapeRecorderFrame = new TapeRecorderFrame(
      this,
      computerHardware.getTapeRecorderHardware());
    peripheralsFrame = new PeripheralsFrame(this, computerHardware);
    settingsFrame = new SettingsFrame(this, peripheralsFrame.getPeripherals());
    resetFrame = new ResetFrame(this, computerHardware.getHardware());
    // aboutFrame = new AboutFrame(this);

    // set the ROM version and reset all stateful hardware
    computerHardware.setVersion(this, UserPreferences.getVersion());

    // start emulation
    new Timer(Parameters.timerPeriod, new TimerListener()).start();

    log.fine("New Computer created");
  }

  // timer listener
  private class TimerListener implements ActionListener {

    // for description see ActionListener
    @Override
    public void actionPerformed(final ActionEvent event) {
      run();
    }
  };

  // the main emulation method
  public void run() {
	
    if (busy) {
      log.fine("Processing took too long, timer event dismissed");
      Parameters.sound.update();
      return;
    }
    busy = true;

    final long cycles =
      (312 - (computerHardware.getDisplayHardware().getEnableFlag() ?
	      computerHardware.getDisplayHardware().getScanLines() :
	      0))
      * 128;
    cpu.requestInterrupt(0);
    cpu.exec(cycles * Parameters.speedUp, 0, null);
    cpu.idle((Parameters.timerCycles - cycles) * Parameters.speedUp);

    // switch (debuggerState) {
    //   case HIDDEN:
    // 	computerHardware.getDebuggerHardware().removeTemporaryBreakpoints();
    // 	stepInButtonCounter = stepOverButtonCounter = 0;
    // 	runStopButtonPressed = false;
    // 	if (resetKey.isPressed() &&
    // 	    (leftShiftKey.isPressed() || rightShiftKey.isPressed()))  {
    // 	  // computerHardware.getCPU().requestReset();
    // 	  // computerHardware.getSystemPIO().reset();
    // 	  // computerHardware.getMemory().reset();
    // 	  computerHardware.reset();
    // 	  // computerHardware.getPeripheralPPI().reset();
    // 	  // computerHardware.getDisplayHardware().display();
    // 	  break;
    // 	}
    // 	// if (interruptButton.isPressed()) {
    // 	//   if (!interruptButtonPressed) {
    // 	//     computerHardware.getCPU().requestInterrupt(7);
    // 	//     interruptButtonPressed = true;
    // 	//     break;
    // 	//   }
    // 	// } else {
    // 	//   interruptButtonPressed = false;
    // 	// }
    // 	computerHardware.getCPU().exec(
    //       Parameters.timerCycles * Parameters.speedUp,
    // 	  0,
    // 	  null);
    // 	// computerHardware.getDisplayHardware().display();
    // 	break;
    //   case STOPPED:
    // 	computerHardware.getDebuggerHardware().removeTemporaryBreakpoints();
    // 	// computerHardware.getDisplayHardware().displayImmediate();
    // 	computerHardware.getDebuggerHardware().update();
    // 	if (resetKey.isPressed() &&
    // 	    (leftShiftKey.isPressed() || rightShiftKey.isPressed()))  {
    // 	  // computerHardware.getCPU().reset();
    // 	  // computerHardware.getSystemPIO().reset();
    // 	  // computerHardware.getMemory().reset();
    // 	  computerHardware.reset();
    // 	  // computerHardware.getPeripheralPPI().reset();
    // 	  computerHardware.getDebuggerHardware().activate();
    // 	  break;
    // 	}
    // 	// if (interruptButton.isPressed()) {
    // 	//   if (!interruptButtonPressed) {
    // 	//     computerHardware.getCPU().interrupt(7);
    // 	//     interruptButtonPressed = true;
    // 	//     computerHardware.getDebuggerHardware().activate();
    // 	//     break;
    // 	//   }
    // 	// } else {
    // 	//   interruptButtonPressed = false;
    // 	// }
    // 	if (computerHardware.getDebuggerHardware().stepInButton.isPressed()) {
    // 	  if (stepInButtonCounter == 0) {
    // 	    computerHardware.getCPU().exec();
    // 	    computerHardware.getDebuggerHardware().activate();
    // 	    stepInButtonCounter = AUTOREPEAT;
    // 	    break;
    // 	  } else {
    // 	    stepInButtonCounter--;
    // 	  }
    // 	} else {
    // 	  stepInButtonCounter = 0;
    // 	}
    // 	if (computerHardware.getDebuggerHardware().stepOverButton.isPressed()) {
    // 	  if (stepOverButtonCounter == 0) {
    // 	    final int pc = computerHardware.getCPU().getPC();
    // 	    final Opcode opcode = computerHardware.getCPU()
    // 	      .getOpcode(computerHardware.getMemory().getByte(pc));
    // 	    if ((opcode.getType() & Processor.INS_CALL) == 0) {
    // 	      computerHardware.getCPU().exec();
    // 	      computerHardware.getDebuggerHardware().activate();
    // 	    } else {
    // 	      computerHardware.getDebuggerHardware().getBreakpointValues()
    // 		.add((pc + opcode.getLength()) & 0xffff);
    // 	      computerHardware.getDebuggerHardware().deactivate();
    // 	      debuggerState = DebuggerState.RUNNING;
    // 	    }
    // 	    stepOverButtonCounter = AUTOREPEAT;
    // 	    break;
    // 	  } else {
    // 	    stepOverButtonCounter--;
    // 	  }
    // 	} else {
    // 	  stepOverButtonCounter = 0;
    // 	}
    // 	if (computerHardware.getDebuggerHardware().runStopButton.isPressed()) {
    // 	  if (!runStopButtonPressed) {
    // 	    computerHardware.getDebuggerHardware().deactivate();
    // 	    debuggerState = DebuggerState.RUNNING;
    // 	    runStopButtonPressed = true;
    // 	    break;
    // 	  }
    // 	} else {
    // 	  runStopButtonPressed = false;
    // 	}
    // 	break;
    //   case RUNNING:
    // 	// computerHardware.getDisplayHardware().reset();
    // 	if (resetKey.isPressed() &&
    // 	    (leftShiftKey.isPressed() || rightShiftKey.isPressed()))  {
    // 	  // computerHardware.getCPU().requestReset();
    // 	  // computerHardware.getSystemPIO().reset();
    // 	  // computerHardware.getMemory().reset();
    // 	  computerHardware.reset();
    // 	  // computerHardware.getPeripheralPPI().reset();
    // 	  // computerHardware.getDisplayHardware().display();
    // 	  break;
    // 	}
    // 	// if (interruptButton.isPressed()) {
    // 	//   if (!interruptButtonPressed) {
    // 	//     computerHardware.getCPU().requestInterrupt(7);
    // 	//     interruptButtonPressed = true;
    // 	//     break;
    // 	//   }
    // 	// } else {
    // 	//   interruptButtonPressed = false;
    // 	// }
    // 	if (computerHardware.getDebuggerHardware().runStopButton.isPressed()) {
    // 	  if (!runStopButtonPressed) {
    // 	    computerHardware.getDebuggerHardware().activate();
    // 	    debuggerState = DebuggerState.STOPPED;
    // 	    runStopButtonPressed = true;
    // 	    break;
    // 	  }
    // 	} else {
    // 	  runStopButtonPressed = false;
    // 	}
    // 	computerHardware.getCPU().exec(Parameters.timerCycles, 0,
    // 	  computerHardware.getDebuggerHardware().getBreakpointValues());
    // 	// computerHardware.getDisplayHardware().display();
    // 	if (computerHardware.getDebuggerHardware().getBreakpointValues()
    // 	    .contains(computerHardware.getCPU().getPC())) {
    // 	  computerHardware.getDebuggerHardware().activate();
    // 	  debuggerState = DebuggerState.STOPPED;
    // 	}
    // 	break;
    // }
    computerHardware.getTapeRecorderHardware().process();
    Parameters.sound.update();
    computerHardware.getKeyboardHardware().update();
    computerHardware.getDisplayHardware().refresh();

    // // update LEDs
    // final float yellowLEDState =
    //   (float)computerHardware.getYellowLEDMeter().getProportionAndReset();
    // computerHardware.getYellowLED().setState(yellowLEDState);
    // computerHardware.getKeyboardHardware().getYellowLED()
    //   .setState(yellowLEDState);
    // final float redLEDState =
    //   (float)computerHardware.getRedLEDMeter().getProportionAndReset();
    // computerHardware.getRedLED().setState(redLEDState);
    // computerHardware.getKeyboardHardware().getRedLED()
    //   .setState(redLEDState);
    
    busy = false;
  }

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
}
