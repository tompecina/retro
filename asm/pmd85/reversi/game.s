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
; reflect_byte - reverse bit order of a byte
; 
;   input:  A - input byte
; 
;   output: A = A reflected
; 
;   uses:   D, E, H, L
; 
	.text
	.global	reflect_byte
reflect_byte:
	ld	hl,byterefl
	ld	e,a
	ld	d,0
	add	hl,de
	ld	a,(hl)
	ret
	
; ==============================================================================
; Byte reflections
; 
	.data
byterefl:
	.byte	0x00, 0x80, 0x40, 0xc0, 0x20, 0xa0, 0x60, 0xe0
	.byte	0x10, 0x90, 0x50, 0xd0, 0x30, 0xb0, 0x70, 0xf0
	.byte	0x08, 0x88, 0x48, 0xc8, 0x28, 0xa8, 0x68, 0xe8
	.byte	0x18, 0x98, 0x58, 0xd8, 0x38, 0xb8, 0x78, 0xf8
	.byte	0x04, 0x84, 0x44, 0xc4, 0x24, 0xa4, 0x64, 0xe4
	.byte	0x14, 0x94, 0x54, 0xd4, 0x34, 0xb4, 0x74, 0xf4
	.byte	0x0c, 0x8c, 0x4c, 0xcc, 0x2c, 0xac, 0x6c, 0xec
	.byte	0x1c, 0x9c, 0x5c, 0xdc, 0x3c, 0xbc, 0x7c, 0xfc
	.byte	0x02, 0x82, 0x42, 0xc2, 0x22, 0xa2, 0x62, 0xe2
	.byte	0x12, 0x92, 0x52, 0xd2, 0x32, 0xb2, 0x72, 0xf2
	.byte	0x0a, 0x8a, 0x4a, 0xca, 0x2a, 0xaa, 0x6a, 0xea
	.byte	0x1a, 0x9a, 0x5a, 0xda, 0x3a, 0xba, 0x7a, 0xfa
	.byte	0x06, 0x86, 0x46, 0xc6, 0x26, 0xa6, 0x66, 0xe6
	.byte	0x16, 0x96, 0x56, 0xd6, 0x36, 0xb6, 0x76, 0xf6
	.byte	0x0e, 0x8e, 0x4e, 0xce, 0x2e, 0xae, 0x6e, 0xee
	.byte	0x1e, 0x9e, 0x5e, 0xde, 0x3e, 0xbe, 0x7e, 0xfe
	.byte	0x01, 0x81, 0x41, 0xc1, 0x21, 0xa1, 0x61, 0xe1
	.byte	0x11, 0x91, 0x51, 0xd1, 0x31, 0xb1, 0x71, 0xf1
	.byte	0x09, 0x89, 0x49, 0xc9, 0x29, 0xa9, 0x69, 0xe9
	.byte	0x19, 0x99, 0x59, 0xd9, 0x39, 0xb9, 0x79, 0xf9
	.byte	0x05, 0x85, 0x45, 0xc5, 0x25, 0xa5, 0x65, 0xe5
	.byte	0x15, 0x95, 0x55, 0xd5, 0x35, 0xb5, 0x75, 0xf5
	.byte	0x0d, 0x8d, 0x4d, 0xcd, 0x2d, 0xad, 0x6d, 0xed
	.byte	0x1d, 0x9d, 0x5d, 0xdd, 0x3d, 0xbd, 0x7d, 0xfd
	.byte	0x03, 0x83, 0x43, 0xc3, 0x23, 0xa3, 0x63, 0xe3
	.byte	0x13, 0x93, 0x53, 0xd3, 0x33, 0xb3, 0x73, 0xf3
	.byte	0x0b, 0x8b, 0x4b, 0xcb, 0x2b, 0xab, 0x6b, 0xeb
	.byte	0x1b, 0x9b, 0x5b, 0xdb, 0x3b, 0xbb, 0x7b, 0xfb
	.byte	0x07, 0x87, 0x47, 0xc7, 0x27, 0xa7, 0x67, 0xe7
	.byte	0x17, 0x97, 0x57, 0xd7, 0x37, 0xb7, 0x77, 0xf7
	.byte	0x0f, 0x8f, 0x4f, 0xcf, 0x2f, 0xaf, 0x6f, 0xef
	.byte	0x1f, 0x9f, 0x5f, 0xdf, 0x3f, 0xbf, 0x7f, 0xff
	
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
; count_discs - count discs
; 
;   input:  (HL) - array of my discs
;           (DE) - array of opponent's discs
; 
;   output: B - number of my discs
;           C - number of opponent's discs
; 
;   uses:   B, D, E, H, L
; 
	.text
	.global	count_discs
