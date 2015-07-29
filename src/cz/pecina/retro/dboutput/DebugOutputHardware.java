/* DebugOutputHardware.java
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

package cz.pecina.retro.dboutput;

import java.util.logging.Logger;
import cz.pecina.retro.cpu.IOElement;
import cz.pecina.retro.common.Parameters;

/**
 * Debug output from a port to <code>System.out</code> hardware.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class DebugOutputHardware implements IOElement {

  // static logger
  private static final Logger log =
    Logger.getLogger(DebugOutputHardware.class.getName());

  // base port
  private int basePort;

  /**
   * Creates a new DebugOutput hardware object.
   *
   * @param basePort the base port
   */
  public DebugOutputHardware(final int basePort) {
    assert (basePort >= 0) && (basePort < 0x100);
    this.basePort = basePort;

    // connect to port
    connect();

    log.fine("New DebugOutput hardware created");
  }

  // connect to port
  private void connect() {
    Parameters.cpu.addIOOutput(basePort, this);
    log.fine("Port connected");
  }

  // disconnect from port
  private void disconnect() {
    Parameters.cpu.removeIOOutput(basePort, this);
    log.fine("Port disconnected");
  }

  /**
   * Reconnects DebugOutput to a new base port
   *
   * @param basePort the new base port
   */
  public void reconnect(final int basePort) {
    assert (basePort >= 0) && (basePort < 0x100);
    if (this.basePort != basePort) {
      disconnect();
      this.basePort = basePort;
      connect();
      log.fine(String.format(
        "DebugOutput hardware reconnected to new base port %02x",
	basePort));
    } else {
      log.finer("DebugOutput hardware port reconnection not required");
    }
  }

  /**
   * Deactivates hardware.
   */
  public void deactivate() {
    disconnect();
    log.fine("DebugOutput hardware deactivated");
  }

  // for description see IOElement
  @Override
  public synchronized void portOutput(final int port, final int data) {
    System.out.print((char)data);
  }

  // for description see IOElement
  @Override
  public int portInput(final int port) {
    return 0xff;
  }
}
