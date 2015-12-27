/* FloppyIcon.java
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

package cz.pecina.retro.floppy;

import java.util.logging.Logger;

import javax.swing.Icon;

import cz.pecina.retro.common.Localized;
import cz.pecina.retro.common.Application;

import cz.pecina.retro.gui.GenericBitmap;
import cz.pecina.retro.gui.GUI;
import cz.pecina.retro.gui.Resizeable;
import cz.pecina.retro.gui.IconCache;

/**
 * Floopy disk drive icon, combining a button with a LED.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class FloppyIcon extends GenericBitmap implements Resizeable, Localized {

  // static logger
  private static final Logger log =
    Logger.getLogger(FloppyIcon.class.getName());

  // color of the icon
  private String color;

  // drive letter
  private char driveLetter;
  
  // state of the icon
  private boolean pressed;

  // state of the LED
  private int state;

  // icon template string
  private String template;

  // array of icons (first index: icon, second index: LED)
  private Icon icons[][] = new Icon[2][2];

  // tool-tip
  private String toolTip;

  // tool-tip resource
  private String toolTipResource;

  /**
   * Creates an instance of a floppy disk drive icon.
   *
   * @param color           color of the LED
   * @param driveLetter     the drive letter
   * @param toolTipResource tool-tip for the icon ({@code null} if none)
   */
  public FloppyIcon(final String color,
		    final char driveLetter,
		    final String toolTipResource) {
    super();
    log.fine("New FloppyIcon creation started for " + driveLetter + ":");
    assert color != null;
    this.color = color;
    this.driveLetter = driveLetter;
    this.toolTipResource = toolTipResource;
    GUI.addResizeable(this);
    Application.addLocalized(this);
    redrawOnPixelResize();
    redrawOnLocaleChange();
    log.fine("New FloppyIcon created for " + driveLetter + ":");
  }

  /**
   * Sets the state of the icon ({@code true} = pressed/down,
   * {@code false} = not pressed/up).
   *
   * @param b state of the icon, {@code true} if pressed (down),
   *          {@code false} otherwise
   */
  public void setPressed(final boolean b) {
    if (b != pressed) {
      pressed = b;
      setIcon(icons[b ? 1 : 0][state]);
    }
  }

  /**
   * The state of the icon.  {@code true} if the icon is pressed
   * (down), {@code false} otherwise.
   *
   * @return state of the icon, {@code true} if pressed (down),
   *         {@code false} otherwise
   */
  public boolean isPressed() {
    return pressed;
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
    log.finer("Setting icon's LED state to: " + b);
    setState(b ? 1 : 0);
  }

  /**
   * Sets the state of the LED.
   *
   * @param n new state of the LED
   */
  public void setState(final int n) {
    log.finer("Setting icon's LED state to: " + n);
    assert (n == 0) || (n == 1);
    if (n != state) {
      state = n;
      setIcon(icons[pressed ? 1 : 0][state]);
      log.finer("Icon's LED state changed to: " + n);
    }
  }

  // for description see Resizeable
  @Override
  public void redrawOnPixelResize() {
    log.finest("FloppyIcon redraw started for: " + color);
    final String template = "floppy/FloppyIcon/" + color +
      "-" + GUI.getPixelSize() + "-%s-%d.png";
    for (int pressed = 0; pressed < 2; pressed++) {
      for (int state = 0; state < 2; state++) {
	icons[pressed][state] =
	  IconCache.get(String.format(template,
				      ((pressed == 0) ? "u" : "d"),
				      state));
      }
    }
    setIcon(icons[pressed ? 1 : 0][state]);
  }

  // for description see Localized
  @Override
  public void redrawOnLocaleChange() {
    log.finest("FloppyIcon redraw started for: " + driveLetter + ":");
    toolTip =
      (toolTipResource == null) ?
      null :
      String.format(Application.getString(FloppyIcon.class, toolTipResource),
		    driveLetter);
  }
}
