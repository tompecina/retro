;
;*****************************************************
;                                                    *
;       runtime library for small C compiler         *
;                                                    *
;       c.s - runtime routine for basic C code       *
;                                                    *
;               Ron Cain                             *
;                                                    *
;*****************************************************
;
        ;; .module LIB8080
        ;; .area   LIB8080   (REL,CON)   ;program area CODE1 is RELOCATABLE
        ;; .list   (err, loc, bin, eqt, cyc, lin, src, lst, md)
        ;; .nlist  (pag)
;        cseg
	.section .text
;
        .global ccgchar, ccgint, ccpchar, ccpint
        .global ccsxt
        .global ccor, ccand, ccxor
        .global cceq, ccne, ccgt, ccle, ccge, cclt, ccuge, ccult, ccugt, ccule
        .global cclsr, ccasr, ccasl
        .global ccsub, ccneg, cccom, cclneg, ccbool, ccmul, ccdiv
        .global cccase
	.global brkend, Xstktop
        .global	cguchar, ccudiv
        .global crun8080_end
	.global etext, edata

; fetch char from (HL) and sign extend into HL
ccgchar:
	ld	a, (hl)
ccsxt:  ld	l, a
        rlca
        sbc	a, a
        ld      h, a
        ret
; fetch int from (HL)
ccgint: ld      a, (hl)
        inc     hl
        ld      h, (hl)
        ld      l, a
        ret
; store char from HL into (DE)
ccpchar:
	ld      a, l
        ld      (de), a
        ret
; store int from HL into (DE)
ccpint: ld      a, l
        ld      (de), a
        inc     de
        ld      a, h
        ld      (de), a
        ret
; "or" HL and DE into HL
ccor:   ld      a, l
        or      e
        ld      l, a
        ld      a, h
        or      d
        ld      h, a
        ret
; "xor" HL and DE into HL
ccxor:  ld      a, l
        xor     e
        ld      l, a
        ld      a, h
        xor     d
        ld      h, a
        ret
; "and" HL and DE into HL
ccand:  ld      a, l
        and     e
        ld      l, a
        ld      a, h
        and     d
        ld      h, a
        ret
;
;......logical operations: HL set to 0 (false) or 1 (true)
;
; DE == HL
cceq:   call    cccmp
        ret	z
        dec     hl
        ret
; DE != HL
ccne:   call    cccmp
        ret	nz
        dec     hl
        ret
; DE > HL [signed]
ccgt:   ex	de, hl
        call    cccmp
        ret	c
        dec     hl
        ret
; DE <= HL [signed]
ccle:   call    cccmp
        ret	z
        ret	c
        dec     hl
        ret
; DE >= HL [signed]
ccge:   call    cccmp
        ret	nc
        dec     hl
        ret
; DE < HL [signed]
cclt:   call    cccmp
        ret	c
        dec     hl
        ret
; DE >= HL [unsigned]
ccuge:  call    ccucmp
        ret	nc
        dec     hl
        ret
; DE < HL [unsigned]
ccult:  call    ccucmp
        ret	c
        dec     hl
        ret
; DE > HL [unsigned]
ccugt:  ex	de, hl
        call    ccucmp
        ret	c
        dec     hl
        ret
; DE <= HL [unsigned]
ccule:  call    ccucmp
        ret	z
        ret	c
        dec     hl
        ret
; signed compare of DE and HL
;   carry is sign of difference [set => DE < HL]
;   zero is zero/non-zero
cccmp:  ld      a, e
        sub     l
        ld      e, a
        ld      a, d
        sbc     a, h
        ld      hl, 1		;preset true
        jp	m, cccmp1
        or      e               ;resets carry
        ret
cccmp1: or      e
        scf
        ret
; unsigned compare of DE and HL
;   carry is sign of difference [set => DE < HL]
;   zero is zero/non-zero
ccucmp: ld      a, d
        cp      h
        jp	nz, ccucmp1
        ld      a, e
        cp      l
ccucmp1:
	ld      hl, 1		;preset true
        ret
; shift DE right logically by HL, move to HL
cclsr:  ex	de, hl
cclsr1: dec     e
        ret	m
        scf
        ccf
        ld	a, h
        rra
        ld      h, a
        ld      a, l
        rra
        ld      l, a
        scf
        ccf
        jp      cclsr1
; shift DE right arithmetically by HL, move to HL
ccasr:  ex	de, hl
ccasr1: dec     e
        ret	m
        ld      a, h
        rla
        ld      a, h
        rra
        ld      h, a
        ld      a, l
        rra
        ld      l, a
        jp      ccasr1
; shift DE left arithmetically by HL, move to HL
ccasl:  ex	de, hl
ccasl1: dec     e
        ret	m
        add     hl, hl
        jp      ccasl1
; HL = DE - HL
ccsub:  ld      a, e
        sub     l
        ld      l, a
        ld      a, d
        sbc     a, h
        ld      h, a
        ret
; HL = -HL
ccneg:  call    cccom
        inc     hl
        ret
; HL = ~HL
cccom:  ld      a, h
        cpl
        ld      h, a
        ld      a, l
        cpl
        ld      l, a
        ret
; HL = !HL
cclneg: ld      a, h
        or      l
        jp	z, cclneg1
        ld      hl, 0
        ret
