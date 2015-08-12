/* SettingsDisplayPanel.java
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
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.BorderFactory;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import cz.pecina.retro.common.Application;

/**
 * The Settings/Display panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class SettingsDisplayPanel extends JPanel {

  // static logger
  private static final Logger log =
    Logger.getLogger(SettingsDisplayPanel.class.getName());
    
  // the computer object
  private Computer computer;

  // the current color mode
  private int currentColorMode;

  // mode buttons
  private JRadioButton[] colorModeButtons =
    new JRadioButton[Constants.NUMBER_COLOR_MODES];

  /**
   * Creates the Settings/Display panel.
   *
   * @param computer the computer object
   */
  public SettingsDisplayPanel(final Computer computer) {
    super(new GridBagLayout());
    log.fine("New Settings/Display panel creation started");
    assert computer != null;
    
    this.computer = computer;

    setBorder(BorderFactory.createEmptyBorder(8, 15, 0, 8));
    final ButtonGroup colorModeGroup = new ButtonGroup();
    for (int mode = 0; mode < Constants.NUMBER_COLOR_MODES; mode++) {
      final GridBagConstraints colorModeConstraints = new GridBagConstraints();
      colorModeButtons[mode] = new JRadioButton(Application.getString(
        this,
        "settings.display.colorMode." + mode));
      colorModeConstraints.gridx = 0;
      colorModeConstraints.gridy = mode;
      colorModeConstraints.insets = new Insets(2, 0, 0, 0);
      colorModeConstraints.anchor = GridBagConstraints.LINE_START;
      colorModeConstraints.weightx = 1.0;
      colorModeConstraints.weighty = 0.0;
      add(colorModeButtons[mode], colorModeConstraints);
      colorModeGroup.add(colorModeButtons[mode]);
    }
    log.fine("Settings/Display panel set up");
  }

  /**
   * Initialize widgets.
   */
  public void setUp() {
    currentColorMode = UserPreferences.getColorMode();
    for (int mode = 0; mode < Constants.NUMBER_COLOR_MODES; mode++) {
      colorModeButtons[mode].setSelected(mode == currentColorMode);
    }
    log.fine("Widgets initialized");
  }

  // partial set listener
  private class SetListener implements ActionListener {
    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Set button event detected");
      for (int mode = 0; mode < Constants.NUMBER_COLOR_MODES; mode++) {
	if ((mode != currentColorMode) && colorModeButtons[mode].isSelected()) {
	  UserPreferences.setColorMode(computer, mode);
	  break;
	}
      }
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
