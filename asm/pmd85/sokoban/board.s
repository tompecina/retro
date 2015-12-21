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
	.equiv	ULC, 0xc400		; upper left corner of the board
	
; ==============================================================================
; draw_board - draw board
; 
;   input:  (board) - board
;           (rows) - number of rows
;           (cols) - number of columns
;           (roff) - row offset
;           (coff) - column offset
;	    (prow) - row of pusher
;	    (pcol) - column of pusher
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
	ld	a,(prow)
	ld	hl,roff
	add	a,(hl)
	ld	b,a
	ld	a,(pcol)
	ld	hl,coff
	add	a,(hl)
	ld	c,a
	ld	a,SQ_PUSHER
	jp	draw_square
2:	cp	WALL
	jp	nz,1f
	ld	a,SQ_WALL
	ret
1:	cp	BOX
	jp	nz,1f
	ld	a,SQ_BOX
	ret
1:	cp	BOX | GOAL
	jp	nz,1f
	ld	a,SQ_BOXONGOAL
	ret
1:	cp	GOAL
	jp	nz,1f
	ld	a,SQ_GOAL
	ret
1:	xor	a
	ret
	
	
; ==============================================================================
; draw_square - draw square
; 
;   input:  A - square
;           B - row
;           C - column
;	    (size) =0 - no bigger than LO_ROWS x LO_COLS
;		   =1 - bigger than LO_ROWS x LO_COLS
;
;   uses:   all
; 
	.text
	.globl	draw_square
draw_square:
	push	af
	ld	a,(size)
	or	a
	jp	nz,2f
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
	ld	hl,squareslo
	add	hl,bc
	ex	de,hl
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
2:	ld	hl,ULC
	ld	d,b
	ld	e,0
	add	hl,de
	ld	b,e
	add	hl,bc
	ld	a,d
	rra
	ld	d,a
	ld	a,e
	rra
	ld	e,a
	add	hl,de
	ex	de,hl
	pop	af
	add	a,a
	ld	c,a
	add	a,a
	add	a,c
	ld	c,a
	ld	b,0
	ld	hl,squareshi
	add	hl,bc
	ex	de,hl
	ld	a,6
	ld	bc,64
1:	push	af
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
	.globl	SQ_BLANK, SQ_WALL, SQ_BOX, SQ_BOXONGOAL, SQ_GOAL, SQ_PUSHER
	.equiv	SQ_BLANK, 0
	.equiv	SQ_WALL, 1
	.equiv	SQ_BOX, 2
	.equiv	SQ_BOXONGOAL, 3
	.equiv	SQ_GOAL, 4
	.equiv	SQ_PUSHER, 5
	
	.data
squareslo:	

; 0 - blank
	.word	0x0000	; ............
	.word	0x0000	; ............
	.word	0x0000	; ............
	.word	0x0000	; ............
	.word	0x0000	; ............
	.word	0x0000	; ............
	.word	0x0000	; ............
	.word	0x0000	; ............
	.word	0x0000	; ............
	.word	0x0000	; ............
	.word	0x0000	; ............
	.word	0x0000	; ............
; 1 - wall
	.word	0x7f7f	; ************
	.word	0x7f7f	; ************
	.word	0x7f7f	; ************
	.word	0x7f7f	; ************
	.word	0x7f7f	; ************
	.word	0x7f7f	; ************
	.word	0x7f7f	; ************
	.word	0x7f7f	; ************
	.word	0x7f7f	; ************
	.word	0x7f7f	; ************
	.word	0x7f7f	; ************
	.word	0x7f7f	; ************
; 2 - box
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
	.word	0x0000	; ............
	.word	0x0000	; ............
	.word	0x0000	; ............
	.word	0x0000	; ............
	.word	0x0330	; ....####....
	.word	0x0330	; ....####....
	.word	0x0330	; ....####....
	.word	0x0330	; ....####....
	.word	0x0000	; ............
	.word	0x0000	; ............
	.word	0x0000	; ............
	.word	0x0000	; ............
; 5 - pusher
	.word	0x0330	; ....####....
	.word	0x0330	; ....####....
	.word	0x0330	; ....####....
	.word	0x0330	; ....####....
	.word	0x3f3f	; ############
	.word	0x3f3f	; ############
	.word	0x3f3f	; ############
	.word	0x3f3f	; ############
	.word	0x0330	; ....####....
	.word	0x0330	; ....####....
	.word	0x0330	; ....####....
	.word	0x0330	; ....####....

	.data
squareshi:	

; 0 - blank
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
; 1 - wall
	.byte	0x7f	; ******
	.byte	0x7f	; ******
	.byte	0x7f	; ******
	.byte	0x7f	; ******
	.byte	0x7f	; ******
	.byte	0x7f	; ******
; 2 - box
	.byte	0x3f	; ######
	.byte	0x21	; #....#
	.byte	0x2d	; #.##.#
	.byte	0x2d	; #.##.#
	.byte	0x21	; #....#
	.byte	0x3f	; ######
; 3 - box on goal
	.byte	0x7f	; ******
	.byte	0x61	; *....*
	.byte	0x6d	; *.**.*
	.byte	0x6d	; *.**.*
	.byte	0x61	; *....*
	.byte	0x7f	; ******
; 4 - goal
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x0c	; ..##..
	.byte	0x0c	; ..##..
	.byte	0x00	; ......
	.byte	0x00	; ......
; 5 - pusher
	.byte	0x0c	; ..##..
	.byte	0x0c	; ..##..
	.byte	0x3f	; ######
	.byte	0x3f	; ######
	.byte	0x0c	; ..##..
	.byte	0x0c	; ..##..

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

; ==============================================================================
; check_board - check if maze is solved
; 
;   input:  (board) - board to be checked
;
;   ouotput: Z if solved
;
;   uses:   A, H, L
; 
	.text
	.globl	check_board
check_board:
	ld	hl,board
1:	ld	a,(hl)
	inc	hl
	cp	0xff
	ret	z
	and	BOX | GOAL
	sub	GOAL
	jp	nz,1b
	dec	a
	ret
	
	.end
