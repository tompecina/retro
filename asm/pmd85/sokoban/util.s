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
; copy16 - copy area
; 
;   input:  (HL) - source area
;           (DE) - destination area
;           BC - number of bytes (1-65536)
; 
;   uses:   all
; 
	.text
	.globl	copy16
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

	.end
