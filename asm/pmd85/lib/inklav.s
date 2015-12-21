; inklav.s
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


; Copy of original monitor's routine.

	.include "pmd85.inc"
	
; ==============================================================================
; inklav - wait for key
; 
;   output: A - ASCII code of the key
; 
;   uses:   -
; 
	.text
	.globl	inklav
inklav:
	push	bc
	push	de
	push	hl
1:	call	inkey
	jp	z,1b
	pop	hl
	pop	de
	pop	bc
	ret

	.data
	.globl	sel_inklav
sel_inklav:
	.word	inklav
	
	.end
