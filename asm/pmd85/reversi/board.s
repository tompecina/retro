; board.s
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


; This is the board drawing code.

	.include "reversi.inc"
	
; ==============================================================================
; Constants
	
	.equiv	ULC, 0xd116		; upper left corner of the board
	.equiv	MSGAREA, 0xffc0		; position of the notification area
	
; ==============================================================================
; draw_board - draw board
; 
;   uses:   all
; 
	.text
	.globl	draw_board
draw_board:
	ld	hl,ULC
	call	2f
	ld	b,7
1:	call	3f
	call	4f
	dec	b
	jp	nz,1b
	call	3f
2:	ld	b,24		; draw solid horizontal line
1:	ld	(hl),0x3f
	inc	hl
	dec	b
	jp	nz,1b
	ld	(hl),0x01
	jp	7f
4:	ld	a,24		; draw dotted horizontal line
1:	ld	(hl),0x15
	inc	hl
	dec	a
	jp	nz,1b
	ld	(hl),0x01
	jp	7f
3:	ld	c,8		; draw one row
1:	call	5f
	call	6f
	dec	c
	jp	nz,1b
5:	ld	(hl),0x01	; draw odd line
	ld	de,24
	add	hl,de
	ld	(hl),0x01
	jp	7f
6:	ld	a,9		; draw even line
1:	ld	(hl),0x01
	inc	hl
	inc	hl
	inc	hl
	dec	a
	jp	nz,1b
	ld	de,37
	jp	1f
7:	ld	de,40		; adjust HL for the next line
1:	add	hl,de
	ret

; ==============================================================================
; draw_labels - draw labels
; 
;   uses:   A, D, E, H, L
; 
	.text
	.globl	draw_labels
draw_labels:
	ld	hl,ULC - 127
	ld	a,'A'
1:	call	2f
	ld	de,10240
	add	hl,de
	call	2f
	ld	de,-10237
	add	hl,de
	inc	a
	cp	'I'
	jp	nz,1b
	ld	hl,ULC + 894
	ld	a,'1'
1:	call	2f
	ld	de,27
	add	hl,de
	call	2f
	ld	de,1125
	add	hl,de
	inc	a
	cp	'9'
	jp	nz,1b
	ret
2:	push	af
	call	wrchar
	pop	af
	ret
	
; ==============================================================================
; draw_shape - draw shape
; 
;   input:  (HL) - shape
;           C - square
;
;   uses:   all
; 
	.text
draw_shape:
	push	hl
	ld	b,c
	ld	a,b
	and	0x38
	ld	e,a
	ld	d,0
	ld	hl,144
	push	bc
	call	mul16
	pop	bc
	ld	a,c
	and	0x07
	ld	e,a
	add	a,a
	add	a,e
	ld	e,a
	ld	d,0
	add	hl,de
	ld	de,ULC + 64
	add	hl,de
	pop	de
	ld	b,17
1:	ld	a,(de)
	ld	c,a
	ld	a,(hl)
	and	1
	or	c
	ld	(hl),a
	inc	hl
	inc	de
	ld	a,(de)
	ld	(hl),a
	inc	hl
	inc	de
	ld	a,(de)
	ld	(hl),a
	dec	b
	ret	z
	ld	a,b
	ld	bc,62
	add	hl,bc
	ld	b,a
	inc	de
	jp	1b
	
; ==============================================================================
; Shapes

	.data
	
; blank square
blank_square:
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00

; black disc
black_disc:
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x1c, 0x00
	.byte	0x00, 0x23, 0x01
	.byte	0x20, 0x00, 0x02
	.byte	0x10, 0x00, 0x04
	.byte	0x08, 0x00, 0x08
	.byte	0x08, 0x00, 0x08
	.byte	0x04, 0x00, 0x10
	.byte	0x04, 0x00, 0x10
	.byte	0x04, 0x00, 0x10
	.byte	0x08, 0x00, 0x08
	.byte	0x08, 0x00, 0x08
	.byte	0x10, 0x00, 0x04
	.byte	0x20, 0x00, 0x02
	.byte	0x00, 0x23, 0x01
	.byte	0x00, 0x1c, 0x00
	.byte	0x00, 0x00, 0x00

