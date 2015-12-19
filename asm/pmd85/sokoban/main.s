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


; The game of Sokoban for Tesla PMD 85.

	.include "sokoban.inc"

; ==============================================================================
; Constants
;

; ==============================================================================
; Main entry point of the program
;
	.text
	.globl	main
main:

; initialize
	di
	ld	sp,initsp


	ld	hl,0
1:	push	hl
	ld	de,0x7000
	call	conv_int
	pop	hl
	inc	hl
	jp	1b




	
	call	init_levels	; must be called before using .bss variables
	call	init_kbd
	call	set_kmap
	call	init_video
	call	add_cust_glyphs
	call	count_levels
	ld	(nlevels),hl
	call	init_hist	

	call	erase

	ld	hl,label_sokoban
	ld	de,LBLPOS
	call	draw_label

	ld	hl,credits
	ld	de,CRPOS
	call	writeln

	ld	hl,legend
	ld	de,LEGPOS
	call	writeln

	ld	hl,msg_menu
	call	disp_msg

	jp	.
	
	
1:	ld	bc,0
2:	push	bc
	call	get_level
	call	nc,draw_board
	pop	bc
	inc	bc
	ld	a,c
	cp	60
	jp	nz,2b	
	jp	1b
	
	.lcomm	nlevels, 2
	
	.end
