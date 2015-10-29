; game.S
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

	.include "reversi.inc"
	
; ==============================================================================
; Constants
;
END	equ	0x01
RMASK	equ	0xb8
CMASK	equ	0x47
	
; ==============================================================================
; legal - check if move is legal
; 
;   input:  (HL) - board
;           C - row * 8 + column 
; 
;   output: CY - illegal move
; 
;   uses:   all
; 
	.global	legal
legal:
	ld	b,0
	add	hl,bc		; HL = ptr in board
	ld	a,(hl)
	or	a
	scf
	ret	nz		; not empty, illegal move
	push	hl
	ld	hl,dirs		; HL = dirs
	jp	1f
2:	pop	bc
	pop	hl
3:	ex	(sp),hl		; HL = ptr in dirs
1:	ld	a,(hl)
	ld	b,a		; B = limit
	dec	a
	jp	z,2f
	inc	hl
	ld	e,(hl)
	inc	hl
	ld	d,(hl)		; DE = delta
	inc	hl
	ex	(sp),hl		; HL = ptr in board
	ld	a,c
	xor	b
	and	RMASK
	jp	z,3b		; row limit
	ld	a,c
	xor	b
	and	CMASK
	jp	z,3b		; column limit
	push	hl
	push	bc
	add	hl,de
	ld	a,(hl)
	cp	2
	jp	nz,2b		; not opponent's disk
4:	ld	a,c
	add	a,e
	ld	c,a
	xor	b
	and	RMASK
	jp	z,2b		; row limit
	ld	a,c
	xor	b
	and	CMASK
	jp	z,2b		; column limit
	add	hl,de
	ld	a,(hl)
	dec	a
	jp	z,4f
	dec	a
	jp	nz,2b		; empty square
	jp	4b
4:	xor	a		; my disk, move is legal
	pop	bc
	pop	hl
	pop	hl
	ret
1:	pop	bc		; illegal move
	pop	hl
2:	pop	hl
	scf
	ret

; ==============================================================================
; score_board - calculate board score, based on
;               <https://github.com/kartikkukreja/blog>.
; 
;   input:  (HL) - board
; 
;   output: HL - score
; 
;   uses:   all
; 
	.global	score_board
score_board:
	
; reset variables
	ex	de,hl
	ld	hl,var
	ld	b,len
	call	zerofill
	ex	de,hl

; count all disks and frontier disks and calculate value differential
	push	hl
	ld	de,sqv		; DE = ptr in square values
	ld	c,0		; C = square offset
4:	ld	a,(hl)
	dec	a
	jp	nz,1f
	push	hl		; my disk
	ld	hl,myd
	inc	(hl)
	ex	de,hl
	ld	e,(hl)
	inc	hl
	ld	d,(hl)		; DE = square value
	inc	hl
	inc	hl
	jp	2f
1:	dec	a
	jp	nz,1f
	push	hl		; opponent's disk
	ld	hl,opd
	inc	(hl)
	ex	de,hl
	inc	hl
	inc	hl
	ld	e,(hl)
	inc	hl
	ld	d,(hl)		; DE = minus square value
2:	inc	hl
	ex	(sp),hl
	push	hl
	ld	hl,(tmp)
	add	hl,de
	ld	(tmp),hl
	ld	hl,dirs
6:	ld	b,(hl)		; B = limit
	inc	hl
	ld	a,b
	cp	END
	jp	z,3f
	xor	c
	and	RMASK
	jp	nz,2f
	inc	hl		; row limit
5:	inc	hl
	jp	6b
2:	ld	a,b
	xor	c
	and	CMASK
	jp	z,5b		; column limit
	pop	de
	push	de
	push	de
	ld	e,(hl)
	inc	hl
	ld	d,(hl)		; DE = delta
	inc	hl
	ex	(sp),hl
	add	hl,de
	ld	a,(hl)		; adjacent square
	pop	hl
	or	a
	jp	nz,6b
	pop	hl		; adjacent square is empty
	push	hl
	ld	a,(hl)
	dec	a
	ld	hl,myfd
	jp	z,5f
	inc	hl
5:	inc	(hl)		; increment frontier disks counter
3:	pop	hl
	pop	de
	jp	2f
1:	inc	de		; next square value
	inc	de
	inc	de
	inc	de
2:	inc	hl		; next square
	inc	c
	ld	a,c
	cp	64
	jp	nz,4b
	ld	hl,(tmp)
	push	hl
	ex	de,hl
	ld	hl,8
	call	div16
	pop	de
	add	hl,de
	ld	(score),hl
	ld	hl,(myd)	; L = my disks
				; H = opponent's disks
	ld	a,l
	cp	h
	jp	z,1f
	jp	c,3f
	jp	4f
3:	xor	a
	sub	h
4:	push	hl
	call	signex
	ld	de,125
	call	mul16
	ex	de,hl
	pop	hl
	ld	a,h
	add	a,l
	ld	l,a
	ld	h,0
	call	div16
	call	2f
1:	ld	hl,(myfd)	; L = my frontier disks
				; H = opponent's frontier disks
	ld	a,l
	cp	h
	jp	z,1f
	jp	c,3f
	xor	a
	sub	l
	jp	4f
3:	ld	a,h
4:	push	hl
	call	signex
	ld	de,930
	call	mul16
	ex	de,hl
	pop	hl
	ld	a,h
	add	a,l
	ld	l,a
	ld	h,0
	call	div16
	call	2f

