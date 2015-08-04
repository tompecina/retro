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
	
; 8255 ports
PORTA	equ	0f8h	; port A
PORTB	equ	0f9h	; port B
PORTC	equ	0fah	; port C
CPORT	equ	0fbh	; control port
	
; ==============================================================================
; init8255 - initialize 8255
;
	.global init8255
init8255:
	ld	a, 0x8a
	out    	(CPORT), a
	ret
	
; ==============================================================================
; cld - clear display
;
	.global cld
cld:
	xor	a
	out	(PORTC), a
	ret
	
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
	cpl
	out	(PORTC), a
	
; ==============================================================================
; getsc - get scancode from the current column
; 
;   output: scan code
;
	.global getsc
getsc:
	in	a, (PORTC)
	cpl
	and	0x70
	ld	l, a
	ld	h, 0
	ret

; ==============================================================================
; outp - output to port (without self-modification)
; 
;   input:  $1 - port
;           $2 - data
;
	.global outp
outp:
	ld	hl, 0xc9
	push	hl
	ld	hl, 4
	add	hl, sp
	ld	a, (hl)
	inc	hl
	inc	hl
	ld	b, (hl)
	ld	c, 0xd3
	push	bc
	ld	hl, 0
	add	hl, sp
	ld	bc, outp1
	push	bc
	jp	(hl)
outp1:	pop	hl
	pop	hl
	ret
	
; ==============================================================================
; inp - input from port (without self-modification)
; 
;   input:  $1 - port
;   output: data
;
	.global inp
inp:
	ld	hl, 0xc9
	push	hl
	ld	hl, 4
	add	hl, sp
	ld	b, (hl)
	ld	c, 0xdb
	push	bc
	ld	hl, 0
	add	hl, sp
	ld	bc, inp1
	push	bc
	jp	(hl)
inp1:	pop	hl
	pop	hl
	ld	l, a
	ld	h, 0
	ret
	
; ==============================================================================
; exit - exit to monitor via RST 7
;
	.global exit
exit:
	rst	0x38
	
	.end
