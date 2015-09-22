/* Constants.java
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

package cz.pecina.retro.ondra;

/**
 * Constants specific to Tesla Ondra SPO 186.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public final class Constants {
    
  /**
   * Number of ROM versions.
   */
  public static final int NUMBER_VERSIONS = 3;

  /**
   * Array of ROM version strings.
   */
  public static final String[] VERSIONS =
    {"Tesla Monitor V5.0", "Tesla BASIC EXP V5.0/G", "ViLi Monitor V27"};

  /**
   * The default ROM version.
   */
  public static final int DEFAULT_VERSION = 0;  // Tesla Monitor V5.0

  /**
   * The sound sampling rate.
   */
  public static int SOUND_SAMPLING_RATE = 16000;

  public static final double CPU_FREQUENCY = 2e6;
  public static final int TIMER_PERIOD = 20;  // in msec
  public static final long TIMER_CYCLES =
    Math.round(TIMER_PERIOD * CPU_FREQUENCY / 1e3);

  public static final String RES_PREFIX = "cz/pecina/retro/ondra/";

  public static final int TAPE_SAMPLE_RATE = 2000000;

  // default constructor disabled
  private Constants() {}
}
