/* DebugOutput.java
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
 **/

package cz.pecina.retro.dboutput;

import java.util.logging.Logger;
import cz.pecina.retro.peripherals.BasePortPeripheral;

/**
 * Debug output from a port to <code>System.out</code>.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class DebugOutput extends BasePortPeripheral {

  // static logger
  private static final Logger log =
    Logger.getLogger(DebugOutput.class.getName());

  /**
   * The default base port.
   */
  protected static final int DEFAULT_BASE_PORT = 0x00;

  // hardware
  private DebugOutputHardware hardware;

  /**
   * Creates a new DebugOutput.
   */
  public DebugOutput() {
    super("dboutput", DEFAULT_BASE_PORT);
    log.fine("New DebugOutput control object created");
  }

  // for description see Peripheral
  @Override
  public void activate() {
    log.fine("DebugOutput activating");
    hardware = new DebugOutputHardware(getBasePort());
    super.activate();
    log.fine("DebugOutput activated");
  }

  // for description see Peripheral
  @Override
  public void deactivate() {
    super.deactivate();
    hardware.deactivate();
    log.fine("DebugOutput deactivated");
  }

  // for description see Peripheral
  @Override
  public void implementSettings() {
    super.implementSettings();
    if (isActive()) {
      hardware.reconnect(getBasePort());
    }
    log.fine("Changed settings implemented for DebugOutput");
  }
}
