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

	.include "reversi.inc"

; ==============================================================================
; Constants
; 
	.globl	LBLPOS, CRPOS, LEGPOS, BCPOS, WCPOS, BSPOS, WSPOS, LVPOS, SNPOS
	.equiv	LBLPOS, 0xc31b
	.equiv	CRPOS, 0xca19
	.equiv	LEGPOS, 0xc700
	.equiv	BCPOS, 0xc700
	.equiv	WCPOS, 0xc711
	.equiv	BSPOS, 0xc9c6
	.equiv	WSPOS, 0xc9cb
	.equiv	LVPOS, 0xcf48
	.equiv	SNPOS, 0xd206
	
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
	db	"  ", 0xe3, "IERNY - BIELY", CR
	db	"         -", CR, CR
	db	0xf5, "rove", 0xce, ":", CR
	db	"Zvuk:", CR, CR, VT, VT, VT, VT
	db	"OVL", 0xe1, "DANIE:", CR, VT, VT, VT, VT
	db	" kurzorov", 0xd7, " kl", 0xc1, "vesy", CR
	db	"  alebo", CR
	db	" 1-8 - rad", CR
	db	" A-H - st", 0xcb, "pec", CR, VT, VT, VT, VT, VT
	db	" EOL - ", 0xd4, "ah", CR
	db	" N - nov", 0xc1, " hra", CR
	db	" V - vr", 0xc1, "ti", 0xd4, " ", 0xd4, "ah", CR
	db	" S - vymeni", 0xd4, " strany", CR
	db	" K1-K", '0' + MAXLEVEL, " - ", 0xd5, "rove", 0xce, CR
	db	" Z - zvuk", CR
	db	" K - koniec", 0

; ==============================================================================
; On/off strings
;
	.data
	.globl	off, on
off:	.asciz	"vyp"
on:	.asciz	"zap"

; ==============================================================================
; Control keys
;
	.globl	KEY_YES, KEY_NO, KEY_ENTER
	.equiv	KEY_YES, 'A'
	.equiv	KEY_NO, 'N'
	.equiv	KEY_ENTER, KEOL
	
	.globl	KEY_NEW, KEY_UNDO, KEY_SWITCH, KEY_SOUND, KEY_QUIT
	.equiv	KEY_NEW, 'N'
	.equiv	KEY_UNDO, 'V'
	.equiv	KEY_SWITCH, 'S'
	.equiv	KEY_SOUND, 'Z'
	.equiv	KEY_QUIT, 'K'
	
; ==============================================================================
; Prompts
;
	.data
	.globl	msg_color, msg_draw, msg_bwin, msg_wwin, msg_quit, msg_new
	.globl	msg_badmove, msg_ppass, msg_cpass, msg_think
msg_color:
	db	0xfa, "el" , 0xc1, "te si hra", 0xd4, " s ", 0xc3
	db	"iernymi? (A/N)", 0
msg_draw:
	db	"Rem", 0xc9, "za, kone", 0xd7, "n", 0xc5, " sk", 0xcf
	db	"re: 32-32", 0
msg_bwin:
	db	0xe3, "ierny vyhral, kone", 0xc3, "n", 0xd7
	db	" sk", 0xcf, "re: ", 0
msg_wwin:
	db	"Biely vyhral, kone", 0xc3, "n", 0xd7
	db	" sk", 0xcf, "re: ", 0
msg_quit:
	db	"Naozaj si ", 0xda, "el", 0xc1, "te program ukonči", 0xd4
	db	"? (A/N)", 0
msg_new:
	db	"Naozaj si ", 0xda, "el", 0xc1, "te za", 0xc3, "a", 0xd4
	db	" nov", 0xd5, " hru? (A/N)", 0
msg_badmove:
	db	"Nedovolen", 0xd9, " ", 0xd4, "ah", 0
msg_ppass:
	db	"Nem", 0xc1, "te ", 0xda, "iaden ", 0xd4, "ah, pros"
	db	0xc9, "m, stla", 0xc3, "te EOL", 0
msg_cpass:
	db	"Po", 0xc3, 0xc9, "ta", 0xc3, " nem", 0xc1, " ", 0xda, 0xc1
	db	"den ", 0xd4, "ah, pokra", 0xc3, "ujte v hre", 0
msg_think:
	db	"Rozm", 0xd9, 0xd3, 0xcc, "am...", 0

	.end
	
