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
        .global _ccgchar, _ccgint, _ccpchar, _ccpint
        .global _ccsxt
        .global _ccor, _ccand, _ccxor
        .global _cceq, _ccne, _ccgt, _ccle, _ccge, _cclt, _ccuge, _ccult, _ccugt, _ccule
        .global _cclsr, _ccasr, _ccasl
        .global _ccsub, _ccneg, _cccom, _cclneg, _ccbool, _ccmul, _ccdiv
        .global _cccase
	.global _brkend, _Xstktop
        .global	_cguchar, _ccudiv
        .global _crun8080_end
	.global _etext, _edata

; fetch char from (HL) and sign extend into HL
_ccgchar:
	ld	a, (hl)
_ccsxt:
	ld	l, a
        rlca
        sbc	a, a
        ld      h, a
        ret
; fetch int from (HL)
_ccgint:
	ld      a, (hl)
        inc     hl
        ld      h, (hl)
        ld      l, a
        ret
; store char from HL into (DE)
_ccpchar:
	ld      a, l
        ld      (de), a
        ret
; store int from HL into (DE)
_ccpint:
	ld      a, l
        ld      (de), a
        inc     de
        ld      a, h
        ld      (de), a
        ret
; "or" HL and DE into HL
_ccor:  ld      a, l
        or      e
        ld      l, a
        ld      a, h
        or      d
        ld      h, a
        ret
; "xor" HL and DE into HL
_ccxor:
	ld      a, l
        xor     e
        ld      l, a
        ld      a, h
        xor     d
        ld      h, a
        ret
; "and" HL and DE into HL
_ccand:
	ld      a, l
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
_cceq:
	call    _cccmp
        ret	z
        dec     hl
        ret
; DE != HL
_ccne:  call    _cccmp
        ret	nz
        dec     hl
        ret
; DE > HL [signed]
_ccgt:  ex	de, hl
        call    _cccmp
        ret	c
        dec     hl
        ret
; DE <= HL [signed]
_ccle:  call    _cccmp
        ret	z
        ret	c
        dec     hl
        ret
; DE >= HL [signed]
_ccge:  call    _cccmp
        ret	nc
        dec     hl
        ret
; DE < HL [signed]
_cclt:  call    _cccmp
        ret	c
        dec     hl
        ret
; DE >= HL [unsigned]
_ccuge: call    _ccucmp
        ret	nc
        dec     hl
        ret
; DE < HL [unsigned]
_ccult: call    _ccucmp
        ret	c
        dec     hl
        ret
; DE > HL [unsigned]
_ccugt: ex	de, hl
        call    _ccucmp
        ret	c
        dec     hl
        ret
; DE <= HL [unsigned]
_ccule: call    _ccucmp
        ret	z
        ret	c
        dec     hl
        ret
; signed compare of DE and HL
;   carry is sign of difference [set => DE < HL]
;   zero is zero/non-zero
_cccmp: ld      a, e
        sub     l
        ld      e, a
        ld      a, d
        sbc     a, h
        ld      hl, 1		;preset true
        jp	m, _cccmp1
        or      e               ;resets carry
        ret
_cccmp1:
	or      e
        scf
        ret
; unsigned compare of DE and HL
;   carry is sign of difference [set => DE < HL]
;   zero is zero/non-zero
_ccucmp:
	ld      a, d
        cp      h
        jp	nz, _ccucmp1
        ld      a, e
        cp      l
_ccucmp1:
	ld      hl, 1		;preset true
        ret
; shift DE right logically by HL, move to HL
_cclsr: ex	de, hl
_cclsr1:
	dec     e
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
        jp      _cclsr1
; shift DE right arithmetically by HL, move to HL
_ccasr: ex	de, hl
_ccasr1:
	dec     e
        ret	m
        ld      a, h
        rla
        ld      a, h
        rra
        ld      h, a
        ld      a, l
        rra
        ld      l, a
        jp      _ccasr1
; shift DE left arithmetically by HL, move to HL
_ccasl:	ex	de, hl
_ccasl1:
	dec     e
        ret	m
        add     hl, hl
        jp      _ccasl1
; HL = DE - HL
_ccsub: ld      a, e
        sub     l
        ld      l, a
        ld      a, d
        sbc     a, h
        ld      h, a
        ret
; HL = -HL
_ccneg: call    _cccom
        inc     hl
        ret
; HL = ~HL
_cccom: ld      a, h
        cpl
        ld      h, a
        ld      a, l
        cpl
        ld      l, a
        ret
; HL = !HL
_cclneg:
	ld      a, h
        or      l
        jp	z, _cclneg1
        ld      hl, 0
        ret
_cclneg1:
	inc     hl
        ret
; HL = !!HL
_ccbool:
	call    _cclneg
        jp      _cclneg
