OUTPUT_FORMAT("binary")
. = 0x0100;
__Ltext = .;
ENTRY (__Ltext)
OUTPUT_ARCH("z80")
SECTIONS
{
.text : {
	*(.text)
	*(text)
	 __Htext = .;
	}
.data : {
	 __Ldata = .;
	*(.data)
	*(data)
	 __Hdata = .;
	}
.bss : {
	 __Lbss = .;
	*(.bss)
	*(bss)
	 __Hbss = .;
	 }
}
