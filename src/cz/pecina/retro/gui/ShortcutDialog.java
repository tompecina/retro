/* ShortcutDialog.java
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

package cz.pecina.retro.gui;

import java.util.logging.Logger;

import java.util.List;

import java.awt.Frame;
import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Color;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;

import cz.pecina.retro.common.Application;

/**
 * Shortcut selection dialog box.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class ShortcutDialog extends JDialog {

  // static logger
  private static final Logger log =
    Logger.getLogger(ShortcutDialog.class.getName());

  // selected shortcut
  private Shortcut shortcut;

  // new shortcut
  private Shortcut newShortcut;

  // set button
  private JButton setButton;
  
  // dynamically modified labels
  final JLabel promptLabel, shortcutLabel;
  
  /**
   * Displays a shortcut selection dialog and returns the result.
   *
   * @param  frame             enclosing frame
   * @param  currentShortcut   shortcut curently assigned to this key or
   *                           {@code null} if none
   * @param  assignedShortcuts list of shortcuts already assigned
   * @return                   the shortcut or {@code null} if aborted
   */
  public static Shortcut getShortcut(
      final Frame frame,
      final Shortcut currentShortcut,
      final List<Shortcut> assignedShortcuts) {
    log.fine("New ShortcutDialog creation started");
    final ShortcutDialog dialog =
      new ShortcutDialog(frame, currentShortcut, assignedShortcuts);
    return dialog.newShortcut;
  }

  // private constructor
  private ShortcutDialog(final Frame frame,
			 final Shortcut currentShortcut,
			 final List<Shortcut> assignedShortcuts) {
    super(frame,
	  Application.getString(ShortcutDialog.class, "shortcutDialog.title"),
	  true);

    final JPanel dialogPanel = new JPanel(new BorderLayout());

    final JPanel promptPanel = new JPanel();
    promptPanel.setLayout(new BoxLayout(promptPanel, BoxLayout.PAGE_AXIS));
    promptPanel.setBorder(BorderFactory.createEmptyBorder(35, 5, 35, 5));

    shortcutLabel = new JLabel();
    shortcutLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    shortcutLabel.setForeground(Color.BLUE);
    shortcutLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));
    promptPanel.add(shortcutLabel);

    promptLabel = new JLabel(Application.getString(ShortcutDialog.class,
						   "shortcutDialog.prompt"));
    promptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    promptPanel.add(promptLabel);

    dialogPanel.add(promptPanel);

    final JPanel buttonsPanel = new JPanel(new FlowLayout());
    setButton =
      new JButton(Application.getString(ShortcutDialog.class,
					"shortcutDialog.button.set"));
    setButton.addActionListener(new SetListener());
    setButton.setEnabled(false);
    buttonsPanel.add(setButton);
    final JButton cancelButton =
      new JButton(Application.getString(ShortcutDialog.class,
					"shortcutDialog.button.cancel"));
    cancelButton.addActionListener(new CancelListener());
    buttonsPanel.add(cancelButton);
    dialogPanel.add(buttonsPanel, BorderLayout.PAGE_END);

    add(dialogPanel);

    setMinimumSize(new Dimension(480, 110));
    setLocationRelativeTo(frame);
    setFocusable(true);
    addKeyListener(new ShortcutListener(currentShortcut, assignedShortcuts));
    pack();
    setVisible(true);
    log.fine("ShortcutDialog set up");
  }

  // shortcut listener
  private class ShortcutListener extends KeyAdapter {
    private Shortcut currentShortcut;
    private List<Shortcut> assignedShortcuts;
    @Override
    public void keyPressed(final KeyEvent event) {
      log.finer("Key event detected:" + event);
      shortcut =
	new Shortcut(event.getExtendedKeyCode(), event.getKeyLocation());
      shortcutLabel.setText(shortcut.getDesc());
      String promptKey;
      if ((currentShortcut != null) && currentShortcut.equals(shortcut)) {
	promptKey = "current";
      } else if (assignedShortcuts.contains(shortcut)) {
	promptKey = "assigned";
      } else {
	promptKey = "available";
      }
      promptLabel.setText(Application.getString(ShortcutDialog.class,
						"shortcutDialog." + promptKey));
      setButton.setEnabled((currentShortcut == null) || !currentShortcut.equals(shortcut));
      event.consume();
    }
	private ShortcutListener(final Shortcut currentShortcut,
				 final List<Shortcut> assignedShortcuts) {
      this.assignedShortcuts = assignedShortcuts;
      this.currentShortcut = currentShortcut;
    }
  }

  // set button listener
  private class SetListener implements ActionListener {

    // for description see ActionListener
    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Set button event detected");
      newShortcut = shortcut;
      dispose();
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
