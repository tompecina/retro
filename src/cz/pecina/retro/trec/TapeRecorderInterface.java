/* TapeRecorderInterface.java
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

import java.util.List;

/**
 * Tape recorder interface.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class TapeRecorderInterface {
  /**
   * Tape sampling rate, in samples per second.
   */
  public static int tapeSampleRate;

  /**
   * Supported tape formats.
   */
  public static List<String> tapeFormats;

  /**
   * Duration of one emulation cycle (used for VU meter).
   */
  public int timerPeriod;

  /**
   * Get maximum tape length, in samples/CPU cycles.
   *
   * @return maximum tape length, in samples/CPU cycles
   */
  public long getMaxTapeLength() {
    return (long)(Tape.MAX_TAPE_LENGTH_IN_SEC) * (long)tapeSampleRate;
  }
}
