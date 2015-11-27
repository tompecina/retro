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


; Sudoku puzzle for Tesla PMD 85.

	.include "sudoku.inc"

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
	.equiv	POS_SUDOKU, 0xc394
	
; ==============================================================================
; Main entry point of the program
;
	.text
	.globl	main
main:

; initialize
	di
	ld	sp,0x7000
	call	init_kbd
	call	set_kmap
	call	add_glyphs
	call	add_cust_glyphs
	call	erase
	call	start_ct1
	jp	nc,1f
	ld	hl,msg_hwerr
	call	get_ack
	call	rel_ct1
	call	erase
	jp	PMD_MONIT
1:	call	start_ct2
	call	init_maps
	call	init_gmap
	call	draw_board
	ld	hl,lbl_sudoku
	ld	de,POS_SUDOKU
	call	writeln
	ld	hl,msg_select
	call	disp_msg
2:	call	inklav_rnd
	cp	'0'
	jp	c,1f
	cp	'3' + 1
	jp	nc,1f
	jp	2f
1:	call	errbeep
	jp	2b
2:	sub	'0'
	ld	b,a
	ld	hl,puzzle
	push	hl
	push	bc
	call	lcg
	pop	bc
	call	get_rnd_puzzle
	pop	hl
	push	hl
	call	randomize_puzzle
	pop	hl
	ld	de,marks
	push	de
	push	hl
	call	get_dups
	pop	hl
	call	disp_puzzle
	pop	de
	call	disp_marks

	jp	0
	
	


	.lcomm	puzzle, 81
	.lcomm	marks, 81

	.end
