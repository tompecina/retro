/* TapeRecorderFrame.java
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

import cz.pecina.retro.common.Application;

import cz.pecina.retro.trec.TapeRecorderHardware;
import cz.pecina.retro.trec.TapeRecorderPanel;

import cz.pecina.retro.gui.Resizeable;
import cz.pecina.retro.gui.GUI;

/**
 * The tape recorder frame.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class TapeRecorderFrame extends HidingFrame implements Resizeable {

  // static logger
  private static final Logger log =
    Logger.getLogger(TapeRecorderFrame.class.getName());

  // tape recorder panel
  private TapeRecorderPanel tapeRecorderPanel;

  // tape recorder hardware object
  private TapeRecorderHardware tapeRecorderHardware;

  /**
   * Creates the tape recorder frame.
   *
   * @param computer             the computer control object
   * @param tapeRecorderHardware hardware to operate on
   */
  public TapeRecorderFrame(final Computer computer,
			   final TapeRecorderHardware tapeRecorderHardware) {
    super(Application.getString(TapeRecorderFrame.class,
      "tapeRecorder.frameTitle"), computer.getIconLayout()
      .getIcon(IconLayout.ICON_POSITION_CASSETTE));
    log.fine("New TapeRecorderFrame creation started");
    this.tapeRecorderHardware = tapeRecorderHardware;
    tapeRecorderPanel = new TapeRecorderPanel(this, tapeRecorderHardware);
    add(tapeRecorderPanel);
    pack();
    GUI.addResizeable(this);
    log.fine("TapeRecorderFrame set up");
  }

  // redraw frame
  private void redraw() {
    log.fine("TapeRecorderFrame redraw started");
    super.setTitle(Application.getString(this, "tapeRecorder.frameTitle"));
    remove(tapeRecorderPanel);
    tapeRecorderPanel = new TapeRecorderPanel(this, tapeRecorderHardware);
    add(tapeRecorderPanel);
    pack();
    log.fine("TapeRecorderFrame redraw completed");
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
