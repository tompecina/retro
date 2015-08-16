/* Intel8251A.java
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
 * Intel 8251A Programmable Communication Interface (USART).
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Intel8251A extends Device implements IOElement {

  // dynamic logger, per device
  private Logger log;

  // main operating state
  private int state;  // 0 = idle, waiting for instruction
                      // 1 = waiting for SYNC1
                      // 2 = waiting for SYNC2
                      // 3 = normal operation
  
  // operating mode
  private int mode;  // 0 = synchronous
                     // 1 = asynchronous

  // baud rate factor
  private int brf;  // 0 = 1x
                    // 1 = 16x
                    // 2 = 64x
  
  // parity enable
  private int pen;
  
  // parity type
  private int ep;  // 0 = odd
                   // 1 = even
  
  // character length, 5-8 bits
  private int clen = 5;
  
  // number of stop bits
  private int sbits;  // 0 = 1 stop bit
                      // 1 = 1 1/2 stop bits
                      // 2 = 2 stop bits
  
  // external sync detect
  private int esd;  // 0 = SYNDET is output
                    // 1 = SYNDET is input
  
  // single character sync
  private int scs;  // 0 = double sync character
                    // 1 = single sync character

  // sync characters
  private int sync1, sync2;

  // transmit enable
  private int txen;

  // receive enable
  private int rxen;

  // send break
  private int sbrk;

  // error flags
  private int pe, oe, fe;

  // enter hunt mode;
  private int eh;

  // modem signals
  private final DSR dsrPin = new DSR();
  private final DTR dtrPin = new DTR();
  private final CTS ctsPin = new CTS();
  private final RTS rtsPin = new RTS();

  // modem flags
  private int dsr, dtr, cts, rts;

  // /DSR pin
  private class DSR extends IOPin {
    @Override
    public void notifyChange() {
      dsr = 1 - IONode.normalize(queryNode());
    }
  }
  
  // /DTR pin
  private class DTR extends IOPin {
    @Override
    public int query() {
      return 1 - dtr;
    }
  }
  
  // /CTS pin
  private class CTS extends IOPin {
    @Override
    public void notifyChange() {
      cts = 1 - IONode.normalize(queryNode());
    }
  }
  
  // /RTS pin
  private class RTS extends IOPin {
    @Override
    public int query() {
      return 1 - rts;
    }
  }
  
  /**
   * Gets the /DSR pin.
   *
   * @return the /DSR pin
   */
  public IOPin getDsrPin() {
    return dsrPin;
  }

  /**
   * Gets the /DTR pin.
   *
   * @return the /DTR pin
   */
  public IOPin getDtrPpin() {
    return dtrPin;
  }

  /**
   * Gets the /CTS pin.
   *
   * @return the /CTS pin
   */
  public IOPin getCtsPin() {
    return ctsPin;
  }

  /**
   * Gets the /RTS pin.
   *
   * @return the /RTS pin
   */
  public IOPin getRtsPin() {
    return rtsPin;
  }

  // notify on all pins
  private void notifyAllPins() {
    dtrPin.notifyChangeNode();
    rtsPin.notifyChangeNode();
  }

  /**
   * Resets the device.
   */
  @Override
  public void reset() {
    state = 0;
    pe = oe = fe = 0;
    sbrk = eh = 0;
    dtr = rts = 0;
    notifyAllPins();
    log.finer("USART reset");
  }

  /**
   * Main constructor.
   *
   * @param name device name
   */
  public Intel8251A(final String name) {
    super(name);
    log = Logger.getLogger(getClass().getName() + "." + name);
    log.fine("New Intel 8251A creation started, name: " + name);

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
    add(new Register("MODE") {
	@Override
	public String getValue() {
	  return String.valueOf(mode);
	}
	@Override
	public void processValue(final String value) {
	  mode = Integer.parseInt(value);
	  log.finer("Mode set to: " + mode +
		    ((mode == 0) ? " (sync)" : " (async)"));
	}
      });
    add(new Register("BRF") {
	@Override
	public String getValue() {
	  return String.valueOf(brf);
	}
	@Override
	public void processValue(final String value) {
	  brf = Integer.parseInt(value);
	  log.finer("Baud rate factor set to: " + brf +
		    (new String[] {" (1x)", " (16x)", " (64x)"})[brf]);
	}
      });
    add(new Register("CLEN") {
	@Override
	public String getValue() {
	  return String.valueOf(clen);
	}
	@Override
	public void processValue(final String value) {
	  clen = Integer.parseInt(value);
	  log.finer("Character length set to: " + clen);
	}
      });
    add(new Register("PEN") {
	@Override
	public String getValue() {
	  return String.valueOf(pen);
	}
	@Override
	public void processValue(final String value) {
	  pen = Integer.parseInt(value);
	  log.finer("Parity enable set to: " + pen);
	}
      });
    add(new Register("EP") {
	@Override
	public String getValue() {
	  return String.valueOf(ep);
	}
	@Override
	public void processValue(final String value) {
	  ep = Integer.parseInt(value);
	  log.finer("Parity model set to: " + ep +
		    ((ep == 0) ? " (odd)" : " (even)"));
	}
      });
    add(new Register("SBITS") {
	@Override
	public String getValue() {
	  return String.valueOf(sbits);
	}
	@Override
	public void processValue(final String value) {
	  sbits = Integer.parseInt(value);
	  log.finer("Number of stop bits set to: " +
		    (new String[] {"1", "1 1/2", "2"})[sbits]);
	}
      });
    add(new Register("ESD") {
	@Override
	public String getValue() {
	  return String.valueOf(esd);
	}
	@Override
	public void processValue(final String value) {
	  esd = Integer.parseInt(value);
	  log.finer("External sync detect: " + esd);
	}
      });
    add(new Register("SCS") {
	@Override
	public String getValue() {
	  return String.valueOf(scs);
	}
	@Override
	public void processValue(final String value) {
	  scs = Integer.parseInt(value);
	  log.finer("Single character sync: " + scs);
	}
      });
    add(new Register("SYNC1") {
	@Override
	public String getValue() {
	  return String.valueOf(sync1);
	}
	@Override
	public void processValue(final String value) {
	  sync1 = Integer.parseInt(value);
	  log.finer("Sync character 1: " + sync1);
	}
      });
    add(new Register("SYNC2") {
	@Override
	public String getValue() {
	  return String.valueOf(sync2);
	}
	@Override
	public void processValue(final String value) {
	  sync2 = Integer.parseInt(value);
	  log.finer("Sync character 2: " + sync2);
	}
      });
    add(new Register("TxEN") {
	@Override
	public String getValue() {
	  return String.valueOf(txen);
	}
	@Override
	public void processValue(final String value) {
	  txen = Integer.parseInt(value);
	  log.finer("Transmit enable: " + txen);
	}
      });
     add(new Register("DTR") {
	@Override
	public String getValue() {
	  return String.valueOf(dtr);
	}
	@Override
	public void processValue(final String value) {
	  dtr = Integer.parseInt(value);
	  log.finer("DTR set to: " + dtr);
	}
      });
    add(new Register("RxEN") {
	@Override
	public String getValue() {
	  return String.valueOf(rxen);
	}
	@Override
	public void processValue(final String value) {
	  rxen = Integer.parseInt(value);
	  log.finer("Receive enable: " + rxen);
	}
      });
    add(new Register("SBRK") {
	@Override
	public String getValue() {
	  return String.valueOf(sbrk);
	}
	@Override
	public void processValue(final String value) {
	  sbrk = Integer.parseInt(value);
	  log.finer("Send break: " + sbrk);
	}
      });
    add(new Register("RTS") {
	@Override
	public String getValue() {
	  return String.valueOf(rts);
	}
	@Override
	public void processValue(final String value) {
	  rts = Integer.parseInt(value);
	  log.finer("RTS set to: " + rts);
	}
      });
    add(new Register("EH") {
	@Override
	public String getValue() {
	  return String.valueOf(eh);
	}
	@Override
	public void processValue(final String value) {
	  eh = Integer.parseInt(value);
	  log.finer("Hunt mode: " + eh);
	}
      });

    reset();
    log.fine("New Intel 8251A creation completed, name: " + name);
  }

  // for description see Device
  @Override
  public void postUnmarshal() {
    notifyAllPins();
    log.fine("Post-unmarshal on 8251A completed");
  }

  // for description see IOElement
  @Override
  public int portInput(final int port) {
    return 0x00;
  }

  // for description see IOElement
  @Override
  public void portOutput(final int port, int data) {
    if ((port & 1) == 0) {
      // data
      log.finest(String.format("Data: 0x%02x", data));
    } else {
      // control
      switch (state) {
	case 0:  // mode instruction
	  log.fine(String.format("Mode instruction: 0x%02x", data));
	  mode = (data | (data >> 1)) & 1;
	  log.finer("Mode set to: " + mode +
		    ((mode == 0) ? " (sync)" : " (async)"));
	  clen = ((data >> 2) & 0x03) + 5;
	  log.finer("Character length set to: " + clen);
	  pen = (data >> 4) & 1;
	  log.finer("Parity enable set to: " + pen);
	  ep = (data >> 5) & 1;
	  log.finer("Parity model set to: " + ep +
		    ((ep == 0) ? " (odd)" : " (even)"));
	  if (mode == 1) {
	    brf = (data & 0x03) - 1;
	    log.finer("Baud rate factor set to: " + brf +
		      (new String[] {" (1x)", " (16x)", " (64x)"})[brf]);
	    sbits = (data >> 6) & 0x03;
	    if (sbits-- == 0) {
	      log.fine("Illegal number of stop bits");
	      sbits = 0;
	    }
	    log.finer("Number of stop bits set to: " +
		      (new String[] {"1", "1 1/2", "2"})[sbits]);
	  } else {
	    esd = (data >> 6) & 1;
	    log.finer("External sync detect: " + esd);
	    scs = (data >> 7) & 1;
	    log.finer("Single character sync: " + scs);
	  }
	  if ((mode == 1) || (esd == 1)) {
	    state = 3;
	  } else {
	    state++;
	  }
	  break;
	case 1:  // sync char 1
	  log.fine(String.format("Sync 1: 0x%02x", data));
	  sync1 = data;
	  state += scs + 1;
	  break;
	case 2:  // sync char 2
	  log.fine(String.format("Sync 2: 0x%02x", data));
	  sync2 = data;
	  state++;
	  break;
	default:  // command instruction
	  log.fine(String.format("Command instruction: 0x%02x", data));
	  if (((data >> 6) & 1) == 1) {  // internal reset
	    reset();
	    log.fine("Internal reset");
	  } else {
	    txen = data & 1;
	    log.finer("Transmit enable: " + txen);
	    dtr = (data >> 1) & 1;
	    log.finer("DTR set to: " + dtr);
	    rxen = (data >> 2) & 1;
	    log.finer("Receive enable: " + rxen);
	    sbrk = (data >> 3) & 1;
	    log.finer("Send break: " + sbrk);
	    if (((data >> 4) & 1) == 1) {  // error reset
	      log.finer("Error reset");
	      pe = oe = fe = 0;
	    }
	    rts = (data >> 5) & 1;
	    log.finer("RTS set to: " + rts);
	    eh = (data >> 7) & 1;
	    log.finer("Hunt mode: " + eh);
	    notifyAllPins();
	  }
	  break;
      }
    }
  }
}
