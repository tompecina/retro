/* ResetPanel.java
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.BorderLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.BorderFactory;
import javax.swing.JToggleButton;
import javax.swing.JButton;
import javax.swing.JSeparator;

import cz.pecina.retro.common.Application;

/**
 * The Reset panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class ResetPanel extends JPanel {

  // static logger
  private static final Logger log =
    Logger.getLogger(ResetPanel.class.getName());

  // enclosing frame
  private HidingFrame frame;

  // the computer hardware object
  private ComputerHardware computerHardware;

  // action buttons
  private JToggleButton resetButton, clearRAMButton, restoreROMButton, restoreRMMButton;

  // vertical filler between buttons
  private Component filler() {
    return Box.createRigidArea(new Dimension(0, 15));
  }
  
  /**
   * Creates the Reset panel.
   *
   * @param frame            the enclosing frame
   * @param computerHardware the computer hardware object
   */
  public ResetPanel(final HidingFrame frame,
		    final ComputerHardware computerHardware) {
    super(new BorderLayout());
    log.fine("New ResetPanel creation started");
    assert frame != null;
    assert computerHardware != null;
    this.frame = frame;
    this.computerHardware = computerHardware;

    final JPanel selectPanel = new JPanel();
    selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.PAGE_AXIS));
    selectPanel.setBorder(BorderFactory.createEmptyBorder(18, 35, 18, 35));

    resetButton =
      new JToggleButton(Application.getString(this, "reset.select.reset"));
    resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    selectPanel.add(resetButton);

    selectPanel.add(filler());
    clearRAMButton =
      new JToggleButton(Application.getString(this, "reset.select.clearRAM"));
    clearRAMButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    selectPanel.add(clearRAMButton);

    selectPanel.add(filler());
    restoreROMButton =
      new JToggleButton(Application.getString(this, "reset.select.restoreROM"));
    restoreROMButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    selectPanel.add(restoreROMButton);

    selectPanel.add(filler());
    restoreRMMButton =
      new JToggleButton(Application.getString(this, "reset.select.restoreRMM"));
    restoreRMMButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    selectPanel.add(restoreRMMButton);

    add(selectPanel);

    final JPanel executePanel = new JPanel();
    executePanel.setLayout(new BoxLayout(executePanel, BoxLayout.PAGE_AXIS));
    executePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

    executePanel.add(new JSeparator());
    executePanel.add(filler());
    
    final JButton executeButton =
      new JButton(Application.getString(this, "reset.button.execute"));
    executeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    executePanel.add(executeButton);
   
    add(executePanel, BorderLayout.PAGE_END);
    
    log.fine("Reset panel set up");
  }

  // // reset button listener
  // private class ResetListener implements ActionListener {

  //   // for description see ActionListener
  //   @Override
  //   public void actionPerformed(final ActionEvent event) {
  //     clearRAMButton.setSelected(true);
  //   }
  // }
  
  // close the frame
  private void closeFrame() {
    frame.close();
  }

  // close listener
  private class CloseListener implements ActionListener {

    // for description see ActionListener
    @Override
    public void actionPerformed(final ActionEvent event) {
      closeFrame();
    }
  }
}
