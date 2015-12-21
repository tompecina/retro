; lang-cs.s
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


; Czech language support.

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
	db	"HLAVN", 0xe9, " MENU:", CR, VT, VT, VT
	db	" H - hr", 0xc1, "t od prvn", 0xc9, " ", 0xd5, "rovn", 0xc5, CR
	db	" Z - hr", 0xc1, "t od zvolen", 0xd7, " ", 0xd5
	db	"rovn", 0xc5, CR
	db	" N - nahr", 0xc1, "t ", 0xd5, "rovn", 0xc5, " z kasety", CR
	db	" K - konec", CR, CR, VT, VT, VT, VT
	db	"OVL", 0xe1, "D", 0xe1, "N", 0xe9, " B", 0xe5
	db	"HEM HRY:", CR, VT, VT, VT
	db	" ", 0xd3, "ipky - krok/tah", CR
	db	" shift + ", 0xd3, "ipky - b", 0xc5, "h", CR
	db	" V - vr", 0xc1, "tit tah", CR
	db	" R - restartovat ", 0xd5, "rove", 0xce, CR
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
	db	0xf5, "rove", 0xce, ":            Krok", 0xca
	db	":            Tah", 0xca, ":", 0
msg_restart:
	db	"Opravdu si p", 0xd2, "ejete restartovat ", 0xd5, "rove", 0xce
	db	"? (A/N)", 0
msg_end:
	db	"Opravdu se chcete vr", 0xc1, "tit do menu? (A/N)", 0
msg_quit:
	db	"Opravdu si p", 0xd2, "ejete program ukon", 0xc3, "it? (A/N)", 0
msg_menu:
	db	"                Volba? (H/Z/N/K)", 0
msg_select1:	
	db	0xf5, "rove", 0xce, " (1-", 0
msg_select2:	
	.asciz	")? "
msg_nolevels:
	db	"Nejsou nahr", 0xc1, "ny ", 0xda, 0xc1, "dn", 0xd7, " ", 0xd5
	db	"rovn", 0xc5, ", stiskn", 0xc5, "te EOL", 0
msg_fail:
	db	"Nepoda", 0xd, "ilo se zav", 0xd7, "st ", 0xd5, "rove", 0xce
	db	", stiskn", 0xc5, "te EOL", 0
msg_fileno:
	db	0xe3, 0xc9, "slo souboru (00-99)? ", 0
msg_loading:
	db	"Nahr", 0xc1, "v", 0xc1, "m...", 0
msg_ftb:
	db	"Soubor je p", 0xd2, 0xc9, "li", 0xd3, " velk", 0xd9
	db	", nelze ho na", 0xc3, 0xc9, "st", 0
msg_sload:
	db	"Po", 0xc3, "et nahran", 0xd9, "ch ", 0xd5, "rovn", 0xc9
	db	": ", 0
msg_next:
	db	"Dal", 0xd3, 0xc9, " ", 0xd5, "rove", 0xce, "? (A/N)", 0
msg_nomore:
	db	0xfa, 0xc1, "dn", 0xd7, " dal", 0xd3, 0xc9, " ", 0xd5
	db	"rovn", 0xc5, ", stiskn", 0xc5, "te EOL", 0
	
	.end
	
