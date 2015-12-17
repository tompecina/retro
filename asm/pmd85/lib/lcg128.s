; lcg128.s
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
; lcg128 - carry out one LCG iteration
; 
;   input:  (seed128) - current seed
; 
;   output: (seed128) - new seed
; 
;   uses:   all
;
	.text
	.globl	lcg128
lcg128:
	call	fixseed128
	ld	hl,consta
	ld	de,mul1
	ld	b,16
	call	copy8
	ld	hl,seed128
	ld	de,mul2
	ld	b,16
	call	copy8
	ld	hl,seed128
	ld	(hl),1
	inc	hl
	ld	b,15
	call	zerofill8
	ld	b,128
7:	ld	c,16
	ld	hl,mul2 + 15
	or	a
1:	ld	a,(hl)
	rra
	ld	(hl),a
	dec	hl
	dec	c
	jp	nz,1b
	jp	nc,2f
	ld	hl,seed128
	ld	de,mul1
	ld	c,16
	or	a
3:	ld	a,(de)
	adc	a,(hl)
	ld	(hl),a
	inc	hl
	inc	de
	dec	c
	jp	nz,3b
2:	ld	hl,mul1
	ld	c,16
	or	a
4:	ld	a,(hl)
	rla
	ld	(hl),a
	inc	hl
	dec	c
	jp	nz,4b
	dec	b
	jp	nz,7b
	ret

	.lcomm	mul1, 16
	.lcomm	mul2, 16
	
	.data
	.globl	seed128, seedp128
seed128:
	.octa	0x89a31454abe0d9453a542d11015f6cb4
seedp128:
	.byte	15
consta:	.octa	47026247687942121848144207491837418733

	.end
