; reflect_byte.s
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
; reflect_byte - reverse bit order of a byte
; 
;   input:  A - input byte
; 
;   output: A = A reflected
; 
;   uses:   D, E, H, L
; 
	.text
	.globl	reflect_byte
reflect_byte:
	ld	hl,byterefl
	ld	e,a
	ld	d,0
	add	hl,de
	ld	a,(hl)
	ret

	.globl	byterefl
	.lcomm	byterefl, 256

	.end
