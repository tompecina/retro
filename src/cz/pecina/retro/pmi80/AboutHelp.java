/* AboutHelp.java
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

import java.awt.Dimension;

import javax.swing.JScrollPane;

import cz.pecina.retro.common.Application;

import cz.pecina.retro.gui.NonEditableJEditorPane;

/**
 * The About/Help panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class AboutHelp extends JScrollPane {

  // static logger
  private static final Logger log =
    Logger.getLogger(AboutHelp.class.getName());

  /**
   * Creates the About/Help panel.
   */
  public AboutHelp() {
    super();
    log.fine("New About/Help panel creation started");
    setViewportView(new NonEditableJEditorPane(
      "text/html",
      Application.getString(this, "about.help.contents")));
    setPreferredSize(new Dimension());
    log.fine("About/Help panel set up");
  }
}
