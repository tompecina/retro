	title	"Preliminary Z80 tests"

; prelim.z80 - Preliminary Z80 tests
; Copyright (C) 1994  Frank D. Cringle
;
; This program is free software; you can redistribute it and/or
; modify it under the terms of the GNU General Public License
; as published by the Free Software Foundation; either version 2
; of the License, or (at your option) any later version.
;
; This program is distributed in the hope that it will be useful,
; but WITHOUT ANY WARRANTY; without even the implied warranty of
; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
; GNU General Public License for more details.
;
; You should have received a copy of the GNU General Public License
; along with this program; if not, write to the Free Software
; Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.


; These tests have two goals.  To start with, we assume the worst and
; successively test the instructions needed to continue testing.
; Then we try to test all instructions which cannot be handled by
; zexlax - the crc-based instruction exerciser.

; Initially errors are 'reported' by jumping to 0.  This should reboot
; cp/m, so if the program terminates without any output one of the
; early tests failed.  Later errors are reported by outputting an
; address via the bdos conout routine.  The address can be located in
; a listing of this program.

; If the program runs to completion it displays a suitable message.

;******************************************************************************
;
; Modified by Ian Bartholomew to run a preliminary test on an 8080 CPU
;
; Assemble using M80
;
;******************************************************************************

	page 0
	cpu 8080
	
	org 0

start:	mvi	a,1		; test simple compares and z/nz jumps
	cpi	2
	jz	error
	cpi	1
	jnz	error
	jmp	lab0
error:	mvi	a, 0ffh
	hlt			; failure
	db	0ffh
	
lab0:	lxi	sp, stack
	call	lab2		; does a simple call work?
lab1:	jmp	error		; fail
	
lab2:	pop	h		; check return address
	mov	a,h
	cpi	lab1 >> 8
	jz	lab3
	jmp	error
lab3:	mov	a,l
	cpi	lab1 & 0ffh
	jz	lab4
	jmp	error

; test presence and uniqueness of all machine registers
; (except ir)
lab4:	lxi	sp,regs1
	pop	psw
	pop	b
	pop	d
	pop	h
	lxi	sp,regs2+8
	push	h
	push	d
	push	b
	push	psw

v	set	0
	rept	8
	lda	regs2+v/2
v	set	v+2
	cpi	v
	jnz	error
	endm

; test access to memory via (hl)
	lxi	h,hlval
	mov	a,m
	cpi	0a5h
	jnz	error
	lxi	h,hlval+1
	mov	a,m
	cpi	03ch
	jnz	error

; test unconditional return
	lxi	sp,stack
	lxi	h,reta
	push	h
	ret
	jmp	error

; test instructions needed for hex output
reta:	mvi	a,0ffh
	ani	0fh
	cpi	0fh
	jnz	error
	mvi	a,05ah
	ani	0fh
	cpi	0ah
	jnz	error
	rrc
	cpi	05h
	jnz	error
	rrc
	cpi	82h
	jnz	error
	rrc
	cpi	41h
	jnz	error
	rrc
	cpi	0a0h
	jnz	error
	lxi	h,01234h
	push	h
	pop	b
	mov	a,b
	cpi	12h
	jnz	error
	mov	a,c
	cpi	34h
	jnz	error
	
; from now on we can report errors by displaying an address

; test conditional call, ret, jp, jr
c_c	macro	par
	cc	par
	endm
c_nc	macro	par
	cnc	par
	endm
c_z	macro	par
	cz	par
	endm
c_nz	macro	par
	cnz	par
	endm
c_pe	macro	par
	cpe	par
	endm
c_po	macro	par
	cpo	par
	endm
c_p	macro	par
	cp	par
	endm
c_m	macro	par
	cm	par
	endm
r_c	macro	par
	rc	par
	endm
r_nc	macro	par
	rnc	par
	endm
r_z	macro	par
	rz	par
	endm
r_nz	macro	par
	rnz	par
	endm
r_pe	macro	par
	rpe	par
	endm
r_po	macro	par
	rpo	par
	endm
r_p	macro	par
	rp	par
	endm
r_m	macro	par
	rm	par
	endm
j_c	macro	par
	jc	par
	endm
j_nc	macro	par
	jnc	par
	endm
j_z	macro	par
	jz	par
	endm
j_nz	macro	par
	jnz	par
	endm
j_pe	macro	par
	jpe	par
	endm
j_po	macro	par
	jpo	par
	endm
j_p	macro	par
	jp	par
	endm
j_m	macro	par
	jm	par
	endm
tcond	macro	flag,pcond,ncond,rel
	lxi	h,flag
	push	h
	pop	psw
	c_pcond	lab1_pcond
	jmp	error
lab1_pcond:	pop	h
	lxi	h,0d7h ! flag
	push	h
	pop	psw
	c_ncond	lab2_pcond
	jmp	error
lab2_pcond:	pop	h
	lxi	h,lab3_pcond
	push	h
	lxi	h,flag
	push	h
	pop	psw
	r_pcond
	call	error
lab3_pcond:	lxi	h,lab4_pcond
	push	h
	lxi	h,0d7h ! flag
	push	h
	pop	psw
	r_ncond
	call	error
lab4_pcond:	lxi	h,flag
	push	h
	pop	psw
	j_pcond	lab5_pcond
	call	error
lab5_pcond:	lxi	h,0d7h ! flag
	push	h
	pop	psw
	j_ncond	lab6_pcond
	call	error
lab6_pcond:	
	endm

	tcond	1,c,nc,1
	tcond	4,pe,po,0
	tcond	040h,z,nz,1
	tcond	080h,m,p,0

; test indirect jumps
	lxi	h,lab7
	pchl
	call	error

; djnz (and (partially) inc a, inc hl)
lab7:	mvi	a,0a5h
	mvi	b,4
lab8:	rrc
	dcr	b
	jnz	lab8
	cpi	05ah
	cnz	error
	mvi	b,16
lab9:	inr	a
	dcr	b
	jnz	lab9
	cpi	06ah
	cnz	error
	mvi	b,0
	lxi	h,0
lab10:	inx	h
	dcr	b
	jnz	lab10
	mov	a,h
	cpi	1
	cnz	error
	mov	a,l
	cpi	0
	cnz	error
	
allok:	mvi	a, 0
	hlt

v	set	0
regs1:	rept	8
v	set	v+2
	db	v
	endm

regs2:	ds	8

hlval:	db	0a5h,03ch

; skip to next page boundary
	org (($+255)/256)*256
	ds	240

stack	equ	$

	end	start

	end
	
