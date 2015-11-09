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

	.text
	.global	main
main:
	ld	sp,0x7f00
	call	add_glyphs
	call	draw_labels
	call	draw_board
	call	init_game
	call	player_select

	jp	.
	
	call	draw_board
	ld	hl,bd
	ld	de,bd + 8
	push	hl
	push	de
	call	draw_pos
	pop	de
	pop	hl

	
	call	draw_board
	
1:
	ld	hl,bd
	ld	de,bd + 8
	push	hl
	push	de
	call	draw_pos
	pop	de
	pop	hl
	call	lpause
	push	hl
	push	de
	call	neswflip
	pop	de
	pop	hl
	jp	1b

lpause:	push	hl
	.rept	15
	call	long_anim_pause
	.endr
	pop	hl
	ret


	;; call	init_rvt
	;; call	init_hash
	
1:
	ld	hl,bd
	ld	de,bd + 8
	.rept	9
	push	hl
	push	de
	;; call	all_legal
	;; call	score_board
	;; call	zobrist_hash
	pop	hl
	pop	de
	ld	bc,16
	add	hl,bc
	ex	de,hl
	add	hl,bc
	.endr
	
	ld	a,'.'
	call	0x8500

	jp	1b

bd:
	.byte	0x54, 0x26, 0x08, 0x83, 0x42, 0x66, 0xa4, 0x9a ; fbe5
	.byte	0x01, 0x08, 0x55, 0x20, 0x01, 0x89, 0x00, 0x40
	.byte	0x11, 0x20, 0x40, 0x00, 0x30, 0x82, 0x1b, 0x49 ; 087a
	.byte	0x80, 0x16, 0x08, 0x1d, 0x80, 0x40, 0x00, 0x02
	.byte	0x00, 0x12, 0x06, 0x40, 0x08, 0x00, 0x54, 0x84 ; 0963
	.byte	0x00, 0x00, 0xe8, 0x22, 0x23, 0x82, 0xaa, 0x78
	.byte	0x88, 0x4c, 0xa1, 0x84, 0xd0, 0x12, 0x20, 0x11 ; 18b6
	.byte	0x12, 0x01, 0x0a, 0x12, 0x0c, 0x20, 0x01, 0x00
	.byte	0x80, 0x08, 0x02, 0xa1, 0xa2, 0x06, 0x99, 0x18 ; f54c
	.byte	0x6d, 0x04, 0x60, 0x1e, 0x4d, 0x08, 0x60, 0xe0
	.byte	0x11, 0x20, 0x11, 0x08, 0x00, 0x03, 0x60, 0x00 ; fa7e
	.byte	0xc2, 0x40, 0x04, 0x11, 0x9a, 0x68, 0x04, 0xc0
	.byte	0x04, 0x2a, 0x30, 0xa4, 0x15, 0x86, 0x10, 0x8d ; 09d7
	.byte	0x41, 0x50, 0x80, 0x18, 0xc8, 0x48, 0x20, 0x02
	.byte	0x00, 0x02, 0x08, 0x19, 0x64, 0x02, 0x20, 0x09 ; 0fcd
	.byte	0x65, 0xc0, 0x10, 0x42, 0x10, 0x21, 0xd2, 0x20
	.byte	0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55 ; 7769
	.byte	0xaa, 0xaa, 0xaa, 0xaa, 0xaa, 0xaa, 0xaa, 0xaa

	
	
	
;; blt:	.byte	0x04, 0x00, 0x00, 0x10, 0x0e, 0x04, 0x98, 0x00
;; wht:	.byte	0x02, 0x00, 0x02, 0x0a, 0x10, 0x08, 0x60, 0xc0

	;; .byte	0, 2, 1, 0, 0, 0, 0, 0
	;; .byte	0, 0, 0, 0, 0, 0, 0, 0
	;; .byte	0, 2, 0, 0, 0, 0, 0, 0
	;; .byte	0, 2, 0, 2, 1, 0, 0, 0
	;; .byte	0, 1, 1, 1, 2, 0, 0, 0
	;; .byte	0, 0, 1, 2, 0, 0, 0, 0
	;; .byte	0, 0, 0, 1, 1, 2, 2, 1
	;; .byte	0, 0, 0, 0, 0, 0, 2, 2










	ld	hl,black
	ld	de,white
	call	count_moves

	jp	.

	
1:	call	init_rvt
	ld	a,'.'
	call	0x8500
	jp	1b
	
	ld	hl,0x5555
	ld	c,2
	call	udiv16_8
	
;; 	call	set_kmap
;; 1:	call	inkey
;; 	jp	z,main
;; 	call	0x8125
;; 	jp	1b

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
	ld	a,'('
	call	wrchar
	inc	hl
	ld	a,'&'
	call	wrchar
	inc	hl
	ld	a,'@'
	call	wrchar
	inc	hl
	ld	a,'G'
	call	wrchar
	inc	hl
	ld	a,'i'
	call	wrchar
	inc	hl
	ld	a,'l'
	call	wrchar
	inc	hl
	ld	a,0xc4
	call	wrchar
	inc	hl
	ld	a,0xc9
	call	wrchar
	inc	hl
	ld	a,0xcb
	call	wrchar
	inc	hl
	ld	a,0xcc
	call	wrchar
	inc	hl
	ld	a,')'
	call	wrchar
	inc	hl


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

black:	.byte	0x0f, 0x03, 0x80, 0x10, 0x08, 0x00, 0xc0, 0x00
white:	.byte	0x40, 0x7c, 0x70, 0x08, 0x10, 0x00, 0x02, 0x40

	/*
	
	B B B B - - W -
	B B W W W W W -
	- - - - W W W B
	- - - W B - - -
	- - - B W - - -
	- - - - - - - -
	- W - - - - B B
	- - - - - - W -

	B B B B * * W *
	B B W W W W W *
	- * * * W W W B
	- - * W B * * -
	- - - B W * - -
	- - - - * - - -
	- W - - - - B B
	- - - - - - W -

	B B B B - - W -
	B B W W W W W -
	- - - - W W W B
	- - - W B * - -
	- - * B W - - -
	- - * * - - * -
	- W - - - - B B
	- - - - - - W -

	*/
	
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
	call	PMD_PRTOUT
	call	draw_labels
	call	draw_board
	ld	c,64
1:	push	bc
	ld	hl,BLANK_SQUARE
	call	draw_shape
	pop	bc
	dec	c
	jp	p,1b
	ld	hl,BLACK_DISC
	ld	c,0x1b
	call	draw_shape
	ld	hl,BLACK_DISC
	ld	c,0x24
	call	draw_shape
	ld	hl,WHITE_DISC
	ld	c,0x1c
	call	draw_shape
	ld	hl,WHITE_DISC
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
	ld	hl,WHITE_DISC
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
	ld	hl,BLACK_DISC
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
	ld	hl,WHITE_DISC
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
 
