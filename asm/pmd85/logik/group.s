; group.s
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


; Search speed-up groups.

	.include "logik.inc"
	
; ==============================================================================
; sc2gr - convert score to group number
; 
;   input:  A - score
; 
;   output: A - group number
; 
;   uses:   A, B, H, L
; 
	.text
	.globl	sc2gr
sc2gr:
	ld	b,a
	ld	hl,scgr
1:	ld	a,(hl)
	inc	hl
	or	a
	ret	z
	and	0x3f
	cp	b
	jp	nz,1b
	ld	a,(hl)
	rlca
	rlca
	and	0x03
	ret
	
; ==============================================================================
; Score -> group
; 
	.data
scgr:
	.byte	(1 << 6) | (0 << 3) | 0		; (0,0) -> 1
	.byte	(2 << 6) | (0 << 3) | 1		; (0,1) -> 2
	.byte	(1 << 6) | (0 << 3) | 2		; (0,2) -> 1
	.byte	(2 << 6) | (0 << 3) | 3		; (0,3) -> 2
	.byte	(3 << 6) | (1 << 3) | 0		; (1,0) -> 3
	.byte	(3 << 6) | (1 << 3) | 1		; (1,1) -> 3
	.byte	0				; end mark
	
; ==============================================================================
; Groups
; 
	.globl	groups
	.data
groups:
	.include "group.inc"
	
	.end
