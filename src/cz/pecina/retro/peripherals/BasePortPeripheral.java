/* BasePortPeripheral.java
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
import javax.swing.BorderFactory;

import cz.pecina.retro.common.Application;

import cz.pecina.retro.gui.HexField;

/**
 * An abstract peripheral object with a base port.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public abstract class BasePortPeripheral extends Peripheral {

  // static logger
  private static final Logger log =
    Logger.getLogger(BasePortPeripheral.class.getName());

  // base port
  private int basePort;

  // default base port
  private int defaultBasePort;

  // base port form field
  private HexField basePortField;

  /**
   * Creates a new BasePortPeripheral object.
   *
   * @param name            the name of the peripheral
   * @param defaultBasePort the default base port
   */
  public BasePortPeripheral(final String name, final int defaultBasePort) {
    super(name);
    assert (defaultBasePort >= 0) && (defaultBasePort < 0x100);
    this.defaultBasePort = defaultBasePort;
    log.fine("New peripheral with a base port created: " + name);
  }

  /**
   * Gets preferences from the backing store.
   */
  protected void getPreferences() {
    try {
      preferences.sync();
    } catch (final Exception exception) {
      throw Application.createError(this, "backingStore");
    }
    basePort = preferences.getInt("basePort", defaultBasePort);
    if ((basePort < 0) || (basePort > 0xff)) {
      basePort = defaultBasePort;
    }
    log.finer("User preferences for '" + name + "' retrieved");
  }

  /**
   * Gets the base port.
   *
   * @return the base port
   */
  public int getBasePort() {
    getPreferences();
    log.finer(String.format("Base port retrieved from User preferences: %02x",
			    basePort));
    return basePort;
  }

  // for description see Peripheral
  @Override
  public JPanel createSettingsPanel() {
    final JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(5, 8, 0, 8));

    final GridBagConstraints basePortLabelConstraints =
      new GridBagConstraints();
    final JLabel basePortLabel = new JLabel(Application.getString(
      BasePortPeripheral.class, "basePort.label") + ":");
    basePortLabelConstraints.gridx = 0;
    basePortLabelConstraints.gridy = 0;
    basePortLabelConstraints.anchor = GridBagConstraints.LINE_END;
    basePortLabelConstraints.weightx = 0.0;
    basePortLabelConstraints.weighty = 0.0;
    panel.add(basePortLabel, basePortLabelConstraints);

    final GridBagConstraints basePortFieldConstraints =
      new GridBagConstraints();
    basePortField = new HexField(2);
    basePortLabel.setLabelFor(basePortField);
    basePortField.setText(String.format("%02x", getBasePort()));
    basePortFieldConstraints.gridx = 1;
    basePortFieldConstraints.gridy = 0;
    basePortFieldConstraints.gridwidth = GridBagConstraints.REMAINDER;
    basePortFieldConstraints.insets = new Insets(0, 8, 0, 0);
    basePortFieldConstraints.anchor = GridBagConstraints.LINE_START;
    basePortFieldConstraints.weightx = 1.0;
    basePortFieldConstraints.weighty = 0.0;
    panel.add(basePortField, basePortFieldConstraints);

    log.fine("BasePortPeripheral panel created for: " + name);
    return panel;
  }

  // for description see Peripheral
  @Override
  public void implementSettings() {
    basePort = basePortField.getValue();
    preferences.putInt("basePort", basePort);
    log.finer(String.format("New base port written to user preferences: %02x",
			    basePort));
    log.fine("Changed settings implemented for: " + name);
  }
}
