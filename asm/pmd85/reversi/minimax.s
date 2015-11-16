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
; minimax - calculate value of the best variant
; 
;   input:  (BC) - parameter block:
;   	      +0 - my discs
;	      +8 - opponents discs
;	      +16 - search depth
;	      +17 - alpha
;	      +19 - beta
;	      +21 - =0xff maximize
;	    	    =0x00 minimize
; 
;   output: HL - value
; 	    C - best move (only if maximize and depth > 0)
; 
;   uses:   all
; 
	.text
	.globl	minimax
minimax:
	
; check if depth > 0
	ld	hl,depo
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
	ld	hl,myo		; no legal moves
	add	hl,bc
	ex	de,hl
	ld	hl,opo
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
	
; set initial value
	ld	hl,mmo
	add	hl,bc
	ld	a,(hl)
	or	a
	ld	de,MINWORD
	jp	nz,5f
	dec	de

; check for sentinel
5:	ex	(sp),hl
	ld	a,h
	or	a
	jp	p,1f
	pop	bc
	ret
	
; convert move
1:	push	bc
	ld	b,h
	ld	c,l
	call	rc2sq
	ld	a,c
	pop	bc
	pop	hl
	ld	h,a
	
; copy board
	push	de
	push	hl
	ld	hl,myo + pblen
	add	hl,bc
	ex	de,hl
	ld	hl,opo
	add	hl,bc
	push	bc
	ld	b,8
	call	copy8
	ld	bc,-16
	add	hl,bc
	ld	b,8
	call	copy8
	ld	bc,8
	add	hl,bc
	
; set remaining parameters
	ld	a,(hl)
	dec	a
	ld	(de),a		; depth = depth - 1
	inc	hl
	inc	de
	ld	b,4
	call	copy8		; alpha, beta
	ld	a,(hl)
	cpl
	ld	(de),a		; maxmin
	pop	bc
	
; perform move
	pop	hl
	push	hl
	ld	a,h
	ld	hl,opo + pblen
	add	hl,bc
	ex	de,hl
	ld	hl,myo + pblen
	add	hl,bc
	push	bc
	ld	c,a
	ld	b,1
	call	make_move

; call itself recursively
	pop	bc
	push	bc
	ld	hl,pblen
	add	hl,bc
	ld	b,h
	ld	c,l
	call	minimax

; check maxmin
	pop	bc
	ex	de,hl
	ld	hl,mmo
	add	hl,bc
	ld	a,(hl)
	or	a
	jp	z,6f
	
; maximize
	pop	hl
	ex	(sp),hl
	call	scmphlde	; value > new value ?
	jp	c,1f
	pop	hl
	ld	l,h
	push	hl
	jp	2f
1:	ex	de,hl
2:	ld	hl,alphao
	add	hl,bc
	push	hl
	push	hl
	ld	a,(hl)
	inc	hl
	ld	h,(hl)
	ld	l,a
	call	scmphlde	; alpha > value ?
	pop	hl
	jp	c,1f
	ld	(hl),e
	inc	hl
	ld	(hl),d
1:	pop	hl
	ld	e,(hl)
	inc	hl
	ld	d,(hl)
	inc	hl
	ld	a,(hl)
	inc	hl
	ld	h,(hl)
	ld	l,a
	call	scmpdehl	; alpha > beta ?
9:	jp	c,7f
8:	pop	hl
	jp	5b

; break
7:	ex	(sp),hl
	ld	a,h
	cp	0xff
	jp	z,7b
	pop	hl
	jp	8b
	
; minimize
6:	pop	hl
	ex	(sp),hl
	call	scmpdehl	; value < new value ?
	jp	c,1f
	pop	hl
	ld	l,h
	push	hl
	jp	2f
1:	ex	de,hl
2:	ld	hl,betao
	add	hl,bc
	push	hl
	ld	a,(hl)
	inc	hl
	ld	h,(hl)
	ld	l,a
	call	scmpdehl	; beta < value ?
	pop	hl
	jp	c,1f
	ld	(hl),e
	inc	hl
	ld	(hl),d
1:	ld	hl,alphao
	add	hl,bc
	ld	e,(hl)
	inc	hl
	ld	d,(hl)
	inc	hl
	ld	a,(hl)
	inc	hl
	ld	h,(hl)
	ld	l,a
	call	scmphlde	; alpha < beta ?
	jp	9b
	
; (HL) = my discs, (DE) = opponent's discs
3:	ld	hl,opo
	add	hl,bc
	ex	de,hl
	ld	hl,myo
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

; parameter block
	.globl	myo, opo, depo, alphao, betao, mmo, pblen
	.struct	0
myo:	.skip	8
opo:	.skip	8
depo:	.skip	1
alphao:	.skip	2
betao:	.skip	2
mmo:	.skip	1
pblen:
	
	.end
