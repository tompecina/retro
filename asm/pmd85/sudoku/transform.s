; trasnform.s
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


; Transformation and permutation routines.

	.include "sudoku.inc"
	
; ==============================================================================
; transform - transform puzzle using a map
; 
;   input:  (HL) - source
;   	    (DE) - destination
;   	    (BC) - map
; 
;   output: (DE) - transformed puzzle
; 
;   uses:   all
; 
	.text
	.globl	transform
transform:
	ld	a,81
1:	push	af
	push	hl
	ld	a,(bc)
	ld	l,a
	ld	h,0
	add	hl,de
	ex	(sp),hl
	ld	a,(hl)
	ex	(sp),hl
	ld	(hl),a
	pop	hl
	inc	hl
	inc	bc
	pop	af
	dec	a
	jp	nz,1b
	ret
	
; ==============================================================================
; init_maps - initialize transformation maps
; 
;   output: (rmaps) - populated rotation maps
; 
;   uses:   all
; 
	.text
	.globl	init_maps
init_maps:
	ld	hl,rmaps
	push	hl
	xor	a
1:	ld	(hl),a
	inc	hl
	inc	a
	cp	81
	jp	nz,1b
	ex	de,hl
	pop	hl
	ld	b,4
	call	rot90
	call	flip
	ld	b,3
	call	rot90
	ret

rot90:	ld	c,0
1:	push	bc
	call	sq2rc
	ld	a,8
	sub	b
	ld	b,c
	ld	c,a
	call	rc2sq
	ld	a,(hl)
	inc	hl
	push	hl
	ld	h,0
	ld	l,c
	add	hl,de
	ld	(hl),a
	pop	hl
	pop	bc
	inc	c
	ld	a,c
	cp	81
	jp	nz,1b
	push	hl
	ld	de,81
	add	hl,de
	ex	de,hl
	pop	hl
	dec	b
	jp	nz,rot90
	ret

flip:	push	hl
	push	de
	ld	d,h
	ld	e,l
	ld	bc,72
	add	hl,bc
	ld	b,4
2:	ld	c,9
1:	push	bc
	ld	a,(de)
	ld	c,(hl)
	ld	(hl),a
	ld	a,c
	ld	(de),a
	pop	bc
	inc	hl
	inc	de
	dec	c
	jp	nz,1b
	push	bc
	ld	bc,-18
	add	hl,bc
	pop	bc
	dec	b
	jp	nz,2b
	pop	de
	pop	hl
	ret

	.lcomm	rmaps, 81 * 8

; ==============================================================================
; permute - transform puzzle using a permutation map
; 
;   input:  (HL) - source
;   	    (DE) - destination
;   	    (BC) - permutation map
; 
;   output: (DE) - transformed puzzle
; 
;   uses:   all
; 
	.text
	.globl	permute
permute:
	ld	a,81
1:	push	af
	ld	a,(hl)
	or	a
	jp	z,2f
	push	hl
	ld	h,0
	ld	l,a
	add	hl,bc
	ld	a,(hl)
	pop	hl
	ld	(de),a
2:	inc	hl
	inc	de
	pop	af
	dec	a
	jp	nz,1b
	ret
	
	.end
