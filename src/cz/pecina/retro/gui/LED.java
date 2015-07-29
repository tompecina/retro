/* LED.java
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
import javax.swing.Icon;

/**
 * On/off LED.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class LED extends GenericBitmap implements Resizeable {

  // static logger
  private static final Logger log =
    Logger.getLogger(LED.class.getName());

  // state of the LED
  private int state;

  // type of the LED
  private String type;

  // color of the LED
  private String color;

  // icons
  private final Icon icons[] = new Icon[2];
    
  /**
   * Creates an instance of a LED, initially set to off.
   *
   * @param type  type of the LED
   * @param color color of the LED
   */
  public LED(final String type, final String color) {
    this.type = type;
    this.color = color;
    GUI.addResizeable(this);
    redrawOnPixelResize();
    log.fine("New LED created");
  }

  /**
   * Gets the state of the LED.
   *
   * @return state of the LED
   */
  public int getState() {
    return state;
  }
    
  /**
   * Sets the state of the LED.
   *
   * @param b new state of the LED
   */
  public void setState(final boolean b) {
    log.finer("Setting LED state to: " + b);
    setState(b ? 1 : 0);
  }

  /**
   * Sets the state of the LED.
   *
   * @param n new state of the LED
   */
  public void setState(final int n) {
    log.finer("Setting LED state to: " + n);
    assert (n == 0) || (n == 1);
    if (n != state) {
      state = n;
      setIcon(icons[n]);
      log.finer("LED state changed to: " + n);
    }
  }

  // for description see Resizeable
  @Override
  public void redrawOnPixelResize() {
    final String template = "gui/LED/" + type + "-" + color +
      "-" + GUI.getPixelSize() + "-%d.png";
    for (int state = 0; state < 2; state++) {
      icons[state] = IconCache.get(String.format(template, state));
    }
    setIcon(icons[state]);
  }
}
