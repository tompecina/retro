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

package cz.pecina.retro.pmi80;

import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
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
  private Shortcut[][] shortcuts =
    new Shortcut[KeyboardLayout.NUMBER_BUTTON_ROWS]
                [KeyboardLayout.NUMBER_BUTTON_COLUMNS];
  
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
    log.fine("New Settings/Keyboard panel creation started");
    assert frame != null;
    assert computer != null;
    
    this.frame = frame;
    this.computer = computer;

    final JPanel shortcutsPane = new JPanel(new GridBagLayout());

    setViewportBorder(BorderFactory.createEmptyBorder(5, 8, 0, 8));
  
    for (int row = 0; row < KeyboardLayout.NUMBER_BUTTON_ROWS; row++) {
      for (int column = 0;
	   column < KeyboardLayout.NUMBER_BUTTON_COLUMNS;
	   column++) {
	
	final int line =
	  (row * KeyboardLayout.NUMBER_BUTTON_COLUMNS) + column;
	final KeyboardButton button = computer.getComputerHardware()
	  .getKeyboardHardware().getKeyboardLayout().getButton(row, column);
	shortcuts[row][column] = button.getShortcut();

	final GridBagConstraints buttonConstraints =
	  new GridBagConstraints();
	final JLabel buttonLabel = new JLabel(IconCache.get(
	  String.format(button.getTemplate(), 1, "u")));
	buttonConstraints.gridx = 0;
	buttonConstraints.gridy = line;
	buttonConstraints.insets = new Insets(8, 0, 8, 0);
	buttonConstraints.anchor = GridBagConstraints.LINE_END;
	buttonConstraints.weightx = 0.0;
	buttonConstraints.weighty = 0.0;
	shortcutsPane.add(buttonLabel, buttonConstraints);
	
	final GridBagConstraints shortcutConstraints =
	  new GridBagConstraints();
	final JLabel shortcutLabel = new JLabel(shortcutToText(button.getShortcut()));
	shortcutConstraints.gridx = 1;
	shortcutConstraints.gridy = line;
	shortcutConstraints.insets = new Insets(0, 8, 0, 8);
	shortcutConstraints.anchor = GridBagConstraints.CENTER;
	shortcutConstraints.weightx = 0.0;
	shortcutConstraints.weighty = 0.0;
	shortcutsPane.add(shortcutLabel, shortcutConstraints);
	
	final GridBagConstraints changeButtonConstraints =
	  new GridBagConstraints();
	final JButton changeButton = new JButton(Application
	  .getString(this, "settings.keyboard.button.change"));
	changeButtonConstraints.gridx = 2;
	changeButtonConstraints.gridy = line;
	changeButtonConstraints.insets = new Insets(0, 0, 0, 8);
	changeButtonConstraints.anchor = GridBagConstraints.LINE_START;
	changeButtonConstraints.weightx = 0.0;
	changeButtonConstraints.weighty = 0.0;
	shortcutsPane.add(changeButton, changeButtonConstraints);
	changeButton.addActionListener(new ChangeListener(row, column,
							  shortcutLabel));
      	final GridBagConstraints clearButtonConstraints =
	  new GridBagConstraints();
	final JButton clearButton = new JButton(Application
	  .getString(this, "settings.keyboard.button.clear"));
	clearButtonConstraints.gridx = 3;
	clearButtonConstraints.gridy = line;
	clearButtonConstraints.insets = new Insets(0, 0, 0, 0);
	clearButtonConstraints.anchor = GridBagConstraints.LINE_START;
	clearButtonConstraints.weightx = 0.0;
	clearButtonConstraints.weighty = 0.0;
	shortcutsPane.add(clearButton, clearButtonConstraints);
	clearButton.addActionListener(new ClearListener(row, column,
							shortcutLabel));
      }
    }

    getVerticalScrollBar().setUnitIncrement(16);
    setViewportView(shortcutsPane);
    setPreferredSize(new Dimension(0, 300));
    
    log.fine("Settings/Memory panel set up");
  }

  /**
   * Initialize widgets.
   */
  public void setUp() {
    log.fine("Widgets initialized");
  }

  // partial set listener
  private class SetListener implements ActionListener {
    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Set button event detected");
      for (int row = 0; row < KeyboardLayout.NUMBER_BUTTON_ROWS; row++) {
	for (int column = 0;
	     column < KeyboardLayout.NUMBER_BUTTON_COLUMNS;
	     column++) {
	  UserPreferences.setShortcut(
	    computer.getComputerHardware().getKeyboardHardware()
	      .getKeyboardLayout(),
	    row, column,
	    shortcuts[row][column]);
	}
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
    private int row, column;
    private JLabel label;

    private ChangeListener(final int row,
			   final int column,
			   final JLabel label) {
      this.row = row;
      this.column = column;
      this.label = label;
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Change button event detected");
      final List<Shortcut> list = new ArrayList<>();
      for (int row2 = 0; row2 < KeyboardLayout.NUMBER_BUTTON_ROWS; row2++) {
	for (int column2 = 0;
	     column2 < KeyboardLayout.NUMBER_BUTTON_COLUMNS;
	     column2++) {
	  if (((row != row2) || (column != column2)) &&
	      (shortcuts[row2][column2] != null)) {
	    list.add(shortcuts[row2][column2]);
	  }
	}
	final Shortcut shortcut = ShortcutDialog.getShortcut(frame, list);
	if (shortcut != null) {
	  shortcuts[row][column] = shortcut;
	  label.setText(shortcut.getDesc());
	}
      }
    }
  }
  
  // clear button listener
  private class ClearListener implements ActionListener {
    private int row, column;
    private JLabel label;

    private ClearListener(final int row,
			  final int column,
			  final JLabel label) {
      this.row = row;
      this.column = column;
      this.label = label;
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Clear button event detected");
      shortcuts[row][column] = null;
      label.setText(shortcutToText(null));
    }
  }
}
