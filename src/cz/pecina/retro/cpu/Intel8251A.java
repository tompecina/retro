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

  // baud rate multipliers
  private static final int[] BRM = {1, 16, 64};

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

  // hunt mode
  private int hunt;

  // SYNDET direction
  private int syndetDir;

  // sync detect
  private int syndet;

  // break detect
  private int brkdet;

  // buffer registers
  private int tbr, rbr;

  // transmit flag
  private int txd;
  
  // transmit pin
  private final TxD txdPin = new TxD();
  
  // transmitter ready flag
  private int txrdy;
  
  // transmitter ready pin
  private final TxRDY txrdyPin = new TxRDY();
  
  // transmitter empty flag
  private int txempty;
  
  // transmitter empty pin
  private final TxEMPTY txemptyPin = new TxEMPTY();
  
  // transmitter clock flag
  private int txc;
  
  // /TxC pin
  private final TxC txcPin = new TxC();
  
  // transmitter shift register
  private int tsr, tsrLen;
  
  // receiver ready flag
  private int rxrdy;
  
  // receiver ready pin
  private final RxRDY rxrdyPin = new RxRDY();
  
  // receive flag
  private int rxd;
  
  // receive pin
  private final RxD rxdPin = new RxD();
  
  // receiver clock flag
  private int rxc;
  
  // /RxC pin
  private final RxC rxcPin = new RxC();
  
  // receiver shift register
  private int rsr, rsrLen;
  
  // sync/break pin
  private final SynBrkPin synBrkPin = new SynBrkPin();

  // transmitter clock countdown
  private int txcCountDown;
  
  // transmitter state
  private int tState;

  // receiver clock countdown
  private int rxcCountDown;
  
  // receiver state
  private int rState;

  // break counter
  private int brkCounter;
  
  // parity calculators
  private int tParity, rParity;

  // modem flags
  private int dsr, dtr, cts, rts;

  // modem pins
  private final DSR dsrPin = new DSR();
  private final DTR dtrPin = new DTR();
  private final CTS ctsPin = new CTS();
  private final RTS rtsPin = new RTS();

  // /DSR pin
  private class DSR extends IOPin {
    @Override
    public void notifyChange() {
      dsr = 1 - IONode.normalize(queryNode());
      log.finer("DSR: " + dsr);
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
      log.finer("CTS: " + cts);
    }
  }
  
  // /RTS pin
  private class RTS extends IOPin {
    @Override
    public int query() {
      return 1 - rts;
    }
  }
  
  // TxD pin
  private class TxD extends IOPin {
    @Override
    public int query() {
      log.finest("TxD: " + txd + " Break: " + sbrk);
      return txd & (1 - sbrk);
    }
  }
  
  // RxD pin
  private class RxD extends IOPin {
    @Override
    public void notifyChange() {
      rxd = IONode.normalize(queryNode());
      log.finest("RxD: " + rxd);
    }
  }
  
  // TxRDY pin
  private class TxRDY extends IOPin {
    @Override
    public int query() {
      return txrdy & cts & txen;
    }
  }
  
  // TxEMPTY pin
  private class TxEMPTY extends IOPin {
    @Override
    public int query() {
      return txempty;
    }
  }

  // get number of ticks to load count-down counter with
  private int getTicks() {
    assert (brf >= 0) && (brf < 3);
    switch (brf) {
      case 0:
	return 0;
      case 1:
	return 15;
      default:
	return 63;
    }
  }
  
  // get reduced number of ticks to load count-down counter with
  private int getHalfTicks() {
    assert (brf >= 0) && (brf < 3);
    switch (brf) {
      case 0:
	return 0;
      case 1:
	return 8;
      default:
	return 32;
    }
  }
  
  // /TxC pin
  private class TxC extends IOPin {
    @Override
    public void notifyChange() {
      final int newTxc = 1 - IONode.normalize(queryNode());
      if (txc != newTxc) {
	log.finest("Edge on /TxC detected");
	if ((newTxc == 1) && (txen == 1)) {  // falling edge of /TxC
	                                     // and transmitter enabled
	  log.finest("Falling edge on /TxC detected");
	  if (mode == 0) {  // sync mode
	    // ***
	  } else {  // async mode
	    if (txcCountDown == 0) {
	      log.finest("Countdown is zero, tState: " + tState);
	      switch (tState) {
		case 0:  // idle
		  break;
		case 1:  // start-bit
		  assert tsrLen > 0;
		  txd = 0;
		  txdPin.notifyChangeNode();
		  txcCountDown = getTicks();
		  tParity = 1 - ep;
		  tState++;
		  break;
		case 2:  // data
		  assert tsrLen > 0;
		  txd = tsr & 1;
		  txdPin.notifyChangeNode();
		  tParity ^= txd;
		  tsr >>= 1;
		  txcCountDown = getTicks();
		  if (--tsrLen == 0) {
		    tState = (pen == 1) ? 3 : 4;
		  }
		  break;
		case 3:  // parity
		  txd = tParity;
		  txdPin.notifyChangeNode();
		  txcCountDown = getTicks();
		  tState++;
		  break;
		case 4:  // stop-bit(s)
		  txd = 1;
		  txdPin.notifyChangeNode();
		  txcCountDown = getTicks();
		  if (sbits == 1) {
		    txcCountDown += getHalfTicks();
		  } else if (sbits == 2) {
		    txcCountDown += getTicks();
		  }
		  tState++;
		  break;
		case 5:  // finished
		  if (txrdy == 1) {
		    txempty = 1;
		    txemptyPin.notifyChangeNode();
		    tState = 0;
		  } else {
		    tsr = tbr;
		    tsrLen = clen;
		    tbr = 0;
		    txrdy = 1;
		    txrdyPin.notifyChangeNode();
		    txd = 0;
		    txdPin.notifyChangeNode();
		    txcCountDown = getTicks();
		    tParity = 1 - ep;
		    tState = 2;
		  }
		  log.finer("Byte transmitted");
		  break;
	      }
	    } else {
	      txcCountDown--;
	    }
	  }
	}
	txc = newTxc;
      }
    }
  }
  
  // RxRDY pin
  private class RxRDY extends IOPin {
    @Override
    public int query() {
      return rxrdy;
    }
  }
  
  // /RxC pin
  private class RxC extends IOPin {
    @Override
    public void notifyChange() {
      final int newRxc = 1 - IONode.normalize(queryNode());
      if (rxc != newRxc) {
	if ((newRxc == 0) && (rxen == 1)) {  // rising edge of /RxC
	                                     // and receiver enabled
	  log.finest("Rising edge on /RxC detected");
	  if (mode == 0) {  // sync mode
	    // ***
	  } else {  // async mode
	    if (rxcCountDown == 0) {
	      log.finest("Countdown is zero, rState: " + rState);
	      switch (rState) {
		case 0:  // scaning for start-bit
		  if (rxd == 0) {
		    if (brf == 0) {
		      rxcCountDown = getTicks();
		      rsr = 0;
		      rsrLen = 0;
		      rParity = 1 - ep;
		      rState = 2;
		    } else {
		      rxcCountDown = getHalfTicks();
		      rState++;
		    }
		  }
		  break;
		case 1:  // start-bit
		  if (rxd == 1) {
		    rState = 0;
		  } else {
		    rsr = 0;
		    rsrLen = 0;
		    rParity = 1 - ep;
		    rxcCountDown = getTicks();
		    rState++;
		  }
		  break;
		case 2:  // data bits
		  rsr = (rsr | (rxd << clen)) >> 1;
		  rParity ^= rxd;
		  rxcCountDown = getTicks();
		  if (++rsrLen == clen) {
		    rState = (pen == 1) ? 3 : 4;
		  }
		  break;
		case 3:  // parity bit
		  if (rxd != rParity) {
		    pe = 1;
		  }
		  rxcCountDown = getTicks();
		  rState++;
		  break;
		case 4:  // stop-bit(s)
		  if (rxd == 0) {
		    fe = 1;
		  }
		  if (rxrdy == 1) {
		    oe = 1;
		  }
		  rbr = rsr;
		  rsr = 0;
		  rsrLen = 0;
		  rxrdy = 1;
		  rxrdyPin.notifyChangeNode();
		  rState = 0;
		  log.finer("Byte received");
		  break;
	      }
	    } else {
	      rxcCountDown--;
	    }
	    if (rxd == 0) {
	      if (brkCounter > (2 * BRM[brf] * (clen + pe + 2))) {
		brkdet = 1;
	      } else {
		brkCounter++;
	      }
	    } else {
	      brkdet = 0;
	      brkCounter = 0;
	    }
	  }
	}
	rxc = newRxc;
      }
    }
  }
  
  // sync/break pin
  private class SynBrkPin extends IOPin {
    @Override
    public void notifyChange() {
      if ((mode == 0) && (esd == 1)) {
	syndet = IONode.normalize(queryNode());
      }
    }
    @Override
    public int query() {
      if (mode == 1) {
	return brkdet;
      } else if (esd == 1) {
	return IONode.HIGH_IMPEDANCE;
      } else {
	return syndet;
      }
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

  /**
   * Gets the TxD pin.
   *
   * @return the TxD pin
   */
  public IOPin getTxdPin() {
    return txdPin;
  }

  /**
   * Gets the TxRDY pin.
   *
   * @return the TxRDY pin
   */
  public IOPin getTxrdyPin() {
    return txrdyPin;
  }

  /**
   * Gets the TxEMPTY pin.
   *
   * @return the TxEMPTY pin
   */
  public IOPin getTxemptyPin() {
    return txemptyPin;
  }

  /**
   * Gets the /TxC pin.
   *
   * @return the /TxC pin
   */
  public IOPin getTxcPin() {
    return txcPin;
  }

  /**
   * Gets the RxRDY pin.
   *
   * @return the RxRDY pin
   */
  public IOPin getRxrdyPin() {
    return rxrdyPin;
  }

  /**
   * Gets the RxD pin.
   *
   * @return the RxD pin
   */
  public IOPin getRxdPin() {
    return rxdPin;
  }

  /**
   * Gets the /RxC pin.
   *
   * @return the /RxC pin
   */
  public IOPin getRxcPin() {
    return rxcPin;
  }

  // notify on all output pins
  private void notifyAllPins() {
    log.finer("Notifying all output pins");
    dtrPin.notifyChangeNode();
    rtsPin.notifyChangeNode();
    txdPin.notifyChangeNode();
    txrdyPin.notifyChangeNode();
    rxrdyPin.notifyChangeNode();
    synBrkPin.notifyChangeNode();
    log.finest("All output pins notified");
  }

  // update internal flags from all input pins
  public void update() {
    log.finer("Updating from all input pins");
    ctsPin.notifyChange();
    dsrPin.notifyChange();
    rxdPin.notifyChange();
    synBrkPin.notifyChange();
    log.finest("Update from all input pins completed");
  }

  /**
   * Resets the device.
   */
  @Override
  public void reset() {
    state = 0;
    pe = oe = fe = 0;
    sbrk = hunt = 0;
    dtr = rts = 0;
    syndet = brkdet = 0;
    syndetDir = INPUT;
    tbr = rbr = 0;
    rxrdy = 0;
    txd = txempty = 1;
    txrdy = 1;
    tsr = rsr = 0;
    tsrLen = rsrLen = 0;
    txc = 1 - IONode.normalize(txcPin.queryNode());
    rxc = 1 - IONode.normalize(rxcPin.queryNode());
    txcCountDown = rxcCountDown = brkCounter = 0;
    tState = rState = 0;
    tParity = rParity = 0;
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
	  return String.format("%02x", sync1);
	}
	@Override
	public void processValue(final String value) {
	  sync1 = Integer.parseInt(value, 16);
	  log.finer(String.format("Sync character 1: 0x%02", sync1));
	}
      });
    add(new Register("SYNC2") {
	@Override
	public String getValue() {
	  return String.format("%02x", sync2);
	}
	@Override
	public void processValue(final String value) {
	  sync2 = Integer.parseInt(value, 16);
	  log.finer(String.format("Sync character 2: 0x%02", sync2));
	}
      });
    add(new Register("TXEN") {
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
    add(new Register("RXEN") {
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
    add(new Register("HUNT") {
	@Override
	public String getValue() {
	  return String.valueOf(hunt);
	}
	@Override
	public void processValue(final String value) {
	  hunt = Integer.parseInt(value);
	  log.finer("Hunt mode: " + hunt);
	}
      });
    add(new Register("SYNDET_DIR") {
	@Override
	public String getValue() {
	  return String.valueOf(syndetDir);
	}
	@Override
	public void processValue(final String value) {
	  syndetDir = Integer.parseInt(value);
	  log.finer("SYNDET dir: " + syndetDir);
	}
      });
    add(new Register("SYNDET") {
	@Override
	public String getValue() {
	  return String.valueOf(syndet);
	}
	@Override
	public void processValue(final String value) {
	  syndet = Integer.parseInt(value);
	  log.finer("Sync detect: " + syndet);
	}
      });
    add(new Register("BRKDET") {
	@Override
	public String getValue() {
	  return String.valueOf(brkdet);
	}
	@Override
	public void processValue(final String value) {
	  brkdet = Integer.parseInt(value);
	  log.finer("Sync detect: " + brkdet);
	}
      });
    add(new Register("PE") {
	@Override
	public String getValue() {
	  return String.valueOf(pe);
	}
	@Override
	public void processValue(final String value) {
	  pe = Integer.parseInt(value);
	  log.finer("Parity error: " + pe);
	}
      });
    add(new Register("OE") {
	@Override
	public String getValue() {
	  return String.valueOf(oe);
	}
	@Override
	public void processValue(final String value) {
	  oe = Integer.parseInt(value);
	  log.finer("Overrun error: " + oe);
	}
      });
    add(new Register("FE") {
	@Override
	public String getValue() {
	  return String.valueOf(fe);
	}
	@Override
	public void processValue(final String value) {
	  fe = Integer.parseInt(value);
	  log.finer("Framing error: " + fe);
	}
      });
    add(new Register("TBR") {
	@Override
	public String getValue() {
	  return String.format("%02x", tbr);
	}
	@Override
	public void processValue(final String value) {
	  tbr = Integer.parseInt(value, 16);
	  log.finer(String.format("Transmit buffer register: 0x%02", tbr));
	}
      });
    add(new Register("RBR") {
	@Override
	public String getValue() {
	  return String.format("%02x", rbr);
	}
	@Override
	public void processValue(final String value) {
	  rbr = Integer.parseInt(value, 16);
	  log.finer(String.format("Receive buffer register: 0x%02", rbr));
	}
      });
    add(new Register("RXRDY") {
	@Override
	public String getValue() {
	  return String.valueOf(rxrdy);
	}
	@Override
	public void processValue(final String value) {
	  rxrdy = Integer.parseInt(value);
	  log.finer("RxRDY: " + rxrdy);
	}
      });
    add(new Register("TXD") {
	@Override
	public String getValue() {
	  return String.valueOf(txd);
	}
	@Override
	public void processValue(final String value) {
	  txd = Integer.parseInt(value);
	  log.finer("TxD: " + txd);
	}
      });
    add(new Register("TXRDY") {
	@Override
	public String getValue() {
	  return String.valueOf(txrdy);
	}
	@Override
	public void processValue(final String value) {
	  txrdy = Integer.parseInt(value);
	  log.finer("TxRDY: " + txrdy);
	}
      });
    add(new Register("TXEMPTY") {
	@Override
	public String getValue() {
	  return String.valueOf(txempty);
	}
	@Override
	public void processValue(final String value) {
	  txempty = Integer.parseInt(value);
	  log.finer("TxEMPTY: " + txempty);
	}
      });
    add(new Register("TXC") {
	@Override
	public String getValue() {
	  return String.valueOf(txc);
	}
	@Override
	public void processValue(final String value) {
	  txc = Integer.parseInt(value);
	  log.finer("TxC: " + txc);
	}
      });
    add(new Register("RXRDY") {
	@Override
	public String getValue() {
	  return String.valueOf(rxrdy);
	}
	@Override
	public void processValue(final String value) {
	  rxrdy = Integer.parseInt(value);
	  log.finer("RxRDY: " + rxrdy);
	}
      });
    add(new Register("RXC") {
	@Override
	public String getValue() {
	  return String.valueOf(rxc);
	}
	@Override
	public void processValue(final String value) {
	  rxc = Integer.parseInt(value);
	  log.finer("RxC: " + rxc);
	}
      });
    add(new Register("TSR") {
	@Override
	public String getValue() {
	  return String.format("%02x", tsr);
	}
	@Override
	public void processValue(final String value) {
	  tsr = Integer.parseInt(value, 16);
	  log.finer(String.format("Transmitter shift register: 0x%02", tsr));
	}
      });
    add(new Register("TSR_LENGTH") {
	@Override
	public String getValue() {
	  return String.valueOf(tsrLen);
	}
	@Override
	public void processValue(final String value) {
	  tsrLen = Integer.parseInt(value);
	  log.finer("Transmitter shift register length: " + tsrLen);
	}
      });
    add(new Register("RSR") {
	@Override
	public String getValue() {
	  return String.format("%02x", rsr);
	}
	@Override
	public void processValue(final String value) {
	  rsr = Integer.parseInt(value, 16);
	  log.finer(String.format("Receiver shift regisrer: 0x%02", rsr));
	}
      });
    add(new Register("RSR_LENGTH") {
	@Override
	public String getValue() {
	  return String.valueOf(rsrLen);
	}
	@Override
	public void processValue(final String value) {
	  rsrLen = Integer.parseInt(value);
	  log.finer("Receiver shift register length: " + rsrLen);
	}
      });
    add(new Register("TXS") {
	@Override
	public String getValue() {
	  return String.valueOf(tState);
	}
	@Override
	public void processValue(final String value) {
	  tState = Integer.parseInt(value);
	  log.finer("Transmitter state: " + tState);
	}
      });
    add(new Register("RXS") {
	@Override
	public String getValue() {
	  return String.valueOf(rState);
	}
	@Override
	public void processValue(final String value) {
	  rState = Integer.parseInt(value);
	  log.finer("Receiver state: " + rState);
	}
      });
    add(new Register("TXC_COUNTDOWN") {
	@Override
	public String getValue() {
	  return String.valueOf(txcCountDown);
	}
	@Override
	public void processValue(final String value) {
	  txcCountDown = Integer.parseInt(value);
	  log.finer("Transmitter clock countdown: " + txcCountDown);
	}
      });
    add(new Register("RXC_COUNTDOWN") {
	@Override
	public String getValue() {
	  return String.valueOf(rxcCountDown);
	}
	@Override
	public void processValue(final String value) {
	  rxcCountDown = Integer.parseInt(value);
	  log.finer("Receiver clock countdown: " + rxcCountDown);
	}
      });
    add(new Register("BRK_COUNTER") {
	@Override
	public String getValue() {
	  return String.valueOf(brkCounter);
	}
	@Override
	public void processValue(final String value) {
	  brkCounter = Integer.parseInt(value);
	  log.finer("Break counter: " + brkCounter);
	}
      });
    add(new Register("TXP") {
	@Override
	public String getValue() {
	  return String.valueOf(tParity);
	}
	@Override
	public void processValue(final String value) {
	  tParity = Integer.parseInt(value);
	  log.finer("Transmitter parity calculator: " + tParity);
	}
      });
    add(new Register("RXP") {
	@Override
	public String getValue() {
	  return String.valueOf(rParity);
	}
	@Override
	public void processValue(final String value) {
	  rParity = Integer.parseInt(value);
	  log.finer("Receiver parity calculator: " + rParity);
	}
      });

    reset();
    update();
    log.fine("New Intel 8251A creation completed, name: " + name);
  }

  // for description see Device
  @Override
  public void postUnmarshal() {
    notifyAllPins();
    update();
    log.fine("Post-unmarshal on 8251A completed");
  }

  // for description see IOElement
  @Override
  public int portInput(final int port) {
    if ((port & 1) == 0) {
      // data
      rxrdy = 0;
      log.finer(String.format("Data input: 0x%02x", rbr));
      return rbr;
    } else {
      // status
      final int status = txrdy | (rxrdy << 1) | (txempty << 2) | (pe << 3) |
	(oe << 4) | (fe << 5) | (((mode == 0) ? syndet : brkdet) << 6) |
        (dsr << 7);
      log.finest(String.format("Status: 0x%02x", status));
      return status;
    }
  }

  // for description see IOElement
  @Override
  public void portOutput(final int port, int data) {
    if ((port & 1) == 0) {
      // data
      log.finer(String.format("Data output: 0x%02x", data));
      if ((txen & cts) == 1) {
	if (txempty == 1) {  // put the data directy in the shift-register
	  tsr = data;
	  tsrLen = clen;
	  tbr = 0;
	  txempty = 0;
	  txemptyPin.notifyChangeNode();
	  tState = 1;
	  log.finest("Data put in TSR");
	} else {  // put the data in the buffer
	  tbr = data;
	  txrdy = 0;
	  txrdyPin.notifyChangeNode();
	  log.finest("Data put in TBR");
	}
      }
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
	    log.finer("Baud rate factor set to: " + brf + " (" + BRM[brf] + ")");
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
	    if (txen == 0) {
	      txrdy = 0;
	      tbr = tsr = tsrLen = txcCountDown = 0;
	      tState = 0;
	    }
	    dtr = (data >> 1) & 1;
	    log.finer("DTR set to: " + dtr);
	    rxen = (data >> 2) & 1;
	    log.finer("Receive enable: " + rxen);
	    if (rxen == 0) {
	      rxrdy = 0;
	      rbr = rsr = rsrLen = rxcCountDown = 0;
	      rState = 0;
	    }
	    sbrk = (data >> 3) & 1;
	    log.finer("Send break: " + sbrk);
	    if (((data >> 4) & 1) == 1) {  // error reset
	      log.finer("Error reset");
	      pe = oe = fe = 0;
	    }
	    rts = (data >> 5) & 1;
	    log.finer("RTS set to: " + rts);
	    hunt = (data >> 7) & 1;
	    log.finer("Enter hunt mode: " + hunt);
	    notifyAllPins();
	    update();
	  }
	  break;
      }
    }
  }
}