; black disc, turned 1
black_turned1:
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x3e, 0x00
	.byte	0x20, 0x01, 0x03
	.byte	0x10, 0x00, 0x04
	.byte	0x08, 0x00, 0x08
	.byte	0x04, 0x00, 0x10
	.byte	0x04, 0x00, 0x10
	.byte	0x04, 0x00, 0x10
	.byte	0x08, 0x00, 0x08
	.byte	0x10, 0x00, 0x04
	.byte	0x20, 0x01, 0x03
	.byte	0x00, 0x3e, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00

; black disc, turned 2
black_turned2:
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x3f, 0x01
	.byte	0x30, 0x00, 0x06
	.byte	0x08, 0x00, 0x08
	.byte	0x0c, 0x00, 0x18
	.byte	0x08, 0x00, 0x08
	.byte	0x30, 0x00, 0x06
	.byte	0x00, 0x3f, 0x01
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00

; black disc, turned 3
	.globl	black_turned3
black_turned3:
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x3f, 0x01
	.byte	0x38, 0x00, 0x0e
	.byte	0x0c, 0x00, 0x18
	.byte	0x38, 0x00, 0x0e
	.byte	0x00, 0x3f, 0x01
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00

; disc, half turned
half_turned:
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x3c, 0x3f, 0x1f
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00

; white disc, turned 3
white_turned3:
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x3f, 0x01
	.byte	0x38, 0x3f, 0x0f
	.byte	0x3c, 0x3f, 0x1f
	.byte	0x38, 0x3f, 0x0f
	.byte	0x00, 0x3f, 0x01
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00

; white disc, turned 2
white_turned2:
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x3f, 0x01
	.byte	0x30, 0x3f, 0x07
	.byte	0x38, 0x3f, 0x0f
	.byte	0x3c, 0x3f, 0x1f
	.byte	0x38, 0x3f, 0x0f
	.byte	0x30, 0x3f, 0x07
	.byte	0x00, 0x3f, 0x01
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00

; white disc, turned 1
white_turned1:
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x3e, 0x00
	.byte	0x20, 0x3f, 0x03
	.byte	0x30, 0x3f, 0x07
	.byte	0x38, 0x3f, 0x0f
	.byte	0x3c, 0x3f, 0x1f
	.byte	0x3c, 0x3f, 0x1f
	.byte	0x3c, 0x3f, 0x1f
	.byte	0x38, 0x3f, 0x0f
	.byte	0x30, 0x3f, 0x07
	.byte	0x20, 0x3f, 0x03
	.byte	0x00, 0x3e, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x00, 0x00

; white disc
white_disc:
	.byte	0x00, 0x00, 0x00
	.byte	0x00, 0x1c, 0x00
	.byte	0x00, 0x3f, 0x01
	.byte	0x20, 0x3f, 0x03
	.byte	0x30, 0x3f, 0x07
	.byte	0x38, 0x3f, 0x0f
	.byte	0x38, 0x3f, 0x0f
	.byte	0x3c, 0x3f, 0x1f
	.byte	0x3c, 0x3f, 0x1f
	.byte	0x3c, 0x3f, 0x1f
	.byte	0x38, 0x3f, 0x0f
	.byte	0x38, 0x3f, 0x0f
	.byte	0x30, 0x3f, 0x07
	.byte	0x20, 0x3f, 0x03
	.byte	0x00, 0x3f, 0x01
	.byte	0x00, 0x1c, 0x00
	.byte	0x00, 0x00, 0x00

; cursor on blank square
blank_cursor:
	.byte	0x2a, 0x2a, 0x2a
	.byte	0x14, 0x15, 0x15
	.byte	0x2a, 0x2a, 0x2a
	.byte	0x14, 0x15, 0x15
	.byte	0x2a, 0x2a, 0x2a
	.byte	0x14, 0x15, 0x15
	.byte	0x2a, 0x2a, 0x2a
	.byte	0x14, 0x15, 0x15
	.byte	0x2a, 0x2a, 0x2a
	.byte	0x14, 0x15, 0x15
	.byte	0x2a, 0x2a, 0x2a
	.byte	0x14, 0x15, 0x15
	.byte	0x2a, 0x2a, 0x2a
	.byte	0x14, 0x15, 0x15
	.byte	0x2a, 0x2a, 0x2a
	.byte	0x14, 0x15, 0x15
	.byte	0x2a, 0x2a, 0x2a
	
