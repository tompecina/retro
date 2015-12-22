; trheadin.s
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
; trprgout - save header and data to tape recorder
; 
;   input:  (trhead) - header data
; 
;   uses:   all
; 
	.text
	.globl	trprgout
trprgout:
	ld	d,0
	call	waits
	ld	a,0xff
	call	trleader
	xor	a
	call	trleader
	ld	a,0x55
	call	trleader
	ld	hl,trhead
	ld	de,trheadlen - 1
	call	tapeout
	ld	d,0x30
	call	waits
	call	trram
	ld	hl,(lenfil)
	ex	de,hl
	ld	hl,(adrfil)
	jp	tapeout

	.data
	.globl	trram
trram:	ret
	.skip	2
	
	.end
