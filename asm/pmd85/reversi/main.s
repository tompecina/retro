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


; The game of Reversi for Tesla PMD 85.

	.include "reversi.inc"

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
	.equiv	MAXLEVEL, 5	; maximum level
	
; ==============================================================================
; Main entry point of the program
;
	.text
	.globl	main
main:
	di
	ld	sp,0x7000
	call	init_btbl
	call	init_rvt
	call	init_kbd
	call	set_kmap
	call	add_glyphs
	call	add_cust_glyphs
	call	erase
	call	draw_labels
	call	draw_board

	ld	hl,label_reversi
	ld	de,LRPOS
	call	draw_label

	ld	hl,credits
	ld	de,CRPOS
	call	writeln

	ld	hl,legend
	ld	de,LEGPOS
	call	writeln

	ld	a,2
	ld	(level),a
	call	disp_level

	ld	a,0xff
	ld	(sound),a
	call	disp_sound

new:	call	init_game
	call	disp_score

	ld	hl,msg_color
	call	get_conf	
	ld	(compcol),a
	call	disp_compcol
	
mainloop:
	call	fhd
	call	count_moves
	ld	a,b
	or	c
	jp	nz,1f
	ld	hl,black
	ld	de,white
	call	count_discs
	ld	a,b
	cp	c
	jp	nz,2f
	ld	hl,msg_draw
	jp	3f
2:	ld	hl,msg_bwin
	jp	nc,2f
	ld	hl,msg_wwin
2:	ld	de,dial
2:	ld	a,(hl)
	or	a
	jp	z,5f
	inc	hl
	ld	(de),a
	inc	de
	jp	2b
5:	ex	de,hl
	ld	a,64
	sub	b
	sub	c
	ld	d,a
	ld	a,b
	cp	c
	jp	c,2f
	ld	a,b
	add	a,d
	ld	b,a
	jp	5f
2:	ld	a,c
	add	a,d
	ld	c,a
5:	push	bc
	ld	a,b
	call	dsc
	ld	(hl),'-'
	inc	hl
	pop	bc
	ld	a,c
	call	dsc
	ld	(hl),0
	ld	hl,dial
3:	call	disp_msg
2:	call	inklav
	call	newnc
	call	undo
	call	switch
	call	tsound
	call	clevel
	call	quit
	jp	2b
1:	ld	a,(tomove)
	ld	b,a
	ld	a,(compcol)
	cp	b
	jp	z,1f
2:	call	fhd
	call	count_discs
	ld	a,b
	or	a
	jp	nz,7f
	ld	hl,msg_ppass
	call	get_ack
6:	ld	a,(tomove)
	cpl
	ld	(tomove),a
	jp	mainloop
7:	call	player_select
	or	a
	jp	nz,2f
	ld	hl,black
	ld	de,white
	ld	a,(tomove)
	or	a
	jp	z,3f
	ex	de,hl
3:	push	bc
	call	one_legal
	pop	bc
	jp	nc,8f
	ld	hl,msg_badmove
	call	disp_msg
	call	errbeep
	jp	7b
8:	ld	hl,moven
	ld	e,(hl)
	ld	d,0
	inc	(hl)
	ld	hl,moves
	add	hl,de
	ld	a,(tomove)
	ld	b,a
	and	0x80
	or	c
	ld	(hl),a
	ld	hl,black
	ld	de,white
	push	bc
	call	anim_move
	pop	bc
	ld	hl,black
	ld	de,white
	call	make_move
	jp	6b
2:	call	newc
	call	undo
	call	switch
	call	tsound
	call	clevel
	call	quit
	jp	mainloop
1:	call	fhd
	call	count_moves
	ld	a,c
	or	a
	jp	nz,1f
	ld	hl,msg_cpass
	call	disp_msg
	jp	7b
1:	ld	a,(moven)
	cp	BOOK_DEPTH
	jp	nc,1f
	call	fhdn
	call	lookup_book
	jp	nc,8b
1:	call	fdh
	push	de
	ld	de,heap
	ld	b,8
	call	copy8
	pop	hl
	ld	b,8
	call	copy8
	ex	de,hl
	ld	a,(level)
	ld	(hl),a
	inc	hl
	ld	(hl),MINWORD & 0xff
	inc	hl
	ld	(hl),MINWORD >> 8
	inc	hl
	ld	(hl),MAXWORD & 0xff
	inc	hl
	ld	(hl),MAXWORD >> 8
	inc	hl
	ld	(hl),0xff
	ld	hl,msg_think
	call	disp_msg
	call	redon
	ld	bc,heap
	call	minimax
	call	redoff
	call	clr_msg
	call	stdbeep
	jp	nz,2b
	jp	8b
	
dsc:	call	prep_score
	ld	a,b
	cp	'0'
	jp	z,1f
	ld	(hl),b
	inc	hl
1:	ld	(hl),c
	inc	hl
	ret

fhdn:	ld	hl,black
	ld	de,white
	ret
	
fhd:	call	fhdn
	ld	a,(tomove)
	or	a
	ret	z
	ex	de,hl
	ret

fdh:	call	fhd
	ex	de,hl
	ret
	
newnc:	cp	KEY_NEW
	ret	nz
2:	pop	hl
	jp	new

