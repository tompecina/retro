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

package cz.pecina.retro.pmd85;

/**
 * Constants specific to Tesla PMD 85.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public final class Constants {
    
  /**
   * Number of PMD 85 models.
   */
  public static final int NUMBER_MODELS = 4;

  /**
   * Array of PMD 85 model strings.
   */
  public static final String[] MODELS =
    {"PMD 85-1", "PMD 85-2", "PMD 85-2A", "PMD 85-3"};

  /**
   * The default model.
   */
  public static final int DEFAULT_MODEL = 3;  // PMD 85-3

  /**
   * Number of color modes.
   */
  public static int NUMBER_COLOR_MODES = 3;  
  
  /**
   * Default color mode.
   */
  public static int DEFAULT_COLOR_MODE = 0;  
  
  public static final double CPU_FREQUENCY = 2.048e6;
  public static final int TIMER_PERIOD = 20;  // in msec
  public static final long TIMER_CYCLES =
    Math.round(TIMER_PERIOD * CPU_FREQUENCY / 1e3);

  public static final String RES_PREFIX = "cz/pecina/retro/pmd85/";

  public static final int TAPE_SAMPLE_RATE = 1111111;

  // default constructor disabled
  private Constants() {};
}
