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
; Language file inclusion
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
	.equiv	MINROUNDS, 1
	.equiv	MAXROUNDS, 8

; ==============================================================================
; Main entry point of the program
;
	.text
	.globl	main
main:

; initialize
	di
	ld	sp,0x7000


	call	init_map

	jp	0






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

	ld	hl,lbl_desc
	ld	de,DSPOS
	call	writeln

; get number of rounds
	ld	hl,msg_nrnds
	call	disp_msg
	ld	hl,inklav_rnd
	ld	(ikf),hl
	ld	hl,valr
	ld	(vf),hl
	ld	bc,0x0101
	ld	hl,kbdbuffer
	call	sedit
	ld	a,(kbdbuffer)
	push	af
	ld	hl,RNDSPOS
	call	write
	pop	af
	sub	'0'
	ld	(rounds),a
	ld	a,1
	ld	(round),a

; display initial score
	xor	a
	ld	(pscore),a
	ld	(cscore),a
	call	disp_score

; display round number
	ld	a,(round)
	add	a,'0'
	ld	hl,RNDPOS
	call	write

; select code
	call	select_code
	ld	ccode,hl

; initialize attemps
	xor	a
	ld	(attempt),a
	
; display starting prompt
	ld	hl,msg_startg
	call	get_ack
	
; let player guess
1:	ld	hl,attempt
	ld	b,(hl)
	push	bc
	call	player_select

; evaluate guess
	ex	de,hl
	ld	hl,(ccode)
	call	match
	ld	a,c
	pop	bc
	push	af
	call	draw_pegs
	pop	af
	cp	40
	ld	hl,attempt
	jp	z,1f
	inc	(hl)
	ld	a,(hl)
	cp	ATTEMPTS
	jp	nz,1b
	ld	hl,msg_out
	call	get_ack
	ld	hl,cscore
	ld	a,(hl)
	add	a,ATTEMPTS + 1
	ld	(hl),a
	jp	2f
1:	ld	e,(hl)
	ld	hl,msg_corr
	call	disp_msg
	ld	hl,corr
	ld	d,0
	add	hl,de
	add	hl,de
	ld	e,(hl)
	inc	hl
	ld	d,(hl)
	ex	de,hl
	call	writeln
	ld	hl,attempt
	ld	a,(cscore)
	add	a,(hl)
	inc	a
	ld	(cscore),a
	
; update score
2:	call	disp_score
	
	
	jp	0
	
	
valr:	cp	'0' + MINROUNDS
	ret	c
	cp	'0' + MAXROUNDS + 1
	ccf
	ret

; score strings
	.data
corr:	
	.irpc	n, "123456789"
	.word	msg_corr\n
	.word	msg_corr10
	.endr
	
	.lcomm	rounds, 1
	.lcomm	round, 1
	.lcomm	pscore, 1
	.lcomm	cscore, 1
	.lcomm	ccode, 2
	.lcomm	attempt, 1
	
; ==============================================================================
; Display score
;
	.text
disp_score:
	ld	hl,PSCPOS
	ld	a,(pscore)
	call	1f
	ld	hl,CSCPOS
	ld	a,(cscore)
1:	ld	(cursor),hl
	ld	c,10
	cp	c
	jp	c,1f
	ld	e,a
	call	udiv8
	ld	a,e
	add	a,'0'
	call	prtout
	ld	a,c
1:	add	a,'0'
	call	prtout
	ld	a,' '
	jp	prtout

	.end
