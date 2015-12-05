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
; Matching macros
; 
	
; masks
	.equiv	MASK0, 0x07
	.equiv	MASK1, 0x38
	.equiv	MASK2, 0xe0
	.equiv	MASK3, 0x0e
	.equiv	MASK4, 0x70
	.equiv	BMASK0, 0x01
	.equiv	BMASK1, 0x02
	.equiv	BMASK2, 0x04
	.equiv	BMASK3, 0x08
	.equiv	BMASK4, 0x10

; load first color
	.macro	ldpos	p
	.if	\p < 2
	ld	a,e
	.elseif	\p == 2
	ld	a,d
	rra
	ld	a,e
	rra
	.else
	ld	a,d
	.endif
	.endm

; compare to second color
	.macro	xorpos	p
	.if	\p < 2
	xor	l
	.elseif	\p == 2
	xor	b
	.else
	xor	h
	.endif
	.endm
	
; align colors
	.macro	alpos	p,q
	.if	\p != \q
	.if	\p == 0
	.if	\q == 1
	rla
	rla
	rla
	.elseif	\q == 2
	rrca
	rrca
	rrca
	.elseif	\q == 3
	rla
	.else
	rla
	rla
	rla
	rla
	.endif
	.elseif	\p == 1
	.if	\q == 0
	rra
	rra
	rra
	.elseif	\q == 2
	rla
	rla
	.elseif	\q == 3
	rra
	rra
	.else
	rla
	.endif
	.elseif	\p == 2
	.if	\q == 0
	rlca
	rlca
	rlca
	.elseif	\q == 1
	rra
	rra
	.elseif	\q == 3
	rra
	rra
	rra
	rra
	.else
	rra
	.endif
	.elseif	\p == 3
	.if	\q == 0
	rra
	.elseif	\q == 1
	rla
	rla
	.elseif	\q == 2
	rla
	rla
	rla
	rla
	.else
	rla
	rla
	rla
	.endif
	.else
	.if	\q == 0
	rra
	rra
	rra
	rra
	.elseif	\q == 1
	rra
	.elseif	\q == 2
	rla
	.else
	rra
	rra
	rra
	.endif
	.endif
	.endif
	.endm
	
; process black pins, count
	.macro	black1	p
	ldpos	\p
	xorpos	\p
	and	MASK\p
	jp	nz,1f
	inc	c
	ld	a,b
	or	BMASK\p
	ld	b,a
1:	
	.endm

; process black pins, check
	.macro	black2	p
	ldpos	\p
	xorpos	\p
	and	MASK\p
	jp	nz,1f
	dec	c
	.if	\p < 4
	ret	m
	.else
	ret	nz
	.endif
	ld	a,b
	or	BMASK\p
	ld	b,a
1:	
	.endm

; process white pins, count
	.macro	white1	p,q
	ld	a,b
	and	BMASK\q
	jp	nz,1f
	ldpos	\p
	alpos	\p,\q
	xorpos	\q
	and	MASK\q
	jp	nz,1f
	inc	c
	ld	a,b
	or	BMASK\q
	ld	b,a
	jp	2f
1:
	.endm

; process white pins, check
	.macro	white2	p,q
	ld	a,b
	and	BMASK\q
	jp	nz,1f
	ldpos	\p
	alpos	\p,\q
	xorpos	\q
	and	MASK\q
	jp	nz,1f
	dec	c
	ret	m
	ld	a,b
	or	BMASK\q
	ld	b,a
	jp	2f
1:
	.endm

; ==============================================================================
; match - compare codes
; 
;   input:  HL, DE - codes
; 
;   output: C - result, (black_pins << 3) | white_pins
; 
;   uses:   A, B
; 
	.text
	.globl	match
match:

; prepare counter
	ld	c,0

; prepare Position 2 & bit mask
	ld	a,h
	rra
	ld	a,l
	rra
	and	0xe0
	ld	b,a

; process black pins
	.irpc	p,"01234"
	black1	\p
	.endr
	
; update counter
	ld	a,c
	add	a,a
	add	a,a
	add	a,a
	ld	c,a
	
; save mask
	ld	a,b
	ld	(temp),a
	
; process white pins
	.irpc	p,"01234"
	ld	a,(temp)
	and	BMASK\p
	jp	nz,2f
	.irpc	q,"01234"
	.if	\p != \q
	white1	\p,\q
	.endif
	.endr
2:
	.endr
	
; done
	ret

	.lcomm	temp, 1
	
; ==============================================================================
; check_match - compare codes and check against expected result
; 
;   input:  HL, DE - codes
;   	    A - expected result, (black_pins << 3) | white_pins
; 
;   output: Z on match
; 
;   uses:   A, B, C
; 
	.text
	.globl	check_match
check_match:

; prepare black pins counter
	ld	(temp),a
	rra
	rra
	rra
	and	0x07
	ld	c,a

; prepare Position 2 & bit mask
	ld	a,h
	rra
	ld	a,l
	rra
	and	0xe0
	ld	b,a

; process black pins
	.irpc	p,"01234"
	black2	\p
	.endr
	
; prepare white pins counter
	ld	a,(temp)
	and	0x07
	ld	c,a

; save mask
	ld	a,b
	ld	(temp),a
	
; process white pins
	.irpc	p,"01234"
	ld	a,(temp)
	and	BMASK\p
	jp	nz,2f
	.irpc	q,"01234"
	.if	\p != \q
	white2	\p,\q
	.endif
	.endr
2:
	.endr
	
; report match
	ld	a,c
	or	a
	ret

	.end