; init_bcbrtbl.s
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

	
; ==============================================================================
; init_bcbrtbl - initialize the bit manipulation tables
; 
;   output: (bitcounts) and (byterefl) populated
; 
;   uses:   all
; 
	.text
	.globl	init_bcbrtbl
init_bcbrtbl:
	ld	hl,bitcounts
	ld	de,byterefl
	xor	a
1:	push	af
	push	de
	ld	bc,0x0080
3:	rla
	jp	nc,2f
	inc	b
2:	ld	d,a
	ld	a,c
	rra
	ld	c,a
	ld	a,d
	jp	nc,3b
	ld	(hl),b
	inc	hl
	pop	de
	ld	a,c
	ld	(de),a
	inc	de
	pop	af
	inc	a
	jp	nz,1b
	ret

	.end
