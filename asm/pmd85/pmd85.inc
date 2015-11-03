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
; PMD 85 addresses
;
	.equiv	SYSPIO_PA, 0xf4
	.equiv	SYSPIO_PB, 0xf5
	.equiv	SYSPIO_PC, 0xf6
	.equiv	SYSPIO_CTRL, 0xf7
	
	.equiv	PMD_PRTOUT, 0x8500
	.equiv	PMD_ECHO, 0x8584
	.equiv	PMD_MONIT, 0x8c40

	.equiv	COLOR, 0xc03a
	.equiv	CURSOR, 0xc03e
	.equiv	TASCII, 0xc0b0
	.equiv	ASCII, 0xc134
	.equiv	STACK, 0xc13e
	.equiv	OLDKEY, 0xc178
