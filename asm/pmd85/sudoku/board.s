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

	.include "sudoku.inc"
	
; ==============================================================================
; Constants
; 
	.globl	MSGAREA
	.equiv	ULC, 0xc4c6		; upper left corner of the board
	.equiv	MSGAREA, 0xffc0		; position of the notification area
	.equiv	MARKCOLOR, 0		; mark color mask
	
; ==============================================================================
; draw_board - draw board
; 
;   uses:   all
; 
	.text
	.globl	draw_board
draw_board:
	ld	hl,ULC
	call	7f
	call	7f
	ld	a,3
1:	push	af
	call	5f
	call	6f
	call	5f
	call	6f
	call	5f
	call	7f
	pop	af
	dec	a
	jp	nz,1b
7:	ld	(hl),0x3c		; solid line
	inc	hl
	ld	bc,(35 << 8) | 0x3f
1:	ld	(hl),c
	inc	hl
	dec	b
	jp	nz,1b
	ld	(hl),0x1f
2:	ld	de,28
	add	hl,de
	ret
5:	ld	a,5
1:	call	3f
	call	4f
	dec	a
	jp	nz,1b
	call	3f
	ret
3:	call	7f
	call	7f
7:	ld	(hl),0x0c		; odd line
	ld	de,12
	ld	bc,(2 << 8) | 0x08
1:	add	hl,de
	ld	(hl),0x08
	dec	b
	jp	nz,1b
	add	hl,de
	ld	(hl),0x18
	jp	2b
4:	ld	(hl),0x0c		; even line
	ld	de,4
	ld	bc,(8 << 8) | 0x08
	jp	1b
6:	ld	(hl),0x0c		; dotted line
	inc	hl
	ld	bc,(17 << 8) | 0x22
1:	ld	(hl),c
	inc	hl
	ld	(hl),0x08
	inc	hl
	dec	b
	jp	nz,1b
	ld	(hl),c
	inc	hl
	ld	(hl),0x18
	jp	2b
	
; ==============================================================================
; sq2rc - decompress square
; 
;   input:  C - square
; 
;   output: B - row
;	    C - column
; 
;   uses:   A
; 
	.text
	.globl	sq2rc
sq2rc:
	ld	a,c
	ld	bc,0xff09
1:	sub	c
	inc	b
	jp	nc,1b
	add	a,c
	ld	c,a
	ret
	
; ==============================================================================
; rc2sq - compress square
; 
;   input:  B - row
;	    C - column
; 
;   output: C - square
; 
;   uses:   A
; 
	.text
	.globl	rc2sq
rc2sq:
	ld	a,b
	add	a,a
	add	a,a
	add	a,a
	add	a,b
	add	a,c
	ld	c,a
	ret
	
; ==============================================================================
; draw_digit - draw digit
; 
;   input:  B - digit
;           C - square
;           E - color mask
;
;   uses:   all
; 
	.text
	.globl	draw_digit
draw_digit:
	push	de
	ld	a,b
	add	a,a
	add	a,a
	add	a,a
	ld	b,a
	add	a,a
	add	a,b
	ld	e,a
	ld	d,0
	ld	hl,digits
	add	hl,de
	ex	de,hl
	ld	hl,ULC + 514
	call	sq2a
	pop	bc
	ld	b,12
1:	ld	a,(de)
	xor	c
	ld	(hl),a
	inc	hl
	inc	de
	ld	a,(de)
	xor	c
	ld	(hl),a
	dec	b
	ret	z
	push	bc
	ld	bc,63
	add	hl,bc
	pop	bc
	inc	de
	jp	1b
sq2a:	call	sq2rc
	ld	a,b
	add	a,a
	ld	b,a
	add	a,a
	add	a,b
	ld	b,a
	ld	a,c
	add	a,a
	add	a,a
	ld	c,a
	add	hl,bc
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
	.word	0x0000	; ........
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
; 9
	.word	0x013e	; .######.
	.word	0x033f	; ########
	.word	0x0303	; ##....##
	.word	0x0303	; ##....##
	.word	0x0303	; ##....##
	.word	0x033f	; ########
	.word	0x033e	; .#######
	.word	0x0300	; ......##
	.word	0x0300	; ......##
	.word	0x0303	; ##....##
	.word	0x033f	; ########
	.word	0x013e	; .######.

