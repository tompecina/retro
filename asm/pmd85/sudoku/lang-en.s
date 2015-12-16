; lang-en.s
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


; English language support.

	.include "sudoku.inc"

; ==============================================================================
; Constants
; 
	
; ==============================================================================
; Control keys
;
	.globl	KEY_YES, KEY_NO, KEY_ENTER
	.equiv	KEY_YES, 'Y'
	.equiv	KEY_NO, 'N'
	.equiv	KEY_ENTER, KEOL

	.globl	KEY_NEW, KEY_QUIT
	.equiv	KEY_NEW, 'N'
	.equiv	KEY_QUIT, 'Q'
	
; ==============================================================================
; Labels
;
	.data
	.globl	lbl_sudoku
lbl_sudoku:
	db	"SUDOKU ", ONEDOT, "0", 0
	
; ==============================================================================
; Prompts
;
	.data
	.globl	msg_hwerr, msg_select, msg_start, msg_done, ph_tmr, msg_quit
	.globl	msg_new, msg_sqerr
msg_hwerr:
	.asciz	"Hardware error, please press EOL to quit"
msg_select:
	db	"          Select difficulty level (0", OBELUS, "3)", 0
msg_start:
	db	"     Controls: arrows, 1", OBELUS, "9, DEL, N-ew, Q-uit", 0
msg_done:
	.ascii	"        Congratulations! Your time: "
ph_tmr:
	.skip	8
msg_quit:
	.asciz	"  Do you really wish to end the program? (Y/N)"
msg_new:	
	.asciz	"Do you really wish to start a new puzzle? (Y/N)"
msg_sqerr:
	.asciz	"          This square cannot be edited"

	.end
	
