/* ColorLEDMatrixHardware.java
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

package cz.pecina.retro.colorledmatrix;

import java.util.logging.Logger;
import cz.pecina.retro.peripherals.Peripheral;
import cz.pecina.retro.common.Parameters;
import cz.pecina.retro.cpu.IOElement;
import cz.pecina.retro.gui.LED;
import cz.pecina.retro.gui.GUI;

/**
 * 32x32 color LED matrix panel hardware.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class ColorLEDMatrixHardware implements IOElement {

  // static logger
  private static final Logger log =
    Logger.getLogger(ColorLEDMatrixHardware.class.getName());

  // base port
  private int basePort;

  // selected LED
  private int row, column;

  // matrix geometry
  private static final int LED_GRID_X = 8;
  private static final int LED_GRID_Y = 8;

  // LEDs
  private ColorLEDMatrixElement element;

  /**
   * Creates the color LED matrix panel hardware object.
   *
   * @param basePort the base port
   */
  public ColorLEDMatrixHardware(final int basePort) {
    assert (basePort >= 0) && (basePort < 0x100);
    log.fine("New color LED matrix hardware creation started");
    this.basePort = basePort;
    element = new ColorLEDMatrixElement("small", LED_GRID_X, LED_GRID_Y);
    connect();
    log.finer("New color LED matrix hardware created");
  }

  /**
   * Deactivates hardware.
   */
  public void deactivate() {
    disconnect();
    GUI.removeResizeable(element);
  }

  /**
   * Gets the color LED matrix element.
   *
   * @return the color LED matrix element object
   */
  public ColorLEDMatrixElement getColorLEDMatrixElement() {
    return element;
  }    

  // connect to ports
  private void connect() {
    for (int i = 0; i < 3; i++) {
      Parameters.cpu.addIOOutput((basePort + i) & 0xff, this);
    }
    log.fine("Ports connected");
  }

  // disconnect from ports
  private void disconnect() {
    for (int i = 0; i < 3; i++) {
      Parameters.cpu.removeIOOutput((basePort + i) & 0xff, this);
    }
    log.fine("Ports disconnected");
  }

  /**
   * Reconnects color LED matrix panel hardware to a new base port.
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
        "Color LED matrix panel hardware reconnected to new base port: %02x",
	basePort));
    } else
      log.finer("Color LED matrix panel hardware port reconnection" +
		" not required");
  }

  // for description see IOElement
  @Override
  public void portOutput(int port, int data) {
    log.finer(String.format("Port output: %02x -> (%02x)", data, port));
    switch ((port - basePort) & 0xff) {
      case 0:
	if ((data >= 0) && (data < ColorLEDMatrixElement.NUMBER_ROWS)) {
	  row = data;
	}
	break;
      case 1:
	if ((data >= 0) && (data < ColorLEDMatrixElement.NUMBER_COLUMNS)) {
	  column = data;
	}
	break;
      case 2:
	if ((data >= 0) && (data <= ColorLEDMatrixElement.NUMBER_COLORS)) {
	  element.setState(row, column, data);
	}
	break;
    }
  }

  // for description see IOElement
  @Override
  public int portInput(int port) {
    return element.getState(row, column);
  }
}