; cursor under black disc
black_cursor:
	.byte	0x2a, 0x2a, 0x2a
	.byte	0x14, 0x3f, 0x15
	.byte	0x2a, 0x23, 0x2b
	.byte	0x34, 0x00, 0x16
	.byte	0x1a, 0x00, 0x2c
	.byte	0x0c, 0x00, 0x18
	.byte	0x0a, 0x00, 0x28
	.byte	0x04, 0x00, 0x10
	.byte	0x06, 0x00, 0x30
	.byte	0x04, 0x00, 0x10
	.byte	0x0a, 0x00, 0x28
	.byte	0x0c, 0x00, 0x18
	.byte	0x1a, 0x00, 0x2c
	.byte	0x34, 0x00, 0x16
	.byte	0x2a, 0x23, 0x2b
	.byte	0x14, 0x3f, 0x15
	.byte	0x2a, 0x2a, 0x2a

; cursor under white disc
white_cursor:
	.byte	0x2a, 0x2a, 0x2a
	.byte	0x14, 0x3f, 0x15
	.byte	0x2a, 0x3f, 0x2b
	.byte	0x34, 0x3f, 0x17
	.byte	0x3a, 0x3f, 0x2f
	.byte	0x3c, 0x3f, 0x1f
	.byte	0x3a, 0x3f, 0x2f
	.byte	0x3c, 0x3f, 0x1f
	.byte	0x3e, 0x3f, 0x3f
	.byte	0x3c, 0x3f, 0x1f
	.byte	0x3a, 0x3f, 0x2f
	.byte	0x3c, 0x3f, 0x1f
	.byte	0x3a, 0x3f, 0x2f
	.byte	0x34, 0x3f, 0x17
	.byte	0x2a, 0x3f, 0x2b
	.byte	0x14, 0x3f, 0x15
	.byte	0x2a, 0x2a, 0x2a
	
; ==============================================================================
; anim_b2w - animate black-to-white
; 
;   input:  C - square
; 
;   uses:   all
; 
	.text
	.globl	anim_b2w
anim_b2w:
	ld	hl,black_disc
	ld	de,51
	jp	1f
	
; ==============================================================================
; anim_w2b - animate white-to-black
; 
;   input:  C - square
; 
;   uses:   all
; 
	.text
	.globl	anim_w2b
anim_w2b:
	ld	hl,white_disc
	ld	de,-51
1:	ld	b,9
	jp	animate
	
; ==============================================================================
; animate - animate shape transition
; 
;   input:  (HL) - initial shape
;           DE - difference (normally 51 or -51)
;           B - number of shapes
;           C - square
;
;   uses:   all
; 
	.text
	.globl	animate
animate:
	push	hl
	push	de
	push	bc
	call	draw_shape
	call	anim_pause
	pop	bc
	pop	de
	pop	hl
	dec	b
	ret	z
	add	hl,de
	jp	animate
	
; ==============================================================================
; anim_pause - animation pause
; 
;   uses:   A, H, L
; 
	.text
	.globl	anim_pause
anim_pause:
	ld	hl,2000
1:	dec	hl
	ld	a,h
	or	l
	jp	nz,1b
	ret
	
; ==============================================================================
; long_anim_pause - longer animation pause
; 
;   uses:   A, H, L
; 
	.text
	.globl	long_anim_pause
long_anim_pause:
	ld	hl,5000
	jp	1b
	
; ==============================================================================
; draw_pos - draw position
; 
;   input:  (black) - array of black discs
;           (white) - array of white discs
; 
;   uses:   all
; 
	.text
	.globl	draw_pos
draw_pos:
	ld	hl,black
	ld	de,white
	ld	b,0
4:	ld	a,(hl)
	push	hl
	ld	h,a
	ld	a,(de)
	push	de
	ld	l,a
	ld	c,7
3:	add	hl,hl
	push	hl
	jp	nc,1f
	ld	hl,black_disc
	jp	2f
1:	ld	a,h
	rra
	ld	hl,white_disc
	jp	c,2f
	ld	hl,blank_square
2:	push	bc
	call	rc2sq
	call	draw_shape
	pop	bc
	pop	hl
	dec	c
	jp	p,3b
	pop	de
	pop	hl
	inc	hl
	inc	de
	inc	b
	ld	a,b
	cp	8
	jp	nz,4b
	ret
	
; ==============================================================================
; anim_move - animate move
; 
;   input:  (HL) - array of black discs
;           (DE) - array of white discs
;           C - move (must be legal)
;           B - =0 black to move
;               >0 white to move
; 
;   uses:   all
; 
	.text
	.globl	anim_move
