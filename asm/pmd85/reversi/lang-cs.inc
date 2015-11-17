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
	.equiv	DPPOS, 0xcf48
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
	db	"  nebo p", 0xd2, 0xc9, "mo:", CR
	db	" 1-8 - ", 0xd2, "ada", CR
	db	" A-H - sloupec", CR, VT, VT, VT, VT, VT
	db	" EOL - tah", CR
	db	" N - nov", 0xc1, " hra", CR
	db	" V - vr", 0xc1, "tit tah", CR
	db	" S - vym", 0xc5, "nit strany", CR
	db	" K1-K", '0' + MAX_DEPTH, " - ", 0xd5, "rove", 0xce, CR
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
	.globl	KEY_YES, KEY_NO
	.equiv	KEY_YES, 'A'
	.equiv	KEY_NO, 'N'

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
	db	"P", 0xd2, "ejete si hr" , 0xe1, "t s ", 0xc3, "ern", 0xd9
	db	"mi? (A/N)", 0
