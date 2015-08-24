/* PMTOutputStream.java
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

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * PMT format output stream.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class PMTOutputStream extends DataOutputStream {

  /**
   * Create PMT format output stream.
   * <p>
   * It is based on {@code DataInputStream}, using the following
   * format for storing non-negative long integers: The values are stored
   * as one or two {@code Integers}. If the value is less than
   * {@code Integer.MAX_VALUE}, it is stored as one {@code Integer}.
   * If it is greater, two {@code Integer}s are used, the first
   * storing the higher half with the sign inverted, the second the lower half.
   * The stream cannot hold negative values, {@code IOException} is
   * thrown if an attempt is made to store a negative value.
   *
   * @param out the underlying output stream
   */
  public PMTOutputStream(final OutputStream out) {
    super(out);
  }

  /**
   * Writes the next {@code long} in compressed format.
   *
   * @param  v           a {@code long} to be written
   * @throws IOException if an I/O error occurs
   */
  public void writeLongCompressed(final long v) throws IOException {
    assert v >= 0;
    if (v <= 0x7fffffff) {
      writeInt((int)v);
    } else {
      writeInt((int)(-(v >> 32)));
      writeInt((int)(v & 0xffffffff));
    }
  }
}
