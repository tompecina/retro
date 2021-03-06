/* CPUScheduler.java
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

import java.util.TreeSet;
import java.util.SortedSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import cz.pecina.retro.common.Parameters;

/**
 * CPU clock-driven event scheduler.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public final class CPUScheduler {

  // static logger
  private static final Logger log =
    Logger.getLogger(CPUScheduler.class.getName());

  // event schedule, held in a synchronized TreeSet
  private static final SortedSet<CPUScheduledEvent> schedule =
    Collections.synchronizedSortedSet(
      new TreeSet<>(new CPUScheduledEventComparator()));

  // event comparator
  private static class CPUScheduledEventComparator
    implements Comparator<CPUScheduledEvent> {

    // for description see Comparator
    @Override
    public int compare(final CPUScheduledEvent event1,
		       final CPUScheduledEvent event2) {
      final int tc = Long.compare(event1.getTime(), event2.getTime());
      if (tc != 0) {
	return tc;
      } else {
	return Integer.compare(event1.hashCode(), event2.hashCode());
      }
    }
  }

  /**
   * Schedules a new event.
   *
   * @param owner     the owner of the event, i.e., the object that
   *                  will receive event notifications
   * @param time      relative time when the event will be fired, in system
   *                  clock units, added to the current time
   * @param parameter numeric parameter the event will provide to the owner
   */
  public static void addEventRelative(final CPUEventOwner owner,
				      final int time,
				      final int parameter) {
    assert owner != null;
    assert time >= 0;
    schedule.add(new CPUScheduledEvent(
      owner, Parameters.systemClockSource.getSystemClock() + time, parameter));
    if (log.isLoggable(Level.FINER)) {
      log.finer("New event added for (relative time): " + time +
		", owner: " + owner.getString() + ", parameter: " + parameter);
    }
  }

  /**
   * Schedules a new event, with the callback parameter set to the default
   * value (zero).
   *
   * @param owner     the owner of the event, i.e., the object that
   *                  will receive event notifications
   * @param time      relative time when the event will be fired, in system
   *                  clock units, added to the current time
   */
  public static void addEventRelative(final CPUEventOwner owner,
				      final int time) {
    addEventRelative(owner, time, 0);
  }

  /**
   * Schedules a new event.
   *
   * @param owner     the owner of the event, i.e., the object that
   *                  will receive event notifications
   * @param time      time when the event will be fired, in system 
   *                  clock units
   * @param parameter numeric parameter the event will provide to the owner
   */
  public static void addEvent(final CPUEventOwner owner,
			      final long time,
			      final int parameter) {
    assert owner != null;
    assert time > 0;
    schedule.add(new CPUScheduledEvent(owner, time, parameter));
    if (log.isLoggable(Level.FINER)) {
      log.finer("New event added for: " + time +
		", owner: " + owner.getString() + ", parameter: " + parameter);
    }
  }

  /**
   * Schedules a new event, with the callback parameter set to the default
   * value (zero).
   *
   * @param owner     the owner of the event, i.e., the object that
   *                  will receive event notifications
   * @param time      time when the event will be fired, in system 
   *                  clock units
   */
  public static void addEvent(final CPUEventOwner owner, final long time) {
    addEvent(owner, time, 0);
  }

  /**
   * Removes all events scheduled by a particular owner.
   *
   * @param owner the owner whose events will be removed from
   *              the schedule
   */
  public static void removeAllEvents(final CPUEventOwner owner) {
    if (log.isLoggable(Level.FINER)) {
      log.finer("Removing all scheduled events for owner: " +
		owner.getString());
    }
    for (Iterator<CPUScheduledEvent> iter =
	   schedule.iterator(); iter.hasNext();) {
      if (iter.next().getOwner() == owner) {
	iter.remove();
      }
    }
  }

  /**
   * Gets time remaining to the next event scheduled by a particular owner.
   *
   * @param  owner the owner whose events will be evaluated
   * @return       the remaining time in clock cycles or {@code -1}
   *               if no event scheduled
   */
  public static long getRemainingTime(final CPUEventOwner owner) {
    final long time = Parameters.systemClockSource.getSystemClock();
    long r = -1;
    for (CPUScheduledEvent event: schedule) {
      if (event.getOwner() == owner) {
	r = event.getTime() - time;
	break;
      }
    }
    if (log.isLoggable(Level.FINER)) {
      log.finer("Remaining time: " + r + " at: " + time +
		", owner: " + owner.getString());
    }
    return r;
  }

  /**
   * Runs the schedule.
   * <p>
   * This method should be called by the CPU object as often
   * as possible, no less than before every instruction 
   * executed.  During long instructions such as block transfers, 
   * the schedule should be run before each step.  It is critical
   * that the schedule is run BEFORE the instruction as peripheral
   * devices such as 8253/4 rely on this for precise timing.
   *
   * @param time the current system clock
   */
  public static void runSchedule(final long time) {
    if (log.isLoggable(Level.FINEST)) {
      log.finest("Running schedule at: " + time);
    }
    while (!schedule.isEmpty()) {
      final CPUScheduledEvent event = schedule.first();
      final long scheduledTime = event.getTime();
      if (scheduledTime > time) {
	break;
      }
      schedule.remove(event);
      event.getOwner().performEvent(event.getParameter(), time - scheduledTime);
    }
  }

  // default constructor disabled
  private CPUScheduler() {}
}
