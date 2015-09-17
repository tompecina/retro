/* OndraCharset.java
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

import java.util.Arrays;
import java.util.List;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

/**
 * The charset used by Tesla Ondra SPO 186.
 * <p>
 * It is a modification of KOI8-CS, with several characters missing and some extra
 * characters (diacritical marks) added).
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class OndraCharset extends Charset {

  // static logger
  private static final Logger log =
    Logger.getLogger(OndraCharset.class.getName());

  // conversion table for characters 0xc0-0xff
  private static final List<Character> SPECIAL_CHARACTERS =
    Arrays.asList(new Character[] {
      /* 0xc0 */ '\u0301',  // accute
      /* 0xc1 */ 'á',
      /* 0xc2 */ 'ĉ',
      /* 0xc3 */ 'č',
      /* 0xc4 */ 'ď',
      /* 0xc5 */ 'ě',
      /* 0xc6 */ 'ŕ',
      /* 0xc7 */ 'ĥ',
      /* 0xc8 */ 'ü',
      /* 0xc9 */ 'í',
      /* 0xca */ 'ů',
      /* 0xcb */ 'ĺ',
      /* 0xcc */ 'ľ',
      /* 0xcd */ 'ö',
      /* 0xce */ 'ň',
      /* 0xcf */ 'ó',
      /* 0xd0 */ 'ô',
      /* 0xd1 */ 'ä',
      /* 0xd2 */ 'ř',
      /* 0xd3 */ 'š',
      /* 0xd4 */ 'ť',
      /* 0xd5 */ 'ú',
      /* 0xd6 */ 'ǔ',
      /* 0xd7 */ 'é',
      /* 0xd8 */ 'à',
      /* 0xd9 */ 'ý',
      /* 0xda */ 'ž',
      /* 0xdb */ 'ĝ',
      /* 0xdc */ '\u030c',  // caron
      /* 0xdd */ 'ĵ',
      /* 0xde */ '\u030a',  // ring
      /* 0xdf */ 'ŝ',
      /* 0xe0 */ '\u0301',  // accute
      /* 0xe1 */ 'Á',
      /* 0xe2 */ 'Ĉ',
      /* 0xe3 */ 'Č',
      /* 0xe4 */ 'Ď',
      /* 0xe5 */ 'Ě',
      /* 0xe6 */ 'Ŕ',
      /* 0xe7 */ 'Ĥ',
      /* 0xe8 */ 'Ü',
      /* 0xe9 */ 'Í',
      /* 0xea */ 'Ů',
      /* 0xeb */ 'Ĺ',
      /* 0xec */ 'Ľ',
      /* 0xed */ 'Ö',
      /* 0xee */ 'Ň',
      /* 0xef */ 'Ó',
      /* 0xf0 */ 'Ô',
      /* 0xf1 */ 'Ä',
      /* 0xf2 */ 'Ř',
      /* 0xf3 */ 'Š',
      /* 0xf4 */ 'Ť',
      /* 0xf5 */ 'Ú',
      /* 0xf6 */ 'Ǔ',
      /* 0xf7 */ 'É',
      /* 0xf8 */ 'À',
      /* 0xf9 */ 'Ý',
      /* 0xfa */ 'Ž',
      /* 0xfb */ 'Ĝ',
      /* 0xfc */ '\u030c',  // caron
      /* 0xfd */ 'Ĵ',
      /* 0xfe */ '\u030a',  // ring
      /* 0xff */ 'Ŝ'
  });

  /**
   * Creates a new charset.
   */
  public OndraCharset() {
    super("Ondra", new String[] {"ONDRA"});
  }

  // for description see Charset
  @Override
  public boolean contains(Charset cs) {
    return cs instanceof OndraCharset;
  }

  // for description see Charset
  @Override
  public CharsetDecoder newDecoder() {
    return new Decoder();
  }
  
  // for description see Charset
  @Override
  public CharsetEncoder newEncoder() {
    return new Encoder();
  }
  
  // decoder
  private class Decoder extends CharsetDecoder {

    // main constructor
    public Decoder() {
      super(OndraCharset.this, 1f, 1f);
    }

    // for description see CharsetDecoder
    @Override
    public CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
      log.finer("Decoder called");
      int mark = in.position();
      try {
	while (in.hasRemaining()) {
	  final int cp = in.get() & 0xff; 
	  log.finest("Replacing " + cp);
	  char ch;
	  if (cp == 0x7f) {
	    ch = '\u2592';  // medium shade
	  } else if ((cp >= 0xc0) && (cp <= 0xff)) {
	    ch = SPECIAL_CHARACTERS.get(cp - 0xc0);
	  } else if ((cp < 0x20) || ((cp >= 0x80) & (cp < 0xc0))) {
	    return CoderResult.malformedForLength(1);
	  } else {
	    ch = (char)cp;
	  }
	  log.finest("Replacing with '" + ch + "'");
	  if (!out.hasRemaining()) {
	    return CoderResult.OVERFLOW;
	  }
	  out.put(ch);
	  mark++;
	}
	log.finest("Decoder succeeded");
	return CoderResult.UNDERFLOW;
      } finally {
	in.position(mark);
      }
    }
  }
  
  // encoder
  private class Encoder extends CharsetEncoder {

    // main constructor
    public Encoder() {
      super(OndraCharset.this, 1.0f, 1.0f);
    }

    // for description see CharsetEncoder
    @Override
    public CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
      log.finer("Encoder called");
      int mark = in.position();
      try {
	while (in.hasRemaining()) {
	  char ch = in.get();
	  log.finest("Replacing '" + ch + "'");
	  int cp;
	  if (ch == '\u2592') {  // medium shade
	    cp = 0x7f;
	  } else if (SPECIAL_CHARACTERS.contains(ch)) {
	    cp = SPECIAL_CHARACTERS.indexOf(ch) + 0xc0;
	  } else if ((int)ch <= 0xff) {
	    cp = (int)ch;
	  } else {
	    return CoderResult.unmappableForLength(1);
	  }
	  log.finest("Replacing with: " + cp);
	  if (!out.hasRemaining()) {
	    return CoderResult.OVERFLOW;
	  }
	  out.put((byte)cp);
	  mark++;
	}
	log.finest("Encoder succeeded");
	return CoderResult.UNDERFLOW;
      } finally {
	out.position(mark);
      }
    }
  }
}
