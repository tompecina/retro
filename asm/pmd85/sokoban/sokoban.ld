OUTPUT_FORMAT("binary")
. = 0x0000;
__Ltext = .;
ENTRY (__Ltext)
OUTPUT_ARCH("z80")
initsp = 0x7000;
SECTIONS
{
.text : {
	*(.text)
	*(text)
	 __Htext = .;
	}
.data : {
	 __Ldata = .;
	*(EXCLUDE_FILE(deflevels.o) .data)
	*(data)
	 __Hdata = .;
	}
.bss : {
	 __Lbss = .;
	*(.bss)
	*(bss)
	 __Hbss = .;
	 heap = .;
	 }
levels : {
        levels = .;
        . += def_levels_len;
        levels_end = .;
        }
deflevels __Lbss : {
	deflevels.o(.data)
	}
}
