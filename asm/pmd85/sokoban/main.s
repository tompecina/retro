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
	.equiv	WAITCONST, 3
	
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
	ld	hl,mvect
	jp	jpvect

; play first level
kplay:	call	tnlvl
	ld	de,0
	jp	2f

; play selected level
kselect:
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
kload:	jp	nz,1f
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
2:	call	trheadin
	jp	nc,4f
3:	call	stop_usart
	jp	msel
4:	call	clr_msg
	ld	a,(numfil)
	add	a,100
	ld	h,0
	ld	l,a
	ld	de,sbuf
	push	de
	call	conv_int
	ex	de,hl
	ld	(hl),'/'
	inc	hl
	ld	a,(typfil)
	ld	(hl),a
	inc	hl
	ld	(hl),' '
	inc	hl
	ex	de,hl
	ld	hl,lenfil
	ld	b,8
	call	copy8
	xor	a
	ld	(de),a
	pop	hl
	inc	hl
	call	disp_msg
	ld	hl,numfil
	ld	a,(fileno)
	cp	(hl)
	jp	nz,2b
	inc	hl
	ld	a,(hl)
	cp	'S'
	jp	nz,2b
	ld	hl,(lenfil)
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
kquit:	ld	hl,msg_quit
	call	get_conf
	jp	z,msel	
quit:	call	erase
	jp	PMD_MONIT

; select in-game option
gsel:	call	inklav
	ld	hl,gvect
	jp	jpvect
kleft:
	call	left
	jp	arrow
kright:
	call	right
	jp	arrow
kup:	
	call	up
	jp	arrow
kdown:	
	call	down
	jp	arrow
ksleft:	
	call	left
	jp	sarrow
ksright:	
	call	right
	jp	sarrow
ksup:	
	call	up
	jp	sarrow
ksdown:	
	call	down
	jp	sarrow
kundo:	
	call	pop_move
	jp	c,gsel
	ld	h,a
	and	0x03
	jp	nz,2f
	call	left
	jp	undo
2:	dec	a
	jp	nz,2f
	call	up
	jp	undo
2:	dec	a
	jp	nz,2f
	call	down
	jp	undo
2:	call	right
	jp	undo
krestart:	
	ld	hl,msg_restart
	call	get_conf
	jp	z,gsel
play2:	ld	hl,(level)
	ld	b,h
	ld	c,l
	jp	play
kmenu:	
	ld	hl,msg_end
	call	get_conf
	jp	z,gsel
	jp	menu
	
; unshifted arrow key processing
arrow:	call	setc
	call	movec
	call	checkc
	jp	z,gsel
	call	gbc
	and	WALL
	jp	nz,gsel
	ld	a,(hl)
	and	BOX
	jp	nz,1f
	call	dpu
	call	setp
	call	rmovec
	call	gbc
	and	GOAL
	jp	nz,2f
	call	dbl
	jp	3f
2:	call	dgo
3:	call	rc2c
	call	push_move
	call	incm
	call	dispm
	jp	gsel
1:	call	movec
	call	checkc
	jp	z,gsel
	call	gbc
	and	WALL | BOX
	jp	nz,gsel
	ld	a,(hl)
	or	BOX
	ld	(hl),a
	and	GOAL
	jp	nz,2f
	call	dbo
	jp	3f
2:	call	dbg
3:	call	rmovec
	call	gbc
	and	~BOX
	ld	(hl),a
	call	dpu
	call	setp
	call	rmovec
	call	gbc
	and	GOAL
	jp	nz,2f
	call	dbl
	jp	3f
2:	call	dgo
3:	call	rc2c
	or	0x04
	call	push_move
	call	incmp
	call	dispmp
	call	check_board
	jp	nz,gsel
	ld	hl,(level)
	inc	hl
	ld	(level),hl
	ex	de,hl
	ld	hl,(nlevels)
	call	ucmphlde
	jp	z,1f
	ld	hl,msg_next
	call	get_conf
	jp	z,menu
	jp	play2
1:	ld	hl,msg_nomore
	call	get_ack
	jp	menu
		
; shifted arrow key processing
sarrow:	call	setc
	call	movec
	call	checkc
	jp	z,gsel
	call	gbc
	and	WALL | BOX
	jp	nz,gsel
	push	bc
	push	de
	call	dpu
	call	setp
	call	rmovec
	call	gbc
	and	GOAL
	jp	nz,2f
	call	dbl
	jp	3f
2:	call	dgo
3:	call	rc2c
	call	push_move
	call	incm
	call	dispm
	ld	d,WAITCONST
	xor	a
	call	waits
	pop	de
	pop	bc
	jp	sarrow
	
; undo processing
undo:	ld	a,h
	and	0x04
	jp	nz,1f
	call	setc
	call	movec
	call	dpu
	call	setp
	call	rmovec
	call	gbc
	and	GOAL
	jp	nz,2f
	call	dbl
	jp	3f
2:	call	dgo
3:	call	decm
	call	dispm
	jp	gsel
1:	call	setc
	call	rmovec
	call	gbc
	and	~BOX
	ld	(hl),a
	and	GOAL
	jp	nz,2f
	call	dbl
	jp	3f
2:	call	dgo
3:	call	movec
	call	gbc
	or	BOX
	ld	(hl),a
	and	GOAL
	jp	nz,2f
	call	dbo
	jp	3f
2:	call	dbg
3:	call	movec
	call	dpu
	call	setp
	call	decmp
	call	dispmp
	jp	gsel

