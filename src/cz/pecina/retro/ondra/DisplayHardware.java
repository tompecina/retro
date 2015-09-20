/* DisplayHardware.java
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

package cz.pecina.retro.ondra;

import java.util.logging.Logger;

import cz.pecina.retro.cpu.Device;
import cz.pecina.retro.cpu.IOElement;
import cz.pecina.retro.cpu.IONode;
import cz.pecina.retro.cpu.IOPin;

/**
 * Display hardware of the Tesla Ondra SPO 186 computer.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class DisplayHardware extends Device implements IOElement {

  // static logger
  private static final Logger log =
    Logger.getLogger(DisplayHardware.class.getName());

  // the display
  private Display display;

  // the video enable flag
  private boolean enableFlag;

  // the address
  private int address;
  
  // number of displayed scanlines
  private int scanLines;

  // the Enable pin
  private EnablePin enablePin = new EnablePin();

  // the address pins
  private AddressPin[] addressPins = new AddressPin[2];
    
  /**
   * Creates a new display hardware object.
   *
   * @param name             the name of the device
   * @param computerHardware the computer hardware object
   */
  public DisplayHardware(final String name, final ComputerHardware computerHardware) {
    super(name);
    log.fine("Display hardware creation started");
    assert computerHardware != null;
    display = new Display(computerHardware, this);
    for (int i = 0; i < 2; i++) {
      addressPins[i] = new AddressPin(i);
    }
    log.finer("Display hardware creation completed");
  }

  /**
   * Gets the display.
   */
  public Display getDisplay() {
    return display;
  }

  /**
   * Refreshes the display.
   */
  public void refresh() {
    display.refresh();
    log.finer("Display refreshed");
  }
  
  // for description see IOElement
  @Override
  public int portInput(final int port) {
    if (address == 0) {
      final int newScanLines = ((port << 1) & 0xff) | ((port >> 7) & 1);
      if (newScanLines != scanLines) {
	scanLines = newScanLines;
	log.finer("Number of scan lines set to: " + scanLines);
	display.repaint();
      }
    }
    return 0xff;
  }

  // Enable pin
  private class EnablePin extends IOPin {

    private EnablePin() {
      super();
    }

    @Override
    public void notifyChange() {
      final boolean newEnableFlag = (queryNode() != 0);
      if (newEnableFlag != enableFlag) {
	enableFlag = newEnableFlag;
	display.repaint();
	log.finer("enableFlag: " + enableFlag);
      }
    }
  }

  // the address pin
  private class AddressPin extends IOPin {

    private int n;
    
    private AddressPin(final int n) {
      super();
      assert (n >= 0) && (n < 2);
      this.n = n;
    }

    @Override
    public void notifyChange() {
      address = (address & ~(1 << n)) | (IONode.normalize(queryNode()) << n);
      log.finer("Address: " + address);
    }
  }

  /**
   * Gets the Enable pin.
   *
   * @return the pin object
   */
  public IOPin getEnablePin() {
    return enablePin;
  }

  /**
   * Gets the address pin.
   *
   * @param  the pin number
   * @return the pin object
   */
  public IOPin getAddressPin(final int n) {
    return addressPins[n];
  }

  /**
   * Gets the enable flag.
   *
   * @return the enable flag
   */
  public boolean getEnableFlag() {
    return enableFlag;
  }

  /**
   * Gets the number of visible scan lines.
   *
   * @return the number of visible scan lines
   */
  public int getScanLines() {
    return scanLines;
  }

  // for description see Device
  @Override
  public void reset() {
    scanLines = 0;
    enablePin.notifyChange();
    addressPins[0].notifyChange();
    addressPins[1].notifyChange();
  }
}
