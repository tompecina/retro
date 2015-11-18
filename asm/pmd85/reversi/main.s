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
	;; call	get_conf	
	ld	a,0xff		; DEBUG
	ld	(compcol),a
	call	disp_compcol
	
mainloop:
	ld	hl,black
	ld	de,white
	ld	a,(tomove)
	jp	nz,1f
	ex	de,hl
1:	call	count_moves
	ld	a,b
	or	c
	;; jp	nz,1f
	ld	hl,black
	ld	de,white
	call	count_discs
	ld	bc,0x0405	; DEBUG
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
1:	
	
	jp	.
	
dsc:	call	prep_score
	ld	a,b
	cp	'0'
	jp	z,1f
	ld	(hl),b
	inc	hl
1:	ld	(hl),c
	inc	hl
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
	
	.end
