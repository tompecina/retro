; lang-cs.inc
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

; ==============================================================================
; Constants
	
	.equiv	LRPOS, 0xc31b
	.equiv	CRPOS, 0xca19
	.equiv	LEGPOS, 0xc700
	.equiv	BCPOS, 0xc700
	.equiv	WCPOS, 0xc70f
	.equiv	BSPOS, 0xc9c5
	.equiv	WSPOS, 0xc9ca
	.equiv	LVPOS, 0xcf48
	.equiv	SNPOS, 0xd206
	
; ==============================================================================
; Credits
;
	.data
credits:
	db	"Autor: Tom", 0xc1, 0xd3, " Pecina", 0

; ==============================================================================
; Legend
;
	.data
legend:
	db	"  ", 0xe3, "ERN", 0xf9, " - B" , 0xe9, "L", 0xf9, CR
	db	"        -", CR, CR
	db	0xf5, "rove", 0xce, ":", CR
	db	"Zvuk:", CR, CR, VT, VT, VT, VT
	db	"OVL", 0xe1, "D", 0xe1, "N", 0xe9, ":", CR, VT, VT, VT, VT
	db	" kursorov", 0xd7, " kl", 0xc1, "vesy", CR
	db	"  nebo", CR
	db	" 1-8 - ", 0xd2, "ada", CR
	db	" A-H - sloupec", CR, VT, VT, VT, VT, VT
	db	" EOL - tah", CR
	db	" N - nov", 0xc1, " hra", CR
	db	" V - vr", 0xc1, "tit tah", CR
	db	" S - vym", 0xc5, "nit strany", CR
	db	" K1-K", '0' + MAXLEVEL, " - ", 0xd5, "rove", 0xce, CR
	db	" Z - zvuk", CR
	db	" K - konec", 0

; ==============================================================================
; On/off strings
;
	.data
off:	.asciz	"vyp"
on:	.asciz	"zap"

; ==============================================================================
; Control keys
;
	.globl	KEY_YES, KEY_NO, KEY_ENTER
	.equiv	KEY_YES, 'A'
	.equiv	KEY_NO, 'N'
	.equiv	KEY_ENTER, KEOL

	.equiv	KEY_NEW, 'N'
	.equiv	KEY_UNDO, 'V'
	.equiv	KEY_SWITCH, 'S'
	.equiv	KEY_SOUND, 'Z'
	.equiv	KEY_QUIT, 'K'
	
; ==============================================================================
; Prompts
;
	.data
msg_color:
	db	"P", 0xd2, "ejete si hr" , 0xc1, "t s ", 0xc3, "ern", 0xd9
	db	"mi? (A/N)", 0
msg_draw:
	db	"Remisa, kone", 0xc3, "n", 0xd7, " skore: 32-32", 0
msg_bwin:
	db	0xe3, "ern", 0xd9, " vyhr", 0xc1, "l, kone", 0xc3, "n", 0xd7
	db	" skore: ", 0
msg_wwin:
	db	"B", 0xc9, "l", 0xd9, " vyhr", 0xc1, "l, kone", 0xc3, "n", 0xd7
	db	" skore: ", 0
msg_quit:
	db	"Opravdu si p", 0xd2, "ejete program ukon", 0xc3, "it? (A/N)", 0
msg_new:
	db	"Opravdu si p", 0xd2, "ejete za", 0xc3, 0xc9
	db	"t novou hru? (A/N)", 0
msg_badmove:
	db	"Nedovolen", 0xd9, " tah", 0
msg_ppass:
	db	"Nem", 0xc1, "te ", 0xda, 0xc1, "dn", 0xd9, " tah, pros", 0xc9
	db	"m, stiskn", 0xc5, "te EOL", 0
msg_cpass:
	db	"Po", 0xc3, 0xc9, "ta", 0xc3, "nem", 0xc1, " ", 0xda, 0xc1
	db	"dn", 0xd9, " tah, pokra", 0xc3, "ujte ve h", 0xd2, "e", 0
