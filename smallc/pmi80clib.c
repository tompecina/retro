/* pmi80clib.c
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

/* This is the Small C library for Tesla PMI-80 hardware. */

/* Number of display positions */
#define DISPLEN 9

/**
 * Display buffer and wait for key (return on release).
 *
 * @param  disp pointer to display buffer
 * @param  seed pointer to seed, incremented on every port read
 * @return      ASCII code of the key
 */
wfk(unsigned char *disp, unsigned int *seed) {
  unsigned char pos, *p, sc;
  do {
    for (pos = 0, p = disp; pos < DISPLEN; pos++) {
      setssd(pos, *p++);
      (*seed)++;
      sc = getsc();
      if (sc) {
	break;
      }
    }
  } while (!sc);
}

/* characters that cannot be represented on SSD are shown as blanks */
#define UNDEF 0x7f

/* conversion table */
unsigned char a2btbl[0x60] = {
  0x7f,	 /* space */
  UNDEF, /* ! */
  0x5d,  /* " */
  UNDEF, /* # */
  UNDEF, /* $ */
  UNDEF, /* % */
  UNDEF, /* & */
  0x5f,  /* ' */
  0x46,	 /* ( */
  0x70,	 /* ) */
  UNDEF, /* * */
  UNDEF, /* + */
  0x6f,  /* , */
  0x3f,	 /* - */
  UNDEF, /* . */
  UNDEF, /* / */
  0x40,	 /* 0 */
  0x79,	 /* 1 */
  0x24,	 /* 2 */
  0x30,	 /* 3 */
  0x19,	 /* 4 */
  0x12,	 /* 5 */
  0x02,	 /* 6 */
  0x78,	 /* 7 */
  0x00,	 /* 8 */
  0x10,	 /* 9 */
  UNDEF, /* : */
  UNDEF, /* ; */
  UNDEF, /* < */
  0x37,	 /* = */
  UNDEF, /* > */
  0x2c,	 /* ? */
  0x44,	 /* @ */
  0x08,	 /* A */
  0x03,	 /* B */
  0x46,	 /* C */
  0x21,	 /* D */
  0x06,	 /* E */
  0x0e,	 /* F */
  0x42,	 /* G */
  0x09,	 /* H */
  0x79,	 /* I */
  0x60,	 /* J */
  UNDEF, /* K */
  0x47,	 /* L */
  0x48,	 /* M */
  UNDEF, /* N */
  0x40,	 /* O */
  0x0c,	 /* P */
  UNDEF, /* Q */
  0x2f,	 /* R */
  0x12,	 /* S */
  0x07,	 /* T */
  0x41,	 /* U */
  UNDEF, /* V */
  UNDEF, /* W */
  0x09,	 /* X */
  0x11,	 /* Y */
  0x24,	 /* Z */
  0x46,	 /* [ */
  UNDEF, /* backslash */
  0x70,	 /* ] */
  0x7e,	 /* ^ */
  0x77,	 /* _ */
  0x7d,	 /* ` */
  0x20,	 /* a */
  0x03,	 /* b */
  0x27,	 /* c */
  0x21,	 /* d */
  UNDEF, /* e */
  UNDEF, /* f */
  0x10,	 /* g */
  0x0b,	 /* h */
  0x7b,	 /* i */
  0x73,	 /* j */
  UNDEF, /* k */
  0x4f,	 /* l */
  UNDEF, /* m */
  0x2b,  /* n */
  0x23,	 /* o */
  UNDEF, /* p */
  0x18,	 /* q */
  0x2f,	 /* r */
  UNDEF, /* s */
  0x07,	 /* t */
  0x63,	 /* u */
  UNDEF, /* v */
  UNDEF, /* w */
  UNDEF, /* x */
  0x11,	 /* y */
  UNDEF, /* z */
  0x46,	 /* { */
  0x4f,	 /* | */
  0x70,	 /* } */
  UNDEF, /* ~ */
  0x1c	 /* DEL (used for degree sign) */
};

/**
 * Convert ASCII buffer to code buffer.
 *
 * @param ascii pointer to ASCII buffer
 * @param disp  pointer to display buffer
 */
asc2buf(unsigned char *ascii, unsigned char *disp) {
  unsigned char pos;
  for (pos = 0; pos < DISPLEN; pos++) {
    *disp = a2btbl[*ascii - 0x20];
    ascii++;
    disp++;
  }	  
}