anim_move:
	ld	a,c
	cp	PASS
	ret	z
	push	hl
	push	de
	push	bc
	ld	a,b
	or	a
	jp	z,1f
	ld	hl,white_disc
	jp	2f
1:	ld	hl,black_disc
2:	call	draw_shape
	call	long_anim_pause
	pop	bc
	pop	de
	pop	hl
	ld	a,b
	or	a
	jp	z,1f
	ex	de,hl
1:	push	bc
	call	one_legal
	pop	bc
	ld	de,dir_offsets
2:	ld	a,(de)
	or	a
	ret	z
	inc	de
	push	de
	ld	d,a
	ld	e,(hl)
	inc	hl
	push	hl
	push	bc
1:	dec	e
	jp	m,1f
	ld	a,c
	add	a,d
	ld	c,a
	ld	a,b
	or	a
	push	de
	push	bc
	push	af
	call	z,anim_w2b
	pop	af
	call	nz,anim_b2w
	call	long_anim_pause
	pop	bc
	pop	de
	jp	1b
1:	pop	bc
	pop	hl
	pop	de
	jp	2b
	
; ==============================================================================
; Direction offsets
; 
	.data
dir_offsets:
	.byte	-9, -8, -7, -1, 1, 7, 8, 9, 0
	
; ==============================================================================
; init_pos - initialize position
; 
;   input:  (black) - array of black discs
;           (white) - array of white discs
; 
;   uses:   A, B, H, L
; 
	.text
	.globl	init_pos
init_pos:
	ld	hl,black
	ld	b,8
	call	zerofill8
	ld	hl,white
	ld	b,8
	call	zerofill8
	ld	a,0x10
	ld	(black + 3),a
	ld	(white + 4),a
	ld	a,0x08
	ld	(black + 4),a
	ld	(white + 3),a
	ret
	
; ==============================================================================
; init_game - initialize game
; 
;   uses:   A, B, H, L
; 
	.text
	.globl	init_game
init_game:
	call	init_pos
	xor	a
	ld	(cur_row),a
	ld	(cur_col),a
	ld	(tomove),a
	ld	(moven),a
	jp	draw_pos

	.lcomm	cur_row, 1
	.lcomm	cur_col, 1
	
; ==============================================================================
; player_select - let player select square
; 
;   input:  (black) - array of black discs
;           (white) - array of white discs
;           (cur_row) - cursor row
;           (cur_col) - cursor column
; 
;   output: C - square selected
;           (cur_row) - new cursor row
;           (cur_col) - new cursor column
;           A =0 selection confirmed with EOL
;             >0 scan code of the key 
; 
;   uses:   all
; 
	.text
	.globl	player_select
player_select:
	call	shcur
4:	call	inklav
	push	af
	call	clr_msg
	pop	af
	cp	KEOL
	jp	nz,1f
	xor	a
2:	call	hdcur
	push	af
	ld	a,(cur_row)
	ld	b,a
	ld	a,(cur_col)
	ld	c,a
	call	rc2sq
	pop	af
	ret
1:	cp	'A'
	jp	c,1f
	cp	'H' + 1
	jp	nc,1f
	sub	'A'
	call	hdcur
	ld	(cur_col),a
	jp	player_select
1:	cp	'1'
	jp	c,1f
	cp	'8' + 1
	jp	nc,1f
	sub	'1'
	call	hdcur
	ld	(cur_row),a
	jp	player_select
1:	cp	KLEFT
	jp	nz,1f
	ld	a,(cur_col)
	dec	a
	jp	m,4b
3:	call	hdcur
	ld	(cur_col),a
	jp	player_select
1:	cp	KRIGHT
	jp	nz,1f
	ld	a,(cur_col)
	inc	a
	cp	8
	jp	z,4b
	jp	3b
1:	cp	KHOME
	jp	nz,1f
5:	ld	a,(cur_row)
	dec	a
	jp	m,4b
3:	call	hdcur
	ld	(cur_row),a
	jp	player_select
1:	cp	KLLEFT
	jp	z,5b
	cp	KEND
	jp	nz,1f
5:	ld	a,(cur_row)
	inc	a
	cp	8
	jp	z,4b
	jp	3b
1:	cp	KRRIGHT
	jp	z,5b
	jp	2b
shcur:	call	getsq
	ld	hl,black_cursor
	dec	a
	jp	z,1f
	ld	hl,white_cursor
	dec	a
	jp	z,1f
	ld	hl,blank_cursor
1:	call	rc2sq
	jp	draw_shape
