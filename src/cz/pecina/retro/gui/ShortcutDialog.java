/* ShortcutDialog.java
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

package cz.pecina.retro.gui;

import java.util.logging.Logger;
import java.io.File;
import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.JLabel;
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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import cz.pecina.retro.common.Application;
import cz.pecina.retro.gui.ErrorBox;
import cz.pecina.retro.gui.InfoBox;
import cz.pecina.retro.gui.ConfirmationBox;

/**
 * Shortcut selection dialog box.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class ShortcutDialog extends JDialog {

  // static logger
  private static final Logger log =
    Logger.getLogger(ShortcutDialog.class.getName());

  // // enclosing panel
  // private TapeRecorderPanel panel;

  /**
   * Displayes a shortcut selection dialog and returns the result.
   *
   * @param  frame             enclosing frame
   * @param  assignedShortcuts list of shortcuts already assigned
   * @return                   the shortcut or <code>null</code> if aborted
   */
  public static Shortcut getShortcut(
    final Frame frame,
    final Iterable<Shortcut> assignedShortcuts) {
    log.fine("New ShortcutDialog creation started");
    new ShortcutDialog(frame);
    return null;
  }

  // private construtor
  private ShortcutDialog(final Frame frame) {
    super(frame,
	  Application.getString(ShortcutDialog.class, "shortcutDialog.title"),
	  true);
    final JPanel dialogPanel = new JPanel(new BorderLayout());
    final ButtonGroup saveGroup = new ButtonGroup();
    final JPanel promptPanel =
      new JPanel(new BorderLayout());
    final JLabel promptLabel = new JLabel(Application.getString(ShortcutDialog.class, "shortcutDialog.prompt"));
    promptLabel.setHorizontalAlignment(JLabel.CENTER);
    promptPanel.add(promptLabel);
    promptPanel.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
    dialogPanel.add(promptPanel);

    final JPanel buttonsPanel = new JPanel(new FlowLayout());
    final JButton setButton =
      new JButton(Application.getString(ShortcutDialog.class, "shortcutDialog.button.set"));
    // setButton.addActionListener(new SetListener());
    setButton.setEnabled(false);
    buttonsPanel.add(setButton);
    final JButton cancelButton =
      new JButton(Application.getString(ShortcutDialog.class, "shortcutDialog.button.cancel"));
    // cancelButton.addActionListener(new CloseListener());
    buttonsPanel.add(cancelButton);
    dialogPanel.add(buttonsPanel, BorderLayout.PAGE_END);

    add(dialogPanel);
    setMinimumSize(new Dimension(400, 150));
    pack();
    setLocationRelativeTo(frame);
    setFocusable(true);
    addKeyListener(new ShortcutListener());
    setVisible(true);
  }

  // shortcut listener
  private class ShortcutListener extends KeyAdapter {
    @Override
    public void keyPressed(final KeyEvent e) {
      System.err.println(e);
      System.err.println(e.getKeyCode());
      System.err.println(KeyEvent.getKeyText(e.getKeyCode()));
      System.err.println(e.getKeyLocation());
    }
  }

  // // Displays localized error message
  // private void errorBox(final RuntimeException exception) {
  //   ErrorBox.display(panel, String.format(Application
  //     .getString(this, "ejectDialog.error.message"),
  //     exception.getLocalizedMessage()));
  // }

  // // save listener
  // private class SaveListener implements ActionListener {
  //   @Override
  //   public void actionPerformed(final ActionEvent event) {
  //     log.finer("Save listener action started");
  //     prepareFilters();
  //     if (fileChooser.showSaveDialog(ShortcutDialog.this) ==
  // 	  JFileChooser.APPROVE_OPTION) {
  // 	final File file = fileChooser.getSelectedFile();
  // 	try {
  // 	  if (formatXML.isSelected()) {
  // 	    new XML(tapeRecorderHardware.getTape(),
  // 	      tapeRecorderHardware.getTapeRecorderInterface()).write(file);
  // 	  } else if (formatPMT.isSelected()) {
  // 	    new PMT(tapeRecorderHardware.getTape(),
  // 	      tapeRecorderHardware.getTapeRecorderInterface()).write(file);
  // 	  } else if (formatPMITAPE.isSelected()) {
  // 	    new PMITAPE(tapeRecorderHardware.getTape(),
  // 	      tapeRecorderHardware.getTapeRecorderInterface()).write(file);
  // 	  } else {
  // 	    assert formatSAM.isSelected();
  // 	    new SAM(tapeRecorderHardware.getTape(),
  // 	      tapeRecorderHardware.getTapeRecorderInterface()).write(file);
  // 	  }
  // 	} catch (RuntimeException exception) {
  // 	  errorBox(exception);
  // 	  return;
  // 	}
  // 	setVisible(false);
  // 	InfoBox.display(panel, Application
  // 	  .getString(this, "ejectDialog.tapeSaved"));
  //     }
  //   }
  // }
	
  // // load listener
  // private class LoadListener implements ActionListener {
  //   @Override
  //   public void actionPerformed(final ActionEvent event) {
  //     log.finer("Load listener action started");
  //     if (tapeRecorderHardware.getTape().isEmpty() ||
  // 	  (ConfirmationBox.display(panel, Application
  // 	  .getString(this, "ejectDialog.confirm.question")) ==
  // 	   JOptionPane.YES_OPTION)) {
  // 	prepareFilters();
  // 	if (fileChooser.showOpenDialog(ShortcutDialog.this) ==
  // 	    JFileChooser.APPROVE_OPTION) {
  // 	  final File file = fileChooser.getSelectedFile();
  // 	  try {
  // 	    if (formatXML.isSelected()) {
  // 	      new XML(tapeRecorderHardware.getTape(),
  // 	        tapeRecorderHardware.getTapeRecorderInterface()).read(file);
  // 	    } else if (formatPMT.isSelected()) {
  // 	      new PMT(tapeRecorderHardware.getTape(),
  // 	        tapeRecorderHardware.getTapeRecorderInterface()).read(file);
  // 	    } else if (formatPMITAPE.isSelected()) {
  // 	      new PMITAPE(tapeRecorderHardware.getTape(),
  // 	        tapeRecorderHardware.getTapeRecorderInterface()).read(file);
  // 	    } else {
  // 	      assert formatSAM.isSelected();
  // 	      new SAM(tapeRecorderHardware.getTape(),
  // 	        tapeRecorderHardware.getTapeRecorderInterface()).read(file);
  // 	    }
  // 	  } catch (RuntimeException exception) {
  // 	    errorBox(exception);
  // 	    return;
  // 	  }
  // 	  tapeRecorderHardware.resetTape();
  // 	  setVisible(false);
  // 	  InfoBox.display(panel, Application
  // 	    .getString(this, "ejectDialog.tapeLoaded"));
  // 	}
  //     }
  //   }
  // }

  // // blank listener	
  // private class BlankListener implements ActionListener {
  //   @Override
  //   public void actionPerformed(final ActionEvent event) {
  //     log.finer("Blank listener action started");
  //     if (tapeRecorderHardware.getTape().isEmpty() ||
  // 	  (ConfirmationBox.display(panel, Application
  // 	  .getString(this, "ejectDialog.confirm.question")) ==
  // 	   JOptionPane.YES_OPTION)) {
  // 	tapeRecorderHardware.getTape().clear();
  // 	tapeRecorderHardware.resetTape();
  // 	setVisible(false);
  // 	InfoBox.display(panel, Application
  // 	  .getString(this, "ejectDialog.tapeBlank"));
  //     }
  //   }
  // }
	
  // // close listener
  // private class CloseListener implements ActionListener {
  //   @Override
  //   public void actionPerformed(final ActionEvent event) {
  //     log.finer("Close listener action started");
  //     setVisible(false);
  //   }
  // }
}
