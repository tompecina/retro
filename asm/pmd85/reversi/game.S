; game.S
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

	.include "reversi.inc"
	
; ==============================================================================
; Constants
;
	.equiv	END, 0x7f
	.equiv	RMASK, 0xb8
	.equiv	CMASK, 0x47

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
	.global	count_bits
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
	
; ==============================================================================
; init_rvt - initialize table of row values
; 
;   output: (rbvt) - initialized table
; 
;   uses:   all
;
	.text
	.global	init_rvt
init_rvt:
	ld	hl,sqv
	push	hl
	ld	de,rvt
	ld	h,4
4:	ld	l,0
3:	ld	c,0
	ld	a,l
	ld	b,8
	ex	(sp),hl
	push	hl
2:	rra
	jp	nc,1f
	push	af
	ld	a,(hl)
	add	a,c
	ld	c,a
	pop	af
1:	inc	hl
	dec	b
	jp	nz,2b
	ld	a,c
	ld	(de),a
	inc	de
	pop	hl
	ex	(sp),hl
	inc	l
	jp	nz,3b
	dec	h
	jp	z,1f
	ex	(sp),hl
	ld	bc,8
	add	hl,bc
	ex	(sp),hl
	jp	4b
1:	pop	hl
	ret

	.lcomm	rvt, 1024
	
; ==============================================================================
; calc_tbv - calculate total board square value
; 
;   input:  (HL) - array of my discs
;           (DE) - array of opponent's discs
; 
;   output: HL - my total board square value
;           DE - opponent's total board square value
; 
;   uses:   all
; 
	.text
	.global	calc_tbv
calc_tbv:
	ld	b,h
	ld	c,l
	ld	hl,0
	ld	(myval),hl
	ld	(opval),hl
	ld	hl,rvt
	ld	a,4
1:	call	2f
	inc	h
	dec	a
	jp	nz,1b
	ld	a,4
1:	dec	h
	call	2f
	dec	a
	jp	nz,1b
	ld	hl,(opval)
	ex	de,hl
	ld	hl,(myval)
	ret
2:	push	af
	push	hl
	push	de
	ld	a,(bc)
	ld	e,a
	ld	d,0
	add	hl,de
	ld	a,(hl)
	call	signexde
	ld	hl,(myval)
	add	hl,de
	ld	(myval),hl
	pop	de
	pop	hl
	push	hl
	push	de
	ld	a,(de)
	ld	e,a
	ld	d,0
	add	hl,de
	ld	a,(hl)
	call	signexde
	ld	hl,(opval)
	add	hl,de
	ld	(opval),hl
	pop	de	
	pop	hl
	pop	af
	inc	bc
	inc	de
	ret
	
	.lcomm	myval, 2
	.lcomm	opval, 2
	
; ==============================================================================
; count_fdiscs - count frontier discs
; 
;   input:  (HL) - array of my discs
;           (DE) - array of opponent's discs
; 
;   output: B - number of my frontier discs
;           C - number of opponent's frontier discs
; 
;   uses:   all
; 
	.text
	.global	count_fdiscs
count_fdiscs:
	xor	a
	ld	(myfd),a
	ld	(opfd),a
	push	hl
	push	de
	ld	bc,tarray
	ld	a,8
1:	push	af
	ld	a,(de)
	or	(hl)
	cpl
	push	bc
	ld	b,a
	add	a,a
	or	b
	ld	c,a
	ld	a,b
	rra
	or	c
	pop	bc
	ld	(bc),a
	inc	hl
	inc	de
	inc	bc
	pop	af
	dec	a
	jp	nz,1b
	ld	hl,tarray
	ld	bc,0x0700
	ld	d,(hl)
1:	inc	hl
	ld	e,(hl)
	ld	a,c
	or	d
	or	e
	dec	hl
	ld	(hl),a
	inc	hl
	ld	c,d
	ld	d,e
	ld	e,(hl)
	dec	b
	jp	nz,1b
	ld	a,c
	or	d
	ld	(hl),a
	pop	de
	pop	hl
	ld	bc,tarray
	ld	a,8