; ==============================================================================
; draw_mark - draw exclamation mark
; 
;   input:  C - square
;           E - color mask
;
;   uses:   all
; 
	.text
	.globl	draw_mark
draw_mark:
	ld	hl,ULC + 833
	call	sq2a
	ld	c,e
	ld	de,mark
	ld	b,7
1:	ld	a,(de)
	xor	c
	ld	(hl),a
	dec	b
	ret	z
	push	bc
	ld	bc,64
	add	hl,bc
	pop	bc
	inc	de
	jp	1b
	
; ==============================================================================
; Exclamation mark
;
	.data
mark:	.byte	0x04	; ..#...
	.byte	0x04	; ..#...
	.byte	0x04	; ..#...
	.byte	0x04	; ..#...
	.byte	0x04	; ..#...
	.byte	0x00	; ......
	.byte	0x04	; ..#...
	
; ==============================================================================
; clr_mark - clear exclamation mark
; 
;   input:  C - square
;
;   uses:   all
; 
	.text
	.globl	clr_mark
clr_mark:
	ld	hl,ULC + 833
	call	sq2a
	ld	b,7
	ld	de,64
	xor	a
1:	ld	(hl),a
	dec	b
	ret	z
	add	hl,de
	jp	1b
	
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
	ld	hl,ULC + 193
	call	sq2a
	ld	(hl),0x0f
	dec	hl
	ld	bc,0x0520
	call	1f
	ld	de,-317
	add	hl,de
	ld	(hl),0x38
	inc	hl
	ld	a,(hl)
	or	0x03
	ld	(hl),a
	ld	de,64
	add	hl,de
	ld	bc,0x0402
	call	2f
	ld	de,700
	add	hl,de
	ld	bc,0x0420
	call	1f
	ld	a,(hl)
	or	c
	ld	(hl),a
	inc	hl
	ld	(hl),0x0f
	ld	de,-253
	add	hl,de
	ld	bc,0x0402
	call	1f
	ld	a,(hl)
	or	0x03
	ld	(hl),a
	dec	hl
	ld	(hl),0x38
	ret
1:	ld	de,64
2:	ld	a,(hl)
	or	c
	ld	(hl),a
	add	hl,de
	dec	b
	jp	nz,2b
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
	ld	hl,ULC + 193
	call	sq2a
	ld	(hl),0
	dec	hl
	ld	bc,0x05df
	call	1f
	ld	de,-317
	add	hl,de
	ld	(hl),0
	inc	hl
	ld	a,(hl)
	and	0xfc
	ld	(hl),a
	ld	de,64
	add	hl,de
	ld	bc,0x04fd
	call	2f
	ld	de,700
	add	hl,de
	ld	bc,0x04df
	call	1f
	ld	a,(hl)
	and	c
	ld	(hl),a
	inc	hl
	ld	(hl),0
	ld	de,-253
	add	hl,de
	ld	bc,0x04fd
	call	1f
	ld	a,(hl)
	and	0xfc
	ld	(hl),a
	dec	hl
	ld	(hl),0
	ret
1:	ld	de,64
2:	ld	a,(hl)
	and	c
	ld	(hl),a
	add	hl,de
	dec	b
	jp	nz,2b
	ret
	
; ==============================================================================
; disp_puzzle - display puzzle
; 
;   input:  (HL) - puzzle
;
;   uses:   all
; 
	.text
	.globl	disp_puzzle
disp_puzzle:
	ld	c,0
1:	ld	a,(hl)
	and	0x80
	ld	e,0
	jp	z,2f
	ld	e,0x40
2:	ld	a,(hl)
	inc	hl
	and	0x7f
	ld	b,a
	push	bc
	push	hl
	call	draw_digit
	pop	hl
	pop	bc
	inc	c
	ld	a,c
	cp	81
	jp	nz,1b
	ret	
	
; ==============================================================================
; disp_marks - display marks
; 
;   input:  (HL) - marks
;
;   uses:   all
; 
	.text
	.globl	disp_marks
