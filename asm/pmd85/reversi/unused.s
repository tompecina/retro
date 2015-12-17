; unused, but useful (and mostly debugged) stuff

	
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
	

