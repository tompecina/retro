; detallram.s
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
; detallram - detect if AllRAM mode available
; 
;   output: Z if PMD 85-1
;	    NZ if PMD 85-2 or higher
;	    A =0xff if AllRAM mode is available
;	      =0 otherwise
;	    (allram) =0xff if AllRAM mode is available
;		     =0 otherwise
;
;   uses:   H, L
;
	.text
	.globl	detallram
detallram:
	ld	hl,0xbfff
	ld	a,(hl)
	cpl
	ld	(hl),a
	cp	(hl)
	jp	nz,1f
	cpl
	ld	(hl),a
	ld	a,0xff
	jp	2f
1:	xor	a
2:	ld	(allram),a
	or	a
	ret

	.globl	allram
	.lcomm	allram, 1

	.end
