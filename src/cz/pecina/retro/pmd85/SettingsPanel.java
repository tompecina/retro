/* SettingsPanel.java
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

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;

import cz.pecina.retro.common.Application;

import cz.pecina.retro.peripherals.Peripheral;

/**
 * The Settings panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class SettingsPanel extends JPanel {

  // static logger
  private static final Logger log =
    Logger.getLogger(SettingsPanel.class.getName());
    
  // enclosing frame
  private HidingFrame frame;

  // computer-related panels
  private SettingsViewPanel viewPanel;
  private SettingsModelPanel modelPanel;
  private SettingsKeyboardPanel keyboardPanel;
  private SettingsDisplayPanel displayPanel;

  // array of available peripherals
  private Peripheral[] peripherals;

  /**
   * Creates the Settings panel.
   *
   * @param frame       enclosing frame
   * @param computer    the computer object
   * @param peripherals array of available peripherals
   */
  public SettingsPanel(final HidingFrame frame,
		       final Computer computer,
		       final Peripheral[] peripherals) {
    super(new BorderLayout());
    log.fine("New Settings panel creation started");
    assert frame != null;
    this.frame = frame;
    this.peripherals = peripherals;

    final JTabbedPane tabbedPanel =
      new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
	
    viewPanel = new SettingsViewPanel();
    JPanel tempPanel = new JPanel(new BorderLayout());
    tempPanel.add(viewPanel, BorderLayout.PAGE_START);
    tabbedPanel.addTab(Application.getString(this, "settings.view"), tempPanel);

    modelPanel = new SettingsModelPanel(computer);
    tempPanel = new JPanel(new BorderLayout());
    tempPanel.add(modelPanel, BorderLayout.PAGE_START);
    tabbedPanel.addTab(Application.getString(this, "settings.model"), tempPanel);

    keyboardPanel = new SettingsKeyboardPanel(frame, computer);
    tempPanel = new JPanel(new BorderLayout());
    tempPanel.add(keyboardPanel, BorderLayout.PAGE_START);
    tabbedPanel.addTab(Application.getString(this, "settings.keyboard"),
		       tempPanel);

    displayPanel = new SettingsDisplayPanel(computer);
    tempPanel = new JPanel(new BorderLayout());
    tempPanel.add(displayPanel, BorderLayout.PAGE_START);
    tabbedPanel.addTab(Application.getString(this, "settings.display"),
		       tempPanel);

    for (Peripheral peripheral: peripherals) {
      final JPanel peripheralPanel = peripheral.createSettingsPanel();
      if (peripheralPanel != null) {
	tempPanel = new JPanel(new BorderLayout());
	tempPanel.add(peripheralPanel, BorderLayout.PAGE_START);
	tabbedPanel.addTab(peripheral.getSettingsTitle(), tempPanel);
      }
    }

    add(tabbedPanel);

    final JPanel buttonsPanel =
      new JPanel(new FlowLayout(FlowLayout.TRAILING, 8, 8));
    final JButton setButton =
      new JButton(Application.getString(this, "settings.button.set"));
    frame.getRootPane().setDefaultButton(setButton);
    setButton.addActionListener(new SetListener());
    setButton.addActionListener(viewPanel.createSetListener());
    setButton.addActionListener(modelPanel.createSetListener());
    setButton.addActionListener(keyboardPanel.createSetListener());
    setButton.addActionListener(displayPanel.createSetListener());
    buttonsPanel.add(setButton);
    final JButton cancelButton =
      new JButton(Application.getString(this, "settings.button.cancel"));
    cancelButton.addActionListener(new CloseListener());
    buttonsPanel.add(cancelButton);
	
    add(buttonsPanel, BorderLayout.PAGE_END);

    log.fine("Settings panel set up");
  }

  // close the frame
  private void closeFrame() {
    frame.close();
    log.fine("Settings panel closed");
  }

  /**
   * Initialize widgets.
   */
  public void setUp() {
    viewPanel.setUp();
    modelPanel.setUp();
    keyboardPanel.setUp();
    displayPanel.setUp();
    log.fine("Widgets initialized");
  }

  // close frame listener
  private class CloseListener implements ActionListener {
    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Close frame event detected");
      closeFrame();
    }
  }

  // set button listener
  private class SetListener implements ActionListener {
    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Set button event detected");
      for (Peripheral peripheral: peripherals) {
	peripheral.implementSettings();
      }
      closeFrame();
      log.fine("All changes implemented");
    }
  }
}
