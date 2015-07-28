/* GenericIndicator.java
 *
 * Copyright (C) 2014-2015, Tomáš Pecina <tomas@pecina.cz>
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
import java.util.Map;
import java.util.HashMap;
import javax.swing.Icon;

/**
 * Generic indicator implemented as a changeable icons.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public abstract class GenericIndicator
  extends GenericBitmap
  implements Resizeable {

  // static logger
  private static final Logger log =
    Logger.getLogger(GenericIndicator.class.getName());

  /**
   * Number of states for the indicator.
   */
  protected int numberStates;

  // state of the indicator
  private int state;

  // template prefix for the indicator
  private String prefix;

  // static cache of icons
  private static final Map<String, Icon[]> cache = new HashMap<>();
    
  /**
   * The icons.
   */
  protected Icon[] icons;

  /**
   * Creates an instance of a generic indicator, with a state initially
   * set to <code>0</code>.
   *
   * @param prefix       template prefix for the indicator
   * @param numberStates number of states
   */
  public GenericIndicator(final String prefix, final int numberStates) {
    log.fine("New generic indicator creation started: " + prefix);
    this.prefix = prefix;
    this.numberStates = numberStates;
    redrawOnPixelResize();
    GUI.addResizeable(this);
    log.fine("New generic indicator created: " + prefix);
  }

  /**
   * Gets the state of the indicator.
   *
   * @return state of the indicator (<code>0</code> to
   *         <code>numberStates - 1</code>)
   */
  public int getState() {
    return state;
  }
    
  /**
   * Sets the state of the indicator.
   *
   * @param n new state of the indicator (<code>0</code> to
   *          <code>numberStates - 1</code>)
   */
  public void setState(final int n) {
    assert (n >= 0) && (n < numberStates);
    if (n != state) {
      state = n;
      setIcon(icons[n]);
      log.finer("Generic Indicator state changed to: " + n);
    }
  }

  // for description see Resizeable
  @Override
  public void redrawOnPixelResize() {
    final String template = prefix + "-" + GUI.getPixelSize() + "-%d.png";
    if (!cache.containsKey(template)) {
      final Icon[] temp = new Icon[numberStates];
      for (int state = 0; state < numberStates; state++) {
	temp[state] = IconCache.get(String.format(template, state));
      }
      cache.put(template, temp);
    }
    icons = cache.get(template);
    setIcon(icons[state]);
  }
}
