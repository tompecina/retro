; Snake.asm
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


	page	0
	cpu	8080
	
	org	2000h

; initialization
	lxi	sp, 0
	call	init8255

; create splash screen
	xra	a
	call	setall
	lxi	h, l6
	mvi	d, 18
	call	l7
	lxi	h, l9
	mvi	d, 0
	call	l7
	mvi	c, COLS - 1
l3:	mvi	b, 4
	mvi	a, 1
	call	setled
	push	b
	mvi	a, COLS - 1
	sub	c
	mov	c, a
	mvi	b, 26
	mvi	a, 1
	call	setled
	pop	b
	lxi	d, 1000
l4:	dcx	d
	mov	a, d
	ora	e
	jnz	l4
	dcr	c
	jp	l3
	jmp	l5
l7:	mov	e, d
l11:	dcr	e
	jm	l8
	xra	a
l10:	dcr	a
	jnz	l10
	jmp	l11
l8:	mov	a, m
	cpi	0ffh
	rz
	mov	b, m
	inx	h
	mov	c, m
	inx	h
	mvi	a, 1
	call	setled
	jmp	l7

l6:	; S
	db	7, 6, 7, 5, 7, 4, 7, 3, 8, 2, 9, 2, 10, 3, 10, 4
	db	10, 5, 11, 6, 12, 6, 13, 5, 13, 4, 13, 3, 13, 2
	; n
	db	13, 8, 12, 8, 11, 8, 10, 8, 9, 8, 10, 9, 9, 10, 9, 11
	db	10, 12, 11, 12, 12, 12, 13, 12
	; a
	db	9, 15, 9, 16, 9, 17, 10, 18, 11, 18, 12, 18, 13, 18, 13, 17
	db	13, 16, 13, 15, 12, 14, 11, 15, 11, 16, 11, 17
	; k
	db	13, 20, 12, 20, 11, 20, 10, 20, 9, 20, 8, 20, 7, 20, 13, 23
	db	12, 23, 11, 22, 11, 21, 10, 23, 9, 23
	; e
	db	13, 29, 13, 28, 13, 27, 13, 26, 12, 25, 11, 25, 10, 25, 9, 26
	db	9, 27, 9, 28, 10, 29, 11, 29, 11, 28, 11, 27, 11, 26
	db	0ffh

l9:	; 1
	db	17, 11, 17, 12, 18, 12, 19, 12, 20, 12, 21, 12, 22, 12, 23, 11
	db	23, 12, 23, 13
	; .
	db	23, 15
	; 0
	db	22, 17, 21, 17, 20, 17, 19, 17, 18, 17, 17, 18, 17, 19, 17, 20
	db	18, 21, 19, 21, 20, 21, 21, 21, 22, 21, 23, 20, 23, 19, 23, 18
	db	21, 18, 20, 19, 19, 20
	db	0ffh
;;------------------------------------------------------------------------------

; gather entropy from user input
l5:	lxi	h, presseq
	call	asc2buf
	call	wfk
	lxi	h, seed
	mov	a, m
	ori	1
	mov	m, a		; seed may not be 0
	
l2:	call	lcg
	lxi	h, seed
	mov	a, m
	ani	ROWS - 1
	mov	b, a
	inx	h
	mov	a, m
	ani	COLS - 1
	mov	c, a
	inx	h
	mov	a, m
	ani	ROWS - 1
	mov	d, a
	inx	h
	mov	a, m
	ani	COLS - 1
	mov	e, a
	cmp	c
	jnz	l1
	mov	a, b
	cmp	d
	jz	l2
	
l1:	
	
	
	hlt
presseq:
	db	' ', 'P', 'r', 'E', 'S', 'S', ' ', '=', ' '

	
;; ; generate pseudo-random board
;; 	mvi	a, 4h
;; 	lxi	h, seed
;; 	mvi	c, 4
;; 	call	fill8
;; 	lxi	h, board
;; 	lxi	b, (ROWS * COLS) / 4
;; ;; l1:	push	b
;; 	push	h
;; 	call	lcg
;; 	pop	h
;; 	pop	b
;; 	lda	seed
;; 	call	setcell
;; 	lda	seed + 1
;; 	call	setcell
;; 	lda	seed + 2
;; 	call	setcell
;; 	lda	seed + 3
;; 	call	setcell
;; 	dcx	b
;; 	mov	a, b
;; 	ora	c
;; 	jnz	l1

