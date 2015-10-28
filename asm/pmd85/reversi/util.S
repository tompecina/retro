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
; mul16 - signed 16-bit multiplication
; 
;   input:  HL, DE
; 
;   output: HL = HL * DE
; 
;   uses:   A, B, C, D, E
; 
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
;   uses:   A, B, C
; 
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
        ex	de,hl
        call    deneg
        ex	de,hl
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
; 
;   uses:   A
; 
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
; signex - sign-extend A to HL
; 
;   input:  A
; 
;   output: HL = A
; 
;   uses:   A
; 
	.global	signex
signex:
	ld	l,a
	rla
	ld	h,0
	ret	nc
	dec	h
	ret

	.end
 