1:	push	af
	ld	a,(bc)
	and	(hl)
	inc	hl
	push	hl
	push	de
	ld	hl,bitcounts
	ld	e,a
	ld	d,0
	add	hl,de
	ld	a,(hl)
	ld	hl,myfd
	add	a,(hl)
	ld	(hl),a
	pop	hl
	ld	a,(bc)
	and	(hl)
	inc	hl
	push	hl
	ld	hl,bitcounts
	ld	e,a
	add	hl,de
	ld	a,(hl)
	ld	hl,opfd
	add	a,(hl)
	ld	(hl),a
	pop	de
	pop	hl
	inc	bc
	pop	af
	dec	a
	jp	nz,1b
	ld	a,(myfd)
	ld	b,a
	ld	a,(opfd)
	ld	c,a
	ret
	
	.lcomm	tarray, 8
	.lcomm	myfd, 1
	.lcomm	opfd, 1
	
; ==============================================================================
; count_cdiscs - count corner and next-to-empty-corner discs
; 
;   input:  (HL) - array of my discs
;           (DE) - array of opponent's discs
; 
;   output: B - number of my corner discs
;           C - number of opponent's corner discs
;           D - number of my next-to-empty-corner discs
;           E - number of opponent's next-to-empty-corner discs
; 
;   uses:   all
; 
	.text
	.global	count_cdiscs
count_cdiscs:
	xor	a
	ld	(mycd),a
	ld	(opcd),a
	ld	(myncd),a
	ld	(opncd),a
	push	hl
	push	de
	ld	b,(hl)
	inc	hl
	ld	c,(hl)
	ex	de,hl
	ld	a,(hl)
	inc	hl
	ld	e,(hl)
	ld	d,a
	call	2f
	pop	hl
	pop	de
	ld	bc,7
	add	hl,bc
	ex	de,hl
	add	hl,bc
	ld	b,(hl)
	inc	hl
	ld	c,(hl)
	ex	de,hl
	ld	a,(hl)
	inc	hl
	ld	e,(hl)
	ld	d,a
	call	2f
	ld	a,(mycd)
	ld	b,a
	ld	a,(opcd)
	ld	c,a
	ld	a,(myncd)
	ld	d,a
	ld	a,(opncd)
	ld	e,a
	ret
2:	push	bc
	push	de
	call	2f
	pop	de
	pop	bc
	ld	a,b
	rlca
	rlca
	ld	h,a
	ld	a,b
	rla
	ld	a,h
	rla
	ld	a,c
	rlca
	rlca
	ld	c,a
	ld	a,d
	rlca
	rlca
	ld	h,a
	ld	a,d
	rla
	ld	a,h
	rla
	ld	a,e
	rlca
	rlca
	ld	e,a
2:	ld	a,b
	rra
	jp	nc,1f
	ld	hl,mycd
	inc	(hl)
	ret
1:	ld	a,d
	rra
	jp	nc,1f
	ld	hl,opcd
	inc	(hl)
	ret
1:	ld	hl,opncd
	rra
	jp	nc,1f
	inc	(hl)
1:	ld	a,e
	rra
	jp	nc,1f
	inc	(hl)
1:	ld	hl,myncd
	ld	a,b
	rra
	rra
	jp	nc,1f
	inc	(hl)
1:	ld	a,c
	rra
	jp	nc,1f
	inc	(hl)
1:	rra
	ret	nc
	inc	(hl)
	ret
	
	.lcomm	mycd, 1
	.lcomm	opcd, 1
	.lcomm	myncd, 1
	.lcomm	opncd, 1
	
; ==============================================================================
; dir_pat - create directions pattern in situ
; 
;   input:  (HL) - pattern
; 
;   uses:   A, B, C, H, L
; 
	.text
	.global	dir_pat
