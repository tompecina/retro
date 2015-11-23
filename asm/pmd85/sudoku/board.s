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

	.include "sudoku.inc"
	
; ==============================================================================
; Constants
	
	.equiv	ULC, 0xc005		; upper left corner of the board
	.equiv	MSGAREA, 0xffc0		; position of the notification area
	
; ==============================================================================
; draw_board - draw board
; 
;   uses:   all
; 
	.text
	.globl	draw_board
draw_board:
	ld	hl,ULC
	call	7f
	call	7f
	ld	a,3
1:	push	af
	call	5f
	call	6f
	call	5f
	call	6f
	call	5f
	call	7f
	pop	af
	dec	a
	jp	nz,1b
7:	ld	(hl),0x3c		; solid line
	inc	hl
	ld	bc,(35 << 8) | 0x3f
1:	ld	(hl),c
	inc	hl
	dec	b
	jp	nz,1b
	ld	(hl),0x1f
2:	ld	de,28
	add	hl,de
	ret
5:	ld	a,5
1:	call	3f
	call	4f
	dec	a
	jp	nz,1b
	call	3f
	ret
3:	call	7f
	call	7f
7:	ld	(hl),0x0c		; odd line
	ld	de,12
	ld	bc,(2 << 8) | 0x08
1:	add	hl,de
	ld	(hl),0x08
	dec	b
	jp	nz,1b
	add	hl,de
	ld	(hl),0x18
	jp	2b
4:	ld	(hl),0x0c		; even line
	ld	de,4
	ld	bc,(8 << 8) | 0x08
	jp	1b
6:	ld	(hl),0x0c		; dotted line
	inc	hl
	ld	bc,(17 << 8) | 0x22
1:	ld	(hl),c
	inc	hl
	ld	(hl),0x08
	inc	hl
	dec	b
	jp	nz,1b
	ld	(hl),c
	inc	hl
	ld	(hl),0x18
	jp	2b
	
; ==============================================================================
; sq2rc - decompress square
; 
;   input:  C - square
; 
;   output: B - row
;	    C - column
; 
;   uses:   A
; 
	.text
	.globl	sq2rc
sq2rc:
	ld	a,c
	ld	bc,0xff09
1:	sub	c
	inc	b
	jp	nc,1b
	add	a,c
	ld	c,a
	ret
	
; ==============================================================================
; draw_shape - draw shape
; 
;   input:  (HL) - shape
;           C - square
;
;   uses:   all
; 
	.text
	.global	draw_shape
draw_shape:
	push	hl
	ld	hl,ULC + 321
	call	sq2a
	pop	de
	ld	b,18
1:	ld	a,(de)
	ld	c,a
	ld	a,(hl)
	and	1
	or	c
	ld	(hl),a
	inc	hl
	inc	de
	ld	a,(de)
	ld	(hl),a
	inc	hl
	inc	de
	ld	a,(de)
	ld	(hl),a
	dec	b
	ret	z
	ld	a,b
	ld	bc,62
	add	hl,bc
	ld	b,a
	inc	de
	jp	1b
sq2a:	call	sq2rc
	ld	a,b
	add	a,a
	ld	b,a
	add	a,a
	add	a,b
	ld	b,a
	ld	a,c
	add	a,a
	add	a,a
	ld	c,a
	add	hl,bc
	ret
	
; ==============================================================================
; prep_digits - prepare digits
; 
;   uses:   all
; 
	.text
	.globl	prep_digits
prep_digits:
	ld	hl,digits
	ld	c,0x00
	call	3f
	ld	c,0x40
3:	ld	de,rdigits
	ld	b,9
1:	push	bc
	call	2f
	ld	b,12
3:	ld	(hl),c
	inc	hl
	ld	a,(de)
	and	0x3f
	or	c
	ld	(hl),a
	inc	hl
	ld	a,(de)
	rlca
	rlca
	and	0x03
	or	c
	ld	(hl),a
	inc	hl
	inc	de
	dec	b
	jp	nz,3b
	call	2f
	call	2f
	pop	bc
	dec	b
	jp	nz,1b
	ret
2:	push	bc
	ld	b,6
1:	ld	(hl),c
	inc	hl
	dec	b
	jp	nz,1b
	pop	bc
	ret

	.globl	digits
	.lcomm	digits, 972
	
; ==============================================================================
; Raw digits

	.data
rdigits:	
	
