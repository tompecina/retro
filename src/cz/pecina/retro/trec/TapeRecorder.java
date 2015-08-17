/* TapeRecorder.java
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
import java.util.List;
import java.util.ArrayList;
import java.awt.Image;
import javax.swing.JFrame;

/**
 * Main package class.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class TapeRecorder {

  // static logger
  private static final Logger log =
    Logger.getLogger(TapeRecorder.class.getName());

  /**
   * Initializes the package.
   */
  public TapeRecorder() {
    log.fine("New TapeRecorder created");
  }

  // for description see Object
  @Override
  public String toString() {
    return "TapeRecorder";
  }
}
