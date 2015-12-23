; glyphs.s
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


; Video parameters and glyphs - full set.

; ==============================================================================
; init_video - initialize video parameters
;
;   uses:   H, L
;
	.text
	.globl	init_video
init_video:	
	xor	a
	ld	(color),a
	ld	hl,0x2c0
	ld	(radsir),hl
	ld	a,0xfb
	ld	(vyska),a
	ld	hl,0xfab0
	ld	(poroll),hl
	ld	a,9
	ld	(rsirrad),a
	ld	hl,0xfcc0
	ld	(curroll),hl
	ld	a,0x30
	ld	(enlnw),a
	ld	a,0xea
	ld	(iiroll),a
	ret

; ==============================================================================
; Video parameters
;
	.globl	cursor, color, radsir, vyska, poroll, rsirrad
	.globl	curroll, enlnw, iiroll
	.lcomm	cursor, 2
	.lcomm	color, 1
	.lcomm	radsir, 2
	.lcomm	vyska, 1
	.lcomm	poroll, 2
	.lcomm	rsirrad, 1
	.lcomm	curroll, 2
	.lcomm	enlnw, 1
	.lcomm	iiroll, 1
	
; ==============================================================================
; Glyphs map
;
	.data
	.globl	tascii
tascii:
	.word	0xff00
	.word	glyphs20 + 10
	.word	glyphs40 + 10
	.word	glyphs60 + 10
	.word	0xff00
	.word	0xff00
	.word	glyphsC0 + 10
	.word	glyphsE0 + 10

	.end