count_discs:
	push	de
	call	count_bits
	ld	(myd),a
	pop	hl
	call	count_bits
	ld	(opd),a
	ld	c,a
	ld	a,(myd)
	ld	b,a
	ret
	
	.lcomm	myd, 1
	.lcomm	opd, 1
	
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
	ld	d,(hl)
	inc	hl
	ld	e,(hl)
	call	2f
	pop	hl
	pop	de
	ld	bc,7
	add	hl,bc
	ex	de,hl
	add	hl,bc
	ld	b,(hl)
	dec	hl
	ld	c,(hl)
	ex	de,hl
	ld	d,(hl)
	dec	hl
	ld	e,(hl)
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
2:	call	2f
	ld	a,b
	rlca
	rlca
	ld	h,a
	ld	a,b
	rla
	ld	a,h
	rla
	ld	b,a
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
	ld	d,a
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
1:	rra
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
; count_moves - count legal moves
; 
;   input:  (HL) - array of my discs
;           (DE) - array of opponent's discs
; 
;   output: B - number of my moves
;           C - number of opponent's moves
; 
;   uses:   all
; 
	.text
	.global	count_moves
count_moves:
	push	hl
	push	de
	call	all_legal
	call	count_bits
	ld	(mym),a
	pop	hl
	pop	de
	call	all_legal
	call	count_bits
	ld	(opm),a
	ld	c,a
	ld	a,(mym)
	ld	b,a
	ret
	
	.lcomm	mym, 1
	.lcomm	opm, 1
	
; ==============================================================================
; dir_pat,dir_pat_full - create directions pattern in situ
; 
;   input:  (HL) - pattern
; 	    C - selector (only dir_pat)
; 
;   uses:   all
; 
	.text
	.global	dir_pat, dir_pat_full

	.macro	chksel	popaf,pushaf
2:	.if	\popaf
	pop	af
	.endif
	or	a
	ret	z
	rra
	.if	\pushaf
	push	af
	.endif
	jp	c,1f
	add	hl,de
	jp	2f
1:
	.endm
	
dir_pat_full:
	ld	c,0xff
dir_pat:
	ld	a,c
	ld	de,8
	ld	b,0
	
; direction 0 (NW)
	chksel	0,1
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
	
; direction 1 (N)
	chksel	1,1
	ld	a,(hl)
	ld	(hl),b
	inc	hl
	.rept	7
	ld	c,(hl)
	ld	(hl),a
	ld	a,c
	inc	hl
	.endr
	
; direction 2 (NE)
	chksel	1,1
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
	
; direction 3 (W)
	chksel	1,1
	.rept	8
	ld	a,(hl)
	add	a,a
	ld	(hl),a
	inc	hl
	.endr
	
; direction 4 (E)
	chksel	1,1
	.rept	8
	ld	a,(hl)
	or	a
	rra
	ld	(hl),a
	inc	hl
	.endr
	
; direction 5 (SW)
2:	ld	c,7
	add	hl,bc
	ld	de,-8
	chksel	1,1
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
	
; direction 6 (S)
2:	ld	c,16
	add	hl,bc
	chksel	1,1
	ld	a,(hl)
	ld	(hl),b
	dec	hl
	.rept	7
	ld	c,(hl)
	ld	(hl),a
	ld	a,c
	dec	hl
	.endr
	
; direction 7 (SE)
2:	ld	c,16
	add	hl,bc
	chksel	1,0
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
2:	ret
	
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
	ld	hl,sfield
	ld	d,7
	call	2f

; zero larray
	ld	hl,larray
	ld	b,8
	call	zerofill

; check sfield and return if no empty squares
	ld	hl,sfield
	ld	b,8
1:	ld	a,(hl)
	inc	hl
	or	a
	jp	nz,1f
	dec	b
	jp	nz,1b
	pop	hl
	pop	hl
	ld	hl,larray
	ret
		
; populate mfield and ofield
1:	pop	hl
	ld	bc,mfield
	ld	d,8
	call	2f
	pop	hl
	ld	bc,ofield
	ld	d,8
	call	2f

