; board.S
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

	.include "reversi.inc"
	
; ==============================================================================
; Constants
	
ULC	equ	0xce15		; the upper left corner
	
; ==============================================================================
; draw_board - draw board
; 
;   uses:   all
; 
	.global	draw_board
draw_board:
	ld	hl,ULC
	call	2f
	ld	b,7
1:	call	3f
	call	4f
	dec	b
	jp	nz,1b
	call	3f
2:	ld	b,24		; draw solid horizontal line
1:	ld	(hl),0x3f
	inc	hl
	dec	b
	jp	nz,1b
	ld	(hl),0x01
	jp	7f
4:	ld	a,24		; draw dotted horizontal line
1:	ld	(hl),0x15
	inc	hl
	dec	a
	jp	nz,1b
	ld	(hl),0x01
	jp	7f
3:	ld	c,8		; draw one row
1:	call	5f
	call	6f
	dec	c
	jp	nz,1b
5:	ld	(hl),0x01	; draw odd line
	ld	de,24
	add	hl,de
	ld	(hl),0x01
	jp	7f
6:	ld	a,9		; draw even line
1:	ld	(hl),0x01
	inc	hl
	inc	hl
	inc	hl
	dec	a
	jp	nz,1b
	ld	de,37
	jp	1f
7:	ld	de,40		; adjust HL for the next line
1:	add	hl,de
	ret

; ==============================================================================
; draw_labels - draw labels
; 
;   uses:   A, D, E, H, L
; 
	.global	draw_labels
draw_labels:
	ld	hl,ULC - 127
	ld	a,'A'
1:	call	2f
	ld	de,10240
	add	hl,de
	call	2f
	ld	de,-10237
	add	hl,de
	inc	a
	cp	'I'
	jp	nz,1b
	ld	hl, ULC + 894
	ld	a,'1'
1:	call	2f
	ld	de,27
	add	hl,de
	call	2f
	ld	de,1125
	add	hl,de
	inc	a
	cp	'9'
	jp	nz,1b
	ret
2:	push	af
	call	ECHO
	pop	af
	ret
	
; ==============================================================================
; draw_shape - draw shape
; 
;   input:  A - shape
;                  0 = black disk
;           C - square
;
;   uses:   all
; 
	.global	draw_shape
draw_shape:
	ld	hl,shapes
	ld	de,51
1:	dec	a
	jp	m,1f
	add	hl,de
	jp	1b
1:	push	hl
	ld	b,c
	ld	a,b
	and	0x38
	ld	e,a
	ld	d,0
	ld	hl,144
	push	bc
	call	mul16
	pop	bc
	ld	a,c
	and	0x07
	ld	e,a
	add	a,a
	add	a,e
	ld	e,a
	ld	d,0
	add	hl,de
	ld	de,ULC + 64
	add	hl,de
	pop	de
	ld	b,17
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
	push	de
	ld	de,62
	add	hl,de
	pop	de
	inc	de
	dec	b
	jp	nz,1b
	ret
	
; ==============================================================================
; shapes
shapes:
	
; blank square
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00

; black disk
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x1c, 0x00
	.byte	0x00, 0x23, 0x01
	.byte	0x20, 0x00, 0x02
	.byte	0x10, 0x00, 0x04
	.byte	0x08, 0x00, 0x08
	.byte	0x08, 0x00, 0x08
	.byte	0x04, 0x00, 0x10
	.byte	0x04, 0x00, 0x10
	.byte	0x04, 0x00, 0x10
	.byte	0x08, 0x00, 0x08
	.byte	0x08, 0x00, 0x08
	.byte	0x10, 0x00, 0x04
	.byte	0x20, 0x00, 0x02
	.byte	0x00, 0x23, 0x01
	.byte	0x00, 0x1c, 0x00
	.byte	0x00, 0x00, 0x00

; white disk
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x1c, 0x00
	.byte	0x00, 0x3f, 0x01
	.byte	0x20, 0x3f, 0x03
	.byte	0x30, 0x3f, 0x07
	.byte	0x38, 0x3f, 0x0f
	.byte	0x38, 0x3f, 0x0f
	.byte	0x3c, 0x3f, 0x1f
	.byte	0x3c, 0x3f, 0x1f
	.byte	0x3c, 0x3f, 0x1f
	.byte	0x38, 0x3f, 0x0f
	.byte	0x38, 0x3f, 0x0f
	.byte	0x30, 0x3f, 0x07
	.byte	0x20, 0x3f, 0x03
	.byte	0x00, 0x3f, 0x01
	.byte	0x00, 0x1c, 0x00
	.byte	0x00, 0x00, 0x00

; curson on blank square
	.byte	0x2a, 0x2a, 0x2a
	.byte	0x14, 0x15, 0x15
	.byte	0x2a, 0x2a, 0x2a
	.byte	0x14, 0x15, 0x15
	.byte	0x2a, 0x2a, 0x2a
	.byte	0x14, 0x15, 0x15
	.byte	0x2a, 0x2a, 0x2a
	.byte	0x14, 0x15, 0x15
	.byte	0x2a, 0x2a, 0x2a
	.byte	0x14, 0x15, 0x15
	.byte	0x2a, 0x2a, 0x2a
	.byte	0x14, 0x15, 0x15
	.byte	0x2a, 0x2a, 0x2a
	.byte	0x14, 0x15, 0x15
	.byte	0x2a, 0x2a, 0x2a
	.byte	0x14, 0x15, 0x15
	.byte	0x2a, 0x2a, 0x2a
	

	.end
 
