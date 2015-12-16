; inkey.s
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


; Modify kmap according to the model.

	.include "pmd85.inc"
	
; ==============================================================================
; set_kmap - modify keymap for PMD 85-1/2
; 
;   uses:   A, H, L
;
	.text
	.globl	set_kmap
set_kmap:
	ld	hl,0x8000
	ld	a,(hl)
	cpl
	ld	(hl),a
	cp	(hl)
	jp	nz,1f
	cpl
	ld	(hl),a
	jp	nz,set_kmap2
1:	cp	0xc3 ^ 0xff
	jp	nz,set_kmap2
	inc	hl
	ld	a,(hl)
	cp	0x03
	jp	nz,set_kmap2
	jp	set_kmap1
	
; ==============================================================================
; set_kmap1 - modify keymap for PMD 85-1
; 
;   uses:   A
;
	.text
	.globl	set_kmap1
set_kmap1:
	ld	a,'['
	ld	(kmap1),a
	ld	a,']'
	ld	(kmap2),a
	ret
	
; ==============================================================================
; set_kmap2 - modify keymap for PMD 85-2
; 
;   uses:   A
;
	.text
	.globl	set_kmap2
set_kmap2:
	ld	a,']'
	ld	(kmap1),a
	ld	a,'['
	ld	(kmap2),a
	ret
	
	.end
