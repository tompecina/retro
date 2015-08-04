/* LEDMatrixHardware.java
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

package cz.pecina.retro.ledmatrix;

import java.util.logging.Logger;
import cz.pecina.retro.peripherals.Peripheral;
import cz.pecina.retro.common.Parameters;
import cz.pecina.retro.cpu.IOElement;
import cz.pecina.retro.gui.LED;
import cz.pecina.retro.gui.GUI;

/**
 * 32x32 LED matrix panel hardware.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class LEDMatrixHardware implements IOElement {

  // static logger
  private static final Logger log =
    Logger.getLogger(LEDMatrixHardware.class.getName());

  // base port
  private int basePort;

  // selected LED
  private int row, column;

  // matrix geometry
  private static final int LED_GRID_X = 8;
  private static final int LED_GRID_Y = 8;

  // LEDs
  private LEDMatrixElement element;

  /**
   * Creates the LED matrix panel hardware object.
   *
   * @param basePort the base port
   */
  public LEDMatrixHardware(final int basePort) {
    assert (basePort >= 0) && (basePort < 0x100);
    log.fine("New LED matrix hardware creation started");
    this.basePort = basePort;
    element = new LEDMatrixElement("small", "red", LED_GRID_X, LED_GRID_Y);
    connect();
    log.finer("New LED matrix hardware created");
  }

  /**
   * Deactivates hardware.
   */
  public void deactivate() {
    disconnect();
    GUI.removeResizeable(element);
  }

  /**
   * Gets the LED matrix element.
   *
   * @return the LED matrix element object
   */
  public LEDMatrixElement getLEDMatrixElement() {
    return element;
  }    

  // connect to ports
  private void connect() {
    for (int i = 0; i < 3; i++) {
      Parameters.cpu.addIOOutput((basePort + i) & 0xff, this);
      Parameters.cpu.addIOInput((basePort + i) & 0xff, this);
    }
    log.fine("Ports connected");
  }

  // disconnect from ports
  private void disconnect() {
    for (int i = 0; i < 3; i++) {
      Parameters.cpu.removeIOOutput((basePort + i) & 0xff, this);
      Parameters.cpu.removeIOInput((basePort + i) & 0xff, this);
    }
    log.fine("Ports disconnected");
  }

  /**
   * Reconnects LED matrix panel hardware to a new base port.
   *
   * @param basePort the new base port
   */
  public void reconnect(int basePort) {
    assert (basePort >= 0) && (basePort < 0x100);
    if (this.basePort != basePort) {
      disconnect();
      this.basePort = basePort;
      connect();
      log.fine(String.format(
        "LED matrix panel hardware reconnected to new base port: %02x",
	basePort));
    } else
      log.finer("LED matrix panel hardware port reconnection not required");
  }

  // for description see IOElement
  @Override
  public void portOutput(int port, int data) {
    log.finer(String.format("Port output: %02x -> (%02x)", data, port));
    switch ((port - basePort) & 0xff) {
      case 0:
	if ((data >= 0) && (data < LEDMatrixElement.NUMBER_ROWS)) {
	  row = data;
	}
	break;
      case 1:
	if ((data >= 0) && (data < LEDMatrixElement.NUMBER_COLUMNS)) {
	  column = data;
	}
	break;
      case 2:
	element.setState(row, column, data & 1);
	break;
    }
  }

  // for description see IOElement
  @Override
  public int portInput(int port) {
    int r = 0xff;
    switch ((port - basePort) & 0xff) {
      case 0:
	r = row;
	break;
      case 1:
	r = column;
	break;
      case 2:
	r = element.getState(row, column);
	break;
    }
    log.finer(String.format("Port input: (%02x) -> %02x", port, r));
    return r;
  }
}
