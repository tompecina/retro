/* DisplayHardware.java
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

package cz.pecina.retro.pmi80;

import java.util.logging.Logger;

import cz.pecina.retro.common.Parameters;

import cz.pecina.retro.cpu.IOPin;
import cz.pecina.retro.cpu.IONode;

import cz.pecina.retro.gui.SSD;

/**
 * Display of the Tesla PMI-80 computer consisting of 9 SSDs.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class DisplayHardware {

  // static logger
  private static final Logger log =
    Logger.getLogger(DisplayHardware.class.getName());

  /**
   * Number of SSDs in the display.
   */
  public static final int NUMBER_SSD = 9;

  // fraction of time a SSD segment must be powered in order to be displayed
  private static final int SSD_RATIO = 100;

  // SSD elements
  private final SSD[] icons = new SSD[NUMBER_SSD];

  // select pins
  private final SelectPin[] selectPins = new SelectPin[4];

  // data (state) pins
  private final DataPin[] dataPins = new DataPin[7];

  // currently selected SSD (as "raw" state of input pins)
  private int select;

  // current data (state)
  private int data;

  // array of counters determining the visual appearance of each segment
  private final int[][] counters = new int[NUMBER_SSD][7];

  // system clock when the array of counters was last updated
  private long lastUpdate;

  // systen clock at the beginning of the period
  private long startPeriod;

  /**
   * Creates the display hardware object.
   */
  public DisplayHardware() {
    log.fine("New display hardware creation started");
    for (int i = 0; i < NUMBER_SSD; i++) {
      icons[i] = new SSD("big", "red");
    }
    for (int i = 0; i < 4; i++) {
      selectPins[i] = new SelectPin(i);
    }
    for (int i = 0; i < 7; i++) {
      dataPins[i] = new DataPin(i);
    }
    reset();
    log.fine("New display hardware created");
  }

  // select pins
  private class SelectPin extends IOPin {
    private int number, mask;

    private SelectPin(final int n) {
      super();
      assert (n >= 0) && (n < 4);
      number = n;
      mask = 1 << number;
    }

    @Override
    public void notifyChange() {
      if ((select & mask) != (IONode.normalize(queryNode()) << number)) {
	update();
	select ^= mask;
      }
    }
  }

  // data (state) pins
  private class DataPin extends IOPin {
    private int number, mask;

    private DataPin(final int n) {
      super();
      assert (n >= 0) && (n < 7);
      number = n;
      mask = 1 << number;
    }

    @Override
    public void notifyChange() {
      if ((data & mask) != (IONode.normalize(queryNode()) << number)) {
	update();
	data ^= mask;
      }
    }
  }

  /**
   * Gets the select pin.
   *
   * @param  n the pin number
   * @return the pin object
   */
  public IOPin getSelectPin(final int n) {
    assert (n >= 0) && (n < 4);
    return selectPins[n];
  }

  /**
   * Gets the data (state) pin.
   *
   * @param  n the pin number
   * @return the pin object
   */
  public IOPin getDataPin(final int n) {
    assert (n >= 0) && (n < 7);
    return dataPins[n];
  }

  // converts select state to position number (-1 if no SSD element selected)
  private static int selectToPosition(final int s) {
    assert (s >= 0) && (s < 16);
    int r = 15 - s;
    if (r >= NUMBER_SSD) {
      r = -1;
    }
    log.finest("Select conversion: " + s + " -> " + r);
    return r;
  }

  /**
   * Updates the array of counters.
   */
  public void update() {
    final long clock = Parameters.systemClockSource.getSystemClock();
    final int period = (int)(clock - lastUpdate);
    final int position = selectToPosition(select);
    if (position != -1) {
      for (int segment = 0, mask = 1; segment < 7; segment++, mask <<= 1) {
	if ((data & mask) == 0) {
	  counters[position][segment] += period;
	}
      }
    }
    lastUpdate = clock;
    log.finest("Counters updated");
  }

  /**
   * Resets the array of counters.
   */
  public void reset() {
    for (int position = 0; position < NUMBER_SSD; position++) {
      for (int segment = 0; segment < 7; segment++) {
	counters[position][segment] = 0;
      }
    }
    lastUpdate = startPeriod = Parameters.systemClockSource.getSystemClock();
    log.finer("Counters reset");
  }

  /**
   * Displays the immediate state of the SSDs, disregarding the counters.
   */
  public void displayImmediate() {
    for (int position = 0; position < NUMBER_SSD; position++) {
      final int value =
	(position != selectToPosition(select)) ? 0 : ((~data) & 0x7f);
      if (value != icons[position].getState()) {
	icons[position].setState(value);
      }
    }
    log.finer("Immediate state displayed");
  }

  /**
   * Displays the state of the SSDs as perceived by the user.
   */
  public void display() {
    final int limit =
      ((int)(Parameters.systemClockSource.getSystemClock() - startPeriod))
      / SSD_RATIO;
    update();
    for (int position = 0; position < NUMBER_SSD; position++) {
      int value = 0;
      for (int segment = 0, mask = 1; segment < 7; segment++, mask <<= 1) {
	if (counters[position][segment] > limit) {
	  value |= mask;
	}
      }
      if (value != icons[position].getState()) {
	icons[position].setState(value);
      }
    }
    log.finest("Perceived state displayed");
  }

  /**
   * Gets the state of select pins (shared with the keyboard).
   *
   * @return the currently selected SSD (as "raw" state of input pins)
   */
  public int getSelect() {
    return select;
  }

  /**
   * Gets the SSD element.
   *
   * @param  n the SSD number, zero-based (SSD #0 is the leftmost element)
   * @return the SSD
   */
  public SSD getIcon(final int n) {
    assert (n >= 0) && (n < NUMBER_SSD);
    return icons[n];
  }
}
