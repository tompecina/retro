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
	.equiv	MAX_DEPTH, 5	; maximum depth
	
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
	ld	(depth),a
	call	disp_depth

	ld	a,0xff
	ld	(sound),a
	call	disp_sound

	call	init_game
	call	disp_score

	ld	a,0xff
	ld	(compcol),a
	call	disp_compcol
	
	jp	.

	.globl	black, white
	.lcomm	black, 8
	.lcomm	white, 8
	.lcomm	depth, 1
	.lcomm	sound, 1
	.lcomm	compcol, 1

; ==============================================================================
; Display depth/difficulty
;
disp_depth:
	ld	a,(depth)
	add	a,'0'
	ld	hl,DPPOS
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
;   output: BC - representation as digits
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
