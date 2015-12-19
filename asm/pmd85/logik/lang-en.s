; lang-en.s
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

	.include "logik.inc"

; ==============================================================================
; Constants
; 
	.globl	LBLPOS, CRPOS, DSPOS, RNDPOS, RNDSPOS, PSCPOS, CSCPOS
	.equiv	LBLPOS, 0xc204
	.equiv	CRPOS, 0xc902
	.equiv	DSPOS, 0xd200
	.equiv	RNDPOS, 0xd207
	.equiv	RNDSPOS, 0xd20c
	.equiv	PSCPOS, 0xd78a
	.equiv	CSCPOS, 0xdb0a
	
; ==============================================================================
; Credits
;
	.data
	.globl	credits
credits:
	db	"by Tom", 0xc1, 0xd3, " Pecina", 0

	
; ==============================================================================
; Control keys
;
	.globl	KEY_YES, KEY_NO, KEY_ENTER
	.equiv	KEY_YES, 'Y'
	.equiv	KEY_NO, 'N'
	.equiv	KEY_ENTER, KEOL

; ==============================================================================
; Labels
;
	.data
	.globl	lbl_desc
lbl_desc:
	db	"Round:   of", CR, CR
	db	"Player:", CR, VT, VT, VT
	db	"Computer:", 0
	
; ==============================================================================
; Prompts
;
	.data
	.globl	msg_hwerr, msg_yguess, msg_bpegs, msg_wpegs, msg_confirm
	.globl	msg_errpegs, msg_nrnds, msg_cstart, msg_pstart, msg_cout
	.globl	msg_pout, msg_perr, msg_ccorr, msg_pcorr, msg_corr1, msg_corr10
	.globl	msg_again, msg_think, msg_cwin, msg_closs, msg_draw
msg_hwerr:
	.asciz	"Hardware error, please press EOL to quit"
msg_yguess:
	db	"Enter your guess: 1", OBELUS, '0' + COLORS, ", ", LEFT1, LEFT2
	db	", ", RIGHT1, RIGHT2, ", DEL, CLR, EOL", 0
msg_bpegs:
	.asciz	"Black pegs? "
msg_wpegs:
	.asciz	"  White pegs? "
msg_confirm:
	.asciz	"  Confirm (Y/N)"
msg_errpegs:
	.asciz	"Invalid combination, please press EOL and retry"
msg_nrnds:
	db	"Number of rounds (1", OBELUS, "8)? ", 0
msg_cstart:
	.asciz	"Computer's code ready, press EOL to proceed"
msg_pstart:
	.asciz	"Set up your code and press EOL"
msg_cout:
	.asciz	"No more attempts, computer scores 11 points"
msg_pout:
	.asciz	"No more attempts, you score 11 points"
msg_perr:
	.asciz	"Incompatible entries, you score 0 points"
msg_ccorr:
	.asciz	"Computer scores "
msg_pcorr:
	.asciz	"You score "
msg_corr1:
	.asciz	"1 point"
	.irpc	n, "23456789"
msg_corr\n:
	.globl	msg_corr\n
	.asciz	"\n points"
	.endr
msg_corr10:
	.asciz	"10 points"
msg_again:
	.asciz	"Another game? (Y/N)"
msg_think:
	.asciz	"Thinking..."
msg_cwin:
	.asciz	"Computer won. "
msg_closs:
	.asciz	"Computer lost. "
msg_draw:
	.asciz	"A draw. "
	
	.end
	
