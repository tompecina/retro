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


; The game of Sokoban for Tesla PMD 85.

	.include "sokoban.inc"

; ==============================================================================
; Constants
;

; ==============================================================================
; Main entry point of the program
;
	.text
	.globl	main
main:

; initialize
	di
	ld	sp,initsp
	call	init_levels	; must be called before using .bss variables
	call	init_kbd
	call	set_kmap
	call	init_video
	call	add_cust_glyphs
	call	count_levels
	ex	de,hl
	ld	(nlevels),hl
	call	init_hist	

	call	erase

	ld	hl,label_sokoban
	ld	de,LBLPOS
	call	draw_label

	ld	hl,credits
	ld	de,CRPOS
	call	writeln

	ld	hl,legend
	ld	de,LEGPOS
	call	writeln

	ld	hl,msg_menu
	call	disp_msg

msel:	call	inklav
	cp	KEY_PLAY
	jp	nz,1f
	call	tnlvl
	ld	de,0
	jp	2f
1:	cp	KEY_SELECT
	jp	nz,1f
	call	tnlvl
4:	ld	hl,msg_select1
	call	disp_msg
	ld	hl,(nlevels)
	ld	de,sbuf
	push	de
	call	conv_int
	ex	de,hl
	ld	(hl),0
	pop	de
	call	writelncur
	ld	de,msg_select2
	call	writelncur
	ld	hl,val_digit
	ld	(sel_val),hl
	ld	bc,0x0105
	ld	hl,sbuf
	push	hl
	call	sedit
	pop	hl
	call	parse_int
	ld	a,h
	or	l
	jp	nz,3f
5:	call	errbeep
	jp	4b
3:	dec	hl
	ex	de,hl
	ld	hl,(nlevels)
	call	ucmphlde
	jp	nc,5b
2:	ld	b,d
	ld	c,e
	ex	de,hl
	ld	(level),hl
	ld	hl,0
	ld	(moves),hl
	ld	(pushes),hl
	call	get_level
	jp	c,fail
	call	draw_board
	ld	hl,msg_stat
	ld	de,STPOS
	call	writeln
	ld	hl,(level)
	inc	hl
	ld	de,LVPOS
	call	wip
	call	dispmp
1:	
	cp	KEY_QUIT
	jp	nz,msel
	ld	hl,msg_quit
	call	get_conf
	jp	z,msel	
quit:	call	erase
	jp	PMD_MONIT
	
	
1:	ld	bc,0
2:	push	bc
	call	get_level
	call	nc,draw_board
	pop	bc
	inc	bc
	ld	a,c
	cp	60
	jp	nz,2b	
	jp	1b

tnlvl:	ld	hl,(nlevels)
	ld	a,h
	or	l
	ret	nz
	ld	hl,msg_nolevels
	call	get_ack
	pop	hl
	jp	msel

fail:	ld	hl,msg_fail
	call	get_ack
	jp	quit

wip:	push	de
	ex	de,hl
	ld	h,' '
	ld	l,h
	ld	(sbuf),hl
	ld	(sbuf + 2),hl
	ld	h,0
	ld	(sbuf + 4),hl
	ex	de,hl
	ld	de,sbuf
	push	de
	call	conv_int
	pop	hl
	pop	de
	jp	writeln
	
dispmp:	ld	hl,(moves)
	ld	de,MVPOS
	call	wip
	ld	hl,(pushes)
	ld	de,PUPOS
	jp	wip
	
	.lcomm	nlevels, 2
	.lcomm	sbuf, 6
	.lcomm	level, 2
	.lcomm	moves, 2
	.lcomm	pushes, 2
	
	.end
