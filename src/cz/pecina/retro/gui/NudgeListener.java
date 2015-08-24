/* NudgeListener.java
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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Nudge button listener for a block of elements.  Presently only
 * SiSDBlock is supported.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class NudgeListener extends MouseAdapter {

  // static logger
  private static final Logger log =
    Logger.getLogger(NudgeListener.class.getName());

  // block the listener is connected to
  private SiSDBlock block;

  // position of the element the listener is connected to
  private int position;

  // increment of the nudge button (-1 or +1)
  private int increment;

  // auxiliary parameters pre-computed by the constructor
  private int shift;
  private long mask;

  /**
   * Creates an instance of a nudge button listener.
   *
   * @param block     the block the listener is connected to
   * @param position  relative position of the controlled element
   *                  ({@code 0} means the right-most element)
   * @param increment increment of the nudge button ({@code -1}
   *                  or {@code +1})
   */
  public NudgeListener(final SiSDBlock block,
		       final int position,
		       final int increment) {
    super();
    log.fine("New NudgeListener creation started, position: " + position +
	     ", increment: " + increment);
    assert !block.isAlpha();
    assert (position >= 0) && (position < block.getSize());
    assert (increment == -1) || (increment == 1);
    this.block = block;
    this.position = position;
    this.increment = increment;
    shift = (block.getSize() - position - 1) * 4;
    mask = 0x0f << shift;
    log.fine("New NudgeListener created");
  }

  // for decription see MouseAdapter
  @Override
  public void mousePressed(final MouseEvent event) {
    log.finer("NudgeListener event detected");
    if (!block.isBlank()) {
      final long state = (long)(block.getState());
      block.setState(((state + (increment << shift)) & mask) |
		     (state & (~mask)));
      block.fireStateChanged();
    }
  }
}
