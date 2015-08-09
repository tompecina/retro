/* SiSDBlock.java
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
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import cz.pecina.retro.gui.PushButton;
import cz.pecina.retro.gui.UniversalPushButton;

/**
 * Hexadecimal or alphanumeric block of SiSD elements.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class SiSDBlock {

  // static logger
  private static final Logger log =
    Logger.getLogger(SiSDBlock.class.getName());

  // state of the block (Long for hex display, String for alphanumeric display)
  private Object state;

  // model for the block
  private BlockModel model;

  // SiSD elements of the block
  private SiSD[] elements;

  // nudge buttons (used only if the block is settable)
  private PushButton[] incrementButtons, decrementButtons;

  // number of elements in the block
  private int size;

  // true = alphanumeric block, false = hexadecimal block
  private boolean alpha;

  // true if the block is user adjustable
  private boolean settable;

  // list of change listeners
  private final List<ChangeListener> changeListeners = new ArrayList<>();

  /**
   * Creates a new SiSD block.
   *
   * @param model    model for the block
   * @param size     number of elements (max. 15 for hexadecimal blocks)
   * @param alpha    if <code>true</code>, an alphanumeric block will
   *                 be created, if <code>false</code>, a hexadecimal one
   * @param settable determines if the block is user adjustable; only
   *                 non-alphanumeric blocks can be created with this option
   */
  public SiSDBlock(final BlockModel model,
		   final int size,
		   final boolean alpha,
		   final boolean settable) {
    log.fine("New SiSDBlock creation started, size: " + size +
	     ", alpha: " + alpha + ", settable: " + settable);
    assert !(alpha && settable);
    assert (size > 0) && (alpha || (size <= 15));
    this.model = model;
    this.size = size;
    this.alpha = alpha;
    this.settable = settable;
    if (alpha) {
      state = new String();
    } else {
      state = new Long(-1);
    }
    elements = new SiSD[size];
    if (settable) {
      incrementButtons = new PushButton[size];
      decrementButtons = new PushButton[size];
    }
    SiSD sisd;
    PushButton button;
    for (int i = 0; i < size; i++) {
      sisd = elements[i] =
	alpha ?
	new SiSD(model.elementType, model.elementColor) :
	new HexSiSD(model.elementType, model.elementColor);
      if (settable) {
	sisd.addMouseWheelListener(new NudgeMouseWheelListener(this, i));
	incrementButtons[i] = button =
	  new UniversalPushButton(model.buttonType,
				  model.buttonColor,
				  model.incrementButtonSymbol,
				  null,
				  null);
	button.addMouseListener(new NudgeListener(this, i, 1));
	decrementButtons[i] = button =
	  new UniversalPushButton(model.buttonType,
				  model.buttonColor,
				  model.decrementButtonSymbol,
				  null,
				  null);
	button.addMouseListener(new NudgeListener(this, i, -1));
      }
    }
    log.fine("New SiSDBlock created");
  }

  /**
   * Places the block on the panel.
   *
   * @param container container where the block will be placed
   * @param positionX x-coordinate, in base-size pixels
   * @param positionY y-coordinate, in base-size pixels
   */
  public void place(final JComponent container,
		    final int positionX,
		    final int positionY) {
    assert container != null;
    for (int i = 0; i < size; i++) {
      elements[i].place(container, positionX + (i * model.gridX), positionY);
      if (settable) {
	incrementButtons[i].place(
	  container,
	  positionX + (i * model.gridX) + model.incrementButtonOffsetX,
	  positionY + model.incrementButtonOffsetY);
	decrementButtons[i].place(
	  container,
	  positionX + (i * model.gridX) + model.decrementButtonOffsetX,
	  positionY + model.decrementButtonOffsetY);
      }
    }
    log.fine("SiSDBlock placed on the panel");
  }

  /**
   * Gets the size (number of elements) of the block.
   *
   * @return the size (number of element) of the block
   */
  public int getSize() {
    return size;
  }

  /**
   * Determines if the block is alphanumeric.
   *
   * @return <code>true</code> if the block is alphanumeric,
   *         <code>false</code> otherwise
   */
  public boolean isAlpha() {
    return alpha;
  }

  /**
   * Blanks the hexadecimal block.
   */
  public void setBlank() {
    assert !alpha;
    setState(-1);
  }

  /**
   * Determines whether the hexadecimal block is blank.
   *
   * @return <code>true</code> if the block is blank,
   *         <code>false</code> otherwise
   */
  public boolean isBlank() {
    assert !alpha;
    return (long)state == -1;
  }

  /**
   * Gets the state of the block.  For alphanumeric blocks, this is
   * a <code>String</code> object, and a <code>Long</code> object for
   * hexadecimal blocks.
   *
   * @return the state of the block
   */
  public Object getState() {
    return state;
  }

  /**
   * Sets the state of the hexadecimal block.
   *
   * @param state the new state of the block
   */
  public void setState(final byte state) {
    assert !alpha;
    setState((long)(state & 0xff));
  }

  /**
   * Sets the state of the hexadecimal block.
   *
   * @param state the new state of the block
   */
  public void setState(final int state) {
    assert !alpha;
    setState((long)state);
  }

  /**
   * Sets the state of the block.
   *
   * @param state the new state of the block
   */
  public void setState(final Object state) {
    log.finer("Setting SiSDBlock state to: '" + state + "'");
    this.state = state;
    if (alpha) {
      final String stringState = (state == null) ? "" : (String)state;
      for (int i = 0; i < size; i++) {
	if (i < stringState.length()) {
	  final char ch = stringState.charAt(i);
	  if ((ch >= SiSD.MIN_VALUE) && (ch <= SiSD.MAX_VALUE)) {
	    elements[i].setState(ch);
	  } else {
	    elements[i].setState('?');
	  }
	} else {
	  elements[i].setState(' ');
	}
      }
    } else {
      long longState = (long)state;
      for (int i = 0; i < size; i++) {
	if (longState == -1) {
	  elements[i].setState(' ');
	} else {
	  elements[size - i - 1].setState((int)longState & 0x0f);
	  longState >>= 4;
	}
      }
    }
  }

  /**
   * Adds a <code>ChangeListener</code> to the block.
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
   * Removes a <code>ChangeListener</code> from the block.
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
}