; count corner and next-to-corner disks
1:	ld	bc,crns
3:	ld	a,(bc)
	cp	END
	jp	z,1f
	inc	bc
	ld	e,a
	ld	d,0
	pop	hl		; HL = board
	push	hl
	add	hl,de
	ld	a,(hl)		; A = corner
	ld	hl,mycd
	dec	a
	jp	z,4f
	inc	hl
4:	dec	a
	jp	nz,4f
	inc	(hl)
	jp	5f
4:	ld	h,3
6:	ld	a,(bc)
	inc	bc
	pop	de
	push	de
	push	de
	ld	e,a
	ld	d,0
	ex	(sp),hl		; HL = board
	add	hl,de
	ld	a,(hl)		; A = next-to-corner
	or	a
	jp	z,4f
	ld	hl,myncd
	dec	a
	jp	z,7f
	inc	hl
7:	inc	(hl)
4:	pop	af		; A = counter
	ld	h,a
	dec	h
	jp	nz,6b
	jp	3b
5:	inc	bc
	inc	bc
	inc	bc
	jp	3b	
1:	ld	hl,(mycd)	; L = my corner disks
				; H = opponent's corner moves
	ld	a,l
	sub	h
	call	signex
	ld	de,2505
	call	mul16
	call	2f
	ld	hl,(mycd)	; L = my next-to-corner disks
				; H = opponent's next-to-corner moves
	ld	a,h
	sub	l
	call	signex
	ld	de,597
	call	mul16
	call	2f

; count legal moves
	pop	hl
	push	hl		; HL = board
	ld	de,revb
	call	rev_board	; create reversed board
	pop	hl
	ld	de,mymd
	call	3f
	ld	hl,revb
	inc	de
	call	3f
	ld	hl,(mymd)	; L = my moves
				; H = opponent's moves
	ld	a,l
	cp	h
	jp	z,1f
	jp	c,5f
	jp	4f
5:	xor	a
	sub	h
4:	push	hl
	call	signex
	ld	de,987
	call	mul16
	ex	de,hl
	pop	hl
	ld	a,h
	add	a,l
	ld	l,a
	ld	h,0
	call	div16
	call	2f

1:	ld	hl,(score)
	ret

; score += HL
2:	ex	de,hl
	ld	hl,(score)
	add	hl,de
	ld	(score),hl
	ret
	
; count legal moves
3:	ld	c,0
2:	push	hl
	push	de
	push	bc
	call	legal
	pop	bc
	pop	hl
	pop	de
	jp	c,1f
	inc	(hl)
1:	ex	de,hl
	inc	c
	ld	a,c
	cp	64
	jp	nz,2b
	ret	
	
; ==============================================================================
; rev_board - reverse board
; 
;   input:  (HL) - board
;           (DE) - output board 
; 
;   output: (DE) - reversed board
; 
;   uses:   all
; 
	.global	rev_board
rev_board:
	ld	bc,0x0340
1:	ld	a,(hl)
	or	a
	jp	z,2f
	xor	b
2:	ld	(de),a
	inc	hl
	inc	de
	dec	c
	jp	nz,1b
	ret
	
; ==============================================================================
; Directions
; 
dirs:
	.byte	0x00
	.word	-9
	.byte	0x40
	.word	-8
	.byte	0x07
	.word	-7
	.byte	0x80
	.word	-1
	.byte	0x87
	.word	1
	.byte	0x38
	.word	7
	.byte	0x78
	.word	8
	.byte	0x3f
	.word	9
	.byte	END
	
; ==============================================================================
; Corners
; 
crns:
	.byte	0, 1, 8, 9
	.byte	7, 6, 14, 15
	.byte	49, 41, 42, 50
	.byte	63, 54, 55, 62
	.byte	END
	
; ==============================================================================
; Square values
; 
sqv:
	.word	20, -20, -3, 3, 11, -11, 8, -8, 8, -8, 11, -11, -3, 3, 20, -20
	.word	-3, 3, -7, 7, -4, 4, 1, -1, 1, -1, -4, 4, -7, 7, -3, 3
	.word	11, -11, -4, 4, 2, -2, 2, -2, 2, -2, 2, -2, -4, 4, 11, -11
	.word	8, -8, 1, -1, 2, -2, -3, 3, -3, 3, 2, -2, 1, -1, 8, -8
	.word	8, -8, 1, -1, 2, -2, -3, 3, -3, 3, 2, -2, 1, -1, 8, -8
	.word	11, -11, -4, 4, 2, -2, 2, -2, 2, -2, 2, -2, -4, 4, 11, -11
	.word	-3, 3, -7, 7, -4, 4, 1, -1, 1, -1, -4, 4, -7, 7, -3, 3
	.word	20, -20, -3, 3, 11, -11, 8, -8, 8, -8, 11, -11, -3, 3, 20, -20

; ==============================================================================
; Variables
; 
score:	ds	2
var	equ	$
myd:	ds	1
opd:	ds	1
myfd:	ds	1
opfd:	ds	1
mycd:	ds	1
opcd:	ds	1
myncd:	ds	1
opncd:	ds	1
mymd:	ds	1
opmd:	ds	1
tmp:	ds	2
len:	equ	$ - var
revb:	ds	64
	
	.end
 
