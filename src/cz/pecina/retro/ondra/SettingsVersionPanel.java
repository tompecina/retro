/* SettingsVersionPanel.java
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

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.BorderFactory;

/**
 * The Settings/Version panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class SettingsVersionPanel extends JPanel {

  // static logger
  private static final Logger log =
    Logger.getLogger(SettingsVersionPanel.class.getName());
    
  // the computer object
  private Computer computer;

  // the current ROM version
  private int currentVersion;

  // version buttons
  private JRadioButton[] versionButtons =
    new JRadioButton[Constants.NUMBER_VERSIONS];

  /**
   * Creates the Settings/Version panel.
   *
   * @param computer the computer object
   */
  public SettingsVersionPanel(final Computer computer) {
    super(new GridBagLayout());
    log.fine("New Settings/Version panel creation started");
    assert computer != null;
    
    this.computer = computer;

    setBorder(BorderFactory.createEmptyBorder(8, 15, 0, 8));
    final ButtonGroup versionGroup = new ButtonGroup();
    for (int version = 0; version < Constants.NUMBER_VERSIONS; version++) {
      final GridBagConstraints versionConstraints = new GridBagConstraints();
      versionButtons[version] = new JRadioButton(Constants.VERSIONS[version]);
      versionConstraints.gridx = 0;
      versionConstraints.gridy = version;
      versionConstraints.insets = new Insets(2, 0, 0, 0);
      versionConstraints.anchor = GridBagConstraints.LINE_START;
      versionConstraints.weightx = 1.0;
      versionConstraints.weighty = 0.0;
      add(versionButtons[version], versionConstraints);
      versionGroup.add(versionButtons[version]);
    }
    log.fine("Settings/Version panel set up");
  }

  /**
   * Initialize widgets.
   */
  public void setUp() {
    currentVersion = UserPreferences.getVersion();
    for (int version = 0; version < Constants.NUMBER_VERSIONS; version++) {
      versionButtons[version].setSelected(version == currentVersion);
    }
    log.fine("Widgets initialized");
  }

  // partial set listener
  private class SetListener implements ActionListener {
    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Set button event detected");
      for (int version = 0; version < Constants.NUMBER_VERSIONS; version++) {
	if ((version != currentVersion) && versionButtons[version].isSelected()) {
	  UserPreferences.setVersion(computer, version);
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
