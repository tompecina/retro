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
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.ScrollPaneConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import cz.pecina.retro.common.Application;
import cz.pecina.retro.gui.WheelSlider;
import cz.pecina.retro.gui.IconCache;

/**
 * The Settings/Memory panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class SettingsKeyboardPanel extends JScrollPane {
  
  // static logger
  private static final Logger log =
    Logger.getLogger(SettingsKeyboardPanel.class.getName());
    
  /**
   * Creates the Settings/Keyboard panel.
   *
   * @param computerHardware the computer hardware object
   */
  public SettingsKeyboardPanel(final ComputerHardware computerHardware) {
    super(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
	  ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    log.fine("New Settings/Keyboard panel creation started");

    final JPanel shortcutsPane = new JPanel(new GridBagLayout());

    setViewportBorder(BorderFactory.createEmptyBorder(5, 8, 0, 8));
  
    for (int row = 0; row < KeyboardLayout.NUMBER_BUTTON_ROWS; row++) {
      for (int column = 0;
	   column < KeyboardLayout.NUMBER_BUTTON_COLUMNS;
	   column++) {
	
	final int line =
	  (row * KeyboardLayout.NUMBER_BUTTON_COLUMNS) + column;
	final KeyboardButton button = computerHardware.getKeyboardHardware()
	  .getKeyboardLayout().getButton(row, column);

	final GridBagConstraints buttonConstraints =
	  new GridBagConstraints();
	final JLabel buttonLabel = new JLabel(IconCache.get(
	  String.format(button.getTemplate(), 1, "u")));
	buttonConstraints.gridx = 0;
	buttonConstraints.gridy = line;
	buttonConstraints.insets = new Insets(0, 0, 0, 0);
	buttonConstraints.anchor = GridBagConstraints.LINE_END;
	buttonConstraints.weightx = 0.0;
	buttonConstraints.weighty = 0.0;
	shortcutsPane.add(buttonLabel, buttonConstraints);
	
	final GridBagConstraints shortcutConstraints =
	  new GridBagConstraints();
	final JLabel shortcutLabel =
	  new JLabel(KeyEvent.getKeyText(button.getShortcut()));
	shortcutConstraints.gridx = 1;
	shortcutConstraints.gridy = line;
	shortcutConstraints.insets = new Insets(0, 0, 0, 0);
	shortcutConstraints.anchor = GridBagConstraints.LINE_END;
	shortcutConstraints.weightx = 0.0;
	shortcutConstraints.weighty = 0.0;
	shortcutsPane.add(shortcutLabel, shortcutConstraints);
	
	final GridBagConstraints changeButtonConstraints =
	  new GridBagConstraints();
	final JButton changeButton = new JButton(Application
	  .getString(this, "settings.keyboard.button.change"));
	changeButtonConstraints.gridx = 2;
	changeButtonConstraints.gridy = line;
	changeButtonConstraints.insets = new Insets(0, 0, 0, 0);
	changeButtonConstraints.anchor = GridBagConstraints.LINE_END;
	changeButtonConstraints.weightx = 0.0;
	changeButtonConstraints.weighty = 0.0;
	shortcutsPane.add(changeButton, changeButtonConstraints);

      	final GridBagConstraints clearButtonConstraints =
	  new GridBagConstraints();
	final JButton clearButton = new JButton(Application
	  .getString(this, "settings.keyboard.button.clear"));
	clearButtonConstraints.gridx = 3;
	clearButtonConstraints.gridy = line;
	clearButtonConstraints.insets = new Insets(0, 0, 0, 0);
	clearButtonConstraints.anchor = GridBagConstraints.LINE_END;
	clearButtonConstraints.weightx = 0.0;
	clearButtonConstraints.weighty = 0.0;
	shortcutsPane.add(clearButton, clearButtonConstraints);
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
}
