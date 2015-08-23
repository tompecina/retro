/* PeriperalsPanel.java
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
import java.awt.FlowLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JButton;

import cz.pecina.retro.common.Application;

import cz.pecina.retro.gui.CloseableFrame;

/**
 * The Peripherals panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class PeripheralsPanel extends JPanel {

  // static logger
  private static final Logger log =
    Logger.getLogger(PeripheralsPanel.class.getName());
    
  // enclosing frame
  private CloseableFrame frame;

  // list of avaiable peripherals
  private Peripheral[] peripherals;

  // checkboxes controlling the peripherals
  private final JCheckBox[] peripheralCheckBoxes;
	
  /**
   * Creates the Peripherals panel.
   *
   * @param frame       enclosing frame
   * @param peripherals array of peripherals
   */
  public PeripheralsPanel(final CloseableFrame frame,
			  final Peripheral[] peripherals) {
    super(new GridBagLayout());
    log.fine("New PeripheralsPanel creation started");
    this.frame = frame;
    this.peripherals = peripherals;
    peripheralCheckBoxes = new JCheckBox[peripherals.length];

    final GridBagConstraints[] peripheralCheckBoxConstraints =
      new GridBagConstraints[peripherals.length];

    setBorder(BorderFactory.createEmptyBorder(2, 8, 0, 8));

    for (int i = 0; i < peripherals.length; i++) {
      peripheralCheckBoxConstraints[i] = new GridBagConstraints();
      peripheralCheckBoxes[i] = new JCheckBox(peripherals[i].getLabelText());
      peripheralCheckBoxConstraints[i].gridx = 0;
      peripheralCheckBoxConstraints[i].gridy = i;
      peripheralCheckBoxConstraints[i].anchor = GridBagConstraints.LINE_START;
      peripheralCheckBoxConstraints[i].weightx = 0.0;
      peripheralCheckBoxConstraints[i].weighty = 0.0;
      add(peripheralCheckBoxes[i], peripheralCheckBoxConstraints[i]);
    }
	
    final GridBagConstraints buttonsConstraints = new GridBagConstraints();
    final JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
    buttonsConstraints.gridx = 0;
    buttonsConstraints.gridy = peripherals.length;
    buttonsConstraints.anchor = GridBagConstraints.LAST_LINE_END;
    buttonsConstraints.weightx = 0.0;
    buttonsConstraints.weighty = 1.0;
    final JButton okButton =
      new JButton(Application.getString(this, "button.ok"));
    frame.getRootPane().setDefaultButton(okButton);
    okButton.addActionListener(new OKListener());
    buttonsPanel.add(okButton);
    final JButton cancelButton =
      new JButton(Application.getString(this, "button.cancel"));
    cancelButton.addActionListener(new CloseListener());
    buttonsPanel.add(cancelButton);
    add(buttonsPanel, buttonsConstraints);

    log.fine("Peripherals panel set up");
  }

  // close the frame
  private void closeFrame() {
    frame.close();
  }

  /**
   * Initialize widgets.
   */
  public void setUp() {
    for (int i = 0; i < peripherals.length; i++) {
      peripheralCheckBoxes[i].setSelected(peripherals[i].isActive());
    }
  }

  // close frame listener
  private class CloseListener implements ActionListener {
    @Override
    public void actionPerformed(final ActionEvent event) {
      closeFrame();
    }
  }

  // OK button listener
  private class OKListener implements ActionListener {
    @Override
    public void actionPerformed(final ActionEvent event) {
      for (int i = 0; i < peripherals.length; i++) {
	if (peripheralCheckBoxes[i].isSelected() &&
	    !peripherals[i].isActive()) {
	  peripherals[i].activate();
	} else if (!peripheralCheckBoxes[i].isSelected() &&
		   peripherals[i].isActive()) {
	  peripherals[i].deactivate();
	}
      }
      closeFrame();
    }
  }
}
