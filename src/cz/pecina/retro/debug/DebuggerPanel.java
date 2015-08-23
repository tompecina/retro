/* DebuggerPanel.java
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

import javax.swing.JFrame;
import javax.swing.KeyStroke;

import cz.pecina.retro.gui.BackgroundFixedPane;
import cz.pecina.retro.gui.PushButton;
import cz.pecina.retro.gui.Shortcut;

/**
 * The Intel 8080A hardware debugger panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class DebuggerPanel extends BackgroundFixedPane {

  // static logger
  private static final Logger log =
    Logger.getLogger(DebuggerPanel.class.getName());

  // button row geometry
  private static final int DEBUGGER_BUTTON_GRID_X = 36;
  private static final int DEBUGGER_BUTTON_OFFSET_X = 192;
  private static final int DEBUGGER_BUTTON_OFFSET_Y = 114;

  // memory dump row geometry
  private static final int SISD_WIDTH = DebuggerHardware.blockModel.gridX;
  private static final int MEMORY_ADDRESS_OFFSET_X = 6;
  private static final int MEMORY_ADDRESS_OFFSET_Y = 12;
  private static final int MEMORY_DATA_GRID_X = (SISD_WIDTH * 5) / 2;
  private static final int MEMORY_DATA_OFFSET_X =
    MEMORY_ADDRESS_OFFSET_X + (SISD_WIDTH * 5);
  private static final int MEMORY_DATA_OFFSET_Y = MEMORY_ADDRESS_OFFSET_Y;

  // positions of register blocks and look-up buttons
  private static final int REGISTER_A_OFFSET_X = 12;
  private static final int REGISTER_A_OFFSET_Y = 42;
  private static final int REGISTER_BC_OFFSET_X = 54;
  private static final int REGISTER_BC_OFFSET_Y = REGISTER_A_OFFSET_Y;
  private static final int REGISTER_DE_OFFSET_X = 120;
  private static final int REGISTER_DE_OFFSET_Y = REGISTER_A_OFFSET_Y;
  private static final int REGISTER_HL_OFFSET_X = 186;
  private static final int REGISTER_HL_OFFSET_Y = REGISTER_A_OFFSET_Y;
  private static final int REGISTER_SP_OFFSET_X = 252;
  private static final int REGISTER_SP_OFFSET_Y = REGISTER_A_OFFSET_Y;
  private static final int LOOKUP_OFFSET_X = -7;
  private static final int LOOKUP_OFFSET_Y = 0;
  private static final int LOOKUP_BC_OFFSET_X =
    REGISTER_BC_OFFSET_X + LOOKUP_OFFSET_X;
  private static final int LOOKUP_BC_OFFSET_Y =
    REGISTER_BC_OFFSET_Y + LOOKUP_OFFSET_Y;
  private static final int LOOKUP_DE_OFFSET_X =
    REGISTER_DE_OFFSET_X + LOOKUP_OFFSET_X;
  private static final int LOOKUP_DE_OFFSET_Y =
    REGISTER_DE_OFFSET_Y + LOOKUP_OFFSET_Y;
  private static final int LOOKUP_HL_OFFSET_X =
    REGISTER_HL_OFFSET_X + LOOKUP_OFFSET_X;
  private static final int LOOKUP_HL_OFFSET_Y =
    REGISTER_HL_OFFSET_Y + LOOKUP_OFFSET_Y;
  private static final int LOOKUP_SP_OFFSET_X =
    REGISTER_SP_OFFSET_X + LOOKUP_OFFSET_X;
  private static final int LOOKUP_SP_OFFSET_Y =
    REGISTER_SP_OFFSET_Y + LOOKUP_OFFSET_Y;

  // breakpoint row geometry
  private static final int BREAKPOINT_GRID_X = (SISD_WIDTH * 9) / 2;
  private static final int BREAKPOINT_OFFSET_X = 90;
  private static final int BREAKPOINT_OFFSET_Y = 84;

  // position of program counter and disassembly row
  private static final int PROGRAM_COUNTER_OFFSET_X = 6;
  private static final int PROGRAM_COUNTER_OFFSET_Y = 114;
  private static final int DISASSEMBLY_OFFSET_X =
    PROGRAM_COUNTER_OFFSET_X + ((SISD_WIDTH * 9) / 2);
  private static final int DISASSEMBLY_OFFSET_Y = PROGRAM_COUNTER_OFFSET_Y;

  // positions of flag indicators
  private static final int LED_S_OFFSET_X = 5;
  private static final int LED_Z_OFFSET_X = 17;
  private static final int LED_AC_OFFSET_X = 29;
  private static final int LED_P_OFFSET_X = 41;
  private static final int LED_CY_OFFSET_X = 53;
  private static final int LED_IE_OFFSET_X = 71;
  private static final int LED_OFFSET_Y = 70;    

  // enclosing frame
  private JFrame frame;

  // debugger hardware object
  private DebuggerHardware debuggerHardware;

  /**
   * Creates the debugger panel.
   *
   * @param frame            the enclosing frame
   * @param debuggerHardware hardware to operate on
   */
  public DebuggerPanel(final JFrame frame,
		       final DebuggerHardware debuggerHardware) {
    super("debug/DebuggerPanel/mask", "plastic", "darkgray");
    log.fine("New DebuggerPanel creation started");
    this.frame = frame;
    this.debuggerHardware = debuggerHardware;
	    
    // set up buttons
    for (int i = 0; i < debuggerHardware.debuggerButtons.length; i++) {
      final PushButton button = debuggerHardware.debuggerButtons[i];
      button.place(this,
		   (i * DEBUGGER_BUTTON_GRID_X) + DEBUGGER_BUTTON_OFFSET_X,
		   DEBUGGER_BUTTON_OFFSET_Y);
      final Shortcut shortcut = button.getShortcut();
      if (shortcut != null) {
	getInputMap().put(
	  KeyStroke.getKeyStroke(shortcut.getKeyCode(), 0, false),
	  "KeyPressedAction_" + shortcut.getID());
	getActionMap().put("KeyPressedAction_" + shortcut.getID(),
			   button.keyPressedAction());
	getInputMap().put(KeyStroke.getKeyStroke(
	  shortcut.getKeyCode(), 0, true),
	  "KeyReleasedAction_" + shortcut.getID());
	getActionMap().put("KeyReleasedAction_" + shortcut.getID(),
			   button.keyReleasedAction());
      }
    }
    log.finer("Buttons set up");

    // set up memory dump row
    debuggerHardware.memoryAddress.place(this,
					 MEMORY_ADDRESS_OFFSET_X,
					 MEMORY_ADDRESS_OFFSET_Y);
    for (int i = 0; i < DebuggerHardware.NUMBER_MEMORY_DATA; i++) {
      debuggerHardware.memoryData[i].place(
        this,
	(i * MEMORY_DATA_GRID_X) + MEMORY_DATA_OFFSET_X,
	MEMORY_DATA_OFFSET_Y);
    }
    log.finer("Memory dump row set up");
	
    // set up register blocks and look-up buttons
    debuggerHardware.registerA.place(this,
				     REGISTER_A_OFFSET_X,
				     REGISTER_A_OFFSET_Y);
    debuggerHardware.registerBC.place(this,
				      REGISTER_BC_OFFSET_X,
				      REGISTER_BC_OFFSET_Y);
    debuggerHardware.registerDE.place(this,
				      REGISTER_DE_OFFSET_X,
				      REGISTER_DE_OFFSET_Y);
    debuggerHardware.registerHL.place(this,
				      REGISTER_HL_OFFSET_X,
				      REGISTER_HL_OFFSET_Y);
    debuggerHardware.registerSP.place(this,
				      REGISTER_SP_OFFSET_X,
				      REGISTER_SP_OFFSET_Y);
    debuggerHardware.lookUpBCButton.place(this,
					  LOOKUP_BC_OFFSET_X,
					  LOOKUP_BC_OFFSET_Y);
    debuggerHardware.lookUpDEButton.place(this,
					  LOOKUP_DE_OFFSET_X,
					  LOOKUP_DE_OFFSET_Y);
    debuggerHardware.lookUpHLButton.place(this,
					  LOOKUP_HL_OFFSET_X,
					  LOOKUP_HL_OFFSET_Y);
    debuggerHardware.lookUpSPButton.place(this,
					  LOOKUP_SP_OFFSET_X,
					  LOOKUP_SP_OFFSET_Y);
    log.finer("Register blocks and look-up buttons set up");

    // set up breakpoint blocks
    for (int i = 0; i < DebuggerHardware.NUMBER_BREAKPOINTS; i++) {
      debuggerHardware.breakpoints[i].place(
        this,
	(i * BREAKPOINT_GRID_X) + BREAKPOINT_OFFSET_X,
	BREAKPOINT_OFFSET_Y);
    }
    log.finer("Breakpoint blocks set up");

    // set up program counter and disassembly row
    debuggerHardware.programCounter.place(this,
					  PROGRAM_COUNTER_OFFSET_X,
					  PROGRAM_COUNTER_OFFSET_Y);
    debuggerHardware.instructionDisplay.place(this,
					      DISASSEMBLY_OFFSET_X,
					      DISASSEMBLY_OFFSET_Y);
    log.finer("Program counter and disassembly row set up");

    // set up flag indicators
    debuggerHardware.ledS.place(this, LED_S_OFFSET_X, LED_OFFSET_Y);
    debuggerHardware.ledZ.place(this, LED_Z_OFFSET_X, LED_OFFSET_Y);
    debuggerHardware.ledAC.place(this, LED_AC_OFFSET_X, LED_OFFSET_Y);
    debuggerHardware.ledP.place(this, LED_P_OFFSET_X, LED_OFFSET_Y);
    debuggerHardware.ledCY.place(this, LED_CY_OFFSET_X, LED_OFFSET_Y);
    debuggerHardware.ledIE.place(this, LED_IE_OFFSET_X, LED_OFFSET_Y);
    log.finer("Flag indicators set up");

    log.fine("Debugger control panel set up");
  }
}
