/* PushButton.java
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

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;

/**
 * Normal button without the lock option.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class PushButton extends GenericButton {

  // static logger
  private static final Logger log =
    Logger.getLogger(PushButton.class.getName());

  /**
   * Creates an instance of a button.
   *
   * @param template the icon template string (if {@code null},
   *                 a dummy button without a graphical representation
   *                 is created)
   * @param shortcut keyboard shortcut for the button ({@code null}
   *                 if none)
   * @param toolTip  tool-tip for the button ({@code null} if none)
   */
  public PushButton(final String template,
		    final Shortcut shortcut,
		    final String toolTip) {
    super(template, shortcut, toolTip);
    log.fine("New PushButton created");
  }

  // for description see GenericButton
  @Override
  protected void addMouseListeners() {
    addMouseListener(new PushMouseListener());
  }
  
  // mouse listener
  private class PushMouseListener extends MouseAdapter {
    @Override
    public void mousePressed(final MouseEvent event) {
      setPressed(!pressed);
    }

    @Override
    public void mouseReleased(final MouseEvent event) {
      setPressed(false);
    }

    @Override
    public void mouseExited(final MouseEvent event) {
      setPressed(pressed);
    }
  }

  // key listeners
  private class KeyPressedAction extends AbstractAction {
    @Override
    public void actionPerformed(final ActionEvent event) {
      setPressed(pressed);
    }
  }

  private class KeyReleasedAction extends AbstractAction {
    @Override
    public void actionPerformed(final ActionEvent event) {
      setPressed(false);
    }
  }

  /**
   * Creates a new {@code KeyPressedAction} object.
   *
   * @return the new {@code KeyPressedAction} object
   */
  public KeyPressedAction keyPressedAction() {
    return new KeyPressedAction();
  }

  /**
   * Creates a new {@code KeyReleasedAction} object.
   *
   * @return the new {@code KeyReleasedAction} object
   */
  public KeyReleasedAction keyReleasedAction() {
    return new KeyReleasedAction();
  }
}
