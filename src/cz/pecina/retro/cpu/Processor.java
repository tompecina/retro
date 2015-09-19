/* Processor.java
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

import java.util.Set;

/**
 * CPU capable of maintaining memory, I/O space, reset condition,
 * interrupts and CPU-clock driven scheduler.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public interface Processor {

  /**
   * Mask for halting instruction.
   */
  public static final int INS_HLT = 1 << 0;

  /**
   * Mask for jump instruction.
   */
  public static final int INS_JMP = 1 << 1;

  /**
   * Mask for call instruction (includes RST).
   */
  public static final int INS_CALL = 1 << 2;

  /**
   * Mask for return instruction.
   */
  public static final int INS_RET = 1 << 3;

  /**
   * Mask for I/O read instruction.
   */
  public static final int INS_IOR = 1 << 4;

  /**
   * Mask for I/O write instruction.
   */
  public static final int INS_IOW = 1 << 5;

  /**
   * Mask for memory read instruction.
   */
  public static final int INS_MR = 1 << 6;

  /**
   * Mask for memory write instruction.
   */
  public static final int INS_MW = 1 << 7;

  /**
   * Mask for block instruction (repeated or not).
   */
  public static final int INS_BLK = 1 << 8;

  /**
   * Mask for (auto)repeated instruction.
   */
  public static final int INS_REP = 1 << 9;

  /**
   * Mask for instruction that is not officially defined by
   * the CPU manufacturer.
   */
  public static final int INS_UND = 1 << 10;

  /**
   * Adds memory.
   *
   * @param memory memory to be added
   */
  public abstract void setMemory(AbstractMemory memory);

  /**
   * Adds I/O element to an input port.
   *
   * @param port    input port number
   * @param element I/O element to be connected
   */
  public abstract void addIOInput(int port, IOElement element);

  /**
   * Removes I/O element from an input port.
   *
   * @param port    input port number
   * @param element I/O element to be disconnected
   */
  public abstract void removeIOInput(int port, IOElement element);

  /**
   * Removes all I/O elements from an input port.
   *
   * @param port input port number
   */
  public abstract void clearIOInput(int port);

  /**
   * Adds I/O element to an output port.
   *
   * @param port    output port number
   * @param element I/O element to be connected
   */
  public abstract void addIOOutput(int port, IOElement element);

  /**
   * Removes I/O element from an output port.
   *
   * @param port    output port number
   * @param element I/O element to be disconnected
   */
  public abstract void removeIOOutput(int port, IOElement element);

  /**
   * Removes all I/O elements from an output port.
   *
   * @param port output port number
   */
  public abstract void clearIOOutput(int port);

  /**
   * Returns {@code true} if interrupt is enabled.
   *
   * @return {@code true} if interrupt is enabled,
   *         {@code false} otherwise
   */
  public abstract boolean isIE();

  /**
   * Gets the program counter.
   *
   * @return program counter
   */
  public abstract int getPC();

  /**
   * Sets the program counter.
   *
   * @param n new value for the program counter
   */
  public abstract void setPC(int n);

  /**
   * Requests maskable interrupt.
   *
   * @param vector the vectoring information, mode-dependent;
   *               {@code -1} if no interrupt is requested
   */
  public default void requestInterrupt(final int vector) {
  }

  /**
   * Requests non-maskable interrupt.
   */
  public default void requestNmi() {
  }

  /**
   * Gets {@code Disassembly} object.
   *
   * @param  address address of the first byte
   * @return         {@code Disassembly} object for the instruction
   */
  public abstract Disassembly getDisassembly(int address);

  /**
   * Gets {@code Disassembly} object.
   *
   * @param  bytes   byte array to be disassembled
   * @param  address address of the first byte
   * @return         {@code Disassembly} object for the instruction
   */
  public abstract Disassembly getDisassembly(byte[] bytes, int address);

  /**
   * Stops the element for maintenance tasks such as snapshot loading.
   */
  public abstract void suspend();

  /**
   * Resumes the operation of the element.
   */
  public abstract void resume();

  /**
   * Returns {@code true} if the element is suspended.
   *
   * @return {@code true} if the element is suspended
   */
  public abstract boolean isSuspended();

  /**
   * Executes series of instructions.
   *
   * @param minCycles   minimum number of program cycles to execute
   * @param mask        execution stops if the instruction matches mask
   * @param breakpoints set of breakpoints (may be {@code null})
   */
  public abstract void exec(final long minCycles,
			    final int mask,
			    final Set<Integer> breakpoints);

  /**
   * Executes one instruction or a divisible part of it.
   */
  public abstract void exec();
}
