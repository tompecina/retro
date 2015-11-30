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
	
	.equiv	ULC, 0xc014		; upper left corner of the board
	.equiv	MSGAREA, 0xffc0		; position of the notification area
	.equiv	ATTEMPTS, 10		; maximum number of attempts
	.equiv	POSITIONS, 5		; number of positions
	
; ==============================================================================
; draw_board - draw board
; 
;   uses:   all
; 
	.text
	.globl	draw_board
draw_board:
	ld	b,ATTEMPTS
2:	ld	c,POSITIONS
1:	xor	a
	push	bc
	call	draw_digit
	pop	bc
	xor	a
	push	bc
	call	draw_pin
	pop	bc
	dec	c
	jp	nz,1b
	dec	b
	jp	nz,2b
	ret
	
	
;; 	ld	hl,ULC
;; 	call	7f
;; 	call	7f
;; 	ld	a,3
;; 1:	push	af
;; 	call	5f
;; 	call	6f
;; 	call	5f
;; 	call	6f
;; 	call	5f
;; 	call	7f
;; 	pop	af
;; 	dec	a
;; 	jp	nz,1b
;; 7:	ld	(hl),0x3c		; solid line
;; 	inc	hl
;; 	ld	bc,(35 << 8) | 0x3f
;; 1:	ld	(hl),c
;; 	inc	hl
;; 	dec	b
;; 	jp	nz,1b
;; 	ld	(hl),0x1f
;; 2:	ld	de,28
;; 	add	hl,de
;; 	ret
;; 5:	ld	a,5
;; 1:	call	3f
;; 	call	4f
;; 	dec	a
;; 	jp	nz,1b
;; 	call	3f
;; 	ret
;; 3:	call	7f
;; 	call	7f
;; 7:	ld	(hl),0x0c		; odd line
;; 	ld	de,12
;; 	ld	bc,(2 << 8) | 0x08
;; 1:	add	hl,de
;; 	ld	(hl),0x08
;; 	dec	b
;; 	jp	nz,1b
;; 	add	hl,de
;; 	ld	(hl),0x18
;; 	jp	2b
;; 4:	ld	(hl),0x0c		; even line
;; 	ld	de,4
;; 	ld	bc,(8 << 8) | 0x08
;; 	jp	1b
;; 6:	ld	(hl),0x0c		; dotted line
;; 	inc	hl
;; 	ld	bc,(17 << 8) | 0x22
;; 1:	ld	(hl),c
;; 	inc	hl
;; 	ld	(hl),0x08
;; 	inc	hl
;; 	dec	b
;; 	jp	nz,1b
;; 	ld	(hl),c
;; 	inc	hl
;; 	ld	(hl),0x18
;; 	jp	2b
	
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
	ld	hl,1280
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
	.word	0x0048	; ...*....
	.word	0x0000	; ........
	.word	0x0000	; ........
	.word	0x0000	; ........
	.word	0x0000	; ........
	.word	0x0000	; ........
; 1
	.word	0x0018	; ...##...
	.word	0x001c	; ..###...
	.word	0x001e	; .####...
	.word	0x0018	; ...##...
	.word	0x0018	; ...##...
	.word	0x0018	; ...##...
	.word	0x0018	; ...##...
	.word	0x0018	; ...##...
	.word	0x0018	; ...##...
	.word	0x0018	; ...##...
	.word	0x0018	; ...##...
	.word	0x0018	; ...##...
; 2
	.word	0x013e	; .######.
	.word	0x033f	; ########
	.word	0x0303	; ##....##
	.word	0x0300	; ......##
	.word	0x0320	; .....###
	.word	0x0130	; ....###.
	.word	0x0038	; ...###..
	.word	0x001c	; ..###...
	.word	0x000e	; .###....
	.word	0x0007	; ###.....
	.word	0x033f	; ########
	.word	0x033f	; ########
