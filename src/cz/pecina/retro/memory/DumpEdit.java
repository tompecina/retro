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
import java.awt.Font;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.ButtonGroup;
import javax.swing.BorderFactory;

import javax.swing.border.Border;

import cz.pecina.retro.common.Application;

import cz.pecina.retro.cpu.Block;

import cz.pecina.retro.gui.RadioClick;
import cz.pecina.retro.gui.HexField;
import cz.pecina.retro.gui.InfoBox;

/**
 * Memory/DumpEdit panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class DumpEdit extends MemoryTab {

  // static logger
  private static final Logger log =
    Logger.getLogger(DumpEdit.class.getName());

  // number of hex lines
  private static final int NUMBER_HEX_LINES = 16;
  
  // number of bytes per line
  private static final int NUMBER_HEX_BYTES = 16;

  // monospaced font
  private static final Font FONT_MONOSPACED =
    new Font(Font.MONOSPACED, Font.PLAIN, 12);

  // components holding values used by listeners
  private JRadioButton dumpTypeHexRadio, dumpTypeDisRadio;
  private HexField dumpAddressField, editAddressField;
  private JTextField editDataField;
  
  // lists of bank selection radio buttons
  private List<JRadioButton> sourceBankRadioButtons = new ArrayList<>();
  private List<JRadioButton> destinationBankRadioButtons = new ArrayList<>();
      
  /**
   * Creates Memory/DumpEdit panel.
   *
   * @param panel enclosing panel
   */
  public DumpEdit(final MemoryPanel panel) {
    super(panel);
    log.fine("New Memory/DumpEdit creation started");
 
    setBorder(BorderFactory.createEmptyBorder(5, 8, 0, 8));
    final ButtonGroup dumpTypeGroup = new ButtonGroup();
    int line = 0;

    if (numberBanks > 1) {

      final GridBagConstraints sourceBankLabelConstraints =
	new GridBagConstraints();
      final JLabel sourceBankLabel =
	new JLabel(Application.getString(this, "dumpEdit.bank.dump") + ":");
      sourceBankLabelConstraints.gridx = 0;
      sourceBankLabelConstraints.gridy = line;
      sourceBankLabelConstraints.insets = new Insets(0, 3, 0, 0);
      sourceBankLabelConstraints.anchor = GridBagConstraints.LINE_END;
      sourceBankLabelConstraints.weightx = 0.0;
      sourceBankLabelConstraints.weighty = 0.0;
      add(sourceBankLabel, sourceBankLabelConstraints);

      final GridBagConstraints sourceBankPanelConstraints =
	new GridBagConstraints();
      final JPanel sourceBankPanel =
	new JPanel(new FlowLayout(FlowLayout.LEADING, 3, 0));
      sourceBankPanelConstraints.gridx = 1;
      sourceBankPanelConstraints.gridy = line;
      sourceBankPanelConstraints.anchor = GridBagConstraints.LINE_START;
      sourceBankPanelConstraints.weightx = 1.0;
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

      add(sourceBankPanel, sourceBankPanelConstraints);
      line++;

      final GridBagConstraints destinationBankLabelConstraints =
	new GridBagConstraints();
      final JLabel destinationBankLabel =
	new JLabel(Application.getString(this, "dumpEdit.bank.edit") + ":");
      destinationBankLabelConstraints.gridx = 0;
      destinationBankLabelConstraints.gridy = line;
      destinationBankLabelConstraints.insets = new Insets(0, 3, 0, 0);
      destinationBankLabelConstraints.anchor = GridBagConstraints.LINE_END;
      destinationBankLabelConstraints.weightx = 0.0;
      destinationBankLabelConstraints.weighty = 0.0;
      add(destinationBankLabel, destinationBankLabelConstraints);

      final GridBagConstraints destinationBankPanelConstraints =
	new GridBagConstraints();
      final JPanel destinationBankPanel =
	new JPanel(new FlowLayout(FlowLayout.LEADING, 3, 0));
      destinationBankPanelConstraints.gridx = 1;
      destinationBankPanelConstraints.gridy = line;
      destinationBankPanelConstraints.anchor = GridBagConstraints.LINE_START;
      destinationBankPanelConstraints.weightx = 1.0;
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
      add(destinationBankPanel, destinationBankPanelConstraints);
      line++;
    }
    
    final GridBagConstraints dumpAddressPaneConstraints =
      new GridBagConstraints();
    final JPanel dumpAddressPane =
      new JPanel(new GridBagLayout());
    dumpAddressPaneConstraints.gridx = 0;
    dumpAddressPaneConstraints.gridy = line;
    dumpAddressPaneConstraints.insets = new Insets(0, 3, 0, 0);
    dumpAddressPaneConstraints.gridwidth = GridBagConstraints.REMAINDER;
    dumpAddressPaneConstraints.anchor = GridBagConstraints.LINE_START;
    dumpAddressPaneConstraints.weightx = 0.0;
    dumpAddressPaneConstraints.weighty = 0.0;
    
    final GridBagConstraints dumpAddressLabelConstraints =
      new GridBagConstraints();
    final JLabel dumpAddressLabel =
      new JLabel(Application.getString(this, "dumpEdit.dump.address") + ":");
    dumpAddressLabelConstraints.gridx = 0;
    dumpAddressLabelConstraints.gridy = 0;
    dumpAddressLabelConstraints.insets = new Insets(0, 3, 0, 0);
    dumpAddressLabelConstraints.anchor = GridBagConstraints.LINE_END;
    dumpAddressLabelConstraints.weightx = 0.0;
    dumpAddressLabelConstraints.weighty = 0.0;
    dumpAddressPane.add(dumpAddressLabel, dumpAddressLabelConstraints);

    final GridBagConstraints dumpAddressFieldConstraints =
      new GridBagConstraints();
    dumpAddressField = new HexField(4);
    dumpAddressLabel.setLabelFor(dumpAddressField);
    dumpAddressFieldConstraints.gridx = 1;
    dumpAddressFieldConstraints.gridy = 0;
    dumpAddressFieldConstraints.insets = new Insets(0, 3, 0, 10);
    dumpAddressFieldConstraints.anchor = GridBagConstraints.LINE_START;
    dumpAddressFieldConstraints.weightx = 0.0;
    dumpAddressFieldConstraints.weighty = 0.0;
    dumpAddressField.setText("0");
    dumpAddressPane.add(dumpAddressField, dumpAddressFieldConstraints);
      
    final GridBagConstraints dumpRefreshButtonConstraints =
      new GridBagConstraints();
    final JButton dumpRefreshButton =
      new JButton(Application.getString(this, "dumpEdit.dump.button.refresh"));
    dumpRefreshButtonConstraints.gridx = 2;
    dumpRefreshButtonConstraints.gridy = 0;
    dumpRefreshButtonConstraints.anchor = GridBagConstraints.LINE_START;
    dumpRefreshButtonConstraints.weightx = 0.0;
    dumpRefreshButtonConstraints.weighty = 1.0;
    // dumpRefreshButton.addActionListener(new RefreshListener());
    dumpAddressPane.add(dumpRefreshButton, dumpRefreshButtonConstraints);

    final GridBagConstraints dumpTypeHexRadioConstraints =
      new GridBagConstraints();
    dumpTypeHexRadio =
      new JRadioButton(Application.getString(this, "dumpEdit.hex"));
    dumpTypeHexRadio.setSelected(true);
    dumpTypeHexRadioConstraints.gridx = 3;
    dumpTypeHexRadioConstraints.gridy = 0;
    dumpTypeHexRadioConstraints.anchor = GridBagConstraints.LINE_START;
    dumpTypeHexRadioConstraints.weightx = 0.0;
    dumpTypeHexRadioConstraints.weighty = 0.0;
    dumpTypeGroup.add(dumpTypeHexRadio);
    dumpAddressPane.add(dumpTypeHexRadio, dumpTypeHexRadioConstraints);

    final GridBagConstraints dumpTypeDisRadioConstraints =
      new GridBagConstraints();
    dumpTypeDisRadio =
      new JRadioButton(Application.getString(this, "dumpEdit.dis"));
    dumpTypeDisRadioConstraints.gridx = 4;
    dumpTypeDisRadioConstraints.gridy = 0;
    dumpTypeDisRadioConstraints.gridwidth = GridBagConstraints.REMAINDER;
    dumpTypeDisRadioConstraints.anchor = GridBagConstraints.LINE_START;
    dumpTypeDisRadioConstraints.weightx = 0.0;
    dumpTypeDisRadioConstraints.weighty = 0.0;
    dumpTypeGroup.add(dumpTypeDisRadio);
    dumpAddressPane.add(dumpTypeDisRadio, dumpTypeDisRadioConstraints);

    add(dumpAddressPane, dumpAddressPaneConstraints);
    line++;

    final GridBagConstraints dumpDataPaneConstraints =
      new GridBagConstraints();
    final JPanel dumpDataPane =
      new JPanel(new GridBagLayout());
    dumpDataPaneConstraints.gridx = 0;
    dumpDataPaneConstraints.gridy = line;
    dumpDataPaneConstraints.insets = new Insets(0, 3, 0, 0);
    dumpDataPaneConstraints.gridwidth = GridBagConstraints.REMAINDER;
    dumpDataPaneConstraints.anchor = GridBagConstraints.LINE_START;
    dumpDataPaneConstraints.weightx = 0.0;
    dumpDataPaneConstraints.weighty = 0.0;
    
    for (int i = 0; i < NUMBER_HEX_LINES; i++) {

      final GridBagConstraints dumpDataAddressConstraints =
	new GridBagConstraints();
      final int address = dumpAddressField.getValue() + (i * NUMBER_HEX_BYTES);
      final JLabel dumpDataAddress = new JLabel(String.format("%04x", address));
      dumpDataAddress.setFont(FONT_MONOSPACED);
      dumpDataAddressConstraints.gridx = 0;
      dumpDataAddressConstraints.gridy = i;
      dumpDataAddressConstraints.insets = new Insets(0, 3, 0, 0);
      dumpDataAddressConstraints.anchor = GridBagConstraints.LINE_START;
      dumpDataAddressConstraints.weightx = 0.0;
      dumpDataAddressConstraints.weighty = 0.0;
      dumpDataAddress.addMouseListener(new AddressListener(address));
      dumpDataPane.add(dumpDataAddress, dumpDataAddressConstraints);
      line++;
    }

    final GridBagConstraints hexLegendConstraints =
      new GridBagConstraints();
    final JLabel hexLegend =
      new JLabel(Application.getString(this, "dumpEdit.hex.legend"));
    hexLegendConstraints.gridx = 0;
    hexLegendConstraints.gridy = line;
    hexLegendConstraints.insets = new Insets(0, 3, 0, 0);
    hexLegendConstraints.gridwidth = GridBagConstraints.REMAINDER;
    hexLegendConstraints.anchor = GridBagConstraints.LINE_START;
    hexLegendConstraints.weightx = 0.0;
    hexLegendConstraints.weighty = 0.0;
    dumpDataPane.add(hexLegend, hexLegendConstraints);
    line++;

    add(dumpDataPane, dumpDataPaneConstraints);
    line++;

    final GridBagConstraints editPaneConstraints =
      new GridBagConstraints();
    final JPanel editPane =
      new JPanel(new GridBagLayout());
    editPaneConstraints.gridx = 0;
    editPaneConstraints.gridy = line;
    editPaneConstraints.insets = new Insets(0, 3, 0, 0);
    editPaneConstraints.gridwidth = GridBagConstraints.REMAINDER;
    editPaneConstraints.anchor = GridBagConstraints.LINE_START;
    editPaneConstraints.weightx = 0.0;
    editPaneConstraints.weighty = 0.0;
    
    final GridBagConstraints editAddressLabelConstraints =
      new GridBagConstraints();
    final JLabel editAddressLabel =
      new JLabel(Application.getString(this, "dumpEdit.edit.address") + ":");
    editAddressLabelConstraints.gridx = 0;
    editAddressLabelConstraints.gridy = 0;
    editAddressLabelConstraints.insets = new Insets(0, 3, 0, 0);
    editAddressLabelConstraints.anchor = GridBagConstraints.LINE_END;
    editAddressLabelConstraints.weightx = 0.0;
    editAddressLabelConstraints.weighty = 0.0;
    editPane.add(editAddressLabel, editAddressLabelConstraints);

    final GridBagConstraints editAddressFieldConstraints =
      new GridBagConstraints();
    editAddressField = new HexField(4);
    editAddressLabel.setLabelFor(editAddressField);
    editAddressFieldConstraints.gridx = 1;
    editAddressFieldConstraints.gridy = 0;
    editAddressFieldConstraints.insets = new Insets(0, 3, 0, 10);
    editAddressFieldConstraints.anchor = GridBagConstraints.LINE_START;
    editAddressFieldConstraints.weightx = 0.0;
    editAddressFieldConstraints.weighty = 0.0;
    editPane.add(editAddressField, editAddressFieldConstraints);
      
    final GridBagConstraints editDataLabelConstraints =
      new GridBagConstraints();
    final JLabel editDataLabel =
      new JLabel(Application.getString(this, "dumpEdit.edit.data") + ":");
    editDataLabelConstraints.gridx = 2;
    editDataLabelConstraints.gridy = 0;
    editDataLabelConstraints.insets = new Insets(0, 3, 0, 0);
    editDataLabelConstraints.anchor = GridBagConstraints.LINE_END;
    editDataLabelConstraints.weightx = 0.0;
    editDataLabelConstraints.weighty = 0.0;
    editPane.add(editDataLabel, editDataLabelConstraints);

    final GridBagConstraints editDataFieldConstraints =
      new GridBagConstraints();
    editDataField = new JTextField(40);
    editDataLabel.setLabelFor(editDataField);
    editDataFieldConstraints.gridx = 3;
    editDataFieldConstraints.gridy = 0;
    editDataFieldConstraints.insets = new Insets(0, 3, 0, 10);
    editDataFieldConstraints.gridwidth = GridBagConstraints.REMAINDER;
    editDataFieldConstraints.anchor = GridBagConstraints.LINE_START;
    editDataFieldConstraints.weightx = 0.0;
    editDataFieldConstraints.weighty = 0.0;
    editPane.add(editDataField, editDataFieldConstraints);
      
    add(editPane, editPaneConstraints);
    line++;



      

      
    // final GridBagConstraints copyRadioConstraints =
    //   new GridBagConstraints();
    // copyRadio = new JRadioButton(Application.getString(this, "copy"));
    // copyRadio.setSelected(true);
    // copyRadioConstraints.gridx = 0;
    // copyRadioConstraints.gridy = line;
    // copyRadioConstraints.anchor = GridBagConstraints.LINE_START;
    // copyRadioConstraints.weightx = 0.0;
    // copyRadioConstraints.weighty = 0.0;
    // add(copyRadio, copyRadioConstraints);
    // dumpEditGroup.add(copyRadio);
	
    // final GridBagConstraints copyStartConstraints =
    //   new GridBagConstraints();
    // final JLabel copyStart =
    //   new JLabel(Application.getString(this, "copy.start") + ":");
    // copyStartConstraints.gridx = 1;
    // copyStartConstraints.gridy = line;
    // copyStartConstraints.insets = new Insets(0, 10, 0, 0);
    // copyStartConstraints.anchor = GridBagConstraints.LINE_END;
    // copyStartConstraints.weightx = 0.0;
    // copyStartConstraints.weighty = 0.0;
    // add(copyStart, copyStartConstraints);

    // final GridBagConstraints copyStartFieldConstraints =
    //   new GridBagConstraints();
    // copyStartField = new HexField(4);
    // copyStart.setLabelFor(copyStartField);
    // copyStartField.addMouseListener(new RadioClick(copyRadio));
    // copyStartFieldConstraints.gridx = 2;
    // copyStartFieldConstraints.gridy = line;
    // copyStartFieldConstraints.insets = new Insets(0, 3, 0, 10);
    // copyStartFieldConstraints.anchor = GridBagConstraints.LINE_START;
    // copyStartFieldConstraints.weightx = 0.0;
    // copyStartFieldConstraints.weighty = 0.0;
    // add(copyStartField, copyStartFieldConstraints);

    // final GridBagConstraints copyEndConstraints =
    //   new GridBagConstraints();
    // final JLabel copyEnd =
    //   new JLabel(Application.getString(this, "copy.end") + ":");
    // copyEndConstraints.gridx = 3;
    // copyEndConstraints.gridy = line;
    // copyEndConstraints.insets = new Insets(0, 10, 0, 0);
    // copyEndConstraints.anchor = GridBagConstraints.LINE_END;
    // copyEndConstraints.weightx = 0.0;
    // copyEndConstraints.weighty = 0.0;
    // add(copyEnd, copyEndConstraints);

    // final GridBagConstraints copyEndFieldConstraints =
    //   new GridBagConstraints();
    // copyEndField = new HexField(4);
    // copyEnd.setLabelFor(copyEndField);
    // copyEndField.addMouseListener(new RadioClick(copyRadio));
    // copyEndFieldConstraints.gridx = 4;
    // copyEndFieldConstraints.gridy = line;
    // copyEndFieldConstraints.insets = new Insets(0, 3, 0, 10);
    // copyEndFieldConstraints.anchor = GridBagConstraints.LINE_START;
    // copyEndFieldConstraints.weightx = 0.0;
    // copyEndFieldConstraints.weighty = 0.0;
    // add(copyEndField, copyEndFieldConstraints);

    // final GridBagConstraints copyDestinationConstraints =
    //   new GridBagConstraints();
    // final JLabel copyDestination =
    //   new JLabel(Application.getString(this, "copy.destination") + ":");
    // copyDestinationConstraints.gridx = 5;
    // copyDestinationConstraints.gridy = line;
    // copyDestinationConstraints.gridwidth = 2;
    // copyDestinationConstraints.insets = new Insets(0, 10, 0, 0);
    // copyDestinationConstraints.anchor = GridBagConstraints.LINE_END;
    // copyDestinationConstraints.weightx = 0.0;
    // copyDestinationConstraints.weighty = 0.0;
    // add(copyDestination, copyDestinationConstraints);

    // final GridBagConstraints copyDestinationFieldConstraints =
    //   new GridBagConstraints();
    // copyDestinationField = new HexField(4);
    // copyDestination.setLabelFor(copyDestinationField);
    // copyDestinationField.addMouseListener(new RadioClick(copyRadio));
    // copyDestinationFieldConstraints.gridx = 7;
    // copyDestinationFieldConstraints.gridy = line;
    // copyDestinationFieldConstraints.gridwidth = GridBagConstraints.REMAINDER;
    // copyDestinationFieldConstraints.insets = new Insets(0, 3, 0, 0);
    // copyDestinationFieldConstraints.anchor = GridBagConstraints.LINE_START;
    // copyDestinationFieldConstraints.weightx = 1.0;
    // copyDestinationFieldConstraints.weighty = 0.0;
    // add(copyDestinationField, copyDestinationFieldConstraints);

    // line++;
    
    // final GridBagConstraints fillRadioConstraints =
    //   new GridBagConstraints();
    // fillRadio = new JRadioButton(Application.getString(this, "fill"));
    // fillRadioConstraints.gridx = 0;
    // fillRadioConstraints.gridy = line;
    // fillRadioConstraints.anchor = GridBagConstraints.LINE_START;
    // fillRadioConstraints.weightx = 0.0;
    // fillRadioConstraints.weighty = 0.0;
    // add(fillRadio, fillRadioConstraints);
    // dumpEditGroup.add(fillRadio);
	
    // final GridBagConstraints fillStartConstraints =
    //   new GridBagConstraints();
    // final JLabel fillStart =
    //   new JLabel(Application.getString(this, "fill.start") + ":");
    // fillStartConstraints.gridx = 1;
    // fillStartConstraints.gridy = line;
    // fillStartConstraints.insets = new Insets(0, 10, 0, 0);
    // fillStartConstraints.anchor = GridBagConstraints.LINE_END;
    // fillStartConstraints.weightx = 0.0;
    // fillStartConstraints.weighty = 0.0;
    // add(fillStart, fillStartConstraints);

    // final GridBagConstraints fillStartFieldConstraints =
    //   new GridBagConstraints();
    // fillStartField = new HexField(4);
    // fillStart.setLabelFor(fillStartField);
    // fillStartField.addMouseListener(new RadioClick(fillRadio));
    // fillStartFieldConstraints.gridx = 2;
    // fillStartFieldConstraints.gridy = line;
    // fillStartFieldConstraints.insets = new Insets(0, 3, 0, 10);
    // fillStartFieldConstraints.anchor = GridBagConstraints.LINE_START;
    // fillStartFieldConstraints.weightx = 0.0;
    // fillStartFieldConstraints.weighty = 0.0;
    // add(fillStartField, fillStartFieldConstraints);

    // final GridBagConstraints fillEndConstraints =
    //   new GridBagConstraints();
    // final JLabel fillEnd =
    //   new JLabel(Application.getString(this, "fill.end") + ":");
    // fillEndConstraints.gridx = 3;
    // fillEndConstraints.gridy = line;
    // fillEndConstraints.insets = new Insets(0, 10, 0, 0);
    // fillEndConstraints.anchor = GridBagConstraints.LINE_END;
    // fillEndConstraints.weightx = 0.0;
    // fillEndConstraints.weighty = 0.0;
    // add(fillEnd, fillEndConstraints);

    // final GridBagConstraints fillEndFieldConstraints =
    //   new GridBagConstraints();
    // fillEndField = new HexField(4);
    // fillEnd.setLabelFor(fillEndField);
    // fillEndField.addMouseListener(new RadioClick(fillRadio));
    // fillEndFieldConstraints.gridx = 4;
    // fillEndFieldConstraints.gridy = line;
    // fillEndFieldConstraints.insets = new Insets(0, 3, 0, 10);
    // fillEndFieldConstraints.anchor = GridBagConstraints.LINE_START;
    // fillEndFieldConstraints.weightx = 0.0;
    // fillEndFieldConstraints.weighty = 0.0;
    // add(fillEndField, fillEndFieldConstraints);

    // final GridBagConstraints fillDataConstraints =
    //   new GridBagConstraints();
    // final JLabel fillData =
    //   new JLabel(Application.getString(this, "fill.data") + ":");
    // fillDataConstraints.gridx = 5;
    // fillDataConstraints.gridy = line;
    // fillDataConstraints.insets = new Insets(0, 10, 0, 0);
    // fillDataConstraints.anchor = GridBagConstraints.LINE_END;
    // fillDataConstraints.weightx = 0.0;
    // fillDataConstraints.weighty = 0.0;
    // add(fillData, fillDataConstraints);

    // final GridBagConstraints fillDataFieldConstraints =
    //   new GridBagConstraints();
    // fillDataField = new HexField(2);
    // fillData.setLabelFor(fillDataField);
    // fillDataField.addMouseListener(new RadioClick(fillRadio));
    // fillDataFieldConstraints.gridx = 6;
    // fillDataFieldConstraints.gridy = line;
    // fillDataFieldConstraints.gridwidth = GridBagConstraints.REMAINDER;
    // fillDataFieldConstraints.insets = new Insets(0, 3, 0, 0);
    // fillDataFieldConstraints.anchor = GridBagConstraints.LINE_START;
    // fillDataFieldConstraints.weightx = 0.0;
    // fillDataFieldConstraints.weighty = 0.0;
    // add(fillDataField, fillDataFieldConstraints);

    // line++;
    
    // final GridBagConstraints compareRadioConstraints =
    //   new GridBagConstraints();
    // compareRadio = new JRadioButton(Application.getString(this, "compare"));
    // compareRadio.setSelected(true);
    // compareRadioConstraints.gridx = 0;
    // compareRadioConstraints.gridy = line;
    // compareRadioConstraints.anchor = GridBagConstraints.LINE_START;
    // compareRadioConstraints.weightx = 0.0;
    // compareRadioConstraints.weighty = 0.0;
    // add(compareRadio, compareRadioConstraints);
    // dumpEditGroup.add(compareRadio);
	
    // final GridBagConstraints compareStartConstraints =
    //   new GridBagConstraints();
    // final JLabel compareStart =
    //   new JLabel(Application.getString(this, "compare.start") + ":");
    // compareStartConstraints.gridx = 1;
    // compareStartConstraints.gridy = line;
    // compareStartConstraints.insets = new Insets(0, 10, 0, 0);
    // compareStartConstraints.anchor = GridBagConstraints.LINE_END;
    // compareStartConstraints.weightx = 0.0;
    // compareStartConstraints.weighty = 0.0;
    // add(compareStart, compareStartConstraints);

    // final GridBagConstraints compareStartFieldConstraints =
    //   new GridBagConstraints();
    // compareStartField = new HexField(4);
    // compareStart.setLabelFor(compareStartField);
    // compareStartField.addMouseListener(new RadioClick(compareRadio));
    // compareStartFieldConstraints.gridx = 2;
    // compareStartFieldConstraints.gridy = line;
    // compareStartFieldConstraints.insets = new Insets(0, 3, 0, 10);
    // compareStartFieldConstraints.anchor = GridBagConstraints.LINE_START;
    // compareStartFieldConstraints.weightx = 0.0;
    // compareStartFieldConstraints.weighty = 0.0;
    // add(compareStartField, compareStartFieldConstraints);

    // final GridBagConstraints compareEndConstraints =
    //   new GridBagConstraints();
    // final JLabel compareEnd =
    //   new JLabel(Application.getString(this, "compare.end") + ":");
    // compareEndConstraints.gridx = 3;
    // compareEndConstraints.gridy = line;
    // compareEndConstraints.insets = new Insets(0, 10, 0, 0);
    // compareEndConstraints.anchor = GridBagConstraints.LINE_END;
    // compareEndConstraints.weightx = 0.0;
    // compareEndConstraints.weighty = 0.0;
    // add(compareEnd, compareEndConstraints);

    // final GridBagConstraints compareEndFieldConstraints =
    //   new GridBagConstraints();
    // compareEndField = new HexField(4);
    // compareEnd.setLabelFor(compareEndField);
    // compareEndField.addMouseListener(new RadioClick(compareRadio));
    // compareEndFieldConstraints.gridx = 4;
    // compareEndFieldConstraints.gridy = line;
    // compareEndFieldConstraints.insets = new Insets(0, 3, 0, 10);
    // compareEndFieldConstraints.anchor = GridBagConstraints.LINE_START;
    // compareEndFieldConstraints.weightx = 0.0;
    // compareEndFieldConstraints.weighty = 0.0;
    // add(compareEndField, compareEndFieldConstraints);

    // final GridBagConstraints compareDestinationConstraints =
    //   new GridBagConstraints();
    // final JLabel compareDestination =
    //   new JLabel(Application.getString(this, "compare.destination") + ":");
    // compareDestinationConstraints.gridx = 5;
    // compareDestinationConstraints.gridy = line;
    // compareDestinationConstraints.gridwidth = 3;
    // compareDestinationConstraints.insets = new Insets(0, 10, 0, 0);
    // compareDestinationConstraints.anchor = GridBagConstraints.LINE_END;
    // compareDestinationConstraints.weightx = 0.0;
    // compareDestinationConstraints.weighty = 0.0;
    // add(compareDestination, compareDestinationConstraints);

    // final GridBagConstraints compareDestinationFieldConstraints =
    //   new GridBagConstraints();
    // compareDestinationField = new HexField(4);
    // compareDestination.setLabelFor(compareDestinationField);
    // compareDestinationField.addMouseListener(new RadioClick(compareRadio));
    // compareDestinationFieldConstraints.gridx = 8;
    // compareDestinationFieldConstraints.gridy = line;
    // compareDestinationFieldConstraints.gridwidth = GridBagConstraints.REMAINDER;
    // compareDestinationFieldConstraints.insets = new Insets(0, 3, 0, 0);
    // compareDestinationFieldConstraints.anchor = GridBagConstraints.LINE_START;
    // compareDestinationFieldConstraints.weightx = 1.0;
    // compareDestinationFieldConstraints.weighty = 0.0;
    // add(compareDestinationField, compareDestinationFieldConstraints);

    // line++;
    
    final GridBagConstraints dumpEditButtonsConstraints =
      new GridBagConstraints();
    final JPanel dumpEditButtonsPanel =
      new JPanel(new FlowLayout(FlowLayout.TRAILING));
    dumpEditButtonsConstraints.gridx = 0;
    dumpEditButtonsConstraints.gridy = line;
    dumpEditButtonsConstraints.gridwidth = GridBagConstraints.REMAINDER;
    dumpEditButtonsConstraints.anchor = GridBagConstraints.LAST_LINE_END;
    dumpEditButtonsConstraints.weightx = 0.0;
    dumpEditButtonsConstraints.weighty = 1.0;
    final JButton editButton = new JButton(Application.getString(
      this, "dumpEdit.button.edit"));
    defaultButton = editButton;
    // editButton.addActionListener(new EditListener());
    dumpEditButtonsPanel.add(editButton);
    final JButton dumpEditCloseButton = new JButton(
      Application.getString(this, "dumpEdit.button.close"));
    dumpEditCloseButton.addActionListener(new CloseListener());
    dumpEditButtonsPanel.add(dumpEditCloseButton);
    add(dumpEditButtonsPanel, dumpEditButtonsConstraints);

    log.fine("Memory/DumpEdit panel set up");
  }

  // // edit listener
  // private class EditListener implements ActionListener {

  //   // for description see ActionListener
  //   @Override
  //   public void actionPerformed(final ActionEvent event) {
  //     log.finer("Copy/fill/compare listener action started");
  //     if (numberBanks > 1) {
  // 	for (JRadioButton button: sourceBankRadioButtons) {
  // 	  if (button.isSelected()) {
  // 	    sourceMemoryBank = button.getText();
  // 	    log.fine("Source memory bank selected: " + sourceMemoryBank);
  // 	    break;
  // 	  }
  // 	}
  // 	for (JRadioButton button: destinationBankRadioButtons) {
  // 	  if (button.isSelected()) {
  // 	    destinationMemoryBank = button.getText();
  // 	    log.fine("Destination memory bank selected: " +
  // 		     destinationMemoryBank);
  // 	    break;
  // 	  }
  // 	}
  //     }
  //     int start = 0, end = 0, destination = 0, data = 0, number = 0;
  //     try {
  // 	if (copyRadio.isSelected()) {
  // 	  start = copyStartField.getValue();
  // 	  end = copyEndField.getValue();
  // 	  destination = copyDestinationField.getValue();
  // 	} else if (fillRadio.isSelected()) {
  // 	  start = fillStartField.getValue();
  // 	  end = fillEndField.getValue();
  // 	  data = fillDataField.getValue();
  // 	} else {	    
  // 	  start = compareStartField.getValue();
  // 	  end = compareEndField.getValue();
  // 	  destination = compareDestinationField.getValue();
  // 	}
  //     } catch (NumberFormatException exception) {
  // 	InfoBox.display(panel, Application.getString(this, "incompleteForm"));
  // 	return;
  //     }
  //     if (copyRadio.isSelected()) {
  // 	number = new MemoryProcessor(
  //         panel.getHardware(),
  // 	  sourceMemoryBank,
  // 	  destinationMemoryBank).copy(start, end, destination);
  // 	InfoBox.display(
  // 	  panel,
  // 	  String.format(Application.getString(this, "copied"), number));
  //     } else if (fillRadio.isSelected()) {
  // 	number = new MemoryProcessor(
  // 	  panel.getHardware(),
  // 	  sourceMemoryBank,
  // 	  destinationMemoryBank).fill(start, end, data);
  // 	InfoBox.display(
  // 	  panel,
  // 	  String.format(Application.getString(this, "filled"), number));
  //     } else {
  // 	int result = new MemoryProcessor(
  // 	  panel.getHardware(),
  // 	  sourceMemoryBank,
  // 	  destinationMemoryBank).compare(start, end, destination);
  // 	if (result == -1) {
  // 	  InfoBox.display(
  // 	    panel,
  // 	    Application.getString(this, "compared.identical"));
  // 	} else {
  // 	  InfoBox.display(
  // 	    panel,
  // 	    String.format(Application.getString(this, "compared.different"),
  // 			  result));
  // 	}
  //     }
  //   }
  // }

  // address listener
  private class AddressListener extends MouseAdapter {

    private int address;

    public AddressListener(final int address) {
      assert (address >= 0) && (address < 0x10000);
      this.address = address;
    }
      
    // for description see MouseListener
    @Override
    public void mouseClicked(final MouseEvent event) {
      log.finer("Address listener action detected");
      editAddressField.setText(String.format("%04x", address));
    }
  }
}
