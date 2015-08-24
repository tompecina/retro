/* NudgeMouseWheelListener.java
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

package cz.pecina.retro.gui;

import java.util.logging.Logger;

import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;

/**
 * Nudge mouse wheel listener for a block of elements.  Presently only
 * SiSDBlock is supported.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class NudgeMouseWheelListener implements MouseWheelListener {

  // static logger
  private static final Logger log =
    Logger.getLogger(NudgeMouseWheelListener.class.getName());

  // block the listener is connected to
  private SiSDBlock block;

  // position of the element the listener is connected to
  private int position;

  // auxiliary parameters pre-computed by the constructor
  private int shift;
  private long mask;

  /**
   * Creates an instance of a nudge mouse wheel listener.
   *
   * @param block     the block the listener is connected to
   * @param position  relative position of the controlled element
   *                  ({@code 0} means the right-most element)
   */
  public NudgeMouseWheelListener(final SiSDBlock block, final int position) {
    log.fine("New NudgeMouseWheelListener creation started, position: " +
	     position);
    assert !block.isAlpha();
    assert (position >= 0) && (position < block.getSize());
    this.block = block;
    this.position = position;
    shift = (block.getSize() - position - 1) * 4;
    mask = 0x0f << shift;
    log.fine("New NudgeMouseWheelListener created");
  }

  // for decription see MouseWheelListener
  @Override
  public void mouseWheelMoved(final MouseWheelEvent event) {
    log.finer("NudgeMouseWheelListener event detected");
    if (!block.isBlank()) {
      final long state = (long)(block.getState());
      final int increment = -event.getWheelRotation();
      block.setState(((state + (increment << shift)) & mask) |
		     (state & (~mask)));	
      block.fireStateChanged();
    }
  }
}
