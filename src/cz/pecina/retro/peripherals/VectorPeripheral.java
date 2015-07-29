/* VectorPeripheral.java
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

package cz.pecina.retro.peripherals;

import java.util.logging.Logger;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.BorderFactory;
import cz.pecina.retro.common.Application;

/**
 * An abstract peripheral object with a base port and interrupt vector.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public abstract class VectorPeripheral extends BasePortPeripheral {

  // static logger
  private static final Logger log =
    Logger.getLogger(VectorPeripheral.class.getName());

  // interrupt vector
  private int vector;

  // default interrupt vector
  private int defaultVector;

  // interrupt vector radio buttons
  private final JRadioButton[] vectorRadioButtons = new JRadioButton[8];

  /**
   * Creates a new VectorPeripheral object.
   *
   * @param name            the name of the peripheral
   * @param defaultBasePort the default base port
   * @param defaultVector   the default interrupt vector
   */
  public VectorPeripheral(final String name,
			  final int defaultBasePort,
			  final int defaultVector) {
    super(name, defaultBasePort);
    assert (defaultVector >= -1) && (defaultVector < 8);
    this.defaultVector = defaultVector;
    log.fine("New peripheral with a base port and interrupt vector created: " +
	     name);
  }

  // for description see BasePortPeripheral
  @Override
  protected void getPreferences() {
    super.getPreferences();
    vector = preferences.getInt("vector", defaultVector);
    if ((vector < -1) || (vector > 7)) {
      vector = defaultVector;
    }
    log.finer("User preferences for '" + name + "' retrieved");
  }

  /**
   * Gets the interrupt vector.
   *
   * @return the interrupt vector
   */
  public int getVector() {
    getPreferences();
    log.finer("Interrupt vector retrieved from User preferences: " + vector);
    return vector;
  }

  // for description see Peripheral
  @Override
  public JPanel createSettingsPanel() {
    final JPanel panel = super.createSettingsPanel();

    final GridBagConstraints vectorLabelConstraints =
      new GridBagConstraints();
    final JLabel vectorLabel =
      new JLabel(Application.getString(VectorPeripheral.class,
				       "vector.label") + ":");
    vectorLabelConstraints.gridx = 0;
    vectorLabelConstraints.gridy = 1;
    vectorLabelConstraints.anchor = GridBagConstraints.LINE_END;
    vectorLabelConstraints.weightx = 0.0;
    vectorLabelConstraints.weighty = 0.0;
    panel.add(vectorLabel, vectorLabelConstraints);

    final ButtonGroup vectorGroup = new ButtonGroup();

    final GridBagConstraints vectorNoneRadioButtonConstraints =
      new GridBagConstraints();
    final JRadioButton vectorNoneRadioButton =
      new JRadioButton(Application.getString(VectorPeripheral.class,
					     "vector.none"));
    vectorNoneRadioButton.setSelected(getVector() == -1);
    vectorNoneRadioButtonConstraints.gridx = 1;
    vectorNoneRadioButtonConstraints.gridy = 1;
    vectorNoneRadioButtonConstraints.insets = new Insets(0, 4, 0, 0);
    vectorNoneRadioButtonConstraints.anchor = GridBagConstraints.LINE_START;
    vectorNoneRadioButtonConstraints.weightx = 0.0;
    vectorNoneRadioButtonConstraints.weighty = 0.0;
    panel.add(vectorNoneRadioButton, vectorNoneRadioButtonConstraints);
    vectorGroup.add(vectorNoneRadioButton);

    final GridBagConstraints[] vectorRadioButtonConstraints =
      new GridBagConstraints[8];
    for (int i = 0; i < 8; i++) {
      vectorRadioButtonConstraints[i] = new GridBagConstraints();
      vectorRadioButtons[i] = new JRadioButton(String.format("%d", i));
      vectorRadioButtons[i].setSelected(getVector() == i);
      vectorRadioButtonConstraints[i].gridx = 2 + i;
      vectorRadioButtonConstraints[i].gridy = 1;
      vectorRadioButtonConstraints[i].insets = new Insets(0, 0, 0, 4);
      vectorRadioButtonConstraints[i].anchor = GridBagConstraints.LINE_START;
      vectorRadioButtonConstraints[i].weightx = 0.0;
      vectorRadioButtonConstraints[i].weighty = 0.0;
      panel.add(vectorRadioButtons[i], vectorRadioButtonConstraints[i]);
      vectorGroup.add(vectorRadioButtons[i]);
    }

    log.fine("VectorPeripheral panel created for: " + name);
    return panel;
  }

  // for description see Peripheral
  @Override
  public void implementSettings() {
    super.implementSettings();
    vector = -1;
    for (int i = 0; i < 8; i++)
      if (vectorRadioButtons[i].isSelected()) {
	vector = i;
	break;
      }
    preferences.putInt("vector", vector);
    log.finer("New interrupt vector written to user preferences: " + vector);
    log.fine("Changed settings implemented for: " + name);
  }
}
