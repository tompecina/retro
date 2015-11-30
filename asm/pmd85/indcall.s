; indcall.s
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


; Indirect call.

	.include "pmd85.inc"
	
; ==============================================================================
; indcall - call indirectly addressed routine
; 
;   input:  ((SP)) - call address
; 
;   uses:   -
; 
	.text
	.globl	indcall
indcall:
	ld	(thl),hl
	ex	de,hl
	ex	(sp),hl
	ld	e,(hl)
	inc	hl
	ld	d,(hl)
	inc	hl
	ex	(sp),hl
	ex	de,hl
	push	af
	ld	a,(hl)
	inc	hl
	ld	h,(hl)
	ld	l,a
	pop	af
	push	hl
	ld	hl,(thl)
	ret

	.lcomm	thl, 2
	
	.end