; shift mfield and ofield
	ld	hl,mfield
	call	dir_pat_full
	ld	hl,ofield
	call	dir_pat_full

; set initial selector
	ld	c,0xff

; update sfield
5:	push	bc
	ld	hl,sfield
	ld	de,ofield
	ld	a,c
3:	or	a
	jp	z,3f
	rra
	jp	nc,4f
	push	af
	ld	bc,larray
	ld	a,8
1:	push	af
	ld	a,(bc)
	inc	bc
	cpl
	and	(hl)
	ex	de,hl
	and	(hl)
	inc	hl
	ex	de,hl
	ld	(hl),a
	inc	hl
	pop	af
	dec	a
	jp	nz,1b
	pop	af
	jp	3b
4:	ld	bc,8
	ex	de,hl
	add	hl,bc
	ex	de,hl
	add	hl,bc
	jp	3b
3:	pop	bc

; check sfield, set selector and return if zero
	ld	hl,sfield
	ld	de,8
	ld	a,c
	ld	c,0x80
3:	rra
	push	af
	jp	nc,4f
	push	hl
	ld	b,e
1:	ld	a,(hl)
	inc	hl
	or	a
	jp	nz,1f
	dec	b
	jp	nz,1b
	inc	sp
	inc	sp
	jp	6f		; CY is still 0
1:	pop	hl
	scf
4:	rla
	add	hl,de
	rra
6:	ld	a,c
	rra
	ld	c,a
	jp	c,1f
	pop	af
	jp	3b
1:	pop	af
	ld	a,c
	or	a
	jp	nz,1f
	ld	hl,larray
	ret
	
; shift mfield and ofield
1:	ld	hl,mfield
	push	bc
	call	dir_pat
	ld	hl,ofield
	pop	bc
	push	bc
	call	dir_pat
	pop	bc

; update larray
	push	bc	
	ld	hl,sfield
	ld	a,c
	ld	bc,mfield
4:	or	a
	jp	z,4f
	rra
	jp	nc,3f
	push	af
	ld	de,larray
	ld	a,8
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
	jp	4b
3:	ld	de,8
	add	hl,de
	ex	de,hl
	add	hl,bc
	ld	b,h
	ld	c,l
	ex	de,hl
	jp	4b
4:	pop	bc
	jp	5b
	
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

	.lcomm	sfield, 64
	.lcomm	mfield, 64
	.lcomm	ofield, 64
	.lcomm	larray, 8

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
6:	push	bc
	ld	hl,mfield
	call	dir_pat_full
	ld	hl,ofield
	call	dir_pat_full
	pop	bc

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
	ld	hl,larray
	ret

; illegal move
2:	scf
	ret
	
	.lcomm	mptr, 2
	.lcomm	optr, 2
	
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
	jp	c2m
	
; ==============================================================================
; c2m - column to mask
; 
;   input:  C - column
; 
;   output: C - mask (1 << column)
; 
;   uses:   A
; 
	.text
	.global	c2m
c2m:	
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
	
; ==============================================================================
; score_board - calculate board score, based on
;               <https://github.com/kartikkukreja/blog-codes>
; 
;   input:  (HL) - array of my discs
;           (DE) - array of opponent's discs
; 
;   output: HL - score
; 
;   uses:   all
; 
	.text
	.global	score_board
score_board:
	
; reset score
	push	hl
	ld	hl,0
	ld	(score),hl
	pop	hl

; count legal moves
	push	hl
	push	de
	call	count_moves	; B = my moves
				; C = opponent's moves
	ld	a,b
	or	c
	jp	nz,1f
	pop	de
	pop	hl
	call	count_discs	; B = my discs
				; C = opponent's discs
	ld	a,b
	sub	c
	jp	z,2f
	jp	c,3f
	ld	hl,POSINF - 65	; I win
	ld	b,0
	jp	4f
3:	ld	hl,NEGINF + 65	; I lose
	ld	b,0xff
4:	ld	c,a
	add	hl,bc
	ret
2:	ld	hl,0		; draw
	ret
1:	ld	a,b
	cp	c
	jp	z,1f
	jp	c,5f
	jp	4f
5:	xor	a
	sub	c
4:
	push	bc
	call	signexhl
	ld	de,987
	call	mul16
	ex	de,hl
	pop	bc
	ld	a,c
	add	a,b
	ld	l,a
	ld	h,0
	call	div16
	call	2f
	pop	de
	pop	hl

