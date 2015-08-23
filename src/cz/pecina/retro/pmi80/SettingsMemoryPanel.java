/* SettingsMemoryPanel.java
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

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JSlider;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import cz.pecina.retro.common.Application;

import cz.pecina.retro.gui.WheelSlider;

/**
 * The Settings/Memory panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class SettingsMemoryPanel extends JPanel {

  // static logger
  private static final Logger log =
    Logger.getLogger(SettingsMemoryPanel.class.getName());
    
  // components holding the new values
  private JSlider startROM, startRAM;
  private JLabel startROMValue, startRAMValue;

  /**
   * Creates the Settings/Memory panel.
   */
  public SettingsMemoryPanel() {
    super(new GridBagLayout());
    log.fine("New Settings/Memory panel creation started");

    setBorder(BorderFactory.createEmptyBorder(5, 8, 0, 8));

    final GridBagConstraints startROMLabelConstraints =
      new GridBagConstraints();
    final JLabel startROMLabel =
      new JLabel(Application.getString(this, "settings.memory.startROM.label") +
		 ":");
    startROMLabelConstraints.gridx = 0;
    startROMLabelConstraints.gridy = 0;
    startROMLabelConstraints.insets = new Insets(0, 0, 0, 10);
    startROMLabelConstraints.anchor = GridBagConstraints.LINE_END;
    startROMLabelConstraints.weightx = 0.0;
    startROMLabelConstraints.weighty = 0.0;
    add(startROMLabel, startROMLabelConstraints);

    final GridBagConstraints startROMConstraints =
      new GridBagConstraints();
    startROM = new WheelSlider(JSlider.HORIZONTAL,
			       0, 64,
			       UserPreferences.getStartROM());
    startROMLabel.setLabelFor(startROM);
    startROM.setMajorTickSpacing(16);
    startROM.setMinorTickSpacing(4);
    startROM.setPaintTicks(true);
    startROM.setPaintLabels(true);
    startROM.addChangeListener(new StartROMChangeListener());
    startROMConstraints.gridx = 1;
    startROMConstraints.gridy = 0;
    startROMConstraints.fill = GridBagConstraints.HORIZONTAL;
    startROMConstraints.insets = new Insets(5, 0, 5, 0);
    startROMConstraints.anchor = GridBagConstraints.LINE_START;
    startROMConstraints.weightx = 1.0;
    startROMConstraints.weighty = 0.0;
    add(startROM, startROMConstraints);

    final GridBagConstraints startROMValueConstraints =
      new GridBagConstraints();
    startROMValue =
      new JLabel(String.format(Application
      .getString(this, "settings.memory.value"),
      UserPreferences.getStartROM()));
    startROMValueConstraints.gridx = 2;
    startROMValueConstraints.gridy = 0;
    startROMValueConstraints.insets = new Insets(0, 10, 0, 0);
    startROMValueConstraints.anchor = GridBagConstraints.LINE_START;
    startROMValueConstraints.weightx = 0.0;
    startROMValueConstraints.weighty = 0.0;
    add(startROMValue, startROMValueConstraints);

    final GridBagConstraints startRAMLabelConstraints =
      new GridBagConstraints();
    final JLabel startRAMLabel =
      new JLabel(Application.getString(this, "settings.memory.startRAM.label") +
		 ":");
    startRAMLabelConstraints.gridx = 0;
    startRAMLabelConstraints.gridy = 1;
    startRAMLabelConstraints.insets = new Insets(0, 0, 0, 10);
    startRAMLabelConstraints.anchor = GridBagConstraints.LINE_END;
    startRAMLabelConstraints.weightx = 0.0;
    startRAMLabelConstraints.weighty = 0.0;
    add(startRAMLabel, startRAMLabelConstraints);

    final GridBagConstraints startRAMConstraints =
      new GridBagConstraints();
    startRAM = new WheelSlider(JSlider.HORIZONTAL,
			       0, 64,
			       UserPreferences.getStartRAM());
    startRAMLabel.setLabelFor(startRAM);
    startRAM.setMajorTickSpacing(16);
    startRAM.setMinorTickSpacing(4);
    startRAM.setPaintTicks(true);
    startRAM.setPaintLabels(true);
    startRAM.addChangeListener(new StartRAMChangeListener());
    startRAMConstraints.gridx = 1;
    startRAMConstraints.gridy = 1;
    startRAMConstraints.fill = GridBagConstraints.HORIZONTAL;
    startRAMConstraints.insets = new Insets(5, 0, 5, 0);
    startRAMConstraints.anchor = GridBagConstraints.LINE_START;
    startRAMConstraints.weightx = 1.0;
    startRAMConstraints.weighty = 0.0;
    add(startRAM, startRAMConstraints);

    final GridBagConstraints startRAMValueConstraints =
      new GridBagConstraints();
    startRAMValue =
      new JLabel(String.format(Application
      .getString(this, "settings.memory.value"),
      UserPreferences.getStartRAM()));
    startRAMValueConstraints.gridx = 2;
    startRAMValueConstraints.gridy = 1;
    startRAMValueConstraints.insets = new Insets(0, 10, 0, 0);
    startRAMValueConstraints.anchor = GridBagConstraints.LINE_START;
    startRAMValueConstraints.weightx = 0.0;
    startRAMValueConstraints.weighty = 0.0;
    add(startRAMValue, startRAMValueConstraints);

    log.fine("Settings/Memory panel set up");
  }

  /**
   * Initialize widgets.
   */
  public void setUp() {
    startROM.setValue(UserPreferences.getStartROM());
    startROMValue.setText(String.format(Application
      .getString(this, "settings.memory.value"),
      UserPreferences.getStartROM()));
    startRAM.setValue(UserPreferences.getStartRAM());
    startRAMValue.setText(String.format(Application
      .getString(this, "settings.memory.value"),
      UserPreferences.getStartRAM()));
    log.fine("Widgets initialized");
  }

  // start ROM change listener
  private class StartROMChangeListener implements ChangeListener {
    @Override
    public void stateChanged(final ChangeEvent event) {
      log.finer("Start ROM change event detected");
      startROMValue.setText(String.format(Application
        .getString(this, "settings.memory.value"), startROM.getValue()));
      if (startROM.getValue() > startRAM.getValue()) {
	startRAM.setValue(startROM.getValue());
      }
    }
  }

  // start RAM change listener
  private class StartRAMChangeListener implements ChangeListener {
    @Override
    public void stateChanged(final ChangeEvent event) {
      log.finer("Start RAM change event detected");
      startRAMValue.setText(String.format(Application
        .getString(this, "settings.memory.value"), startRAM.getValue()));
      if (startROM.getValue() > startRAM.getValue()) {
	startROM.setValue(startRAM.getValue());
      }
    }
  }

  // partial set listener
  private class SetListener implements ActionListener {
    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Set button event detected");
      UserPreferences.setStartROM(startROM.getValue());
      UserPreferences.setStartRAM(startRAM.getValue());
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
