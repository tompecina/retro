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

package cz.pecina.retro.ondra;

import java.util.logging.Logger;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.MatchResult;

import java.util.HashSet;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;

import java.nio.ByteBuffer;

import java.nio.charset.Charset;

import cz.pecina.retro.common.Parameters;

/**
 * BASIG programs utilities.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Basic {

  // static logger
  private static final Logger log =
    Logger.getLogger(Basic.class.getName());

  // BASIC tokens 0x80-0xff
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
    /* 0x8e */ "FUNC",
    /* 0x8f */ "REM",
    /* 0x90 */ "!",
    /* 0x91 */ "STOP",
    /* 0x92 */ "OUT",
    /* 0x93 */ "ON",
    /* 0x94 */ "WAIT",
    /* 0x95 */ "TR",
    /* 0x96 */ "POKE",
    /* 0x97 */ "PRINT",  // equivalent to '?'
    /* 0x98 */ "DEF",
    /* 0x99 */ "CONT",
    /* 0x9a */ "LIST",
    /* 0x9b */ "@",
    /* 0x9c */ "CLEAR",
    /* 0x9d */ "LOAD",
    /* 0x9e */ "SAVE",
    /* 0x9f */ "NEW",
    /* 0xa0 */ "BYE",
    /* 0xa1 */ "OFF",
    /* 0xa2 */ "CLS",
    /* 0xa3 */ "LLIST",
    /* 0xa4 */ "UNTIL",
    /* 0xa5 */ "WHILE",
    /* 0xa6 */ "DO",
    /* 0xa7 */ "REPEAT",
    /* 0xa8 */ "WEND",
    /* 0xa9 */ "BEND",
    /* 0xaa */ "BEG",
    /* 0xab */ "DELETE",
    /* 0xac */ "CODE",
    /* 0xad */ "AUTO",
    /* 0xae */ "RENUM",
    /* 0xaf */ "SEEK",
    /* 0xb0 */ "INTRP",
    /* 0xb1 */ "DEG",
    /* 0xb2 */ "OPEN",
    /* 0xb3 */ "FIND",
    /* 0xb4 */ "CLOSE",
    /* 0xb5 */ "CURS",
    /* 0xb6 */ "PROC",
    /* 0xb7 */ "MODE",
    /* 0xb8 */ "WND",
    /* 0xb9 */ "FAST",
    /* 0xba */ "BEEP",
    /* 0xbb */ "PUT",
    /* 0xbc */ "VAR",
    /* 0xbd */ "SCALE",
    /* 0xbe */ "AXES",
    /* 0xbf */ "PLOT",
    /* 0xc0 */ "MOVE",
    /* 0xc1 */ "LABEL",
    /* 0xc2 */ "FILL",
    /* 0xc3 */ "COLOR",
    /* 0xc4 */ "DRAW",
    /* 0xc5 */ "TAB(",
    /* 0xc6 */ "TO",
    /* 0xc7 */ "SPC(",
    /* 0xc8 */ "&",
    /* 0xc9 */ "FN",
    /* 0xca */ "THEN",
    /* 0xcb */ "ELSE",
    /* 0xcc */ "USING",
    /* 0xcd */ "NOT",
    /* 0xce */ "STEP",
    /* 0xcf */ "+",
    /* 0xd0 */ "-",
    /* 0xd1 */ "*",
    /* 0xd2 */ "/",
    /* 0xd3 */ "^",
    /* 0xd4 */ "AND",
    /* 0xd5 */ "OR",
    /* 0xd6 */ "XOR",
    /* 0xd7 */ ">",
    /* 0xd8 */ "=",
    /* 0xd9 */ ">",
    /* 0xda */ "POS",
    /* 0xdb */ "SGN",
    /* 0xdc */ "INT",
    /* 0xdd */ "ABS",
    /* 0xde */ "INP",
    /* 0xdf */ "SQR",
    /* 0xe0 */ "RND",
    /* 0xe1 */ "LOG",
    /* 0xe2 */ "EXP",
    /* 0xe3 */ "COS",
    /* 0xe4 */ "SIN",
    /* 0xe5 */ "TAN",
    /* 0xe6 */ "ANT",
    /* 0xe7 */ "PEEK",
    /* 0xe8 */ "FRE",
    /* 0xe9 */ "LEN",
    /* 0xea */ "STR$",
    /* 0xeb */ "VAL",
    /* 0xec */ "ASC",
    /* 0xed */ "CHR$",
    /* 0xee */ "LEFT$",
    /* 0xef */ "RIGHT$",
    /* 0xf0 */ "MID$",
    /* 0xf1 */ "HEX",
    /* 0xf2 */ "USR",
    /* 0xf3 */ "CALL",
    /* 0xf4 */ "PTR",
    /* 0xf5 */ "INKEX",
    /* 0xf6 */ "GET",
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
  private static final int REM = 0x8f;

  // ! token
  private static final int EXCL = 0x90;

  // DATA token
  private static final int DATA = 0x83;

  // PRINT token - '?' is automatically converted to PRINT
  private static final int PRINT = 0x97;

  // DEL character (charset page switching)
  private static final byte DEL = (byte)0x7f;

  // the warm start address
  private static final int WARM_START = 0x1003;

  // the pointers in RAM
  private static final int START_ADDRESS = 0x4000;
  private static final int END_ADDRESS = 0x4002;
  private static final int RAM_END = 0x4092;
  
  // check address against end address
  private static void check(final int address,
			    final int endAddress)
                            throws BasicException {
    if (address > endAddress) {
      throw new BasicException("Outside the program area");
    }
  }

  // Ondra charset
  private static final Charset CS = new OndraCharset();
  
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
    Pattern.compile("^(\\d+)*(\\D.*)$");
    
  /**
   * Encodes BASIC program in text to RAM.
   *
   * @param  reader         the program as text
   * @param  ram            the RAM
   * @throws IOException    on I/O exception
   * @throws BasicException on error during processing
   */
  public static void encode(final BufferedReader reader,
			    final byte[] ram)
                            throws IOException,
				   BasicException {
    log.fine("Encoding started");
    assert reader != null;
    assert ram != null;
    String line;
    int a = (ram[START_ADDRESS] & 0xff) + ((ram[START_ADDRESS + 1] & 0xff) << 8);
    final int endAddress = (ram[RAM_END] & 0xff) + ((ram[RAM_END + 1] & 0xff) << 8);
    int nextLineAddress;
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
      if (lineNumber > 64999) {
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
	  nextByte = PRINT;
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
	    final int cp = (int)encodeChar(ch);
	    if ((cp >= 0x20) & (cp < 0x7f)) {
	      nextByte = cp;
	    } else if ((cp >= 0xc0) && (cp <= 0xff)) {
	      check(a, endAddress);
	      ram[a++] = DEL;
	      nextByte = cp - 0x80;
	    }
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
    a += 2;

    log.fine("Encoding finished, resetting pointers and variables");
    ram[END_ADDRESS] = (byte)(a & 0xff);
    ram[END_ADDRESS + 1] = (byte)(a >> 8);

    Parameters.cpu.setPC(WARM_START);
    
    log.fine("Pointers and variables reset");
  }

  /**
   * Decodes BASIC program in RAM to text.
   *
   * @param  ram            the RAM
   * @param  writer         the program as text
   * @throws IOException    on I/O exception
   * @throws BasicException on error during processing
   */
  public static void decode(final byte[] ram,
			    final PrintWriter writer)
                            throws IOException,
				   BasicException {
    log.fine("Decoding started");
    assert writer != null;
    assert ram != null;
    int a = (ram[START_ADDRESS] & 0xff) + ((ram[START_ADDRESS + 1] & 0xff) << 8);
    final int endAddress = (ram[RAM_END] & 0xff) + ((ram[RAM_END + 1] & 0xff) << 8);
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
      writer.printf("%d", lineNumber);
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
	} else if (nextByte == EXCL) {
	  inRem = true;
	  s = "!";
	} else if (nextByte == (int)'"') {
	  inString = true;
	  s = "\"";
	} else if ((nextByte >= 0x80) &&
		   (TOKENS[nextByte - 0x80] != null)) {
	  s = TOKENS[nextByte - 0x80];
	} else if (nextByte == 0x7f) {
	  s = decodeChar((ram[++a] & 0xff) + 0x80);
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
