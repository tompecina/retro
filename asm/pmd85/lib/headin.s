; headin.s
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
; headin - read sync and header
; 
;   uses:   all
; 
	.text
	.globl	headin
headin:
	ld	a,(hw1)
	or	a
	jp	z,2f
	call	beclr
	ld	d,0x13
	ld	b,a
1:	in	a,(SYSPIO_PB)
	cpl
	rlca
	rlca
	ret	c
	call	trscan2
	ld	a,c
	cp	d
	jp	m,headin
	dec	b
	jp	nz,1b
	sub	0x04
	ld	d,a
1:	ld	l,c
	call	bepuk
	call	trscan2
	in	a,(SYSPIO_PB)
	cpl
	rlca
	rlca
	ret	c
	ld	a,c
	cp	d
	jp	p,1b
	sub	0x04
	add	a,l
	rra
	ld	l,a
	ld	(lchar),hl
	call	trbyte1
	call	beclr
1:	jp	c,headin
	call	trbyte
	or	a
	jp	nz,1b
1:	call	trbyte
	jp	c,headin
	cp	0x55
	jp	nz,1b
	ld	d,0x0f
1:	call	trbyte
	cp	0x55
	jp	nz,headin
	dec	d
	jp	nz,1b
	ld	hl,trhead
	ld	de,0x000d
	call	trload
	jp	nz,headin
	ret
2:	ld	l,16
1:	call	waimgi
	in	a,(USART_DATA)
	or	a
	jp	nz,3f
	dec	l
	jp	nz,1b
	ld	l,16
1:	call	waimgi
	in	a,(USART_DATA)
	cp	0x55
	jp	nz,2b
	dec	l
	jp	nz,1b
	ld	hl,trhead
	ld	de,0x000d
	call	trload
	jp	nz,2b
	ret
3:	call	test_stop
	jp	nz,2b
	or	a
	ret
	
	.globl	trhead
	.lcomm	trhead, 14
	
	.end
