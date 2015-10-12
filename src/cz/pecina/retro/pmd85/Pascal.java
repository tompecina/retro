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

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.MatchResult;

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

  // Pascal tokens 0x80-0xff
  private static String TOKENS[] = {
    /* 0x80 */ "END", 
    /* 0x81 */ "FOR",
    /* 0x82 */ "NEXT",
    /* 0x83 */ "DATA",
    /* 0x84 */ "INPUT",
    /* 0x85 */ "DIM",
    /* 0x86 */ "READ",
    /* 0x87 */ "LET",
    /* 0x88 */ "GOTO",
    /* 0x89 */ "RUN",
    /* 0x8a */ "IF",
    /* 0x8b */ "RESTORE",
    /* 0x8c */ "GOSUB",
    /* 0x8d */ "RETURN",
    /* 0x8e */ "REM",
    /* 0x8f */ "STOP",
    /* 0x90 */ "BIT",
    /* 0x91 */ "ON",
    /* 0x92 */ "NULL",
    /* 0x93 */ "WAIT",
    /* 0x94 */ "DEF",
    /* 0x95 */ "POKE",
    /* 0x96 */ "PRINT",
    /* 0x97 */ "ERR",
    /* 0x98 */ "LIST",
    /* 0x99 */ "CLEAR",
    /* 0x9a */ "LLIST",
    /* 0x9b */ "RAD",    // MONIT in PMD 85-1
    /* 0x9c */ "NEW",
    /* 0x9d */ "TAB(",
    /* 0x9e */ "TO",
    /* 0x9f */ "FNC",
    /* 0xa0 */ "SPC(",
    /* 0xa1 */ "THEN",
    /* 0xa2 */ "NOT",
    /* 0xa3 */ "STEP",
    /* 0xa4 */ "+",
    /* 0xa5 */ "-",
    /* 0xa6 */ "*",
    /* 0xa7 */ "/",
    /* 0xa8 */ "^",
    /* 0xa9 */ "AND",
    /* 0xaa */ "OR",
    /* 0xab */ ">",
    /* 0xac */ "=",
    /* 0xad */ "<",
    /* 0xae */ "SGN",
    /* 0xaf */ "INT",
    /* 0xb0 */ "ABS",
    /* 0xb1 */ "USR",
    /* 0xb2 */ "FRE",
    /* 0xb3 */ "INP",
    /* 0xb4 */ "POS",
    /* 0xb5 */ "SQR",
    /* 0xb6 */ "RND",
    /* 0xb7 */ "LOG",
    /* 0xb8 */ "EXP",
    /* 0xb9 */ "COS",
    /* 0xba */ "SIN",
    /* 0xbb */ "TAN",
    /* 0xbc */ "ATN",
    /* 0xbd */ "PEEK",
    /* 0xbe */ "LEN",
    /* 0xbf */ "STR$",
    /* 0xc0 */ "VAL",
    /* 0xc1 */ "ASC",
    /* 0xc2 */ "CHR$",
    /* 0xc3 */ "LEFT$",
    /* 0xc4 */ "RIGHT$",
    /* 0xc5 */ "MID$",
    /* 0xc6 */ "SCALE",
    /* 0xc7 */ "PLOT",
    /* 0xc8 */ "MOVE",
    /* 0xc9 */ "BEEP",
    /* 0xca */ "AXES",
    /* 0xcb */ "GCLEAR",
    /* 0xcc */ "PAUSE",
    /* 0xcd */ "DISP",
    /* 0xce */ "_",      // equivalent to ?
    /* 0xcf */ "BMOVE",
    /* 0xd0 */ "BPLOT",
    /* 0xd1 */ "LOAD",
    /* 0xd2 */ "SAVE",
    /* 0xd3 */ "DLOAD",
    /* 0xd4 */ "DSAVE",
    /* 0xd5 */ "LABEL",
    /* 0xd6 */ "FILL",
    /* 0xd7 */ "AUTO",
    /* 0xd8 */ "OUTPUT",
    /* 0xd9 */ "STATUS",
    /* 0xda */ "ENTER",
    /* 0xdb */ "CONTROL",
    /* 0xdc */ "CHECK",
    /* 0xdd */ "CONT",
    /* 0xde */ "OUT",
    /* 0xdf */ "INKEY",
    /* 0xe0 */ "CODE",
    /* 0xe1 */ "ROM",
    /* 0xe2 */ "APOKE",
    /* 0xe3 */ "PEN",
    /* 0xe4 */ "INK(",
    /* 0xe5 */ "APEEK",
    /* 0xe6 */ "ADR",
    /* 0xe7 */ "AT",
    /* 0xe8 */ "HEX$",
    /* 0xe9 */ "DEG",
    /* 0xea */ "WINDOW",
    /* 0xeb */ "BEN",
    /* 0xec */ null,
    /* 0xed */ null,
    /* 0xee */ null,
    /* 0xef */ null,
    /* 0xf0 */ null,
    /* 0xf1 */ null,
    /* 0xf2 */ null,
    /* 0xf3 */ null,
    /* 0xf4 */ null,
    /* 0xf5 */ null,
    /* 0xf6 */ null,
    /* 0xf7 */ null,
    /* 0xf8 */ null,
    /* 0xf9 */ null,
    /* 0xfa */ null,
    /* 0xfb */ null,
    /* 0xfc */ null,
    /* 0xfd */ null,
    /* 0xfe */ null,
    /* 0xff */ null
  };

  // REM token
  private static final int REM = 0x8e;

  // DATA token
  private static final int DATA = 0x83;

  // common ?/_ token
  private static final int QM = 0xce;
  
  // check address against end address
  private static void check(final int address,
			    final int endAddress)
                            throws PascalException {
    if (address > endAddress) {
      throw new PascalException("Outside the program area");
    }
  }

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

  // pattern for getting the line number
  private static final Pattern LINE_NUMBER_PATTERN =
    Pattern.compile("^(\\d+) *(\\D.*)$");

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
	throw new PascalException("Line tokenization failed on line " + lineNumber);
      }
      cpu.setPC(ENCODE_INS_START[version]);
      cpu.exec(Integer.MAX_VALUE, 0, breakPoints);
      if ((cpu.getF() & Intel8080A.CF) == 1) {
	throw new PascalException("Line insertion failed on line " + lineNumber);
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
