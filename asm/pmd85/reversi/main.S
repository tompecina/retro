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
	di
	ld	sp,0x7f00
	
	call	add_glyphs

	xor	a
	ld	(COLOR),a
	ld	a,0x1c
	call	PMD_PRTOUT
	ld	hl,0xc000
	ld	bc,0x3010
	;; ld	a,0x3f
	;; ld	(COLOR),a
	;; call	rect
	ld	hl,label_reversi
	ld	de,0xc190
	call	label
	xor	a
	ld	(COLOR),a
	call	draw_labels
	call	draw_board

	ld	hl,0xd000
	ld	a,'c' + 0x80
	call	wrchar
	inc	hl
	ld	a,'e'
	call	wrchar
	inc	hl
	ld	a,'r'
	call	wrchar
	inc	hl
	ld	a,'n'
	call	wrchar
	inc	hl
	ld	a,'Y' + 0x80
	call	wrchar


	ld	hl,black
	ld	de,white
	call	draw_pos

	ld	hl,black
	ld	de,white
	ld	c,0x13
	ld	b,0
	call	anim_move
	ld	hl,black
	ld	de,white
	ld	c,0x13
	ld	b,0
	call	make_move
	call	long_anim_pause
	call	long_anim_pause
	call	long_anim_pause
	call	long_anim_pause
	call	long_anim_pause

	ld	hl,black
	ld	de,white
	ld	c,0x22
	ld	b,1
	call	anim_move
	ld	hl,black
	ld	de,white
	ld	c,0x22
	ld	b,1
	call	make_move
	call	long_anim_pause
	call	long_anim_pause
	call	long_anim_pause
	call	long_anim_pause
	call	long_anim_pause

	ld	hl,black
	ld	de,white
	ld	c,0x04
	ld	b,0
	call	anim_move
	ld	hl,black
	ld	de,white
	ld	c,0x04
	ld	b,0
	call	make_move
	call	long_anim_pause
	call	long_anim_pause
	call	long_anim_pause
	call	long_anim_pause
	call	long_anim_pause

	ld	hl,black
	ld	de,white
	ld	c,0x06
	ld	b,1
	call	anim_move
	ld	hl,black
	ld	de,white
	ld	c,0x06
	ld	b,1
	call	make_move
	call	long_anim_pause
	call	long_anim_pause
	call	long_anim_pause
	call	long_anim_pause
	call	long_anim_pause

	ld	hl,black
	ld	de,white
	ld	c,0x2a
	ld	b,0
	call	anim_move
	ld	hl,black
	ld	de,white
	ld	c,0x2a
	ld	b,0
	call	make_move
	call	long_anim_pause
	call	long_anim_pause
	call	long_anim_pause
	call	long_anim_pause
	call	long_anim_pause

	ld	hl,black
	ld	de,white
	ld	c,0x09
	ld	b,1
	call	anim_move
	ld	hl,black
	ld	de,white
	ld	c,0x09
	ld	b,1
	call	make_move
	call	long_anim_pause
	call	long_anim_pause
	call	long_anim_pause
	call	long_anim_pause
	call	long_anim_pause

	ld	hl,black
	ld	de,white
	ld	c,0x05
	ld	b,0
	call	anim_move
	ld	hl,black
	ld	de,white
	ld	c,0x05
	ld	b,0
	call	make_move
	call	long_anim_pause
	call	long_anim_pause
	call	long_anim_pause
	call	long_anim_pause
	call	long_anim_pause

	ld	hl,black
	ld	de,white
	ld	c,0x00
	ld	b,1
	call	anim_move
	ld	hl,black
	ld	de,white
	ld	c,0x00
	ld	b,1
	call	make_move
	call	long_anim_pause
	call	long_anim_pause
	call	long_anim_pause
	call	long_anim_pause
	call	long_anim_pause

	ld	hl,black
	ld	de,white
	ld	c,0x08
	ld	b,0
	call	anim_move
	ld	hl,black
	ld	de,white
	ld	c,0x08
	ld	b,0
	call	make_move
	call	long_anim_pause
	call	long_anim_pause
	call	long_anim_pause
	call	long_anim_pause
	call	long_anim_pause

	ld	hl,black
	ld	de,white
	ld	c,0x10
	ld	b,1
	call	anim_move
	ld	hl,black
	ld	de,white
	ld	c,0x10
	ld	b,1
	call	make_move
	call	long_anim_pause
	call	long_anim_pause
	call	long_anim_pause
	call	long_anim_pause
	call	long_anim_pause

	
	jp	$

