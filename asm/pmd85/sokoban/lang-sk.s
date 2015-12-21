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
	.globl	LBLPOS, CRPOS, LEGPOS, PRPOS, STPOS, LVPOS, MVPOS, PUPOS
	.equiv	LBLPOS, 0xc510
	.equiv	CRPOS, 0xcc0e
	.equiv	LEGPOS, 0xd400
	.equiv	STPOS, 0xc300
	.equiv	LVPOS, 0xc308
	.equiv	MVPOS, 0xc31a
	.equiv	PUPOS, 0xc32b
	
; ==============================================================================
; Credits
;
	.data
	.globl	credits
credits:
	db	"Autor: Tom", 0xc1, 0xd3, " Pecina", 0
	
; ==============================================================================
; Legend
;
	.data
	.globl	legend
legend:
	db	"HLAVN", 0xf7, " MENU:", CR, VT, VT, VT
	db	" H - hra", 0xd4, " od prvej ", 0xd5, "rovne", CR
	db	" Z - hra", 0xd4, " od zvolenej ", 0xd5, "rovne", CR
	db	" N - nahra", 0xd4, " ", 0xd5, "rovne z kazety", CR
	db	" K - koniec", CR, CR, VT, VT, VT, VT
	db	"OVL", 0xe1, "DANIE PO", 0xe3, "AS HRY:", CR, VT, VT, VT
	db	" ", 0xd3, 0xc9, "pky - krok/", 0xd4, "ah", CR
	db	" shift + ", 0xd3, 0xc9, "pky - beh", CR
	db	" V - vr", 0xc1, "ti", 0xd4, " ", 0xd4, "ah", CR
	db	" R - re", 0xd3, "tartova", 0xd4, " ", 0xd5, "rove", 0xce, CR
	db	" M - n", 0xc1, "vrat do menu", 0

; ==============================================================================
; Control keys
;
	.globl	KEY_YES, KEY_NO, KEY_ENTER
	.equiv	KEY_YES, 'A'
	.equiv	KEY_NO, 'N'
	.equiv	KEY_ENTER, KEOL
	
	.globl	KEY_PLAY, KEY_SELECT, KEY_LOAD, KEY_QUIT
	.equiv	KEY_PLAY, 'H'
	.equiv	KEY_SELECT, 'Z'
	.equiv	KEY_LOAD, 'N'
	.equiv	KEY_QUIT, 'K'

	.globl	KEY_RESTART, KEY_UNDO, KEY_MENU
	.equiv	KEY_RESTART, 'R'
	.equiv	KEY_UNDO, 'V'
	.equiv	KEY_MENU, 'M'
	
; ==============================================================================
; Prompts
;
	.data
	.globl	msg_stat, msg_restart, msg_end, msg_quit, msg_menu
	.globl	msg_select1, msg_select2, msg_nolevels, msg_fail, msg_fileno
	.globl	msg_loading, msg_ftb, msg_sload, msg_next, msg_nomore
msg_stat:
	db	0xf5, "rove", 0xce, ":           Krokov:           ", 0xf4
	db	"ahov:", 0
msg_restart:
	db	"Naozaj si ", 0xda, "el", 0xc1, "te re", 0xd3, "tartova", 0xd4
	db	" ", 0xd5, "rove", 0xce, "? (A/N)", 0
msg_end:
	db	"Naozaj sa chcete vr", 0xc1, "ti", 0xd4, " do menu? (A/N)", 0
msg_quit:
	db	"Naozaj si ", 0xda, "el", 0xc1, "te program ukonči", 0xd4
	db	"? (A/N)", 0
msg_menu:
	db	"                Vo", 0xcc, "ba? (H/Z/N/K)", 0
msg_select1:	
	db	0xf5, "rove", 0xce, " (1-", 0
msg_select2:	
	.asciz	")? "
msg_nolevels:
	db	"Nie s", 0xd5, " nahran", 0xd7, " ", 0xda, "iadne ", 0xd5
	db	"rovne, stla", 0xc3, "te EOL", 0
msg_fail:
	db	"Nepodarilo sa zavies", 0xd4, " ", 0xd5, "rove", 0xce
	db	", stla", 0xc3, "te EOL", 0
msg_fileno:
	db	0xe3, 0xc9, "slo s", 0xd5, "boru (00-99)? ", 0
msg_loading:
	db	"Nahr", 0xc1, "vam...", 0
msg_ftb:
	db	"S", 0xd5, "bor je prive", 0xcc, "k", 0xd9, ", nemo", 0xda
	db	"no ho nahra", 0xd4, 0
msg_sload:
	db	"Po", 0xc3, "et nahran", 0xd9, "ch ", 0xd5, "rovn", 0xc9, ": ", 0
msg_next:
	db	0xe4, "al", 0xd3, "iu ", 0xd5, "rove", 0xce, "? (A/N)", 0
msg_nomore:
	db	0xfa, "iadne ", 0xc4, "al", 0xd3, "ie ", 0xd5
	db	"rovne, stla", 0xc3, "te EOL", 0

	.end
	
