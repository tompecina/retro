/* Emulator.java
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
import java.util.Locale;
import java.util.Arrays;
import javax.swing.ToolTipManager;
import cz.pecina.retro.common.GeneralConstants;
import cz.pecina.retro.common.GeneralUserPreferences;
import cz.pecina.retro.common.Parameters;
import cz.pecina.retro.common.Application;
import cz.pecina.retro.gui.GUI;
import cz.pecina.retro.gui.IconCache;
import cz.pecina.retro.memory.Memory;
import cz.pecina.retro.trec.TapeRecorder;
import cz.pecina.retro.debug.Debugger;
import cz.pecina.retro.peripherals.Peripherals;

/**
 * Main class of the application.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Emulator {

  // static logger
  private static final Logger log =
    Logger.getLogger(Emulator.class.getName());

  /**
   * Creates an instance of the emulator.
   */
  public Emulator() {
    log.fine("New Emulator creation started");

    // set OpenGL for Linux environment
    System.setProperty("sun.java2d.opengl", Parameters.openGL ? "true" : "false");

    // set general parameters
    Parameters.CPUFrequency = Constants.CPU_FREQUENCY;
    Parameters.timerPeriod = Constants.TIMER_PERIOD;
    Parameters.timerCycles = Constants.TIMER_CYCLES;

    // initialize application
    GeneralUserPreferences.setNodeClass(UserPreferences.class);
    Application.setLocale(Locale.forLanguageTag(UserPreferences.getLocale()));
    Application.addModules(this,
    			   new GUI(),
    			   new Memory(),
    			   new TapeRecorder(),
    			   new Debugger(),
    			   new Peripherals());
    GUI.setPixelSize(UserPreferences.getPixelSize());
    log.fine("Application set up");

    // set up tooltip parameters
    final ToolTipManager ttm = ToolTipManager.sharedInstance();
    ttm.setInitialDelay(GeneralConstants.TOOL_TIP_INITIAL_DELAY);
    ttm.setDismissDelay(GeneralConstants.TOOL_TIP_DISMISS_DELAY);
    ttm.setReshowDelay(GeneralConstants.TOOL_TIP_RESHOW_DELAY);
    log.fine("General tooltip options set");

    // set up application icons
    for (int size: GUI.APPLICATION_ICON_SIZES) {
      GUI.addApplicationIcon(IconCache.get("pmd85/ApplicationIcons/icon-" +
    					   size + ".png").getImage());
    }

    // // create new computer control object
    // new Computer();

    log.fine("New Emulator creation completed");
  }

  // for description see Object
  @Override
  public String toString() {
    return "Emulator";
  }

  /**
   * Main method.
   *
   * @param args command-line arguments
   */
  public static void main(final String args[]) {
    log.fine("Application started");
    Parameters.arguments = args;
    new Emulator();
  }
}
