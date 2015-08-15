/* Intel8251.java
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

package cz.pecina.retro.cpu;

import java.util.logging.Logger;

/**
 * Intel 8251 Programmable Communication Interface (USART).
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Intel8251 extends Device implements IOElement {

  // dynamic logger, per device
  private Logger log;

  // main operating state
  private int state;  // 0 = idle, waiting for instruction
                      // 1 = waiting for SYNC1
                      // 2 = waiting for SYNC2
                      // 3 = normal operation
  
  // modem signals
  private final DSR DSRp = new DSR();
  private final DTR DTRp = new DTR();
  private final CTS CTSp = new CTS();
  private final RTS RTSp = new RTS();

  // modem flags
  private int DSRf, DTRf, CTSf, RTSf;

  // /DSR pin
  private class DSR extends IOPin {
    @Override
    public void notifyChange() {
      DSRf = IONode.normalize(queryNode());
    }
  }
  
  // /DTR pin
  private class DTR extends IOPin {
    @Override
    public int query() {
      return DTRf;
    }
  }
  
  // /CTS pin
  private class CTS extends IOPin {
    @Override
    public void notifyChange() {
      CTSf = IONode.normalize(queryNode());
    }
  }
  
  // /RTS pin
  private class RTS extends IOPin {
    @Override
    public int query() {
      return RTSf;
    }
  }
  
  /**
   * Gets the /DSR pin.
   *
   * @return the /DSR pin
   */
  public IOPin getDSRpin() {
    return DSRp;
  }

  /**
   * Gets the /DTR pin.
   *
   * @return the /DTR pin
   */
  public IOPin getDTRpin() {
    return DTRp;
  }

  /**
   * Gets the /CTS pin.
   *
   * @return the /CTS pin
   */
  public IOPin getCTSpin() {
    return CTSp;
  }

  /**
   * Gets the /RTS pin.
   *
   * @return the /RTS pin
   */
  public IOPin getRTSpin() {
    return RTSp;
  }

  // clear all internal registers
  private void clearRegisters() {
  }

  // notify on all pins
  private void notifyAllPins() {
    DTRp.notifyChangeNode();
    RTSp.notifyChangeNode();
  }

  /**
   * Resets the device.  All I/O pins are configured as inputs, Mode 0
   * is activated on both A and B.
   */
  @Override
  public void reset() {
    notifyAllPins();
    log.finer(String.format("%s: reset", name));
  }

  /**
   * Main constructor.
   *
   * @param name device name
   */
  public Intel8251(final String name) {
    super(name);
    log = Logger.getLogger(getClass().getName() + "." + name);
    log.fine("New Intel 8251 creation started, name: " + name);

    add(new Register("STATE") {
	@Override
	public String getValue() {
	  return String.valueOf(state);
	}
	@Override
	public void processValue(final String value) {
	  state = Integer.parseInt(value);
	  log.finer("State set to: " + state);
	}
      });
    add(new Register("nDTR") {
	@Override
	public String getValue() {
	  return String.valueOf(DTRf);
	}
	@Override
	public void processValue(final String value) {
	  DTRf = Integer.parseInt(value);
	  log.finer("/DTR set to: " + DTRf);
	}
      });
    add(new Register("nRTS") {
	@Override
	public String getValue() {
	  return String.valueOf(RTSf);
	}
	@Override
	public void processValue(final String value) {
	  RTSf = Integer.parseInt(value);
	  log.finer("/RTS set to: " + RTSf);
	}
      });

    reset();
    log.fine("New Intel 8251 creation completed, name: " + name);
  }

  // for description see Device
  @Override
  public void postUnmarshal() {
    notifyAllPins();
    log.fine("Post-unmarshal on 8251 completed");
  }

  // for description see IOElement
  @Override
  public int portInput(final int port) {
    return 0x00;
  }

  // for description see IOElement
  @Override
  public void portOutput(final int port, int data) {
  }
}
