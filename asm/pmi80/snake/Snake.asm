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


; This is the old game of snake chasing mice and growing on eating one,
; adapted for PMI-80 with a 32x32 LED matrix.  The snake's movement is
; controlled by the "cursor" keys '9', '6', '4' and '1', a new game is
; started by pressing '='.  The graphics of the game is truly amazing ;-)
; Enjoy!

	page	0
	cpu	8080
	
	org	2000h

; initialization
	lxi	sp, 0
	call	init8255

; create splash screen
	xra	a
	call	setall
	mvi	e, 1
	lxi	h, snakeseq
	mvi	d, 16
	call	setseq
	lxi	h, versionseq
	mvi	d, 0
	call	setseq
	mvi	c, COLS - 1
l3:	mvi	a, 1
	mvi	b, 4
	call	setled
	mvi	b, 28
	call	setled
	push	b
	mvi	a, COLS - 1
	sub	c
	mov	c, a
	mvi	a, 1
	mvi	b, 2
	call	setled
	mvi	b, 26
	call	setled
	pop	b
	lxi	d, 250
l4:	dcx	d
	mov	a, d
	ora	e
	jnz	l4
	dcr	c
	jp	l3

; wait for '=' while gathering entropy
	lxi	h, presseq
	call	asc2buf
l14:	call	wfk
	cpi	'='
	jnz	l14
	
; clear display
	xra	a
	call	setall
	
; initialize snake
	call	getpos
	lxi	h, snake
	shld	head
	shld	tail
	mov	m, b
	inx	h
	mov	m, c
	lxi	h, 1
	shld	len
	shld	lenbcd

; display snake's initial position (head==tail)
	lxi	h, snake
	mov	b, m
	inx	h
	mov	c, m
	mvi	a, 1
	call	setled

; reset direction
	lxi	h, 0
	shld	dir

; pause before displaying mouse
	mvi	a, 40
l12:	push	psw
	call	fixseed
	call	lcg
	pop	psw
	dcr	a
	jnz	l12

; display mouse, display length and wait for initial direction
	call	newmouse
	call	dlen
l13:	call	refk
	call	setdir
	jnz	l13

; main loop
loop:
	
; get head
	lhld	head
	mov	b, m
	inx	h
	mov	c, m
	inx	h

; calculate new position
	lda	dir
	add	b
	cpi	-1
	jz	oops
	cpi	ROWS
	jz	oops
	mov	b, a
	lda	dir + 1
	add	c
	cpi	-1
	jz	oops
	cpi	COLS
	jz	oops
	mov	c, a

; check collision with snake
	push	h
	push	b
	call	checksnake
	pop	b
	pop	h
	jc	oops
	
; update head
	call	wraphl
	shld	head
	mov	m, b
	inx	h
	mov	m, c

; check collision with mouse
	lda	mouse
	cmp	b
	jnz	l17
	lda	mouse + 1
	cmp	c
	jnz	l17

; mouse eaten, create new mouse and nudge counter
	call	newmouse
	lxi	h, lenbcd
	mvi	c, 2
	call	incbcd
	lhld	len
	inx	h
	shld	len
	call	dlen
	jmp	l18

; no mouse eaten, turn on new head, turn off and remove tail
l17:	mvi	a, 1
	call	setled
	lhld	tail
	mov	b, m
	inx	h
	mov	c, m
	inx	h
	call	wraphl
	shld	tail
	xra	a
	call	setled

; pause while waiting for new direction
l18:	mvi	e, 100
l15:	call	refk
	call	setdir
	dcr	e
	jnz	l15
	jmp	loop

; create oops screen and wait for '=' before the main loop
oops:	mvi	a, 1
	call	setall
	lxi	h, oopsseq
	lxi	d, 0
	call	setseq
	jmp	l14
	
; ==============================================================================
; Constants
	
DISPLEN	equ	9	; display length (equal to number of keyboard columns)

; board size
ROWS	equ	32
COLS	equ	32
AREA	equ	ROWS * COLS
MASK	equ	0f7h

PORTA	port	0f8h	; port A
PORTB	port	0f9h	; port B
PORTC	port	0fah	; port C
CPORT	port	0fbh	; control port
	
ROWPORT	port	0ch
COLPORT	port	0dh
LEDPORT	port	0eh

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
; setseq - turn set of LEDs on/off
; 
;   input:  (HL) - sequence to be procesed, (row,col), 0ffh-terminated
;           D - delay (0 = none)
;           E - =0 off
;               =1 on
;   uses:   A, B, C, H, L
;
	section setseq
	public setseq
setseq:
	push	d
l2:	dcr	d
	jm	l3
	xra	a
l4:	dcr	a
	jnz	l4
	jmp	l2
l3:	pop	d
	mov	a, m
	cpi	0ffh
	rz
	mov	b, m
	inx	h
	mov	c, m
	inx	h
	mov	a, e
	call	setled
	jmp	setseq

	endsection setseq
	
