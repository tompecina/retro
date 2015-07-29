/* ComputerPanel.java
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
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JSlider;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import cz.pecina.retro.common.Application;
import cz.pecina.retro.peripherals.Peripheral;
import cz.pecina.retro.gui.WheelSlider;

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

  // array of available peripherals
  private Peripheral[] peripherals;

  // components holding the new values
  private JSlider viewPixelSize, memoryStartROM, memoryStartRAM;
  private JRadioButton[] viewLocales; 
  private JLabel memoryStartROMValue, memoryStartRAMValue;

  /**
   * Creates the Settings panel.
   *
   * @param frame       enclosing frame
   * @param peripherals array of available peripherals
   */
  public SettingsPanel(final HidingFrame frame,
		       final Peripheral[] peripherals) {
    super(new BorderLayout());
    log.fine("New SettingsPanel creation started");
    assert frame != null;
    this.frame = frame;
    this.peripherals = peripherals;

    final Border border = BorderFactory.createEmptyBorder(5, 8, 0, 8);

    final JTabbedPane tabbedPanel =
      new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
	
    final JPanel viewPanel = new JPanel(new GridBagLayout());
    viewPanel.setBorder(border);

    final GridBagConstraints viewPixelSizeLabelConstraints =
      new GridBagConstraints();
    final JLabel viewPixelSizeLabel =
      new JLabel(Application.getString(this, "settings.view.pixelSize.label") +
		 ":");
    viewPixelSizeLabelConstraints.gridx = 0;
    viewPixelSizeLabelConstraints.gridy = 0;
    viewPixelSizeLabelConstraints.insets = new Insets(5, 0, 0, 10);
    viewPixelSizeLabelConstraints.anchor = GridBagConstraints.LINE_END;
    viewPixelSizeLabelConstraints.weightx = 0.0;
    viewPixelSizeLabelConstraints.weighty = 0.0;
    viewPanel.add(viewPixelSizeLabel, viewPixelSizeLabelConstraints);

    final GridBagConstraints viewPixelSizeConstraints =
      new GridBagConstraints();
    viewPixelSize =
      new WheelSlider(JSlider.HORIZONTAL,
		      1,
		      Constants.PIXEL_SIZES.length,
		      UserPreferences.getPixelSize());
    viewPixelSizeLabel.setLabelFor(viewPixelSize);
    viewPixelSize.setMajorTickSpacing(1);
    viewPixelSize.setPaintTicks(true);
    viewPixelSize.setPaintLabels(true);
    viewPixelSizeConstraints.gridx = 1;
    viewPixelSizeConstraints.gridy = 0;
    viewPixelSizeConstraints.gridwidth = GridBagConstraints.REMAINDER;
    viewPixelSizeConstraints.fill = GridBagConstraints.HORIZONTAL;
    viewPixelSizeConstraints.insets = new Insets(10, 0, 10, 0);
    viewPixelSizeConstraints.anchor = GridBagConstraints.LINE_START;
    viewPixelSizeConstraints.weightx = 0.0;
    viewPixelSizeConstraints.weighty = 0.0;
    viewPanel.add(viewPixelSize, viewPixelSizeConstraints);

    final GridBagConstraints viewLocaleLabelConstraints =
      new GridBagConstraints();
    final JLabel viewLocaleLabel =
      new JLabel(Application.getString(this, "settings.view.locale.label") +
		 ":");
    viewLocaleLabelConstraints.gridx = 0;
    viewLocaleLabelConstraints.gridy = 1;
    viewLocaleLabelConstraints.insets = new Insets(0, 0, 0, 10);
    viewLocaleLabelConstraints.anchor = GridBagConstraints.LINE_END;
    viewLocaleLabelConstraints.weightx = 0.0;
    viewLocaleLabelConstraints.weighty = 0.0;
    viewPanel.add(viewLocaleLabel, viewLocaleLabelConstraints);

    final ButtonGroup viewLocaleGroup = new ButtonGroup();
    final GridBagConstraints[] viewLocaleConstraints =
      new GridBagConstraints[Constants.SUPPORTED_LOCALES.length];
    viewLocales = new JRadioButton[Constants.SUPPORTED_LOCALES.length];
    for (int i = 0; i < Constants.SUPPORTED_LOCALES.length; i++) {
      final String languageTag = Constants.SUPPORTED_LOCALES[i];
      viewLocales[i] =
	new JRadioButton(Application.getString(this, "settings.view.locale." +
					       languageTag));
      viewLocales[i].setSelected(languageTag.equals(
        UserPreferences.getLocale()));
      viewLocaleConstraints[i] = new GridBagConstraints();
      viewLocaleConstraints[i].gridx = i + 1;
      viewLocaleConstraints[i].gridy = 1;
      viewLocaleConstraints[i].insets = new Insets(0, 0, 0, 5);
      viewLocaleConstraints[i].anchor = GridBagConstraints.LINE_START;
      viewLocaleConstraints[i].weightx = 1.0;
      viewLocaleConstraints[i].weighty = 0.0;
      viewPanel.add(viewLocales[i], viewLocaleConstraints[i]);
      viewLocaleGroup.add(viewLocales[i]);
    }

    JPanel tempPanel = new JPanel(new BorderLayout());
    tempPanel.add(viewPanel, BorderLayout.PAGE_START);
    tabbedPanel.addTab(Application.getString(this, "settings.view"), tempPanel);

    final JPanel memoryPanel = new JPanel(new GridBagLayout());
    memoryPanel.setBorder(border);

    final GridBagConstraints memoryStartROMLabelConstraints =
      new GridBagConstraints();
    final JLabel memoryStartROMLabel =
      new JLabel(Application.getString(this, "settings.memory.startROM.label") +
		 ":");
    memoryStartROMLabelConstraints.gridx = 0;
    memoryStartROMLabelConstraints.gridy = 0;
    memoryStartROMLabelConstraints.insets = new Insets(0, 0, 0, 10);
    memoryStartROMLabelConstraints.anchor = GridBagConstraints.LINE_END;
    memoryStartROMLabelConstraints.weightx = 0.0;
    memoryStartROMLabelConstraints.weighty = 0.0;
    memoryPanel.add(memoryStartROMLabel, memoryStartROMLabelConstraints);

    final GridBagConstraints memoryStartROMConstraints =
      new GridBagConstraints();
    memoryStartROM = new WheelSlider(JSlider.HORIZONTAL,
				     0,
				     64,
				     UserPreferences.getStartROM());
    memoryStartROMLabel.setLabelFor(memoryStartROM);
    memoryStartROM.setMajorTickSpacing(16);
    memoryStartROM.setMinorTickSpacing(4);
    memoryStartROM.setPaintTicks(true);
    memoryStartROM.setPaintLabels(true);
    memoryStartROM.addChangeListener(new StartROMChangeListener());
    memoryStartROMConstraints.gridx = 1;
    memoryStartROMConstraints.gridy = 0;
    memoryStartROMConstraints.fill = GridBagConstraints.HORIZONTAL;
    memoryStartROMConstraints.insets = new Insets(5, 0, 5, 0);
    memoryStartROMConstraints.anchor = GridBagConstraints.LINE_START;
    memoryStartROMConstraints.weightx = 1.0;
    memoryStartROMConstraints.weighty = 0.0;
    memoryPanel.add(memoryStartROM, memoryStartROMConstraints);

    final GridBagConstraints memoryStartROMValueConstraints =
      new GridBagConstraints();
    memoryStartROMValue =
      new JLabel(String.format(Application
      .getString(this, "settings.memory.value"),
      UserPreferences.getStartROM()));
    memoryStartROMValueConstraints.gridx = 2;
    memoryStartROMValueConstraints.gridy = 0;
    memoryStartROMValueConstraints.insets = new Insets(0, 10, 0, 0);
    memoryStartROMValueConstraints.anchor = GridBagConstraints.LINE_START;
    memoryStartROMValueConstraints.weightx = 0.0;
    memoryStartROMValueConstraints.weighty = 0.0;
    memoryPanel.add(memoryStartROMValue, memoryStartROMValueConstraints);

    final GridBagConstraints memoryStartRAMLabelConstraints =
      new GridBagConstraints();
    final JLabel memoryStartRAMLabel =
      new JLabel(Application.getString(this, "settings.memory.startRAM.label") +
		 ":");
    memoryStartRAMLabelConstraints.gridx = 0;
    memoryStartRAMLabelConstraints.gridy = 1;
    memoryStartRAMLabelConstraints.insets = new Insets(0, 0, 0, 10);
    memoryStartRAMLabelConstraints.anchor = GridBagConstraints.LINE_END;
    memoryStartRAMLabelConstraints.weightx = 0.0;
    memoryStartRAMLabelConstraints.weighty = 0.0;
    memoryPanel.add(memoryStartRAMLabel, memoryStartRAMLabelConstraints);

    final GridBagConstraints memoryStartRAMConstraints =
      new GridBagConstraints();
    memoryStartRAM = new WheelSlider(JSlider.HORIZONTAL,
				     0,
				     64,
				     UserPreferences.getStartRAM());
    memoryStartRAMLabel.setLabelFor(memoryStartRAM);
    memoryStartRAM.setMajorTickSpacing(16);
    memoryStartRAM.setMinorTickSpacing(4);
    memoryStartRAM.setPaintTicks(true);
    memoryStartRAM.setPaintLabels(true);
    memoryStartRAM.addChangeListener(new StartRAMChangeListener());
    memoryStartRAMConstraints.gridx = 1;
    memoryStartRAMConstraints.gridy = 1;
    memoryStartRAMConstraints.fill = GridBagConstraints.HORIZONTAL;
    memoryStartRAMConstraints.insets = new Insets(5, 0, 5, 0);
    memoryStartRAMConstraints.anchor = GridBagConstraints.LINE_START;
    memoryStartRAMConstraints.weightx = 1.0;
    memoryStartRAMConstraints.weighty = 0.0;
    memoryPanel.add(memoryStartRAM, memoryStartRAMConstraints);

    final GridBagConstraints memoryStartRAMValueConstraints =
      new GridBagConstraints();
    memoryStartRAMValue =
      new JLabel(String.format(Application
      .getString(this, "settings.memory.value"),
      UserPreferences.getStartRAM()));
    memoryStartRAMValueConstraints.gridx = 2;
    memoryStartRAMValueConstraints.gridy = 1;
    memoryStartRAMValueConstraints.insets = new Insets(0, 10, 0, 0);
    memoryStartRAMValueConstraints.anchor = GridBagConstraints.LINE_START;
    memoryStartRAMValueConstraints.weightx = 0.0;
    memoryStartRAMValueConstraints.weighty = 0.0;
    memoryPanel.add(memoryStartRAMValue, memoryStartRAMValueConstraints);

    tempPanel = new JPanel(new BorderLayout());
    tempPanel.add(memoryPanel, BorderLayout.PAGE_START);
    tabbedPanel.addTab(Application.getString(this, "settings.memory"),
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
    for (int i = 0; i < Constants.SUPPORTED_LOCALES.length; i++) {
      viewLocales[i].setSelected(UserPreferences.getLocale()
        .equals(Constants.SUPPORTED_LOCALES[i]));
    }
    viewPixelSize.setValue(UserPreferences.getPixelSize());
    memoryStartROM.setValue(UserPreferences.getStartROM());
    memoryStartROMValue.setText(String.format(Application
      .getString(this, "settings.memory.value"),
      UserPreferences.getStartROM()));
    memoryStartRAM.setValue(UserPreferences.getStartRAM());
    memoryStartRAMValue.setText(String.format(Application
      .getString(this, "settings.memory.value"),
      UserPreferences.getStartRAM()));
    log.fine("Widgets initialized");
  }

  // start ROM change listener
  private class StartROMChangeListener implements ChangeListener {
    @Override
    public void stateChanged(final ChangeEvent event) {
      log.finer("Start ROM change event detected");
      memoryStartROMValue.setText(String.format(Application
        .getString(this, "settings.memory.value"), memoryStartROM.getValue()));
      if (memoryStartROM.getValue() > memoryStartRAM.getValue()) {
	memoryStartRAM.setValue(memoryStartROM.getValue());
      }
    }
  }

  // start RAM change listener
  private class StartRAMChangeListener implements ChangeListener {
    @Override
    public void stateChanged(final ChangeEvent event) {
      log.finer("Start RAM change event detected");
      memoryStartRAMValue.setText(String.format(Application
        .getString(this, "settings.memory.value"), memoryStartRAM.getValue()));
      if (memoryStartROM.getValue() > memoryStartRAM.getValue()) {
	memoryStartROM.setValue(memoryStartRAM.getValue());
      }
    }
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
      for (int i = 0; i < Constants.SUPPORTED_LOCALES.length; i++)
	if (viewLocales[i].isSelected()) {
	  UserPreferences.setLocale(Constants.SUPPORTED_LOCALES[i]);
	  break;
	}
      UserPreferences.setPixelSize(viewPixelSize.getValue());
      UserPreferences.setStartROM(memoryStartROM.getValue());
      UserPreferences.setStartRAM(memoryStartRAM.getValue());
      log.fine("All changes implemented");
    }
  }
}