disp_marks:
	ld	c,0
1:	ld	a,(hl)
	inc	hl
	or	a
	push	hl
	push	bc
	push	af
	call	z,clr_mark
	pop	af
	ld	e,MARKCOLOR
	call	nz,draw_mark
	pop	bc
	pop	hl
	inc	c
	ld	a,c
	cp	81
	jp	nz,1b
	ret
	
; ==============================================================================
; upd_marks - update and display marks
; 
;   input:  (HL) - old marks
;   	    (DE) - new marks
; 
;   output: (HL) - new marks
;
;   uses:   all
; 
	.text
	.globl	upd_marks
upd_marks:
	ld	c,0
1:	ld	a,(de)
	cp	(hl)
	jp	z,2f
	ld	a,(de)
	ld	(hl),a
	or	a
	push	hl
	push	de
	push	bc
	push	af
	call	z,clr_mark
	pop	af
	ld	e,MARKCOLOR
	call	nz,draw_mark
	pop	bc
	pop	de
	pop	hl
2:	inc	hl
	inc	de
	inc	c
	ld	a,c
	cp	81
	jp	nz,1b
	ret
	
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
	ld	a,(cur_row)
	ld	b,a
	ld	a,(cur_col)
	ld	c,a
	jp	rc2sq
	
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
	call	inklav_rnd128
	push	af
	call	clr_msg
	pop	af
	cp	KLEFT
	jp	nz,1f
	ld	a,(cur_col)
	dec	a
	jp	m,player_select
3:	push	af
	call	hide_cursor
	pop	af
	ld	(cur_col),a
2:	call	show_cursor
	jp	player_select
1:	cp	KRIGHT
	jp	nz,1f
	ld	a,(cur_col)
	inc	a
	cp	9
	jp	z,player_select
	jp	3b
1:	cp	KHOME
	jp	nz,1f
5:	ld	a,(cur_row)
	dec	a
	jp	m,player_select
3:	push	af
	call	hide_cursor
	pop	af
4:	ld	(cur_row),a
	call	show_cursor
	jp	player_select
1:	cp	KLLEFT
	jp	z,5b
	cp	KEND
	jp	nz,1f
5:	ld	a,(cur_row)
	inc	a
	cp	9
	jp	z,player_select
	jp	3b
1:	cp	KRRIGHT
	jp	z,5b
	push	af
	call	getcurp
	pop	af
	ret
	
; ==============================================================================
; Custom glyphs
;
	.data
	.globl	glyphs80
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

	.globl	TIMECOL
	.equiv	TIMECOL, 0x82
	; 82
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x00	; ......
	.byte	0x08	; ...#..
	.byte	0x00	; ......
	.byte	0x08	; ...#..
	.byte	0x00	; ......
	.byte	0x00	; ......
	
; ==============================================================================
; init_gmap - populate group map
; 
;   output: (gmap) - populated group map
; 
;   uses:   all
; 
	.text
	.globl	init_gmap
init_gmap:
	
; populate grp
	ld	hl,grp
	ld	c,0
1:	ld	(hl),c
	inc	hl
	inc	c
	ld	a,c
	cp	81
	jp	nz,1b
	ld	c,0
1:	ld	d,c
	call	sq2rc
	ld	a,b
	ld	b,c
	ld	c,a
	call	rc2sq
	ld	(hl),c
	inc	hl
	ld	c,d
	inc	c
	ld	a,c
	cp	81
	jp	nz,1b
	ex	de,hl
	ld	hl,boxes
	ld	b,81
	call	copy8

; process grp
	ld	hl,gmap
	ld	(pend),hl
	ld	b,0
4:	ld	hl,(pend)
	ld	(pbeg),hl
	ld	hl,grp
	ld	c,27
2:	push	hl
	ld	d,9
1:	ld	a,(hl)
	cp	b
	jp	z,1f
	inc	hl
	dec	d
	jp	nz,1b
3:	pop	hl
	ld	de,9
	add	hl,de
	dec	c
	jp	nz,2b
	inc	b
	ld	a,b
	cp	81
	jp	nz,4b
	ret
1:	pop	hl
	push	hl
	push	bc
	ld	c,9
