	.cpu 8080
	org 3000h

c1:	equ	5dh
ct:	equ	5fh

	mvi	a,70h
	out	ct
	mvi	a,0ffh
	out	c1
	out	c1

	call	read
	push	h
	call	read
	pop	d
	mov	a,e
	sub	l
	mov	l,a
	mov	a,d
	sbb	h
	mov	h,a
	shld    res
	ret

sub2:	lda	res + 1
	ret
	
read:	mvi	a,40h
	out	ct
	in	c1
	mov	l,a
	in	c1
	mov	h,a
	ret

res:	ds	2
		
	mvi	a,55h
	ret

	end
	
