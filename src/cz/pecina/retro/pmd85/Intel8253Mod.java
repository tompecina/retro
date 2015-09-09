/* Intel8253Mod.java
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

import java.util.logging.Logger;

import cz.pecina.retro.cpu.Intel8253;
import cz.pecina.retro.cpu.Intel8254;

/**
 * Modified Intel 8253 Programmable Interval Timer.  The only
 * modification concerns Counter 1: if 0x20 is written to the counter,
 * (i.e., "silence" for the tape recorder interface), the Mode is
 * changed to 1 so the counter is never started.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Intel8253Mod extends Intel8253 {

  // dynamic logger, per device
  private Logger log;

  /**
   * Main constructor.
   *
   * @param name  device name
   * @param types  array of counter connection types: if {@code true},
   *               the counter's clock is connected to system clock and
   *               the regular clock pin is disabled, if {@code false},
   *               the clock pin is enabled
   */
  public Intel8253Mod(final String name, final boolean[] types) {
    super(name, types);
    log = Logger.getLogger(getClass().getName() + "." + name);
    log.finer("New modified Intel 8253 creation completed, name: " + name);
  }

  // for description see Intel8254
  @Override
  public void portOutput(final int port, final int data) {
    super.portOutput(port, data);
    if (((port & 0x03) == 1) && (data == 0x20)) {
      counters[1].mode = 1;
      log.finer("Mode of Counter 1 set to 1");
    }
  }    
}
