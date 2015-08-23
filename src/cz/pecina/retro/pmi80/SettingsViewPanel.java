/* SettingsViewPanel.java
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

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JSlider;
import javax.swing.BorderFactory;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import cz.pecina.retro.common.GeneralConstants;
import cz.pecina.retro.common.Application;

import cz.pecina.retro.gui.WheelSlider;

/**
 * The Settings/View panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class SettingsViewPanel extends JPanel {

  // static logger
  private static final Logger log =
    Logger.getLogger(SettingsViewPanel.class.getName());
    
  // components holding the new values
  private JSlider pixelSize;
  private JRadioButton[] locales; 

  /**
   * Creates the Settings/View panel.
   */
  public SettingsViewPanel() {
    super(new GridBagLayout());
    log.fine("New Settings/View panel creation started");

    setBorder(BorderFactory.createEmptyBorder(5, 8, 0, 8));

    final GridBagConstraints pixelSizeLabelConstraints =
      new GridBagConstraints();
    final JLabel pixelSizeLabel =
      new JLabel(Application.getString(this, "settings.view.pixelSize.label") +
		 ":");
    pixelSizeLabelConstraints.gridx = 0;
    pixelSizeLabelConstraints.gridy = 0;
    pixelSizeLabelConstraints.insets = new Insets(5, 0, 0, 10);
    pixelSizeLabelConstraints.anchor = GridBagConstraints.LINE_END;
    pixelSizeLabelConstraints.weightx = 0.0;
    pixelSizeLabelConstraints.weighty = 0.0;
    add(pixelSizeLabel, pixelSizeLabelConstraints);

    final GridBagConstraints pixelSizeConstraints =
      new GridBagConstraints();
    pixelSize =
      new WheelSlider(JSlider.HORIZONTAL,
		      1,
		      GeneralConstants.PIXEL_SIZES.length,
		      UserPreferences.getPixelSize());
    pixelSizeLabel.setLabelFor(pixelSize);
    pixelSize.setMajorTickSpacing(1);
    pixelSize.setPaintTicks(true);
    pixelSize.setPaintLabels(true);
    pixelSizeConstraints.gridx = 1;
    pixelSizeConstraints.gridy = 0;
    pixelSizeConstraints.gridwidth = GridBagConstraints.REMAINDER;
    pixelSizeConstraints.fill = GridBagConstraints.HORIZONTAL;
    pixelSizeConstraints.insets = new Insets(10, 0, 10, 0);
    pixelSizeConstraints.anchor = GridBagConstraints.LINE_START;
    pixelSizeConstraints.weightx = 0.0;
    pixelSizeConstraints.weighty = 0.0;
    add(pixelSize, pixelSizeConstraints);

    final GridBagConstraints localeLabelConstraints =
      new GridBagConstraints();
    final JLabel localeLabel =
      new JLabel(Application.getString(this, "settings.view.locale.label") +
		 ":");
    localeLabelConstraints.gridx = 0;
    localeLabelConstraints.gridy = 1;
    localeLabelConstraints.insets = new Insets(0, 0, 0, 10);
    localeLabelConstraints.anchor = GridBagConstraints.LINE_END;
    localeLabelConstraints.weightx = 0.0;
    localeLabelConstraints.weighty = 0.0;
    add(localeLabel, localeLabelConstraints);

    final ButtonGroup localeGroup = new ButtonGroup();
    final GridBagConstraints[] localeConstraints =
      new GridBagConstraints[GeneralConstants.SUPPORTED_LOCALES.length];
    locales = new JRadioButton[GeneralConstants.SUPPORTED_LOCALES.length];
    for (int i = 0; i < GeneralConstants.SUPPORTED_LOCALES.length; i++) {
      final String languageTag = GeneralConstants.SUPPORTED_LOCALES[i];
      locales[i] =
	new JRadioButton(Application.getString(this, "settings.view.locale." +
					       languageTag));
      locales[i].setSelected(languageTag.equals(
        UserPreferences.getLocale()));
      localeConstraints[i] = new GridBagConstraints();
      localeConstraints[i].gridx = i + 1;
      localeConstraints[i].gridy = 1;
      localeConstraints[i].insets = new Insets(0, 0, 0, 5);
      localeConstraints[i].anchor = GridBagConstraints.LINE_START;
      localeConstraints[i].weightx = 1.0;
      localeConstraints[i].weighty = 0.0;
      add(locales[i], localeConstraints[i]);
      localeGroup.add(locales[i]);
    }
    log.fine("Settings/View panel set up");
  }

  /**
   * Initialize widgets.
   */
  public void setUp() {
    for (int i = 0; i < GeneralConstants.SUPPORTED_LOCALES.length; i++) {
      locales[i].setSelected(UserPreferences.getLocale()
        .equals(GeneralConstants.SUPPORTED_LOCALES[i]));
    }
    pixelSize.setValue(UserPreferences.getPixelSize());
    log.fine("Widgets initialized");
  }

  // partial set listener
  private class SetListener implements ActionListener {
    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Set button event detected");
      for (int i = 0; i < GeneralConstants.SUPPORTED_LOCALES.length; i++)
	if (locales[i].isSelected()) {
	  UserPreferences.setLocale(GeneralConstants.SUPPORTED_LOCALES[i]);
	  break;
	}
      UserPreferences.setPixelSize(pixelSize.getValue());
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
