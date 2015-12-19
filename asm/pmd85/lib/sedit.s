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

	.include "pmd85.inc"
	
; ==============================================================================
; sedit - enter string with simple editing facilities
; 
;   input:  (cursor) - cursor address
; 	    (color) - color mask
;   	    B - minimum number of characters
;   	    C - maximum number of characters
;   	    (HL) - buffer (with capacity of at least C + 1)
;   	    (sel_inklav) - inklav funcion
;   	    (sel_val) - validation function (A = char, C = position -> CY if invalid)
; 
;   output: (HL) - zero-terminated entry
;   	    C - number of characters
; 
;   uses:   all
; 
	.text
	.globl	sedit
sedit:
	xor	a
	ld	(nchar),a
	call	shcur
2:	indcall	inklav
	cp	KEOL
	jp	nz,1f
	ld	a,(nchar)
	cp	b
	jp	c,2b
	ld	(hl),0
	ld	c,a
	ld	hl,(cursor)
	dec	hl
	ld	(cursor),hl
	jp	hdcur
1:	cp	KLEFT
	jp	nz,1f
3:	ld	a,(nchar)
	dec	a
	jp	m,2b
	ld	(nchar),a
	push	hl
	ld	hl,(cursor)
	dec	hl
	ld	(cursor),hl
	call	hdcur
	dec	hl
	ld	(cursor),hl
	pop	hl
	call	shcur
	jp	2b
1:	cp	KDEL
	jp	z,3b
1:	cp	KCLR
	jp	nz,1f
	ld	a,(nchar)
3:	or	a
	jp	z,2b
	ld	d,a
	push	hl
	ld	hl,(cursor)
	dec	hl
	ld	(cursor),hl
	call	hdcur
	dec	hl
	ld	(cursor),hl
	call	shcur
	pop	hl
	dec	hl
	ld	a,d
	dec	a
	ld	(nchar),a
	jp	3b
1:	ld	d,a
	ld	a,(nchar)
	cp	c
	jp	z,2b
	push	hl
	ld	hl,nchar
	push	bc
	ld	c,(hl)
	ld	a,d
	indcall	val
	pop	bc
	pop	hl
	jp	c,2b
	ld	(hl),d
	inc	hl
	ld	a,(nchar)
	inc	a
	ld	(nchar),a
	push	hl
	ld	hl,(cursor)
	dec	hl
	ld	(cursor),hl
	pop	hl
	ld	a,d
	call	prtout
	call	shcur
	jp	2b
shcur:	ld	a,0x7f
	jp	prtout
hdcur:	ld	a,' '
	jp	prtout

	.globl	val_char
val_char:
	cp	0x20
	ret

	.lcomm	nchar, 1

	.data
	.globl	sel_val
sel_val:
	.word	val_char
	
	.end
