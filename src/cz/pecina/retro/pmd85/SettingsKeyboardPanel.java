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

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.HashSet;

import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
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
import cz.pecina.retro.gui.Shortcuts;
import cz.pecina.retro.gui.ShortcutDialog;
import cz.pecina.retro.gui.ConfirmationBox;

/**
 * The Settings/Keyboard panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class SettingsKeyboardPanel extends JPanel {
  
  // static logger
  private static final Logger log =
    Logger.getLogger(SettingsKeyboardPanel.class.getName());
    
  // the enclosing frame
  private Frame frame;

  // computer object
  private Computer computer;

  // tentative map of shortcuts
  private Shortcuts shortcuts;

  // // dynamically updated labels
  // private JLabel[] shortcutLabels =
  //   new JLabel[KeyboardLayout.NUMBER_KEYS];

  // // convert shortcut to text description
  // private String shortcutToText(final Shortcut shortcut) {
  //   return (shortcut != null) ?
  //           shortcut.getDesc() :
  //           Application.getString(this, "settings.keyboard.noShortcut");
  // }

  // the scroll pane containing the current shortcuts
  final JScrollPane shortcutsScrollPane;

  // dynamically updated shortcuts pane
  private JPanel shortcutsPane = new JPanel();
  
  /**
   * Creates the Settings/Keyboard panel.
   *
   * @param frame    the controlling frame
   * @param computer the computer object
   */
  public SettingsKeyboardPanel(final Frame frame, final Computer computer) {
    super(new BorderLayout());

    log.fine("New Settings/Keyboard panel creation started");
    assert frame != null;
    assert computer != null;
    
    this.frame = frame;
    this.computer = computer;

    shortcutsScrollPane =
      new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
		      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    shortcutsScrollPane.setViewportBorder(BorderFactory
      .createEmptyBorder(5, 8, 5, 8));
    shortcutsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
    shortcutsScrollPane.setViewportView(shortcutsPane);
    shortcutsScrollPane.setPreferredSize(new Dimension(0, 300));
    add(shortcutsScrollPane);

    final JPanel buttonPanel = new JPanel(new GridBagLayout());
    
    final GridBagConstraints addButtonConstraints =
      new GridBagConstraints();
    final JButton addButton = new JButton(Application
      .getString(this, "settings.keyboard.button.add"));
    addButtonConstraints.gridx = 0;
    addButtonConstraints.gridy = 0;
    addButtonConstraints.insets = new Insets(8, 5, 8, 4);
    addButtonConstraints.anchor = GridBagConstraints.LINE_START;
    addButtonConstraints.weightx = 0.0;
    addButtonConstraints.weighty = 0.0;
    addButton.addActionListener(new AddListener());
    buttonPanel.add(addButton, addButtonConstraints);

    final GridBagConstraints clearButtonConstraints =
      new GridBagConstraints();
    final JButton clearButton = new JButton(Application
      .getString(this, "settings.keyboard.button.clear"));
    clearButtonConstraints.gridx = 1;
    clearButtonConstraints.gridy = 0;
    clearButtonConstraints.insets = new Insets(8, 4, 8, 4);
    clearButtonConstraints.anchor = GridBagConstraints.LINE_END;
    clearButtonConstraints.weightx = 1.0;
    clearButtonConstraints.weighty = 0.0;
    clearButton.addActionListener(new ClearListener());
    buttonPanel.add(clearButton, clearButtonConstraints);

    add(buttonPanel, BorderLayout.PAGE_END);
    
    final GridBagConstraints restoreButtonConstraints =
      new GridBagConstraints();
    final JButton restoreButton = new JButton(Application
      .getString(this, "settings.keyboard.button.restore"));
    restoreButtonConstraints.gridx = 2;
    restoreButtonConstraints.gridy = 0;
    restoreButtonConstraints.insets = new Insets(8, 4, 8, 5);
    restoreButtonConstraints.anchor = GridBagConstraints.LINE_END;
    restoreButtonConstraints.weightx = 0.0;
    restoreButtonConstraints.weighty = 0.0;
    restoreButton.addActionListener(new RestoreListener());
    buttonPanel.add(restoreButton, restoreButtonConstraints);

    log.fine("Settings/Keyboard panel set up");
  }

  // update shortcuts pane
  private void updateShortcutsPane() {
    log.fine("Updating shortcuts pane");
    
    if (shortcuts.isEmpty()) {

      shortcutsPane = new JPanel();

    } else {

      final JPanel shortpane = new JPanel(new GridBagLayout());

      int line = 0;
      
      for (Shortcut shortcut: shortcuts.keySet()) {
    
	final GridBagConstraints shortcutConstraints =
	  new GridBagConstraints();
	final JLabel shortcutLabel = new JLabel();
	shortcutConstraints.gridx = 0;
	shortcutConstraints.gridy = line;
	shortcutConstraints.insets = new Insets(0, 10, 0, 10);
	shortcutConstraints.anchor = GridBagConstraints.LINE_END;
	shortcutConstraints.weightx = 0.5;
	shortcutConstraints.weighty = 0.0;
	shortcutsPane.add(shortcutLabel, shortcutConstraints);

	final JPanel keysPane =
	  new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
	for (int i: shortcuts.get(shortcut)) {
	  final KeyboardKey key = computer.getComputerHardware()
	    .getKeyboardHardware().getKeyboardLayout().getKey(i);
	  final JLabel keyLabel = new JLabel(IconCache.get(
    	    String.format(key.getTemplate(), 2, "u")));
	  keysPane.add(keyLabel);
	}
	final GridBagConstraints keysPaneConstraints =
	  new GridBagConstraints();
	keysPaneConstraints.gridx = 1;
	keysPaneConstraints.gridy = line;
	keysPaneConstraints.insets = new Insets(0, 10, 0, 10);
	keysPaneConstraints.anchor = GridBagConstraints.CENTER;
	keysPaneConstraints.weightx = 0.0;
	keysPaneConstraints.weighty = 0.0;
	shortcutsPane.add(keysPane, keysPaneConstraints);
	
	final GridBagConstraints removeButtonConstraints =
	  new GridBagConstraints();
	final JButton removeButton = new JButton(Application
    	  .getString(this, "settings.keyboard.button.remove"));
	removeButtonConstraints.gridx = 2;
	removeButtonConstraints.gridy = line;
	removeButtonConstraints.insets = new Insets(0, 0, 0, 10);
	removeButtonConstraints.anchor = GridBagConstraints.LINE_START;
	removeButtonConstraints.weightx = 0.5;
	removeButtonConstraints.weighty = 0.0;
	// removeButton.addActionListener(new RemoveListener(shortcut));
	shortcutsPane.add(removeButton, removeButtonConstraints);
      
	line++;
      }
      this.shortcutsPane = shortcutsPane;
    }
    shortcutsScrollPane.setViewportView(this.shortcutsPane);
    
    log.finer("New shortcuts pane updated");
  }

  /**
   * Initialize widgets.
   */
  public void setUp() {
    shortcuts = (Shortcuts)(UserPreferences.getShortcuts().clone());
    updateShortcutsPane();
    log.fine("Widgets initialized");
  }

  // partial set listener
  private class SetListener implements ActionListener {
    @Override
    public void actionPerformed(final ActionEvent event) {
      // log.finer("Set button event detected");
      // for (int i = 0; i < KeyboardLayout.NUMBER_KEYS; i++) {
      // 	UserPreferences.setShortcut(
      // 	  computer.getComputerHardware().getKeyboardHardware()
      // 	    .getKeyboardLayout(),
      // 	  i,
      // 	  shortcuts[i]);
      // }
      // computer.getComputerFrame().getComputerPanel().setShortcuts();
      // log.fine("Partial changes implemented");
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
      // log.finer("Change button event detected");
      // final List<Shortcut> list = new ArrayList<>();
      // for (int i = 0; i < KeyboardLayout.NUMBER_KEYS; i++) {
      // 	if (shortcuts[i] != null) {
      // 	  list.add(shortcuts[i]);
      // 	}
      // }
      // final Shortcut shortcut =
      // 	ShortcutDialog.getShortcut(frame, shortcuts[number], list);
      // if (shortcut != null) {
      // 	for (int i = 0; i < KeyboardLayout.NUMBER_KEYS; i++) {
      // 	  if (number == i) {
      // 	    shortcuts[i] = shortcut;
      // 	  } else if (shortcut.equals(shortcuts[i])) {
      // 	    shortcuts[i] = null;
      // 	  }
      // 	  shortcutLabels[i].setText(shortcutToText(shortcuts[i]));
      // 	}
      // }
    }
  }
  
  // remove button listener
  private class RemoveListener implements ActionListener {
    private int number;

    private RemoveListener(final int number) {
      this.number = number;
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
      // log.finer("Remove button event detected");
      // shortcuts[number] = null;
      // shortcutLabels[number].setText(shortcutToText(null));
    }
  }

  // add button listener
  private class AddListener implements ActionListener {

    // for description see ActionListener
    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Add button event detected");
      final Shortcut shortcut =
	ShortcutDialog.getShortcut(frame, shortcuts.keySet());
      final SortedSet<Integer> keys = KeyChooserDialog.getKeys(
        frame,
	computer,
	String.format(Application.getString(SettingsKeyboardPanel.class,
	  "settings.keyboard.keyChooser.frameTitle"),
	  shortcut.getDesc()),
	(shortcuts.containsKey(shortcut) ?
	 shortcuts.get(shortcut) :
	 new HashSet<Integer>()));
      if ((keys != null) && !keys.isEmpty()) {
	shortcuts.put(shortcut, keys);
	updateShortcutsPane();
      }
    }
  }

  // clear button listener
  private class ClearListener implements ActionListener {

    // for description see ActionListener
    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Clear button event detected");
      if (ConfirmationBox.display(frame, Application.getString(
          SettingsKeyboardPanel.this, "settings.keyboard.confirm.clear"))
	  == 1) {
	shortcuts = new Shortcuts();
	updateShortcutsPane();
      }
    }
  }

  // restore button listener
  private class RestoreListener implements ActionListener {

    // for description see ActionListener
    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Restore button event detected");
      if (ConfirmationBox.display(frame, Application.getString(
          SettingsKeyboardPanel.this, "settings.keyboard.confirm.restore"))
	  == 1) {
	shortcuts = UserPreferences.getDefaultShortcuts();
	updateShortcutsPane();
      }
    }
  }
}
