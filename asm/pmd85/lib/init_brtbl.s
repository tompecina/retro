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

	
; ==============================================================================
; init_brtbl - initialize the byte reflection table
; 
;   output: (byterefl) populated
; 
;   uses:   A, B, C, H, L
; 
	.text
	.globl	init_brtbl
init_brtbl:
	ld	hl,byterefl
	ld	b,0
1:	ld	c,0x80
2:	ld	a,b
	rlca
	ld	b,a
	ld	a,c
	rra
	ld	c,a
	jp	nc,2b
	ld	(hl),c
	inc	hl
	inc	b
	jp	nz,1b
	ret

	.end
