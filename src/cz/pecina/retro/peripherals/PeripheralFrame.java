/* PeripheralFrame.java
 *
 * Copyright (C) 2014-2015, Tomáš Pecina <tomas@pecina.cz>
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

package cz.pecina.retro.peripherals;

import java.util.logging.Logger;
import javax.swing.JFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import cz.pecina.retro.common.Localized;
import cz.pecina.retro.common.Application;
import cz.pecina.retro.gui.GUI;

/**
 * Abstract peripheral frame.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public abstract class PeripheralFrame extends JFrame implements Localized {

  // static logger
  private static final Logger log =
    Logger.getLogger(PeripheralFrame.class.getName());

  // linked peripheral
  private Peripheral peripheral;

  // title of the frame
  private String title;

  /**
   * Creates an instance of a peripheral <code>JFrame</code>.
   *
   * @param peripheral linked peripheral
   */
  public PeripheralFrame(final Peripheral peripheral) {
    super();
    assert peripheral != null;
    this.peripheral = peripheral;
    title = peripheral.getFrameTitle();
    setTitle(title);
    Application.addLocalized(this);
    GUI.setApplicationIcons(this);
    log.fine("New peripheral frame set up: " + title);
  }

  /**
   * Completes and displays the frame.
   */
  protected void postamble() {
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    addWindowListener(new CloseListener());
    pack();
    setLocationRelativeTo(null);
    setVisible(true);
    log.fine("Postamble completed for peripheral frame: " + title);
  }

  // close listener
  private class CloseListener extends WindowAdapter {
    @Override
    public void windowClosing(final WindowEvent event) {
      Application.removeLocalized(PeripheralFrame.this);
      peripheral.deactivate();
    }
  }

  // for description see Localized
  @Override
  public void redrawOnLocaleChange() {
    title = peripheral.getFrameTitle();
    setTitle(title);
  }
}