; increment number of moves & pushes
incmp:	ld	hl,(pushes)
	inc	hl
	ld	(pushes),hl
	; fall through
	
; increment number of moves
incm:	ld	hl,(moves)
	inc	hl
	ld	(moves),hl
	ret
	
; decrement number of moves & pushes
decmp:	ld	hl,(pushes)
	dec	hl
	ld	(pushes),hl
	; fall through
	
; decrement number of moves
decm:	ld	hl,(moves)
	dec	hl
	ld	(moves),hl
	ret
	
; get byte at cursor
gbc:	ld	hl,(cpos)
	ld	a,(hl)
	ret
	
; convert row & column to history code
rc2c:	ld	a,b
	xor	c
	and	0x02
	ld	h,a
	rra
	xor	b
	and	0x01
	or	h
	ret
	
; set cursor to pusher
setc:	ld	a,(prow)
	ld	(crow),a
	ld	a,(pcol)
	ld	(ccol),a
	ld	hl,(ppos)
	ld	(cpos),hl
	ret
	
; set pusher to cursor
setp:	ld	a,(crow)
	ld	(prow),a
	ld	a,(ccol)
	ld	(pcol),a
	ld	hl,(cpos)
	ld	(ppos),hl
	ret
	
; move cursor according to offsets
movec:	ld	a,(crow)
	add	a,b
	ld	(crow),a
	ld	a,(ccol)
	add	a,c
	ld	(ccol),a
	ld	hl,(cpos)
	add	hl,de
	ld	(cpos),hl
	ret
	
; move cursor according to offsets, in reverse
rmovec:	ld	a,(crow)
	sub	b
	ld	(crow),a
	ld	a,(ccol)
	sub	c
	ld	(ccol),a
	ld	hl,(cpos)
	ld	a,l
	sub	e
	ld	l,a
	ld	a,h
	sbc	a,d
	ld	h,a
	ld	(cpos),hl
	ret
	
; check if cursor is inside board 
checkc:	ld	a,(rows)
	ld	h,a
	ld	a,(crow)
	cp	0xff
	ret	z
	cp	h
	ret	z
	ld	a,(cols)
	ld	h,a
	ld	a,(ccol)
	cp	0xff
	ret	z
	cp	h
	ret
	
; draw blank at cursor position
dbl:	xor	a
	jp	draw
		
; draw pusher at cursor position
dpu:	ld	a,SQ_PUSHER
	jp	draw
	
; draw box at cursor position
dbo:	ld	a,SQ_BOX
	jp	draw
	
; draw box on goal at cursor position
dbg:	ld	a,SQ_BOXONGOAL
	jp	draw
	
; draw goal at cursor position
dgo:	ld	a,SQ_GOAL
	jp	draw
	
; draw square at cursor position
draw:	push	bc
	push	de
	push	af
	ld	a,(roff)
	ld	b,a
	ld	a,(crow)
	add	a,b
	ld	b,a
	ld	a,(coff)
	ld	c,a
	ld	a,(ccol)
	add	a,c
	ld	c,a
	pop	af
	call	draw_square
	pop	de
	pop	bc
	ret
	
; left presets
left:	ld	bc,0x00ff
	ld	d,c
	ld	e,c
	ret
	
; right presets
right:	ld	bc,0x0001
	ld	d,b
	ld	e,c
	ret
	
; up presets
up:	ld	bc,0xff00
	ld	a,(cols)
	cpl
	inc	a
	ld	e,a
	ld	d,b
	ret
	
; down presets
down:	ld	bc,0x0100
	ld	a,(cols)
	ld	e,a
	ld	d,c
	ret
	
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
	
; display number of moves & pushes
dispmp:	ld	hl,(pushes)
	ld	de,PUPOS
	call	wip
	; fall through
	
; display number of moves
dispm:	ld	hl,(moves)
	ld	de,MVPOS
	jp	wip
	
; manu vector table
	.data
mvect:
	.byte	KEY_PLAY
	.word	kplay
	.byte	KEY_SELECT
	.word	kselect
	.byte	KEY_LOAD
	.word	kload
	.byte	KEY_QUIT
	.word	kquit
	.byte	0
	.word	msel
	
; in-game vector table
	.data
gvect:
	.byte	KLEFT
	.word	kleft
	.byte	KRIGHT
	.word	kright
	.byte	KHOME
	.word	kup
	.byte	KLLEFT
	.word	kup
	.byte	KEND
	.word	kdown
	.byte	KRRIGHT
	.word	kdown
	.byte	KSLEFT
	.word	ksleft
	.byte	KSRIGHT
	.word	ksright
	.byte	KSHOME
	.word	ksup
	.byte	KSLLEFT
	.word	ksup
	.byte	KSEND
	.word	ksdown
	.byte	KSRRIGHT
	.word	ksdown
	.byte	KEY_UNDO
	.word	kundo
	.byte	KEY_RESTART
	.word	krestart
	.byte	KEY_MENU
	.word	kmenu
	.byte	0
	.word	gsel
	
; ==============================================================================
; Variables
; 
	.lcomm	sbuf, 15
	.lcomm	nlevels, 2
	.lcomm	level, 2
	.lcomm	moves, 2
	.lcomm	pushes, 2
	.lcomm	fileno, 1
	.lcomm	filesize, 2
	.lcomm	crow, 1
	.lcomm	ccol, 1
	.lcomm	cpos, 2
	
	.end
