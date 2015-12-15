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
	
	.equiv	LRPOS, 0xc204
	.equiv	CRPOS, 0xc900
	.equiv	DSPOS, 0xd200
	.equiv	RNDPOS, 0xd206
	.equiv	RNDSPOS, 0xd208
	.equiv	PSCPOS, 0xd789
	.equiv	CSCPOS, 0xdb09
	
; ==============================================================================
; Credits
;
	.data
credits:
	db	"Autor: Tom", 0xc1, 0xd3, " Pecina", 0

; ==============================================================================
; Control keys
;
	.globl	KEY_YES, KEY_NO, KEY_ENTER
	.equiv	KEY_YES, 'A'
	.equiv	KEY_NO, 'N'
	.equiv	KEY_ENTER, KEOL

; ==============================================================================
; Labels
;
lbl_desc:
	db	"Kolo:  /", CR, CR
	db	"Hr", 0xc1, 0xc3, ":", CR, VT, VT, VT
	db	"Po", 0xc3, 0xc9, "ta", 0xc3, ":", 0
	
; ==============================================================================
; Prompts
;
	.data
	.globl	msg_yguess, msg_bpegs, msg_wpegs, msg_confirm, msg_errpegs
	.globl	msg_think
msg_hwerr:
	db	"Vadn", 0xd9, " hardware, pros", 0xc9, "m, stiskn", 0xc5
	db	"te EOL", 0
msg_bpegs:
	db	0xe3, "ern", 0xd9, "ch? ", 0
msg_wpegs:
	db	"  B", 0xc9, "l", 0xd9, "ch? ", 0
msg_confirm:
	db	"  V po", 0xd2, 0xc1, "dku? (A/N)", 0
msg_errpegs:
	db	"Nedovolen", 0xc1, " kombinace, stiskn", 0xc5
	db	"te EOL a opravte", 0
msg_nrnds:
	db	"Po", 0xc3, "et kol (1-8)? ", 0
msg_cstart:
	db	"Po", 0xc3, 0xc9, "ta", 0xc3, " sestavil kod, stiskn", 0xc5
	db	"te EOL", 0
msg_pstart:
	db	"Sestavte kod a stiskn", 0xc5, "te EOL", 0
msg_cout:
	db	"Pokusy vy", 0xc3, "erp", 0xc1, "ny, po", 0xc3, 0xc9, "ta", 0xc3
	db	" z", 0xc9, "sk", 0xc1, "v", 0xc1, " 11 bod", 0xca, 0
msg_pout:
	db	"Pokusy vy", 0xc3, "erp", 0xc1, "ny, z", 0xc9, "sk", 0xc1
	db	"v", 0xc1, "te 11 bod", 0xca, 0
msg_perr:
	db	"Chybn", 0xd7, " zad", 0xc1, "n", 0xc9, ", z", 0xc9, "sk", 0xc1
	db	"v", 0xc1, "te 0 bod", 0xca, 0
msg_again:
	db	"Dal", 0xd3, 0xc9, " hru? (A/N)", 0
msg_think:
	db	"P", 0xd2, "em", 0xd9, 0xd3, "l", 0xc9, "m...", 0
msg_cwin:
	db	"Po", 0xc3, 0xc9, "ta", 0xc3, " vyhr", 0xc1, "l. ", 0
msg_closs:
	db	"Po", 0xc3, 0xc9, "ta", 0xc3, " prohr", 0xc1, "l. ", 0
msg_draw:
	db	"Nerozhodn", 0xd9, " v", 0xd9, "sledek. ", 0
