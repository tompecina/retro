; minimax.s
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


; Minimax search with alpha-beta pruning.

	.include "reversi.inc"
	
; ==============================================================================
; minimax - calculate value of the best/worst variant
; 
;   input:  (SP+2) - my discs
;	    (SP+10) - opponents discs
;	    (SP+18) - search depth
;	    (SP+19) - alpha
;	    (SP+21) - beta
;	    (SP+23) - =0xff maximize
;	    	      =0x00 minimize
; 
;   output: HL - value
; 	    C - best move (only valid if maximize and depth > 0)
; 
;   uses:   all
; 
	.text
	.global	minimax
minimax:
	
; BC = initial SP
	ld	hl,0
	add	hl,sp
	ld	b,h
	ld	c,l

; check if depth > 0
	ld	hl,depth
	add	hl,bc
	ld	a,(hl)
	or	a
	jp	nz,1f
	call	3f		; depth = 0
	jp	score_board
	
; check if terminal position
1:	call	3f
	push	bc
	call	all_legal
	pop	bc
	call	4f
	jp	nz,1f
	ld	hl,my		; no legal moves
	add	hl,bc
	ex	de,hl
	ld	hl,op
	add	hl,bc
	push	bc
	call	all_legal
	pop	bc
	call	4f
	jp	nz,1f
	call	3f		; terminal position
	call	score_board
	ld	c,PASS
	ret
	
; push legal moves
1:	ld	d,0xff
	push	de		; sentinel
	ld	d,0
2:	ld	a,(hl)
	inc	hl
	ld	e,0xff
5:	or	a
	jp	z,1f
	rra			; CY = 0
	inc	e
	jp	nc,5b
	push	de
	jp	5b
1:	inc	d
	ld	a,d
	cp	8
	jp	nz,2b
	
; set initial V
	ld	hl,maxmin
	add	hl,bc
	ld	a,(hl)
	or	a
	jp	z,1f
	ld	de,0x8000
	jp	2f
1:	ld	de,0x7fff

; check for sentinel
2:	pop	hl
	ld	a,h
	or	a
	jp	m,2f
	
; convert move
	push	bc
	ld	b,h
	ld	c,l
	call	rc2sq
	ld	a,c
	pop	bc
	
; make room for new parameters
	ld	hl,-parlen
	add	hl,sp
	ld	sp,hl

; copy board
	ld	hl,my
	add	hl,bc
	push	hl
	ld	hl,op
	add	hl,sp
	ex	de,hl
	ex	(sp),hl
	push	af
	push	bc
	ld	b,8
	call	copy8
	push	hl
	ld	de,-16
	add	hl,de
	ex	de,hl
	pop	hl
	ld	b,8
	call	copy8
	
; set remaining parameters
	ld	bc,8
	ex	de,hl
	add	hl,bc
	ld	a,(de)
	dec	a
	ld	(hl),de		; depth = depth - 1
	ld	b,4
	call	copy8		; alpha, beta
	ld	a,(hl)
	cpl
	ld	(de),a		; maxmin
	
	
2:	
	
; (HL) = my discs, (DE) = opponent's discs
3:	ld	hl,op
	add	hl,bc
	ex	de,hl
	ld	hl,my
	add	hl,bc
	ret

; check if (HL) to (HL+7) are 0's
4:	push	hl
	ld	d,8
1:	ld	a,(hl)
	or	a
	jp	nz,1f
	inc	hl
	dec	d
	jp	nz,1b
1:	pop	hl
	ret
	
	.equiv	my, 2
	.equiv	op, 10
	.equiv	depth, 18 
	.equiv	alpha, 19
	.equiv	beta, 21
	.equiv	maxmin, 23
	.equiv	parlen, 24
	
	.end
 
