; debug.s
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


; Copy of original monitor's routine.

	.include "pmd85.inc"

	.if	DEBUG
; ==============================================================================
; Constants
; 
	.equiv	DEBUGPORT, 0
	
; ==============================================================================
; log - log message to DEBUGPORT (w/o LF)
; 
;   input:  zero-terminated message appended to call
; 
;   uses:   -
; 
	.text
	.globl	log
log:	ld	(shl),hl
	push	af
	pop	hl
	ld	(saf),hl
	pop	hl
1:	ld	a,(hl)
	inc	hl
	or	a
	jp	z,1f
	out	(DEBUGPORT),a
	jp	1b
1:	push	hl
	ld	hl,(saf)
	push	hl
	pop	af
	ld	hl,(shl)
	ret

	.lcomm	shl, 2
	.lcomm	saf, 2
	
; ==============================================================================
; logmsg - log message to DEBUGPORT (with LF)
; 
;   input:  zero-terminated message appended to call
; 
;   uses:   -
; 
	.text
	.globl	logmsg
logmsg:	ld	(shl),hl
	push	af
	pop	hl
	ld	(saf),hl
	pop	hl
1:	ld	a,(hl)
	inc	hl
	or	a
	jp	z,1f
	out	(DEBUGPORT),a
	jp	1b
1:	call	lognl
	push	hl
	ld	hl,(saf)
	push	hl
	pop	af
	ld	hl,(shl)
	ret
	
; ==============================================================================
; logmsg - log LF to DEBUGPORT
; 
;   uses:   -
; 
	.text
	.globl	lognl
lognl:	push	af
	ld	a,LF
	out	(DEBUGPORT),a
	pop	af
	ret
	
; ==============================================================================
; loghex1 - log A in hex to DEBUGPORT
; 
;   input:  A
; 
;   uses:   -
; 
	.text
	.globl	loghex1
loghex1:
	push	af
	push	bc
	push	hl
	ld	hl,hexbuf
	push	hl
	call	PMD_PREVO2
	pop	hl
	ld	a,(hl)
	out	(DEBUGPORT),a
	inc	hl
	ld	a,(hl)
	out	(DEBUGPORT),a
	pop	hl
	pop	bc
	pop	af
	ret
	
	.lcomm	hexbuf, 2
	
; ==============================================================================
; loghex2 - log HL in hex to DEBUGPORT
; 
;   input:  HL
; 
;   uses:   -
; 
	.text
	.globl	loghex2
loghex2:
	push	af
	push	hl
	ld	a,h
	call	loghex1
	ld	a,l
	call	loghex1
	pop	hl
	pop	af
	ret
	
; ==============================================================================
; logstat - log CPU status to DEBUGPORT
; 
;   uses:   -
; 
	.text
	.globl	logstat
logstat:
	ld	(shl),hl
	pop	hl
	push	hl
	ld	(spc),hl
	ld	hl,0
	add	hl,sp
	ld	(ssp),hl
	push	af
	pop	hl
	ld	(saf),hl
	ld	h,b
	ld	l,c
	ld	(sbc),hl
	ex	de,hl
	ld	(sde),hl
	call	log
	.asciz	"PC:"
	ld	hl,(spc)
	dec	hl
	dec	hl
	dec	hl
	ld	a,h
	call	1f
	ld	a,l
	call	1f
	call	log
	.asciz	" A:"
	ld	a,(saf + 1)
	call	loghex1
	call	log
	.asciz	" BC:"
	ld	hl,(sbc)
	call	loghex2
	call	log
	.asciz	" DE:"
	ld	hl,(sde)
	call	loghex2
	call	log
	.asciz	" HL:"
	ld	hl,(shl)
	call	loghex2
	call	log
	.asciz	" SP:"
	ld	hl,(ssp)
	call	loghex2
	call	log
	.asciz	" S:"
	ld	a,(saf)
	and	0x80
	call	1f
	call	log
	.asciz	" Z:"
	ld	a,(saf)
	and	0x40
	call	1f
	call	log
	.asciz	" AC:"
	ld	a,(saf)
	and	0x10
	call	1f
	call	log
	.asciz	" P:"
	ld	a,(saf)
	and	0x04
	call	1f
	call	log
	.asciz	" C:"
	ld	a,(saf)
	and	0x01
	call	1f
	call	logmsg
	.byte	0	
	ld	hl,(sde)
	ex	de,hl
	ld	hl,(sbc)
	ld	b,h
	ld	c,l
	ld	hl,(saf)
	push	hl
	pop	af
	ld	hl,(shl)
	ret
1:	ld	a,'0'
	jp	z,1f
	inc	a
1:	out	(DEBUGPORT),a
	ret
	
	.lcomm	sbc, 2
	.lcomm	sde, 2
	.lcomm	spc, 2
	.lcomm	ssp, 2
	
	.endif
	
	.end
