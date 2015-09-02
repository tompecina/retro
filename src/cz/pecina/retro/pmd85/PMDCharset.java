/* PMDCharset.java
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

import java.util.Arrays;
import java.util.List;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

/**
 * The charset used by Tesla PMD 85.
 * <p>
 * It is a subset of KOI8-CS enhanced with some extra characters (five Greek
 * letters, the integral sign, selected block characters).
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class PMDCharset extends Charset {

  // static logger
  private static final Logger log =
    Logger.getLogger(PMDCharset.class.getName());

  // conversion table for characters 0xc0-0xff
  private static final List<Character> SPECIAL_CHARACTERS =
    Arrays.asList(new Character[] {
      /* 0xc0 */ '\u222b',  // integral
      /* 0xc1 */ 'á',
      /* 0xc2 */ '\u03b1',  // alpha
      /* 0xc3 */ 'č',
      /* 0xc4 */ 'ď',
      /* 0xc5 */ 'ě',
      /* 0xc6 */ 'ŕ',
      /* 0xc7 */ '\u2580',  // upper half block
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
      /* 0xd6 */ '\u03b2',  // beta
      /* 0xd7 */ 'é',
      /* 0xd8 */ 'à',
      /* 0xd9 */ 'ý',
      /* 0xda */ 'ž',
      /* 0xdb */ '\u258c',  // left half block
      /* 0xdc */ '\u2590',  // right half block
      /* 0xdd */ '\u2599',
        // quadrant upper left and lower left and lower right
      /* 0xde */ '\u259b',
        // quadrant upper left and upper right and lower left
      /* 0xdf */ '\u259c',
        // quadrant upper left and upper right and lower right
      /* 0xe0 */ '\u03c0',  // pi
      /* 0xe1 */ 'Á',
      /* 0xe2 */ '\u03b3',  // gamma
      /* 0xe3 */ 'Č',
      /* 0xe4 */ 'Ď',
      /* 0xe5 */ 'Ě',
      /* 0xe6 */ 'Ŕ',
      /* 0xe7 */ '\u2584',  // lower half block
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
      /* 0xf6 */ '\u03b4',  // delta
      /* 0xf7 */ 'É',
      /* 0xf8 */ 'À',
      /* 0xf9 */ 'Ý',
      /* 0xfa */ 'Ž',
      /* 0xfb */ '\u259d',  // quadrant upper right
      /* 0xfc */ '\u2597',  // quadrant lower right
      /* 0xfd */ '\u259f',
        // quadrant upper right and lower left and lower right
      /* 0xfe */ '\u2596',  // quadrant lower left
      /* 0xff */ '\u2598'   // quadrant upper left
  });

  /**
   * Creates a new charset.
   */
  public PMDCharset() {
    super("PMD-85", new String[] {"PMD85", "PMD_85"});
  }

  // for description see Charset
  @Override
  public boolean contains(Charset cs) {
    return cs instanceof PMDCharset;
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
      super(PMDCharset.this, 1.0f, 1.0f);
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
	    ch = '\u2588';  // full block
	  } else if ((cp >= 0xc0) && (cp <= 0xff)) {
	    ch = SPECIAL_CHARACTERS.get(cp - 0xc0);
	  } else if ((cp < 0x20) || ((cp >= 0x80) & (cp < 0xc0))) {
	    return CoderResult.unmappableForLength(1);
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
      super(PMDCharset.this, 1.0f, 1.0f);
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
	  if (ch == '\u2588') {  // full block
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
