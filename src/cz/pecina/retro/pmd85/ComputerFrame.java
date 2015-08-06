/* ComputerFrame.java
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
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import cz.pecina.retro.common.Localized;
import cz.pecina.retro.common.Application;
import cz.pecina.retro.gui.Resizeable;
import cz.pecina.retro.gui.GUI;

/**
 * The frame holding the main control panel (keyboard and display).
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class ComputerFrame extends JFrame implements Localized, Resizeable {

  // static logger
  private static final Logger log =
    Logger.getLogger(ComputerFrame.class.getName());
    
  // control panel of the computer
  private ComputerPanel computerPanel;

  // the display hardware object
  // private DisplayHardware displayHardware;

  // the computer control object
  private Computer computer;

  // the keyboard hardware object
  // private KeyboardHardware keyboardHardware;

  /**
   * Creates the main computer control panel frame.
   *
   * @param computer         the computer control object
   * @param displayHardware  the display hardware to operate on
   * @param keyboardHardware the keyboard hardware to operate on
   */
  public ComputerFrame(final Computer computer //,
		       // final DisplayHardware displayHardware,
		       // final KeyboardHardware keyboardHardware
		       ) {
    super(Application.getString(ComputerFrame.class, "appName"));
    log.fine("New ComputerFrame creation started");
    assert computer != null;
    // assert displayHardware != null;
    // assert keyboardHardware != null;
    this.computer = computer;
    // this.displayHardware = displayHardware;
    // this.keyboardHardware = keyboardHardware;
    log.finer("Application icons set up");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    computerPanel =
      new ComputerPanel(computer /* , displayHardware, keyboardHardware */);
    add(computerPanel);
    pack();
    setLocationRelativeTo(null);
    Application.addLocalized(this);
    GUI.addResizeable(this);
    GUI.setApplicationIcons(this);
    setVisible(true);
    log.fine("ComputerFrame set up and displayed");
  }

  // redraw frame
  private void redraw() {
    log.fine("ComputerFrame redraw started");
    ComputerFrame.super.setTitle(Application.getString(this, "appName"));
    remove(computerPanel);
    computerPanel =
      new ComputerPanel(computer /* , displayHardware, keyboardHardware */);
    add(computerPanel);
    pack();
    log.fine("ComputerFrame redraw completed");
  }

  // for description see Localized
  @Override
  public void redrawOnLocaleChange() {
    redraw();
  }

  // for description see Resizeable
  @Override
  public void redrawOnPixelResize() {
    redraw();
  }
}