hdcur:	push	af
	call	getsq
	ld	hl,black_disc
	dec	a
	jp	z,1f
	ld	hl,white_disc
	dec	a
	jp	z,1f
	ld	hl,blank_square
1:	call	rc2sq
	call	draw_shape
	pop	af
	ret
getsq:	ld	a,(cur_row)
	ld	b,a
	ld	a,(cur_col)
	ld	c,a
	ld	hl,black
	ld	de,white
	push	bc
	call	getsquare
	pop	bc
	ret
	
; ==============================================================================
; write - display one character
; 
;   input:  A - character
;           (HL) - destination
;           (color) - color mask
; 
;   uses:   A, H, L
; 
	.text
	.globl	write
write:
	ld	(cursor),hl
	jp	prtout
	
; ==============================================================================
; writeln - display zero-terminated string
; 
;   input:  (HL) - string
;           (DE) - destination
;           (color) - color mask
; 
;   uses:   A, H, L
; 
	.text
	.globl	writeln
writeln:
	ex	de,hl
	ld	(cursor),hl
1:	ld	a,(de)
	or	a
	ret	z
	call	prtout
	inc	de
	jp	1b
	
; ==============================================================================
; clr_msg - clear the notification area
; 
;   uses:   all
; 
	.text
	.globl	clr_msg
clr_msg:
	ld	a,(msg)
	or	a
	ret	z
	ld	hl,MSGAREA
	ld	de,MSGAREA + 48 - (64 * 11)
	ld	b,10
	call	part_erase
	xor	a
	ld	(msg),a
	ret

	.lcomm	msg, 1
	
; ==============================================================================
; disp_msg - display message in the notification area
; 
;   input:  (HL) - string
;           (color) - color mask
; 
;   uses:   all
; 
	.text
	.globl	disp_msg
disp_msg:
	push	hl
	call	clr_msg
	pop	hl
	ld	de,MSGAREA
	call	writeln
	ld	a,1
	ld	(msg),a
	ret

; ==============================================================================
; get_conf - display prompt and wait for confirmation (Y/N)
; 
;   input:  (HL) - prompt
;           (color) - color mask
; 
;   output: NZ answer is YES
; 
;   uses:   A, B, D, E, H, L
; 
	.text
	.globl	get_conf
get_conf:
	call	disp_msg
1:	call	inklav
	cp	KEY_YES
	jp	z,1f
	cp	KEY_NO
	jp	z,2f
	jp	1b
1:	call	clr_msg
	or	0xff
	ret
2:	call	clr_msg
	xor	a
	ret
	
; ==============================================================================
; get_ack - display prompt and wait for acknowledgement (Enter)
; 
;   input:  (HL) - prompt
;           (color) - color mask
; 
;   uses:   A, B, D, E, H, L
; 
	.text
	.globl	get_ack
get_ack:
	call	disp_msg
1:	call	inklav
	cp	KEY_ENTER
	jp	nz,1b
	jp	clr_msg
	
; ==============================================================================
; Red LED on
; 
;   uses:   A
;
	.text
	.globl	redon
redon:
	ld	a,0x07
	out	(SYSPIO_CTRL),a
	ret
	
; ==============================================================================
; Red LED off
;
;   uses:   A
;
	.text
	.globl	redoff
redoff:
	ld	a,0x06
	out	(SYSPIO_CTRL),a
	ret
	
; ==============================================================================
; add_cust_glyphs - add custom glyphs
;
;   uses:   H, L
;
	.text
	.globl	add_cust_glyphs
add_cust_glyphs:	
	ld	hl,glyphs80 + 10
	ld	(tascii + 0x08),hl
	ret
	
; ==============================================================================
; Custom glyphs
;
	.data
glyphs80:
	.globl	COMP_ICON
	.equiv	COMP_ICON, 0x80
	; 80
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x3f	; ######
	.byte	0x21	; #....#
	.byte	0x21	; #....#
	.byte	0x21	; #....#
	.byte	0x3f	; ######
	.byte	0x0c	; ..##..
	.byte	0x3f	; ######
	.byte	0x00	; ......
	
	.globl	OBELUS
	.equiv	OBELUS, 0x81
	; 81
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x08	; ...#..
	.byte	0x00	; ......
	.byte	0x3e	; .#####
	.byte	0x00	; ......
	.byte	0x08	; ...#..
	.byte	0x00	; ......
	.byte	0x00	; ......

	.end
