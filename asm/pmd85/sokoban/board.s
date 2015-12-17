; board.s
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


; This is the board drawing code.

	.include "sokoban.inc"
	
; ==============================================================================
; Constants
; 
	.globl	MSGAREA
	.equiv	MSGAREA, 0xffc0		; position of the notification area
	.equiv	ULC, 0xc000		; upper left corner of the board
	
; ==============================================================================
; draw_board - draw board
; 
;   input:  (board) - board
;           (rows) - number of rows
;           (cols) - number of columns
;           (roff) - row offset
;           (coff) - column offset
;
;   uses:   all
; 
	.text
	.globl	draw_board
draw_board:
	call	erase
	ld	hl,board
	ld	b,0
3:	ld	c,0
2:	ld	a,(hl)
	call	2f
	or	a
	jp	z,1f
	ld	d,a
	push	hl
	push	bc
	ld	a,(roff)
	add	a,b
	ld	b,a
	ld	a,(coff)
	add	a,c
	ld	c,a
	ld	a,d
	call	draw_square
	pop	bc
	pop	hl
1:	inc	hl
	inc	c
	ld	a,(cols)
	cp	c
	jp	nz,2b
	inc	b
	ld	a,(rows)
	cp	b
	jp	nz,3b
	ret
2:	cp	WALL
	jp	nz,1f
	ld	a,wall
	ret
1:	cp	BOX
	jp	nz,1f
	ld	a,box
	ret
1:	cp	BOX | GOAL
	jp	nz,1f
	ld	a,boxongoal
	ret
1:	cp	GOAL
	jp	nz,1f
	ld	a,goal
	ret
1:	xor	a
	ret
	
; ==============================================================================
; draw_square - draw square
; 
;   input:  A - square
;           B - row
;           C - column
;
;   uses:   all
; 
	.text
	.globl	draw_square
draw_square:
	push	af
	ld	a,b
	add	a,a
	add	a,b
	ld	d,a
	ld	a,c
	add	a,a
	ld	e,a
	ld	hl,ULC
	add	hl,de
	ex	de,hl
	pop	af
	add	a,a
	add	a,a
	add	a,a
	ld	c,a
	add	a,a
	add	a,c
	ld	c,a
	ld	b,0
	ld	hl,squares
	add	hl,bc
	ex	de,hl
	ld	bc,63
	ld	a,12
	ld	bc,63
1:	push	af
	ld	a,(de)
	inc	de
	ld	(hl),a
	inc	hl
	ld	a,(de)
	inc	de
	ld	(hl),a
	add	hl,bc
	pop	af
	dec	a
	jp	nz,1b
	ret
	
; ==============================================================================
; Squares
;
	.data
squares:	

; 0 - blank
	.rept	12
	.word	0x0000	; ............
	.endr
; 1 - wall
	.equiv	wall, 1
	.rept	6
	;; .word	0x1515	; #,#,#,#,#,#,
	;; .word	0x2a2a	; .#,#,#,#,#,#
	.word	0x7f7f
	.word	0x7f7f
	.endr
; 2 - box
	.equiv	box, 2
	.word	0x3f3f	; ############
	.word	0x3003	; ##........##
	.word	0x2805	; #.#......#.#
	.word	0x2409	; #..#....#..#
	.word	0x2211	; #...#..#,..#
	.word	0x2121	; #....##,...#
	.word	0x2121	; #....##,...#
	.word	0x2211	; #...#..#,..#
	.word	0x2409	; #..#....#..#
	.word	0x2805	; #.#......#.#
	.word	0x3003	; ##........##
	.word	0x3f3f	; ############
; 3 - box on goal
	.equiv	boxongoal, 3
	.word	0x7f7f	; ************
	.word	0x7043	; **........**
	.word	0x6845	; *.*......*.*
	.word	0x6449	; *..*....*..*
	.word	0x6251	; *...*..*,..*
	.word	0x6161	; *....**,...*
	.word	0x6161	; *....**,...*
	.word	0x6251	; *...*..*,..*
	.word	0x6449	; *..*....*..*
	.word	0x6845	; *.*......*.*
	.word	0x7043	; **........**
	.word	0x7f7f	; ************
; 4 - goal
	.equiv	goal, 4
	.word	0x0000	; ............
	.word	0x0000	; ............
	.word	0x0000	; ............
	.word	0x0000	; ............
	.word	0x0120	; .....##.....
	.word	0x0330	; ....####....
	.word	0x0330	; ....####....
	.word	0x0120	; .....##.....
	.word	0x0000	; ............
	.word	0x0000	; ............
	.word	0x0000	; ............
	.word	0x0000	; ............

; ==============================================================================
; Custom glyphs
;
	.data
	.globl	glyphs80
glyphs80:
	.globl	OBELUS
	.equiv	OBELUS, 0x80
	; 80
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x08	; ...#..
	.byte	0x00	; ......
	.byte	0x3e	; .#####
	.byte	0x00	; ......
	.byte	0x08	; ...#..
	.byte	0x00	; ......
	.byte	0x00	; ......

	.end