; calculate total board value differential
	push	hl
	push	de
	call	calc_tbv	; HL = my score
				; DE = opponent's score
	ld	a,l
	sub	e
	ld	l,a
	ld	a,h
	sbc	a,d
	ld	h,a
	push	hl
	ex	de,hl
	ld	hl,8
	call	div16
	pop	de
	add	hl,de
	call	2f
	pop	de
	pop	hl
	
; count all discs
	push	hl
	push	de
	call	count_discs	; B = my discs
				; C = opponent's discs
	ld	a,b
	cp	c
	jp	z,1f
	jp	c,3f
	jp	4f
3:	xor	a
	sub	c
4:	push	bc
	call	signexhl
	ld	de,125
	call	mul16
	ex	de,hl
	pop	bc
	ld	a,c
	add	a,b
	ld	l,a
	ld	h,0
	call	div16
	call	2f
1:	pop	de
	pop	hl
	
; count frontier discs
	push	hl
	push	de
	call	count_fdiscs	; B = my frontier discs
				; C = opponent's frontier discs
	ld	a,b
	cp	c
	jp	z,1f
	jp	c,3f
	xor	a
	sub	b
	jp	4f
3:	ld	a,c
4:	push	bc
	call	signexhl
	ld	de,930
	call	mul16
	ex	de,hl
	pop	bc
	ld	a,b
	add	a,c
	ld	l,a
	ld	h,0
	call	div16
	call	2f
1:	pop	de
	pop	hl

; count corner and next-to-empty-corner discs
	call	count_cdiscs	; B - my corner discs
				; C - opponent's corner discs
				; D - my next-to-empty-corner discs
				; E - opponent's next-to-empty-corner discs
	push	de
	ld	a,b
	sub	c
	call	signexhl
	ld	de,2505
	call	mul16
	call	2f
	pop	de
	ld	a,e
	sub	d
	call	signexhl
	ld	de,597
	call	mul16
	call	2f

1:	ld	hl,(score)
	ret

; score += HL
2:	ex	de,hl
	ld	hl,(score)
	add	hl,de
	ld	(score),hl
	ret

	.lcomm	score, 2

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
; init_hash - initialize the hash table
; 
;   output: (hashtbl) - initialized table
; 
;   uses:   all
;
	.text
	.global	init_hash
init_hash:
	ld	de,hashtbl
	ld	hl,0x55aa
	ld	c,0
1:	ld	a,0xc3
	call	crc16
	ex	de,hl
	ld	(hl),e
	inc	hl
	ld	(hl),d
	inc	hl
	ex	de,hl
	dec	c
	jp	nz,1b
	ret

	.lcomm	hashtbl, 256
	
; ==============================================================================
; zobrist_hash - calculate the Zobrist hash of the board
; 
;   input:  (HL) - array of black discs
;           (DE) - array of white discs
; 
;   output: HL - hash
; 
;   uses:   all
; 
	.text
	.global	zobrist_hash
zobrist_hash:
	ld	bc,hashtbl
	ex	de,hl
	push	hl
	ld	hl,0
	ld	(hash),hl
	call	1f
	pop	de
1:	ld	a,8
2:	push	af
	ld	a,(de)
	inc	de
	ld	l,8
3:	rra
	ld	h,a
	jp	nc,1f
	ld	a,(bc)
	inc	bc
	push	hl
	ld	hl,hash
	xor	(hl)
	ld	(hl),a
	inc	hl
	ld	a,(bc)
	xor	(hl)
	ld	(hl),a
	pop	hl
	jp	4f
1:	inc	bc
4:	inc	bc
	dec	l
	ld	a,h
	jp	nz,3b
	pop	af
	dec	a
	jp	nz,2b
	ld	hl,(hash)
	ret
	
	.lcomm	hash, 2
	
; ==============================================================================
; board_crc24 - get the board CRC-24
; 
;   input:  (HL) - array of black discs
;           (DE) - array of white discs
; 
;   output: CHL - CRC-24 of the board
; 
;   uses:   all
; 
	.text
	.global	board_crc24
board_crc24:
	push	de
	push	hl
	call	init_crc24
	pop	de
	call	2f
	pop	de
2:	ld	a,8
1:	push	af
	ld	a,(de)
	inc	de
	call	crc24
	pop	af
	dec	a
	jp	nz,1b
	ret
	
; ==============================================================================
; rotate90 - rotate the board in situ clockwise
; 
;   input:  (HL) - array of black discs
;           (DE) - array of white discs
; 
;   output: rotated board
; 
;   uses:   all
; 
	.text
	.global	rotate90
