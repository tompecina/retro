; lang-en.inc
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

; ==============================================================================
; Constants
	
	.equiv	LRPOS, 0xc31b
	.equiv	CRPOS, 0xca1b
	.equiv	LEGPOS, 0xc700
	.equiv	BCPOS, 0xc700
	.equiv	WCPOS, 0xc710
	.equiv	BSPOS, 0xc9c5
	.equiv	WSPOS, 0xc9ca
	.equiv	LVPOS, 0xcf47
	.equiv	SNPOS, 0xd207
	
; ==============================================================================
; Credits
;
	.data
credits:
	db	"by Tom", 0xc1, 0xd3, " Pecina", 0

; ==============================================================================
; Legend
;
	.data
legend:
	db	"  BLACK - WHITE", CR
	db	"        -", CR, CR
	db	"Level:", CR
	db	"Sound:", CR, CR, VT, VT, VT, VT
	db	"CONTROLS:", CR, VT, VT, VT, VT
	db	" arrow keys", CR
	db	"  or", CR
	db	" 1", OBELUS, "8 - row", CR
	db	" A", OBELUS, "H - column", CR, VT, VT, VT, VT, VT
	db	" EOL - move", CR
	db	" N - new game", CR
	db	" U - undo move", CR
	db	" W - switch sides", CR
	db	" K1", OBELUS, "K", '0' + MAXLEVEL, " - level", CR
	db	" S - sound", CR
	db	" Q - quit", 0

; ==============================================================================
; On/off strings
;
	.data
off:	.asciz	"off"
on:	.asciz	"on "

; ==============================================================================
; Control keys
;
	.globl	KEY_YES, KEY_NO
	.equiv	KEY_YES, 'Y'
	.equiv	KEY_NO, 'N'

	.equiv	KEY_NEW, 'N'
	.equiv	KEY_UNDO, 'U'
	.equiv	KEY_SWITCH, 'W'
	.equiv	KEY_SOUND, 'S'
	.equiv	KEY_QUIT, 'Q'
	
; ==============================================================================
; Prompts
;
	.data
msg_color:
	.asciz	"Do you wish to play black? (Y/N)"
msg_draw:
	.asciz	"A draw, final score: 32-32"
msg_bwin:
	.asciz	"Black wins, final score: "
msg_wwin:
	.asciz	"White wins, final score: "
msg_quit:
	.asciz	"Do you really wish to end the program? (Y/N)"
msg_new:	
	.asciz	"Do you really wish to start a new game? (Y/N)"
msg_badmove:
	.asciz	"Bad move"
