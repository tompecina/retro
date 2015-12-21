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

	.include "sokoban.inc"

; ==============================================================================
; Constants
;
	.globl	LBLPOS, CRPOS, LEGPOS, PRPOS, STPOS, LVPOS, MVPOS, PUPOS
	.equiv	LBLPOS, 0xc510
	.equiv	CRPOS, 0xcc10
	.equiv	LEGPOS, 0xd400
	.equiv	STPOS, 0xc300
	.equiv	LVPOS, 0xc307
	.equiv	MVPOS, 0xc318
	.equiv	PUPOS, 0xc32b
	
; ==============================================================================
; Credits
;
	.data
	.globl	credits
credits:
	db	"by Tom", 0xc1, 0xd3, " Pecina", 0

; ==============================================================================
; Legend
;
	.data
	.globl	legend
legend:
	db	"MAIN MENU:", CR, VT, VT, VT
	db	" P - play from first level", CR
	db	" S - play from selected level", CR
	db	" L - load levels from cassette", CR
	db	" Q - quit", CR, CR, VT, VT, VT, VT
	db	"IN-GAME CONTROLS:", CR, VT, VT, VT
	db	" arrow keys - move/push", CR
	db	" shift + arrow keys - run", CR
	db	" U - undo", CR
	db	" R - restart level", CR
	db	" M - return to menu", 0

; ==============================================================================
; Control keys
;
	.globl	KEY_YES, KEY_NO, KEY_ENTER
	.equiv	KEY_YES, 'Y'
	.equiv	KEY_NO, 'N'
	.equiv	KEY_ENTER, KEOL

	.globl	KEY_PLAY, KEY_SELECT, KEY_LOAD, KEY_QUIT
	.equiv	KEY_PLAY, 'P'
	.equiv	KEY_SELECT, 'S'
	.equiv	KEY_LOAD, 'L'
	.equiv	KEY_QUIT, 'Q'

	.globl	KEY_RESTART, KEY_UNDO, KEY_MENU
	.equiv	KEY_RESTART, 'R'
	.equiv	KEY_UNDO, 'U'
	.equiv	KEY_MENU, 'M'
	
; ==============================================================================
; Prompts
;
	.data
	.globl	msg_stat, msg_restart, msg_end, msg_quit, msg_menu
	.globl	msg_select1, msg_select2, msg_nolevels, msg_fail, msg_fileno
	.globl	msg_loading, msg_ftb, msg_sload, msg_next, msg_nomore
msg_stat:
	.asciz	"Level:           Moves:            Pushes:"
msg_restart:
	.asciz	"Do you really wish to restart the level? (Y/N)"
msg_end:
	.asciz	"Do you really wish to return to menu? (Y/N)"
msg_quit:
	.asciz	"Do you really wish to end the program? (Y/N)"
msg_menu:
	.asciz	"           Your selection? (P/S/L/Q)"
msg_select1:	
	db	"Level (1", OBELUS, 0
msg_select2:	
	.asciz	")? "
msg_nolevels:
	.asciz	"No levels loaded, press EOL"
msg_fail:
	.asciz	"Failed to load level, press EOL to exit"
msg_fileno:
	db	"File number (00", OBELUS, "99)? ", 0
msg_loading:
	.asciz	"Loading..."
msg_ftb:
	.asciz	"File is too big, cannot be loaded" 
msg_sload:
	.asciz	"Number of levels loaded: "
msg_next:
	.asciz	"Next level? (Y/N)"
msg_nomore:
	.asciz	"No more levels, press EOL"
	
	.end
	
