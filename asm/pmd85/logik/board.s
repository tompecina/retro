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

	.include "logik.inc"
	
; ==============================================================================
; Constants
	
	.equiv	ULC, 0xc216		; upper left corner of the board
	.equiv	MSGAREA, 0xffc0		; position of the notification area
	
; ==============================================================================
; draw_board - draw board
; 
;   uses:   all
; 
	.text
	.globl	draw_board
draw_board:
	ld	b,0
2:	ld	c,0
1:	xor	a
	push	bc
	call	draw_digit
	pop	bc
	xor	a
	push	bc
	call	draw_peg
	pop	bc
	inc	c
	ld	a,c
	cp	POSITIONS
	jp	nz,1b
	inc	b
	ld	a,b
	cp	ATTEMPTS
	jp	nz,2b
	ret
	
; ==============================================================================
; draw_digit - draw digit
; 
;   input:  A - digit
;           B - line
;           C - position
;
;   uses:   all
; 
	.text
	.globl	draw_digit
draw_digit:
	push	af
	call	getln
	ld	a,c
	add	a,a
	add	a,c
	ld	e,a
	ld	d,0
	add	hl,de
	ld	de,ULC
	add	hl,de
	ex	de,hl
	pop	af
	add	a,a
	add	a,a
	add	a,a
	ld	c,a
	add	a,a
	add	a,c
	ld	c,a
	ld	b,0
	ld	hl,digits
	add	hl,bc
	ex	de,hl
	ld	bc,63
	ld	a,12
ddig:	ld	bc,63
1:	push	af
	ld	a,(de)
	inc	de
	ld	(hl),a
	inc	hl
	ld	a,(de)
	inc	de
	ld	(hl),a
	add	hl,bc
	pop	af
	dec	a
	jp	nz,1b
	ret
getln:	push	bc
	ld	d,0
	ld	e,b
	ld	hl,1472
	call	mul16
	pop	bc
	ret
	
; ==============================================================================
; Digits
;
	.data
digits:	

; blank
	.word	0x0000	; ........
	.word	0x0000	; ........
	.word	0x0000	; ........
	.word	0x0000	; ........
	.word	0x0000	; ........
	.word	0x0000	; ........
	.word	0x0060	; ...*....
	.word	0x0000	; ........
	.word	0x0000	; ........
	.word	0x0000	; ........
	.word	0x0000	; ........
	.word	0x0000	; ........
; 1
	.word	0x0120	; ...##...
	.word	0x0130	; ..###...
	.word	0x0138	; .####...
	.word	0x0120	; ...##...
	.word	0x0120	; ...##...
	.word	0x0120	; ...##...
	.word	0x0120	; ...##...
	.word	0x0120	; ...##...
	.word	0x0120	; ...##...
	.word	0x0120	; ...##...
	.word	0x0120	; ...##...
	.word	0x0120	; ...##...
; 2
	.word	0x0738	; .######.
	.word	0x0f3c	; ########
	.word	0x0c0c	; ##....##
	.word	0x0c00	; ......##
	.word	0x0e00	; .....###
	.word	0x0700	; ....###.
	.word	0x0320	; ...###..
	.word	0x0130	; ..###...
	.word	0x0038	; .###....
	.word	0x001c	; ###.....
	.word	0x0f3c	; ########
	.word	0x0f3c	; ########
; 3
	.word	0x0738	; .######.
	.word	0x0f3c	; ########
	.word	0x0c0c	; ##....##
	.word	0x0c00	; ......##
	.word	0x0c00	; ......##
	.word	0x0700	; ....###.
	.word	0x0700	; ....###.
	.word	0x0c00	; ......##
	.word	0x0c00	; ......##
	.word	0x0c0c	; ##....##
	.word	0x0f3c	; ########
	.word	0x0738	; .######.
; 4
	.word	0x0600	; .....##.
	.word	0x0700	; ....###.
	.word	0x0720	; ...####.
	.word	0x0730	; ..#####.
	.word	0x0638	; .###.##.
	.word	0x061c	; ###..##.
	.word	0x060c	; ##...##.
	.word	0x0f3c	; ########
	.word	0x0f3c	; ########
	.word	0x0600	; .....##.
	.word	0x0600	; .....##.
	.word	0x0600	; .....##.
