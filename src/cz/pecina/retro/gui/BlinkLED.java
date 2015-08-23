/* BlinkLED.java
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
import java.awt.event.ActionEvent;

/**
 * Blinking LED.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class BlinkLED extends LED {

  // static logger
  private static final Logger log =
    Logger.getLogger(BlinkLED.class.getName());

  // Blink control object
  private Blink blink;

  /**
   * Creates an instance of a blinking LED, initially set to off.
   *
   * @param type  type of the LED
   * @param color color of the LED
   */
  public BlinkLED(final String type, final String color) {
    super(type, color);
    blink = new Blink(new BlinkListener(true), new BlinkListener(false));
    log.fine("New BlinkLED created");
  }

  /**
   * Sets the state of the BlinkLED.
   * 
   * @param pattern the blinking pattern
   */
  public void setState(final BlinkPattern pattern) {
    log.finer("Setting BlinkLED state to: " + pattern);
    blink.setState(pattern);
  }

  /**
   * Sets the state of the BlinkLED (blinking).
   *
   * @param timeOn  the on parameter of the pattern (in milliseconds)
   * @param timeOff the off parameter of the pattern (in milliseconds)
   */
  public void setState(final int timeOn, final int timeOff) {
    log.finer("Setting BlinkLED state to: " + timeOn + ", " + timeOff);
    blink.setState(new BlinkPattern(timeOn, timeOff));
  }

  /**
   * Sets the state of the LED (pulse).
   *
   * @param n new state of the LED (pulse duration in milliseconds)
   */
  @Override
  public void setState(final int n) {
    log.finer("Setting BlinkLED state to: " + n);
    blink.setState(new BlinkPattern(n, 0));
  }

  /**
   * Sets the state of the BlinkLED (on/off).
   *
   * @param b new state of the LED
   */
  @Override
  public void setState(final boolean b) {
    log.finer("Setting BlinkLED state to: " + b);
    blink.setState(new BlinkPattern(b));
  }

  // on/off listener
  private class BlinkListener implements ActionListener {
    private boolean onOff;

    public BlinkListener(final boolean b) {
      onOff = b;
      log.finer("Blink listener created for: " + b);
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finest("Blink action fired for: " + onOff);
      BlinkLED.super.setState(onOff ? 1 : 0);
    }
  }
}
