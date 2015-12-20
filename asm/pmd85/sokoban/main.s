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
	.equiv	MEMLIMIT, 0x400
	
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

; main menu
menu:	call	erase
	ld	hl,label_sokoban
	ld	de,LBLPOS
	call	draw_label
	ld	hl,credits
	ld	de,CRPOS
	call	writeln
	ld	hl,legend
	ld	de,LEGPOS
	call	writeln

; select menu option
msel:	ld	hl,msg_menu
	call	disp_msg
	call	inklav

; play first level
	cp	KEY_PLAY
	jp	nz,1f
	call	tnlvl
	ld	de,0
	jp	2f

; play selected level
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
play:	ld	hl,0
	ld	(moves),hl
	ld	(pushes),hl
	call	reset_hist
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
	jp	gsel

; load levels from cassette
1:	cp	KEY_LOAD
	jp	nz,1f
	ld	hl,msg_fileno
	call	disp_msg
	ld	hl,val_digit
	ld	(sel_val),hl
	ld	bc,0x0202
	ld	hl,sbuf
	push	hl
	call	sedit
	pop	hl
	call	parse_int
	ld	a,l
	ld	(fileno),a
	ld	hl,msg_loading
	call	disp_msg
	call	start_usart
2:	call	headin
	jp	nc,4f
3:	call	stop_usart
	jp	msel
4:	call	clr_msg
	ld	a,(trhead)
	add	a,100
	ld	h,0
	ld	l,a
	ld	de,sbuf
	push	de
	call	conv_int
	ex	de,hl
	ld	(hl),'/'
	inc	hl
	ld	a,(trhead + 1)
	ld	(hl),a
	inc	hl
	ld	(hl),' '
	inc	hl
	ex	de,hl
	ld	hl,trhead + 6
	ld	b,8
	call	copy8
	xor	a
	ld	(de),a
	pop	hl
	inc	hl
	call	disp_msg
	ld	hl,trhead
	ld	a,(fileno)
	cp	(hl)
	jp	nz,2b
	inc	hl
	ld	a,(hl)
	cp	'S'
	jp	nz,2b
	ld	hl,(trhead + 4)
	ld	(filesize),hl
	ld	de,levels
	add	hl,de
	ld	de,MEMLIMIT
	add	hl,de
	ld	de,initsp
	call	ucmphlde
	jp	nc,2f
	call	errbeep
	ld	hl,msg_ftb
	call	get_any
	jp	3b
2:	ld	hl,(filesize)
	ex	de,hl
	ld	hl,levels
	push	hl
	push	de
	call	trload
	push	af
	call	stop_usart
	pop	af
	pop	de
	pop	hl
	jp	z,2f
	ld	hl,0
	ld	(nlevels),hl
	call	errbeep
	jp	msel
2:	add	hl,de
	inc	hl
	ld	(hl),0		; end-mark
	inc	hl
	ld	(hl),0
	call	count_levels
	push	de
	ex	de,hl
	ld	(nlevels),hl
	call	init_hist
	ld	hl,msg_sload
	call	disp_msg
	pop	hl
	ld	de,sbuf
	push	de
	call	conv_int
	xor	a
	ld	(de),a
	pop	de
	call	writelncur
	call	inklav
	jp	msel

; quit
1:	cp	KEY_QUIT
	jp	nz,msel
	ld	hl,msg_quit
	call	get_conf
	jp	z,msel	
quit:	call	erase
	jp	PMD_MONIT


; select game option
gsel:	call	inklav
	cp	KLEFT
	ld	a,(pcol)
	or	a
	jp	z,gsel
	ld	hl,(ppos)
	dec	hl
	ld	a,(hl)
	and	BOX
	jp	nz,2f
	ld	a,(hl)
	and	WALL
	jp	nz,gsel
	ld	(ppos),hl
	push	hl
	ld	hl,pcol
	dec	(hl)
	ld	c,(hl)
	ld	a,(coff)
	add	a,c
	ld	c,a
	ld	a,(prow)
	ld	b,a
	ld	a,(roff)
	add	a,b
	ld	b,a
	ld	a,SQ_PUSHER
	push	bc
	call	draw_square
	pop	bc
	pop	hl
	inc	hl
	ld	a,GOAL
	and	(hl)
	ld	a,SQ_BLANK
	jp	z,3f
	ld	a,SQ_GOAL
3:	inc	c
	call	draw_square
2:	jp	gsel		; DEBUG
1:	cp	KEY_RESTART
	jp	nz,1f
	ld	hl,msg_restart
	call	get_conf
	jp	z,gsel
	ld	hl,(level)
	ld	b,h
	ld	c,l
	jp	play
1:	cp	KEY_MENU
	jp	nz,gsel
	ld	hl,msg_end
	call	get_conf
	jp	z,gsel
	jp	menu
	
; check if any levels available
tnlvl:	ld	hl,(nlevels)
	ld	a,h
	or	l
	ret	nz
	ld	hl,msg_nolevels
	call	get_ack
	pop	hl
	jp	msel

; display failure message and quit
fail:	ld	hl,msg_fail
	call	get_ack
	jp	quit

; display 5-digit integer, left-aligned, right-padded with blanks
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
	
; display number of moves and pushes
dispmp:	ld	hl,(moves)
	ld	de,MVPOS
	call	wip
	ld	hl,(pushes)
	ld	de,PUPOS
	jp	wip
	
; variables
	.lcomm	nlevels, 2
	.lcomm	sbuf, 15
	.lcomm	level, 2
	.lcomm	moves, 2
	.lcomm	pushes, 2
	.lcomm	fileno, 1
	.lcomm	filesize, 2
	
	.end