; 5
	.word	0x0f3c	; ########
	.word	0x0f3c	; ########
	.word	0x000c	; ##......
	.word	0x000c	; ##......
	.word	0x000c	; ##......
	.word	0x073c	; #######.
	.word	0x0f3c	; ########
	.word	0x0c00	; ......##
	.word	0x0c00	; ......##
	.word	0x0c0c	; ##....##
	.word	0x0f3c	; ########
	.word	0x0738	; .######.
; 6
	.word	0x0738	; .######.
	.word	0x0f3c	; ########
	.word	0x0c0c	; ##....##
	.word	0x000c	; ##......
	.word	0x000c	; ##......
	.word	0x073c	; #######.
	.word	0x0f3c	; ########
	.word	0x0c0c	; ##....##
	.word	0x0c0c	; ##....##
	.word	0x0c0c	; ##....##
	.word	0x0f3c	; ########
	.word	0x0738	; .######.
; 7
	.word	0x0f3c	; ########
	.word	0x0f3c	; ########
	.word	0x0c00	; ......##
	.word	0x0e00	; .....###
	.word	0x0700	; ....###.
	.word	0x0320	; ...###..
	.word	0x0130	; ..###...
	.word	0x0030	; ..##...
	.word	0x0030	; ..##...
	.word	0x0030	; ..##...
	.word	0x0030	; ..##...
	.word	0x0030	; ..##...
; 8
	.word	0x0738	; .######.
	.word	0x0f3c	; ########
	.word	0x0c0c	; ##....##
	.word	0x0c0c	; ##....##
	.word	0x0c0c	; ##....##
	.word	0x0738	; .######.
	.word	0x0738	; .######.
	.word	0x0c0c	; ##....##
	.word	0x0c0c	; ##....##
	.word	0x0c0c	; ##....##
	.word	0x0f3c	; ########
	.word	0x0738	; .######.

; ==============================================================================
; draw_peg - draw one peg
; 
;   input:  A - color (0 = blank, 1 = black, 2 = white)
;           B - line
;           C - position
;
;   uses:   all
; 
	.text
	.globl	draw_peg
draw_peg:
	push	af
	call	getln
	ld	a,c
	add	a,a
	ld	e,a
	ld	d,0
	add	hl,de
	ld	de,ULC + 208
	add	hl,de
	ex	de,hl
	pop	af
	add	a,a
	ld	c,a
	add	a,a
	add	a,a
	add	a,a
	sub	c
	ld	c,a
	ld	b,0
	ld	hl,pegs
	add	hl,bc
	ex	de,hl
	ld	a,7
	jp	ddig
	
; ==============================================================================
; draw_pegs - draw line of pegs
; 
;   input:  A - (black_pegs << 3) | white_pegs
;           B - line
;
;   uses:   A, C, D, H, L
; 
	.text
	.globl	draw_pegs
draw_pegs:
	push	af
	rra
	rra
	rra
	ld	c,0
	ld	d,1
	call	1f
	pop	af
	ld	d,2
1:	and	0x07
1:	dec	a
	ret	m
	push	af
	push	bc
	push	de
	ld	a,d
	call	draw_peg
	pop	de
	pop	bc
	pop	af
	inc	c
	jp	1b
	
	
; ==============================================================================
; Pegs
;
	.data
pegs:	

; blank
	.word	0x0000	; .....,.
	.word	0x0000	; .....,.
	.word	0x0000	; .....,.
	.word	0x0048	; ...*.,.
	.word	0x0000	; .....,.
	.word	0x0000	; .....,.
	.word	0x0000	; .....,.
; black
	.word	0x0008	; ...#.,.
	.word	0x0014	; ..#.#..
	.word	0x0022	; .#...#.
	.word	0x0101	; #.....#
	.word	0x0022	; .#...#.
	.word	0x0014	; ..#.#..
	.word	0x0008	; ...#.,.
; white
	.word	0x0008	; ...#.,.
	.word	0x001c	; ..###..
	.word	0x003e	; .#####.
	.word	0x013f	; #######
	.word	0x003e	; .#####.
	.word	0x001c	; ..###..
	.word	0x0008	; ...#.,.
	
; ==============================================================================
; draw_cursor - draw cursor
; 
;   input:  B - line
;           C - position
;
;   uses:   all
; 
	.text
	.globl	draw_cursor
draw_cursor:
	call	getln
	ld	a,c
	add	a,a
	add	a,c
	ld	e,a
	ld	d,0
	add	hl,de
	ld	de,ULC + 831
	add	hl,de
	ld	de,61
	ld	b,3
