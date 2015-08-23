/* DebuggerFrame.java
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

import cz.pecina.retro.common.Application;

import cz.pecina.retro.debug.DebuggerPanel;
import cz.pecina.retro.debug.DebuggerHardware;

import cz.pecina.retro.gui.Resizeable;
import cz.pecina.retro.gui.GUI;

/**
 * The debugger frame.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class DebuggerFrame extends HidingFrame implements Resizeable {

  // static logger
  private static final Logger log =
    Logger.getLogger(DebuggerFrame.class.getName());

  // debugger panel
  private DebuggerPanel debuggerPanel;

  // computer control object
  private Computer computer;

  // debugger hardware object
  private DebuggerHardware debuggerHardware;

  /**
   * Creates the debugger frame.
   *
   * @param computer         the computer control object
   * @param debuggerHardware hardware to operate on
   */
  public DebuggerFrame(final Computer computer,
		       final DebuggerHardware debuggerHardware) {
    super(Application.getString(DebuggerFrame.class, "debugger.frameTitle"),
	  computer.getIconLayout().getIcon(IconLayout.ICON_POSITION_DEBUG));
    assert computer != null;
    assert debuggerHardware != null;
    log.fine("New DebuggerFrame creation started");
    this.computer = computer;
    this.debuggerHardware = debuggerHardware;
    debuggerPanel = new DebuggerPanel(this, debuggerHardware);
    add(debuggerPanel);
    pack();
    GUI.addResizeable(this);
    log.fine("DebuggerFrame set up");
  }

  // for description see HidingFrame
  @Override
  public void setUp() {
    computer.debuggerStop();
    debuggerHardware.activate();
  }

  // for description see HidingFrame
  @Override
  public void tearDown() {
    computer.debuggerHide();
  }

  // redraw frame
  private void redraw() {
    log.fine("DebuggerFrame redraw started");
    super.setTitle(Application.getString(this, "debugger.frameTitle"));
    remove(debuggerPanel);
    debuggerPanel = new DebuggerPanel(this, debuggerHardware);
    add(debuggerPanel);
    pack();
    log.fine("DebuggerFrame redraw completed");
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
