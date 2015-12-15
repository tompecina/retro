; udiv8.s
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
; udiv8 - unsigned 8-bit division
; 
;   input:  E, C
; 
;   output: E = E / C
;           C = E % C
; 
;   uses:   A, B, D
; 
	.text
	.globl	udiv8
udiv8:
	ld	d,0
	ld	b,8
1:	ld	a,e
	rla
	ld	e,a
	ld	a,d
	rla
	sub	c
	jp	nc,2f
	add	a,c
2:	ld	d,a
	dec	b
	jp	nz,1b
	ld	c,a
	ld	a,e
	rla
	cpl
	ld	e,a
	ret

	.end
