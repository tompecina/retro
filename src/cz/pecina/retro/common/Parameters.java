/* Parameters.java
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
import java.util.prefs.Preferences;
import cz.pecina.retro.cpu.SystemClockSource;
import cz.pecina.retro.cpu.Processor;
import cz.pecina.retro.cpu.Device;
import cz.pecina.retro.cpu.AbstractMemory;

/**
 * General parameters.  These are supposed to by filled in by the emulator
 * and read by separately packaged hardware module, specific or general.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public final class Parameters {

  /**
   * Command line arguments.
   */
  public static String[] arguments;

  /**
   * CPU frequency, in Hz.
   */
  public static double CPUFrequency;

  /**
   * Duration of emulator timer period, in usec.
   */
  public static int timerPeriod;

  /**
   * Duration of emulator timer period, in cycles.  Unless suspended or
   * in debug mode, the emulated CPU performs that may processor cycles.
   */
  public static long timerCycles;

  /**
   * The CPU controlling the port system.
   */
  public static Processor cpu;

  /**
   * System clock source.
   */
  public static SystemClockSource systemClockSource;

  /**
   * The device holding the main memory.
   */
  public static Device memoryDevice;

  /**
   * The main memory object.
   */
  public static AbstractMemory memoryObject;

  /**
   * The root node of the current user preferences.
   */
  public static Preferences preferences;

  /**
   * The processor speed-up factor.
   */
  public static int speedUp = 1;

  /**
   * Use of OpenGL.
   */
  public static boolean openGL = true;

  // default constructor disabled
  private Parameters() { }
}
