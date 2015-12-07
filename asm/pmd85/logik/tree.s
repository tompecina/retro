; tree.s
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


; Search tree.

	.include "logik.inc"
	
; ==============================================================================
; Constants
;

; ==============================================================================
; search_tree - search the tree
; 
;   input:  A - last score of 0xff if first guess requested
; 
;   output: HL - guess
;	    CY if none available
; 
;   uses:   all
; 
	.text
	.globl	search_tree
search_tree:
	cp	0xff
	jp	nz,1f
	ld	hl,tree
2:	ld	e,(hl)
	inc	hl
	ld	d,(hl)
	inc	hl
	ld	(tptr),hl
	ex	de,hl
	or	a		; CY = 0
	ret
1:	ld	b,a
	ld	hl,(tptr)
	ld	a,(hl)
	ld	c,a
	and	0x80
	jp	nz,3f
	scf
	ret
3:	ld	a,c
	and	0x3f
	cp	b
	inc	hl
	jp	z,1f
	ld	a,c
	and	0x40
	jp	z,1f
	or	a		; CY = 0
	ret
1:	inc	hl
	inc	hl
	ld	a,(hl)
	ld	c,a
	and	0x80
	jp	z,3b
	ld	d,1
	inc	hl
	inc	hl
	inc	hl
	ld	a,c
	and	0x40
	jp	z,?
	dec	c
	jp	z,4b
	
	.lcomm	tptr, 2
	
; ==============================================================================
; Tree
; 
	.data
tree:
	.include "tree.inc"
	.byte	0		; end mark
	
	.end
