; keymap.s
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


; Keymaps (keyboard layouts).

	.include "pmd85.inc"
	
; ==============================================================================
; keymap - keyboard map
; 
	.data
	.globl	keymap, kmp1, kmp2
keymap:
	.byte	0x3b
	.ascii "QWERTZUIOP@"
	.byte	'\'
	.byte	KLEFT, KHOME, KRIGHT
	.byte	0x37
	.ascii	"ASDFGHJKL;:"
kmp1:	.byte	'['
	.byte	KLLEFT, KEND, KRRIGHT
	.byte	0x2f
	.ascii	" YXCVBNM,./"
	.byte	0, 0, KEOL, KEOL
	.byte	0x3d
	.ascii	"1234567890_}"
	.byte	KINS, KDEL, KCLR
	.byte	0x1d, '!', 0x22
	.ascii	"#$%&'()-={"
	.byte	KPTL, KSDEL, KSCLR
	.byte	0x1b
	.ascii	"qwertzuiop`^"
	.byte	KSLEFT, KSHOME, KSRIGHT
	.byte	0x17
	.ascii	"asdfghjkl+*"
kmp2:	.byte	']'
	.byte	KSLLEFT, KSEND, KSRRIGHT
	.byte	0x0f
	.ascii	" yxcvbnm<>?"
	.byte	0, 0, KEOL, KEOL
	.byte	0x1e, 0x94, 0x95, 0x96, 0x97, 0x98, 0x99, 0x9a, 0x9b, 0x9c
	.byte	0x9d, 0x9e, 0x9f, KSWRK, KSCD, KSRCL
	.byte	0x3e
	.irp	i, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11
	.byte	KK\i
	.endr
	.byte	KWRK, KCD, KRCL
	.byte	0
	
	.end
