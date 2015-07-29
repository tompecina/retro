/* Opcode.java
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

/**
 * A CPU instruction.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Opcode {

  // static logger
  private static final Logger log =
    Logger.getLogger(Opcode.class.getName());

  // string representation of the instruction code
  private String mnemo;

  // template for the instruction parameters
  private String parameters;

  // number of bytes the instruction takes up in memory
  private int length;

  // type of the instruction as defined in Processor
  private int type;

  // the Executable object of the instruction
  private Executable exec;

  /**
   * Creates a new CPU instruction.
   *
   * @param mnemo      the string representation of the instruction code
   * @param parameters the template for the instruction parameters
   * @param length     the number of bytes the instruction takes up in memory
   * @param type       the type of the instruction as defined
   *                   in {@link Processor}
   * @param exec       the {@link Executable} object of the instruction
   */
  public Opcode(final String mnemo,
		final String parameters,
		final int length,
		final int type,
		final Executable exec) {
    this.mnemo = mnemo;
    this.parameters = parameters;
    this.length = length;
    this.type = type;
    this.exec = exec;
    log.fine("New Opcode object created, mnemo: " + mnemo +
	     ", parameers: " + parameters + ", length: " + length +
	     ", type: " + type);
  };

  /**
   * Gets the string representation of the instruction code.
   *
   * @return the string representation of the instruction code
   */
  public String getMnemo() {
    return mnemo;
  }

  /**
   * Gets the template for the instruction parameters.
   *
   * @return the template for the instruction parameters
   */
  public String getParameters() {
    return parameters;
  }

  /**
   * Gets the number of bytes the instruction takes up in memory
   *
   * @return the number of bytes the instruction takes up in memory
   */
  public int getLength() {
    return length;
  }

  /**
   * Gets the type of the instruction as defined in {@link Processor}.
   *
   * @return the type of the instruction as defined in {@link Processor}
   */
  public int getType() {
    return type;
  }

  /**
   * Executes the instruction.
   *
   * @return the duration of the instruction in system clock units
   */
  int exec() {
    final int duration = exec.exec();
    if (log.isLoggable(Level.FINEST))
      log.finest("Instruction executed, mnemo: " + mnemo +
		 ", parameers: " + parameters + ", duration: " + duration);
    return duration;
  }
}
