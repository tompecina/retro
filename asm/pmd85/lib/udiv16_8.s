; udiv16_8.s
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

	.end
