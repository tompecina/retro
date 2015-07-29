/* Load.java
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
import java.io.File;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JFileChooser;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.ButtonGroup;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import cz.pecina.retro.common.Application;
import cz.pecina.retro.gui.RadioClick;
import cz.pecina.retro.gui.HexField;
import cz.pecina.retro.gui.InfoBox;

/**
 * Memory/Load panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Load extends MemoryTab {

  // static logger
  private static final Logger log =
    Logger.getLogger(Load.class.getName());

  // components holding values used by listeners
  private JRadioButton loadRadioRaw, loadRadioHEX, loadRadioXML,
    loadRadioSnapshot;
  private HexField loadRawDestinationField, loadHEXDestinationField,
    loadXMLDestinationField;

  /**
   * Creates Memory/Load panel.
   *
   * @param panel enclosing panel
   */
  public Load(final MemoryPanel panel) {
    super(panel);
    log.fine("New Memory/Load panel creation started");
 
    setBorder(BorderFactory.createEmptyBorder(5, 8, 0, 8));
    final ButtonGroup loadGroup = new ButtonGroup();

    final GridBagConstraints loadRadioRawConstraints =
      new GridBagConstraints();
    loadRadioRaw =
      new JRadioButton(Application.getString(this, "load.raw"));
    loadRadioRaw.setSelected(true);
    loadRadioRawConstraints.gridx = 0;
    loadRadioRawConstraints.gridy = 0;
    loadRadioRawConstraints.anchor = GridBagConstraints.LINE_START;
    loadRadioRawConstraints.weightx = 0.0;
    loadRadioRawConstraints.weighty = 0.0;
    add(loadRadioRaw, loadRadioRawConstraints);
    loadGroup.add(loadRadioRaw);
	
    final GridBagConstraints loadRawDestinationConstraints =
      new GridBagConstraints();
    final JLabel loadRawDestination =
      new JLabel(Application.getString(this, "load.raw.destination") + ":");
    loadRawDestinationConstraints.gridx = 1;
    loadRawDestinationConstraints.gridy = 0;
    loadRawDestinationConstraints.insets = new Insets(0, 10, 0, 0);
    loadRawDestinationConstraints.anchor = GridBagConstraints.LINE_END;
    loadRawDestinationConstraints.weightx = 0.0;
    loadRawDestinationConstraints.weighty = 0.0;
    add(loadRawDestination, loadRawDestinationConstraints);

    final GridBagConstraints loadRawDestinationFieldConstraints =
      new GridBagConstraints();
    loadRawDestinationField =
      new HexField(4);
    loadRawDestination.setLabelFor(loadRawDestinationField);
    loadRawDestinationField.addMouseListener(new RadioClick(loadRadioRaw));
    loadRawDestinationFieldConstraints.gridx = 2;
    loadRawDestinationFieldConstraints.gridy = 0;
    loadRawDestinationFieldConstraints.gridwidth = GridBagConstraints.REMAINDER;
    loadRawDestinationFieldConstraints.insets = new Insets(0, 3, 0, 0);
    loadRawDestinationFieldConstraints.anchor = GridBagConstraints.LINE_START;
    loadRawDestinationFieldConstraints.weightx = 0.0;
    loadRawDestinationFieldConstraints.weighty = 0.0;
    add(loadRawDestinationField, loadRawDestinationFieldConstraints);

    final GridBagConstraints loadRadioHEXConstraints =
      new GridBagConstraints();
    loadRadioHEX =
      new JRadioButton(Application.getString(this, "load.HEX"));
    loadRadioHEX.setSelected(true);
    loadRadioHEXConstraints.gridx = 0;
    loadRadioHEXConstraints.gridy = 1;
    loadRadioHEXConstraints.anchor = GridBagConstraints.LINE_START;
    loadRadioHEXConstraints.weightx = 0.0;
    loadRadioHEXConstraints.weighty = 0.0;
    add(loadRadioHEX, loadRadioHEXConstraints);
    loadGroup.add(loadRadioHEX);
	
    final GridBagConstraints loadHEXDestinationConstraints =
      new GridBagConstraints();
    final JLabel loadHEXDestination =
      new JLabel(Application.getString(this, "load.HEX.destination") + ":");
    loadHEXDestinationConstraints.gridx = 1;
    loadHEXDestinationConstraints.gridy = 1;
    loadHEXDestinationConstraints.insets = new Insets(0, 10, 0, 0);
    loadHEXDestinationConstraints.anchor = GridBagConstraints.LINE_END;
    loadHEXDestinationConstraints.weightx = 0.0;
    loadHEXDestinationConstraints.weighty = 0.0;
    add(loadHEXDestination, loadHEXDestinationConstraints);

    final GridBagConstraints loadHEXDestinationFieldConstraints =
      new GridBagConstraints();
    loadHEXDestinationField = new HexField(4);
    loadHEXDestination.setLabelFor(loadHEXDestinationField);
    loadHEXDestinationField.addMouseListener(new RadioClick(loadRadioHEX));
    loadHEXDestinationFieldConstraints.gridx = 2;
    loadHEXDestinationFieldConstraints.gridy = 1;
    loadHEXDestinationFieldConstraints.insets = new Insets(0, 3, 0, 0);
    loadHEXDestinationFieldConstraints.anchor = GridBagConstraints.LINE_START;
    loadHEXDestinationFieldConstraints.weightx = 0.0;
    loadHEXDestinationFieldConstraints.weighty = 0.0;
    add(loadHEXDestinationField, loadHEXDestinationFieldConstraints);

    final GridBagConstraints loadHEXOptionalConstraints =
      new GridBagConstraints();
    final JLabel loadHEXOptional =
      new JLabel("(" + Application.getString(this, "load.HEX.optional") + ")");
    loadHEXOptionalConstraints.gridx = 3;
    loadHEXOptionalConstraints.gridy = 1;
    loadHEXOptionalConstraints.gridwidth = GridBagConstraints.REMAINDER;
    loadHEXOptionalConstraints.insets = new Insets(0, 3, 0, 0);
    loadHEXOptionalConstraints.anchor = GridBagConstraints.LINE_START;
    loadHEXOptionalConstraints.weightx = 1.0;
    loadHEXOptionalConstraints.weighty = 0.0;
    add(loadHEXOptional, loadHEXOptionalConstraints);

    final GridBagConstraints loadRadioXMLConstraints =
      new GridBagConstraints();
    loadRadioXML = new JRadioButton(Application.getString(this, "load.XML"));
    loadRadioXML.setSelected(true);
    loadRadioXMLConstraints.gridx = 0;
    loadRadioXMLConstraints.gridy = 2;
    loadRadioXMLConstraints.anchor = GridBagConstraints.LINE_START;
    loadRadioXMLConstraints.weightx = 0.0;
    loadRadioXMLConstraints.weighty = 0.0;
    add(loadRadioXML, loadRadioXMLConstraints);
    loadGroup.add(loadRadioXML);
	
    final GridBagConstraints loadXMLDestinationConstraints =
      new GridBagConstraints();
    final JLabel loadXMLDestination =
      new JLabel(Application.getString(this, "load.XML.destination") + ":");
    loadXMLDestinationConstraints.gridx = 1;
    loadXMLDestinationConstraints.gridy = 2;
    loadXMLDestinationConstraints.insets = new Insets(0, 10, 0, 0);
    loadXMLDestinationConstraints.anchor = GridBagConstraints.LINE_END;
    loadXMLDestinationConstraints.weightx = 0.0;
    loadXMLDestinationConstraints.weighty = 0.0;
    add(loadXMLDestination, loadXMLDestinationConstraints);

    final GridBagConstraints loadXMLDestinationFieldConstraints =
      new GridBagConstraints();
    loadXMLDestinationField = new HexField(4);
    loadXMLDestination.setLabelFor(loadXMLDestinationField);
    loadXMLDestinationField.addMouseListener(new RadioClick(loadRadioXML));
    loadXMLDestinationFieldConstraints.gridx = 2;
    loadXMLDestinationFieldConstraints.gridy = 2;
    loadXMLDestinationFieldConstraints.insets = new Insets(0, 3, 0, 0);
    loadXMLDestinationFieldConstraints.anchor = GridBagConstraints.LINE_START;
    loadXMLDestinationFieldConstraints.weightx = 0.0;
    loadXMLDestinationFieldConstraints.weighty = 0.0;
    add(loadXMLDestinationField, loadXMLDestinationFieldConstraints);

    final GridBagConstraints loadXMLOptionalConstraints =
      new GridBagConstraints();
    final JLabel loadXMLOptional =
      new JLabel("(" + Application.getString(this, "load.XML.optional") + ")");
    loadXMLOptionalConstraints.gridx = 3;
    loadXMLOptionalConstraints.gridy = 2;
    loadXMLOptionalConstraints.gridwidth = GridBagConstraints.REMAINDER;
    loadXMLOptionalConstraints.insets = new Insets(0, 3, 0, 0);
    loadXMLOptionalConstraints.anchor = GridBagConstraints.LINE_START;
    loadXMLOptionalConstraints.weightx = 1.0;
    loadXMLOptionalConstraints.weighty = 0.0;
    add(loadXMLOptional, loadXMLOptionalConstraints);

    final GridBagConstraints loadRadioSnapshotConstraints =
      new GridBagConstraints();
    loadRadioSnapshot =
      new JRadioButton(Application.getString(this, "load.snapshot"));
    loadRadioSnapshot.setSelected(true);
    loadRadioSnapshotConstraints.gridx = 0;
    loadRadioSnapshotConstraints.gridy = 3;
    loadRadioSnapshotConstraints.gridwidth = GridBagConstraints.REMAINDER;
    loadRadioSnapshotConstraints.anchor = GridBagConstraints.LINE_START;
    loadRadioSnapshotConstraints.weightx = 0.0;
    loadRadioSnapshotConstraints.weighty = 0.0;
    add(loadRadioSnapshot, loadRadioSnapshotConstraints);
    loadGroup.add(loadRadioSnapshot);

    final GridBagConstraints loadButtonsConstraints =
      new GridBagConstraints();
    final JPanel loadButtonsPanel =
      new JPanel(new FlowLayout(FlowLayout.TRAILING));
    loadButtonsConstraints.gridx = 0;
    loadButtonsConstraints.gridy = 4;
    loadButtonsConstraints.gridwidth = GridBagConstraints.REMAINDER;
    loadButtonsConstraints.anchor = GridBagConstraints.LAST_LINE_END;
    loadButtonsConstraints.weightx = 0.0;
    loadButtonsConstraints.weighty = 1.0;
    final JButton loadLoadButton =
      new JButton(Application.getString(this, "load.button.load"));
    defaultButton = loadLoadButton;
    loadLoadButton.addActionListener(new LoadListener());
    loadButtonsPanel.add(loadLoadButton);
    final JButton loadCloseButton =
      new JButton(Application.getString(this, "load.button.close"));
    loadCloseButton.addActionListener(new CloseListener());
    loadButtonsPanel.add(loadCloseButton);
    add(loadButtonsPanel, loadButtonsConstraints);

    log.fine("Memory/Load panel set up");
  }

  // load listener
  private class LoadListener implements ActionListener {
    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Load listener action started");
      int destination = 0, number = 0;
      try {
	if (loadRadioRaw.isSelected()) {
	  destination = loadRawDestinationField.getValue();
	} else if (loadRadioHEX.isSelected()) {
	  if (loadHEXDestinationField.isEmpty()) {
	    destination = -1;
	  } else {
	    destination = loadHEXDestinationField.getValue();
	  }
	} else if (loadRadioXML.isSelected()) {
	  if (loadXMLDestinationField.isEmpty()) {
	    destination = -1;
	  } else {
	    destination = loadXMLDestinationField.getValue();
	  }
	}		    
      } catch (NumberFormatException exception) {
	InfoBox.display(panel,
			Application.getString(this, "incompleteForm"));
	return;
      }
      FileNameExtensionFilter filter;
      fileChooser.resetChoosableFileFilters();
      fileChooser.setAcceptAllFileFilterUsed(true);
      if (loadRadioRaw.isSelected()) {
	filter = rawFilter;
      } else if (loadRadioHEX.isSelected()) {
	filter = HEXFilter;
      } else {
	filter = XMLFilter;
      }
      fileChooser.addChoosableFileFilter(filter);
      fileChooser.setFileFilter(filter);
      if (fileChooser.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION) {
	final File file = fileChooser.getSelectedFile();
	try {
	  if (loadRadioRaw.isSelected()) {
	    number = new Raw(
	      panel.getHardware(),
	      sourceMemoryBank,
	      destinationMemoryBank).read(file, destination).number;
	  } else if (loadRadioHEX.isSelected()) {
	    number = new IntelHEX(
	      panel.getHardware(),
	      sourceMemoryBank,
	      destinationMemoryBank).read(file, destination).number;
	  } else if (loadRadioXML.isSelected()) {
	    number = new XML(
	      panel.getHardware(),
	      sourceMemoryBank,
	      destinationMemoryBank).read(file, destination).number;
	  } else {
	    new Snapshot(panel.getHardware()).read(file);
	  }
	} catch (RuntimeException exception) {
	  errorBox(exception);
	  return;
	}
	if (loadRadioSnapshot.isSelected()) {
	  InfoBox.display(panel,
			  Application.getString(this, "snapshotLoaded"));
	} else {
	  InfoBox.display(
	    panel,
	    String.format(Application.getString(this, "loaded"), number));
	}
      }
    }
  }
}
