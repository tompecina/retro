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
	.equiv	DPPOS, 0xcf47
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
	db	"  or directly:", CR
	db	" 1-8 - rank", CR
	db	" A-H - file", CR, VT, VT, VT, VT, VT
	db	" EOL - move", CR
	db	" N - new game", CR
	db	" U - undo move", CR
	db	" W - switch sides", CR
	db	" K1-K", '0' + MAX_DEPTH, " - level", CR
	db	" S - sound", CR
	db	" Q - quit", 0

; ==============================================================================
; On/off strings
;
	.data
off:	db	"off", 0
on:	db	"on ", 0
