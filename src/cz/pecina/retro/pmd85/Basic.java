/* Basic.java
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

import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;

import java.nio.ByteBuffer;

import java.nio.charset.Charset;

import cz.pecina.retro.common.Parameters;

/**
 * BASIG-G programs utilities.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Basic {

  // static logger
  private static final Logger log =
    Logger.getLogger(Basic.class.getName());

  // BASIC-G tokens 0x80-0xff
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
    /* 0xea */ null,
    /* 0xeb */ null,
    /* 0xec */ null,
    /* 0xed */ "WINDOW",
    /* 0xee */ "REN",
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
                            throws BasicException {
    if (address > endAddress) {
      throw new BasicException("Outside the program area");
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
    
  // check if BASIC-G interpreter is loaded
  private static boolean isLoaded(final byte[] ram) {
    final boolean r = (ram[0] == (byte)0x21) && (ram[1] == (byte)0x82);
    log.finer("Basic " + (r ? "" : "not ") + "loaded");
    return r;
  }
  
  /**
   * Encodes BASIC-G program in text to RAM.
   *
   * @param  reader         the program as text
   * @param  ram            the RAM
   * @param  startAddress   the starting address
   * @param  endAddress     the ending address beyond which the program 
   *                        may not be written
   * @throws IOException    on I/O exception
   * @throws BasicException on error during processing
   */
  public static void encode(final BufferedReader reader,
			    final byte[] ram,
			    final int startAddress,
			    final int endAddress)
                            throws IOException,
				   BasicException {
    log.fine("Encoding started");
    assert reader != null;
    assert ram != null;
    assert (startAddress >= 0) && (startAddress < ram.length);
    assert (endAddress > startAddress) && (endAddress < ram.length);
    if (!isLoaded(ram)) {
      throw new BasicException("BASIC-G not loaded");
    }
    String line;
    int a = startAddress, nextLineAddress;
    while ((line = reader.readLine()) != null) {
      log.finer("Parsing line: " + line);
      line = line.trim();
      if (line.isEmpty()) {
	continue;
      }
      final Matcher matcher = LINE_NUMBER_PATTERN.matcher(line);
      if (!matcher.matches()) {
	throw new BasicException("Line number missing");
      }
      final MatchResult matchResult = matcher.toMatchResult();
      final int lineNumber = Integer.parseInt(matchResult.group(1));
      log.finest("Line number: " + lineNumber);
      if (lineNumber > 0xffff) {
	throw new BasicException("Illegal line number");
      }
      line = matchResult.group(2);
      nextLineAddress = a;
      a += 2;
      check(a + 1, endAddress);
      ram[a++] = (byte)(lineNumber & 0xff);
      ram[a++] = (byte)(lineNumber >> 8);
      boolean inRem = false;
      boolean inString = false;
      boolean inData = false;
      while (!line.isEmpty()) {
	final char ch = line.charAt(0);
	int nextByte = 0;
	if (inRem) {
	  nextByte = encodeChar(ch);
	  line = line.substring(1);
	} else if (inString) {
	  if (ch == '"') {
	    inString = false;
	  }
	  nextByte = encodeChar(ch);
	  line = line.substring(1);
	} else if (ch == '"') {
	  inString = true;
	  nextByte = encodeChar(ch);
	  line = line.substring(1);
	} else if (ch == '?') {
	  nextByte = QM;
	  line = line.substring(1);
	} else if (line.startsWith("DATA")) {
	  inData = true;
	  nextByte = DATA;
	  line = line.substring(4);
	} else if (ch == ':') {
	  inData = false;
	  nextByte = (int)ch;	    
	  line = line.substring(1);
	} else {
	  int i = 0x80;
	  if (!inData) {
	    for (i = 0; i < 0x80; i++) {
	      final String token = TOKENS[i];
	      if (token != null) {
		if (line.startsWith(token)) {
		  nextByte = 0x80 + i;
		  line = line.substring(token.length());
		  if (nextByte == REM) {
		    inRem = true;
		  }
		  break;
		}
	      }
	    }
	  }
	  if (i == 0x80) {
	    nextByte = encodeChar(ch);
	    line = line.substring(1);
	  }
	}
	check(a, endAddress);
	ram[a++] = (byte)nextByte;
      }
      check(a, endAddress);
      ram[a++] = (byte)0;
      ram[nextLineAddress++] = (byte)(a & 0xff);
      ram[nextLineAddress] = (byte)(a >> 8);
    }
    check(a + 2, endAddress);
    ram[a] = ram[a + 1] = ram[a + 2] = (byte)0;
    a += 3;

    log.fine("Encoding finished, resetting variables");

    final int runAddress = a;
    final int tempAddress = a + 6;
    
    check(a + 10, endAddress);
    ram[a++] = (byte)0xf5;  // PUSH PSW
    ram[a++] = (byte)0xcd;  // CALL tempAddress
    ram[a++] = (byte)(tempAddress & 0xff);
    ram[a++] = (byte)(tempAddress >> 8);
    ram[a++] = (byte)0xf1;  // POP PSW
    final int breakPoint = a;
    ram[a++] = (byte)0x00;  // NOP
    
    // tempAddress
    ram[a++] = (byte)0xe5;  // PUSH H
    ram[a++] = (byte)0xc3;  // JMP 0x1e04
    ram[a++] = (byte)0x04;
    ram[a++] = (byte)0x1e;

    final int pc = Parameters.cpu.getPC();
    Parameters.cpu.setPC(runAddress);
    Parameters.cpu.resume();
    Parameters.cpu.exec(Integer.MAX_VALUE,
			0,
			new ArrayList<Integer>() {{add(breakPoint);}});
    Parameters.cpu.suspend();
    Parameters.cpu.setPC(pc);
			
    log.fine("Variables reset");
  }

  /**
   * Decodes BASIC-G program in RAM to text.
   *
   * @param  ram            the RAM
   * @param  writer         the program as text
   * @param  startAddress   the starting address
   * @param  endAddress     the ending address beyond which the program 
   *                        may not be read
   * @throws IOException    on I/O exception
   * @throws BasicException on error during processing
   */
  public static void decode(final byte[] ram,
			    final PrintWriter writer,
			    final int startAddress,
			    final int endAddress)
                            throws IOException,
				   BasicException {
    log.fine("Decoding started");
    assert writer != null;
    assert ram != null;
    assert (startAddress >= 0) && (startAddress < ram.length);
    assert (endAddress > startAddress) && (endAddress < ram.length);
    if (!isLoaded(ram)) {
      throw new BasicException("BASIC-G not loaded");
    }
    int a = startAddress;
    while (true) {
      check(a + 1, endAddress);
      final int nextAddress = (ram[a] & 0xff) + ((ram[a + 1] & 0xff) << 8);
      if (nextAddress == 0) {
	break;
      }
      a += 2;
      if (nextAddress < (a + 3)) {
	throw new BasicException("Illegal next line pointer");
      }
      check(nextAddress, endAddress);
      final int lineNumber = (ram[a] & 0xff) + ((ram[a + 1] & 0xff) << 8);
      if (lineNumber > 0xffff) {
	throw new BasicException("Illegal line number");
      }
      a += 2;
      writer.printf("%d ", lineNumber);
      boolean inRem = false;
      boolean inString = false;
      String s;
      while (a < (nextAddress - 1)) {
	final int nextByte = ram[a++] & 0xff;
	if (nextByte == 0) {
	  throw new BasicException("Premature end-of-line marker");
	} else if (inRem) {
	  s = decodeChar(nextByte);
	} else if (inString) {
	  if (nextByte == (int)'"') {
	    inString = false;
	  }
	  s = decodeChar(nextByte);
	} else if (nextByte == REM) {
	  inRem = true;
	  s = "REM";
	} else if (nextByte == (int)'"') {
	  inString = true;
	  s = "\"";
	} else if ((nextByte >= 0x80) &&
		   (TOKENS[nextByte - 0x80] != null)) {
	  s = TOKENS[nextByte - 0x80];
	} else {
	  s = decodeChar(nextByte);
	}
	writer.print(s);	  
      }
      if (ram[a++] != (byte)0) {
	throw new BasicException("Bad line terminator");
      }
      writer.printf("%n");
    }
    log.fine("Decoding finished");
  }
}