newc:	cp	KEY_NEW
	ret	nz
	push	af
	ld	hl,msg_new
	call	get_conf
	jp	nz,1f
	pop	af
	ret
1:	pop	af
	jp	2b
	
undo:	cp	KEY_UNDO
	ret	nz
	pop	hl
	ld	a,(moven)
	ld	e,a
	ld	d,0
	ld	hl,moves - 1
	add	hl,de
	ld	a,(compcol)
	ld	d,a
	ld	a,(tomove)
	xor	d
	ld	d,a
1:	ld	a,e
	or	a
	jp	z,1f
	dec	e
	ld	a,(hl)
	dec	hl
	xor	d
	and	0x80
	jp	z,1b
1:	ld	hl,moven
	ld	a,e
	cp	(hl)
	jp	z,mainloop	; no moves to be undone
	ld	(hl),e
	call	init_pos
	ld	hl,moves
1:	dec	e
	jp	m,1f
	ld	a,(hl)
	inc	hl
	push	hl
	push	de
	ld	hl,black
	ld	de,white
	rla
	ld	c,a
	ld	a,0
	rla
	ld	b,a
	ld	a,c
	rra
	ld	b,a
	call	make_move
	pop	de
	pop	hl
	jp	1b
1:	ld	hl,black
	ld	de,white
	call	draw_pos
	jp	mainloop	
	
toggle:	ld	a,(hl)
	cpl
	ld	(hl),a
	ret
	
switch:	cp	KEY_SWITCH
	ret	nz
	ld	hl,compcol
	call	toggle
	call	disp_compcol
	xor	a
	ret
	
tsound:	cp	KEY_SOUND
	ret	nz
	ld	hl,sound
	call	toggle
	call	disp_sound
	xor	a
	ret

clevel:	cp	KK1
	ret	c
	cp	KK1 + MAXLEVEL
	ret	nc
	sub	KK0
	ld	(level),a
	call	disp_level
	xor	a
	ret
	
quit:	cp	KEY_QUIT
	ret	nz
	ld	hl,msg_quit
	call	get_conf
	ret	z
	call	erase
	jp	PMD_MONIT
	
	.globl	black, white, tomove, moven
	.lcomm	black, 8
	.lcomm	white, 8
	.lcomm	level, 1
	.lcomm	sound, 1
	.lcomm	compcol, 1	; 0 - computer plays black
				; 0xff - computer plays white
	.lcomm	tomove, 1	; 0 - black, 0xff - white
	.lcomm	moven, 1
	.lcomm	moves, 64	; white moves have bit 7 set
	.lcomm	dial, 49

; ==============================================================================
; Display level
;
	.text
disp_level:
	ld	a,(level)
	add	a,'0'
	ld	hl,LVPOS
	jp	write
	
; ==============================================================================
; Display on/off string
; 
;   input:  A =0 - off
;	      >0 - on
;	    (HL) - destination
;
;   uses:   A, D, E, H, L
; 
	.text
disp_onoff:
	ex	de,hl
	or	a
	jp	nz,1f
	ld	hl,off
	jp	writeln
1:	ld	hl,on
	jp	writeln
	
; ==============================================================================
; Display sound setting
;
	.text
disp_sound:
	ld	a,(sound)
	ld	hl,SNPOS
	jp	disp_onoff
	
; ==============================================================================
; Prepare score
; 
;   input:  A - score
; 
;   output: BC - representation of A as decimal digits
;
;   uses:   A, D
;
	.text
prep_score:
	ld	e,a
	ld	c,10
	call	udiv8
	ld	a,c
	add	a,'0'
	ld	c,a
	ld	a,e
	add	a,'0'
	ld	b,a
	ret
	
; ==============================================================================
; Display current score
;
	.text
disp_score:	
	ld	hl,black
	ld	de,white
	call	count_discs
	push	bc
	ld	a,b
	call	prep_score
	ld	a,b
	cp	'0'
	jp	nz,1f
	ld	a,' '
1:	ld	hl,BSPOS
	call	write
	ld	a,c
	call	prtout
	pop	bc
	ld	a,c
	call	prep_score
	ld	a,b
	cp	'0'
	jp	z,1f
	ld	a,b
	ld	hl,WSPOS
	call	write
	ld	a,c
	jp	prtout
1:	ld	a,c
	ld	hl,WSPOS
	call	write
	ld	a,' '
	jp	prtout
	
; ==============================================================================
; Display computer color icon
;
	.text
disp_compcol:
	ld	h,COMP_ICON
	ld	d,' '
	ld	a,(compcol)
	or	a
	jp	z,1f
	ex	de,hl
1:	ld	a,h
	ld	hl,BCPOS
	call	write
	ld	a,d
	ld	hl,WCPOS
	jp	write
	
; ==============================================================================
; Standard (alert) beep
;
	.text
stdbeep:
	ld	hl,stbdt
	jp	pbp

	.data
stbdt:	.byte	2, 16, 0xff
	
; ==============================================================================
; Error beep
;
	.text
errbeep:
	ld	hl,erbdt
pbp:	ld	a,(sound)
	or	a
	ret	z
	jp	bell

	.data
erbdt:	.byte	2, 8, 0, 8, 2, 8, 0xff
	
	.end
