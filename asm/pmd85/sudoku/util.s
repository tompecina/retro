; util.s
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


; These are auxiliary routines.  Some of them were originally copied from
; the Small-C runtime library, but they are so simple they could easily 
; be written from scratch.

	
; ==============================================================================
; zerofill,fill - zero/fill area
; 
;   input:  HL - start of area
;           B - number of bytes (0 = 256 bytes)
;           A - fill value (only applicable to fill)
; 
;   uses:   A, B, H, L
; 
	.text
	.globl	zerofill
zerofill:
	xor	a
	.globl	fill
fill:
	ld	(hl),a
	inc	hl
	dec	b
	jp	nz,fill
	ret
	
; ==============================================================================
; copy8 - copy area
; 
;   input:  (HL) - source area
;           (DE) - destination area
;           B - number of bytes (1-256)
; 
;   uses:   A, B, D, E, H, L
; 
	.text
	.globl	copy8
copy8:
	ld	a,(hl)
	ld	(de),a
	inc	hl
	inc	de
	dec	b
	jp	nz,copy8
	ret
	
; ==============================================================================
; mul16 - signed 16-bit multiplication
; 
;   input:  HL, DE
; 
;   output: HL = HL * DE
; 
;   uses:   all
; 
	.text
	.globl	mul16
mul16:
	ld      b,h
        ld      c,l
        ld      hl,0
1:	ld      a,c
        rrca
        jp	nc,2f
        add     hl,de
2:	xor     a
        ld      a,b
        rra
        ld      b,a
        ld      a,c
        rra
        ld      c,a
        or      b
        ret	z
        xor     a
        ld      a,e
        rla
        ld      e,a
        ld      a,d
        rla
        ld      d,a
        or      e
        ret	z
        jp      1b

; ==============================================================================
; udiv16_8 - unsigned 16-bit/8-bit division
; 
;   input:  HL, C
; 
;   output: HL = HL / C
;           DE = HL % C
; 
;   uses:   all
; 
	.text
	.globl	udiv16_8
udiv16_8:
        ld      de,0
	ld      b,16
1:	add     hl,hl
        call    rdel
        jp	z,2f
        ld      a,e
        sub     c
        ld      a,d
        sbc     a,0
        jp	m,2f
        ld      a,l
        or      1
        ld      l,a
        ld      a,e
        sub     c
        ld      e,a
        ld      a,d
        sbc     a,0
        ld      d,a
2:	dec     b
        jp	nz,1b
	ret
	
; ==============================================================================
; rdel - rotate DE left
; 
;   input:  DE
; 
;   output: DE = DE << 1
;           Z if DE == 0
; 
;   uses:   A
; 
	.text
	.globl	rdel
rdel:
	ld      a,e
        rla
        ld      e,a
        ld      a,d
        rla
        ld      d,a
        or      e
        ret
	
; ==============================================================================
; ucmpbcde - compare BC and DE (unsigned)
; 
;   input:  BC, DE
; 
;   output: F = (DE - BC)
; 
;   uses:   A
; 
	.text
	.globl	ucmpbcde
ucmpbcde:
        ld      a,e
        sub     c
        ld      a,d
        sbc     a,b
        ret

; ==============================================================================
; scmphlde - compare HL and DE (signed)
; 
;   input:  HL, DE
; 
;   output: CY if HL > DE
; 
;   uses:   A
; 
	.text
	.globl	scmphlde
scmphlde:
	ld	a,e
	sub	l
	ld	a,d
	sbc	a,h
	rra
	xor	h
	xor	d
	rla
	ret

; ==============================================================================
; scmpdehl - compare DE and HL (signed)
; 
;   input:  HL, DE
; 
;   output: CY if DE > HL
; 
;   uses:   A
; 
	.text
	.globl	scmpdehl
scmpdehl:
	ld	a,l
	sub	e
	ld	a,h
	sbc	a,d
	rra
	xor	d
	xor	h
	rla
	ret

; ==============================================================================
; signexhl - sign-extend A to HL
; 
;   input:  A
; 
;   output: HL = A
; 
;   uses:   A
; 
	.text
	.globl	signexhl
signexhl:
	ld	l,a
	rla
	ld	h,0
	ret	nc
	dec	h
	ret

; ==============================================================================
; signexde - sign-extend A to DE
; 
;   input:  A
; 
;   output: HL = A
; 
;   uses:   A
; 
	.text
	.globl	signexde
signexde:
	ld	e,a
	rla
	ld	d,0
	ret	nc
	dec	d
	ret

; ==============================================================================
; udiv8 - unsigned 8-bit division
; 
;   input:  E, C
; 
;   output: E = E / C
;           C = E % C
; 
;   uses:   A, B, D
; 
	.text
	.globl	udiv8
udiv8:
	ld	d,0
	ld	b,8
1:	ld	a,e
	rla
	ld	e,a
	ld	a,d
	rla
	sub	c
	jp	nc,2f
	add	a,c
2:	ld	d,a
	dec	b
	jp	nz,1b
	ld	c,a
	ld	a,e
	rla
	cpl
	ld	e,a
	ret
	
; ==============================================================================
; init_crc24 - initialize CRC-24
; 
;   output: CHL - initial CRC
; 
;   uses:   -
; 
	.text
	.globl	init_crc24
init_crc24:
	ld	c,0xb7
	ld	hl,0x04ce
	ret
	
; ==============================================================================
; crc24 - update CRC-24
; 
;   input:  CHL - initial CRC
; 	    A - input byte
; 
;   output: CHL - updated CRC
; 
;   uses:   A, B
; 
	.text
	.globl	crc24
crc24:
	xor	c
	ld	c,a
	ld	b,8
2:	add	hl,hl
	ld	a,c
	rla
	ld	c,a
	jp	nc,1f
	ld	a,c
	xor	0x86
	ld	c,a
	ld	a,h
	xor	0x4c
	ld	h,a
	ld	a,l
	xor	0xfb
	ld	l,a
1:	dec	b
	jp	nz,2b
	ret
	
	.end