1:	ld	a,(hl)
	or	0x20
	ld	(hl),a
	inc	hl
	inc	hl
	inc	hl
	ld	a,(hl)
	or	0x01
	ld	(hl),a
	add	hl,de
	dec	b
	jp	nz,1b
	ld	de,-63
	add	hl,de
	ld	(hl),0x3f
	inc	hl
	ld	(hl),0x3f
	ret
	
; ==============================================================================
; clr_cursor - clear cursor
; 
;   input:  C - square
;
;   uses:   all
; 
	.text
	.globl	clr_cursor
clr_cursor:
	call	getln
	ld	a,c
	add	a,a
	add	a,c
	ld	e,a
	ld	d,0
	add	hl,de
	ld	de,ULC + 831
	add	hl,de
	ld	de,61
	ld	b,3
1:	ld	a,(hl)
	and	0x1f
	ld	(hl),a
	inc	hl
	inc	hl
	inc	hl
	ld	a,(hl)
	and	0x3e
	ld	(hl),a
	add	hl,de
	dec	b
	jp	nz,1b
	ld	de,-63
	add	hl,de
	ld	(hl),b
	inc	hl
	ld	(hl),b
	ret
	
; ==============================================================================
; player_select - let player enter guess
; 
;   input:  B - line
; 
;   output: HL - player's guess
; 
;   uses:   all
; 
	.text
	.globl	player_select
player_select:
	push	bc
	ld	hl,msg_yguess
	call	disp_msg
	pop	bc
5:	ld	hl,guess
	push	hl
	ld	c,POSITIONS
1:	ld	(hl),0xff
	inc	hl
	dec	c
	jp	nz,1b
	push	bc
	call	draw_cursor
	pop	bc
	pop	hl
2:	push	hl
	push	bc	
	call	inklav_rnd
	push	af
	call	clr_msg
	pop	af
	pop	bc
	pop	hl
	cp	KEY_ENTER
	jp	nz,1f
	push	bc
	ld	de,guess
	ld	c,POSITIONS
3:	ld	a,(de)
	inc	de
	or	a
	jp	p,4f
	push	hl
	call	errbeep
	pop	hl
	pop	bc
	jp	2b
4:	dec	c
	jp	nz,3b
	pop	bc
	call	clr_cursor
	ld	de,guess
	ld	b,POSITIONS
	ld	l,0
4:	add	hl,hl
	add	hl,hl
	add	hl,hl
	ld	a,(de)
	inc	de
	or	l
	ld	l,a
	dec	b
	jp	nz,4b
	ret
1:	cp	KLEFT
	jp	nz,1f
	ld	a,c
	or	a
	jp	z,2b
	dec	hl
	push	hl
	push	bc
	call	clr_cursor
	pop	bc
	dec	c
3:	push	bc
	call	draw_cursor
	pop	bc
	pop	hl
	jp	2b
1:	cp	KRIGHT
	jp	nz,1f
4:	ld	a,c
	cp	POSITIONS - 1
	jp	z,2b
	inc	hl
	push	hl
	push	bc
	call	clr_cursor
	pop	bc
	inc	c
	jp	3b
1:	cp	KDEL
	jp	nz,1f
	ld	(hl),0xff
	push	hl
	push	bc
	xor	a
	call	draw_digit
	pop	bc
	pop	hl
	jp	2b
1:	cp	KCLR
	jp	nz,1f
	push	bc
	call	clr_cursor
	pop	bc
	ld	c,0
3:	push	bc
	xor	a
	call	draw_digit
	pop	bc
	inc	c
	ld	a,c
	cp	POSITIONS
	jp	nz,3b
	jp	5b
1:	sub	'1'
	jp	c,2b
	cp	COLORS
	jp	nc,2b
	ld	(hl),a
	push	hl
	push	bc
	inc	a
	call	draw_digit
	pop	bc
	pop	hl
	jp	4b
	
	.lcomm	guess, POSITIONS

; ==============================================================================
; player_score - let player enter score
; 
;   output: A - score entered
; 
;   uses:   all
; 
	.text
	.globl	player_score
