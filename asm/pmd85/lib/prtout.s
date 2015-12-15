; prtout.s
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


; Modified PRTOUT/NEWLINE of the original monitor.

	.include "pmd85.inc"
	
; ==============================================================================
; prtout/newline - write character and move cursor/new line
; 
;   input:  A - character code (only prtout)
; 	    (cursor) - cursor address
; 	    (color) - color mask
;   	    (radsir) - line height
; 	    (vyska) - writing limit
; 
;   output: (cursor) - new cursor address
;    	    (enlnw) - maximum number of characters per line
; 
;   uses:   A
; 
	.text
	.globl	prtout, newline
newline:
	ld	a,CR
prtout:
	push	bc
	push	de
	push	hl
	ld	hl,2f
	push	hl
	cp	CR
	jp	nz,1f
	ld	hl,(cursor)
	ld	a,(vyska)
	cp	h
	jp	c,scroll
	ld	a,l
	and	0xc0
	ld	l,a
	ex	de,hl
	ld	hl,(radsir)
	add	hl,de
	ld	(cursor),hl
	ret
1:	cp	CLS
	jp	z,erase
	cp	LF
	ret	z
	cp	VT
	jp	nz,1f
	ld	hl,(cursor)
	ld	de,0x0040
	add	hl,de
	ld	(cursor),hl
	ret
1:	cp	BS
	ret	z		; BS is not supported
	ld	c,a
	ld	a,(cursor)
	and	0x3f
	ld	b,a
	ld	a,(enlnw)
	cp	b
	call	z,newline
	ld	a,c
	ld	hl,cursor
	inc	(hl)
	ld	hl,(cursor)
	dec	hl
	jp	wrchar
2:	pop	hl
	pop	de
	pop	bc
	ret
	
	.end
