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


; Copy of original monitor's routine.

	.include "pmd85.inc"
	
; ==============================================================================
; init_kbd - initialize keyboard routines
; 
;   uses:   H, L
; 
	.text
	.globl	init_kbd
init_kbd:
	ld	hl,0x007f
	ld	(oldkey),hl
	ret
	
; ==============================================================================
; inkey - test keyboard without waiting
; 
;   output: Z - no key pressed
;           A - ASCII code of the key
; 
;   uses:   all
; 
	.text
	.globl	inkey
inkey:
	ld	hl,(oldkey)
	dec	h
	jp	m,6f
1:	in	a,(SYSPIO_PB)
	and	0x7f
	cp	l
	jp	nz,5f
	jp	3f
2:	ld	(oldkey),hl
	xor	a
	ret
3:	dec	b
	jp	nz,1b
	dec	h
	jp	nz,1b
	ld	h,7
	in	a,(SYSPIO_PA)
	and	0x0f
	ld	e,a
4:	ld	(oldkey),hl
	ld	a,l
	and	0x3f
	ld	d,a
	ld	bc,0x0010
	ld	hl,keymap - 0x10
1:	add	hl,bc
	ld	a,(hl)
	and	a
	jp	z,inkey
	cp	d
	jp	nz,1b
	ld	d,0
	add	hl,de
	inc	hl
	call	bepuk
1:	inc	d
	jp	nz,1b
	call	bepuk
	ld	a,(hl)
	cp	' ' + 1
	jp	c,1f
	in	a,(SYSPIO_PB)
	cpl
	rlca
	and	0x80
	or	(hl)
1:	ld	(ascii),a
	or	a
	ret
5:	ld	e,0
1:	in	a,(SYSPIO_PB)
	and	0x7f
	cp	l
	jp	z,3b
	dec	e
	jp	nz,1b
6:	ld	e,15
1:	dec	e
	ld	h,0
	jp	m,2b
	in	a,(SYSPIO_PA)
	and	0xf0
	or	e
	out	(SYSPIO_PA),a
	in	a,(SYSPIO_PB)
	and	0x7f
	ld	l,a
	cpl
	and	0x1f
	jp	z,1b
	ld	h,80
	jp	4b

	.lcomm	oldkey, 2
	.lcomm	ascii, 1
	
	.end
