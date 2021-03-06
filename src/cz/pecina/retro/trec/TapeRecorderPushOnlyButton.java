/* TapeRecorderPushOnlyButton.java
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

package cz.pecina.retro.trec;

import java.util.logging.Logger;

import cz.pecina.retro.common.Localized;
import cz.pecina.retro.common.Application;

import cz.pecina.retro.gui.PushOnlyButton;

/**
 * Wrapper class for localized PushOnlyButton.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class TapeRecorderPushOnlyButton
  extends PushOnlyButton
  implements Localized {

  // static logger
  private static final Logger log =
    Logger.getLogger(TapeRecorderPushOnlyButton.class.getName());

  // text identification of the button
  private String id;

  // tool-tip resource
  private String toolTipResource;

  /**
   * Creates an instance of the button.
   *
   * @param id              text identification of the button
   *                        (used when fetching the icons)
   * @param toolTipResource tool-tip for the button ({@code null} if none)
   */
  public TapeRecorderPushOnlyButton(final String id,
				    final String toolTipResource) {
    super("trec/TapeRecorderButton/" + id + "-%d-%s.png", null,
      (toolTipResource == null) ? null : Application
      .getString(TapeRecorderPushOnlyButton.class, toolTipResource));
    this.id = id;
    this.toolTipResource = toolTipResource;
    Application.addLocalized(this);
    log.fine("New TapeRecorderPushOnlyButton created: " + id);
  }
    
  // for description see Object
  @Override
  public String toString() {
    return id;
  }

  // for description see Localized
  @Override
  public void redrawOnLocaleChange() {
    setToolTip(Application.getString(this, toolTipResource));
  }
}