;
; HL = DE * HL [signed]
_ccmul: ld      b, h
        ld      c, l
        ld      hl, 0
_ccmul1:
	ld      a, c
        rrca
        jp	nc, _ccmul2
        add     hl, de
_ccmul2:
	xor     a
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
        jp      _ccmul1
; HL = DE / HL, DE = DE % HL
_ccdiv: ld      b, h
        ld      c, l
        ld      a, d
        xor     b
        push    af
        ld      a, d
        or      a
        call	m, _ccdeneg
        ld      a, b
        or      a
        call	m, _ccbcneg
        ld      a, 16
        push    af
        ex	de, hl
        ld      de, 0
_ccdiv1:
	add     hl, hl
        call    _ccrdel
        jp	z, _ccdiv2
        call    _cccmpbd
        jp	m, _ccdiv2
        ld      a, l
        or      1
        ld      l, a
        ld      a, e
        sub     c
        ld      e, a
        ld      a, d
        sbc     a, b
        ld      d, a
_ccdiv2:
	pop     af
        dec     a
        jp	z, _ccdiv3
        push    af
        jp      _ccdiv1
_ccdiv3:
	pop     af
        ret	p
        call    _ccdeneg
        ex	de, hl
        call    _ccdeneg
        ex	de, hl
        ret
; {DE = -DE}
_ccdeneg:
        ld      a, d
        cpl
        ld      d, a
        ld      a, e
        cpl
        ld      e, a
        inc     de
        ret
; {BC = -BC}
_ccbcneg:
        ld      a, b
        cpl
        ld      b, a
        ld      a, c
        cpl
        ld      c, a
        inc     bc
        ret
; {DE <r<r 1}
_ccrdel:
	ld      a, e
        rla
        ld      e, a
        ld      a, d
        rla
        ld      d, a
        or      e
        ret
; {BC : DE}
_cccmpbd:
        ld      a, e
        sub     c
        ld      a, d
        sbc     a, b
        ret
; case jump
_cccase:
	ex	de, hl		;switch value to DE. exchange HL with DE
        pop     hl              ;get table address
_cccase1:
	call    _cccase4        ;get case value
        ld      a, e
        cp      c               ;equal to switch value cc
        jp	nz, _cccase2    ;no
        ld      a, d
        cp      b               ;equal to switch value cc
        jp	nz, _cccase2    ;no
        call    _cccase4        ;get case label
        jp	z, _cccase3     ;end of table, go to default
        push    bc
        ret                     ;case jump
_cccase2:
	call    _cccase4        ;get case label
        jp	nz, _cccase1    ;next case
_cccase3:
	dec     hl
        dec     hl
        dec     hl              ;position HL to the default label
        ld      d, (hl)         ;read where it points to
        dec     hl
        ld      e, (hl)
        ex	de, hl          ;exchange HL with DE and vice versa - address is now in HL
        jp	(hl)            ;default jump. loads HL to PC
_cccase4:
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
_Xstktop:
	ld     hl, 0 		;return current stack pointer (for sbrk)
        add    hl, sp
        ret
;        cseg
_etext:
;        dseg
;brkend: .blkw      edata           ;current "break"
;edata:  .blkw
;
;       .blkb   0H40
;       .blkw   0H20
;

; fetch char from (HL) into HL no sign extend
_cguchar:
	ld      l, (hl)
        ld      h, 0
        ret
; unsigned divide DE by HL and return quotient in HL, remainder in DE
; HL = DE / HL, DE = DE % HL
_ccudiv:
	ld      b, h            ; store divisor to bc 
        ld      c, l
        ld      hl, 0           ; clear remainder
        xor     a               ; clear carry        
        ld      a, 17           ; load loop counter
        push    af
_ccduv1:
	ld      a, e            ; left shift dividend into carry 
        rla
        ld      e, a
        ld      a, d
        rla
        ld      d, a
        jp	c, _ccduv2      ; we have to keep carry -> calling else branch
        pop     af              ; decrement loop counter
        dec     a
        jp	z, _ccduv5
        push    af
        xor     a               ; clear carry
        jp      _ccduv3
_ccduv2:
	pop     af              ; decrement loop counter
        dec     a
        jp	z, _ccduv5
        push    af
        scf                     ; set carry
_ccduv3:
	ld      a, l            ; left shift carry into remainder 
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
        jp	nc, _ccduv4	; if result negative, add back divisor, clear carry
        ld      a, l            ; add back divisor
        add     a, c
        ld      l, a
        ld      a, h
        adc     a, b
        ld      h, a     
        xor     a               ; clear carry
        jp      _ccduv1
_ccduv4:
	scf                     ; set carry
        jp      _ccduv1
_ccduv5: ex	de, hl
        ret
;
_crun8080_end:
	.end
