; pmd85.inc
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


; Tesla PMD 85 - include file.

; ==============================================================================
; PMD 85 ports
;
	.equiv	SYSPIO_PA, 0xf4
	.equiv	SYSPIO_PB, 0xf5
	.equiv	SYSPIO_PC, 0xf6
	.equiv	SYSPIO_CTRL, 0xf7
	
; ==============================================================================
; PMD 85 addresses
;
	.equiv	PMD_PRTOUT, 0x8500
	.equiv	PMD_ECHO, 0x8584
	.equiv	PMD_MONIT, 0x8c40

	.equiv	COLOR, 0xc03a
	.equiv	BEEPPOS, 0xc03b
	.equiv	CURSOR, 0xc03e
	.equiv	TASCII, 0xc0b0
	.equiv	BEEDT, 0xc130
	.equiv	ASCII, 0xc134
	.equiv	STACK, 0xc13e
	.equiv	OLDKEY, 0xc178

; ==============================================================================
; Key codes
;
	.equiv	KSCLR, 0x01
	.equiv	KEND, 0x03
	.equiv	KSCD, 0x06
	.equiv	KSWRK, 0x07
	.equiv	KLEFT, 0x08
	.equiv	KSRRIGHT, 0x09
	.equiv	KSLLEFT, 0x0a
	.equiv	KWRK, 0x0b
	.equiv	KHOME, 0x0c
	.equiv	KEOL, 0x0d
	.equiv	KSRCL, 0x0e
	.equiv	KPTL, 0x0f
	.equiv	KSEND, 0x10
	.equiv	KSRIGHT, 0x11
	.equiv	KSDEL, 0x12
	.equiv	KSLEFT, 0x13
	.equiv	KRCL, 0x17
	.equiv	KRIGHT, 0x18
	.equiv	KRRIGHT, 0x19
	.equiv	KLLEFT, 0x1a
	.equiv	KCLR, 0x1b
	.equiv	KINS, 0x1c
	.equiv	KDEL, 0x1d
	.equiv	KCD, 0x1e
	.equiv	KSHOME, 0x1f
