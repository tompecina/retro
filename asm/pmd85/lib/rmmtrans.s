; rmmtrans.s
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
; rmmtrans - transfer data from ROM module
; 
;   input:  HL - start address in ROM module
;	    DE - start address in RAM
;	    BC - number of bytes
;
;   output: (DE) - transferred data
;
;   uses:   all
;
	.text
	.globl	rmmtrans
rmmtrans:
	ld	a,0x90
	out	(RMMPIO_CTRL),a
1:	ld	a,l
	out	(RMMPIO_PB),a
	ld	a,h
	out	(RMMPIO_PC),a
	in	a,(RMMPIO_PA)
	ld	(de),a
	inc	hl
	inc	de
	dec	bc
	ld	a,b
	or	c
	jp	nz,1b
	ld	a,0x0f
	out	(RMMPIO_CTRL),a
	ret
	
	.end