;; 	call	board2temp1
;; back:	call	nextgen

;; ;; ROWPORT	port	0ch
;; ;; COLPORT	port	0dh
;; ;; LEDPORT	port	0eh
	
;; 	lxi	h, temp1 + COLS + 3
;; 	mvi	b, 0
;; l9:	mov	a, b
;; 	out	ROWPORT
;; 	mvi	c, 0
;; l10:	mov	a, c
;; 	out	COLPORT
;; 	mov	a, m
;; 	out	LEDPORT
;; 	inx	h
;; 	inr	c
;; 	mov	a, c
;; 	cpi	COLS
;; 	jnz	l10
;; 	inx	h
;; 	inx	h
;; 	inr	b
;; 	mov	a, b
;; 	cpi	ROWS
;; 	jnz	l9

;; 	;; lxi	h, test
;; 	;; call	asc2buf
;; 	;; call	wfk

;; 	jmp	back

;; setcell:
;; 	cpi	50		; threshhold
;; 	mvi	a, 0
;; 	ral
;; 	mov	m, a
;; 	inx	h
;; 	ret

;; board2temp1:
;; 	lxi	h, temp1
;; 	lxi	b, AREA
;; 	call	zero16
;; 	lxi	h, board
;; 	lxi	d, temp1 + COLS + 3
;; 	mvi	b, ROWS
;; ;; l2:	mvi	c, COLS
;; 	call	copy8
;; 	inx	d
;; 	inx	d
;; 	dcr	b
;; 	jnz	l2
;; 	ret

;; nextgen:
;; 	lxi	h, temp1
;; 	lxi	d, temp2
;; 	lxi	b, AREA
;; 	call	copy16
;; 	lxi	d, temp1 + COLS + 3
;; 	lxi	h, temp2
;; 	mvi	b, ROWS
;; ;; l4:	mvi	c, COLS
;; ;; l3:	push	d
;; 	mov	a, m
;; 	inx	h
;; 	add	m
;; 	inx	h
;; 	add	m
;; 	lxi	d, ROWS
;; 	dad	d
;; 	add	m
;; 	inx	h
;; 	inx	h
;; 	add	m
;; 	dad	d
;; 	add	m
;; 	inx	h
;; 	add	m
;; 	inx	h
;; 	add	m
;; 	lxi	d, -(ROWS * 2 + 5)
;; 	dad	d
;; 	pop	d
;; 	stax	d
;; 	inx	d
;; 	dcr	c
;; 	jnz	l3
;; 	inx	d
;; 	inx	d
;; 	inx	h
;; 	inx	h
;; 	dcr	b
;; 	jnz	l4
;; 	lxi	h, temp1
;; 	lxi	d, temp2
;; 	lxi	b, AREA
;; ;; l5:	mov	a, m
;; 	cpi	3
;; 	jnz	l6
;; 	mvi	a, 1
;; 	jmp	l8	
;; ;; l6:	cpi	2
;; 	ldax	d
;; 	jz	l8
;; 	xra	a
;; ;; l8:	mov	m, a
;; ;; l7:	inx	h
;; 	inx	d
;; 	dcx	b
;; 	mov	a, b
;; 	ora	c
;; 	jnz	l5
;; 	ret
	
	

;; again:	
;; 	lxi	h, ASCBUF
;; 	call	dwv
;; 	db	'r'
;; 	dw	0
;; 	db	'C'
;; 	dw	loop
;; 	db	0
;; 	dw	again

	
;; loop:	lxi	h, ASCBUF
;; 	call	asc2buf
;; 	call	wfk
;; 	sta	ASCBUF + 3
;; 	lxi	h, ASCBUF
;; 	call	byte2hex
;; 	jmp	loop
	
;; 	mvi	d, 0
;; loop:	mov	a, d
;; 	lxi	h, ASCBUF
;; 	call	byte2hex
;; 	lxi	h, ASCBUF
;; 	push	d
;; 	call	asc2buf
;; 	call	wfk
;; 	pop	d
;; 	inr	d
;; 	jmp	loop

ASCBUF:	db	' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '

	;; mvi	a, 30h
	;; mov	b, a
	;; dcr	a
	;; ana	b
	;; sub	b
	;; cma
	;; inr	a
	;; jmp	2000h
	

