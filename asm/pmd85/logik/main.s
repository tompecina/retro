; main.s
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


; The game of Logik (Super Mastermind) for Tesla PMD 85.

	.include "logik.inc"

; ==============================================================================
; Langage file inclusion
;
	.ifdef	en
	.include "lang-en.inc"
	.endif

	.ifdef	cs
	.include "lang-cs.inc"
	.endif

	.ifdef	sk
	.include "lang-sk.inc"
	.endif
	
; ==============================================================================
; Constants
;
	
; ==============================================================================
; Main entry point of the program
;
	.text
	.globl	main
main:
	di
	ld	sp,0x7000
	call	add_glyphs
	call	erase
	
	ld	a,1
	ld	b,0
	ld	c,0
	call	draw_digit
	
	ld	a,2
	ld	b,0
	ld	c,1
	call	draw_digit
	
	ld	a,3
	ld	b,1
	ld	c,0
	call	draw_digit
	
	ld	a,8
	ld	b,1
	ld	c,2
	call	draw_digit
	
	ld	a,2
	ld	b,0
	ld	c,3
	call	draw_digit
	
	ld	a,5
	ld	b,0
	ld	c,4
	call	draw_digit
	
	ld	b,2
1:	ld	c,0
2:	push	bc
	xor	a
	call	draw_digit
	pop	bc
	inc	c
	ld	a,c
	cp	5
	jp	nz,2b
	inc	b
	ld	a,b
	cp	12
	jp	nz,1b

	ld	a,1
	ld	b,0
	ld	c,0
	call	draw_pin

	ld	a,2
	ld	b,0
	ld	c,1
	call	draw_pin

	ld	a,1
	ld	b,1
	ld	c,0
	call	draw_pin

	ld	a,1
	ld	b,1
	ld	c,1
	call	draw_pin

	ld	a,1
	ld	b,1
	ld	c,2
	call	draw_pin

	ld	a,1
	ld	b,1
	ld	c,3
	call	draw_pin

	ld	a,1
	ld	b,1
	ld	c,4
	call	draw_pin

	jp	0

	call	init_kbd
	call	set_kmap
	call	add_glyphs
	call	add_cust_glyphs
	call	start_ct1
	jp	nc,1f
	call	erase
	ld	hl,msg_hwerr
	call	get_ack
quit:	call	rel_ct1
	call	erase
	jp	PMD_MONIT
1:

	call	erase

	.end
