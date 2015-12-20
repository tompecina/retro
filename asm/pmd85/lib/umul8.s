; umul8.s
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
; umul8 - unsigned 8-bit multiplication
; 
;   input:  B, C
; 
;   output: HL = B * C
; 
;   uses:   all
; 
	.text
	.globl	umul8
umul8:
	ld	hl,0
	ld	d,h
	ld	e,b
2:	ld	a,c
	or	a
	ret	z
	rra
	ld	c,a
	jp	nc,1f
	add	hl,de
1:	ex	de,hl
	add	hl,hl
	ex	de,hl
	jp	2b
	
	.end
