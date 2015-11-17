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
	.equiv	DPPOS, 0xcf4b
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
	db	"Obt", 0xc9, 0xda, "nost:", CR
	db	"Zvuk:", CR, CR, VT, VT, VT, VT
	db	"OVL", 0xe1, "D", 0xe1, "N", 0xe9, ":", CR, VT, VT, VT, VT
	db	" kursorov", 0xd7, " kl", 0xc1, "vesy", CR
	db	"  nebo p", 0xd2, 0xc9, "mo:", CR
	db	" 1-8 - ", 0xd2, "ada", CR
	db	" A-H - sloupec", CR, VT, VT, VT, VT, VT
	db	" EOL - tah", CR
	db	" N - nov", 0xc1, " hra", CR
	db	" U - vr", 0xc1, "tit tah", CR
	db	" W - vym", 0xc5, "nit strany", CR
	db	" K1-K", '0' + MAX_DEPTH, " - obt", 0xc9, 0xda, "nost", CR
	db	" S - zvuk", CR
	db	" Q - konec", 0

; ==============================================================================
; On/off strings
;
	.data
off:	db	"vyp", 0
on:	db	"zap", 0
