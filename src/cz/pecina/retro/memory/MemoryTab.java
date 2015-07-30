/* MemoryTab.java
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
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JFileChooser;
import javax.swing.JButton;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import cz.pecina.retro.common.Application;
import cz.pecina.retro.gui.ErrorBox;

/**
 * Memory tab.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public abstract class MemoryTab extends JPanel {

  // static logger
  private static final Logger log =
    Logger.getLogger(MemoryTab.class.getName());

  // temporary fix
  protected static final String sourceMemoryBank = "COMBINED";
  protected static final String destinationMemoryBank = "COMBINED";

  /**
   * The enclosing MemoryPanel object.
   */
  protected MemoryPanel panel;

  /**
   * File chooser used by Save and Load panels.
   */
  protected static final JFileChooser fileChooser =
    new JFileChooser(new File("."));

  /**
   * Raw extension filter used by Save and Load panels.
   */
  protected static final FileNameExtensionFilter rawFilter =
    new FileNameExtensionFilter(
      Application.getString(MemoryTab.class, "fileFilter.raw"), "bin");

  /**
   * HEX extension filter used by Save and Load panels.
   */
  protected static final FileNameExtensionFilter HEXFilter =
    new FileNameExtensionFilter(
      Application.getString(MemoryTab.class, "fileFilter.HEX"), "hex");

  /**
   * XML extension filter used by Save and Load panels.
   */
  protected static final FileNameExtensionFilter XMLFilter =
    new FileNameExtensionFilter(
      Application.getString(MemoryTab.class, "fileFilter.XML"), "xml");
	
  /**
   * The default button.
   */
  protected JButton defaultButton;

  /**
   * Creates a MemoryTab instance.
   *
   * @param panel enclosing panel
   */
  public MemoryTab(final MemoryPanel panel) {
    super(new GridBagLayout());
    this.panel = panel;
  }

  /**
   * Closes the enclosing frame.
   */
  protected void closeFrame() {
    panel.closeFrame();
  }

  /**
   * Close listener.
   */
  protected class CloseListener implements ActionListener {
    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Close listener action started");
      closeFrame();
    }
  }
    
  /**
   * Displays localized error message.
   *
   * @param exception exception to be reported
   */
  protected void errorBox(final RuntimeException exception) {
    ErrorBox.display(
      panel,
      String.format(Application.getString(this, "error.message"),
        exception.getLocalizedMessage()));
  }

  /**
   * Gets the default button.
   *
   * @return the default button
   */
  public JButton getDefaultButton() {
    return defaultButton;
  }
}