rotate90:
	push	de
	call	1f
	pop	hl
1:	ld	a,8
1:	push	af
	ld	a,(hl)
	inc	hl
	ld	de,tmprot
	ld	b,8
2:	rra
	ld	c,a
	ld	a,(de)
	rla
	ld	(de),a
	inc	de
	ld	a,c
	dec	b
	jp	nz,2b
	pop	af
	dec	a
	jp	nz,1b
	ld	b,8
1:	dec	hl
	dec	de
	ld	a,(de)
	ld	(hl),a
	dec	b
	jp	nz,1b
	ret
	
	.lcomm	tmprot, 8
	
; ==============================================================================
; move_rotate90 - rotate move clockwise
; 
;   input:  C - move
; 
;   output: C - rotated move
; 
;   uses:   A
; 
	.text
	.global	move_rotate90
move_rotate90:
	ld	a,0x3f
	sub	c
	rra
	rra
	rra
	and	0x07
	push	bc
	ld	b,a
	ld	a,c
	rla
	rla
	rla
.L1:	and	0x38
	or	b
	pop	bc
	ld	c,a
	ret
	
; ==============================================================================
; rotate180 - rotate the board in situ by 180 degrees
; 
;   input:  (HL) - array of black discs
;           (DE) - array of white discs
; 
;   output: rotated board
; 
;   uses:   all
; 
	.text
	.global	rotate180
rotate180:
	push	de
	call	1f
	pop	hl
1:	ld	d,h
	ld	e,l
	ld	bc,7
	add	hl,bc
	ld	b,4
1:	ld	a,(de)
	call	1f
	ld	c,(hl)
	ld	(hl),a
	ld	a,c
	call	1f
	ld	(de),a
	inc	de
	dec	hl
	dec	b
	jp	nz,1b
	ret
1:	push	hl
	push	de
	call	reflect_byte
	pop	de
	pop	hl
	ret
	
; ==============================================================================
; move_rotate180 - rotate move by 180 degrees
; 
;   input:  C - move
; 
;   output: C - rotated move
; 
;   uses:   A
; 
	.text
	.global	move_rotate180
move_rotate180:
	ld	a,0x07
	sub	c
	and	0x07
	push	bc
	ld	b,a
	ld	a,0x3f
	sub	c
	jp	.L1
	
; ==============================================================================
; rotate270 - rotate the board in situ counterclockwise
; 
;   input:  (HL) - array of black discs
;           (DE) - array of white discs
; 
;   output: rotated board
; 
;   uses:   all
; 
	.text
	.global	rotate270
rotate270:
	push	de
	call	1f
	pop	hl
1:	ld	a,8
1:	push	af
	ld	a,(hl)
	inc	hl
	ld	de,tmprot
	ld	b,8
2:	rla
	ld	c,a
	ld	a,(de)
	rra
	ld	(de),a
	inc	de
	ld	a,c
	dec	b
	jp	nz,2b
	pop	af
	dec	a
	jp	nz,1b
	ld	b,8
1:	dec	hl
	dec	de
	ld	a,(de)
	ld	(hl),a
	dec	b
	jp	nz,1b
	ret
	
; ==============================================================================
; move_rotate270 - rotate move counterclockwise
; 
;   input:  C - move
; 
;   output: C - rotated move
; 
;   uses:   A
; 
	.text
	.global	move_rotate270
move_rotate270:
	ld	a,c
	rra
	rra
	rra
	and	0x07
	push	bc
	ld	b,a
	ld	a,0x07
	sub	c
	rla
	rla
	rla
	jp	.L1
	
; ==============================================================================
; horflip - flip board in situ horizontally
; 
;   input:  (HL) - array of black discs
;           (DE) - array of white discs
; 
;   output: flipped board
; 
;   uses:   all
; 
	.text
	.global	horflip
horflip:
	push	de
	call	1f
	pop	hl
1:	ld	b,h
	ld	c,l
	ld	a,8
1:	push	af
	ld	a,(bc)
	call	reflect_byte
	ld	(bc),a
	inc	bc
	pop	af
	dec	a
	jp	nz,1b
	ret
	
; ==============================================================================
; move_horflip - flip move horizontally
; 
;   input:  C - move
; 
;   output: C - flipped move
; 
;   uses:   A
; 
	.text
	.global	move_horflip