; 3
	.word	0x013e	; .######.
	.word	0x033f	; ########
	.word	0x0303	; ##....##
	.word	0x0300	; ......##
	.word	0x0300	; ......##
	.word	0x0130	; ....###.
	.word	0x0130	; ....###.
	.word	0x0300	; ......##
	.word	0x0300	; ......##
	.word	0x0303	; ##....##
	.word	0x033f	; ########
	.word	0x013e	; .######.
; 4
	.word	0x0120	; .....##.
	.word	0x0130	; ....###.
	.word	0x0138	; ...####.
	.word	0x013c	; ..#####.
	.word	0x012e	; .###.##.
	.word	0x0127	; ###..##.
	.word	0x0123	; ##...##.
	.word	0x033f	; ########
	.word	0x033f	; ########
	.word	0x0120	; .....##.
	.word	0x0120	; .....##.
	.word	0x0120	; .....##.
; 5
	.word	0x033f	; ########
	.word	0x033f	; ########
	.word	0x0003	; ##......
	.word	0x0003	; ##......
	.word	0x0003	; ##......
	.word	0x013f	; #######.
	.word	0x033f	; ########
	.word	0x0300	; ......##
	.word	0x0300	; ......##
	.word	0x0303	; ##....##
	.word	0x033f	; ########
	.word	0x013e	; .######.
; 6
	.word	0x013e	; .######.
	.word	0x033f	; ########
	.word	0x0303	; ##....##
	.word	0x0003	; ##......
	.word	0x0003	; ##......
	.word	0x013f	; #######.
	.word	0x033f	; ########
	.word	0x0303	; ##....##
	.word	0x0303	; ##....##
	.word	0x0303	; ##....##
	.word	0x033f	; ########
	.word	0x013e	; .######.
; 7
	.word	0x033f	; ########
	.word	0x033f	; ########
	.word	0x0300	; ......##
	.word	0x0320	; .....###
	.word	0x0130	; ....###.
	.word	0x0038	; ...###..
	.word	0x001c	; ..###...
	.word	0x000c	; ..##...
	.word	0x000c	; ..##...
	.word	0x000c	; ..##...
	.word	0x000c	; ..##...
	.word	0x000c	; ..##...
; 8
	.word	0x013e	; .######.
	.word	0x033f	; ########
	.word	0x0303	; ##....##
	.word	0x0303	; ##....##
	.word	0x0303	; ##....##
	.word	0x013e	; .######.
	.word	0x013e	; .######.
	.word	0x0303	; ##....##
	.word	0x0303	; ##....##
	.word	0x0303	; ##....##
	.word	0x033f	; ########
	.word	0x013e	; .######.

; ==============================================================================
; draw_pin - draw pin
; 
;   input:  A - color (0 = blank, 1 = black, 2 = white)
;           B - line
;           C - position
;
;   uses:   all
; 
	.text
	.globl	draw_pin
draw_pin:
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
	ld	hl,pins
	add	hl,bc
	ex	de,hl
	ld	a,7
	jp	ddig
	
; ==============================================================================
; Pins
;
	.data
pins:	

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
;   input:  C - square
;
;   uses:   all
; 
	.text
	.globl	draw_cursor
