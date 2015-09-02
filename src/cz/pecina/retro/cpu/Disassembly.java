/* Disassembly.java
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
   * @param   upperCase if {@code true}, uppercase version is generated
   * @return            mnemo code of the instruction
   */
  public abstract String getMnemo(boolean upperCase);

  /**
   * Gets mnemo code of the instruction (without parameters), using
   * the default format.
   *
   * @return mnemo code of the instruction
   */
  public String getMnemo() {
    return getMnemo(true);
  }

  /**
   * Gets parameter string of the instruction, in hex.
   *
   * @param  upperCase   if {@code true}, uppercase version is generated
   *                     (this only applies to register names, hexadecimal
   *                     values are generated according to their respective
   *                     templates supplied by the invoker)
   * @param  template1   template for one-byte hexadecimal values, e.g.,
   *                     {@code "0x%02x"}
   * @param  template2   template for two-byte hexadecimal values, e.g.,
   *                     {@code "0x%04x"}
   * @param  prependZero if {@code true}, zero is prepended to values whose
   *                     representation starts with a non-digit 
   * @return             parameter string of the instruction
   */
  public abstract String getParameters(boolean upperCase,
				       String template1,
				       String template2,
				       boolean prependZero);

  /**
   * Gets parameter string of the instruction, in hex, using the default format.
   *
   * @return parameter string of the instruction
   */
  public String getParameters() {
    return getParameters(true, "%02XH", "%04XH", true);
  }

  /**
   * Gets simplified string representation of the instruction.
   * This can be used, for instance, by debuggers requiring minimum
   * string length.
   *
   * @return simplified string representing the instruction
   */
  public abstract String getSimplified();
}
