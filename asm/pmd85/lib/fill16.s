; fill16.s
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
; zerofill16,fill16 - zero/fill area
; 
;   input:  HL - start of area
;           BC - number of bytes (0 = 65536 bytes)
;           A - fill value (only applicable to fill)
; 
;   uses:   A, B, C, D, H, L
; 
	.text
	.globl	zerofill16
zerofill16:
	xor	a
	.globl	fill16
fill16:
	ld	d,a
1:	ld	(hl),d
	inc	hl
	dec	bc
	ld	a,b
	or	c
	jp	nz,1b
	ret

	.end
