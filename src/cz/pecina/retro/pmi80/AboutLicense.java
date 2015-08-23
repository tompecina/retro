/* AboutLicense.java
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

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;

import cz.pecina.retro.common.Application;

import cz.pecina.retro.gui.IconCache;
import cz.pecina.retro.gui.NonEditableJEditorPane;

/**
 * The About/License panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class AboutLicense extends JPanel {

  // static logger
  private static final Logger log =
    Logger.getLogger(AboutLicense.class.getName());

  /**
   * Creates the About/License panel.
   */
  public AboutLicense() {
    super(new BorderLayout());
    log.fine("New About/License panel creation started");
    final JLabel logo =
      new JLabel(IconCache.get("pmi80/AboutLicense/gpl.png"));
    logo.setBorder(BorderFactory.createEmptyBorder(8, 120, 8, 0));
    logo.setHorizontalAlignment(JLabel.LEFT);
    add(logo, BorderLayout.PAGE_START);
    JEditorPane editorPane;
    try {
      editorPane =
	new NonEditableJEditorPane(getClass().getResource("License/gpl.txt"));
    } catch (Exception exception) {
      log.fine("Error reading license");
      throw Application.createError(this, "licenseRead");
    }
    final JScrollPane scrollPane = new JScrollPane(editorPane);
    add(scrollPane);
    setPreferredSize(new Dimension());
    log.fine("About/License panel set up");
  }
}
