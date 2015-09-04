/* Intel8253.java
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

package cz.pecina.retro.cpu;

import java.util.logging.Logger;

/**
 * Intel 8253 Programmable Interval Timer.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Intel8253 extends Intel8254 {

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
  public Intel8253(final String name, final boolean[] types) {
    super(name);
    log = Logger.getLogger(getClass().getName() + "." + name);
    log.fine("New Intel 8253 creation completed, name: " + name);
  }

  // for description see IOElement
  @Override
  public int portInput(final int port) {
    if ((port & 0x03) == 3) {
      log.finer("Attempting to read status register, but 8253 has none");
      return 0xff;
    } else {
      return super.portInput(port);
    }
  }
}
