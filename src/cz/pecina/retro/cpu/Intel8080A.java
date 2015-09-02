/* Intel8080A.java
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

/**
 * Intel 8080A CPU.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Intel8080A extends Device implements Processor, SystemClockSource {
  // dynamic logger, per device
  private Logger log;

  // flags
  private static final int SF = 0x80;
  private static final int ZF = 0x40;
  private static final int ACF = 0x10;
  private static final int PF = 0x04;
  private static final int CF = 0x01;

  // F manipulation constants
  private static final int FFIX = 0x02;
  private static final int FMASK = 0xd5;

  // memory
  private AbstractMemory memory;

  // ports
  private final List<List<IOElement>> inputPorts = new ArrayList<>();
  private final List<List<IOElement>> outputPorts = new ArrayList<>();

  // CPU registers
  private int A, F = FFIX, B, C, D, E, H, L;
  private int PC, SP;

  // interrupt enable
  private boolean IE;

  // CPU cycle counter
  private long cycleCounter;

  // CPU-clock driven scheduler
  private final CPUScheduler scheduler = new CPUScheduler();

  // aux flags
  private boolean resetPending;
  private int interruptPending = -1;

  // Table combining S, Z and P flags
  private static final int[] tftbl = {
    0x44, 0x00, 0x00, 0x04, 0x00, 0x04, 0x04, 0x00,
    0x00, 0x04, 0x04, 0x00, 0x04, 0x00, 0x00, 0x04,
    0x00, 0x04, 0x04, 0x00, 0x04, 0x00, 0x00, 0x04,
    0x04, 0x00, 0x00, 0x04, 0x00, 0x04, 0x04, 0x00,
    0x00, 0x04, 0x04, 0x00, 0x04, 0x00, 0x00, 0x04,
    0x04, 0x00, 0x00, 0x04, 0x00, 0x04, 0x04, 0x00,
    0x04, 0x00, 0x00, 0x04, 0x00, 0x04, 0x04, 0x00,
    0x00, 0x04, 0x04, 0x00, 0x04, 0x00, 0x00, 0x04,
    0x00, 0x04, 0x04, 0x00, 0x04, 0x00, 0x00, 0x04,
    0x04, 0x00, 0x00, 0x04, 0x00, 0x04, 0x04, 0x00,
    0x04, 0x00, 0x00, 0x04, 0x00, 0x04, 0x04, 0x00,
    0x00, 0x04, 0x04, 0x00, 0x04, 0x00, 0x00, 0x04,
    0x04, 0x00, 0x00, 0x04, 0x00, 0x04, 0x04, 0x00,
    0x00, 0x04, 0x04, 0x00, 0x04, 0x00, 0x00, 0x04,
    0x00, 0x04, 0x04, 0x00, 0x04, 0x00, 0x00, 0x04,
    0x04, 0x00, 0x00, 0x04, 0x00, 0x04, 0x04, 0x00,
    0x80, 0x84, 0x84, 0x80, 0x84, 0x80, 0x80, 0x84,
    0x84, 0x80, 0x80, 0x84, 0x80, 0x84, 0x84, 0x80,
    0x84, 0x80, 0x80, 0x84, 0x80, 0x84, 0x84, 0x80,
    0x80, 0x84, 0x84, 0x80, 0x84, 0x80, 0x80, 0x84,
    0x84, 0x80, 0x80, 0x84, 0x80, 0x84, 0x84, 0x80,
    0x80, 0x84, 0x84, 0x80, 0x84, 0x80, 0x80, 0x84,
    0x80, 0x84, 0x84, 0x80, 0x84, 0x80, 0x80, 0x84,
    0x84, 0x80, 0x80, 0x84, 0x80, 0x84, 0x84, 0x80,
    0x84, 0x80, 0x80, 0x84, 0x80, 0x84, 0x84, 0x80,
    0x80, 0x84, 0x84, 0x80, 0x84, 0x80, 0x80, 0x84,
    0x80, 0x84, 0x84, 0x80, 0x84, 0x80, 0x80, 0x84,
    0x84, 0x80, 0x80, 0x84, 0x80, 0x84, 0x84, 0x80,
    0x80, 0x84, 0x84, 0x80, 0x84, 0x80, 0x80, 0x84,
    0x84, 0x80, 0x80, 0x84, 0x80, 0x84, 0x84, 0x80,
    0x84, 0x80, 0x80, 0x84, 0x80, 0x84, 0x84, 0x80,
    0x80, 0x84, 0x84, 0x80, 0x84, 0x80, 0x80, 0x84
  };

  /**
   * Main constructor.  Memory must be attached using {@link #setMemory}
   * for the CPU to function.
   *
   * @param name davice name
   */
  public Intel8080A(final String name) {
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
	  F = fixF(Integer.parseInt(value, 16));
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
      inputPorts.add(new ArrayList<IOElement>());
      outputPorts.add(new ArrayList<IOElement>());
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

  // for description see Processor
  @Override
  public CPUScheduler getCPUScheduler() {
    return scheduler;
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

  // fixes the flags register
  private int fixF(final int n) {
    assert (n >= 0) && (n < 0x100);
    return (n & FMASK) | FFIX;
  }

  // fixed the flags register
  private int fixF(final byte n) {
    return fixF(n & 0xff);
  }

  // gets BC
  private int BC() {
    return (B << 8) + C;
  }

  // gets DE
  private int DE() {
    return (D << 8) + E;
  }

  // gets HL
  private int HL() {
    return (H << 8) + L;
  }

  // adds the three-flag composite value
  private void TF(final int v) {
    assert (v >= 0) && (v < 0x100);
    F = (F & 0x3b) | tftbl[v];
  }

  // sets the sign (S) flag
  private void SETSF() {
    F |= SF;
  }

  // resets the sign (S) flag
  private void RESETSF() {
    F &= ~SF;
  }

  // gets the sign (S) flag
  private boolean SFSET() {
    return (F & SF) != 0;
  }

  // sets the zero (Z) flag
  private void SETZF() {
    F |= ZF;
  }

  // resets the zero (Z) flag
  private void RESETZF() {
    F &= ~ZF;
  }

  // gets the zero (Z) flag
  private boolean ZFSET() {
    return (F & ZF) != 0;
  }

  // sets the auxiliary carry (AC) flag
  private void SETACF() {
    F |= ACF;
  }

  // resets the auxiliary carry (AC) flag
  private void RESETACF() {
    F &= ~ACF;
  }

  // gets the auxiliary carry (AC) flag
  private boolean ACFSET() {
    return (F & ACF) != 0;
  }

  // sets the parity (P) flag
  private void SETPF() {
    F |= PF;
  }

  // resets the parity (P) flag
  private void RESETPF() {
    F &= ~PF;
  }

  // gets the parity (P) flag
  private boolean PFSET() {
    return (F & PF) != 0;
  }

  // true of parity even
  private boolean PE() {
    return (F & PF) != 0;
  }

  // true of parity odd
  private boolean PO() {
    return (F & PF) == 0;
  }

  // sets the carry (C) flag
  private void SETCF() {
    F |= CF;
  }

  // resets the carry (C) flag
  private void RESETCF() {
    F &= ~CF;
  }

  // gets the carry (C) flag
  private boolean CFSET() {
    return (F & CF) != 0;
  }

  // increments PC
  private void incPC() {
    PC = (PC + 1) & 0xffff;
  }

  // adds n to PC
  private void incPC(final int n) {
    PC = (PC + n) & 0xffff;
  }

  // increments SP
  private void incSP() {
    SP = (SP + 1) & 0xffff;
  }

  // decrements SP
  private void decSP() {
    SP = (SP - 1) & 0xffff;
  }

  /**
   * Gets register A.
   *
   * @return register value
   */
  public int getA() {
    return A;
  }

  /**
   * Sets register A.
   *
   * @param n new register value
   */
  public void setA(final int n) {
    assert (n >= 0) && (n < 0x100);
    A = n & 0xff;
  }

  /**
   * Gets register F (flags).
   *
   * @return register value
   */
  public int getF() {
    return F;
  }

  /**
   * Sets register F (flags).  Fixed bits are corrected before assignment.
   *
   * @param n new register value
   */
  public void setF(final int n) {
    assert (n >= 0) && (n < 0x100);
    F = fixF(n);
  }

  /**
   * Gets register B.
   *
   * @return register value
   */
  public int getB() {
    return B;
  }

  /**
   * Sets register B.
   *
   * @param n new register value
   */
  public void setB(final int n) {
    assert (n >= 0) && (n < 0x100);
    B = n & 0xff;
  }

  /**
   * Gets register C.
   *
   * @return register value
   */
  public int getC() {
    return C;
  }

  /**
   * Sets register C.
   *
   * @param n new register value
   */
  public void setC(final int n) {
    assert (n >= 0) && (n < 0x100);
    C = n & 0xff;
  }

  /**
   * Gets register D.
   *
   * @return register value
   */
  public int getD() {
    return D;
  }

  /**
   * Sets register D.
   *
   * @param n new register value
   */
  public void setD(final int n) {
    assert (n >= 0) && (n < 0x100);
    D = n & 0xff;
  }

  /**
   * Gets register E.
   *
   * @return register value
   */
  public int getE() {
    return E;
  }

  /**
   * Sets register E.
   *
   * @param n new register value
   */
  public void setE(final int n) {
    assert (n >= 0) && (n < 0x100);
    E = n & 0xff;
  }

  /**
   * Gets register H.
   *
   * @return register value
   */
  public int getH() {
    return H;
  }

  /**
   * Sets register H.
   *
   * @param n new register value
   */
  public void setH(final int n) {
    assert (n >= 0) && (n < 0x100);
    H = n & 0xff;
  }

  /**
   * Gets register L.
   *
   * @return register value
   */
  public int getL() {
    return L;
  }

  /**
   * Sets register L.
   *
   * @param n new register value
   */
  public void setL(final int n) {
    assert (n >= 0) && (n < 0x100);
    L = n & 0xff;
  }

  /**
   * Gets register pair BC.
   *
   * @return register pair value
   */
  public int getBC() {
    return BC();
  }

  /**
   * Sets register pair BC.
   *
   * @param n new register pair value
   */
  public void setBC(final int n) {
    assert (n >= 0) && (n < 0x10000);
    B = (n >> 8) & 0xff;
    C = n & 0xff;
  }

  /**
   * Gets register pair DE.
   *
   * @return register pair value
   */
  public int getDE() {
    return DE();
  }

  /**
   * Sets register pair DE.
   *
   * @param n new register pair value
   */
  public void setDE(final int n) {
    assert (n >= 0) && (n < 0x10000);
    D = (n >> 8) & 0xff;
    E = n & 0xff;
  }

  /**
   * Gets register pair HL.
   *
   * @return register pair value
   */
  public int getHL() {
    return HL();
  }

  /**
   * Sets register pair HL.
   *
   * @param n new register pair value
   */
  public void setHL(final int n) {
    assert (n >= 0) && (n < 0x10000);
    H = (n >> 8) & 0xff;
    L = n & 0xff;
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
   * Gets the stack pointer.
   *
   * @return stack pointer
   */
  public int getSP() {
    return SP;
  }

  /**
   * Sets the stack pointer.
   *
   * @param n new value for the stack pointer
   */
  public void setSP(final int n) {
    assert (n >= 0) && (n < 0x10000);
    SP = n & 0xffff;
  }

  /**
   * Returns {@code true} if the sign (S) flag is set.
   *
   * @return {@code true} if flag set, {@code false} otherwise
   */
  public boolean isSF() {
    return SFSET();
  }

  /**
   * Sets the sign (S) flag.
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
   * Returns {@code true} if the zero (Z) flag is set.
   *
   * @return {@code true} if flag set, {@code false} otherwise
   */
  public boolean isZF() {
    return ZFSET();
  }

  /**
   * Sets the zero (Z) flag.
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
   * Returns {@code true} if the auxiliary carry (AC) flag is set.
   *
   * @return {@code true} if flag set, {@code false} otherwise
   */
  public boolean isACF() {
    return ACFSET();
  }

  /**
   * Sets the auxiliary carry (AC) flag.
   *
   * @param b new value for the flag
   */
  public void setACF(final boolean b) {
    if (b) {
      SETACF();
    } else {
      RESETACF();
    }
  }

  /**
   * Returns {@code true} if the parity (P) flag is set.
   *
   * @return {@code true} if flag set, {@code false} otherwise
   */
  public boolean isPF() {
    return PFSET();
  }

  /**
   * Sets the parity (P) flag.
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
   * Returns {@code true} if the carry (C) flag is set.
   *
   * @return {@code true} if flag set, {@code false} otherwise
   */
  public boolean isCF() {
    return CFSET();
  }

  /**
   * Sets the carry (C) flag.
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

  // array of opcodes
  private final Opcode[] opcodes = new Opcode[] {

    // 00 NOP
    new Opcode("NOP", "", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 4;
	}
      }
      ),
	
    // 01 LXIB	  
    new Opcode("LXI", "B,", 3, Processor.INS_NONE, new Executable() {
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
	
    // 02 STAXB	  
    new Opcode("STAX", "B", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  memory.setByte(BC(), A);
	  incPC();
	  return 7;
	}
      }
      ),
	
    // 03 INXB	  
    new Opcode("INX", "B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  C = (C + 1) & 0xff;
	  if (C == 0) {
	    B = (B + 1) & 0xff;
	  }
	  incPC();
	  return 5;
	}
      }
      ),
	
    // 04 INRB	  
    new Opcode("INR", "B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  B = (B + 1) & 0xff;
	  TF(B);
	  if ((B & 0x0f) != 0) {
	    RESETACF();
	  } else {
	    SETACF();
	  }
	  incPC();
	  return 5;
	}
      }
      ),
	
    // 05 DCRB	  
    new Opcode("DCR", "B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  B = (B - 1) & 0xff;
	  TF(B);
	  if ((B & 0x0f) == 0x0f) {
	    RESETACF();
	  } else {
	    SETACF();
	  }
	  incPC();
	  return 5;
	}
      }
      ),
	    
    // 06 MVIB	  
    new Opcode("MVI", "B,", 2, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  B = memory.getByte(PC);
	  incPC();
	  return 7;
	}
      }
      ),
	
    // 07 RLC	  
    new Opcode("RLC", "", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  if ((A & 0x80) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = ((A << 1) | (F & 1)) & 0xff;
	  incPC();
	  return 4;
	}
      }		    
      ),
	
    // 08 undefined (NOP)
    new Opcode("UND", "", 1, Processor.INS_UND, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 4;
	}
      }		    
      ),
	
    // 09 DADB	  
    new Opcode("DAD", "B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int ti = (H << 8) + L + (B << 8) + C;
	  L = ti & 0xff;
	  H = (ti >> 8) & 0xff;
	  if (ti > 0xffff) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  incPC();
	  return 10;
	}	
      }		    
      ),
	
    // 0a LDAXB	  
    new Opcode("LDAX", "B", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  A = memory.getByte(BC());
	  incPC();
	  return 7;
	}
      }		    
      ),
	
    // 0b DCXB	  
    new Opcode("DCX", "B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  if (--C < 0) {
	    C = 0xff;
	    B = (B - 1) & 0xff;
	  }
	  incPC();
	  return 5;
	}
      }		    
      ),
	
    // 0c INRC	  
    new Opcode("INR", "C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  C = (C + 1) & 0xff;
	  TF(C);
	  if ((C & 0x0f) != 0) {
	    RESETACF();
	  } else {
	    SETACF();
	  }
	  incPC();
	  return 5;
	}
      }		    
      ),
	
    // 0d DCRC	  
    new Opcode("DCR", "C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  C = (C - 1) & 0xff;
	  TF(C);
	  if ((C & 0x0f) == 0x0f) {
	    RESETACF();
	  } else {
	    SETACF();
	  }
	  incPC();
	  return 5;
	}
      }		    
      ),
	
    // 0e MVIC	  
    new Opcode("MVI", "C,", 2, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  C = memory.getByte(PC);
	  incPC();
	  return 7;
	}
      }		    
      ),
	
    // 0f RRC	  
    new Opcode("RRC", "", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  if ((A & 1) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = ((A >> 1) | (F << 7)) & 0xff;
	  incPC();
	  return 4;
	}
      }		    
      ),
	
    // 10 undefined (NOP)
    new Opcode("UND", "", 1, Processor.INS_UND, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 4;
	}
      }		    
      ),
	
    // 11 LXID	  
    new Opcode("LXI", "D,", 3, Processor.INS_NONE, new Executable() {
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
	
    // 12 STAXD
    new Opcode("STAX", "D", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  memory.setByte(DE(), A);
	  incPC();
	  return 7;
	}
      }		    
      ),
	
    // 13 INXD	  
    new Opcode("INX", "D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  E = (E + 1) & 0xff;
	  if (E == 0) {
	    D = (D + 1) & 0xff;
	  }
	  incPC();
	  return 5;
	}
      }		    
      ),
	
    // 14 INRD	  
    new Opcode("INR", "D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  D = (D + 1) & 0xff;
	  TF(D);
	  if ((D & 0x0f) != 0) {
	    RESETACF();
	  } else {
	    SETACF();
	  }
	  incPC();
	  return 5;
	}
      }		    
      ),
	
    // 15 DCRD	  
    new Opcode("DCR", "D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  D = (D - 1) & 0xff;
	  TF(D);
	  if ((D & 0x0f) == 0x0f) {
	    RESETACF();
	  } else {
	    SETACF();
	  }
	  incPC();
	  return 5;
	}
      }		    
      ),
	
    // 16 MVID	  
    new Opcode("MVI", "D,", 2, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  D = memory.getByte(PC);
	  incPC();
	  return 7;
	}
      }		    
      ),
	
    // 17 RAL	  
    new Opcode("RAL", "", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tb = A;
	  A = ((A << 1) | (F & 1)) & 0xff;
	  if ((tb & 0x80) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  incPC();
	  return 4;
	}
      }		    
      ),
	
    // 18 undefined (NOP)
    new Opcode("UND", "", 1, Processor.INS_UND, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 4;
	}
      }		    
      ),
	
    // 19 DADD
    new Opcode("DAD", "D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int ti = (H << 8) + L + (D << 8) + E;
	  L = ti & 0xff;
	  H = (ti >> 8) & 0xff;
	  if (ti > 0xffff) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  incPC();
	  return 10;
	}	
      }		    
      ),
	
    // 1a LDAXD	  
    new Opcode("LDAX", "D", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  A = memory.getByte(DE());
	  incPC();
	  return 7;
	}
      }		    
      ),
	
    // 1b DCXD	  
    new Opcode("DCX", "D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  if (--E < 0) {
	    E = 0xff;
	    D = (D - 1) & 0xff;
	  }
	  incPC();
	  return 5;
	}
      }		    
      ),
	
    // 1c INRE	  
    new Opcode("INR", "E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  E = (E + 1) & 0xff;
	  TF(E);
	  if ((E & 0x0f) != 0) {
	    RESETACF();
	  } else {
	    SETACF();
	  }
	  incPC();
	  return 5;
	}
      }		    
      ),
	
    // 1d DCRE	  
    new Opcode("DCR", "E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  E = (E - 1) & 0xff;
	  TF(E);
	  if ((E & 0x0f) == 0x0f) {
	    RESETACF();
	  } else {
	    SETACF();
	  }
	  incPC();
	  return 5;
	}
      }		    
      ),
	
    // 1e MVIE	  
    new Opcode("MVI", "E,", 2, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  E = memory.getByte(PC);
	  incPC();
	  return 7;
	}
      }		    
      ),
	
    // 1f RAR	  
    new Opcode("RAR", "", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tb = A;
	  A = ((A >> 1) | (F << 7)) & 0xff;
	  if ((tb & 1) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 20 undefined (NOP)
    new Opcode("UND", "", 1, Processor.INS_UND, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 4;
	}
      }		    
      ),
	
    // 21 LXIH	  
    new Opcode("LXI", "H,", 3, Processor.INS_NONE, new Executable() {
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
	
    // 22 SHLD
    new Opcode("SHLD", "", 3, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  int tw = memory.getByte((PC + 1) & 0xffff) +
	    (memory.getByte((PC + 2) & 0xffff) << 8);
	  memory.setByte(tw, L);
	  memory.setByte((tw + 1) & 0xffff, H);
	  incPC(3);
	  return 16;
	}
      }		    
      ),
	
    // 23 INXH
    new Opcode("INX", "H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  L = (L + 1) & 0xff;
	  if (L == 0) {
	    H = (H + 1) & 0xff;
	  }
	  incPC();
	  return 5;
	}
      }		    
      ),
	
    // 24 INRH	  
    new Opcode("INR", "H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  H = (H + 1) & 0xff;
	  TF(H);
	  if ((H & 0x0f) != 0) {
	    RESETACF();
	  } else {
	    SETACF();
	  }
	  incPC();
	  return 5;
	}
      }		    
      ),
	
    // 25 DCRH	  
    new Opcode("DCR", "H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  H = (H - 1) & 0xff;
	  TF(H);
	  if ((H & 0x0f) == 0x0f) {
	    RESETACF();
	  } else {
	    SETACF();
	  }
	  incPC();
	  return 5;
	}
      }		    
      ),
	
    // 26 MVIH	  
    new Opcode("MVI", "H,", 2, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  H = memory.getByte(PC);
	  incPC();
	  return 7;
	}
      }		    
      ),
	
    // 27 DAA	  
    new Opcode("DAA", "", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tw = A;
	  if (((tw & 0x0f) > 0x09) || ACFSET()) {
	    if ((tw & 0x0f) > 0x09) {
	      SETACF();
	    } else {
	      RESETACF();
	    }
	    tw += 0x06;
	  }
	  if ((tw > 0x9f) || CFSET()) {
	    tw += 0x60;
	    SETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 28 undefined (NOP)
    new Opcode("UND", "", 1, Processor.INS_UND, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 4;
	}
      }		    
      ),
	
    // 29 DADH	  
    new Opcode("DAD", "H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int ti = (H << 9) + (L << 1);
	  L = ti & 0xff;
	  H = (ti >> 8) & 0xff;
	  if (ti > 0xffff) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  incPC();
	  return 10;
	}	
      }		    
      ),
	
    // 2a LHLD	  
    new Opcode("LHLD", "", 3, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  int tw = memory.getByte((PC + 1) & 0xffff) +
	    (memory.getByte((PC + 2) & 0xffff) << 8);
	  L = memory.getByte(tw);
	  H = memory.getByte((tw + 1) & 0xffff);
	  incPC(3);
	  return 16;
	}
      }		    
      ), 
	
    // 2b DCXH
    new Opcode("DCX", "H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  if (--L < 0) {
	    L = 0xff;
	    H = (H - 1) & 0xff;
	  }
	  incPC();
	  return 5;
	}
      }		    
      ),
	
    // 2c INRL	  
    new Opcode("INR", "L", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  L = (L + 1) & 0xff;
	  TF(L);
	  if ((L & 0x0f) != 0) {
	    RESETACF();
	  } else {
	    SETACF();
	  }
	  incPC();
	  return 5;
	}
      }		    
      ),
	
    // 2d DCRL	  
    new Opcode("DCR", "L", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  L = (L - 1) & 0xff;
	  TF(L);
	  if ((L & 0x0f) == 0x0f) {
	    RESETACF();
	  } else {
	    SETACF();
	  }
	  incPC();
	  return 5;
	}
      }		    
      ),
	
    // 2e MVIL	  
    new Opcode("MVI", "L,", 2, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  L = memory.getByte(PC);
	  incPC();
	  return 7;
	}
      }		    
      ),
	
    // 2f CMA	  
    new Opcode("CMA", "", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A = (~A) & 0xff;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 30 undefined (NOP)
    new Opcode("UND", "", 1, Processor.INS_UND, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 4;
	}
      }		    
      ),
	
    // 31 LXISP	  
    new Opcode("LXI", "SP,", 3, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  int tb = memory.getByte(PC);
	  incPC();
	  SP = tb + (memory.getByte(PC) << 8);
	  incPC();
	  return 10;
	}
      }		    
      ),
	
    // 32 STA
    new Opcode("STA", "", 3, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  int tw = memory.getByte((PC + 1) & 0xffff) +
	    (memory.getByte((PC + 2) & 0xffff) << 8);
	  memory.setByte(tw, A);
	  incPC(3);
	  return 13;
	}
      }		    
      ), 
	
    // 33 INXSP	  
    new Opcode("INX", "SP", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incSP();
	  incPC();
	  return 5;
	}
      }		    
      ),
	
    // 34 INRM	  
    new Opcode("INR", "M", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  int tw = HL();
	  int tb = (memory.getByte(tw) + 1) & 0xff;
	  memory.setByte(tw, tb);
	  TF(tb);
	  if ((tb & 0x0f) != 0) {
	    RESETACF();
	  } else {
	    SETACF();
	  }
	  incPC();
	  return 10;
	}
      }		    
      ),
	
    // 35 DCRM	  
    new Opcode("DCR", "M", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  int tw = HL();
	  int tb = (memory.getByte(tw) - 1) & 0xff;
	  memory.setByte(tw, tb);
	  TF(tb);
	  if ((tb & 0x0f) == 0x0f) {
	    RESETACF();
	  } else {
	    SETACF();
	  }
	  incPC();
	  return 10;
	}
      }		    
      ),
	
    // 36 MVIM	  
    new Opcode("MVI", "M,", 2, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  int tw = HL();
	  incPC();
	  memory.setByte(tw, memory.getByte(PC));
	  incPC();
	  return 10;
	}
      }		    
      ),
	
    // 37 STC	  
    new Opcode("STC", "", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  SETCF();
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 38 undefined (NOP)
    new Opcode("UND", "", 1, Processor.INS_UND, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 4;
	}
      }		    
      ),
	
    // 39 DADSP
    new Opcode("DAD", "SP", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int ti = (H << 8) + L + SP;
	  L = ti & 0xff;
	  H = (ti >> 8) & 0xff;
	  if (ti > 0xffff) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  incPC();
	  return 10;
	}	
      }		    
      ), 
	
    // 3a LDA	  
    new Opcode("LDA", "", 3, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  A = memory.getByte((memory.getByte((PC + 1) & 0xffff)) +
			     (memory.getByte((PC + 2) & 0xffff) << 8));
	  incPC(3);
	  return 13;
	}
      }		    
      ),
	
    // 3b DCXSP	  
    new Opcode("DCX", "SP", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  decSP();
	  incPC();
	  return 5;
	}
      }		    
      ),
	
    // 3c INRA	  
    new Opcode("INR", "A", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A = (A + 1) & 0xff;
	  TF(A);
	  if ((A & 0x0f) != 0) {
	    RESETACF();
	  } else {
	    SETACF();
	  }
	  incPC();
	  return 5;
	}
      }		    
      ),
	
    // 3d DCRA	  
    new Opcode("DCR", "A", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A = (A - 1) & 0xff;
	  TF(A);
	  if ((A & 0x0f) == 0x0f) {
	    RESETACF();
	  } else {
	    SETACF();
	  }
	  incPC();
	  return 5;
	}
      }		    
      ),
	
    // 3e MVIA	  
    new Opcode("MVI", "A,", 2, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  A = memory.getByte(PC);
	  incPC();
	  return 7;
	}
      }		    
      ),
	
    // 3f CMC	  
    new Opcode("CMC", "", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  F ^= CF;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 40 MOVBB	  
    new Opcode("MOV", "B,B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 41 MOVBC	  
    new Opcode("MOV", "B,C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  B = C;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 42 MOVBD	  
    new Opcode("MOV", "B,D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  B = D;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 43 MOVBE	  
    new Opcode("MOV", "B,E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  B = E;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 44 MOVBH	  
    new Opcode("MOV", "B,H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  B = H;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 45 MOVBL	  
    new Opcode("MOV", "B,L", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  B = L;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 46 MOVBM	  
    new Opcode("MOV", "B,M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  B = memory.getByte(HL());
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // 47 MOVBA	  
    new Opcode("MOV", "B,A", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  B = A;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 48 MOVCB	  
    new Opcode("MOV", "C,B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  C = B;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 49 MOVCC	  
    new Opcode("MOV", "C,C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 4a MOVCD	  
    new Opcode("MOV", "C,D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  C = D;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 4b MOVCE	  
    new Opcode("MOV", "C,E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  C = E;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 4c MOVCH	  
    new Opcode("MOV", "C,H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  C = H;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 4d MOVCL	  
    new Opcode("MOV", "C,L", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  C = L;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
	
    // 4e MOVCM	  
    new Opcode("MOV", "C,M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  C = memory.getByte(HL());
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // 4f MOVCA	  
    new Opcode("MOV", "C,A", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  C = A;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 50 MOVDB	  
    new Opcode("MOV", "D,B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  D = B;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 51 MOVDC	  
    new Opcode("MOV", "D,C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  D = C;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 52 MOVDD	  
    new Opcode("MOV", "D,D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 53 MOVDE	  
    new Opcode("MOV", "D,E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  D = E;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 54 MOVDH	  
    new Opcode("MOV", "D,H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  D = H;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 55 MOVDL	  
    new Opcode("MOV", "D,L", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  D = L;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 56 MOVDM	  
    new Opcode("MOV", "D,M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  D = memory.getByte(HL());
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // 57 MOVDA	  
    new Opcode("MOV", "D,A", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  D = A;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 58 MOVEB	  
    new Opcode("MOV", "E,B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  E = B;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 59 MOVEC	  
    new Opcode("MOV", "E,C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  E = C;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 5a MOVED	  
    new Opcode("MOV", "E,D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  E = D;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 5b MOVEE	  
    new Opcode("MOV", "E,E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 5c MOVEH	  
    new Opcode("MOV", "E,H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  E = H;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 5d MOVEL	  
    new Opcode("MOV", "E,L", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  E = L;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 5e MOVEM	  
    new Opcode("MOV", "E,M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  E = memory.getByte(HL());
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // 5f MOVEA	  
    new Opcode("MOV", "E,A", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  E = A;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 60 MOVHB	  
    new Opcode("MOV", "H,B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  H = B;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 61 MOVHC	  
    new Opcode("MOV", "H,C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  H = C;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 62 MOVHD	  
    new Opcode("MOV", "H,D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  H = D;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 63 MOVHE	  
    new Opcode("MOV", "H,E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  H = E;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 64 MOVHH	  
    new Opcode("MOV", "H,H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 65 MOVHL	  
    new Opcode("MOV", "H,L", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  H = L;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 66 MOVHM	  
    new Opcode("MOV", "H,M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  H = memory.getByte(HL());
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // 67 MOVHA	  
    new Opcode("MOV", "H,A", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  H = A;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 68 MOVLB	  
    new Opcode("MOV", "L,B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  L = B;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 69 MOVLC	  
    new Opcode("MOV", "L,C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  L = C;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 6a MOVLD	  
    new Opcode("MOV", "L,D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  L = D;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 6b MOVLE	  
    new Opcode("MOV", "L,E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  L = E;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 6c MOVLH	  
    new Opcode("MOV", "L,H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  L = H;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 6d MOVLL	  
    new Opcode("MOV", "L,L", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 6e MOVLM	  
    new Opcode("MOV", "L,M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  L = memory.getByte(HL());
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // 6f MOVLA	  
    new Opcode("MOV", "L,A", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  L = A;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 70 MOVMB	  
    new Opcode("MOV", "M,B", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  int tw = HL();
	  memory.setByte(tw, B);
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // 71 MOVMC	  
    new Opcode("MOV", "M,C", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  int tw = HL();
	  memory.setByte(tw, C);
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // 72 MOVMD	  
    new Opcode("MOV", "M,D", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  int tw = HL();
	  memory.setByte(tw, D);
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // 73 MOVME	  
    new Opcode("MOV", "M,E", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  int tw = HL();
	  memory.setByte(tw, E);
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // 74 MOVMH	  
    new Opcode("MOV", "M,H", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  int tw = HL();
	  memory.setByte(tw, H);
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // 75 MOVML	  
    new Opcode("MOV", "M,L", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  int tw = HL();
	  memory.setByte(tw, L);
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // 76 HLT	  
    new Opcode("HLT", "", 1, Processor.INS_HLT, new Executable() {
	@Override
	public int exec() {
	  return 5;
	}	
      }		    
      ),
	
    // 77 MOVMA	  
    new Opcode("MOV", "M,A", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  int tw = HL();
	  memory.setByte(tw, A);
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // 78 MOVAB	  
    new Opcode("MOV", "A,B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A = B;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 79 MOVAC	  
    new Opcode("MOV", "A,C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A = C;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 7a MOVAD	  
    new Opcode("MOV", "A,D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A = D;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 7b MOVAE	  
    new Opcode("MOV", "A,E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A = E;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 7c MOVAH	  
    new Opcode("MOV", "A,H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A = H;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 7d MOVAL	  
    new Opcode("MOV", "A,L", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A = L;
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 7e MOVAM	  
    new Opcode("MOV", "A,M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  A = memory.getByte(HL());
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // 7f MOVAA	  
    new Opcode("MOV", "A,A", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // 80 ADDB	  
    new Opcode("ADD", "B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tw = A + B;
	  if (((A & 0x0f) + (B & 0x0f)) > 0x0f) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 81 ADDC	  
    new Opcode("ADD", "C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tw = A + C;
	  if (((A & 0x0f) + (C & 0x0f)) > 0x0f) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 82 ADDD	  
    new Opcode("ADD", "D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tw = A + D;
	  if (((A & 0x0f) + (D & 0x0f)) > 0x0f) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 83 ADDE	  
    new Opcode("ADD", "E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tw = A + E;
	  if (((A & 0x0f) + (E & 0x0f)) > 0x0f) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 84 ADDH	  
    new Opcode("ADD", "H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tw = A + H;
	  if (((A & 0x0f) + (H & 0x0f)) > 0x0f) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 85 ADDL	  
    new Opcode("ADD", "L", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tw = A + L;
	  if (((A & 0x0f) + (L & 0x0f)) > 0x0f) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 86 ADDM	  
    new Opcode("ADD", "M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(HL());
	  int tw = A + tb;
	  if (((A & 0x0f) + (tb & 0x0f)) > 0x0f) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // 87 ADDA	  
    new Opcode("ADD", "A", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tw = A << 1;
	  if (((A & 0x0f) << 1) > 0x0f) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 88 ADCB	  
    new Opcode("ADC", "B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tw = A + B + (F & CF);
	  if (((A & 0x0f) + (B & 0x0f) + (F & CF)) > 0x0f) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 89 ADCC	  
    new Opcode("ADC", "C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tw = A + C + (F & CF);
	  if (((A & 0x0f) + (C & 0x0f) + (F & CF)) > 0x0f) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 8a ADCD	  
    new Opcode("ADC", "D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tw = A + D + (F & CF);
	  if (((A & 0x0f) + (D & 0x0f) + (F & CF)) > 0x0f) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 8b ADCE	  
    new Opcode("ADC", "E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tw = A + E + (F & CF);
	  if (((A & 0x0f) + (E & 0x0f) + (F & CF)) > 0x0f) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 8c ADCH	  
    new Opcode("ADC", "H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tw = A + H + (F & CF);
	  if (((A & 0x0f) + (H & 0x0f) + (F & CF)) > 0x0f) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 8d ADCL	  
    new Opcode("ADC", "L", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tw = A + L + (F & CF);
	  if (((A & 0x0f) + (L & 0x0f) + (F & CF)) > 0x0f) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 8e ADCM	  
    new Opcode("ADC", "M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(HL());
	  int tw = A + tb + (F & CF);
	  if (((A & 0x0f) + (tb & 0x0f) + (F & CF)) > 0x0f) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // 8f ADCA	  
    new Opcode("ADC", "A", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tw = (A << 1) + (F & CF);
	  if ((((A & 0x0f) << 1) + (F & CF)) > 0x0f) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 90 SUBB	  
    new Opcode("SUB", "B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tb = B;
	  int tw = A - tb;
	  if (((A & 0x0f) - (tb & 0x0f)) >= 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 91 SUBC	  
    new Opcode("SUB", "C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tb = C;
	  int tw = A - tb;
	  if (((A & 0x0f) - (tb & 0x0f)) >= 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 92 SUBD	  
    new Opcode("SUB", "D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tb = D;
	  int tw = A - tb;
	  if (((A & 0x0f) - (tb & 0x0f)) >= 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 93 SUBE	  
    new Opcode("SUB", "E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tb = E;
	  int tw = A - tb;
	  if (((A & 0x0f) - (tb & 0x0f)) >= 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 94 SUBH	  
    new Opcode("SUB", "H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tb = H;
	  int tw = A - tb;
	  if (((A & 0x0f) - (tb & 0x0f)) >= 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 95 SUBL	  
    new Opcode("SUB", "L", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tb = L;
	  int tw = A - tb;
	  if (((A & 0x0f) - (tb & 0x0f)) >= 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 96 SUBM	  
    new Opcode("SUB", "M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(HL());
	  int tw = A - tb;
	  if (((A & 0x0f) - (tb & 0x0f)) >= 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // 97 SUBA	  
    new Opcode("SUB", "A", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A = 0;
	  F = FFIX | ZF | PF | ACF;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 98 SBBB	  
    new Opcode("SBB", "B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tb = B;
	  int tw = A - tb - (F & CF);
	  if (((A & 0x0f) - (tb & 0x0f) - (F & CF)) >= 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 99 SBBC	  
    new Opcode("SBB", "C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tb = C;
	  int tw = A - tb - (F & CF);
	  if (((A & 0x0f) - (tb & 0x0f) - (F & CF)) >= 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 9a SBBD	  
    new Opcode("SBB", "D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tb = D;
	  int tw = A - tb - (F & CF);
	  if (((A & 0x0f) - (tb & 0x0f) - (F & CF)) >= 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 9b SBBE	  
    new Opcode("SBB", "E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tb = E;
	  int tw = A - tb - (F & CF);
	  if (((A & 0x0f) - (tb & 0x0f) - (F & CF)) >= 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 9c SBBH	  
    new Opcode("SBB", "H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tb = H;
	  int tw = A - tb - (F & CF);
	  if (((A & 0x0f) - (tb & 0x0f) - (F & CF)) >= 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 9d SBBL	  
    new Opcode("SBB", "L", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tb = L;
	  int tw = A - tb - (F & CF);
	  if (((A & 0x0f) - (tb & 0x0f) - (F & CF)) >= 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // 9e SBBM	  
    new Opcode("SBB", "M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(HL());
	  int tw = A - tb - (F & CF);
	  if (((A & 0x0f) - (tb & 0x0f) - (F & CF)) >= 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // 9f SBBA	  
    new Opcode("SBB", "A", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tb = A;
	  int tw = -(F & CF);
	  if (CFSET()) {
	    RESETACF();
	  } else {
	    SETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // a0 ANAB	  
    new Opcode("ANA", "B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  if (((A | B) & 0x08) != 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  A &= B;
	  RESETCF();
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // a1 ANAC	  
    new Opcode("ANA", "C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  if (((A | C) & 0x08) != 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  A &= C;
	  RESETCF();
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // a2 ANAD	  
    new Opcode("ANA", "D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  if (((A | D) & 0x08) != 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  A &= D;
	  RESETCF();
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // a3 ANAE	  
    new Opcode("ANA", "E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  if (((A | E) & 0x08) != 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  A &= E;
	  RESETCF();
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // a4 ANAH	  
    new Opcode("ANA", "H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  if (((A | H) & 0x08) != 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  A &= H;
	  RESETCF();
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // a5 ANAL	  
    new Opcode("ANA", "L", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  if (((A | L) & 0x08) != 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  A &= L;
	  RESETCF();
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // a6 ANAM	  
    new Opcode("ANA", "M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(HL());
	  if (((A | tb) & 0x08) != 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  A &= tb;
	  RESETCF();
	  TF(A);
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // a7 ANAA	  
    new Opcode("ANA", "A", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  if ((A & 0x08) != 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  RESETCF();
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // a8 XRAB	  
    new Opcode("XRA", "B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A ^= B;
	  RESETACF();
	  RESETCF();
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // a9 XRAC	  
    new Opcode("XRA", "C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A ^= C;
	  RESETACF();
	  RESETCF();
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // aa XRAD	  
    new Opcode("XRA", "D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A ^= D;
	  RESETACF();
	  RESETCF();
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // ab XRAE	  
    new Opcode("XRA", "E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A ^= E;
	  RESETACF();
	  RESETCF();
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // ac XRAH	  
    new Opcode("XRA", "H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A ^= H;
	  RESETACF();
	  RESETCF();
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // ad XRAL	  
    new Opcode("XRA", "L", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A ^= L;
	  RESETACF();
	  RESETCF();
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // ae XRAM	  
    new Opcode("XRA", "M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  A ^= memory.getByte(HL());
	  RESETACF();
	  RESETCF();
	  TF(A);
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // af XRAA	  
    new Opcode("XRA", "A", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A = 0;
	  F = FFIX | ZF | PF;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // b0 ORAB	  
    new Opcode("ORA", "B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A |= B;
	  RESETACF();
	  RESETCF();
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // b1 ORAC	  
    new Opcode("ORA", "C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A |= C;
	  RESETACF();
	  RESETCF();
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // b2 ORAD	  
    new Opcode("ORA", "D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A |= D;
	  RESETACF();
	  RESETCF();
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // b3 ORAE	  
    new Opcode("ORA", "E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A |= E;
	  RESETACF();
	  RESETCF();
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // b4 ORAH	  
    new Opcode("ORA", "H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A |= H;
	  RESETACF();
	  RESETCF();
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // b5 ORAL	  
    new Opcode("ORA", "L", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  A |= L;
	  RESETACF();
	  RESETCF();
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // b6 ORAM	  
    new Opcode("ORA", "M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  A |= memory.getByte(HL());
	  RESETACF();
	  RESETCF();
	  TF(A);
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // b7 ORAA	  
    new Opcode("ORA", "A", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  RESETACF();
	  RESETCF();
	  TF(A);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // b8 CMPB	  
    new Opcode("CMP", "B", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tb = B;
	  int tw = A - tb;
	  if (((A & 0x0f) - (tb & 0x0f)) >= 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  TF(tw & 0xff);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // b9 CMPC	  
    new Opcode("CMP", "C", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tb = C;
	  int tw = A - tb;
	  if (((A & 0x0f) - (tb & 0x0f)) >= 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  TF(tw & 0xff);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // ba CMPD	  
    new Opcode("CMP", "D", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tb = D;
	  int tw = A - tb;
	  if (((A & 0x0f) - (tb & 0x0f)) >= 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  TF(tw & 0xff);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // bb CMPE	  
    new Opcode("CMP", "E", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tb = E;
	  int tw = A - tb;
	  if (((A & 0x0f) - (tb & 0x0f)) >= 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  TF(tw & 0xff);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // bc CMPH	  
    new Opcode("CMP", "H", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tb = H;
	  int tw = A - tb;
	  if (((A & 0x0f) - (tb & 0x0f)) >= 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  TF(tw & 0xff);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // bd CMPL	  
    new Opcode("CMP", "L", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tb = L;
	  int tw = A - tb;
	  if (((A & 0x0f) - (tb & 0x0f)) >= 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  TF(tw & 0xff);
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // be CMPM	  
    new Opcode("CMP", "M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(HL());
	  int tw = A - tb;
	  if (((A & 0x0f) - (tb & 0x0f)) >= 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  TF(tw & 0xff);
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // bf CMPA	  
    new Opcode("CMP", "A", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  F = FFIX | ZF | PF | ACF;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // c0 RNZ
    new Opcode("RNZ", "", 1, Processor.INS_RET, new Executable() {
	@Override
	public int exec() {
	  if (ZFSET()) {
	    incPC();
	    return 5;
	  } else {
	    int tb = memory.getByte(SP);
	    incSP();
	    PC = tb + (memory.getByte(SP) << 8);
	    incSP();
	    return 11;
	  }
	}
      }		    
      ),
	
    // c1 POPB	  
    new Opcode("POP", "B", 1, Processor.INS_NONE, new Executable() {
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
	
    // c2 JNZ	  
    new Opcode("JNZ", "", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  if (ZFSET()) {
	    incPC(3);
	  } else {
	    incPC();
	    int tb = memory.getByte(PC);
	    PC = tb + (memory.getByte((PC + 1) & 0xffff) << 8);
	  }
	  return 10;
	}	
      }		    
      ),
	
    // c3 JMP
    new Opcode("JMP", "", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  int tb = memory.getByte(PC);
	  PC = tb + (memory.getByte((PC + 1) & 0xffff) << 8);
	  return 10;
	}	
      }		    
      ),
	
    // c4 CNZ	  
    new Opcode("CNZ", "", 3, Processor.INS_CALL, new Executable() {
	@Override
	public int exec() {
	  if (ZFSET()) {
	    incPC(3);
	    return 11;
	  } else {
	    incPC();
	    int tb = memory.getByte(PC);
	    incPC();
	    int tw = tb + (memory.getByte(PC) << 8);
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
	
    // c5 PUSHB	  
    new Opcode("PUSH", "B", 1, Processor.INS_NONE, new Executable() {
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
	
    // c6 ADI	  
    new Opcode("ADI", "", 2, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  int tb = memory.getByte(PC);
	  int tw = A + tb;
	  if (((A & 0x0f) + (tb & 0x0f)) > 0x0f) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // c7 RST0	  
    new Opcode("RST", "0", 1, Processor.INS_CALL, new Executable() {
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
	
    // c8 RZ	  
    new Opcode("RZ", "", 1, Processor.INS_RET, new Executable() {
	@Override
	public int exec() {
	  if (ZFSET()) {
	    int tb = memory.getByte(SP);
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
	
    // c9 RET	  
    new Opcode("RET", "", 1, Processor.INS_RET, new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(SP);
	  incSP();
	  PC = tb + (memory.getByte(SP) << 8);
	  incSP();
	  return 10;
	}	
      }		    
      ),
	
    // ca JZ	  
    new Opcode("JZ", "", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  if (ZFSET()) {
	    incPC();
	    int tb = memory.getByte(PC);
	    PC = tb + (memory.getByte((PC + 1) & 0xffff) << 8);
	  } else {
	    incPC(3);
	  }
	  return 10;
	}	
      }		    
      ),   
	
    // cb undefined (JMP)
    new Opcode("UND", "", 3, Processor.INS_UND |
	       Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  int tb = memory.getByte(PC);
	  PC = tb + (memory.getByte((PC + 1) & 0xffff) << 8);
	  return 10;
	}	
      }		    
      ),
	
    // cc CZ	  
    new Opcode("CZ", "", 3, Processor.INS_CALL, new Executable() {
	@Override
	public int exec() {
	  if (ZFSET()) {
	    incPC();
	    int tb = memory.getByte(PC);
	    incPC();
	    int tw = tb + (memory.getByte(PC) << 8);
	    incPC();
	    decSP();
	    memory.setByte(SP, PC >> 8);
	    decSP();
	    memory.setByte(SP, PC & 0xff);
	    PC = tw;
	    return 17;
	  } else {
	    incPC(3);
	    return 11;
	  }
	}	
      }		    
      ),  
	
    // cd CALL	  
    new Opcode("CALL", "", 3, Processor.INS_CALL, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  int tb = memory.getByte(PC);
	  incPC();
	  int tw = tb + (memory.getByte(PC) << 8);
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
	
    // ce ACI	  
    new Opcode("ACI", "", 2, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  int tb = memory.getByte(PC);
	  int tw = A + tb + (F & CF);
	  if (((A & 0x0f) + (tb & 0x0f) + (F & CF)) > 0x0f) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // cf RST1	  
    new Opcode("RST", "1", 1, Processor.INS_CALL, new Executable() {
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
	
    // d0 RNC	  
    new Opcode("RNC", "", 1, Processor.INS_RET, new Executable() {
	@Override
	public int exec() {
	  if (CFSET()) {
	    incPC();
	    return 5;
	  } else {
	    int tb = memory.getByte(SP);
	    incSP();
	    PC = tb + (memory.getByte(SP) << 8);
	    incSP();
	    return 11;
	  }
	}	
      }		    
      ),
	
    // d1 POPD	  
    new Opcode("POP", "D", 1, Processor.INS_NONE, new Executable() {
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
	
    // d2 JNC	  
    new Opcode("JNC", "", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  if (CFSET()) {
	    incPC(3);
	  } else {
	    incPC();
	    int tb = memory.getByte(PC);
	    PC = tb + (memory.getByte((PC + 1) & 0xffff) << 8);
	  }
	  return 10;
	}	
      }		    
      ),
	
    // d3 OUT	  
    new Opcode("OUT", "", 2, Processor.INS_IO, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  int port = memory.getByte(PC);
	  for (IOElement t: outputPorts.get(port)) {
	    t.portOutput(port, A);
	  }
	  incPC();
	  return 10;
	}	
      }		    
      ),
	
    // d4 CNC	  
    new Opcode("CNC", "", 3, Processor.INS_CALL, new Executable() {
	@Override
	public int exec() {
	  if (CFSET()) {
	    incPC(3);
	    return 11;
	  } else {
	    incPC();
	    int tb = memory.getByte(PC);
	    incPC();
	    int tw = tb + (memory.getByte(PC) << 8);
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
	
    // d5 PUSHD	  
    new Opcode("PUSH", "D", 1, Processor.INS_NONE, new Executable() {
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
	
    // d6 SUI	  
    new Opcode("SUI", "", 2, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  int tb = memory.getByte(PC);
	  int tw = A - tb;
	  if (((A & 0x0f) - (tb & 0x0f)) >= 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // d7 RST2	  
    new Opcode("RST", "2", 1, Processor.INS_CALL, new Executable() {
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
	
    // d8 RC	  
    new Opcode("RC", "", 1, Processor.INS_RET, new Executable() {
	@Override
	public int exec() {
	  if (CFSET()) {
	    int tb = memory.getByte(SP);
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
	
    // d9 undefined (RET)
    new Opcode("UND", "", 1, Processor.INS_UND | Processor.INS_RET,
	       new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(SP);
	  incSP();
	  PC = tb + (memory.getByte(SP) << 8);
	  incSP();
	  return 10;
	}	
      }		    
      ),
	
    // da JC	  
    new Opcode("JC", "", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  if (CFSET()) {
	    incPC();
	    int tb = memory.getByte(PC);
	    PC = tb + (memory.getByte((PC + 1) & 0xffff) << 8);
	  } else {
	    incPC(3);
	  }
	  return 10;
	}	
      }		    
      ),
	
    // db IN	  
    new Opcode("IN", "", 2, Processor.INS_IO, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  int port = memory.getByte(PC);
	  A = 0xff;
	  for (IOElement t: inputPorts.get(port)) {
	    A &= t.portInput(port);
	  }
	  incPC();
	  return 10;
	}	
      }
      ),
	
    // dc CC	  
    new Opcode("CC", "", 3, Processor.INS_CALL, new Executable() {
	@Override
	public int exec() {
	  if (CFSET()) {
	    incPC();
	    int tb = memory.getByte(PC);
	    incPC();
	    int tw = tb + (memory.getByte(PC) << 8);
	    incPC();
	    decSP();
	    memory.setByte(SP, PC >> 8);
	    decSP();
	    memory.setByte(SP, PC & 0xff);
	    PC = tw;
	    return 17;
	  } else {
	    incPC(3);
	    return 11;
	  }
	}	
      }		    
      ),
	
    // dd undefined (CALL)
    new Opcode("UND", "", 3, Processor.INS_UND | Processor.INS_CALL,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  int tb = memory.getByte(PC);
	  incPC();
	  int tw = tb + (memory.getByte(PC) << 8);
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
	
    // de SBI	  
    new Opcode("SBI", "", 2, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  int tb = memory.getByte(PC);
	  int tw = A - tb - (F & CF);
	  if (((A & 0x0f) - (tb & 0x0f) - (F & CF)) >= 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  A = tw & 0xff;
	  TF(A);
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // df RST3	  
    new Opcode("RST", "3", 1, Processor.INS_CALL, new Executable() {
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
	
    // e0 RPO	  
    new Opcode("RPO", "", 1, Processor.INS_RET, new Executable() {
	@Override
	public int exec() {
	  if (PE()) {
	    incPC();
	    return 5;
	  } else {
	    int tb = memory.getByte(SP);
	    incSP();
	    PC = tb + (memory.getByte(SP) << 8);
	    incSP();
	    return 11;
	  }
	}	
      }		    
      ),
	
    // e1 POPH	  
    new Opcode("POP", "H", 1, Processor.INS_NONE, new Executable() {
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
	
    // e2 JPO	  
    new Opcode("JPO", "", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  if (PE()) {
	    incPC(3);
	  } else {
	    incPC();
	    int tb = memory.getByte(PC);
	    PC = tb + (memory.getByte((PC + 1) & 0xffff) << 8);
	  }
	  return 10;
	}	
      }		    
      ),
	
    // e3 XTHL	  
    new Opcode("XTHL", "", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  int tb = memory.getByte(SP);
	  memory.setByte(SP, L);
	  L = tb;
	  int tw = (SP + 1) & 0xffff;
	  tb = memory.getByte(tw);
	  memory.setByte(tw, H);
	  H = tb;
	  incPC();
	  return 18;
	}	
      }		    
      ),
	
    // e4 CPO	  
    new Opcode("CPO", "", 3, Processor.INS_CALL, new Executable() {
	@Override
	public int exec() {
	  if (PE()) {
	    incPC(3);
	    return 11;
	  } else {
	    incPC();
	    int tb = memory.getByte(PC);
	    incPC();
	    int tw = tb + (memory.getByte(PC) << 8);
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
	
    // e5 PUSHH	  
    new Opcode("PUSH", "H", 1, Processor.INS_NONE, new Executable() {
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
	
    // e6 ANI	  
    new Opcode("ANI", "", 2, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  int tb = memory.getByte(PC);
	  if (((A | tb) & 0x08) != 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  A &= tb & 0xff;
	  RESETCF();
	  TF(A);
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // e7 RST4	  
    new Opcode("RST", "4", 1, Processor.INS_CALL, new Executable() {
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
	
    // e8 RPE	  
    new Opcode("RPE", "", 1, Processor.INS_RET, new Executable() {
	@Override
	public int exec() {
	  if (PE()) {
	    int tb = memory.getByte(SP);
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
	
    // e9 PCHL	  
    new Opcode("PCHL", "", 1, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  PC = HL();
	  return 5;
	}	
      }		    
      ),
	
    // ea JPE	  
    new Opcode("JPE", "", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  if (PE()) {
	    incPC();
	    int tb = memory.getByte(PC);
	    PC = tb + (memory.getByte((PC + 1) & 0xffff) << 8);
	  } else {
	    incPC(3);
	  }
	  return 10;
	}	
      }		    
      ),
	
    // eb XCHG	  
    new Opcode("XCHG", "", 1, Processor.INS_NONE, new Executable() {
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
	
    // ec CPE	  
    new Opcode("CPE", "", 3, Processor.INS_CALL, new Executable() {
	@Override
	public int exec() {
	  if (PE()) {
	    incPC();
	    int tb = memory.getByte(PC);
	    incPC();
	    int tw = tb + (memory.getByte(PC) << 8);
	    incPC();
	    decSP();
	    memory.setByte(SP, PC >> 8);
	    decSP();
	    memory.setByte(SP, PC & 0xff);
	    PC = tw;
	    return 17;
	  } else {
	    incPC(3);
	    return 11;
	  }
	}	
      }		    
      ),
	
    // ed undefined (CALL)
    new Opcode("UND", "", 3, Processor.INS_UND | Processor.INS_CALL,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  int tb = memory.getByte(PC);
	  incPC();
	  int tw = tb + (memory.getByte(PC) << 8);
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
	
    // ee XRI	  
    new Opcode("XRI", "", 2, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  A ^= memory.getByte(PC);
	  RESETACF();
	  RESETCF();
	  TF(A);
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // ef RST5	  
    new Opcode("RST", "5", 1, Processor.INS_CALL, new Executable() {
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
	
    // f0 RP	  
    new Opcode("RP", "", 1, Processor.INS_RET, new Executable() {
	@Override
	public int exec() {
	  if (SFSET()) {
	    incPC();
	    return 5;
	  } else {
	    int tb = memory.getByte(SP);
	    incSP();
	    PC = tb + (memory.getByte(SP) << 8);
	    incSP();
	    return 11;
	  }
	}	
      }		    
      ),
	
    // f1 POPPSW	  
    new Opcode("POP", "PSW", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  F = fixF(memory.getByte(SP));
	  incSP();
	  A = memory.getByte(SP);
	  incSP();
	  incPC();
	  return 10;
	}	
      }		    
      ),
	
    // f2 JP	  
    new Opcode("JP", "", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  if (SFSET()) {
	    incPC(3);
	  } else {
	    incPC();
	    int tb = memory.getByte(PC);
	    PC = tb + (memory.getByte((PC + 1) & 0xffff) << 8);
	  }
	  return 10;
	}	
      }		    
      ),
	
    // f3 DI	  
    new Opcode("DI", "", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  IE = false;
	  incPC();
	  return 4;
	}	
      }		    
      ), 
	
    // f4 CP	  
    new Opcode("CP", "", 3, Processor.INS_CALL, new Executable() {
	@Override
	public int exec() {
	  if (SFSET()) {
	    incPC(3);
	    return 11;
	  } else {
	    incPC();
	    int tb = memory.getByte(PC);
	    incPC();
	    int tw = tb + (memory.getByte(PC) << 8);
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
	
    // f5 PUSHPSW	  
    new Opcode("PUSH", "PSW", 1, Processor.INS_NONE, new Executable() {
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
	
    // f6 ORI	  
    new Opcode("ORI", "", 2, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  A |= memory.getByte(PC);
	  RESETACF();
	  RESETCF();
	  TF(A);
	  incPC();
	  return 7;
	}	
      }		    
      ),
	
    // f7 RST6	  
    new Opcode("RST", "6", 1, Processor.INS_CALL, new Executable() {
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
	
    // f8 RM	  
    new Opcode("RM", "", 1, Processor.INS_RET, new Executable() {
	@Override
	public int exec() {
	  if (SFSET()) {
	    int tb = memory.getByte(SP);
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
	
    // f9 SPHL	  
    new Opcode("SPHL", "", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  SP = HL();
	  incPC();
	  return 5;
	}	
      }		    
      ),
	
    // fa JM	  
    new Opcode("JM", "", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  if (SFSET()) {
	    incPC();
	    int tb = memory.getByte(PC);
	    PC = tb + (memory.getByte((PC + 1) & 0xffff) << 8);
	  } else {
	    incPC(3);
	  }
	  return 10;
	}	
      }		    
      ),
	
    // fb EI	  
    new Opcode("EI", "", 1, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  IE = true;
	  incPC();
	  return 4;
	}	
      }		    
      ),
	
    // fc CM	  
    new Opcode("CM", "", 3, Processor.INS_CALL, new Executable() {
	@Override
	public int exec() {
	  if (SFSET()) {
	    incPC();
	    int tb = memory.getByte(PC);
	    incPC();
	    int tw = tb + (memory.getByte(PC) << 8);
	    incPC();
	    decSP();
	    memory.setByte(SP, PC >> 8);
	    decSP();
	    memory.setByte(SP, PC & 0xff);
	    PC = tw;
	    return 17;
	  } else {
	    incPC(3);
	    return 11;
	  }
	}	
      }		    
      ),
	
    // fd undefined (CALL)
    new Opcode("UND", "", 3, Processor.INS_UND | Processor.INS_CALL,
	       new Executable() {
	@Override
	public int exec() {
	  incPC();
	  int tb = memory.getByte(PC);
	  incPC();
	  int tw = tb + (memory.getByte(PC) << 8);
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
	
    // fe CPI	  
    new Opcode("CPI", "", 2, Processor.INS_NONE, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  int tb = memory.getByte(PC);
	  int tw = A - tb;
	  if (((A & 0x0f) - (tb & 0x0f)) >= 0) {
	    SETACF();
	  } else {
	    RESETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    RESETCF();
	  }
	  TF(tw & 0xff);
	  incPC();
	  return 7;
	}	
      }		    
      ), 
	
    // ff RST7	  
    new Opcode("RST", "7", 1, Processor.INS_CALL, new Executable() {
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
   * Gets Opcode.
   *
   * @param  n first byte of {@code Opcode}
   * @return {@code Opcode} value
   */
  public Opcode getOpcode(final int n) {
    assert (n >= 0) && (n < 0x100);
    return opcodes[n];
  }

  // inner subclass of Disassembly
  private class Intel8080ADisassembly extends Disassembly {
    private Opcode opcode;

    // main constructor
    private Intel8080ADisassembly(final int[] bytes) {
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
    return new Intel8080ADisassembly(bytes);
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
    return new Intel8080ADisassembly(newBytes);
  }

  /**
   * Gets a string representation of CPU state.
   *
   * @return CPU state
   */
  public String CPUState() {
    return String.format(
      "PC:%04x SP:%04x A:%02x B:%02x C:%02x D:%02x E:%02x H:%02x L:%02x " +
      "CF:%d ACF:%d ZF:%d SF:%d PF:%d IE:%d F:%02x",
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
      (ACFSET() ? 1 : 0),
      (ZFSET() ? 1 : 0),
      (SFSET() ? 1 : 0),
      (PE() ? 1 : 0),
      (IE ? 1 : 0), F);
  }

  /**
   * Gets disassembled instruction.
   *
   * @param  pc program counter
   * @return disassembled instruction
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
		   final List<Integer> breakpoints) {
    assert minCycles >= 0;
    final long endCycleCounter = cycleCounter + minCycles;

    while (!suspended) {
      scheduler.runSchedule(cycleCounter);
      if (resetPending) {
	reset();
	break;
      } else if (interruptPending != -1) {
	interrupt(interruptPending);
	break;
      } else {
	final Opcode opcode = opcodes[memory.getByte(PC)];
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
      if ((cycleCounter >= endCycleCounter) || breakpoints.contains(PC))
	break;
    }
  }
}
