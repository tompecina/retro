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
; init_maps - populate transformation maps
; 
;   output: (rmaps) - populated rotation maps
; 
;   uses:   all
; 
	.text
	.globl	init_maps
init_maps:
	
; populate rotation maps
	ld	hl,rotmaps
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

; populate band maps
	ld	bc,bpat
	ld	de,bmaps
1:	ld	a,(bc)
	or	a
	jp	m,1f
	inc	bc
	push	bc
	ld	h,0
	ld	l,a
	push	de
	ld	de,rotmaps
	add	hl,de
	pop	de
	ld	b,27
	call	copy8
	pop	bc
	jp	1b
	
; populate stack maps
1:	ld	hl,rotmaps
	ld	de,tempmap
	ld	b,81
	call	copy8
	ld	hl,tempmap
	call	swap
	ld	bc,bpat
	ld	de,smaps
1:	ld	a,(bc)
	or	a
	jp	m,1f
	inc	bc
	push	bc
	ld	h,0
	ld	l,a
	push	de
	ld	de,tempmap
	add	hl,de
	pop	de
	ld	b,27
	call	copy8
	pop	bc
	jp	1b
1:	ld	hl,smaps
	ld	a,6
1:	push	af
	call	swap
	pop	af
	dec	a
	jp	nz,1b
	
; populate row maps
	ld	bc,rpat
	ld	de,rmaps
1:	ld	a,(bc)
	or	a
	jp	m,1f
	inc	bc
	push	bc
	ld	h,0
	ld	l,a
	push	de
	ld	de,rotmaps
	add	hl,de
	pop	de
	ld	b,9
	call	copy8
	pop	bc
	jp	1b
	
; populate column maps
1:	ld	hl,rotmaps
	ld	de,tempmap
	ld	b,81
	call	copy8
	ld	hl,tempmap
	call	swap
	ld	bc,rpat
	ld	de,cmaps
1:	ld	a,(bc)
	or	a
	jp	m,1f
	inc	bc
	push	bc
	ld	h,0
	ld	l,a
	push	de
	ld	de,tempmap
	add	hl,de
	pop	de
	ld	b,9
	call	copy8
	pop	bc
	jp	1b
1:	ld	hl,cmaps
	ld	a,18
1:	push	af
	call	swap
	pop	af
	dec	a
	jp	nz,1b
	
	ret

; rotate clockwise B times, (HL) -> (DE) -> (DE + 81) -> ...
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

; flip vertically in situ, (HL) -> (HL)
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

; swap rows and columns in situ, (HL) -> (HL)
swap:	ld	d,h
	ld	e,l
	ld	c,0
2:	push	bc
	call	sq2rc
	ld	a,b
	cp	c
	jp	nc,1f
	ld	b,c
	ld	c,a
	call	rc2sq
	ld	a,(hl)
	push	hl
	ld	h,0
	ld	l,c
	add	hl,de
	ld	b,(hl)
	ld	(hl),a
	pop	hl
	ld	(hl),b
1:	inc	hl
	pop	bc
	inc	c
	ld	a,c
	cp	81
	jp	nz,2b
	ret

	.globl	rotmaps, bmaps, smaps, rmaps, cmaps
	.lcomm	rotmaps, 81 * 8
	.lcomm	bmaps, 81 * 6
	.lcomm	smaps, 81 * 6
	.lcomm	rmaps, 81 * 18
	.lcomm	cmaps, 81 * 18
	.lcomm	tempmap, 81
	
; ==============================================================================
; Band swapping pattern
; 
	.data
bpat:	.byte	0, 27, 54
	.byte	0, 54, 27
	.byte	27, 0, 54
	.byte	27, 54, 0
	.byte	54, 0, 27
	.byte	54, 27, 0
	.byte	-1

; ==============================================================================
; Row swapping pattern
; 
	.data
rpat:	.byte	0, 9, 18, 27, 36, 45, 54, 63, 72
	.byte	0, 18, 9, 27, 36, 45, 54, 63, 72
	.byte	9, 0, 18, 27, 36, 45, 54, 63, 72
	.byte	9, 18, 0, 27, 36, 45, 54, 63, 72
	.byte	18, 0, 9, 27, 36, 45, 54, 63, 72
	.byte	18, 9, 0, 27, 36, 45, 54, 63, 72
	.byte	0, 9, 18, 27, 36, 45, 54, 63, 72
	.byte	0, 9, 18, 27, 45, 36, 54, 63, 72
	.byte	0, 9, 18, 36, 27, 45, 54, 63, 72
	.byte	0, 9, 18, 36, 45, 27, 54, 63, 72
	.byte	0, 9, 18, 45, 27, 36, 54, 63, 72
	.byte	0, 9, 18, 45, 36, 27, 54, 63, 72
	.byte	0, 9, 18, 27, 36, 45, 54, 63, 72
	.byte	0, 9, 18, 27, 36, 45, 54, 72, 63
	.byte	0, 9, 18, 27, 36, 45, 63, 54, 72
	.byte	0, 9, 18, 27, 36, 45, 63, 72, 54
	.byte	0, 9, 18, 27, 36, 45, 72, 54, 63
	.byte	0, 9, 18, 27, 36, 45, 72, 63, 54
	.byte	-1

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
	dec	bc
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
2:	ld	(de),a
	inc	hl
	inc	de
	pop	af
	dec	a
	jp	nz,1b
	ret
	
	.end
