; random.s
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


; PRNG and board randomization related routines.

	.include "sudoku.inc"

; ==============================================================================
; get_rnd_puzzle - get random puzzle using seed
; 
;   input:  (HL) - destination
;	    B - difficulty level
; 
;   output: (HL) - random puzzle
; 
;   uses:   all
; 
	.text
	.globl	get_rnd_puzzle
get_rnd_puzzle:
	ld	a,(seed128 + 15)
	and	0x7f
	ld	c,a
	jp	get_puzzle
	
; ==============================================================================
; randomize_puzzle - transform puzzle using seed
; 
;   input:  (HL) - puzzle
;           (seed128) - PRNG value used for the transformation
; 
;   output: (HL) - transformed puzzle
; 
;   uses:   all
; 
	.text
	.globl	randomize_puzzle
randomize_puzzle:
	
; prepare permutation map
	push	hl
	push	hl
	ld	hl,tperm1
	push	hl
	ld	a,(seed128 + 5)
	push	af
	and	0x1f
	ld	e,a
	ld	c,9
	call	udiv8
	ld	(hl),c		; 0-8
	inc	hl
	pop	af
	rlca
	rlca
	rlca
	and	0x07
	ld	(hl),a		; 0-7
	inc	hl
	ld	a,(seed128 + 6)
	push	af
	and	0x0f
	ld	e,a
	ld	c,7
	call	udiv8
	ld	(hl),c		; 0-6
	inc	hl
	pop	af
	rra
	rra
	rra
	rra
	and	0x0f
	ld	e,a
	ld	c,6
	call	udiv8
	ld	(hl),c		; 0-5
	inc	hl
	ld	a,(seed128 + 7)
	push	af
	and	0x0f
	ld	e,a
	ld	c,5
	call	udiv8
	ld	(hl),c		; 0-4
	inc	hl
	pop	af
	rlca
	rlca
	push	af
	and	0x03
	ld	(hl),a		; 0-3
	inc	hl
	ld	a,(seed128 + 8)
	and	0x0f
	ld	e,a
	ld	c,3
	call	udiv8		; 0-2
	ld	(hl),c
	inc	hl
	pop	af
	rlca
	and	0x01
	ld	(hl),a		; 0-1
	inc	hl
	ld	(hl),0		; 0
	ld	hl,tperm2
	push	hl
	ld	b,1
1:	ld	(hl),b
	inc	hl
	inc	b
	ld	a,b
	cp	10
	jp	nz,1b
	pop	de
	pop	hl
	ld	bc,0x0900
2:	push	hl
	ld	l,(hl)
	ld	h,c
	add	hl,de
1:	ld	a,(hl)
	or	a
	jp	nz,1f
	inc	hl
	jp	1b
1:	ld	(hl),c
	pop	hl
	ld	(hl),a
	inc	hl
	dec	b
	jp	nz,2b

; apply permutation map
	pop	hl
	ld	de,tpuzzle1
	ld	bc,tperm1
	call	permute
	
	
; apply rotation map
	ld	a,(seed128 + 9)
	and	0x07
	ld	l,a
	ld	h,0
	ld	de,81
	call	mul16
	ld	de,rotmaps
	add	hl,de
	ld	b,h
	ld	c,l
	ld	hl,tpuzzle1
	ld	de,tpuzzle2
	call	transform
	
; apply band swap map
	ld	a,(seed128 + 10)
	and	0x0f
	ld	e,a
	ld	c,6
	call	udiv8
	ld	l,c
	ld	h,0
	ld	de,81
	call	mul16
	ld	de,bmaps
	add	hl,de
	ld	b,h
	ld	c,l
	ld	hl,tpuzzle2
	ld	de,tpuzzle1
	call	transform

; apply stack swap map
	ld	a,(seed128 + 11)
	rra
	rra
	rra
	rra
	and	0x0f
	ld	e,a
	ld	c,6
	call	udiv8
	ld	l,c
	ld	h,0
	ld	de,81
	call	mul16
	ld	de,smaps
	add	hl,de
	ld	b,h
	ld	c,l
	ld	hl,tpuzzle1
	ld	de,tpuzzle2
	call	transform

; apply row and column swap maps
	ld	hl,rmaps
	ld	de,cmaps
	ld	a,(seed128 + 12)
	call	1f
	ld	hl,rmaps + (6 * 81)
	ld	de,cmaps + (6 * 81)
	ld	a,(seed128 + 13)
	call	1f
	ld	hl,rmaps + (12 * 81)
	ld	de,cmaps + (12 * 81)
	ld	a,(seed128 + 14)
	call	1f

; copy puzzle back to (HL)
	ld	hl,tpuzzle2
	pop	de
	ld	b,81
	jp	copy8

1:	push	de
	push	af
	push	hl
	and	0x0f
	ld	e,a
	ld	c,6
	call	udiv8
	ld	l,c
	ld	h,0
	ld	de,81
	call	mul16
	pop	de
	add	hl,de
	ld	b,h
	ld	c,l
	ld	hl,tpuzzle2
	ld	de,tpuzzle1
	call	transform
	pop	af
	rra
	rra
	rra
	rra
	and	0x0f
	ld	e,a
	ld	c,6
	call	udiv8
	ld	l,c
	ld	h,0
	ld	de,81
	call	mul16
	pop	de
	add	hl,de
	ld	b,h
	ld	c,l
	ld	hl,tpuzzle1
	ld	de,tpuzzle2
	jp	transform

	.lcomm	tperm1, 9
	.lcomm	tperm2, 9
	.lcomm	tpuzzle1, 81
	.lcomm	tpuzzle2, 81
	
	.end
