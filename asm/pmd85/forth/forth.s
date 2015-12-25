	.title	"8080 FIG-FORTH 1.1 VERSION A0 17SEP79"
;
;	FIG-FORTH  RELEASE 1.1  FOR THE 8080 PROCESSOR
;
;	ALL PUBLICATIONS OF THE FORTH INTEREST GROUP
;	ARE PUBLIC DOMAIN.  THEY MAY BE FURTHER
;	DISTRIBUTED BY THE INCLUSION OF THIS CREDIT
;	NOTICE:
;
;	THIS PUBLICATION HAS BEEN MADE AVAILABLE BY THE
;		     FORTH INTEREST GROUP
;		     P. O. BOX 1105
;		     SAN CARLOS, CA 94070
;
;	IMPLEMENTATION BY:
;		JOHN CASSADY
;                FOR THE FORTH IMPLEMENTATION TEAM (FIT) MARCH 1979
;	MODIFIED for CP/M by:
;	   	KIM HARRIS
;               FIT LIBRARIAN SEPT 1979
;	ACKNOWLEDGEMENTS:
;		GEORGE FLAMMER
;		ROBT. D. VILLWOCK
;               Microsystems inc. Pasadena Ca.
;
;        DISTRIBUTED BY    FORTH POWER
;               P.O. BOX 2455 SAN RAFAEL CA
;               94902   415-471-1762
;               SUPPORT, SYSTEMS PROGRAMMING, 
;               APPLICATIONS PROGRAMMING
;
;  UNLESS OTHERWISE INDICATED, THIS DISTRIBUTION IS SUPPORTED
;  SOLELY BY THE FORTH INTEREST GROUP (LISTINGS) AND BY
;  FORTH POWER (MACHINE READABLE COPIES AND EXTENSIONS).
;
;   COPYRIGHT AND TRADEMARK NOTICES:
;   FORTH (C) 1974,1975,1976,1977,1978,1979 FORTH INC.
;   FIST (C) 1979 FORTH INTERNATIONAL STANDARDS TEAM
;   FIG, FORTH DIMENSIONS, FIT, (C) 1978, 1979 FORTH INTEREST GROUP
;   FORTH POWER (C) 1978, 1979 MARIN SERVICES, INC.
;   FORTH 77, FORTH 78, FORTH 79, STANDARD FORTH, FORTH INTERNATIONAL
;   STANDARD, (C) 1976, 1977, 1978, 1979, FIST
;   MULTI-FORTH (C) 1978, 1979 CREATIVE SOLUTIONS
;   CP/M (C) 1979 DIGITAL RESEARCH INC.
;   MOST ANYTHING WITH AN 11 IN IT (C) DIGITAL EQUIPMENT CORP
;   THERE MAY BE OTHERS ! !
;   MINIFORTH, MICROFORTH, POLYFORTH, FORTH  TM FORTH INC.
;   FIG-FORTH (C) 1978 1979 FORTH INTEREST GROUP
;   ALL RIGHTS RESERVED EXCEPT AS EXPRESSLY INDICATED !
;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;
;               UPDATES, PATCHES, BUG REPORTS, EXTENSIONS
;               FOR THIS SOFTWARE IN  FORTH DIMENSIONS  
;               NEWSLETTER OF FORTH INTEREST GROUP (FIG)
;               6 issues $5.00 includes fig membership
;
;          DOCUMENTATION FROM FIG or FORTH POWER
;        
;               FORTH PRIMER (240pp) Richard Stevens
;               KITT PEAK NATIONAL OBSERVATORY    $20.00
;    
;               FORTH IMPLEMENTATION TEAM LANGUAGE MODEL, EDITOR SOURCE,
;               LANGUAGE GLOSSARY, AND IMPLEMENTATION GUIDE  $10.00
;
;               FORTH FOR MICROCOMPUTERS by JOHN S JAMES
;               reprint from DDJ #25          $2.00
;
;               FORTH POCKET PROGRAMMERS CARD  FREE W/ S.A.S.E.
;
;               SOURCE CODE FOR TI990, 6502, 6800, PDP11, PACE,
;               8080 (included here)    $10.00/ LISTING
;
;          DOCUMENTATION FROM FIG
;
;               USING FORTH by ELIZABETH RATHER (200pp)
;               FORTH INC. 1979               $20.00
;
;          DOCUMENTATION FROM FORTH POWER
;               
;
;               CP/M MULTI-FORTH USERS MANUAL  $20.00
;               FORTH 79 INTERNATIONAL STANDARD 
;
;               CP/M 8080 FORTH BY FIG 8" DISKETT IBM STD.
;               WITH EDITOR AND ASSEMBLER, COPY AND PRINT,
;               AND USERS GUIDE                $65.00
;
;               also on 5" CP/M, 5 & 8 Northstar DOS
;
;               CP/M Multi-Forth, Full 79 International
;               Standard with extensions, Strings, Prom burner,
;               Real time clock, VIDEO EDITOR, UTILITIES
;               A PROFESSIONAL LEVEL PRODUCT    $150.00
;               includes manual
;
;               PDP 11 FORTH by JOHN S. JAMES
;               8" RX01 diskett or 9 track 800 bpi DOS tape
;               runs under OS or stand alone
;               WITH USERS GUIDE                $150.00
;
;               FIG TRS 80 FORTH cassette or diskette
;               WRITE FOR PRICES
;
;               APPLE FORTH BY CapN' SOFTWARE   $40.00
;               EASYWRITER (word processor for APPLE
;               by CapN' SOFTWARE)        $100.00
;
;               APPLE FORTH BY UNIVERSITY OF UTRECHT,
;               includes floating pt and many extensions
;               A PROFESSIONAL LEVEL PRODUCT  $100.00
;
;               FORTH FOR MICROPROSSOR DEVELOPMENT SYSTEMS,
;               FORTH FOR D.G., VAX 11, INTERDATA, Series 1,
;               C.A., HONEYWELL LEVEL 6, and others,   Write for prices
;
;          DOCUMENTATION FROM CALTECH
;                CALTECH FORTH MANUAL $6.00
;               CAL TECH BOOKSTORE PASADENA CA
;               by MARTIN S. EWING 100pp postpaid
;
;  CALL FOR PAPERS, ARTICLES, SPEAKERS: FOR FORTH DIMENSIONS
;   AND TRADE PUBLICATIONS SEND TO FIG.  FOR SPEAKERS, WORKSHOPS,
;   SHOWS AND CONVENTIONS CONTACT FIG.  FIG SOLICITES FORTH SOFTWARE
;   FOR INCLUSION IN THIS EFFORT.
;               FORTH INTERNATIONAL STANDARDS TEAM (FIT)
;              FORTH 79 INTERNATIONAL STANDARD, REQUIRED AND
;              RESERVED WORD GLOSSARY, AND STANDARDS ACTIVITY
;              DISTRIBUTION.  $30.00 TO FIT c/o FIG or to
;
;              CAROLYN ROSENBERG, FIT SECRETARY
;              c/o FORTH INC. MANHATTAN BEACH CA.
;
;
;-----------------------------------------------------
;	LABELS USED WHICH DIFFER FROM FIG-FORTH PUBLISHED
;	8080 LISTING 1.0:
;
;	REL 1.1		REL 1.0
;	-------		-------
;	ANDD		AND
;	CSPP		CSP
;	ELSEE		ELSE
;	ENDD		END
;	ENDIFF		ENDIF
;	ERASEE		ERASE
;	IDO		I
;	IFF		IF
;	INN		IN
;	MODD		MOD
;	ORR		OR
;	OUTT		OUT
;	RR		R
;	RPP		RP
;	SUBB		SUB
;	XORR		XOR
;
;	SEE ALSO:
;		RELEASE & VERSION NUMBERS
;		ASCII CHARACTER EQUATES
;		MEMORY ALLOCATION
;		DISK INTERFACE
;		CONSOLE & PRINTER INTERFACE
;
	.eject
;
;----------------------------------------------------------
;
;	RELEASE & VERSION NUMBERS
;
	.equiv	FIGREL, 1	; FIG RELEASE #
	.equiv	FIGREV, 1	; FIG REVISION #
	.equiv	USRVER, 0	; USER VERSION #
;
;	ASCII CHARACTERS USED
;
	.equiv	ABL, 20H	; SPACE
	.equiv	ACR, 0DH	; CARRIAGE RETURN
	.equiv	ADOT, 02EH	; PERIOD
	.equiv	BELL, 07H	; (^G)
	.equiv	BSIN, 7FH	; INPUT BACKSPACE CHR = RUBOUT
	.equiv	BSOUT, 08H	; OUTPUT BACKSPACE (^H)
	.equiv	DLE, 10H	; (^P)
	.equiv	LF, 0AH	; LINE FEED
	.equiv	FF, 0CH	; FORM FEED (^L)
;
;	MEMORY ALLOCATION
;
	.equiv	EM, 4000H	; TOP OF MEMORY + 1 = LIMIT
	.equiv	NSCR, 1	; NUMBER OF 1024 BYTE SCREENS
	.equiv	KBBUF, 128	; DATA BYTES PER DISK BUFFER
	.equiv	US, 40H	; USER VARIABLES SPACE
	.equiv	RTS, 0A0H	; RETURN STACK & TERM BUFF SPACE
;
	.equiv	CO, KBBUF+4	; DISK BUFFER + 2 HEADER + 2 TAIL
	.equiv	NBUF, NSCR*400H/KBBUF	; NUMBER OF BUFFERS
	.equiv	BUF1, EM-CO*NBUF	; ADDR FIRST DISK BUFFER
	.equiv	INITR0, BUF1-US		; (R0)
	.equiv	INITS0, INITR0-RTS	; (S0)
;
	.eject
;
;-------------------------------------------------------
;
	.text
	.globl	ORIG
ORIG:	nop
	jp	CLD	; VECTOR TO COLD START
	nop
	jp	WRM	; VECTOR TO WARM START
	.byte	FIGREL	; FIG RELEASE #
	.byte	FIGREV	; FIG REVISION #
	.byte	USRVER	; USER VERSION #
	.byte	0EH	; IMPLEMENTATION ATTRIBUTES
	.word	TASK-7  ; TOPMOST WORD IN FORTH VOCABULARY
	.word	BSIN	; BKSPACE CHARACTER
	.word	INITR0	; INIT (UP)
;<<<<<< FOLLOWING USED BY COLD;
;	MUST BE IN SAME ORDER AS USER VARIABLES
	.word	INITS0	; INIT (S0)
	.word	INITR0	; INIT (R0)
	.word	INITS0	; INIT (TIB)
	.word	20H		; INIT (WIDTH)
	.word	0		; INIT (WARNING)
	.word	INITDP		; INIT (FENCE)
	.word	INITDP		; INIT (DP)
	.word	FORTH+6		; INIT (VOC-LINK)
;<<<<<< END DATA USED BY COLD
	.word	5H,0B320H	; CPU NAME	( HW,LW )
;				  ( 32 BIT, BASE 36 INTEGER )
;
;
;			+---------------+
;	B +ORIGIN	| . . .W:I.E.B.A|	IMPLEMENTATION
;			+---------------+	ATTRIBUTES
;			       ^ ^ ^ ^ ^
;			       | | | | +-- PROCESSOR ADDR =
;			       | | | |     { 0 BYTE | 1 WORD }
;			       | | | +---- HIGH BYTE AT
;			       | | |       { 0 LOW ADDR |
;			       | | |	     1 HIGH ADDR }
;			       | | +------ ADDR MUST BE EVEN
;			       | |	   { 0 YES | 1 NO }
;			       | +-------- INTERPRETER IS
;			       |	   { 0 PRE | 1 POST }
;			       |	   INCREMENTING
;			       +---------- { 0 ABOVE SUFFICIENT
;					     | 1 OTHER DIFFER-
;					     ENCES EXIST }
;
	.eject
;
;------------------------------------------------------
;
;	FORTH REGISTERS
;
;	FORTH	8080	FORTH PRESERVATION RULES
;	-----	----	------------------------------------------------------------------------HH+	;	IP	BC	SHOULD BE PRESERVED ACROSS
;			  FORTH WORDS
;	W	DE	SOMETIMES OUTPUT FROM NEXT
;			MAY BE ALTERED BEFORE JMP'ING TO NEXT
;			INPUT ONLY WHEN 'DPUSH' CALLED
;	SP	SP	SHOULD BE USED ONLY AS DATA STACK
;			  ACROSS FORTH WORDS
;			MAY BE USED WITHIN FORTH WORDS
;			  IF RESTORED BEFORE 'NEXT'
;		HL	NEVER OUTPUT FROM NEXT
;			INPUT ONLY WHEN 'HPUSH' CALLED
;
UP:	.word	INITR0	; USER AREA POINTER
RPP:	.word	INITR0	; RETURN STACK POINTER
;
;------------------------------------------------------
;
;	COMMENT CONVENTIONS:
;
;	=	MEANS	"IS EQUAL TO"
;	<-	MEANS	ASSIGNMENT
;
;	NAME	=	ADDRESS OF NAME
;	(NAME)	=	CONTENTS AT NAME
;	((NAME))=	INDIRECT CONTENTS
;
;	CFA	=	ADDRESS OF CODE FIELD
;	LFA	=	ADDRESS OF LINK FIELD
;	NFA	=	ADDR OF START OF NAME FIELD
;	PFA	=	ADDR OF START OF PARAMETER FIELD
;
;	S1	=	ADDR OF 1ST WORD OF PARAMETER STACK
;	S2	=	ADDR OF 2ND WORD OF PARAMETER STACK
;	R1	=	ADDR OF 1ST WORD OF RETURN STACK
;	R2	=	ADDR OF 2ND WORD OF RETURN STACK
;	( ABOVE STACK POSITIONS VALID BEFORE & AFTER EXECUTION
;	OF ANY WORD, NOT DURING. )
;
;	LSB	=	LEAST SIGNIFICANT BIT
;	MSB	=	MOST SIGNIFICANT BIT
;	LB	=	LOW BYTE
;	HB	=	HIGH BYTE
;	LW	=	LOW WORD
;	HW	=	HIGH WORD
;	( MAY BE USED AS SUFFIX TO ABOVE NAMES )
;
	.eject
;
;---------------------------------------------------
;	DEBUG SUPPORT
;
;	TO USE:
;	(1)	SET 'BIP' TO IP VALUE TO HALT, CANNOT BE CFA
;	(2)	SET MONITOR'S BREAKPOINT PC TO 'BREAK'
;			OR PATCH 'HLT' INSTR. THERE
;	(3)	PATCH A 'JMP TNEXT' AT 'NEXT'
;	WHEN (IP) = (BIP) CPU WILL HALT
;
BIP:	.word	0	; BREAKPOINT ON IP VALUE
;
TNEXT:	ld	hl,BIP
	ld	a,(hl)	; LB
	cp	c
	jp	nz,TNEXT1
	inc	hl
	ld	a,(hl)	; HB
	cp	b
	jp	nz,TNEXT1
BREAK:	nop		; PLACE BREAKPOINT HERE
	nop
	nop
TNEXT1:	ld	a,(bc)
	inc	bc
	ld	l,a
	jp	NEXT+3
;
;--------------------------------------------------
;
;	NEXT, THE FORTH ADDRESS INTERPRETER
;	  ( POST INCREMENTING VERSION )
;
DPUSH:	push	de
HPUSH:	push	hl
NEXT:	ld	a,(bc)	;(W) <- ((IP))
	inc	bc	;(IP) <- (IP)+2
	ld	l,a
	ld	a,(bc)
	inc	bc
	ld	h,a	; (HL) <- CFA
NEXT1:	ld	e,(hl)	;(PC) <- ((W))
	inc	hl
	ld	d,(hl)
	ex	de,hl
	jp	(hl)		; NOTE: (DE) = CFA+1
;
	.eject
;
;		FORTH DICTIONARY
;
;
;	DICTIONARY FORMAT:
;
;				BYTE
;	ADDRESS	NAME		CONTENTS
;	------- ----		--------
;					  ( MSB=1
;					  ( P=PRECEDENCE BIT
;					  ( S=SMUDGE BIT
;	NFA	NAME FIELD	1PS<LEN>  < NAME LENGTH
;				0<1CHAR>  MSB=0, NAME'S 1ST CHAR
;				0<2CHAR>
;				  ...
;				1<LCHAR>  MSB=1, NAME'S LAST CHR
;	LFA	LINK FIELD	<LINKLB>  = PREVIOUS WORD'S NFA
;				<LINKHB>
;LABEL:	CFA	CODE FIELD	<CODELB>  = ADDR CPU CODE
;				<CODEHB>
;	PFA	PARAMETER	<1PARAM>  1ST PARAMETER BYTE
;		FIELD		<2PARAM>
;				  ...
;
;
DP0:	.byte	83H	; LIT
	.ascii	"LI"
	.byte	'T'+80H
	.word	0	; (LFA)=0 MARKS END OF DICTIONARY
LIT:	.word	.+2	;(S1) <- ((IP))
	ld	a,(bc)	; (HL) <- ((IP)) = LITERAL
	inc	bc	; (IP) <- (IP) + 2
	ld	l,a	; LB
	ld	a,(bc)	; HB
	inc	bc
	ld	h,a
	jp	HPUSH	; (S1) <- (HL)
 ;
	.byte	87H	; EXECUTE
	.ascii	"EXECUT"
	.byte	'E'+80H
	.word	LIT-6
EXEC:	.word	.+2
	pop	hl	; (HL) <- (S1) = CFA
	jp	NEXT1
;
	.byte	86H	; BRANCH
	.ascii	"BRANC"
	.byte	'H'+80H
	.word	EXEC-0AH
BRAN:	.word	.+2	;(IP) <- (IP) + ((IP))
BRAN1:	ld	h,b	; (HL) <- (IP)
	ld	l,c
	ld	e,(hl)	; (DE) <- ((IP)) = BRANCH OFFSET
	inc	hl
	ld	d,(hl)
	dec	hl
	add	hl,de	; (HL) <- (HL) + ((IP))
	ld	c,l	; (IP) <- (HL)
	ld	b,h
	jp	NEXT
;
	.byte	87H	; 0BRANCH
	.ascii	"0BRANC"
	.byte	'H'+80H
	.word	BRAN-9
ZBRAN:	.word	.+2
	pop	hl
	ld	a,l
	or	h
	jp	z,BRAN1	; IF (S1)=0 THEN BRANCH
	inc	bc	; ELSE SKIP BRANCH OFFSET
	inc	bc
	jp	NEXT
;
	.byte	86H	; (LOOP)
	.ascii	"(LOOP"
	.byte	')'+80H
	.word	ZBRAN-0AH
XLOOP:	.word	.+2
	ld	de,1	; (DE) <- INCREMENT
XLOO1:	ld	hl,(RPP)	; ((HL)) = INDEX
	ld	a,(hl)	; INDEX <- INDEX + INCR
	add	a,e
	ld	(hl),a
	ld	e,a
	inc	hl
	ld	a,(hl)
	adc	a,d
	ld	(hl),a
	inc	hl	; ((HL)) = LIMIT
	inc	d
	dec	d
	ld	d,a	; (DE) <- NEW INDEX
	jp	m,XLOO2	; IF INCR > 0
	ld	a,e
	sub	(hl)	; THEN (A) <- INDEX - LIMIT
	ld	a,d
	inc	hl
	sbc	a,(hl)
	jp	XLOO3
XLOO2:	ld	a,(hl)	; ELSE (A) <- LIMIT - INDEX
	sub	e
	inc	hl
	ld	a,(hl)
	sbc	a,d
;			; IF (A) < 0
XLOO3:	jp	m,BRAN1	; THEN LOOP AGAIN
	inc	hl	; ELSE DONE
	ld	(RPP),hl	; DISCARD R1 & R2
	inc	bc	; SKIP BRANCH OFFSET
	inc	bc
	jp	NEXT
;
	.byte	87H	; (+LOOP)
	.ascii	"(+LOOP"
	.byte	')'+80H
	.word	XLOOP-9
XPLOO:	.word	.+2
	pop	de	; (DE) <- INCR
	jp	XLOO1
;
	.byte	84H	; (DO)
	.ascii	"(DO"
	.byte	')'+80H
	.word	XPLOO-0AH
XDO:	.word	.+2
	ld	hl,(RPP)	; (RP) <- (RP) - 4
	dec	hl
	dec	hl
	dec	hl
	dec	hl
	ld	(RPP),hl
	pop	de	; (R1) <- (S1) = INIT INDEX
	ld	(hl),e
	inc	hl
	ld	(hl),d
	pop	de	; (R2) <- (S2) = LIMIT
	inc	hl
	ld	(hl),e
	inc	hl
	ld	(hl),d
	jp	NEXT
;
	.byte	81H	; I
	.byte	'I'+80H
	.word	XDO-7
IDO:	.word	.+2	;(S1) <- (R1) , (R1) UNCHANGED
	ld	hl,(RPP)
	ld	e,(hl)	; (DE) <- (R1)
	inc	hl
	ld	d,(hl)
	push	de	; (S1) <- (DE)
	jp	NEXT
;
	.byte	85H	; DIGIT
	.ascii	"DIGI"
	.byte	'T'+80H
	.word	IDO-4
DIGIT:	.word	.+2
	pop	hl	; (L) <- (S1)LB = ASCII CHR TO BE
;			 CONVERTED
	pop	de	; (DE) <- (S2) = BASE VALUE
	ld	a,e
	sub	30H	; IF CHR > "0"
	jp	m,DIGI2
	cp	0AH	; AND IF CHR > "9"
	jp	m,DIGI1
	sub	7
	cp	0AH	; AND IF CHR >= "A"
	jp	m,DIGI2
;			; THEN VALID NUMERIC OR ALPHA CHR
DIGI1:	cp	l	; IF < BASE VALUE
	jp	p,DIGI2
;			; THEN VALID DIGIT CHR
	ld	e,a	; (S2) <- (DE) = CONVERTED DIGIT
	ld	hl,1	; (S1) <- TRUE
	jp	DPUSH
;			; ELSE INVALID DIGIT CHR
DIGI2:	ld	l,h	; (HL) <- FALSE
	jp	HPUSH	; (S1) <- FALSE
;
	.byte	86H	; (FIND)  (2-1)FAILURE
	.ascii	"(FIND"	; (2-3)SUCCESS
	.byte	')'+80H
	.word	DIGIT-8
PFIND:	.word	.+2
	pop	de	; (DE) <- NFA
PFIN1:	pop	hl	; (HL) <- STRING ADDR
	push	hl	; SAVE STRING ADDR FOR NEXT ITERATION
	ld	a,(de)
	xor	(hl)	; CHECK LENGTHS & SMUDGE BIT
	and	3FH
	jp	nz,PFIN4	; LENGTHS DIFFERENT
;			; LENGTHS MATCH, CHECK EACH CHR
PFIN2:	inc	hl	; (HL) <- ADDR NEXT CHR IN STRING
	inc	de	; (DE) <- ADDR NEXT CHR IN NF
	ld	a,(de)
	xor	(hl)	; IGNORE MSB
	add	a,a
	jp	nz,PFIN3	; NO MATCH
	jp	nc,PFIN2	; MATCH SO FAR, LOOP AGAIN
	ld	hl,5	; STRING MATCHES
	add	hl,de	; ((SP)) <- PFA
	ex	(sp),hl
;			; BACK UP TO LENGTH BYTE OF NF = NFA
PFIN6:	dec	de
	ld	a,(de)
	or	a
	jp	p,PFIN6	; IF MSB = 1 THEN (DE) = NFA
	ld	e,a	; (DE) <- LENGTH BYTE
	ld	d,0
	ld	hl,1	; (HL) <- TRUE
	jp	DPUSH  ; RETURN, NF FOUND
;	ABOVE NF NOT A MATCH, TRY ANOTHER
PFIN3:	jp	c,PFIN5	; IF NOT END OF NF
PFIN4:	inc	de	; THEN FIND END OF NF
	ld	a,(de)
	or	a
	jp	p,PFIN4
PFIN5:	inc	de	; (DE) <- LFA
	ex	de,hl
	ld	e,(hl)	; (DE) <- (LFA)
	inc	hl
	ld	d,(hl)
	ld	a,d
	or	e	; IF (LFA) <> 0
	jp	nz,PFIN1	; THEN TRY PREVIOUS DICT. DEF.
;			; ELSE END OF DICTIONARY
	pop	hl	; DISCARD STRING ADDR
	ld	hl,0	; (HL) <- FALSE
	jp	HPUSH  	; RETURN, NO MATCH FOUND
;
	.byte	87H	; ENCLOSE
	.ascii	"ENCLOS"
	.byte	'E'+80H
	.word	PFIND-9
ENCL:	.word	.+2
	pop	de	; (DE) <- (S1) = DELIMITER CHAR
	pop	hl	; (HL) <- (S2) = ADDR TEXT TO SCAN
	push	hl	; (S4) <- ADDR
	ld	a,e
	ld	d,a	; (D) <- DELIM CHR
	ld	e,-1	; INITIALIZE CHR OFFSET COUNTER
	dec	hl	; (HL) <- ADDR-1
;			; SKIP OVER LEADING DELIMITER CHRS
ENCL1:	inc	hl
	inc	e
	cp	(hl)	; IF TEXT CHR = DELIM CHR
	jp	z,ENCL1	; THEN LOOP AGAIN
;			; ELSE NON-DELIM CHR FOUND
	ld	d,0	; (S3) <- (E) = OFFSET TO 1ST NON-DELIM
	push	de
	ld	d,a	; (D) <- DELIM CHR
	ld	a,(hl)	; IF 1ST NON-DELIM = NULL
	and	a
	jp	nz,ENCL2
	ld	d,0	; THEN (S2) <- OFFSET TO BYTE
	inc	e	;   FOLLOWING NULL
	push	de
	dec	e	; (S1) <- OFFSET TO NULL
	push	de
	jp	NEXT
;			; ELSE TEXT CONTAINS NON-DELIM &
;			  NON-NULL CHR
ENCL2:	ld	a,d	; (A) <- DELIM CHR
	inc	hl	; (HL) <- ADDR NEXT CHR
	inc	e	; (E) <- OFFSET TO NEXT CHR
	cp	(hl)	; IF NEXT CHR <> DELIM CHR
	jp	z,ENCL4
	ld	a,(hl)	; AND IF NEXT CHR <> NULL
	and	a
	jp	nz,ENCL2	; THEN CONTINUE SCAN
;			; ELSE CHR = NULL
ENCL3:	ld	d,0	; (S2) <- OFFSET TO NULL
	push	de
	push	de	; (S1) <- OFFSET TO NULL
	jp	NEXT
;			; ELSE CHR = DELIM CHR
ENCL4:	ld	d,0	; (S2) <- OFFSET TO BYTE
;			  FOLLOWING TEXT
	push	de
	inc	e	; (S1) <- OFFSET TO 2 BYTES AFTER
;			    END OF WORD
	push	de
	jp	NEXT
;
	.byte	84H	; EMIT
	.ascii	"EMI"
	.byte	'T'+80H
	.word	ENCL-0AH
EMIT:	.word	DOCOL
	.word	PEMIT
	.word	ONE,OUTT
	.word	PSTOR,SEMIS
;
	.byte	83H	; KEY
	.ascii	"KE"
	.byte	'Y'+80H
	.word	EMIT-7
KEY:	.word	.+2
	jp	PKEY
;
	.byte	89H	; ?TERMINAL
	.ascii	"?TERMINA"
	.byte	'L'+80H
	.word	KEY-6
QTERM:	.word	.+2
	ld	hl,0
	jp	PQTER
;
	.byte	82H	; CR
	.byte	'C'
	.byte	'R'+80H
	.word	QTERM-0CH
CR:	.word	.+2
	jp	PCR
;
	.byte	85H	; CMOVE
	.ascii	"CMOV"
	.byte	'E'+80H
	.word	CR-5
CMOVE:	.word	.+2
	ld	l,c	; (HL) <- (IP)
	ld	h,b
	pop	bc	; (BC) <- (S1) = #CHRS
	pop	de	; (DE) <- (S2) = DEST ADDR
	ex	(sp),hl		; (HL) <- (S3) = SOURCE ADDR
;			; (S1) <- (IP)
	jp	CMOV2	; RETURN IF #CHRS = 0
CMOV1:	ld	a,(hl)	; ((DE)) <- ((HL))
	inc	hl	; INC SOURCE ADDR
	ld	(de),a
	inc	de	; INC DEST ADDR
	dec	bc	; DEC #CHRS
CMOV2:	ld	a,b
	or	c
	jp	nz,CMOV1	; REPEAT IF #CHRS <> 0
	pop	bc	; RESTORE (IP) FROM (S1)
	jp	NEXT
;
	.byte	82H	; U*	16X16 UNSIGNED MULTIPLY
	.byte	'U'	; AVG EXECUUION TIME = 994 CYCLES
	.byte	'*'+80H
	.word	CMOVE-8
USTAR:	.word	.+2
	pop	de	; (DE) <- MPLIER
	pop	hl	; (HL) <- MPCAND
	push	bc	; SAVE IP
	ld	b,h
	ld	a,l	; (BA) <- MPCAND
	call	MPYX	; (AHL)1 <- MPCAND.LB * MPLIER
;			       1ST PARTIAL PRODUCT
	push	hl	; SAVE (HL)1
	ld	h,a
	ld	a,b
	ld	b,h	; SAVE (A)1
	call	MPYX	; (AHL)2 <- MPCAND.HB * MPLIER
;			       2ND PARTIAL PRODUCT
	pop	de	; (DE) <- (HL)1
	ld	c,d	; (BC) <- (AH)1
;	FORM SUM OF PARTIALS:
;			   (AHL) 1
;			+ (AHL)  2
;			--------
;			  (AHLE)
	add	hl,bc	; (HL) <- (HL)2 + (AH)1
	adc	a,0	; (AHLE) <- (BA) * (DE)
	ld	d,l
	ld	l,h
	ld	h,a	; (HLDE) <- MPLIER * MPCAND
	pop	bc	; RESTORE IP
	push	de	; (S2) <- PRODUCT.LW
	jp	HPUSH	; (S1) <- PRODUCT.HW
;
;	MULTIPLY PRIMITIVE
;		(AHL) <- (A) * (DE)
;	#BITS =	 24	  8	16
MPYX:	ld	hl,0	; (HL) <- 0 = PARTIAL PRODUCT.LW
	ld	c,8	; LOOP COUNTER
MPYX1:	add	hl,hl	; LEFT SHIFT (AHL) 24 BITS
	rla
	jp	nc,MPYX2	; IF NEXT MPLIER BIT = 1
	add	hl,de	; THEN ADD MPCAND
	adc	a,0
MPYX2:	dec	c	; IF NOT LAST MPLIER BIT
	jp	nz,MPYX1	; THEN LOOP AGAIN
	ret		; ELSE DONE
;
	.byte	82H	; U/
	.byte	'U'
	.byte	'/'+80H
	.word	USTAR-5
USLAS:	.word	.+2
	ld	hl,4
	add	hl,sp	; ((HL)) <- NUMERATOR.LW
	ld	e,(hl)	; (DE) <- NUMER.LW
	ld	(hl),c	; SAVE IP ON STACK
	inc	hl
	ld	d,(hl)
	ld	(hl),b
	pop	bc	; (BC) <- DENOMINATOR
	pop	hl	; (HL) <- NUMER.HW
	ld	a,l
	sub	c	; IF NUMER >= DENOM
	ld	a,h
	sbc	a,b
	jp	c,USLA1
	ld	hl,0FFFFH	; THEN OVERFLOW
	ld	de,0FFFFH	; SET REM & QUOT TO MAX
	jp	USLA7
USLA1:	ld	a,16	; LOOP COUNTER
USLA2:	add	hl,hl	; LEFT SHIFT (HLDE) THRU CARRY
	rla
	ex	de,hl
	add	hl,hl
	jp	nc,USLA3
	inc	de
	and	a
USLA3:	ex	de,hl		; SHIFT DONE
	rra		; RESTORE 1ST CARRY
	push	af	; SAVE COUNTER
	jp	nc,USLA4	; IF CARRY = 1
	ld	a,l	; THEN (HL) <- (HL) - (BC)
	sub	c
	ld	l,a
	ld	a,h
	sbc	a,b
	ld	h,a
	jp	USLA5
USLA4:	ld	a,l	; ELSE TRY (HL) <- (HL) - (BC)
	sub	c
	ld	l,a
	ld	a,h
	sbc	a,b	; (HL) <- PARTIAL REMAINDER
	ld	h,a
	jp	nc,USLA5
	add	hl,bc	; UNDERFLOW, RESTORE
	dec	de
USLA5:	inc	de	; INC QUOT
USLA6:	pop	af	; RESTORE COUNTER
	dec	a	; IF COUNTER > 0
	jp	nz,USLA2	; THEN LOOP AGAIN
USLA7:	pop	bc	; ELSE DONE, RESTORE IP
	push	hl	; (S2) <- REMAINDER
	push	de	; (S1) <- QUOTIENT
	jp	NEXT
;
	.byte	83H	; AND
	.ascii	"AN"
	.byte	'D'+80H
	.word	USLAS-5
ANDD:	.word	.+2	; (S1) <- (S1) AND (S2)
	pop	de
	pop	hl
	ld	a,e
	and	l
	ld	l,a
	ld	a,d
	and	h
	ld	h,a
	jp	HPUSH
;
	.byte	82H	; OR
	.byte	'O'
	.byte	'V'+80H
	.word	ANDD-6
ORR:	.word	.+2	; (S1) <- (S1) OR (S2)
	pop	de
	pop	hl
	ld	a,e
	or	l
	ld	l,a
	ld	a,d
	or	h
	ld	h,a
	jp	HPUSH
;
	.byte	83H	; XOR
	.ascii	"XO"
	.byte	'R'+80H
	.word	ORR-5
XORR:	.word	.+2	; (S1) <- (S1) XOR (S2)
	pop	de
	pop	hl
	ld	a,e
	xor	l
	ld	l,a
	ld	a,d
	xor	h
	ld	h,a
	jp	HPUSH
;
	.byte	83H	; SP@
	.ascii	"SP"
	.byte	'@'+80H
	.word	XORR-6
SPAT:	.word	.+2	;(S1) <- (SP)
	ld	hl,0
	add	hl,sp	; (HL) <- (SP)
	jp	HPUSH	; (S1) <- (HL)
;
	.byte	83H	; STACK POINTER STORE
	.ascii	"SP"
	.byte	'!'+80H
	.word	SPAT-6
SPSTO:	.word	.+2	;(SP) <- (S0) ( USER VARIABLE )
	ld	hl,(UP)	; (HL) <- USER VAR BASE ADDR
	ld	de,6
	add	hl,de	; (HL) <- S0
	ld	e,(hl)	; (DE) <- (S0)
	inc	hl
	ld	d,(hl)
	ex	de,hl
	ld	sp,hl		; (SP) <- (S0)
	jp	NEXT
;
	.byte	83H	; RP@
	.ascii	"RP"
	.byte	'@'+80H
	.word	SPSTO-6
RPAT:	.word	.+2	;(S1) <- (RP)
	ld	hl,(RPP)
	jp	HPUSH
;
	.byte	83H	; RETURN STACK POINTER STORE
	.ascii	"RP"
	.byte	'!'+80H
	.word	RPAT-6
RPSTO:	.word	.+2	;(RP) <- (R0) ( USER VARIABLE )
	ld	hl,(UP)	; (HL) <- USER VARIABLE BASE ADDR
	ld	de,8
	add	hl,de	; (HL) <- R0
	ld	e,(hl)	; (DE) <- (R0)
	inc	hl
	ld	d,(hl)
	ex	de,hl
	ld	(RPP),hl	; (RP) <- (R0)
	jp	NEXT
;
	.byte	82H	; ;S
	.byte	';'
	.byte	'S'+80H
	.word	RPSTO-6
SEMIS:	.word	.+2	;(IP) <- (R1)
	ld	hl,(RPP)
	ld	c,(hl)	; (BC) <- (R1)
	inc	hl
	ld	b,(hl)
	inc	hl
	ld	(RPP),hl	; (RP) <- (RP) + 2
	jp	NEXT
;
	.byte	85H	; LEAVE
	.ascii	"LEAV"
	.byte	'E'+80H
	.word	SEMIS-5
LEAVE:	.word	.+2	;LIMIT <- INDEX
	ld	hl,(RPP)
	ld	e,(hl)	; (DE) <- (R1) = INDEX
	inc	hl
	ld	d,(hl)
	inc	hl
	ld	(hl),e	; (R2) <- (DE) = LIMIT
	inc	hl
	ld	(hl),d
	jp	NEXT
;
	.byte	82H	; >R
	.byte	'>'
	.byte	'R'+80H
	.word	LEAVE-8
TOR:	.word	.+2	;(R1) <- (S1)
	pop	de	; (DE) <- (S1)
	ld	hl,(RPP)
	dec	hl	; (RP) <- (RP) - 2
	dec	hl
	ld	(RPP),hl
	ld	(hl),e	; ((HL)) <- (DE)
	inc	hl
	ld	(hl),d
	jp	NEXT
;
	.byte	82H	; R>
	.byte	'R'
	.byte	'>'+80H
	.word	TOR-5
FROMR:	.word	.+2	;(S1) <- (R1)
	ld	hl,(RPP)
	ld	e,(hl)	; (DE) <- (R1)
	inc	hl
	ld	d,(hl)
	inc	hl
	ld	(RPP),hl	; (RP) <- (RP) + 2
	push	de	; (S1) <- (DE)
	jp	NEXT
;
	.byte	81H	; R
	.byte	'R'+80H
	.word	FROMR-5
RR:	.word	IDO+2
;
	.byte	82H	; 0=
	.byte	'0'
	.byte	'='+80H
	.word	RR-4
ZEQU:	.word	.+2
	pop	hl	; (HL) <- (S1)
	ld	a,l
	or	h	; IF (HL) = 0
	ld	hl,0	; THEN (HL) <- FALSE
	jp	nz,ZEQU1
	inc	hl	; ELSE (HL) <- TRUE
ZEQU1:	jp	HPUSH	; (S1) <- (HL)
;
	.byte	82H	; 0<
	.byte	'0'
	.byte	'<'+80H
	.word	ZEQU-5
ZLESS:	.word	.+2
	pop	hl	; (HL) <- (S1)
	add	hl,hl	; IF (HL) >= 0
	ld	hl,0	; THEN (HL) <- FALSE
	jp	nc,ZLES1
	inc	hl	; ELSE (HL) <- TRUE
ZLES1:	jp	HPUSH	; (S1) <- (HL)
;
	.byte	81H	; +
	.byte	'+'+80H
	.word	ZLESS-5
PLUS:	.word	.+2	;(S1) <- (S1) + (S2)
	pop	de
	pop	hl
	add	hl,de
	jp	HPUSH
;
	.byte	82H	; D+	(4-2)
	.byte	'D'	; XLW XHW  YLW YHW  ---  SLW SHW
	.byte	'+'+80H	; S4  S3   S2  S1        S2  S1
	.word	PLUS-4
DPLUS:	.word	.+2
	ld	hl,6
	add	hl,sp	; ((HL)) = XLW
	ld	e,(hl)	; (DE) = XLW
	ld	(hl),c	; SAVE IP ON STACK
	inc	hl
	ld	d,(hl)
	ld	(hl),b
	pop	bc	; (BC) <- YHW
	pop	hl	; (HL) <- YLW
	add	hl,de
	ex	de,hl		; (DE) <- YLW + XLW = SUM.LW
	pop	hl	; (HL) <- XHW
	ld	a,l
	adc	a,c
	ld	l,a	; (HL) <- YHW + XHW + CARRY
	ld	a,h
	adc	a,b
	ld	h,a
	pop	bc	; RESTORE IP
	push	de	; (S2) <- SUM.LW
	jp	HPUSH	; (S1) <- SUM.HW
;
	.byte	85H	; MINUS
	.ascii	"MINU"
	.byte	'S'+80H
	.word	DPLUS-5
MINUS:	.word	.+2	;(S1) <- -(S1)	( 2'S COMPLEMENT )
	pop	hl
	ld	a,l
	cpl
	ld	l,a
	ld	a,h
	cpl
	ld	h,a
	inc	hl
	jp	HPUSH
;
	.byte	86H	; DMINUS
	.ascii	"DMINU"
	.byte	'S'+80H
	.word	MINUS-8
DMINU:	.word	.+2
	pop	hl	; (HL) <- HW
	pop	de	; (DE) <- LW
	sub	a
	sub	e	; (DE) <- 0 - (DE)
	ld	e,a
	ld	a,0
	sbc	a,d
	ld	d,a
	ld	a,0
	sbc	a,l	; (HL) <- 0 - (HL)
	ld	l,a
	ld	a,0
	sbc	a,h
	ld	h,a
	push	de	; (S2) <- LW
	jp	HPUSH	; (S1) <- HW
;
	.byte	84H	; OVER
	.ascii	"OVE"
	.byte	'R'+80H
	.word	DMINU-9
OVER:	.word	.+2
	pop	de
	pop	hl
	push	hl
	jp	DPUSH
;
	.byte	84H	; DROP
	.ascii	"DRO"
	.byte	'P'+80H
	.word	OVER-7
DROP:	.word	.+2
	pop	hl
	jp	NEXT
;
	.byte	84H	; SWAP
	.ascii	"SWA"
	.byte	'P'+80H
	.word	DROP-7
SWAP:	.word	.+2
	pop	hl
	ex	(sp),hl
	jp	HPUSH
;
	.byte	83H	; DUP
	.ascii	"DU"
	.byte	'P'+80H
	.word	SWAP-7
DUP:	.word	.+2
	pop	hl
	push	hl
	jp	HPUSH
;
	.byte	84H	; 2DUP
	.ascii	"2DU"
	.byte	'P'+80H
	.word	DUP-6
TDUP:	.word	.+2
	pop	hl
	pop	de
	push	de
	push	hl
	jp	DPUSH
;
	.byte	82H	; PLUS STORE
	.byte	'+'
	.byte	'!'+80H
	.word	TDUP-7
PSTOR:	.word	.+2	;((S1)) <- ((S1)) + (S2)
	pop	hl	; (HL) <- (S1) = ADDR
	pop	de	; (DE) <- (S2) = INCR
	ld	a,(hl)	; ((HL)) <- ((HL)) + (DE)
	add	a,e
	ld	(hl),a
	inc	hl
	ld	a,(hl)
	adc	a,d
	ld	(hl),a
	jp	NEXT
;
	.byte	86H	; TOGGLE
	.ascii	"TOGGL"
	.byte	'E'+80H
	.word	PSTOR-5
TOGGL:	.word	.+2	;((S2)) <- ((S2)) XOR (S1)LB
	pop	de	; (E) <- BYTE MASK
	pop	hl	; (HL) <- ADDR
	ld	a,(hl)
	xor	e
	ld	(hl),a	; (ADDR) <- (ADDR) XOR (E)
	jp	NEXT
;
	.byte	81H	; @
	.byte	'@'+80H
	.word	TOGGL-9
AT:	.word	.+2	;(S1) <- ((S1))
	pop	hl	; (HL) <- ADDR
	ld	e,(hl)	; (DE) <- (ADDR)
	inc	hl
	ld	d,(hl)
	push	de	; (S1) <- (DE)
	jp	NEXT
;
	.byte	82H	; C@
	.byte	'C'
	.byte	'@'+80H
	.word	AT-4
CAT:	.word	.+2	;(S1) <- ((S1))LB
	pop	hl	; (HL) <- ADDR
	ld	l,(hl)	; (HL) <- (ADDR)LB
	ld	h,0
	jp	HPUSH
;
	.byte	82H	; 2@
	.byte	'2'
	.byte	'@'+80H
	.word	CAT-5
TAT:	.word	.+2
	pop	hl	; (HL) <- ADDR HW
	ld	de,2
	add	hl,de	; (HL) <- ADDR LW
	ld	e,(hl)	; (DE) <- LW
	inc	hl
	ld	d,(hl)
	push	de	; (S2) <- LW
	ld	de,-3	; (HL) <- ADDR HW
	add	hl,de
	ld	e,(hl)	; (DE) <- HW
	inc	hl
	ld	d,(hl)
	push	de	; (S1) <- HW
	jp	NEXT
;
	.byte	81H	; STORE
	.byte	'!'+80H
	.word	TAT-5
STORE:	.word	.+2	;((S1)) <- (S2)
	pop	hl	; (HL) <- (S1) = ADDR
	pop	de	; (DE) <- (S2) = VALUE
	ld	(hl),e	; ((HL)) <- (DE)
	inc	hl
	ld	(hl),d
	jp	NEXT
;
	.byte	82H	; C STORE
	.byte	'C'
	.byte	'!'+80H
	.word	STORE-4
CSTOR:	.word	.+2	;((S1))LB <- (S2)LB
	pop	hl	; (HL) <- (S1) = ADDR
	pop	de	; (DE) <- (S2) = BYTE
	ld	(hl),e	; ((HL))LB <- (E)
	jp	NEXT
;
	.byte	82H	; 2 STORE
	.byte	'2'
	.byte	'!'+80H
	.word	CSTOR-5
TSTOR:	.word	.+2
	pop	hl	; (HL) <- ADDR
	pop	de	; (DE) <- HW
	ld	(hl),e	; (ADDR) <- HW
	inc	hl
	ld	(hl),d
	inc	hl	; (HL) <- ADDR LW
	pop	de	; (DE) <- LW
	ld	(hl),e	; (ADDR+2) <- LW
	inc	hl
	ld	(hl),d
	jp	NEXT
;
	.byte	0C1H	; :
	.byte	':'+80H
	.word	TSTOR-5
COLON:	.word	DOCOL
	.word	QEXEC
	.word	SCSP
	.word	CURR
	.word	AT
	.word	CONT
	.word	STORE
	.word	CREAT
	.word	RBRAC
	.word	PSCOD
DOCOL:	ld	hl,(RPP)
	dec	hl	; (R1) <- (IP)
	ld	(hl),b
	dec	hl	; (RP) <- (RP) - 2
	ld	(hl),c
	ld	(RPP),hl
	inc	de	; (DE) <- CFA+2 = (W)
	ld	c,e	; (IP) <- (DE) = (W)
	ld	b,d
	jp	NEXT
;
	.byte	0C1H	; ;
	.byte	';'+80H
	.word	COLON-4
SEMI:	.word	DOCOL
	.word	QCSP
	.word	COMP
	.word	SEMIS
	.word	SMUDG
	.word	LBRAC
	.word	SEMIS
;
	.byte	84H	; NOOP
	.ascii	"NOO"
	.byte	'P'+80H
	.word	SEMI-4
NOOP:	.word	DOCOL
	.word	SEMIS
 ;
	.byte	88H	; CONSTANT
	.ascii	"CONSTAN"
	.byte	'T'+80H
	.word	NOOP-7
CON:	.word	DOCOL
	.word	CREAT
	.word	SMUDG
	.word	COMMA
	.word	PSCOD
DOCON:	inc	de	; (DE) <- PFA
	ex	de,hl
	ld	e,(hl)	; (DE) <- (PFA)
	inc	hl
	ld	d,(hl)
	push	de	; (S1) <- (PFA)
	jp	NEXT
;
	.byte	88H	; VARIABLE
	.ascii	"VARIABL"
	.byte	'E'+80H
	.word	CON-0BH
VAR:	.word	DOCOL
	.word	CON
	.word	PSCOD
DOVAR:	inc	de	; (DE) <- PFA
	push	de	; (S1) <- PFA
	jp	NEXT
;
	.byte	84H	; USER
	.ascii	"USE"
	.byte	'R'+80H
	.word	VAR-0BH
USER:	.word	DOCOL
	.word	CON
	.word	PSCOD
DOUSE:	inc	de	; (DE) <- PFA
	ex	de,hl
	ld	e,(hl)	; (DE) <- USER VARIABLE OFFSET
	ld	d,0
	ld	hl,(UP)	; (HL) <- USER VARIABLE BASE ADDR
	add	hl,de	; (HL) <- (HL) + (DE)
	jp	HPUSH	; (S1) <- BASE + OFFSET
;
	.byte	81H	; 0
	.byte	'0'+80H
	.word	USER-7
ZERO:	.word	DOCON
	.word	0
;
	.byte	81H	; 1
	.byte	'1'+80H
	.word	ZERO-4
ONE:	.word	DOCON
	.word	1
;
	.byte	81H	; 2
	.byte	'2'+80H
	.word	ONE-4
TWO:	.word	DOCON
	.word	2
;
	.byte	81H	; 3
	.byte	'3'+80H
	.word	TWO-4
THREE:	.word	DOCON
	.word	3
;
	.byte	82H	; BL
	.byte	'B'
	.byte	'L'+80H
	.word	THREE-4
BL:	.word	DOCON
	.word	20H
;
	.byte	83H	; C/L ( CHARACTERS/LINE )
	.ascii	"C/"
	.byte	'L'+80H
	.word	BL-5
CSLL:	.word	DOCON
	.word	64
;
	.byte	85H	; FIRST
	.ascii	"FIRS"
	.byte	'T'+80H
	.word	CSLL-6
FIRST:	.word	DOCON
	.word	BUF1
;
	.byte	85H	; LIMIT
	.ascii	"LIMI"
	.byte	'T'+80H
	.word	FIRST-8
LIMIT:	.word	DOCON
	.word	EM
;
	.byte	85H	; B/BUF ( BYTES/BUFFER )
	.ascii	"B/BU"
	.byte	'F'+80H
	.word	LIMIT-8
BBUF:	.word	DOCON
	.word	KBBUF
;
	.byte	85H	; B/SCR ( BUFFERS/SCREEN )
	.ascii	"B/SC"
	.byte	'R'+80H
	.word	BBUF-8
BSCR:	.word	DOCON
	.word	400H/KBBUF
;
	.byte	87H	; +ORIGIN
	.ascii	"+ORIGI"
	.byte	'N'+80H
	.word	BSCR-8
PORIG:	.word	DOCOL
	.word	LIT
	.word	ORIG
	.word	PLUS
	.word	SEMIS
;
;	USER VARIABLES
;
	.byte	82H	; S0
	.byte	'S'
	.byte	'0'+80H
	.word	PORIG-0AH
SZERO:	.word	DOUSE
	.word	6
;
	.byte	82H	; R0
	.byte	'R'
	.byte	'0'+80H
	.word	SZERO-5
RZERO:	.word	DOUSE
	.word	8
;
	.byte	83H	; TIB
	.ascii	"TI"
	.byte	'B'+80H
	.word	RZERO-5
TIB:	.word	DOUSE
	.byte	0AH
;
	.byte	85H	; WIDTH
	.ascii	"WIDT"
	.byte	'H'+80H
	.word	TIB-6
WIDTH:	.word	DOUSE
	.byte	0CH
;
	.byte	87H	; WARNING
	.ascii	"WARNIN"
	.byte	'G'+80H
	.word	WIDTH-8
WARN:	.word	DOUSE
	.byte	0EH
;
	.byte	85H	; FENCE
	.ascii	"FENC"
	.byte	'E'+80H
	.word	WARN-0AH
FENCE:	.word	DOUSE
	.byte	10H
;
	.byte	82H	; DP
	.byte	'D'
	.byte	'P'+80H
	.word	FENCE-8
DP:	.word	DOUSE
	.byte	12H
;
	.byte	88H	; VOC-LINK
	.ascii	"VOC-LIN"
	.byte	'K'+80H
	.word	DP-5
VOCL:	.word	DOUSE
	.word	14H
;
	.byte	83H	; BLK
	.ascii	"BL"
	.byte	'K'+80H
	.word	VOCL-0BH
BLK:	.word	DOUSE
	.byte	16H
;
	.byte	82H	; IN
	.byte	'I'
	.byte	'N'+80H
	.word	BLK-6
INN:	.word	DOUSE
	.byte	18H
;
	.byte	83H	; OUT
	.ascii	"OU"
	.byte	'T'+80H
	.word	INN-5
OUTT:	.word	DOUSE
	.byte	1AH
;
	.byte	83H	; SCR
	.ascii	"SC"
	.byte	'R'+80H
	.word	OUTT-6
SCR:	.word	DOUSE
	.byte	1CH
;
	.byte	86H	; OFFSET
	.ascii	"OFFSE"
	.byte	'T'+80H
	.word	SCR-6
OFSET:	.word	DOUSE
	.byte	1EH
;
	.byte	87H	; CONTEXT
	.ascii	"CONTEX"
	.byte	'T'+80H
	.word	OFSET-9
CONT:	.word	DOUSE
	.byte	20H
;
	.byte	87H	; CURRENT
	.ascii	"CURREN"
	.byte	'T'+80H
	.word	CONT-0AH
CURR:	.word	DOUSE
	.byte	22H
;
	.byte	85H	; STATE
	.ascii	"STAT"
	.byte	'E'+80H
	.word	CURR-0AH
STATE:	.word	DOUSE
	.byte	24H
;
	.byte	84H	; BASE
	.ascii	"BAS"
	.byte	'E'+80H
	.word	STATE-8
BASE:	.word	DOUSE
	.byte	26H
;
	.byte	83H	; DPL
	.ascii	"DP"
	.byte	'L'+80H
	.word	BASE-7
DPL:	.word	DOUSE
	.byte	28H
;
	.byte	83H	; FLD
	.ascii	"FL"
	.byte	'D'+80H
	.word	DPL-6
FLD:	.word	DOUSE
	.byte	2AH
;
	.byte	83H	; CSP
	.ascii	"CS"
	.byte	'P'+80H
	.word	FLD-6
CSPP:	.word	DOUSE
	.byte	2CH
;
	.byte	82H	; R#
	.byte	'R'
	.byte	'#'+80H
	.word	CSPP-6
RNUM:	.word	DOUSE
	.byte	2EH
;
	.byte	83H	; HLD
	.ascii	"HL"
	.byte	'D'+80H
	.word	RNUM-5
HLD:	.word	DOUSE
	.word	30H
;
;	END OF USER VARIABLES
;
	.byte	82H	; 1+
	.byte	'1'
	.byte	'+'+80H
	.word	HLD-6
ONEP:	.word	DOCOL
	.word	ONE
	.word	PLUS
	.word	SEMIS
;
	.byte	82H	; 2+
	.byte	'2'
	.byte	'+'+80H
	.word	ONEP-5
TWOP:	.word	DOCOL
	.word	TWO
	.word	PLUS
	.word	SEMIS
;
	.byte	84H	; HERE
	.ascii	"HER"
	.byte	'E'+80H
	.word	TWOP-5
HERE:	.word	DOCOL
	.word	DP
	.word	AT
	.word	SEMIS
;
	.byte	85H	; ALLOT
	.ascii	"ALLO"
	.byte	'T'+80H
	.word	HERE-7
ALLOT:	.word	DOCOL
	.word	DP
	.word	PSTOR
	.word	SEMIS
;
	.byte	81H	; ,
	.byte	','+80H
	.word	ALLOT-8
COMMA:	.word	DOCOL
	.word	HERE
	.word	STORE
	.word	TWO
	.word	ALLOT
	.word	SEMIS
;
	.byte	82H	; C,
	.byte	'C'
	.byte	','+80H
	.word	COMMA-4
CCOMM:	.word	DOCOL
	.word	HERE
	.word	CSTOR
	.word	ONE
	.word	ALLOT
	.word	SEMIS
;
;	SUBROUTINE USED BY - AND <
;			; (HL) <- (HL) - (DE)
SSUB:	ld	a,l	; LB
	sub	e
	ld	l,a
	ld	a,h	; HB
	sbc	a,d
	ld	h,a
	ret
;
	.byte	81H	; -
	.byte	'-'+80H
	.word	CCOMM-5
SUBB:	.word	.+2
	pop	de	; (DE) <- (S1) = Y
	pop	hl	; (HL) <- (S2) = X
	call	SSUB
	jp	HPUSH	; (S1) <- X - Y
;
	.byte	81H	; =
	.byte	'='+80H
	.word	SUBB-4
EQUAL:	.word	DOCOL
	.word	SUBB
	.word	ZEQU
	.word	SEMIS
;
	.byte	81H	; <
	.byte	'<'+80H		; X  <  Y
	.word	EQUAL-4		; S2    S1
LESS:	.word	.+2
	pop	de	; (DE) <- (S1) = Y
	pop	hl	; (HL) <- (S2) = X
	ld	a,d	; IF X & Y HAVE SAME SIGNS
	xor	h
	jp	m,LES1
	call	SSUB	; (HL) <- X - Y
LES1:	inc	h	; IF (HL) >= 0
	dec	h
	jp	m,LES2
	ld	hl,0	; THEN X >= Y
	jp	HPUSH	; (S1) <- FALSE
LES2:	ld	hl,1	; ELSE X < Y
	jp	HPUSH	; (S1) <- TRUE
;
	.byte	82H	; U< ( UNSIGNED < )
	.byte	'U'
	.byte	'<'+80H
	.word	LESS-4
ULESS:	.word	DOCOL,TDUP
	.word	XORR,ZLESS
	.word	ZBRAN,ULES1-.	; IF
	.word	DROP,ZLESS
	.word	ZEQU
	.word	BRAN,ULES2-.
ULES1:	.word	SUBB,ZLESS	; ELSE
ULES2:	.word	SEMIS		; ENDIF
;
	.byte	81H	; >
	.byte	'>'+80H
	.word	ULESS-5
GREAT:	.word	DOCOL
	.word	SWAP
	.word	LESS
	.word	SEMIS
;
	.byte	83H	; ROT
	.ascii	"RO"
	.byte	'T'+80H
	.word	GREAT-4
ROT:	.word	.+2
	pop	de
	pop	hl
	ex	(sp),hl
	jp	DPUSH
;
	.byte	85H	; SPACE
	.ascii	"SPAC"
	.byte	'E'+80H
	.word	ROT-6
SPACE:	.word	DOCOL
	.word	BL
	.word	EMIT
	.word	SEMIS
;
	.byte	84H	; -DUP
	.ascii	"-DU"
	.byte	'P'+80H
	.word	SPACE-8
DDUP:	.word	DOCOL
	.word	DUP
	.word	ZBRAN	; IF
	.word	DDUP1-.
	.word	DUP	; ENDIF
DDUP1:	.word	SEMIS
;
	.byte	88H	; TRAVERSE
	.ascii	"TRAVERS"
	.byte	'E'+80H
	.word	DDUP-7
TRAV:	.word	DOCOL
	.word	SWAP
TRAV1:	.word	OVER	; BEGIN
	.word	PLUS
	.word	LIT
	.word	7FH
	.word	OVER
	.word	CAT
	.word	LESS
	.word	ZBRAN	; UNTIL
	.word	TRAV1-.
	.word	SWAP
	.word	DROP
	.word	SEMIS
;
	.byte	86H	; LATEST
	.ascii	"LATES"
	.byte	'T'+80H
	.word	TRAV-0BH
LATES:	.word	DOCOL
	.word	CURR
	.word	AT
	.word	AT
	.word	SEMIS
;
	.byte	83H	; LFA
	.ascii	"LF"
	.byte	'A'+80H
	.word	LATES-9
LFA:	.word	DOCOL
	.word	LIT
	.word	4
	.word	SUBB
	.word	SEMIS
;
	.byte	83H	; CFA
	.ascii	"CF"
	.byte	'A'+80H
	.word	LFA-6
CFA:	.word	DOCOL
	.word	TWO
	.word	SUBB
	.word	SEMIS
;
	.byte	83H	; NFA
	.ascii	"NF"
	.byte	'A'+80H
	.word	CFA-6
NFA:	.word	DOCOL
	.word	LIT
	.word	5
	.word	SUBB
	.word	LIT
	.word	-1
	.word	TRAV
	.word	SEMIS
;
	.byte	83H	; PFA
	.ascii	"PF"
	.byte	'A'+80H
	.word	NFA-6
PFA:	.word	DOCOL
	.word	ONE
	.word	TRAV
	.word	LIT
	.word	5
	.word	PLUS
	.word	SEMIS
;
	.byte	84H	; STORE CSP
	.ascii	"!CS"
	.byte	'P'+80H
	.word	PFA-6
SCSP:	.word	DOCOL
	.word	SPAT
	.word	CSPP
	.word	STORE
	.word	SEMIS
;
	.byte	86H	; ?ERROR
	.ascii	"?ERRO"
	.byte	'R'+80H
	.word	SCSP-7
QERR:	.word	DOCOL
	.word	SWAP
	.word	ZBRAN	; IF
	.word	QERR1-.
	.word	ERROR
	.word	BRAN	; ELSE
	.word	QERR2-.
QERR1:	.word	DROP	; ENDIF
QERR2:	.word	SEMIS
;
	.byte	85H	; ?COMP
	.ascii	"?COM"
	.byte	'P'+80H
	.word	QERR-9
QCOMP:	.word	DOCOL
	.word	STATE
	.word	AT
	.word	ZEQU
	.word	LIT
	.word	11H
	.word	QERR
	.word	SEMIS
;
	.byte	85H	; ?EXEC
	.ascii	"?EXE"
	.byte	'C'+80H
	.word	QCOMP-8
QEXEC:	.word	DOCOL
	.word	STATE
	.word	AT
	.word	LIT
	.word	12H
	.word	QERR
	.word	SEMIS
;
	.byte	86H	; ?PAIRS
	.ascii	"?PAIR"
	.byte	'S'+80H
	.word	QEXEC-8
QPAIR:	.word	DOCOL
	.word	SUBB
	.word	LIT
	.word	13H
	.word	QERR
	.word	SEMIS
;
	.byte	84H	; ?CSP
	.ascii	"?CS"
	.byte	'P'+80H
	.word	QPAIR-9
QCSP:	.word	DOCOL
	.word	SPAT
	.word	CSPP
	.word	AT
	.word	SUBB
	.word	LIT
	.word	14H
	.word	QERR
	.word	SEMIS
;
	.byte	88H	; ?LOADING
	.ascii	"?LOADIN"
	.byte	'G'+80H
	.word	QCSP-7
QLOAD:	.word	DOCOL
	.word	BLK
	.word	AT
	.word	ZEQU
	.word	LIT
	.word	16H
	.word	QERR
	.word	SEMIS
;
	.byte	87H	; COMPILE
	.ascii	"COMPIL"
	.byte	'E'+80H
	.word	QLOAD-0BH
COMP:	.word	DOCOL
	.word	QCOMP
	.word	FROMR
	.word	DUP
	.word	TWOP
	.word	TOR
	.word	AT
	.word	COMMA
	.word	SEMIS
;
	.byte	0C1H	; [
	.byte	'['+80H
	.word	COMP-0AH
LBRAC:	.word	DOCOL
	.word	ZERO
	.word	STATE
	.word	STORE
	.word	SEMIS
;
	.byte	81H	; ]
	.byte	']'+80H
	.word	LBRAC-4
RBRAC:	.word	DOCOL
	.word	LIT,0C0H
	.word	STATE,STORE
	.word	SEMIS
;
	.byte	86H	; SMUDGE
	.ascii	"SMUDG"
	.byte	'E'+80H
	.word	RBRAC-4
SMUDG:	.word	DOCOL
	.word	LATES
	.word	LIT
	.word	20H
	.word	TOGGL
	.word	SEMIS
;
	.byte	83H	; HEX
	.ascii	"HE"
	.byte	'X'+80H
	.word	SMUDG-9
HEX:	.word	DOCOL
	.word	LIT
	.word	10H
	.word	BASE
	.word	STORE
	.word	SEMIS
;
	.byte	87H	; DECIMAL
	.ascii	"DECIMA"
	.byte	'L'+80H
	.word	HEX-6
DEC:	.word	DOCOL
	.word	LIT
	.word	0AH
	.word	BASE
	.word	STORE
	.word	SEMIS
;
	.byte	87H	; (;CODE)
	.ascii	"(;CODE"
	.byte	')'+80H
	.word	DEC-0AH
PSCOD:	.word	DOCOL
	.word	FROMR
	.word	LATES
	.word	PFA
	.word	CFA
	.word	STORE
	.word	SEMIS
;
	.byte	0C5H	; ;CODE
	.ascii	";COD"
	.byte	'E'+80H
	.word	PSCOD-0AH
SEMIC:	.word	DOCOL
	.word	QCSP
	.word	COMP
	.word	PSCOD
	.word	LBRAC
SEMI1:	.word	NOOP	; ( ASSEMBLER )
	.word	SEMIS
;
	.byte	87H	; <BUILDS
	.ascii	"<BUILD"
	.byte	'S'+80H
	.word	SEMIC-8
BUILD:	.word	DOCOL
	.word	ZERO
	.word	CON
	.word	SEMIS
;
	.byte	85H	; DOES>
	.ascii	"DOES"
	.byte	'>'+80H
	.word	BUILD-0AH
DOES:	.word	DOCOL
	.word	FROMR
	.word	LATES
	.word	PFA
	.word	STORE
	.word	PSCOD
DODOE:	ld	hl,(RPP)	; (HL) <- (RP)
	dec	hl
	ld	(hl),b	; (R1) <- (IP) = PFA = (SUBSTITUTE CFA)
	dec	hl
	ld	(hl),c
	ld	(RPP),hl	; (RP) <- (RP) - 2
	inc	de	; (DE) <- PFA = (SUBSTITUTE CFA)
	ex	de,hl
	ld	c,(hl)	; (IP) <- (SUBSTITUTE CFA)
	inc	hl
	ld	b,(hl)
	inc	hl
	jp	HPUSH	; (S1) <- PFA+2 = SUBSTITUTE PFA
;
	.byte	85H	; COUNT
	.ascii	"COUN"
	.byte	'T'+80H
	.word	DOES-8
COUNT:	.word	DOCOL
	.word	DUP
	.word	ONEP
	.word	SWAP
	.word	CAT
	.word	SEMIS
;
	.byte	84H	; TYPE
	.ascii	"TYP"
	.byte	'E'+80H
	.word	COUNT-8
TYPE:	.word	DOCOL
	.word	DDUP
	.word	ZBRAN	; IF
	.word	TYPE1-.
	.word	OVER
	.word	PLUS
	.word	SWAP
	.word	XDO	; DO
TYPE2:	.word	IDO
	.word	CAT
	.word	EMIT
	.word	XLOOP	; LOOP
	.word	TYPE2-.
	.word	BRAN	; ELSE
	.word	TYPE3-.
TYPE1:	.word	DROP	; ENDIF
TYPE3:	.word	SEMIS
;
	.byte	89H	; -TRAILING
	.ascii	"-TRAILIN"
	.byte	'G'+80H
	.word	TYPE-7
DTRAI:	.word	DOCOL
	.word	DUP
	.word	ZERO
	.word	XDO	; DO
DTRA1:	.word	OVER
	.word	OVER
	.word	PLUS
	.word	ONE
	.word	SUBB
	.word	CAT
	.word	BL
	.word	SUBB
	.word	ZBRAN	; IF
	.word	DTRA2-.
	.word	LEAVE
	.word	BRAN	; ELSE
	.word	DTRA3-.
DTRA2:	.word	ONE
	.word	SUBB	; ENDIF
DTRA3:	.word	XLOOP	; LOOP
	.word	DTRA1-.
	.word	SEMIS
;
	.byte	84H	; (.")
	.byte	'(', '.', 0x22
	.byte	')'+80H
	.word	DTRAI-0CH
PDOTQ:	.word	DOCOL
	.word	RR
	.word	COUNT
	.word	DUP
	.word	ONEP
	.word	FROMR
	.word	PLUS
	.word	TOR
	.word	TYPE
	.word	SEMIS
;
	.byte	0C2H	; ."
	.byte	'.'
	.byte	0x22+80H
	.word	PDOTQ-7
DOTQ:	.word	DOCOL
	.word	LIT
	.word	22H
	.word	STATE
	.word	AT
	.word	ZBRAN	; IF
	.word	DOTQ1-.
	.word	COMP
	.word	PDOTQ
	.word	WORD
	.word	HERE
	.word	CAT
	.word	ONEP
	.word	ALLOT
	.word	BRAN	; ELSE
	.word	DOTQ2-.
DOTQ1:	.word	WORD
	.word	HERE
	.word	COUNT
	.word	TYPE	; ENDIF
DOTQ2:	.word	SEMIS
;
	.byte	86H	; EXPECT
	.ascii	"EXPEC"
	.byte	'T'+80H
	.word	DOTQ-5
EXPEC:	.word	DOCOL
	.word	OVER
	.word	PLUS
	.word	OVER
	.word	XDO	; DO
EXPE1:	.word	KEY
	.word	DUP
	.word	LIT
	.word	0EH
	.word	PORIG
	.word	AT
	.word	EQUAL
	.word	ZBRAN	; IF
	.word	EXPE2-.
	.word	DROP
	.word	DUP
	.word	IDO
	.word	EQUAL
	.word	DUP
	.word	FROMR
	.word	TWO
	.word	SUBB
	.word	PLUS
	.word	TOR
	.word	ZBRAN	; IF
	.word	EXPE6-.
	.word	LIT
	.word	BELL
	.word	BRAN	; ELSE
	.word	EXPE7-.
EXPE6:	.word	LIT
	.word	BSOUT	; ENDIF
EXPE7:	.word	BRAN	; ELSE
	.word	EXPE3-.
EXPE2:	.word	DUP
	.word	LIT
	.word	0DH
	.word	EQUAL
	.word	ZBRAN	; IF
	.word	EXPE4-.
	.word	LEAVE
	.word	DROP
	.word	BL
	.word	ZERO
	.word	BRAN	; ELSE
	.word	EXPE5-.
EXPE4:	.word	DUP	; ENDIF
EXPE5:	.word	IDO
	.word	CSTOR
	.word	ZERO
	.word	IDO
	.word	ONEP
	.word	STORE	; ENDIF
EXPE3:	.word	EMIT
	.word	XLOOP	; LOOP
	.word	EXPE1-.
	.word	DROP
	.word	SEMIS
;
	.byte	85H	; QUERY
	.ascii	"QUER"
	.byte	'Y'+80H
	.word	EXPEC-9
QUERY:	.word	DOCOL
	.word	TIB
	.word	AT
	.word	LIT
	.word	50H
	.word	EXPEC
	.word	ZERO
	.word	INN
	.word	STORE
	.word	SEMIS
;
	.byte	0C1H	; 0 (NULL)
	.byte	80H
	.word	QUERY-8
NULL:	.word	DOCOL
	.word	BLK
	.word	AT
	.word	ZBRAN	; IF
	.word	NULL1-.
	.word	ONE
	.word	BLK
	.word	PSTOR
	.word	ZERO
	.word	INN
	.word	STORE
	.word	BLK
	.word	AT
	.word	BSCR
	.word	ONE
	.word	SUBB
	.word	ANDD
	.word	ZEQU
	.word	ZBRAN	; IF
	.word	NULL2-.
	.word	QEXEC
	.word	FROMR
	.word	DROP	; ENDIF
NULL2:	.word	BRAN	; ELSE
	.word	NULL3-.
NULL1:	.word	FROMR
	.word	DROP	; ENDIF
NULL3:	.word	SEMIS
;
	.byte	84H	; FILL
	.ascii	"FIL"
	.byte	'L'+80H
	.word	NULL-4
FILL:	.word	.+2
	ld	l,c
	ld	h,b
	pop	de
	pop	bc
	ex	(sp),hl
	ex	de,hl
FILL1:	ld	a,b	; BEGIN
	or	c
	jp	z,FILL2	; WHILE
	ld	a,l
	ld	(de),a
	inc	de
	dec	bc
	jp	FILL1	; REPEAT
FILL2:	pop	bc
	jp	NEXT
;
	.byte	85H	; ERASE
	.ascii	"ERAS"
	.byte	'E'+80H
	.word	FILL-7
ERASEE:	.word	DOCOL
	.word	ZERO
	.word	FILL
	.word	SEMIS
;
	.byte	86H	; BLANKS
	.ascii	"BLANK"
	.byte	'S'+80H
	.word	ERASEE-8
BLANK:	.word	DOCOL
	.word	BL
	.word	FILL
	.word	SEMIS
;
	.byte	84H	; HOLD
	.ascii	"HOL"
	.byte	'D'+80H
	.word	BLANK-9
HOLD:	.word	DOCOL
	.word	LIT
	.word	-1
	.word	HLD
	.word	PSTOR
	.word	HLD
	.word	AT
	.word	CSTOR
	.word	SEMIS
;
	.byte	83H	; PAD
	.ascii	"PA"
	.byte	'D'+80H
	.word	HOLD-7
PAD:	.word	DOCOL
	.word	HERE
	.word	LIT
	.word	44H
	.word	PLUS
	.word	SEMIS
;
	.byte	84H	; WORD
	.ascii	"WOR"
	.byte	'D'+80H
	.word	PAD-6
WORD:	.word	DOCOL
	.word	BLK
	.word	AT
	.word	ZBRAN	; IF
	.word	WORD1-.
	.word	BLK
	.word	AT
	.word	BLOCK
	.word	BRAN	; ELSE
	.word	WORD2-.
WORD1:	.word	TIB
	.word	AT	; ENDIF
WORD2:	.word	INN
	.word	AT
	.word	PLUS
	.word	SWAP
	.word	ENCL
	.word	HERE
	.word	LIT
	.word	22H
	.word	BLANK
	.word	INN
	.word	PSTOR
	.word	OVER
	.word	SUBB
	.word	TOR
	.word	RR
	.word	HERE
	.word	CSTOR
	.word	PLUS
	.word	HERE
	.word	ONEP
	.word	FROMR
	.word	CMOVE
	.word	SEMIS
;
	.byte	88H	; (NUMBER)
	.ascii	"(NUMBER"
	.byte	')'+80H
	.word	WORD-7
PNUMB:	.word	DOCOL
PNUM1:	.word	ONEP	; BEGIN
	.word	DUP
	.word	TOR
	.word	CAT
	.word	BASE
	.word	AT
	.word	DIGIT
	.word	ZBRAN	; WHILE
	.word	PNUM2-.
	.word	SWAP
	.word	BASE
	.word	AT
	.word	USTAR
	.word	DROP
	.word	ROT
	.word	BASE
	.word	AT
	.word	USTAR
	.word	DPLUS
	.word	DPL
	.word	AT
	.word	ONEP
	.word	ZBRAN	; IF
	.word	PNUM3-.
	.word	ONE
	.word	DPL
	.word	PSTOR	; ENDIF
PNUM3:	.word	FROMR
	.word	BRAN	; REPEAT
	.word	PNUM1-.
PNUM2:	.word	FROMR
	.word	SEMIS
;
	.byte	86H	; NUMBER
	.ascii	"NUMBE"
	.byte	'R'+80H
	.word	PNUMB-0BH
NUMB:	.word	DOCOL
	.word	ZERO
	.word	ZERO
	.word	ROT
	.word	DUP
	.word	ONEP
	.word	CAT
	.word	LIT
	.word	2DH
	.word	EQUAL
	.word	DUP
	.word	TOR
	.word	PLUS
	.word	LIT
	.word	-1
NUMB1:	.word	DPL	; BEGIN
	.word	STORE
	.word	PNUMB
	.word	DUP
	.word	CAT
	.word	BL
	.word	SUBB
	.word	ZBRAN	; WHILE
	.word	NUMB2-.
	.word	DUP
	.word	CAT
	.word	LIT
	.word	2EH
	.word	SUBB
	.word	ZERO
	.word	QERR
	.word	ZERO
	.word	BRAN	; REPEAT
	.word	NUMB1-.
NUMB2:	.word	DROP
	.word	FROMR
	.word	ZBRAN	; IF
	.word	NUMB3-.
	.word	DMINU	; ENDIF
NUMB3:	.word	SEMIS
;
	.byte	85H	; -FIND	(0-3) SUCCESS
	.ascii	"-FIN"	; (0-1) FAILURE
	.byte	'D'+80H
	.word	NUMB-9
DFIND:	.word	DOCOL
	.word	BL
	.word	WORD
	.word	HERE
	.word	CONT
	.word	AT
	.word	AT
	.word	PFIND
	.word	DUP
	.word	ZEQU
	.word	ZBRAN	; IF
	.word	DFIN1-.
	.word	DROP
	.word	HERE
	.word	LATES
	.word	PFIND	; ENDIF
DFIN1:	.word	SEMIS
;
	.byte	87H	; (ABORT)
	.ascii	"(ABORT"
	.byte	')'+80H
	.word	DFIND-8
PABOR:	.word	DOCOL
	.word	ABORT
	.word	SEMIS
;
	.byte	85H	; ERROR
	.ascii	"ERRO"
	.byte	'R'+80H
	.word	PABOR-0AH
ERROR:	.word	DOCOL
	.word	WARN
	.word	AT
	.word	ZLESS
	.word	ZBRAN	; IF
	.word	ERRO1-.
	.word	PABOR	; ENDIF
ERRO1:	.word	HERE
	.word	COUNT
	.word	TYPE
	.word	PDOTQ
	.byte	2
	.ascii	"? "
	.word	MESS
	.word	SPSTO
;	CHANGE FROM FIG MODEL
;	.word	INN,AT,BLK,AT
	.word	BLK,AT
	.word	DDUP
	.word	ZBRAN,ERRO2-.	; IF
	.word	INN,AT
	.word	SWAP		; ENDIF
ERRO2:	.word	QUIT
;
	.byte	83H	; ID.
	.ascii	"ID"
	.byte	'.'+80H
	.word	ERROR-8
IDDOT:	.word	DOCOL
	.word	PAD
	.word	LIT
	.word	20H
	.word	LIT
	.word	5FH
	.word	FILL
	.word	DUP
	.word	PFA
	.word	LFA
	.word	OVER
	.word	SUBB
	.word	PAD
	.word	SWAP
	.word	CMOVE
	.word	PAD
	.word	COUNT
	.word	LIT
	.word	1FH
	.word	ANDD
	.word	TYPE
	.word	SPACE
	.word	SEMIS
;
	.byte	86H	; CREATE
	.ascii	"CREAT"
	.byte	'E'+80H
	.word	IDDOT-6
CREAT:	.word	DOCOL
	.word	DFIND
	.word	ZBRAN	; IF
	.word	CREA1-.
	.word	DROP
	.word	NFA
	.word	IDDOT
	.word	LIT
	.word	4
	.word	MESS
	.word	SPACE	; ENDIF
CREA1:	.word	HERE
	.word	DUP
	.word	CAT
	.word	WIDTH
	.word	AT
	.word	MIN
	.word	ONEP
	.word	ALLOT
	.word	DUP
	.word	LIT
	.word	0A0H
	.word	TOGGL
	.word	HERE
	.word	ONE
	.word	SUBB
	.word	LIT
	.word	80H
	.word	TOGGL
	.word	LATES
	.word	COMMA
	.word	CURR
	.word	AT
	.word	STORE
	.word	HERE
	.word	TWOP
	.word	COMMA
	.word	SEMIS
;
	.byte	0C9H	; [COMPILE]
	.ascii	"[COMPILE"
	.byte	']'+80H
	.word	CREAT-9
BCOMP:	.word	DOCOL
	.word	DFIND
	.word	ZEQU
	.word	ZERO
	.word	QERR
	.word	DROP
	.word	CFA
	.word	COMMA
	.word	SEMIS
;
	.byte	0C7H	; LITERAL
	.ascii	"LITERA"
	.byte	'L'+80H
	.word	BCOMP-0CH
LITER:	.word	DOCOL
	.word	STATE
	.word	AT
	.word	ZBRAN	; IF
	.word	LITE1-.
	.word	COMP
	.word	LIT
	.word	COMMA	; ENDIF
LITE1:	.word	SEMIS
;
	.byte	0C8H	; DLITERAL
	.ascii	"DLITERA"
	.byte	'L'+80H
	.word	LITER-0AH
DLITE:	.word	DOCOL
	.word	STATE
	.word	AT
	.word	ZBRAN	; IF
	.word	DLIT1-.
	.word	SWAP
	.word	LITER
	.word	LITER	; ENDIF
DLIT1:	.word	SEMIS
;
	.byte	86H	; ?STACK
	.ascii	"?STAC"
	.byte	'K'+80H
	.word	DLITE-0BH
QSTAC:	.word	DOCOL
	.word	SPAT
	.word	SZERO
	.word	AT
	.word	SWAP
	.word	ULESS
	.word	ONE
	.word	QERR
	.word	SPAT
	.word	HERE
	.word	LIT
	.word	80H
	.word	PLUS
	.word	ULESS
	.word	LIT
	.word	7
	.word	QERR
	.word	SEMIS
;
	.byte	89H	; INTERPRET
	.ascii	"INTERPRE"
	.byte	'T'+80H
	.word	QSTAC-9
INTER:	.word	DOCOL
INTE1:	.word	DFIND	; BEGIN
	.word	ZBRAN	; IF
	.word	INTE2-.
	.word	STATE
	.word	AT
	.word	LESS
	.word	ZBRAN	; IF
	.word	INTE3-.
	.word	CFA
	.word	COMMA
	.word	BRAN	; ELSE
	.word	INTE4-.
INTE3:	.word	CFA
	.word	EXEC	; ENDIF
INTE4:	.word	QSTAC
	.word	BRAN	; ELSE
	.word	INTE5-.
INTE2:	.word	HERE
	.word	NUMB
	.word	DPL
	.word	AT
	.word	ONEP
	.word	ZBRAN	; IF
	.word	INTE6-.
	.word	DLITE
	.word	BRAN	; ELSE
	.word	INTE7-.
INTE6:	.word	DROP
	.word	LITER	; ENDIF
INTE7:	.word	QSTAC	; ENDIF
INTE5:	.word	BRAN	; AGAIN
	.word	INTE1-.
;
	.byte	89H	; IMMEDIATE
	.ascii	"IMMEDIAT"
	.byte	'E'+80H
	.word	INTER-0CH
IMMED:	.word	DOCOL
	.word	LATES
	.word	LIT
	.word	40H
	.word	TOGGL
	.word	SEMIS
;
	.byte	8AH	; VOCABULARY
	.ascii	"VOCABULAR"
	.byte	'Y'+80H
	.word	IMMED-0CH
VOCAB:	.word	DOCOL
	.word	BUILD
	.word	LIT
	.word	0A081H
	.word	COMMA
	.word	CURR
	.word	AT
	.word	CFA
	.word	COMMA
	.word	HERE
	.word	VOCL
	.word	AT
	.word	COMMA
	.word	VOCL
	.word	STORE
	.word	DOES
DOVOC:	.word	TWOP
	.word	CONT
	.word	STORE
	.word	SEMIS
;
	.byte	0C5H	; FORTH
	.ascii	"FORT"
	.byte	'H'+80H
	.word	VOCAB-0DH
FORTH:	.word	DODOE
	.word	DOVOC
	.word	0A081H
	.word	TASK-7	; COLD START VALUE ONLY
;			  CHANGED EACH TIME A DEF IS APPENDED
;			  TO THE FORTH VOCABULARY
	.word	0	; END OF VOCABULARY LIST
;
	.byte	8BH	; DEFINITIONS
	.ascii	"DEFINITION"
	.byte	'S'+80H
	.word	FORTH-8
DEFIN:	.word	DOCOL
	.word	CONT
	.word	AT
	.word	CURR
	.word	STORE
	.word	SEMIS
;
	.byte	0C1H	; (
	.byte	'('+80H
	.word	DEFIN-0EH
PAREN:	.word	DOCOL
	.word	LIT
	.word	29H
	.word	WORD
	.word	SEMIS
;
	.byte	84H	; QUIT
	.ascii	"QUI"
	.byte	'T'+80H
	.word	PAREN-4
QUIT:	.word	DOCOL
	.word	ZERO
	.word	BLK
	.word	STORE
	.word	LBRAC
QUIT1:	.word	RPSTO	; BEGIN
	.word	CR
	.word	QUERY
	.word	INTER
	.word	STATE
	.word	AT
	.word	ZEQU
	.word	ZBRAN	; IF
	.word	QUIT2-.
	.word	PDOTQ
	.byte	2
	.ascii	"OK"	; ENDIF
QUIT2:	.word	BRAN	; AGAIN
	.word	QUIT1-.
;
	.byte	85H	; ABORT
	.ascii	"ABOR"
	.byte	'T'+80H
	.word	QUIT-7
ABORT:	.word	DOCOL
	.word	SPSTO
	.word	DEC
	.word	QSTAC
	.word	CR
	.word	DOTCPU
	.word	PDOTQ
	.byte	0DH
	.ascii	"fig-FORTH "
	.byte	FIGREL+30H,ADOT,FIGREV+30H
	.word	FORTH
	.word	DEFIN
	.word	QUIT
;
WRM:	ld	bc,WRM1
	jp	NEXT
WRM1:	.word	WARM
;
	.byte	84H	; WARM
	.ascii	"WAR"
	.byte	'M'+80H
	.word	ABORT-8
WARM:	.word	DOCOL
	.word	MTBUF
	.word	ABORT
;
CLD:	ld	bc,CLD1
	ld	hl,(ORIG+12H)
	ld	sp,hl
	jp	NEXT
CLD1:	.word	COLD
;
	.byte	84H	; COLD
	.ascii	"COL"
	.byte	'D'+80H
	.word	WARM-7
COLD:	.word	DOCOL
	.word	MTBUF
	.word	ZERO,DENSTY
	.word	STORE
	.word	LIT,BUF1
	.word	USE,STORE
	.word	LIT,BUF1
	.word	PREV,STORE
	.word	DRZER
	.word	LIT,0
	.word	LIT,EPRINT
	.word	STORE
;
	.word	LIT
	.word	ORIG+12H
	.word	LIT
	.word	UP
	.word	AT
	.word	LIT
	.word	6
	.word	PLUS
	.word	LIT
	.word	10H
	.word	CMOVE
	.word	LIT
	.word	ORIG+0CH
	.word	AT
	.word	LIT
	.word	FORTH+6
	.word	STORE
	.word	ABORT
;
	.byte	84H	; S->D
	.ascii	"S->"
	.byte	'D'+80H
	.word	COLD-7
STOD:	.word	.+2
	pop	de
	ld	hl,0
	ld	a,d
	and	80H
	jp	z,STOD1
	dec	hl
STOD1:	jp	DPUSH
;
	.byte	82H	; +-
	.byte	'+'
	.byte	'-'+80H
	.word	STOD-7
PM:	.word	DOCOL
	.word	ZLESS
	.word	ZBRAN	; IF
	.word	PM1-.
	.word	MINUS	; ENDIF
PM1:	.word	SEMIS
;
	.byte	83H	; D+-
	.ascii	"D+"
	.byte	'-'+80H
	.word	PM-5
DPM:	.word	DOCOL
	.word	ZLESS
	.word	ZBRAN	; IF
	.word	DPM1-.
	.word	DMINU	; ENDIF
DPM1:	.word	SEMIS
;
	.byte	83H	; ABS
	.ascii	"AB"
	.byte	'S'+80H
	.word	DPM-6
ABS:	.word	DOCOL
	.word	DUP
	.word	PM
	.word	SEMIS
;
	.byte	84H	; DABS
	.ascii	"DAB"
	.byte	'S'+80H
	.word	ABS-6
DABS:	.word	DOCOL
	.word	DUP
	.word	DPM
	.word	SEMIS
;
	.byte	83H	; MIN
	.ascii	"MI"
	.byte	'N'+80H
	.word	DABS-7
MIN:	.word	DOCOL,TDUP
	.word	GREAT
	.word	ZBRAN	; IF
	.word	MIN1-.
	.word	SWAP	; ENDIF
MIN1:	.word	DROP
	.word	SEMIS
;
	.byte	83H	; MAX
	.ascii	"MA"
	.byte	'X'+80H
	.word	MIN-6
MAX:	.word	DOCOL,TDUP
	.word	LESS
	.word	ZBRAN	; IF
	.word	MAX1-.
	.word	SWAP	; ENDIF
MAX1:	.word	DROP
	.word	SEMIS
;
	.byte	82H	; M*
	.byte	'M'
	.byte	'*'+80H
	.word	MAX-6
MSTAR:	.word	DOCOL,TDUP
	.word	XORR
	.word	TOR
	.word	ABS
	.word	SWAP
	.word	ABS
	.word	USTAR
	.word	FROMR
	.word	DPM
	.word	SEMIS
;
	.byte	82H	; M/
	.byte	'M'
	.byte	'/'+80H
	.word	MSTAR-5
MSLAS:	.word	DOCOL
	.word	OVER
	.word	TOR
	.word	TOR
	.word	DABS
	.word	RR
	.word	ABS
	.word	USLAS
	.word	FROMR
	.word	RR
	.word	XORR
	.word	PM
	.word	SWAP
	.word	FROMR
	.word	PM
	.word	SWAP
	.word	SEMIS
;
	.byte	81H	; *
	.byte	'*'+80H
	.word	MSLAS-5
STAR:	.word	DOCOL
	.word	MSTAR
	.word	DROP
	.word	SEMIS
;
	.byte	84H	; /MOD
	.ascii	"/MO"
	.byte	'D'+80H
	.word	STAR-4
SLMOD:	.word	DOCOL
	.word	TOR
	.word	STOD
	.word	FROMR
	.word	MSLAS
	.word	SEMIS
;
	.byte	81H	; /
	.byte	'/'+80H
	.word	SLMOD-7
SLASH:	.word	DOCOL
	.word	SLMOD
	.word	SWAP
	.word	DROP
	.word	SEMIS
;
	.byte	83H	; MOD
	.ascii	"MO"
	.byte	'D'+80H
	.word	SLASH-4
MODD:	.word	DOCOL
	.word	SLMOD
	.word	DROP
	.word	SEMIS
;
	.byte	85H	; */MOD
	.ascii	"*/MO"
	.byte	'D'+80H
	.word	MODD-6
SSMOD:	.word	DOCOL
	.word	TOR
	.word	MSTAR
	.word	FROMR
	.word	MSLAS
	.word	SEMIS
;
	.byte	82H	; */
	.byte	'*'
	.byte	'/'+80H
	.word	SSMOD-8
SSLA:	.word	DOCOL
	.word	SSMOD
	.word	SWAP
	.word	DROP
	.word	SEMIS
;
	.byte	85H	; M/MOD
	.ascii	"M/MO"
	.byte	'D'+80H
	.word	SSLA-5
MSMOD:	.word	DOCOL
	.word	TOR
	.word	ZERO
	.word	RR
	.word	USLAS
	.word	FROMR
	.word	SWAP
	.word	TOR
	.word	USLAS
	.word	FROMR
	.word	SEMIS
;
;	BLOCK MOVED DOWN 2 PAGES
;
;
	.byte	86H	; (LINE)
	.ascii	"(LINE"
	.byte	')'+80H
	.word	MSMOD-8
PLINE:	.word	DOCOL
	.word	TOR
	.word	LIT
	.word	40H
	.word	BBUF
	.word	SSMOD
	.word	FROMR
	.word	BSCR
	.word	STAR
	.word	PLUS
	.word	BLOCK
	.word	PLUS
	.word	LIT
	.word	40H
	.word	SEMIS
;
	.byte	85H	; .LINE
	.ascii	".LIN"
	.byte	'E'+80H
	.word	PLINE-9
DLINE:	.word	DOCOL
	.word	PLINE
	.word	DTRAI
	.word	TYPE
	.word	SEMIS
;
	.byte	87H	; MESSAGE
	.ascii	"MESSAG"
	.byte	'E'+80H
	.word	DLINE-8
MESS:	.word	DOCOL
	.word	WARN
	.word	AT
	.word	ZBRAN	; IF
	.word	MESS1-.
	.word	DDUP
	.word	ZBRAN	; IF
	.word	MESS2-.
	.word	LIT
	.word	4
	.word	OFSET
	.word	AT
	.word	BSCR
	.word	SLASH
	.word	SUBB
	.word	DLINE
	.word	SPACE	; ENDIF
MESS2:	.word	BRAN	; ELSE
	.word	MESS3-.
MESS1:	.word	PDOTQ
	.byte	6
	.ascii	"MSG # "
	.word	DOT	; ENDIF
MESS3:	.word	SEMIS
	.eject
;------------------------------------------
;
;	8080 PORT FETCH AND STORE
;	( SELF MODIFYING CODE, NOT REENTRANT )
;
	.byte	82H	; P@ "PORT @"
	.byte	'P'
	.byte	'@'+80H
	.word	MESS-0AH
PTAT:	.word	.+2
	pop	de	;E <- PORT#
	ld	hl,.+5
	ld	(hl),e
	in	a,(0)	;( PORT# MODIFIED )
	ld	l,a	;L <- (PORT#)
	ld	h,0
	jp	HPUSH
;
	.byte	82H	; "PORT STORE"
	.byte	'P'
	.byte	'!'+80H
	.word	PTAT-5
PTSTO:	.word	.+2
	pop	de	;E <- PORT#
	ld	hl,.+7
	ld	(hl),e
	pop	hl	;H <- CDATA
	ld	a,l
	out	(0),a	;( PORT# MODIFIED )
	jp	NEXT
	.eject
;--------------------------------------------------
;	CP/M DISK INTERFACE
;
;	CP/M BIOS CALLS USED
;	( NOTE EQU'S ARE 3 LOWER THAN DOCUMENTED OFFSETS
;	  BECAUSE BASE ADDR IS BIOS+3 )
;
	.equiv	RITSEC, 39
	.equiv	RDSEC, 36
	.equiv	SETDMA, 33
	.equiv	SETSEC, 30
	.equiv	SETTRK, 27
	.equiv	SETDSK, 24
;
;	DOUBLE DENSITY 8" FLOPPY CAPACITIES
	.equiv	SPT2, 52	; SECTORS PER TRACK
	.equiv	TRKS2, 77	; NUMBER OF TRACKS
	.equiv	SPDRV2, SPT2*TRKS2	; SECTORS/DRIVE
;	SINGLE DENSITY 8" FLOPPY CAPACITIES
	.equiv	SPT1, 26	; SECTORS/TRACK
	.equiv	TRKS1, 77	; # TRACKS
	.equiv	SPDRV1, SPT1*TRKS1	; SECTORS/DRIVE
;
	.equiv	BPS, 128	; BYTES PER SECTOR
	.equiv	MXDRV, 2	; MAX # DRIVES
;
;	FORTH VARIABLES AND CONSTANTS USED IN DISK INTERFACE
;
	.byte	85H	; DRIVE ( CURRENT DRIVE # )
	.ascii	"DRIV"
	.byte	'E'+80H
	.word	PTSTO-5
DRIVE:	.word	DOVAR,0
;
	.byte	83H	; SEC	( SECTOR # )
	.ascii	"SE"
	.byte	'C'+80H
	.word	DRIVE-8
SEC:	.word	DOVAR
	.word	0
;
	.byte	85H	; TRACK	( TRACK # )
	.ascii	"TRAC"
	.byte	'K'+80H
	.word	SEC-6
TRACK:	.word	DOVAR,0
;
	.byte	83H	; USE	( ADDR OF NEXT BUFFER TO USE )
	.ascii	"US"
	.byte	'E'+80H
	.word	TRACK-8
USE:	.word	DOVAR
	.word	BUF1
;
	.byte	84H	; PREV
;			( ADDR OF PREVIOUSLY USED BUFFER )
	.ascii	"PRE"
	.byte	'V'+80H
	.word	USE-6
PREV:	.word	DOVAR
	.word	BUF1
;
	.byte	87H	; SEC/BLK ( # SECTORS/BLOCK )
	.ascii	"SEC/BL"
	.byte	'K'+80H
	.word	PREV-7
SPBLK:	.word	DOCON
	.word	KBBUF/BPS
;
	.byte	85H	; #BUFF  ( NUMBER OF BUFFERS )
	.ascii	"#BUF"
	.byte	'F'+80H
	.word	SPBLK-10
NOBUF:	.word	DOCON,NBUF
;
	.byte	87H	; DENSITY ( 0 = SINGLE , 1 = DOUBLE )
	.ascii	"DENSIT"
	.byte	'Y'+80H
	.word	NOBUF-8
DENSTY:	.word	DOVAR
	.word	0
;
	.byte	8AH	; DISK-ERROR  ( DISK ERROR STATUS )
	.ascii	"DISK-ERRO"
	.byte	'R'+80H
	.word	DENSTY-10
DSKERR:	.word	DOVAR,0
;
;	DISK INTERFACE HIGH-LEVEL ROUTINES
;
	.byte	84H	; +BUF	( ADVANCE BUFFER )
	.ascii	"+BU"
	.byte	'F'+80H
	.word	DSKERR-13
PBUF:	.word	DOCOL
	.word	LIT,CO
	.word	PLUS,DUP
	.word	LIMIT,EQUAL
	.word	ZBRAN,PBUF1-.
	.word	DROP,FIRST
PBUF1:	.word	DUP,PREV
	.word	AT,SUBB
	.word	SEMIS
;
	.byte	86H	; UPDATE
	.ascii	"UPDAT"
	.byte	'E'+80H
	.word	PBUF-7
UPDAT:	.word	DOCOL,PREV
	.word	AT,AT
	.word	LIT,8000H
	.word	ORR
	.word	PREV,AT
	.word	STORE,SEMIS
;
	.byte	8DH	; EMPTY-BUFFERS
	.ascii	"EMPTY-BUFFER"
	.byte	'S'+80H
	.word	UPDAT-9
MTBUF:	.word	DOCOL,FIRST
	.word	LIMIT,OVER
	.word	SUBB,ERASEE
	.word	SEMIS
;
	.byte	83H	; DR0
	.ascii	"DR"
	.byte	'0'+80H
	.word	MTBUF-16
DRZER:	.word	DOCOL,ZERO
	.word	OFSET,STORE
	.word	SEMIS
;
	.byte	83H	; DR1
	.ascii	"DR"
	.byte	'1'+80H
	.word	DRZER-6
DRONE:	.word	DOCOL
	.word	DENSTY,AT
	.word	ZBRAN,DRON1-.
	.word	LIT,SPDRV2
	.word	BRAN,DRON2-.
DRON1:	.word	LIT,SPDRV1
DRON2:	.word	OFSET,STORE
	.word	SEMIS
;
	.byte	86H	; BUFFER
	.ascii	"BUFFE"
	.byte	'R'+80H
	.word	DRONE-6
BUFFE:	.word	DOCOL,USE
	.word	AT,DUP
	.word	TOR
BUFF1:	.word	PBUF		; WON'T WORK IF SINGLE BUFFER
	.word	ZBRAN,BUFF1-.
	.word	USE,STORE
	.word	RR,AT
	.word	ZLESS
	.word	ZBRAN,BUFF2-.
	.word	RR,TWOP
	.word	RR,AT
	.word	LIT,7FFFH
	.word	ANDD,ZERO
	.word	RSLW
BUFF2:	.word	RR,STORE
	.word	RR,PREV
	.word	STORE,FROMR
	.word	TWOP,SEMIS
;
	.byte	85H	; BLOCK
	.ascii	"BLOC"
	.byte	'K'+80H
	.word	BUFFE-9
BLOCK:	.word	DOCOL,OFSET
	.word	AT,PLUS
	.word	TOR,PREV
	.word	AT,DUP
	.word	AT,RR
	.word	SUBB
	.word	DUP,PLUS
	.word	ZBRAN,BLOC1-.
BLOC2:	.word	PBUF,ZEQU
	.word	ZBRAN,BLOC3-.
	.word	DROP,RR
	.word	BUFFE,DUP
	.word	RR,ONE
	.word	RSLW
	.word	TWO,SUBB
BLOC3:	.word	DUP,AT
	.word	RR,SUBB
	.word	DUP,PLUS
	.word	ZEQU
	.word	ZBRAN,BLOC2-.
	.word	DUP,PREV
	.word	STORE
BLOC1:	.word	FROMR,DROP
	.word	TWOP,SEMIS
;
;
;	CP/M INTERFACE ROUTINES
;
;		SERVICE REQUEST
;
IOS:	ld	hl,(1)	; (HL) <- BIOS TABLE ADDR+3
	add	hl,de	; + SERVICE REQUEST OFFSET
	jp	(hl)		; EXECUTE REQUEST
;	ret FUNCTION PROVIDED BY CP/M
;
	.byte	86H	; SET-IO
;			( ASSIGN SECTOR, TRACK FOR BDOS )
	.ascii	"SET-I"
	.byte	'O'+80H
	.word	BLOCK-8
SETIO:	.word	.+2
	push	bc	; SAVE (IP)
	ld	hl,(USE+2)	; (BC) <- ADDR BUFFER
	ld	b,h
	ld	c,l
	ld	de,SETDMA ; SEND BUFFER ADDR TO CP/M
	call	IOS
;
	ld	hl,(SEC+2)	; (BC) <- (SEC) = SECTOR #
	ld	c,l
	ld	de,SETSEC	; SEND SECTOR # TO CP/M
	call	IOS
;
	ld	hl,(TRACK+2)	; (BC) <- (TRACK) = TRACK #
	ld	b,h
	ld	c,l
	ld	de,SETTRK
	call	IOS
;
	pop	bc	; RESTORE (IP)
	jp	NEXT
;
	.byte	89H	; SET-DRIVE
	.ascii	"SET-DRIV"
	.byte	'E'+80H
	.word	SETIO-9
SETDRV:	.word	.+2
	push	bc	; SAVE (IP)
	ld	a,(DRIVE+2)	; (C) <- (DRIVE) = DRIVE #
	ld	c,a
	ld	de,SETDSK	; SEND DRIVE # TO CP/M
	call	IOS
	pop	bc	; RESTORE (IP)
	jp	NEXT
;
;	T&SCALC		( CALCULATES DRIVE#, TRACK#, & SECTOR# )
;	STACK INPUT: SECTOR-DISPLACEMENT = BLK# * SEC/BLK
;	OUTPUT: VARIABLES DRIVE, TRACK, & SEC
;
	.byte	87H	; T&SCALC
	.ascii	"T&SCAL"
	.byte	'C'+80H
	.word	SETDRV-12
TSCALC:	.word	DOCOL,DENSTY
	.word	AT
	.word	ZBRAN,TSCALS-.
	.word	LIT,SPDRV2
	.word	SLMOD
	.word	LIT,MXDRV
	.word	MIN
	.word	DUP,DRIVE
	.word	AT,EQUAL
	.word	ZBRAN,TSCAL1-.
	.word	DROP
	.word	BRAN,TSCAL2-.
TSCAL1:	.word	DRIVE,STORE
	.word	SETDRV
TSCAL2:	.word	LIT,SPT2
	.word	SLMOD,TRACK
	.word	STORE,ONEP
	.word	SEC,STORE
	.word	SEMIS
;	SINGLE DENSITY
TSCALS:	.word	LIT,SPDRV1
	.word	SLMOD
	.word	LIT,MXDRV

	.word	MIN
	.word	DUP,DRIVE
	.word	AT,EQUAL
	.word	ZBRAN,TSCAL3-.
	.word	DROP
	.word	BRAN,TSCAL4-.
TSCAL3:	.word	DRIVE,STORE
	.word	SETDRV
TSCAL4:	.word	LIT,SPT1
	.word	SLMOD,TRACK
	.word	STORE,ONEP
	.word	SEC,STORE
	.word	SEMIS
;
;	SEC-READ
;	( READ A SECTOR SETUP BY 'SET-DRIVE' & 'SETIO' )
;
	.byte	88H	; SEC-READ
	.ascii	"SEC-REA"
	.byte	'D'+80H
	.word	TSCALC-10
SECRD:	.word	.+2
	push	bc	; SAVE (IP)
	ld	de,RDSEC	; ASK CP/M TO READ SECTOR
	call	IOS
	ld	(DSKERR+2),a	; (DSKERR) <- ERROR STATUS
	pop	bc	; RESTORE (IP)
	jp	NEXT
;
;	SEC-WRITE
;	( WRITE A SECTOR SETUP BY 'SET-DRIVE' & 'SETIO' )
;
	.byte	89H	; SEC-WRITE
	.ascii	"SEC-WRIT"
	.byte	'E'+80H
	.word	SECRD-11
SECWT:	.word	.+2
	push	bc	; SAVE (IP)
	ld	de,RITSEC	; ASK CP/M TO WRITE SECTOR
	call	IOS
	ld	(DSKERR+2),a	; (DSKERR) <- ERROR STATUS
	pop	bc	; RESTORE (IP)
	jp	NEXT
;
	.byte	83H	; R/W	( FORTH DISK PRIMATIVE )
	.ascii	"R/"
	.byte	'W'+80H
	.word	SECWT-12
RSLW:	.word	DOCOL
	.word	USE,AT
	.word	TOR
	.word	SWAP,SPBLK
	.word	STAR,ROT
	.word	USE,STORE
	.word	SPBLK,ZERO
	.word	XDO
RSLW1:	.word	OVER,OVER
	.word	TSCALC,SETIO
	.word	ZBRAN,RSLW2-.
	.word	SECRD
	.word	BRAN,RSLW3-.
RSLW2:	.word	SECWT
RSLW3:	.word	ONEP
	.word	LIT,80H
	.word	USE,PSTOR
	.word	XLOOP,RSLW1-.
	.word	DROP,DROP
	.word	FROMR,USE
	.word	STORE,SEMIS
;
;--------------------------------------------------------
;
;	ALTERNATIVE R/W FOR NO DISK INTERFACE
;
;RSLW	.word	DOCOL,DROP,DROP,DROP,SEMIS
;
;--------------------------------------------------------
;
	.byte	85H	; FLUSH
	.ascii	"FLUS"
	.byte	'H'+80H
	.word	RSLW-6
FLUSH:	.word	DOCOL
	.word	NOBUF,ONEP
	.word	ZERO,XDO
FLUS1:	.word	ZERO,BUFFE
	.word	DROP
	.word	XLOOP,FLUS1-.
	.word	SEMIS
;
	.byte	84H	; LOAD
	.ascii	"LOA"
	.byte	'D'+80H
	.word	FLUSH-8
LOAD:	.word	DOCOL,BLK
	.word	AT,TOR
	.word	INN,AT
	.word	TOR,ZERO
	.word	INN,STORE
	.word	BSCR,STAR
	.word	BLK,STORE	; BLK <- SCR * B/SCR
	.word	INTER		; INTERPRET FROM OTHER SCREEN
	.word	FROMR,INN
	.word	STORE
	.word	FROMR,BLK
	.word	STORE
	.word	SEMIS
;
	.byte	0C3H	; -->
	.ascii	"--"
	.byte	'>'+80H
	.word	LOAD-7
ARROW:	.word	DOCOL
	.word	QLOAD
	.word	ZERO
	.word	INN
	.word	STORE
	.word	BSCR
	.word	BLK
	.word	AT
	.word	OVER
	.word	MODD
	.word	SUBB
	.word	BLK
	.word	PSTOR
	.word	SEMIS
;
	.eject
;-------------------------------------------------
;
;	CP/M CONSOLE & PRINTER INTERFACE
;
;	CP/M BIOS CALLS USED
;	( NOTE: BELOW OFFSETS ARE 3 LOWER THAN CP/M
;	  DOCUMENTATION SINCE BASE ADDR = BIOS+3 )
;
	.equiv	KCSTAT, 3	; CONSOLE STATUS
	.equiv	KCIN, 6	; CONSOLE INPUT
	.equiv	KCOUT, 9	; CONSOLE OUTPUT
	.equiv	KPOUT, 0CH	; PRINTER OUTPUT
;
EPRINT:	.word	0	; ENABLE PRINTER VARIABLE
;			; 0 = DISABLED, 1 = ENABLED
;
;	BELOW BIOS CALLS USE 'IOS' IN DISK INTERFACE
;
CSTAT:	push	bc	; CONSOLE STATUS
	ld	de,KCSTAT  ; CHECK IF ANY CHR HAS BEEN TYPED
	call	IOS
	pop	bc	; IF CHR TYPED THEN (A) <- 0FFH
	ret		; ELSE (A) <- 0
;			; CHR IGNORED
;
CIN:	push	bc	; CONSOLE INPUT
	ld	de,KCIN	; WAIT FOR CHR TO BE TYPED
	call	IOS	; (A) <- CHR, (MSB) <- 0
	pop	bc
	ret
;
COUT:	push	hl	; CONSOLE OUTPUT
	ld	de,KCOUT	; WAIT UNTIL READY
	call	IOS	; THEN OUTPUT (C)
	pop	hl
	ret
;
POUT:	ld	de,KPOUT	; PRINTER OUTPUT
	call	IOS	; WAIT UNTIL READY
	ret		; THEN OUTPUT (C)
;
CPOUT:	call	COUT	; OUTPUT (C) TO CONSOLE
	ex	de,hl
	ld	hl,EPRINT
	ld	a,(hl)	; IF (EPRINT) <> 0
	or	a
	jp	z,CPOU1
	ld	c,e	; THEN OUTPUT (C) TO PRINTER
	call	POUT
CPOU1:	ret
;
;	FORTH TO CP/M SERIAL IO INTERFACE
;
PQTER:	call	CSTAT	; IF CHR TYPED
	ld	hl,0
	or	a
	jp	z,PQTE1
	inc	l	; THEN (S1) <- TRUE
PQTE1:	jp	HPUSH	; ELSE (S1) <- FALSE
;
PKEY:	call	CIN	; READ CHR FROM CONSOLE
	cp	DLE	; IF CHR = (^P)
	ld	e,a
	jp	nz,PKEY1
	ld	hl,EPRINT  ; THEN TOGGLE (EPRINT)LSB
	ld	e,ABL	; CHR <- BLANK
	ld	a,(hl)
	xor	1
	ld	(hl),a
PKEY1:	ld	l,e
	ld	h,0
	jp	HPUSH	; (S1)LB <- CHR
;
PEMIT:	.word	.+2	; (EMIT)	ORPHAN
	pop	hl	; (L) <- (S1)LB = CHR
	push	bc	; SAVE (IP)
	ld	c,l
	call	CPOUT	; OUTPUT CHR TO CONSOLE
;			; & MAYBE PRINTER
	pop	bc	; RESTORE (IP)
	jp	NEXT
;
PCR:	push	bc	; SAVE (IP)
	ld	c,ACR	; OUTPUT (CR) TO CONSOLE
	ld	l,c
	call	CPOUT	; & MAYBE TO PRINTER
	ld	c,LF	; OUTPUT (LF) TO CONSOLE
	ld	l,c
	call	CPOUT	; & MAYBE TO PRINTER
	pop	bc	; RESTORE (IP)
	jp	NEXT
;
;----------------------------------------------------
	.eject
;
	.byte	0C1H	; '	( TICK )
	.byte	0A7H
	.word	ARROW-6
TICK:	.word	DOCOL
	.word	DFIND
	.word	ZEQU
	.word	ZERO
	.word	QERR
	.word	DROP
	.word	LITER
	.word	SEMIS
;
	.byte	86H	; FORGET
	.ascii	"FORGE"
	.byte	'T'+80H
	.word	TICK-4
FORG:	.word	DOCOL
	.word	CURR
	.word	AT
	.word	CONT
	.word	AT
	.word	SUBB
	.word	LIT
	.word	18H
	.word	QERR
	.word	TICK
	.word	DUP
	.word	FENCE
	.word	AT
	.word	LESS
	.word	LIT
	.word	15H
	.word	QERR
	.word	DUP
	.word	NFA
	.word	DP
	.word	STORE
	.word	LFA
	.word	AT
	.word	CONT
	.word	AT
	.word	STORE
	.word	SEMIS
;
	.byte	84H	; BACK
	.ascii	"BAC"
	.byte	'K'+80H
	.word	FORG-9
BACK:	.word	DOCOL
	.word	HERE
	.word	SUBB
	.word	COMMA
	.word	SEMIS
;
	.byte	0C5H	; BEGIN
	.ascii	"BEGI"
	.byte	'N'+80H
	.word	BACK-7
BEGIN:	.word	DOCOL
	.word	QCOMP
	.word	HERE
	.word	ONE
	.word	SEMIS
;
	.byte	0C5H	; ENDIF
	.ascii	"ENDI"
	.byte	'F'+80H
	.word	BEGIN-8
ENDIFF:	.word	DOCOL
	.word	QCOMP
	.word	TWO
	.word	QPAIR
	.word	HERE
	.word	OVER
	.word	SUBB
	.word	SWAP
	.word	STORE
	.word	SEMIS
;
	.byte	0C4H	; THEN
	.ascii	"THE"
	.byte	'N'+80H
	.word	ENDIFF-8
THEN:	.word	DOCOL
	.word	ENDIFF
	.word	SEMIS
;
	.byte	0C2H	; DO
	.byte	'D'
	.byte	'O'+80H
	.word	THEN-7
DO:	.word	DOCOL
	.word	COMP
	.word	XDO
	.word	HERE
	.word	THREE
	.word	SEMIS
;
	.byte	0C4H	; LOOP
	.ascii	"LOO"
	.byte	'P'+80H
	.word	DO-5
LOOP:	.word	DOCOL
	.word	THREE
	.word	QPAIR
	.word	COMP
	.word	XLOOP
	.word	BACK
	.word	SEMIS
;
	.byte	0C5H	; +LOOP
	.ascii	"+LOO"
	.byte	'P'+80H
	.word	LOOP-7
PLOOP:	.word	DOCOL
	.word	THREE
	.word	QPAIR
	.word	COMP
	.word	XPLOO
	.word	BACK
	.word	SEMIS
;
	.byte	0C5H	; UNTIL
	.ascii	"UNTI"
	.byte	'L'+80H
	.word	PLOOP-8
UNTIL:	.word	DOCOL
	.word	ONE
	.word	QPAIR
	.word	COMP
	.word	ZBRAN
	.word	BACK
	.word	SEMIS
;
	.byte	0C3H	; END
	.ascii	"EN"
	.byte	'D'+80H
	.word	UNTIL-8
ENDD:	.word	DOCOL
	.word	UNTIL
	.word	SEMIS
;
	.byte	0C5H	; AGAIN
	.ascii	"AGAI"
	.byte	'N'+80H
	.word	ENDD-6
AGAIN:	.word	DOCOL
	.word	ONE
	.word	QPAIR
	.word	COMP
	.word	BRAN
	.word	BACK
	.word	SEMIS
;
	.byte	0C6H	; REPEAT
	.ascii	"REPEA"
	.byte	'T'+80H
	.word	AGAIN-8
REPEA:	.word	DOCOL
	.word	TOR
	.word	TOR
	.word	AGAIN
	.word	FROMR
	.word	FROMR
	.word	TWO
	.word	SUBB
	.word	ENDIFF
	.word	SEMIS
;
	.byte	0C2H	; IF
	.byte	'I'
	.byte	'F'+80H
	.word	REPEA-9
IFF:	.word	DOCOL
	.word	COMP
	.word	ZBRAN
	.word	HERE
	.word	ZERO
	.word	COMMA
	.word	TWO
	.word	SEMIS
;
	.byte	0C4H	; ELSE
	.ascii	"ELS"
	.byte	'E'+80H
	.word	IFF-5
ELSEE:	.word	DOCOL
	.word	TWO
	.word	QPAIR
	.word	COMP
	.word	BRAN
	.word	HERE
	.word	ZERO
	.word	COMMA
	.word	SWAP
	.word	TWO
	.word	ENDIFF
	.word	TWO
	.word	SEMIS
;
	.byte	0C5H	; WHILE
	.ascii	"WHIL"
	.byte	'E'+80H
	.word	ELSEE-7
WHILE:	.word	DOCOL
	.word	IFF
	.word	TWOP
	.word	SEMIS
;
	.byte	86H	; SPACES
	.ascii	"SPACE"
	.byte	'S'+80H
	.word	WHILE-8
SPACS:	.word	DOCOL
	.word	ZERO
	.word	MAX
	.word	DDUP
	.word	ZBRAN	; IF
	.word	SPAX1-.
	.word	ZERO
	.word	XDO	; DO
SPAX2:	.word	SPACE
	.word	XLOOP	; LOOP	ENDIF
	.word	SPAX2-.
SPAX1:	.word	SEMIS
;
	.byte	82H	; <#
	.byte	'<'
	.byte	'#'+80H
	.word	SPACS-9
BDIGS:	.word	DOCOL
	.word	PAD
	.word	HLD
	.word	STORE
	.word	SEMIS
;
	.byte	82H	; #>
	.byte	'#'
	.byte	'>'+80H
	.word	BDIGS-5
EDIGS:	.word	DOCOL
	.word	DROP
	.word	DROP
	.word	HLD
	.word	AT
	.word	PAD
	.word	OVER
	.word	SUBB
	.word	SEMIS
;
	.byte	84H	; SIGN
	.ascii	"SIG"
	.byte	'N'+80H
	.word	EDIGS-5
SIGN:	.word	DOCOL
	.word	ROT
	.word	ZLESS
	.word	ZBRAN	; IF
	.word	SIGN1-.
	.word	LIT
	.word	2DH
	.word	HOLD	; ENDIF
SIGN1:	.word	SEMIS
;
	.byte	81H	; #
	.byte	'#'+80H
	.word	SIGN-7
DIG:	.word	DOCOL
	.word	BASE
	.word	AT
	.word	MSMOD
	.word	ROT
	.word	LIT
	.word	9
	.word	OVER
	.word	LESS
	.word	ZBRAN	; IF
	.word	DIG1-.
	.word	LIT
	.word	7
	.word	PLUS	; ENDIF
DIG1:	.word	LIT
	.word	30H
	.word	PLUS
	.word	HOLD
	.word	SEMIS
;
	.byte	82H	; #S
	.byte	'#'
	.byte	'S'+80H
	.word	DIG-4
DIGS:	.word	DOCOL
DIGS1:	.word	DIG	; BEGIN
	.word	OVER
	.word	OVER
	.word	ORR
	.word	ZEQU
	.word	ZBRAN	; UNTIL
	.word	DIGS1-.
	.word	SEMIS
;
	.byte	83H	; D.R
	.ascii	"D."
	.byte	'R'+80H
	.word	DIGS-5
DDOTR:	.word	DOCOL
	.word	TOR
	.word	SWAP
	.word	OVER
	.word	DABS
	.word	BDIGS
	.word	DIGS
	.word	SIGN
	.word	EDIGS
	.word	FROMR
	.word	OVER
	.word	SUBB
	.word	SPACS
	.word	TYPE
	.word	SEMIS
;
	.byte	82H	; .R
	.byte	'.'
	.byte	'R'+80H
	.word	DDOTR-6
DOTR:	.word	DOCOL
	.word	TOR
	.word	STOD
	.word	FROMR
	.word	DDOTR
	.word	SEMIS
;
	.byte	82H	; D.
	.byte	'D'
	.byte	'.'+80H
	.word	DOTR-5
DDOT:	.word	DOCOL
	.word	ZERO
	.word	DDOTR
	.word	SPACE
	.word	SEMIS
;
	.byte	81H	; .
	.byte	'.'+80H
	.word	DDOT-5
DOT:	.word	DOCOL
	.word	STOD
	.word	DDOT
	.word	SEMIS
;
	.byte	81H	; ?
	.byte	'?'+80H
	.word	DOT-4
QUES:	.word	DOCOL
	.word	AT
	.word	DOT
	.word	SEMIS
;
	.byte	82H	; U.
	.byte	'U'
	.byte	'.'+80H
	.word	QUES-4
UDOT:	.word	DOCOL
	.word	ZERO
	.word	DDOT
	.word	SEMIS
;
	.byte	85H	; VLIST
	.ascii	"VLIS"
	.byte	'T'+80H
	.word	UDOT-5
VLIST:	.word	DOCOL
	.word	LIT
	.word	80H
	.word	OUTT
	.word	STORE
	.word	CONT
	.word	AT
	.word	AT
VLIS1:	.word	OUTT	; BEGIN
	.word	AT
	.word	CSLL
	.word	GREAT
	.word	ZBRAN	; IF
	.word	VLIS2-.
	.word	CR
	.word	ZERO
	.word	OUTT
	.word	STORE	; ENDIF
VLIS2:	.word	DUP
	.word	IDDOT
	.word	SPACE
	.word	SPACE
	.word	PFA
	.word	LFA
	.word	AT
	.word	DUP
	.word	ZEQU
	.word	QTERM
	.word	ORR
	.word	ZBRAN	; UNTIL
	.word	VLIS1-.
	.word	DROP
	.word	SEMIS
;
;------ EXIT CP/M  -----------------------
;
	.byte	83H	; BYE
	.ascii	"BY"
	.byte	'E'+80H
	.word	VLIST-8
BYE:	.word	.+2
	jp	0
;-----------------------------------------------
;
	.byte	84H	; LIST
	.ascii	"LIS"
	.byte	'T'+80H
	.word	BYE-6
LIST:	.word	DOCOL,DEC
	.word	CR,DUP
	.word	SCR,STORE
	.word	PDOTQ
	.byte	6
	.ascii	"SCR # "
	.word	DOT
	.word	LIT,10H
	.word	ZERO,XDO
LIST1:	.word	CR,IDO
	.word	LIT,3
	.word	DOTR,SPACE
	.word	IDO,SCR
	.word	AT,DLINE
	.word	QTERM		; ?TERMINAL
	.word	ZBRAN,LIST2-.	; IF
	.word	LEAVE		; LEAVE
LIST2:	.word	XLOOP,LIST1-.	; ENDIF
	.word	CR,SEMIS
;
	.byte	85H	; INDEX
	.ascii	"INDE"
	.byte	'X'+80H
	.word	LIST-7
INDEX:	.word	DOCOL
	.word	LIT,FF
	.word	EMIT,CR
	.word	ONEP,SWAP
	.word	XDO
INDE1:	.word	CR,IDO
	.word	LIT,3
	.word	DOTR,SPACE
	.word	ZERO,IDO
	.word	DLINE,QTERM
	.word	ZBRAN,INDE2-.
	.word	LEAVE
INDE2:	.word	XLOOP,INDE1-.
	.word	SEMIS
;
	.byte	85H	; TRIAD
	.ascii	"TRIA"
	.byte	'D'+80H
	.word	INDEX-8
TRIAD:	.word	DOCOL
	.word	LIT,FF
	.word	EMIT
	.word	LIT,3
	.word	SLASH
	.word	LIT,3
	.word	STAR
	.word	LIT,3
	.word	OVER,PLUS
	.word	SWAP,XDO
TRIA1:	.word	CR,IDO
	.word	LIST
	.word	QTERM		; ?TERMINAL
	.word	ZBRAN,TRIA2-.	; IF
	.word	LEAVE		; LEAVE
TRIA2:	.word	XLOOP,TRIA1-.	; ENDIF
	.word	CR
	.word	LIT,15
	.word	MESS,CR
	.word	SEMIS
;
	.byte	84H	; .CPU
	.ascii	".CP"
	.byte	'U'+80H
	.word	TRIAD-8
DOTCPU:	.word	DOCOL
	.word	BASE,AT
	.word	LIT,36
	.word	BASE,STORE
	.word	LIT,22H
	.word	PORIG,TAT
	.word	DDOT
	.word	BASE,STORE
	.word	SEMIS
;
	.byte	84H	; TASK
	.ascii	"TAS"
	.byte	'K'+80H
	.word	DOTCPU-7
TASK:	.word	DOCOL
	.word	SEMIS
;
;; INITDP:	.skip	EM-.	;CONSUME MEMORY TO LIMIT
INITDP:
;
	.eject
;
;		MEMORY MAP
;	( THE FOLLOWING EQUATES ARE NOT REFERENCED ELSEWHERE )
;
;		LOCATION	CONTENTS
;		--------	--------
	.equiv	MCOLD, ORIG		;JMP TO COLD START
	.equiv	MWARM, ORIG+4		;JMP TO WARM START
	.equiv	MA2, ORIG+8		;COLD START PARAMETERS
	.equiv	MUP, UP		;USER VARIABLES' BASE 'REG'
	.equiv	MRP, RPP		;RETURN STACK 'REGISTER'
;
	.equiv	MBIP, BIP		;DEBUG SUPPORT
	.equiv	MDPUSH, DPUSH		;ADDRESS INTERPRETER
	.equiv	MHPUSH, HPUSH
	.equiv	MNEXT, NEXT
;
	.equiv	MDP0, DP0		;START FORTH DICTIONARY
	.equiv	MDIO, DRIVE		  ;CP/M DISK INTERFACE
	.equiv	MCIO, EPRINT		  ;CONSOLE & PRINTER INTERFACE
	.equiv	MIDP, INITDP		;END INITIAL FORTH DICTIONARY
;				  = COLD (DP) VALUE
;				  = COLD (FENCE) VALUE
;				  |  NEW
;				  |  DEFINITIONS
;				  V
;
;				  ^
;				  |  DATA
;				  |  STACK
	.equiv	MIS0, INITS0		;  = COLD (SP) VALUE = (S0)
;				   = (TIB)
;				  |  TERMINAL INPUT
;				  |  BUFFER
;				  V
;
;				  ^
;				  |  RETURN
;				  |  STACK
	.equiv	MIR0, INITR0		;START USER VARIABLES
;				  = COLD (RP) VALUE = (R0)
;				  = (UP)
;				;END USER VARIABLES
	.equiv	MFIRST, BUF1		;START DISK BUFFERS
;				  = FIRST
	.equiv	MEND, EM-1		;END DISK BUFFERS
	.equiv	MLIMIT, EM		;LAST MEMORY LOC USED + 1
;				  = LIMIT
;
;
	.end