;; 	lxi	h, test
;; 	call	asc2buf
;; loop:	call	wfk
;; 	jmp	loop
	
;; l1:	lxi	h, DISPBUF
;; 	mvi	b, 0
;; 	mvi	c, DISPLEN
;; l2:	xra	a
;; 	out	0fah
;; 	mov	a, m
;; 	out	0f8h
;; 	mov	a, b
;; 	cma
;; 	out	0fah
;; 	inx	h
;; 	inr	b
;; 	dcr	c
;; 	jnz	l2
;; 	jmp	l1
	
	
test:	db	'T', 'E', 'S', 'T', ' ', '0', '0', '1', ' '

; ==============================================================================
; Constants
	
DISPLEN	equ	9		; display length (equal to number of keyboard columns)

ROWS	equ	32
COLS	equ	32
AREA	equ	(ROWS + 2) * (COLS + 2)
	
PORTA	port	0f8h		; port A
PORTB	port	0f9h		; port B
PORTC	port	0fah		; port C
CPORT	port	0fbh		; control port
	
ROWPORT	port	0ch
COLPORT	port	0dh
LEDPORT	port	0eh

; ==============================================================================
; lcg - carry out one LCG iteration
; 
;   input:  seed - current seed
;   output: seed - new seed
;   uses:   A, B, C, D, E, H, L
;
	section lcg
	public lcg
lcg:
	lxi	h, consta
	lxi	d, mul1
	mvi	c, 4
	call	copy8
	lxi	h, seed
	lxi	d, mul2
	mvi	c, 4
	call	copy8
	lxi	h, constc
	lxi	d, seed
	mvi	c, 4
	call	copy8
	mvi	b, 32
l7:	mvi	c, 4
	lxi	h, mul2 + 3
	ora	a
l1:	mov	a, m
	rar
	mov	m, a
	dcx	h
	dcr	c
	jnz	l1
	jnc	l2
	lxi	h, seed
	lxi	d, mul1
	mvi	c, 4
	ora	a
l3:	ldax	d
	adc	m
	mov	m, a
	inx	h
	inx	d
	dcr	c
	jnz	l3
l2:	lxi	h, mul1
	mvi	c, 4
	ora	a
l4:	mov	a, m
	ral
	mov	m, a
	inx	h
	dcr	c
	jnz	l4
	dcr	b
	jnz	l7
	ret
consta:	db	0dh, 66h, 19h, 00h 		; 1664525
constc:	db	5fh, 0f3h, 6eh, 3ch		; 1013904223

	endsection lcg

; ==============================================================================
; init8255 - initialize PPI1
; 
;   uses:   A
;
	section init8255
	public init8255
init8255:
	mvi	a, 8ah
	out	CPORT
	ret

	endsection init8255

; ==============================================================================
; fill8 - fill block of memory with byte (max. 256 bytes)
; zero8 - zero block of memory (max. 256 bytes)
; 
;   input:  HL - pointer to memory
;	    A - byte to fill
;	    C - number of bytes (00 = 256 bytes)
;   output: (HL) - filled memory
;   uses:   A, C, H, L
;
	section fill8
	public fill8, zero8
zero8:
	xra	a
fill8:
	mov	m, a
	inx	h
	dcr	c
	jnz	fill8
	ret
	
	endsection fill8

; ==============================================================================
; copy8 - copy block of memory (max. 256 bytes)
; 
;   input:  HL - pointer to source
;	    DE - pointer to destination
;	    C - number of bytes (00 = 256 bytes)
;   output: (DE) - copied data
;	    HL, DE incremented 
;   uses:   A, C
;
	section copy8
	public copy8
copy8:
	mov	a, m
	stax	d
	inx	h
	inx	d
	dcr	c
	jnz	copy8
	ret
	
	endsection copy8

; ==============================================================================
; fill16 - fill block of memory with byte
; zero16 - zero block of memory
; 
;   input:  HL - pointer to memory
;	    A - byte to fill
;	    BC - number of bytes
;   output: (HL) - filled memory
;   uses:   A, B, C, D, H, L
;
	section fill16
	public fill16, zero16
zero16:
	xra	a
fill16:
	mov	d, a
