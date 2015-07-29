/* MemoryFrame.java
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
import cz.pecina.retro.common.Parameters;
import cz.pecina.retro.common.Application;
import cz.pecina.retro.cpu.Hardware;
import cz.pecina.retro.cpu.SimpleMemory;
import cz.pecina.retro.memory.MemoryPanel;

/**
 * The Memory frame.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class MemoryFrame extends HidingFrame {

  // static logger
  private static final Logger log =
    Logger.getLogger(MemoryFrame.class.getName());

  // Memory panel
  private MemoryPanel memoryPanel;

  // hardware object to operate on
  private Hardware hardware;

  /**
   * Creates the Memory frame.
   *
   * @param computer the computer control object
   * @param hardware the hardware object to operate on
   */
  public MemoryFrame(final Computer computer, final Hardware hardware) {
    super(Application.getString(MemoryFrame.class, "memory.frameTitle"),
	  computer.getIconLayout().getIcon(IconLayout.ICON_POSITION_MEM));
    log.fine("New MemoryFrame creation started");
    assert hardware != null;
    this.hardware = hardware;
    memoryPanel = new MemoryPanel(this, hardware);
    add(memoryPanel);
    pack();
    log.fine("MemoryFrame set up");
  }

  // redraw frame
  private void redraw() {
    log.fine("MemoryFrame redraw started");
    super.setTitle(Application.getString(this, "memory.frameTitle"));
    remove(memoryPanel);
    memoryPanel = new MemoryPanel(this, hardware);
    add(memoryPanel);
    pack();
    log.fine("MemoryFrame redraw completed");
  }

  // for description see Localized
  @Override
  public void redrawOnLocaleChange() {
    redraw();
  }

  // for description see HidingFrame
  @Override
  protected void setUp() {
    hardware.suspend();
  }

  // for description see HidingFrame
  @Override
  protected void tearDown() {
    final SimpleMemory memory = (SimpleMemory)Parameters.memoryObject;
    UserPreferences.setStartROM(memory.getStartROM());
    UserPreferences.setStartRAM(memory.getStartRAM());
    hardware.resume();
  }
}
