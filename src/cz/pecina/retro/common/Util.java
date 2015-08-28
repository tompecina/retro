/* Util.java
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

package cz.pecina.retro.common;

import java.util.List;
import java.util.ArrayList;

/**
 * Miscellaneous static methods.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public final class Util {

  /**
   * Modulo, with correction for negative dividends.
   * The result is always non-negative.
   *
   * @param  x dividend
   * @param  y divisor
   * @return   x % y, non-negative
   */
  public static int modulo(final int x, final int y) {
    return ((x % y) + y) % y;
  }

  /**
   * Modulo, with correction for negative dividends.
   * The result is always non-negative.
   *
   * @param  x dividend
   * @param  y divisor
   * @return   x % y, non-negative
   */
  public static long modulo(final long x, final long y) {
    return ((x % y) + y) % y;
  }

  /**
   * Modulo, with correction for negative dividends.
   * The result is always non-negative.
   *
   * @param  x dividend
   * @param  y divisor
   * @return   x % y, non-negative
   */
  public static float modulo(final float x, final float y) {
    return ((x % y) + y) % y;
  }

  /**
   * Modulo, with correction for negative dividends.
   * The result is always non-negative.
   *
   * @param  x dividend
   * @param  y divisor
   * @return   x % y, non-negative
   */
  public static double modulo(final double x, final double y) {
    return ((x % y) + y) % y;
  }

  /**
   * Keeps value within limits.
   *
   * @param  value the value
   * @param  min   the lower limit
   * @param  max   the upper limit
   * @return       {@code value} such that {@code min} &lt;=
   *               {@code value} &lt;= {@code max}
   */
  public static int limit(final int value,
			  final int min,
			  final int max) {
    if (value < min) {
      return min;
    }
    if (value > max) {
      return max;
    }
    return value;
  }

  /**
   * Keeps value within limits.
   *
   * @param  value the value
   * @param  min   the lower limit
   * @param  max   the upper limit
   * @return       {@code value} such that {@code min} &lt;=
   *               {@code value} &lt;= {@code max}
   */
  public static long limit(final long value,
			   final long min,
			   final long max) {
    if (value < min) {
      return min;
    }
    if (value > max) {
      return max;
    }
    return value;
  }

  /**
   * Keeps value within limits.
   *
   * @param  value the value
   * @param  min   the lower limit
   * @param  max   the upper limit
   * @return       {@code value} such that {@code min} &lt;=
   *               {@code value} &lt;= {@code max}
   */
  public static float limit(final float value,
			    final float min,
			    final float max) {
    if (value < min) {
      return min;
    }
    if (value > max) {
      return max;
    }
    return value;
  }

  /**
   * Keeps value within limits.
   *
   * @param  value the value
   * @param  min   the lower limit
   * @param  max   the upper limit
   * @return       {@code value} such that {@code min} &lt;=
   *               {@code value} &lt;= {@code max}
   */
  public static double limit(final double value,
			     final double min,
			     final double max) {
    if (value < min) {
      return min;
    }
    if (value > max) {
      return max;
    }
    return value;
  }

  /**
   * Creates an iterable covering ports addresses according to
   * a value/mask pair.
   *
   * @param value value of the port address
   * @param mask  mask of the port address
   * @return      the iterator
   */
  public static Iterable<Integer> portIterator(final int value,
					       final int mask) {
    final List<Integer> list = new ArrayList<>();
    for (int port = 0; port < 0x100; port++) {
      if (((port ^ value) & mask) == 0) {
	list.add(port);
      }
    }
    return list;
  }

  // default constructor disabled
  private Util() {}
}
