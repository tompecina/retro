; tapeout.s
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
; tapeout - save data block to tape recorder
; 
;   input:  (HL) - data block
;	    DE - number of bytes - 1
; 
;   uses:   A, B, D, E, H, L
; 
	.text
	.globl	tapeout
tapeout:
	inc	de
	ld	b,0
1:	ld	a,(hl)
	call	usartout
	add	a,b
	ld	b,a
	inc	hl
	dec	de
	ld	a,d
	or	e
	jp	nz,1b
	ld	a,b
	jp	usartout
	
	.end
