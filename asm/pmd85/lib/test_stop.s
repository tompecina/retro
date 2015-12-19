; test_stop.s
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

	.include "pmd85.inc"
	
; ==============================================================================
; test_stop - test STOP key
; 
;   output:  Z, A=0x03 if STOP pressed
;	    NZ, A=0x40 otherwise
; 
;   uses:   -
; 
	.text
	.globl	test_stop
test_stop:
	in	a,(SYSPIO_PB)
	and	0x40
	ret	nz
	ld	a,0x03
	ret

	.end
