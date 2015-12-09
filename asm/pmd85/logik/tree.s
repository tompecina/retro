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
	.equiv	START, 0x80
	.equiv	STOP, 0x40
	
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
	ld	b,a
	inc	a
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
1:	ld	hl,(tptr)
	ld	a,(hl)
	ld	c,a
	and	START
	jp	nz,3f
1:	scf
	ret
3:	ld	a,c
	and	0x3f
	cp	b
	jp	nz,1f
	inc	hl
	jp	2b
1:	ld	a,c
	and	STOP
	jp	nz,1b
	ld	d,0
4:	inc	hl
	inc	hl
	inc	hl
	ld	a,(hl)
	ld	c,a
	and	START
	jp	nz,1f
	ld	a,d
	or	a
	jp	z,3b
2:	ld	a,c
	and	STOP
	jp	z,4b
	dec	d
	jp	4b	
1:	inc	d
	jp	2b
	
	.lcomm	tptr, 2
	
; ==============================================================================
; Tree
; 
	.data
tree:
	.include "tree.inc"
	
	.end
