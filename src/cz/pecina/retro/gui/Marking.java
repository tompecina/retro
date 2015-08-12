/* Marking.java
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
 * The brand marking.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Marking extends GenericBitmap implements Resizeable {

  // static logger
  private static final Logger log =
    Logger.getLogger(Marking.class.getName());

  // the template
  private String template;
  
  // number of states
  private int numberStates;
  
  // state of the marking
  private int state;

  // icons
  private final Icon icons[];
    
  /**
   * Creates an instance of the marking, initialized to the first state.
   *
   * @param template     the template
   * @param numberStates number of states
   * @param state        initial state
   */
  public Marking(final String template,
		 final int numberStates,
		 final int state) {
    assert (template != null) && !template.isEmpty();
    assert numberStates > 0;
    this.template = template;
    this.numberStates = numberStates;
    icons = new Icon[numberStates];
    setState(state);
    GUI.addResizeable(this);
    redrawOnPixelResize();
    log.fine("New marking created");
  }

  /**
   * Sets the state of the marking.
   *
   * @param n new state of the marking
   */
  public void setState(final int n) {
    log.finer("Setting marking state to: " + n);
    assert (n >= 0) && (n < numberStates);
    if (n != state) {
      state = n;
      setIcon(icons[n]);
      log.finer("Marking state changed to: " + n);
    }
  }

  /**
   * Gets the state of the marking.
   *
   * @return state of the marking
   */
  public int getState() {
    return state;
  }
    
  // for description see Resizeable
  @Override
  public void redrawOnPixelResize() {
    for (int state = 0; state < numberStates; state++) {
      icons[state] = IconCache.get(String.format(template, GUI.getPixelSize(), state));
    }
    setIcon(icons[state]);
  }
}