l1:	mov	m, d
	inx	h
	dcx	b
	mov	a, b
	ora	c
	jnz	l1
	ret

	endsection fill16

; ==============================================================================
; copy16 - copy block of memory
; 
;   input:  HL - pointer to source
;	    DE - pointer to destination
;	    BC - number of bytes
;   output: (DE) - copied data
;	    HL, DE incremented 
;   uses:   A, B, C
;
	section copy16
	public copy16
copy16:
	mov	a, m
	stax	d
	inx	h
	inx	d
	dcx	b
	mov	a, b
	ora	c
	jnz	copy16
	ret
	
	endsection copy16

; ==============================================================================
; word2hex - convert word to hex representation
; 
;   input:  BC - data
;	    HL - pointer to buffer 
;   output: (HL) - converted data
;	    HL incremented 
;   uses:   A
;
	section word2hex
	public word2hex
word2hex:
	mov	a, b
	call	byte2hex
	mov	a, c
	
	endsection word2hex

; ==============================================================================
; byte2hex - convert byte to hex representation
; 
;   input:  A - data
;	    HL - pointer to buffer 
;   output: (HL) - converted data
;	    HL incremented 
;   uses:   A
;
	section byte2hex
	public byte2hex
byte2hex:
	push	psw
	rar
	rar
	rar
	rar
	call	nibble2hex
	pop	psw
	
	endsection byte2hex

; ==============================================================================
; nibble2hex - convert nibble to hex representation
; 
;   input:  A - data
;	    HL - pointer to buffer 
;   output: (HL) - converted data
;	    HL incremented 
;   uses:   A
;
	section nibble2hex
	public nibble2hex
nibble2hex:
	ani	0fh
	cpi	10
	jc	l1
	adi	'A' - '0' - 10
l1:	adi	'0'
	mov	m, a
	inx	h
	ret
	
	endsection nibble2hex

; ==============================================================================
; incbcd - increment BCD integer
; 
;   input:  (HL) - pointer to data, stored little-endian
;           C - byte-width of data
;   output: (HL) - incremented data
;   uses:   A, C, H, L
;
	section incbcd
	public incbcd
incbcd:
	xra	a
	mvi	a, 1
l1:	adc	m
	daa
	mov	m, a
	inx	h
	dcr	c
	rz
	mvi	a, 0
	jmp	l1
	
	endsection incbcd

; ==============================================================================
; setled - turn LED on/off
; 
;   input:  B - row
;           C - column
;           A - status (bit 0 = off/on)
;   uses:   A
;
	section setled
	public setled
setled:
	push	psw
	mov	a, b
	out	ROWPORT
	mov	a, c
	out	COLPORT
	pop	psw
	ani	1
	out	LEDPORT
	ret
	
	endsection setled

; ==============================================================================
; setall - turn all LEDs on/off
; 
;   input:  A - status (bit 0 = off/on)
;   uses:   A, B, C
;
	section setall
	public setall
setall:
	ani	1
	mvi	b, ROWS - 1
l2:	mvi	c, COLS - 1
l1:	push	psw
	mov	a, b
	out	ROWPORT
	mov	a, c
	out	COLPORT
	pop	psw
	out	LEDPORT
	dcr	c
	jp	l1
	dcr	b
	jp	l2
	ret
	
	endsection setall

; ==============================================================================
; dwv - display buffer, wait for key & follow vector table
; 
;   input:  (HL) - pointer to ASCII data
;           stack - pointer to vector table:
;                     1 byte keycode, 2 bytes vector
;                     keycode 0 = default vector
;   output: jump to vector
;   uses:   A, B, C, D, E, H, L
;
	section dwv
	public dwv
dwv:
	call	asc2buf
	call	wfk
	mov	b, a
	pop	h
l2:	mov	a, m
	inx	h
	mov	e, m
	inx	h
	mov	d, m
	inx	h
	ora	a
	jz	l1
	cmp	b
	jnz	l2
l1:	xchg
	pchl
	
	endsection dwv

; ==============================================================================
; wfk - display buffer & wait for key (returns on release)
; 
;   input:  (DISPBUF) - display buffer
;   output: A - ASCII code of key
;           (seed) - pseudo-random value
;   uses:   B, C, H, L
;
	section wfk
	public wfk
