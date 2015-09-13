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
   * The Program Counter (PC).
   */
  private int PC;

  /**
   * The Stack Pointer (SP).
   */
  private int SP;

  /**
   * The Interrupt Enable (IE) flag.
   */
  protected boolean IE;

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

    add(new Register("IE") {
	@Override
	public String getValue() {
	  return IE ? "1" : "0";
	}
	@Override
	public void processValue(final String value) {
	  IE = value.equals("1");
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
    resetPending = false;
    interruptPending = -1;
    log.fine("Reset performed");
  }

  // for description see Processor
  @Override
  public void requestInterrupt(final int vector) {
    assert (vector >= 0) && (vector < 8);
    if (IE) {
      interruptPending = vector;
    }
  }

  // for description see Processor
  @Override
  public void interrupt(final int vector) {
    assert (vector >= 0) && (vector < 8);
    if (IE) {
      IE = false;
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
   * Sets the Sign (S) flag.
   */
  protected void SETSF() {
    F |= SF;
  }

  /**
   * Resets the Sign (S) flag.
   */
  protected void RESETSF() {
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
   * Resets the Zero (Z) flag.
   */
  protected void RESETZF() {
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
   * Resets the Half Carry (H) flag.
   */
  protected void RESETHF() {
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
   * Sets the Parity/Overflow (P) flag.
   */
  protected void SETPF() {
    F |= PF;
  }

  /**
   * Resets the Parity/Overflow (P) flag.
   */
  protected void RESETPF() {
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
   * Resets the Add/Subtract (N) flag.
   */
  protected void RESETNF() {
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
   * Resets the Carry (C) flag.
   */
  protected void RESETCF() {
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
      RESETSF();
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
      RESETZF();
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
      RESETHF();
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
      RESETPF();
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
      RESETCF();
    }
  }

  // for description see Processor
  @Override
  public boolean isIE() {
    return IE;
  }

  /**
   * Enables/disables interrupts.
   *
   * @param b if {@code true}, interrupts will be enabled
   */
  public void setIE(final boolean b) {
    IE = b;
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
    new Opcode("NOP", "", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 4;
	}
      }
      ),
	
    // 01
    new Opcode("LD", "BC,%s", 3, Processor.INS_NONE, new Executable() {
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
	  incPC();
	  return 7;
	}
      }
      ),
	
    // 03
    new Opcode("INC", "BC", 1, Processor.INS_NONE, new Executable() {
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
    new Opcode("INC", "B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  B = (B + 1) & 0xff;
	  F4(B);
	  if (B == 0x80) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  if ((B & 0x0f) == 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  RESETNF();
	  incPC();
	  return 4;
	}
      }
      ),
	
    // 05
    new Opcode("DEC", "B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  B = (B - 1) & 0xff;
	  F4(B);
	  if (B == 0x7f) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  if ((B & 0x0f) == 0x0f) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  SETNF();
	  incPC();
	  return 4;
	}
      }
      ),
	    
    // 06
    new Opcode("LD", "B,%s", 2, Processor.INS_NONE, new Executable() {
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
    new Opcode("RLCA", "", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  if ((A & 0x80) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = ((A << 1) | (F & 1)) & 0xff;
	  F2(A);
	  RESETHF();
	  RESETNF();
	  incPC();
	  return 4;
	}
      }		    
      ),
	
    // 08
    new Opcode("EX", "AF,AF'", 1, Processor.INS_UND, new Executable() {
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
    new Opcode("ADD", "HL,BC", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int ti = (H << 8) + L + (B << 8) + C;
	  if (((H ^ B ^ (ti >> 8)) & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  L = ti & 0xff;
	  H = (ti >> 8) & 0xff;
	  F2(H);
	  if (ti > 0xffff) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  RESETNF();
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
	  incPC();
	  return 7;
	}
      }		    
      ),
	
    // 0b
    new Opcode("DEC", "BC", 1, Processor.INS_NONE, new Executable() {
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
    new Opcode("INC", "C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  C = (C + 1) & 0xff;
	  F4(C);
	  if (C == 0x80) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  if ((C & 0x0f) == 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  RESETNF();
	  incPC();
	  return 4;
	}
      }		    
      ),
	
    // 0d
    new Opcode("DEC", "C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  C = (C - 1) & 0xff;
	  F4(C);
	  if (C == 0x7f) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  if ((C & 0x0f) == 0x0f) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  SETNF();
	  incPC();
	  return 4;
	}
      }		    
      ),
	
    // 0e
    new Opcode("LD", "C,%s", 2, Processor.INS_NONE, new Executable() {
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
    new Opcode("RRCA", "", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  if ((A & 1) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = ((A >> 1) | (F << 7)) & 0xff;
	  F2(A);
	  RESETHF();
	  RESETNF();
	  incPC();
	  return 4;
	}
      }		    
      ),
	
    // 10
    new Opcode("DJNZ", "<r>", 1, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  B = (B - 1) & 0xff;
	  if (B == 0) {
	    incPC(2);
	    return 8;
	  } else {
	    incPC();
	    final int tb = memory.getByte(PC);
	    PC = (PC + 1 + tb) & 0xffff;
	    return 13;
	  }
	}
      }		    
      ),
	
    // 11
    new Opcode("LD", "DE,%s", 3, Processor.INS_NONE, new Executable() {
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
	  incPC();
	  return 7;
	}
      }		    
      ),
	
    // 13
    new Opcode("INC", "DE", 1, Processor.INS_NONE, new Executable() {
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
    new Opcode("INC", "D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  D = (D + 1) & 0xff;
	  F4(D);
	  if (D == 0x80) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  if ((D & 0x0f) == 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  RESETNF();
	  incPC();
	  return 4;
	}
      }		    
      ),
	
    // 15
    new Opcode("DEC", "D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  D = (D - 1) & 0xff;
	  F4(D);
	  if (D == 0x7f) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  if ((D & 0x0f) == 0x0f) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  SETNF();
	  incPC();
	  return 4;
	}
      }		    
      ),
	
    // 16
    new Opcode("LD", "D,%s", 2, Processor.INS_NONE, new Executable() {
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
    new Opcode("RLA", "", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tb = A;
	  A = ((A << 1) | (F & 1)) & 0xff;
	  if ((tb & 0x80) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  F2(A);
	  RESETHF();
	  RESETNF();
	  incPC();
	  return 4;
	}
      }		    
      ),
	
    // 18
    new Opcode("JR", "<r>", 1, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int tb = memory.getByte(PC);
	  PC = (PC + 1 + tb) & 0xffff;
	  return 12;
	}
      }		    
      ),
	
    // 19
    new Opcode("ADD", "HL,DE", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int ti = (H << 8) + L + (D << 8) + E;
	  if (((H ^ D ^ (ti >> 8)) & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  L = ti & 0xff;
	  H = (ti >> 8) & 0xff;
	  F2(H);
	  if (ti > 0xffff) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  RESETNF();
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
	  incPC();
	  return 7;
	}
      }		    
      ),
	
    // 1b
    new Opcode("DEC", "DE", 1, Processor.INS_NONE, new Executable() {
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
    new Opcode("INC", "E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  E = (E + 1) & 0xff;
	  F4(E);
	  if (E == 0x80) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  if ((E & 0x0f) == 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  RESETNF();
	  incPC();
	  return 4;
	}
      }		    
      ),
	
    // 1d
    new Opcode("DEC", "E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  E = (E - 1) & 0xff;
	  F4(E);
	  if (E == 0x7f) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  if ((E & 0x0f) == 0x0f) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  SETNF();
	  incPC();
	  return 4;
	}
      }		    
      ),
	
    // 1e
    new Opcode("LD", "E,%s", 2, Processor.INS_NONE, new Executable() {
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
    new Opcode("RRA", "", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tb = A;
	  A = ((A >> 1) | (F << 7)) & 0xff;
	  if ((tb & 1) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  F2(A);
	  RESETHF();
	  RESETNF();	  
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 20
    new Opcode("JR", "NZ,<r>", 1, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  if (ZFSET()) {
	    incPC(2);
	    return 7;
	  } else {
	    incPC();
	    final int tb = memory.getByte(PC);
	    PC = (PC + 1 + tb) & 0xffff;
	    return 12;
	  }
	}
      }		    
      ),
	
    // 21
    new Opcode("LD", "HL,%s", 3, Processor.INS_NONE, new Executable() {
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
    new Opcode("LD", "(%s),HL", 3, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  final int tw = memory.getByte((PC + 1) & 0xffff) +
	    (memory.getByte((PC + 2) & 0xffff) << 8);
	  memory.setByte(tw, L);
	  memory.setByte((tw + 1) & 0xffff, H);
	  incPC(3);
	  return 16;
	}
      }		    
      ),
	
    // 23
    new Opcode("INC", "HL", 1, Processor.INS_NONE, new Executable() {
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
    new Opcode("INC", "H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  H = (H + 1) & 0xff;
	  F4(H);
	  if (H == 0x80) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  if ((H & 0x0f) == 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  RESETNF();
	  incPC();
	  return 4;
	}
      }		    
      ),
	
    // 25
    new Opcode("DEC", "H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  H = (H - 1) & 0xff;
	  F4(H);
	  if (H == 0x7f) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  if ((H & 0x0f) == 0x0f) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  SETNF();
	  incPC();
	  return 4;
	}
      }		    
      ),
	
    // 26
    new Opcode("LD", "H,%s", 2, Processor.INS_NONE, new Executable() {
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
    new Opcode("DAA", "", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int ld = A & 0x0f;
	  if (NFSET()) {
	    final boolean hd = CFSET() || (A > 0x99);
	    if (HFSET() || (ld > 9)) {
	      if (ld > 5) {
		RESETHF();
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
		RESETHF();
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
    new Opcode("JR", "Z,<r>", 1, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  if (ZFSET()) {
	    incPC();
	    final int tb = memory.getByte(PC);
	    PC = (PC + 1 + tb) & 0xffff;
	    return 12;
	  } else {
	    incPC(2);
	    return 7;
	  }
	}
      }		    
      ),
	
    // 29	  
    new Opcode("ADD", "HL,HL", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int ti = (HL() << 1);
	  if (((ti >> 8) & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  L = ti & 0xff;
	  H = (ti >> 8) & 0xff;
	  F2(H);
	  if (ti > 0xffff) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  RESETNF();
	  incPC();
	  return 11;
	}	
      }		    
      ),
	
    // 2a
    new Opcode("LD", "HL,(%s)", 3, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  final int tw = memory.getByte((PC + 1) & 0xffff) +
	    (memory.getByte((PC + 2) & 0xffff) << 8);
	  L = memory.getByte(tw);
	  H = memory.getByte((tw + 1) & 0xffff);
	  incPC(3);
	  return 16;
	}
      }		    
      ), 
	
    // 2b
    new Opcode("DEC", "HL", 1, Processor.INS_NONE, new Executable() {
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
    new Opcode("INC", "L", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  L = (L + 1) & 0xff;
	  F4(L);
	  if (L == 0x80) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  if ((L & 0x0f) == 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  RESETNF();
	  incPC();
	  return 4;
	}
      }		    
      ),
	
    // 2d
    new Opcode("DEC", "L", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  L = (L - 1) & 0xff;
	  F4(L);
	  if (L == 0x7f) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  if ((L & 0x0f) == 0x0f) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  SETNF();
	  incPC();
	  return 4;
	}
      }		    
      ),
	
    // 2e
    new Opcode("LD", "L,%s", 2, Processor.INS_NONE, new Executable() {
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
    new Opcode("CPL", "", 1, Processor.INS_NONE, new Executable() {
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
    new Opcode("JR", "NC,<r>", 1, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  if (CFSET()) {
	    incPC(2);
	    return 7;
	  } else {
	    incPC();
	    final int tb = memory.getByte(PC);
	    PC = (PC + 1 + tb) & 0xffff;
	    return 12;
	  }
	}
      }		    
      ),
	
    // 31
    new Opcode("LD", "SP,%s", 3, Processor.INS_NONE, new Executable() {
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
    new Opcode("LD", "(%s),A", 3, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  final int tw = memory.getByte((PC + 1) & 0xffff) +
	    (memory.getByte((PC + 2) & 0xffff) << 8);
	  memory.setByte(tw, A);
	  incPC(3);
	  return 13;
	}
      }		    
      ), 
	
    // 33
    new Opcode("INC", "SP", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incSP();
	  incPC();
	  return 6;
	}
      }		    
      ),
	
    // 34
    new Opcode("INC", "(HL)", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  final int tw = HL();
	  final int tb = (memory.getByte(tw) + 1) & 0xff;
	  memory.setByte(tw, tb);
	  F4(tb);
	  if (tb == 0x80) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  if ((tb & 0x0f) == 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  RESETNF();
	  incPC();
	  return 11;
	}
      }		    
      ),
	
    // 35
    new Opcode("DEC", "M", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  final int tw = HL();
	  final int tb = (memory.getByte(tw) - 1) & 0xff;
	  memory.setByte(tw, tb);
	  F4(tb);
	  if (tb == 0x7f) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  if ((tb & 0x0f) == 0x0f) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  SETNF();
	  incPC();
	  return 11;
	}
      }		    
      ),
	
    // 36
    new Opcode("LD", "(HL),%s", 2, Processor.INS_MR, new Executable() {
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
    new Opcode("SCF", "", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  SETCF();
	  F2(A);
	  RESETHF();
	  RESETNF();
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 38
    new Opcode("JR", "C,<r>", 1, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  if (CFSET()) {
	    incPC();
	    final int tb = memory.getByte(PC);
	    PC = (PC + 1 + tb) & 0xffff;
	    return 12;
	  } else {
	    incPC(2);
	    return 7;
	  }
	}
      }		    
      ),
	
    // 39
    new Opcode("ADD", "HL,SP", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int ti = (H << 8) + L + SP;
	  if (((H ^ ((SP ^ ti) >> 8)) & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  L = ti & 0xff;
	  H = (ti >> 8) & 0xff;
	  F2(H);
	  if (ti > 0xffff) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  RESETNF();
	  incPC();
	  return 11;
	}	
      }		    
      ), 
	
    // 3a
    new Opcode("LD", "A,(%s)", 3, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  A = memory.getByte((memory.getByte((PC + 1) & 0xffff)) +
			     (memory.getByte((PC + 2) & 0xffff) << 8));
	  incPC(3);
	  return 13;
	}
      }		    
      ),
	
    // 3b
    new Opcode("DEC", "SP", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  decSP();
	  incPC();
	  return 6;
	}
      }		    
      ),
	
    // 3c	  
    new Opcode("INC", "A", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A = (A + 1) & 0xff;
	  F4(A);
	  if (A == 0x80) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  if ((A & 0x0f) == 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  RESETNF();
	  incPC();
	  return 4;
	}
      }		    
      ),
	
    // 3d
    new Opcode("DEC", "A", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A = (A - 1) & 0xff;
	  F4(A);
	  if (A == 0x7f) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  if ((A & 0x0f) == 0x0f) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  SETNF();
	  incPC();
	  return 4;
	}
      }		    
      ),
	
    // 3e
    new Opcode("LD", "A,%s", 2, Processor.INS_NONE, new Executable() {
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
    new Opcode("CCF", "", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  F ^= CF;
	  F2(A);
	  if (CFSET()) {
	    RESETHF();
	  } else {
	    SETHF();
	  }
	  RESETNF();
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 40
    new Opcode("LD", "B,B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 41
    new Opcode("LD", "B,C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  B = C;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 42
    new Opcode("LD", "B,D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  B = D;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 43
    new Opcode("LD", "B,E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  B = E;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 44
    new Opcode("LD", "B,H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  B = H;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 45
    new Opcode("LD", "B,L", 1, Processor.INS_NONE, new Executable() {
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
    new Opcode("LD", "B,A", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  B = A;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 48
    new Opcode("LD", "C,B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  C = B;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 49
    new Opcode("LD", "C,C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 4a
    new Opcode("LD", "C,D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  C = D;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 4b
    new Opcode("LD", "C,E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  C = E;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 4c
    new Opcode("LD", "C,H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  C = H;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 4d
    new Opcode("LD", "C,L", 1, Processor.INS_NONE, new Executable() {
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
    new Opcode("LD", "C,A", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  C = A;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 50
    new Opcode("LD", "D,B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  D = B;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 51
    new Opcode("LD", "D,C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  D = C;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 52
    new Opcode("LD", "D,D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 53
    new Opcode("LD", "D,E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  D = E;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 54
    new Opcode("LD", "D,H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  D = H;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 55
    new Opcode("LD", "D,L", 1, Processor.INS_NONE, new Executable() {
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
    new Opcode("LD", "D,A", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  D = A;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 58
    new Opcode("LD", "E,B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  E = B;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 59
    new Opcode("LD", "E,C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  E = C;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 5a
    new Opcode("LD", "E,D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  E = D;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 5b
    new Opcode("LD", "E,E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 5c
    new Opcode("LD", "E,H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  E = H;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 5d
    new Opcode("LD", "E,L", 1, Processor.INS_NONE, new Executable() {
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
    new Opcode("LD", "E,A", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  E = A;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 60
    new Opcode("LD", "H,B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  H = B;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 61
    new Opcode("LD", "H,C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  H = C;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 62
    new Opcode("LD", "H,D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  H = D;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 63
    new Opcode("LD", "H,E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  H = E;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 64
    new Opcode("LD", "H,H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 65
    new Opcode("LD", "H,L", 1, Processor.INS_NONE, new Executable() {
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
    new Opcode("LD", "H,A", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  H = A;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 68
    new Opcode("LD", "L,B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  L = B;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 69
    new Opcode("LD", "L,C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  L = C;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 6a
    new Opcode("LD", "L,D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  L = D;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 6b
    new Opcode("LD", "L,E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  L = E;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 6c
    new Opcode("LD", "L,H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  L = H;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 6d
    new Opcode("LD", "L,L", 1, Processor.INS_NONE, new Executable() {
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
    new Opcode("LD", "L,A", 1, Processor.INS_NONE, new Executable() {
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
    new Opcode("LD", "A,B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A = B;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 79
    new Opcode("LD", "A,C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A = C;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 7a
    new Opcode("LD", "A,D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A = D;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 7b
    new Opcode("LD", "A,E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A = E;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 7c
    new Opcode("LD", "A,H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A = H;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 7d
    new Opcode("LD", "A,L", 1, Processor.INS_NONE, new Executable() {
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
    new Opcode("LD", "A,A", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 80
    new Opcode("ADD", "A,B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + B;
	  final int cb = A ^ B ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  RESETNF();
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 81
    new Opcode("ADD", "A,C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + C;
	  final int cb = A ^ C ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  RESETNF();
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 82
    new Opcode("ADD", "A,D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + D;
	  final int cb = A ^ D ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  RESETNF();
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 83
    new Opcode("ADD", "A,E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + E;
	  final int cb = A ^ E ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  RESETNF();
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 84
    new Opcode("ADD", "A,H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + H;
	  final int cb = A ^ H ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  RESETNF();
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 85
    new Opcode("ADD", "A,L", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + L;
	  final int cb = A ^ L ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  RESETNF();
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
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  RESETNF();
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // 87
    new Opcode("ADD", "A,A", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = A << 1;
	  if ((tw & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((tw ^ (tw >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  RESETNF();
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 88
    new Opcode("ADC", "A,B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + B + (F & CF);
	  final int cb = A ^ B ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  RESETNF();
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 89
    new Opcode("ADC", "A,C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + C + (F & CF);
	  final int cb = A ^ C ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  RESETNF();
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 8a
    new Opcode("ADC", "A,D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + D + (F & CF);
	  final int cb = A ^ D ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  RESETNF();
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 8b
    new Opcode("ADC", "A,E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + E + (F & CF);
	  final int cb = A ^ E ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  RESETNF();
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 8c
    new Opcode("ADC", "A,H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + H + (F & CF);
	  final int cb = A ^ H ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  RESETNF();
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 8d
    new Opcode("ADC", "A,L", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + L + (F & CF);
	  final int cb = A ^ L ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  RESETNF();
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
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  RESETNF();
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // 8f
    new Opcode("ADC", "A,A", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = (A << 1) + (F & CF);
	  if ((tw & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((tw ^ (tw >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  RESETNF();
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 90
    new Opcode("SUB", "B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - B;
	  final int cb = A ^ B ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
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
    new Opcode("SUB", "C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - C;
	  final int cb = A ^ C ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
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
    new Opcode("SUB", "D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - D;
	  final int cb = A ^ D ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
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
    new Opcode("SUB", "E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - E;
	  final int cb = A ^ E ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
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
    new Opcode("SUB", "H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - H;
	  final int cb = A ^ H ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
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
    new Opcode("SUB", "L", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - L;
	  final int cb = A ^ L ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
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
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
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
    new Opcode("SUB", "A", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  RESETHF();
	  RESETCF();
	  RESETPF();
	  A = 0;
	  F4(0);
	  SETNF();
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 98
    new Opcode("SBC", "A,B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - B - (F & CF);
	  final int cb = A ^ B ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
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
    new Opcode("SBC", "A,C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - C - (F & CF);
	  final int cb = A ^ C ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
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
    new Opcode("SBC", "A,D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - D - (F & CF);
	  final int cb = A ^ D ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
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
    new Opcode("SBC", "A,E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - E - (F & CF);
	  final int cb = A ^ E ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
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
    new Opcode("SBC", "A,H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - H - (F & CF);
	  final int cb = A ^ H ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
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
    new Opcode("SBC", "A,L", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - L - (F & CF);
	  final int cb = A ^ L ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
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
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
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
    new Opcode("SBC", "A,A", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = -(F & CF);
	  if ((tw & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((tw ^ (tw >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
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
    new Opcode("AND", "B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A &= B;
	  SETHF();
	  RESETCF();
	  RESETNF();
	  F5(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // a1
    new Opcode("AND", "C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A &= C;
	  SETHF();
	  RESETCF();
	  RESETNF();
	  F5(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // a2
    new Opcode("AND", "D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A &= D;
	  SETHF();
	  RESETCF();
	  RESETNF();
	  F5(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // a3
    new Opcode("AND", "E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A &= E;
	  SETHF();
	  RESETCF();
	  RESETNF();
	  F5(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // a4
    new Opcode("AND", "H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A &= H;
	  SETHF();
	  RESETCF();
	  RESETNF();
	  F5(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // a5
    new Opcode("AND", "L", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A &= L;
	  SETHF();
	  RESETCF();
	  RESETNF();
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
	  RESETCF();
	  RESETNF();
	  F5(A);
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // a7
    new Opcode("AND", "A", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  SETHF();
	  RESETCF();
	  RESETNF();
	  F5(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // a8
    new Opcode("XOR", "B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A ^= B;
	  RESETHF();
	  RESETCF();
	  RESETNF();
	  F5(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // a9
    new Opcode("XOR", "C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A ^= C;
	  RESETHF();
	  RESETCF();
	  RESETNF();
	  F5(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // aa
    new Opcode("XOR", "D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A ^= D;
	  RESETHF();
	  RESETCF();
	  RESETNF();
	  F5(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // ab
    new Opcode("XOR", "E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A ^= E;
	  RESETHF();
	  RESETCF();
	  RESETNF();
	  F5(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // ac
    new Opcode("XOR", "H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A ^= H;
	  RESETHF();
	  RESETCF();
	  RESETNF();
	  F5(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // ad
    new Opcode("XOR", "L", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A ^= L;
	  RESETHF();
	  RESETCF();
	  RESETNF();
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
	  RESETHF();
	  RESETCF();
	  RESETNF();
	  F5(A);
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // af
    new Opcode("XOR", "A", 1, Processor.INS_NONE, new Executable() {
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
    new Opcode("OR", "B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A |= B;
	  RESETHF();
	  RESETCF();
	  RESETNF();
	  F5(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // b1	  
    new Opcode("OR", "C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A |= C;
	  RESETHF();
	  RESETCF();
	  RESETNF();
	  F5(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // b2	  
    new Opcode("OR", "D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A |= D;
	  RESETHF();
	  RESETCF();
	  RESETNF();
	  F5(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // b3	  
    new Opcode("OR", "E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A |= E;
	  RESETHF();
	  RESETCF();
	  RESETNF();
	  F5(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // b4	  
    new Opcode("OR", "H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A |= H;
	  RESETHF();
	  RESETCF();
	  RESETNF();
	  F5(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // b5	  
    new Opcode("OR", "L", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A |= L;
	  RESETHF();
	  RESETCF();
	  RESETNF();
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
	  RESETHF();
	  RESETCF();
	  RESETNF();
	  F5(A);
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // b7
    new Opcode("OR", "A", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  RESETHF();
	  RESETCF();
	  RESETNF();
	  F5(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // b8
    new Opcode("CP", "B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - B;
	  final int cb = A ^ B ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  F22(tw & 0xff, B);
	  SETNF();
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // b9
    new Opcode("CP", "C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - C;
	  final int cb = A ^ C ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  F22(tw & 0xff, C);
	  SETNF();
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // ba
    new Opcode("CP", "D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - D;
	  final int cb = A ^ D ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  F22(tw & 0xff, D);
	  SETNF();
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // bb
    new Opcode("CP", "E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - E;
	  final int cb = A ^ E ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  F22(tw & 0xff, E);
	  SETNF();
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // bc
    new Opcode("CP", "H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - H;
	  final int cb = A ^ H ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  F22(tw & 0xff, H);
	  SETNF();
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // bd
    new Opcode("CP", "L", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - L;
	  final int cb = A ^ L ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
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
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  F22(tw & 0xff, tb);
	  SETNF();
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // bf	  
    new Opcode("CP", "A", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  RESETHF();
	  RESETCF();
	  RESETPF();
	  F22(0, A);
	  SETNF();
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // c0
    new Opcode("RET", "NZ", 1, Processor.INS_RET, new Executable() {
	@Override
	public int exec() {
	  if (ZFSET()) {
	    incPC();
	    return 5;
	  } else {
	    final int tb = memory.getByte(SP);
	    incSP();
	    PC = tb + (memory.getByte(SP) << 8);
	    incSP();
	    return 11;
	  }
	}
      }		    
      ),
	
    // c1
    new Opcode("POP", "BC", 1, Processor.INS_NONE, new Executable() {
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
    new Opcode("JP", "NZ,%s", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  if (ZFSET()) {
	    incPC(3);
	  } else {
	    incPC();
	    final int tb = memory.getByte(PC);
	    PC = tb + (memory.getByte((PC + 1) & 0xffff) << 8);
	  }
	  return 10;
	}	
      }		    
      ),
	
    // c3
    new Opcode("JMP", "%s", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int tb = memory.getByte(PC);
	  PC = tb + (memory.getByte((PC + 1) & 0xffff) << 8);
	  return 10;
	}	
      }		    
      ),
	
    // c4
    new Opcode("CALL", "NZ,%s", 3, Processor.INS_CALL, new Executable() {
	@Override
	public int exec() {
	  if (ZFSET()) {
	    incPC(3);
	    return 10;
	  } else {
	    incPC();
	    final int tb = memory.getByte(PC);
	    incPC();
	    final int tw = tb + (memory.getByte(PC) << 8);
	    incPC();
	    decSP();
	    memory.setByte(SP, PC >> 8);
	    decSP();
	    memory.setByte(SP, PC & 0xff);
	    PC = tw;
	    return 17;
	  }
	}	
      }		    
      ),
	
    // c5
    new Opcode("PUSH", "BC", 1, Processor.INS_NONE, new Executable() {
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
    new Opcode("ADD", "A,%s", 2, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int tb = memory.getByte(PC);
	  final int tw = A + tb;
	  final int cb = A ^ tb ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  RESETNF();
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // c7
    new Opcode("RST", "<i>", 1, Processor.INS_CALL, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  decSP();
	  memory.setByte(SP, PC >> 8);
	  decSP();
	  memory.setByte(SP, PC & 0xff);
	  PC = 0x0000;
	  return 11;
	}	
      }		    
      ),
	
    // c8
    new Opcode("RET", "Z", 1, Processor.INS_RET, new Executable() {
	@Override
	public int exec() {
	  if (ZFSET()) {
	    final int tb = memory.getByte(SP);
	    incSP();
	    PC = tb + (memory.getByte(SP) << 8);
	    incSP();
	    return 11;
	  } else {
	    incPC();
	    return 5;
	  }
	}	
      }		    
      ), 
	
    // c9
    new Opcode("RET", "", 1, Processor.INS_RET, new Executable() {
	@Override
	public int exec() {
	  final int tb = memory.getByte(SP);
	  incSP();
	  PC = tb + (memory.getByte(SP) << 8);
	  incSP();
	  return 10;
	}	
      }		    
      ),
	
    // ca	  
    new Opcode("JP", "NZ,%s", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  if (ZFSET()) {
	    incPC();
	    final int tb = memory.getByte(PC);
	    PC = tb + (memory.getByte((PC + 1) & 0xffff) << 8);
	  } else {
	    incPC(3);
	  }
	  return 10;
	}	
      }		    
      ),   
	
    // cb
    null,
	
    // cc
    new Opcode("CALL", "Z,%s", 3, Processor.INS_CALL, new Executable() {
	@Override
	public int exec() {
	  if (ZFSET()) {
	    incPC();
	    final int tb = memory.getByte(PC);
	    incPC();
	    final int tw = tb + (memory.getByte(PC) << 8);
	    incPC();
	    decSP();
	    memory.setByte(SP, PC >> 8);
	    decSP();
	    memory.setByte(SP, PC & 0xff);
	    PC = tw;
	    return 17;
	  } else {
	    incPC(3);
	    return 10;
	  }
	}	
      }		    
      ),  
	
    // cd
    new Opcode("CALL", "%s", 3, Processor.INS_CALL, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int tb = memory.getByte(PC);
	  incPC();
	  final int tw = tb + (memory.getByte(PC) << 8);
	  incPC();
	  decSP();
	  memory.setByte(SP, PC >> 8);
	  decSP();
	  memory.setByte(SP, PC & 0xff);
	  PC = tw;
	  return 17;
	}	
      }		    
      ),
	
    // ce	  
    new Opcode("ADC", "A,%s", 2, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int tb = memory.getByte(PC);
	  final int tw = A + tb + (F & CF);
	  final int cb = A ^ tb ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  A = tw & 0xff;
	  F4(A);
	  RESETNF();
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // cf
    new Opcode("RST", "<i>", 1, Processor.INS_CALL, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  decSP();
	  memory.setByte(SP, PC >> 8);
	  decSP();
	  memory.setByte(SP, PC & 0xff);
	  PC = 0x0008;
	  return 11;
	}	
      }		    
      ),
	
    // d0
    new Opcode("RET", "NC", 1, Processor.INS_RET, new Executable() {
	@Override
	public int exec() {
	  if (CFSET()) {
	    incPC();
	    return 5;
	  } else {
	    final int tb = memory.getByte(SP);
	    incSP();
	    PC = tb + (memory.getByte(SP) << 8);
	    incSP();
	    return 11;
	  }
	}	
      }		    
      ),
	
    // d1
    new Opcode("POP", "DE", 1, Processor.INS_NONE, new Executable() {
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
    new Opcode("JP", "NC,%s", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  if (CFSET()) {
	    incPC(3);
	  } else {
	    incPC();
	    final int tb = memory.getByte(PC);
	    PC = tb + (memory.getByte((PC + 1) & 0xffff) << 8);
	  }
	  return 10;
	}	
      }		    
      ),
	
    // d3
    new Opcode("OUT", "(%s),A", 2, Processor.INS_IO, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  int port = memory.getByte(PC);
	  for (IOElement t: outputPorts.get(port)) {
	    t.portOutput(port, A);
	  }
	  incPC();
	  return 11;
	}	
      }
      ),
	
    // d4
    new Opcode("CALL", "NC,%s", 3, Processor.INS_CALL, new Executable() {
	@Override
	public int exec() {
	  if (CFSET()) {
	    incPC(3);
	    return 10;
	  } else {
	    incPC();
	    final int tb = memory.getByte(PC);
	    incPC();
	    final int tw = tb + (memory.getByte(PC) << 8);
	    incPC();
	    decSP();
	    memory.setByte(SP, PC >> 8);
	    decSP();
	    memory.setByte(SP, PC & 0xff);
	    PC = tw;
	    return 17;
	  }
	}	
      }		    
      ),
	
    // d5
    new Opcode("PUSH", "DE", 1, Processor.INS_NONE, new Executable() {
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
    new Opcode("SUB", "%s", 2, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int tb = memory.getByte(PC);
	  final int tw = A - tb;
	  final int cb = A ^ tb ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
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
    new Opcode("RST", "<i>", 1, Processor.INS_CALL, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  decSP();
	  memory.setByte(SP, PC >> 8);
	  decSP();
	  memory.setByte(SP, PC & 0xff);
	  PC = 0x0010;
	  return 11;
	}	
      }		    
      ),
	
    // d8
    new Opcode("RET", "C", 1, Processor.INS_RET, new Executable() {
	@Override
	public int exec() {
	  if (CFSET()) {
	    final int tb = memory.getByte(SP);
	    incSP();
	    PC = tb + (memory.getByte(SP) << 8);
	    incSP();
	    return 11;
	  } else {
	    incPC();
	    return 5;
	  }
	}	
      }		    
      ),
	
    // d9
    new Opcode("EXX", "", 1, Processor.INS_UND | Processor.INS_RET,
	       new Executable() {
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
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // da
    new Opcode("JP", "C,%s", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  if (CFSET()) {
	    incPC();
	    final int tb = memory.getByte(PC);
	    PC = tb + (memory.getByte((PC + 1) & 0xffff) << 8);
	  } else {
	    incPC(3);
	  }
	  return 10;
	}	
      }		    
      ),
	
    // db
    new Opcode("IN", "A,(%s)", 2, Processor.INS_IO, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  int port = memory.getByte(PC);
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
    new Opcode("CALL", "C,%s", 3, Processor.INS_CALL, new Executable() {
	@Override
	public int exec() {
	  if (CFSET()) {
	    incPC();
	    final int tb = memory.getByte(PC);
	    incPC();
	    final int tw = tb + (memory.getByte(PC) << 8);
	    incPC();
	    decSP();
	    memory.setByte(SP, PC >> 8);
	    decSP();
	    memory.setByte(SP, PC & 0xff);
	    PC = tw;
	    return 17;
	  } else {
	    incPC(3);
	    return 10;
	  }
	}	
      }		    
      ),
	
    // dd
    null,
	
    // de	  
    new Opcode("SBC", "A,%s", 2, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int tb = memory.getByte(PC);
	  final int tw = A - tb - (F & CF);
	  final int cb = A ^ tb ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
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
    new Opcode("RST", "<i>", 1, Processor.INS_CALL, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  decSP();
	  memory.setByte(SP, PC >> 8);
	  decSP();
	  memory.setByte(SP, PC & 0xff);
	  PC = 0x0018;
	  return 11;
	}	
      }		    
      ),
	
    // e0
    new Opcode("RET", "PO", 1, Processor.INS_RET, new Executable() {
	@Override
	public int exec() {
	  if (PE()) {
	    incPC();
	    return 5;
	  } else {
	    final int tb = memory.getByte(SP);
	    incSP();
	    PC = tb + (memory.getByte(SP) << 8);
	    incSP();
	    return 11;
	  }
	}	
      }		    
      ),
	
    // e1
    new Opcode("POP", "HL", 1, Processor.INS_NONE, new Executable() {
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
    new Opcode("JP", "PO,%s", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  if (PE()) {
	    incPC(3);
	  } else {
	    incPC();
	    final int tb = memory.getByte(PC);
	    PC = tb + (memory.getByte((PC + 1) & 0xffff) << 8);
	  }
	  return 10;
	}	
      }		    
      ),
	
    // e3
    new Opcode("EX", "(SP),HL", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(SP);
	  memory.setByte(SP, L);
	  L = tb;
	  final int tw = (SP + 1) & 0xffff;
	  tb = memory.getByte(tw);
	  memory.setByte(tw, H);
	  H = tb;
	  incPC();
	  return 19;
	}	
      }		    
      ),
	
    // e4
    new Opcode("CALL", "PO,%s", 3, Processor.INS_CALL, new Executable() {
	@Override
	public int exec() {
	  if (PE()) {
	    incPC(3);
	    return 10;
	  } else {
	    incPC();
	    final int tb = memory.getByte(PC);
	    incPC();
	    final int tw = tb + (memory.getByte(PC) << 8);
	    incPC();
	    decSP();
	    memory.setByte(SP, PC >> 8);
	    decSP();
	    memory.setByte(SP, PC & 0xff);
	    PC = tw;
	    return 17;
	  }
	}	
      }		    
      ),
	
    // e5
    new Opcode("PUSH", "HL", 1, Processor.INS_NONE, new Executable() {
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
    new Opcode("AND", "%s", 2, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int tb = memory.getByte(PC);
	  A &= tb;
	  SETHF();
	  RESETCF();
	  RESETNF();
	  F5(A);
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // e7
    new Opcode("RST", "<i>", 1, Processor.INS_CALL, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  decSP();
	  memory.setByte(SP, PC >> 8);
	  decSP();
	  memory.setByte(SP, PC & 0xff);
	  PC = 0x0020;
	  return 11;
	}	
      }		    
      ),
	
    // e8
    new Opcode("RET", "PE", 1, Processor.INS_RET, new Executable() {
	@Override
	public int exec() {
	  if (PE()) {
	    final int tb = memory.getByte(SP);
	    incSP();
	    PC = tb + (memory.getByte(SP) << 8);
	    incSP();
	    return 11;
	  } else {
	    incPC();
	    return 5;
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
    new Opcode("JP", "PE,%s", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  if (PE()) {
	    incPC();
	    final int tb = memory.getByte(PC);
	    PC = tb + (memory.getByte((PC + 1) & 0xffff) << 8);
	  } else {
	    incPC(3);
	  }
	  return 10;
	}	
      }		    
      ),
	
    // eb
    new Opcode("EX", "DE,HL", 1, Processor.INS_NONE, new Executable() {
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
    new Opcode("CALL", "PE,%s", 3, Processor.INS_CALL, new Executable() {
	@Override
	public int exec() {
	  if (PE()) {
	    incPC();
	    final int tb = memory.getByte(PC);
	    incPC();
	    final int tw = tb + (memory.getByte(PC) << 8);
	    incPC();
	    decSP();
	    memory.setByte(SP, PC >> 8);
	    decSP();
	    memory.setByte(SP, PC & 0xff);
	    PC = tw;
	    return 17;
	  } else {
	    incPC(3);
	    return 10;
	  }
	}	
      }		    
      ),
	
    // ed
    null,
	
    // ee
    new Opcode("XOR", "%s", 2, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  A ^= memory.getByte(PC);
	  RESETHF();
	  RESETCF();
	  RESETNF();
	  F5(A);
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // ef
    new Opcode("RST", "<i>", 1, Processor.INS_CALL, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  decSP();
	  memory.setByte(SP, PC >> 8);
	  decSP();
	  memory.setByte(SP, PC & 0xff);
	  PC = 0x0028;
	  return 11;
	}	
      }		    
      ),
	
    // f0
    new Opcode("RET", "P", 1, Processor.INS_RET, new Executable() {
	@Override
	public int exec() {
	  if (SFSET()) {
	    incPC();
	    return 5;
	  } else {
	    final int tb = memory.getByte(SP);
	    incSP();
	    PC = tb + (memory.getByte(SP) << 8);
	    incSP();
	    return 11;
	  }
	}	
      }		    
      ),
	
    // f1
    new Opcode("POP", "AF", 1, Processor.INS_NONE, new Executable() {
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
    new Opcode("JP", "%s", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  if (SFSET()) {
	    incPC(3);
	  } else {
	    incPC();
	    final int tb = memory.getByte(PC);
	    PC = tb + (memory.getByte((PC + 1) & 0xffff) << 8);
	  }
	  return 10;
	}	
      }		    
      ),
	
    // f3
    new Opcode("DI", "", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  IE = false;
	  incPC();
	  return 4;
	}	
      }		    
      ), 
	
    // f4
    new Opcode("CALL", "P,%s", 3, Processor.INS_CALL, new Executable() {
	@Override
	public int exec() {
	  if (SFSET()) {
	    incPC(3);
	    return 10;
	  } else {
	    incPC();
	    final int tb = memory.getByte(PC);
	    incPC();
	    final int tw = tb + (memory.getByte(PC) << 8);
	    incPC();
	    decSP();
	    memory.setByte(SP, PC >> 8);
	    decSP();
	    memory.setByte(SP, PC & 0xff);
	    PC = tw;
	    return 17;
	  }
	}	
      }		    
      ),
	
    // f5
    new Opcode("PUSH", "AF", 1, Processor.INS_NONE, new Executable() {
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
    new Opcode("OR", "%s", 2, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  A |= memory.getByte(PC);
	  RESETHF();
	  RESETCF();
	  RESETNF();
	  F5(A);
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // f7
    new Opcode("RST", "<i>", 1, Processor.INS_CALL, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  decSP();
	  memory.setByte(SP, PC >> 8);
	  decSP();
	  memory.setByte(SP, PC & 0xff);
	  PC = 0x0030;
	  return 11;
	}	
      }		    
      ),
	
    // f8
    new Opcode("RET", "M", 1, Processor.INS_RET, new Executable() {
	@Override
	public int exec() {
	  if (SFSET()) {
	    final int tb = memory.getByte(SP);
	    incSP();
	    PC = tb + (memory.getByte(SP) << 8);
	    incSP();
	    return 11;
	  } else {
	    incPC();
	    return 5;
	  }
	}	
      }		    
      ),
	
    // f9
    new Opcode("LD", "SP,HL", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  SP = HL();
	  incPC();
	  return 6;
	}	
      }		    
      ),
	
    // fa
    new Opcode("JP", "M,%s", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  if (SFSET()) {
	    incPC();
	    final int tb = memory.getByte(PC);
	    PC = tb + (memory.getByte((PC + 1) & 0xffff) << 8);
	  } else {
	    incPC(3);
	  }
	  return 10;
	}	
      }		    
      ),
	
    // fb
    new Opcode("EI", "", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  IE = true;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // fc
    new Opcode("CALL", "M,%s", 3, Processor.INS_CALL, new Executable() {
	@Override
	public int exec() {
	  if (SFSET()) {
	    incPC();
	    final int tb = memory.getByte(PC);
	    incPC();
	    final int tw = tb + (memory.getByte(PC) << 8);
	    incPC();
	    decSP();
	    memory.setByte(SP, PC >> 8);
	    decSP();
	    memory.setByte(SP, PC & 0xff);
	    PC = tw;
	    return 17;
	  } else {
	    incPC(3);
	    return 10;
	  }
	}	
      }		    
      ),
	
    // fd
    null,
	
    // fe
    new Opcode("CP", "%s", 2, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int tb = memory.getByte(PC);
	  final int tw = A - tb;
	  final int cb = A ^ tb ^ tw;
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  F22(tw & 0xff, tb);
	  SETNF();
	  incPC();
	  return 7;
	}	
      }		    
      ), 
	
    // ff
    new Opcode("RST", "<i>", 1, Processor.INS_CALL, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  decSP();
	  memory.setByte(SP, PC >> 8);
	  decSP();
	  memory.setByte(SP, PC & 0xff);
	  PC = 0x0038;
	  return 11;
	}
      }	
      )
  };

  /**
   * The array of opcodes with prefix ED.
   */
  protected final Opcode[] opcodesED = new Opcode[] {

    // 00 - 3f
    null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null,

    // 40
    new Opcode("IN", "B,(C)", 2, Processor.INS_IO, new Executable() {
	@Override
	public int exec() {
	  B = 0xff;
	  for (IOElement t: inputPorts.get(C)) {
	    B &= t.portInput(C);
	  }
	  F5(B);
	  RESETHF();
	  RESETNF();
	  incPC();
	  return 12;
	}	
      }
      ),

    // 41
    new Opcode("OUT", "(C),B", 2, Processor.INS_IO, new Executable() {
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

    // 42
    new Opcode("SBC", "HL,BC", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = (H << 8) + L - (B << 8) - C - (F & CF);
	  final int cb = H ^ B ^ (tw >> 8);
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((cb & 0x100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  L = tw & 0xff;
	  H = (tw >> 8) & 0xff;
	  F4(H);
	  if ((H | L) == 0) {
	    SETZF();
	  } else {
	    RESETZF();
	  }
	  SETNF();
	  incPC();
	  return 15;
	}	
      }		    
      ),

    // 43
    null,

    // 44
    null,

    // 45
    null,

    // 46
    null,

    // 47
    null,

    // 48
    new Opcode("IN", "C,(C)", 2, Processor.INS_IO, new Executable() {
	@Override
	public int exec() {
	  final int tb = C;
	  C = 0xff;
	  for (IOElement t: inputPorts.get(tb)) {
	    C &= t.portInput(tb);
	  }
	  F5(C);
	  RESETHF();
	  RESETNF();
	  incPC();
	  return 12;
	}	
      }
      ),

    // 49
    new Opcode("OUT", "(C),C", 2, Processor.INS_IO, new Executable() {
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

    // 4a
    new Opcode("ADC", "HL,BC", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = (H << 8) + L + (B << 8) + C + (F & CF);
	  final int cb = H ^ B ^ (tw >> 8);
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((cb & 0x100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  L = tw & 0xff;
	  H = (tw >> 8) & 0xff;
	  F4(H);
	  if ((H | L) == 0) {
	    SETZF();
	  } else {
	    RESETZF();
	  }
	  RESETNF();
	  incPC();
	  return 15;
	}	
      }		    
      ),

    // 4b
    null,

    // 4c
    null,

    // 4d
    null,

    // 4e
    null,

    // 4f
    null,

    // 50
    new Opcode("IN", "D,(C)", 2, Processor.INS_IO, new Executable() {
	@Override
	public int exec() {
	  D = 0xff;
	  for (IOElement t: inputPorts.get(C)) {
	    D &= t.portInput(C);
	  }
	  F5(D);
	  RESETHF();
	  RESETNF();
	  incPC();
	  return 12;
	}	
      }
      ),

    // 51
    new Opcode("OUT", "(C),D", 2, Processor.INS_IO, new Executable() {
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

    // 52
    new Opcode("SBC", "HL,DE", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = (H << 8) + L - (D << 8) - E - (F & CF);
	  final int cb = H ^ D ^ (tw >> 8);
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((cb & 0x100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  L = tw & 0xff;
	  H = (tw >> 8) & 0xff;
	  F4(H);
	  if ((H | L) == 0) {
	    SETZF();
	  } else {
	    RESETZF();
	  }
	  SETNF();
	  incPC();
	  return 15;
	}	
      }		    
      ),

    // 53
    null,

    // 54
    null,

    // 55
    null,

    // 56
    null,

    // 57
    null,

    // 58
    new Opcode("IN", "E,(C)", 2, Processor.INS_IO, new Executable() {
	@Override
	public int exec() {
	  E = 0xff;
	  for (IOElement t: inputPorts.get(C)) {
	    E &= t.portInput(C);
	  }
	  F5(E);
	  RESETHF();
	  RESETNF();
	  incPC();
	  return 12;
	}	
      }
      ),

    // 59
    new Opcode("OUT", "(C),E", 2, Processor.INS_IO, new Executable() {
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

    // 5a
    new Opcode("ADC", "HL,DE", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = (H << 8) + L + (D << 8) + E + (F & CF);
	  final int cb = H ^ D ^ (tw >> 8);
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((cb & 0x100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  L = tw & 0xff;
	  H = (tw >> 8) & 0xff;
	  F4(H);
	  if ((H | L) == 0) {
	    SETZF();
	  } else {
	    RESETZF();
	  }
	  RESETNF();
	  incPC();
	  return 15;
	}	
      }		    
      ),

    // 5b
    null,

    // 5c
    null,

    // 5d
    null,

    // 5e
    null,

    // 5f
    null,

    // 60
    new Opcode("IN", "H,(C)", 2, Processor.INS_IO, new Executable() {
	@Override
	public int exec() {
	  H = 0xff;
	  for (IOElement t: inputPorts.get(C)) {
	    H &= t.portInput(C);
	  }
	  F5(H);
	  RESETHF();
	  RESETNF();
	  incPC();
	  return 12;
	}	
      }
      ),

    // 61
    new Opcode("OUT", "(C),H", 2, Processor.INS_IO, new Executable() {
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

    // 62
    new Opcode("SBC", "HL,HL", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = -(F & CF);
	  if ((tw & 0x1000) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x10000) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((tw ^ (tw >> 1)) & 0x8000) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  L = tw & 0xff;
	  H = (tw >> 8) & 0xff;
	  F4(H);
	  if ((H | L) == 0) {
	    SETZF();
	  } else {
	    RESETZF();
	  }
	  SETNF();
	  incPC();
	  return 15;
	}	
      }		    
      ),

    // 63
    null,

    // 64
    null,

    // 65
    null,

    // 66
    null,

    // 67
    null,

    // 68
    new Opcode("IN", "L,(C)", 2, Processor.INS_IO, new Executable() {
	@Override
	public int exec() {
	  L = 0xff;
	  for (IOElement t: inputPorts.get(C)) {
	    L &= t.portInput(C);
	  }
	  F5(L);
	  RESETHF();
	  RESETNF();
	  incPC();
	  return 12;
	}	
      }
      ),

    // 69
    new Opcode("OUT", "(C),L", 2, Processor.INS_IO, new Executable() {
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

    // 6a
    new Opcode("ADC", "HL,HL", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = (H << 9) + (L << 1) + (F & CF);
	  if ((tw & 0x1000) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((tw & 0x10000) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((tw ^ (tw >> 1)) & 0x8000) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  L = tw & 0xff;
	  H = (tw >> 8) & 0xff;
	  F4(H);
	  if ((H | L) == 0) {
	    SETZF();
	  } else {
	    RESETZF();
	  }
	  RESETNF();
	  incPC();
	  return 15;
	}	
      }		    
      ),

    // 6b
    null,

    // 6c
    null,

    // 6d
    null,

    // 6e
    null,

    // 6f
    null,

    // 70
    new Opcode("IN", "(C)", 2, Processor.INS_IO, new Executable() {
	@Override
	public int exec() {
	  int tb = 0xff;
	  for (IOElement t: inputPorts.get(C)) {
	    tb &= t.portInput(C);
	  }
	  F5(tb);
	  RESETHF();
	  RESETNF();
	  incPC();
	  return 12;
	}	
      }
      ),

    // 71
    new Opcode("OUT", "(C),0", 2, Processor.INS_IO, new Executable() {
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

    // 72
    new Opcode("SBC", "HL,SP", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = (H << 8) + L - SP - (F & CF);
	  final int cb = H ^ (SP >> 8) ^ (tw >> 8);
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((cb & 0x100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  L = tw & 0xff;
	  H = (tw >> 8) & 0xff;
	  F4(H);
	  if ((H | L) == 0) {
	    SETZF();
	  } else {
	    RESETZF();
	  }
	  SETNF();
	  incPC();
	  return 15;
	}	
      }		    
      ),

    // 73
    null,

    // 74
    null,

    // 75
    null,

    // 76
    null,

    // 77
    null,

    // 78
    new Opcode("IN", "A,(C)", 2, Processor.INS_IO, new Executable() {
	@Override
	public int exec() {
	  A = 0xff;
	  for (IOElement t: inputPorts.get(C)) {
	    A &= t.portInput(C);
	  }
	  F5(A);
	  RESETHF();
	  RESETNF();
	  incPC();
	  return 12;
	}	
      }
      ),

    // 79
    new Opcode("OUT", "(C),A", 2, Processor.INS_IO, new Executable() {
	@Override
	public int exec() {
	  for (IOElement t: outputPorts.get(C)) {
	    t.portOutput(C, A);
	  }
	  incPC();
	  return 12;
	}	
      }
      ),

    // 7a
    new Opcode("ADC", "HL,SP", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  final int tw = (H << 8) + L + SP + (F & CF);
	  final int cb = H ^ (SP >> 8) ^ (tw >> 8);
	  if ((cb & 0x10) != 0) {
	    SETHF();
	  } else {
	    RESETHF();
	  }
	  if ((cb & 0x100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  if (((cb ^ (cb >> 1)) & 0x80) != 0) {
	    SETPF();
	  } else {
	    RESETPF();
	  }
	  L = tw & 0xff;
	  H = (tw >> 8) & 0xff;
	  F4(H);
	  if ((H | L) == 0) {
	    SETZF();
	  } else {
	    RESETZF();
	  }
	  RESETNF();
	  incPC();
	  return 15;
	}	
      }
      ),

    // 7b
    null,

    // 7c
    null,

    // 7d
    null,

    // 7e
    null,

    // 7f
    null,

    // 80
    null,

    // 81
    null,

    // 82
    null,

    // 83
    null,

    // 84
    null,

    // 85
    null,

    // 86
    null,

    // 87
    null,

    // 88
    null,

    // 89
    null,

    // 8a
    null,

    // 8b
    null,

    // 8c
    null,

    // 8d
    null,

    // 8e
    null,

    // 8f
    null,

    // 90
    null,

    // 91
    null,

    // 92
    null,

    // 93
    null,

    // 94
    null,

    // 95
    null,

    // 96
    null,

    // 97
    null,

    // 98
    null,

    // 99
    null,

    // 9a
    null,

    // 9b
    null,

    // 9c
    null,

    // 9d
    null,

    // 9e
    null,

    // 9f
    null,

    // a0
    null,

    // a1
    null,

    // a2
    null,

    // a3
    null,

    // a4
    null,

    // a5
    null,

    // a6
    null,

    // a7
    null,

    // a8
    null,

    // a9
    null,

    // aa
    null,

    // ab
    null,

    // ac
    null,

    // ad
    null,

    // ae
    null,

    // af
    null,

    // b0
    null,

    // b1
    null,

    // b2
    null,

    // b3
    null,

    // b4
    null,

    // b5
    null,

    // b6
    null,

    // b7
    null,

    // b8
    null,

    // b9
    null,

    // ba
    null,

    // bb
    null,

    // bc
    null,

    // bd
    null,

    // be
    null,

    // bf
    null,

    // c0
    null,

    // c1
    null,

    // c2
    null,

    // c3
    null,

    // c4
    null,

    // c5
    null,

    // c6
    null,

    // c7
    null,

    // c8
    null,

    // c9
    null,

    // ca
    null,

    // cb
    null,

    // cc
    null,

    // cd
    null,

    // ce
    null,

    // cf
    null,

    // d0
    null,

    // d1
    null,

    // d2
    null,

    // d3
    null,

    // d4
    null,

    // d5
    null,

    // d6
    null,

    // d7
    null,

    // d8
    null,

    // d9
    null,

    // da
    null,

    // db
    null,

    // dc
    null,

    // dd
    null,

    // de
    null,

    // df
    null,

    // e0
    null,

    // e1
    null,

    // e2
    null,

    // e3
    null,

    // e4
    null,

    // e5
    null,

    // e6
    null,

    // e7
    null,

    // e8
    null,

    // e9
    null,

    // ea
    null,

    // eb
    null,

    // ec
    null,

    // ed
    null,

    // ee
    null,

    // ef
    null,

    // f0
    null,

    // f1
    null,

    // f2
    null,

    // f3
    null,

    // f4
    null,

    // f5
    null,

    // f6
    null,

    // f7
    null,

    // f8
    null,

    // f9
    null,

    // fa
    null,

    // fb
    null,

    // fc
    null,

    // fd
    null,

    // fe
    null,

    // ff
    null
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
      "CF:%d HF:%d ZF:%d SF:%d PF:%d IE:%d F:%02x",
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
      (IE ? 1 : 0), F);
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
	final int tb = memory.getByte(PC);
	Opcode opcode = opcodes[tb];
	if (opcode == null) {
	  incPC();
	  opcode = opcodesED[memory.getByte(PC)];
	}
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
