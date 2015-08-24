/* PMDValidBlock.java
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

package cz.pecina.retro.trec;

import java.util.logging.Logger;

import java.util.List;
import java.util.ArrayList;

import cz.pecina.retro.common.Application;

/**
 * PMD 85 valid tape block.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class PMDValidBlock extends PMDBlock {

  // static logger
  private static final Logger log =
    Logger.getLogger(PMDValidBlock.class.getName());

  // block length
  private int length;

  /**
   * Constructs a new PMD 85 valid block (i.e., a block with
   * a correct check sum).
   *
   * @param  list          list of input data
   * @throws TapeException on error in data
   */
  public PMDValidBlock(final List<Byte> list) throws TapeException {
    super(list);
    log.fine("Creating new PMD valid block");

    // check block size
    final int size = list.size();
    if (size < 2) {
      log.finer("Block too short");
      throw new TapeException("Block too short");
    }

    // test check sum
    if (PMDUtil.checkSum(list, 0, size - 1) != (list.get(size - 1))) {
      log.finer("Bad chech sum");
      throw new TapeException("Bad check sum");
    }
    log.finest("Checksum ok");
    
    log.finer("New PMD valid block set up");
  }
}