; ==============================================================================
; getpos - get pseudo-random position
; 
;   input:  seed - seed for LCG (may be 0)
;   output: seed - new seed
;           B - row
;           C - column
;   uses:   A, D, E, H, L
;
	section getpos
	public getpos
getpos:	
	call	fixseed
	call	lcg
	lxi	h, seed
	mov	a, m
	ani	ROWS - 1
	mov	b, a
	inx	h
	mov	a, m
	ani	COLS - 1
	mov	c, a
	ret

	endsection getpos
	
; ==============================================================================
; newmouse - create and display a new mouse not colliding with the snake
; 
;   input:  seed - seed for LCG (may be 0)
;   output: seed - new seed
;           mouse - mouse position
;   uses:   A, B, C, D, E, H, L
;
	section newmouse
	public newmouse
newmouse:
	call	getpos
	call	checksnake
	jc	newmouse
	lxi	h, mouse
	mov	m, b
	inx	h
	mov	m, c
	mvi	a, 1
	jmp	setled

	endsection newmouse
	
; ==============================================================================
; dlen - put length to display buffer
; 
;   input:  lenbcd - length in BCD
;   output: buffer - display buffer
;   uses:   A, B, C, D, E, H, L
;
	section dlen
	public dlen
dlen:
	lxi	h, lenbcd
	mov	c, m
	inx	h
	mov	b, m
	lxi	h, ph
	call	word2hex
	lxi	h, lendisp
	jmp	asc2buf

	endsection dlen
	