; 1
	.byte	0x18	; ...##...
	.byte	0x1c	; ..###...
	.byte	0x1e	; .####...
	.byte	0x18	; ...##...
	.byte	0x18	; ...##...
	.byte	0x18	; ...##...
	.byte	0x18	; ...##...
	.byte	0x18	; ...##...
	.byte	0x18	; ...##...
	.byte	0x18	; ...##...
	.byte	0x18	; ...##...
	.byte	0x18	; ...##...
; 2
	.byte	0x7e	; .######.
	.byte	0xff	; ########
	.byte	0xc3	; ##....##
	.byte	0xc0	; ......##
	.byte	0xe0	; .....###
	.byte	0x70	; ....###.
	.byte	0x38	; ...###..
	.byte	0x1c	; ..###...
	.byte	0x0e	; .###....
	.byte	0x07	; ###.....
	.byte	0xff	; ########
	.byte	0xff	; ########
; 3
	.byte	0x7e	; .######.
	.byte	0xff	; ########
	.byte	0xc3	; ##....##
	.byte	0xc0	; ......##
	.byte	0xc0	; ......##
	.byte	0x70	; ....###.
	.byte	0x70	; ....###.
	.byte	0xc0	; ......##
	.byte	0xc0	; ......##
	.byte	0xc3	; ##....##
	.byte	0xff	; ########
	.byte	0x7e	; .######.
; 4
	.byte	0xc0	; ......##
	.byte	0xe0	; .....###
	.byte	0xf0	; ....####
	.byte	0xf8	; ...#####
	.byte	0xdc	; ..###.##
	.byte	0xce	; .###..##
	.byte	0xc7	; ###...##
	.byte	0xff	; ########
	.byte	0xff	; ########
	.byte	0xc0	; ......##
	.byte	0xc0	; ......##
	.byte	0xc0	; ......##
; 5
	.byte	0xff	; ########
	.byte	0xff	; ########
	.byte	0x03	; ##......
	.byte	0x03	; ##......
	.byte	0x03	; ##......
	.byte	0x7f	; #######.
	.byte	0xff	; ########
	.byte	0xc0	; ......##
	.byte	0xc0	; ......##
	.byte	0xc3	; ##....##
	.byte	0xff	; ########
	.byte	0x7e	; .######.
; 6
	.byte	0x7e	; .######.
	.byte	0xff	; ########
	.byte	0xc3	; ##....##
	.byte	0x03	; ##......
	.byte	0x03	; ##......
	.byte	0x7f	; #######.
	.byte	0xff	; ########
	.byte	0xc3	; ##....##
	.byte	0xc3	; ##....##
	.byte	0xc3	; ##....##
	.byte	0xff	; ########
	.byte	0x7e	; .######.
; 7
	.byte	0xff	; ########
	.byte	0xff	; ########
	.byte	0xc0	; ......##
	.byte	0xe0	; .....###
	.byte	0x70	; ....###.
	.byte	0x38	; ...###..
	.byte	0x1c	; ..###...
	.byte	0x0c	; ..##...
	.byte	0x0c	; ..##...
	.byte	0x0c	; ..##...
	.byte	0x0c	; ..##...
	.byte	0x0c	; ..##...
; 8
	.byte	0x7e	; .######.
	.byte	0xff	; ########
	.byte	0xc3	; ##....##
	.byte	0xc3	; ##....##
	.byte	0xc3	; ##....##
	.byte	0x7e	; .######.
	.byte	0x7e	; .######.
	.byte	0xc3	; ##....##
	.byte	0xc3	; ##....##
	.byte	0xc3	; ##....##
	.byte	0xff	; ########
	.byte	0x7e	; .######.
; 9
	.byte	0x7e	; .######.
	.byte	0xff	; ########
	.byte	0xc3	; ##....##
	.byte	0xc3	; ##....##
	.byte	0xc3	; ##....##
	.byte	0xff	; ########
	.byte	0xfe	; .#######
	.byte	0xc0	; ......##
	.byte	0xc0	; ......##
	.byte	0xc3	; ##....##
	.byte	0xff	; ########
	.byte	0x7e	; .######.

; ==============================================================================
; add_cust_glyphs - add custom glyphs
;
;   uses:   H, L
;
	.text
	.globl	add_cust_glyphs
add_cust_glyphs:	
	ld	hl,glyphs80 + 10
	ld	(tascii + 0x08),hl
	ret
	
; ==============================================================================
; Custom glyphs
;
	.data
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
