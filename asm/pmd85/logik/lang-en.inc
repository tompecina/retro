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
	
	.equiv	LRPOS, 0xc404
	.equiv	CRPOS, 0xcb02
	
; ==============================================================================
; Credits
;
	.data
credits:
	db	"by Tom", 0xc1, 0xd3, " Pecina", 0

	
; ==============================================================================
; Control keys
;
	.globl	KEY_YES, KEY_NO, KEY_ENTER
	.equiv	KEY_YES, 'Y'
	.equiv	KEY_NO, 'N'
	.equiv	KEY_ENTER, KEOL

	.equiv	KEY_NEW, 'N'
	.equiv	KEY_QUIT, 'Q'
	
; ==============================================================================
; Labels
;
	
; ==============================================================================
; Prompts
;
	.data
msg_hwerr:
	.asciz	"Hardware error, please press EOL to quit"
msg_quit:
	.asciz	"  Do you really wish to end the program? (Y/N)"
msg_new:	
	.asciz	"Do you really wish to start a new game? (Y/N)"
