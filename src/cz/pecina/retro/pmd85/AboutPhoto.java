/* AboutPhoto.java
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

package cz.pecina.retro.pmd85;

import java.util.logging.Logger;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BorderFactory;

import cz.pecina.retro.common.Application;

import cz.pecina.retro.gui.IconCache;

/**
 * The About/Photo panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class AboutPhoto extends JPanel {

  // static logger
  private static final Logger log =
    Logger.getLogger(AboutPhoto.class.getName());

  /**
   * Creates the About/Photo panel.
   */
  public AboutPhoto() {
    super(new BorderLayout(0, 10));
    log.fine("New About/Photo panel creation started");
    setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
    final JLabel photo = new JLabel(IconCache.get("pmd85/AboutPhoto/pmd.jpg"));
    add(photo, BorderLayout.PAGE_START);
    final JLabel credits =
      new JLabel(Application.getString(this, "about.photo.contents"),
		 JLabel.CENTER);
    add(credits);
    log.fine("About/Photo panel set up");
  }
}
