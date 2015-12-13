; game.s
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


; Various game related routines.

	.include "logik.inc"
	
; ==============================================================================
; Constants
;
	.equiv	LIMIT, 8
	
; ==============================================================================
; Matching macros
; 
	
; masks
	.equiv	MASK0, 0x07
	.equiv	MASK1, 0x38
	.equiv	MASK2, 0xe0
	.equiv	MASK3, 0x0e
	.equiv	MASK4, 0x70
	.equiv	BMASK0, 0x01
	.equiv	BMASK1, 0x02
	.equiv	BMASK2, 0x04
	.equiv	BMASK3, 0x08
	.equiv	BMASK4, 0x10

; load first color
	.macro	ldpos	p
	.if	\p < 2
	ld	a,e
	.elseif	\p == 2
	ld	a,d
	rra
	ld	a,e
	rra
	.else
	ld	a,d
	.endif
	.endm

; compare to second color
	.macro	xorpos	p
	.if	\p < 2
	xor	l
	.elseif	\p == 2
	xor	b
	.else
	xor	h
	.endif
	.endm
	
; align colors
	.macro	alpos	p,q
	.if	\p != \q
	.if	\p == 0
	.if	\q == 1
	rla
	rla
	rla
	.elseif	\q == 2
	rrca
	rrca
	rrca
	.elseif	\q == 3
	rla
	.else
	rla
	rla
	rla
	rla
	.endif
	.elseif	\p == 1
	.if	\q == 0
	rra
	rra
	rra
	.elseif	\q == 2
	rla
	rla
	.elseif	\q == 3
	rra
	rra
	.else
	rla
	.endif
	.elseif	\p == 2
	.if	\q == 0
	rlca
	rlca
	rlca
	.elseif	\q == 1
	rra
	rra
	.elseif	\q == 3
	rra
	rra
	rra
	rra
	.else
	rra
	.endif
	.elseif	\p == 3
	.if	\q == 0
	rra
	.elseif	\q == 1
	rla
	rla
	.elseif	\q == 2
	rla
	rla
	rla
	rla
	.else
	rla
	rla
	rla
	.endif
	.else
	.if	\q == 0
	rra
	rra
	rra
	rra
	.elseif	\q == 1
	rra
	.elseif	\q == 2
	rla
	.else
	rra
	rra
	rra
	.endif
	.endif
	.endif
	.endm
	
; process black pegs, count
	.macro	black1	p
	ldpos	\p
	xorpos	\p
	and	MASK\p
	jp	nz,1f
	inc	c
	ld	a,b
	or	BMASK\p
	ld	b,a
1:	
	.endm

; process black pegs, check
	.macro	black2	p
	ldpos	\p
	xorpos	\p
	and	MASK\p
	jp	nz,1f
	dec	c
	.if	\p < 4
	ret	m
	.else
	ret	nz
	.endif
	ld	a,b
	or	BMASK\p
	ld	b,a
1:	
	.endm

; process white pegs, count
	.macro	white1	p,q
	ld	a,b
	and	BMASK\q
	jp	nz,1f
	ldpos	\p
	alpos	\p,\q
	xorpos	\q
	and	MASK\q
	jp	nz,1f
	inc	c
	ld	a,b
	or	BMASK\q
	ld	b,a
	jp	2f
1:
	.endm

; process white pegs, check
	.macro	white2	p,q
	ld	a,b
	and	BMASK\q
	jp	nz,1f
	ldpos	\p
	alpos	\p,\q
	xorpos	\q
	and	MASK\q
	jp	nz,1f
	dec	c
	ret	m
	ld	a,b
	or	BMASK\q
	ld	b,a
	jp	2f
1:
	.endm

; ==============================================================================
; match - compare codes
; 
;   input:  HL, DE - codes
; 
;   output: C - result, (black_pegs << 3) | white_pegs
; 
;   uses:   A, B
; 
	.text
	.globl	match
match:

; prepare counter
	ld	c,0

; prepare Position 2 & bit mask
	ld	a,h
	rra
	ld	a,l
	rra
	and	0xe0
	ld	b,a