2:	ld	a,(hl)
	inc	hl
	push	hl
	cp	b
	jp	z,1f
	ld	e,a
	ld	hl,(pbeg)
4:	ld	a,(pend)
	cp	l
	jp	z,5f
	ld	a,(hl)
	cp	e
	jp	z,1f
	inc	hl
	jp	4b
5:	ld	(hl),e
	inc	hl
	ld	(pend),hl
1:	pop	hl
	dec	c
	jp	nz,2b
	pop	bc
	jp	3b
	
	
	.lcomm	grp, 81 * 3
	.lcomm	gmap, 81 * 20
	.lcomm	pbeg, 2
	.lcomm	pend, 2
	
; ==============================================================================
; Boxes
; 
	.data
boxes:	.byte	0x00, 0x01, 0x02, 0x09, 0x0a, 0x0b, 0x12, 0x13, 0x14
	.byte	0x03, 0x04, 0x05, 0x0c, 0x0d, 0x0e, 0x15, 0x16, 0x17
	.byte	0x06, 0x07, 0x08, 0x0f, 0x10, 0x11, 0x18, 0x19, 0x1a
	.byte	0x1b, 0x1c, 0x1d, 0x24, 0x25, 0x26, 0x2d, 0x2e, 0x2f
	.byte	0x1e, 0x1f, 0x20, 0x27, 0x28, 0x29, 0x30, 0x31, 0x32
	.byte	0x21, 0x22, 0x23, 0x2a, 0x2b, 0x2c, 0x33, 0x34, 0x35
	.byte	0x36, 0x37, 0x38, 0x3f, 0x40, 0x41, 0x48, 0x49, 0x4a
	.byte	0x39, 0x3a, 0x3b, 0x42, 0x43, 0x44, 0x4b, 0x4c, 0x4d
	.byte	0x3c, 0x3d, 0x3e, 0x45, 0x46, 0x47, 0x4e, 0x4f, 0x50
	
; ==============================================================================
; get_dups - get map of duplicate squares
; 
;   input:  (HL) - puzzle
;           (DE) - destination for map of duplicates
; 
;   output: A - number of duplicates
;   	    NZ if any duplicates
;   	    (DE) - map of duplicates
; 
;   uses:   all
; 
	.text
	.globl	get_dups
get_dups:
	xor	a
	ld	(ndup),a
	ex	de,hl
	ld	b,81
	push	hl
	call	zerofill8
	pop	hl
	ex	de,hl
	ld	(tpuzzle),hl
	ld	b,0
3:	ld	a,(hl)
	or	a
	jp	z,4f
	ld	(tdup),a
	push	hl
	push	de
	ld	a,b
	add	a,a
	ld	l,a
	ld	h,0
	add	hl,hl
	ld	d,h
	ld	e,l
	add	hl,hl
	add	hl,hl
	add	hl,de
	ld	de,gmap
	add	hl,de
	ld	c,20
2:	ld	e,(hl)
	ld	d,0
	push	hl
	ld	hl,(tpuzzle)
	add	hl,de
	ld	a,(tdup)
	xor	(hl)
	and	0x7f
	pop	hl
	jp	z,2f
	inc	hl
	dec	c
	jp	nz,2b
	pop	de
	jp	1f
2:	pop	de
	ld	a,0xff
	ld	(de),a
	ld	hl,ndup
	inc	(hl)
1:	pop	hl
4:	inc	hl
	inc	de
	inc	b
	ld	a,b
	cp	81
	jp	nz,3b
	ld	a,(ndup)
	or	a
	ret

	.lcomm	tdup, 1
	.lcomm	tpuzzle, 2
	.lcomm	ndup, 1
	
; ==============================================================================
; check_puzzle - check if all squares are filled in
; 
;   input:  (HL) - puzzle
; 
;   output: NZ if all squares are filled in
; 
;   uses:   A, B, H, L
; 
	.text
	.globl	check_puzzle
check_puzzle:
	ld	b,81
1:	ld	a,(hl)
	inc	hl
	or	a
	ret	z
	dec	b
	jp	nz,1b
	inc	b
	ret
	
	.end
