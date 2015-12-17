; conv_time.s
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
; conv_time - convert time to string
; 
;   input:  HL - time in seconds
;	    (DE) - destination
; 
;   output: (DE) updated
; 
;   uses:   all
; 
	.text
	.globl	conv_time
conv_time:
	ld	a,h
	or	l
	jp	nz,1f
	ld	hl,tmerr
	ld	b,5
	jp	copy8
1:	ld	b,d
	ld	c,e
	push	bc
	ld	c,60
	call	udiv16_8
	pop	bc
	push	de
	ld	d,0xff
	push	de
1:	push	bc
	ld	c,10
	call	udiv16_8
	pop	bc
	ld	a,h
	or	l
	jp	z,1f
	push	de
	jp	1b
1:	ld	a,'0'
	add	a,e
	ld	(bc),a
	inc	bc
	pop	de
	inc	d
	jp	nz,1b
	ld	a,TIMECOL
	ld	(bc),a
	inc	bc
	pop	hl
	push	bc
	ld	c,10
	call	udiv16_8
	pop	bc
	ld	a,l
	add	a,'0'
	ld	(bc),a
	inc	bc
	ld	a,e
	add	a,'0'
	ld	(bc),a
	inc	bc
	xor	a
	ld	(bc),a
	ret
	
	.data
tmerr:	db	"?", TIMECOL, "??", 0
	
	.end
