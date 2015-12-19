; trbyte.s
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
; trbyte,trbyte1 - read byte from tape recorder
; 
;   uses:   all
; 
	.text
	.globl	trbyte, trbyte1
trbyte1:
	push	bc
	push	de
	push	hl
	jp	2f
trbyte:
	push	bc
	push	de
	push	hl
	ld	hl,(lchar)
1:	ld	c,0x26
	call	trscan
	scf
	jp	z,3f
	ld	a,c
	cp	l
	jp	p,1b
2:	ld	de,0x007f
1:	call	trscan2
	ld	a,c
	cp	l
	jp	c,2f
	call	trscan2
	.byte	0x26
2:	inc	d
	ld	a,d
	rrca
	ld	a,e
	rra
	ld	e,a
	jp	c,1b
3:	pop	hl
	pop	de
	pop	bc
	ret
	
	.globl	lchar
	.lcomm	lchar, 2

	.end