; process black pegs
	.irpc	p,"01234"
	black1	\p
	.endr
	
; update counter
	ld	a,c
	add	a,a
	add	a,a
	add	a,a
	ld	c,a
	
; save mask
	ld	a,b
	ld	(temp),a
	
; process white pegs
	.irpc	p,"01234"
	ld	a,(temp)
	and	BMASK\p
	jp	nz,2f
	.irpc	q,"01234"
	.if	\p != \q
	white1	\p,\q
	.endif
	.endr
2:
	.endr
	
; done
	ret

	.lcomm	temp, 1
	
; ==============================================================================
; check_match - compare codes and check against expected result
; 
;   input:  HL, DE - codes
;   	    A - expected result, (black_pegs << 3) | white_pegs
; 
;   output: Z on match
; 
;   uses:   A, B, C
; 
	.text
	.globl	check_match
check_match:

; prepare black pegs counter
	ld	(temp),a
	rra
	rra
	rra
	and	0x07
	ld	c,a

; prepare Position 2 & bit mask
	ld	a,h
	rra
	ld	a,l
	rra
	and	0xe0
	ld	b,a

; process black pegs
	.irpc	p,"01234"
	black2	\p
	.endr
	
; prepare white pegs counter
	ld	a,(temp)
	and	0x07
	ld	c,a

; save mask
	ld	a,b
	ld	(temp),a
	
; process white pegs
	.irpc	p,"01234"
	ld	a,(temp)
	and	BMASK\p
	jp	nz,2f
	.irpc	q,"01234"
	.if	\p != \q
	white2	\p,\q
	.endif
	.endr
2:
	.endr
	
; report match
	ld	a,c
	or	a
	ret

; ==============================================================================
; select_code - select pseudorandom code
; 
;   input:  (seed) - PRNG seed
; 
;   output: (seed) updated
;	    HL - code
; 
;   uses:   all
; 
	.text
	.globl	select_code
select_code:
	call	lcg
	ld	hl,(seed + 14)
	ld	a,h
	and	0x7f
	ld	h,a
	ret
	
; ==============================================================================
; rnd_map - populate pseudorandom transformation (mapping)
; 
;   input:  (seed) - PRNG seed
; 
;   output: (seed) updated
;	    (cmap) - color map
;	    (pmap) - position map
; 
;   uses:   all
; 
	.text
	.globl	rnd_map
rnd_map:
	call	lcg
	ld	hl,cmap
	push	hl
	ld	a,(seed + 15)
	push	af
	and	0x07
	ld	(hl),a		; 0-7
	inc	hl
	ld	a,(seed + 14)
	ld	e,a
	ld	c,7
	call	udiv8
	ld	(hl),c		; 0-6
	inc	hl
	ld	a,(seed + 13)
	ld	e,a
	ld	c,6
	call	udiv8
	ld	(hl),c		; 0-5
	inc	hl
	ld	a,(seed + 12)
	ld	e,a
	ld	c,5
	call	udiv8
	ld	(hl),c		; 0-4
	inc	hl
	pop	af
	push	af
	rra
	rra
	rra
	rra
	and	0x03
	ld	(hl),a		; 0-3
	inc	hl
	ld	a,(seed + 11)
	ld	e,a
	ld	c,3
	call	udiv8
	ld	(hl),c		; 0-2
	inc	hl
	pop	af
	rlca
	and	0x01
	ld	(hl),a		; 0-1
	inc	hl
	ld	(hl),0		; 0
	pop	de	
	ld	b,COLORS
	call	1f
	ld	hl,pmap
	push	hl
	ld	a,(seed + 10)
	ld	e,a
	ld	c,5
	call	udiv8
	ld	(hl),c		; 0-4
	inc	hl
	ld	a,(seed + 9)
	push	af
	and	0x03
	ld	(hl),a		; 0-3
	inc	hl
	ld	a,(seed + 8)
	ld	e,a
	ld	c,3
	call	udiv8
	ld	(hl),c		; 0-2
	inc	hl
	pop	af
	rlca
	and	0x01
	ld	(hl),a		; 0-1
	inc	hl
	ld	(hl),0		; 0
	pop	de
	ld	b,POSITIONS
