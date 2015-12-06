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


; Character glyphs.

	.include "logik.inc"
	
; ==============================================================================
; Page selectors
;
	.equiv	SEL_20, 1
	.equiv	SEL_40, 1
	.equiv	SEL_60, 1
	.equiv	SEL_C0, 1
	.equiv	SEL_E0, 1

	.include "../glyphs10.inc"

	.end
