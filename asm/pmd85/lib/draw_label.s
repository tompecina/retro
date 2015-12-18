; draw_label.s
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
; draw_label - display label
; 
;   input:  (HL) - label data
;           DE - destination
;           (color) - color mask
; 
;   uses:   all
; 
	.text
	.globl	draw_label
draw_label:
	ld	c,(hl)		; C = rows
	inc	hl
	ld	b,(hl)		; B = columns
	inc	hl
2:	push	bc
	push	de
1:	ld	a,(color)
	xor	(hl)
	inc	hl
	ld	(de),a
	inc	de
	dec	b
	jp	nz,1b
	dec	c
	jp	z,1f
	ex	(sp),hl
	ld	de,64
	add	hl,de
	ex	de,hl
	pop	hl
	pop	af
	ld	b,a
	jp	2b
1:	pop	hl
	pop	hl
	ret
	
	.end
