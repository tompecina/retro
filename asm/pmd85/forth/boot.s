; boot.s
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


; Boot code for fig-forth.

	.include "forth.inc"

; ==============================================================================
; Constants
;
	
; ==============================================================================
; Main entry point of the program
;
	.text
	.globl	boot
boot:
	di
	ld	hl,(ORIG+12H)
	ld	sp,hl
	call	init_kbd
	call	set_kmap
	call	init_video
	call	erase
	
	jp	ORIG

tt1:	db	0xe3, "erny kun", CR, 0
tt2:	db	0xe3, "erny orel", CR, 0
	
	.end
