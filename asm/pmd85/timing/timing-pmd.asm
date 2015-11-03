	.cpu 8080
	org 3000h

c1	equ	5dh
ct	equ	5fh
	
	jmp	start
sub2:	lda	res
	ret
ins:	db	0
start:	mvi	a,70h
	out	ct
	mvi	a,0ffh
	out	c1
	nop
	out	c1
	lda	ins
	mov	d,a
	lxi	h,field
	lxi	b,0f00h
l1:	mov	m,d
	inx	h
	dcx	b
	mov	a,b
	ora	c
	jnz	l1
	mvi	m,0c9h
	call	read
	shld	res
	lxi	h,aux
	call	field
	call	read
	xchg
	lhld	res
	mov	a,l
	sub	e
	mov	l,a
	mov	a,h
	sbb	d
	mov	h,a
	shld    res
	ret
read:	mvi	a,40h
	out	ct
	nop
	in	c1
	mov	l,a
	in	c1
	mov	h,a
	ret

res:	ds	2
aux:	ds	2
field:	
	
	end
