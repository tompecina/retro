; beep.S
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


; Copy of original monitor's beeper routines.

	.include "pmd85.inc"
	
; ==============================================================================
; bepuk - invert beep and yellow LED pin
; 
;   uses:   A
; 
	.global	bepuk
bepuk:
	in	a,(SYSPIO_PC)
	xor	0x02
	out	(SYSPIO_PC),a
	ret

; ==============================================================================
; beclr - beeper off
; 
;   uses:   A
; 
	.global	beclr
beclr:
	in	a,(SYSPIO_PC)
	and	0xfc
	out	(SYSPIO_PC),a
	ret

; ==============================================================================
; beep,bell - beep according to a pattern
; 
;   input:  (BEEDT) - pattern (for beep)
;           (HL) - pattern (for bell)
; 
;   uses:   A, B, D, H, L
; 
	.global beep, beclr
beep:
	ld	hl,(BEEDT)
bell:
	call	beclr
	ld	b,a
	ld	a,(hl)
	cp	-1
	ret	z
	or	b
	out	(SYSPIO_PC),A
	inc	hl
	ld	d,(hl)
	call	waits
	inc	hl
	jp	bell
	
	.end