wfk:
	mvi	c, DISPLEN - 1
	lxi	h, DISPBUF + DISPLEN - 1
l1:	xra	a
	out	PORTC
	mov	a, m
	out	PORTA
	mov	a, c
	cma
	out	PORTC
	push	h
	lhld	seed
	inx	h
	shld	seed
	pop	h
	in	PORTC
	cma
	ani	70h
	jnz	l2
	dcx	h
	dcr	c
	jp	l1
	jmp	wfk
l2:	mov	b, c
	push	psw
l3:	mvi	c, DISPLEN - 1
	lxi	h, DISPBUF + DISPLEN - 1
l5:	xra	a
	out	PORTC
	mov	a, m
	out	PORTA
	mov	a, c
	cma
	out	PORTC
	mov	a, b
	cmp	c
	jnz	l4
	in	PORTC
	push	h
	lhld	seed + 2
	inx	h
	shld	seed + 2
	pop	h
	cma
	ani	70h
	jz	l6
l4:	dcx	h
	dcr	c
	jp	l5
	jmp	l3
l6:	xra	a
	out	PORTC
	pop	psw	
	mov	b, a
	dcr	a
	ana	b
	sub	b
	cma
	inr	a
	ora	c
	mov	c, a
	lxi	h, SCANCODES - 1
l7:	inx	h
	mov	a, m
	inx	h
	cmp	c
	jnz	l7
	mov	a, m
	ret
	
SCANCODES:
	db	20h, '2'
	db	40h, '0'
	db	11h, 's'	; S
	db	21h, '6'
	db	41h, '4'
	db	12h, 'l'	; L
	db	22h, 'A'
	db	42h, '8'
	db	23h, 'r'	; R
	db	43h, 'e'	; EX
	db	14h, 'b'	; BR
	db	24h, 'F'
	db	44h, 'D'
	db	15h, 'm'	; M
	db	25h, 'E'
	db	45h, 'C'
	db	26h, 'B'
	db	46h, '9'
	db	27h, '7'
	db	47h, '5'
	db	18h, '='
	db	28h, '3'
	db	48h, '1'

	endsection wfk

; ==============================================================================
; asc2buf - convert ASCII data to display buffer
; 
;   input:  HL - pointer to ASCII data
;   output: (DISPBUF) - display buffer
;   uses:   A, B, C, D, E, H, L
;
	section asc2buf
	public asc2buf
asc2buf:
	mvi	d, 0
	lxi	b, DISPBUF
l1:	mov	e, m
	push	h
	lxi	h, A2BTBL - 20h
	dad	d
	mov	a, m
	pop	h
	stax	b
	inx	b
	inx	h
	mov	a, c
	cpi	(DISPBUF + DISPLEN) & 0ffh
	jnz	l1
	ret
	