dir_pat:
	ld	b,0
	ld	a,(hl)
	ld	(hl),b
	inc	hl
	.rept	7
	add	a,a
	ld	c,(hl)
	ld	(hl),a
	ld	a,c
	inc	hl
	.endr
	ld	a,(hl)
	ld	(hl),b
	inc	hl
	.rept	7
	ld	c,(hl)
	ld	(hl),a
	ld	a,c
	inc	hl
	.endr
	ld	a,(hl)
	ld	(hl),b
	inc	hl
	.rept	7
	or	a
	rra
	ld	c,(hl)
	ld	(hl),a
	ld	a,c
	inc	hl
	.endr
	.rept	8
	ld	a,(hl)
	add	a,a
	ld	(hl),a
	inc	hl
	.endr
	.rept	8
	ld	a,(hl)
	or	a
	rra
	ld	(hl),a
	inc	hl
	.endr
	ld	c,23
	add	hl,bc
	ld	a,(hl)
	ld	(hl),b
	dec	hl
	.rept	7
	or	a
	rra
	ld	c,(hl)
	ld	(hl),a
	ld	a,c
	dec	hl
	.endr
	ld	a,(hl)
	ld	(hl),b
	dec	hl
	.rept	7
	ld	c,(hl)
	ld	(hl),a
	ld	a,c
	dec	hl
	.endr
	ld	a,(hl)
	ld	(hl),b
	dec	hl
	.rept	7
	add	a,a
	ld	c,(hl)
	ld	(hl),a
	ld	a,c
	dec	hl
	.endr
	ret
	
; ==============================================================================
; all_legal - create array of all legal moves
; 
;   input:  (HL) - array of my discs
;           (DE) - array of opponent's discs
; 
;   output: (HL) - array of legal moves
; 
;   uses:   all
; 
	.text
	.global	all_legal
all_legal:

; create array of empty squares
	push	de
	push	hl
	ld	bc,sfield
	push	bc
	ld	a,8
1:	push	af
	ld	a,(de)
	inc	de
	or	(hl)
	inc	hl
	cpl
	ld	(bc),a
	inc	bc
	pop	af
	dec	a
	jp	nz,1b

; extend to whole sfield
	pop	hl
	ld	d,7
	call	2f

; populate mfield and ofield
	pop	hl
	ld	bc,mfield
	ld	d,8
	call	2f
	pop	hl
	ld	bc,ofield
	ld	d,8
	call	2f

; zero larray
	ld	hl,larray
	ld	b,8
	call	zerofill

; check sfield
	call	3f
		
; shift mfield and ofield
	ld	hl,mfield
	call	dir_pat
	ld	hl,ofield
	call	dir_pat

; update sfield
5:	ld	hl,sfield
	ld	de,ofield
	ld	b,64
1:	ld	a,(de)
	inc	de
	and	(hl)
	ld	(hl),a
	inc	hl
	dec	b
	jp	nz,1b 

; check sfield
	call	3f

; shift mfield and ofield
	ld	hl,mfield
	call	dir_pat
	ld	hl,ofield
	call	dir_pat

; update larray
	ld	hl,sfield
	ld	bc,mfield
	ld	a,8
4:	push	af
	ld	a,8
	ld	de,larray
1:	push	af
	ld	a,(bc)
	inc	bc
	and	(hl)
	inc	hl
	ex	de,hl
	or	(hl)
	ld	(hl),a
	inc	hl
	ex	de,hl
	pop	af
	dec	a
	jp	nz,1b
	pop	af
	dec	a
	jp	z,1f
	jp	4b
1:	jp	5b
	
; extend array to field
2:	push	hl
	ld	e,8
1:	ld	a,(hl)
	inc	hl
	ld	(bc),a
	inc	bc
	dec	e
	jp	nz,1b
	pop	hl
	dec	d
	jp	nz,2b
	ret

; check sfield and return if zero
3:	ld	hl,sfield
	ld	b,64
1:	ld	a,(hl)
	inc	hl
	or	a
	ret	nz
	dec	b
	jp	nz,1b
	pop	hl
7:	ld	hl,larray
	ret
	
