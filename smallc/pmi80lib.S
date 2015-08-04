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
; _init8255 - initialize 8255
;
	.global _init8255
_init8255:
	ld	a, 0x8a
	out    	(CPORT), a
	ret
	
; ==============================================================================
; _cld - clear display
;
	.global _cld
_cld:
	xor	a
	out	(PORTC), a
	ret
	
; ==============================================================================
; _setssd - display one character on SSD
; 
;   input:  $1 - position
;           $2 - code
;
	.global _setssd
_setssd:
	ld	hl, 2
	add	hl, sp
	ld	b, (hl)
	inc	hl
	inc	hl
	ld	c, (hl)
	call	_cld
	ld	a, b
	out	(PORTA), a
	ld	a, c
	cpl
	out	(PORTC), a
	ret
	
; ==============================================================================
; _getsc - get scancode from the current column
; 
;   output: scan code
;
	.global _getsc
_getsc:
	in	a, (PORTC)
	cpl
	and	0x70
	ld	l, a
	ld	h, 0
	ret

; ==============================================================================
; _outp - output to port (without self-modification)
; 
;   input:  $1 - port
;           $2 - data
;
	.global _outp
_outp:
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
	ld	bc, .L1
	push	bc
	jp	(hl)
.L1:	pop	hl
	pop	hl
	ret
	
; ==============================================================================
; _inp - input from port (without self-modification)
; 
;   input:  $1 - port
;   output: data
;
	.global _inp
_inp:
	ld	hl, 0xc9
	push	hl
	ld	hl, 4
	add	hl, sp
	ld	b, (hl)
	ld	c, 0xdb
	push	bc
	ld	hl, 0
	add	hl, sp
	ld	bc, .L2
	push	bc
	jp	(hl)
.L2:	pop	hl
	pop	hl
	ld	l, a
	ld	h, 0
	ret
	
; ==============================================================================
; _exit - exit to monitor via RST 7
;
	.global _exit
_exit:
	rst	0x38
	
	.end