1:	ld	hl,tperm
	push	hl
	ld	c,0
1:	ld	(hl),c
	inc	hl
	inc	c
	dec	b
	jp	nz,1b
	ex	de,hl
	pop	de
2:	push	hl
	ld	l,(hl)
	ld	h,b
	add	hl,de
1:	ld	a,(hl)
	or	a
	jp	p,1f
	inc	hl
	jp	1b
1:	ld	(hl),0xff
	pop	hl
	ld	(hl),a
	inc	hl
	dec	c
	jp	nz,2b
	ret

	.lcomm	cmap, COLORS
	.lcomm	pmap, POSITIONS
	.lcomm	tperm, COLORS
	
; ==============================================================================
; trans_code - transform code
; 
;   input:  HL - code
;	    (cmap) - color map
;	    (pmap) - position map
; 
;   output: HL - transformed code
; 
;   uses:   all
; 
	.text
	.globl	trans_code
trans_code:
	ld	de,cbuff1 + POSITIONS - 1
	ld	c,POSITIONS
1:	ld	a,h
	rra
	rra
	rra
	rra
	and	0x07
	ld	(de),a
	dec	de
	add	hl,hl
	add	hl,hl
	add	hl,hl
	dec	c
	jp	nz,1b
	inc	de
	ex	de,hl
	push	hl
	ld	de,cmap
	ld	b,0
	ld	a,POSITIONS
1:	push	af
	ld	c,(hl)
	push	hl
	ld	h,d
	ld	l,e
	add	hl,bc
	ld	a,(hl)
	pop	hl
	ld	(hl),a
	inc	hl
	pop	af
	dec	a
	jp	nz,1b
	pop	bc
	ld	hl,pmap
	ld	d,0
	ld	a,POSITIONS
1:	push	af
	ld	e,(hl)
	inc	hl
	push	hl
	ld	hl,cbuff2
	add	hl,de
	ld	a,(bc)
	inc	bc
	ld	(hl),a
	pop	hl
	pop	af
	dec	a
	jp	nz,1b
	ld	de,cbuff2
	ld	b,POSITIONS
	ld	l,0
1:	add	hl,hl
	add	hl,hl
	add	hl,hl
	ld	a,(de)
	inc	de
	or	l
	ld	l,a
	dec	b
	jp	nz,1b
	ret	

	.lcomm	cbuff1, POSITIONS
	.lcomm	cbuff2, POSITIONS
	
; ==============================================================================
; get_guess - get the tree
; 
;   input:  A - last score of 0xff if first guess requested
;	    B - attempt, 0-based
;	    (guesses) - all guesses and scores
; 
;   output: HL - guess
;	    CY if incompatible scores detected
; 
;   uses:   all
; 
	.text
	.globl	get_guess
get_guess:
	cp	0xff
	jp	nz,1f
	ld	hl,0
	ld	(lastcode),hl
	jp	search_tree
1:	push	bc
	call	search_tree
	pop	bc
	ret	nc
	push	bc
	ld	hl,msg_think
	call	disp_msg
	pop	bc
	ld	hl,(lastcode)
	ex	de,hl
3:	ld	c,b
	ld	hl,guesses
2:	push	de
	ld	e,(hl)
	inc	hl
	ld	d,(hl)
	inc	hl
	ld	a,(hl)
	ex	(sp),hl
	ex	de,hl
	push	bc
	call	check_match
	pop	bc
	pop	hl
	jp	nz,2f
	dec	c
	jp	nz,2b
	ex	de,hl
	ld	(lastcode),hl
	push	hl
	call	clr_msg
	pop	hl
	or	a		; CY = 0
	ret
2:	inc	de
	ld	a,d
	or	a
	jp	p,3b
	call	clr_msg
	scf
	ret

	.lcomm	lastcode, 2
	
	.end
