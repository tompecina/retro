; init_bctbl.s
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
; init_bctbl - initialize the bit count table
; 
;   output: (bitcounts) populated
; 
;   uses:   A, B, C, H, L
; 
	.text
	.globl	init_bctbl
init_bctbl:
	ld	hl,bitcounts
	ld	c,0
3:	ld	b,0
	ld	a,c
1:	or	a
	jp	z,2f
	rra
	jp	nc,1b
	inc	b
	jp	1b
2:	ld	(hl),b
	inc	hl
	inc	c
	jp	nz,3b
	ret

	.end
