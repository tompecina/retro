; get_ack.s
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
; get_ack,get_ack2 - optionally display prompt and wait for Enter
; 
;   input:  (HL) - prompt (only get_ack)
;           (color) - color mask
; 
;   uses:   A, B, D, E, H, L
; 
	.text
	.globl	get_ack, get_ack2
get_ack:
	call	disp_msg
get_ack2:
	indcall	inklav
	cp	KEY_ENTER
	jp	nz,get_ack2
	jp	clr_msg
	
	.end
