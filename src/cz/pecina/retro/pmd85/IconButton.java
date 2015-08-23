/* IconButton.java
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

package cz.pecina.retro.pmd85;

import java.util.logging.Logger;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import cz.pecina.retro.common.Localized;
import cz.pecina.retro.common.Application;

import cz.pecina.retro.gui.GenericButton;

/**
 * Icons displayed on the main panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class IconButton extends GenericButton implements Localized {

  // static logger
  private static final Logger log =
    Logger.getLogger(IconButton.class.getName());

  // text identification of the icon
  private String id;

  // position of the icon on the control panel
  private int positionX, positionY;

  // frame controlled by the icon
  private HidingFrame frame;

  // the computer control object
  private Computer computer;

  // tool-tip resource
  private String toolTipResource;

  /**
   * Creates an instance of an icon controlling the hiding frames.
   *
   * @param computer        the computer control object
   * @param id              text identification of the button (used
   *                        when fetching the icons)
   * @param positionX       position of the icon on the control panel
   *                        (x-coordinate)
   * @param positionY       position of the icon on the control panel
   *                        (y-coordinate)
   * @param toolTipResource tool-tip for the button (<code>null</code> if none)
   */
  public IconButton(final Computer computer,
		    final String id,
		    final int positionX,
		    final int positionY,
		    final String toolTipResource) {
    super("pmd85/IconButton/" + id + "-%d-%s.png",
	  null,
	  (toolTipResource == null) ?
	    null :
	    Application.getString(IconButton.class, toolTipResource));
    log.fine("New IconButton creation started: " + id);
    assert computer != null;
    assert id != null;
    this.computer = computer;
    this.id = id;
    this.positionX = positionX;
    this.positionY = positionY;
    this.toolTipResource = toolTipResource;
  log.fine("New IconButton created: " + id);
  }

  // mouse listener
  private class IconButtonMouseListener extends MouseAdapter {
    @Override
    public void mousePressed(final MouseEvent event) {
      if (pressed) {
	setPressed(false);
	frame.tearDown();
	frame.setVisible(false);
      } else {
	setPressed(true);
	frame.setUp();
	frame.setLocationRelativeTo(computer.getComputerFrame());
	frame.setVisible(true);
	frame.requestFocus();
      }
    }
  }

  // close listener
  private class CloseWindowListener extends WindowAdapter {
    @Override
    public void windowClosing(final WindowEvent event) {
      setPressed(false);
      frame.tearDown();
      frame.setVisible(false);
    }
  }

  /**
   * Sets the frame controlled by the icon.
   *
   * @param frame the frame controlled by the icon.
   */
  public void setFrame(final HidingFrame frame) {
    this.frame = frame;
    addMouseListener(new IconButtonMouseListener());
    frame.addWindowListener(new CloseWindowListener());
  }

  /**
   * Gets the position of the icon on the control panel (x-coordinate).
   *
   * @return position of the icon on the control panel (x-coordinate)
   */
  public int getPositionX() {
    return positionX;
  }

  /**
   * Gets the position of the icon on the control panel (y-coordinate).
   *
   * @return position of the icon on the control panel (y-coordinate)
   */
  public int getPositionY() {
    return positionY;
  }

  // for description see Localized
  @Override
  public void redrawOnLocaleChange() {
    if (toolTipResource != null) {
      setToolTip(Application.getString(this, toolTipResource));
    }
  }
}
