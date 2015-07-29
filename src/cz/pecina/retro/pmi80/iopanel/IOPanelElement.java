/* IOPanelElement.java
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

package cz.pecina.retro.pmi80.iopanel;

import java.util.logging.Logger;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import cz.pecina.retro.gui.GenericBitmap;
import cz.pecina.retro.gui.Resizeable;
import cz.pecina.retro.gui.GUI;
import cz.pecina.retro.gui.IconCache;
import cz.pecina.retro.cpu.IOPin;
import cz.pecina.retro.cpu.IONode;

/**
 * Input/output panel element.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class IOPanelElement extends GenericBitmap implements Resizeable {

  // static logger
  private static final Logger log =
    Logger.getLogger(IOPanelElement.class.getName());

  // zones
  private enum Zone {NULL, INDICATOR, SWITCH, BUTTON};

  // number of modes
  private static final int NUMBER_MODES = 12;

  // zone boundaries
  private static final int ZONE_NULL_START = 0;
  private static final int ZONE_NULL_END = 0;
  private static final int ZONE_INDICATOR_START = 1;
  private static final int ZONE_INDICATOR_END = 3;
  private static final int ZONE_SWITCH_START = 4;
  private static final int ZONE_SWITCH_END = 4;
  private static final int ZONE_BUTTON_START = 5;
  private static final int ZONE_BUTTON_END = 11;

  // icon template
  private static final String TEMPLATE =
    "pmi80/iopanel/IOPanelElement/element-%d-%d-%s.png";

  // element mode
  private int mode;

  // element zones
  private Zone zone;

  // button states
  private boolean pressed, locked;

  // element icons
  private Icon offIcon, onIcon, lockedIcon;

  // pin connected to the element
  private final Pin pin = new Pin();

  /**
   * Creates an I/O panel element.
   */
  public IOPanelElement() {
    addMouseListener(new ElementMouseListener());
    setMode(0);
    GUI.addResizeable(this);
    log.fine("New I/O panel element created");
  }

  // converts mode to zone
  private Zone modeToZone(final int mode) {
    assert (mode >= 0) && (mode < NUMBER_MODES);
    if ((mode >= ZONE_NULL_START) && (mode <= ZONE_NULL_END)) {
      return Zone.NULL;
    }
    if ((mode >= ZONE_INDICATOR_START) && (mode <= ZONE_INDICATOR_END)) {
      return Zone.INDICATOR;
    }
    if ((mode >= ZONE_SWITCH_START) && (mode <= ZONE_SWITCH_END)) {
      return Zone.SWITCH;
    }
    if ((mode >= ZONE_BUTTON_START) && (mode <= ZONE_BUTTON_END)) {
      return Zone.BUTTON;
    }
    return Zone.NULL;
  }

  // sets icons for mode
  private void setIcons(final int mode) {
    assert (mode >= 0) && (mode < NUMBER_MODES);
    offIcon =
      IconCache.get(String.format(TEMPLATE, GUI.getPixelSize(), mode, "u"));
    onIcon =
      IconCache.get(String.format(TEMPLATE, GUI.getPixelSize(), mode, "d"));
    lockedIcon =
      IconCache.get(String.format(TEMPLATE, GUI.getPixelSize(), mode, "l"));
    this.mode = mode;
    zone = modeToZone(mode);
    switch (zone) {
      case NULL:
	pressed = locked = false;
	setIcon(offIcon);
	break;
      case INDICATOR:
	pressed = locked = false;
	setIcon((pin.queryNode() == 1) ? onIcon : offIcon);
	break;
      case SWITCH:
	locked = false;
      case BUTTON:
	if (locked) {
	  setIcon(lockedIcon);
	} else if (pressed) {
	  setIcon(onIcon);
	} else {
	  setIcon(offIcon);
	}
	break;
    }
  }

  /**
   * Sets the mode for the element.
   *
   * @param mode mode for the element
   */
  public void setMode(final int mode) {
    assert (mode >= 0) && (mode < NUMBER_MODES);
    setIcons(mode);
    elementChangeAction();
  }
    
  /**
   * Sets the indicator state.
   *
   * @param b new state of the indicator
   */
  public void setIndicator(final boolean b) {
    if (zone == Zone.INDICATOR)
      setIcon(b ? onIcon : offIcon);
  }

  // mouse listener 
  private class ElementMouseListener extends MouseAdapter {

    @Override
    public void mousePressed(final MouseEvent event) {
      final int modifiers = event.getModifiersEx();
      if ((modifiers & MouseEvent.SHIFT_DOWN_MASK) != 0) {
	mode = (mode + 1) % NUMBER_MODES;
	setMode(mode);
      } else {
	switch (zone) {
	  case SWITCH:
	    pressed = !pressed;
	    setIcon(pressed ? onIcon : offIcon);
	    elementChangeAction();
	    break;
	  case BUTTON:
	    if ((modifiers & MouseEvent.CTRL_DOWN_MASK) != 0) {
	      locked = pressed = !locked;
	      setIcon(locked ? lockedIcon : offIcon);
	    } else if (locked) {
	      locked = pressed = false;
	      setIcon(offIcon);
	    } else {
	      pressed = true;
	      setIcon(onIcon);
	    }
	    elementChangeAction();
	    break;
	  default:
	    break;
	}
      }
    }

    @Override
    public void mouseReleased(final MouseEvent event) {
      if (((event.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == 0) &&
	  (zone == Zone.BUTTON) && !locked) {
	pressed = false;
	setIcon(offIcon);
	elementChangeAction();
      }
    }
	
    @Override
    public void mouseExited(final MouseEvent event) {
      mouseReleased(event);
    }
  }

  /**
   * Notifies node on element change.
   */
  public void elementChangeAction() {
    pin.notifyChangeNode();
  }

  // pin class
  private class Pin extends IOPin {

    @Override
    public int query() {
      if ((zone == Zone.SWITCH) || (zone == Zone.BUTTON)) {
	return pressed ? 0 : 1;
      } else {
	return IONode.HIGH_IMPEDANCE;
      }
    }

    @Override
    public void notifyChange() {
      if (zone == Zone.INDICATOR) {
	setIndicator(pin.queryNode() == 1);
      }
    }
  }

  /**
   * Gets the <code>IOPin</code> object connected to the element.
   *
   * @return the <code>IOPin</code> object connected to the element
   */
  public IOPin getPin() {
    return pin;
  }

  // for description see Resizeable
  @Override
  public void redrawOnPixelResize() {
    setIcons(mode);
    log.finer("I/O panel element redrawn");
  }
}
