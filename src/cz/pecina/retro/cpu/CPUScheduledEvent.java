/* CPUScheduledEvent.java
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
import java.util.logging.Level;

/**
 * The CPU scheduler event.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
class CPUScheduledEvent {

  // static logger
  private static final Logger log =
    Logger.getLogger(CPUScheduledEvent.class.getName());

  // owner of the event
  private CPUEventOwner owner;

  // system clock time when the event is to be fired
  private long time;

  // owner-supplied numeric parameter for the event
  private int parameter;
    
  /**
   * Creates and instance of a CPU scheduler event.
   *
   * @param owner     the owner of the event, i.e., the object that
   *                  will receive event notifications
   * @param time      time when the event will be fired, in system
   *                  clock units
   * @param parameter numeric parameter the event will provide to the owner
   */
  public CPUScheduledEvent(final CPUEventOwner owner,
			   final long time,
			   final int parameter) {
    this.owner = owner;
    this.time = time;
    this.parameter = parameter;
    if (log.isLoggable(Level.FINEST)) {
      log.finest("New CPUSCheduledEvent created, time: " + time +
		 ", owner: " + owner + ", parameter: " + parameter);
    }
  }
    
  /**
   * Gets the event owner.
   *
   * @return the event owner
   */
  public CPUEventOwner getOwner() {
    return owner;
  }
    
  /**
   * Gets the time when the event is to be fired, in system clock units.
   *
   * @return the time of the event
   */
  public long getTime() {
    return time;
  }
    
  /**
   * Gets the event parameter.
   *
   * @return the parameter supplied by the event owner
   */
  public int getParameter() {
    return parameter;
  }
}
