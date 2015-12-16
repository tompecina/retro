; level.s
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


; Level-related routines.

	.include "sokoban.inc"
	
; ==============================================================================
; Constants
;
	
; ==============================================================================
; init_levels - initialize levels
; 
;   uses:   all
; 
	.text
	.globl	init_levels
init_levels:
	ld	hl,def_levels_end
	ld	de,levels_end
	ld	bc,def_levels_len
1:	dec	hl
	ld	a,(hl)
	dec	de
	ld	(de),a
	dec	bc
	ld	a,b
	or	c
	jp	nz,1b
	ret	
	
; ==============================================================================
; count_levels - count levels
; 
;   output: HL - number of levels
; 
;   uses:   all
; 
	.text
	.globl	count_levels
count_levels:
	ld	hl,levels
	ld	de,0
1:	ld	c,(hl)
	inc	hl
	ld	b,(hl)
	inc	hl
	ld	a,b
	or	c
	ex	de,hl
	ret	z
	ex	de,hl
	inc	de
	add	hl,bc
	jp	1b	
	
; ==============================================================================
; get_level - decompress level
; 
;   input:  BC - level number
; 
;   output: (board) - decompressed level
;	    B - initial row of player
;	    C - initial column of player
;	    CY if not found or oversized
; 
;   uses:   all
; 
	.text
	.globl	get_level
get_level:
	ld	hl,levels
2:	ld	e,(hl)
	inc	hl
	ld	d,(hl)
	inc	hl
	ld	a,d
	or	e
	jp	nz,1f
	scf
	ret			; level not found
1:	ld	a,b
	or	c
	jp	z,1f
	dec	bc
	add	hl,de
	jp	2b
1:	ld	a,COLS
	sub	(hl)
	ret	c		; too many columns
	rra
	ld	(coff),a
	ld	a,(hl)
	inc	hl
	ld	(cols),a
	ld	a,ROWS
	sub	(hl)
	ret	c		; too many rows
	rra
	ld	(roff),a
	ld	a,(hl)
	inc	hl
	ld	(rows),a
	push	hl
	ld	hl,board
	push	hl
	ld	a,(roff)
	ld	de,ROWS
1:	dec	a
	jp	m,1f
	add	hl,de
	jp	1b
1:	ld	a,(coff)
	ld	e,a
	add	hl,de
	ld	(orig),hl
	pop	hl
	ld	bc,ROWS * COLS
	call	zerofill16
	pop	hl
	ld	b,0
again:	call	gbit
	jp	again
	ld	c,(hl)
	inc	hl
	ld	b,(hl)
	or	a		; CY = 0
	ret
gbit:	dec	b
	jp	p,1f
	ld	c,(hl)
	inc	hl
1:	ld	a,c
	rra
	ld	c,a
	ret
	
	
	.lcomm	board, ROWS * COLS
	.lcomm	rows, 1
	.lcomm	roff, 1
	.lcomm	cols, 1
	.lcomm	coff, 1
	.lcomm	orig, 2

	.end
