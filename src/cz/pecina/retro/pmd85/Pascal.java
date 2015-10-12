/* Pascal.java
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

import java.util.List;
import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;

import java.nio.ByteBuffer;

import java.nio.charset.Charset;

import cz.pecina.retro.common.ASCII;
import cz.pecina.retro.common.Parameters;

import cz.pecina.retro.cpu.Intel8080A;

/**
 * Pascal programs utilities.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Pascal {

  // static logger
  private static final Logger log =
    Logger.getLogger(Pascal.class.getName());

  // PMD 85 charset
  private static final Charset CS = new PMDCharset();
  
  // encode one character
  private static byte encodeChar(final Character ch) {
    return CS.encode(ch.toString()).get();
  }

  // decode one byte
  private static String decodeChar(final int b) {
    return CS.decode(ByteBuffer.wrap(new byte[] {(byte)b})).toString();
  }

  // detect Pascal version (-1 = no Pascal, 0 = V1.02, 1 = V2.2)
  private static int getPascalVersion(final byte[] ram) {
    int r = -1;
    if ((ram[0x7965] == (byte)0x50) && (ram[0x7966] == (byte)0x41)) {
      r = 0;
    } else if ((ram[0x0b24] == (byte)0x43) && (ram[0x0b25] == (byte)0x48)) {
      r = 1;
    }
    log.finer("Pascal version detected: " + r);
    return r;
  }
  
  // version-dependent addresses
  private static final int ENCODE_NEW[] = {0x7d0a, 0x0621};
  private static final int ENCODE_BUFFER[] = {0x02d4, 0x3b04};
  private static final int ENCODE_PREP_START[] = {0x7b91, 0x3160};
  private static final int ENCODE_PREP_STOP[] = {0x7ba3, 0x3172};
  private static final int ENCODE_TOK_START[] = {0x7bc0, 0x318f};
  private static final int ENCODE_TOK_STOP[] = {0x7bc6, 0x3195};
  private static final int ENCODE_INS_START[] = {0x7bc7, 0x3196};
  private static final int ENCODE_INS_STOP[] = {0x7bd4, 0x31a3};
  private static final int DECODE_CALLS[] = {0x7ac3, 0x3094};
  private static final int DECODE_CALLBACK_ADDRESSES[] = {0x7a2f, 0x2ffa};
  private static final int DECODE_CALLBACKS[] = {0x7a2d, 0x2ff8};
  
  /**
   * Encodes Pascal program in text to RAM.
   *
   * @param  reader          the program as text
   * @param  ram             the RAM
   * @throws IOException     on I/O exception
   * @throws PascalException on error during processing
   */
  public static void encode(final BufferedReader reader,
			    final byte[] ram)
                            throws IOException,
				   PascalException {
    log.fine("Encoding started");
    assert reader != null;
    assert ram != null;
    final int version = getPascalVersion(ram);
    if (version < 0) {
      throw new PascalException("Pascal not loaded");
    }
    final Intel8080A cpu = (Intel8080A)Parameters.cpu;
    final int pc = cpu.getPC();
    final int a = cpu.getA();
    final int f = cpu.getF();
    final int bc = cpu.getBC();
    final int de = cpu.getDE();
    final int hl = cpu.getHL();
    final List<Integer> breakPoints = new ArrayList<>();
    breakPoints.add(ENCODE_NEW[version] + 3);
    breakPoints.add(ENCODE_PREP_STOP[version]);
    breakPoints.add(ENCODE_TOK_STOP[version]);
    breakPoints.add(ENCODE_INS_STOP[version]);
    cpu.resume();
    cpu.setPC(ENCODE_NEW[version]);
    cpu.exec(Integer.MAX_VALUE, 0, breakPoints);
    log.finer("Program erased");
    ram[0xc439] = (byte)1;
    cpu.setPC(ENCODE_PREP_START[version]);
    cpu.exec(Integer.MAX_VALUE, 0, breakPoints);
    String line;
    int lineNumber = 0;
    while ((line = reader.readLine()) != null) {
      lineNumber++;
      log.finer("Parsing line " + lineNumber + ": " + line);
      int addr = ENCODE_BUFFER[version];
      for (int i = 0; i < line.length(); i++) { 
	ram[addr++] = encodeChar(line.charAt(i));
      }
      ram[addr] = 0;
      cpu.setPC(ENCODE_TOK_START[version]);
      cpu.exec(Integer.MAX_VALUE, 0, breakPoints);
      if ((cpu.getF() & Intel8080A.CF) == 1) {
	throw new PascalException("Line tokenization failed on line " +
				  lineNumber);
      }
      cpu.setPC(ENCODE_INS_START[version]);
      cpu.exec(Integer.MAX_VALUE, 0, breakPoints);
      if ((cpu.getF() & Intel8080A.CF) == 1) {
	throw new PascalException("Line insertion failed on line " +
				  lineNumber);
      }
    }
    cpu.suspend();
    cpu.setPC(pc);
    cpu.setA(a);
    cpu.setF(f);
    cpu.setBC(bc);
    cpu.setDE(de);
    cpu.setHL(hl);
    log.fine("Encoding finished");
  }

  /**
   * Decodes Pascal program in RAM to text.
   *
   * @param  ram             the RAM
   * @param  writer          the program as text
   * @throws IOException     on I/O exception
   * @throws PascalException on error during processing
   */
  public static void decode(final byte[] ram,
			    final PrintWriter writer)
                            throws IOException,
				   PascalException {
    log.fine("Decoding started");
    assert writer != null;
    assert ram != null;
    final int version = getPascalVersion(ram);
    if (version < 0) {
      throw new PascalException("Pascal not loaded");
    }
    final Intel8080A cpu = (Intel8080A)Parameters.cpu;
    final int pc = cpu.getPC();
    final int a = cpu.getA();
    final int f = cpu.getF();
    final int bc = cpu.getBC();
    final int de = cpu.getDE();
    final int hl = cpu.getHL();
    final List<Integer> breakPoints = new ArrayList<>();
    breakPoints.add(DECODE_CALLS[version] + 3);
    breakPoints.add(DECODE_CALLBACKS[version]);
    cpu.setPC(DECODE_CALLS[version]);
    cpu.setHL(DECODE_CALLBACKS[version]);
    cpu.resume();
    for(;;) {
      cpu.exec(Integer.MAX_VALUE, 0, breakPoints);
      if (cpu.getPC() != (DECODE_CALLBACKS[version])) {
	break;
      }
      final int ch = cpu.getA();
      if (ch == ASCII.CR) {
	writer.println();
      } else if (ch != ASCII.LF) {
	writer.write(decodeChar(ch));
      }
    }
    cpu.suspend();
    cpu.setPC(pc);
    cpu.setA(a);
    cpu.setF(f);
    cpu.setBC(bc);
    cpu.setDE(de);
    cpu.setHL(hl);
    log.fine("Decoding finished");
  }
}
