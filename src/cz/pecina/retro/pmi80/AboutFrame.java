/* AboutFrame.java
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

import cz.pecina.retro.common.Application;

/**
 * The About frame.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class AboutFrame extends HidingFrame {

  // static logger
  private static final Logger log =
    Logger.getLogger(AboutFrame.class.getName());

  // About panel
  private AboutPanel aboutPanel;

  /**
   * Creates the About frame.
   *
   * @param computer the computer control object
   */
  public AboutFrame(final Computer computer) {
    super(Application.getString(AboutFrame.class, "about.frameTitle"),
	  computer.getIconLayout().getIcon(IconLayout.ICON_POSITION_INFO));
    log.fine("New AboutFrame creation started");
    aboutPanel = new AboutPanel(this);
    add(aboutPanel);
    pack();
    log.fine("AboutFrame set up");
  }

  /**
   * Close the frame.
   */
  public void closeFrame() {
    close();
  }

  // redraw frame
  private void redraw() {
    log.fine("AboutFrame redraw started");
    super.setTitle(Application.getString(this, "about.frameTitle"));
    remove(aboutPanel);
    aboutPanel = new AboutPanel(this);
    add(aboutPanel);
    pack();
    log.fine("AboutFrame redraw completed");
  }

  // for description see Localized
  @Override
  public void redrawOnLocaleChange() {
    redraw();
  }
}
