/* GenericButton.java
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
import java.util.List;
import java.util.ArrayList;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

/**
 * Abstract mouse- or keyboard-controlled button.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */

public abstract class GenericButton
  extends GenericBitmap
  implements Resizeable {

  // static logger
  private static final Logger log =
    Logger.getLogger(GenericButton.class.getName());
    
  /**
   * State of the button.  <code>true</code> = pressed/down,
   *                       <code>false</code> = not pressed/up.
   */
  protected boolean pressed;

  /**
   * Icon template string.
   */
  protected String template;

  /**
   * Icon displayed when the button is not pressed (up).
   */
  protected Icon offIcon;

  /**
   * Icon displayed when the button is pressed (down).
   */
  protected Icon onIcon;

  // shortcut
  private Shortcut shortcut;

  // tool-tip
  private String toolTip;

  // list of change listeners
  private final List<ChangeListener> changeListeners = new ArrayList<>();

  /**
   * Creates an instance of a button.
   *
   * @param template the icon template string (if <code>null</code>,
   *                 a dummy button without a graphical representation
   *                 is created)
   * @param shortcut keyboard shortcut for the button (<code>null</code>
   *                 if none)
   * @param toolTip  tool-tip for the button (<code>null</code> if none)
   */
  public GenericButton(final String template,
		       final Shortcut shortcut,
		       final String toolTip) {
    super();
    log.fine("New GenericButton creation started: " + template);
    this.template = template;
    if (template != null) {
      redraw();
      setIcon(offIcon);
      GUI.addResizeable(this);
    }
    this.shortcut = shortcut;
    setToolTip(toolTip);
    log.fine("New GenericButton created: " + template);
  }

  /**
   * Redraw the icons.
   */
  public void redraw() {
    log.finer("Redraw started, template: " + template);
    final int pixelSize = GUI.getPixelSize();
    offIcon = IconCache.get(String.format(template, pixelSize, "u"));
    onIcon = IconCache.get(String.format(template, pixelSize, "d"));
    log.finer("Redraw finished, pixel size: " + pixelSize +
	      ", template: " + template);
  }

  /**
   * Sets the icon template string.
   *
   * @param template the icon template string
   */
  public void setTemplate(final String template) {
    this.template = template;
    if (template != null) {
      redraw();
      setIcon(offIcon);
    }
    log.fine("New template set: " + template);
  }

  /**
   * Gets the icon template string.
   *
   * @return the icon template string
   */
  public String getTemplate() {
    log.finer("Template retrieved: " + template);
    return template;
  }

  /**
   * Sets the state of the button (<code>true</code> = pressed/down,
   * <code>false</code> = not pressed/up).
   *
   * @param b state of the button, <code>true</code> if pressed (down),
   *          <code>false</code> otherwise
   */
  public void setPressed(final boolean b) {
    if (b != pressed) {
      pressed = b;
      setIcon(b ? onIcon : offIcon);
      fireStateChanged();
    }
  }

  /**
   * The state of the button.  <code>true</code> if the button is pressed
   * (down), <code>false</code> otherwise.
   *
   * @return state of the button, <code>true</code> if pressed (down),
   *         <code>false</code> otherwise
   */
  public boolean isPressed() {
    return pressed;
  }

  /**
   * Sets the keyboard shortcut associated with the button.
   *
   * @param shortcut the keyboard shortcut associated with the button
   *                 or <code>null</code> if none

   */
  public void setShortcut(final Shortcut shortcut) {
    this.shortcut = shortcut;
  }

  /**
   * Gets the keyboard shortcut associated with the button.
   *
   * @return the keyboard shortcut associated with the button
   *         or <code>null</code> if none
   */
  public Shortcut getShortcut() {
    return shortcut;
  }

  /**
   * Sets the tool-tip associated with the button.
   *
   * @param toolTip new tool-tip associated with the button
   */
  public void setToolTip(final String toolTip) {
    this.toolTip = toolTip;
    if (toolTip != null) {
      setToolTipText(toolTip);
    }
  }

  /**
   * Gets the tool-tip associated with the button.
   *
   * @return tool-tip associated with the button
   */
  public String getToolTip() {
    return toolTip;
  }

  // for description see Resizeable
  @Override
  public void redrawOnPixelResize() {
    redraw();
    if (pressed) {
      setIcon(onIcon);
    } else {
      setIcon(offIcon);
    }
  }

  /**
   * Adds a <code>ChangeListener</code> to the button.
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
   * Removes a <code>ChangeListener</code> from the button.
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
