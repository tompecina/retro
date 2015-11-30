; game.s
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


; Various game related routines.

	.include "logik.inc"
	
; ==============================================================================
; Constants
;

; ==============================================================================
; init_btbl - initialize the bit count table
; 
;   output: (bitcounts) populated
; 
;   uses:   A, B, C, H, L
; 
	.text
	.globl	init_btbl
init_btbl:
	ld	hl,bitcounts
	ld	c,0
3:	ld	b,0
	ld	a,c
1:	or	a
	jp	z,2f
	rra
	jp	nc,1b
	inc	b
	jp	1b
2:	ld	(hl),b
	inc	hl
	inc	c
	jp	nz,3b
	ret
	
; ==============================================================================
; count_bits - count bits in an 8-byte array
; 
;   input:  (HL) - array
; 
;   output: A - number of bits
; 
;   uses:   B, D, E, H, L
; 
	.text
	.globl	count_bits
count_bits:
	ld	de,bitcounts
	ld	b,8
	xor	a
1:	push	hl
	ld	l,(hl)
	ld	h,0
	add	hl,de
	add	a,(hl)
	pop	hl
	inc	hl
	dec	b
	jp	nz,1b
	ret

	.lcomm	bitcounts, 256
	
; ==============================================================================
; compare - compare codes
; 
;   input:  HL, DE - codes
; 
;   output: A - result, (black_pins << 3) | white_pins
; 
;   uses:   all
; 
	.text
	.globl	compare
compare:
	push	hl
	ld	b,0
	ld	a,h
	xor	d
	ld	h,a
	ld	a,l
	xor	e
	ld	l,a
	add	hl,hl
	ld	a,h
	add	hl,hl
	and	0xe0
	jp	nz,1f
	inc	b
1:	ld	a,h
	and	0x38
	jp	nz,1f
	inc	b
1:	ld	a,h
	and	0x03
	jp	nz,1f
	inc	b
1:	ld	a,l
	and	0xe0
	jp	nz,1f
	inc	b
1:	ld	a,l
	and	0x1c
	jp	nz,1f
	inc	b
1:	
	
	
	
	.end
