; fixseed128.s
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
; fixseed128 - make sure the seed is not 0
; 
;   input:  (seed) - current seed
; 
;   output: (seed) - new seed
; 
;   uses:   A, B, H, L
;
	.text
	.globl	fixseed128
fixseed128:
	ld	hl,seed128
	ld	b,16
1:	ld	a,(hl)
	inc	hl
	or	a
	ret	nz
	dec	b
	jp	nz,1b
	dec	hl
	dec	(hl)
	ret

	.end
