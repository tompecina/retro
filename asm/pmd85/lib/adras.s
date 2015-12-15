; adras.s
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


; Copy of original monitor's ADRAS, modified for 10-cell glyphs.

	.include "pmd85.inc"
	
; ==============================================================================
; adras - return glyph address
; 
;   input:  A - character code
; 	    (tascii) - table of glyphs
; 
;   output: HL - glyph address + 8
; 
;   uses:   all
; 
	.text
	.globl	adras
adras:
	ld	b,a
	and	0x1f
	ld	c,a
	ld	a,b
	sub	c
	rrca
	rrca
	rrca
	rrca
	ld	l,a
	ld	h,0
	ld	b,h
	ld	a,c
	add	a,a
	add	a,a
	add	a,c
	ld	c,a
	ld	de,tascii
	add	hl,de
	ld	d,(hl)
	inc	hl
	ld	a,(hl)
	ld	hl,undef_glyph + 10
	cp	0xff
	ret	z
	ld	l,d
	ld	h,a
	add	hl,bc
	add	hl,bc
	ld	a,h
	inc	a
	ret

	.end