draw_cursor:
;; 	ld	hl,ULC + 193
;; 	call	sq2a
;; 	ld	(hl),0x0f
;; 	dec	hl
;; 	ld	bc,0x0520
;; 	call	1f
;; 	ld	de,-317
;; 	add	hl,de
;; 	ld	(hl),0x38
;; 	inc	hl
;; 	ld	a,(hl)
;; 	or	0x03
;; 	ld	(hl),a
;; 	ld	de,64
;; 	add	hl,de
;; 	ld	bc,0x0402
;; 	call	2f
;; 	ld	de,700
;; 	add	hl,de
;; 	ld	bc,0x0420
;; 	call	1f
;; 	ld	a,(hl)
;; 	or	c
;; 	ld	(hl),a
;; 	inc	hl
;; 	ld	(hl),0x0f
;; 	ld	de,-253
;; 	add	hl,de
;; 	ld	bc,0x0402
;; 	call	1f
;; 	ld	a,(hl)
;; 	or	0x03
;; 	ld	(hl),a
;; 	dec	hl
;; 	ld	(hl),0x38
;; 	ret
;; 1:	ld	de,64
;; 2:	ld	a,(hl)
;; 	or	c
;; 	ld	(hl),a
;; 	add	hl,de
;; 	dec	b
;; 	jp	nz,2b
;; 	ret
	
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
;; 	ld	hl,ULC + 193
;; 	call	sq2a
;; 	ld	(hl),0
;; 	dec	hl
;; 	ld	bc,0x05df
;; 	call	1f
;; 	ld	de,-317
;; 	add	hl,de
;; 	ld	(hl),0
;; 	inc	hl
;; 	ld	a,(hl)
;; 	and	0xfc
;; 	ld	(hl),a
;; 	ld	de,64
;; 	add	hl,de
;; 	ld	bc,0x04fd
;; 	call	2f
;; 	ld	de,700
;; 	add	hl,de
;; 	ld	bc,0x04df
;; 	call	1f
;; 	ld	a,(hl)
;; 	and	c
;; 	ld	(hl),a
;; 	inc	hl
;; 	ld	(hl),0
;; 	ld	de,-253
;; 	add	hl,de
;; 	ld	bc,0x04fd
;; 	call	1f
;; 	ld	a,(hl)
;; 	and	0xfc
;; 	ld	(hl),a
;; 	dec	hl
;; 	ld	(hl),0
;; 	ret
;; 1:	ld	de,64
;; 2:	ld	a,(hl)
;; 	and	c
;; 	ld	(hl),a
;; 	add	hl,de
;; 	dec	b
;; 	jp	nz,2b
;; 	ret
	
; ==============================================================================
; init_cursor - initialize cursor
; 
;   uses:   all
; 
	.text
	.globl	init_cursor
init_cursor:
	xor	a
	ld	(cur_row),a
	ld	(cur_col),a
	ld	c,a
	jp	draw_cursor
	
	.lcomm	cur_row, 1
	.lcomm	cur_col, 1
	
; ==============================================================================
; getcurp - get cursor position
; 
;   output: C - column
; 
;   uses:   A
; 
	.text
	.globl	getcurp
getcurp:
	;; ld	a,(cur_row)
	;; ld	b,a
	;; ld	a,(cur_col)
	;; ld	c,a
	;; jp	rc2sq
	
; ==============================================================================
; show_cursor - show cursor
; 
;   uses:   all
; 
	.text
	.globl	show_cursor
show_cursor:
	call	getcurp
	jp	draw_cursor
	
; ==============================================================================
; hide_cursor - hide cursor
; 
;   uses:   all
; 
	.text
	.globl	hide_cursor
hide_cursor:
	call	getcurp
	jp	clr_cursor
	
; ==============================================================================
; player_select - let player select square
; 
;   input:  (cur_row) - cursor row
;           (cur_col) - cursor column
; 
;   output: C - square selected
;           (cur_row) - new cursor row
;           (cur_col) - new cursor column
;           A - scan code of the key 
; 
;   uses:   all
; 
	.text
	.globl	player_select
player_select:
	
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
;   output: Z answer is YES
; 
;   uses:   A, B, D, E, H, L
; 
	.text
	.globl	get_conf
get_conf:
	call	disp_msg
1:	call	inklav_rnd
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

	.globl	ONEDOT
	.equiv	ONEDOT, 0x81
	; 81
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x04	; ..#...
	.byte	0x06	; .##...
	.byte	0x04	; ..#...
	.byte	0x04	; ..#...
	.byte	0x04	; ..#...
	.byte	0x04	; ..#...
	.byte	0x2e	; .###.#
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
