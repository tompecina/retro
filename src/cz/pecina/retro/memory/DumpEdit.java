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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.ButtonGroup;
import javax.swing.BorderFactory;

import javax.swing.border.Border;

import cz.pecina.retro.common.Application;
import cz.pecina.retro.common.Parameters;

import cz.pecina.retro.cpu.Block;

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
      
  // mutable inner pane
  private JPanel dumpDataPane;
  private GridBagConstraints dumpDataPaneConstraints;
  
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
    dumpRefreshButton.addActionListener(new RefreshListener());
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

    dumpDataPaneConstraints = new GridBagConstraints();
    dumpDataPaneConstraints.gridx = 0;
    dumpDataPaneConstraints.gridy = line;
    dumpDataPaneConstraints.insets = new Insets(0, 3, 0, 0);
    dumpDataPaneConstraints.gridwidth = GridBagConstraints.REMAINDER;
    dumpDataPaneConstraints.anchor = GridBagConstraints.LINE_START;
    dumpDataPaneConstraints.weightx = 0.0;
    dumpDataPaneConstraints.weighty = 0.0;
    
    updateDumpDataPane();
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

    panel.getFrame().addComponentListener(new ComponentListener());

    log.fine("Memory/DumpEdit panel set up");
  }

  // update dump data pane
  private void updateDumpDataPane() {

    // find source memory bank
    if (numberBanks > 1) {
      for (JRadioButton button: sourceBankRadioButtons) {
	if (button.isSelected()) {
	  sourceMemoryBank = button.getText();
	  log.finer("Source memory bank selected: " + sourceMemoryBank);
	  break;
	}
      }
    }

    final JPanel dumpDataPane = new JPanel(new GridBagLayout());
    int line = 0;

    for (int i = 0; i < NUMBER_HEX_LINES; i++) {

      final GridBagConstraints dumpDataAddressConstraints =
	new GridBagConstraints();
      final int address = dumpAddressField.getValue() + (i * NUMBER_HEX_BYTES);
      final JLabel dumpDataAddress = new JLabel(String.format("%04x", address));
      dumpDataAddress.setFont(FONT_MONOSPACED);
      dumpDataAddressConstraints.gridx = 0;
      dumpDataAddressConstraints.gridy = line;
      dumpDataAddressConstraints.insets = new Insets(0, 3, 0, 0);
      dumpDataAddressConstraints.anchor = GridBagConstraints.LINE_START;
      dumpDataAddressConstraints.weightx = 0.0;
      dumpDataAddressConstraints.weighty = 0.0;
      dumpDataAddress.addMouseListener(new AddressListener(address));
      dumpDataPane.add(dumpDataAddress, dumpDataAddressConstraints);

      for (int j = 0; j < NUMBER_HEX_BYTES; j++) {

	final GridBagConstraints dumpDataByteConstraints =
	  new GridBagConstraints();
	final int byteAddress = address + j;
	final JLabel dumpDataByte = new JLabel(String.format("%02x",
	  Parameters.memoryDevice.getBlockByName(sourceMemoryBank).getMemory()
	  [byteAddress]));
	dumpDataByte.setFont(FONT_MONOSPACED);
	dumpDataByteConstraints.gridx = j + 1;
	dumpDataByteConstraints.gridy = line;
	dumpDataByteConstraints.insets = new Insets(0, 3, 0, 0);
	dumpDataByteConstraints.anchor = GridBagConstraints.LINE_START;
	dumpDataByteConstraints.weightx = 0.0;
	dumpDataByteConstraints.weighty = 0.0;
	dumpDataByte.addMouseListener(new AddressListener(byteAddress));
	dumpDataPane.add(dumpDataByte, dumpDataByteConstraints);
      }
      line++;
    }
    if (this.dumpDataPane != null) {
      remove(this.dumpDataPane);
    }
    add(dumpDataPane, dumpDataPaneConstraints);
    revalidate();
    this.dumpDataPane = dumpDataPane;
  }

  // refresh listener
  private class RefreshListener implements ActionListener {

    // for description see ActionListener
    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Refresh listener action detected");
      updateDumpDataPane();
    }
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

    // main constructor
    public AddressListener(final int address) {
      super();
      log.finest(String.format(
        "Address listener created, address: 0x%04x", address));
      assert (address >= 0) && (address < 0x10000);
      this.address = address;
    }
      
    // for description see MouseListener
    @Override
    public void mouseClicked(final MouseEvent event) {
      log.fine(String.format(
        "Address listener action detected, address: 0x%04x", address));
      editAddressField.setText(String.format("%04x", address));
    }
  }

  // focus listener
  private class ComponentListener extends ComponentAdapter {
      
    // for description see ComponentListener
    @Override
    public void componentShown(final ComponentEvent event) {
      log.finer("Component listener action detected");
      updateDumpDataPane();
    }
  }
}
