/* PMDBlock.java
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
 * PMD 85 tape block.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class PMDBlock {

  // static logger
  private static final Logger log =
    Logger.getLogger(PMDBlock.class.getName());

  // block data
  private List<Byte> bytes = new ArrayList<>();

  /**
   * Constructs a new PMD 85 tape block.
   *
   * @param     list          list of input data
   * @exception TapeException on error in data
   */
  public PMDBlock(final List<Byte> list) throws TapeException {
    log.fine("Creating new PMD block");

    // check data
    if ((list == null) || list.isEmpty()) {
      log.finer("Invalid data, null or empty block not allowed");
      throw new TapeException("Invalid data");
    }

    // make a local copy of the data
    for (byte b: list) {
      bytes.add(b);
    }
    
    log.finer("New PMD tape block set up");
  }

  /**
   * Gets the block length.
   *
   * @return the body length
   */
  public int getLength() {
    return bytes.size();
  }

  /**
   * Gets the list of bytes in the block.
   *
   * @return the list of bytes
   */
  public List<Byte> getBytes() {
    return bytes;
  }
}
