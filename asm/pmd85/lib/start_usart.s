; start_usart.s
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
; start_usart,stop_usart - start/stop USART and PIT
; 
;   uses:   A, H, L
; 
	.text
	.globl	start_usart, stop_usart
start_usart:
	ld	hl,0x06ab
	jp	1f
stop_usart:
	ld	hl,0x0020
1:	xor	a
	out	(USART_CTRL),a
	out	(USART_CTRL),a
	out	(USART_CTRL),a
	ld	a,0x40
	out	(USART_CTRL),a
	ld	a, 0xed
	out	(USART_CTRL),a
	ld	a,(hw1)
	or	a
	ld	a,0x23
	jp	nz,1f
	ld	a,0x25
1:	out	(USART_CTRL),a
	ld	a,0x76
	out	(PIT_CTRL),a
	ld	a,l
	out	(PIT_1),a
	ld	a,h
	out	(PIT_1),a
	ret
	
	.end