; ==============================================================================
; one_legal - check if move is legal and return an array of captured discs
; 
;   input:  (HL) - array of my discs
;           (DE) - array of opponent's discs
;           C - move
; 
;   output: CY - on illegal move
;           (HL) - array of captured disc numbers in different directions
; 
;   uses:   all
; 
	.text
	.global	one_legal
one_legal:
	
; populate mfield and ofield
	push	bc
	push	de
	ld	bc,mfield
	ld	d,8
	call	2b
	pop	hl
	ld	bc,ofield
	ld	d,8
	call	2b

; zero larray
	ld	hl,larray
	ld	b,8
	call	zerofill
	
; square to offset and mask
	pop	bc
	call	sq2om

; apply offset
	ld	hl,mfield
	ld	e,b
	ld	d,0
	add	hl,de
	ld	(mptr),hl
	ld	hl,ofield
	add	hl,de
	ld	(optr),hl

; check if square empty
	ld	hl,(mptr)
	ld	a,(hl)
	cpl
	and	c
	jp	z,2f
	ld	hl,(optr)
	ld	a,(hl)
	cpl
	and	c
	jp	z,2f
	
; shift mfield and ofield
6:	ld	d,c
	ld	hl,mfield
	call	dir_pat
	ld	hl,ofield
	call	dir_pat
	ld	c,d

; update larray
	ld	b,8
	ld	hl,larray
	push	hl
	ld	hl,(optr)
	ex	de,hl
	ld	hl,(mptr)
1:	ex	(sp),hl
	ld	a,(hl)
	rla
	jp	c,3f
	ex	(sp),hl
	ld	a,(de)
	or	(hl)
	and	c
	ex	(sp),hl
	jp	z,4f
	ld	a,(de)
	and	c
	jp	z,5f
	inc	(hl)
	jp	3f
5:	ld	a,(hl)
	or	0x80
	ld	(hl),a
	jp	3f
4:	ld	(hl),0x80
3:	inc	hl
	ex	(sp),hl
	push	bc
	ld	bc,8
	add	hl,bc
	ex	de,hl
	add	hl,bc
	ex	de,hl
	pop	bc
	dec	b
	jp	nz,1b
	pop	hl
	
; check if done
	ld	hl,larray
	ld	b,8
1:	ld	a,(hl)
	inc	hl
	rla
	jp	nc,6b
	dec	b
	jp	nz,1b

; clear flags in larray
	ld	hl,larray
	ld	b,8
	ld	c,0x7f
1:	ld	a,(hl)
	and	c
	ld	(hl),a
	inc	hl
	dec	b
	jp	nz,1b
	or	a
	jp	7b

; illegal move
2:	scf
	ret
	
; ==============================================================================
; rc2sq - compress square
; 
;   input:  B - row
;	    C - column
; 
;   output: C - square
; 
;   uses:   A
; 
	.text
	.global	rc2sq
rc2sq:
	ld	a,b
	add	a,a
	add	a,a
	add	a,a
	or	c
	ld	c,a
	ret
	
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
	.global	sq2rc
sq2rc:
	ld	a,c
	rra
	rra
	rra
	and	0x07
	ld	b,a
	ld	a,c
	and	0x07
	ld	c,a
	ret
	
; ==============================================================================
; sq2om - square to offset and mask
; 
;   input:  C - square
; 
;   output: B - offset (row)
;	    C - mask (1 << column)
; 
;   uses:   A
; 
	.text
	.global	sq2om
sq2om:
	call	sq2rc
	ld	a,1
1:	dec	c
	jp	m,1f
	add	a,a
	jp	1b
1:	ld	c,a
	ret
	
; ==============================================================================
; make_move - perform one move
; 
;   input:  (HL) - array of black discs
;           (DE) - array of white discs
;           C - move (must be legal)
;           B - =0 black to move
;               =1 white to move
; 
;   output:  updated arrays
; 
;   uses:   all
; 
	.text
	.global	make_move
make_move:

; check for pass
	ld	a,c
	or	a
	ret	m
	
; if white's turn, switch arrays
	ld	a,b
	or	a
	jp	z,1f
	ex	de,hl
	
