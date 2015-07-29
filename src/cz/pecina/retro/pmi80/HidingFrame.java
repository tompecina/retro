/* HidingFrame.java
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

package cz.pecina.retro.pmi80;

import java.util.logging.Logger;
import javax.swing.JFrame;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import cz.pecina.retro.common.Localized;
import cz.pecina.retro.common.Application;
import cz.pecina.retro.gui.CloseableFrame;
import cz.pecina.retro.gui.GUI;

/**
 * Icon-controlled <code>JFrame</code>.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public abstract class HidingFrame extends CloseableFrame implements Localized {

  // static logger
  private static final Logger log =
    Logger.getLogger(HidingFrame.class.getName());

  // title of the frame
  private String title;

  // the icon controlling the frame
  private IconButton icon;

  /**
   * Creates an instance of an icon-controlled <code>JFrame</code>.
   *
   * @param title title of the frame
   * @param icon  icon controlling the frame
   */
  public HidingFrame(final String title, final IconButton icon) {
    super(title);
    log.fine("New HidingFrame '" + title + "' creation started");
    this.title = title;
    this.icon = icon;
    Application.addLocalized(this);
    GUI.setApplicationIcons(this);
    icon.setFrame(this);
    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    addFocusListener(new FocusAdapter() {
	@Override
	public void focusGained(FocusEvent event) {
	  toFront();
	}
      });
    log.fine("New HidingFrame '" + title + "' set up");
  }

  /**
   * Close (hide) the frame.
   */
  public void close() {
    log.fine("HidingFrame '" + title + "' closing down");
    tearDown();
    setVisible(false);
    icon.setPressed(false);
    log.fine("HidingFrame '" + title + "' closed down");
  }

  /**
   * Method invoked after the <code>HidingFrame</code> is shown.
   * Placeholder method expected to be overridden by subclasses.
   */
  protected void setUp() {
  }

  /**
   * Method invoked before the <code>HidingFrame</code> is hidden.
   * Placeholder method expected to be overridden by subclasses.
   */
  protected void tearDown() {
  }

  /**
   * Gets the icon button controlling the frame.
   *
   * @return the icon button controlling the frame
   */
  public IconButton getIcon() {
    return icon;
  }

  // for description see Localized
  @Override
  public abstract void redrawOnLocaleChange();
}
