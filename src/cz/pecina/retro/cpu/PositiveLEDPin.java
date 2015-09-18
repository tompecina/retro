/* PositiveLEDPin.java
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

import cz.pecina.retro.gui.LED;

/**
 * An input pin driving a LED supplied by the constructor, active in H.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class PositiveLEDPin extends IOPin {

  // the LED
  private LED led;

  /**
   * Main constructor.
   *
   * @param led the LED connected to the pin
   */
  public PositiveLEDPin(final LED led) {
    assert led != null;
    this.led = led;
  }

  // for description see IOPin
  @Override
  public void notifyChange() {
    led.setState(queryNode() == 1);
  }
}
