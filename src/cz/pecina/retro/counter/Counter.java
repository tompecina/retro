/* Counter.java
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

package cz.pecina.retro.counter;

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
import cz.pecina.retro.peripherals.BasePortPeripheral;
import cz.pecina.retro.gui.GUI;

/**
 * RFT G-2002.500 frequency counter.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Counter extends BasePortPeripheral {

  // static logger
  private static final Logger log =
    Logger.getLogger(Counter.class.getName());

  /**
   * The default base port.
   */
  protected static final int DEFAULT_BASE_PORT = 0xc0;

  /**
   * The default trigger bit (<code>=1</code> = any port write).
   */
  protected static final int DEFAULT_BIT = -1;

  // hardware
  private CounterHardware hardware;

  // enclosing frame
  private CounterFrame frame;

  // trigger bit
  private int bit;

  // trigger bit radio buttons
  private final JRadioButton[] bitRadioButtons = new JRadioButton[8];

  /**
   * Creates a new frequency counter.
   */
  public Counter() {
    super("counter", DEFAULT_BASE_PORT);
    log.fine("New frequency counter created");
  }

  // for description see Peripheral
  @Override
  public void activate() {
    log.fine("Counter/digital meter activating");
    hardware = new CounterHardware(getBasePort(), bit);
    frame = new CounterFrame(this, hardware);
    super.activate();
    log.fine("Frequency counter activated");
  }

  // for description see Peripheral
  @Override
  public void deactivate() {
    super.deactivate();
    hardware.deactivate();
    GUI.removeResizeable(frame);
    frame.dispose();
    log.fine("Frequency counter deactivated");
  }

  // for description see BasePortPeripheral
  @Override
  protected void getPreferences() {
    super.getPreferences();
    bit = preferences.getInt("bit", DEFAULT_BIT);
    if ((bit < -1) || (bit > 7)) {
      bit = DEFAULT_BIT;
    }
    log.finer("User preferences for '" + name + "' retrieved");
  }

  // gets the trigger bit
  public int getBit() {
    getPreferences();
    log.finer("Trigger bit retrieved from User preferences: " + bit);
    return bit;
  }

  // for description see Peripheral
  @Override
  public JPanel createSettingsPanel() {
    final JPanel panel = super.createSettingsPanel();

    final GridBagConstraints bitLabelConstraints = new GridBagConstraints();
    final JLabel bitLabel =
      new JLabel(Application.getString(Counter.class, "bit.label") + ":");
    bitLabelConstraints.gridx = 0;
    bitLabelConstraints.gridy = 1;
    bitLabelConstraints.anchor = GridBagConstraints.LINE_END;
    bitLabelConstraints.weightx = 0.0;
    bitLabelConstraints.weighty = 0.0;
    panel.add(bitLabel, bitLabelConstraints);

    final ButtonGroup bitGroup = new ButtonGroup();

    final GridBagConstraints bitAnyRadioButtonConstraints =
      new GridBagConstraints();
    final JRadioButton bitAnyRadioButton =
      new JRadioButton(Application.getString(Counter.class, "bit.any"));
    bitAnyRadioButton.setSelected(getBit() == -1);
    bitAnyRadioButtonConstraints.gridx = 1;
    bitAnyRadioButtonConstraints.gridy = 1;
    bitAnyRadioButtonConstraints.insets = new Insets(0, 4, 0, 0);
    bitAnyRadioButtonConstraints.anchor = GridBagConstraints.LINE_START;
    bitAnyRadioButtonConstraints.weightx = 0.0;
    bitAnyRadioButtonConstraints.weighty = 0.0;
    panel.add(bitAnyRadioButton, bitAnyRadioButtonConstraints);
    bitGroup.add(bitAnyRadioButton);

    final GridBagConstraints[] bitRadioButtonConstraints =
      new GridBagConstraints[8];
    for (int i = 0; i < 8; i++) {
      bitRadioButtonConstraints[i] = new GridBagConstraints();
      bitRadioButtons[i] = new JRadioButton(String.format("%d", i));
      bitRadioButtons[i].setSelected(getBit() == i);
      bitRadioButtonConstraints[i].gridx = 2 + i;
      bitRadioButtonConstraints[i].gridy = 1;
      bitRadioButtonConstraints[i].insets = new Insets(0, 0, 0, 4);
      bitRadioButtonConstraints[i].anchor = GridBagConstraints.LINE_START;
      bitRadioButtonConstraints[i].weightx = 0.0;
      bitRadioButtonConstraints[i].weighty = 0.0;
      panel.add(bitRadioButtons[i], bitRadioButtonConstraints[i]);
      bitGroup.add(bitRadioButtons[i]);
    }

    log.fine("Frequency counter settings panel created");
    return panel;
  }

  // for description see Peripheral
  @Override
  public void implementSettings() {
    super.implementSettings();
    bit = -1;
    for (int i = 0; i < 8; i++)
      if (bitRadioButtons[i].isSelected()) {
	bit = i;
	break;
      }
    preferences.putInt("bit", bit);
    if (isActive())
      hardware.reconnect(getBasePort(), getBit());
    log.finer("New trigger bit written to user preferences");
    log.fine("Changed settings implemented for the frequency counter");
  }
}
