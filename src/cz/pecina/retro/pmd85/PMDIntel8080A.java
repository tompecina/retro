/* PMDIntel8080A.java
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
import java.util.logging.Level;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import cz.pecina.retro.cpu.Intel8080A;
import cz.pecina.retro.cpu.Opcode;
import cz.pecina.retro.cpu.Executable;
import cz.pecina.retro.cpu.Processor;
import cz.pecina.retro.cpu.IOElement;
import cz.pecina.retro.cpu.CPUScheduler;

/**
 * Intel 8080A CPU, with modified timing used in Tesla PMD 85.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class PMDIntel8080A extends Intel8080A {

  // dynamic logger, per device
  private Logger log;

  // modified timing constants
  private static final int[] TIMING = new int[] {
     4,  //  0 = R4
     7,  //  1 = R4-R3
    11,  //  2 = R4-R3-R3
    15,  //  3 = R4-R3-R3-R3
    19,  //  4 = R4-R3-R3-R3-R3
     8,  //  5 = R4-W3
    10,  //  6 = R4-R3-W3
    14,  //  7 = R4-R3-R3-W3
    18,  //  8 = R4-R3-R3-W3-W3
    20,  //  9 = R4-R3-R3-W3-W5
     5,  // 10 = R5
    13,  // 11 = R5-R3-R3
    20,  // 12 = R5-R3-R3-W3-W3
    13,  // 13 = R5-W3-W3
    10   // 14 = R4-N3-N3
  };

  /**
   * The main constructor.  Memory must be attached using
   * {@link #setMemory} for the CPU to function.
   *
   * @param name davice name
   */
  public PMDIntel8080A(final String name) {
    super(name);
    log = Logger.getLogger(getClass().getName() + "." + name);

    log.fine(String.format("New PMDIntel8080A created, name: %s", name));
  }

  // for description see Intel8080A
  protected final Opcode[] opcodes = new Opcode[] {

    // 00 NOP
    new Opcode("NOP", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 01 LXIB
    new Opcode("LXI", "B,", 3, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  C = memory.getByte(PC);
	  incPC();
	  B = memory.getByte(PC);
	  incPC();
	  return TIMING[2] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 02 STAXB
    new Opcode("STAX", "B", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  memory.setByte(BC(), A);
	  incPC();
	  return TIMING[5] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 03 INXB
    new Opcode("INX", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C = (C + 1) & 0xff;
	  if (C == 0) {
	    B = (B + 1) & 0xff;
	  }
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 04 INRB
    new Opcode("INR", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B = (B + 1) & 0xff;
	  F3(B);
	  if ((B & 0x0f) != 0) {
	    CLEARACF();
	  } else {
	    SETACF();
	  }
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 05 DCRB
    new Opcode("DCR", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B = (B - 1) & 0xff;
	  F3(B);
	  if ((B & 0x0f) == 0x0f) {
	    CLEARACF();
	  } else {
	    SETACF();
	  }
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 06 MVIB
    new Opcode("MVI", "B,", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  B = memory.getByte(PC);
	  incPC();
	  return TIMING[1] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 07 RLC
    new Opcode("RLC", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((A & 0x80) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = ((A << 1) | (F & 1)) & 0xff;
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 08 undefined (NOP)
    new Opcode("UND", "", 1, Processor.INS_UND, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 09 DADB
    new Opcode("DAD", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int ti = (H << 8) + L + (B << 8) + C;
	  L = ti & 0xff;
	  H = (ti >> 8) & 0xff;
	  if (ti > 0xffff) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  incPC();
	  return TIMING[14] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 0a LDAXB
    new Opcode("LDAX", "B", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  A = memory.getByte(BC());
	  incPC();
	  return TIMING[1] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 0b DCXB
    new Opcode("DCX", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if (--C < 0) {
	    C = 0xff;
	    B = (B - 1) & 0xff;
	  }
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 0c INRC
    new Opcode("INR", "C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C = (C + 1) & 0xff;
	  F3(C);
	  if ((C & 0x0f) != 0) {
	    CLEARACF();
	  } else {
	    SETACF();
	  }
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 0d DCRC
    new Opcode("DCR", "C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C = (C - 1) & 0xff;
	  F3(C);
	  if ((C & 0x0f) == 0x0f) {
	    CLEARACF();
	  } else {
	    SETACF();
	  }
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 0e MVIC
    new Opcode("MVI", "C,", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  C = memory.getByte(PC);
	  incPC();
	  return TIMING[1] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 0f RRC
    new Opcode("RRC", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((A & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = ((A >> 1) | (F << 7)) & 0xff;
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 10 undefined (NOP)
    new Opcode("UND", "", 1, Processor.INS_UND, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 11 LXID
    new Opcode("LXI", "D,", 3, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  E = memory.getByte(PC);
	  incPC();
	  D = memory.getByte(PC);
	  incPC();
	  return TIMING[2] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 12 STAXD
    new Opcode("STAX", "D", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  memory.setByte(DE(), A);
	  incPC();
	  return TIMING[5] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 13 INXD
    new Opcode("INX", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E = (E + 1) & 0xff;
	  if (E == 0) {
	    D = (D + 1) & 0xff;
	  }
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 14 INRD
    new Opcode("INR", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D = (D + 1) & 0xff;
	  F3(D);
	  if ((D & 0x0f) != 0) {
	    CLEARACF();
	  } else {
	    SETACF();
	  }
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 15 DCRD
    new Opcode("DCR", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D = (D - 1) & 0xff;
	  F3(D);
	  if ((D & 0x0f) == 0x0f) {
	    CLEARACF();
	  } else {
	    SETACF();
	  }
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 16 MVID
    new Opcode("MVI", "D,", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  D = memory.getByte(PC);
	  incPC();
	  return TIMING[1] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 17 RAL
    new Opcode("RAL", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tb = A;
	  A = ((A << 1) | (F & 1)) & 0xff;
	  if ((tb & 0x80) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 18 undefined (NOP)
    new Opcode("UND", "", 1, Processor.INS_UND, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 19 DADD
    new Opcode("DAD", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int ti = (H << 8) + L + (D << 8) + E;
	  L = ti & 0xff;
	  H = (ti >> 8) & 0xff;
	  if (ti > 0xffff) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  incPC();
	  return TIMING[14] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 1a LDAXD
    new Opcode("LDAX", "D", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  A = memory.getByte(DE());
	  incPC();
	  return TIMING[1] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 1b DCXD
    new Opcode("DCX", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if (--E < 0) {
	    E = 0xff;
	    D = (D - 1) & 0xff;
	  }
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 1c INRE
    new Opcode("INR", "E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E = (E + 1) & 0xff;
	  F3(E);
	  if ((E & 0x0f) != 0) {
	    CLEARACF();
	  } else {
	    SETACF();
	  }
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 1d DCRE
    new Opcode("DCR", "E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E = (E - 1) & 0xff;
	  F3(E);
	  if ((E & 0x0f) == 0x0f) {
	    CLEARACF();
	  } else {
	    SETACF();
	  }
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 1e MVIE
    new Opcode("MVI", "E,", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  E = memory.getByte(PC);
	  incPC();
	  return TIMING[1] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 1f RAR
    new Opcode("RAR", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tb = A;
	  A = ((A >> 1) | (F << 7)) & 0xff;
	  if ((tb & 1) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 20 undefined (NOP)
    new Opcode("UND", "", 1, Processor.INS_UND, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 21 LXIH
    new Opcode("LXI", "H,", 3, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  L = memory.getByte(PC);
	  incPC();
	  H = memory.getByte(PC);
	  incPC();
	  return TIMING[2] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 22 SHLD
    new Opcode("SHLD", "", 3, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  final int tw = memory.getByte((PC + 1) & 0xffff) +
	    (memory.getByte((PC + 2) & 0xffff) << 8);
	  memory.setByte(tw, L);
	  memory.setByte((tw + 1) & 0xffff, H);
	  incPC(3);
	  return TIMING[8] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 23 INXH
    new Opcode("INX", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L = (L + 1) & 0xff;
	  if (L == 0) {
	    H = (H + 1) & 0xff;
	  }
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 24 INRH
    new Opcode("INR", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H = (H + 1) & 0xff;
	  F3(H);
	  if ((H & 0x0f) != 0) {
	    CLEARACF();
	  } else {
	    SETACF();
	  }
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 25 DCRH
    new Opcode("DCR", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H = (H - 1) & 0xff;
	  F3(H);
	  if ((H & 0x0f) == 0x0f) {
	    CLEARACF();
	  } else {
	    SETACF();
	  }
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 26 MVIH
    new Opcode("MVI", "H,", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  H = memory.getByte(PC);
	  incPC();
	  return TIMING[1] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 27 DAA
    new Opcode("DAA", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  int tw = A;
	  if (((tw & 0x0f) > 0x09) || ACFSET()) {
	    if ((tw & 0x0f) > 0x09) {
	      SETACF();
	    } else {
	      CLEARACF();
	    }
	    tw += 0x06;
	  }
	  if ((tw > 0x9f) || CFSET()) {
	    tw += 0x60;
	    SETCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 28 undefined (NOP)
    new Opcode("UND", "", 1, Processor.INS_UND, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 29 DADH
    new Opcode("DAD", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int ti = (H << 9) + (L << 1);
	  L = ti & 0xff;
	  H = (ti >> 8) & 0xff;
	  if (ti > 0xffff) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  incPC();
	  return TIMING[14] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 2a LHLD
    new Opcode("LHLD", "", 3, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  final int tw = memory.getByte((PC + 1) & 0xffff) +
	    (memory.getByte((PC + 2) & 0xffff) << 8);
	  L = memory.getByte(tw);
	  H = memory.getByte((tw + 1) & 0xffff);
	  incPC(3);
	  return TIMING[4] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 2b DCXH
    new Opcode("DCX", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if (--L < 0) {
	    L = 0xff;
	    H = (H - 1) & 0xff;
	  }
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 2c INRL
    new Opcode("INR", "L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L = (L + 1) & 0xff;
	  F3(L);
	  if ((L & 0x0f) != 0) {
	    CLEARACF();
	  } else {
	    SETACF();
	  }
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 2d DCRL
    new Opcode("DCR", "L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L = (L - 1) & 0xff;
	  F3(L);
	  if ((L & 0x0f) == 0x0f) {
	    CLEARACF();
	  } else {
	    SETACF();
	  }
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 2e MVIL
    new Opcode("MVI", "L,", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  L = memory.getByte(PC);
	  incPC();
	  return TIMING[1] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 2f CMA
    new Opcode("CMA", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = (~A) & 0xff;
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 30 undefined (NOP)
    new Opcode("UND", "", 1, Processor.INS_UND, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 31 LXISP
    new Opcode("LXI", "SP,", 3, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int tb = memory.getByte(PC);
	  incPC();
	  SP = tb + (memory.getByte(PC) << 8);
	  incPC();
	  return TIMING[2] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 32 STA
    new Opcode("STA", "", 3, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  final int tw = memory.getByte((PC + 1) & 0xffff) +
	    (memory.getByte((PC + 2) & 0xffff) << 8);
	  memory.setByte(tw, A);
	  incPC(3);
	  return TIMING[7] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 33 INXSP
    new Opcode("INX", "SP", 1, 0, new Executable() {
	@Override
	public int exec() {
	  incSP();
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 34 INRM
    new Opcode("INR", "M",
	       1,
	       Processor.INS_MR | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  final int tw = HL();
	  final int tb = (memory.getByte(tw) + 1) & 0xff;
	  memory.setByte(tw, tb);
	  F3(tb);
	  if ((tb & 0x0f) != 0) {
	    CLEARACF();
	  } else {
	    SETACF();
	  }
	  incPC();
	  return TIMING[6] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 35 DCRM
    new Opcode("DCR", "M",
	       1,
	       Processor.INS_MR | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  final int tw = HL();
	  final int tb = (memory.getByte(tw) - 1) & 0xff;
	  memory.setByte(tw, tb);
	  F3(tb);
	  if ((tb & 0x0f) == 0x0f) {
	    CLEARACF();
	  } else {
	    SETACF();
	  }
	  incPC();
	  return TIMING[6] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 36 MVIM
    new Opcode("MVI", "M,", 2, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  final int tw = HL();
	  incPC();
	  memory.setByte(tw, memory.getByte(PC));
	  incPC();
	  return TIMING[6] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 37 STC
    new Opcode("STC", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  SETCF();
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 38 undefined (NOP)
    new Opcode("UND", "", 1, Processor.INS_UND, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 39 DADSP
    new Opcode("DAD", "SP", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int ti = (H << 8) + L + SP;
	  L = ti & 0xff;
	  H = (ti >> 8) & 0xff;
	  if (ti > 0xffff) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  incPC();
	  return TIMING[14] + (int)(cycleCounter & 1);
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
	  return TIMING[3] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 3b DCXSP
    new Opcode("DCX", "SP", 1, 0, new Executable() {
	@Override
	public int exec() {
	  decSP();
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 3c INRA
    new Opcode("INR", "A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = (A + 1) & 0xff;
	  F3(A);
	  if ((A & 0x0f) != 0) {
	    CLEARACF();
	  } else {
	    SETACF();
	  }
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 3d DCRA
    new Opcode("DCR", "A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = (A - 1) & 0xff;
	  F3(A);
	  if ((A & 0x0f) == 0x0f) {
	    CLEARACF();
	  } else {
	    SETACF();
	  }
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 3e MVIA
    new Opcode("MVI", "A,", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  A = memory.getByte(PC);
	  incPC();
	  return TIMING[1] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 3f CMC
    new Opcode("CMC", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F ^= CF;
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 40 MOVBB
    new Opcode("MOV", "B,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 41 MOVBC
    new Opcode("MOV", "B,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B = C;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 42 MOVBD
    new Opcode("MOV", "B,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B = D;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 43 MOVBE
    new Opcode("MOV", "B,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B = E;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 44 MOVBH
    new Opcode("MOV", "B,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B = H;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 45 MOVBL
    new Opcode("MOV", "B,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B = L;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 46 MOVBM
    new Opcode("MOV", "B,M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  B = memory.getByte(HL());
	  incPC();
	  return TIMING[1] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 47 MOVBA
    new Opcode("MOV", "B,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  B = A;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 48 MOVCB
    new Opcode("MOV", "C,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C = B;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 49 MOVCC
    new Opcode("MOV", "C,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 4a MOVCD
    new Opcode("MOV", "C,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C = D;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 4b MOVCE
    new Opcode("MOV", "C,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C = E;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 4c MOVCH
    new Opcode("MOV", "C,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C = H;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 4d MOVCL
    new Opcode("MOV", "C,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C = L;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),


    // 4e MOVCM
    new Opcode("MOV", "C,M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  C = memory.getByte(HL());
	  incPC();
	  return TIMING[1] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 4f MOVCA
    new Opcode("MOV", "C,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  C = A;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 50 MOVDB
    new Opcode("MOV", "D,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D = B;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 51 MOVDC
    new Opcode("MOV", "D,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D = C;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 52 MOVDD
    new Opcode("MOV", "D,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 53 MOVDE
    new Opcode("MOV", "D,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D = E;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 54 MOVDH
    new Opcode("MOV", "D,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D = H;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 55 MOVDL
    new Opcode("MOV", "D,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D = L;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 56 MOVDM
    new Opcode("MOV", "D,M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  D = memory.getByte(HL());
	  incPC();
	  return TIMING[1] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 57 MOVDA
    new Opcode("MOV", "D,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  D = A;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 58 MOVEB
    new Opcode("MOV", "E,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E = B;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 59 MOVEC
    new Opcode("MOV", "E,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E = C;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 5a MOVED
    new Opcode("MOV", "E,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E = D;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 5b MOVEE
    new Opcode("MOV", "E,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 5c MOVEH
    new Opcode("MOV", "E,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E = H;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 5d MOVEL
    new Opcode("MOV", "E,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E = L;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 5e MOVEM
    new Opcode("MOV", "E,M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  E = memory.getByte(HL());
	  incPC();
	  return TIMING[1] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 5f MOVEA
    new Opcode("MOV", "E,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  E = A;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 60 MOVHB
    new Opcode("MOV", "H,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H = B;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 61 MOVHC
    new Opcode("MOV", "H,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H = C;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 62 MOVHD
    new Opcode("MOV", "H,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H = D;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 63 MOVHE
    new Opcode("MOV", "H,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H = E;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 64 MOVHH
    new Opcode("MOV", "H,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 65 MOVHL
    new Opcode("MOV", "H,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H = L;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 66 MOVHM
    new Opcode("MOV", "H,M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  H = memory.getByte(HL());
	  incPC();
	  return TIMING[1] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 67 MOVHA
    new Opcode("MOV", "H,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  H = A;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 68 MOVLB
    new Opcode("MOV", "L,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L = B;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 69 MOVLC
    new Opcode("MOV", "L,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L = C;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 6a MOVLD
    new Opcode("MOV", "L,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L = D;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 6b MOVLE
    new Opcode("MOV", "L,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L = E;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 6c MOVLH
    new Opcode("MOV", "L,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L = H;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 6d MOVLL
    new Opcode("MOV", "L,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 6e MOVLM
    new Opcode("MOV", "L,M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  L = memory.getByte(HL());
	  incPC();
	  return TIMING[1] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 6f MOVLA
    new Opcode("MOV", "L,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  L = A;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 70 MOVMB
    new Opcode("MOV", "M,B", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  final int tw = HL();
	  memory.setByte(tw, B);
	  incPC();
	  return TIMING[5] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 71 MOVMC
    new Opcode("MOV", "M,C", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  final int tw = HL();
	  memory.setByte(tw, C);
	  incPC();
	  return TIMING[5] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 72 MOVMD
    new Opcode("MOV", "M,D", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  final int tw = HL();
	  memory.setByte(tw, D);
	  incPC();
	  return TIMING[5] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 73 MOVME
    new Opcode("MOV", "M,E", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  final int tw = HL();
	  memory.setByte(tw, E);
	  incPC();
	  return TIMING[5] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 74 MOVMH
    new Opcode("MOV", "M,H", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  final int tw = HL();
	  memory.setByte(tw, H);
	  incPC();
	  return TIMING[5] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 75 MOVML
    new Opcode("MOV", "M,L", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  final int tw = HL();
	  memory.setByte(tw, L);
	  incPC();
	  return TIMING[5] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 76 HLT
    new Opcode("HLT", "", 1, Processor.INS_HLT, new Executable() {
	@Override
	public int exec() {
	  HALTED = true;
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 77 MOVMA
    new Opcode("MOV", "M,A", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  final int tw = HL();
	  memory.setByte(tw, A);
	  incPC();
	  return TIMING[5] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 78 MOVAB
    new Opcode("MOV", "A,B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = B;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 79 MOVAC
    new Opcode("MOV", "A,C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = C;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 7a MOVAD
    new Opcode("MOV", "A,D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = D;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 7b MOVAE
    new Opcode("MOV", "A,E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = E;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 7c MOVAH
    new Opcode("MOV", "A,H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = H;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 7d MOVAL
    new Opcode("MOV", "A,L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = L;
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 7e MOVAM
    new Opcode("MOV", "A,M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  A = memory.getByte(HL());
	  incPC();
	  return TIMING[1] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 7f MOVAA
    new Opcode("MOV", "A,A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 80 ADDB
    new Opcode("ADD", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + B;
	  if (((A & 0x0f) + (B & 0x0f)) > 0x0f) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 81 ADDC
    new Opcode("ADD", "C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + C;
	  if (((A & 0x0f) + (C & 0x0f)) > 0x0f) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 82 ADDD
    new Opcode("ADD", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + D;
	  if (((A & 0x0f) + (D & 0x0f)) > 0x0f) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 83 ADDE
    new Opcode("ADD", "E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + E;
	  if (((A & 0x0f) + (E & 0x0f)) > 0x0f) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 84 ADDH
    new Opcode("ADD", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + H;
	  if (((A & 0x0f) + (H & 0x0f)) > 0x0f) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 85 ADDL
    new Opcode("ADD", "L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + L;
	  if (((A & 0x0f) + (L & 0x0f)) > 0x0f) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 86 ADDM
    new Opcode("ADD", "M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  final int tb = memory.getByte(HL());
	  final int tw = A + tb;
	  if (((A & 0x0f) + (tb & 0x0f)) > 0x0f) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[1] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 87 ADDA
    new Opcode("ADD", "A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A << 1;
	  if (((A & 0x0f) << 1) > 0x0f) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 88 ADCB
    new Opcode("ADC", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + B + (F & CF);
	  if (((A & 0x0f) + (B & 0x0f) + (F & CF)) > 0x0f) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 89 ADCC
    new Opcode("ADC", "C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + C + (F & CF);
	  if (((A & 0x0f) + (C & 0x0f) + (F & CF)) > 0x0f) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 8a ADCD
    new Opcode("ADC", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + D + (F & CF);
	  if (((A & 0x0f) + (D & 0x0f) + (F & CF)) > 0x0f) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 8b ADCE
    new Opcode("ADC", "E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + E + (F & CF);
	  if (((A & 0x0f) + (E & 0x0f) + (F & CF)) > 0x0f) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 8c ADCH
    new Opcode("ADC", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + H + (F & CF);
	  if (((A & 0x0f) + (H & 0x0f) + (F & CF)) > 0x0f) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 8d ADCL
    new Opcode("ADC", "L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A + L + (F & CF);
	  if (((A & 0x0f) + (L & 0x0f) + (F & CF)) > 0x0f) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 8e ADCM
    new Opcode("ADC", "M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  final int tb = memory.getByte(HL());
	  final int tw = A + tb + (F & CF);
	  if (((A & 0x0f) + (tb & 0x0f) + (F & CF)) > 0x0f) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[1] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 8f ADCA
    new Opcode("ADC", "A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = (A << 1) + (F & CF);
	  if ((((A & 0x0f) << 1) + (F & CF)) > 0x0f) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 90 SUBB
    new Opcode("SUB", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - B;
	  if (((A & 0x0f) - (B & 0x0f)) >= 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 91 SUBC
    new Opcode("SUB", "C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - C;
	  if (((A & 0x0f) - (C & 0x0f)) >= 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 92 SUBD
    new Opcode("SUB", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - D;
	  if (((A & 0x0f) - (D & 0x0f)) >= 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 93 SUBE
    new Opcode("SUB", "E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - E;
	  if (((A & 0x0f) - (E & 0x0f)) >= 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 94 SUBH
    new Opcode("SUB", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - H;
	  if (((A & 0x0f) - (H & 0x0f)) >= 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 95 SUBL
    new Opcode("SUB", "L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - L;
	  if (((A & 0x0f) - (L & 0x0f)) >= 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 96 SUBM
    new Opcode("SUB", "M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  final int tb = memory.getByte(HL());
	  final int tw = A - tb;
	  if (((A & 0x0f) - (tb & 0x0f)) >= 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[1] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 97 SUBA
    new Opcode("SUB", "A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = 0;
	  F = FMASK_OR | ZF | PF | ACF;
	  incPC();
	  return 4;
	}
      }
      ),

    // 98 SBBB
    new Opcode("SBB", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - B - (F & CF);
	  if (((A & 0x0f) - (B & 0x0f) - (F & CF)) >= 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 99 SBBC
    new Opcode("SBB", "C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - C - (F & CF);
	  if (((A & 0x0f) - (C & 0x0f) - (F & CF)) >= 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 9a SBBD
    new Opcode("SBB", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - D - (F & CF);
	  if (((A & 0x0f) - (D & 0x0f) - (F & CF)) >= 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 9b SBBE
    new Opcode("SBB", "E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - E - (F & CF);
	  if (((A & 0x0f) - (E & 0x0f) - (F & CF)) >= 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 9c SBBH
    new Opcode("SBB", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - H - (F & CF);
	  if (((A & 0x0f) - (H & 0x0f) - (F & CF)) >= 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 9d SBBL
    new Opcode("SBB", "L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = A - L - (F & CF);
	  if (((A & 0x0f) - (L & 0x0f) - (F & CF)) >= 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 9e SBBM
    new Opcode("SBB", "M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  final int tb = memory.getByte(HL());
	  final int tw = A - tb - (F & CF);
	  if (((A & 0x0f) - (tb & 0x0f) - (F & CF)) >= 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[1] + (int)(cycleCounter & 1);
	}
      }
      ),

    // 9f SBBA
    new Opcode("SBB", "A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tw = -(F & CF);
	  if (CFSET()) {
	    CLEARACF();
	  } else {
	    SETACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // a0 ANAB
    new Opcode("ANA", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if (((A | B) & 0x08) != 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  A &= B;
	  CLEARCF();
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // a1 ANAC
    new Opcode("ANA", "C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if (((A | C) & 0x08) != 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  A &= C;
	  CLEARCF();
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // a2 ANAD
    new Opcode("ANA", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if (((A | D) & 0x08) != 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  A &= D;
	  CLEARCF();
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // a3 ANAE
    new Opcode("ANA", "E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if (((A | E) & 0x08) != 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  A &= E;
	  CLEARCF();
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // a4 ANAH
    new Opcode("ANA", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if (((A | H) & 0x08) != 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  A &= H;
	  CLEARCF();
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // a5 ANAL
    new Opcode("ANA", "L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if (((A | L) & 0x08) != 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  A &= L;
	  CLEARCF();
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // a6 ANAM
    new Opcode("ANA", "M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  final int tb = memory.getByte(HL());
	  if (((A | tb) & 0x08) != 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  A &= tb;
	  CLEARCF();
	  F3(A);
	  incPC();
	  return TIMING[1] + (int)(cycleCounter & 1);
	}
      }
      ),

    // a7 ANAA
    new Opcode("ANA", "A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  if ((A & 0x08) != 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  CLEARCF();
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // a8 XRAB
    new Opcode("XRA", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A ^= B;
	  CLEARACF();
	  CLEARCF();
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // a9 XRAC
    new Opcode("XRA", "C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A ^= C;
	  CLEARACF();
	  CLEARCF();
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // aa XRAD
    new Opcode("XRA", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A ^= D;
	  CLEARACF();
	  CLEARCF();
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // ab XRAE
    new Opcode("XRA", "E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A ^= E;
	  CLEARACF();
	  CLEARCF();
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // ac XRAH
    new Opcode("XRA", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A ^= H;
	  CLEARACF();
	  CLEARCF();
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // ad XRAL
    new Opcode("XRA", "L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A ^= L;
	  CLEARACF();
	  CLEARCF();
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // ae XRAM
    new Opcode("XRA", "M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  A ^= memory.getByte(HL());
	  CLEARACF();
	  CLEARCF();
	  F3(A);
	  incPC();
	  return TIMING[1] + (int)(cycleCounter & 1);
	}
      }
      ),

    // af XRAA
    new Opcode("XRA", "A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A = 0;
	  F = FMASK_OR | ZF | PF;
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // b0 ORAB
    new Opcode("ORA", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A |= B;
	  CLEARACF();
	  CLEARCF();
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // b1 ORAC
    new Opcode("ORA", "C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A |= C;
	  CLEARACF();
	  CLEARCF();
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // b2 ORAD
    new Opcode("ORA", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A |= D;
	  CLEARACF();
	  CLEARCF();
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // b3 ORAE
    new Opcode("ORA", "E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A |= E;
	  CLEARACF();
	  CLEARCF();
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // b4 ORAH
    new Opcode("ORA", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A |= H;
	  CLEARACF();
	  CLEARCF();
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // b5 ORAL
    new Opcode("ORA", "L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  A |= L;
	  CLEARACF();
	  CLEARCF();
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // b6 ORAM
    new Opcode("ORA", "M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  A |= memory.getByte(HL());
	  CLEARACF();
	  CLEARCF();
	  F3(A);
	  incPC();
	  return TIMING[1] + (int)(cycleCounter & 1);
	}
      }
      ),

    // b7 ORAA
    new Opcode("ORA", "A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  CLEARACF();
	  CLEARCF();
	  F3(A);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // b8 CMPB
    new Opcode("CMP", "B", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tb = B;
	  final int tw = A - tb;
	  if (((A & 0x0f) - (tb & 0x0f)) >= 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  F3(tw & 0xff);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // b9 CMPC
    new Opcode("CMP", "C", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tb = C;
	  final int tw = A - tb;
	  if (((A & 0x0f) - (tb & 0x0f)) >= 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  F3(tw & 0xff);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // ba CMPD
    new Opcode("CMP", "D", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tb = D;
	  final int tw = A - tb;
	  if (((A & 0x0f) - (tb & 0x0f)) >= 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  F3(tw & 0xff);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // bb CMPE
    new Opcode("CMP", "E", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tb = E;
	  final int tw = A - tb;
	  if (((A & 0x0f) - (tb & 0x0f)) >= 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  F3(tw & 0xff);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // bc CMPH
    new Opcode("CMP", "H", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tb = H;
	  final int tw = A - tb;
	  if (((A & 0x0f) - (tb & 0x0f)) >= 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  F3(tw & 0xff);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // bd CMPL
    new Opcode("CMP", "L", 1, 0, new Executable() {
	@Override
	public int exec() {
	  final int tb = L;
	  final int tw = A - tb;
	  if (((A & 0x0f) - (tb & 0x0f)) >= 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  F3(tw & 0xff);
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // be CMPM
    new Opcode("CMP", "M", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  final int tb = memory.getByte(HL());
	  final int tw = A - tb;
	  if (((A & 0x0f) - (tb & 0x0f)) >= 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  F3(tw & 0xff);
	  incPC();
	  return TIMING[1] + (int)(cycleCounter & 1);
	}
      }
      ),

    // bf CMPA
    new Opcode("CMP", "A", 1, 0, new Executable() {
	@Override
	public int exec() {
	  F = FMASK_OR | ZF | PF | ACF;
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // c0 RNZ
    new Opcode("RNZ", "",
	       1,
	       Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  if (ZFSET()) {
	    incPC();
	    return TIMING[10] + (int)(cycleCounter & 1);
	  } else {
	    final int tb = memory.getByte(SP);
	    incSP();
	    PC = tb + (memory.getByte(SP) << 8);
	    incSP();
	    return TIMING[11] + (int)(cycleCounter & 1);
	  }
	}
      }
      ),

    // c1 POPB
    new Opcode("POP", "B", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  C = memory.getByte(SP);
	  incSP();
	  B = memory.getByte(SP);
	  incSP();
	  incPC();
	  return TIMING[2] + (int)(cycleCounter & 1);
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
	    final int tb = memory.getByte(PC);
	    PC = tb + (memory.getByte((PC + 1) & 0xffff) << 8);
	  }
	  return TIMING[2] + (int)(cycleCounter & 1);
	}
      }
      ),

    // c3 JMP
    new Opcode("JMP", "", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int tb = memory.getByte(PC);
	  PC = tb + (memory.getByte((PC + 1) & 0xffff) << 8);
	  return TIMING[2] + (int)(cycleCounter & 1);
	}
      }
      ),

    // c4 CNZ
    new Opcode("CNZ", "",
	       3,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  if (ZFSET()) {
	    incPC(3);
	    return TIMING[11] + (int)(cycleCounter & 1);
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
	    return TIMING[12] + (int)(cycleCounter & 1);
	  }
	}
      }
      ),

    // c5 PUSHB
    new Opcode("PUSH", "B", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  decSP();
	  memory.setByte(SP, B);
	  decSP();
	  memory.setByte(SP, C);
	  incPC();
	  return TIMING[13] + (int)(cycleCounter & 1);
	}
      }
      ),

    // c6 ADI
    new Opcode("ADI", "", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int tb = memory.getByte(PC);
	  final int tw = A + tb;
	  if (((A & 0x0f) + (tb & 0x0f)) > 0x0f) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[1] + (int)(cycleCounter & 1);
	}
      }
      ),

    // c7 RST0
    new Opcode("RST", "0",
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
	  PC = 0x0000;
	  return TIMING[13] + (int)(cycleCounter & 1);
	}
      }
      ),

    // c8 RZ
    new Opcode("RZ", "",
	       1,
	       Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  if (ZFSET()) {
	    final int tb = memory.getByte(SP);
	    incSP();
	    PC = tb + (memory.getByte(SP) << 8);
	    incSP();
	    return TIMING[11] + (int)(cycleCounter & 1);
	  } else {
	    incPC();
	    return TIMING[10] + (int)(cycleCounter & 1);
	  }
	}
      }
      ),

    // c9 RET
    new Opcode("RET", "",
	       1,
	       Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  final int tb = memory.getByte(SP);
	  incSP();
	  PC = tb + (memory.getByte(SP) << 8);
	  incSP();
	  return TIMING[2] + (int)(cycleCounter & 1);
	}
      }
      ),

    // ca JZ
    new Opcode("JZ", "", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  if (ZFSET()) {
	    incPC();
	    final int tb = memory.getByte(PC);
	    PC = tb + (memory.getByte((PC + 1) & 0xffff) << 8);
	  } else {
	    incPC(3);
	  }
	  return TIMING[2] + (int)(cycleCounter & 1);
	}
      }
      ),

    // cb undefined (JMP)
    new Opcode("UND", "", 3, Processor.INS_UND |
	       Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int tb = memory.getByte(PC);
	  PC = tb + (memory.getByte((PC + 1) & 0xffff) << 8);
	  return TIMING[2] + (int)(cycleCounter & 1);
	}
      }
      ),

    // cc CZ
    new Opcode("CZ", "",
	       3,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
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
	    return TIMING[12] + (int)(cycleCounter & 1);
	  } else {
	    incPC(3);
	    return TIMING[11] + (int)(cycleCounter & 1);
	  }
	}
      }
      ),

    // cd CALL
    new Opcode("CALL", "",
	       3,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
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
	  return TIMING[12] + (int)(cycleCounter & 1);
	}
      }
      ),

    // ce ACI
    new Opcode("ACI", "", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int tb = memory.getByte(PC);
	  final int tw = A + tb + (F & CF);
	  if (((A & 0x0f) + (tb & 0x0f) + (F & CF)) > 0x0f) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[1] + (int)(cycleCounter & 1);
	}
      }
      ),

    // cf RST1
    new Opcode("RST", "1",
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
	  PC = 0x0008;
	  return TIMING[13] + (int)(cycleCounter & 1);
	}
      }
      ),

    // d0 RNC
    new Opcode("RNC", "",
	       1,
	       Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  if (CFSET()) {
	    incPC();
	    return TIMING[10] + (int)(cycleCounter & 1);
	  } else {
	    final int tb = memory.getByte(SP);
	    incSP();
	    PC = tb + (memory.getByte(SP) << 8);
	    incSP();
	    return TIMING[11] + (int)(cycleCounter & 1);
	  }
	}
      }
      ),

    // d1 POPD
    new Opcode("POP", "D", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  E = memory.getByte(SP);
	  incSP();
	  D = memory.getByte(SP);
	  incSP();
	  incPC();
	  return TIMING[2] + (int)(cycleCounter & 1);
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
	    final int tb = memory.getByte(PC);
	    PC = tb + (memory.getByte((PC + 1) & 0xffff) << 8);
	  }
	  return TIMING[2] + (int)(cycleCounter & 1);
	}
      }
      ),

    // d3 OUT
    new Opcode("OUT", "", 2, Processor.INS_IOW, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int port = memory.getByte(PC);
	  for (IOElement t: outputPorts.get(port)) {
	    t.portOutput(port, A);
	  }
	  incPC();
	  return TIMING[6] + (int)(cycleCounter & 1);
	}
      }
      ),

    // d4 CNC
    new Opcode("CNC", "",
	       3,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  if (CFSET()) {
	    incPC(3);
	    return TIMING[11] + (int)(cycleCounter & 1);
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
	    return TIMING[12] + (int)(cycleCounter & 1);
	  }
	}
      }
      ),

    // d5 PUSHD
    new Opcode("PUSH", "D", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  decSP();
	  memory.setByte(SP, D);
	  decSP();
	  memory.setByte(SP, E);
	  incPC();
	  return TIMING[13] + (int)(cycleCounter & 1);
	}
      }
      ),

    // d6 SUI
    new Opcode("SUI", "", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int tb = memory.getByte(PC);
	  final int tw = A - tb;
	  if (((A & 0x0f) - (tb & 0x0f)) >= 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[1] + (int)(cycleCounter & 1);
	}
      }
      ),

    // d7 RST2
    new Opcode("RST", "2",
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
	  PC = 0x0010;
	  return TIMING[13] + (int)(cycleCounter & 1);
	}
      }
      ),

    // d8 RC
    new Opcode("RC", "",
	       1,
	       Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  if (CFSET()) {
	    final int tb = memory.getByte(SP);
	    incSP();
	    PC = tb + (memory.getByte(SP) << 8);
	    incSP();
	    return TIMING[11] + (int)(cycleCounter & 1);
	  } else {
	    incPC();
	    return TIMING[10] + (int)(cycleCounter & 1);
	  }
	}
      }
      ),

    // d9 undefined (RET)
    new Opcode("UND", "",
	       1,
	       Processor.INS_UND | Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  final int tb = memory.getByte(SP);
	  incSP();
	  PC = tb + (memory.getByte(SP) << 8);
	  incSP();
	  return TIMING[2] + (int)(cycleCounter & 1);
	}
      }
      ),

    // da JC
    new Opcode("JC", "", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  if (CFSET()) {
	    incPC();
	    final int tb = memory.getByte(PC);
	    PC = tb + (memory.getByte((PC + 1) & 0xffff) << 8);
	  } else {
	    incPC(3);
	  }
	  return TIMING[2] + (int)(cycleCounter & 1);
	}
      }
      ),

    // db IN
    new Opcode("IN", "", 2, Processor.INS_IOR, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int port = memory.getByte(PC);
	  A = 0xff;
	  for (IOElement t: inputPorts.get(port)) {
	    A &= t.portInput(port);
	  }
	  incPC();
	  return TIMING[2] + (int)(cycleCounter & 1);
	}
      }
      ),

    // dc CC
    new Opcode("CC", "",
	       3,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
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
	    return TIMING[12] + (int)(cycleCounter & 1);
	  } else {
	    incPC(3);
	    return TIMING[11] + (int)(cycleCounter & 1);
	  }
	}
      }
      ),

    // dd undefined (CALL)
    new Opcode("UND", "",
	       3,
	       Processor.INS_UND | Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
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
	  return TIMING[12] + (int)(cycleCounter & 1);
	}
      }
      ),

    // de SBI
    new Opcode("SBI", "", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int tb = memory.getByte(PC);
	  final int tw = A - tb - (F & CF);
	  if (((A & 0x0f) - (tb & 0x0f) - (F & CF)) >= 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  A = tw & 0xff;
	  F3(A);
	  incPC();
	  return TIMING[1] + (int)(cycleCounter & 1);
	}
      }
      ),

    // df RST3
    new Opcode("RST", "3",
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
	  PC = 0x0018;
	  return TIMING[13] + (int)(cycleCounter & 1);
	}
      }
      ),

    // e0 RPO
    new Opcode("RPO", "",
	       1,
	       Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  if (PE()) {
	    incPC();
	    return TIMING[10] + (int)(cycleCounter & 1);
	  } else {
	    final int tb = memory.getByte(SP);
	    incSP();
	    PC = tb + (memory.getByte(SP) << 8);
	    incSP();
	    return TIMING[11] + (int)(cycleCounter & 1);
	  }
	}
      }
      ),

    // e1 POPH
    new Opcode("POP", "H", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  L = memory.getByte(SP);
	  incSP();
	  H = memory.getByte(SP);
	  incSP();
	  incPC();
	  return TIMING[2] + (int)(cycleCounter & 1);
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
	    final int tb = memory.getByte(PC);
	    PC = tb + (memory.getByte((PC + 1) & 0xffff) << 8);
	  }
	  return TIMING[2] + (int)(cycleCounter & 1);
	}
      }
      ),

    // e3 XTHL
    new Opcode("XTHL", "",
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
	  incPC();
	  return TIMING[9] + (int)(cycleCounter & 1);
	}
      }
      ),

    // e4 CPO
    new Opcode("CPO", "",
	       3,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  if (PE()) {
	    incPC(3);
	    return TIMING[11] + (int)(cycleCounter & 1);
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
	    return TIMING[12] + (int)(cycleCounter & 1);
	  }
	}
      }
      ),

    // e5 PUSHH
    new Opcode("PUSH", "H", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  decSP();
	  memory.setByte(SP, H);
	  decSP();
	  memory.setByte(SP, L);
	  incPC();
	  return TIMING[13] + (int)(cycleCounter & 1);
	}
      }
      ),

    // e6 ANI
    new Opcode("ANI", "", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int tb = memory.getByte(PC);
	  if (((A | tb) & 0x08) != 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  A &= tb & 0xff;
	  CLEARCF();
	  F3(A);
	  incPC();
	  return TIMING[1] + (int)(cycleCounter & 1);
	}
      }
      ),

    // e7 RST4
    new Opcode("RST", "4",
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
	  PC = 0x0020;
	  return TIMING[13] + (int)(cycleCounter & 1);
	}
      }
      ),

    // e8 RPE
    new Opcode("RPE", "",
	       1,
	       Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  if (PE()) {
	    final int tb = memory.getByte(SP);
	    incSP();
	    PC = tb + (memory.getByte(SP) << 8);
	    incSP();
	    return TIMING[11] + (int)(cycleCounter & 1);
	  } else {
	    incPC();
	    return TIMING[10] + (int)(cycleCounter & 1);
	  }
	}
      }
      ),

    // e9 PCHL
    new Opcode("PCHL", "", 1, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  PC = HL();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // ea JPE
    new Opcode("JPE", "", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  if (PE()) {
	    incPC();
	    final int tb = memory.getByte(PC);
	    PC = tb + (memory.getByte((PC + 1) & 0xffff) << 8);
	  } else {
	    incPC(3);
	  }
	  return TIMING[2] + (int)(cycleCounter & 1);
	}
      }
      ),

    // eb XCHG
    new Opcode("XCHG", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  int tb = H;
	  H = D;
	  D = tb;
	  tb = L;
	  L = E;
	  E = tb;
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // ec CPE
    new Opcode("CPE", "",
	       3,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
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
	    return TIMING[12] + (int)(cycleCounter & 1);
	  } else {
	    incPC(3);
	    return TIMING[11] + (int)(cycleCounter & 1);
	  }
	}
      }
      ),

    // ed undefined (CALL)
    new Opcode("UND", "",
	       3,
	       Processor.INS_UND | Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
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
	  return TIMING[12] + (int)(cycleCounter & 1);
	}
      }
      ),

    // ee XRI
    new Opcode("XRI", "", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  A ^= memory.getByte(PC);
	  CLEARACF();
	  CLEARCF();
	  F3(A);
	  incPC();
	  return TIMING[1] + (int)(cycleCounter & 1);
	}
      }
      ),

    // ef RST5
    new Opcode("RST", "5",
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
	  PC = 0x0028;
	  return TIMING[13] + (int)(cycleCounter & 1);
	}
      }
      ),

    // f0 RP
    new Opcode("RP", "",
	       1,
	       Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  if (SFSET()) {
	    incPC();
	    return TIMING[10] + (int)(cycleCounter & 1);
	  } else {
	    final int tb = memory.getByte(SP);
	    incSP();
	    PC = tb + (memory.getByte(SP) << 8);
	    incSP();
	    return TIMING[11] + (int)(cycleCounter & 1);
	  }
	}
      }
      ),

    // f1 POPPSW
    new Opcode("POP", "PSW", 1, Processor.INS_MR, new Executable() {
	@Override
	public int exec() {
	  F = fixF(memory.getByte(SP));
	  incSP();
	  A = memory.getByte(SP);
	  incSP();
	  incPC();
	  return TIMING[2] + (int)(cycleCounter & 1);
	}
      }
      ),

    // f2 JP
    new Opcode("JP", "",
	       3,
	       Processor.INS_JMP,
	       new Executable() {
	@Override
	public int exec() {
	  if (SFSET()) {
	    incPC(3);
	  } else {
	    incPC();
	    final int tb = memory.getByte(PC);
	    PC = tb + (memory.getByte((PC + 1) & 0xffff) << 8);
	  }
	  return TIMING[2] + (int)(cycleCounter & 1);
	}
      }
      ),

    // f3 DI
    new Opcode("DI", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  IE = false;
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // f4 CP
    new Opcode("CP", "",
	       3,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
	@Override
	public int exec() {
	  if (SFSET()) {
	    incPC(3);
	    return TIMING[11] + (int)(cycleCounter & 1);
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
	    return TIMING[12] + (int)(cycleCounter & 1);
	  }
	}
      }
      ),

    // f5 PUSHPSW
    new Opcode("PUSH", "PSW", 1, Processor.INS_MW, new Executable() {
	@Override
	public int exec() {
	  decSP();
	  memory.setByte(SP, A);
	  decSP();
	  memory.setByte(SP, F);
	  incPC();
	  return TIMING[13] + (int)(cycleCounter & 1);
	}
      }
      ),

    // f6 ORI
    new Opcode("ORI", "", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  A |= memory.getByte(PC);
	  CLEARACF();
	  CLEARCF();
	  F3(A);
	  incPC();
	  return TIMING[1] + (int)(cycleCounter & 1);
	}
      }
      ),

    // f7 RST6
    new Opcode("RST", "6",
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
	  PC = 0x0030;
	  return TIMING[13] + (int)(cycleCounter & 1);
	}
      }
      ),

    // f8 RM
    new Opcode("RM", "",
	       1,
	       Processor.INS_RET | Processor.INS_MR,
	       new Executable() {
	@Override
	public int exec() {
	  if (SFSET()) {
	    final int tb = memory.getByte(SP);
	    incSP();
	    PC = tb + (memory.getByte(SP) << 8);
	    incSP();
	    return TIMING[11] + (int)(cycleCounter & 1);
	  } else {
	    incPC();
	    return TIMING[10] + (int)(cycleCounter & 1);
	  }
	}
      }
      ),

    // f9 SPHL
    new Opcode("SPHL", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  SP = HL();
	  incPC();
	  return TIMING[10] + (int)(cycleCounter & 1);
	}
      }
      ),

    // fa JM
    new Opcode("JM", "", 3, Processor.INS_JMP, new Executable() {
	@Override
	public int exec() {
	  if (SFSET()) {
	    incPC();
	    final int tb = memory.getByte(PC);
	    PC = tb + (memory.getByte((PC + 1) & 0xffff) << 8);
	  } else {
	    incPC(3);
	  }
	  return TIMING[2] + (int)(cycleCounter & 1);
	}
      }
      ),

    // fb EI
    new Opcode("EI", "", 1, 0, new Executable() {
	@Override
	public int exec() {
	  IE = TID = true;
	  incPC();
	  return TIMING[0] + (int)(cycleCounter & 1);
	}
      }
      ),

    // fc CM
    new Opcode("CM", "",
	       3,
	       Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
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
	    return TIMING[12] + (int)(cycleCounter & 1);
	  } else {
	    incPC(3);
	    return TIMING[11] + (int)(cycleCounter & 1);
	  }
	}
      }
      ),

    // fd undefined (CALL)
    new Opcode("UND", "",
	       3,
	       Processor.INS_UND | Processor.INS_CALL | Processor.INS_MW,
	       new Executable() {
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
	  return TIMING[12] + (int)(cycleCounter & 1);
	}
      }
      ),

    // fe CPI
    new Opcode("CPI", "", 2, 0, new Executable() {
	@Override
	public int exec() {
	  incPC();
	  final int tb = memory.getByte(PC);
	  final int tw = A - tb;
	  if (((A & 0x0f) - (tb & 0x0f)) >= 0) {
	    SETACF();
	  } else {
	    CLEARACF();
	  }
	  if ((tw & 0x0100) != 0) {
	    SETCF();
	  } else {
	    CLEARCF();
	  }
	  F3(tw & 0xff);
	  incPC();
	  return TIMING[1] + (int)(cycleCounter & 1);
	}
      }
      ),

    // ff RST7
    new Opcode("RST", "7",
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
	  PC = 0x0038;
	  return TIMING[13] + (int)(cycleCounter & 1);
	}
      }
      )
  };

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
      } else if ((interruptPending >= 0) && IE && !TID) {
	IE = false;
	if (HALTED) {
	  HALTED = false;
	  incPC();
	}
	if ((interruptPending & 0xc7) == 0xc7) {  // RST
	  PC = interruptPending & 0x38;
	  cycleCounter += TIMING[13] + (int)(cycleCounter & 1);
	} else if ((interruptPending & 0xff) == 0xcd) {  // CALL
	  decSP();
	  memory.setByte(SP, PC >> 8);
	  decSP();
	  memory.setByte(SP, PC & 0xff);
	  PC = (interruptPending >> 8) & 0xffff;
	  cycleCounter += TIMING[12] + (int)(cycleCounter & 1);
	} else {  // something else, ignored
	  log.finer(String.format("Unsupported interrupt vector: 0x%x",
				  interruptPending));
	  cycleCounter += TIMING[0] + (int)(cycleCounter & 1);
	}
	interruptPending = -1;
      } else if (HALTED) {
	cycleCounter++;
      } else {
	TID = false;
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
      if ((cycleCounter >= endCycleCounter) ||
	  ((breakpoints != null) && breakpoints.contains(PC))) {
	break;
      }
    }
  }

  // for description see Processor
  @Override
  public void idle(final long minCycles) {
    assert minCycles >= 0;

    if (!suspended) {
      cycleCounter += minCycles;
      CPUScheduler.runSchedule(cycleCounter);
    }
  }
}
