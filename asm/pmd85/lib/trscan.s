; trscan.s
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
; trscan,trscan2 - wait for tape recorder input change
; 
;   uses:   all
; 
	.text
	.globl	trscan,trscan2
trscan2:
	ld	c,37
	in	a,(USART_CTRL)
	ld	h,a
trscan:
	dec	c
	ret	z
	in	a,(SYSPIO_PB)
	and	0x40
	ret	z
	in	a,(USART_CTRL)
	xor	h
	jp	p,trscan
	ret
	
	.end
