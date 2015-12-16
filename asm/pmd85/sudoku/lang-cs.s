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

	.include "sudoku.inc"

; ==============================================================================
; Constants
; 
	
; ==============================================================================
; Control keys
;
	.globl	KEY_YES, KEY_NO, KEY_ENTER
	.equiv	KEY_YES, 'A'
	.equiv	KEY_NO, 'N'
	.equiv	KEY_ENTER, KEOL

	.globl	KEY_NEW, KEY_QUIT
	.equiv	KEY_NEW, 'N'
	.equiv	KEY_QUIT, 'K'
	
; ==============================================================================
; Labels
;
	.data
	.globl	lbl_sudoku
lbl_sudoku:
	db	"SUDOKU ", ONEDOT, "0", 0
	
; ==============================================================================
; Prompts
;
	.data
	.globl	msg_hwerr, msg_select, msg_start, msg_done, ph_tmr, msg_quit
	.globl	msg_new, msg_sqerr
msg_hwerr:
	db	"Vadn", 0xd9, " hardware, pros", 0xc9, "m, stiskn", 0xc5
	db	"te EOL", 0
msg_select:
	db	"             Zvolte obt", 0xc9, 0xda, "nost (0-3)", 0
msg_start:
	db	"    Ovl", 0xc1, "d", 0xc1, "n", 0xc9, ": ", 0xd3
	db	"ipky, 1-9, DEL, N-ov", 0xd7, ", K-onec", 0
msg_done:
	db	"            Blahop", 0xd2, "eji! V", 0xc1, 0xd3, " ", 0xc3
	db	"as: "
ph_tmr:
	.skip	8
msg_quit:
	db	"   Opravdu si p", 0xd2, "ejete program ukon", 0xc3
	db	"it? (A/N)", 0
msg_new:
	db	" Opravdu si p", 0xd2, "ejete vytvo", 0xd2, "it nov", 0xd7
	db	" sudoku? (A/N)", 0
msg_sqerr:
	.asciz	"             Toto pole nelze editovat"

	.end
	
