; dethw1.s
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
; dethw1 - detect hardware, coarse
; 
;   output: Z if PMD 85-1
;	    NZ if PMD 85-2 or higher
;	    A =0 if PMD 85-1
;	      =0xff if PMD 85-2 or higher
;	    (hw1) =0 if PMD 85-1
;		  =0xff if PMD 85-2 or higher
;
;   uses:   H, L
;
	.text
	.globl	dethw1
dethw1:
	ld	hl,0x8000
	ld	a,(hl)
	cpl
	ld	(hl),a
	cp	(hl)
	jp	nz,1f
	cpl
	ld	(hl),a
2:	ld	a,0xff
	jp	2f
1:	cp	0xc3 ^ 0xff
	jp	nz,2b
	inc	hl
	ld	a,(hl)
	cp	0x03
	jp	nz,2b
	xor	a
2:	ld	(hw1),a
	or	a
	ret

	.globl	hw1
	.lcomm	hw1, 1

	.end
