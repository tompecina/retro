; div16.s
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
	.globl	div16
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
        call    ucmpbcde
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
	
	.end