move_horflip:
	ld	a,0x07
	sub	c
	and	0x07
	push	bc
	ld	b,a
	ld	a,c
	jp	.L1
	
; ==============================================================================
; verflip - flip board in situ vertically
; 
;   input:  (HL) - array of black discs
;           (DE) - array of white discs
; 
;   output: flipped board
; 
;   uses:   all
; 
	.text
	.global	verflip
verflip:
	push	de
	call	1f
	pop	hl
1:	ld	d,h
	ld	e,l
	ld	bc,7
	add	hl,bc
	ld	b,4
1:	ld	a,(de)
	ld	c,(hl)
	ld	(hl),a
	ld	a,c
	ld	(de),a
	inc	de
	dec	hl
	dec	b
	jp	nz,1b
	ret
	
; ==============================================================================
; move_verflip - flip move vertically
; 
;   input:  C - move
; 
;   output: C - flipped move
; 
;   uses:   A
; 
	.text
	.global	move_verflip
move_verflip:
	ld	a,c
	and	0x07
	push	bc
	ld	b,a
	ld	a,0x3f
	sub	c
	jp	.L1
	
; ==============================================================================
; nwseflip - flip the board in situ along the NW-SE diagonal
; 
;   input:  (HL) - array of black discs
;           (DE) - array of white discs
; 
;   output: flipped board
; 
;   uses:   all
; 
	.text
	.global	nwseflip
nwseflip:
	push	de
	call	1f
	pop	hl
1:	ld	a,8
1:	push	af
	ld	a,(hl)
	inc	hl
	ld	de,tmprot
	ld	b,8
2:	rra
	ld	c,a
	ld	a,(de)
	rra
	ld	(de),a
	inc	de
	ld	a,c
	dec	b
	jp	nz,2b
	pop	af
	dec	a
	jp	nz,1b
	ld	b,8
1:	dec	hl
	dec	de
	ld	a,(de)
	ld	(hl),a
	dec	b
	jp	nz,1b
	ret
	
; ==============================================================================
; move_nwseflip - flip move along the NW-SE diagonal
; 
;   input:  C - move
; 
;   output: C - flipped move
; 
;   uses:   A
; 
	.text
	.global	move_nwseflip
move_nwseflip:
	ld	a,c
	rra
	rra
	rra
	and	0x07
	push	bc
	ld	b,a
	ld	a,c
	rla
	rla
	rla
	jp	.L1
	
; ==============================================================================
; neswflip - flip the board in situ along the NW-SW diagonal
; 
;   input:  (HL) - array of black discs
;           (DE) - array of white discs
; 
;   output: flipped board
; 
;   uses:   all
; 
	.text
	.global	neswflip
neswflip:
	push	de
	call	1f
	pop	hl
1:	ld	a,8
1:	push	af
	ld	a,(hl)
	inc	hl
	ld	de,tmprot
	ld	b,8
2:	rla
	ld	c,a
	ld	a,(de)
	rla
	ld	(de),a
	inc	de
	ld	a,c
	dec	b
	jp	nz,2b
	pop	af
	dec	a
	jp	nz,1b
	ld	b,8
1:	dec	hl
	dec	de
	ld	a,(de)
	ld	(hl),a
	dec	b
	jp	nz,1b
	ret
	
; ==============================================================================
; move_neswflip - flip move along the NW-SE diagonal
; 
;   input:  C - move
; 
;   output: C - flipped move
; 
;   uses:   A
; 
	.text
	.global	move_neswflip
move_neswflip:
	ld	a,0x3f
	sub	c
	rra
	rra
	rra
	and	0x07
	push	bc
	ld	b,a
	ld	a,0x07
	sub	c
	rla
	rla
	rla
	jp	.L1
	
; ==============================================================================
; getsquare - get state of selected square
; 
;   input:  (HL) - array of black discs
;           (DE) - array of white discs
;   	    B - row
;	    C - column
; 
;   output: A =0 empty
;             =1 black
;             =2 white
; 
;   uses:   all
; 
	.text
	.global	getsquare
getsquare:
	call	c2m
	ld	a,c
	ld	c,b
	ld	b,0
	add	hl,bc
	ex	de,hl
	add	hl,bc
	ld	c,a
	ld	a,(de)
	and	c
	ld	a,1
	ret	nz
	ld	a,(hl)
	and	c
	ld	a,2
	ret	nz
	xor	a
	ret
	
	.end
 
