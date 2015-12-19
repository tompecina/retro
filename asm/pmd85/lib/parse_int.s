; parse_int.s
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
; parse_int - parse non-negative integer
; 
;   input:  (HL) - zero-terminated input string
; 
;   output: HL - result
;   	    CY on error
; 
;   uses:   all
; 
	.text
	.globl	parse_int
parse_int:
	ex	de,hl
	ld	a,(de)
	inc	de
	call	val_digit
	ret	c
	ld	hl,0
1:	ld	(tmp),a
	add	hl,hl
	ret	c
	ld	b,h
	ld	c,l
	add	hl,hl
	ret	c
	add	hl,hl
	ret	c
	add	hl,bc
	ret	c
	ld	a,(tmp)
	sub	'0'
	add	a,l
	ld	l,a
	ld	a,h
	adc	a,0
	ret	c
	ld	h,a
	ld	a,(de)
	inc	de
	or	a
	ret	z
	call	val_digit
	ret	c
	jp	1b

	.lcomm	tmp, 1
	
	.end
