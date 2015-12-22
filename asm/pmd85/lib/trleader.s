; trleader.s
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
; trleader - write tape recorder leader
; 
;   input:  A - byte
; 
;   uses:   A, H, L
; 
	.text
	.globl	trleader
trleader:
	ld	l,16
1:	ld	h,a
	call	usartout
	ld	a,h
	dec	l
	jp	nz,1b
	ret
	
	.end
