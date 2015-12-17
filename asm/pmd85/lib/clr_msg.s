; clr_msg.s
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

	.include "pmd85.inc"
	
; ==============================================================================
; clr_msg - clear the notification area
; 
;   uses:   all
; 
	.text
	.globl	clr_msg
clr_msg:
	ld	a,(msg)
	or	a
	ret	z
	ld	hl,MSGAREA
	ld	de,MSGAREA + 48 - (64 * 11)
	ld	b,10
	call	part_erase
	xor	a
	ld	(msg),a
	ret

	.globl	msg
	.lcomm	msg, 1
	
	.end
