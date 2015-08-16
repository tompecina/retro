/* Intel8255A.java
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
 * Intel 8255A Programmable Peripheral Interface.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Intel8255A extends Device implements IOElement {

  // dynamic logger, per device
  private Logger log;

  // ports
  private static final int PORT_A = 0;
  private static final int PORT_B = 1;
  private static final int PORT_C = 2;
  private static final int CONTROL_PORT = 3;

  // Port C pins
  private static final int PC0 = 16;
  private static final int PC1 = 17;
  private static final int PC2 = 18;
  private static final int PC3 = 19;
  private static final int PC4 = 20;
  private static final int PC5 = 21;
  private static final int PC6 = 22;
  private static final int PC7 = 23;

  // Mode 1 control signals
  private static final int IN_INTR_A = PC3;
  private static final int IN_IBF_A = PC5;
  private static final int IN_nSTB_A = PC4;
  private static final int IN_INTE_A = PC4;
  private static final int IN_INTR_B = PC0;
  private static final int IN_IBF_B = PC1;
  private static final int IN_nSTB_B = PC2;
  private static final int IN_INTE_B = PC2;
  private static final int OUT_INTR_A = PC3;
  private static final int OUT_nACK_A = PC6;
  private static final int OUT_INTE_A = PC6;
  private static final int OUT_nOBF_A = PC7;
  private static final int OUT_INTR_B = PC0;
  private static final int OUT_nACK_B = PC2;
  private static final int OUT_INTE_B = PC2;
  private static final int OUT_nOBF_B = PC1;

  // Mode 2 control signals
  private static final int BD_IBF_A = PC5;
  private static final int BD_nSTB_A = PC4;
  private static final int BD_INTE2 = PC4;
  private static final int BD_nACK_A = PC6;
  private static final int BD_INTE1 = PC6;
  private static final int BD_nOBF_A = PC7;
  private static final int BD_INTR_A = PC3;

  // internal registers
  private int modeA, modeB;
  private int directionA, directionB, directionCL, directionCH;
  private int strobeA, strobeB, acknowledgeA, acknowledgeB;
  private final int[] outputBuffer = new int[24];
  private final int[] inputLatch = new int[16];

  // pins
  private final Pin[] pins = new Pin[24];

  /**
   * Gets I/O pin.
   *
   * @param  n pin number
   * @return <code>IOPin</code> object
   */
  public IOPin getPin(final int n) {
    assert (n >= 0) && (n < 24);
    return pins[n];
  }

  // clear all internal registers
  private void clearRegisters() {
    strobeA = strobeB = acknowledgeA = acknowledgeB = 1;
    for (int i = 0; i < 24; i++) {
      outputBuffer[i] = 0;
    }
    for (int i = 0; i < 16; i++) {
      inputLatch[i] = 0;
    }
  }

  // notify on all pins
  private void notifyAllPins() {
    for (int i = 0; i < 24; i++) {
      pins[i].notifyChangeNode();
    }
  }

  /**
   * Resets the device.  All I/O pins are configured as inputs, Mode 0
   * is activated on both A and B.
   */
  @Override
  public void reset() {
    modeA = modeB = 0;
    directionA = directionB = directionCL = directionCH = INPUT;
    clearRegisters();
    notifyAllPins();
    log.finer(String.format("%s: reset", name));
  }

  /**
   * Main constructor.
   *
   * @param name device name
   */
  public Intel8255A(final String name) {
    super(name);
    log = Logger.getLogger(getClass().getName() + "." + name);
    log.fine("New Intel 8255A creation started, name: " + name);

    add(new Register("MODE_A") {
	@Override
	public String getValue() {
	  return String.valueOf(modeA);
	}
	@Override
	public void processValue(final String value) {
	  modeA = Integer.parseInt(value);
	  log.finer("Mode A set to: " + modeA);
	}
      });
    add(new Register("MODE_B") {
	@Override
	public String getValue() {
	  return String.valueOf(modeB);
	}
	@Override
	public void processValue(final String value) {
	  modeB = Integer.parseInt(value);
	  log.finer("Mode B set to: " + modeB);
	}
      });
    add(new Register("DIR_A") {
	@Override
	public String getValue() {
	  return String.valueOf(directionA);
	}
	@Override
	public void processValue(final String value) {
	  directionA = Integer.parseInt(value);
	  log.finer("Direction A set to: " + directionA);
	}
      });
    add(new Register("DIR_B") {
	@Override
	public String getValue() {
	  return String.valueOf(directionB);
	}
	@Override
	public void processValue(final String value) {
	  directionB = Integer.parseInt(value);
	  log.finer("Direction B set to: " + directionB);
	}
      });
    add(new Register("DIR_CL") {
	@Override
	public String getValue() {
	  return String.valueOf(directionCL);
	}
	@Override
	public void processValue(final String value) {
	  directionCL = Integer.parseInt(value);
	  log.finer("Direction CL set to: " + directionCL);
	}
      });
    add(new Register("DIR_CH") {
	@Override
	public String getValue() {
	  return String.valueOf(directionCH);
	}
	@Override
	public void processValue(final String value) {
	  directionCH = Integer.parseInt(value);
	  log.finer("Direction CH set to: " + directionCH);
	}
      });
    add(new Register("IN") {
	@Override
	public String getValue() {
	  int n = 0;
	  for (int i = 0, r = 1; i < 16; i++, r <<= 1) {
	    if (inputLatch[i] != 0) {
	      n |= r;
	    }
	  }
	  return String.format("%04x", n);
	}
	@Override
	public void processValue(final String value) {
	  for (int i = 0, n = Integer.parseInt(value, 16);
	       i < 16; i++, n >>= 1) {
	    inputLatch[i] = n;
	  }
	  log.finer("Input latch registers set");
	}
      });
    add(new Register("OUT") {
	@Override
	public String getValue() {
	  int n = 0;
	  for (int i = 0, r = 1; i < 24; i++, r <<= 1) {
	    if (outputBuffer[i] != 0) {
	      n |= r;
	    }
	  }
	  return String.format("%06x", n);
	}
	@Override
	public void processValue(final String value) {
	  for (int i = 0, n = Integer.parseInt(value, 16);
	       i < 24; i++, n >>= 1) {
	    outputBuffer[i] = n & 1;
	  }
	  log.finer("Output buffers set");
	}
      });
    add(new Register("STB_A") {
	@Override
	public String getValue() {
	  return String.valueOf(strobeA);
	}
	@Override
	public void processValue(final String value) {
	  strobeA = Integer.parseInt(value);
	  log.finer("Strobe A set to: " + strobeA);
	}
      });
    add(new Register("STB_B") {
	@Override
	public String getValue() {
	  return String.valueOf(strobeB);
	}
	@Override
	public void processValue(final String value) {
	  strobeB = Integer.parseInt(value);
	  log.finer("Strobe B set to: " + strobeB);
	}
      });
    add(new Register("ACK_A") {
	@Override
	public String getValue() {
	  return String.valueOf(acknowledgeA);
	}
	@Override
	public void processValue(final String value) {
	  acknowledgeA = Integer.parseInt(value);
	  log.finer("Acknowledge A set to: " + acknowledgeA);
	}
      });
    add(new Register("ACK_B") {
	@Override
	public String getValue() {
	  return String.valueOf(acknowledgeB);
	}
	@Override
	public void processValue(final String value) {
	  acknowledgeB = Integer.parseInt(value);
	  log.finer("Acknowledge B set to: " + acknowledgeB);
	}
      });

    for (int i = 0; i < 24; i++) {
      pins[i] = new Pin(i);
    }
    reset();
    log.fine("New Intel 8255A creation completed, name: " + name);
  }

  // for description see Device
  @Override
  public void postUnmarshal() {
    for (int i = 0; i < 24; i++) {
      pins[i].notifyChangeNode();
    }
    log.fine("Post-unmarshal on 8255A completed");
  }

  // convert tri-state to level
  private static int highImpedanceToHigh(final int ts) {
    return (ts == 0) ? 0 : 1;
  }

  // I/O pin
  private class Pin extends IOPin {
    private int pinNumber;
	
    // main constructor
    private Pin(final int n) {
      super();
      pinNumber = n;
    }

    // for description see IOPin
    @Override
    public int query() {
      if (pinNumber < 8) {  // Port A
	if (modeA < 2) {
	  return (directionA == OUTPUT) ?
	    outputBuffer[pinNumber] :
	    IONode.HIGH_IMPEDANCE;
	} else {
	  return (pins[BD_nACK_A].queryNode() == 0) ?
	    outputBuffer[pinNumber] :
	    IONode.HIGH_IMPEDANCE;
	}
      } else if (pinNumber < 16) {  // Port B
	return (directionB == OUTPUT) ?
	  outputBuffer[pinNumber] :
	  IONode.HIGH_IMPEDANCE;
      }
      else  // Port C
	switch(pinNumber) {
	  case PC0:
	  case PC1:
	    if (modeB == 1) {
	      return outputBuffer[pinNumber];
	    } else {
	      return (directionCL == OUTPUT) ?
		outputBuffer[pinNumber] :
		IONode.HIGH_IMPEDANCE;
	    }
	  case PC2:
	    if (modeB == 1) {
	      return IONode.HIGH_IMPEDANCE;
	    } else {
	      return (directionCL == OUTPUT) ?
		outputBuffer[pinNumber] :
		IONode.HIGH_IMPEDANCE;
	    }
	  case PC3:
	    if (modeA != 0) {
	      return outputBuffer[pinNumber];
	    } else {
	      return (directionCL == OUTPUT) ?
		outputBuffer[pinNumber] :
		IONode.HIGH_IMPEDANCE;
	    }
	  case PC4:
	    if (((modeA == 1) && (directionA == INPUT)) || (modeA == 2)) {
	      return IONode.HIGH_IMPEDANCE;
	    } else {
	      return (directionCH == OUTPUT) ?
		outputBuffer[pinNumber] :
		IONode.HIGH_IMPEDANCE;
	    }
	  case PC5:
	    if (((modeA == 1) && (directionA == INPUT)) || (modeA == 2)) {
	      return outputBuffer[pinNumber];
	    } else {
	      return (directionCH == OUTPUT) ?
		outputBuffer[pinNumber] :
		IONode.HIGH_IMPEDANCE;
	    }
	  case PC6:
	    if (((modeA == 1) && (directionA == OUTPUT)) || (modeA == 2)) {
	      return IONode.HIGH_IMPEDANCE;
	    } else {
	      return (directionCH == OUTPUT) ?
		outputBuffer[pinNumber] :
		IONode.HIGH_IMPEDANCE;
	    }
	  case PC7:
	  default:
	    if (((modeA == 1) && (directionA == OUTPUT)) || (modeA == 2)) {
	      return outputBuffer[pinNumber];
	    } else {
	      return (directionCH == OUTPUT) ?
		outputBuffer[pinNumber] :
		IONode.HIGH_IMPEDANCE;
	    }
	}
    }

    // for description see IOPin
    @Override
    public void notifyChange() {
      switch (pinNumber) {
	case PC2:
	  if ((modeB == 1) && (directionB == INPUT)) {
	    final int s = highImpedanceToHigh(queryNode());
	    if ((strobeB == 1) && (s == 0)) {
	      outputBuffer[IN_IBF_B] = 1;
	      pins[IN_IBF_B].notifyChangeNode();
	    } else if ((strobeB == 0) && (s == 1)) {
	      for (int i = 0; i < 8; i++) {
		inputLatch[8 + i] =
		  highImpedanceToHigh(pins[8 + i].queryNode());
	      }
	      if (outputBuffer[IN_INTE_B] == 1) {
		outputBuffer[IN_INTR_B] = 1;
		pins[IN_INTR_B].notifyChangeNode();
	      }
	    }
	    strobeB = s;
	  } else if ((modeB == 1) && (directionB == OUTPUT)) {
	    final int s = highImpedanceToHigh(queryNode());
	    if ((acknowledgeB == 1) && (s == 0)) {
	      outputBuffer[OUT_nOBF_B] = 1;
	      pins[OUT_nOBF_B].notifyChangeNode();
	    } else if ((acknowledgeB == 0) && (s == 1)) {
	      if (outputBuffer[OUT_INTE_B] == 1) {
		outputBuffer[OUT_INTR_B] = 1;
		pins[OUT_INTR_B].notifyChangeNode();
	      }
	    }
	    acknowledgeB = s;
	  }
	  break;
	case PC4:
	  if ((modeA == 1) && (directionA == INPUT)) {
	    final int s = highImpedanceToHigh(queryNode());
	    if ((strobeA == 1) && (s == 0)) {
	      outputBuffer[IN_IBF_A] = 1;
	      pins[IN_IBF_A].notifyChangeNode();
	    } else if ((strobeA == 0) && (s == 1)) {
	      for (int i = 0; i < 8; i++) {
		inputLatch[i] = highImpedanceToHigh(pins[i].queryNode());
	      }
	      if (outputBuffer[IN_INTE_A] == 1) {
		outputBuffer[IN_INTR_A] = 1;
		pins[IN_INTR_A].notifyChangeNode();
	      }
	    }
	    strobeA = s;
	  } else if (modeA == 2) {
	    final int s = highImpedanceToHigh(queryNode());
	    if ((strobeA == 1) && (s == 0)) {
	      outputBuffer[BD_IBF_A] = 1;
	      pins[BD_IBF_A].notifyChangeNode();
	    } else if ((strobeA == 0) && (s == 1)) {
	      for (int i = 0; i < 8; i++) {
		inputLatch[i] = highImpedanceToHigh(pins[i].queryNode());
	      }
	      if (outputBuffer[BD_INTE2] == 1) {
		outputBuffer[BD_INTR_A] = 1;
		pins[BD_INTR_A].notifyChangeNode();
	      }
	    }
	    strobeA = s;
	  }
	  break;
	case PC6:
	  if ((modeA == 1) && (directionA == OUTPUT)) {
	    final int s = highImpedanceToHigh(queryNode());
	    if ((acknowledgeA == 1) && (s == 0)) {
	      outputBuffer[OUT_nOBF_A] = 1;
	      pins[OUT_nOBF_A].notifyChangeNode();
	    } else if ((acknowledgeA == 0) && (s == 1)) {
	      if (outputBuffer[OUT_INTE_A] == 1) {
		outputBuffer[OUT_INTR_A] = 1;
		pins[OUT_INTR_A].notifyChangeNode();
	      }
	    }
	    acknowledgeA = s;
	  } else if (modeA == 2) {
	    final int s = highImpedanceToHigh(queryNode());
	    if ((acknowledgeA == 1) && (s == 0)) {
	      for (int i = 0; i < 8; i++) {
		pins[i].notifyChangeNode();
	      }
	      outputBuffer[BD_nOBF_A] = 1;
	      pins[BD_nOBF_A].notifyChangeNode();
	    } else if ((acknowledgeA == 0) && (s == 1)) {
	      for (int i = 0; i < 8; i++) {
		pins[i].notifyChangeNode();
	      }
	      if (outputBuffer[BD_INTE1] == 1) {
		outputBuffer[BD_INTR_A] = 1;
		pins[BD_INTR_A].notifyChangeNode();
	      }
	    }
	    acknowledgeA = s;
	  }
	  break;
      }
    }
  }

  // for description see IOElement
  @Override
  public int portInput(final int port) {
    int d = 0xff;
    switch (port & 0x03) {
      case PORT_A:
	if (modeA == 0) {
	  d = 0;
	  for (int i = 0; i < 8; i++) {
	    final int r = (directionA == OUTPUT) ?
	      outputBuffer[i] :
	      highImpedanceToHigh(pins[i].queryNode());
	    d = (d >> 1) | (r << 7);
	  }
	} else if (modeA == 1) {
	  d = 0;
	  for (int i = 0; i < 8; i++) {
	    final int r = (directionA == OUTPUT) ?
	      outputBuffer[i] :
	      inputLatch[i];
	    d = (d >> 1) | (r << 7);
	  }
	  if (directionA == INPUT) {
	    outputBuffer[IN_IBF_A] = 0;
	    pins[IN_IBF_A].notifyChangeNode();
	    if (outputBuffer[IN_INTE_A] == 1) {
	      outputBuffer[IN_INTR_A] = 0;
	      pins[IN_INTR_A].notifyChangeNode();
	    }
	  }
	} else {
	  d = 0;
	  for (int i = 0; i < 8; i++) {
	    final int r = (directionA == OUTPUT) ?
	      outputBuffer[i] :
	      inputLatch[i];
	    d = (d >> 1) | (r << 7);
	  }
	  outputBuffer[IN_IBF_A] = 0;
	  pins[IN_IBF_A].notifyChangeNode();
	  if (((outputBuffer[BD_INTE2] == 1) &&
	       ((outputBuffer[BD_INTE1] == 0) ||
		(outputBuffer[BD_nOBF_A] == 0)))) {
	    outputBuffer[IN_INTR_A] = 0;
	    pins[IN_INTR_A].notifyChangeNode();
	  }
	}
	log.finer(String.format("%s: PA -> 0x%02x", name, d));
	break;
      case PORT_B:
	if (modeB == 0) {
	  d = 0;
	  for (int i = 0; i < 8; i++) {
	    final int r = (directionB == OUTPUT) ?
	      outputBuffer[8 + i] :
	      highImpedanceToHigh(pins[8 + i].queryNode());
	    d = (d >> 1) | (r << 7);
	  }
	} else {
	  d = 0;
	  for (int i = 0; i < 8; i++) {
	    final int r = (directionB == OUTPUT) ?
	      outputBuffer[8 + i] :
	      inputLatch[8 + i];
	    d = (d >> 1) | (r << 7);
	  }
	  if (directionB == INPUT) {
	    outputBuffer[IN_IBF_B] = 0;
	    pins[IN_IBF_B].notifyChangeNode();
	    if (outputBuffer[IN_INTE_B] == 1) {
	      outputBuffer[IN_INTR_B] = 0;
	      pins[IN_INTR_B].notifyChangeNode();
	    }
	  }
	} 
	log.finer(String.format("%s: PB -> 0x%02x", name, d));
	break;
      case PORT_C:
	// PC7
	if ((modeA == 0) || ((modeA == 1) && (directionA == INPUT))) {
	  d = ((directionCH == OUTPUT) ?
	       outputBuffer[PC7] :
	       highImpedanceToHigh(pins[PC7].queryNode()));
	} else {
	  d = outputBuffer[PC7];
	}
	// PC6
	if ((modeA == 0) || ((modeA == 1) && (directionA == INPUT))) {
	  d = (d << 1) | ((directionCH == OUTPUT) ?
			  outputBuffer[PC6] :
			  highImpedanceToHigh(pins[PC6].queryNode()));
	} else {
	  d = (d << 1) | highImpedanceToHigh(pins[PC6].queryNode());
	}
	// PC5
	if ((modeA == 0) || ((modeA == 1) && (directionA == OUTPUT))) {
	  d = (d << 1) | ((directionCH == OUTPUT) ?
			  outputBuffer[PC5] :
			  highImpedanceToHigh(pins[PC5].queryNode()));
	} else {
	  d = (d << 1) | outputBuffer[PC5];
	}
	// PC4
	if ((modeA == 0) || ((modeA == 1) && (directionA == OUTPUT))) {
	  d = (d << 1) | ((directionCH == OUTPUT) ?
			  outputBuffer[PC4] :
			  highImpedanceToHigh(pins[PC4].queryNode()));
	} else {
	  d = (d << 1) | highImpedanceToHigh(pins[PC4].queryNode());
	}
	// PC3
	if (modeA == 0) {
	  d = (d << 1) | ((directionCL == OUTPUT) ?
			  outputBuffer[PC3] :
			  highImpedanceToHigh(pins[PC3].queryNode()));
	} else {
	  d = (d << 1) | outputBuffer[PC3];
	}
	// PC2
	if (modeB == 0) {
	  d = (d << 1) | ((directionCL == OUTPUT) ?
			  outputBuffer[PC2] :
			  highImpedanceToHigh(pins[PC2].queryNode()));
	} else {
	  d = (d << 1) | highImpedanceToHigh(pins[PC2].queryNode());
	}
	// PC1
	if (modeB == 0) {
	  d = (d << 1) | ((directionCL == OUTPUT) ?
			  outputBuffer[PC1] :
			  highImpedanceToHigh(pins[PC1].queryNode()));
	} else {
	  d = (d << 1) | outputBuffer[PC1];
	}
	// PC0
	if (modeB == 0) {
	  d = (d << 1) | ((directionCL == OUTPUT) ?
			  outputBuffer[PC0] :
			  highImpedanceToHigh(pins[PC0].queryNode()));
	} else {
	  d = (d << 1) | outputBuffer[PC0];
	}
	log.finer(String.format("%s: PC -> 0x%02x", name, d));
	break;
    }
    return d;
  }

  // direction to string conversion array
  private static final String[] d2s = new String[] {"out", "in"};

  // for description see IOElement
  @Override
  public void portOutput(final int port, int data) {
    switch (port & 0x03) {
      case PORT_A:
	log.finer(String.format("%s: 0x%02x -> PA", name, data));
	if ((modeA == 0) || ((modeA == 1) && (directionA == INPUT))) {
	  for (int i = 0; i < 8; i++, data >>= 1) {
	    outputBuffer[i] = data & 1;
	    if ((modeA == 0) && (directionA == OUTPUT))
	      pins[i].notifyChangeNode();
	  }
	} else if (modeA == 1) {
	  outputBuffer[OUT_INTR_A] = 0;
	  pins[OUT_INTR_A].notifyChangeNode();
	  for (int i = 0; i < 8; i++, data >>= 1) {
	    outputBuffer[i] = data & 1;
	    pins[i].notifyChangeNode();
	  }
	  outputBuffer[OUT_nOBF_A] = 0;
	  pins[OUT_nOBF_A].notifyChangeNode();
	} else {
	  outputBuffer[OUT_INTR_A] = 0;
	  pins[OUT_INTR_A].notifyChangeNode();
	  for (int i = 0; i < 8; i++, data >>= 1) {
	    outputBuffer[i] = data & 1;
	  }
	  outputBuffer[OUT_nOBF_A] = 0;
	  pins[OUT_nOBF_A].notifyChangeNode();
	}
	break;
      case PORT_B:
	log.finer(String.format("%s: 0x%02x -> PB", name, data));
	if ((modeB == 0) || ((modeB == 1) && (directionB == INPUT))) {
	  for (int i = 8; i < 16; i++, data >>= 1) {
	    outputBuffer[i] = data & 1;
	    if ((modeB == 0) && (directionB == OUTPUT)) {
	      pins[i].notifyChangeNode();
	    }
	  }
	} else {
	  outputBuffer[OUT_INTR_B] = 0;
	  pins[OUT_INTR_B].notifyChangeNode();
	  for (int i = 8; i < 16; i++, data >>= 1) {
	    outputBuffer[i] = data & 1;
	    pins[i].notifyChangeNode();
	  }
	  outputBuffer[OUT_nOBF_B] = 0;
	  pins[OUT_nOBF_B].notifyChangeNode();
	}
	break;
      case PORT_C:
	log.finer(String.format("%s: 0x%02x -> PC", name, data));
	if ((modeA == 0) && (modeB == 0)) {
	  for (int i = 16; i < 20; i++, data >>= 1) {
	    outputBuffer[i] = data & 1;
	    if (directionCL == OUTPUT) {
	      pins[i].notifyChangeNode();
	    }
	  }
	  for (int i = 20; i < 24; i++, data >>= 1) {
	    outputBuffer[i] = data & 1;
	    if (directionCH == OUTPUT) {
	      pins[i].notifyChangeNode();
	    }
	  }
	} else if (modeB == 0) {
	  for (int i = 16; i < 19; i++, data >>= 1) {
	    outputBuffer[i] = data & 1;
	    if (directionCL == OUTPUT) {
	      pins[i].notifyChangeNode();
	    }
	  }
	}
	break;
      case CONTROL_PORT:
	if ((data & 0x80) != 0) {  // mode selection
	  directionB = (data >> 1) & 1;
	  modeB = (data >> 2) & 1;
	  directionCL = data & 1;
	  directionA = (data >> 4) & 1;
	  modeA = Math.min((data >> 5) & 3, 2);
	  directionCH = (data >> 3) & 1;
	  log.fine(String.format(
	    "%s: 0x%02x -> Control Port (modeA: %d, modeB: %d," +
	    " dirA: %s, dirB: %s, dirCH: %s, dirCL: %s)",
	    name,
	    data,
	    modeA,
	    modeB,
	    d2s[directionA],
	    d2s[directionB],
	    d2s[directionCH],
	    d2s[directionCL]));
	  clearRegisters();
	  if ((modeA == 1) && (directionA == OUTPUT)) {
	    outputBuffer[OUT_nOBF_A] = 1;
	  } else if (modeA == 2) {
	    outputBuffer[BD_nOBF_A] = 1;
	  }
	  if ((modeB == 1) && (directionB == OUTPUT)) {
	    outputBuffer[OUT_nOBF_B] = 1;
	  }
	  notifyAllPins();
	} else {  // set/reset Port C pins
	  final int d = data & 1;
	  log.finer(String.format("%s: PC%d %sset",
				  name,
				  (data >> 1) & 7,
				  (d == 0) ? "re" : ""));
	  switch(PC0 + ((data >> 1) & 7)) {
	    case PC0:
	      if (modeB == 0) {
		outputBuffer[PC0] = d;
		if (directionCL == OUTPUT) {
		  pins[PC0].notifyChangeNode();
		}
	      }
	      break;
	    case PC1:
	      if (modeB == 0) {
		outputBuffer[PC1] = d;
		if (directionCL == OUTPUT) {
		  pins[PC1].notifyChangeNode();
		}
	      }
	      break;
	    case PC2:
	      if (modeB == 0) {
		outputBuffer[PC2] = d;
		if (directionCL == OUTPUT) {
		  pins[PC2].notifyChangeNode();
		}
	      } else if (directionB == INPUT) {
		outputBuffer[PC2] = d;
		outputBuffer[IN_INTR_B] = d & outputBuffer[IN_IBF_B];
		pins[IN_INTR_B].notifyChangeNode();
	      } else {
		outputBuffer[PC2] = d;
		outputBuffer[OUT_INTR_B] = d & outputBuffer[OUT_nOBF_B];
		pins[OUT_INTR_B].notifyChangeNode();
	      }
	      break;
	    case PC3:
	      if ((modeA == 0) || ((modeA == 1) && (directionA == INPUT))) {
		outputBuffer[PC3] = d;
		if (directionCL == OUTPUT) {
		  pins[PC3].notifyChangeNode();
		}
	      }
	      break;
	    case PC4:
	      if ((modeA == 0) || ((modeA == 1) && (directionA == OUTPUT))) {
		outputBuffer[PC4] = d;
		if (directionCH == OUTPUT) {
		  pins[PC4].notifyChangeNode();
		}
	      } else if ((modeA == 1) && (directionA == INPUT)) {
		outputBuffer[PC4] = d;
		outputBuffer[IN_INTR_A] = d & outputBuffer[IN_IBF_A];
		pins[IN_INTR_A].notifyChangeNode();
	      } else {
		outputBuffer[PC4] = d;
		outputBuffer[BD_INTR_A] = d & outputBuffer[BD_IBF_A];
		pins[BD_INTR_A].notifyChangeNode();
	      }
	      break;
	    case PC5:
	      if ((modeA == 0) || ((modeA == 1) && (directionA == OUTPUT))) {
		outputBuffer[PC5] = d;
		if (directionCH == OUTPUT) {
		  pins[PC5].notifyChangeNode();
		}
	      }
	      break;
	    case PC6:
	      if ((modeA == 0) || ((modeA == 1) && (directionA == INPUT))) {
		outputBuffer[PC6] = d;
		if (directionCH == OUTPUT) {
		  pins[PC6].notifyChangeNode();
		}
	      } else if ((modeA == 1) && (directionA == OUTPUT)) {
		outputBuffer[PC6] = d;
		outputBuffer[OUT_INTR_A] = d & outputBuffer[OUT_nOBF_A];
		pins[OUT_INTR_A].notifyChangeNode();
	      } else {
		outputBuffer[PC6] = d;
		outputBuffer[BD_INTR_A] = d & outputBuffer[BD_nOBF_A];
		pins[BD_INTR_A].notifyChangeNode();
	      }
	      break;
	    case PC7:
	      if ((modeA == 0) || ((modeA == 1) && (directionA == INPUT))) {
		outputBuffer[PC7] = d;
		if (directionCH == OUTPUT) {
		  pins[PC7].notifyChangeNode();
		}
	      }
	      break;	
	  }
	}
	break;
    }
  }
}
