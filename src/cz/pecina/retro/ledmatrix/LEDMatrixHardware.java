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
 * 4x24 LED matrix panel hardware.
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

  /**
   * Number of rows.
   */
  public static final int NUMBER_ROWS = 32;

  /**
   * Number of columns.
   */
  public static final int NUMBER_COLUMNS = 32;

  // selected LED
  private int row, column;

  // LEDs
  private final LED[][] leds = new LED[NUMBER_ROWS][NUMBER_COLUMNS];

  /**
   * Creates the LED matrix panel hardware object.
   *
   * @param basePort the base port
   */
  public LEDMatrixHardware(final int basePort) {
    assert (basePort >= 0) && (basePort < 0x100);
    this.basePort = basePort;
    for (int row = 0; row < NUMBER_ROWS; row++) {
      for (int column = 0; column < NUMBER_COLUMNS; column++) {
	leds[row][column] = new LED("small", "red");
      }
    }
    connect();
    log.fine("New LED matrix hardware created");
  }

  /**
   * Deactivates hardware.
   */
  public void deactivate() {
    disconnect();
    for (int row = 0; row < NUMBER_ROWS; row++) {
      for (int column = 0; column < NUMBER_COLUMNS; column++) {
	GUI.removeResizeable(leds[row][column]);
      }
    }
  }

  /**
   * Gets a LED.
   *
   * @param  row    row of the LED
   * @param  column column of the LED
   * @return the LED object
   */
  public LED getLED(final int row, final int column) {
    assert (row >= 0) && (row < NUMBER_ROWS);
    assert (column >= 0) && (column < NUMBER_COLUMNS);
    return leds[row][column];
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
	if ((data >= 0) && (data < NUMBER_ROWS)) {
	  row = data;
	}
	break;
      case 1:
	if ((data >= 0) && (data < NUMBER_COLUMNS)) {
	  column = data;
	}
	break;
      case 2:
	leds[row][column].setState(data & 1);
	break;
    }
  }

  // for description see IOElement
  @Override
  public int portInput(int port) {
    return 0xff;
  }
}
