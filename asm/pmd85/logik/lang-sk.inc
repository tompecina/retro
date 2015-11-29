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
	
	
; ==============================================================================
; Control keys
;
	.globl	KEY_YES, KEY_NO, KEY_ENTER
	.equiv	KEY_YES, 'A'
	.equiv	KEY_NO, 'N'
	.equiv	KEY_ENTER, KEOL
	
	.equiv	KEY_NEW, 'N'
	.equiv	KEY_QUIT, 'K'
	
; ==============================================================================
; Labels
;
	
; ==============================================================================
; Prompts
;
	.data
msg_hwerr:
	db	"Vadn", 0xd9, " hardv", 0xd7, "r, pros", 0xc9, "m, stla", 0xc3
	db	"te EOL", 0
msg_quit:
	db	"     Naozaj si ", 0xda, "el", 0xc1, "te program ukon", 0xc3
	db	"i", 0xd4, "? (A/N)", 0
msg_new:
	db	"  Naozaj si ", 0xda, "el", 0xc1, "te za", 0xc3, "a", 0xd4
	db	" nov", 0xd5, " hru? (A/N)", 0
