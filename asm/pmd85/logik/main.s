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
	call	init_btbl
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
1:	call	erase

	ld	hl,label_logik
	ld	de,LRPOS
	call	draw_label

	ld	hl,credits
	ld	de,CRPOS
	call	writeln

	call	draw_board

	ld	a,0x19
	ld	b,2
	call	draw_pins
	
	jp	0

	.end
