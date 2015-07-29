/* Knob.java
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
import java.util.List;
import java.util.ArrayList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import cz.pecina.retro.common.Util;

/**
 * Rotary knob.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Knob extends GenericIndicator {

  // static logger
  private static final Logger log =
    Logger.getLogger(Knob.class.getName());

  // limits
  private int minState, maxState;

  // icon width, in (actual) pixels
  private int width;

  // angular size of a segment (in degrees)
  private double step;

  // list of change listeners
  private final List<ChangeListener> changeListeners = new ArrayList<>();

  /**
   * Creates an instance of a knob, initially set to the minimum
   * limit or <code>0</code> if no minimum limit is set.
   * <p>
   * Note: If no limits are set, both <code>minState</code> and
   * <code>maxState</code> must be equal to <code>-1</code>.
   *
   * @param type         type of the element
   * @param color        color of the element
   * @param numberStates number of states (including non-settable ones)
   * @param minState     minimum state (<code>-1</code> if none)
   * @param maxState     maximum state (<code>-1</code> if none)
   */
  public Knob(final String type,
	      final String color,
	      final int numberStates,
	      final int minState,
	      final int maxState) {
    super("gui/Knob/" + type + "-" + color, numberStates);
    log.fine("New knob creation started: " + type + ", " + color);
    assert type != null;
    assert color != null;
    assert numberStates > 1;
    assert (minState >= -1) && (minState < numberStates);
    assert (maxState >= -1) && (maxState < numberStates);
    assert (minState != -1) || (maxState == -1);
    this.minState = minState;
    this.maxState = maxState;
    step = 360.0 / numberStates;
    width = icons[0].getIconWidth();
    assert width == icons[0].getIconHeight();
    if (minState != -1) {
      super.setState(minState);
    }
    addMouseListener(new KnobMouseListener());
    addMouseWheelListener(new KnobMouseWheelListener());
    log.fine("New knob created: " + type + ", " + color);
  }

  /**
   * Sets the state of the knob.
   *
   * @param n new state of the knob (<code>minState</code> to
   *        <code>maxState</code> or <code>0</code> to
   *        <code>numberStates - 1</code> if limits not set)
   */
  @Override
  public void setState(final int n) {
    if (n != getState()) {
      if (minState != -1) {
	if (minState > maxState) {
	  assert (n >= minState) || (n <= maxState);
	} else {
	  assert (n >= minState) && (n <= maxState);
	}
      }
      super.setState(n);
      fireStateChanged();
      log.finer("Knob set to: " + n);
    } else {
      log.finest("Knob state not changed");
    }
  }

  // mouse listener
  private class KnobMouseListener extends MouseAdapter {
    @Override
    public void mousePressed(final MouseEvent event) {
      log.finer("KnobMouseListener event detected");
      final double x = event.getX() - (width / 2.0);
      final double y = (width / 2.0) - event.getY();
      final double r = Math.hypot(x, y);
      if ((r < (width / 8.0)) || (r > (width / 2.0))) {
	log.finer("Knob not set, distance from center not within limits");
	return;
      }
      final double theta = Math.toDegrees(Math.atan2(x, y));
      final int n = Util.modulo((int)Math.round(theta / step), numberStates);
      if ((n < 0) || (n >= numberStates)) {
	log.finer("Knob not set");
	return;
      }
      if (minState > maxState) {
	if ((n >= minState) || (n <= maxState)) {
	  setState(n);
	  log.finer("Knob set to: " + n);
	} else {
	  log.finer("Knob not set, outside limits");
	}
      } else if ((n >= minState) && (n <= maxState)) {
	setState(n);
	log.finer("Knob set to: " + n);
      } else {
	log.finer("Knob not set, outside limits");
      }
    }
  }

  // mouse wheel listener    
  private class KnobMouseWheelListener implements MouseWheelListener {
    @Override
    public void mouseWheelMoved(final MouseWheelEvent event) {
      log.finer("KnobMouseWheelListener event detected");
      int n = getState();
      if (event.getWheelRotation() > 0) {
	if (n != minState) {
	  n = Util.modulo(--n, numberStates);
	  setState(n);
	  log.finer("Knob set to: " + n);
	} else {
	  log.finer("Knob not set");
	}
      } else {			
	if (n != maxState) {
	  n = Util.modulo(++n, numberStates);
	  setState(n);
	  log.finer("Knob set to: " + n);
	} else {
	  log.finer("Knob not set");
	}
      }
    }
  }

  /**
   * Adds a <code>ChangeListener</code> to the knob.
   *
   * @param l the listener to be added
   */
  public void addChangeListener(final ChangeListener l) {
    assert l != null;
    if (!changeListeners.contains(l)) {
      changeListeners.add(l);
    }
  }
	
  /**
   * Removes a <code>ChangeListener</code> from the knob.
   *
   * @param l the listener to be removed
   */
  public void removeChangeListener(final ChangeListener l) {
    assert l != null;
    if (changeListeners.contains(l)) {
      changeListeners.remove(l);
    }
  }

  /**
   * Notifies all listeners that have registered interest for
   * notification on this event type. 
   */
  public void fireStateChanged() {
    final ChangeEvent event = new ChangeEvent(this);
    for (ChangeListener listener: changeListeners) {
      listener.stateChanged(event);
    }
  }

  // for description see Resizeable
  @Override
  public void redrawOnPixelResize() {
    super.redrawOnPixelResize();
    width = icons[0].getIconWidth();
    assert width == icons[0].getIconHeight();
  }
}
