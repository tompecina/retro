; main.S
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

	.global	main
main:
	ld	sp,0x7f00
	call	add_glyphs
;; 	ld	c,0
;; 	ld	de,res
;; 1:	ld	hl,testboard
;; 	push	bc
;; 	push	de
;; 	call	legal
;; 	pop	de
;; 	pop	bc
;; 	rla
;; 	and	1
;; 	ld	(de),a
;; 	inc	de
;; 	inc	c
;; 	ld	a,c
;; 	cp	64
;; 	jp	nz,1b
;; 	jp	$

1:
	ld	hl,testboard
	call	score_board
	ld	hl,testboard
	call	score_board
	ld	hl,testboard
	call	score_board
	ld	hl,testboard
	call	score_board
	ld	hl,testboard
	call	score_board
	ld	hl,testboard
	call	score_board
	ld	hl,testboard
	call	score_board
	ld	hl,testboard
	call	score_board
	ld	hl,testboard
	call	score_board
	ld	hl,testboard
	call	score_board

	ld	a,'.'
	call	0x8500

	jp	1b
	
	jp	$


res:	ds	64

testboard:
	.byte	0, 2, 1, 0, 0, 0, 0, 0
	.byte	0, 0, 0, 0, 0, 0, 0, 0
	.byte	0, 2, 0, 0, 0, 0, 0, 0
	.byte	0, 2, 0, 2, 1, 0, 0, 0
	.byte	0, 1, 1, 1, 2, 0, 0, 0
	.byte	0, 0, 1, 2, 0, 0, 0, 0
	.byte	0, 0, 0, 1, 1, 2, 2, 1
	.byte	0, 0, 0, 0, 0, 0, 2, 2
	
	.end
 
