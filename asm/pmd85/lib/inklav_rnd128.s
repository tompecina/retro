; inklav_rnd128.s
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
; inklav_rnd128 - wait for key and modify PRNG seed
; 
;   output: A - ASCII code of the key
; 
;   uses:   -
; 
	.text
	.globl	inklav_rnd128
inklav_rnd128:
	call	inklav
	push	af
	push	hl
	push	de
	ld	a,(seedp128)
	inc	a
	cp	16
	jp	c,1f
	xor	a
1:	ld	(seedp128),a
	add	a,a
	ld	d,0
	ld	e,a
	ld	hl,seed128
	add	hl,de
	in	a,(PIT_1)
	xor	(hl)
	ld	(hl),a
	inc	hl
	in	a,(PIT_1)
	xor	(hl)
	ld	(hl),a
	pop	de
	pop	hl
	pop	af
	ret

	.end