player_score:
	ld	hl, msg_bpegs
	call	disp_msg
	ld	hl,inklav_rnd
	ld	(ikf),hl
	ld	hl,valp
	ld	(vf),hl
	ld	bc,0x0101
	ld	hl,kbdbuffer
	push	hl
	push	bc
	call	sedit
	ld	a,(kbdbuffer)
	sub	'0'
	ld	(nblack),a
	ld	de,msg_wpegs
	call	writelncur
	pop	bc
	pop	hl
	call	sedit
	ld	a,(kbdbuffer)
	sub	'0'
	ld	(nwhite),a
	ld	de,msg_confirm
	call	writelncur
	call	get_conf2
	jp	z,player_score
	ld	a,(nblack)
	ld	b,a
	ld	a,(nwhite)
	ld	c,a
	add	a,b
	cp	POSITIONS + 1
	jp	nc,1f
	ld	a,b
	add	a,a
	add	a,a
	add	a,a
	or	c
	cp	((POSITIONS - 1) << 3) | 1
	jp	z,1f
	push	af
	call	clr_msg
	pop	af
	ret
1:	ld	hl,msg_errpegs
	call	get_ack
	jp	player_score
valp:	cp	'0'
	ret	c
	cp	'0' + POSITIONS + 1
	ccf
	ret

	.globl	kbdbuffer
	.lcomm	kbdbuffer, 2
	.lcomm	nblack, 1
	.lcomm	nwhite, 1
	
; ==============================================================================
; disp_guess - display computer's guess
; 
;   input:  B - line
;	    HL - computer's guess
; 
;   uses:   all
; 
	.text
	.globl	disp_guess
disp_guess:
	ld	c,0
1:	ld	a,h
	rra
	rra
	rra
	rra
	and	0x07
	inc	a
	add	hl,hl
	add	hl,hl
	add	hl,hl
	push	hl
	push	bc
	call	draw_digit
	pop	bc
	pop	hl
	inc	c
	ld	a,c
	cp	POSITIONS
	jp	nz,1b
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
; writeln/writelncur - display zero-terminated string
; 
;   input:  (HL) - string (only writeln)
;           (DE) - string (only writelncur)
;           (DE) - destination (only writeln)
;           (cursor) - destination (only writelncur)
;           (color) - color mask
; 
;   uses:   A, H, L
; 
	.text
	.globl	writeln, writelncur
writeln:
	ex	de,hl
	ld	(cursor),hl
writelncur:
	ld	a,(de)
	or	a
	ret	z
	call	prtout
	inc	de
	jp	writelncur
	
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
; get_conf,getconf_2 - optionally display prompt and wait for confirmation (Y/N)
; 
;   input:  (HL) - prompt (only get_conf)
;           (color) - color mask
; 
;   output: NZ answer is YES
; 
;   uses:   A, B, D, E, H, L
; 
	.text
	.globl	get_conf, get_conf2
get_conf:
	call	disp_msg
get_conf2:
	call	inklav_rnd
	cp	KEY_YES
	jp	z,1f
	cp	KEY_NO
	jp	z,2f
	jp	get_conf2
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
1:	call	inklav_rnd
	cp	KEY_ENTER
	jp	nz,1b
	jp	clr_msg
	
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
	.globl	OBELUS
	.equiv	OBELUS, 0x80
	; 80
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

	.globl	LEFT1
	.equiv	LEFT1, 0x81
	; 81
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x08	; ...#..
	.byte	0x0c	; ..##..
	.byte	0x3e	; .#####
	.byte	0x0c	; ..##..
	.byte	0x08	; ...#..
	.byte	0x00	; ......
	.byte	0x00	; ......

	.globl	LEFT2
	.equiv	LEFT2, 0x82
	; 82
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x3f	; ######
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......

	.globl	RIGHT1
	.equiv	RIGHT1, 0x83
	; 83
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x3e	; .#####
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......

	.globl	RIGHT2
	.equiv	RIGHT2, 0x84
	; 84
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x08	; ...#..
	.byte	0x18	; ...##.
	.byte	0x3f	; ######
	.byte	0x18	; ...##.
	.byte	0x08	; ...#..
	.byte	0x00	; ......
	.byte	0x00	; ......

; ==============================================================================
; errbeep - Error beep
; 
;   uses:   A, B, D, H, L
;
	.text
	.globl	errbeep
errbeep:
	ld	hl,erbdt
	jp	bell

	.data
erbdt:	.byte	2, 8, 0, 8, 2, 8, 0xff
	
	.end
