/* LockableButton.java
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
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Button with a lock option.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class LockableButton extends GenericButton {

  // static logger
  private static final Logger log =
    Logger.getLogger(LockableButton.class.getName());
    
  /**
   * State of the button.  <code>true</code> = permanently pressed/locked,
   * <code>false</code> otherwise (may be pressed, but not locked).
   */
  protected boolean locked;

  /**
   * Icon displayed when the button is locked.
   */
  protected Icon lockedIcon;

  /**
   * Creates an instance of a lockable button.
   *
   * @param template the icon template string (if <code>null</code>,
   *                 a dummy button without a graphical representation
   *                 is created)
   * @param shortcut keyboard shortcut for the button (<code>null</code>
   *                 if none)
   * @param toolTip  tool-tip for the button (<code>null</code> if none)
   */
  public LockableButton(final String template,
			final Shortcut shortcut,
			final String toolTip) {
    super(template, shortcut, toolTip);
    if (template != null) {
      lockedIcon =
	IconCache.get(String.format(template, GUI.getPixelSize(), "l"));
    }
    log.fine("New LockableButton created");
  }

  // for description see GenericButton
  @Override
  protected void addMouseListeners() {
    addMouseListener(new LockableMouseListener());
  }
  
  /**
   * Redraw the icons.
   */
  @Override
  public void redraw() {
    super.redraw();
    lockedIcon =
      IconCache.get(String.format(template, GUI.getPixelSize(), "l"));
  }

  /**
   * Sets the icon template string.
   *
   * @param template the icon template string
   */
  @Override
  public void setTemplate(final String template) {
    super.setTemplate(template);
    if (template != null) {
      redraw();
    }
    log.fine("New template set: " + template);
  }

  /**
   * Sets the icon displayed when the button is locked.
   *
   * @param lockedIcon icon displayed when the button is locked
   */
  public void setLockedIcon(final Icon lockedIcon) {
    this.lockedIcon = lockedIcon;
    log.finer("LockedIcon set");
  }

  /**
   * Sets the state of the button (<code>true</code> = pressed/down,
   * <code>false</code> = not pressed/up).
   *
   * @param b state of the button, <code>true</code> if pressed (down),
   *          <code>false</code> otherwise
   */
  @Override
  public void setPressed(final boolean b) {
    if (b != pressed) {
      pressed = b;
      if (b) {
	setIcon(onIcon);
      } else {
	setIcon(offIcon);
	locked = false;
      }
      fireStateChanged();
    }
  }

  /**
   * The lock state of the button.  <code>true</code> if the button is
   * permanently pressed (locked), <code>false</code> otherwise (may
   * be pressed, but not locked).
   *
   * @return state of the button, <code>true</code> if locked,
   *         <code>false</code> otherwise (may be pressed, but not locked)
   */
  public boolean isLocked() {
    return locked;
  }

  /**
   * Sets the lock state of the button (<code>true</code> = locked,
   * <code>false</code> = not locked).
   *
   * @param b state of the button, <code>true</code> if locked,
   *          <code>false</code> otherwise
   */
  public void setLocked(final boolean b) {
    if (b != locked) {
      pressed = locked = b;
      setIcon(b ? lockedIcon : offIcon);
      fireStateChanged();
    }
  }

  // mouse listener
  private class LockableMouseListener extends MouseAdapter {
    @Override
    public void mousePressed(MouseEvent event) {
      if ((event.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) != 0) {
	setLocked(!locked);
      } else {
	if (locked) {
	  setLocked(false);
	} else {
	  setPressed(true);
	}
      }
    }

    @Override
    public void mouseReleased(final MouseEvent event) {
      if (!locked) {
	setPressed(false);
      }
    }
	
    @Override
    public void mouseExited(final MouseEvent event) {
      mouseReleased(event);
    }
  }
	
  // key listeners
  private class KeyPressedAction extends AbstractAction {
    @Override
    public void actionPerformed(final ActionEvent event) {
      setPressed(true);
    }
  }

  private class KeyReleasedAction extends AbstractAction {
    @Override
    public void actionPerformed(final ActionEvent event) {
      setPressed(false);
    }
  }

  /**
   * Creates a new <code>KeyPressedAction</code> object.
   *
   * @return the new <code>KeyPressedAction</code> object
   */
  public KeyPressedAction keyPressedAction() {
    return new KeyPressedAction();
  }

  /**
   * Creates a new <code>KeyReleasedAction</code> object.
   *
   * @return the new <code>KeyReleasedAction</code> object
   */
  public KeyReleasedAction keyReleasedAction() {
    return new KeyReleasedAction();
  }

  // for description see Resizeable
  @Override
  public void redrawOnPixelResize() {
    redraw();
    if (locked) {
      setIcon(lockedIcon);
    } else if (pressed) {
      setIcon(onIcon);
    } else {
      setIcon(offIcon);
    }
  }
}