UNDEF	equ	00h ! 7fh	; characters that cannot be represented on SSD are shown as blank
A2BTBL:	db	00h ! 7fh	; space
	db	UNDEF		; !
	db	22h ! 7fh	; "
	db	UNDEF		; #
	db	UNDEF		; $
	db	UNDEF		; %
	db	UNDEF		; &
	db	20h ! 7fh	; '
	db	39h ! 7fh	; (
	db	0fh ! 7fh	; )
	db	UNDEF		; *
	db	UNDEF		; +
	db	10h ! 7fh	; ,
	db	40h ! 7fh	; -
	db	UNDEF		; .
	db	UNDEF		; /
	db	3fh ! 7fh	; 0
	db	06h ! 7fh	; 1
	db	5bh ! 7fh	; 2
	db	4fh ! 7fh	; 3
	db	66h ! 7fh	; 4
	db	6dh ! 7fh	; 5
	db	7dh ! 7fh	; 6
	db	07h ! 7fh	; 7
	db	7fh ! 7fh	; 8
	db	6fh ! 7fh	; 9
	db	UNDEF		; :
	db	UNDEF		; ;
	db	UNDEF		; <
	db	48h ! 7fh	; =
	db	UNDEF		; >
	db	53h ! 7fh	; ?
	db	3bh ! 7fh	; @
	db	77h ! 7fh	; A
	db	7ch ! 7fh	; B
	db	39h ! 7fh	; C
	db	5eh ! 7fh	; D
	db	79h ! 7fh	; E
	db	71h ! 7fh	; F
	db	3dh ! 7fh	; G
	db	76h ! 7fh	; H
	db	06h ! 7fh	; I
	db	1fh ! 7fh	; J
	db	UNDEF		; K
	db	38h ! 7fh	; L
	db	37h ! 7fh	; M
	db	UNDEF		; N
	db	3fh ! 7fh	; O
	db	73h ! 7fh	; P
	db	UNDEF		; Q
	db	50h ! 7fh	; R
	db	6dh ! 7fh	; S
	db	78h ! 7fh	; T
	db	3eh ! 7fh	; U
	db	UNDEF		; V
	db	UNDEF		; W
	db	76h ! 7fh	; X
	db	6eh ! 7fh	; Y
	db	5bh ! 7fh	; Z
	db	39h ! 7fh	; [
	db	UNDEF		; backslash
	db	0fh ! 7fh	; ]
	db	01h ! 7fh	; ^
	db	08h ! 7fh	; _
	db	02h ! 7fh	; `
	db	5fh ! 7fh	; a
	db	7ch ! 7fh	; b
	db	58h ! 7fh	; c
	db	5eh ! 7fh	; d
	db	UNDEF		; e
	db	UNDEF		; f
	db	6fh ! 7fh	; g
	db	74h ! 7fh	; h
	db	04h ! 7fh	; i
	db	0ch ! 7fh	; j
	db	UNDEF		; k
	db	30h ! 7fh	; l
	db	UNDEF		; m
	db	54h ! 7fh	; n
	db	5ch ! 7fh	; o
	db	UNDEF		; p
	db	67h ! 7fh	; q
	db	50h ! 7fh	; r
	db	UNDEF		; s
	db	78h ! 7fh	; t
	db	1ch ! 7fh	; u
	db	UNDEF		; v
	db	UNDEF		; w
	db	UNDEF		; x
	db	6eh ! 7fh	; y
	db	UNDEF		; z
	db	39h ! 7fh	; {
	db	30h ! 7fh	; |
	db	0fh ! 7fh	; }
	db	UNDEF		; ~
	db	63h ! 7fh	; DEL (used for degree sign)
	
	endsection asc2buf
	
; ==============================================================================
; Data

DISPBUF:
	ds	DISPLEN		; display buffer
;; seed:	ds	4
mul1:	ds	4
mul2:	ds	4

	org	3000h
board:	ds	ROWS * COLS
	org	4000h
temp1:	ds	AREA
	org	5000h
temp2:	ds	AREA
	org	6000h
seed:	ds	4
snake:	ds	2 * ROWS * COLS
	
	end
	
	;; lxi	h, 1
	;; shld	seed
	;; lxi	h, ffffh
	;; shld	seed + 2
	;; call	lcg
	;; rst	1

again:	
	call	getent

loop:
	lda	seed + 3
	mov	l, a
	lda	seed + 2
	mov	h, a
	shld	IN_ADDR
	lxi	b, VIDEORAM
	call	00beh
	lda	seed + 1
	mov	l, a
	lda	seed
	mov	h, a
	shld	IN_ADDR
	lxi	b, VIDEORAM + 4
	call	00beh
	mvi	a, 19h
	sta	VIDEORAM + 8
	lxi	h, VIDEORAM
	shld	VIDEO_POINTER
	call	OUTKE
	call	lcg
	jmp	loop
	
	rst	1
	
;; Gather some entropy and put it to seed
getent:	
	push    psw
	mov	a, h
	add	b
	mov	h, a
	mov	a, l
	add	c
	mov	l, a
	pop	b
	mov	a, h
	add	b
	mov	h, a
	mov	a, l
	add	c
	mov	l, a
	mov	a, h
	add	d
	mov	h, a
	mov	a, l
	add	e
	mov	l, a
	xchg
	lxi	h, 0
	dad	sp
	mov	a, h
	add	d
	mov	h, d
	mov	a, l
	add	e
	mov	l, a
	lxi	d, 0000h
	lxi	b, 0400h
	call	g1
	lxi	d, 1c00h
	lxi	b, 0400h
	call	g1
	push	h
	lxi	h, press_eq
	shld	VIDEO_POINTER
	call	OUTKE
	pop	h
	mov	a, h
	add	b
	add	e
	mov	h, a
	mov	a, l
	add	c
	add	d
	mov	l, a
	shld	seed
	shld	seed + 2
	call	lcg
	ret
g1:	xchg
	mov	a, e
	add	m
	mov	e, a
	inx	h
	dcx	b
	mov	a, d
	add	m
	mov	d, a
	inx	d
	dcx	b
	xchg
	mov	a, b
	ora	c
	jnz	g1
	ret
press_eq:
	db	13h, 12h, 0eh, 05h, 05h, 19h, 18h, 19h, 19h


;; LCG iteration
lcg:	lxi	h, consta
	lxi	d, mul1
	call	l5
	lxi	h, seed
	lxi	d, mul2
	call	l5
	lxi	h, constc
	lxi	d, seed
	call	l5
	mvi	b, 32
l7:	mvi	c, 4
	lxi	h, mul2 + 3
	ora	a
l1:	mov	a, m
	rar
	mov	m, a
	dcx	h
	dcr	c
	jnz	l1
	jnc	l2
	lxi	h, seed
	lxi	d, mul1
	mvi	c, 4
	ora	a
l3:	ldax	d
	adc	m
	mov	m, a
	inx	h
	inx	d
	dcr	c
	jnz	l3
l2:	lxi	h, mul1
	mvi	c, 4
	ora	a
l4:	mov	a, m
	ral
	mov	m, a
	inx	h
	dcr	c
	jnz	l4
	dcr	b
	jnz	l7
	ret
l5:	mvi	c, 4
l6:	mov	a, m
	stax	d
	inx	h
	inx	d
	dcr	c
	jnz	l6
	ret
consta:	db	0dh, 66h, 19h, 00h 		; 1664525
constc:	db	5fh, 0f3h, 6eh, 3ch		; 1013904223

STACK     	equ     1fe9h
INT_VECTOR 	equ    	1fe6h
VIDEO_POINTER	equ	1ffch
VIDEORAM  	equ     1fefh
IN_DATA   	equ     1ffah
IN_ADDR    	equ     1ff8h
STATUS		equ	1ffeh
PORTA		equ	0f8h
PORTC		equ	0fah

;; Modified OUTKE
OUTKE:
	lxi	b, 0
	mov	d, b
	mov	e, c
o2:	call    DISP
	inx	b
	jnc     o2
	rrc
	mov     h, a
o1:     call    DISP
	inx	d
	jc      o1
	mov	a, h
	ret
	
;; Modified DISP
DISP:
	push    h
	push    b
	push    d
	lxi     d, 0
	mov	b, d
	mov     a, d
	sta     STATUS
d1:     mvi     a,7fh
	out     PORTA
	mov     a, e
	cma
	out     PORTC
	lhld    VIDEO_POINTER
	dad     d
        mov     c, m
	lxi     h, TPREV
	dad     b
	mov     a, m
	out     PORTA	
	lda	STATUS
	ora     a
        jnz     d2
        mvi     c, 09h
        lxi	h, TABKEY - 9
	in      PORTC
	ani     70h
	rlc
	rlc
	jnc     d3
	rlc
	jnc     d4
	rlc
	jc      d2
	dad     b
d4:	dad     b
d3:	dad     b
	dad     d
	mov     a, m
	sta     STATUS
d2:	inr     e
	mvi     a, 0ah
	cmp     e
	jnz     d1
	lda     STATUS
	rlc
	pop     d
	pop     b
	pop     h
	ret
TABKEY:
        db	80h, 84h, 88h, 91h, 8dh, 8ch, 89h, 85h, 81h
	db      82h, 86h, 8ah, 9ah, 8fh, 8eh, 8bh, 87h, 83h
	db      0ffh, 94h, 93h, 0ffh, 97h, 92h, 0ffh, 0ffh, 90h
TPREV:
	db      40h, 79h
	db      24h, 30h, 19h, 12h, 02h, 78h, 00h, 10h, 08h, 03h, 46h, 21h
	db      06h, 0eh, 07h, 23h, 2fh, 0ch, 47h, 63h, 48h, 71h, 37h, 7fh
	db      09h, 2bh, 0bh, 2ch, 5dh, 3fh, 42h, 61h
	db      7bh, 11

	org	3000h
seed:	ds	4
mul1:	ds	4
mul2:	ds	4

	end
	
