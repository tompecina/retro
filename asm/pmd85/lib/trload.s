; trload.s
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

	.include "pmd85.inc"
	
; ==============================================================================
; trload - load data block from tape recorder
; 
;   input:  HL - start address
;	    DE - number of bytes - 1
; 
;   output: (HL) - data block
;	    NZ on error or STOP pressed
; 
;   uses:   A, B, D, E
; 
	.text
	.globl	trload
trload:
	push	hl
	ld	bc,0x00ff
	ld	a,(hw1)
	or	a
	jp	z,3f	
	
; version for PMD 85-2 or higher
2:	call	trbyte
	jp	c,2f
	inc	c
	dec	c
	jp	z,1f
	ld	(hl),a
1:	inc	hl
	add	a,b
	ld	b,a
	ld	a,d
	or	e
	dec	de
	jp	nz,2b
	call	trbyte
	cp	b
2:	pop	hl
	ret
	
; version for PMD 85-1
3:	call	waimgi
	in	a,(USART_DATA)
	dec	c
	inc	c
	jp	z,1f
	ld	(hl),a
1:	add	a,b
	ld	b,a
	inc	hl
	dec	de
	ld	a,d
	cp	0xff
	jp	nz,3b
	call	waimgi
	in	a,(USART_DATA)
	cp	b
	pop	hl
	ret
	
	.end
