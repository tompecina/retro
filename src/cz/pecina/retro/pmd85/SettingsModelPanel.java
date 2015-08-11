/* SettingsModelPanel.java
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

/**
 * The Settings/Model panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class SettingsModelPanel extends JPanel {

  // static logger
  private static final Logger log =
    Logger.getLogger(SettingsModelPanel.class.getName());
    
  // the computer object
  private Computer computer;

  // the current model
  private int currentModel;

  // model buttons
  private JRadioButton[] modelButtons =
    new JRadioButton[Constants.NUMBER_MODELS];

  /**
   * Creates the Settings/Model panel.
   *
   * @param computer the computer object
   */
  public SettingsModelPanel(final Computer computer) {
    super(new GridBagLayout());
    log.fine("New Settings/Model panel creation started");
    assert computer != null;
    
    this.computer = computer;

    setBorder(BorderFactory.createEmptyBorder(5, 8, 0, 8));
    final ButtonGroup modelGroup = new ButtonGroup();
    for (int model = 0; model < Constants.NUMBER_MODELS; model++) {
      final GridBagConstraints modelConstraints = new GridBagConstraints();
      modelButtons[model] = new JRadioButton(Constants.MODELS[model]);
      modelConstraints.gridx = 0;
      modelConstraints.gridy = model;
      modelConstraints.insets = new Insets(0, 0, 0, 0);
      modelConstraints.anchor = GridBagConstraints.LINE_START;
      modelConstraints.weightx = 1.0;
      modelConstraints.weighty = 0.0;
      add(modelButtons[model], modelConstraints);
      modelGroup.add(modelButtons[model]);
    }
    log.fine("Settings/Model panel set up");
  }

  /**
   * Initialize widgets.
   */
  public void setUp() {
    currentModel = UserPreferences.getModel();
    for (int model = 0; model < Constants.NUMBER_MODELS; model++) {
      modelButtons[model].setSelected(model == currentModel);
    }
    log.fine("Widgets initialized");
  }

  // partial set listener
  private class SetListener implements ActionListener {
    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Set button event detected");
      for (int model = 0; model < Constants.NUMBER_MODELS; model++) {
	if ((model != currentModel) && modelButtons[model].isSelected()) {
	  UserPreferences.setModel(computer, model);
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
