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

	.include "logik.inc"

; ==============================================================================
; Constants
; 
	.globl	LBLPOS, CRPOS, DSPOS, RNDPOS, RNDSPOS, PSCPOS, CSCPOS
	.equiv	LBLPOS, 0xc204
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
	.globl	credits
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
	.data
	.globl	lbl_desc
lbl_desc:
	db	"Kolo:  /", CR, CR
	db	"Hr", 0xc1, 0xc3, ":", CR, VT, VT, VT
	db	"Po", 0xc3, 0xc9, "ta", 0xc3, ":", 0
	
; ==============================================================================
; Prompts
;
	.data
	.globl	msg_hwerr, msg_yguess, msg_bpegs, msg_wpegs, msg_confirm
	.globl	msg_errpegs, msg_nrnds, msg_cstart, msg_pstart, msg_cout
	.globl	msg_pout, msg_perr, msg_ccorr, msg_pcorr, msg_corr1, msg_corr10
	.globl	msg_again, msg_think, msg_cwin, msg_closs, msg_draw
msg_hwerr:
	db	"Vadn", 0xd9, " hardv", 0xd7, "r, pros", 0xc9, "m, stla", 0xc3
	db	"te EOL", 0
msg_yguess:
	db	"Zadajte kombin", 0xc1, "ciu: 1-", '0' + COLORS
	db	", ", LEFT1, LEFT2, ", ", RIGHT1, RIGHT2, ", DEL, CLR, EOL", 0
msg_bpegs:
	db	0xe3, "iernych? ", 0
msg_wpegs:
	.asciz	"  Bielych? "
msg_confirm:
	.asciz	"  V poriadku? (A/N)"
msg_errpegs:
	db	"Nedovolen", 0xc1, " kombin", 0xc1, "cia, stla", 0xc3
	db	"te EOL a opravte", 0
msg_nrnds:
	db	"Po", 0xc3, "et k", 0xd0, "l (1-8)? ", 0
msg_cstart:
	db	"Po", 0xc3, 0xc9, "ta", 0xc3, " zostavil k", 0xcf
	db	"d, stla", 0xc3, "te EOL", 0
msg_pstart:
	db	"Zostavte k", 0xcf, "d a stla", 0xc3, "te EOL", 0
msg_cout:
	db	"Pokusy vy", 0xc3, "erpan", 0xd7, ", po", 0xc3, 0xc9, "ta", 0xc3
	db	" z", 0xc9, "skava 11 bodov", 0
msg_pout:
	db	"Pokusy vy", 0xc3, "erpan", 0xd7, ", z", 0xc9
	db	"skavate 11 bodov", 0
msg_perr:
	db	"Zl", 0xd7, " zadanie, z", 0xc9, "skavate 0 bodov", 0
msg_ccorr:
	db	"Po", 0xc3, 0xc9, "ta", 0xc3, " z", 0xc9, "skava ", 0
msg_pcorr:
	db	"Z", 0xc9, "skavate ", 0
msg_corr1:
	.asciz	"1 bod"
	.irpc	n, "234"
msg_corr\n:
	.globl	msg_corr\n
	.asciz	"\n body"
	.endr
	.irpc	n, "56789"
msg_corr\n:
	.globl	msg_corr\n
	.asciz	"\n bodov"
	.endr
msg_corr10:
	.asciz	"10 bodov"
msg_again:
	db	0xe4, "al", 0xd3, "iu hru? (A/N)", 0
msg_think:
	db	"Rozm", 0xd9, 0xd3, 0xcc, "am...", 0
msg_cwin:
	db	"Po", 0xc3, 0xc9, "ta", 0xc3, " vyhral. ", 0
msg_closs:
	db	"Po", 0xc3, 0xc9, "ta", 0xc3, " prehral. ", 0
msg_draw:
	db	"Nerozhodn", 0xd9, " v", 0xd9, "sledok. ", 0

	.end
	
