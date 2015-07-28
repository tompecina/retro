/* TerminalFont.java
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

package cz.pecina.retro.pmi80;

import java.io.FileInputStream;
import java.awt.Graphics;
import java.awt.Color;

public class TerminalFont {
  public static final int NUMBER_GLYPHS = 16 * 18;
  public static final int NUMBER_CHARSETS = 14;
  public static final int CHARSET_SIZE = 0x5e;
  public static final int CHARSET_OFFSET = 0x21;
  public static final int BOX = 0xff;
  public static final int CHARSET_ASCII = 0;
  public static final int CHARSET_SUPPLEMENTAL = 1;
  public static final int CHARSET_GRAPHICS = 2;
  public static final int CHARSET_BRITISH = 3;
  public static final int CHARSET_DUTCH = 4;
  public static final int CHARSET_FINNISH = 5;
  public static final int CHARSET_FRENCH = 6;
  public static final int CHARSET_FRENCH_CANADIAN = 7;
  public static final int CHARSET_GERMAN = 8;
  public static final int CHARSET_ITALIAN = 9;
  public static final int CHARSET_NORWEGIAN_DANISH = 10;
  public static final int CHARSET_SPANISH = 11;
  public static final int CHARSET_SWEDISH = 12;
  public static final int CHARSET_SWISS = 13;

  private static boolean glyphsLoaded;
  private static byte[][] fontMatrix = new byte[NUMBER_GLYPHS][10];
  public static int[][] charsetMapping = new int[NUMBER_CHARSETS][CHARSET_SIZE];

  public static void paintGlyph(Graphics canvas,
				int code,
				boolean condensed,
				Color foregroundColor,
				Color backgroundColor,
				int positionX,
				int positionY,
				int pixelWidth,
				int pixelHeight,
				boolean reverse,
				boolean bold,
				boolean underscored,
				boolean doubleWidth,
				boolean doubleHeight,
				boolean rightHalf,
				boolean bottomHalf) {
    int i, j, mask, maskLine, bit, width;
    if ((code >= 0) && (code < NUMBER_GLYPHS)) {
      if (!glyphsLoaded) {
	try (FileInputStream inFile =
	     new FileInputStream(Constants.RES_PREFIX + "misc/vtfont.bin")) {
	  for (i = 0; i < NUMBER_GLYPHS; i++) {
	    inFile.read(fontMatrix[i]);
	  }
	} catch (Exception exception) {
	}
	for (i = 0; i < NUMBER_CHARSETS; i++) {
	  for (j = 0; j < CHARSET_SIZE; j++) {
	    charsetMapping[i][j] =
	      j + CHARSET_OFFSET + ((i == CHARSET_SUPPLEMENTAL) ? 0x80 : 0);
	  }
	}
	for (j = 0x5f; j < 0x7f; j++) {
	  charsetMapping[CHARSET_GRAPHICS][j - CHARSET_OFFSET] = j;
	}
	charsetMapping[CHARSET_BRITISH][0x23 - CHARSET_OFFSET] =
	  0x1e;  // pound sign (£)
	charsetMapping[CHARSET_DUTCH][0x23 - CHARSET_OFFSET] =
	  0x1e;  // pound sign (£)
	charsetMapping[CHARSET_DUTCH][0x40 - CHARSET_OFFSET] =
	  BOX;  // 3/4 (n/a)
	charsetMapping[CHARSET_DUTCH][0x5b - CHARSET_OFFSET] =
	  BOX;  // ij (n/a)
	charsetMapping[CHARSET_DUTCH][0x5c - CHARSET_OFFSET] =
	  0xbd;  // 1/2
	charsetMapping[CHARSET_DUTCH][0x5d - CHARSET_OFFSET] =
	  0x7c;  // |
	charsetMapping[CHARSET_DUTCH][0x7b - CHARSET_OFFSET] =
	  BOX;  // diaeresis (n/a)
	charsetMapping[CHARSET_DUTCH][0x7c - CHARSET_OFFSET] =
	  BOX;  // florin (ƒ; n/a)
	charsetMapping[CHARSET_DUTCH][0x7d - CHARSET_OFFSET] =
	  0xbc;  // 1/4
	charsetMapping[CHARSET_DUTCH][0x7e - CHARSET_OFFSET] =
	  BOX;  // acute accent (n/a)
	charsetMapping[CHARSET_FINNISH][0x5b - CHARSET_OFFSET] =
	  0xc4;  // A umlaut (Ä)
	charsetMapping[CHARSET_FINNISH][0x5c - CHARSET_OFFSET] =
	  0xd6;  // O umlaut (Ö)
	charsetMapping[CHARSET_FINNISH][0x5d - CHARSET_OFFSET] =
	  0xc5;  // A ring (Å)
	charsetMapping[CHARSET_FINNISH][0x5e - CHARSET_OFFSET] =
	  0xdc;  // U umlaut (Ü)
	charsetMapping[CHARSET_FINNISH][0x60 - CHARSET_OFFSET] =
	  0xe9;  // e accute accent (é)
	charsetMapping[CHARSET_FINNISH][0x7b - CHARSET_OFFSET] =
	  0xe4;  // a umlaut (ä)
	charsetMapping[CHARSET_FINNISH][0x7c - CHARSET_OFFSET] =
	  0xf6;  // o umlaut (ö)
	charsetMapping[CHARSET_FINNISH][0x7d - CHARSET_OFFSET] =
	  0xe5;  // a ring (å)
	charsetMapping[CHARSET_FINNISH][0x7e - CHARSET_OFFSET] =
	  0xfc;  // u umlaut (ü)
	charsetMapping[CHARSET_FRENCH][0x23 - CHARSET_OFFSET] =
	  0x1e;  // pound sign (£)
	charsetMapping[CHARSET_FRENCH][0x40 - CHARSET_OFFSET] =
	  0xe0;  // a grave accent (à)
	charsetMapping[CHARSET_FRENCH][0x5b - CHARSET_OFFSET] =
	  0xb0;  // degree (°)
	charsetMapping[CHARSET_FRENCH][0x5c - CHARSET_OFFSET] =
	  0xe7;  // c cedille (ç)
	charsetMapping[CHARSET_FRENCH][0x5d - CHARSET_OFFSET] =
	  0xa7;  // section (§)
	charsetMapping[CHARSET_FRENCH][0x7b - CHARSET_OFFSET] =
	  0xe9;  // e accute accent (é)
	charsetMapping[CHARSET_FRENCH][0x7c - CHARSET_OFFSET] =
	  0xf9;  // u grave accent (ù)
	charsetMapping[CHARSET_FRENCH][0x7d - CHARSET_OFFSET] =
	  0xe8;  // e grave accent (è)
	charsetMapping[CHARSET_FRENCH][0x7e - CHARSET_OFFSET] =
	  BOX;  // diaeresis (n/a)
	charsetMapping[CHARSET_FRENCH_CANADIAN][0x40 - CHARSET_OFFSET] =
	  0xe0;  // a grave accent (à)
	charsetMapping[CHARSET_FRENCH_CANADIAN][0x5b - CHARSET_OFFSET] =
	  0xe2;  // a circumflex accent (â)
	charsetMapping[CHARSET_FRENCH_CANADIAN][0x5c - CHARSET_OFFSET] =
	  0xe7;  // c cedille (ç)
	charsetMapping[CHARSET_FRENCH_CANADIAN][0x5d - CHARSET_OFFSET] =
	  0xea;  // e circumflex accent (è)
	charsetMapping[CHARSET_FRENCH_CANADIAN][0x5e - CHARSET_OFFSET] =
	  0xee;  // i circumflex accent (î)
	charsetMapping[CHARSET_FRENCH_CANADIAN][0x7b - CHARSET_OFFSET] =
	  0xe9;  // e accute accent (é)
	charsetMapping[CHARSET_FRENCH_CANADIAN][0x7c - CHARSET_OFFSET] =
	  0xf9;  // u grave accent (ù)
	charsetMapping[CHARSET_FRENCH_CANADIAN][0x7d - CHARSET_OFFSET] =
	  0xe8;  // e grave accent (è)
	charsetMapping[CHARSET_FRENCH_CANADIAN][0x7e - CHARSET_OFFSET] =
	  0xfb;  // u circumflex accent (û)
	charsetMapping[CHARSET_GERMAN][0x40 - CHARSET_OFFSET] =
	  0xa7;  // section (§)
	charsetMapping[CHARSET_GERMAN][0x5b - CHARSET_OFFSET] =
	  0xc4;  // A umlaut (Ä)
	charsetMapping[CHARSET_GERMAN][0x5c - CHARSET_OFFSET] =
	  0xd6;  // O umlaut (Ö)
	charsetMapping[CHARSET_GERMAN][0x5d - CHARSET_OFFSET] =
	  0xdc;  // U umlaut (Ü)
	charsetMapping[CHARSET_GERMAN][0x7b - CHARSET_OFFSET] =
	  0xe4;  // a umlaut (ä)
	charsetMapping[CHARSET_GERMAN][0x7c - CHARSET_OFFSET] =
	  0xf6;  // o umlaut (ö)
	charsetMapping[CHARSET_GERMAN][0x7d - CHARSET_OFFSET] =
	  0xfc;  // u umlaut (ü)
	charsetMapping[CHARSET_GERMAN][0x7e - CHARSET_OFFSET] =
	  0xdf;  // ss (ß)
	charsetMapping[CHARSET_ITALIAN][0x23 - CHARSET_OFFSET] =
	  0x1e;  // pound sign (£)
	charsetMapping[CHARSET_ITALIAN][0x40 - CHARSET_OFFSET] =
	  0xa7;  // section (§)
	charsetMapping[CHARSET_ITALIAN][0x5b - CHARSET_OFFSET] =
	  0xb0;  // degree (°)
	charsetMapping[CHARSET_ITALIAN][0x5c - CHARSET_OFFSET] =
	  0xe7;  // c cedille (ç)
	charsetMapping[CHARSET_ITALIAN][0x5d - CHARSET_OFFSET] =
	  0xe9;  // e accute accent (é)
	charsetMapping[CHARSET_ITALIAN][0x60 - CHARSET_OFFSET] =
	  0xf9;  // u grave accent (ù)
	charsetMapping[CHARSET_ITALIAN][0x7b - CHARSET_OFFSET] =
	  0xe0;  // a grave accent (à)
	charsetMapping[CHARSET_ITALIAN][0x7c - CHARSET_OFFSET] =
	  0xf2;  // o grave accent (ò)
	charsetMapping[CHARSET_ITALIAN][0x7d - CHARSET_OFFSET] =
	  0xe8;  // e grave accent (è)
	charsetMapping[CHARSET_ITALIAN][0x7e - CHARSET_OFFSET] =
	  0xec;  // i grave accent (ì)
	charsetMapping[CHARSET_NORWEGIAN_DANISH][0x40 - CHARSET_OFFSET] =
	  0xc4;  // A umlaut (Ä)
	charsetMapping[CHARSET_NORWEGIAN_DANISH][0x5b - CHARSET_OFFSET] =
	  0xc6;  // AE (Æ)
	charsetMapping[CHARSET_NORWEGIAN_DANISH][0x5c - CHARSET_OFFSET] =
	  0xd8;  // O stroke (Ø)
	charsetMapping[CHARSET_NORWEGIAN_DANISH][0x5d - CHARSET_OFFSET] =
	  0xc5;  // A ring (Å)
	charsetMapping[CHARSET_NORWEGIAN_DANISH][0x5e - CHARSET_OFFSET] =
	  0xdc;  // U umlaut (Ü)
	charsetMapping[CHARSET_NORWEGIAN_DANISH][0x60 - CHARSET_OFFSET] =
	  0xe4;  // a umlaut (ä)
	charsetMapping[CHARSET_NORWEGIAN_DANISH][0x7b - CHARSET_OFFSET] =
	  0xe6;  // ae (æ)
	charsetMapping[CHARSET_NORWEGIAN_DANISH][0x7c - CHARSET_OFFSET] =
	  0xf8;  // o stroke (ø)
	charsetMapping[CHARSET_NORWEGIAN_DANISH][0x7d - CHARSET_OFFSET] =
	  0xe5;  // a ring (å)
	charsetMapping[CHARSET_NORWEGIAN_DANISH][0x7e - CHARSET_OFFSET] =
	  0xfc;  // u umlaut (ü)
	charsetMapping[CHARSET_SPANISH][0x23 - CHARSET_OFFSET] =
	  0x1e;  // pound sign (£)
	charsetMapping[CHARSET_SPANISH][0x40 - CHARSET_OFFSET] =
	  0xa7;  // section (§)
	charsetMapping[CHARSET_SPANISH][0x5b - CHARSET_OFFSET] =
	  0xa1;  // inverted ! (¡)
	charsetMapping[CHARSET_SPANISH][0x5c - CHARSET_OFFSET] =
	  0xd1;  // N tilde (Ñ)
	charsetMapping[CHARSET_SPANISH][0x5d - CHARSET_OFFSET] =
	  0xbf;  // inverted ? (¿)
	charsetMapping[CHARSET_SPANISH][0x7b - CHARSET_OFFSET] =
	  0xb0;  // degree (°)
	charsetMapping[CHARSET_SPANISH][0x7c - CHARSET_OFFSET] =
	  0xf1;  // n tilde (ñ)
	charsetMapping[CHARSET_SPANISH][0x7d - CHARSET_OFFSET] =
	  0xe7;  // c cedille (ç)
	charsetMapping[CHARSET_SWEDISH][0x40 - CHARSET_OFFSET] =
	  0xc9;  // E acute accent (É)
	charsetMapping[CHARSET_SWEDISH][0x5b - CHARSET_OFFSET] =
	  0xc4;  // A umlaut (Ä)
	charsetMapping[CHARSET_SWEDISH][0x5c - CHARSET_OFFSET] =
	  0xd6;  // O umlaut (Ö)
	charsetMapping[CHARSET_SWEDISH][0x5d - CHARSET_OFFSET] =
	  0xc5;  // A ring (Å)
	charsetMapping[CHARSET_SWEDISH][0x5e - CHARSET_OFFSET] =
	  0xdc;  // U umlaut (Ü)
	charsetMapping[CHARSET_SWEDISH][0x60 - CHARSET_OFFSET] =
	  0xe9;  // e acute accent (é)
	charsetMapping[CHARSET_SWEDISH][0x7b - CHARSET_OFFSET] =
	  0xe4;  // a umlaut (ä)
	charsetMapping[CHARSET_SWEDISH][0x7c - CHARSET_OFFSET] =
	  0xf6;  // o umlaut (ö)
	charsetMapping[CHARSET_SWEDISH][0x7d - CHARSET_OFFSET] =
	  0xe5;  // a ring (å)
	charsetMapping[CHARSET_SWEDISH][0x7e - CHARSET_OFFSET] =
	  0xfc;  // u umlaut (ü)
	charsetMapping[CHARSET_SWISS][0x23 - CHARSET_OFFSET] =
	  0xf9;  // u grave accent (ù)
	charsetMapping[CHARSET_SWISS][0x40 - CHARSET_OFFSET] =
	  0xe0;  // a grave accent (à)
	charsetMapping[CHARSET_SWISS][0x5b - CHARSET_OFFSET] =
	  0xe9;  // e acute accent (é)
	charsetMapping[CHARSET_SWISS][0x5c - CHARSET_OFFSET] =
	  0xe7;  // c cedille (ç)
	charsetMapping[CHARSET_SWISS][0x5d - CHARSET_OFFSET] =
	  0xea;  // e circumflex accent (è)
	charsetMapping[CHARSET_SWISS][0x5e - CHARSET_OFFSET] =
	  0xee;  // i circumflex accent (î)
	charsetMapping[CHARSET_SWISS][0x5f - CHARSET_OFFSET] =
	  0xe8;  // e grave accent (è)
	charsetMapping[CHARSET_SWISS][0x60 - CHARSET_OFFSET] =
	  0xf4;  // o circumflex accent (ô)
	charsetMapping[CHARSET_SWISS][0x7b - CHARSET_OFFSET] =
	  0xe4;  // a umlaut (ä)
	charsetMapping[CHARSET_SWISS][0x7c - CHARSET_OFFSET] =
	  0xf6;  // o umlaut (ö)
	charsetMapping[CHARSET_SWISS][0x7d - CHARSET_OFFSET] =
	  0xfc;  // u umlaut (ü)
	charsetMapping[CHARSET_SWISS][0x7e - CHARSET_OFFSET] =
	  0xfb;  // u circumflex accent (û)
	glyphsLoaded = true;
      }
      width = condensed ? 9 : 10;
      canvas.setColor(reverse ? foregroundColor : backgroundColor);
      canvas.fillRect(positionX,
		      positionY,
		      pixelWidth * width,
		      pixelHeight * 20);
      canvas.setColor(reverse ? backgroundColor : foregroundColor);
      for (int scanLine = 0; scanLine < 10; scanLine++) {
	maskLine =
	  doubleHeight ?
	  ((bottomHalf ? 5 : 0) + (scanLine / 2)) :
	  scanLine;
	mask = (underscored && (maskLine == 9)) ?
	  0xff :
	  (fontMatrix[code][maskLine] & 0xff);
	mask = (mask << 1) | (mask & 1);
	if (!condensed) {
	  mask = (mask << 1) | (mask & 1);
	}
	if (doubleWidth) {
	  for (bit = 19; bit >= 0; bit--) {
	    mask =
	      (mask & ~(1 << bit)) | ((mask & (1 << (bit / 2))) << (bit / 2));
	  }
	}
	mask |= mask >> 1;
	if (doubleWidth && !rightHalf) {
	  mask >>= width;
	}
	for (bit = 0; bit < width; bit++) {
	  if (((mask >> bit) & 1) != 0) {
	    canvas.fillRect(positionX + ((width - 1 - bit) * pixelWidth),
			    positionY + (scanLine * pixelHeight * 2),
			    pixelWidth,
			    pixelHeight * (bold ? 2 : 1));
	  }
	}
      }
    }
  }
}