; get counters
1:	push	hl
	push	de
	push	hl
	push	bc
	call	one_legal
	pop	bc
	
; convert square number
	call	sq2om
	
; calculate initial pointer
	ex	(sp),hl
	ld	e,b
	ld	d,0
	add	hl,de
	ld	a,(hl)
	or	c
	ld	(hl),a
	pop	de

; direction 0 (NW)
	push	hl
	push	bc
	ld	a,(de)
	ld	b,a
1:	dec	b
	jp	m,1f
	dec	hl
	ld	a,c
	rrca
	ld	c,a
	or	(hl)
	ld	(hl),a
	jp	1b
1:	inc	de
	pop	bc
	pop	hl
	
; direction 1 (N)
	push	hl
	ld	a,(de)
	ld	b,a
1:	dec	b
	jp	m,1f
	dec	hl
	ld	a,c
	or	(hl)
	ld	(hl),a
	jp	1b
1:	inc	de
	pop	hl
	
; direction 2 (NE)
	push	hl
	push	bc
	ld	a,(de)
	ld	b,a
1:	dec	b
	jp	m,1f
	dec	hl
	ld	a,c
	rlca
	ld	c,a
	or	(hl)
	ld	(hl),a
	jp	1b
1:	inc	de
	pop	bc
	pop	hl
	
; direction 3 (W)
	push	bc
	ld	a,(de)
	ld	b,a
1:	dec	b
	jp	m,1f
	ld	a,c
	rrca
	ld	c,a
	or	(hl)
	ld	(hl),a
	jp	1b
1:	inc	de
	pop	bc
	
; direction 4 (E)
	push	bc
	ld	a,(de)
	ld	b,a
1:	dec	b
	jp	m,1f
	ld	a,c
	rlca
	ld	c,a
	or	(hl)
	ld	(hl),a
	jp	1b
1:	inc	de
	pop	bc
	
; direction 5 (SW)
	push	hl
	push	bc
	ld	a,(de)
	ld	b,a
1:	dec	b
	jp	m,1f
	inc	hl
	ld	a,c
	rrca
	ld	c,a
	or	(hl)
	ld	(hl),a
	jp	1b
1:	inc	de
	pop	bc
	pop	hl
	
; direction 6 (S)
	push	hl
	ld	a,(de)
	ld	b,a
1:	dec	b
	jp	m,1f
	inc	hl
	ld	a,c
	or	(hl)
	ld	(hl),a
	jp	1b
1:	inc	de
	pop	hl
	
; direction 7 (SE)
	ld	a,(de)
	ld	b,a
1:	dec	b
	jp	m,1f
	inc	hl
	ld	a,c
	rlca
	ld	c,a
	or	(hl)
	ld	(hl),a
	jp	1b
	
; adjust opponent's discs
2:	pop	hl
1:	pop	hl
	pop	de
	ld	c,8
1:	ld	a,(de)
	cpl
	and	(hl)
	ld	(hl),a
	inc	hl
	inc	de
	dec	c
	jp	nz,1b
	ret
	
;; ; ==============================================================================
;; ; Direction offsets and rotations
;; ; 
	;; .data
;; dir_or:
;; 	.byte	0x05, 0x04, 0x06, 0x01, 0x02, 0x09, 0x08, 0x0a, 0
	
