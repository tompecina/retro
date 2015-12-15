; mul16.s
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

	.end
