; crc24.s
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
; init_crc24 - initialize CRC-24
; 
;   output: CHL - initial CRC
; 
;   uses:   -
; 
	.text
	.globl	init_crc24
init_crc24:
	ld	c,0xb7
	ld	hl,0x04ce
	ret
	
; ==============================================================================
; crc24 - update CRC-24
; 
;   input:  CHL - initial CRC
; 	    A - input byte
; 
;   output: CHL - updated CRC
; 
;   uses:   A, B
; 
	.text
	.globl	crc24
crc24:
	xor	c
	ld	c,a
	ld	b,8
2:	add	hl,hl
	ld	a,c
	rla
	ld	c,a
	jp	nc,1f
	ld	a,c
	xor	0x86
	ld	c,a
	ld	a,h
	xor	0x4c
	ld	h,a
	ld	a,l
	xor	0xfb
	ld	l,a
1:	dec	b
	jp	nz,2b
	ret
	
	.end
