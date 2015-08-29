/* SettingsKeyboardPanel.java
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

import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.Dimension;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JButton;

import cz.pecina.retro.common.Application;

import cz.pecina.retro.gui.IconCache;
import cz.pecina.retro.gui.Shortcut;
import cz.pecina.retro.gui.ShortcutDialog;

/**
 * The Settings/Keyboard panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class SettingsKeyboardPanel extends JScrollPane {
  
  // static logger
  private static final Logger log =
    Logger.getLogger(SettingsKeyboardPanel.class.getName());
    
  // the enclosing frame
  private Frame frame;

  // computer object
  private Computer computer;

  // tentative map of shortcuts
  private Shortcut[] shortcuts =
    new Shortcut[KeyboardLayout.NUMBER_KEYS];

  // dynamically updated labels
  private JLabel[] shortcutLabels =
    new JLabel[KeyboardLayout.NUMBER_KEYS];

  // convert shortcut to text description
  private String shortcutToText(final Shortcut shortcut) {
    return (shortcut != null) ?
            shortcut.getDesc() :
            Application.getString(this, "settings.keyboard.noShortcut");
  }
  
  /**
   * Creates the Settings/Keyboard panel.
   *
   * @param frame    the controlling frame
   * @param computer the computer object
   */
  public SettingsKeyboardPanel(final Frame frame, final Computer computer) {
    super(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
	  ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);




    new KeyChooserDialog(frame, computer, new ArrayList<Integer>());





    log.fine("New Settings/Keyboard panel creation started");
    assert frame != null;
    assert computer != null;
    
    this.frame = frame;
    this.computer = computer;

    final JPanel shortcutsPane = new JPanel(new GridBagLayout());

    setViewportBorder(BorderFactory.createEmptyBorder(5, 8, 0, 8));
  
    final GridBagConstraints restorePaneConstraints =
      new GridBagConstraints();
    final JButton restoreButton = new JButton(Application
      .getString(this, "settings.keyboard.button.restore"));
    restorePaneConstraints.gridx = 0;
    restorePaneConstraints.gridy = 0;
    restorePaneConstraints.gridwidth = GridBagConstraints.REMAINDER;
    restorePaneConstraints.insets = new Insets(15, 0, 10, 0);
    restorePaneConstraints.anchor = GridBagConstraints.CENTER;
    restorePaneConstraints.weightx = 0.0;
    restorePaneConstraints.weighty = 0.0;
    final JPanel restorePane = new JPanel(new BorderLayout());
    restoreButton.setPreferredSize(new Dimension(440, 35));
    restorePane.add(restoreButton);
    shortcutsPane.add(restorePane, restorePaneConstraints);
    restoreButton.addActionListener(new RestoreListener());

    for (int i = 0; i < KeyboardLayout.NUMBER_KEYS; i++) {
      final int line = i + 1;
      final KeyboardKey key = computer.getComputerHardware()
	.getKeyboardHardware().getKeyboardLayout().getKey(i);

      final GridBagConstraints keyConstraints =
	new GridBagConstraints();
      final JLabel keyLabel = new JLabel(IconCache.get(
	String.format(key.getTemplate(), 2, "u")));
      keyConstraints.gridx = 0;
      keyConstraints.gridy = line;
      keyConstraints.insets = new Insets(10, 0, 10, 0);
      keyConstraints.anchor = GridBagConstraints.LINE_END;
      keyConstraints.weightx = 0.4;
      keyConstraints.weighty = 0.0;
      shortcutsPane.add(keyLabel, keyConstraints);
      
      final GridBagConstraints shortcutConstraints =
	new GridBagConstraints();
      shortcutLabels[i] = new JLabel();
      shortcutConstraints.gridx = 1;
      shortcutConstraints.gridy = line;
      shortcutConstraints.insets = new Insets(0, 10, 0, 10);
      shortcutConstraints.anchor = GridBagConstraints.CENTER;
      shortcutConstraints.weightx = 0.0;
      shortcutConstraints.weighty = 0.0;
      shortcutsPane.add(shortcutLabels[i], shortcutConstraints);
	
      final GridBagConstraints changeButtonConstraints =
	new GridBagConstraints();
      final JButton changeButton = new JButton(Application
	.getString(this, "settings.keyboard.button.change"));
      changeButtonConstraints.gridx = 2;
      changeButtonConstraints.gridy = line;
      changeButtonConstraints.insets = new Insets(0, 0, 0, 7);
      changeButtonConstraints.anchor = GridBagConstraints.LINE_START;
      changeButtonConstraints.weightx = 0.0;
      changeButtonConstraints.weighty = 0.0;
      shortcutsPane.add(changeButton, changeButtonConstraints);
      changeButton.addActionListener(new ChangeListener(i));

      final GridBagConstraints clearButtonConstraints =
	new GridBagConstraints();
      final JButton clearButton = new JButton(Application
	.getString(this, "settings.keyboard.button.clear"));
      clearButtonConstraints.gridx = 3;
      clearButtonConstraints.gridy = line;
      clearButtonConstraints.insets = new Insets(0, 0, 0, 10);
      clearButtonConstraints.anchor = GridBagConstraints.LINE_START;
      clearButtonConstraints.weightx = 0.6;
      clearButtonConstraints.weighty = 0.0;
      shortcutsPane.add(clearButton, clearButtonConstraints);
      clearButton.addActionListener(new ClearListener(i));
    }

    getVerticalScrollBar().setUnitIncrement(16);
    setViewportView(shortcutsPane);
    setPreferredSize(new Dimension(0, 300));
    
    log.fine("Settings/Keyboard panel set up");
  }

  /**
   * Initialize widgets.
   */
  public void setUp() {
    for (int i = 0; i < KeyboardLayout.NUMBER_KEYS; i++) {
      shortcuts[i] = UserPreferences.getShortcut(i);
      shortcutLabels[i].setText(shortcutToText(shortcuts[i]));
    }
    log.fine("Widgets initialized");
  }

  // partial set listener
  private class SetListener implements ActionListener {
    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Set button event detected");
      for (int i = 0; i < KeyboardLayout.NUMBER_KEYS; i++) {
	UserPreferences.setShortcut(
	  computer.getComputerHardware().getKeyboardHardware()
	    .getKeyboardLayout(),
	  i,
	  shortcuts[i]);
      }
      computer.getComputerFrame().getComputerPanel().setShortcuts();
      log.fine("Partial changes implemented");
    }
  }

  /**
   * Partial set listener factory method.
   *
   * @return new partial set listener
   */
  public SetListener createSetListener() {
    log.finer("Partial set listener created");
    return new SetListener();
  }

  // change button listener
  private class ChangeListener implements ActionListener {
    private int number;

    private ChangeListener(final int number) {
      this.number = number;
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Change button event detected");
      final List<Shortcut> list = new ArrayList<>();
      for (int i = 0; i < KeyboardLayout.NUMBER_KEYS; i++) {
	if (shortcuts[i] != null) {
	  list.add(shortcuts[i]);
	}
      }
      final Shortcut shortcut =
	ShortcutDialog.getShortcut(frame, shortcuts[number], list);
      if (shortcut != null) {
	for (int i = 0; i < KeyboardLayout.NUMBER_KEYS; i++) {
	  if (number == i) {
	    shortcuts[i] = shortcut;
	  } else if (shortcut.equals(shortcuts[i])) {
	    shortcuts[i] = null;
	  }
	  shortcutLabels[i].setText(shortcutToText(shortcuts[i]));
	}
      }
    }
  }
  
  // clear button listener
  private class ClearListener implements ActionListener {
    private int number;

    private ClearListener(final int number) {
      this.number = number;
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Clear button event detected");
      shortcuts[number] = null;
      shortcutLabels[number].setText(shortcutToText(null));
    }
  }

  // restore button listener
  private class RestoreListener implements ActionListener {
    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Restore button event detected");
      for (int i = 0; i < KeyboardLayout.NUMBER_KEYS; i++) {
	shortcuts[i] = UserPreferences.getDefaultShortcut(i);
	shortcutLabels[i].setText(shortcutToText(shortcuts[i]));
      }
    }
  }
}
