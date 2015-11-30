; sedit.s
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


; Simple editing.

	.include "pmd85.inc"
	
; ==============================================================================
; sedit - enter string with simple editing facilities
; 
;   input:  (cursor) - cursor address
; 	    (color) - color mask
;   	    B - minimum number of characters
;   	    C - maximum number of characters
;   	    (HL) - buffer (with capacity of at least C + 1)
;   	    (ikf) - inklav funcion
;   	    (vf) - validation function (A = char, C = position -> CY if invalid)
; 
;   output: (HL) - zero-terminated entry
;   	    C - number of characters
; 
;   uses:   all
; 
	.text
	.globl	sedit
sedit:	inc	b
	xor	a
	ld	(nchar),a
	call	shcur
2:	call	indcall
	dw	ikf
	cp	KEOL
	jp	nz,1f
	ld	a,(nchar)
	inc	c
	cp	b
	jp	c,2b

	
	
1:	cp	KLEFT
	jp	nz,1f

1:	cp	KCLR
	jp	nz,1f

1:	

	
shcur:	ld	a,0x7f
	jp	prtout
hdcur:	ld	a,' '
	jp	prtout
svf:	cp	0x20
	ret

	.lcomm	nchar, 1

	.data
	.globl	ikf, vf
ikf:	.word	inklav
vf:	.word	svf
	
	.end
