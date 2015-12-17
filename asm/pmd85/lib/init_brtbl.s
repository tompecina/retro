; init_brtbl.s
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
; init_brtbl - initialize the byte reflection table
; 
;   output: (byterefl) populated
; 
;   uses:   A, B, C, D, H, L
; 
	.text
	.globl	init_brtbl
init_brtbl:
	ld	hl,byterefl
	ld	b,a
	ld	c,0x80
1:	ld	b,0x80
3:	ld	a,c
	rla
	ld	a,d
	rra
	ld	d,a
	ld	a,b
	jp	nc,3b
	ld	(hl),b
	inc	hl
	inc	c
	jp	nz,1b
	ret

	.end
