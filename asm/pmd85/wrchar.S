; wrchar.S
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


; Modified WRCHAR, with ADRAS (not available in PMD 85-1).  In addition,
; this version is interrupt-compatible as it does not use the stack pointer.

	.include "pmd85.inc"
	
; ==============================================================================
; wrchar - write character
; 
;   input:  A - character code
; 	    HL - cursor address
; 	    (color) - color mask
; 
;   uses:   A
; 
	.text
	.global	wrchar
wrchar:
	push	bc
	push	de
	push	hl
	call	adras
	ex	de,hl
	pop	hl
	push	hl
	ld	bc,-128
	add	hl,bc
	ld	b,a
	ld	c,8
	ld	a,(color)
	ld	b,a
1:	dec	de
	ld	a,(de)
	xor	b
	ld	(hl),a
	push	de
	ld	de,-64
	add	hl,de
	pop	de
	dec	c
	jp	nz,1b
	ld	(hl),b
	pop	hl
	pop	de
	pop	bc
	ret

	.end
