; crc16.s
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
; crc16 - update CRC-16-CCITT
; 
;   input:  HL - initial CRC
; 	    A - input byte
; 
;   output: HL - updated CRC
; 
;   uses:   A, B
; 
	.text
	.globl	crc16
crc16:
	xor	h
	ld	h,a
	ld	b,8
2:	add	hl,hl
	jp	nc,1f
	ld	a,h
	xor	0x10
	ld	h,a
	ld	a,l
	xor	0x21
	ld	l,a
1:	dec	b
	jp	nz,2b
	ret
	
	.end
