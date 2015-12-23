; scroll.s
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


; Modified ROLL of the original monitor.

	.include "pmd85.inc"
	
; ==============================================================================
; scroll - scroll display
; 
;   input:  (radsir) - line height
; 	    (iiroll) - number of scrolled scanlines
; 	    (poroll) - address of the last text line
; 	    (rsirrad) - number of erased scanlines
; 	    (curroll) - address of the next line
; 
;   output: (cursor) - new cursor address
;    	    (enlnw) - maximum number of characters per line
; 
;   uses:   all
; 
	.text
	.globl	scroll
scroll:
	ld	hl,0
	add	hl,sp
	ld	(tstack),hl
	ld	hl,(radsir)
	ld	bc,0x0c000
	add	hl,bc
	ld	sp,hl
	ld	hl,0xc000 - 1
	ld	a,(iiroll)
2:	ld	b,8
1:	.rept	3
	pop	de
	inc	hl
	ld	(hl),e
	inc	hl
	ld	(hl),d
	.endr
	dec	b
	jp	nz,1b
	ex	de,hl
	ld	hl,0x0010
	add	hl,sp
	ld	sp,hl
	ld	hl,0x10
	add	hl,de
	dec	a
	jp	nz,2b
	ld	hl,(tstack)
	ld	sp,hl
	ld	hl,(poroll)
	ex	de,hl
	ld	a,(rsirrad)
	ld	b,a
	ld	hl,(curroll)
	jp	part_erase

	.comm	tstack, 2
	
	.end