;; ; ==============================================================================
;; ; legal - check if move is legal
;; ; 
;; ;   input:  (HL) - board
;; ;           C - row * 8 + column 
;; ; 
;; ;   output: CY - illegal move
;; ; 
;; ;   uses:   all
;; ; 
;; 	.text
;; 	.global	legal
;; legal:
;; 	ld	b,0
;; 	add	hl,bc		; HL = ptr in board
;; 	ld	a,(hl)
;; 	or	a
;; 	scf
;; 	ret	nz		; not empty, illegal move
;; 	push	hl
;; 	ld	hl,dirs		; HL = dirs
;; 	jp	1f
;; 2:	pop	bc
;; 	pop	hl
;; 3:	ex	(sp),hl		; HL = ptr in dirs
;; 1:	ld	a,(hl)
;; 	ld	b,a		; B = limit
;; 	dec	a
;; 	jp	z,2f
;; 	inc	hl
;; 	ld	e,(hl)
;; 	inc	hl
;; 	ld	d,(hl)		; DE = delta
;; 	inc	hl
;; 	ex	(sp),hl		; HL = ptr in board
;; 	ld	a,c
;; 	xor	b
;; 	and	RMASK
;; 	jp	z,3b		; row limit
;; 	ld	a,c
;; 	xor	b
;; 	and	CMASK
;; 	jp	z,3b		; column limit
;; 	push	hl
;; 	push	bc
;; 	add	hl,de
;; 	ld	a,(hl)
;; 	cp	2
;; 	jp	nz,2b		; not opponent's disc
;; 4:	ld	a,c
;; 	add	a,e
;; 	ld	c,a
;; 	xor	b
;; 	and	RMASK
;; 	jp	z,2b		; row limit
;; 	ld	a,c
;; 	xor	b
;; 	and	CMASK
;; 	jp	z,2b		; column limit
;; 	add	hl,de
;; 	ld	a,(hl)
;; 	dec	a
;; 	jp	z,4f
;; 	dec	a
;; 	jp	nz,2b		; empty square
;; 	jp	4b
;; 4:	xor	a		; my disc, move is legal
;; 	pop	bc
;; 	pop	hl
;; 	pop	hl
;; 	ret
;; 1:	pop	bc		; illegal move
;; 	pop	hl
;; 2:	pop	hl
;; 	scf
;; 	ret

; ==============================================================================
; score_board - calculate board score, based on
;               <https://github.com/kartikkukreja/blog-codes>.
; 
;   input:  (HL) - board
; 
;   output: HL - score
; 
;   uses:   all
; 
	.text
	.global	score_board
score_board:
	
; reset variables
	ex	de,hl
	ld	hl,var
	ld	b,len
	call	zerofill
	ex	de,hl

; count all discs and frontier discs and calculate value differential
	push	hl
	ld	de,sqv		; DE = ptr in square values
	ld	c,0		; C = square offset
4:	ld	a,(hl)
	dec	a
	jp	nz,1f
	push	hl		; my disc
	ld	hl,myd
	inc	(hl)
	ex	de,hl
	ld	e,(hl)
	inc	hl
	ld	d,(hl)		; DE = square value
	inc	hl
	inc	hl
	jp	2f
1:	dec	a
	jp	nz,1f
	push	hl		; opponent's disc
	ld	hl,opd
	inc	(hl)
	ex	de,hl
	inc	hl
	inc	hl
	ld	e,(hl)
	inc	hl
	ld	d,(hl)		; DE = minus square value
2:	inc	hl
	ex	(sp),hl
	push	hl
	ld	hl,(tmp)
	add	hl,de
	ld	(tmp),hl
	ld	hl,dirs
6:	ld	b,(hl)		; B = limit
	inc	hl
	ld	a,b
	cp	END
	jp	z,3f
	xor	c
	and	RMASK
	jp	nz,2f
	inc	hl		; row limit
5:	inc	hl
	jp	6b
2:	ld	a,b
	xor	c
	and	CMASK
	jp	z,5b		; column limit
	pop	de
	push	de
	push	de
	ld	e,(hl)
	inc	hl
	ld	d,(hl)		; DE = delta
	inc	hl
	ex	(sp),hl
	add	hl,de
	ld	a,(hl)		; adjacent square
	pop	hl
	or	a
	jp	nz,6b
	pop	hl		; adjacent square is empty
	push	hl
	ld	a,(hl)
	dec	a
	ld	hl,myfd
	jp	z,5f
	inc	hl
5:	inc	(hl)		; increment frontier discs counter
3:	pop	hl
	pop	de
	jp	2f
1:	inc	de		; next square value
	inc	de
	inc	de
	inc	de
