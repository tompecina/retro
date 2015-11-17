; unused, but useful (and usually debugged) stuff

; ==============================================================================
; copy16 - copy area
; 
;   input:  (HL) - source area
;           (DE) - destination area
;           BC - number of bytes (1-65536)
; 
;   uses:   all
; 
	.text
	.globl	copy16
copy16:
	ld	a,(hl)
	ld	(de),a
	inc	hl
	inc	de
	dec	bc
	ld	a,b
	or	c
	jp	nz,copy16
	ret
	
; ==============================================================================
; shrhlb - signed 16-bit right shift
; 
;   input:  HL, B
; 
;   output: HL = HL >> B
; 
;   uses:   A, B
; 
	.text
	.globl	shrhlb
shrhlb:
	ld	a,h
	rla
	ld	a,h
	rra
	ld	h,a
	ld	a,l
	rra
	ld	l,a
	dec	b
	jp	nz,shrhlb
	ret

; ==============================================================================
; udiv16_8 - unsigned 16-bit/8-bit division
; 
;   input:  HL, C
; 
;   output: HL = HL / C
;           DE = HL % C
; 
;   uses:   all
; 
	.text
	.globl	udiv16_8
udiv16_8:
        ld      de,0
	ld      b,16
1:	add     hl,hl
        call    rdel
        jp	z,2f
        ld      a,e
        sub     c
        ld      a,d
        sbc     a,0
        jp	m,2f
        ld      a,l
        or      1
        ld      l,a
        ld      a,e
        sub     c
        ld      e,a
        ld      a,d
        sbc     a,0
        ld      d,a
2:	dec     b
        jp	nz,1b
	ret
	
; ==============================================================================
; crc16 - update CRC-16-CCITT
; 
;   input:  HL - initial CRC
; 	    A - input byte
; 
;   output: HL - updated CRC
; 
;   uses:   A, B
; 
	.text
	.globl	crc16
crc16:
	xor	h
	ld	h,a
	ld	b,8
2:	add	hl,hl
	jp	nc,1f
	ld	a,h
	xor	0x10
	ld	h,a
	ld	a,l
	xor	0x21
	ld	l,a
1:	dec	b
	jp	nz,2b
	ret
	
; ==============================================================================
; init_hash - initialize the hash table
; 
;   output: (hashtbl) - initialized table
; 
;   uses:   all
;
	.text
	.globl	init_hash
init_hash:
	ld	de,hashtbl
	ld	hl,0x55aa
	ld	c,0
1:	ld	a,0xc3
	call	crc16
	ex	de,hl
	ld	(hl),e
	inc	hl
	ld	(hl),d
	inc	hl
	ex	de,hl
	dec	c
	jp	nz,1b
	ret

	.lcomm	hashtbl, 256
	
; ==============================================================================
; zobrist_hash - calculate the Zobrist hash of the board
; 
;   input:  (HL) - array of black discs
;           (DE) - array of white discs
; 
;   output: HL - hash
; 
;   uses:   all
; 
	.text
	.globl	zobrist_hash
zobrist_hash:
	ld	bc,hashtbl
	ex	de,hl
	push	hl
	ld	hl,0
	ld	(hash),hl
	call	1f
	pop	de
1:	ld	a,8
2:	push	af
	ld	a,(de)
	inc	de
	ld	l,8
3:	rra
	ld	h,a
	jp	nc,1f
	ld	a,(bc)
	inc	bc
	push	hl
	ld	hl,hash
	xor	(hl)
	ld	(hl),a
	inc	hl
	ld	a,(bc)
	xor	(hl)
	ld	(hl),a
	pop	hl
	jp	4f
1:	inc	bc
4:	inc	bc
	dec	l
	ld	a,h
	jp	nz,3b
	pop	af
	dec	a
	jp	nz,2b
	ld	hl,(hash)
	ret
	
	.lcomm	hash, 2
	
; ==============================================================================
; move_rotate90 - rotate move clockwise
; 
;   input:  C - move
; 
;   output: C - rotated move
; 
;   uses:   A
; 
	.text
	.globl	move_rotate90
move_rotate90:
	ld	a,0x3f
	sub	c
	rra
	rra
	rra
	and	0x07
	push	bc
	ld	b,a
	ld	a,c
	rla
	rla
	rla
	jp	.L1
	
; ==============================================================================
; rotate180 - rotate the board in situ by 180 degrees
; 
;   input:  (HL) - array of black discs
;           (DE) - array of white discs
; 
;   output: rotated board
; 
;   uses:   all
; 
	.text
	.globl	rotate180
rotate180:
	push	de
	call	1f
	pop	hl
1:	ld	d,h
	ld	e,l
	ld	bc,7
	add	hl,bc
	ld	b,4
1:	ld	a,(de)
	call	1f
	ld	c,(hl)
	ld	(hl),a
	ld	a,c
	call	1f
	ld	(de),a
	inc	de
	dec	hl
	dec	b
	jp	nz,1b
	ret
1:	push	hl
	push	de
	call	reflect_byte
	pop	de
	pop	hl
	ret
	
; ==============================================================================
; move_rotate180 - rotate move by 180 degrees
; 
;   input:  C - move
; 
;   output: C - rotated move
; 
;   uses:   A
; 
	.text
	.globl	move_rotate180
move_rotate180:
	ld	a,0x07
	sub	c
	and	0x07
	push	bc
	ld	b,a
	ld	a,0x3f
	sub	c
	jp	.L1
	
