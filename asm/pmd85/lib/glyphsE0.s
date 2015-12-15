; glyphsE0.S
;
; Copyright (C) 2015, Tomáš Pecina <tomas@pecina.cz>
;
; This file is part of cz.pecina.retro, retro 8-bit computer emulators.
;
; This application is free software: you can redistribute it and/or
; modify it under the terms of the GNU General Public License as
; published by the Free Software Foundation, either version 3 of the
; License, or (at your option) any later version.
;
; This application is distributed in the hope that it will be useful,
; but WITHOUT ANY WARRANTY; without even the implied warranty of
; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
; GNU General Public License for more details.         
;
; You should have received a copy of the GNU General Public License
; along with this program.  If not, see <http://www.gnu.org/licenses/>.


; Character glyphs 6x10 - 0xe0-0eff.

; ==============================================================================
; Glyphs
;
	.data
	.globl	glyphsE0
glyphsE0:	
	; e0
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x20	; .....#
	.byte	0x1e	; .####.
	.byte	0x14	; ..#.#.
	.byte	0x14	; ..#.#.
	.byte	0x14	; ..#.#.
	.byte	0x14	; ..#.#.
	.byte	0x00	; ......
	; e1
	.byte	0x10	; ....#.
	.byte	0x08	; ...#..
	.byte	0x08	; ...#..
	.byte	0x14	; ..#.#.
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x3e	; .#####
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x00	; ......
	; e2
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x04	; ..#...
	.byte	0x2a	; .#.#.#
	.byte	0x18	; ...##.
	.byte	0x0c	; ..##..
	.byte	0x0a	; .#.#..
	.byte	0x0a	; .#.#..
	.byte	0x04	; ..#...
	; e3
	.byte	0x14	; ..#.#.
	.byte	0x08	; ...#..
	.byte	0x1c	; ..###.
	.byte	0x22	; .#...#
	.byte	0x02	; .#....
	.byte	0x02	; .#....
	.byte	0x02	; .#....
	.byte	0x22	; .#...#
	.byte	0x1c	; ..###.
	.byte	0x00	; ......
	; e4
	.byte	0x14	; ..#.#.
	.byte	0x08	; ...#..
	.byte	0x1e	; .####.
	.byte	0x24	; ..#..#
	.byte	0x24	; ..#..#
	.byte	0x24	; ..#..#
	.byte	0x24	; ..#..#
	.byte	0x24	; ..#..#
	.byte	0x1e	; .####.
	.byte	0x00	; ......
	; e5
	.byte	0x14	; ..#.#.
	.byte	0x08	; ...#..
	.byte	0x3e	; .#####
	.byte	0x02	; .#....
	.byte	0x02	; .#....
	.byte	0x1e	; .####.
	.byte	0x02	; .#....
	.byte	0x02	; .#....
	.byte	0x3e	; .#####
	.byte	0x00	; ......
	; e6
	.byte	0x10	; ....#.
	.byte	0x08	; ...#..
	.byte	0x1e	; .####.
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x1e	; .####.
	.byte	0x0a	; .#.#..
	.byte	0x12	; .#..#.
	.byte	0x22	; .#...#
	.byte	0x00	; ......
	; e7
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x3f	; ######
	.byte	0x3f	; ######
	.byte	0x3f	; ######
	.byte	0x3f	; ######
	.byte	0x3f	; ######
	; e8
	.byte	0x14	; ..#.#.
	.byte	0x00	; ......
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x1c	; ..###.
	.byte	0x00	; ......
	; e9
	.byte	0x10	; ....#.
	.byte	0x08	; ...#..
	.byte	0x1c	; ..###.
	.byte	0x08	; ...#..
	.byte	0x08	; ...#..
	.byte	0x08	; ...#..
	.byte	0x08	; ...#..
	.byte	0x08	; ...#..
	.byte	0x1c	; ..###.
	.byte	0x00	; ......
	; ea
	.byte	0x08	; ...#..
	.byte	0x14	; ..#.#.
	.byte	0x2a	; .#.#.#
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x1c	; ..###.
	.byte	0x00	; ......
	; eb
	.byte	0x10	; ....#.
	.byte	0x08	; ...#..
	.byte	0x02	; .#....
	.byte	0x02	; .#....
	.byte	0x02	; .#....
	.byte	0x02	; .#....
	.byte	0x02	; .#....
	.byte	0x02	; .#....
	.byte	0x3e	; .#####
	.byte	0x00	; ......
	; ec
	.byte	0x14	; ..#.#.
	.byte	0x08	; ...#..
	.byte	0x02	; .#....
	.byte	0x02	; .#....
	.byte	0x02	; .#....
	.byte	0x02	; .#....
	.byte	0x02	; .#....
	.byte	0x02	; .#....
	.byte	0x3e	; .#####
	.byte	0x00	; ......
	; ed
	.byte	0x14	; ..#.#.
	.byte	0x00	; ......
	.byte	0x1c	; ..###.
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x1c	; ..###.
	.byte	0x00	; ......
	; ee
	.byte	0x14	; ..#.#.
	.byte	0x08	; ...#..
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x26	; .##..#
	.byte	0x2a	; .#.#.#
	.byte	0x32	; .#..##
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x00	; ......
	; ef
	.byte	0x10	; ....#.
	.byte	0x08	; ...#..
	.byte	0x1c	; ..###.
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x1c	; ..###.
	.byte	0x00	; ......
	; f0
	.byte	0x08	; ...#..
	.byte	0x14	; ..#.#.
	.byte	0x1c	; ..###.
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x1c	; ..###.
	.byte	0x00	; ......
	; f1
	.byte	0x14	; ..#.#.
	.byte	0x00	; ......
	.byte	0x08	; ...#..
	.byte	0x14	; ..#.#.
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x3e	; .#####
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x00	; ......
	; f2
	.byte	0x14	; ..#.#.
	.byte	0x08	; ...#..
	.byte	0x1e	; .####.
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x1e	; .####.
	.byte	0x0a	; .#.#..
	.byte	0x12	; .#..#.
	.byte	0x22	; .#...#
	.byte	0x00	; ......
	; f3
	.byte	0x14	; ..#.#.
	.byte	0x08	; ...#..
	.byte	0x1c	; ..###.
	.byte	0x22	; .#...#
	.byte	0x02	; .#....
	.byte	0x1c	; ..###.
	.byte	0x20	; .....#
	.byte	0x22	; .#...#
	.byte	0x1c	; ..###.
	.byte	0x00	; ......
	; f4
	.byte	0x14	; ..#.#.
	.byte	0x08	; ...#..
	.byte	0x3e	; .#####
	.byte	0x08	; ...#..
	.byte	0x08	; ...#..
	.byte	0x08	; ...#..
	.byte	0x08	; ...#..
	.byte	0x08	; ...#..
	.byte	0x08	; ...#..
	.byte	0x00	; ......
	; f5
	.byte	0x10	; ....#.
	.byte	0x08	; ...#..
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x1c	; ..###.
	.byte	0x00	; ......
	; f6
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x30	; ....##
	.byte	0x08	; ...#..
	.byte	0x10	; ....#.
	.byte	0x1c	; ..###.
	.byte	0x12	; .#..#.
	.byte	0x12	; .#..#.
	.byte	0x0c	; ..##..
	.byte	0x00	; ......
	; f7
	.byte	0x10	; ....#.
	.byte	0x08	; ...#..
	.byte	0x3e	; .#####
	.byte	0x02	; .#....
	.byte	0x02	; .#....
	.byte	0x1e	; .####.
	.byte	0x02	; .#....
	.byte	0x02	; .#....
	.byte	0x3e	; .#####
	.byte	0x00	; ......
	; f8
	.byte	0x04	; ..#...
	.byte	0x08	; ...#..
	.byte	0x08	; ...#..
	.byte	0x14	; ..#.#.
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x3e	; .#####
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x00	; ......
	; f9
	.byte	0x10	; ....#.
	.byte	0x08	; ...#..
	.byte	0x22	; .#...#
	.byte	0x22	; .#...#
	.byte	0x14	; ..#.#.
	.byte	0x08	; ...#..
	.byte	0x08	; ...#..
	.byte	0x08	; ...#..
	.byte	0x08	; ...#..
	.byte	0x00	; ......
	; fa
	.byte	0x14	; ..#.#.
	.byte	0x08	; ...#..
	.byte	0x3e	; .#####
	.byte	0x20	; .....#
	.byte	0x10	; ....#.
	.byte	0x08	; ...#..
	.byte	0x04	; ..#...
	.byte	0x02	; .#....
	.byte	0x3e	; .#####
	.byte	0x00	; ......
	; fb
	.byte	0x38	; ...###
	.byte	0x38	; ...###
	.byte	0x38	; ...###
	.byte	0x38	; ...###
	.byte	0x38	; ...###
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	; fc
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x38	; ...###
	.byte	0x38	; ...###
	.byte	0x38	; ...###
	.byte	0x38	; ...###
	.byte	0x38	; ...###
	; fd
	.byte	0x38	; ...###
	.byte	0x38	; ...###
	.byte	0x38	; ...###
	.byte	0x38	; ...###
	.byte	0x38	; ...###
	.byte	0x3f	; ######
	.byte	0x3f	; ######
	.byte	0x3f	; ######
	.byte	0x3f	; ######
	.byte	0x3f	; ######
	; fe
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x07	; ###...
	.byte	0x07	; ###...
	.byte	0x07	; ###...
	.byte	0x07	; ###...
	.byte	0x07	; ###...
	; ff
	.byte	0x07	; ###...
	.byte	0x07	; ###...
	.byte	0x07	; ###...
	.byte	0x07	; ###...
	.byte	0x07	; ###...
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......

	.end
