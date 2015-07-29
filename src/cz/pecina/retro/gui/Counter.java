/* Counter.java
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
import cz.pecina.retro.common.Util;

/**
 * Counter consisting of several <code>Digit</code>s.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Counter {

  // static logger
  private static final Logger log =
    Logger.getLogger(Counter.class.getName());

  // number of digits
  private int numberDigits;

  // state of the counter
  private double state;

  // digits comprising the counter
  private Digit[] digits;

  // maximum state of the counter
  private double maxState;

  /**
   * Creates an instance of a counter, initially set to zero.
   *
   * @param numberDigits number of digits the counter consists of
   * @param type         type of the digits
   * @param color        color of the digits
   */
  public Counter(final int numberDigits,
		 final String type,
		 final String color) {
    log.fine("New Counter creattion started: " + numberDigits + " digits, " +
	     " type: " + type + ", color: " + color);
    assert (numberDigits > 0) && (numberDigits <= 8);
    assert type != null;
    assert color != null;
    this.numberDigits = numberDigits;
    maxState = Math.pow(10.0, numberDigits);
    digits = new Digit[numberDigits];
    for (int i = 0; i < numberDigits; i++) {
      digits[i] = new Digit(type, color);
    }
    log.fine("New Counter created");
  }

  /**
   * Gets the state of the counter.
   *
   * @return the state of the counter, in fractions
   */
  public double getState() {
    log.finer("State of counter retrieved: " + state);
    return state;
  }

  /**
   * Sets the state of the counter.
   *
   * @param state the new state of the counter
   */
  public void setState(final double state) {
    this.state = Util.modulo(state, maxState);
    double b = this.state;
    double t = b % 10.0;
    double c = (t > 9.0) ? (t - 9.0) : 0.0;
    digits[0].setState(t);
    for (int i = 1; i < numberDigits; i++) {
      b = Math.floor(b / 10.0);
      t = b % 10.0;
      digits[i].setState(t + c);
      if (t < 9.0) {
	c = 0.0;
      }
    }
    log.finer("State of counter set: " + state);
  }

  /**
   * Gets one digit of the counter.
   *
   * @param n the digit number (<code>0</code> is the rightmost digit)
   * @return the <code>Digit</code> object
   */
  public Digit getDigit(final int n) {
    assert (n >= 0) && (n < numberDigits);
    return digits[n];
  }
}
