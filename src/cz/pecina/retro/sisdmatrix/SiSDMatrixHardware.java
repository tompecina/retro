/* SiSDMatrixHardware.java
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

package cz.pecina.retro.sisdmatrix;

import java.util.logging.Logger;

import cz.pecina.retro.peripherals.Peripheral;

import cz.pecina.retro.common.Parameters;

import cz.pecina.retro.cpu.IOElement;

import cz.pecina.retro.gui.SiSD;
import cz.pecina.retro.gui.GUI;

/**
 * 4x24 SiSD matrix panel hardware.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class SiSDMatrixHardware implements IOElement {

  // static logger
  private static final Logger log =
    Logger.getLogger(SiSDMatrixHardware.class.getName());

  // base port
  private int basePort;

  /**
   * Panel height.
   */
  public static final int NUMBER_LINES = 4;

  /**
   * Panel width.
   */
  public static final int NUMBER_COLUMNS = 24;

  // selected element
  private int line, column;

  // elements
  private final SiSD[][] elements = new SiSD[NUMBER_LINES][NUMBER_COLUMNS];

  /**
   * Creates the SiSD matrix panel hardware object.
   *
   * @param basePort the base port
   */
  public SiSDMatrixHardware(final int basePort) {
    assert (basePort >= 0) && (basePort < 0x100);
    this.basePort = basePort;
    for (int line = 0; line < NUMBER_LINES; line++) {
      for (int column = 0; column < NUMBER_COLUMNS; column++) {
	elements[line][column] = new SiSD("small", "red");
      }
    }
    connect();
    log.fine("New SiSD matrix hardware created");
  }

  /**
   * Deactivates hardware.
   */
  public void deactivate() {
    disconnect();
    for (int line = 0; line < NUMBER_LINES; line++) {
      for (int column = 0; column < NUMBER_COLUMNS; column++) {
	GUI.removeResizeable(elements[line][column]);
      }
    }
  }

  /**
   * Gets a SiSD element.
   *
   * @param  line   line of the element
   * @param  column column of the element
   * @return the element
   */
  public SiSD getElement(final int line, final int column) {
    assert (line >= 0) && (line < NUMBER_LINES);
    assert (column >= 0) && (column < NUMBER_COLUMNS);
    return elements[line][column];
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
   * Reconnects SiSD matrix panel hardware to a new base port.
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
        "SiSD matrix panel hardware reconnected to new base port: %02x",
	basePort));
    } else {
      log.finer("SiSD matrix panel  hardware port reconnection not required");
    }
  }

  // for description see IOElement
  @Override
  public void portOutput(final int port, final int data) {
    log.finer(String.format("Port output: %02x -> (%02x)", data, port));
    switch ((port - basePort) & 0xff) {
      case 0:
	if ((data >= 0) && (data < NUMBER_LINES)) {
	  line = data;
	}
	break;
      case 1:
	if ((data >= 0) && (data < NUMBER_COLUMNS)) {
	  column = data;
	}
	break;
      case 2:
	if ((data >= SiSD.MIN_VALUE) && (data <= SiSD.MAX_VALUE)) {
	  elements[line][column].setState(data);
	}
	break;
    }
  }

  // for description see IOElement
  @Override
  public int portInput(final int port) {
    return 0xff;
  }
}