black:	.byte	0x0e, 0x00, 0x80, 0x10, 0x08, 0x00, 0x00, 0x00
white:	.byte	0x00, 0x3c, 0x70, 0x08, 0x10, 0x00, 0x00, 0x00

	
	ld	hl,mf
	ld	de,of
	ld	c,0x00
	call	one_legal

	ld	hl,mf
	ld	de,of
	ld	c,0x01
	call	one_legal

	ld	hl,mf
	ld	de,of
	ld	c,0x05
	call	one_legal

	ld	hl,mf
	ld	de,of
	ld	c,0x0d
	call	one_legal

	ld	hl,mf
	ld	de,of
	ld	c,0x13
	call	one_legal

	jp	$
	
	ld	hl,mf
	ld	de,of
	call	all_legal
	jp	$
	

mf:	.byte	0x08, 0x00, 0x00, 0x10, 0x08, 0x00, 0x00, 0x00
of:	.byte	0x16, 0x1e, 0x00, 0x08, 0x10, 0x00, 0x00, 0x00


2:	push	hl
	ld	e,8
1:	ld	a,(hl)
	inc	hl
	ld	(bc),a
	inc	bc
	dec	e
	jp	nz,1b
	pop	hl
	dec	d
	jp	nz,2b
	ret










	di
	call	add_glyphs

	xor	a
	ld	(COLOR),a
	ld	a,0x1c
	call	PRTOUT
	call	draw_labels
	call	draw_board
	ld	c,64
1:	push	bc
	ld	hl,BLANK_SQUARE
	call	draw_shape
	pop	bc
	dec	c
	jp	p,1b
	ld	hl,BLACK_DISK
	ld	c,0x1b
	call	draw_shape
	ld	hl,BLACK_DISK
	ld	c,0x24
	call	draw_shape
	ld	hl,WHITE_DISK
	ld	c,0x1c
	call	draw_shape
	ld	hl,WHITE_DISK
	ld	c,0x23
	call	draw_shape
	ld	hl,BLANK_CURSOR
	ld	c,0x32
	call	draw_shape
	ld	hl,BLACK_CURSOR
	ld	c,0x11
	call	draw_shape
	ld	hl,WHITE_CURSOR
	ld	c,0x19
	call	draw_shape
	ld	hl,BLACK_TURNED1
	ld	c,0x33
	call	draw_shape
	ld	hl,BLACK_TURNED2
	ld	c,0x34
	call	draw_shape
	ld	hl,HALF_TURNED
	ld	c,0x35
	call	draw_shape
	ld	hl,WHITE_TURNED1
	ld	c,0x36
	call	draw_shape
	ld	hl,WHITE_TURNED2
	ld	c,0x37
	call	draw_shape

2:	ld	c,0
1:	push	bc
	call	anim_b2w
	pop	bc
	inc	c
	ld	a,c
	cp	64
	jp	nz,1b

	ld	c,0
1:	push	bc
	call	anim_w2b
	pop	bc
	inc	c
	ld	a,c
	cp	64
	jp	nz,1b

	jp	2b
1:	
	ld	hl,WHITE_DISK
	ld	c,0x24
	call	draw_shape
	call	animpause
	ld	hl,WHITE_TURNED1
	ld	c,0x24
	call	draw_shape
	call	animpause
	ld	hl,WHITE_TURNED2
	ld	c,0x24
	call	draw_shape
	call	animpause
	ld	hl,HALF_TURNED
	ld	c,0x24
	call	draw_shape
	call	animpause
	ld	hl,BLACK_TURNED2
	ld	c,0x24
	call	draw_shape
	call	animpause
	ld	hl,BLACK_TURNED1
	ld	c,0x24
	call	draw_shape
	call	animpause
	ld	hl,BLACK_DISK
	ld	c,0x24
	call	draw_shape
	call	pause
	ld	hl,BLACK_TURNED1
	ld	c,0x24
	call	draw_shape
	call	animpause
	ld	hl,BLACK_TURNED2
	ld	c,0x24
	call	draw_shape
	call	animpause
	ld	hl,HALF_TURNED
	ld	c,0x24
	call	draw_shape
	call	animpause
	ld	hl,WHITE_TURNED2
	ld	c,0x24
	call	draw_shape
	call	animpause
	ld	hl,WHITE_TURNED1
	ld	c,0x24
	call	draw_shape
	call	animpause
	ld	hl,WHITE_DISK
	ld	c,0x24
	call	draw_shape
	call	pause
	
	
	jp	1b

pause:	ld	hl,40000
1:	dec	hl
	ld	a,h
	or	l
	jp	nz,1b
	ret


animpause:
	ld	hl,3000
	jp	1b
	
	ld	hl,0xc000
	ld	bc,0x3010
	ld	a,0x3f
	ld	(COLOR),a
	call	rect
	ld	hl,label_reversi
	ld	de,0xc082
	call	label

	jp	$
	
	ld	hl,0xd000
	ld	(CURSOR),hl
	ld	hl,credits
	call	prtstr
	jp	$
credits:
	db	"(C) 2015 Tom", 'A' + 0x80, 'S' + 0x80, " Pecina", 0
	db	" <tomas@pecina.cz>", 0
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
 
