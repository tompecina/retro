/* TapeRecorderPanel.java
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import cz.pecina.retro.gui.BackgroundFixedPane;

/**
 * The PMD 85 keyboard panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class KeyboardPanel extends BackgroundFixedPane {

  // static logger
  private static final Logger log =
    Logger.getLogger(KeyboardPanel.class.getName());

  // key matrix geometry
  private static final int KEY_OFFSET_X = 1;
  private static final int KEY_OFFSET_Y = 1;
  private static final int KEY_MODULE = 4;
  
  // enclosing frame
  private JFrame frame;

  // keyboard hardware object
  private KeyboardHardware keyboardHardware;

  /**
   * Creates the layered panel containing the keyboard.
   *
   * @param frame            enclosing frame
   * @param keyboardHardware hardware to operate on
   */
  public KeyboardPanel(final JFrame frame,
		       final keyboardHardware keyboardHardware) {
    super("pmd85/KeyboardPanel/mask", "plastic", "gray");
    log.fine("New KeyboardPanel creation started");
    this.frame = frame;
    this.keyboardHardware = keyboardHardware;

    // set up keys
    for (KeyboardKey key: keyboardHardware.getKeyboardLayout().getKeys()) {
      key.place(this,
		key.get
		   BUTTON_OFFSET_Y);
      log.finest("Button '" + button + "' added");
    }
    layout.getButton(TapeRecorderButtonsLayout.BUTTON_POSITION_EJECT)
      .addMouseListener(new EjectListener());
    log.finer("Buttons set up");

    // set up counter
    final Counter counter = tapeRecorderHardware.getCounter();
    for (int i = 0; i < TapeRecorderHardware.NUMBER_COUNTER_DIGITS; i++) {
      final Digit digit = counter.getDigit(i);
      digit.place(this, (i * DIGIT_GRID_X) + DIGIT_OFFSET_X, DIGIT_OFFSET_Y);
      log.finest("Digit " + i + " added");
    }
    final TapeRecorderCounterResetButton reset =
      tapeRecorderHardware.getTapeRecorderCounterResetButton();
    reset.place(this, RESET_OFFSET_X, RESET_OFFSET_Y);
    log.finer("Counter set up");
	
    // set up VU-meter
    final VUMeter vumeter = tapeRecorderHardware.getVUMeter();
    vumeter.place(this, VUMETER_OFFSET_X, VUMETER_OFFSET_Y);
    log.finer("VU-meter set up");

    // set up recording LED
    final BlinkLED recordingLED = tapeRecorderHardware.getRecordingLED();
    recordingLED.place(this, RECLED_OFFSET_X, RECLED_OFFSET_Y);
    log.finer("Recording LED set up");

    log.fine("Tape recorder control panel set up");
  }

  /**
   * Get the enclosing frame.
   *
   * @return the enclosing frame
   */
  public JFrame getFrame() {
    return frame;
  }

  /**
   * Get the tape recorder hardware object.
   *
   * @return tape recorder hardware object
   */
  public TapeRecorderHardware getTapeRecorderHardware() {
    return tapeRecorderHardware;
  }

  // eject button listener
  private class EjectListener extends MouseAdapter {
    @Override
    public void mousePressed(final MouseEvent event) {
      new EjectDialog(KeyboardPanel.this, tapeRecorderHardware);
    }
  }
}
