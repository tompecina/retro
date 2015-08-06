/* UserPreferences.java
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
import java.util.prefs.Preferences;
import java.util.prefs.BackingStoreException;
import java.util.Locale;
import java.util.Arrays;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import cz.pecina.retro.common.GeneralUserPreferences;
import cz.pecina.retro.common.GeneralConstants;
import cz.pecina.retro.common.Parameters;
import cz.pecina.retro.common.Application;
import cz.pecina.retro.gui.GUI;
import cz.pecina.retro.cpu.SimpleMemory;

/**
 * Static user preferences to be imported on start-up (emulator specific).
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public final class UserPreferences extends GeneralUserPreferences {

  // static logger
  private static final Logger log =
    Logger.getLogger(UserPreferences.class.getName());

  // gets preferences from the backing store
  private static void getPreferences() {
    GeneralUserPreferences.getGeneralPreferences();
    log.finer("User preferences retrieved");
  }

  // default constructor disabled
  private UserPreferences() {};
}
