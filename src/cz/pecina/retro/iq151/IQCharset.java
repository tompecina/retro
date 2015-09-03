/* IQCharset.java
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

package cz.pecina.retro.iq151;

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
 * The standard charset used by IQ 151.  This charset is hard-coded in
 * the two basic text graphics modules, VIDEO 32 and VIDEO 64.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class IQCharset extends Charset {

  // static logger
  private static final Logger log =
    Logger.getLogger(IQCharset.class.getName());

  // conversion table for characters 0x00-0x1f
  private static final List<Character> SPECIAL_CHARACTERS =
    Arrays.asList(new Character[] {
      /* 0x00 */ '\u25cf',  // black circle
      /* 0x01 */ '\u25cb',  // white circle
      /* 0x02 */ '\u2193',  // downwards arrow
      /* 0x03 */ '\u2191',  // upwards arrow
      /* 0x04 */ '\u2192',  // downwards arrow
      /* 0x05 */ '\u2190',  // leftwards arrow
      /* 0x06 */ '\u2660',  // black spade suit
      /* 0x07 */ '\u2665',  // black heart suit
      /* 0x08 */ '\u2666',  // black diamond suit
      /* 0x09 */ '\u2663',  // black club suit
      /* 0x0a */ '\u2518',  // box drawings light up and left
      /* 0x0b */ '\u2514',  // box drawings light up and right
      /* 0x0c */ '\u2534',  // box drawings light up and horizontal
      /* 0x0d */ null,      // blank
      /* 0x0e */ '\u2524',  // box drawings light vertical and left
      /* 0x0f */ '\u253c',  // box drawings light vertical and horizontal
      /* 0x10 */ '\u250c',  // box drawings light down and right
      /* 0x11 */ '\u2500',  // box drawings light horizontal
      /* 0x12 */ '\u252c',  // box drawings light down and horizontal
      /* 0x13 */ '\u251c',  // box drawings light vertical and right
      /* 0x14 */ '\u2502',  // box drawings light vertical
      /* 0x15 */ '\u2598',  // quadrant upper left
      /* 0x16 */ '\u259d',  // quadrant upper right
      /* 0x17 */ '\u2580',  // upper half block
      /* 0x18 */ '\u2596',  // quadrant lower left
      /* 0x19 */ '\u258c',  // left half block
      /* 0x1a */ '\u259e',  // quadrant upper right and lower left
      /* 0x1b */ '\u259b',
        // quadrant upper left and upper right and lower left
      /* 0x1c */ '\u2592',  // medium shade
      /* 0x1d */ null,      // medium shade lower half block
      /* 0x1e */ null,      // medium shade upper half block
      /* 0x1f */ '\u2510'   // box drawings light down and left
  });

  /**
   * Creates a new charset.
   */
  public IQCharset() {
    super("IQ-151", new String[] {"IQ151", "IQ_151"});
  }

  // for description see Charset
  @Override
  public boolean contains(Charset cs) {
    return cs instanceof IQCharset;
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
      super(IQCharset.this, 1f, 1f);
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
	  Character ch;
	  if (cp == 0x7f) {
	    ch = '\u2590';  // right half block
	  } else if (cp == 0x0d) {
	    ch = ' ';  // blank
	  } else if (cp < 0x20) {
	    ch = SPECIAL_CHARACTERS.get(cp);
	    if (ch == null) {
	      return CoderResult.unmappableForLength(1);
	    }	      
	  } else if (cp >= 0x80) {
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
      super(IQCharset.this, 1.0f, 1.0f);
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
	  if (ch == '\u2590') {  // right half block
	    cp = 0x7f;
	  } else if (SPECIAL_CHARACTERS.contains(ch)) {
	    cp = SPECIAL_CHARACTERS.indexOf(ch);
	  } else if (((int)ch >= 0x20) && ((int)ch < 0x7f))  {
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
