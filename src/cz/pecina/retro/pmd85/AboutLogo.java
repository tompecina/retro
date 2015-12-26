/* AboutLogo.java
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
import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BorderFactory;

import cz.pecina.retro.common.Application;

import cz.pecina.retro.gui.IconCache;

/**
 * The About/Logo panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class AboutLogo extends JPanel {

  // static logger
  private static final Logger log =
    Logger.getLogger(AboutLogo.class.getName());

  /**
   * Creates the About/Logo panel.
   */
  public AboutLogo() {
    super(new BorderLayout());
    log.fine("New About/Logo panel creation started");
    setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
    final JLabel logo =
      new JLabel(IconCache.get("pmd85/AboutLogo/tesla.png"));
    add(logo);
    final JPanel contents = new JPanel();
    contents.setLayout(new BoxLayout(contents, BoxLayout.PAGE_AXIS));
    final JLabel name =
      new JLabel(Application.getString(this, "about.logo.name"));
    name.setAlignmentX(Component.CENTER_ALIGNMENT);
    contents.add(name);
    final JLabel version =
      new JLabel(String.format(Application.getString(
      this, "about.logo.version"), "@VERSION@"));
    version.setAlignmentX(Component.CENTER_ALIGNMENT);
    contents.add(version);
    add(contents, BorderLayout.PAGE_END);
    log.fine("About/Logo panel set up");
  }
}
