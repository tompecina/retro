; erase.s
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
; erase/part_erase - clear display/part of display
; 
;   input:  HL - new cursor address (only part_erase)
; 	    DE - end of the topmost scanline to clear + 1 (only part_erase)
; 	    B - number of scanlines to clear (only part_erase)
; 	    (cursor) - cursor address
; 	    (color) - new color mask for the erased area
;   	    (radsir) - line height
; 
;   uses:   all
; 
	.text
	.globl	erase, part_erase
erase:
	ld	bc, 0x0c300
	ld	de, 0x0c030
	ld	hl,(radsir)
	add	hl,bc
	ld	b,0
part_erase:
	ld	(cursor),hl
	ld	a,(color)
	ld	hl,0
	add	hl,sp
	ex	de,hl
	ld	sp,hl
	ld	c,a
	ld	a,b
	ld	b,c
1:	.rept	24
	push	bc
	.endr
	ld	sp,0x0040
	add	hl,sp
	ld	sp,hl
	dec	a
	jp	nz,1b
	ex	de,hl
	ld	sp,hl
	ret

	.end
