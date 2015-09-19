/* KeyChooserDialog.java
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

import java.util.Set;
import java.util.NavigableSet;

import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.BorderFactory;

import cz.pecina.retro.common.Application;

import cz.pecina.retro.gui.Resizeable;
import cz.pecina.retro.gui.GUI;
import cz.pecina.retro.gui.InfoBox;

/**
 * The key chooser frame.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class KeyChooserDialog extends JDialog {

  // static logger
  private static final Logger log =
    Logger.getLogger(KeyChooserDialog.class.getName());

  // key chooser panel
  private KeyChooserPanel keyChooserPanel;
    
  /**
   * Displays a key chooser dialog and returns the result.
   *
   * @param  frame    enclosing frame
   * @param  computer the computer control object
   * @param  title    the title of the dialog window
   * @param  keys     set of key numbers curently assigned to this shortcut
   * @return          new set of key numbers assigned to this shortcut
   */
  public static NavigableSet<Integer> getKeys(final Frame frame,
					   final Computer computer,
					   final String title,
					   final Set<Integer> keys) {
    log.fine("New KeyChooserDialog creation started");
    assert frame != null;
    assert computer != null;
    assert title != null;
    assert keys != null;
    final KeyChooserDialog dialog =
      new KeyChooserDialog(frame, computer, title, keys);
    return dialog.getKeys();
  }

  // private constructor
  public KeyChooserDialog(final Frame frame,
			  final Computer computer,
			  final String title,
			  final Set<Integer> keys) {
    super(frame, title, true);
    log.fine("New KeyChooserDialog creation started");

    final JPanel dialogPanel = new JPanel(new BorderLayout());
    
    keyChooserPanel = new KeyChooserPanel(
      frame,
      computer.getComputerHardware().getKeyboardHardware().getKeyboardLayout(),
      keys);
    dialogPanel.add(keyChooserPanel);

    final JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
    buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    final JButton setButton =
      new JButton(Application.getString(
        KeyChooserDialog.class, "settings.keyboard.keyChooser.button.set"));
    setButton.addActionListener(new SetListener());
    buttonsPanel.add(setButton);
    final JButton cancelButton =
      new JButton(Application.getString(
        KeyChooserDialog.class, "settings.keyboard.keyChooser.button.cancel"));
    cancelButton.addActionListener(new CancelListener());
    buttonsPanel.add(cancelButton);
    dialogPanel.add(buttonsPanel, BorderLayout.PAGE_END);

    add(dialogPanel);

    setLocationRelativeTo(frame);
    pack();
    setVisible(true);
    log.fine("KeyChooserDialog set up");
  }

  // get selected keys
  private NavigableSet<Integer> getKeys() {
    return keyChooserPanel.getKeys();
  }

  // set button listener
  private class SetListener implements ActionListener {

    // for description see ActionListener
    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Set button event detected");
      if (getKeys().isEmpty()) {
	InfoBox.display(keyChooserPanel, Application.getString(
          KeyChooserDialog.this, "settings.keyboard.keyChooser.noKey"));
      } else {
	dispose();
      }
    }
  }
  
  // cancel button listener
  private class CancelListener implements ActionListener {

    // for description see ActionListener
    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Cancel button event detected");
      dispose();
    }
  }
}
