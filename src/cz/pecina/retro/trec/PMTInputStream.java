/* PMTInputStream.java
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

package cz.pecina.retro.trec;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * PMT format input stream.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class PMTInputStream extends DataInputStream {

  /**
   * Create PMT format input stream.
   * <p>
   * It is based on <code>DataInputStream</code>, using the following
   * format for storing non-negative long integers: The values are stored
   * as one or two <code>Integers</code>. If the value is less than
   * <code>Integer.MAX_VALUE</code>, it is stored as one <code>Integer</code>.
   * If it is greater, two <code>Integer</code>s are used, the first
   * storing the higher half with the sign inverted, the second the lower half.
   * The stream cannot hold negative values, <code>IOException</code> is
   * thrown if an attempt is made to store a negative value.
   *
   * @param in the specified input stream
   */
  public PMTInputStream(final InputStream in) {
    super(in);
  }

  /**
   * Reads the next <code>long</code> stored in compressed format.
   *
   * @return                 the next <code>long</code>
   * @exception EOFException if this input stream reaches the end before
   *                         reading the value
   * @exception IOException  the stream has been closed and the contained
   *                         input stream does not support reading after
   *                         close, or another I/O error occurs
   */
  public long readLongCompressed() throws IOException {
    final int firstInt = readInt();
    if (firstInt > 0) {
      return firstInt;
    }
    final int secondInt = readInt();
    return ((-firstInt) << 32) | secondInt;
  }
}
