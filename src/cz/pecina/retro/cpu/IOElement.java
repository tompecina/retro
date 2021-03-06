/* IOElement.java
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

/**
 * A device capable of communicating with the CPU using I/O ports.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public interface IOElement {

  /**
   * Port direction (output).
   */
  public static final int OUTPUT = 0;

  /**
   * Port direction (output).
   */
  public static final int INPUT = 1;

  /**
   * Responds to port input request.
   *
   * @param  port port number
   * @return byte returned by the device
   */
  public default int portInput(int port) {
    return 0xff;
  }

  /**
   * Responds to port output request.
   *
   * @param port port number
   * @param data byte sent to the device
   */
  public default void portOutput(int port, int data) {
  }
}
