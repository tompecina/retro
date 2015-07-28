/* Save.java
 *
 * Copyright (C) 2014-2015, Tomáš Pecina <tomas@pecina.cz>
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
 * Memory/Save panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Save extends MemoryTab {

  // static logger
  private static final Logger log =
    Logger.getLogger(Save.class.getName());

  // components holding values used by listeners
  private JRadioButton saveRadioRaw, saveRadioHEX, saveRadioXML,
    saveRadioSnapshot;
  private HexField saveRawStartField, saveRawEndField, saveHEXStartField,
    saveHEXEndField, saveHEXDestinationField, saveXMLStartField,
    saveXMLEndField, saveXMLDestinationField;

  /**
   * Creates Memory/Save panel.
   *
   * @param panel enclosing panel
   */
  public Save(final MemoryPanel panel) {
    super(panel);
    log.fine("New Memory/Save panel creation started");
 
    setBorder(BorderFactory.createEmptyBorder(5, 8, 0, 8));
    final ButtonGroup saveGroup = new ButtonGroup();

    final GridBagConstraints saveRadioRawConstraints =
      new GridBagConstraints();
    saveRadioRaw =
      new JRadioButton(Application.getString(this, "save.raw"));
    saveRadioRaw.setSelected(true);
    saveRadioRawConstraints.gridx = 0;
    saveRadioRawConstraints.gridy = 0;
    saveRadioRawConstraints.anchor = GridBagConstraints.LINE_START;
    saveRadioRawConstraints.weightx = 0.0;
    saveRadioRawConstraints.weighty = 0.0;
    add(saveRadioRaw, saveRadioRawConstraints);
    saveGroup.add(saveRadioRaw);
	
    final GridBagConstraints saveRawStartConstraints =
      new GridBagConstraints();
    final JLabel saveRawStart =
      new JLabel(Application.getString(this, "save.raw.start") + ":");
    saveRawStartConstraints.gridx = 1;
    saveRawStartConstraints.gridy = 0;
    saveRawStartConstraints.insets = new Insets(0, 10, 0, 0);
    saveRawStartConstraints.anchor = GridBagConstraints.LINE_END;
    saveRawStartConstraints.weightx = 0.0;
    saveRawStartConstraints.weighty = 0.0;
    add(saveRawStart, saveRawStartConstraints);

    final GridBagConstraints saveRawStartFieldConstraints =
      new GridBagConstraints();
    saveRawStartField = new HexField(4);
    saveRawStart.setLabelFor(saveRawStartField);
    saveRawStartField.addMouseListener(new RadioClick(saveRadioRaw));
    saveRawStartFieldConstraints.gridx = 2;
    saveRawStartFieldConstraints.gridy = 0;
    saveRawStartFieldConstraints.insets = new Insets(0, 3, 0, 10);
    saveRawStartFieldConstraints.anchor = GridBagConstraints.LINE_START;
    saveRawStartFieldConstraints.weightx = 0.0;
    saveRawStartFieldConstraints.weighty = 0.0;
    add(saveRawStartField, saveRawStartFieldConstraints);

    final GridBagConstraints saveRawEndConstraints =
      new GridBagConstraints();
    final JLabel saveRawEnd =
      new JLabel(Application.getString(this, "save.raw.end") + ":");
    saveRawEndConstraints.gridx = 3;
    saveRawEndConstraints.gridy = 0;
    saveRawEndConstraints.insets = new Insets(0, 10, 0, 0);
    saveRawEndConstraints.anchor = GridBagConstraints.LINE_END;
    saveRawEndConstraints.weightx = 0.0;
    saveRawEndConstraints.weighty = 0.0;
    add(saveRawEnd, saveRawEndConstraints);

    final GridBagConstraints saveRawEndFieldConstraints =
      new GridBagConstraints();
    saveRawEndField = new HexField(4);
    saveRawEnd.setLabelFor(saveRawEndField);
    saveRawEndField.addMouseListener(new RadioClick(saveRadioRaw));
    saveRawEndFieldConstraints.gridx = 4;
    saveRawEndFieldConstraints.gridy = 0;
    saveRawEndFieldConstraints.gridwidth = GridBagConstraints.REMAINDER;
    saveRawEndFieldConstraints.insets = new Insets(0, 3, 0, 0);
    saveRawEndFieldConstraints.anchor = GridBagConstraints.LINE_START;
    saveRawEndFieldConstraints.weightx = 0.0;
    saveRawEndFieldConstraints.weighty = 0.0;
    add(saveRawEndField, saveRawEndFieldConstraints);

    final GridBagConstraints saveRadioHEXConstraints =
      new GridBagConstraints();
    saveRadioHEX =
      new JRadioButton(Application.getString(this, "save.HEX"));
    saveRadioHEX.setSelected(true);
    saveRadioHEXConstraints.gridx = 0;
    saveRadioHEXConstraints.gridy = 1;
    saveRadioHEXConstraints.anchor = GridBagConstraints.LINE_START;
    saveRadioHEXConstraints.weightx = 0.0;
    saveRadioHEXConstraints.weighty = 0.0;
    add(saveRadioHEX, saveRadioHEXConstraints);
    saveGroup.add(saveRadioHEX);
	
    final GridBagConstraints saveHEXStartConstraints =
      new GridBagConstraints();
    final JLabel saveHEXStart =
      new JLabel(Application.getString(this, "save.HEX.start") + ":");
    saveHEXStartConstraints.gridx = 1;
    saveHEXStartConstraints.gridy = 1;
    saveHEXStartConstraints.insets = new Insets(0, 10, 0, 0);
    saveHEXStartConstraints.anchor = GridBagConstraints.LINE_END;
    saveHEXStartConstraints.weightx = 0.0;
    saveHEXStartConstraints.weighty = 0.0;
    add(saveHEXStart, saveHEXStartConstraints);

    final GridBagConstraints saveHEXStartFieldConstraints =
      new GridBagConstraints();
    saveHEXStartField = new HexField(4);
    saveHEXStart.setLabelFor(saveHEXStartField);
    saveHEXStartField.addMouseListener(new RadioClick(saveRadioHEX));
    saveHEXStartFieldConstraints.gridx = 2;
    saveHEXStartFieldConstraints.gridy = 1;
    saveHEXStartFieldConstraints.insets = new Insets(0, 3, 0, 10);
    saveHEXStartFieldConstraints.anchor = GridBagConstraints.LINE_START;
    saveHEXStartFieldConstraints.weightx = 0.0;
    saveHEXStartFieldConstraints.weighty = 0.0;
    add(saveHEXStartField, saveHEXStartFieldConstraints);

    final GridBagConstraints saveHEXEndConstraints =
      new GridBagConstraints();
    final JLabel saveHEXEnd =
      new JLabel(Application.getString(this, "save.HEX.end") + ":");
    saveHEXEndConstraints.gridx = 3;
    saveHEXEndConstraints.gridy = 1;
    saveHEXEndConstraints.insets = new Insets(0, 10, 0, 0);
    saveHEXEndConstraints.anchor = GridBagConstraints.LINE_END;
    saveHEXEndConstraints.weightx = 0.0;
    saveHEXEndConstraints.weighty = 0.0;
    add(saveHEXEnd, saveHEXEndConstraints);

    final GridBagConstraints saveHEXEndFieldConstraints =
      new GridBagConstraints();
    saveHEXEndField = new HexField(4);
    saveHEXEnd.setLabelFor(saveHEXEndField);
    saveHEXEndField.addMouseListener(new RadioClick(saveRadioHEX));
    saveHEXEndFieldConstraints.gridx = 4;
    saveHEXEndFieldConstraints.gridy = 1;
    saveHEXEndFieldConstraints.insets = new Insets(0, 3, 0, 10);
    saveHEXEndFieldConstraints.anchor = GridBagConstraints.LINE_START;
    saveHEXEndFieldConstraints.weightx = 0.0;
    saveHEXEndFieldConstraints.weighty = 0.0;
    add(saveHEXEndField, saveHEXEndFieldConstraints);

    final GridBagConstraints saveHEXDestinationConstraints =
      new GridBagConstraints();
    final JLabel saveHEXDestination =
      new JLabel(Application.getString(this, "save.HEX.destination") + ":");
    saveHEXDestinationConstraints.gridx = 5;
    saveHEXDestinationConstraints.gridy = 1;
    saveHEXDestinationConstraints.insets = new Insets(0, 10, 0, 0);
    saveHEXDestinationConstraints.anchor = GridBagConstraints.LINE_END;
    saveHEXDestinationConstraints.weightx = 0.0;
    saveHEXDestinationConstraints.weighty = 0.0;
    add(saveHEXDestination, saveHEXDestinationConstraints);

    final GridBagConstraints saveHEXDestinationFieldConstraints =
      new GridBagConstraints();
    saveHEXDestinationField = new HexField(4);
    saveHEXDestination.setLabelFor(saveHEXDestinationField);
    saveHEXDestinationField.addMouseListener(new RadioClick(saveRadioHEX));
    saveHEXDestinationFieldConstraints.gridx = 6;
    saveHEXDestinationFieldConstraints.gridy = 1;
    saveHEXDestinationFieldConstraints.insets = new Insets(0, 3, 0, 0);
    saveHEXDestinationFieldConstraints.anchor = GridBagConstraints.LINE_START;
    saveHEXDestinationFieldConstraints.weightx = 0.0;
    saveHEXDestinationFieldConstraints.weighty = 0.0;
    add(saveHEXDestinationField, saveHEXDestinationFieldConstraints);

    final GridBagConstraints saveHEXOptionalConstraints =
      new GridBagConstraints();
    final JLabel saveHEXOptional =
      new JLabel("(" + Application.getString(this, "save.HEX.optional") + ")");
    saveHEXOptionalConstraints.gridx = 7;
    saveHEXOptionalConstraints.gridy = 1;
    saveHEXOptionalConstraints.gridwidth = GridBagConstraints.REMAINDER;
    saveHEXOptionalConstraints.insets = new Insets(0, 3, 0, 0);
    saveHEXOptionalConstraints.anchor = GridBagConstraints.LINE_START;
    saveHEXOptionalConstraints.weightx = 1.0;
    saveHEXOptionalConstraints.weighty = 0.0;
    add(saveHEXOptional, saveHEXOptionalConstraints);

    final GridBagConstraints saveRadioXMLConstraints =
      new GridBagConstraints();
    saveRadioXML =
      new JRadioButton(Application.getString(this, "save.XML"));
    saveRadioXML.setSelected(true);
    saveRadioXMLConstraints.gridx = 0;
    saveRadioXMLConstraints.gridy = 2;
    saveRadioXMLConstraints.anchor = GridBagConstraints.LINE_START;
    saveRadioXMLConstraints.weightx = 0.0;
    saveRadioXMLConstraints.weighty = 0.0;
    add(saveRadioXML, saveRadioXMLConstraints);
    saveGroup.add(saveRadioXML);
	
    final GridBagConstraints saveXMLStartConstraints =
      new GridBagConstraints();
    final JLabel saveXMLStart =
      new JLabel(Application.getString(this, "save.XML.start") + ":");
    saveXMLStartConstraints.gridx = 1;
    saveXMLStartConstraints.gridy = 2;
    saveXMLStartConstraints.insets = new Insets(0, 10, 0, 0);
    saveXMLStartConstraints.anchor = GridBagConstraints.LINE_END;
    saveXMLStartConstraints.weightx = 0.0;
    saveXMLStartConstraints.weighty = 0.0;
    add(saveXMLStart, saveXMLStartConstraints);

    final GridBagConstraints saveXMLStartFieldConstraints =
      new GridBagConstraints();
    saveXMLStartField = new HexField(4);
    saveXMLStart.setLabelFor(saveXMLStartField);
    saveXMLStartField.addMouseListener(new RadioClick(saveRadioXML));
    saveXMLStartFieldConstraints.gridx = 2;
    saveXMLStartFieldConstraints.gridy = 2;
    saveXMLStartFieldConstraints.insets = new Insets(0, 3, 0, 10);
    saveXMLStartFieldConstraints.anchor = GridBagConstraints.LINE_START;
    saveXMLStartFieldConstraints.weightx = 0.0;
    saveXMLStartFieldConstraints.weighty = 0.0;
    add(saveXMLStartField, saveXMLStartFieldConstraints);

    final GridBagConstraints saveXMLEndConstraints =
      new GridBagConstraints();
    final JLabel saveXMLEnd =
      new JLabel(Application.getString(this, "save.XML.end") + ":");
    saveXMLEndConstraints.gridx = 3;
    saveXMLEndConstraints.gridy = 2;
    saveXMLEndConstraints.insets = new Insets(0, 10, 0, 0);
    saveXMLEndConstraints.anchor = GridBagConstraints.LINE_END;
    saveXMLEndConstraints.weightx = 0.0;
    saveXMLEndConstraints.weighty = 0.0;
    add(saveXMLEnd, saveXMLEndConstraints);

    final GridBagConstraints saveXMLEndFieldConstraints =
      new GridBagConstraints();
    saveXMLEndField = new HexField(4);
    saveXMLEnd.setLabelFor(saveXMLEndField);
    saveXMLEndField.addMouseListener(new RadioClick(saveRadioXML));
    saveXMLEndFieldConstraints.gridx = 4;
    saveXMLEndFieldConstraints.gridy = 2;
    saveXMLEndFieldConstraints.insets = new Insets(0, 3, 0, 10);
    saveXMLEndFieldConstraints.anchor = GridBagConstraints.LINE_START;
    saveXMLEndFieldConstraints.weightx = 0.0;
    saveXMLEndFieldConstraints.weighty = 0.0;
    add(saveXMLEndField, saveXMLEndFieldConstraints);

    final GridBagConstraints saveXMLDestinationConstraints =
      new GridBagConstraints();
    final JLabel saveXMLDestination =
      new JLabel(Application.getString(this, "save.XML.destination") + ":");
    saveXMLDestinationConstraints.gridx = 5;
    saveXMLDestinationConstraints.gridy = 2;
    saveXMLDestinationConstraints.insets = new Insets(0, 10, 0, 0);
    saveXMLDestinationConstraints.anchor = GridBagConstraints.LINE_END;
    saveXMLDestinationConstraints.weightx = 0.0;
    saveXMLDestinationConstraints.weighty = 0.0;
    add(saveXMLDestination, saveXMLDestinationConstraints);

    final GridBagConstraints saveXMLDestinationFieldConstraints =
      new GridBagConstraints();
    saveXMLDestinationField = new HexField(4);
    saveXMLDestination.setLabelFor(saveXMLDestinationField);
    saveXMLDestinationField.addMouseListener(new RadioClick(saveRadioXML));
    saveXMLDestinationFieldConstraints.gridx = 6;
    saveXMLDestinationFieldConstraints.gridy = 2;
    saveXMLDestinationFieldConstraints.insets = new Insets(0, 3, 0, 0);
    saveXMLDestinationFieldConstraints.anchor = GridBagConstraints.LINE_START;
    saveXMLDestinationFieldConstraints.weightx = 0.0;
    saveXMLDestinationFieldConstraints.weighty = 0.0;
    add(saveXMLDestinationField, saveXMLDestinationFieldConstraints);

    final GridBagConstraints saveXMLOptionalConstraints =
      new GridBagConstraints();
    final JLabel saveXMLOptional =
      new JLabel("(" + Application.getString(this, "save.XML.optional") + ")");
    saveXMLOptionalConstraints.gridx = 7;
    saveXMLOptionalConstraints.gridy = 2;
    saveXMLOptionalConstraints.gridwidth = GridBagConstraints.REMAINDER;
    saveXMLOptionalConstraints.insets = new Insets(0, 3, 0, 0);
    saveXMLOptionalConstraints.anchor = GridBagConstraints.LINE_START;
    saveXMLOptionalConstraints.weightx = 1.0;
    saveXMLOptionalConstraints.weighty = 0.0;
    add(saveXMLOptional, saveXMLOptionalConstraints);

    final GridBagConstraints saveRadioSnapshotConstraints =
      new GridBagConstraints();
    saveRadioSnapshot =
      new JRadioButton(Application.getString(this, "save.snapshot"));
    saveRadioSnapshot.setSelected(true);
    saveRadioSnapshotConstraints.gridx = 0;
    saveRadioSnapshotConstraints.gridy = 3;
    saveRadioSnapshotConstraints.gridwidth = GridBagConstraints.REMAINDER;
    saveRadioSnapshotConstraints.anchor = GridBagConstraints.LINE_START;
    saveRadioSnapshotConstraints.weightx = 0.0;
    saveRadioSnapshotConstraints.weighty = 0.0;
    add(saveRadioSnapshot, saveRadioSnapshotConstraints);
    saveGroup.add(saveRadioSnapshot);
	
    final GridBagConstraints saveButtonsConstraints =
      new GridBagConstraints();
    final JPanel saveButtonsPanel =
      new JPanel(new FlowLayout(FlowLayout.TRAILING));
    saveButtonsConstraints.gridx = 0;
    saveButtonsConstraints.gridy = 4;
    saveButtonsConstraints.gridwidth = GridBagConstraints.REMAINDER;
    saveButtonsConstraints.anchor = GridBagConstraints.LAST_LINE_END;
    saveButtonsConstraints.weightx = 0.0;
    saveButtonsConstraints.weighty = 1.0;
    final JButton saveSaveButton =
      new JButton(Application.getString(this, "save.button.save"));
    defaultButton = saveSaveButton;
    saveSaveButton.addActionListener(new SaveListener());
    saveButtonsPanel.add(saveSaveButton);
    final JButton saveCloseButton =
      new JButton(Application.getString(this, "save.button.close"));
    saveCloseButton.addActionListener(new CloseListener());
    saveButtonsPanel.add(saveCloseButton);
    add(saveButtonsPanel, saveButtonsConstraints);

    log.fine("Memory/Save panel set up");
  }

  // save listener
  private class SaveListener implements ActionListener {
    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Save listener action started");
      int start = 0, end = 0, destination = 0;
      try {
	if (saveRadioRaw.isSelected()) {
	  start = saveRawStartField.getValue();
	  end = saveRawEndField.getValue();
	} else if (saveRadioHEX.isSelected()) {
	  start = saveHEXStartField.getValue();
	  end = saveHEXEndField.getValue();
	  if (saveHEXDestinationField.isEmpty()) {
	    destination = start;
	  } else {
	    destination = saveHEXDestinationField.getValue();
	  }
	} else if (saveRadioXML.isSelected()) {
	  start = saveXMLStartField.getValue();
	  end = saveXMLEndField.getValue();
	  if (saveXMLDestinationField.isEmpty()) {
	    destination = start;
	  } else {
	    destination = saveXMLDestinationField.getValue();
	  }
	}		    
      } catch (NumberFormatException exception) {
	InfoBox.display(panel, Application.getString(this, "incompleteForm"));
	return;
      }
      FileNameExtensionFilter filter;
      fileChooser.resetChoosableFileFilters();
      fileChooser.setAcceptAllFileFilterUsed(true);
      if (saveRadioRaw.isSelected()) {
	filter = rawFilter;
      } else if (saveRadioHEX.isSelected()) {
	filter = HEXFilter;
      } else {
	filter = XMLFilter;
      }
      fileChooser.addChoosableFileFilter(filter);
      fileChooser.setFileFilter(filter);
      if (fileChooser.showSaveDialog(panel) == JFileChooser.APPROVE_OPTION) {
	final File file = fileChooser.getSelectedFile();
	try {
	  if (saveRadioRaw.isSelected()) {
	    new Raw(
	      panel.getHardware(),
	      sourceMemoryBank,
	      destinationMemoryBank).write(file,
					   start,
					   ((end - start) & 0xffff) + 1);
	  } else if (saveRadioHEX.isSelected()) {
	    new IntelHEX(
	      panel.getHardware(),
	      sourceMemoryBank,
	      destinationMemoryBank).write(file,
					   start,
					   ((end - start) & 0xffff) + 1,
					   destination);
	  } else if (saveRadioXML.isSelected()) {
	    new XML(panel.getHardware(),
		    sourceMemoryBank,
		    destinationMemoryBank).write(file,
						 start,
						 ((end - start) & 0xffff) + 1,
						 destination);
	  } else {
	    new Snapshot(panel.getHardware()).write(file);
	  }
	} catch (RuntimeException exception) {
	  errorBox(exception);
	  return;
	}
	if (saveRadioSnapshot.isSelected()) {
	  InfoBox.display(panel,
			  Application.getString(this, "snapshotSaved"));
	} else {
	  InfoBox.display(panel,
			  Application.getString(this, "saved"));
	}
      }
    }
  }
}
