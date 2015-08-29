/* AboutPanel.java
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

import java.awt.FlowLayout;
import java.awt.BorderLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JButton;

import cz.pecina.retro.common.Application;

/**
 * The About panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class AboutPanel extends JPanel {

  // static logger
  private static final Logger log =
    Logger.getLogger(AboutPanel.class.getName());

  // enclosing frame
  private HidingFrame frame;

  /**
   * Creates the About panel.
   *
   * @param frame enclosing frame
   */
  public AboutPanel(final HidingFrame frame) {
    super();
    log.fine("New AboutPanel creation started");
    assert frame != null;
    this.frame = frame;

    setLayout(new BorderLayout());

    final JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.addTab(Application.getString(
      this, "about.logo"), new AboutLogo());
    tabbedPane.addTab(Application.getString(
      this, "about.photo"), new AboutPhoto());
    tabbedPane.addTab(Application.getString(
      this, "about.history"), new AboutHistory());
    tabbedPane.addTab(Application.getString(
      this, "about.help"), new AboutHelp());
    tabbedPane.addTab(Application.getString(
      this, "about.credits"), new AboutCredits());
    tabbedPane.addTab(Application.getString(
      this, "about.license"), new AboutLicense());

    final JPanel aboutButtonPane = new JPanel();
    aboutButtonPane.setLayout(new FlowLayout(FlowLayout.TRAILING,
					     10,
					     10));
    final JButton closeButton =
      new JButton(Application.getString(this, "about.button.close"));
    closeButton.addActionListener(new CloseListener());
    aboutButtonPane.add(closeButton);
	
    add(tabbedPane);
    add(aboutButtonPane, BorderLayout.PAGE_END);

    log.fine("About panel set up");
  }

  // close the frame
  private void closeFrame() {
    frame.close();
  }

  // close listener
  private class CloseListener implements ActionListener {
    @Override
    public void actionPerformed(final ActionEvent event) {
      closeFrame();
    }
  }
}
