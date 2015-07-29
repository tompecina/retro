/* Blink.java
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

package cz.pecina.retro.gui;

import java.util.logging.Logger;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 * Blink control object.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 * @see BlinkPattern
 */
public class Blink {

  // static logger
  private static final Logger log =
    Logger.getLogger(Blink.class.getName());

  // timers
  private Timer timerOn, timerOff;

  // instances of listeners
  private ActionListener onListener, offListener;

  /**
   * Creates a blink control object.
   *
   * @param onListener  on listener object
   * @param offListener off listener object
   */
  public Blink(final ActionListener onListener,
	       final ActionListener offListener) {
    this.onListener = onListener;
    this.offListener = offListener;
    timerOn = new Timer(0, onListener);
    timerOff = new Timer(0, offListener);
    log.fine("New Blink created");
  }

  /**
   * Sets the state of the blink control object.
   *
   * @param pattern new state of the object
   */
  public void setState(final BlinkPattern pattern) {
    log.finer("Setting of Blink started");
    timerOn.stop();
    timerOff.stop();
    log.finest("Timers stopped");
    int timeOn = pattern.getTimeOn();
    int timeOff = pattern.getTimeOff();
    log.finest("Times: on: " + timeOn + ", off: " + timeOff);
    if (timeOn == 0) {
      if (timeOff == 0) {  // off
	offListener.actionPerformed(null);
      } else {  // on
	onListener.actionPerformed(null);
      }
    } else {
      timerOff.setInitialDelay(timeOn);
      if (timeOff == 0) {  // pulse
	onListener.actionPerformed(null);
	timerOff.setRepeats(false);
      } else {  // blink
	timerOff.setDelay(timeOn + timeOff);
	timerOff.setRepeats(true);
	timerOn.setDelay(timeOn + timeOff);
	timerOn.setInitialDelay(0);
	timerOn.setRepeats(true);
	timerOn.start();
      }
      timerOff.start();
    }
    log.finer("New Blink state set");
  }
}
