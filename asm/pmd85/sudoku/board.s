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
	
	.equiv	ULC, 0xc4c6		; upper left corner of the board
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
; draw_digit - draw digit
; 
;   input:  B - digit
;           C - square
;           E - color mask
;
;   uses:   all
; 
	.text
	.global	draw_digit
draw_digit:
	push	de
	ld	a,b
	add	a,a
	add	a,a
	add	a,a
	ld	b,a
	add	a,a
	add	a,b
	ld	e,a
	ld	d,0
	ld	hl,digits
	add	hl,de
	ex	de,hl
	ld	hl,ULC + 514
	call	sq2a
	pop	bc
	ld	b,12
1:	ld	a,(de)
	xor	c
	ld	(hl),a
	inc	hl
	inc	de
	ld	a,(de)
	xor	c
	ld	(hl),a
	dec	b
	ret	z
	push	bc
	ld	bc,63
	add	hl,bc
	pop	bc
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
; draw_excl - draw exclamation mark
; 
;   input:  C - square
;           E - color mask
;
;   uses:   all
; 
	.text
	.global	draw_excl
draw_excl:
	ld	hl,ULC + 833
	call	sq2a
	ld	c,e
	ld	de,excl
	ld	b,7
1:	ld	a,(de)
	xor	c
	ld	(hl),a
	dec	b
	ret	z
	push	bc
	ld	bc,64
	add	hl,bc
	pop	bc
	inc	de
	jp	1b
	
; ==============================================================================
; Exclamation mark
;
	.data
excl:	.byte	0x04	; ..#...
	.byte	0x04	; ..#...
	.byte	0x04	; ..#...
	.byte	0x04	; ..#...
	.byte	0x04	; ..#...
	.byte	0x00	; ......
	.byte	0x04	; ..#...
	
; ==============================================================================
; clr_excl - clear exclamation mark
; 
;   input:  C - square
;
;   uses:   all
; 
	.text
	.global	clr_excl
clr_excl:
	ld	hl,ULC + 833
	call	sq2a
	ld	b,7
	ld	de,64
	xor	a
1:	ld	(hl),a
	dec	b
	ret	z
	add	hl,de
	jp	1b
	
; ==============================================================================
; Digits
;
	.data
digits:	

; 1
	.word	0x0018	; ...##...
	.word	0x001c	; ..###...
	.word	0x001e	; .####...
	.word	0x0018	; ...##...
	.word	0x0018	; ...##...
	.word	0x0018	; ...##...
	.word	0x0018	; ...##...
	.word	0x0018	; ...##...
	.word	0x0018	; ...##...
	.word	0x0018	; ...##...
	.word	0x0018	; ...##...
	.word	0x0018	; ...##...
; 2
	.word	0x013e	; .######.
	.word	0x033f	; ########
	.word	0x0303	; ##....##
	.word	0x0300	; ......##
	.word	0x0320	; .....###
	.word	0x0130	; ....###.
	.word	0x0038	; ...###..
	.word	0x001c	; ..###...
	.word	0x000e	; .###....
	.word	0x0007	; ###.....
	.word	0x033f	; ########
	.word	0x033f	; ########
; 3
	.word	0x013e	; .######.
	.word	0x033f	; ########
	.word	0x0303	; ##....##
	.word	0x0300	; ......##
	.word	0x0300	; ......##
	.word	0x0130	; ....###.
	.word	0x0130	; ....###.
	.word	0x0300	; ......##
	.word	0x0300	; ......##
	.word	0x0303	; ##....##
	.word	0x033f	; ########
	.word	0x013e	; .######.
; 4
	.word	0x0300	; ......##
	.word	0x0320	; .....###
	.word	0x0330	; ....####
	.word	0x0338	; ...#####
	.word	0x031c	; ..###.##
	.word	0x030e	; .###..##
	.word	0x0307	; ###...##
	.word	0x033f	; ########
	.word	0x033f	; ########
	.word	0x0300	; ......##
	.word	0x0300	; ......##
	.word	0x0300	; ......##
; 5
	.word	0x033f	; ########
	.word	0x033f	; ########
	.word	0x0003	; ##......
	.word	0x0003	; ##......
	.word	0x0003	; ##......
	.word	0x013f	; #######.
	.word	0x033f	; ########
	.word	0x0300	; ......##
	.word	0x0300	; ......##
	.word	0x0303	; ##....##
	.word	0x033f	; ########
	.word	0x013e	; .######.
; 6
	.word	0x013e	; .######.
	.word	0x033f	; ########
	.word	0x0303	; ##....##
	.word	0x0003	; ##......
	.word	0x0003	; ##......
	.word	0x013f	; #######.
	.word	0x033f	; ########
	.word	0x0303	; ##....##
	.word	0x0303	; ##....##
	.word	0x0303	; ##....##
	.word	0x033f	; ########
	.word	0x013e	; .######.
; 7
	.word	0x033f	; ########
	.word	0x033f	; ########
	.word	0x0300	; ......##
	.word	0x0320	; .....###
	.word	0x0130	; ....###.
	.word	0x0038	; ...###..
	.word	0x001c	; ..###...
	.word	0x000c	; ..##...
	.word	0x000c	; ..##...
	.word	0x000c	; ..##...
	.word	0x000c	; ..##...
	.word	0x000c	; ..##...