2:	inc	hl		; next square
	inc	c
	ld	a,c
	cp	64
	jp	nz,4b
	ld	hl,(tmp)
	push	hl
	ex	de,hl
	ld	hl,8
	call	div16
	pop	de
	add	hl,de
	ld	(score),hl
	ld	hl,(myd)	; L = my discs
				; H = opponent's discs
	ld	a,l
	cp	h
	jp	z,1f
	jp	c,3f
	jp	4f
3:	xor	a
	sub	h
4:	push	hl
	call	signexhl
	ld	de,125
	call	mul16
	ex	de,hl
	pop	hl
	ld	a,h
	add	a,l
	ld	l,a
	ld	h,0
	call	div16
	call	2f
1:	ld	hl,(myfd)	; L = my frontier discs
				; H = opponent's frontier discs
	ld	a,l
	cp	h
	jp	z,1f
	jp	c,3f
	xor	a
	sub	l
	jp	4f
3:	ld	a,h
4:	push	hl
	call	signexhl
	ld	de,930
	call	mul16
	ex	de,hl
	pop	hl
	ld	a,h
	add	a,l
	ld	l,a
	ld	h,0
	call	div16
	call	2f

; count corner and next-to-corner discs
1:	ld	bc,crns
3:	ld	a,(bc)
	cp	END
	jp	z,1f
	inc	bc
	ld	e,a
	ld	d,0
	pop	hl		; HL = board
	push	hl
	add	hl,de
	ld	a,(hl)		; A = corner
	ld	hl,mycd
	dec	a
	jp	z,4f
	inc	hl
4:	dec	a
	jp	nz,4f
	inc	(hl)
	jp	5f
4:	ld	h,3
6:	ld	a,(bc)
	inc	bc
	pop	de
	push	de
	push	de
	ld	e,a
	ld	d,0
	ex	(sp),hl		; HL = board
	add	hl,de
	ld	a,(hl)		; A = next-to-corner
	or	a
	jp	z,4f
	ld	hl,myncd
	dec	a
	jp	z,7f
	inc	hl
7:	inc	(hl)
4:	pop	af		; A = counter
	ld	h,a
	dec	h
	jp	nz,6b
	jp	3b
5:	inc	bc
	inc	bc
	inc	bc
	jp	3b	
1:	ld	hl,(mycd)	; L = my corner discs
				; H = opponent's corner moves
	ld	a,l
	sub	h
	call	signexhl
	ld	de,2505
	call	mul16
	call	2f
	ld	hl,(mycd)	; L = my next-to-corner discs
				; H = opponent's next-to-corner moves
	ld	a,h
	sub	l
	call	signexhl
	ld	de,597
	call	mul16
	call	2f

; count legal moves
	pop	hl
	push	hl		; HL = board
	ld	de,revb
	call	rev_board	; create reversed board
	pop	hl
	ld	de,mymd
	call	3f
	ld	hl,revb
	inc	de
	call	3f
	ld	hl,(mymd)	; L = my moves
				; H = opponent's moves
	ld	a,l
	cp	h
	jp	z,1f
	jp	c,5f
	jp	4f
5:	xor	a
	sub	h
4:	push	hl
	call	signexhl
	ld	de,987
	call	mul16
	ex	de,hl
	pop	hl
	ld	a,h
	add	a,l
	ld	l,a
	ld	h,0
	call	div16
	call	2f

1:	ld	hl,(score)
	ret

; score += HL
2:	ex	de,hl
	ld	hl,(score)
	add	hl,de
	ld	(score),hl
	ret
	
; count legal moves
3:	ld	c,0
2:	push	hl
	push	de
	push	bc
	call	all_legal
	pop	bc
	pop	hl
	pop	de
	jp	c,1f
	inc	(hl)
1:	ex	de,hl
	inc	c
	ld	a,c
	cp	64
	jp	nz,2b
	ret	
	
; ==============================================================================
; rev_board - reverse board
; 
;   input:  (HL) - board
;           (DE) - output board 
; 
;   output: (DE) - reversed board
; 
;   uses:   all
; 
	.text
	.global	rev_board
