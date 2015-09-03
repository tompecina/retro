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

import java.nio.ByteBuffer;

import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.CharacterCodingException;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Component;
import java.awt.Color;

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
import cz.pecina.retro.common.GeneralConstants;

import cz.pecina.retro.cpu.Block;
import cz.pecina.retro.cpu.Disassembly;

import cz.pecina.retro.gui.HexField;
import cz.pecina.retro.gui.ErrorBox;

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

  // number of disassembly lines
  private static final int NUMBER_DIS_LINES = 16;
  
  // monospaced font
  private static final Font FONT_MONOSPACED =
    new Font(Font.MONOSPACED, Font.PLAIN, 13);

  // placeholder for character without safe representation
  private static final String PLACEHOLDER = ".";
  
  // components holding values used by listeners
  private JRadioButton dumpTypeHexRadio, dumpTypeDisRadio;
  private HexField dumpAddressField, editAddressField;
  private JTextField editDataField;
  
  // lists of bank selection radio buttons
  private List<JRadioButton> sourceBankRadioButtons = new ArrayList<>();
      
  // mutable inner pane
  private JPanel dumpDataPane;
  private GridBagConstraints dumpDataPaneConstraints;

  // internal charset decoder
  final CharsetDecoder decoder = Parameters.charset.newDecoder();
  {
    decoder.replaceWith(PLACEHOLDER);
    decoder.onMalformedInput(CodingErrorAction.REPLACE);
    decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
  }
  
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
	new JLabel(Application.getString(this, "bank") + ":");
      sourceBankLabelConstraints.gridx = 0;
      sourceBankLabelConstraints.gridy = line;
      sourceBankLabelConstraints.insets = new Insets(0, 3, 2, 0);
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
      sourceBankPanelConstraints.insets = new Insets(0, 0, 2, 0);
      sourceBankPanelConstraints.anchor = GridBagConstraints.LINE_START;
      sourceBankPanelConstraints.weightx = 1.0;
      sourceBankPanelConstraints.weighty = 0.0;

      final ButtonGroup sourceBankGroup = new ButtonGroup();
      for (Block bank: banks) {
	final JRadioButton sourceBankRadioButton =
	  new JRadioButton(bank.getName());
	sourceBankRadioButton.addActionListener(new RefreshListener());
	sourceBankRadioButtons.add(sourceBankRadioButton);
	sourceBankPanel.add(sourceBankRadioButton);
	sourceBankGroup.add(sourceBankRadioButton);
      }
      sourceBankRadioButtons.get(0).setSelected(true);

      add(sourceBankPanel, sourceBankPanelConstraints);
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
    dumpAddressField.setText("0000");
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
    defaultButton = dumpRefreshButton;
    dumpAddressPane.add(dumpRefreshButton, dumpRefreshButtonConstraints);

    final GridBagConstraints dumpTypeHexRadioConstraints =
      new GridBagConstraints();
    dumpTypeHexRadio =
      new JRadioButton(Application.getString(this, "dumpEdit.hex"));
    dumpTypeHexRadio.setSelected(true);
    dumpTypeHexRadioConstraints.gridx = 3;
    dumpTypeHexRadioConstraints.gridy = 0;
    dumpTypeHexRadioConstraints.insets = new Insets(0, 15, 0, 0);
    dumpTypeHexRadioConstraints.anchor = GridBagConstraints.LINE_START;
    dumpTypeHexRadioConstraints.weightx = 0.0;
    dumpTypeHexRadioConstraints.weighty = 0.0;
    dumpTypeHexRadio.addActionListener(new RefreshListener());
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
    dumpTypeDisRadio.addActionListener(new RefreshListener());
    dumpTypeGroup.add(dumpTypeDisRadio);
    dumpAddressPane.add(dumpTypeDisRadio, dumpTypeDisRadioConstraints);

    add(dumpAddressPane, dumpAddressPaneConstraints);
    line++;

    dumpDataPaneConstraints = new GridBagConstraints();
    dumpDataPaneConstraints.gridx = 0;
    dumpDataPaneConstraints.gridy = line;
    dumpDataPaneConstraints.insets = new Insets(10, 3, 15, 0);
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
    editPaneConstraints.insets = new Insets(0, 3, 3, 0);
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
    editButton.addActionListener(new EditListener());
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

    // find memory bank
    if (numberBanks > 1) {
      for (JRadioButton button: sourceBankRadioButtons) {
	if (button.isSelected()) {
	  sourceMemoryBank = button.getText();
	  log.finer("Source memory bank selected: " + sourceMemoryBank);
	  break;
	}
      }
    }
    final byte[] sourceBank =
      Parameters.memoryDevice.getBlockByName(sourceMemoryBank).getMemory();
    final int size = sourceBank.length;
    
    final JPanel dumpDataPane = new JPanel(new GridBagLayout());
    int line = 0;
      
    if (dumpTypeHexRadio.isSelected()) {

      for (int i = 0; i < NUMBER_HEX_LINES; i++) {
	
	final GridBagConstraints dumpDataAddressConstraints =
	  new GridBagConstraints();
	final int address = (dumpAddressField.getValue() + (i * NUMBER_HEX_BYTES)) % size;
	final JLabel dumpDataAddress =
	  new JLabel(String.format("%04x", address));
	dumpDataAddress.setFont(FONT_MONOSPACED);
	dumpDataAddressConstraints.gridx = 0;
	dumpDataAddressConstraints.gridy = line;
	dumpDataAddressConstraints.insets = new Insets(1, 12, 0, 1);
	dumpDataAddressConstraints.anchor = GridBagConstraints.LINE_START;
	dumpDataAddressConstraints.weightx = 0.0;
	dumpDataAddressConstraints.weighty = 0.0;
	dumpDataAddress.addMouseListener(new AddressListener(address));
	dumpDataPane.add(dumpDataAddress, dumpDataAddressConstraints);

	for (int j = 0; j < NUMBER_HEX_BYTES; j++) {
	  
	  final GridBagConstraints dumpDataByteConstraints =
	    new GridBagConstraints();
	  final int byteAddress = (address + j) % size;
	  final JLabel dumpDataByte =
	    new JLabel(String.format("%02x", sourceBank[byteAddress]));
	  dumpDataByte.setFont(FONT_MONOSPACED);
	  dumpDataByteConstraints.gridx = j + 1;
	  dumpDataByteConstraints.gridy = line;
	  dumpDataByteConstraints.insets =
	    new Insets(1, (((j % 8) == 0) ? 10 : 4), 0, 1);
	  dumpDataByteConstraints.anchor = GridBagConstraints.LINE_START;
	  dumpDataByteConstraints.weightx = 0.0;
	  dumpDataByteConstraints.weighty = 0.0;
	  dumpDataByte.addMouseListener(new AddressListener(byteAddress));
	  dumpDataPane.add(dumpDataByte, dumpDataByteConstraints);
	}
	
	for (int j = 0; j < NUMBER_HEX_BYTES; j++) {
	  
	  final GridBagConstraints dumpDataCharConstraints =
	    new GridBagConstraints();
	  final int byteAddress = address + j;
	  final ByteBuffer bb =
	    ByteBuffer.wrap(new byte[] {(byte)sourceBank[byteAddress]});
	  String ch;
	  try {
	    ch = decoder.decode(bb).toString();
	  } catch (final CharacterCodingException exception) {
	    ch = PLACEHOLDER;
	  }	  
	  if (!GeneralConstants.SAFE_CHARACTERS.contains(ch.charAt(0))) {
	    ch = PLACEHOLDER;
	  }
	  final JLabel dumpDataChar = new JLabel(ch);
	  dumpDataChar.setFont(FONT_MONOSPACED);
	  dumpDataCharConstraints.gridx = NUMBER_HEX_BYTES + j + 1;
	  dumpDataCharConstraints.gridy = line;
	  dumpDataCharConstraints.insets =
	    new Insets(1, (((j % 8) == 0) ? 10 : 1), 0, 1);
	  dumpDataCharConstraints.anchor = GridBagConstraints.LINE_START;
	  dumpDataCharConstraints.weightx = 0.0;
	  dumpDataCharConstraints.weighty = 0.0;
	  dumpDataChar.addMouseListener(new AddressListener(byteAddress));
	  dumpDataPane.add(dumpDataChar, dumpDataCharConstraints);
	}
	line++;
      }
      
    } else {

      int address = dumpAddressField.getValue() % size;

      for (int i = 0; i < NUMBER_DIS_LINES; i++) {
	
	final Disassembly disassembly =
	  Parameters.cpu.getDisassembly(sourceBank, address);
	final int[] bytes = disassembly.getBytes();
	final int length = disassembly.getLength();
	String hexString = "";
	for (int j = 0; j < length; j++) {
	  hexString += String.format(" %02x", bytes[j]);
	}
	hexString = hexString.substring(1);
	    
	final GridBagConstraints disAddressConstraints =
	  new GridBagConstraints();
	final JLabel disAddress =
	  new JLabel(String.format("%04x", address));
	disAddress.setFont(FONT_MONOSPACED);
	disAddressConstraints.gridx = 0;
	disAddressConstraints.gridy = line;
	disAddressConstraints.insets = new Insets(1, 12, 0, 3);
	disAddressConstraints.anchor = GridBagConstraints.LINE_START;
	disAddressConstraints.weightx = 0.0;
	disAddressConstraints.weighty = 0.0;
	disAddress.addMouseListener(new AddressListener(address, hexString));
	dumpDataPane.add(disAddress, disAddressConstraints);

	final GridBagConstraints disByte0Constraints =
	  new GridBagConstraints();
	final JLabel disByte0 =
	  new JLabel(String.format("%02x", bytes[0]));
	disByte0.setFont(FONT_MONOSPACED);
	disByte0Constraints.gridx = 1;
	disByte0Constraints.gridy = line;
	disByte0Constraints.insets = new Insets(1, 4, 0, 0);
	if (length == 1) {
	  disByte0Constraints.gridwidth = 3;
	}
	disByte0Constraints.anchor = GridBagConstraints.LINE_START;
	disByte0Constraints.weightx = 0.0;
	disByte0Constraints.weighty = 0.0;
	disByte0.addMouseListener(new AddressListener(address, hexString));
	dumpDataPane.add(disByte0, disByte0Constraints);
	
	if (length > 1) {
	  final GridBagConstraints disByte1Constraints =
	    new GridBagConstraints();
	  final JLabel disByte1 =
	    new JLabel(String.format("%02x", bytes[1]));
	  disByte1.setFont(FONT_MONOSPACED);
	  disByte1Constraints.gridx = 2;
	  disByte1Constraints.gridy = line;
	  disByte1Constraints.insets = new Insets(1, 4, 0, 0);
	  if (length == 2) {
	    disByte1Constraints.gridwidth = 2;
	  }
	  disByte1Constraints.anchor = GridBagConstraints.LINE_START;
	  disByte1Constraints.weightx = 0.0;
	  disByte1Constraints.weighty = 0.0;
	  disByte1.addMouseListener(new AddressListener(address, hexString));
	  dumpDataPane.add(disByte1, disByte1Constraints);
	}
	
	if (length > 2) {
	  final GridBagConstraints disByte2Constraints =
	    new GridBagConstraints();
	  final JLabel disByte2 =
	    new JLabel(String.format("%02x", bytes[2]));
	  disByte2.setFont(FONT_MONOSPACED);
	  disByte2Constraints.gridx = 3;
	  disByte2Constraints.gridy = line;
	  disByte2Constraints.insets = new Insets(1, 4, 0, 0);
	  if (length == 2) {
	    disByte2Constraints.gridwidth = 2;
	  }
	  disByte2Constraints.anchor = GridBagConstraints.LINE_START;
	  disByte2Constraints.weightx = 0.0;
	  disByte2Constraints.weighty = 0.0;
	  disByte2.addMouseListener(new AddressListener(address, hexString));
	  dumpDataPane.add(disByte2, disByte2Constraints);
	}

	final String parameters =
	  disassembly.getParameters(false, "%02x", "%04x", false);

	final GridBagConstraints disMnemoConstraints =
	  new GridBagConstraints();
	final JLabel disMnemo =
	  new JLabel(disassembly.getMnemo(false));
	disMnemo.setFont(FONT_MONOSPACED);
	disMnemoConstraints.gridx = 4;
	disMnemoConstraints.gridy = line;
	disMnemoConstraints.insets = new Insets(1, 25, 0, 0);
	if (parameters.isEmpty()) {
	  disMnemoConstraints.gridwidth = GridBagConstraints.REMAINDER;
	}
	disMnemoConstraints.anchor = GridBagConstraints.LINE_START;
	disMnemoConstraints.weightx = 0.0;
	disMnemoConstraints.weighty = 0.0;
	disMnemo.addMouseListener(new AddressListener(address, hexString));
	dumpDataPane.add(disMnemo, disMnemoConstraints);

	if (!parameters.isEmpty()) {
	  final GridBagConstraints disParametersConstraints =
	    new GridBagConstraints();
	  final JLabel disParameters = new JLabel(parameters);
	  disParameters.setFont(FONT_MONOSPACED);
	  disParametersConstraints.gridx = 5;
	  disParametersConstraints.gridy = line;
	  disParametersConstraints.insets = new Insets(1, 15, 0, 0);
	  disParametersConstraints.anchor = GridBagConstraints.LINE_START;
	  disParametersConstraints.weightx = 0.0;
	  disParametersConstraints.weighty = 0.0;
	  disParameters.addMouseListener(new AddressListener(address, hexString));
	  dumpDataPane.add(disParameters, disParametersConstraints);
	}
	address += length;
	line++;
      }
    }
    
    if (this.dumpDataPane != null) {
      remove(this.dumpDataPane);
    }
    add(dumpDataPane, dumpDataPaneConstraints);
    revalidate();
    repaint();
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
  
  // edit listener
  private class EditListener implements ActionListener {

    // for description see ActionListener
    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Dump/edit listener action started");

      final String text = editDataField.getText().trim();
      if (text.isEmpty()) {
  	log.finer("No data to process");
  	return;
      }
      
      // find memory bank (source bank is used for both targets)
      if (numberBanks > 1) {
  	for (JRadioButton button: sourceBankRadioButtons) {
  	  if (button.isSelected()) {
  	    destinationMemoryBank = button.getText();
  	    log.finer("Destination memory bank selected: " + destinationMemoryBank);
  	    break;
  	  }
  	}
      }
      final byte[] destinationBank =
  	Parameters.memoryDevice.getBlockByName(destinationMemoryBank).getMemory();

      final String syntaxError = Application.getString(this, "error.editLine.syntax");
      
      final List<Byte> data = new ArrayList<>();
      
      for (String chunk: text.split("[ ,;]")) {

	if (chunk.length() < 3) {
	  try {
	    data.add((byte)(Integer.parseInt(chunk, 16)));
	  } catch (final NumberFormatException exception) {
	    ErrorBox.display(panel, syntaxError);
	    return;
	  }
	} else if ((chunk.length() % 1) == 1) {
	    ErrorBox.display(panel, syntaxError);
	    return;
	} else {
	  for (int i = 0; i < chunk.length(); i += 2) {
	    try {
	      data.add((byte)(Integer.parseInt(chunk.substring(i, i + 2), 16)));
	    } catch (final NumberFormatException exception) {
	      ErrorBox.display(panel, syntaxError);
	      return;
	    }
	  }
	}
      }
      final int address = editAddressField.getValue();
      
      for (int i = 0; i < data.size(); i++) {
	destinationBank[(address + i) % destinationBank.length] = data.get(i);
      }
      editAddressField.setText("");
      editDataField.setText("");
      updateDumpDataPane();
    }
  }

  // address listener
  private class AddressListener extends MouseAdapter {

    private int address;
    private String editString;

    // main constructor
    public AddressListener(final int address, final String editString) {
      super();
      log.finest(String.format(
        "Address listener created, address: 0x%04x, string: %s",
	address, editString));
      assert (address >= 0) && (address < 0x10000);
      this.address = address;
      this.editString = editString;
    }

    // simplified constructor
    public AddressListener(final int address) {
      this(address, null);
    }
      
    // for description see MouseListener
    @Override
    public void mousePressed(final MouseEvent event) {
      log.fine(String.format(
        "Address listener action detected (pressed), address: 0x%04x", address));
      editAddressField.setText(String.format("%04x", address));
      if (editString != null) {
	editDataField.setText(editString);
      }
      final Component source = event.getComponent();
      if (source instanceof JLabel) {
	((JLabel)source).setForeground(Color.RED);
      }
    }

    // for description see MouseListener
    @Override
    public void mouseReleased(final MouseEvent event) {
      log.fine(String.format(
        "Address listener action detected (released), address: 0x%04x", address));
      final Component source = event.getComponent();
      if (source instanceof JLabel) {
	((JLabel)source).setForeground(Color.BLACK);
      }
      editDataField.requestFocusInWindow();
    }
  }

  // component listener
  private class ComponentListener extends ComponentAdapter {
      
    // for description see ComponentListener
    @Override
    public void componentShown(final ComponentEvent event) {
      log.finer("Component listener action detected");
      updateDumpDataPane();
    }
  }
}
