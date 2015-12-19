; conv_int.s
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
; conv_int - convert non-negative integer to string
; 
;   input:  HL - value
;	    (DE) - destination
; 
;   output: (DE) updated
; 
;   uses:   all
; 
	.text
	.globl	conv_int
conv_int:
	ld	b,d
	ld	c,e
	ld	d,0xff
	push	de
1:	push	bc
	ld	c,10
	call	udiv16_8
	pop	bc
	ld	a,h
	or	l
	jp	z,1f
	push	de
	jp	1b
1:	ld	a,'0'
	add	a,e
	ld	(bc),a
	inc	bc
	pop	de
	inc	d
	jp	nz,1b
	ld	d,b
	ld	e,c
	ret
	
	.end