cclneg1:
	inc     hl
        ret
; HL = !!HL
ccbool: call    cclneg
        jp      cclneg
;
; HL = DE * HL [signed]
ccmul:  ld      b, h
        ld      c, l
        ld      hl, 0
ccmul1: ld      a, c
        rrca
        jp	nc, ccmul2
        add     hl, de
ccmul2: xor     a
        ld      a, b
        rra
        ld      b, a
        ld      a, c
        rra
        ld      c, a
        or      b
        ret	z
        xor     a
        ld      a, e
        rla
        ld      e, a
        ld      a, d
        rla
        ld      d, a
        or      e
        ret	z
        jp      ccmul1
; HL = DE / HL, DE = DE % HL
ccdiv:  ld      b, h
        ld      c, l
        ld      a, d
        xor     b
        push    af
        ld      a, d
        or      a
        call	m, ccdeneg
        ld      a, b
        or      a
        call	m, ccbcneg
        ld      a, 16
        push    af
        ex	de, hl
        ld      de, 0
ccdiv1: add     hl, hl
        call    ccrdel
        jp	z, ccdiv2
        call    cccmpbd
        jp	m, ccdiv2
        ld      a, l
        or      1
        ld      l, a
        ld      a, e
        sub     c
        ld      e, a
        ld      a, d
        sbc     a, b
        ld      d, a
ccdiv2: pop     af
        dec     a
        jp	z, ccdiv3
        push    af
        jp      ccdiv1
ccdiv3: pop     af
        ret	p
        call    ccdeneg
        ex	de, hl
        call    ccdeneg
        ex	de, hl
        ret
; {DE = -DE}
ccdeneg:
        ld      a, d
        cpl
        ld      d, a
        ld      a, e
        cpl
        ld      e, a
        inc     de
        ret
; {BC = -BC}
ccbcneg:
        ld      a, b
        cpl
        ld      b, a
        ld      a, c
        cpl
        ld      c, a
        inc     bc
        ret
; {DE <r<r 1}
ccrdel: ld      a, e
        rla
        ld      e, a
        ld      a, d
        rla
        ld      d, a
        or      e
        ret
; {BC : DE}
cccmpbd:
        ld      a, e
        sub     c
        ld      a, d
        sbc     a, b
        ret
; case jump
cccase: ex	de, hl		;switch value to DE. exchange HL with DE
        pop     hl              ;get table address
cccase1:
	call    cccase4         ;get case value
        ld      a, e
        cp      c               ;equal to switch value cc
        jp	nz, cccase2     ;no
        ld      a, d
        cp      b               ;equal to switch value cc
        jp	nz, cccase2     ;no
        call    cccase4         ;get case label
        jp	z, cccase3      ;end of table, go to default
        push    bc
        ret                     ;case jump
cccase2:
	call    cccase4         ;get case label
        jp	nz, cccase1     ;next case
cccase3:
	dec     hl
        dec     hl
        dec     hl              ;position HL to the default label
        ld      d, (hl)         ;read where it points to
        dec     hl
        ld      e, (hl)
        ex	de, hl          ;exchange HL with DE and vice versa - address is now in HL
        jp	(hl)            ;default jump. loads HL to PC
cccase4:
	ld      c, (hl)
        inc     hl
        ld      b, (hl)
        inc     hl
        ld      a, c
        or      b
        ret
;
;
;
Xstktop:
	ld     hl, 0 		;return current stack pointer (for sbrk)
        add    hl, sp
        ret
;        cseg
etext:
;        dseg
;brkend: .blkw      edata           ;current "break"
;edata:  .blkw
;
;       .blkb   0H40
;       .blkw   0H20
;

; fetch char from (HL) into HL no sign extend
cguchar:
	ld      l, (hl)
        ld      h, 0
        ret
; unsigned divide DE by HL and return quotient in HL, remainder in DE
; HL = DE / HL, DE = DE % HL
ccudiv: ld      b, h            ; store divisor to bc 
        ld      c, l
        ld      hl, 0           ; clear remainder
        xor     a               ; clear carry        
        ld      a, 17           ; load loop counter
        push    af
ccduv1: ld      a, e            ; left shift dividend into carry 
        rla
        ld      e, a
        ld      a, d
        rla
        ld      d, a
        jp	c, ccduv2       ; we have to keep carry -> calling else branch
        pop     af              ; decrement loop counter
        dec     a
        jp	z, ccduv5
        push    af
        xor     a               ; clear carry
        jp      ccduv3
ccduv2: pop     af              ; decrement loop counter
        dec     a
        jp	z, ccduv5
        push    af
        scf                     ; set carry
ccduv3: ld      a, l            ; left shift carry into remainder 
        rla
        ld      l, a
        ld      a, h
        rla
        ld      h, a
        ld      a, l      	; substract divisor from remainder
        sub     c
        ld      l, a
        ld      a, h
        sbc     a, b
        ld      h, a
        jp	nc, ccduv4	; if result negative, add back divisor, clear carry
        ld      a, l            ; add back divisor
        add     a, c
        ld      l, a
        ld      a, h
        adc     a, b
        ld      h, a     
        xor     a               ; clear carry
        jp      ccduv1
ccduv4: scf                     ; set carry
        jp      ccduv1
ccduv5: ex	de, hl
        ret
;
crun8080_end:
	.end
