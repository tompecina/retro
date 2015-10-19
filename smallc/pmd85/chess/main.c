/* main.c
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


/* Game of chess for PMD 85. */

/* main entry point of the application */
main() {
  int r, f;
  erase();
  for (r = 0; r < 8; r++)
    for (f = 0; f < 8; f++)
      draw_piece(0, 21 + f * 10 + r);
  draw_piece(5, 21);
  draw_piece(3, 22);
  draw_piece(4, 23);
  draw_piece(6, 24);
  draw_piece(7, 25);
  draw_piece(4, 26);
  draw_piece(3, 27);
  draw_piece(5, 28);
  draw_piece(2, 31);
  draw_piece(2, 32);
  draw_piece(2, 33);
  draw_piece(2, 34);
  draw_piece(2, 35);
  draw_piece(2, 36);
  draw_piece(2, 37);
  draw_piece(2, 38);
  draw_piece(12, 81);
  draw_piece(12, 82);
  draw_piece(12, 83);
  draw_piece(12, 84);
  draw_piece(12, 85);
  draw_piece(12, 86);
  draw_piece(12, 87);
  draw_piece(12, 88);
  draw_piece(15, 91);
  draw_piece(13, 92);
  draw_piece(14, 93);
  draw_piece(16, 94);
  draw_piece(17, 95);
  draw_piece(14, 96);
  draw_piece(13, 97);
  draw_piece(15, 98);
  draw_piece(6, 54);
  draw_piece(7, 55);
  draw_piece(16, 64);
  draw_piece(17, 65);
  draw_border();
  draw_labels();
}
