/* ComputerPanel.java
 *
 * Copyright (C) 2014-2015, Tomáš Pecina <tomas@pecina.cz>
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
import javax.swing.KeyStroke;
import cz.pecina.retro.gui.BackgroundFixedPane;
import cz.pecina.retro.gui.GenericBitmap;
import cz.pecina.retro.gui.SSD;
import cz.pecina.retro.gui.GUI;

/**
 * Main control panel of the computer.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class ComputerPanel extends BackgroundFixedPane {

  // static logger
  private static final Logger log =
    Logger.getLogger(ComputerPanel.class.getName());
    
  // button matrix geometry
  private static final int BUTTON_GRID_X = 50;
  private static final int BUTTON_GRID_Y = 50;
  private static final int BUTTON_OFFSET_X = 25;
  private static final int BUTTON_OFFSET_Y = 85;

  // SSD geometry
  private static final int SSD_GRID_X = 26;
  private static final int SSD_OFFSET_X = 29;
  private static final int SSD_OFFSET_Y = 28;

  // brand marking position
  private static final int MARKING_OFFSET_X = 11;
  private static final int MARKING_OFFSET_Y = 331;

  // brank marking
  private final Marking marking = new Marking();

  /**
   * Creates the layered panel containing the elements of the main
   * control panel.
   *
   * @param computer         the computer control object
   * @param displayHardware  the display hardware to operate on
   * @param keyboardHardware the keyboard hardware to operate on
   */
  public ComputerPanel(final Computer computer,
		       final DisplayHardware displayHardware,
		       final KeyboardHardware keyboardHardware) {
    super("pmi80/ComputerPanel/mask", "plastic", "gray");
    assert computer != null;
    assert displayHardware != null;
    assert keyboardHardware != null;
    log.fine("New ComputerPanel creation started");

    // set up buttons
    for (int row = 0; row < KeyboardLayout.NUMBER_BUTTON_ROWS; row++)
      for (int column = 0;
	   column < KeyboardLayout.NUMBER_BUTTON_COLUMNS;
	   column++) {
	final KeyboardButton button =
	  keyboardHardware.getKeyboardLayout().getButton(row, column);
	final int shortcut = button.getShortcut();
	if (shortcut != -1) {
	  getInputMap().put(KeyStroke.getKeyStroke(shortcut, 0, false),
			    "KeyPressedAction_" + shortcut);
	  getActionMap().put("KeyPressedAction_" + shortcut,
			     button.keyPressedAction());
	  getInputMap().put(KeyStroke.getKeyStroke(shortcut, 0, true),
			    "KeyReleasedAction_" + shortcut);
	  getActionMap().put("KeyReleasedAction_" + shortcut,
			     button.keyReleasedAction());
	}
	button.place(this,
		     (column * BUTTON_GRID_X) + BUTTON_OFFSET_X,
		     (row * BUTTON_GRID_Y) + BUTTON_OFFSET_Y);
	log.finest("Button '" + button + "' added");
      }
    log.finer("Buttons set up");
	
    // set up display
    for (int column = 0; column < DisplayHardware.NUMBER_SSD; column++) {
      final SSD ssd = displayHardware.getIcon(column);
      ssd.place(this, (column * SSD_GRID_X) + SSD_OFFSET_X, SSD_OFFSET_Y);
    }
    log.finer("Display set up");
	
    // set up brand marking
    marking.place(this, MARKING_OFFSET_X, MARKING_OFFSET_Y);
    log.finer("Brand marking set up");
	
    // set up icons
    for (int i = 0; i < IconLayout.NUMBER_ICONS; i++) {
      final IconButton icon = computer.getIconLayout().getIcon(i);
      icon.place(this, icon.getPositionX(), icon.getPositionY());
    }
    log.finer("Icons set up");

    log.fine("Computer control panel set up");
  }
}
