; labels.s
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


; Labels.

	.include "reversi.inc"
	
; ==============================================================================
; Label "REVERSI 1.0"
;
	.data
	.globl	label_reversi
label_reversi:
	.byte	12, 15
	.byte	0x3f, 0x31, 0x3f, 0x0c, 0x0c, 0x3f, 0x33, 0x1f
	.byte	0x38, 0x07, 0x06, 0x00, 0x0c, 0x20, 0x07
	.byte	0x3f, 0x33, 0x3f, 0x0c, 0x0c, 0x3f, 0x33, 0x3f
	.byte	0x3c, 0x0f, 0x06, 0x00, 0x0e, 0x30, 0x0f
	.byte	0x03, 0x33, 0x00, 0x0c, 0x0c, 0x03, 0x30, 0x30
	.byte	0x0c, 0x0c, 0x06, 0x00, 0x0f, 0x38, 0x1c
	.byte	0x03, 0x33, 0x00, 0x0c, 0x0c, 0x03, 0x30, 0x30
	.byte	0x0c, 0x00, 0x06, 0x00, 0x0c, 0x18, 0x18
	.byte	0x03, 0x33, 0x00, 0x0c, 0x0c, 0x03, 0x30, 0x30
	.byte	0x0c, 0x00, 0x06, 0x00, 0x0c, 0x18, 0x18
	.byte	0x3f, 0x33, 0x1f, 0x0c, 0x0c, 0x3f, 0x31, 0x3f
	.byte	0x3c, 0x07, 0x06, 0x00, 0x0c, 0x18, 0x18
	.byte	0x3f, 0x31, 0x1f, 0x18, 0x06, 0x3f, 0x31, 0x1f
	.byte	0x38, 0x0f, 0x06, 0x00, 0x0c, 0x18, 0x18
	.byte	0x1f, 0x30, 0x00, 0x18, 0x06, 0x03, 0x30, 0x07
	.byte	0x00, 0x0c, 0x06, 0x00, 0x0c, 0x18, 0x18
	.byte	0x3b, 0x30, 0x00, 0x30, 0x03, 0x03, 0x30, 0x0e
	.byte	0x00, 0x0c, 0x06, 0x00, 0x0c, 0x18, 0x18
	.byte	0x33, 0x31, 0x00, 0x30, 0x03, 0x03, 0x30, 0x1c
	.byte	0x0c, 0x0c, 0x06, 0x00, 0x0c, 0x38, 0x1c
	.byte	0x23, 0x33, 0x3f, 0x20, 0x01, 0x3f, 0x33, 0x38
	.byte	0x3c, 0x0f, 0x06, 0x00, 0x0c, 0x33, 0x0f
	.byte	0x03, 0x33, 0x3f, 0x20, 0x01, 0x3f, 0x33, 0x30
	.byte	0x38, 0x07, 0x06, 0x00, 0x0c, 0x23, 0x07
	
	.end
