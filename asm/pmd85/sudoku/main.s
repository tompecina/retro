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
; Constants
;
	.equiv	POS_SUDOKU, 0xc394
	
; ==============================================================================
; Main entry point of the program
;
	.text
	.globl	main
main:
	di
	ld	sp,initsp
	call	init_kbd
	call	set_kmap
	call	init_video
	call	add_cust_glyphs
	call	start_ct1
	jp	nc,1f
	call	erase
	ld	hl,msg_hwerr
	call	get_ack
quit:	call	rel_ct1
	call	erase
	jp	PMD_MONIT
1:	ld	hl,inklav_rnd
	ld	(sel_inklav),hl
	call	start_ct2
	call	init_maps
	call	init_gmap
new:	call	erase
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
	push	bc
	call	clr_msg
	call	lcg
	pop	bc
	ld	hl,puzzle
	push	hl
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
	call	init_cursor
	ld	hl,msg_start
	call	disp_msg
	call	read_ct2
	ld	(timer),hl
loop:	call	player_select
	cp	'1'
	jp	c,1f
	cp	'9' + 1
	jp	nc,1f
	sub	'0'
3:	ld	b,0
	ld	hl,puzzle
	add	hl,bc
	ld	b,a
	ld	a,(hl)
	or	a
	jp	z,2f
	rla
	jp	c,2f
	ld	hl,msg_sqerr
	call	disp_msg
	call	errbeep
	jp	loop
2:	ld	a,b
	xor	0x80
	ld	(hl),a
	ld	e,0x40
	call	draw_digit
	ld	hl,puzzle
	ld	de,newmarks
	push	hl
	call	get_dups
	pop	hl
	push	af
	ld	hl,marks
	ld	de,newmarks
	call	upd_marks
	pop	af
	jp	nz,loop
	ld	hl,puzzle
	call	check_puzzle
	jp	z,loop
	call	hide_cursor
	call	read_ct2
	ex	de,hl
	ld	hl,(timer)
	ld	a,l
	sub	e
	ld	l,a
	ld	a,h
	sbc	a,d
	ld	h,a
	ld	de,ph_tmr
	call	conv_time
	ld	hl,msg_done
	call	disp_msg
	call	inklav_rnd
	jp	new
1:	cp	KDEL
	jp	nz,1f
	ld	a,0x80
	jp	3b
1:	cp	KEY_NEW
	jp	nz,1f
	ld	hl,msg_new
	call	get_conf
	jp	nz,new
	jp	loop
1:	cp	KEY_QUIT
	jp	nz,loop
	ld	hl,msg_quit
	call	get_conf
	jp	nz,quit
	jp	loop

	.lcomm	puzzle, 81
	.lcomm	marks, 81
	.lcomm	newmarks, 81
	.lcomm	timer, 2
	
	.end
