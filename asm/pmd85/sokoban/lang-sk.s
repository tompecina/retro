; lang-sk.s
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


; Slovak language support.

	.include "sokoban.inc"

; ==============================================================================
; Constants
;
	
; ==============================================================================
; Credits
;
	.data
	.globl	credits
credits:
	db	"Autor: Tom", 0xc1, 0xd3, " Pecina", 0

	
; ==============================================================================
; Control keys
;
	.globl	KEY_YES, KEY_NO, KEY_ENTER
	.equiv	KEY_YES, 'A'
	.equiv	KEY_NO, 'N'
	.equiv	KEY_ENTER, KEOL
	
	.globl	KEY_RESTART, KEY_UNDO, KEY_END, KEY_QUIT
	.equiv	KEY_RESTART, 'R'
	.equiv	KEY_UNDO, 'V'
	.equiv	KEY_MENU, 'M'
	.equiv	KEY_QUIT, 'K'
	
; ==============================================================================
; Labels
;
	.data
	
; ==============================================================================
; Prompts
;
	.data
	.globl	msg_stat, msg_restart, msg_end, msg_quit
msg_stat:
	db	0xf5, "rove", 0xce, "     Krokov:      ", 0xf4, "ahov:", 0
msg_restart:
	db	"Naozaj si ", 0xda, "el", 0xc1, "te re", 0xd3, "tartova", 0xd4
	db	" ", 0xd5, "rove", 0xce, "? (A/N)", 0
msg_end:
	db	"Naozaj sa chcete vr", 0xc1, "ti", 0xd4, " do menu? (A/N)", 0
msg_quit:
	db	"Naozaj si ", 0xda, "el", 0xc1, "te program ukonči", 0xd4
	db	"? (A/N)", 0

	.end
	
