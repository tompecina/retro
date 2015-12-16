OUTPUT_FORMAT("binary")
. = 0x000;
__Ltext = .;
ENTRY (__Ltext)
OUTPUT_ARCH("z80")
initsp = 0x7000;
SECTIONS
{
.text :	{
	*(.text)
	*(text)
	 __Htext = .;
	}
.data :	{
	 __Ldata = .;
	*(.data)
	*(data)
	 __Hdata = .;
	}
.bss :	{
	 __Lbss = .;
	*(.bss)
	*(bss)
	 __Hbss = .;
	 heap = .;
	}
}