; ==============================================================================
; setdir - set direction
; 
;   input:  A - ASCII code of key
;   output: dir - new direction
;           Z - match found (command possibly ignored
;           NZ - no match found
;   uses:   A, B, H, L
;
	section setdir
	public setdir
setdir:
	mov	b, a
	lxi	h, tbl
l2:	mov	a, m
	inx	h
	ora	a
	jnz	l1
	inr	a
	ret
l1:	cmp	b
	jz	l3
	inx	h
	inx	h
	jmp	l2
l3:	lda	dir
	xra	m
	cpi	0feh
	rz
	mov	a, m
	sta	dir
	inx	h
	lda	dir + 1
	xra	m
	cpi	0feh
	rz
	mov	a, m
	sta	dir + 1
	xra	a
	ret
	
tbl:	db	'9', -1, 0
	db	'6', 0, 1
	db	'1', 1, 0
	db	'4', 0, -1
	db	0	
	
	endsection setdir

; ==============================================================================
; checksnake - check if snake occupies cell
; 
;   input:  B - row
;	    C - column
;   output: CY - if cell is occupied
;   uses:   A, D, E, H, L
;
	section checksnake
	public checksnake
checksnake:
	lhld	len
	xchg			; length -> DE
	lhld	tail		; tail -> HL
l2:	mov	a, m
	inx	h
	cmp	b
	jnz	l1
	mov	a, m
	cmp	c
	stc
	rz
l1:	dcx	d
	mov	a, d
	ora	e		; always resets CY
	rz
	inx	h
	call	wraphl
	jmp	l2
	
	endsection checksnake
	
; ==============================================================================
; wraphl - wrap HL if past snake buffer
; 
;   input:  HL - pointer to snake buffer
;   output: HL - fixed
;   uses:   A
;
	section wraphl
	public wraphl
wraphl:
	push	h
	push	d
	lxi	d, -(snake + (AREA * 2) - 1)
	dad	d
	pop	d
	pop	h
	rnc
	lxi	h, snake
	ret

	endsection wraphl
	
; ==============================================================================
; fixseed - make sure the seed is not 0
; 
;   input:  seed - current seed
;   output: seed - new seed
;   uses:   A, H, L
;
	section fixseed
	public fixseed
fixseed:
	lhld	seed
	mov	a, h
	ora	l
	lhld	seed + 2
	ora	h
	ora	l
	rnz
	mvi	a, 1
	sta	seed
	ret

	endsection fixseed

; ==============================================================================
; lcg - carry out one LCG iteration
; 
;   input:  seed - current seed (may not be 0)
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
; wfk - display buffer & wait for key (returns on release)
; refk - refresh buffer & check if key pressed
; 
;   input:  dispbuf - display buffer
;           seed - seed for LCG
;   output: A - ASCII code of key or 0 if none pressed (only refk)
;           seed - new seed
;   uses:   B, C, D (only refk), H, L
;
	section wfk
	public wfk, refk
wfk:
	mvi	c, DISPLEN - 1
	lxi	h, dispbuf + DISPLEN - 1
l1:	call	l11
	jnz	l2
	dcx	h
	dcr	c
	jp	l1
	jmp	wfk
l2:	mov	b, c
	push	psw
l3:	mvi	c, DISPLEN - 1
	lxi	h, dispbuf + DISPLEN - 1
l5:	call	l12
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
l10:	mov	b, a
	dcr	a
	ana	b
	sub	b
	cma
	inr	a
	ora	c
	mov	c, a
	lxi	h, scancodes - 1
l7:	inx	h
	mov	a, m
	inx	h
	cmp	c
	jnz	l7
	mov	a, m
	ret
l11:	call	l12
	push	h
	lhld	seed
	inx	h
	shld	seed
	pop	h
	in	PORTC
	cma
	ani	70h
	ret
l12:	xra	a
	out	PORTC
	mov	a, m
	out	PORTA
	mov	a, c
	cma
	out	PORTC
	ret
refk:
	mvi	c, DISPLEN - 1
	mvi	b, 0ffh
	lxi	h, dispbuf + DISPLEN - 1
l9:	call	l11
	jz	l8
	mov	b, c
	mov	d, a
l8:	dcx	h
	dcr	c
	jp	l9
	xra	a
	out	PORTC
	inr	b
	rz
	dcr	b
	mov	c, b
	mov	a, d
	jmp	l10
	
scancodes:
	db	20h, '2'
	db	40h, '0'
	db	11h, 'S'	; S
	db	21h, '6'
	db	41h, '4'
	db	12h, 'L'	; L
	db	22h, 'a'
	db	42h, '8'
	db	23h, 'R'	; R
	db	43h, 'X'	; EX
	db	14h, 'B'	; BR
	db	24h, 'f'
	db	44h, 'd'
	db	15h, 'M'	; M
	db	25h, 'e'
	db	45h, 'c'
	db	26h, 'b'
	db	46h, '9'
	db	27h, '7'
	db	47h, '5'
	db	18h, '='
	db	28h, '3'
	db	48h, '1'

	endsection wfk

; ==============================================================================
; asc2buf - convert ascii data to display buffer
; 
;   input:  hl - pointer to ascii data
;   output: dispbuf - display buffer
;   uses:   A, B, C, D, E, H, L
;
	section asc2buf
	public asc2buf
asc2buf:
	mvi	d, 0
	lxi	b, dispbuf
l1:	mov	e, m
	push	h
	lxi	h, a2btbl - 20h
	dad	d
	mov	a, m
	pop	h
	stax	b
	inx	b
	inx	h
	mov	a, c
	cpi	(dispbuf + DISPLEN) & 0ffh
	jnz	l1
	ret
	
UNDEF	equ	00h ! 7fh	; characters that cannot be represented on SSD
				; are shown as blanks
a2btbl:	db	00h ! 7fh	; space
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
; Fixed data

presseq:
	db	' ', 'P', 'r', 'E', 'S', 'S', ' ', '=', ' '

lendisp:
	db	'L', 'E', 'M', '='
ph:	db	' ', ' ', ' ', ' ', ' '

snakeseq:
	; S
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

versionseq:
	; 1
	db	17, 11, 17, 12, 18, 12, 19, 12, 20, 12, 21, 12, 22, 12, 23, 11
	db	23, 12, 23, 13
	; .
	db	23, 15
	; 0
	db	22, 17, 21, 17, 20, 17, 19, 17, 18, 17, 17, 18, 17, 19, 17, 20
	db	18, 21, 19, 21, 20, 21, 21, 21, 22, 21, 23, 20, 23, 19, 23, 18
	db	21, 18, 20, 19, 19, 20
	db	0ffh

oopsseq:
	; O
	db	12, 5, 12, 6, 12, 7, 13, 8, 14, 8, 15, 8, 16, 8, 17, 8
	db	18, 7, 18, 6, 18, 5, 17, 4, 16, 4, 15, 4, 14, 4, 13, 4
	; O
	db	12, 11, 12, 12, 12, 13, 13, 14, 14, 14, 15, 14, 16, 14, 17, 14
	db	18, 13, 18, 12, 18, 11, 17, 10, 16, 10, 15, 10, 14, 10, 13, 10
	; P
	db	18, 16, 17, 16, 16, 16, 15, 16, 14, 16, 13, 16, 12, 16, 12, 17
	db	12, 18, 12, 19, 13, 20, 14, 20, 15, 19, 15, 18, 15, 17
	; S
	db	18, 22, 18, 23, 18, 24, 18, 25, 17, 26, 16, 26, 15, 25, 15, 24
	db	15, 23, 14, 22, 13, 22, 12, 23, 12, 24, 12, 25, 12, 26
	; !
	db	18, 28, 16, 28, 15, 28, 14, 28, 13, 28, 12, 28
	db	0ffh

; ==============================================================================
; Variable data

dispbuf:
	ds	DISPLEN		; display buffer

seed:	ds	4		; LCG seed
mul1:	ds	4		; LCG aux registers
mul2:	ds	4
	
head:	ds	2		; position of snake's head
tail:	ds	2		; position of snake's tail
len:	ds	2		; length of snake
lenbcd:	ds	2		; the same in BCD
	
dir:	ds	2		; direction of snake's movement

mouse:	ds	2		; mouse position

snake:	ds	2 * AREA	; snake data
	
	end
