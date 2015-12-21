; hist.s
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


; History related routines.

	.include "sokoban.inc"
	
; ==============================================================================
; Constants
; 
	
; ==============================================================================
; init_hist - initialize history (on loading new levels)
; 
;   uses:   all
; 
	.text
	.globl	init_hist
init_hist:
	call	count_levels
	ld	(hbeg),hl
	ld	hl,initsp - STACKSIZE
	ld	(hend),hl
	ret	

; ==============================================================================
; reset_hist - reset history (on starting a new level)
; 
;   uses:   H, L
; 
	.text
	.globl	reset_hist
reset_hist:
	ld	hl,(hbeg)
	ld	(hfirst),hl
	ld	hl,0
	ld	(hlast),hl
	ret
	
; ==============================================================================
; push_move - push move to history
; 
;   input:  A - move (bits 0-2 = move, bits 3-7 = don't care)
; 
;   uses:   A, B, D, E, H, L
; 
	.text
	.globl	push_move
push_move:
	and	0x07
	ld	b,a
	ld	hl,(hlast)
	ld	a,h
	or	l
	jp	nz,1f
	ld	hl,(hfirst)
	ld	(hlast),hl
	ld	(hl),b
	ret
1:	ld	a,b
	xor	(hl)
	and	0x07
	jp	nz,1f
	ld	a,(hl)
	add	a,0x08
	jp	c,1f
	ld	(hl),a
	ret
1:	call	1f
	ld	(hlast),hl
	ld	(hl),b
	ex	de,hl
	ld	hl,(hfirst)
	ex	de,hl
	ld	a,h
	cp	d
	ret	nz
	ld	a,l
	cp	e
	ret	nz
	call	1f
	ld	(hfirst),hl
	ret
1:	inc	hl
	ex	de,hl
	ld	hl,(hend)
	ex	de,hl
	ld	a,h
	cp	d
	ret	nz
	ld	a,l
	cp	e
	ret	nz
	ld	hl,(hbeg)
	ret
	
; ==============================================================================
; pop_move - pop move from history
; 
;   output: A - move (bits 0-2 = move, bits 3-7 = 0)
;	    CY if none available
; 
;   uses:   B, D, E, H, L
; 
	.text
	.globl	pop_move
pop_move:
	ld	hl,(hlast)
	ld	a,h
	or	l
	scf
	ret	z
	ld	a,(hl)
	sub	0x08
	jp	c,1f
	ld	(hl),a
	and	0x07		; CY = 0
	ret
1:	ld	b,a
	ex	de,hl
	ld	hl,(hfirst)
	ex	de,hl
	ld	a,h
	cp	d
	jp	nz,1f
	ld	a,l
	cp	e
	jp	nz,1f
	ld	hl,0
	jp	2f
1:	ex	de,hl
	ld	hl,(hbeg)
	ex	de,hl
	ld	a,h
	cp	d
	jp	nz,1f
	ld	a,l
	cp	e
	jp	nz,1f
	ld	hl,(hend)
1:	dec	hl
2:	ld	(hlast),hl
	ld	a,b
	and	0x07		; CY = 0
	ret
	
; ==============================================================================
; Variables
; 
	.lcomm	hbeg, 2
	.lcomm	hend, 2
	.lcomm	hfirst, 2
	.lcomm	hlast, 2

	.end
