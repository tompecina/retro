/* ROMModuleHardware.java
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

import cz.pecina.retro.cpu.Intel8255A;
import cz.pecina.retro.cpu.IOPin;
import cz.pecina.retro.cpu.IONode;

/**
 * Hardware of the Tesla PMD 85 pluggable ROM module.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class ROMModuleHardware {

  // static logger
  private static final Logger log =
    Logger.getLogger(ROMModuleHardware.class.getName());

  // the memory object
  private PMDMemory memory;

  // the PIO
  private Intel8255A pio = new Intel8255A("ROM_MODULE_PIO");

  // address pins
  private IOPin[] addressPins = new IOPin[16];

  // data pins
  private DataPin[] dataPins = new DataPin[8];

  /**
   * Creates a new ROM module hardware object.
   *
   * @param computerHardware the computer hardware object
   */
  public ROMModuleHardware(final ComputerHardware computerHardware) {
    log.fine("ROM module creation started");
    assert computerHardware != null;

    memory = computerHardware.getMemory();

    // register PIO
    computerHardware.getHardware().add(pio);

    // set up address pins
    for (int i = 0; i < 16; i++) {
      addressPins[i] = new IOPin();
      new IONode().add(pio.getPin(8 + i)).add(addressPins[i]);
    }
    
    // set up data pins
    for (int i = 0; i < 8; i++) {
      dataPins[i] = new DataPin(i);
      new IONode().add(pio.getPin(i)).add(dataPins[i]);
    }
    
    log.finer("ROM module set up");
  }

  /**
   * Gets the PIO.
   */
  public Intel8255A getPIO() {
    return pio;
  }

  // data pins
  private class DataPin extends IOPin {
    private int mask;

    private DataPin(final int n) {
      super();
      assert (n >= 0) && (n < 8);
      mask = 1 << n;
    }

    @Override
    public int query() {
      int a = 0;
      for (int i = 15; i >= 0; i--) {
	a = (a << 1) | (addressPins[i].queryNode() & 1);
      }
      return (a < (memory.getSizeRMM() * 0x400)) ?
	     (memory.getRMM()[a] & mask) :
	     0xff;
    }
  }
}
