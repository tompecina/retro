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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import cz.pecina.retro.common.Localized;
import cz.pecina.retro.common.Application;

import cz.pecina.retro.gui.GenericButton;

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

  // position of the icon on the control panel
  private int positionX, positionY;

  // state of the icon
  private boolean pressed;

  // state of the LED
  private boolean state;

  // icon template string
  private String template;

  // array of icons (first index: icon, second index: LED)
  private Icon icons[][] = new Icon[2][2];

  // tool-tip
  private String toolTip;

  // tool-tip resource
  private String toolTipResource;

  // list of change listeners
  private final List<ChangeListener> changeListeners = new ArrayList<>();

  /**
   * Creates an instance of a floppy disk drive icon.
   *
   * @param color           color of the LED
   * @param toolTipResource tool-tip for the icon ({@code null} if none)
   */
  public IconButton(final String color,
		    final String toolTipResource) {
    super();
    log.fine("New FloppyIcon creation started");
    assert color != null;
    this.color = color;
    setToolTip((toolTipResource == null) ?
	       null :
	       Application.getString(IconButton.class, toolTipResource));
    addMouseListener(new FloppyIconMouseListener());
    assert computer != null;
    assert id != null;
    this.computer = computer;
    this.id = id;
    this.positionX = positionX;
    this.positionY = positionY;
    this.toolTipResource = toolTipResource;
  log.fine("New FloppyIcon created");
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
