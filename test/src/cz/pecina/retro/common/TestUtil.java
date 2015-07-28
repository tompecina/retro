/* TestUtil.java
 *
 * Copyright (C) 2014, Tomáš Pecina <tomas@pecina.cz>
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

import junit.framework.TestCase;

public class TestUtil extends TestCase {

    public void testModuloIntInt() {
	final int[][] values = new int[][] {
	    new int[] {5, 2, 1},
	    new int[] {-5, 2, 1},
	    new int[] {2, 2, 0},
	    new int[] {-2, 2, 0}
	};
	for (int[] value: values)
	    assertEquals(value[2], Util.modulo(value[0], value[1]), 0);
	try {
	    Util.modulo(5, 0);
	    fail();
	} catch (ArithmeticException exception) {
	    assertTrue(true);
	}
	try {
	    Util.modulo(0, 0);
	    fail();
	} catch (ArithmeticException exception) {
	    assertTrue(true);
	}
    }

    public void testModuloLongLong() {
	final long[][] values = new long[][] {
	    new long[] {5L, 2L, 1L},
	    new long[] {-5L, 2L, 1L},
	    new long[] {2L, 2L, 0L},
	    new long[] {-2L, 2L, 0L}
	};
	for (long[] value: values)
	    assertEquals(value[2], Util.modulo(value[0], value[1]), 0L);
	try {
	    Util.modulo(5L, 0L);
	    fail();
	} catch (ArithmeticException exception) {
	    assertTrue(true);
	}
	try {
	    Util.modulo(0L, 0L);
	    fail();
	} catch (ArithmeticException exception) {
	    assertTrue(true);
	}
    }

    public void testModuloFloatFloat() {
	final float[][] values = new float[][] {
	    new float[] {5.0f, 2.0f, 1.0f},
	    new float[] {-5.0f, 2.0f, 1.0f},
	    new float[] {2.0f, 2.0f, 0.0f},
	    new float[] {-2.0f, 2.0f, 0.0f}
	};
	for (float[] value: values)
	    assertEquals(value[2], Util.modulo(value[0], value[1]), 0.0f);
	assertTrue(Float.isNaN(Util.modulo(5.0f, 0.0f)));
	assertTrue(Float.isNaN(Util.modulo(0.0f, 0.0f)));
    }

    public void testModuloDoubleDouble() {
	final double[][] values = new double[][] {
	    new double[] {5.0, 2.0, 1.0},
	    new double[] {-5.0, 2.0, 1.0},
	    new double[] {2.0, 2.0, 0.0},
	    new double[] {-2.0, 2.0, 0.0}
	};
	for (double[] value: values)
	    assertEquals(value[2], Util.modulo(value[0], value[1]), 0.0);
	assertTrue(Double.isNaN(Util.modulo(5.0, 0.0)));
	assertTrue(Double.isNaN(Util.modulo(0.0, 0.0)));
    }

    public void testLimitIntInt() {
	final int[][] values = new int[][] {
	    new int[] {4, 2, 6, 4},
	    new int[] {1, 2, 6, 2},
	    new int[] {8, 2, 6, 6},
	    new int[] {2, 2, 6, 2},
	    new int[] {6, 2, 6, 6}
	};
	for (int[] value: values)
	    assertEquals(value[3], Util.limit(value[0], value[1], value[2]), 0);
    }

    public void testLimitLongLong() {
	final long[][] values = new long[][] {
	    new long[] {4L, 2L, 6L, 4L},
	    new long[] {1L, 2L, 6L, 2L},
	    new long[] {8L, 2L, 6L, 6L},
	    new long[] {2L, 2L, 6L, 2L},
	    new long[] {6L, 2L, 6L, 6L}
	};
	for (long[] value: values)
	    assertEquals(value[3], Util.limit(value[0], value[1], value[2]), 0L);
    }

    public void testLimitFloatFloat() {
	final float[][] values = new float[][] {
	    new float[] {4.3f, 2.3f, 6.3f, 4.3f},
	    new float[] {1.3f, 2.3f, 6.3f, 2.3f},
	    new float[] {8.3f, 2.3f, 6.3f, 6.3f},
	    new float[] {2.3f, 2.3f, 6.3f, 2.3f},
	    new float[] {6.3f, 2.3f, 6.3f, 6.3f}
	};
	for (float[] value: values)
	    assertEquals(value[3], Util.limit(value[0], value[1], value[2]), 0.0f);
    }

    public void testLimitDoubleDouble() {
	final double[][] values = new double[][] {
	    new double[] {4.3, 2.3, 6.3, 4.3},
	    new double[] {1.3, 2.3, 6.3, 2.3},
	    new double[] {8.3, 2.3, 6.3, 6.3},
	    new double[] {2.3, 2.3, 6.3, 2.3},
	    new double[] {6.3, 2.3, 6.3, 6.3}
	};
	for (double[] value: values)
	    assertEquals(value[3], Util.limit(value[0], value[1], value[2]), 0.0);
    }
}
