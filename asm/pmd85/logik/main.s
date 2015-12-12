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
again:	ld	hl,msg_nrnds
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
	ld	a,0
	ld	(round),a

; display initial score
	xor	a
	ld	(pscore),a
	ld	(cscore),a
	call	disp_score

; display round number
3:	ld	a,(round)
	add	a,'1'
	ld	hl,RNDPOS
	call	write

; select code
	call	select_code
	ld	ccode,hl

; initialize attemps
	ld	b,0
	
; display starting prompt
	push	bc
	ld	hl,msg_cstart
	call	get_ack
	pop	bc
	
; let player guess
1:	push	bc
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
	cp	SUCC
	jp	z,1f
	inc	b
	ld	a,b
	cp	ATTEMPTS
	jp	nz,1b
	ld	hl,msg_cout
	call	get_ack
	ld	hl,cscore
	ld	a,(hl)
	add	a,ATTEMPTS + 1
	ld	(hl),a
	jp	2f
1:	push	bc
	ld	e,(hl)
	ld	d,0
	push	de
	ld	hl,msg_ccorr
	call	disp_msg
	pop	de
	ld	hl,corr
	add	hl,de
	add	hl,de
	ld	e,(hl)
	inc	hl
	ld	d,(hl)
	ex	de,hl
	call	writeln
	pop	bc
	ld	a,(cscore)
	add	a,b
	inc	a
	ld	(cscore),a
	
; update score
2:	call	disp_score
	
; clear board and display prompt
	call	draw_board
	call	rnd_map
	ld	hl,msg_pstart
	call	get_ack
	
; initialize variables
	ld	b,0
	ld	hl,guesses
	ld	(pguess),hl

; create guess
	ld	a,0xff
4:	push	bc
	call	get_guess
	pop	bc
	jp	nc,1f
	ld	hl,msg_perr
	call	get_ack
	jp	2f
1:	ex	de,hl
	ld	hl,(pguess)
	ld	(hl),e
	inc	hl
	ld	(hl),d
	inc	hl
	ld	(pguess),hl
	ex	de,hl
	push	bc
	call	trans_code
	pop	bc
	push	bc
	call	disp_guess

; evaluate guess
	call	player_score
	pop	bc
	push	af
	call	draw_pegs
	pop	af
	ld	hl,(pguess)
	ld	(hl),a
	inc	hl
	ld	(pguess),hl
	cp	SUCC
	jp	z,1f
	inc	b
	ld	c,a
	ld	a,b
	cp	ATTEMPTS
	ld	a,c
	jp	nz,4b
	ld	hl,msg_pout
	call	get_ack
	ld	hl,pscore
	ld	a,(hl)
	add	a,ATTEMPTS + 1
	ld	(hl),a
	jp	2f
1:	ld	hl,pscore
	ld	a,b
	add	a,(hl)
	inc	a
	ld	(hl),a
	push	bc
	ld	hl,msg_pcorr
	call	disp_msg
	pop	bc
	ld	e,b
	ld	d,0
	ld	hl,corr
	add	hl,de
	add	hl,de
	ld	e,(hl)
	inc	hl
	ld	d,(hl)
	ex	de,hl
	call	writeln

; next round
2:	call	draw_board
	ld	hl,round
	inc	(hl)
	ld	a,(rounds)
	cp	(hl)
	jp	nz,3b

; check if player wishes to play again
	ld	hl,msg_again
	call	get_conf
	jp	z,quit
	jp	again
	
; number of rounds validation function
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
	.endr
	.word	msg_corr10
	
	.globl	guesses
	.lcomm	rounds, 1
	.lcomm	round, 1
	.lcomm	pscore, 1
	.lcomm	cscore, 1
	.lcomm	ccode, 2
	.lcomm	guesses, ATTEMPTS * 3
	.lcomm	pguess, 2
	
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
