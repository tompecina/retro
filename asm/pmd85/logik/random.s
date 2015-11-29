; random.s
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


; PRNG and board randomization related routines.

	.include "logik.inc"

; ==============================================================================
; start_ct1 - start PIT Counter 1, used by PRNG
; 
;   output: CY - failure
; 
;   uses:   A
;
	.text
	.globl	start_ct1
start_ct1:
	ld	a,0x70
	out	(PIT_CTRL),a
	xor	a
	out	(PIT_1),a
	out	(PIT_1),a
	in	a,(PIT_1)
	cp	0xff
	in	a,(PIT_1)
	ccf
	ret	z
	or	a
	ret
	
; ==============================================================================
; rel_ct1 - release PIT Counter 1, restore "Silence" mode
; 
;   uses:   A
;
	.text
	.globl	rel_ct1
rel_ct1:
	ld	a,0x76
	out	(PIT_CTRL),a
	ld	a,0x20
	out	(PIT_1),a
	xor	a
	out	(PIT_1),a
	ret
	
; ==============================================================================
; fixseed - make sure the seed is not 0
; 
;   input:  (seed) - current seed
; 
;   output: (seed) - new seed
; 
;   uses:   A, B, H, L
;
	.text
	.globl	fixseed
fixseed:
	ld	hl,seed
	ld	b,16
1:	ld	a,(hl)
	inc	hl
	or	a
	ret	nz
	dec	b
	jp	nz,1b
	dec	hl
	dec	(hl)
	ret

; ==============================================================================
; lcg - carry out one LCG iteration
; 
;   input:  (seed) - current seed
; 
;   output: (seed) - new seed
; 
;   uses:   all
;
	.text
	.globl	lcg
lcg:
	call	fixseed
	ld	hl,consta
	ld	de,mul1
	ld	b,16
	call	copy8
	ld	hl,seed
	ld	de,mul2
	ld	b,16
	call	copy8
	ld	hl,seed
	ld	(hl),1
	inc	hl
	ld	b,15
	call	zerofill
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
	ld	hl,seed
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
	.globl	seed
seed:	.octa	0x89a31454abe0d9453a542d11015f6cb4
seedp:	.byte	15
consta:	.octa	47026247687942121848144207491837418733

; ==============================================================================
; inklav_rnd - wait for key and modify PRNG seed
; 
;   output: A - ASCII code of the key
; 
;   uses:   -
; 
	.text
	.globl	inklav_rnd
inklav_rnd:
	call	inklav
	push	af
	push	hl
	push	de
	ld	a,(seedp)
	inc	a
	cp	16
	jp	c,1f
	xor	a
1:	ld	(seedp),a
	add	a,a
	ld	d,0
	ld	e,a
	ld	hl,seed
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
