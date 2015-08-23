/* MemoryPanel.java
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

import javax.swing.JTabbedPane;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import cz.pecina.retro.common.Application;

import cz.pecina.retro.cpu.Hardware;

import cz.pecina.retro.gui.CloseableFrame;

/**
 * The Memory panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class MemoryPanel extends JTabbedPane {

  // static logger
  private static final Logger log =
    Logger.getLogger(MemoryPanel.class.getName());

  // enclosing frame
  private CloseableFrame frame;

  // hardware for snapshots
  private Hardware hardware;

  // tabs
  private final MemoryTab[] tabs;

  /**
   * Creates the memory panel.
   *
   * @param frame       enclosing frame
   * @param hardware    hardware for shapshots
   * @param savePlugins array of save plugins
   * @param loadPlugins array of load plugins
   */
  public MemoryPanel(final CloseableFrame frame,
		     final Hardware hardware,
		     final MemoryPlugin[] savePlugins,
		     final MemoryPlugin[] loadPlugins) {
    super();
    log.fine("New MemoryPanel creation started");
    this.frame = frame;
    this.hardware = hardware;

    tabs = new MemoryTab[] {new Save(this, savePlugins),
			    new Load(this, loadPlugins),
			    new CopyFillCompare(this)};
    addTab(Application.getString(this, "save"), tabs[0]);
    addTab(Application.getString(this, "load"), tabs[1]);
    addTab(Application.getString(this, "copyFillCompare"), tabs[2]);
    frame.getRootPane().setDefaultButton(tabs[0].getDefaultButton());
    addChangeListener(
      new ChangeListener() {
	@Override
	  public void stateChanged(final ChangeEvent event) {
	  frame.getRootPane().setDefaultButton(
	    tabs[MemoryPanel.this.getSelectedIndex()].getDefaultButton());
	}
      });

    log.fine("Memory panel set up");
  }

  /**
   * Close the enclosing frame.
   */
  public void closeFrame() {
    frame.close();
  }

  /**
   * Gets the <code>Hardware</code> object.
   *
   * @return the <code>Hardware</code> object
   */
  public Hardware getHardware() {
    return hardware;
  }
}
