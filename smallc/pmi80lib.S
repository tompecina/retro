; pmi80lib.S
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


; This is the assembler library for Tesla PMI-80 hardware.

	.section .text

; ==============================================================================
; Constants
	
DISPLEN	equ	9	; display length (equal to number of keyboard columns)

; 8255 ports
PORTA	equ	0f8h	; port A
PORTB	equ	0f9h	; port B
PORTC	equ	0fah	; port C
CPORT	equ	0fbh	; control port
	
; ==============================================================================
; setssd - display one character on SSD
; 
;   input:  $1 - position
;           $2 - code
;
	.global setssd
setssd:
	ld	hl, 2
	add	hl, sp
	ld	b, (hl)
	inc	hl
	inc	hl
	ld	c, (hl)
	xor	a
	out	(PORTC), a
	ld	a, b
	out	(PORTA), a
	ld	a, c
	out	(PORTC), a
	ret
	
	.end
