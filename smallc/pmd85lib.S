; pmd85lib.S
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


; This is the assembler library for Tesla PMD 85 hardware.

	.section .text

; ==============================================================================
; PMD 85 addresses
	
PRTOUT	equ	0x8500
MONIT	equ	0x8c40	
	
; ==============================================================================
; _monit - return to monitor
;
	.global _monit
_monit:
	jp	MONIT

; ==============================================================================
; _prtout - print character
; 
;   input:  $1 - character
;
	.global	_prtout
_prtout:
	ld	hl,2
	add	hl,sp
	ld	a,(hl)		; character -> A
	jp	PRTOUT
	
; ==============================================================================
; _erase - clear screen
;
	.global _erase
_erase:
	ld	a,0x1c
	jp	PRTOUT
	
	.end
