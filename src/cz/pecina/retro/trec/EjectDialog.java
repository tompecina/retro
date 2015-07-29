/* EjectDialog.java
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

package cz.pecina.retro.trec;

import java.util.logging.Logger;
import java.io.File;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JDialog;
import javax.swing.JRadioButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import cz.pecina.retro.common.Parameters;
import cz.pecina.retro.common.Application;
import cz.pecina.retro.gui.ErrorBox;
import cz.pecina.retro.gui.InfoBox;
import cz.pecina.retro.gui.ConfirmationBox;

/**
 * Eject button dialog box.  It is displayed on pressing the eject button.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class EjectDialog extends JDialog {

  // static logger
  private static final Logger log =
    Logger.getLogger(EjectDialog.class.getName());

  // components holding values used by listeners
  private JRadioButton formatXML, formatPMT, formatPMITAPE, formatSAM;

  // enclosing panel
  private TapeRecorderPanel panel;

  // tape recorder hardware
  private TapeRecorderHardware tapeRecorderHardware;

  // common file chooser
  private static final JFileChooser fileChooser = new JFileChooser();

  // file name extension filters
  private static final FileNameExtensionFilter XMLFilter =
    new FileNameExtensionFilter(Application.getString(
      EjectDialog.class, "fileFilter.XML"), "xml");
  private static final FileNameExtensionFilter PMTFilter =
    new FileNameExtensionFilter(Application.getString(
      EjectDialog.class, "fileFilter.PMT"), "pmt");
  private static final FileNameExtensionFilter PMITAPEFilter =
    new FileNameExtensionFilter(Application.getString(
      EjectDialog.class, "fileFilter.PMITAPE"), "pmitape");
  private static final FileNameExtensionFilter SAMFilter =
    new FileNameExtensionFilter(Application.getString(
      EjectDialog.class, "fileFilter.SAM"), "sam");

  /**
   * Creates the eject button dialog box.
   *
   * @param panel                enclosing panel
   * @param tapeRecorderHardware tape recorder hardware
   */
  public EjectDialog(final TapeRecorderPanel panel,
		     final TapeRecorderHardware tapeRecorderHardware) {
    super(panel.getFrame(),
	  Application.getString(EjectDialog.class, "ejectDialog.title"), true);
    log.fine("New EjectDialog creation started");
    this.panel = panel;
    this.tapeRecorderHardware = tapeRecorderHardware;
	
    final JPanel dialogPanel = new JPanel(new BorderLayout());
    final ButtonGroup saveGroup = new ButtonGroup();

    final JPanel formatsPanel =
      new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
    formatsPanel.setBorder(BorderFactory.createTitledBorder(Application
      .getString(this, "ejectDialog.format")));
    final ButtonGroup group = new ButtonGroup();
    if (true)  {  // XML is the default format, it cannot be suppressed
      formatXML =
	new JRadioButton(Application.getString(this, "ejectDialog.XML"));
      group.add(formatXML);
      formatsPanel.add(formatXML);
      formatXML.setSelected(true);
    }
    if (Parameters.tapeFormats.contains("PMT")) {
      formatPMT =
	new JRadioButton(Application.getString(this, "ejectDialog.PMT"));
      group.add(formatPMT);
      formatsPanel.add(formatPMT);
    }
    if (Parameters.tapeFormats.contains("PMITAPE")) {
      formatPMITAPE =
	new JRadioButton(Application.getString(this, "ejectDialog.PMITAPE"));
      group.add(formatPMITAPE);
      formatsPanel.add(formatPMITAPE);
    }
    if (Parameters.tapeFormats.contains("SAM")) {
      formatSAM =
	new JRadioButton(Application.getString(this, "ejectDialog.SAM"));
      group.add(formatSAM);
      formatsPanel.add(formatSAM);
    }
    dialogPanel.add(formatsPanel);

    final JPanel buttonsPanel = new JPanel(new FlowLayout());
    final JButton saveButton =
      new JButton(Application.getString(this, "ejectDialog.save"));
    saveButton.addActionListener(new SaveListener());
    buttonsPanel.add(saveButton);
    final JButton loadButton =
      new JButton(Application.getString(this, "ejectDialog.load"));
    loadButton.addActionListener(new LoadListener());
    buttonsPanel.add(loadButton);
    final JButton blankButton =
      new JButton(Application.getString(this, "ejectDialog.blank"));
    blankButton.addActionListener(new BlankListener());
    buttonsPanel.add(blankButton);
    final JButton cancelButton =
      new JButton(Application.getString(this, "ejectDialog.cancel"));
    cancelButton.addActionListener(new CloseListener());
    buttonsPanel.add(cancelButton);
    dialogPanel.add(buttonsPanel, BorderLayout.PAGE_END);

    add(dialogPanel);
    pack();
    setLocationRelativeTo(panel.getFrame());
    setVisible(true);
    panel.getTapeRecorderHardware().getTapeRecorderButtonsLayout()
      .getButton(TapeRecorderButtonsLayout.BUTTON_POSITION_EJECT)
      .setPressed(false);
  }

  // prepare file name extension filters
  private void prepareFilters() {
    log.finer("File name extension filters prepared");
    FileNameExtensionFilter filter;
    fileChooser.resetChoosableFileFilters();
    fileChooser.setAcceptAllFileFilterUsed(true);
    if (formatXML.isSelected()) {
      filter = XMLFilter;
    } else if (formatPMT.isSelected()) {
      filter = PMTFilter;
    } else if (formatPMITAPE.isSelected()) {
      filter = PMITAPEFilter;
    } else {
      filter = SAMFilter;
    }
    fileChooser.addChoosableFileFilter(filter);
    fileChooser.setFileFilter(filter);
  }

  // Displays localized error message
  private void errorBox(final RuntimeException exception) {
    ErrorBox.display(panel, String.format(Application
      .getString(this, "ejectDialog.error.message"),
      exception.getLocalizedMessage()));
  }

  // save listener
  private class SaveListener implements ActionListener {
    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Save listener action started");
      prepareFilters();
      if (fileChooser.showSaveDialog(EjectDialog.this) ==
	  JFileChooser.APPROVE_OPTION) {
	final File file = fileChooser.getSelectedFile();
	try {
	  if (formatXML.isSelected()) {
	    new XML(tapeRecorderHardware.getTape()).write(file);
	  } else if (formatPMT.isSelected()) {
	    new PMT(tapeRecorderHardware.getTape()).write(file);
	  } else if (formatPMITAPE.isSelected()) {
	    new PMITAPE(tapeRecorderHardware.getTape()).write(file);
	  } else {
	    assert formatSAM.isSelected();
	    new SAM(tapeRecorderHardware.getTape()).write(file);
	  }
	} catch (RuntimeException exception) {
	  errorBox(exception);
	  return;
	}
	setVisible(false);
	InfoBox.display(panel, Application
	  .getString(this, "ejectDialog.tapeSaved"));
      }
    }
  }
	
  // load listener
  private class LoadListener implements ActionListener {
    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Load listener action started");
      if (tapeRecorderHardware.getTape().isEmpty() ||
	  (ConfirmationBox.display(panel, Application
	  .getString(this, "ejectDialog.confirm.question")) ==
	   JOptionPane.YES_OPTION)) {
	prepareFilters();
	if (fileChooser.showOpenDialog(EjectDialog.this) ==
	    JFileChooser.APPROVE_OPTION) {
	  final File file = fileChooser.getSelectedFile();
	  try {
	    if (formatXML.isSelected()) {
	      new XML(tapeRecorderHardware.getTape()).read(file);
	    } else if (formatPMT.isSelected()) {
	      new PMT(tapeRecorderHardware.getTape()).read(file);
	    } else if (formatPMITAPE.isSelected()) {
	      new PMITAPE(tapeRecorderHardware.getTape()).read(file);
	    } else {
	      assert formatSAM.isSelected();
	      new SAM(tapeRecorderHardware.getTape()).read(file);
	    }
	  } catch (RuntimeException exception) {
	    errorBox(exception);
	    return;
	  }
	  tapeRecorderHardware.resetTape();
	  setVisible(false);
	  InfoBox.display(panel, Application
	    .getString(this, "ejectDialog.tapeLoaded"));
	}
      }
    }
  }

  // blank listener	
  private class BlankListener implements ActionListener {
    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Blank listener action started");
      if (tapeRecorderHardware.getTape().isEmpty() ||
	  (ConfirmationBox.display(panel, Application
	  .getString(this, "ejectDialog.confirm.question")) ==
	   JOptionPane.YES_OPTION)) {
	tapeRecorderHardware.getTape().clear();
	tapeRecorderHardware.resetTape();
	setVisible(false);
	InfoBox.display(panel, Application
	  .getString(this, "ejectDialog.tapeBlank"));
      }
    }
  }
	
  // close listener
  private class CloseListener implements ActionListener {
    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Close listener action started");
      setVisible(false);
    }
  }
}
