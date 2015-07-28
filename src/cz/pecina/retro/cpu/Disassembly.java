/* Disassembly.java
 *
 * Copyright (C) 2014-2015, Tomáš Pecina <tomas@pecina.cz>
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

/**
 * Object representing disassembled instruction.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public abstract class Disassembly {

  /**
   * Byte array containing the instruction.
   */
  protected int[] bytes;

  /**
   * Gets byte array containing the instruction.
   *
   * @return byte array containing the instruction
   */
  public int[] getBytes() {
    return bytes;
  }

  /**
   * Gets length of the instruction in bytes.
   *
   * @return length of the instruction in bytes
   */
  public int getLength() {
    return bytes.length;
  }

  /**
   * Gets length of instruction prefix.
   *
   * @return length of instruction prefix
   */
  public abstract int getPrefixLength();

  /**
   * Gets mnemo code of the instruction (without parameters).
   *
   * @return mnemo code of the instruction
   */
  public abstract String getMnemo();

  /**
   * Gets parameter string of the instruction, in hex.
   *
   * @return parameter string of the instruction
   */
  public abstract String getParameters();

  /**
   * Gets simplified string representation of the instruction.
   * This can be used, for instance, by debuggers requiring minimum
   * string length.
   *
   * @return simplified string representing the instruction
   */
  public abstract String getSimplified();
}
