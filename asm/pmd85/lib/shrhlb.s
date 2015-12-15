; shrhlb.s
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
; shrhlb - signed 16-bit right shift
; 
;   input:  HL, B
; 
;   output: HL = HL >> B
; 
;   uses:   A, B
; 
	.text
	.globl	shrhlb
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

	.end
