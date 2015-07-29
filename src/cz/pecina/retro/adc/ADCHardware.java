/* ADCHardware.java
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

package cz.pecina.retro.adc;

import java.util.logging.Logger;
import cz.pecina.retro.peripherals.Peripheral;
import cz.pecina.retro.common.Parameters;
import cz.pecina.retro.cpu.IOElement;
import cz.pecina.retro.gui.GUI;
import cz.pecina.retro.common.Util;

/**
 * 8-channel ADC hardware.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class ADCHardware implements IOElement {

  // static logger
  private static final Logger log =
    Logger.getLogger(ADCHardware.class.getName());

  /**
   * Number of channels.
   */
  public static final int NUMBER_CHANNELS = 8;

  // base port
  private int basePort;

  // channels
  private final ADCChannel[] channels = new ADCChannel[NUMBER_CHANNELS];

  // selected channel
  private int channel;

  /**
   * Creates the ADC hardware object.
   *
   * @param basePort the base port
   * @param offset   the x-offset of the channel's path in pixels
   * @param length   the base length of the channel's path in pixels
   */
  public ADCHardware(final int basePort,
		     final int offset,
		     final int length) {
    assert (basePort >= 0) && (basePort < 0x100);
    assert offset >= 0;
    assert length > 0;
    this.basePort = basePort;
    for (int i = 0; i < NUMBER_CHANNELS; i++) {
      channels[i] = new ADCChannel(offset, length);
    }
    connect();
    log.fine("New ADC hardware created");
  }

  /**
   * Gets the channel object.
   *
   * @param  n channel number
   * @return the channel object
   */
  public ADCChannel getChannel(final int n) {
    assert (n >= 0) && (n < NUMBER_CHANNELS);
    return channels[n];
  }

  /**
   * Deactivates hardware.
   */
  public void deactivate() {
    disconnect();
    for (int i = 0; i < NUMBER_CHANNELS; i++) {
      GUI.removeResizeable(channels[i]);
    }
    log.fine("ADC hardware deactivated");
  }

  // connect to ports
  private void connect() {
    Parameters.cpu.addIOInput(basePort, this);
    Parameters.cpu.addIOOutput(basePort, this);
    log.fine("Ports connected");
  }

  // disconnect from ports
  private void disconnect() {
    Parameters.cpu.removeIOInput(basePort, this);
    Parameters.cpu.removeIOOutput(basePort, this);
    log.fine("Ports disconnected");
  }

  /**
   * Reconnects ADC to a new base port
   *
   * @param basePort the new base port
   */
  public void reconnect(final int basePort) {
    assert (basePort >= 0) && (basePort < 0x100);
    if (this.basePort != basePort) {
      disconnect();
      this.basePort = basePort;
      connect();
      log.fine(String.format("ADC hardware reconnected to new base port %02x",
			     basePort));
    } else {
      log.finer("ADC hardware port reconnection not required");
    }
  }

  // for description see IOElement
  @Override
  public void portOutput(final int port, final int data) {
    log.finest(String.format("Port output: %02x -> (%02x)", data, port));
    assert port == basePort;
    if ((data >= 0) && (data < NUMBER_CHANNELS)) {
      channel = data;
    }
  }

  // for description see IOElement
  @Override
  public int portInput(final int port) {
    assert port == basePort;
    int r = (int)Math.round(channels[channel].getState() * 0xff);
    r = Util.limit(r, 0, 0xff);
    log.finest(String.format("Port input: (%02x) -> %02x", port, r));
    return r;
  }
}
