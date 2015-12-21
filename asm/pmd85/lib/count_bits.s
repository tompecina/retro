; count_bits.s
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
; count_bits - count bits in an 8-byte array
; 
;   input:  (HL) - array
; 
;   output: A - number of bits
; 
;   uses:   B, D, E, H, L
; 
	.text
	.globl	count_bits
count_bits:
	ld	de,bitcounts
	ld	b,8
	xor	a
1:	push	hl
	ld	l,(hl)
	ld	h,0
	add	hl,de
	add	a,(hl)
	pop	hl
	inc	hl
	dec	b
	jp	nz,1b
	ret

	.globl	bitcounts
	.lcomm	bitcounts, 256
	
	.end
