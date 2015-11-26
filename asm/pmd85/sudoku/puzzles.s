; db.s
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


; Database of puzzles.

	.include "sudoku.inc"
	
; ==============================================================================
; Constants
	
	.globl	NUMD, NUMP
	.equiv	NUMD, 4		; number of difficulties
	.equiv	NUMP, 128	; number of puzzles for each difficulty
	
; ==============================================================================
; get_puzzle - decompress puzzle
; 
;   input:  B - difficulty
; 	    C - puzzle number
; 	    HL - destination address
; 
;   output: (HL) - decompressed puzzle
; 
;   uses:   all
; 
	.text
	.globl	get_puzzle
get_puzzle:
	
; get address
	push	hl
	ld	a,c
	add	a,a
	ld	e,a
	ld	a,b
	rra
	ld	d,a
	ld	a,e
	rra
	ld	e,a
	ld	hl,9
	call	mul16
	ld	de,puzzles
1:	ld	a,h
	or	l
	jp	z,1f
2:	ld	a,(de)
	inc	de
	rla
	jp	nc,2b
	jp	1b
	
; reset destination
1:	pop	hl
	push	hl
	ld	b,81
	call	zerofill
	pop	hl

; decompress puzzle
	ld	b,1
1:	ld	a,(de)
	cp	0xff
	jp	z,2f
	and	0x7f
	push	hl
	push	bc
	ld	b,0
	ld	c,a
	add	hl,bc
	pop	bc
	ld	(hl),b
	pop	hl
	ld	a,(de)
	rla
	jp	nc,3f
2:	inc	b
3:	inc	de
	ld	a,b
	cp	10
	jp	nz,1b
	ret
	
; ==============================================================================
; puzzles - database of compressed puzzles
; 
	.data
	.globl	puzzles
puzzles:
	.include "puzzles.inc"

	.end