rev_board:
	ld	bc,0x0340
1:	ld	a,(hl)
	or	a
	jp	z,2f
	xor	b
2:	ld	(de),a
	inc	hl
	inc	de
	dec	c
	jp	nz,1b
	ret
	
; ==============================================================================
; Directions
; 
	.data
dirs:
	.byte	0x00
	.word	-9
	.byte	0x40
	.word	-8
	.byte	0x07
	.word	-7
	.byte	0x80
	.word	-1
	.byte	0x87
	.word	1
	.byte	0x38
	.word	7
	.byte	0x78
	.word	8
	.byte	0x3f
	.word	9
	.byte	END
	
; ==============================================================================
; Corners
; 
	.data
crns:
	.byte	0, 1, 8, 9
	.byte	7, 6, 14, 15
	.byte	49, 41, 42, 50
	.byte	63, 54, 55, 62
	.byte	END
	
;; ; ==============================================================================
;; ; Square values
;; ; 
;; 	.data
;; sqv:
;; 	.word	20, -20, -3, 3, 11, -11, 8, -8, 8, -8, 11, -11, -3, 3, 20, -20
;; 	.word	-3, 3, -7, 7, -4, 4, 1, -1, 1, -1, -4, 4, -7, 7, -3, 3
;; 	.word	11, -11, -4, 4, 2, -2, 2, -2, 2, -2, 2, -2, -4, 4, 11, -11
;; 	.word	8, -8, 1, -1, 2, -2, -3, 3, -3, 3, 2, -2, 1, -1, 8, -8
;; 	.word	8, -8, 1, -1, 2, -2, -3, 3, -3, 3, 2, -2, 1, -1, 8, -8
;; 	.word	11, -11, -4, 4, 2, -2, 2, -2, 2, -2, 2, -2, -4, 4, 11, -11
;; 	.word	-3, 3, -7, 7, -4, 4, 1, -1, 1, -1, -4, 4, -7, 7, -3, 3
;; 	.word	20, -20, -3, 3, 11, -11, 8, -8, 8, -8, 11, -11, -3, 3, 20, -20

; ==============================================================================
; Square values
; 
	.data
sqv:
	.byte	20, -20, -3, 3, 11, -11, 8, -8, 8, -8, 11, -11, -3, 3, 20, -20
	.byte	-3, 3, -7, 7, -4, 4, 1, -1, 1, -1, -4, 4, -7, 7, -3, 3
	.byte	11, -11, -4, 4, 2, -2, 2, -2, 2, -2, 2, -2, -4, 4, 11, -11
	.byte	8, -8, 1, -1, 2, -2, -3, 3, -3, 3, 2, -2, 1, -1, 8, -8

; ==============================================================================
; Bit counts
; 
	.data
bitcounts:
	.byte	0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4
	.byte	1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5
	.byte	1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5
	.byte	2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6
	.byte	1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5
	.byte	2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6
	.byte	2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6
	.byte	3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7
	.byte	1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5
	.byte	2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6
	.byte	2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6
	.byte	3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7
	.byte	2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6
	.byte	3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7
	.byte	3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7
	.byte	4, 5, 5, 6, 5, 6, 6, 7, 5, 6, 6, 7, 6, 7, 7, 8

; ==============================================================================
; Variables
; 
	.bss

score:	.skip	2
	.equiv	var, .
myd:	.skip	1
opd:	.skip	1
;; 	.global	myfd
;; myfd:	.skip	1
;; 	.global	opfd
;; opfd:	.skip	1
;; myval:	.skip	1
;; opval:	.skip	1
;; mycd:	.skip	1
;; opcd:	.skip	1
;; myncd:	.skip	1
;; opncd:	.skip	1
mymd:	.skip	1
opmd:	.skip	1
tmp:	.skip	2
	.equiv	len,. - var
revb:	.skip	64
mptr:	.skip	2
optr:	.skip	2
field:	.skip	64
sfield:	.skip	64
mfield:	.skip	64
ofield:	.skip	64
larray:	.skip	8
	
	.end
 
