; util.S
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
	.global	zerofill
zerofill:
	xor	a
	.global	fill
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
	.global	copy8
copy8:
	ld	a,(hl)
	ld	(de),a
	inc	hl
	inc	de
	dec	b
	jp	nz,copy8
	ret
	
; ==============================================================================
; copy16 - copy area
; 
;   input:  (HL) - source area
;           (DE) - destination area
;           BC - number of bytes (1-65536)
; 
;   uses:   all
; 
	.text
	.global	copy16
copy16:
	ld	a,(hl)
	ld	(de),a
	inc	hl
	inc	de
	dec	bc
	ld	a,b
	or	c
	jp	nz,copy16
	ret
	
; ==============================================================================
; mul16 - signed 16-bit multiplication
; 
;   input:  HL, DE
; 
;   output: HL = HL * DE
; 
;   uses:   A, B, C, D, E
; 
	.text
	.global	mul16
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
; div16 - signed 16-bit division
; 
;   input:  HL, DE
; 
;   output: HL = DE / HL
;           DE = DE % HL
; 
;   uses:   all
; 
	.text
	.global	div16
div16:	ld      b,h
        ld      c,l
        ld      a,d
        xor     b
        push    af
        ld      a,d
        or      a
        call	m,deneg
        ld      a,b
        or      a
        call	m,bcneg
        ld      a,16
        push    af
        ex	de,hl
        ld      de,0
1:	add     hl,hl
        call    rdel
        jp	z,2f
        call    cmpbcde
        jp	m,2f
        ld      a,l
        or      1
        ld      l,a
        ld      a,e
        sub     c
        ld      e,a
        ld      a,d
        sbc     a,b
        ld      d,a
2:	pop     af
        dec     a
        jp	z,3f
        push    af
        jp      1b
3:	pop     af
        ret	p
        call    deneg
	jp	hlneg
	
; ==============================================================================
; hlneg - negate HL
; 
;   input:  HL
; 
;   output: HL = -HL
; 
;   uses:   A
; 
	.text
	.global	hlneg
hlneg:
        ld      a,h
        cpl
        ld      h,a
        ld      a,l
        cpl
        ld      l,a
        inc     hl
        ret
	
; ==============================================================================
; deneg - negate DE
; 
;   input:  DE
; 
;   output: DE = -DE
; 
;   uses:   A
; 
	.text
	.global	deneg
deneg:
        ld      a,d
        cpl
        ld      d,a
        ld      a,e
        cpl
        ld      e,a
        inc     de
        ret
	
; ==============================================================================
; bcneg - negate BC
; 
;   input:  BC
; 
;   output: BC = -BC
; 
;   uses:   A
; 
	.text
	.global	bcneg
bcneg:
        ld      a,b
        cpl
        ld      b,a
        ld      a,c
        cpl
        ld      c,a
        inc     bc
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
	.global	rdel
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
; cmpbcde - compare BC and DE
; 
;   input:  BC, DE
; 
;   output: F = (DE - BC)
; 
;   uses:   A
; 
	.text
	.global	cmpbcde
cmpbcde:
        ld      a,e
        sub     c
        ld      a,d
        sbc     a,b
        ret

; ==============================================================================
; shrhlb - signed 16-bit right shift
; 
;   input:  HL, B
; 
;   output: HL = HL >> B
; 
;   uses:   A, B
; 
	.text
	.global	shrhlb
shrhlb:
	ld	a,h
	rla
	ld	a,h
	rra
	ld	h,a
	ld	a,l
	rra
	ld	l,a
	dec	b
	jp	nz,shrhlb
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
	.global	signexhl
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
	.global	signexde
signexde:
	ld	e,a
	rla
	ld	d,0
	ret	nc
	dec	d
	ret

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
	.global	udiv16_8
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
; crc16 - update CRC-16-CCITT
; 
;   input:  HL - initial CRC
; 	    A - input byte
; 
;   output: HL - updated CRC
; 
;   uses:   A, B
; 
	.text
	.global	crc16
crc16:
	xor	h
	ld	h,a
	ld	b,8
2:	add	hl,hl
	jp	nc,1f
	ld	a,h
	xor	0x10
	ld	h,a
	ld	a,l
	xor	0x21
	ld	l,a
1:	dec	b
	jp	nz,2b
	ret
	
; ==============================================================================
; init_crc24 - initialize CRC-24
; 
;   output: CHL - initial CRC
; 
;   uses:   -
; 
	.text
	.global	init_crc24
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
	.global	crc24
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
 
