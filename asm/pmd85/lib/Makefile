PLATFORM = pmd85
OBJ = erase.o indcall.o sedit.o beep.o bepuk.o beclr.o waits.o inkey.o \
	setkmap.o inklav.o prtout.o scroll.o adras.o wrchar.o glyphs20.o \
	glyphs40.o glyphs60.o glyphsC0.o glyphsE0.o glyphs.o \
	fill8.o fill16.o copy8.o copy16.o mul16.o udiv8.o udiv16_8.o \
	rdel.o div16.o hlneg.o deneg.o bcneg.o ucmpbcde.o scmphlde.o \
	scmpdehl.o signexhl.o signexde.o shrhlb.o crc16.o crc24.o \
	undef_glyph.o
AS = as-z80
AR = ar-z80
ASFLAGS = -z80
ARFLAGS =

build: lib$(PLATFORM).a

lib$(PLATFORM).a: $(OBJ)
	$(AR) $(ARFLAGS) -r $@ $(OBJ)

%.o: %.s ../pmd85.inc Makefile
	$(AS) $(ASFLAGS) -c $< -o $@ -aglms=$*.lst

clean:
	rm -f *.o *.map *.lst

.SUFFIXES:

.PHONY: build clean
