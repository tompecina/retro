/* ZilogZ80.java
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
import java.util.logging.Level;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/**
 * Zilog Z80 CPU.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class ZilogZ80 extends Device implements Processor, SystemClockSource {

  // dynamic logger, per device
  private Logger log;

  /**
   * Mask of the Sign (S) flag.
   */
  public static final int SF = 0x80;

  /**
   * Mask of the Zero (Z) flag.
   */
  public static final int ZF = 0x40;

  /**
   * Mask of the Y flag.
   */
  public static final int YF = 0x20;

  /**
   * Mask of the Half Carry (H) flag.
   */
  public static final int HF = 0x10;

  /**
   * Mask of the X flag.
   */
  public static final int XF = 0x08;

  /**
   * Mask of the Parity/Overflow (P) flag.
   */
  public static final int PF = 0x04;

  /**
   * Mask of the Add/Subtract (N) flag.
   */
  public static final int NF = 0x02;

  /**
   * Mask of the Carry (C) flag.
   */
  public static final int CF = 0x01;

  /**
   * The memory device attached to the processor.
   */
  protected AbstractMemory memory;

  /**
   * List of input ports.
   */
  protected final List<Set<IOElement>> inputPorts = new ArrayList<>();

  /**
   * List of output ports.
   */
  protected final List<Set<IOElement>> outputPorts = new ArrayList<>();

  /**
   * CPU register A (Accumulator).
   */
  protected int A;

  /**
   * CPU register F (Flags).
   */
  protected int F;

  /**
   * CPU register B.
   */
  protected int B;

  /**
   * CPU register C.
   */
  protected int C;

  /**
   * CPU register D.
   */
  protected int D;

  /**
   * CPU register E.
   */
  protected int E;

  /**
   * CPU register H.
   */
  protected int H;

  /**
   * CPU register L.
   */
  protected int L;

  /**
   * CPU register IX.
   */
  protected int IX;

  /**
   * CPU register IY.
   */
  protected int IY;

  /**
   * CPU register pair WZ.
   */
  protected int WZ;

  /**
   * CPU register A' (alternative Accumulator).
   */
  protected int Aa;

  /**
   * CPU register F' (alternative Flags).
   */
  protected int Fa;

  /**
   * CPU register B'.
   */
  protected int Ba;

  /**
   * CPU register C'.
   */
  protected int Ca;

  /**
   * CPU register D'.
   */
  protected int Da;

  /**
   * CPU register E'.
   */
  protected int Ea;

  /**
   * CPU register H'.
   */
  protected int Ha;

  /**
   * CPU register L'.
   */
  protected int La;

  /**
   * CPU register pair WZ'.
   */
  protected int WZa;

  /**
   * The Program Counter (PC).
   */
  private int PC;

  /**
   * The Stack Pointer (SP).
   */
  private int SP;

  /**
   * The Interrupt Enable flag IFF1.
   */
  protected boolean IFF1;

  /**
   * The Interrupt Enable flag IFF2.
   */
  protected boolean IFF2;

  /**
   * The Interrupt Mode.
   */
  protected int IM;

  /**
   * The Interrupt Register (I).
   */
  protected int I;

  /**
   * The Refresh Register (R).  This register holds the seven
   * lowest bits of the actual register; bit 7 is held by R7.
   */
  protected int R;

  /**
   * The register holding bit 7 of the Refresh Register (R).
   */
  protected int R7;

  /**
   * The CPU cycle counter
   */
  protected long cycleCounter;

  // aux flags
  private boolean resetPending;
  private int interruptPending = -1;

  /**
   * A table combining S, Z, Y and X flags.
   */
  protected static final int[] TBL4 = {
    0x40, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
    0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08,
    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
    0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08,
    0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20,
    0x28, 0x28, 0x28, 0x28, 0x28, 0x28, 0x28, 0x28,
    0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20,
    0x28, 0x28, 0x28, 0x28, 0x28, 0x28, 0x28, 0x28,
    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
    0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08,
    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
    0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08,
    0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20,
    0x28, 0x28, 0x28, 0x28, 0x28, 0x28, 0x28, 0x28,
    0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20,
    0x28, 0x28, 0x28, 0x28, 0x28, 0x28, 0x28, 0x28,
    0x80, 0x80, 0x80, 0x80, 0x80, 0x80, 0x80, 0x80,
    0x88, 0x88, 0x88, 0x88, 0x88, 0x88, 0x88, 0x88,
    0x80, 0x80, 0x80, 0x80, 0x80, 0x80, 0x80, 0x80,
    0x88, 0x88, 0x88, 0x88, 0x88, 0x88, 0x88, 0x88,
    0xa0, 0xa0, 0xa0, 0xa0, 0xa0, 0xa0, 0xa0, 0xa0,
    0xa8, 0xa8, 0xa8, 0xa8, 0xa8, 0xa8, 0xa8, 0xa8,
    0xa0, 0xa0, 0xa0, 0xa0, 0xa0, 0xa0, 0xa0, 0xa0,
    0xa8, 0xa8, 0xa8, 0xa8, 0xa8, 0xa8, 0xa8, 0xa8,
    0x80, 0x80, 0x80, 0x80, 0x80, 0x80, 0x80, 0x80,
    0x88, 0x88, 0x88, 0x88, 0x88, 0x88, 0x88, 0x88,
    0x80, 0x80, 0x80, 0x80, 0x80, 0x80, 0x80, 0x80,
    0x88, 0x88, 0x88, 0x88, 0x88, 0x88, 0x88, 0x88,
    0xa0, 0xa0, 0xa0, 0xa0, 0xa0, 0xa0, 0xa0, 0xa0,
    0xa8, 0xa8, 0xa8, 0xa8, 0xa8, 0xa8, 0xa8, 0xa8,
    0xa0, 0xa0, 0xa0, 0xa0, 0xa0, 0xa0, 0xa0, 0xa0,
    0xa8, 0xa8, 0xa8, 0xa8, 0xa8, 0xa8, 0xa8, 0xa8
  };

  /**
   * A table combining S, Z, Y, X and P flags.
   */
  protected static final int[] TBL5 = {
    0x44, 0x00, 0x00, 0x04, 0x00, 0x04, 0x04, 0x00,
    0x08, 0x0c, 0x0c, 0x08, 0x0c, 0x08, 0x08, 0x0c,
    0x00, 0x04, 0x04, 0x00, 0x04, 0x00, 0x00, 0x04,
    0x0c, 0x08, 0x08, 0x0c, 0x08, 0x0c, 0x0c, 0x08,
    0x20, 0x24, 0x24, 0x20, 0x24, 0x20, 0x20, 0x24,
    0x2c, 0x28, 0x28, 0x2c, 0x28, 0x2c, 0x2c, 0x28,
    0x24, 0x20, 0x20, 0x24, 0x20, 0x24, 0x24, 0x20,
    0x28, 0x2c, 0x2c, 0x28, 0x2c, 0x28, 0x28, 0x2c,
    0x00, 0x04, 0x04, 0x00, 0x04, 0x00, 0x00, 0x04,
    0x0c, 0x08, 0x08, 0x0c, 0x08, 0x0c, 0x0c, 0x08,
    0x04, 0x00, 0x00, 0x04, 0x00, 0x04, 0x04, 0x00,
    0x08, 0x0c, 0x0c, 0x08, 0x0c, 0x08, 0x08, 0x0c,
    0x24, 0x20, 0x20, 0x24, 0x20, 0x24, 0x24, 0x20,
    0x28, 0x2c, 0x2c, 0x28, 0x2c, 0x28, 0x28, 0x2c,
    0x20, 0x24, 0x24, 0x20, 0x24, 0x20, 0x20, 0x24,
    0x2c, 0x28, 0x28, 0x2c, 0x28, 0x2c, 0x2c, 0x28,
    0x80, 0x84, 0x84, 0x80, 0x84, 0x80, 0x80, 0x84,
    0x8c, 0x88, 0x88, 0x8c, 0x88, 0x8c, 0x8c, 0x88,
    0x84, 0x80, 0x80, 0x84, 0x80, 0x84, 0x84, 0x80,
    0x88, 0x8c, 0x8c, 0x88, 0x8c, 0x88, 0x88, 0x8c,
    0xa4, 0xa0, 0xa0, 0xa4, 0xa0, 0xa4, 0xa4, 0xa0,
    0xa8, 0xac, 0xac, 0xa8, 0xac, 0xa8, 0xa8, 0xac,
    0xa0, 0xa4, 0xa4, 0xa0, 0xa4, 0xa0, 0xa0, 0xa4,
    0xac, 0xa8, 0xa8, 0xac, 0xa8, 0xac, 0xac, 0xa8,
    0x84, 0x80, 0x80, 0x84, 0x80, 0x84, 0x84, 0x80,
    0x88, 0x8c, 0x8c, 0x88, 0x8c, 0x88, 0x88, 0x8c,
    0x80, 0x84, 0x84, 0x80, 0x84, 0x80, 0x80, 0x84,
    0x8c, 0x88, 0x88, 0x8c, 0x88, 0x8c, 0x8c, 0x88,
    0xa0, 0xa4, 0xa4, 0xa0, 0xa4, 0xa0, 0xa0, 0xa4,
    0xac, 0xa8, 0xa8, 0xac, 0xa8, 0xac, 0xac, 0xa8,
    0xa4, 0xa0, 0xa0, 0xa4, 0xa0, 0xa4, 0xa4, 0xa0,
    0xa8, 0xac, 0xac, 0xa8, 0xac, 0xa8, 0xa8, 0xac
  };

  /**
   * The main constructor.  Memory must be attached using
   * {@link #setMemory} for the CPU to function.
   *
   * @param name davice name
   */
  public ZilogZ80(final String name) {
    super(name);
    log = Logger.getLogger(getClass().getName() + "." + name);

    add(new Register("A") {
	@Override
	public String getValue() {
	  return String.format("%02x", A);
	}
	@Override
	public void processValue(final String value) {
	  A = Integer.parseInt(value, 16);
	}
      });

    add(new Register("F") {
	@Override
	public String getValue() {
	  return String.format("%02x", F);
	}
	@Override
	public void processValue(final String value) {
	  F = Integer.parseInt(value, 16);
	}
      });

    add(new Register("B") {
	@Override
	public String getValue() {
	  return String.format("%02x", B);
	}
	@Override
	public void processValue(final String value) {
	  B = Integer.parseInt(value, 16);
	}
      });

    add(new Register("C") {
	@Override
	public String getValue() {
	  return String.format("%02x", C);
	}
	@Override
	public void processValue(final String value) {
	  C = Integer.parseInt(value, 16);
	}
      });

    add(new Register("D") {
	@Override
	public String getValue() {
	  return String.format("%02x", D);
	}
	@Override
	public void processValue(final String value) {
	  D = Integer.parseInt(value, 16);
	}
      });

    add(new Register("E") {
	@Override
	public String getValue() {
	  return String.format("%02x", E);
	}
	@Override
	public void processValue(final String value) {
	  E = Integer.parseInt(value, 16);
	}
      });

    add(new Register("H") {
	@Override
	public String getValue() {
	  return String.format("%02x", H);
	}
	@Override
	public void processValue(final String value) {
	  H = Integer.parseInt(value, 16);
	}
      });

    add(new Register("L") {
	@Override
	public String getValue() {
	  return String.format("%02x", L);
	}
	@Override
	public void processValue(final String value) {
	  L = Integer.parseInt(value, 16);
	}
      });

    add(new Register("Aa") {
	@Override
	public String getValue() {
	  return String.format("%02x", Aa);
	}
	@Override
	public void processValue(final String value) {
	  Aa = Integer.parseInt(value, 16);
	}
      });

    add(new Register("Fa") {
	@Override
	public String getValue() {
	  return String.format("%02x", Fa);
	}
	@Override
	public void processValue(final String value) {
	  Fa = Integer.parseInt(value, 16);
	}
      });

    add(new Register("Ba") {
	@Override
	public String getValue() {
	  return String.format("%02x", Ba);
	}
	@Override
	public void processValue(final String value) {
	  Ba = Integer.parseInt(value, 16);
	}
      });

    add(new Register("Ca") {
	@Override
	public String getValue() {
	  return String.format("%02x", Ca);
	}
	@Override
	public void processValue(final String value) {
	  Ca = Integer.parseInt(value, 16);
	}
      });

    add(new Register("Da") {
	@Override
	public String getValue() {
	  return String.format("%02x", Da);
	}
	@Override
	public void processValue(final String value) {
	  Da = Integer.parseInt(value, 16);
	}
      });

    add(new Register("Ea") {
	@Override
	public String getValue() {
	  return String.format("%02x", Ea);
	}
	@Override
	public void processValue(final String value) {
	  Ea = Integer.parseInt(value, 16);
	}
      });

    add(new Register("Ha") {
	@Override
	public String getValue() {
	  return String.format("%02x", Ha);
	}
	@Override
	public void processValue(final String value) {
	  Ha = Integer.parseInt(value, 16);
	}
      });

    add(new Register("La") {
	@Override
	public String getValue() {
	  return String.format("%02x", La);
	}
	@Override
	public void processValue(final String value) {
	  La = Integer.parseInt(value, 16);
	}
      });

    add(new Register("PC") {
	@Override
	public String getValue() {
	  return String.format("%04x", PC);
	}
	@Override
	public void processValue(final String value) {
	  PC = Integer.parseInt(value, 16);
	}
      });

    add(new Register("SP") {
	@Override
	public String getValue() {
	  return String.format("%04x", SP);
	}
	@Override
	public void processValue(final String value) {
	  SP = Integer.parseInt(value, 16);
	}
      });

    add(new Register("WZ") {
	@Override
	public String getValue() {
	  return String.format("%04x", WZ);
	}
	@Override
	public void processValue(final String value) {
	  WZ = Integer.parseInt(value, 16);
	}
      });

    add(new Register("WZa") {
	@Override
	public String getValue() {
	  return String.format("%04x", WZa);
	}
	@Override
	public void processValue(final String value) {
	  WZa = Integer.parseInt(value, 16);
	}
      });

    add(new Register("IFF1") {
	@Override
	public String getValue() {
	  return IFF1 ? "1" : "0";
	}
	@Override
	public void processValue(final String value) {
	  IFF1 = value.equals("1");
	}
      });

    add(new Register("IFF2") {
	@Override
	public String getValue() {
	  return IFF2 ? "1" : "0";
	}
	@Override
	public void processValue(final String value) {
	  IFF2 = value.equals("1");
	}
      });

    add(new Register("IM") {
	@Override
	public String getValue() {
	  return String.valueOf(IM);
	}
	@Override
	public void processValue(final String value) {
	  IM = Integer.parseInt(value);
	}
      });

    add(new Register("I") {
	@Override
	public String getValue() {
	  return String.valueOf(I);
	}
	@Override
	public void processValue(final String value) {
	  I = Integer.parseInt(value);
	}
      });

    add(new Register("R") {
	@Override
	public String getValue() {
	  return String.valueOf(R);
	}
	@Override
	public void processValue(final String value) {
	  R = Integer.parseInt(value);
	}
      });

    for (int i = 0; i < 0x100; i++) {
      inputPorts.add(new HashSet<IOElement>());
      outputPorts.add(new HashSet<IOElement>());
    }
    log.fine(String.format("New Intel 8080A created, name: %s", name));
  }

  // for description see Processor
  @Override
  public void setMemory(final AbstractMemory memory) {
    assert memory != null;
    this.memory = memory;
  }

  // for description see Processor
  @Override
  public void addIOInput(final int port, final IOElement element) {
    assert (port >= 0) && (port < 0x100);
    assert element != null;
    inputPorts.get(port).add(element);
  }

  // for description see Processor
  @Override
  public void removeIOInput(final int port, final IOElement element) {
    assert (port >= 0) && (port < 0x100);
    assert element != null;
    inputPorts.get(port).remove(element);
  }

  // for description see Processor
  @Override
  public void clearIOInput(final int port) {
    assert (port >= 0) && (port < 0x100);
    inputPorts.get(port).clear();
  }

  // for description see Processor
  @Override
  public void addIOOutput(final int port, final IOElement element) {
    assert (port >= 0) && (port < 0x100);
    assert element != null;
    outputPorts.get(port).add(element);
  }

  // for description see Processor
  @Override
  public void removeIOOutput(final int port, final IOElement element) {
    assert (port >= 0) && (port < 0x100);
    assert element != null;
    outputPorts.get(port).remove(element);
  }

  // for description see Processor
  @Override
  public void clearIOOutput(final int port) {
    assert (port >= 0) && (port < 0x100);
    outputPorts.get(port).clear();
  }

  /**
   * Requests reset.  It will be executed before the next instruction
   * is executed.
   *
   * @see #reset
   */
  public void requestReset() {
    log.fine("Reset requested");
    resetPending = true;
  }

  /**
   * Performs reset.  It will be executed immediately.
   *
   * @see #requestReset
   */
  @Override
  public void reset() {
    PC = 0;
    IFF1 = IFF2 = false;
    IM = 0;
    R = R7 = I = 0;
    A = F = B = C = D = E = H = L =
      Aa = Fa = Ba = Ca = Da = Ea = Ha = La = 0xff;
    SP = IX = IY = 0xffff;
    WZ = WZa = 0;
    resetPending = false;
    interruptPending = -1;
    log.fine("Reset performed");
  }

  // for description see Processor
  @Override
  public void requestInterrupt(final int vector) {
    assert (vector >= 0) && (vector < 8);
    if (IFF1) {
      interruptPending = vector;
    }
  }

  // for description see Processor
  @Override
  public void interrupt(final int vector) {
    assert (vector >= 0) && (vector < 8);
    if (IFF1) {
      IFF1 = false;
      decSP();
      memory.setByte(SP, PC >> 8);
      decSP();
      memory.setByte(SP, PC & 0xff);
      PC = 8 * vector;
      cycleCounter += 11;
    }
    interruptPending = -1;
  }

  /**
   * Gets the register pair BC.
   *
   * @return the register pair BC
   */
  protected int BC() {
    return (B << 8) + C;
  }

  /**
   * Gets the register pair DE.
   *
   * @return the register pair DE
   */
  protected int DE() {
    return (D << 8) + E;
  }

  /**
   * Gets the register pair HL.
   *
   * @return the register pair HL
   */
  protected int HL() {
    return (H << 8) + L;
  }

  /**
   * Puts the two-flag composite value consisting of flags X and Y
   * to the Flags (F) register according to the parameter.
   *
   * @param v the evaluated byte
   */
  protected void F2(final int v) {
    assert (v >= 0) && (v < 0x100);
    F = (F & 0xd7) | (v & 0x28);
  }

  /**
   * Adds the four-flag composite value consisting of flags S, Z, X and Y
   * to the Flags (F) register according to the parameter.
   *
   * @param v the evaluated byte
   */
  protected void F4(final int v) {
    assert (v >= 0) && (v < 0x100);
    F = (F & 0x17) | TBL4[v];
  }

  /**
   * Adds the four-flag composite value consisting of flags S, Z, X and Y
   * to the Flags (F) register according to two different parameters.
   *
   * @param sz the byte according to which S and Z will be set
   * @param xy the byte according to which X and Y will be set
   */
  protected void F22(final int sz, final int xy) {
    assert (sz >= 0) && (sz < 0x100);
    assert (xy >= 0) && (xy < 0x100);
    F = (((F & 0x17) | TBL4[sz]) & 0xd7) | (xy & 0x28);
  }

  /**
   * Adds the five-flag composite value consisting of flags S, Z, X, Y and P
   * (Parity) to the Flags (F) register according to the parameter.
   *
   * @param v the evaluated byte
   */
  protected void F5(final int v) {
    assert (v >= 0) && (v < 0x100);
    F = (F & 0x13) | TBL5[v];
  }

  /**
   * Adds the five-flag composite value consisting of flags S, Z, X, Y and P
   * to the Flags (F) register according to two different parameters.
   *
   * @param szp the byte according to which S, Z and P will be set
   * @param xy  the byte according to which X and Y will be set
   */
  protected void F32(final int szp, final int xy) {
    assert (szp >= 0) && (szp < 0x100);
    assert (xy >= 0) && (xy < 0x100);
    F = (((F & 0x13) | TBL5[szp]) & 0xd7) | (xy & 0x28);
  }

  /**
   * Sets the Sign (S) flag.
   */
  protected void SETSF() {
    F |= SF;
  }

  /**
   * Clears the Sign (S) flag.
   */
  protected void CLEARSF() {
    F &= ~SF;
  }

  /**
   * Gets the Sign (S) flag.
   *
   * @return the Sign (S) flag
   */
  protected boolean SFSET() {
    return (F & SF) != 0;
  }

  /**
   * Sets the Zero (Z) flag.
   */
  protected void SETZF() {
    F |= ZF;
  }

  /**
   * Clears the Zero (Z) flag.
   */
  protected void CLEARZF() {
    F &= ~ZF;
  }

  /**
   * Gets the Zero (Z) flag.
   *
   * @return the Zero (Z) flag
   */
  protected boolean ZFSET() {
    return (F & ZF) != 0;
  }

  /**
   * Sets the Half Carry (H) flag.
   */
  protected void SETHF() {
    F |= HF;
  }

  /**
   * Sets the Y flag.
   */
  protected void SETYF() {
    F |= YF;
  }

  /**
   * Clears the Y flag.
   */
  protected void CLEARYF() {
    F &= ~YF;
  }

  /**
   * Clears the Half Carry (H) flag.
   */
  protected void CLEARHF() {
    F &= ~HF;
  }

  /**
   * Gets the Half Carry (H) flag.
   *
   * @return the Half Carry (H) flag
   */
  protected boolean HFSET() {
    return (F & HF) != 0;
  }

  /**
   * Sets the X flag.
   */
  protected void SETXF() {
    F |= XF;
  }

  /**
   * Clears the X flag.
   */
  protected void CLEARXF() {
    F &= ~XF;
  }

  /**
   * Sets the Parity/Overflow (P) flag.
   */
  protected void SETPF() {
    F |= PF;
  }

  /**
   * Clears the Parity/Overflow (P) flag.
   */
  protected void CLEARPF() {
    F &= ~PF;
  }

  /**
   * Gets the Parity/Overflow (P) flag.
   *
   * @return the Parity/Overflow (P) flag
   */
  protected boolean PFSET() {
    return (F & PF) != 0;
  }

  /**
   * {@code true} of parity even.
   */
  protected boolean PE() {
    return (F & PF) != 0;
  }

  /**
   * {@code true} of parity odd.
   */
  protected boolean PO() {
    return (F & PF) == 0;
  }

  /**
   * Sets the Add/Subtract (N) flag.
   */
  protected void SETNF() {
    F |= NF;
  }

  /**
   * Clears the Add/Subtract (N) flag.
   */
  protected void CLEARNF() {
    F &= ~NF;
  }

  /**
   * Gets the Add/Subtract (N) flag.
   *
   * @return the Add/Subtract (N) flag
   */
  protected boolean NFSET() {
    return (F & NF) != 0;
  }

  /**
   * Sets the Carry (C) flag.
   */
  protected void SETCF() {
    F |= CF;
  }

  /**
   * Clears the Carry (C) flag.
   */
  protected void CLEARCF() {
    F &= ~CF;
  }

  /**
   * Gets the Carry (C) flag.
   *
   * @return the Carry (C) flag
   */
  protected boolean CFSET() {
    return (F & CF) != 0;
  }

  /**
   * Increments the Program Counter (PC).
   */
  protected void incPC() {
    PC = (PC + 1) & 0xffff;
  }

  /**
   * Adds {@code n} to the Program Counter (PC).
   *
   * @param n the value added to the Program Counter (PC)
   */
  protected void incPC(final int n) {
    PC = (PC + n) & 0xffff;
  }

  /**
   * Decrements the Program Counter (PC).
   */
  protected void decPC() {
    PC = (PC - 1) & 0xffff;
  }

  /**
   * Increments the Stack Pointer (SP).
   */
  protected void incSP() {
    SP = (SP + 1) & 0xffff;
  }

  /**
   * Decrements the Stack Pointer (SP).
   */
  protected void decSP() {
    SP = (SP - 1) & 0xffff;
  }

  /**
   * Gets the register A.
   *
   * @return the register value
   */
  public int getA() {
    return A;
  }

  /**
   * Sets the register A.
   *
   * @param n the new register value
   */
  public void setA(final int n) {
    assert (n >= 0) && (n < 0x100);
    A = n & 0xff;
  }

  /**
   * Gets the Flags (F) register.
   *
   * @return the register value
   */
  public int getF() {
    return F;
  }

  /**
   * Sets the Flags (F) register.
   *
   * @param n the new register value
   */
  public void setF(final int n) {
    assert (n >= 0) && (n < 0x100);
    F = n;
  }

  /**
   * Gets the register B.
   *
   * @return the register value
   */
  public int getB() {
    return B;
  }

  /**
   * Sets the register B.
   *
   * @param n the new register value
   */
  public void setB(final int n) {
    assert (n >= 0) && (n < 0x100);
    B = n & 0xff;
  }

  /**
   * Gets the register C.
   *
   * @return the register value
   */
  public int getC() {
    return C;
  }

  /**
   * Sets the register C.
   *
   * @param n the new register value
   */
  public void setC(final int n) {
    assert (n >= 0) && (n < 0x100);
    C = n & 0xff;
  }

  /**
   * Gets the register D.
   *
   * @return the register value
   */
  public int getD() {
    return D;
  }

  /**
   * Sets the register D.
   *
   * @param n the new register value
   */
  public void setD(final int n) {
    assert (n >= 0) && (n < 0x100);
    D = n & 0xff;
  }

  /**
   * Gets the register E.
   *
   * @return the register value
   */
  public int getE() {
    return E;
  }

  /**
   * Sets the register E.
   *
   * @param n the new register value
   */
  public void setE(final int n) {
    assert (n >= 0) && (n < 0x100);
    E = n & 0xff;
  }

  /**
   * Gets the register H.
   *
   * @return the register value
   */
  public int getH() {
    return H;
  }

  /**
   * Sets the register H.
   *
   * @param n the new register value
   */
  public void setH(final int n) {
    assert (n >= 0) && (n < 0x100);
    H = n & 0xff;
  }

  /**
   * Gets the register L.
   *
   * @return the register value
   */
  public int getL() {
    return L;
  }

  /**
   * Sets the register L.
   *
   * @param n the new register value
   */
  public void setL(final int n) {
    assert (n >= 0) && (n < 0x100);
    L = n & 0xff;
  }

  /**
   * Gets the register A'.
   *
   * @return the register value
   */
  public int getAa() {
    return Aa;
  }

  /**
   * Sets the register A'.
   *
   * @param n the new register value
   */
  public void setAa(final int n) {
    assert (n >= 0) && (n < 0x100);
    Aa = n & 0xff;
  }

  /**
   * Gets the alternative Flags (F') register.
   *
   * @return the register value
   */
  public int getFa() {
    return Fa;
  }

  /**
   * Sets the alternative Flags (F') register.
   *
   * @param n the new register value
   */
  public void setFa(final int n) {
    assert (n >= 0) && (n < 0x100);
    Fa = n;
  }

  /**
   * Gets the register B'.
   *
   * @return the register value
   */
  public int getBa() {
    return Ba;
  }

  /**
   * Sets the register B'.
   *
   * @param n the new register value
   */
  public void setBa(final int n) {
    assert (n >= 0) && (n < 0x100);
    Ba = n & 0xff;
  }

  /**
   * Gets the register C'.
   *
   * @return the register value
   */
  public int getCa() {
    return Ca;
  }

  /**
   * Sets the register C'.
   *
   * @param n the new register value
   */
  public void setCa(final int n) {
    assert (n >= 0) && (n < 0x100);
    Ca = n & 0xff;
  }

  /**
   * Gets the register D'.
   *
   * @return the register value
   */
  public int getDa() {
    return Da;
  }

  /**
   * Sets the register D'.
   *
   * @param n the new register value
   */
  public void setDa(final int n) {
    assert (n >= 0) && (n < 0x100);
    Da = n & 0xff;
  }

  /**
   * Gets the register E'.
   *
   * @return the register value
   */
  public int getEa() {
    return Ea;
  }

  /**
   * Sets the register E'.
   *
   * @param n the new register value
   */
  public void setEa(final int n) {
    assert (n >= 0) && (n < 0x100);
    Ea = n & 0xff;
  }

  /**
   * Gets the register H'.
   *
   * @return the register value
   */
  public int getHa() {
    return Ha;
  }

  /**
   * Sets the register H'.
   *
   * @param n the new register value
   */
  public void setHa(final int n) {
    assert (n >= 0) && (n < 0x100);
    Ha = n & 0xff;
  }

  /**
   * Gets the register L'.
   *
   * @return the register value
   */
  public int getLa() {
    return La;
  }

  /**
   * Sets the register L'.
   *
   * @param n the new register value
   */
  public void setLa(final int n) {
    assert (n >= 0) && (n < 0x100);
    La = n & 0xff;
  }

  /**
   * Gets the register pair BC.
   *
   * @return register pair value
   */
  public int getBC() {
    return BC();
  }

  /**
   * Sets the register pair BC.
   *
   * @param n the new register pair value
   */
  public void setBC(final int n) {
    assert (n >= 0) && (n < 0x10000);
    B = (n >> 8) & 0xff;
    C = n & 0xff;
  }

  /**
   * Increments the register pair BC.
   */
  protected void incBC() {
    final int tw = (B << 8) + C + 1;
    B = (tw >> 8) & 0xff;
    C = tw & 0xff;
  }

  /**
   * Decrements the register pair BC.
   */
  protected void decBC() {
    final int tw = (B << 8) + C - 1;
    B = (tw >> 8) & 0xff;
    C = tw & 0xff;
  }

  /**
   * Gets the register pair DE.
   *
   * @return register pair value
   */
  public int getDE() {
    return DE();
  }

  /**
   * Sets the register pair DE.
   *
   * @param n the new register pair value
   */
  public void setDE(final int n) {
    assert (n >= 0) && (n < 0x10000);
    D = (n >> 8) & 0xff;
    E = n & 0xff;
  }

  /**
   * Increments the register pair DE.
   */
  protected void incDE() {
    final int tw = (D << 8) + E + 1;
    D = (tw >> 8) & 0xff;
    E = tw & 0xff;
  }

  /**
   * Decrements the register pair DE.
   */
  protected void decDE() {
    final int tw = (D << 8) + E - 1;
    D = (tw >> 8) & 0xff;
    E = tw & 0xff;
  }

  /**
   * Gets the register pair HL.
   *
   * @return register pair value
   */
  public int getHL() {
    return HL();
  }

  /**
   * Sets the register pair HL.
   *
   * @param n the new register pair value
   */
  public void setHL(final int n) {
    assert (n >= 0) && (n < 0x10000);
    H = (n >> 8) & 0xff;
    L = n & 0xff;
  }

  /**
   * Increments the register pair HL.
   */
  protected void incHL() {
    final int tw = (H << 8) + L + 1;
    H = (tw >> 8) & 0xff;
    L = tw & 0xff;
  }

  /**
   * Decrements the register pair HL.
   */
  protected void decHL() {
    final int tw = (H << 8) + L - 1;
    H = (tw >> 8) & 0xff;
    L = tw & 0xff;
  }

  /**
   * Gets the register IX.
   *
   * @return the register value
   */
  public int getIX() {
    return IX;
  }

  /**
   * Sets the register IX.
   *
   * @param n the new register value
   */
  public void setIX(final int n) {
    assert (n >= 0) && (n < 0x10000);
    IX = n;
  }

  /**
   * Gets the register IY.
   *
   * @return the register value
   */
  public int getIY() {
    return IY;
  }

  /**
   * Sets the register IY.
   *
   * @param n the new register value
   */
  public void setIY(final int n) {
    assert (n >= 0) && (n < 0x10000);
    IY = n;
  }

  // for description see Processor
  @Override
  public int getPC() {
    return PC;
  }

  // for description see Processor
  @Override
  public void setPC(final int n) {
    assert (n >= 0) && (n < 0x10000);
    PC = n & 0xffff;
  }

  /**
   * Gets the Stack Pointer (SP).
   *
   * @return the Stack Pointer (SP)
   */
  public int getSP() {
    return SP;
  }

  /**
   * Sets the Stack Pointer (SP).
   *
   * @param n the new value for the Stack Pointer (SP)
   */
  public void setSP(final int n) {
    assert (n >= 0) && (n < 0x10000);
    SP = n & 0xffff;
  }

  /**
   * Gets the register pair WZ.
   *
   * @return the register pair WZ
   */
  public int getWZ() {
    return WZ;
  }

  /**
   * Sets the register pair WZ.
   *
   * @param n the new value for the register pair WZ
   */
  public void setWZ(final int n) {
    assert (n >= 0) && (n < 0x10000);
    WZ = n & 0xffff;
  }

  /**
   * Increments the register pair WZ.
   */
  protected void incWZ() {
    WZ = (WZ + 1) & 0xffff;
  }

  /**
   * Decrements the register pair WZ.
   */
  protected void decWZ() {
    WZ = (WZ - 1) & 0xffff;
  }

  /**
   * Returns {@code true} if the Sign (S) flag is set.
   *
   * @return {@code true} if flag set, {@code false} otherwise
   */
  public boolean isSF() {
    return SFSET();
  }

  /**
   * Sets the Sign (S) flag.
   *
   * @param b new value for the flag
   */
  public void setSF(final boolean b) {
    if (b) {
      SETSF();
    } else {
      CLEARSF();
    }
  }

  /**
   * Returns {@code true} if the Zero (Z) flag is set.
   *
   * @return {@code true} if flag set, {@code false} otherwise
   */
  public boolean isZF() {
    return ZFSET();
  }

  /**
   * Sets the Zero (Z) flag.
   *
   * @param b new value for the flag
   */
  public void setZF(final boolean b) {
    if (b) {
      SETZF();
    } else {
      CLEARZF();
    }
  }

  /**
   * Returns {@code true} if the Half Carry (H) flag is set.
   *
   * @return {@code true} if flag set, {@code false} otherwise
   */
  public boolean isHF() {
    return HFSET();
  }

  /**
   * Sets the Half Carry (H) flag.
   *
   * @param b new value for the flag
   */
  public void setHF(final boolean b) {
    if (b) {
      SETHF();
    } else {
      CLEARHF();
    }
  }

  /**
   * Returns {@code true} if the Parity/Overflow (P) flag is set.
   *
   * @return {@code true} if flag set, {@code false} otherwise
   */
  public boolean isPF() {
    return PFSET();
  }

  /**
   * Sets the Parity/Overflow (P) flag.
   *
   * @param b new value for the flag
   */
  public void setPF(final boolean b) {
    if (b) {
      SETPF();
    } else {
      CLEARPF();
    }
  }

  /**
   * Returns {@code true} if the Carry (C) flag is set.
   *
   * @return {@code true} if flag set, {@code false} otherwise
   */
  public boolean isCF() {
    return CFSET();
  }

  /**
   * Sets the Carry (C) flag.
   *
   * @param b new value for the flag
   */
  public void setCF(final boolean b) {
    if (b) {
      SETCF();
    } else {
      CLEARCF();
    }
  }

  // for description see Processor
  @Override
  public boolean isIE() {
    return IFF1;
  }

  /**
   * Enables/disables interrupts.
   *
   * @param b if {@code true}, interrupts will be enabled
   */
  public void setIE(final boolean b) {
    IFF1 = b;
  }

  /**
   * Gets the program cycle counter.  It is incremented on every completed
   * instruction by the number of elapsed system clock cycles.
   *
   * @return current program cycle counter
   */
  public long getCycleCounter() {
    return cycleCounter;
  }

  /**
   * Sets the program cycle counter.
   *
   * @param n new value for the program cycle counter
   */
  public void setCycleCounter(final long n) {
    assert (n >= 0);
    cycleCounter = n;
  }

  // for description see SystemClockSource
  @Override
  public long getSystemClock() {
    return cycleCounter;
  }

  /**
   * The array of unprefixed opcodes.
   */
  protected final Opcode[] opcodes = new Opcode[] {

    // 00
    new Opcode("NOP", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 4;
	}
      }
      ),

    // 01
    new Opcode("LD", "BC,<nn>", 3, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  C = memory.getByte(PC);
	  incPC();
	  B = memory.getByte(PC);
	  incPC();
	  return 10;
	}
      }
      ),

    // 02
    new Opcode("LD", "(BC),A", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  memory.setByte(BC(), A);
	  WZ = ((C + 1) & 0xff) + (A << 8);
	  incPC();
	  return 7;
	}
      }
      ),

    // 03
    new Opcode("INC", "BC", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C = (C + 1) & 0xff;
	  if (C == 0) {
	    B = (B + 1) & 0xff;
	  }
	  incPC();
	  return 6;
	}
      }
      ),

    // 04
    new Opcode("INC", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B = (B + 1) & 0xff;
	  F4(B);
	  if (B == 0x80) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if ((B & 0x0f) == 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 05
    new Opcode("DEC", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B = (B - 1) & 0xff;
	  F4(B);
	  if (B == 0x7f) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if ((B & 0x0f) == 0x0f) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 06
    new Opcode("LD", "B,<n>", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  B = memory.getByte(PC);
	  incPC();
	  return 7;
	}
      }
      ),

    // 07
    new Opcode("RLCA", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((A & 0x80) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = ((A << 1) | (F & 1)) & 0xff;
	  F2(A);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 08
    new Opcode("EX", "AF,AF'", 1, 0, new Executable() {
	@Override
	public int exec() {
	  int tb = A;
	  A = Aa;
	  Aa = tb;
	  tb = F;
	  F = Fa;
	  Fa = tb;
	  incPC();
	  return 4;
	}
      }
      ),

    // 09
    new Opcode("ADD", "HL,BC", 1, 0, new Executable() {
	@Override
	public int exec() {
	  WZ = (HL() + 1) & 0xffff;
	  final int ti = (H << 8) + L + (B << 8) + C;
	  if (((H ^ B ^ (ti >> 8)) & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  L = ti & 0xff;
	  H = (ti >> 8) & 0xff;
	  F2(H);
	  if (ti > 0xffff) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  CLEARNF();
	  incPC();
	  return 11;
	}
      }
      ),

    // 0a
    new Opcode("LD", "A,(BC)", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  A = memory.getByte(BC());
	  WZ = (BC() + 1) & 0xffff;
	  incPC();
	  return 7;
	}
      }
      ),

    // 0b
    new Opcode("DEC", "BC", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if (--C < 0) {
	    C = 0xff;
	    B = (B - 1) & 0xff;
	  }
	  incPC();
	  return 6;
	}
      }
      ),

    // 0c
    new Opcode("INC", "C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C = (C + 1) & 0xff;
	  F4(C);
	  if (C == 0x80) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if ((C & 0x0f) == 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 0d
    new Opcode("DEC", "C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C = (C - 1) & 0xff;
	  F4(C);
	  if (C == 0x7f) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if ((C & 0x0f) == 0x0f) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 0e
    new Opcode("LD", "C,<n>", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  C = memory.getByte(PC);
	  incPC();
	  return 7;
	}
      }
      ),

    // 0f
    new Opcode("RRCA", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((A & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = ((A >> 1) | (F << 7)) & 0xff;
	  F2(A);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 10
    new Opcode("DJNZ", "<e>", 1, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  B = (B - 1) & 0xff;
	  if (B == 0) {
	    incPC(2);
	    return 8;
	  } else {
	    incPC();
	    PC = WZ = (PC + 1 + (byte)(memory.getByte(PC))) & 0xffff;
	    return 13;
	  }
	}
      }
      ),

    // 11
    new Opcode("LD", "DE,<nn>", 3, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  E = memory.getByte(PC);
	  incPC();
	  D = memory.getByte(PC);
	  incPC();
	  return 10;
	}
      }
      ),

    // 12
    new Opcode("LD", "(DE),A", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  memory.setByte(DE(), A);
	  WZ = ((E + 1) & 0xff) + (A << 8);
	  incPC();
	  return 7;
	}
      }
      ),

    // 13
    new Opcode("INC", "DE", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E = (E + 1) & 0xff;
	  if (E == 0) {
	    D = (D + 1) & 0xff;
	  }
	  incPC();
	  return 6;
	}
      }
      ),

    // 14
    new Opcode("INC", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D = (D + 1) & 0xff;
	  F4(D);
	  if (D == 0x80) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if ((D & 0x0f) == 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 15
    new Opcode("DEC", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D = (D - 1) & 0xff;
	  F4(D);
	  if (D == 0x7f) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if ((D & 0x0f) == 0x0f) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 16
    new Opcode("LD", "D,<n>", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  D = memory.getByte(PC);
	  incPC();
	  return 7;
	}
      }
      ),

    // 17
    new Opcode("RLA", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tb = A;
	  A = ((A << 1) | (F & 1)) & 0xff;
	  if ((tb & 0x80) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  F2(A);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 18
    new Opcode("JR", "<e>", 1, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  PC = WZ = (PC + 1 + (byte)(memory.getByte(PC))) & 0xffff;
	  return 12;
	}
      }
      ),

    // 19
    new Opcode("ADD", "HL,DE", 1, 0, new Executable() {
	@Override
	public int exec() {
	  WZ = (HL() + 1) & 0xffff;
	  final int ti = (H << 8) + L + (D << 8) + E;
	  if (((H ^ D ^ (ti >> 8)) & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  L = ti & 0xff;
	  H = (ti >> 8) & 0xff;
	  F2(H);
	  if (ti > 0xffff) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  CLEARNF();
	  incPC();
	  return 11;
	}
      }
      ),

    // 1a
    new Opcode("LD", "A,(DE)", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  A = memory.getByte(DE());
	  WZ = (DE() + 1) & 0xffff;
	  incPC();
	  return 7;
	}
      }
      ),

    // 1b
    new Opcode("DEC", "DE", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if (--E < 0) {
	    E = 0xff;
	    D = (D - 1) & 0xff;
	  }
	  incPC();
	  return 6;
	}
      }
      ),

    // 1c
    new Opcode("INC", "E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E = (E + 1) & 0xff;
	  F4(E);
	  if (E == 0x80) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if ((E & 0x0f) == 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 1d
    new Opcode("DEC", "E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E = (E - 1) & 0xff;
	  F4(E);
	  if (E == 0x7f) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if ((E & 0x0f) == 0x0f) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 1e
    new Opcode("LD", "E,<n>", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  E = memory.getByte(PC);
	  incPC();
	  return 7;
	}
      }
      ),

    // 1f
    new Opcode("RRA", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tb = A;
	  A = ((A >> 1) | (F << 7)) & 0xff;
	  if ((tb & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  F2(A);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 20
    new Opcode("JR", "NZ,<e>", 1, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  if (ZFSET()) {
	    incPC(2);
	    return 7;
	  } else {
	    incPC();
	    PC = WZ = (PC + 1 + (byte)(memory.getByte(PC))) & 0xffff;
	    return 12;
	  }
	}
      }
      ),

    // 21
    new Opcode("LD", "HL,<nn>", 3, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  L = memory.getByte(PC);
	  incPC();
	  H = memory.getByte(PC);
	  incPC();
	  return 10;
	}
      }
      ),

    // 22
    new Opcode("LD", "(<nn>),HL", 3, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  memory.setByte(WZ, L);
	  incWZ();
	  memory.setByte(WZ, H);
	  incPC();
	  return 16;
	}
      }
      ),

    // 23
    new Opcode("INC", "HL", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L = (L + 1) & 0xff;
	  if (L == 0) {
	    H = (H + 1) & 0xff;
	  }
	  incPC();
	  return 6;
	}
      }
      ),

    // 24
    new Opcode("INC", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H = (H + 1) & 0xff;
	  F4(H);
	  if (H == 0x80) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if ((H & 0x0f) == 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 25
    new Opcode("DEC", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H = (H - 1) & 0xff;
	  F4(H);
	  if (H == 0x7f) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if ((H & 0x0f) == 0x0f) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 26
    new Opcode("LD", "H,<n>", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  H = memory.getByte(PC);
	  incPC();
	  return 7;
	}
      }
      ),

    // 27
    new Opcode("DAA", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int ld = A & 0x0f;
	  if (NFSET()) {
	    final boolean hd = CFSET() || (A > 0x99);
	    if (HFSET() || (ld > 9)) {
	      if (ld > 5) {
		CLEARHF();
	      }
	      A = (A - 6) & 0xff;
	    }
	    if (hd) {
	      A -= 0x0160;
	    }
	  } else {
	    if (HFSET() || (ld > 9)) {
	      if (ld > 9) {
		SETHF();
	      } else {
		CLEARHF();
	      }
	      A += 6;
	    }
	    if (CFSET() || ((A & 0x01f0) > 0x90)) {
	      A += 0x60;
	    }
	  }
	  if (((A >> 8) & 1) == 1) {
	    SETCF();
	  }
	  A &= 0xff;
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // 28
    new Opcode("JR", "Z,<e>", 1, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  if (!ZFSET()) {
	    incPC(2);
	    return 7;
	  } else {
	    incPC();
	    PC = WZ = (PC + 1 + (byte)(memory.getByte(PC))) & 0xffff;
	    return 12;
	  }
	}
      }
      ),

    // 29
    new Opcode("ADD", "HL,HL", 1, 0, new Executable() {
	@Override
	public int exec() {
	  WZ = (HL() + 1) & 0xffff;
	  final int ti = HL() << 1;
	  if (((ti >> 8) & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  L = ti & 0xff;
	  H = (ti >> 8) & 0xff;
	  F2(H);
	  if (ti > 0xffff) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  CLEARNF();
	  incPC();
	  return 11;
	}
      }
      ),

    // 2a
    new Opcode("LD", "HL,(<nn>)", 3, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  L = memory.getByte(WZ);
	  incWZ();
	  H = memory.getByte(WZ);
	  incPC();
	  return 16;
	}
      }
      ),

    // 2b
    new Opcode("DEC", "HL", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if (--L < 0) {
	    L = 0xff;
	    H = (H - 1) & 0xff;
	  }
	  incPC();
	  return 6;
	}
      }
      ),

    // 2c
    new Opcode("INC", "L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L = (L + 1) & 0xff;
	  F4(L);
	  if (L == 0x80) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if ((L & 0x0f) == 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 2d
    new Opcode("DEC", "L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L = (L - 1) & 0xff;
	  F4(L);
	  if (L == 0x7f) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if ((L & 0x0f) == 0x0f) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 2e
    new Opcode("LD", "L,<n>", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  L = memory.getByte(PC);
	  incPC();
	  return 7;
	}
      }
      ),

    // 2f
    new Opcode("CPL", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = (~A) & 0xff;
	  F2(A);
	  SETHF();
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 30
    new Opcode("JR", "NC,<e>", 1, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  if (CFSET()) {
	    incPC(2);
	    return 7;
	  } else {
	    incPC();
	    PC = WZ = (PC + 1 + (byte)(memory.getByte(PC))) & 0xffff;
	    return 12;
	  }
	}
      }
      ),

    // 31
    new Opcode("LD", "SP,<nn>", 3, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int tb = memory.getByte(PC);
	  incPC();
	  SP = tb + (memory.getByte(PC) << 8);
	  incPC();
	  return 10;
	}
      }
      ),

    // 32
    new Opcode("LD", "(<nn>),A", 3, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  memory.setByte(WZ + (memory.getByte(PC) << 8), A);
	  WZ += A << 8;
	  incPC();
	  return 13;
	}
      }
      ),

    // 33
    new Opcode("INC", "SP", 1, 0, new Executable() {
	@Override
	public int exec() {
	  incSP();
	  incPC();
	  return 6;
	}
      }
      ),

    // 34
    new Opcode("INC", "(HL)",
	       1,
	       Processor.INS_MR | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  final int tw = HL();
	  final int tb = (memory.getByte(tw) + 1) & 0xff;
	  memory.setByte(tw, tb);
	  F4(tb);
	  if (tb == 0x80) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if ((tb & 0x0f) == 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  CLEARNF();
	  incPC();
	  return 11;
	}
      }
      ),

    // 35
    new Opcode("DEC", "M",
	       1,
	       Processor.INS_MR | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  final int tw = HL();
	  final int tb = (memory.getByte(tw) - 1) & 0xff;
	  memory.setByte(tw, tb);
	  F4(tb);
	  if (tb == 0x7f) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if ((tb & 0x0f) == 0x0f) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  SETNF();
	  incPC();
	  return 11;
	}
      }
      ),

    // 36
    new Opcode("LD", "(HL),<n>", 2, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  final int tw = HL();
	  incPC();
	  memory.setByte(tw, memory.getByte(PC));
	  incPC();
	  return 10;
	}
      }
      ),

    // 37
    new Opcode("SCF", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  SETCF();
	  F2(A);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 38
    new Opcode("JR", "C,<e>", 1, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  if (!CFSET()) {
	    incPC(2);
	    return 7;
	  } else {
	    incPC();
	    PC = WZ = (PC + 1 + (byte)(memory.getByte(PC))) & 0xffff;
	    return 12;
	  }
	}
      }
      ),

    // 39
    new Opcode("ADD", "HL,SP", 1, 0, new Executable() {
	@Override
	public int exec() {
	  WZ = (HL() + 1) & 0xffff;
	  final int ti = (H << 8) + L + SP;
	  if (((H ^ ((SP ^ ti) >> 8)) & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  L = ti & 0xff;
	  H = (ti >> 8) & 0xff;
	  F2(H);
	  if (ti > 0xffff) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  CLEARNF();
	  incPC();
	  return 11;
	}
      }
      ),

    // 3a
    new Opcode("LD", "A,(<nn>)", 3, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  A = memory.getByte(WZ);
	  incWZ();
	  incPC();
	  return 13;
	}
      }
      ),

    // 3b
    new Opcode("DEC", "SP", 1, 0, new Executable() {
	@Override
	public int exec() {
	  decSP();
	  incPC();
	  return 6;
	}
      }
      ),

    // 3c
    new Opcode("INC", "A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = (A + 1) & 0xff;
	  F4(A);
	  if (A == 0x80) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if ((A & 0x0f) == 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 3d
    new Opcode("DEC", "A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = (A - 1) & 0xff;
	  F4(A);
	  if (A == 0x7f) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if ((A & 0x0f) == 0x0f) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 3e
    new Opcode("LD", "A,<n>", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  A = memory.getByte(PC);
	  incPC();
	  return 7;
	}
      }
      ),

    // 3f
    new Opcode("CCF", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F ^= CF;
	  F2(A);
	  if (CFSET()) {
	    CLEARHF();
	  } else {
	    SETHF();
	  }
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 40
    new Opcode("LD", "B,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 4;
	}
      }
      ),

    // 41
    new Opcode("LD", "B,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B = C;
	  incPC();
	  return 4;
	}
      }
      ),

    // 42
    new Opcode("LD", "B,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B = D;
	  incPC();
	  return 4;
	}
      }
      ),

    // 43
    new Opcode("LD", "B,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B = E;
	  incPC();
	  return 4;
	}
      }
      ),

    // 44
    new Opcode("LD", "B,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B = H;
	  incPC();
	  return 4;
	}
      }
      ),

    // 45
    new Opcode("LD", "B,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B = L;
	  incPC();
	  return 4;
	}
      }
      ),

    // 46
    new Opcode("LD", "B,M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  B = memory.getByte(HL());
	  incPC();
	  return 7;
	}
      }
      ),

    // 47
    new Opcode("LD", "B,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B = A;
	  incPC();
	  return 4;
	}
      }
      ),

    // 48
    new Opcode("LD", "C,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C = B;
	  incPC();
	  return 4;
	}
      }
      ),

    // 49
    new Opcode("LD", "C,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 4;
	}
      }
      ),

    // 4a
    new Opcode("LD", "C,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C = D;
	  incPC();
	  return 4;
	}
      }
      ),

    // 4b
    new Opcode("LD", "C,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C = E;
	  incPC();
	  return 4;
	}
      }
      ),

    // 4c
    new Opcode("LD", "C,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C = H;
	  incPC();
	  return 4;
	}
      }
      ),

    // 4d
    new Opcode("LD", "C,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C = L;
	  incPC();
	  return 4;
	}
      }
      ),


    // 4e
    new Opcode("LD", "C,M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  C = memory.getByte(HL());
	  incPC();
	  return 7;
	}
      }
      ),

    // 4f
    new Opcode("LD", "C,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C = A;
	  incPC();
	  return 4;
	}
      }
      ),

    // 50
    new Opcode("LD", "D,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D = B;
	  incPC();
	  return 4;
	}
      }
      ),

    // 51
    new Opcode("LD", "D,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D = C;
	  incPC();
	  return 4;
	}
      }
      ),

    // 52
    new Opcode("LD", "D,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 4;
	}
      }
      ),

    // 53
    new Opcode("LD", "D,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D = E;
	  incPC();
	  return 4;
	}
      }
      ),

    // 54
    new Opcode("LD", "D,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D = H;
	  incPC();
	  return 4;
	}
      }
      ),

    // 55
    new Opcode("LD", "D,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D = L;
	  incPC();
	  return 4;
	}
      }
      ),

    // 56
    new Opcode("LD", "D,M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  D = memory.getByte(HL());
	  incPC();
	  return 7;
	}
      }
      ),

    // 57
    new Opcode("LD", "D,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D = A;
	  incPC();
	  return 4;
	}
      }
      ),

    // 58
    new Opcode("LD", "E,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E = B;
	  incPC();
	  return 4;
	}
      }
      ),

    // 59
    new Opcode("LD", "E,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E = C;
	  incPC();
	  return 4;
	}
      }
      ),

    // 5a
    new Opcode("LD", "E,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E = D;
	  incPC();
	  return 4;
	}
      }
      ),

    // 5b
    new Opcode("LD", "E,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 4;
	}
      }
      ),

    // 5c
    new Opcode("LD", "E,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E = H;
	  incPC();
	  return 4;
	}
      }
      ),

    // 5d
    new Opcode("LD", "E,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E = L;
	  incPC();
	  return 4;
	}
      }
      ),

    // 5e
    new Opcode("LD", "E,M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  E = memory.getByte(HL());
	  incPC();
	  return 7;
	}
      }
      ),

    // 5f
    new Opcode("LD", "E,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E = A;
	  incPC();
	  return 4;
	}
      }
      ),

    // 60
    new Opcode("LD", "H,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H = B;
	  incPC();
	  return 4;
	}
      }
      ),

    // 61
    new Opcode("LD", "H,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H = C;
	  incPC();
	  return 4;
	}
      }
      ),

    // 62
    new Opcode("LD", "H,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H = D;
	  incPC();
	  return 4;
	}
      }
      ),

    // 63
    new Opcode("LD", "H,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H = E;
	  incPC();
	  return 4;
	}
      }
      ),

    // 64
    new Opcode("LD", "H,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 4;
	}
      }
      ),

    // 65
    new Opcode("LD", "H,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H = L;
	  incPC();
	  return 4;
	}
      }
      ),

    // 66
    new Opcode("LD", "H,M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  H = memory.getByte(HL());
	  incPC();
	  return 7;
	}
      }
      ),

    // 67
    new Opcode("LD", "H,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H = A;
	  incPC();
	  return 4;
	}
      }
      ),

    // 68
    new Opcode("LD", "L,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L = B;
	  incPC();
	  return 4;
	}
      }
      ),

    // 69
    new Opcode("LD", "L,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L = C;
	  incPC();
	  return 4;
	}
      }
      ),

    // 6a
    new Opcode("LD", "L,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L = D;
	  incPC();
	  return 4;
	}
      }
      ),

    // 6b
    new Opcode("LD", "L,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L = E;
	  incPC();
	  return 4;
	}
      }
      ),

    // 6c
    new Opcode("LD", "L,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L = H;
	  incPC();
	  return 4;
	}
      }
      ),

    // 6d
    new Opcode("LD", "L,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 4;
	}
      }
      ),

    // 6e
    new Opcode("LD", "L,M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  L = memory.getByte(HL());
	  incPC();
	  return 7;
	}
      }
      ),

    // 6f
    new Opcode("LD", "L,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L = A;
	  incPC();
	  return 4;
	}
      }
      ),

    // 70
    new Opcode("LD", "M,B", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  final int tw = HL();
	  memory.setByte(tw, B);
	  incPC();
	  return 7;
	}
      }
      ),

    // 71
    new Opcode("LD", "M,C", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  final int tw = HL();
	  memory.setByte(tw, C);
	  incPC();
	  return 7;
	}
      }
      ),

    // 72
    new Opcode("LD", "M,D", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  final int tw = HL();
	  memory.setByte(tw, D);
	  incPC();
	  return 7;
	}
      }
      ),

    // 73
    new Opcode("LD", "M,E", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  final int tw = HL();
	  memory.setByte(tw, E);
	  incPC();
	  return 7;
	}
      }
      ),

    // 74
    new Opcode("LD", "M,H", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  final int tw = HL();
	  memory.setByte(tw, H);
	  incPC();
	  return 7;
	}
      }
      ),

    // 75
    new Opcode("LD", "M,L", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  final int tw = HL();
	  memory.setByte(tw, L);
	  incPC();
	  return 7;
	}
      }
      ),

    // 76
    new Opcode("HALT", "", 1, Processor.INS_HLT, new Executable() {
	@Override
	public int exec() {
	  return 4;
	}
      }
      ),

    // 77
    new Opcode("LD", "M,A", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  final int tw = HL();
	  memory.setByte(tw, A);
	  incPC();
	  return 7;
	}
      }
      ),

    // 78
    new Opcode("LD", "A,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = B;
	  incPC();
	  return 4;
	}
      }
      ),

    // 79
    new Opcode("LD", "A,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = C;
	  incPC();
	  return 4;
	}
      }
      ),

    // 7a
    new Opcode("LD", "A,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = D;
	  incPC();
	  return 4;
	}
      }
      ),

    // 7b
    new Opcode("LD", "A,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = E;
	  incPC();
	  return 4;
	}
      }
      ),

    // 7c
    new Opcode("LD", "A,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = H;
	  incPC();
	  return 4;
	}
      }
      ),

    // 7d
    new Opcode("LD", "A,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = L;
	  incPC();
	  return 4;
	}
      }
      ),

    // 7e
    new Opcode("LD", "A,M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  A = memory.getByte(HL());
	  incPC();
	  return 7;
	}
      }
      ),

    // 7f
    new Opcode("LD", "A,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 4;
	}
      }
      ),

    // 80
    new Opcode("ADD", "A,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + B;
	  final int cb = A ^ B ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 81
    new Opcode("ADD", "A,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + C;
	  final int cb = A ^ C ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 82
    new Opcode("ADD", "A,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + D;
	  final int cb = A ^ D ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 83
    new Opcode("ADD", "A,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + E;
	  final int cb = A ^ E ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 84
    new Opcode("ADD", "A,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + H;
	  final int cb = A ^ H ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 85
    new Opcode("ADD", "A,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + L;
	  final int cb = A ^ L ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 86
    new Opcode("ADD", "A,(HL)", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  final int tb = memory.getByte(HL());
	  final int tw = A + tb;
	  final int cb = A ^ tb ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 7;
	}
      }
      ),

    // 87
    new Opcode("ADD", "A,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A << 1;
	  if ((tw & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((tw ^ (tw >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 88
    new Opcode("ADC", "A,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + B + (F & CF);
	  final int cb = A ^ B ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 89
    new Opcode("ADC", "A,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + C + (F & CF);
	  final int cb = A ^ C ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 8a
    new Opcode("ADC", "A,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + D + (F & CF);
	  final int cb = A ^ D ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 8b
    new Opcode("ADC", "A,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + E + (F & CF);
	  final int cb = A ^ E ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 8c
    new Opcode("ADC", "A,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + H + (F & CF);
	  final int cb = A ^ H ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 8d
    new Opcode("ADC", "A,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + L + (F & CF);
	  final int cb = A ^ L ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 8e
    new Opcode("ADC", "A,(HL)", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  final int tb = memory.getByte(HL());
	  final int tw = A + tb + (F & CF);
	  final int cb = A ^ tb ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 7;
	}
      }
      ),

    // 8f
    new Opcode("ADC", "A,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = (A << 1) + (F & CF);
	  if ((tw & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((tw ^ (tw >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 90
    new Opcode("SUB", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - B;
	  final int cb = A ^ B ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 91
    new Opcode("SUB", "C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - C;
	  final int cb = A ^ C ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 92
    new Opcode("SUB", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - D;
	  final int cb = A ^ D ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 93
    new Opcode("SUB", "E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - E;
	  final int cb = A ^ E ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 94
    new Opcode("SUB", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - H;
	  final int cb = A ^ H ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 95
    new Opcode("SUB", "L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - L;
	  final int cb = A ^ L ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 96
    new Opcode("SUB", "(HL)", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  final int tb = memory.getByte(HL());
	  final int tw = A - tb;
	  final int cb = A ^ tb ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 7;
	}
      }
      ),

    // 97
    new Opcode("SUB", "A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  CLEARHF();
	  CLEARCF();
	  CLEARPF();
	  A = 0;
	  F4(0);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 98
    new Opcode("SBC", "A,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - B - (F & CF);
	  final int cb = A ^ B ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 99
    new Opcode("SBC", "A,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - C - (F & CF);
	  final int cb = A ^ C ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 9a
    new Opcode("SBC", "A,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - D - (F & CF);
	  final int cb = A ^ D ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 9b
    new Opcode("SBC", "A,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - E - (F & CF);
	  final int cb = A ^ E ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 9c
    new Opcode("SBC", "A,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - H - (F & CF);
	  final int cb = A ^ H ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 9d
    new Opcode("SBC", "A,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - L - (F & CF);
	  final int cb = A ^ L ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // 9e
    new Opcode("SBC", "A,(HL)", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  final int tb = memory.getByte(HL());
	  final int tw = A - tb - (F & CF);
	  final int cb = A ^ tb ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 7;
	}
      }
      ),

    // 9f
    new Opcode("SBC", "A,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = -(F & CF);
	  if ((tw & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((tw ^ (tw >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // a0
    new Opcode("AND", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A &= B;
	  SETHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // a1
    new Opcode("AND", "C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A &= C;
	  SETHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // a2
    new Opcode("AND", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A &= D;
	  SETHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // a3
    new Opcode("AND", "E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A &= E;
	  SETHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // a4
    new Opcode("AND", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A &= H;
	  SETHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // a5
    new Opcode("AND", "L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A &= L;
	  SETHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // a6
    new Opcode("AND", "(HL)", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  final int tb = memory.getByte(HL());
	  A &= tb;
	  SETHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 7;
	}
      }
      ),

    // a7
    new Opcode("AND", "A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  SETHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // a8
    new Opcode("XOR", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A ^= B;
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // a9
    new Opcode("XOR", "C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A ^= C;
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // aa
    new Opcode("XOR", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A ^= D;
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // ab
    new Opcode("XOR", "E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A ^= E;
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // ac
    new Opcode("XOR", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A ^= H;
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // ad
    new Opcode("XOR", "L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A ^= L;
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // ae
    new Opcode("XOR", "(HL)", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  A ^= memory.getByte(HL());
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 7;
	}
      }
      ),

    // af
    new Opcode("XOR", "A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = 0;
	  F = ZF | PF;
	  incPC();
	  return 4;
	}
      }
      ),

    // b0
    new Opcode("OR", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A |= B;
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // b1
    new Opcode("OR", "C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A |= C;
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // b2
    new Opcode("OR", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A |= D;
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // b3
    new Opcode("OR", "E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A |= E;
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // b4
    new Opcode("OR", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A |= H;
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // b5
    new Opcode("OR", "L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A |= L;
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // b6
    new Opcode("OR", "(HL)", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  A |= memory.getByte(HL());
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 7;
	}
      }
      ),

    // b7
    new Opcode("OR", "A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // b8
    new Opcode("CP", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - B;
	  final int cb = A ^ B ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  F22(tw & 0xff, B);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // b9
    new Opcode("CP", "C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - C;
	  final int cb = A ^ C ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  F22(tw & 0xff, C);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // ba
    new Opcode("CP", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - D;
	  final int cb = A ^ D ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  F22(tw & 0xff, D);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // bb
    new Opcode("CP", "E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - E;
	  final int cb = A ^ E ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  F22(tw & 0xff, E);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // bc
    new Opcode("CP", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - H;
	  final int cb = A ^ H ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  F22(tw & 0xff, H);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // bd
    new Opcode("CP", "L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - L;
	  final int cb = A ^ L ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  F22(tw & 0xff, L);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // be
    new Opcode("CP", "(HL)", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  final int tb = memory.getByte(HL());
	  final int tw = A - tb;
	  final int cb = A ^ tb ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  F22(tw & 0xff, tb);
	  SETNF();
	  incPC();
	  return 7;
	}
      }
      ),

    // bf
    new Opcode("CP", "A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  CLEARHF();
	  CLEARCF();
	  CLEARPF();
	  F22(0, A);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // c0
    new Opcode("RET", "NZ",
	       1,
	       Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  if (ZFSET()) {
	    incPC();
	    return 5;
	  } else {
	    WZ = memory.getByte(SP);
	    incSP();
	    WZ += memory.getByte(SP) << 8;
	    incSP();
	    PC = WZ;
	    return 11;
	  }
	}
      }
      ),

    // c1
    new Opcode("POP", "BC", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  C = memory.getByte(SP);
	  incSP();
	  B = memory.getByte(SP);
	  incSP();
	  incPC();
	  return 10;
	}
      }
      ),

    // c2
    new Opcode("JP", "NZ,<nn>", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  if (ZFSET()) {
	    incPC();
	  } else {
	    PC = WZ;
	  }
	  return 10;
	}
      }
      ),

    // c3
    new Opcode("JP", "<nn>", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  PC = WZ;
	  return 10;
	}
      }
      ),

    // c4
    new Opcode("CALL", "NZ,<nn>",
	       3,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  incPC();
	  if (ZFSET()) {
	    return 10;
	  } else {
	    decSP();
	    memory.setByte(SP, PC >> 8);
	    decSP();
	    memory.setByte(SP, PC & 0xff);
	    PC = WZ;
	    return 17;
	  }
	}
      }
      ),

    // c5
    new Opcode("PUSH", "BC", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  decSP();
	  memory.setByte(SP, B);
	  decSP();
	  memory.setByte(SP, C);
	  incPC();
	  return 11;
	}
      }
      ),

    // c6
    new Opcode("ADD", "A,<n>", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int tb = memory.getByte(PC);
	  final int tw = A + tb;
	  final int cb = A ^ tb ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 7;
	}
      }
      ),

    // c7
    new Opcode("RST", "<p>",
	       1,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  decSP();
	  memory.setByte(SP, PC >> 8);
	  decSP();
	  memory.setByte(SP, PC & 0xff);
	  PC = WZ = 0x0000;
	  return 11;
	}
      }
      ),

    // c8
    new Opcode("RET", "Z",
	       1,
	       Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  if (!ZFSET()) {
	    incPC();
	    return 5;
	  } else {
	    WZ = memory.getByte(SP);
	    incSP();
	    WZ += memory.getByte(SP) << 8;
	    incSP();
	    PC = WZ;
	    return 11;
	  }
	}
      }
      ),

    // c9
    new Opcode("RET", "",
	       1,
	       Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  WZ = memory.getByte(SP);
	  incSP();
	  WZ += memory.getByte(SP) << 8;
	  incSP();
	  PC = WZ;
	  return 10;
	}
      }
      ),

    // ca
    new Opcode("JP", "Z,<nn>", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  if (!ZFSET()) {
	    incPC();
	  } else {
	    PC = WZ;
	  }
	  return 10;
	}
      }
      ),

    // cb
    null,

    // cc
    new Opcode("CALL", "Z,<nn>",
	       3,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  incPC();
	  if (!ZFSET()) {
	    return 10;
	  } else {
	    decSP();
	    memory.setByte(SP, PC >> 8);
	    decSP();
	    memory.setByte(SP, PC & 0xff);
	    PC = WZ;
	    return 17;
	  }
	}
      }
      ),

    // cd
    new Opcode("CALL", "<nn>",
	       3,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  incPC();
	  decSP();
	  memory.setByte(SP, PC >> 8);
	  decSP();
	  memory.setByte(SP, PC & 0xff);
	  PC = WZ;
	  return 17;
	}
      }
      ),

    // ce
    new Opcode("ADC", "A,<n>", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int tb = memory.getByte(PC);
	  final int tw = A + tb + (F & CF);
	  final int cb = A ^ tb ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 7;
	}
      }
      ),

    // cf
    new Opcode("RST", "<p>",
	       1,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  decSP();
	  memory.setByte(SP, PC >> 8);
	  decSP();
	  memory.setByte(SP, PC & 0xff);
	  PC = WZ = 0x0008;
	  return 11;
	}
      }
      ),

    // d0
    new Opcode("RET", "NC",
	       1,
	       Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  if (CFSET()) {
	    incPC();
	    return 5;
	  } else {
	    WZ = memory.getByte(SP);
	    incSP();
	    WZ += memory.getByte(SP) << 8;
	    incSP();
	    PC = WZ;
	    return 11;
	  }
	}
      }
      ),

    // d1
    new Opcode("POP", "DE", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  E = memory.getByte(SP);
	  incSP();
	  D = memory.getByte(SP);
	  incSP();
	  incPC();
	  return 10;
	}
      }
      ),

    // d2
    new Opcode("JP", "NC,<nn>", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  if (CFSET()) {
	    incPC();
	  } else {
	    PC = WZ;
	  }
	  return 10;
	}
      }
      ),

    // d3
    new Opcode("OUT", "(<n>),A", 2, Processor.INS_IOW, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  int port = memory.getByte(PC);
	  for (IOElement t: outputPorts.get(port)) {
	    t.portOutput(port, A);
	  }
	  WZ = ((port + 1) & 0xff) + (A << 8);
	  incPC();
	  return 11;
	}
      }
      ),

    // d4
    new Opcode("CALL", "NC,<nn>",
	       3,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  incPC();
	  if (CFSET()) {
	    return 10;
	  } else {
	    decSP();
	    memory.setByte(SP, PC >> 8);
	    decSP();
	    memory.setByte(SP, PC & 0xff);
	    PC = WZ;
	    return 17;
	  }
	}
      }
      ),

    // d5
    new Opcode("PUSH", "DE", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  decSP();
	  memory.setByte(SP, D);
	  decSP();
	  memory.setByte(SP, E);
	  incPC();
	  return 11;
	}
      }
      ),

    // d6
    new Opcode("SUB", "<n>", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int tb = memory.getByte(PC);
	  final int tw = A - tb;
	  final int cb = A ^ tb ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 7;
	}
      }
      ),

    // d7
    new Opcode("RST", "<p>",
	       1,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  decSP();
	  memory.setByte(SP, PC >> 8);
	  decSP();
	  memory.setByte(SP, PC & 0xff);
	  PC = WZ = 0x0010;
	  return 11;
	}
      }
      ),

    // d8
    new Opcode("RET", "C",
	       1,
	       Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  if (!CFSET()) {
	    incPC();
	    return 5;
	  } else {
	    WZ = memory.getByte(SP);
	    incSP();
	    WZ += memory.getByte(SP) << 8;
	    incSP();
	    PC = WZ;
	    return 11;
	  }
	}
      }
      ),

    // d9
    new Opcode("EXX", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  int tb = B;
	  B = Ba;
	  Ba = tb;
	  tb = C;
	  C = Ca;
	  Ca = tb;
	  tb = D;
	  D = Da;
	  Da = tb;
	  tb = E;
	  E = Ea;
	  Ea = tb;
	  tb = H;
	  H = Ha;
	  Ha = tb;
	  tb = L;
	  L = La;
	  La = tb;
	  tb = WZ;
	  WZ = WZa;
	  WZa = tb;
	  incPC();
	  return 4;
	}
      }
      ),

    // da
    new Opcode("JP", "C,<nn>",
	       3,
	       Processor.INS_JMP,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  if (!CFSET()) {
	    incPC();
	  } else {
	    PC = WZ;
	  }
	  return 10;
	}
      }
      ),

    // db
    new Opcode("IN", "A,(<n>)", 2, Processor.INS_IOR, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  int port = memory.getByte(PC);
	  WZ = ((A << 8) + port + 1) & 0xffff;
	  A = 0xff;
	  for (IOElement t: inputPorts.get(port)) {
	    A &= t.portInput(port);
	  }
	  incPC();
	  return 11;
	}
      }
      ),

    // dc
    new Opcode("CALL", "C,<nn>",
	       3,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  incPC();
	  if (!CFSET()) {
	    return 10;
	  } else {
	    decSP();
	    memory.setByte(SP, PC >> 8);
	    decSP();
	    memory.setByte(SP, PC & 0xff);
	    PC = WZ;
	    return 17;
	  }
	}
      }
      ),

    // dd
    null,

    // de
    new Opcode("SBC", "A,<n>", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int tb = memory.getByte(PC);
	  final int tw = A - tb - (F & CF);
	  final int cb = A ^ tb ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 7;
	}
      }
      ),

    // df
    new Opcode("RST", "<p>",
	       1,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  decSP();
	  memory.setByte(SP, PC >> 8);
	  decSP();
	  memory.setByte(SP, PC & 0xff);
	  PC = WZ = 0x0018;
	  return 11;
	}
      }
      ),

    // e0
    new Opcode("RET", "PO",
	       1,
	       Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  if (PFSET()) {
	    incPC();
	    return 5;
	  } else {
	    WZ = memory.getByte(SP);
	    incSP();
	    WZ += memory.getByte(SP) << 8;
	    incSP();
	    PC = WZ;
	    return 11;
	  }
	}
      }
      ),

    // e1
    new Opcode("POP", "HL", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  L = memory.getByte(SP);
	  incSP();
	  H = memory.getByte(SP);
	  incSP();
	  incPC();
	  return 10;
	}
      }
      ),

    // e2
    new Opcode("JP", "PO,<nn>",
	       3,
	       Processor.INS_JMP,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  if (PFSET()) {
	    incPC();
	  } else {
	    PC = WZ;
	  }
	  return 10;
	}
      }
      ),

    // e3
    new Opcode("EX", "(SP),HL",
	       1,
	       Processor.INS_MR | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(SP);
	  memory.setByte(SP, L);
	  L = tb;
	  final int tw = (SP + 1) & 0xffff;
	  tb = memory.getByte(tw);
	  memory.setByte(tw, H);
	  H = tb;
	  WZ = HL();
	  incPC();
	  return 19;
	}
      }
      ),

    // e4
    new Opcode("CALL", "PO,<nn>",
	       3,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  incPC();
	  if (PFSET()) {
	    return 10;
	  } else {
	    decSP();
	    memory.setByte(SP, PC >> 8);
	    decSP();
	    memory.setByte(SP, PC & 0xff);
	    PC = WZ;
	    return 17;
	  }
	}
      }
      ),

    // e5
    new Opcode("PUSH", "HL", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  decSP();
	  memory.setByte(SP, H);
	  decSP();
	  memory.setByte(SP, L);
	  incPC();
	  return 11;
	}
      }
      ),

    // e6
    new Opcode("AND", "<n>", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int tb = memory.getByte(PC);
	  A &= tb;
	  SETHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 7;
	}
      }
      ),

    // e7
    new Opcode("RST", "<p>",
	       1,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  decSP();
	  memory.setByte(SP, PC >> 8);
	  decSP();
	  memory.setByte(SP, PC & 0xff);
	  PC = WZ = 0x0020;
	  return 11;
	}
      }
      ),

    // e8
    new Opcode("RET", "PE",
	       1,
	       Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  if (!PFSET()) {
	    incPC();
	    return 5;
	  } else {
	    WZ = memory.getByte(SP);
	    incSP();
	    WZ += memory.getByte(SP) << 8;
	    incSP();
	    PC = WZ;
	    return 11;
	  }
	}
      }
      ),

    // e9
    new Opcode("JP", "(HL)", 1, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  PC = HL();
	  return 4;
	}
      }
      ),

    // ea
    new Opcode("JP", "PE,<nn>", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  if (!PFSET()) {
	    incPC();
	  } else {
	    PC = WZ;
	  }
	  return 10;
	}
      }
      ),

    // eb
    new Opcode("EX", "DE,HL", 1, 0, new Executable() {
	@Override
	public int exec() {
	  int tb = H;
	  H = D;
	  D = tb;
	  tb = L;
	  L = E;
	  E = tb;
	  incPC();
	  return 4;
	}
      }
      ),

    // ec
    new Opcode("CALL", "PE,<nn>",
	       3,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  incPC();
	  if (!PFSET()) {
	    return 10;
	  } else {
	    decSP();
	    memory.setByte(SP, PC >> 8);
	    decSP();
	    memory.setByte(SP, PC & 0xff);
	    PC = WZ;
	    return 17;
	  }
	}
      }
      ),

    // ed
    null,

    // ee
    new Opcode("XOR", "<n>", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  A ^= memory.getByte(PC);
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 7;
	}
      }
      ),

    // ef
    new Opcode("RST", "<p>",
	       1,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  decSP();
	  memory.setByte(SP, PC >> 8);
	  decSP();
	  memory.setByte(SP, PC & 0xff);
	  PC = WZ = 0x0028;
	  return 11;
	}
      }
      ),

    // f0
    new Opcode("RET", "P",
	       1,
	       Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  if (SFSET()) {
	    incPC();
	    return 5;
	  } else {
	    WZ = memory.getByte(SP);
	    incSP();
	    WZ += memory.getByte(SP) << 8;
	    incSP();
	    PC = WZ;
	    return 11;
	  }
	}
      }
      ),

    // f1
    new Opcode("POP", "AF", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  F = memory.getByte(SP);
	  incSP();
	  A = memory.getByte(SP);
	  incSP();
	  incPC();
	  return 10;
	}
      }
      ),

    // f2
    new Opcode("JP", "P,<nn>", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  if (SFSET()) {
	    incPC();
	  } else {
	    PC = WZ;
	  }
	  return 10;
	}
      }
      ),

    // f3
    new Opcode("DI", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  IFF1 = false;
	  incPC();
	  return 4;
	}
      }
      ),

    // f4
    new Opcode("CALL", "P,<nn>",
	       3,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  incPC();
	  if (SFSET()) {
	    return 10;
	  } else {
	    decSP();
	    memory.setByte(SP, PC >> 8);
	    decSP();
	    memory.setByte(SP, PC & 0xff);
	    PC = WZ;
	    return 17;
	  }
	}
      }
      ),

    // f5
    new Opcode("PUSH", "AF", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  decSP();
	  memory.setByte(SP, A);
	  decSP();
	  memory.setByte(SP, F);
	  incPC();
	  return 11;
	}
      }
      ),

    // f6
    new Opcode("OR", "<n>", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  A |= memory.getByte(PC);
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 7;
	}
      }
      ),

    // f7
    new Opcode("RST", "<p>",
	       1,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  decSP();
	  memory.setByte(SP, PC >> 8);
	  decSP();
	  memory.setByte(SP, PC & 0xff);
	  PC = WZ = 0x0030;
	  return 11;
	}
      }
      ),

    // f8
    new Opcode("RET", "M",
	       1,
	       Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  if (!SFSET()) {
	    incPC();
	    return 5;
	  } else {
	    WZ = memory.getByte(SP);
	    incSP();
	    WZ += memory.getByte(SP) << 8;
	    incSP();
	    PC = WZ;
	    return 11;
	  }
	}
      }
      ),

    // f9
    new Opcode("LD", "SP,HL", 1, 0, new Executable() {
	@Override
	public int exec() {
	  SP = HL();
	  incPC();
	  return 6;
	}
      }
      ),

    // fa
    new Opcode("JP", "M,<nn>", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  if (!SFSET()) {
	    incPC();
	  } else {
	    PC = WZ;
	  }
	  return 10;
	}
      }
      ),

    // fb
    new Opcode("EI", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  IFF1 = true;
	  incPC();
	  return 4;
	}
      }
      ),

    // fc
    new Opcode("CALL", "M,<nn>",
	       3,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  incPC();
	  if (!SFSET()) {
	    return 10;
	  } else {
	    decSP();
	    memory.setByte(SP, PC >> 8);
	    decSP();
	    memory.setByte(SP, PC & 0xff);
	    PC = WZ;
	    return 17;
	  }
	}
      }
      ),

    // fd
    null,

    // fe
    new Opcode("CP", "<n>", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int tb = memory.getByte(PC);
	  final int tw = A - tb;
	  final int cb = A ^ tb ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  F22(tw & 0xff, tb);
	  SETNF();
	  incPC();
	  return 7;
	}
      }
      ),

    // ff
    new Opcode("RST", "<p>",
	       1,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  decSP();
	  memory.setByte(SP, PC >> 8);
	  decSP();
	  memory.setByte(SP, PC & 0xff);
	  PC = WZ = 0x0038;
	  return 11;
	}
      }
      )
  };

  /**
   * The array of opcodes with the prefix CB.
   */
  protected final Opcode[] opcodesCB = new Opcode[] {

    // cb 00
    new Opcode("RLC", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((B & 0x80) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  B = ((B << 1) | (F & 1)) & 0xff;
	  F5(B);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 01
    new Opcode("RLC", "C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((C & 0x80) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  C = ((C << 1) | (F & 1)) & 0xff;
	  F5(C);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 02
    new Opcode("RLC", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((D & 0x80) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  D = ((D << 1) | (F & 1)) & 0xff;
	  F5(D);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 03
    new Opcode("RLC", "E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((E & 0x80) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  E = ((E << 1) | (F & 1)) & 0xff;
	  F5(E);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 04
    new Opcode("RLC", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((H & 0x80) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  H = ((H << 1) | (F & 1)) & 0xff;
	  F5(H);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 05
    new Opcode("RLC", "L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((L & 0x80) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  L = ((L << 1) | (F & 1)) & 0xff;
	  F5(L);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 06
    new Opcode("RLC", "(HL)",
	       1,
	       Processor.INS_MR | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(HL());
	  if ((tb & 0x80) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  tb = ((tb << 1) | (F & 1)) & 0xff;
	  memory.setByte(HL(), tb);
	  F5(tb);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 15;
	}
      }
      ),

    // cb 07
    new Opcode("RLC", "A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((A & 0x80) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = ((A << 1) | (F & 1)) & 0xff;
	  F5(A);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 08
    new Opcode("RRC", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((B & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  B = ((B >> 1) | (F << 1)) & 0xff;
	  F5(B);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 09
    new Opcode("RRC", "C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((C & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  C = ((C >> 1) | (F << 1)) & 0xff;
	  F5(C);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 0a
    new Opcode("RRC", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((D & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  D = ((D >> 1) | (F << 1)) & 0xff;
	  F5(D);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 0b
    new Opcode("RRC", "E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((E & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  E = ((E >> 1) | (F << 1)) & 0xff;
	  F5(E);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 0c
    new Opcode("RRC", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((H & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  H = ((H >> 1) | (F << 1)) & 0xff;
	  F5(H);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 0d
    new Opcode("RRC", "L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((L & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  L = ((L >> 1) | (F << 1)) & 0xff;
	  F5(L);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 0e
    new Opcode("RRC", "(HL)",
	       1,
	       Processor.INS_MR | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(HL());
	  if ((tb & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  tb = ((tb >> 1) | (F << 1)) & 0xff;
	  memory.setByte(HL(), tb);
	  F5(tb);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 15;
	}
      }
      ),

    // cb 0f
    new Opcode("RRC", "A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((A & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = ((A >> 1) | (F << 1)) & 0xff;
	  F5(A);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 10
    new Opcode("RL", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B = (B << 1) | (F & 1);
	  if ((B & 0x100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  B &= 0xff;
	  F5(B);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 11
    new Opcode("RL", "C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C = (C << 1) | (F & 1);
	  if ((C & 0x100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  C &= 0xff;
	  F5(C);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 12
    new Opcode("RL", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D = (D << 1) | (F & 1);
	  if ((D & 0x100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  D &= 0xff;
	  F5(D);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 13
    new Opcode("RL", "E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E = (E << 1) | (F & 1);
	  if ((E & 0x100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  E &= 0xff;
	  F5(E);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 14
    new Opcode("RL", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H = (H << 1) | (F & 1);
	  if ((H & 0x100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  H &= 0xff;
	  F5(H);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 15
    new Opcode("RL", "L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L = (L << 1) | (F & 1);
	  if ((L & 0x100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  L &= 0xff;
	  F5(L);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 16
    new Opcode("RL", "(HL)",
	       1,
	       Processor.INS_MR | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(HL());
	  tb = (tb << 1) | (F & 1);
	  if ((tb & 0x100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  tb &= 0xff;
	  memory.setByte(HL(), tb);
	  F5(tb);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 15;
	}
      }
      ),

    // cb 17
    new Opcode("RL", "A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = (A << 1) | (F & 1);
	  if ((A & 0x100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A &= 0xff;
	  F5(A);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 18
    new Opcode("RR", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int ti = B;
	  B = (B >> 1) | ((F & 1) << 7);
	  if ((ti & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  F5(B);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 19
    new Opcode("RR", "C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int ti = C;
	  C = (C >> 1) | ((F & 1) << 7);
	  if ((ti & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  F5(C);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 1a
    new Opcode("RR", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int ti = D;
	  D = (D >> 1) | ((F & 1) << 7);
	  if ((ti & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  F5(D);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 1b
    new Opcode("RR", "E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int ti = E;
	  E = (E >> 1) | ((F & 1) << 7);
	  if ((ti & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  F5(E);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 1c
    new Opcode("RR", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int ti = H;
	  H = (H >> 1) | ((F & 1) << 7);
	  if ((ti & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  F5(H);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 1d
    new Opcode("RR", "L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int ti = L;
	  L = (L >> 1) | ((F & 1) << 7);
	  if ((ti & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  F5(L);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 1e
    new Opcode("RR", "(HL)",
	       1,
	       Processor.INS_MR | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(HL());
	  final int ti = tb;
	  tb = (tb >> 1) | ((F & 1) << 7);
	  memory.setByte(HL(), tb);
	  if ((ti & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  F5(tb);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 15;
	}
      }
      ),

    // cb 1f
    new Opcode("RR", "A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int ti = A;
	  A = (A >> 1) | ((F & 1) << 7);
	  if ((ti & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  F5(A);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 20
    new Opcode("SLA", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((B & 0x80) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  B = (B << 1) & 0xff;
	  F5(B);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 21
    new Opcode("SLA", "C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((C & 0x80) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  C = (C << 1) & 0xff;
	  F5(C);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 22
    new Opcode("SLA", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((D & 0x80) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  D = (D << 1) & 0xff;
	  F5(D);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 23
    new Opcode("SLA", "E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((E & 0x80) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  E = (E << 1) & 0xff;
	  F5(E);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 24
    new Opcode("SLA", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((H & 0x80) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  H = (H << 1) & 0xff;
	  F5(H);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 25
    new Opcode("SLA", "L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((L & 0x80) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  L = (L << 1) & 0xff;
	  F5(L);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 26
    new Opcode("SLA", "(HL)",
	       1,
	       Processor.INS_MR | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(HL());
	  if ((tb & 0x80) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  tb = (tb << 1) & 0xff;
	  memory.setByte(HL(), tb);
	  F5(tb);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 15;
	}
      }
      ),

    // cb 27
    new Opcode("SLA", "A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((A & 0x80) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = (A << 1) & 0xff;
	  F5(A);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 28
    new Opcode("SRA", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((B & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  B = (B & 0x80) | (B >> 1);
	  F5(B);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 29
    new Opcode("SRA", "C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((C & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  C = (C & 0x80) | (C >> 1);
	  F5(C);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 2a
    new Opcode("SRA", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((D & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  D = (D & 0x80) | (D >> 1);
	  F5(D);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 2b
    new Opcode("SRA", "E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((E & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  E = (E & 0x80) | (E >> 1);
	  F5(E);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 2c
    new Opcode("SRA", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((H & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  H = (H & 0x80) | (H >> 1);
	  F5(H);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 2d
    new Opcode("SRA", "L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((L & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  L = (L & 0x80) | (L >> 1);
	  F5(L);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 2e
    new Opcode("SRA", "(HL)",
	       1,
	       Processor.INS_MR | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(HL());
	  if ((tb & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  tb = (tb & 0x80) | (tb >> 1);
	  memory.setByte(HL(), tb);
	  F5(tb);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 15;
	}
      }
      ),

    // cb 2f
    new Opcode("SRA", "A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((A & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = (A & 0x80) | (A >> 1);
	  F5(A);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 30
    new Opcode("SLL", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((B & 0x80) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  B = ((B << 1) & 0xff) | 1;
	  F5(B);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 31
    new Opcode("SLL", "C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((C & 0x80) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  C = ((C << 1) & 0xff) | 1;
	  F5(C);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 32
    new Opcode("SLL", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((D & 0x80) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  D = ((D << 1) & 0xff) | 1;
	  F5(D);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 33
    new Opcode("SLL", "E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((E & 0x80) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  E = ((E << 1) & 0xff) | 1;
	  F5(E);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 34
    new Opcode("SLL", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((H & 0x80) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  H = ((H << 1) & 0xff) | 1;
	  F5(H);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 35
    new Opcode("SLL", "L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((L & 0x80) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  L = ((L << 1) & 0xff) | 1;
	  F5(L);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 36
    new Opcode("SLL", "(HL)",
	       1,
	       Processor.INS_MR | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(HL());
	  if ((tb & 0x80) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  tb = ((tb << 1) & 0xff) | 1;
	  memory.setByte(HL(), tb);
	  F5(tb);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 15;
	}
      }
      ),

    // cb 37
    new Opcode("SLL", "A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((A & 0x80) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = ((A << 1) & 0xff) | 1;
	  F5(A);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 38
    new Opcode("SRL", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((B & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  B >>= 1;
	  F5(B);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 39
    new Opcode("SRL", "C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((C & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  C >>= 1;
	  F5(C);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 3a
    new Opcode("SRL", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((D & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  D >>= 1;
	  F5(D);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 3b
    new Opcode("SRL", "E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((E & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  E >>= 1;
	  F5(E);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 3c
    new Opcode("SRL", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((H & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  H >>= 1;
	  F5(H);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 3d
    new Opcode("SRL", "L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((L & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  L >>= 1;
	  F5(L);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 3e
    new Opcode("SRL", "(HL)",
	       1,
	       Processor.INS_MR | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(HL());
	  if ((tb & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  tb >>= 1;
	  memory.setByte(HL(), tb);
	  F5(tb);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 15;
	}
      }
      ),

    // cb 3f
    new Opcode("SRL", "A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((A & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A >>= 1;
	  F5(A);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 40
    new Opcode("BIT", "0,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(B);
	  if ((B & 0x01) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 41
    new Opcode("BIT", "0,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(C);
	  if ((C & 0x01) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 42
    new Opcode("BIT", "0,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(D);
	  if ((D & 0x01) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 43
    new Opcode("BIT", "0,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(E);
	  if ((E & 0x01) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 44
    new Opcode("BIT", "0,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(H);
	  if ((H & 0x01) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 45
    new Opcode("BIT", "0,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(L);
	  if ((L & 0x01) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 46
    new Opcode("BIT", "0,(HL)", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(HL());
	  F22(tb, WZ >> 8);
	  if ((tb & 0x01) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 12;
	}
      }
      ),

    // cb 47
    new Opcode("BIT", "0,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(A);
	  if ((A & 0x01) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 48
    new Opcode("BIT", "1,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(B);
	  if ((B & 0x02) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 49
    new Opcode("BIT", "1,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(C);
	  if ((C & 0x02) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 4a
    new Opcode("BIT", "1,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(D);
	  if ((D & 0x02) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 4b
    new Opcode("BIT", "1,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(E);
	  if ((E & 0x02) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 4c
    new Opcode("BIT", "1,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(H);
	  if ((H & 0x02) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 4d
    new Opcode("BIT", "1,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(L);
	  if ((L & 0x02) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 4e
    new Opcode("BIT", "1,(HL)", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(HL());
	  F22(tb, WZ >> 8);
	  if ((tb & 0x02) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 12;
	}
      }
      ),

    // cb 4f
    new Opcode("BIT", "1,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(A);
	  if ((A & 0x02) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 50
    new Opcode("BIT", "2,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(B);
	  if ((B & 0x04) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 51
    new Opcode("BIT", "2,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(C);
	  if ((C & 0x04) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 52
    new Opcode("BIT", "2,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(D);
	  if ((D & 0x04) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 53
    new Opcode("BIT", "2,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(E);
	  if ((E & 0x04) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 54
    new Opcode("BIT", "2,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(H);
	  if ((H & 0x04) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 55
    new Opcode("BIT", "2,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(L);
	  if ((L & 0x04) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 56
    new Opcode("BIT", "2,(HL)", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(HL());
	  F22(tb, WZ >> 8);
	  if ((tb & 0x04) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 12;
	}
      }
      ),

    // cb 57
    new Opcode("BIT", "2,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(A);
	  if ((A & 0x04) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 58
    new Opcode("BIT", "3,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(B);
	  if ((B & 0x08) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 59
    new Opcode("BIT", "3,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(C);
	  if ((C & 0x08) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 5a
    new Opcode("BIT", "3,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(D);
	  if ((D & 0x08) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 5b
    new Opcode("BIT", "3,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(E);
	  if ((E & 0x08) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 5c
    new Opcode("BIT", "3,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(H);
	  if ((H & 0x08) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 5d
    new Opcode("BIT", "3,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(L);
	  if ((L & 0x08) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 5e
    new Opcode("BIT", "3,(HL)", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(HL());
	  F22(tb, WZ >> 8);
	  if ((tb & 0x08) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 12;
	}
      }
      ),

    // cb 5f
    new Opcode("BIT", "3,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(A);
	  if ((A & 0x08) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 60
    new Opcode("BIT", "4,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(B);
	  if ((B & 0x10) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 61
    new Opcode("BIT", "4,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(C);
	  if ((C & 0x10) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 62
    new Opcode("BIT", "4,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(D);
	  if ((D & 0x10) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 63
    new Opcode("BIT", "4,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(E);
	  if ((E & 0x10) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 64
    new Opcode("BIT", "4,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(H);
	  if ((H & 0x10) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 65
    new Opcode("BIT", "4,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(L);
	  if ((L & 0x10) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 66
    new Opcode("BIT", "4,(HL)", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(HL());
	  F22(tb, WZ >> 8);
	  if ((tb & 0x10) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 12;
	}
      }
      ),

    // cb 67
    new Opcode("BIT", "4,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(A);
	  if ((A & 0x10) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 68
    new Opcode("BIT", "5,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(B);
	  if ((B & 0x20) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 69
    new Opcode("BIT", "5,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(C);
	  if ((C & 0x20) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 6a
    new Opcode("BIT", "5,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(D);
	  if ((D & 0x20) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 6b
    new Opcode("BIT", "5,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(E);
	  if ((E & 0x20) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 6c
    new Opcode("BIT", "5,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(H);
	  if ((H & 0x20) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 6d
    new Opcode("BIT", "5,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(L);
	  if ((L & 0x20) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 6e
    new Opcode("BIT", "5,(HL)", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(HL());
	  F22(tb, WZ >> 8);
	  if ((tb & 0x20) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 12;
	}
      }
      ),

    // cb 6f
    new Opcode("BIT", "5,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(A);
	  if ((A & 0x20) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 70
    new Opcode("BIT", "6,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(B);
	  if ((B & 0x40) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 71
    new Opcode("BIT", "6,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(C);
	  if ((C & 0x40) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 72
    new Opcode("BIT", "6,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(D);
	  if ((D & 0x40) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 73
    new Opcode("BIT", "6,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(E);
	  if ((E & 0x40) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 74
    new Opcode("BIT", "6,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(H);
	  if ((H & 0x40) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 75
    new Opcode("BIT", "6,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(L);
	  if ((L & 0x40) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 76
    new Opcode("BIT", "6,(HL)", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(HL());
	  F22(tb, WZ >> 8);
	  if ((tb & 0x40) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 12;
	}
      }
      ),

    // cb 77
    new Opcode("BIT", "6,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(A);
	  if ((A & 0x40) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  CLEARSF();
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 78
    new Opcode("BIT", "7,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(B);
	  if ((B & 0x80) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  if (ZFSET()) {
	    CLEARSF();
	  } else {
	    SETSF();
	  }
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 79
    new Opcode("BIT", "7,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(C);
	  if ((C & 0x80) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  if (ZFSET()) {
	    CLEARSF();
	  } else {
	    SETSF();
	  }
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 7a
    new Opcode("BIT", "7,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(D);
	  if ((D & 0x80) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  if (ZFSET()) {
	    CLEARSF();
	  } else {
	    SETSF();
	  }
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 7b
    new Opcode("BIT", "7,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(E);
	  if ((E & 0x80) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  if (ZFSET()) {
	    CLEARSF();
	  } else {
	    SETSF();
	  }
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 7c
    new Opcode("BIT", "7,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(H);
	  if ((H & 0x80) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  if (ZFSET()) {
	    CLEARSF();
	  } else {
	    SETSF();
	  }
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 7d
    new Opcode("BIT", "7,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(L);
	  if ((L & 0x80) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  if (ZFSET()) {
	    CLEARSF();
	  } else {
	    SETSF();
	  }
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 7e
    new Opcode("BIT", "7,(HL)", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(HL());
	  F22(tb, WZ >> 8);
	  if ((tb & 0x80) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  if (ZFSET()) {
	    CLEARSF();
	  } else {
	    SETSF();
	  }
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 12;
	}
      }
      ),

    // cb 7f
    new Opcode("BIT", "7,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F4(A);
	  if ((A & 0x80) == 0) {
	    SETZF();
	    SETPF();
	  } else {
	    CLEARZF();
	    CLEARPF();
	  }
	  if (ZFSET()) {
	    CLEARSF();
	  } else {
	    SETSF();
	  }
	  SETHF();
	  CLEARNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 80
    new Opcode("RES", "0,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B &= 0xfe;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 81
    new Opcode("RES", "0,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C &= 0xfe;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 82
    new Opcode("RES", "0,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D &= 0xfe;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 83
    new Opcode("RES", "0,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E &= 0xfe;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 84
    new Opcode("RES", "0,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H &= 0xfe;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 85
    new Opcode("RES", "0,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L &= 0xfe;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 86
    new Opcode("RES", "0,(HL)", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  memory.setByte(HL(), memory.getByte(HL()) & 0xfe);
	  incPC();
	  return 15;
	}
      }
      ),

    // cb 87
    new Opcode("RES", "0,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A &= 0xfe;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 88
    new Opcode("RES", "1,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B &= 0xfd;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 89
    new Opcode("RES", "1,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C &= 0xfd;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 8a
    new Opcode("RES", "1,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D &= 0xfd;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 8b
    new Opcode("RES", "1,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E &= 0xfd;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 8c
    new Opcode("RES", "1,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H &= 0xfd;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 8d
    new Opcode("RES", "1,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L &= 0xfd;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 8e
    new Opcode("RES", "1,(HL)", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  memory.setByte(HL(), memory.getByte(HL()) & 0xfd);
	  incPC();
	  return 15;
	}
      }
      ),

    // cb 8f
    new Opcode("RES", "1,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A &= 0xfd;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 90
    new Opcode("RES", "2,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B &= 0xfb;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 91
    new Opcode("RES", "2,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C &= 0xfb;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 92
    new Opcode("RES", "2,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D &= 0xfb;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 93
    new Opcode("RES", "2,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E &= 0xfb;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 94
    new Opcode("RES", "2,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H &= 0xfb;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 95
    new Opcode("RES", "2,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L &= 0xfb;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 96
    new Opcode("RES", "2,(HL)", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  memory.setByte(HL(), memory.getByte(HL()) & 0xfb);
	  incPC();
	  return 15;
	}
      }
      ),

    // cb 97
    new Opcode("RES", "2,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A &= 0xfb;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 98
    new Opcode("RES", "3,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B &= 0xf7;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 99
    new Opcode("RES", "3,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C &= 0xf7;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 9a
    new Opcode("RES", "3,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D &= 0xf7;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 9b
    new Opcode("RES", "3,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E &= 0xf7;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 9c
    new Opcode("RES", "3,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H &= 0xf7;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 9d
    new Opcode("RES", "3,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L &= 0xf7;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb 9e
    new Opcode("RES", "3,(HL)", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  memory.setByte(HL(), memory.getByte(HL()) & 0xf7);
	  incPC();
	  return 15;
	}
      }
      ),

    // cb 9f
    new Opcode("RES", "3,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A &= 0xf7;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb a0
    new Opcode("RES", "4,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B &= 0xef;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb a1
    new Opcode("RES", "4,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C &= 0xef;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb a2
    new Opcode("RES", "4,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D &= 0xef;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb a3
    new Opcode("RES", "4,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E &= 0xef;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb a4
    new Opcode("RES", "4,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H &= 0xef;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb a5
    new Opcode("RES", "4,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L &= 0xef;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb a6
    new Opcode("RES", "4,(HL)", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  memory.setByte(HL(), memory.getByte(HL()) & 0xef);
	  incPC();
	  return 15;
	}
      }
      ),

    // cb a7
    new Opcode("RES", "4,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A &= 0xef;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb a8
    new Opcode("RES", "5,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B &= 0xdf;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb a9
    new Opcode("RES", "5,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C &= 0xdf;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb aa
    new Opcode("RES", "5,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D &= 0xdf;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb ab
    new Opcode("RES", "5,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E &= 0xdf;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb ac
    new Opcode("RES", "5,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H &= 0xdf;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb ad
    new Opcode("RES", "5,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L &= 0xdf;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb ae
    new Opcode("RES", "5,(HL)", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  memory.setByte(HL(), memory.getByte(HL()) & 0xdf);
	  incPC();
	  return 15;
	}
      }
      ),

    // cb af
    new Opcode("RES", "5,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A &= 0xdf;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb b0
    new Opcode("RES", "6,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B &= 0xbf;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb b1
    new Opcode("RES", "6,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C &= 0xbf;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb b2
    new Opcode("RES", "6,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D &= 0xbf;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb b3
    new Opcode("RES", "6,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E &= 0xbf;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb b4
    new Opcode("RES", "6,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H &= 0xbf;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb b5
    new Opcode("RES", "6,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L &= 0xbf;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb b6
    new Opcode("RES", "6,(HL)", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  memory.setByte(HL(), memory.getByte(HL()) & 0xbf);
	  incPC();
	  return 15;
	}
      }
      ),

    // cb b7
    new Opcode("RES", "6,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A &= 0xbf;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb b8
    new Opcode("RES", "7,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B &= 0x7f;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb b9
    new Opcode("RES", "7,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C &= 0x7f;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb ba
    new Opcode("RES", "7,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D &= 0x7f;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb bb
    new Opcode("RES", "7,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E &= 0x7f;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb bc
    new Opcode("RES", "7,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H &= 0x7f;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb bd
    new Opcode("RES", "7,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L &= 0x7f;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb be
    new Opcode("RES", "7,(HL)", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  memory.setByte(HL(), memory.getByte(HL()) & 0x7f);
	  incPC();
	  return 15;
	}
      }
      ),

    // cb bf
    new Opcode("RES", "7,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A &= 0x7f;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb c0
    new Opcode("SET", "0,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B |= 0x01;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb c1
    new Opcode("SET", "0,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C |= 0x01;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb c2
    new Opcode("SET", "0,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D |= 0x01;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb c3
    new Opcode("SET", "0,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E |= 0x01;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb c4
    new Opcode("SET", "0,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H |= 0x01;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb c5
    new Opcode("SET", "0,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L |= 0x01;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb c6
    new Opcode("SET", "0,(HL)", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  memory.setByte(HL(), memory.getByte(HL()) | 0x01);
	  incPC();
	  return 15;
	}
      }
      ),

    // cb c7
    new Opcode("SET", "0,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A |= 0x01;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb c8
    new Opcode("SET", "1,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B |= 0x02;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb c9
    new Opcode("SET", "1,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C |= 0x02;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb ca
    new Opcode("SET", "1,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D |= 0x02;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb cb
    new Opcode("SET", "1,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E |= 0x02;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb cc
    new Opcode("SET", "1,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H |= 0x02;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb cd
    new Opcode("SET", "1,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L |= 0x02;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb ce
    new Opcode("SET", "1,(HL)", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  memory.setByte(HL(), memory.getByte(HL()) | 0x02);
	  incPC();
	  return 15;
	}
      }
      ),

    // cb cf
    new Opcode("SET", "1,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A |= 0x02;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb d0
    new Opcode("SET", "2,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B |= 0x04;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb d1
    new Opcode("SET", "2,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C |= 0x04;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb d2
    new Opcode("SET", "2,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D |= 0x04;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb d3
    new Opcode("SET", "2,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E |= 0x04;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb d4
    new Opcode("SET", "2,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H |= 0x04;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb d5
    new Opcode("SET", "2,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L |= 0x04;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb d6
    new Opcode("SET", "2,(HL)", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  memory.setByte(HL(), memory.getByte(HL()) | 0x04);
	  incPC();
	  return 15;
	}
      }
      ),

    // cb d7
    new Opcode("SET", "2,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A |= 0x04;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb d8
    new Opcode("SET", "3,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B |= 0x08;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb d9
    new Opcode("SET", "3,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C |= 0x08;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb da
    new Opcode("SET", "3,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D |= 0x08;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb db
    new Opcode("SET", "3,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E |= 0x08;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb dc
    new Opcode("SET", "3,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H |= 0x08;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb dd
    new Opcode("SET", "3,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L |= 0x08;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb de
    new Opcode("SET", "3,(HL)", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  memory.setByte(HL(), memory.getByte(HL()) | 0x08);
	  incPC();
	  return 15;
	}
      }
      ),

    // cb df
    new Opcode("SET", "3,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A |= 0x08;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb e0
    new Opcode("SET", "4,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B |= 0x10;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb e1
    new Opcode("SET", "4,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C |= 0x10;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb e2
    new Opcode("SET", "4,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D |= 0x10;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb e3
    new Opcode("SET", "4,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E |= 0x10;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb e4
    new Opcode("SET", "4,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H |= 0x10;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb e5
    new Opcode("SET", "4,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L |= 0x10;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb e6
    new Opcode("SET", "4,(HL)", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  memory.setByte(HL(), memory.getByte(HL()) | 0x10);
	  incPC();
	  return 15;
	}
      }
      ),

    // cb e7
    new Opcode("SET", "4,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A |= 0x10;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb e8
    new Opcode("SET", "5,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B |= 0x20;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb e9
    new Opcode("SET", "5,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C |= 0x20;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb ea
    new Opcode("SET", "5,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D |= 0x20;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb eb
    new Opcode("SET", "5,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E |= 0x20;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb ec
    new Opcode("SET", "5,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H |= 0x20;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb ed
    new Opcode("SET", "5,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L |= 0x20;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb ee
    new Opcode("SET", "5,(HL)", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  memory.setByte(HL(), memory.getByte(HL()) | 0x20);
	  incPC();
	  return 15;
	}
      }
      ),

    // cb ef
    new Opcode("SET", "5,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A |= 0x20;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb f0
    new Opcode("SET", "6,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B |= 0x40;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb f1
    new Opcode("SET", "6,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C |= 0x40;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb f2
    new Opcode("SET", "6,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D |= 0x40;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb f3
    new Opcode("SET", "6,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E |= 0x40;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb f4
    new Opcode("SET", "6,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H |= 0x40;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb f5
    new Opcode("SET", "6,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L |= 0x40;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb f6
    new Opcode("SET", "6,(HL)", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  memory.setByte(HL(), memory.getByte(HL()) | 0x40);
	  incPC();
	  return 15;
	}
      }
      ),

    // cb f7
    new Opcode("SET", "6,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A |= 0x40;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb f8
    new Opcode("SET", "7,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B |= 0x80;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb f9
    new Opcode("SET", "7,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C |= 0x80;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb fa
    new Opcode("SET", "7,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D |= 0x80;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb fb
    new Opcode("SET", "7,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E |= 0x80;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb fc
    new Opcode("SET", "7,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H |= 0x80;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb fd
    new Opcode("SET", "7,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L |= 0x80;
	  incPC();
	  return 8;
	}
      }
      ),

    // cb fe
    new Opcode("SET", "7,(HL)", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  memory.setByte(HL(), memory.getByte(HL()) | 0x80);
	  incPC();
	  return 15;
	}
      }
      ),

    // cb ff
    new Opcode("SET", "7,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A |= 0x80;
	  incPC();
	  return 8;
	}
      }
      ),
  };

  /**
   * The array of opcodes with the prefix DD.
   */
  protected final Opcode[] opcodesDD = new Opcode[] {

    // dd 00 - dd 08
    null, null, null, null, null, null, null, null,
    null,
    
    // dd 09
    new Opcode("ADD", "IX,BC", 1, 0, new Executable() {
	@Override
	public int exec() {
	  WZ = (IX + 1) & 0xffff;
	  final int ti = IX + BC();
	  if ((((IX >> 8) ^ B ^ (ti >> 8)) & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  IX = ti & 0xffff;
	  F2(IX >> 8);
	  if (ti > 0xffff) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  CLEARNF();
	  incPC();
	  return 11;
	}
      }
      ),

    // dd 0a
    new Opcode("LD", "A,(BC)", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  A = memory.getByte(BC());
	  WZ = (BC() + 1) & 0xffff;
	  incPC();
	  return 7;
	}
      }
      ),

    // dd 0b
    new Opcode("DEC", "BC", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if (--C < 0) {
	    C = 0xff;
	    B = (B - 1) & 0xff;
	  }
	  incPC();
	  return 6;
	}
      }
      ),

    // dd 0c
    new Opcode("INC", "C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C = (C + 1) & 0xff;
	  F4(C);
	  if (C == 0x80) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if ((C & 0x0f) == 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 0d
    new Opcode("DEC", "C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C = (C - 1) & 0xff;
	  F4(C);
	  if (C == 0x7f) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if ((C & 0x0f) == 0x0f) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 0e
    new Opcode("LD", "C,<n>", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  C = memory.getByte(PC);
	  incPC();
	  return 7;
	}
      }
      ),

    // dd 0f
    new Opcode("RRCA", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((A & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = ((A >> 1) | (F << 7)) & 0xff;
	  F2(A);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 10
    new Opcode("DJNZ", "<e>", 1, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  B = (B - 1) & 0xff;
	  if (B == 0) {
	    incPC(2);
	    return 8;
	  } else {
	    incPC();
	    PC = WZ = (PC + 1 + (byte)(memory.getByte(PC))) & 0xffff;
	    return 13;
	  }
	}
      }
      ),

    // dd 11
    new Opcode("LD", "DE,<nn>", 3, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  E = memory.getByte(PC);
	  incPC();
	  D = memory.getByte(PC);
	  incPC();
	  return 10;
	}
      }
      ),

    // dd 12
    new Opcode("LD", "(DE),A", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  memory.setByte(DE(), A);
	  WZ = ((E + 1) & 0xff) + (A << 8);
	  incPC();
	  return 7;
	}
      }
      ),

    // dd 13
    new Opcode("INC", "DE", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E = (E + 1) & 0xff;
	  if (E == 0) {
	    D = (D + 1) & 0xff;
	  }
	  incPC();
	  return 6;
	}
      }
      ),

    // dd 14
    new Opcode("INC", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D = (D + 1) & 0xff;
	  F4(D);
	  if (D == 0x80) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if ((D & 0x0f) == 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 15
    new Opcode("DEC", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D = (D - 1) & 0xff;
	  F4(D);
	  if (D == 0x7f) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if ((D & 0x0f) == 0x0f) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 16
    new Opcode("LD", "D,<n>", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  D = memory.getByte(PC);
	  incPC();
	  return 7;
	}
      }
      ),

    // dd 17
    new Opcode("RLA", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tb = A;
	  A = ((A << 1) | (F & 1)) & 0xff;
	  if ((tb & 0x80) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  F2(A);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 18
    new Opcode("JR", "<e>", 1, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  PC = WZ = (PC + 1 + (byte)(memory.getByte(PC))) & 0xffff;
	  return 12;
	}
      }
      ),

    // dd 19
    new Opcode("ADD", "IX,DE", 1, 0, new Executable() {
	@Override
	public int exec() {
	  WZ = (IX + 1) & 0xffff;
	  final int ti = IX + DE();
	  if ((((IX >> 8) ^ D ^ (ti >> 8)) & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  IX = ti & 0xffff;
	  F2(IX >> 8);
	  if (ti > 0xffff) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  CLEARNF();
	  incPC();
	  return 11;
	}
      }
      ),

    // dd 1a
    new Opcode("LD", "A,(DE)", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  A = memory.getByte(DE());
	  WZ = (DE() + 1) & 0xffff;
	  incPC();
	  return 7;
	}
      }
      ),

    // dd 1b
    new Opcode("DEC", "DE", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if (--E < 0) {
	    E = 0xff;
	    D = (D - 1) & 0xff;
	  }
	  incPC();
	  return 6;
	}
      }
      ),

    // dd 1c
    new Opcode("INC", "E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E = (E + 1) & 0xff;
	  F4(E);
	  if (E == 0x80) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if ((E & 0x0f) == 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 1d
    new Opcode("DEC", "E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E = (E - 1) & 0xff;
	  F4(E);
	  if (E == 0x7f) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if ((E & 0x0f) == 0x0f) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 1e
    new Opcode("LD", "E,<n>", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  E = memory.getByte(PC);
	  incPC();
	  return 7;
	}
      }
      ),

    // dd 1f
    new Opcode("RRA", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tb = A;
	  A = ((A >> 1) | (F << 7)) & 0xff;
	  if ((tb & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  F2(A);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 20
    new Opcode("JR", "NZ,<e>", 1, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  if (ZFSET()) {
	    incPC(2);
	    return 7;
	  } else {
	    incPC();
	    PC = WZ = (PC + 1 + (byte)(memory.getByte(PC))) & 0xffff;
	    return 12;
	  }
	}
      }
      ),

    // dd 21
    new Opcode("LD", "HL,<nn>", 3, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  L = memory.getByte(PC);
	  incPC();
	  H = memory.getByte(PC);
	  incPC();
	  return 10;
	}
      }
      ),

    // dd 22
    new Opcode("LD", "(<nn>),HL", 3, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  memory.setByte(WZ, L);
	  incWZ();
	  memory.setByte(WZ, H);
	  incPC();
	  return 16;
	}
      }
      ),

    // dd 23
    new Opcode("INC", "HL", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L = (L + 1) & 0xff;
	  if (L == 0) {
	    H = (H + 1) & 0xff;
	  }
	  incPC();
	  return 6;
	}
      }
      ),

    // dd 24
    new Opcode("INC", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H = (H + 1) & 0xff;
	  F4(H);
	  if (H == 0x80) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if ((H & 0x0f) == 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 25
    new Opcode("DEC", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H = (H - 1) & 0xff;
	  F4(H);
	  if (H == 0x7f) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if ((H & 0x0f) == 0x0f) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 26
    new Opcode("LD", "H,<n>", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  H = memory.getByte(PC);
	  incPC();
	  return 7;
	}
      }
      ),

    // dd 27
    new Opcode("DAA", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int ld = A & 0x0f;
	  if (NFSET()) {
	    final boolean hd = CFSET() || (A > 0x99);
	    if (HFSET() || (ld > 9)) {
	      if (ld > 5) {
		CLEARHF();
	      }
	      A = (A - 6) & 0xff;
	    }
	    if (hd) {
	      A -= 0x0160;
	    }
	  } else {
	    if (HFSET() || (ld > 9)) {
	      if (ld > 9) {
		SETHF();
	      } else {
		CLEARHF();
	      }
	      A += 6;
	    }
	    if (CFSET() || ((A & 0x01f0) > 0x90)) {
	      A += 0x60;
	    }
	  }
	  if (((A >> 8) & 1) == 1) {
	    SETCF();
	  }
	  A &= 0xff;
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 28
    new Opcode("JR", "Z,<e>", 1, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  if (!ZFSET()) {
	    incPC(2);
	    return 7;
	  } else {
	    incPC();
	    PC = WZ = (PC + 1 + (byte)(memory.getByte(PC))) & 0xffff;
	    return 12;
	  }
	}
      }
      ),

    // dd 29
    new Opcode("ADD", "IX,IX", 1, 0, new Executable() {
	@Override
	public int exec() {
	  WZ = (IX + 1) & 0xffff;
	  final int ti = IX << 1;
	  if (((ti >> 8) & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  IX = ti & 0xffff;
	  F2(IX >> 8);
	  if (ti > 0xffff) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  CLEARNF();
	  incPC();
	  return 11;
	}
      }
      ),

    // dd 2a
    new Opcode("LD", "HL,(<nn>)", 3, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  L = memory.getByte(WZ);
	  incWZ();
	  H = memory.getByte(WZ);
	  incPC();
	  return 16;
	}
      }
      ),

    // dd 2b
    new Opcode("DEC", "HL", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if (--L < 0) {
	    L = 0xff;
	    H = (H - 1) & 0xff;
	  }
	  incPC();
	  return 6;
	}
      }
      ),

    // dd 2c
    new Opcode("INC", "L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L = (L + 1) & 0xff;
	  F4(L);
	  if (L == 0x80) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if ((L & 0x0f) == 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 2d
    new Opcode("DEC", "L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L = (L - 1) & 0xff;
	  F4(L);
	  if (L == 0x7f) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if ((L & 0x0f) == 0x0f) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 2e
    new Opcode("LD", "L,<n>", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  L = memory.getByte(PC);
	  incPC();
	  return 7;
	}
      }
      ),

    // dd 2f
    new Opcode("CPL", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = (~A) & 0xff;
	  F2(A);
	  SETHF();
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 30
    new Opcode("JR", "NC,<e>", 1, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  if (CFSET()) {
	    incPC(2);
	    return 7;
	  } else {
	    incPC();
	    PC = WZ = (PC + 1 + (byte)(memory.getByte(PC))) & 0xffff;
	    return 12;
	  }
	}
      }
      ),

    // dd 31
    new Opcode("LD", "SP,<nn>", 3, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int tb = memory.getByte(PC);
	  incPC();
	  SP = tb + (memory.getByte(PC) << 8);
	  incPC();
	  return 10;
	}
      }
      ),

    // dd 32
    new Opcode("LD", "(<nn>),A", 3, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  memory.setByte(WZ + (memory.getByte(PC) << 8), A);
	  WZ += A << 8;
	  incPC();
	  return 13;
	}
      }
      ),

    // dd 33
    new Opcode("INC", "SP", 1, 0, new Executable() {
	@Override
	public int exec() {
	  incSP();
	  incPC();
	  return 6;
	}
      }
      ),

    // dd 34
    new Opcode("INC", "(HL)",
	       1,
	       Processor.INS_MR | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  final int tw = HL();
	  final int tb = (memory.getByte(tw) + 1) & 0xff;
	  memory.setByte(tw, tb);
	  F4(tb);
	  if (tb == 0x80) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if ((tb & 0x0f) == 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  CLEARNF();
	  incPC();
	  return 11;
	}
      }
      ),

    // dd 35
    new Opcode("DEC", "M",
	       1,
	       Processor.INS_MR | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  final int tw = HL();
	  final int tb = (memory.getByte(tw) - 1) & 0xff;
	  memory.setByte(tw, tb);
	  F4(tb);
	  if (tb == 0x7f) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if ((tb & 0x0f) == 0x0f) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  SETNF();
	  incPC();
	  return 11;
	}
      }
      ),

    // dd 36
    new Opcode("LD", "(HL),<n>", 2, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  final int tw = HL();
	  incPC();
	  memory.setByte(tw, memory.getByte(PC));
	  incPC();
	  return 10;
	}
      }
      ),

    // dd 37
    new Opcode("SCF", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  SETCF();
	  F2(A);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 38
    new Opcode("JR", "C,<e>", 1, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  if (!CFSET()) {
	    incPC(2);
	    return 7;
	  } else {
	    incPC();
	    PC = WZ = (PC + 1 + (byte)(memory.getByte(PC))) & 0xffff;
	    return 12;
	  }
	}
      }
      ),

    // dd 39
    new Opcode("ADD", "IX,SP", 1, 0, new Executable() {
	@Override
	public int exec() {
	  WZ = (IX + 1) & 0xffff;
	  final int ti = IX + SP;
	  if ((((IX >> 8) ^ ((SP ^ ti) >> 8)) & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  IX = ti & 0xffff;
	  F2(IX >> 8);
	  if (ti > 0xffff) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  CLEARNF();
	  incPC();
	  return 11;
	}
      }
      ),

    // dd 3a
    new Opcode("LD", "A,(<nn>)", 3, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  A = memory.getByte(WZ);
	  incWZ();
	  incPC();
	  return 13;
	}
      }
      ),

    // dd 3b
    new Opcode("DEC", "SP", 1, 0, new Executable() {
	@Override
	public int exec() {
	  decSP();
	  incPC();
	  return 6;
	}
      }
      ),

    // dd 3c
    new Opcode("INC", "A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = (A + 1) & 0xff;
	  F4(A);
	  if (A == 0x80) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if ((A & 0x0f) == 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 3d
    new Opcode("DEC", "A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = (A - 1) & 0xff;
	  F4(A);
	  if (A == 0x7f) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if ((A & 0x0f) == 0x0f) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 3e
    new Opcode("LD", "A,<n>", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  A = memory.getByte(PC);
	  incPC();
	  return 7;
	}
      }
      ),

    // dd 3f
    new Opcode("CCF", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F ^= CF;
	  F2(A);
	  if (CFSET()) {
	    CLEARHF();
	  } else {
	    SETHF();
	  }
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 40
    new Opcode("LD", "B,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 41
    new Opcode("LD", "B,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B = C;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 42
    new Opcode("LD", "B,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B = D;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 43
    new Opcode("LD", "B,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B = E;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 44
    new Opcode("LD", "B,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B = H;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 45
    new Opcode("LD", "B,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B = L;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 46
    new Opcode("LD", "B,M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  B = memory.getByte(HL());
	  incPC();
	  return 7;
	}
      }
      ),

    // dd 47
    new Opcode("LD", "B,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B = A;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 48
    new Opcode("LD", "C,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C = B;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 49
    new Opcode("LD", "C,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 4a
    new Opcode("LD", "C,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C = D;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 4b
    new Opcode("LD", "C,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C = E;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 4c
    new Opcode("LD", "C,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C = H;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 4d
    new Opcode("LD", "C,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C = L;
	  incPC();
	  return 4;
	}
      }
      ),


    // dd 4e
    new Opcode("LD", "C,M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  C = memory.getByte(HL());
	  incPC();
	  return 7;
	}
      }
      ),

    // dd 4f
    new Opcode("LD", "C,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C = A;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 50
    new Opcode("LD", "D,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D = B;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 51
    new Opcode("LD", "D,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D = C;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 52
    new Opcode("LD", "D,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 53
    new Opcode("LD", "D,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D = E;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 54
    new Opcode("LD", "D,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D = H;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 55
    new Opcode("LD", "D,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D = L;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 56
    new Opcode("LD", "D,M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  D = memory.getByte(HL());
	  incPC();
	  return 7;
	}
      }
      ),

    // dd 57
    new Opcode("LD", "D,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D = A;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 58
    new Opcode("LD", "E,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E = B;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 59
    new Opcode("LD", "E,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E = C;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 5a
    new Opcode("LD", "E,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E = D;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 5b
    new Opcode("LD", "E,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 5c
    new Opcode("LD", "E,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E = H;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 5d
    new Opcode("LD", "E,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E = L;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 5e
    new Opcode("LD", "E,M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  E = memory.getByte(HL());
	  incPC();
	  return 7;
	}
      }
      ),

    // dd 5f
    new Opcode("LD", "E,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E = A;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 60
    new Opcode("LD", "H,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H = B;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 61
    new Opcode("LD", "H,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H = C;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 62
    new Opcode("LD", "H,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H = D;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 63
    new Opcode("LD", "H,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H = E;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 64
    new Opcode("LD", "H,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 65
    new Opcode("LD", "H,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H = L;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 66
    new Opcode("LD", "H,M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  H = memory.getByte(HL());
	  incPC();
	  return 7;
	}
      }
      ),

    // dd 67
    new Opcode("LD", "H,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H = A;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 68
    new Opcode("LD", "L,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L = B;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 69
    new Opcode("LD", "L,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L = C;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 6a
    new Opcode("LD", "L,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L = D;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 6b
    new Opcode("LD", "L,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L = E;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 6c
    new Opcode("LD", "L,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L = H;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 6d
    new Opcode("LD", "L,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 6e
    new Opcode("LD", "L,M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  L = memory.getByte(HL());
	  incPC();
	  return 7;
	}
      }
      ),

    // dd 6f
    new Opcode("LD", "L,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L = A;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 70
    new Opcode("LD", "M,B", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  final int tw = HL();
	  memory.setByte(tw, B);
	  incPC();
	  return 7;
	}
      }
      ),

    // dd 71
    new Opcode("LD", "M,C", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  final int tw = HL();
	  memory.setByte(tw, C);
	  incPC();
	  return 7;
	}
      }
      ),

    // dd 72
    new Opcode("LD", "M,D", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  final int tw = HL();
	  memory.setByte(tw, D);
	  incPC();
	  return 7;
	}
      }
      ),

    // dd 73
    new Opcode("LD", "M,E", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  final int tw = HL();
	  memory.setByte(tw, E);
	  incPC();
	  return 7;
	}
      }
      ),

    // dd 74
    new Opcode("LD", "M,H", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  final int tw = HL();
	  memory.setByte(tw, H);
	  incPC();
	  return 7;
	}
      }
      ),

    // dd 75
    new Opcode("LD", "M,L", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  final int tw = HL();
	  memory.setByte(tw, L);
	  incPC();
	  return 7;
	}
      }
      ),

    // dd 76
    new Opcode("HALT", "", 1, Processor.INS_HLT, new Executable() {
	@Override
	public int exec() {
	  return 4;
	}
      }
      ),

    // dd 77
    new Opcode("LD", "M,A", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  final int tw = HL();
	  memory.setByte(tw, A);
	  incPC();
	  return 7;
	}
      }
      ),

    // dd 78
    new Opcode("LD", "A,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = B;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 79
    new Opcode("LD", "A,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = C;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 7a
    new Opcode("LD", "A,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = D;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 7b
    new Opcode("LD", "A,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = E;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 7c
    new Opcode("LD", "A,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = H;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 7d
    new Opcode("LD", "A,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = L;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 7e
    new Opcode("LD", "A,M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  A = memory.getByte(HL());
	  incPC();
	  return 7;
	}
      }
      ),

    // dd 7f
    new Opcode("LD", "A,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 80
    new Opcode("ADD", "A,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + B;
	  final int cb = A ^ B ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 81
    new Opcode("ADD", "A,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + C;
	  final int cb = A ^ C ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 82
    new Opcode("ADD", "A,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + D;
	  final int cb = A ^ D ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 83
    new Opcode("ADD", "A,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + E;
	  final int cb = A ^ E ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 84
    new Opcode("ADD", "A,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + H;
	  final int cb = A ^ H ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 85
    new Opcode("ADD", "A,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + L;
	  final int cb = A ^ L ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 86
    new Opcode("ADD", "A,(HL)", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  final int tb = memory.getByte(HL());
	  final int tw = A + tb;
	  final int cb = A ^ tb ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 7;
	}
      }
      ),

    // dd 87
    new Opcode("ADD", "A,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A << 1;
	  if ((tw & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((tw ^ (tw >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 88
    new Opcode("ADC", "A,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + B + (F & CF);
	  final int cb = A ^ B ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 89
    new Opcode("ADC", "A,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + C + (F & CF);
	  final int cb = A ^ C ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 8a
    new Opcode("ADC", "A,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + D + (F & CF);
	  final int cb = A ^ D ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 8b
    new Opcode("ADC", "A,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + E + (F & CF);
	  final int cb = A ^ E ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 8c
    new Opcode("ADC", "A,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + H + (F & CF);
	  final int cb = A ^ H ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 8d
    new Opcode("ADC", "A,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + L + (F & CF);
	  final int cb = A ^ L ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 8e
    new Opcode("ADC", "A,(HL)", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  final int tb = memory.getByte(HL());
	  final int tw = A + tb + (F & CF);
	  final int cb = A ^ tb ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 7;
	}
      }
      ),

    // dd 8f
    new Opcode("ADC", "A,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = (A << 1) + (F & CF);
	  if ((tw & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((tw ^ (tw >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 90
    new Opcode("SUB", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - B;
	  final int cb = A ^ B ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 91
    new Opcode("SUB", "C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - C;
	  final int cb = A ^ C ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 92
    new Opcode("SUB", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - D;
	  final int cb = A ^ D ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 93
    new Opcode("SUB", "E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - E;
	  final int cb = A ^ E ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 94
    new Opcode("SUB", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - H;
	  final int cb = A ^ H ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 95
    new Opcode("SUB", "L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - L;
	  final int cb = A ^ L ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 96
    new Opcode("SUB", "(HL)", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  final int tb = memory.getByte(HL());
	  final int tw = A - tb;
	  final int cb = A ^ tb ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 7;
	}
      }
      ),

    // dd 97
    new Opcode("SUB", "A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  CLEARHF();
	  CLEARCF();
	  CLEARPF();
	  A = 0;
	  F4(0);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 98
    new Opcode("SBC", "A,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - B - (F & CF);
	  final int cb = A ^ B ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 99
    new Opcode("SBC", "A,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - C - (F & CF);
	  final int cb = A ^ C ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 9a
    new Opcode("SBC", "A,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - D - (F & CF);
	  final int cb = A ^ D ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 9b
    new Opcode("SBC", "A,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - E - (F & CF);
	  final int cb = A ^ E ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 9c
    new Opcode("SBC", "A,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - H - (F & CF);
	  final int cb = A ^ H ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 9d
    new Opcode("SBC", "A,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - L - (F & CF);
	  final int cb = A ^ L ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd 9e
    new Opcode("SBC", "A,(HL)", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  final int tb = memory.getByte(HL());
	  final int tw = A - tb - (F & CF);
	  final int cb = A ^ tb ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 7;
	}
      }
      ),

    // dd 9f
    new Opcode("SBC", "A,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = -(F & CF);
	  if ((tw & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((tw ^ (tw >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd a0
    new Opcode("AND", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A &= B;
	  SETHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // dd a1
    new Opcode("AND", "C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A &= C;
	  SETHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // dd a2
    new Opcode("AND", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A &= D;
	  SETHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // dd a3
    new Opcode("AND", "E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A &= E;
	  SETHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // dd a4
    new Opcode("AND", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A &= H;
	  SETHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // dd a5
    new Opcode("AND", "L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A &= L;
	  SETHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // dd a6
    new Opcode("AND", "(HL)", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  final int tb = memory.getByte(HL());
	  A &= tb;
	  SETHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 7;
	}
      }
      ),

    // dd a7
    new Opcode("AND", "A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  SETHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // dd a8
    new Opcode("XOR", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A ^= B;
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // dd a9
    new Opcode("XOR", "C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A ^= C;
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // dd aa
    new Opcode("XOR", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A ^= D;
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // dd ab
    new Opcode("XOR", "E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A ^= E;
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // dd ac
    new Opcode("XOR", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A ^= H;
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // dd ad
    new Opcode("XOR", "L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A ^= L;
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // dd ae
    new Opcode("XOR", "(HL)", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  A ^= memory.getByte(HL());
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 7;
	}
      }
      ),

    // dd af
    new Opcode("XOR", "A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = 0;
	  F = ZF | PF;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd b0
    new Opcode("OR", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A |= B;
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // dd b1
    new Opcode("OR", "C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A |= C;
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // dd b2
    new Opcode("OR", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A |= D;
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // dd b3
    new Opcode("OR", "E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A |= E;
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // dd b4
    new Opcode("OR", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A |= H;
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // dd b5
    new Opcode("OR", "L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A |= L;
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // dd b6
    new Opcode("OR", "(HL)", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  A |= memory.getByte(HL());
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 7;
	}
      }
      ),

    // dd b7
    new Opcode("OR", "A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 4;
	}
      }
      ),

    // dd b8
    new Opcode("CP", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - B;
	  final int cb = A ^ B ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  F22(tw & 0xff, B);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd b9
    new Opcode("CP", "C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - C;
	  final int cb = A ^ C ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  F22(tw & 0xff, C);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd ba
    new Opcode("CP", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - D;
	  final int cb = A ^ D ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  F22(tw & 0xff, D);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd bb
    new Opcode("CP", "E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - E;
	  final int cb = A ^ E ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  F22(tw & 0xff, E);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd bc
    new Opcode("CP", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - H;
	  final int cb = A ^ H ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  F22(tw & 0xff, H);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd bd
    new Opcode("CP", "L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - L;
	  final int cb = A ^ L ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  F22(tw & 0xff, L);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd be
    new Opcode("CP", "(HL)", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  final int tb = memory.getByte(HL());
	  final int tw = A - tb;
	  final int cb = A ^ tb ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  F22(tw & 0xff, tb);
	  SETNF();
	  incPC();
	  return 7;
	}
      }
      ),

    // dd bf
    new Opcode("CP", "A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  CLEARHF();
	  CLEARCF();
	  CLEARPF();
	  F22(0, A);
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),

    // dd c0
    new Opcode("RET", "NZ",
	       1,
	       Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  if (ZFSET()) {
	    incPC();
	    return 5;
	  } else {
	    WZ = memory.getByte(SP);
	    incSP();
	    WZ += memory.getByte(SP) << 8;
	    incSP();
	    PC = WZ;
	    return 11;
	  }
	}
      }
      ),

    // dd c1
    new Opcode("POP", "BC", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  C = memory.getByte(SP);
	  incSP();
	  B = memory.getByte(SP);
	  incSP();
	  incPC();
	  return 10;
	}
      }
      ),

    // dd c2
    new Opcode("JP", "NZ,<nn>", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  if (ZFSET()) {
	    incPC();
	  } else {
	    PC = WZ;
	  }
	  return 10;
	}
      }
      ),

    // dd c3
    new Opcode("JP", "<nn>", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  PC = WZ;
	  return 10;
	}
      }
      ),

    // dd c4
    new Opcode("CALL", "NZ,<nn>",
	       3,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  incPC();
	  if (ZFSET()) {
	    return 10;
	  } else {
	    decSP();
	    memory.setByte(SP, PC >> 8);
	    decSP();
	    memory.setByte(SP, PC & 0xff);
	    PC = WZ;
	    return 17;
	  }
	}
      }
      ),

    // dd c5
    new Opcode("PUSH", "BC", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  decSP();
	  memory.setByte(SP, B);
	  decSP();
	  memory.setByte(SP, C);
	  incPC();
	  return 11;
	}
      }
      ),

    // dd c6
    new Opcode("ADD", "A,<n>", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int tb = memory.getByte(PC);
	  final int tw = A + tb;
	  final int cb = A ^ tb ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 7;
	}
      }
      ),

    // dd c7
    new Opcode("RST", "<p>",
	       1,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  decSP();
	  memory.setByte(SP, PC >> 8);
	  decSP();
	  memory.setByte(SP, PC & 0xff);
	  PC = WZ = 0x0000;
	  return 11;
	}
      }
      ),

    // dd c8
    new Opcode("RET", "Z",
	       1,
	       Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  if (!ZFSET()) {
	    incPC();
	    return 5;
	  } else {
	    WZ = memory.getByte(SP);
	    incSP();
	    WZ += memory.getByte(SP) << 8;
	    incSP();
	    PC = WZ;
	    return 11;
	  }
	}
      }
      ),

    // dd c9
    new Opcode("RET", "",
	       1,
	       Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  WZ = memory.getByte(SP);
	  incSP();
	  WZ += memory.getByte(SP) << 8;
	  incSP();
	  PC = WZ;
	  return 10;
	}
      }
      ),

    // dd ca
    new Opcode("JP", "Z,<nn>", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  if (!ZFSET()) {
	    incPC();
	  } else {
	    PC = WZ;
	  }
	  return 10;
	}
      }
      ),

    // dd cb
    null,

    // dd cc
    new Opcode("CALL", "Z,<nn>",
	       3,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  incPC();
	  if (!ZFSET()) {
	    return 10;
	  } else {
	    decSP();
	    memory.setByte(SP, PC >> 8);
	    decSP();
	    memory.setByte(SP, PC & 0xff);
	    PC = WZ;
	    return 17;
	  }
	}
      }
      ),

    // dd cd
    new Opcode("CALL", "<nn>",
	       3,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  incPC();
	  decSP();
	  memory.setByte(SP, PC >> 8);
	  decSP();
	  memory.setByte(SP, PC & 0xff);
	  PC = WZ;
	  return 17;
	}
      }
      ),

    // dd ce
    new Opcode("ADC", "A,<n>", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int tb = memory.getByte(PC);
	  final int tw = A + tb + (F & CF);
	  final int cb = A ^ tb ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  CLEARNF();
	  incPC();
	  return 7;
	}
      }
      ),

    // dd cf
    new Opcode("RST", "<p>",
	       1,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  decSP();
	  memory.setByte(SP, PC >> 8);
	  decSP();
	  memory.setByte(SP, PC & 0xff);
	  PC = WZ = 0x0008;
	  return 11;
	}
      }
      ),

    // dd d0
    new Opcode("RET", "NC",
	       1,
	       Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  if (CFSET()) {
	    incPC();
	    return 5;
	  } else {
	    WZ = memory.getByte(SP);
	    incSP();
	    WZ += memory.getByte(SP) << 8;
	    incSP();
	    PC = WZ;
	    return 11;
	  }
	}
      }
      ),

    // dd d1
    new Opcode("POP", "DE", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  E = memory.getByte(SP);
	  incSP();
	  D = memory.getByte(SP);
	  incSP();
	  incPC();
	  return 10;
	}
      }
      ),

    // dd d2
    new Opcode("JP", "NC,<nn>", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  if (CFSET()) {
	    incPC();
	  } else {
	    PC = WZ;
	  }
	  return 10;
	}
      }
      ),

    // dd d3
    new Opcode("OUT", "(<n>),A", 2, Processor.INS_IOW, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  int port = memory.getByte(PC);
	  for (IOElement t: outputPorts.get(port)) {
	    t.portOutput(port, A);
	  }
	  WZ = ((port + 1) & 0xff) + (A << 8);
	  incPC();
	  return 11;
	}
      }
      ),

    // dd d4
    new Opcode("CALL", "NC,<nn>",
	       3,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  incPC();
	  if (CFSET()) {
	    return 10;
	  } else {
	    decSP();
	    memory.setByte(SP, PC >> 8);
	    decSP();
	    memory.setByte(SP, PC & 0xff);
	    PC = WZ;
	    return 17;
	  }
	}
      }
      ),

    // dd d5
    new Opcode("PUSH", "DE", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  decSP();
	  memory.setByte(SP, D);
	  decSP();
	  memory.setByte(SP, E);
	  incPC();
	  return 11;
	}
      }
      ),

    // dd d6
    new Opcode("SUB", "<n>", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int tb = memory.getByte(PC);
	  final int tw = A - tb;
	  final int cb = A ^ tb ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 7;
	}
      }
      ),

    // dd d7
    new Opcode("RST", "<p>",
	       1,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  decSP();
	  memory.setByte(SP, PC >> 8);
	  decSP();
	  memory.setByte(SP, PC & 0xff);
	  PC = WZ = 0x0010;
	  return 11;
	}
      }
      ),

    // dd d8
    new Opcode("RET", "C",
	       1,
	       Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  if (!CFSET()) {
	    incPC();
	    return 5;
	  } else {
	    WZ = memory.getByte(SP);
	    incSP();
	    WZ += memory.getByte(SP) << 8;
	    incSP();
	    PC = WZ;
	    return 11;
	  }
	}
      }
      ),

    // dd d9
    new Opcode("EXX", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  int tb = B;
	  B = Ba;
	  Ba = tb;
	  tb = C;
	  C = Ca;
	  Ca = tb;
	  tb = D;
	  D = Da;
	  Da = tb;
	  tb = E;
	  E = Ea;
	  Ea = tb;
	  tb = H;
	  H = Ha;
	  Ha = tb;
	  tb = L;
	  L = La;
	  La = tb;
	  tb = WZ;
	  WZ = WZa;
	  WZa = tb;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd da
    new Opcode("JP", "C,<nn>",
	       3,
	       Processor.INS_JMP,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  if (!CFSET()) {
	    incPC();
	  } else {
	    PC = WZ;
	  }
	  return 10;
	}
      }
      ),

    // dd db
    new Opcode("IN", "A,(<n>)", 2, Processor.INS_IOR, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  int port = memory.getByte(PC);
	  WZ = ((A << 8) + port + 1) & 0xffff;
	  A = 0xff;
	  for (IOElement t: inputPorts.get(port)) {
	    A &= t.portInput(port);
	  }
	  incPC();
	  return 11;
	}
      }
      ),

    // dd dc
    new Opcode("CALL", "C,<nn>",
	       3,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  incPC();
	  if (!CFSET()) {
	    return 10;
	  } else {
	    decSP();
	    memory.setByte(SP, PC >> 8);
	    decSP();
	    memory.setByte(SP, PC & 0xff);
	    PC = WZ;
	    return 17;
	  }
	}
      }
      ),

    // dd dd
    null,

    // dd de
    new Opcode("SBC", "A,<n>", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int tb = memory.getByte(PC);
	  final int tw = A - tb - (F & CF);
	  final int cb = A ^ tb ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 7;
	}
      }
      ),

    // dd df
    new Opcode("RST", "<p>",
	       1,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  decSP();
	  memory.setByte(SP, PC >> 8);
	  decSP();
	  memory.setByte(SP, PC & 0xff);
	  PC = WZ = 0x0018;
	  return 11;
	}
      }
      ),

    // dd e0
    new Opcode("RET", "PO",
	       1,
	       Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  if (PFSET()) {
	    incPC();
	    return 5;
	  } else {
	    WZ = memory.getByte(SP);
	    incSP();
	    WZ += memory.getByte(SP) << 8;
	    incSP();
	    PC = WZ;
	    return 11;
	  }
	}
      }
      ),

    // dd e1
    new Opcode("POP", "HL", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  L = memory.getByte(SP);
	  incSP();
	  H = memory.getByte(SP);
	  incSP();
	  incPC();
	  return 10;
	}
      }
      ),

    // dd e2
    new Opcode("JP", "PO,<nn>",
	       3,
	       Processor.INS_JMP,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  if (PFSET()) {
	    incPC();
	  } else {
	    PC = WZ;
	  }
	  return 10;
	}
      }
      ),

    // dd e3
    new Opcode("EX", "(SP),HL",
	       1,
	       Processor.INS_MR | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(SP);
	  memory.setByte(SP, L);
	  L = tb;
	  final int tw = (SP + 1) & 0xffff;
	  tb = memory.getByte(tw);
	  memory.setByte(tw, H);
	  H = tb;
	  WZ = HL();
	  incPC();
	  return 19;
	}
      }
      ),

    // dd e4
    new Opcode("CALL", "PO,<nn>",
	       3,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  incPC();
	  if (PFSET()) {
	    return 10;
	  } else {
	    decSP();
	    memory.setByte(SP, PC >> 8);
	    decSP();
	    memory.setByte(SP, PC & 0xff);
	    PC = WZ;
	    return 17;
	  }
	}
      }
      ),

    // dd e5
    new Opcode("PUSH", "HL", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  decSP();
	  memory.setByte(SP, H);
	  decSP();
	  memory.setByte(SP, L);
	  incPC();
	  return 11;
	}
      }
      ),

    // dd e6
    new Opcode("AND", "<n>", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int tb = memory.getByte(PC);
	  A &= tb;
	  SETHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 7;
	}
      }
      ),

    // dd e7
    new Opcode("RST", "<p>",
	       1,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  decSP();
	  memory.setByte(SP, PC >> 8);
	  decSP();
	  memory.setByte(SP, PC & 0xff);
	  PC = WZ = 0x0020;
	  return 11;
	}
      }
      ),

    // dd e8
    new Opcode("RET", "PE",
	       1,
	       Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  if (!PFSET()) {
	    incPC();
	    return 5;
	  } else {
	    WZ = memory.getByte(SP);
	    incSP();
	    WZ += memory.getByte(SP) << 8;
	    incSP();
	    PC = WZ;
	    return 11;
	  }
	}
      }
      ),

    // dd e9
    new Opcode("JP", "(HL)", 1, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  PC = HL();
	  return 4;
	}
      }
      ),

    // dd ea
    new Opcode("JP", "PE,<nn>", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  if (!PFSET()) {
	    incPC();
	  } else {
	    PC = WZ;
	  }
	  return 10;
	}
      }
      ),

    // dd eb
    new Opcode("EX", "DE,HL", 1, 0, new Executable() {
	@Override
	public int exec() {
	  int tb = H;
	  H = D;
	  D = tb;
	  tb = L;
	  L = E;
	  E = tb;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd ec
    new Opcode("CALL", "PE,<nn>",
	       3,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  incPC();
	  if (!PFSET()) {
	    return 10;
	  } else {
	    decSP();
	    memory.setByte(SP, PC >> 8);
	    decSP();
	    memory.setByte(SP, PC & 0xff);
	    PC = WZ;
	    return 17;
	  }
	}
      }
      ),

    // dd ed
    null,

    // dd ee
    new Opcode("XOR", "<n>", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  A ^= memory.getByte(PC);
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 7;
	}
      }
      ),

    // dd ef
    new Opcode("RST", "<p>",
	       1,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  decSP();
	  memory.setByte(SP, PC >> 8);
	  decSP();
	  memory.setByte(SP, PC & 0xff);
	  PC = WZ = 0x0028;
	  return 11;
	}
      }
      ),

    // dd f0
    new Opcode("RET", "P",
	       1,
	       Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  if (SFSET()) {
	    incPC();
	    return 5;
	  } else {
	    WZ = memory.getByte(SP);
	    incSP();
	    WZ += memory.getByte(SP) << 8;
	    incSP();
	    PC = WZ;
	    return 11;
	  }
	}
      }
      ),

    // dd f1
    new Opcode("POP", "AF", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  F = memory.getByte(SP);
	  incSP();
	  A = memory.getByte(SP);
	  incSP();
	  incPC();
	  return 10;
	}
      }
      ),

    // dd f2
    new Opcode("JP", "P,<nn>", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  if (SFSET()) {
	    incPC();
	  } else {
	    PC = WZ;
	  }
	  return 10;
	}
      }
      ),

    // dd f3
    new Opcode("DI", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  IFF1 = false;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd f4
    new Opcode("CALL", "P,<nn>",
	       3,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  incPC();
	  if (SFSET()) {
	    return 10;
	  } else {
	    decSP();
	    memory.setByte(SP, PC >> 8);
	    decSP();
	    memory.setByte(SP, PC & 0xff);
	    PC = WZ;
	    return 17;
	  }
	}
      }
      ),

    // dd f5
    new Opcode("PUSH", "AF", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  decSP();
	  memory.setByte(SP, A);
	  decSP();
	  memory.setByte(SP, F);
	  incPC();
	  return 11;
	}
      }
      ),

    // dd f6
    new Opcode("OR", "<n>", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  A |= memory.getByte(PC);
	  CLEARHF();
	  CLEARCF();
	  CLEARNF();
	  F5(A);
	  incPC();
	  return 7;
	}
      }
      ),

    // dd f7
    new Opcode("RST", "<p>",
	       1,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  decSP();
	  memory.setByte(SP, PC >> 8);
	  decSP();
	  memory.setByte(SP, PC & 0xff);
	  PC = WZ = 0x0030;
	  return 11;
	}
      }
      ),

    // dd f8
    new Opcode("RET", "M",
	       1,
	       Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  if (!SFSET()) {
	    incPC();
	    return 5;
	  } else {
	    WZ = memory.getByte(SP);
	    incSP();
	    WZ += memory.getByte(SP) << 8;
	    incSP();
	    PC = WZ;
	    return 11;
	  }
	}
      }
      ),

    // dd f9
    new Opcode("LD", "SP,HL", 1, 0, new Executable() {
	@Override
	public int exec() {
	  SP = HL();
	  incPC();
	  return 6;
	}
      }
      ),

    // dd fa
    new Opcode("JP", "M,<nn>", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  if (!SFSET()) {
	    incPC();
	  } else {
	    PC = WZ;
	  }
	  return 10;
	}
      }
      ),

    // dd fb
    new Opcode("EI", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  IFF1 = true;
	  incPC();
	  return 4;
	}
      }
      ),

    // dd fc
    new Opcode("CALL", "M,<nn>",
	       3,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  incPC();
	  if (!SFSET()) {
	    return 10;
	  } else {
	    decSP();
	    memory.setByte(SP, PC >> 8);
	    decSP();
	    memory.setByte(SP, PC & 0xff);
	    PC = WZ;
	    return 17;
	  }
	}
      }
      ),

    // dd fd
    null,

    // dd fe
    new Opcode("CP", "<n>", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int tb = memory.getByte(PC);
	  final int tw = A - tb;
	  final int cb = A ^ tb ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  F22(tw & 0xff, tb);
	  SETNF();
	  incPC();
	  return 7;
	}
      }
      ),

    // dd ff
    new Opcode("RST", "<p>",
	       1,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  decSP();
	  memory.setByte(SP, PC >> 8);
	  decSP();
	  memory.setByte(SP, PC & 0xff);
	  PC = WZ = 0x0038;
	  return 11;
	}
      }
      )
  };

  /**
   * The array of opcodes with the prefix ED.
   */
  protected final Opcode[] opcodesED = new Opcode[] {

    // ed 00 - ed 3f
    null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null,

    // ed 40
    new Opcode("IN", "B,(C)", 2, Processor.INS_IOR, new Executable() {
	@Override
	public int exec() {
	  B = 0xff;
	  for (IOElement t: inputPorts.get(C)) {
	    B &= t.portInput(C);
	  }
	  F5(B);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 12;
	}
      }
      ),

    // ed 41
    new Opcode("OUT", "(C),B", 2, Processor.INS_IOW, new Executable() {
	@Override
	public int exec() {
	  for (IOElement t: outputPorts.get(C)) {
	    t.portOutput(C, B);
	  }
	  incPC();
	  return 12;
	}
      }
      ),

    // ed 42
    new Opcode("SBC", "HL,BC", 1, 0, new Executable() {
	@Override
	public int exec() {
	  WZ = (HL() + 1) & 0xffff;
	  final int tw = (H << 8) + L - (B << 8) - C - (F & CF);
	  final int cb = H ^ B ^ (tw >> 8);
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((cb & 0x100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  L = tw & 0xff;
	  H = (tw >> 8) & 0xff;
	  F4(H);
	  if ((H | L) == 0) {
	    SETZF();
	  } else {
	    CLEARZF();
	  }
	  SETNF();
	  incPC();
	  return 15;
	}
      }
      ),

    // ed 43
    new Opcode("LD", "(<nn>),BC", 3, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  memory.setByte(WZ, C);
	  incWZ();
	  memory.setByte(WZ, B);
	  incPC();
	  return 20;
	}
      }
      ),

    // ed 44
    new Opcode("NEG", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if (A != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if ((A & 0x0f) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if (A == 0x80) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = (-A) & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // ed 45
    new Opcode("RETN", "", 1, Processor.INS_RET | Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  IFF1 = IFF2;
	  WZ = memory.getByte(SP);
	  incSP();
	  WZ += memory.getByte(SP) << 8;
	  incSP();
	  PC = WZ;
	  return 14;
	}
      }
      ),

    // ed 46
    new Opcode("IM", "0", 1, 0, new Executable() {
	@Override
	public int exec() {
	  IM = 0;
	  incPC();
	  return 8;
	}
      }
      ),

    // ed 47
    new Opcode("LD", "I,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  I = A;
	  incPC();
	  return 9;
	}
      }
      ),


    // ed 48
    new Opcode("IN", "C,(C)", 2, Processor.INS_IOR, new Executable() {
	@Override
	public int exec() {
	  final int tb = C;
	  C = 0xff;
	  for (IOElement t: inputPorts.get(tb)) {
	    C &= t.portInput(tb);
	  }
	  F5(C);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 12;
	}
      }
      ),

    // ed 49
    new Opcode("OUT", "(C),C", 2, Processor.INS_IOW, new Executable() {
	@Override
	public int exec() {
	  for (IOElement t: outputPorts.get(C)) {
	    t.portOutput(C, C);
	  }
	  incPC();
	  return 12;
	}
      }
      ),

    // ed 4a
    new Opcode("ADC", "HL,BC", 1, 0, new Executable() {
	@Override
	public int exec() {
	  WZ = (HL() + 1) & 0xffff;
	  final int tw = (H << 8) + L + (B << 8) + C + (F & CF);
	  final int cb = H ^ B ^ (tw >> 8);
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((cb & 0x100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  L = tw & 0xff;
	  H = (tw >> 8) & 0xff;
	  F4(H);
	  if ((H | L) == 0) {
	    SETZF();
	  } else {
	    CLEARZF();
	  }
	  CLEARNF();
	  incPC();
	  return 15;
	}
      }
      ),

    // ed 4b
    new Opcode("LD", "BC,(<nn>)", 3, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  C = memory.getByte(WZ);
	  incWZ();
	  B = memory.getByte(WZ);
	  incPC();
	  return 20;
	}
      }
      ),

    // ed 4c (undocumented)
    new Opcode("NEG", "", 1, Processor.INS_UND, new Executable() {
	@Override
	public int exec() {
	  if (A != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if ((A & 0x0f) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if (A == 0x80) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = (-A) & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // ed 4d
    new Opcode("RETI", "",
	       1,
	       Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  IFF1 = IFF2;
	  WZ = memory.getByte(SP);
	  incSP();
	  WZ += memory.getByte(SP) << 8;
	  incSP();
	  PC = WZ;
	  return 14;
	}
      }
      ),

    // ed 4e (undocumented)
    new Opcode("IM", "0", 1, Processor.INS_UND, new Executable() {
	@Override
	public int exec() {
	  IM = 0;
	  incPC();
	  return 8;
	}
      }
      ),

    // ed 4f
    new Opcode("LD", "R,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  R = R7 = A;
	  incPC();
	  return 9;
	}
      }
      ),

    // ed 50
    new Opcode("IN", "D,(C)", 2, Processor.INS_IOR, new Executable() {
	@Override
	public int exec() {
	  D = 0xff;
	  for (IOElement t: inputPorts.get(C)) {
	    D &= t.portInput(C);
	  }
	  F5(D);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 12;
	}
      }
      ),

    // ed 51
    new Opcode("OUT", "(C),D", 2, Processor.INS_IOW, new Executable() {
	@Override
	public int exec() {
	  for (IOElement t: outputPorts.get(C)) {
	    t.portOutput(C, D);
	  }
	  incPC();
	  return 12;
	}
      }
      ),

    // ed 52
    new Opcode("SBC", "HL,DE", 1, 0, new Executable() {
	@Override
	public int exec() {
	  WZ = (HL() + 1) & 0xffff;
	  final int tw = (H << 8) + L - (D << 8) - E - (F & CF);
	  final int cb = H ^ D ^ (tw >> 8);
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((cb & 0x100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  L = tw & 0xff;
	  H = (tw >> 8) & 0xff;
	  F4(H);
	  if ((H | L) == 0) {
	    SETZF();
	  } else {
	    CLEARZF();
	  }
	  SETNF();
	  incPC();
	  return 15;
	}
      }
      ),

    // ed 53
    new Opcode("LD", "(<nn>),DE", 3, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  memory.setByte(WZ, E);
	  incWZ();
	  memory.setByte(WZ, D);
	  incPC();
	  return 20;
	}
      }
      ),

    // ed 54 (undocumented)
    new Opcode("NEG", "", 1, Processor.INS_UND, new Executable() {
	@Override
	public int exec() {
	  if (A != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if ((A & 0x0f) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if (A == 0x80) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = (-A) & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // ed 55 (undocumented)
    new Opcode("RETN", "",
	       1,
	       Processor.INS_UND | Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  IFF1 = IFF2;
	  WZ = memory.getByte(SP);
	  incSP();
	  WZ += memory.getByte(SP) << 8;
	  incSP();
	  PC = WZ;
	  return 14;
	}
      }
      ),

    // ed 56
    new Opcode("IM", "1", 1, 0, new Executable() {
	@Override
	public int exec() {
	  IM = 1;
	  incPC();
	  return 8;
	}
      }
      ),

    // ed 57
    new Opcode("LD", "A,I", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = I;
	  F4(A);
	  if (IFF2) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 9;
	}
      }
      ),

    // ed 58
    new Opcode("IN", "E,(C)", 2, Processor.INS_IOR, new Executable() {
	@Override
	public int exec() {
	  E = 0xff;
	  for (IOElement t: inputPorts.get(C)) {
	    E &= t.portInput(C);
	  }
	  F5(E);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 12;
	}
      }
      ),

    // ed 59
    new Opcode("OUT", "(C),E", 2, Processor.INS_IOW, new Executable() {
	@Override
	public int exec() {
	  for (IOElement t: outputPorts.get(C)) {
	    t.portOutput(C, E);
	  }
	  incPC();
	  return 12;
	}
      }
      ),

    // ed 5a
    new Opcode("ADC", "HL,DE", 1, 0, new Executable() {
	@Override
	public int exec() {
	  WZ = (HL() + 1) & 0xffff;
	  final int tw = (H << 8) + L + (D << 8) + E + (F & CF);
	  final int cb = H ^ D ^ (tw >> 8);
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((cb & 0x100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  L = tw & 0xff;
	  H = (tw >> 8) & 0xff;
	  F4(H);
	  if ((H | L) == 0) {
	    SETZF();
	  } else {
	    CLEARZF();
	  }
	  CLEARNF();
	  incPC();
	  return 15;
	}
      }
      ),

    // ed 5b
    new Opcode("LD", "DE,(<nn>)", 3, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  E = memory.getByte(WZ);
	  incWZ();
	  D = memory.getByte(WZ);
	  incPC();
	  return 20;
	}
      }
      ),

    // ed 5c (undocumented)
    new Opcode("NEG", "", 1, Processor.INS_UND, new Executable() {
	@Override
	public int exec() {
	  if (A != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if ((A & 0x0f) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if (A == 0x80) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = (-A) & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // ed 5d (undocumented)
    new Opcode("RETN", "",
	       1,
	       Processor.INS_UND | Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  IFF1 = IFF2;
	  WZ = memory.getByte(SP);
	  incSP();
	  WZ += memory.getByte(SP) << 8;
	  incSP();
	  PC = WZ;
	  return 14;
	}
      }
      ),

    // ed 5e
    new Opcode("IM", "2", 1, 0, new Executable() {
	@Override
	public int exec() {
	  IM = 2;
	  incPC();
	  return 8;
	}
      }
      ),

    // ed 5f
    new Opcode("LD", "A,R", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = (R & 0x7f) | (R7 & 0x80);
	  F4(A);
	  if (IFF2) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 9;
	}
      }
      ),

    // ed 60
    new Opcode("IN", "H,(C)", 2, Processor.INS_IOR, new Executable() {
	@Override
	public int exec() {
	  H = 0xff;
	  for (IOElement t: inputPorts.get(C)) {
	    H &= t.portInput(C);
	  }
	  F5(H);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 12;
	}
      }
      ),

    // ed 61
    new Opcode("OUT", "(C),H", 2, Processor.INS_IOW, new Executable() {
	@Override
	public int exec() {
	  for (IOElement t: outputPorts.get(C)) {
	    t.portOutput(C, H);
	  }
	  incPC();
	  return 12;
	}
      }
      ),

    // ed 62
    new Opcode("SBC", "HL,HL", 1, 0, new Executable() {
	@Override
	public int exec() {
	  WZ = (HL() + 1) & 0xffff;
	  final int tw = -(F & CF);
	  if ((tw & 0x1000) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x10000) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((tw ^ (tw >> 1)) & 0x8000) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  L = tw & 0xff;
	  H = (tw >> 8) & 0xff;
	  F4(H);
	  if ((H | L) == 0) {
	    SETZF();
	  } else {
	    CLEARZF();
	  }
	  SETNF();
	  incPC();
	  return 15;
	}
      }
      ),

    // ed 63
    new Opcode("LD", "(<nn>),HL", 3, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  memory.setByte(WZ, L);
	  incWZ();
	  memory.setByte(WZ, H);
	  incPC();
	  return 20;
	}
      }
      ),

    // ed 64 (undocumented)
    new Opcode("NEG", "", 1, Processor.INS_UND, new Executable() {
	@Override
	public int exec() {
	  if (A != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if ((A & 0x0f) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if (A == 0x80) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = (-A) & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // ed 65 (undocumented)
    new Opcode("RETN", "",
	       1,
	       Processor.INS_UND | Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  IFF1 = IFF2;
	  WZ = memory.getByte(SP);
	  incSP();
	  WZ += memory.getByte(SP) << 8;
	  incSP();
	  PC = WZ;
	  return 14;
	}
      }
      ),

    // ed 66 (undocumented)
    new Opcode("IM", "0", 1, Processor.INS_UND, new Executable() {
	@Override
	public int exec() {
	  IM = 0;
	  incPC();
	  return 8;
	}
      }
      ),

    // ed 67
    new Opcode("RRD", "",
	       1,
	       Processor.INS_MR | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  final int tb = memory.getByte(HL());
	  memory.setByte(HL(), (tb >> 4) | ((A & 0x0f) << 4));
	  A = (A & 0xf0) | (tb & 0x0f);
	  F5(A);
	  CLEARHF();
	  CLEARNF();
	  WZ = (HL() + 1) & 0xffff;
	  incPC();
	  return 18;
	}
      }
      ),

    // ed 68
    new Opcode("IN", "L,(C)", 2, Processor.INS_IOR, new Executable() {
	@Override
	public int exec() {
	  L = 0xff;
	  for (IOElement t: inputPorts.get(C)) {
	    L &= t.portInput(C);
	  }
	  F5(L);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 12;
	}
      }
      ),

    // ed 69
    new Opcode("OUT", "(C),L", 2, Processor.INS_IOW, new Executable() {
	@Override
	public int exec() {
	  for (IOElement t: outputPorts.get(C)) {
	    t.portOutput(C, L);
	  }
	  incPC();
	  return 12;
	}
      }
      ),

    // ed 6a
    new Opcode("ADC", "HL,HL", 1, 0, new Executable() {
	@Override
	public int exec() {
	  WZ = (HL() + 1) & 0xffff;
	  final int tw = (H << 9) + (L << 1) + (F & CF);
	  if ((tw & 0x1000) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x10000) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((tw ^ (tw >> 1)) & 0x8000) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  L = tw & 0xff;
	  H = (tw >> 8) & 0xff;
	  F4(H);
	  if ((H | L) == 0) {
	    SETZF();
	  } else {
	    CLEARZF();
	  }
	  CLEARNF();
	  incPC();
	  return 15;
	}
      }
      ),

    // ed 6b
    new Opcode("LD", "HL,(<nn>)", 3, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  L = memory.getByte(WZ);
	  incWZ();
	  H = memory.getByte(WZ);
	  incPC();
	  return 20;
	}
      }
      ),

    // ed 6c (undocumented)
    new Opcode("NEG", "", 1, Processor.INS_UND, new Executable() {
	@Override
	public int exec() {
	  if (A != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if ((A & 0x0f) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if (A == 0x80) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = (-A) & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // ed 6d (undocumented)
    new Opcode("RETN", "",
	       1,
	       Processor.INS_UND | Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  IFF1 = IFF2;
	  WZ = memory.getByte(SP);
	  incSP();
	  WZ += memory.getByte(SP) << 8;
	  incSP();
	  PC = WZ;
	  return 14;
	}
      }
      ),

    // ed 6e (undocumented)
    new Opcode("IM", "0", 1, Processor.INS_UND, new Executable() {
	@Override
	public int exec() {
	  IM = 0;
	  incPC();
	  return 8;
	}
      }
      ),

    // ed 6f
    new Opcode("RLD", "",
	       1,
	       Processor.INS_MR | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  final int tb = memory.getByte(HL());
	  memory.setByte(HL(), ((tb & 0x0f) << 4) | (A & 0x0f));
	  A = (A & 0xf0) | ((tb >> 4) & 0x0f);
	  F5(A);
	  CLEARHF();
	  CLEARNF();
	  WZ = (HL() + 1) & 0xffff;
	  incPC();
	  return 18;
	}
      }
      ),

    // ed 70
    new Opcode("IN", "(C)", 2, Processor.INS_IOR, new Executable() {
	@Override
	public int exec() {
	  int tb = 0xff;
	  for (IOElement t: inputPorts.get(C)) {
	    tb &= t.portInput(C);
	  }
	  F5(tb);
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 12;
	}
      }
      ),

    // ed 71
    new Opcode("OUT", "(C),0", 2, Processor.INS_IOW, new Executable() {
	@Override
	public int exec() {
	  for (IOElement t: outputPorts.get(C)) {
	    t.portOutput(C, 0);
	  }
	  incPC();
	  return 12;
	}
      }
      ),

    // ed 72
    new Opcode("SBC", "HL,SP", 1, 0, new Executable() {
	@Override
	public int exec() {
	  WZ = (HL() + 1) & 0xffff;
	  final int tw = (H << 8) + L - SP - (F & CF);
	  final int cb = H ^ (SP >> 8) ^ (tw >> 8);
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((cb & 0x100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  L = tw & 0xff;
	  H = (tw >> 8) & 0xff;
	  F4(H);
	  if ((H | L) == 0) {
	    SETZF();
	  } else {
	    CLEARZF();
	  }
	  SETNF();
	  incPC();
	  return 15;
	}
      }
      ),

    // ed 73
    new Opcode("LD", "(<nn>),SP", 3, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  memory.setByte(WZ, SP & 0xff);
	  incWZ();
	  memory.setByte(WZ, SP >> 8);
	  incPC();
	  return 20;
	}
      }
      ),

    // ed 74 (undocumented)
    new Opcode("NEG", "", 1, Processor.INS_UND, new Executable() {
	@Override
	public int exec() {
	  if (A != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if ((A & 0x0f) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if (A == 0x80) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = (-A) & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // ed 75 (undocumented)
    new Opcode("RETN", "",
	       1,
	       Processor.INS_UND | Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  IFF1 = IFF2;
	  WZ = memory.getByte(SP);
	  incSP();
	  WZ += memory.getByte(SP) << 8;
	  incSP();
	  PC = WZ;
	  return 14;
	}
      }
      ),

    // ed 76 (undocumented)
    new Opcode("IM", "1", 1, Processor.INS_UND, new Executable() {
	@Override
	public int exec() {
	  IM = 1;
	  incPC();
	  return 8;
	}
      }
      ),

    // ed 77
    null,

    // ed 78
    new Opcode("IN", "A,(C)", 2, Processor.INS_IOR, new Executable() {
	@Override
	public int exec() {
	  A = 0xff;
	  for (IOElement t: inputPorts.get(C)) {
	    A &= t.portInput(C);
	  }
	  F5(A);
	  CLEARHF();
	  CLEARNF();
	  WZ = (BC() + 1) & 0xffff;
	  incPC();
	  return 12;
	}
      }
      ),

    // ed 79
    new Opcode("OUT", "(C),A", 2, Processor.INS_IOW, new Executable() {
	@Override
	public int exec() {
	  for (IOElement t: outputPorts.get(C)) {
	    t.portOutput(C, A);
	  }
	  WZ = (BC() + 1) & 0xffff;
	  incPC();
	  return 12;
	}
      }
      ),

    // ed 7a
    new Opcode("ADC", "HL,SP", 1, 0, new Executable() {
	@Override
	public int exec() {
	  WZ = (HL() + 1) & 0xffff;
	  final int tw = (H << 8) + L + SP + (F & CF);
	  final int cb = H ^ (SP >> 8) ^ (tw >> 8);
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if ((cb & 0x100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  L = tw & 0xff;
	  H = (tw >> 8) & 0xff;
	  F4(H);
	  if ((H | L) == 0) {
	    SETZF();
	  } else {
	    CLEARZF();
	  }
	  CLEARNF();
	  incPC();
	  return 15;
	}
      }
      ),

    // ed 7b
    new Opcode("LD", "SP,(<nn>)", 3, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  WZ = memory.getByte(PC);
	  incPC();
	  WZ += memory.getByte(PC) << 8;
	  final int tb = memory.getByte(WZ);
	  incWZ();
	  SP = (memory.getByte(WZ) << 8) + tb;
	  incPC();
	  return 20;
	}
      }
      ),

    // ed 7c (undocumented)
    new Opcode("NEG", "", 1, Processor.INS_UND, new Executable() {
	@Override
	public int exec() {
	  if (A != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  if ((A & 0x0f) != 0) {
	    SETHF();
	  } else {
	    CLEARHF();
	  }
	  if (A == 0x80) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  A = (-A) & 0xff;
	  F4(A);
	  SETNF();
	  incPC();
	  return 8;
	}
      }
      ),

    // ed 7d (undocumented)
    new Opcode("RETN", "",
	       1,
	       Processor.INS_UND | Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  IFF1 = IFF2;
	  WZ = memory.getByte(SP);
	  incSP();
	  WZ += memory.getByte(SP) << 8;
	  incSP();
	  PC = WZ;
	  return 14;
	}
      }
      ),

    // ed 7e (undocumented)
    new Opcode("IM", "2", 1, Processor.INS_UND, new Executable() {
	@Override
	public int exec() {
	  IM = 2;
	  incPC();
	  return 8;
	}
      }
      ),

    // ed 7f
    null,

    // ed 80 - ed 9f
    null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null,

    // ed a0
    new Opcode("LDI", "", 1,
	       Processor.INS_MR | Processor.INS_MW | Processor.INS_BLK,
	       new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(HL());
	  memory.setByte(DE(), tb);
	  incHL();
	  incDE();
	  decBC();
	  tb += A;
	  if ((tb & 0x02) != 0) {
	    SETYF();
	  } else {
	    CLEARYF();
	  }
	  if ((tb & 0x08) != 0) {
	    SETXF();
	  } else {
	    CLEARXF();
	  }
	  if ((B | C) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 16;
	}
      }
      ),

    // ed a1
    new Opcode("CPI", "",
	       1,
	       Processor.INS_MR | Processor.INS_BLK,
	       new Executable() {
	@Override
	public int exec() {
	  incWZ();
	  final int tb = memory.getByte(HL());
	  int tw = A - tb;
	  final int cb = A ^ tb ^ tw;
	  incHL();
	  decBC();
	  F4(tw & 0xff);
	  if ((cb & 0x10) != 0) {
	    SETHF();
	    tw--;
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x02) != 0) {
	    SETYF();
	  } else {
	    CLEARYF();
	  }
	  if ((tw & 0x08) != 0) {
	    SETXF();
	  } else {
	    CLEARXF();
	  }
	  if ((B | C) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  SETNF();
	  incPC();
	  return 16;
	}
      }
      ),

    // ed a2
    new Opcode("INI", "",
	       1,
	       Processor.INS_IOR | Processor.INS_MW | Processor.INS_BLK,
	       new Executable() {
	@Override
	public int exec() {
	  WZ = (BC() + 1) & 0xffff;
	  int tb = 0xff;
	  for (IOElement t: inputPorts.get(C)) {
	    tb &= t.portInput(C);
	  }
	  memory.setByte(HL(), tb);
	  incHL();
	  B--;
	  F4(B);
	  final int tw = ((C + 1) & 0xff) + tb;
	  if ((tb & SF) != 0) {
	    SETNF();
	  } else {
	    CLEARNF();
	  }
	  if ((tw & 0x100) != 0) {
	    SETHF();
	    SETCF();
	  } else {
	    CLEARHF();
	    CLEARCF();
	  }
	  if ((TBL5[(tw & 0x07) ^ B] & PF) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  incPC();
	  return 16;
	}
      }
      ),

    // ed a3
    new Opcode("OUTI", "",
	       1,
	       Processor.INS_IOW | Processor.INS_MR | Processor.INS_BLK,
	       new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(HL());
	  for (IOElement t: outputPorts.get(C)) {
	    t.portOutput(C, tb);
	  }
	  incHL();
	  B--;
	  WZ = (BC() + 1) & 0xffff;
	  F4(B);
	  final int tw = L + tb;
	  if ((tb & SF) != 0) {
	    SETNF();
	  } else {
	    CLEARNF();
	  }
	  if ((tw & 0x100) != 0) {
	    SETHF();
	    SETCF();
	  } else {
	    CLEARHF();
	    CLEARCF();
	  }
	  if ((TBL5[(tw & 0x07) ^ B] & PF) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  incPC();
	  return 16;
	}
      }
      ),

    // ed a4 - ed a7
    null, null, null, null,

    // ed a8
    new Opcode("LDD", "",
	       1,
	       Processor.INS_MR | Processor.INS_MW | Processor.INS_BLK,
	       new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(HL());
	  memory.setByte(DE(), tb);
	  decHL();
	  decDE();
	  decBC();
	  tb += A;
	  if ((tb & 0x02) != 0) {
	    SETYF();
	  } else {
	    CLEARYF();
	  }
	  if ((tb & 0x08) != 0) {
	    SETXF();
	  } else {
	    CLEARXF();
	  }
	  if ((B | C) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  CLEARHF();
	  CLEARNF();
	  incPC();
	  return 16;
	}
      }
      ),

    // ed a9
    new Opcode("CPD", "",
	       1,
	       Processor.INS_MR | Processor.INS_BLK,
	       new Executable() {
	@Override
	public int exec() {
	  decWZ();
	  final int tb = memory.getByte(HL());
	  int tw = A - tb;
	  final int cb = A ^ tb ^ tw;
	  decHL();
	  decBC();
	  F4(tw & 0xff);
	  if ((cb & 0x10) != 0) {
	    SETHF();
	    tw--;
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x02) != 0) {
	    SETYF();
	  } else {
	    CLEARYF();
	  }
	  if ((tw & 0x08) != 0) {
	    SETXF();
	  } else {
	    CLEARXF();
	  }
	  if ((B | C) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  SETNF();
	  incPC();
	  return 16;
	}
      }
      ),

    // ed aa
    new Opcode("IND", "",
	       1,
	       Processor.INS_IOW | Processor.INS_MW | Processor.INS_BLK,
	       new Executable() {
	@Override
	public int exec() {
	  WZ = (BC() - 1) & 0xffff;
	  int tb = 0xff;
	  for (IOElement t: inputPorts.get(C)) {
	    tb &= t.portInput(C);
	  }
	  memory.setByte(HL(), tb);
	  decHL();
	  B--;
	  F4(B);
	  final int tw = ((C - 1) & 0xff) + tb;
	  if ((tb & SF) != 0) {
	    SETNF();
	  } else {
	    CLEARNF();
	  }
	  if ((tw & 0x100) != 0) {
	    SETHF();
	    SETCF();
	  } else {
	    CLEARHF();
	    CLEARCF();
	  }
	  if ((TBL5[(tw & 0x07) ^ B] & PF) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  incPC();
	  return 16;
	}
      }
      ),

    // ed ab
    new Opcode("OUTD", "",
	       1,
	       Processor.INS_IOW | Processor.INS_MR | Processor.INS_BLK,
	       new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(HL());
	  for (IOElement t: outputPorts.get(C)) {
	    t.portOutput(C, tb);
	  }
	  decHL();
	  B--;
	  WZ = (BC() - 1) & 0xffff;
	  F4(B);
	  final int tw = L + tb;
	  if ((tb & SF) != 0) {
	    SETNF();
	  } else {
	    CLEARNF();
	  }
	  if ((tw & 0x100) != 0) {
	    SETHF();
	    SETCF();
	  } else {
	    CLEARHF();
	    CLEARCF();
	  }
	  if ((TBL5[(tw & 0x07) ^ B] & PF) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  incPC();
	  return 16;
	}
      }
      ),

    // ed ac - ed af
    null, null, null, null,

    // ed b0
    new Opcode("LDIR", "",
	       1,
	       Processor.INS_MR | Processor.INS_MW |
	       Processor.INS_BLK | Processor.INS_REP,
	       new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(HL());
	  memory.setByte(DE(), tb);
	  incHL();
	  incDE();
	  decBC();
	  tb += A;
	  if ((tb & 0x02) != 0) {
	    SETYF();
	  } else {
	    CLEARYF();
	  }
	  if ((tb & 0x08) != 0) {
	    SETXF();
	  } else {
	    CLEARXF();
	  }
	  CLEARHF();
	  CLEARNF();
	  if ((B | C) != 0) {
	    SETPF();
	    WZ = PC;
	    decPC();
	    return 21;
	  } else {
	    CLEARPF();
	    incPC();
	    return 16;
	  }
	}
      }
      ),

    // ed b1
    new Opcode("CPIR", "",
	       1,
	       Processor.INS_MR | Processor.INS_BLK | Processor.INS_REP,
	       new Executable() {
	@Override
	public int exec() {
	  incWZ();
	  final int tb = memory.getByte(HL());
	  int tw = A - tb;
	  final int cb = A ^ tb ^ tw;
	  incHL();
	  decBC();
	  F4(tw & 0xff);
	  if ((cb & 0x10) != 0) {
	    SETHF();
	    tw--;
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x02) != 0) {
	    SETYF();
	  } else {
	    CLEARYF();
	  }
	  if ((tw & 0x08) != 0) {
	    SETXF();
	  } else {
	    CLEARXF();
	  }
	  SETNF();
	  if ((B | C) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if (!ZFSET() && PFSET()) {
	    WZ = PC;
	    decPC();
	    return 21;
	  } else {
	    incPC();
	    return 16;
	  }
	}
      }
      ),

    // ed b2
    new Opcode("INIR", "", 1,
	       Processor.INS_IOW | Processor.INS_MW |
	       Processor.INS_BLK | Processor.INS_REP,
	       new Executable() {
	@Override
	public int exec() {
	  WZ = (BC() + 1) & 0xffff;
	  int tb = 0xff;
	  for (IOElement t: inputPorts.get(C)) {
	    tb &= t.portInput(C);
	  }
	  memory.setByte(HL(), tb);
	  incHL();
	  B--;
	  F4(B);
	  final int tw = ((C + 1) & 0xff) + tb;
	  if ((tb & SF) != 0) {
	    SETNF();
	  } else {
	    CLEARNF();
	  }
	  if ((tw & 0x100) != 0) {
	    SETHF();
	    SETCF();
	  } else {
	    CLEARHF();
	    CLEARCF();
	  }
	  if ((TBL5[(tw & 0x07) ^ B] & PF) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if (B != 0) {
	    decPC();
	    return 21;
	  } else {
	    incPC();
	    return 16;
	  }
	}
      }
      ),

    // ed b3
    new Opcode("OTIR", "",
	       1,
	       Processor.INS_IOW | Processor.INS_MR |
	       Processor.INS_BLK | Processor.INS_REP,
	       new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(HL());
	  for (IOElement t: outputPorts.get(C)) {
	    t.portOutput(C, tb);
	  }
	  incHL();
	  B--;
	  WZ = (BC() + 1) & 0xffff;
	  F4(B);
	  final int tw = L + tb;
	  if ((tb & SF) != 0) {
	    SETNF();
	  } else {
	    CLEARNF();
	  }
	  if ((tw & 0x100) != 0) {
	    SETHF();
	    SETCF();
	  } else {
	    CLEARHF();
	    CLEARCF();
	  }
	  if ((TBL5[(tw & 0x07) ^ B] & PF) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if (B != 0) {
	    decPC();
	    return 21;
	  } else {
	    incPC();
	    return 16;
	  }
	}
      }
      ),

    // ed b4 - ed b7
    null, null, null, null,

    // ed b8
    new Opcode("LDDR", "",
	       1,
	       Processor.INS_MR | Processor.INS_MW |
	       Processor.INS_BLK | Processor.INS_REP,
	       new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(HL());
	  memory.setByte(DE(), tb);
	  decHL();
	  decDE();
	  decBC();
	  tb += A;
	  if ((tb & 0x02) != 0) {
	    SETYF();
	  } else {
	    CLEARYF();
	  }
	  if ((tb & 0x08) != 0) {
	    SETXF();
	  } else {
	    CLEARXF();
	  }
	  CLEARHF();
	  CLEARNF();
	  if ((B | C) != 0) {
	    SETPF();
	    WZ = PC;
	    decPC();
	    return 21;
	  } else {
	    CLEARPF();
	    incPC();
	    return 16;
	  }
	}
      }
      ),

    // ed b9
    new Opcode("CPDR", "",
	       1,
	       Processor.INS_MR | Processor.INS_BLK | Processor.INS_REP,
	       new Executable() {
	@Override
	public int exec() {
	  decWZ();
	  final int tb = memory.getByte(HL());
	  int tw = A - tb;
	  final int cb = A ^ tb ^ tw;
	  decHL();
	  decBC();
	  F4(tw & 0xff);
	  if ((cb & 0x10) != 0) {
	    SETHF();
	    tw--;
	  } else {
	    CLEARHF();
	  }
	  if ((tw & 0x02) != 0) {
	    SETYF();
	  } else {
	    CLEARYF();
	  }
	  if ((tw & 0x08) != 0) {
	    SETXF();
	  } else {
	    CLEARXF();
	  }
	  SETNF();
	  if ((B | C) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if (!ZFSET() && PFSET()) {
	    WZ = PC;
	    decPC();
	    return 21;
	  } else {
	    incPC();
	    return 16;
	  }
	}
      }
      ),

    // ed ba
    new Opcode("INDR", "",
	       1,
	       Processor.INS_IOR | Processor.INS_MW |
	       Processor.INS_BLK | Processor.INS_REP,
	       new Executable() {
	@Override
	public int exec() {
	  WZ = (BC() - 1) & 0xffff;
	  int tb = 0xff;
	  for (IOElement t: inputPorts.get(C)) {
	    tb &= t.portInput(C);
	  }
	  memory.setByte(HL(), tb);
	  decHL();
	  B--;
	  F4(B);
	  final int tw = ((C - 1) & 0xff) + tb;
	  if ((tb & SF) != 0) {
	    SETNF();
	  } else {
	    CLEARNF();
	  }
	  if ((tw & 0x100) != 0) {
	    SETHF();
	    SETCF();
	  } else {
	    CLEARHF();
	    CLEARCF();
	  }
	  if ((TBL5[(tw & 0x07) ^ B] & PF) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if (B != 0) {
	    decPC();
	    return 21;
	  } else {
	    incPC();
	    return 16;
	  }
	}
      }
      ),

    // ed bb
    new Opcode("OTDR", "",
	       1,
	       Processor.INS_IOW | Processor.INS_MR |
	       Processor.INS_BLK | Processor.INS_REP,
	       new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(HL());
	  for (IOElement t: outputPorts.get(C)) {
	    t.portOutput(C, tb);
	  }
	  decHL();
	  B--;
	  WZ = (BC() - 1) & 0xffff;
	  F4(B);
	  final int tw = L + tb;
	  if ((tb & SF) != 0) {
	    SETNF();
	  } else {
	    CLEARNF();
	  }
	  if ((tw & 0x100) != 0) {
	    SETHF();
	    SETCF();
	  } else {
	    CLEARHF();
	    CLEARCF();
	  }
	  if ((TBL5[(tw & 0x07) ^ B] & PF) != 0) {
	    SETPF();
	  } else {
	    CLEARPF();
	  }
	  if (B != 0) {
	    decPC();
	    return 21;
	  } else {
	    incPC();
	    return 16;
	  }
	}
      }
      ),

    // ed bc - ed bf
    null, null, null, null,

    // ed c0 - ed ff
    null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null
  };

  /**
   * Gets an Opcode.
   *
   * @param  n the first byte of {@code Opcode}
   * @return   {@code Opcode} value
   */
  public Opcode getOpcode(final int n) {
    assert (n >= 0) && (n < 0x100);
    return opcodes[n];
  }

  /**
   * An inner subclass of Disassembly.
   */
  protected class ZilogZ80Disassembly extends Disassembly {

    /**
     * The disassebled opcode.
     */
    protected Opcode opcode;

    /**
     * The main constructor.
     */
    protected ZilogZ80Disassembly(final int[] bytes) {
      assert bytes != null;
      this.bytes = bytes;
      opcode = opcodes[bytes[0]];
    }

    // for description see Disassembly
    @Override
    public int getPrefixLength() {
      return 0;
    }

    // for description see Disassembly
    @Override
    public String getMnemo(final boolean upperCase) {
      return upperCase ?
	     opcode.getMnemo().toUpperCase() :
	     opcode.getMnemo().toLowerCase();
    }

    // for description see Disassembly
    @Override
    public String getParameters(final boolean upperCase,
				final String template1,
				final String template2,
				final boolean prependZero) {
      final String parameters =
	(upperCase ?
	 opcode.getParameters().toUpperCase() :
	 opcode.getParameters().toLowerCase());
      switch (getLength()) {
	case 1:
	  return parameters;
	case 2: {
	  String value = String.format(template1, bytes[1]);
	  if (prependZero && !Character.isDigit(value.charAt(0))) {
	    value = "0" + value;
	  }
	  return parameters + value;
	}
	default: {
	  String value =
	    String.format(template2, (bytes[2] << 8) + bytes[1]);
	  if (prependZero && !Character.isDigit(value.charAt(0))) {
	    value = "0" + value;
	  }
	  return parameters + value;
	}
      }
    }

    // for description see Disassembly
    @Override
    public String getSimplified() {
      switch (getLength()) {
	case 1:
	  if (opcode.getParameters().isEmpty()) {
	    return opcode.getMnemo();
	  } else {
	    return String.format("%s %s",
				 opcode.getMnemo(),
				 opcode.getParameters());
	  }
	case 2:
	  return String.format("%s %s%02X",
			       opcode.getMnemo(),
			       opcode.getParameters(),
			       bytes[1]);
	default:
	  return String.format("%s %s%04X",
			       opcode.getMnemo(),
			       opcode.getParameters(),
			       (bytes[2] << 8) + bytes[1]);
      }
    }
  }

  // for description see Processor
  @Override
  public Disassembly getDisassembly(final int address) {
    assert (address >= 0) && (address < 0x10000);
    final int[] bytes = new int[opcodes[memory.getByte(address)].getLength()];
    for (int i = 0; i < bytes.length; i++) {
      bytes[i] = memory.getByte((address + i) & 0xffff);
    }
    return new ZilogZ80Disassembly(bytes);
  }

  // for description see Processor
  @Override
  public Disassembly getDisassembly(final byte[] bytes, final int address) {
    assert bytes != null;
    assert (address >= 0) && (address < 0x10000);
    final int bytesLength = bytes.length;
    final Opcode opcode = opcodes[bytes[address % bytesLength] & 0xff];
    final int length = opcode.getLength();
    final int[] newBytes = new int[length];
    for (int i = 0; i < length; i++) {
      newBytes[i] = bytes[(address + i) % bytesLength] & 0xff;
    }
    return new ZilogZ80Disassembly(newBytes);
  }

  /**
   * Gets a string representation of CPU state.
   *
   * @return the CPU state as a string
   */
  public String CPUState() {
    return String.format(
      "PC:%04x SP:%04x A:%02x B:%02x C:%02x D:%02x E:%02x H:%02x L:%02x " +
      "CF:%d HF:%d ZF:%d SF:%d PF:%d IFF1:%d IFF2:%d F:%02x",
      PC,
      SP,
      A,
      B,
      C,
      D,
      E,
      H,
      L,
      (CFSET() ? 1 : 0),
      (HFSET() ? 1 : 0),
      (ZFSET() ? 1 : 0),
      (SFSET() ? 1 : 0),
      (PE() ? 1 : 0),
      (IFF1 ? 1 : 0),
      (IFF2 ? 1 : 0),
      F);
  }

  /**
   * Gets the disassembled instruction.
   *
   * @param  pc the Program Counter (PC)
   * @return    the disassembled instruction
   */
  public String CPUDissassemble(final int pc) {
    assert (pc >= 0) && (pc < 0x10000);
    int opc = memory.getByte(pc);

    switch (opcodes[opc].getLength()) {
      case 1:
	return String.format("%08d %04x %02x        %s %s",
			     cycleCounter,
			     pc,
			     opc,
			     opcodes[opc].getMnemo(),
			     opcodes[opc].getParameters());
      case 2:
	return String.format("%08d %04x %02x %02x     %s %s%02x",
			     cycleCounter,
			     pc,
			     opc,
			     memory.getByte((PC + 1) & 0xffff),
			     opcodes[opc].getMnemo(),
			     opcodes[opc].getParameters(),
			     memory.getByte((PC + 1) & 0xffff));
      default:
	return String.format("%08d %04x %02x %02x %02x  %s %s%02x%02x",
			     cycleCounter,
			     pc,
			     opc,
			     memory.getByte((PC + 1) & 0xffff),
			     memory.getByte((PC + 2) & 0xffff),
			     opcodes[opc].getMnemo(),
			     opcodes[opc].getParameters(),
			     memory.getByte((PC + 2) & 0xffff),
			     memory.getByte((PC + 1) & 0xffff));
    }
  }

  // for description see Processor
  @Override
  public void exec(final long minCycles,
		   final int mask,
		   final Set<Integer> breakpoints) {
    assert minCycles >= 0;
    final long endCycleCounter = cycleCounter + minCycles;

    while (!suspended) {
      CPUScheduler.runSchedule(cycleCounter);
      if (resetPending) {
	reset();
	break;
      } else if (interruptPending != -1) {
	interrupt(interruptPending);
	break;
      } else {
	int prefix = 0;
	Opcode[] table = null;
	int tb;
	for (;;) {
	  R++;
	  tb = memory.getByte(PC);
	  if ((tb == 0xdd) || (tb == 0xfd)) {
	    prefix = tb;
	    cycleCounter += 4;
	    incPC();
	    continue;
	  }
	  if (tb == 0xcb) {
	    if (prefix == 0xdd) {
	      // table = opcodesCBDD;
	    } else if (prefix == 0xfd) {
	      // table = opcodesCBFD;
	    } else {
	      R++;
	      table = opcodesCB;
	    }
	    incPC();
	    tb = memory.getByte(PC);
	    break;
	  }
	  if (tb == 0xed) {
	    table = opcodesED;
	    R++;
	    incPC();
	    tb = memory.getByte(PC);
	    break;
	  }
	  if (prefix == 0) {
	    table = opcodes;
	  } else if ((prefix == 0xdd) && (opcodesDD[tb] != null)) {
	    table = opcodesDD;
	  // } else if ((prefix == 0xfd) && (opcodesFD[tb] != null)) {
	  //   table = opcodesFD;
	  }
	  break;
	}
	final Opcode opcode = table[tb];
	if (opcode == null) {
	  R++;
	  cycleCounter += 4;
	  incPC();
	} else {
	  if ((opcode.getType() & mask) != 0)
	    break;
	  if (log.isLoggable(Level.FINER)) {
	    log.finer(String.format("%s: executing '%s'",
				    name,
				    getDisassembly(PC).getSimplified()));
	    log.finest(String.format("%s: state '%s'", name, CPUState()));
	  }
	  cycleCounter += opcode.exec();
	}
      }
      if ((cycleCounter >= endCycleCounter) ||
	  ((breakpoints != null) && breakpoints.contains(PC))) {
	break;
      }
    }
  }

  // for description see Processor
  @Override
  public void exec() {
    exec(1, 0, null);
  }
}
