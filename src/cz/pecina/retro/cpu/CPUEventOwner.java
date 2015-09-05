/* CPUEventOwner.java
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
 * The owner of the CPU scheduler event.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public interface CPUEventOwner {

  /**
   * Performs the scheduled event.
   *
   * @param parameter the numeric parameter provided by the event owner
   * @param delay     the difference between the actual and scheduled time
   *                  in system clock units
   */
  public void performScheduledEvent(int parameter, long delay);

  /**
   * Get descriptive information on the owner.
   */
  public default String getString() {
    return String.format("%s(%08x)", getClass().getName(), hashCode());
  }
}
