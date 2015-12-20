; jpvect.s
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


; ==============================================================================
; jpvect - jump according to vector table
; 
;   input:  A - selection
;   	    (HL) - vector table, zero-terminated, followed by default vector
; 
;   uses:   A, B, H, L
; 
	.text
	.globl	jpvect
jpvect:
	ld	b,a
2:	ld	a,(hl)
	inc	hl
	or	a
	jp	z,1f
	cp	b
	jp	z,1f
	inc	hl
	inc	hl
	jp	2b
1:	ld	a,(hl)
	inc	hl
	ld	h,(hl)
	ld	l,a
	jp	(hl)
	
	.end
