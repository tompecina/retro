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
public class CPUScheduler {

  // static logger
  private static final Logger log =
    Logger.getLogger(CPUScheduler.class.getName());

  // event schedule, held in a synchronized TreeSet
  private final SortedSet<CPUScheduledEvent> schedule =
    Collections.synchronizedSortedSet(
      new TreeSet<>(new CPUScheduledEventComparator()));

  // event comparator
  private class CPUScheduledEventComparator
    implements Comparator<CPUScheduledEvent> {
    @Override
    public int compare(final CPUScheduledEvent event1,
		       final CPUScheduledEvent event2) {
      return Long.compare(event1.getTime(), event2.getTime());
    }
  }

  /**
   * Creates a new CPU scheduler.
   */
  public CPUScheduler() {
    log.fine("New CPU scheduler created");
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
  public void addScheduledEvent(final CPUEventOwner owner,
				final int time,
				final int parameter) {
    assert owner != null;
    assert time >= 0;
    schedule.add(new CPUScheduledEvent(
      owner, Parameters.systemClockSource.getSystemClock() + time, parameter));
    log.finest("New event added for: time (relative): " + time +
	       ", parameter: " + parameter);
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
  public void addScheduledEvent(final CPUEventOwner owner,
				final long time,
				final int parameter) {
    assert owner != null;
    assert time > 0;
    schedule.add(new CPUScheduledEvent(owner, time, parameter));
    log.finest("New event added for: time: " + time +
	       ", parameter: " + parameter);
  }

  /**
   * Removes all events scheduled by a particular owner.
   *
   * @param owner the owner whose events will be removed from
   *              the schedule
   */
  public void removeAllScheduledEvents(final CPUEventOwner owner) {
    for (Iterator<CPUScheduledEvent> iter =
	   schedule.iterator(); iter.hasNext();)
      if (iter.next().getOwner() == owner)
	iter.remove();
  }

  /**
   * Runs the schedule.
   * <p>
   * This method should be called by the CPU object as often
   * as possible, no less than before/after every instruction 
   * executed.  During long instructions such as block transfers, 
   * the schedule should be run before/after each step.
   *
   * @param time the current system clock
   */
  void runSchedule(final long time) {
    CPUScheduledEvent event;
    while (!schedule.isEmpty() && (schedule.first().getTime() <= time)) {
      event = schedule.first();
      schedule.remove(event);
      event.getOwner().performScheduledEvent(event.getParameter());
    }
  }
}