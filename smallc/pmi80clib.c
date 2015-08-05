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
 * Seed for LCG or other randomizer.
 */
unsigned int seed[2];

/**
 * Display buffer and wait for key (return on release).
 * Seed is incremented, generating entropy. 
 *
 * @param  disp pointer to display buffer
 * @return      ASCII code of the key (0 = hw failure)
 */
wfk(unsigned char *disp) {
  unsigned char pos, pos2, *p, sc, sc2;
  do {
    for (pos = 0, p = disp; pos < DISPLEN; pos++) {
      setssd(pos, *p++);
      seed[0]++;
      sc = getsc();
      if (sc) {
	break;
      }
    }
  } while (!sc);
  do {
    for (pos2 = 0, p = disp; pos2 < DISPLEN; pos2++) {
      setssd(pos2, *p++);
      seed[1]++;
      if (pos == pos2) {
	sc2 = getsc();
      }
    }
  } while (sc2);
  cld();
  return sc2asc(pos, sc);
}

/* keyboard scancodes */
unsigned char sc_c[23] = {
  0x20, 0x40, 0x11, 0x21, 0x41, 0x12, 0x22, 0x42,
  0x23, 0x43, 0x14, 0x24, 0x44, 0x15, 0x25, 0x45,
  0x26, 0x46, 0x27, 0x47, 0x18, 0x28, 0x48
};
char sc_a[23] = {
  '2', '0', 'S', '6', '4', 'L', 'a', '8',
  'R', 'X', 'B', 'f', 'd', 'M', 'e', 'c',
  'b', '9', '7', '5', '=', '3', '1'
};

/**
 * Convert keyboard scancode to ASCII.
 *
 * @param  pos  column
 * @param  sc   scancode
 * @return      ASCII code of the key (0 = hw failure)
 */
sc2asc(unsigned char pos, unsigned char sc) {
  unsigned char *p;
  sc = ((~((sc & (sc - 1)) - sc)) + 1) | pos;
  for (pos = 0, p = sc_c; pos < 23; pos++, p++) {
    if (*p == sc) {
      return sc_a[pos];
    }
  }
  return 0;
}

/* characters that cannot be represented on SSD are shown as blanks */
#define UNDEF 0x7f

/* conversion table */
unsigned char a2btbl[0x60] = {
  0x7f,	 /*   */  UNDEF, /* ! */  0x5d,  /* " */  UNDEF, /* # */
  UNDEF, /* $ */  UNDEF, /* % */  UNDEF, /* & */  0x5f,  /* ' */
  0x46,	 /* ( */  0x70,	 /* ) */  UNDEF, /* * */  UNDEF, /* + */
  0x6f,  /* , */  0x3f,	 /* - */  UNDEF, /* . */  UNDEF, /* / */
  0x40,	 /* 0 */  0x79,	 /* 1 */  0x24,	 /* 2 */  0x30,	 /* 3 */
  0x19,	 /* 4 */  0x12,	 /* 5 */  0x02,	 /* 6 */  0x78,	 /* 7 */
  0x00,	 /* 8 */  0x10,	 /* 9 */  UNDEF, /* : */  UNDEF, /* ; */
  UNDEF, /* < */  0x37,	 /* = */  UNDEF, /* > */  0x2c,	 /* ? */
  0x44,	 /* @ */  0x08,	 /* A */  0x03,	 /* B */  0x46,	 /* C */
  0x21,	 /* D */  0x06,	 /* E */  0x0e,	 /* F */  0x42,	 /* G */
  0x09,	 /* H */  0x79,	 /* I */  0x60,	 /* J */  UNDEF, /* K */
  0x47,	 /* L */  0x48,	 /* M */  UNDEF, /* N */  0x40,	 /* O */
  0x0c,	 /* P */  UNDEF, /* Q */  0x2f,	 /* R */  0x12,	 /* S */
  0x07,	 /* T */  0x41,	 /* U */  UNDEF, /* V */  UNDEF, /* W */
  0x09,	 /* X */  0x11,	 /* Y */  0x24,	 /* Z */  0x46,	 /* [ */
  UNDEF, /* \ */  0x70,	 /* ] */  0x7e,	 /* ^ */  0x77,	 /* _ */
  0x7d,	 /* ` */  0x20,	 /* a */  0x03,	 /* b */  0x27,	 /* c */
  0x21,	 /* d */  UNDEF, /* e */  UNDEF, /* f */  0x10,	 /* g */
  0x0b,	 /* h */  0x7b,	 /* i */  0x73,	 /* j */  UNDEF, /* k */
  0x4f,	 /* l */  UNDEF, /* m */  0x2b,  /* n */  0x23,	 /* o */
  UNDEF, /* p */  0x18,	 /* q */  0x2f,	 /* r */  UNDEF, /* s */
  0x07,	 /* t */  0x63,	 /* u */  UNDEF, /* v */  UNDEF, /* w */
  UNDEF, /* x */  0x11,	 /* y */  UNDEF, /* z */  0x46,	 /* { */
  0x4f,	 /* | */  0x70,	 /* } */  UNDEF, /* ~ */  0x1c
                                /* DEL (used for degree sign) */
};

/**
 * Convert ASCII buffer to code buffer.
 *
 * @param ascii pointer to ASCII buffer
 * @param disp  pointer to display buffer
 */
asc2buf(char *ascii, unsigned char *disp) {
  unsigned char pos;
  for (pos = 0; pos < DISPLEN; pos++) {
    *disp = a2btbl[*ascii - 0x20];
    ascii++;
    disp++;
  }	  
}
