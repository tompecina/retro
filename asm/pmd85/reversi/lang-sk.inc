; lang-sk.inc
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

; ==============================================================================
; Constants
	
	.equiv	LRPOS, 0xc31b
	.equiv	CRPOS, 0xca19
	.equiv	LEGPOS, 0xc700
	.equiv	BCPOS, 0xc700
	.equiv	WCPOS, 0xc711
	.equiv	BSPOS, 0xc9c6
	.equiv	WSPOS, 0xc9cb
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
	db	"  ", 0xe3, "IERNY - BIELY", CR
	db	"         -", CR, CR
	db	0xf5, "rove", 0xce, ":", CR
	db	"Zvuk:", CR, CR, VT, VT, VT, VT
	db	"OVL", 0xe1, "DANIE:", CR, VT, VT, VT, VT
	db	" kurzorov", 0xd7, " kl", 0xc1, "vesy", CR
	db	"  alebo priamo:", CR
	db	" 1-8 - rad", CR
	db	" A-H - st", 0xcb, "pec", CR, VT, VT, VT, VT, VT
	db	" EOL - ", 0xd4, "ah", CR
	db	" N - nov", 0xc1, " hra", CR
	db	" U - vr", 0xc1, "ti", 0xd4, " tah", CR
	db	" W - vymeni", 0xd4, " strany", CR
	db	" K1-K", '0' + MAX_DEPTH, " - ", 0xd5, "rove", 0xce, CR
	db	" S - zvuk", CR
	db	" Q - koniec", 0

; ==============================================================================
; On/off strings
;
	.data
off:	db	"vyp", 0
on:	db	"zap", 0
