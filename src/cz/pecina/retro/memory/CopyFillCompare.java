/* CopyFillCompare.java
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

package cz.pecina.retro.memory;

import java.util.logging.Logger;

import java.util.List;
import java.util.ArrayList;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.FlowLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.ButtonGroup;
import javax.swing.BorderFactory;

import cz.pecina.retro.common.Application;

import cz.pecina.retro.cpu.Block;

import cz.pecina.retro.gui.RadioClick;
import cz.pecina.retro.gui.HexField;
import cz.pecina.retro.gui.InfoBox;

/**
 * Memory/CopyFillCompare panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class CopyFillCompare extends MemoryTab {

  // static logger
  private static final Logger log =
    Logger.getLogger(CopyFillCompare.class.getName());

  // components holding values used by listeners
  private JRadioButton copyRadio, fillRadio, compareRadio;
  private HexField copyStartField, copyEndField, copyDestinationField,
    fillStartField, fillEndField, fillDataField,
    compareStartField, compareEndField, compareDestinationField;

  // lists of bank selection radio buttons
  private List<JRadioButton> sourceBankRadioButtons = new ArrayList<>();
  private List<JRadioButton> destinationBankRadioButtons = new ArrayList<>();
      
  /**
   * Creates Memory/CopyFillCompare panel.
   *
   * @param panel enclosing panel
   */
  public CopyFillCompare(final MemoryPanel panel) {
    super(panel);
    log.fine("New Memory/CopyFillCompare creation started");
 
    setBorder(BorderFactory.createEmptyBorder(5, 8, 0, 8));
    final ButtonGroup copyFillCompareGroup = new ButtonGroup();
    int line = 0;

    if (numberBanks > 1) {

      final GridBagConstraints banksPaneConstraints =
	new GridBagConstraints();
      final JPanel banksPane =
	new JPanel(new GridBagLayout());
      banksPaneConstraints.gridx = 0;
      banksPaneConstraints.gridy = line;
      banksPaneConstraints.insets = new Insets(0, 3, 0, 0);
      banksPaneConstraints.gridwidth = GridBagConstraints.REMAINDER;
      banksPaneConstraints.anchor = GridBagConstraints.LINE_START;
      banksPaneConstraints.weightx = 0.0;
      banksPaneConstraints.weighty = 0.0;
      
      final GridBagConstraints sourceBankLabelConstraints =
	new GridBagConstraints();
      final JLabel sourceBankLabel =
	new JLabel(Application.getString(this, "bank.source") + ":");
      sourceBankLabelConstraints.gridx = 0;
      sourceBankLabelConstraints.gridy = 0;
      sourceBankLabelConstraints.insets = new Insets(0, 3, 0, 0);
      sourceBankLabelConstraints.anchor = GridBagConstraints.LINE_END;
      sourceBankLabelConstraints.weightx = 0.0;
      sourceBankLabelConstraints.weighty = 0.0;
      banksPane.add(sourceBankLabel, sourceBankLabelConstraints);

      final GridBagConstraints sourceBankPanelConstraints =
	new GridBagConstraints();
      final JPanel sourceBankPanel =
	new JPanel(new FlowLayout(FlowLayout.LEADING, 3, 0));
      sourceBankPanelConstraints.gridx = 1;
      sourceBankPanelConstraints.gridy = 0;
      sourceBankPanelConstraints.gridwidth = GridBagConstraints.REMAINDER;
      sourceBankPanelConstraints.anchor = GridBagConstraints.LINE_START;
      sourceBankPanelConstraints.weightx = 0.0;
      sourceBankPanelConstraints.weighty = 0.0;

      final ButtonGroup sourceBankGroup = new ButtonGroup();
      for (Block bank: banks) {
	final JRadioButton sourceBankRadioButton =
	  new JRadioButton(bank.getName());
	sourceBankRadioButtons.add(sourceBankRadioButton);
	sourceBankPanel.add(sourceBankRadioButton);
	sourceBankGroup.add(sourceBankRadioButton);
      }
      sourceBankRadioButtons.get(0).setSelected(true);

      banksPane.add(sourceBankPanel, sourceBankPanelConstraints);

      final GridBagConstraints destinationBankLabelConstraints =
	new GridBagConstraints();
      final JLabel destinationBankLabel =
	new JLabel(Application.getString(this, "bank.destination") + ":");
      destinationBankLabelConstraints.gridx = 0;
      destinationBankLabelConstraints.gridy = 1;
      destinationBankLabelConstraints.insets = new Insets(0, 3, 0, 0);
      destinationBankLabelConstraints.anchor = GridBagConstraints.LINE_END;
      destinationBankLabelConstraints.weightx = 0.0;
      destinationBankLabelConstraints.weighty = 0.0;
      banksPane.add(destinationBankLabel, destinationBankLabelConstraints);

      final GridBagConstraints destinationBankPanelConstraints =
	new GridBagConstraints();
      final JPanel destinationBankPanel =
	new JPanel(new FlowLayout(FlowLayout.LEADING, 3, 0));
      destinationBankPanelConstraints.gridx = 1;
      destinationBankPanelConstraints.gridy = 1;
      destinationBankPanelConstraints.gridwidth = GridBagConstraints.REMAINDER;
      destinationBankPanelConstraints.anchor = GridBagConstraints.LINE_START;
      destinationBankPanelConstraints.weightx = 0.0;
      destinationBankPanelConstraints.weighty = 0.0;

      final ButtonGroup destinationBankGroup = new ButtonGroup();
      for (Block bank: banks) {
	final JRadioButton destinationBankRadioButton =
	  new JRadioButton(bank.getName());
	destinationBankRadioButtons.add(destinationBankRadioButton);
	destinationBankPanel.add(destinationBankRadioButton);
	destinationBankGroup.add(destinationBankRadioButton);
      }
      destinationBankRadioButtons.get(0).setSelected(true);
      banksPane.add(destinationBankPanel, destinationBankPanelConstraints);

      add(banksPane, banksPaneConstraints);
      line++;
    }
    
    final GridBagConstraints copyRadioConstraints =
      new GridBagConstraints();
    copyRadio = new JRadioButton(Application.getString(this, "copy"));
    copyRadio.setSelected(true);
    copyRadioConstraints.gridx = 0;
    copyRadioConstraints.gridy = line;
    copyRadioConstraints.insets = new Insets(2, 0, 0, 0);
    copyRadioConstraints.anchor = GridBagConstraints.LINE_START;
    copyRadioConstraints.weightx = 0.0;
    copyRadioConstraints.weighty = 0.0;
    add(copyRadio, copyRadioConstraints);
    copyFillCompareGroup.add(copyRadio);
	
    final GridBagConstraints copyStartConstraints =
      new GridBagConstraints();
    final JLabel copyStart =
      new JLabel(Application.getString(this, "copy.start") + ":");
    copyStartConstraints.gridx = 1;
    copyStartConstraints.gridy = line;
    copyStartConstraints.insets = new Insets(2, 10, 0, 0);
    copyStartConstraints.anchor = GridBagConstraints.LINE_END;
    copyStartConstraints.weightx = 0.0;
    copyStartConstraints.weighty = 0.0;
    add(copyStart, copyStartConstraints);

    final GridBagConstraints copyStartFieldConstraints =
      new GridBagConstraints();
    copyStartField = new HexField(4);
    copyStart.setLabelFor(copyStartField);
    copyStartField.addMouseListener(new RadioClick(copyRadio));
    copyStartFieldConstraints.gridx = 2;
    copyStartFieldConstraints.gridy = line;
    copyStartFieldConstraints.insets = new Insets(2, 3, 0, 10);
    copyStartFieldConstraints.anchor = GridBagConstraints.LINE_START;
    copyStartFieldConstraints.weightx = 0.0;
    copyStartFieldConstraints.weighty = 0.0;
    add(copyStartField, copyStartFieldConstraints);

    final GridBagConstraints copyEndConstraints =
      new GridBagConstraints();
    final JLabel copyEnd =
      new JLabel(Application.getString(this, "copy.end") + ":");
    copyEndConstraints.gridx = 3;
    copyEndConstraints.gridy = line;
    copyEndConstraints.insets = new Insets(2, 10, 0, 0);
    copyEndConstraints.anchor = GridBagConstraints.LINE_END;
    copyEndConstraints.weightx = 0.0;
    copyEndConstraints.weighty = 0.0;
    add(copyEnd, copyEndConstraints);

    final GridBagConstraints copyEndFieldConstraints =
      new GridBagConstraints();
    copyEndField = new HexField(4);
    copyEnd.setLabelFor(copyEndField);
    copyEndField.addMouseListener(new RadioClick(copyRadio));
    copyEndFieldConstraints.gridx = 4;
    copyEndFieldConstraints.gridy = line;
    copyEndFieldConstraints.insets = new Insets(2, 3, 0, 10);
    copyEndFieldConstraints.anchor = GridBagConstraints.LINE_START;
    copyEndFieldConstraints.weightx = 0.0;
    copyEndFieldConstraints.weighty = 0.0;
    add(copyEndField, copyEndFieldConstraints);

    final GridBagConstraints copyDestinationConstraints =
      new GridBagConstraints();
    final JLabel copyDestination =
      new JLabel(Application.getString(this, "copy.destination") + ":");
    copyDestinationConstraints.gridx = 5;
    copyDestinationConstraints.gridy = line;
    copyDestinationConstraints.gridwidth = 2;
    copyDestinationConstraints.insets = new Insets(2, 10, 0, 0);
    copyDestinationConstraints.anchor = GridBagConstraints.LINE_END;
    copyDestinationConstraints.weightx = 0.0;
    copyDestinationConstraints.weighty = 0.0;
    add(copyDestination, copyDestinationConstraints);

    final GridBagConstraints copyDestinationFieldConstraints =
      new GridBagConstraints();
    copyDestinationField = new HexField(4);
    copyDestination.setLabelFor(copyDestinationField);
    copyDestinationField.addMouseListener(new RadioClick(copyRadio));
    copyDestinationFieldConstraints.gridx = 7;
    copyDestinationFieldConstraints.gridy = line;
    copyDestinationFieldConstraints.gridwidth = GridBagConstraints.REMAINDER;
    copyDestinationFieldConstraints.insets = new Insets(2, 3, 0, 0);
    copyDestinationFieldConstraints.anchor = GridBagConstraints.LINE_START;
    copyDestinationFieldConstraints.weightx = 1.0;
    copyDestinationFieldConstraints.weighty = 0.0;
    add(copyDestinationField, copyDestinationFieldConstraints);

    line++;
    
    final GridBagConstraints fillRadioConstraints =
      new GridBagConstraints();
    fillRadio = new JRadioButton(Application.getString(this, "fill"));
    fillRadioConstraints.gridx = 0;
    fillRadioConstraints.gridy = line;
    fillRadioConstraints.anchor = GridBagConstraints.LINE_START;
    fillRadioConstraints.weightx = 0.0;
    fillRadioConstraints.weighty = 0.0;
    add(fillRadio, fillRadioConstraints);
    copyFillCompareGroup.add(fillRadio);
	
    final GridBagConstraints fillStartConstraints =
      new GridBagConstraints();
    final JLabel fillStart =
      new JLabel(Application.getString(this, "fill.start") + ":");
    fillStartConstraints.gridx = 1;
    fillStartConstraints.gridy = line;
    fillStartConstraints.insets = new Insets(0, 10, 0, 0);
    fillStartConstraints.anchor = GridBagConstraints.LINE_END;
    fillStartConstraints.weightx = 0.0;
    fillStartConstraints.weighty = 0.0;
    add(fillStart, fillStartConstraints);

    final GridBagConstraints fillStartFieldConstraints =
      new GridBagConstraints();
    fillStartField = new HexField(4);
    fillStart.setLabelFor(fillStartField);
    fillStartField.addMouseListener(new RadioClick(fillRadio));
    fillStartFieldConstraints.gridx = 2;
    fillStartFieldConstraints.gridy = line;
    fillStartFieldConstraints.insets = new Insets(0, 3, 0, 10);
    fillStartFieldConstraints.anchor = GridBagConstraints.LINE_START;
    fillStartFieldConstraints.weightx = 0.0;
    fillStartFieldConstraints.weighty = 0.0;
    add(fillStartField, fillStartFieldConstraints);

    final GridBagConstraints fillEndConstraints =
      new GridBagConstraints();
    final JLabel fillEnd =
      new JLabel(Application.getString(this, "fill.end") + ":");
    fillEndConstraints.gridx = 3;
    fillEndConstraints.gridy = line;
    fillEndConstraints.insets = new Insets(0, 10, 0, 0);
    fillEndConstraints.anchor = GridBagConstraints.LINE_END;
    fillEndConstraints.weightx = 0.0;
    fillEndConstraints.weighty = 0.0;
    add(fillEnd, fillEndConstraints);

    final GridBagConstraints fillEndFieldConstraints =
      new GridBagConstraints();
    fillEndField = new HexField(4);
    fillEnd.setLabelFor(fillEndField);
    fillEndField.addMouseListener(new RadioClick(fillRadio));
    fillEndFieldConstraints.gridx = 4;
    fillEndFieldConstraints.gridy = line;
    fillEndFieldConstraints.insets = new Insets(0, 3, 0, 10);
    fillEndFieldConstraints.anchor = GridBagConstraints.LINE_START;
    fillEndFieldConstraints.weightx = 0.0;
    fillEndFieldConstraints.weighty = 0.0;
    add(fillEndField, fillEndFieldConstraints);

    final GridBagConstraints fillDataConstraints =
      new GridBagConstraints();
    final JLabel fillData =
      new JLabel(Application.getString(this, "fill.data") + ":");
    fillDataConstraints.gridx = 5;
    fillDataConstraints.gridy = line;
    fillDataConstraints.insets = new Insets(0, 10, 0, 0);
    fillDataConstraints.anchor = GridBagConstraints.LINE_END;
    fillDataConstraints.weightx = 0.0;
    fillDataConstraints.weighty = 0.0;
    add(fillData, fillDataConstraints);

    final GridBagConstraints fillDataFieldConstraints =
      new GridBagConstraints();
    fillDataField = new HexField(2);
    fillData.setLabelFor(fillDataField);
    fillDataField.addMouseListener(new RadioClick(fillRadio));
    fillDataFieldConstraints.gridx = 6;
    fillDataFieldConstraints.gridy = line;
    fillDataFieldConstraints.gridwidth = GridBagConstraints.REMAINDER;
    fillDataFieldConstraints.insets = new Insets(0, 3, 0, 0);
    fillDataFieldConstraints.anchor = GridBagConstraints.LINE_START;
    fillDataFieldConstraints.weightx = 0.0;
    fillDataFieldConstraints.weighty = 0.0;
    add(fillDataField, fillDataFieldConstraints);

    line++;
    
    final GridBagConstraints compareRadioConstraints =
      new GridBagConstraints();
    compareRadio = new JRadioButton(Application.getString(this, "compare"));
    compareRadio.setSelected(true);
    compareRadioConstraints.gridx = 0;
    compareRadioConstraints.gridy = line;
    compareRadioConstraints.anchor = GridBagConstraints.LINE_START;
    compareRadioConstraints.weightx = 0.0;
    compareRadioConstraints.weighty = 0.0;
    add(compareRadio, compareRadioConstraints);
    copyFillCompareGroup.add(compareRadio);
	
    final GridBagConstraints compareStartConstraints =
      new GridBagConstraints();
    final JLabel compareStart =
      new JLabel(Application.getString(this, "compare.start") + ":");
    compareStartConstraints.gridx = 1;
    compareStartConstraints.gridy = line;
    compareStartConstraints.insets = new Insets(0, 10, 0, 0);
    compareStartConstraints.anchor = GridBagConstraints.LINE_END;
    compareStartConstraints.weightx = 0.0;
    compareStartConstraints.weighty = 0.0;
    add(compareStart, compareStartConstraints);

    final GridBagConstraints compareStartFieldConstraints =
      new GridBagConstraints();
    compareStartField = new HexField(4);
    compareStart.setLabelFor(compareStartField);
    compareStartField.addMouseListener(new RadioClick(compareRadio));
    compareStartFieldConstraints.gridx = 2;
    compareStartFieldConstraints.gridy = line;
    compareStartFieldConstraints.insets = new Insets(0, 3, 0, 10);
    compareStartFieldConstraints.anchor = GridBagConstraints.LINE_START;
    compareStartFieldConstraints.weightx = 0.0;
    compareStartFieldConstraints.weighty = 0.0;
    add(compareStartField, compareStartFieldConstraints);

    final GridBagConstraints compareEndConstraints =
      new GridBagConstraints();
    final JLabel compareEnd =
      new JLabel(Application.getString(this, "compare.end") + ":");
    compareEndConstraints.gridx = 3;
    compareEndConstraints.gridy = line;
    compareEndConstraints.insets = new Insets(0, 10, 0, 0);
    compareEndConstraints.anchor = GridBagConstraints.LINE_END;
    compareEndConstraints.weightx = 0.0;
    compareEndConstraints.weighty = 0.0;
    add(compareEnd, compareEndConstraints);

    final GridBagConstraints compareEndFieldConstraints =
      new GridBagConstraints();
    compareEndField = new HexField(4);
    compareEnd.setLabelFor(compareEndField);
    compareEndField.addMouseListener(new RadioClick(compareRadio));
    compareEndFieldConstraints.gridx = 4;
    compareEndFieldConstraints.gridy = line;
    compareEndFieldConstraints.insets = new Insets(0, 3, 0, 10);
    compareEndFieldConstraints.anchor = GridBagConstraints.LINE_START;
    compareEndFieldConstraints.weightx = 0.0;
    compareEndFieldConstraints.weighty = 0.0;
    add(compareEndField, compareEndFieldConstraints);

    final GridBagConstraints compareDestinationConstraints =
      new GridBagConstraints();
    final JLabel compareDestination =
      new JLabel(Application.getString(this, "compare.destination") + ":");
    compareDestinationConstraints.gridx = 5;
    compareDestinationConstraints.gridy = line;
    compareDestinationConstraints.gridwidth = 3;
    compareDestinationConstraints.insets = new Insets(0, 10, 0, 0);
    compareDestinationConstraints.anchor = GridBagConstraints.LINE_END;
    compareDestinationConstraints.weightx = 0.0;
    compareDestinationConstraints.weighty = 0.0;
    add(compareDestination, compareDestinationConstraints);

    final GridBagConstraints compareDestinationFieldConstraints =
      new GridBagConstraints();
    compareDestinationField = new HexField(4);
    compareDestination.setLabelFor(compareDestinationField);
    compareDestinationField.addMouseListener(new RadioClick(compareRadio));
    compareDestinationFieldConstraints.gridx = 8;
    compareDestinationFieldConstraints.gridy = line;
    compareDestinationFieldConstraints.gridwidth = GridBagConstraints.REMAINDER;
    compareDestinationFieldConstraints.insets = new Insets(0, 3, 0, 0);
    compareDestinationFieldConstraints.anchor = GridBagConstraints.LINE_START;
    compareDestinationFieldConstraints.weightx = 1.0;
    compareDestinationFieldConstraints.weighty = 0.0;
    add(compareDestinationField, compareDestinationFieldConstraints);

    line++;
    
    final GridBagConstraints copyFillCompareButtonsConstraints =
      new GridBagConstraints();
    final JPanel copyFillCompareButtonsPanel =
      new JPanel(new FlowLayout(FlowLayout.TRAILING));
    copyFillCompareButtonsConstraints.gridx = 0;
    copyFillCompareButtonsConstraints.gridy = line;
    copyFillCompareButtonsConstraints.gridwidth = GridBagConstraints.REMAINDER;
    copyFillCompareButtonsConstraints.anchor = GridBagConstraints.LAST_LINE_END;
    copyFillCompareButtonsConstraints.weightx = 0.0;
    copyFillCompareButtonsConstraints.weighty = 1.0;
    final JButton copyFillCompareButton = new JButton(Application.getString(
      this, "copyFillCompare.button.copyFillCompare"));
    defaultButton = copyFillCompareButton;
    copyFillCompareButton.addActionListener(new CopyFillCompareListener());
    copyFillCompareButtonsPanel.add(copyFillCompareButton);
    final JButton copyFillCompareCloseButton = new JButton(
      Application.getString(this, "copyFillCompare.button.close"));
    copyFillCompareCloseButton.addActionListener(new CloseListener());
    copyFillCompareButtonsPanel.add(copyFillCompareCloseButton);
    add(copyFillCompareButtonsPanel, copyFillCompareButtonsConstraints);

    log.fine("Memory/CopyFillCompare panel set up");
  }

  // copy/fill/compare listener
  private class CopyFillCompareListener implements ActionListener {

    // for description see ActionListener
    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Copy/fill/compare listener action started");
      if (numberBanks > 1) {
	for (JRadioButton button: sourceBankRadioButtons) {
	  if (button.isSelected()) {
	    sourceMemoryBank = button.getText();
	    log.fine("Source memory bank selected: " + sourceMemoryBank);
	    break;
	  }
	}
	for (JRadioButton button: destinationBankRadioButtons) {
	  if (button.isSelected()) {
	    destinationMemoryBank = button.getText();
	    log.fine("Destination memory bank selected: " +
		     destinationMemoryBank);
	    break;
	  }
	}
      }
      int start = 0, end = 0, destination = 0, data = 0, number = 0;
      try {
	if (copyRadio.isSelected()) {
	  start = copyStartField.getValue();
	  end = copyEndField.getValue();
	  destination = copyDestinationField.getValue();
	} else if (fillRadio.isSelected()) {
	  start = fillStartField.getValue();
	  end = fillEndField.getValue();
	  data = fillDataField.getValue();
	} else {	    
	  start = compareStartField.getValue();
	  end = compareEndField.getValue();
	  destination = compareDestinationField.getValue();
	}
      } catch (final NumberFormatException exception) {
	InfoBox.display(panel, Application.getString(this, "incompleteForm"));
	return;
      }
      if (copyRadio.isSelected()) {
	number = new MemoryProcessor(
          panel.getHardware(),
	  sourceMemoryBank,
	  destinationMemoryBank).copy(start, end, destination);
	InfoBox.display(
	  panel,
	  String.format(Application.getString(this, "copied"), number));
      } else if (fillRadio.isSelected()) {
	number = new MemoryProcessor(
	  panel.getHardware(),
	  sourceMemoryBank,
	  destinationMemoryBank).fill(start, end, data);
	InfoBox.display(
	  panel,
	  String.format(Application.getString(this, "filled"), number));
      } else {
	int result = new MemoryProcessor(
	  panel.getHardware(),
	  sourceMemoryBank,
	  destinationMemoryBank).compare(start, end, destination);
	if (result == -1) {
	  InfoBox.display(
	    panel,
	    Application.getString(this, "compared.identical"));
	} else {
	  InfoBox.display(
	    panel,
	    String.format(Application.getString(this, "compared.different"),
			  result));
	}
      }
    }
  }
}