; 8
	.word	0x013e	; .######.
	.word	0x033f	; ########
	.word	0x0303	; ##....##
	.word	0x0303	; ##....##
	.word	0x0303	; ##....##
	.word	0x013e	; .######.
	.word	0x013e	; .######.
	.word	0x0303	; ##....##
	.word	0x0303	; ##....##
	.word	0x0303	; ##....##
	.word	0x033f	; ########
	.word	0x013e	; .######.
; 9
	.word	0x013e	; .######.
	.word	0x033f	; ########
	.word	0x0303	; ##....##
	.word	0x0303	; ##....##
	.word	0x0303	; ##....##
	.word	0x033f	; ########
	.word	0x033e	; .#######
	.word	0x0300	; ......##
	.word	0x0300	; ......##
	.word	0x0303	; ##....##
	.word	0x033f	; ########
	.word	0x013e	; .######.

; ==============================================================================
; draw_cursor - draw cursor
; 
;   input:  C - square
;
;   uses:   all
; 
	.text
	.global	draw_cursor
draw_cursor:
	ld	hl,ULC + 193
	call	sq2a
	ld	(hl),0x0f
	dec	hl
	ld	bc,0x0520
	call	1f
	ld	de,-317
	add	hl,de
	ld	(hl),0x38
	inc	hl
	ld	a,(hl)
	or	0x03
	ld	(hl),a
	ld	de,64
	add	hl,de
	ld	bc,0x0402
	call	2f
	ld	de,700
	add	hl,de
	ld	bc,0x0420
	call	1f
	ld	a,(hl)
	or	c
	ld	(hl),a
	inc	hl
	ld	(hl),0x0f
	ld	de,-253
	add	hl,de
	ld	bc,0x0402
	call	1f
	ld	a,(hl)
	or	0x03
	ld	(hl),a
	dec	hl
	ld	(hl),0x38
	ret
1:	ld	de,64
2:	ld	a,(hl)
	or	c
	ld	(hl),a
	add	hl,de
	dec	b
	jp	nz,2b
	ret
	
; ==============================================================================
; clr_cursor - clear cursor
; 
;   input:  C - square
;
;   uses:   all
; 
	.text
	.global	clr_cursor
clr_cursor:
	ld	hl,ULC + 193
	call	sq2a
	ld	(hl),0
	dec	hl
	ld	bc,0x05df
	call	1f
	ld	de,-317
	add	hl,de
	ld	(hl),0
	inc	hl
	ld	a,(hl)
	and	0xfc
	ld	(hl),a
	ld	de,64
	add	hl,de
	ld	bc,0x04fd
	call	2f
	ld	de,700
	add	hl,de
	ld	bc,0x04df
	call	1f
	ld	a,(hl)
	and	c
	ld	(hl),a
	inc	hl
	ld	(hl),0
	ld	de,-253
	add	hl,de
	ld	bc,0x04fd
	call	1f
	ld	a,(hl)
	and	0xfc
	ld	(hl),a
	dec	hl
	ld	(hl),0
	ret
1:	ld	de,64
2:	ld	a,(hl)
	and	c
	ld	(hl),a
	add	hl,de
	dec	b
	jp	nz,2b
	ret
	
; ==============================================================================
; write - display one character
; 
;   input:  A - character
;           (HL) - destination
;           (color) - color mask
; 
;   uses:   A, H, L
; 
	.text
	.globl	write
write:
	ld	(cursor),hl
	jp	prtout
	
; ==============================================================================
; writeln - display zero-terminated string
; 
;   input:  (HL) - string
;           (DE) - destination
;           (color) - color mask
; 
;   uses:   A, H, L
; 
	.text
	.globl	writeln
writeln:
	ex	de,hl
	ld	(cursor),hl
1:	ld	a,(de)
	or	a
	ret	z
	call	prtout
	inc	de
	jp	1b
	
; ==============================================================================
; clr_msg - clear the notification area
; 
;   uses:   all
; 
	.text
	.globl	clr_msg
clr_msg:
	ld	a,(msg)
	or	a
	ret	z
	ld	hl,MSGAREA
	ld	de,MSGAREA + 48 - (64 * 11)
	ld	b,10
	call	part_erase
	xor	a
	ld	(msg),a
	ret

	.lcomm	msg, 1
	
; ==============================================================================
; disp_msg - display message in the notification area
; 
;   input:  (HL) - string
;           (color) - color mask
; 
;   uses:   all
; 
	.text
	.globl	disp_msg
disp_msg:
	push	hl
	call	clr_msg
	pop	hl
	ld	de,MSGAREA
	call	writeln
	ld	a,1
	ld	(msg),a
	ret

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

	.globl	ONEDOT
	.equiv	ONEDOT, 0x81
	; 80
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x04	; ..#...
	.byte	0x06	; .##...
	.byte	0x04	; ..#...
	.byte	0x04	; ..#...
	.byte	0x04	; ..#...
	.byte	0x04	; ..#...
	.byte	0x2e	; .###.#
	.byte	0x00	; ......

	.end
