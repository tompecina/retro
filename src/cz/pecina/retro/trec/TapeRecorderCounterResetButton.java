/* TapeRecorderCounterResetButton.java
 *
 * Copyright (C) 2014-2015, Tomáš Pecina <tomas@pecina.cz>
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
import cz.pecina.retro.gui.PushButton;
import cz.pecina.retro.common.Localized;
import cz.pecina.retro.common.Application;

/**
 * Tape recorder counter reset button.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class TapeRecorderCounterResetButton
  extends PushButton
  implements Localized {

  // static logger
  private static final Logger log =
    Logger.getLogger(TapeRecorderCounterResetButton.class.getName());

  // tool-tip resource
  private String toolTipResource;

  /**
   * Creates an instance of the button.
   *
   * @param toolTipResource tool-tip for the button (<code>null</code> if none)
   */
  public TapeRecorderCounterResetButton(final String toolTipResource) {
    super("gui/CounterResetButton/basic-gray-%d-%s.png", -1,
      (toolTipResource == null) ? null : Application
      .getString(TapeRecorderCounterResetButton.class, toolTipResource));
    this.toolTipResource = toolTipResource;
    Application.addLocalized(this);
    log.fine("New TapeRecorderCounterResetButton created");
  }
    
  // for description see Localized
  @Override
  public void redrawOnLocaleChange() {
    setToolTip(Application.getString(this, toolTipResource));
  }
}