; ==============================================================================
; rotate270 - rotate the board in situ counterclockwise
; 
;   input:  (HL) - array of black discs
;           (DE) - array of white discs
; 
;   output: rotated board
; 
;   uses:   all
; 
	.text
	.globl	rotate270
rotate270:
	push	de
	call	1f
	pop	hl
1:	ld	a,8
1:	push	af
	ld	a,(hl)
	inc	hl
	ld	de,tmprot
	ld	b,8
2:	rla
	ld	c,a
	ld	a,(de)
	rra
	ld	(de),a
	inc	de
	ld	a,c
	dec	b
	jp	nz,2b
	pop	af
	dec	a
	jp	nz,1b
	ld	b,8
1:	dec	hl
	dec	de
	ld	a,(de)
	ld	(hl),a
	dec	b
	jp	nz,1b
	ret
	
; ==============================================================================
; horflip - flip board in situ horizontally
; 
;   input:  (HL) - array of black discs
;           (DE) - array of white discs
; 
;   output: flipped board
; 
;   uses:   all
; 
	.text
	.globl	horflip
horflip:
	push	de
	call	1f
	pop	hl
1:	ld	b,h
	ld	c,l
	ld	a,8
1:	push	af
	ld	a,(bc)
	call	reflect_byte
	ld	(bc),a
	inc	bc
	pop	af
	dec	a
	jp	nz,1b
	ret
	
; ==============================================================================
; move_horflip - flip move horizontally
; 
;   input:  C - move
; 
;   output: C - flipped move
; 
;   uses:   A
; 
	.text
	.globl	move_horflip
move_horflip:
	ld	a,0x07
	sub	c
	and	0x07
	push	bc
	ld	b,a
	ld	a,c
	jp	.L1

; ==============================================================================
; nwseflip - flip the board in situ along the NW-SE diagonal
; 
;   input:  (HL) - array of black discs
;           (DE) - array of white discs
; 
;   output: flipped board
; 
;   uses:   all
; 
	.text
	.globl	nwseflip
nwseflip:
	push	de
	call	1f
	pop	hl
1:	ld	a,8
1:	push	af
	ld	a,(hl)
	inc	hl
	ld	de,tmprot
	ld	b,8
2:	rra
	ld	c,a
	ld	a,(de)
	rra
	ld	(de),a
	inc	de
	ld	a,c
	dec	b
	jp	nz,2b
	pop	af
	dec	a
	jp	nz,1b
	ld	b,8
1:	dec	hl
	dec	de
	ld	a,(de)
	ld	(hl),a
	dec	b
	jp	nz,1b
	ret
	
; ==============================================================================
; move_nwseflip - flip move along the NW-SE diagonal
; 
;   input:  C - move
; 
;   output: C - flipped move
; 
;   uses:   A
; 
	.text
	.globl	move_nwseflip
move_nwseflip:
	ld	a,c
	rra
	rra
	rra
	and	0x07
	push	bc
	ld	b,a
	ld	a,c
	rla
	rla
	rla
	jp	.L1
	
; ==============================================================================
; neswflip - flip the board in situ along the NW-SW diagonal
; 
;   input:  (HL) - array of black discs
;           (DE) - array of white discs
; 
;   output: flipped board
; 
;   uses:   all
; 
	.text
	.globl	neswflip
neswflip:
	push	de
	call	1f
	pop	hl
1:	ld	a,8
1:	push	af
	ld	a,(hl)
	inc	hl
	ld	de,tmprot
	ld	b,8
2:	rla
	ld	c,a
	ld	a,(de)
	rla
	ld	(de),a
	inc	de
	ld	a,c
	dec	b
	jp	nz,2b
	pop	af
	dec	a
	jp	nz,1b
	ld	b,8
1:	dec	hl
	dec	de
	ld	a,(de)
	ld	(hl),a
	dec	b
	jp	nz,1b
	ret
	
; ==============================================================================
; move_neswflip - flip move along the NW-SE diagonal
; 
;   input:  C - move
; 
;   output: C - flipped move
; 
;   uses:   A
; 
	.text
	.globl	move_neswflip
move_neswflip:
	ld	a,0x3f
	sub	c
	rra
	rra
	rra
	and	0x07
	push	bc
	ld	b,a
	ld	a,0x07
	sub	c
	rla
	rla
	rla
	jp	.L1
	
; ==============================================================================
; rect - draw simple filled rectangle
; 
;   input:  HL - destination
;           C - number of rows (lines)
;           B - number of columns (cells)
;           (color) - color mask
; 
;   uses:   all
; 
	.text
	.globl	rect
rect:
	ld	a,(color)
3:	push	bc
	push	hl
2:	ld	(hl),a
	inc	hl
	dec	b
	jp	nz,2b
	dec	c
	jp	z,1b
	pop	hl
	ld	de,64
	add	hl,de
	ld	d,c
	pop	bc
	ld	c,d
	jp	3b
	
; ==============================================================================
; init_btbl - initialize the bit manipulation tables
; 
;   output: (bitcounts) and (bitrefl) populated
; 
;   uses:   all
; 
	.text
	.globl	init_btbl
init_btbl:
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
	
; ==============================================================================
; reflect_byte - reverse bit order of a byte
; 
;   input:  A - input byte
; 
;   output: A = A reflected
; 
;   uses:   D, E, H, L
; 
	.text
	.globl	reflect_byte
reflect_byte:
	ld	hl,byterefl
	ld	e,a
	ld	d,0
	add	hl,de
	ld	a,(hl)
	ret

	.lcomm	byterefl, 256

