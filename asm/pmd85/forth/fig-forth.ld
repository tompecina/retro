OUTPUT_FORMAT("binary")
. = 0x0000;
mlim = 0x7000;
EM = 0x7000 - 0x0100;
__Lboot = .;
ENTRY (__Ltext)
OUTPUT_ARCH("z80")
SECTIONS
{
boot : {
	boot.o(.text)
	 __Hboot = .;
	}
.text : {
	*(EXCLUDE_FILE(fig-forth.o) .text)
	*(text)
	 __Htext = .;
	}
.data : {
	 __Ldata = .;
	*(.data)
	*(data)
	 __Hdata = .;
	}
forth : {
	 __Lforth = .;
	fig-forth.o(.text)
	 __Hforth = .;
	}
.bss EM : {
	 __Lbss = .;
	*(.bss)
	*(bss)
	 __Hbss = .;
	 }
}
