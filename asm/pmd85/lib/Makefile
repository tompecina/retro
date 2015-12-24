PLATFORM = pmd85
OBJ = erase.o indcall.o sedit.o beep.o bepuk.o beclr.o waits.o inkey.o \
	setkmap.o inklav.o prtout.o scroll.o adras.o wrchar.o glyphs20.o \
	glyphs40.o glyphs60.o glyphsC0.o glyphsE0.o glyphs.o keymap.o \
	fill8.o fill16.o copy8.o copy16.o mul16.o udiv8.o udiv16_8.o \
	rdel.o div16.o hlneg.o deneg.o bcneg.o ucmpbcde.o scmphlde.o \
	scmpdehl.o signexhl.o signexde.o shrhlb.o crc16.o crc24.o \
	undef_glyph.o write.o writeln.o redon.o redoff.o add_cust_glyphs.o \
	clr_msg.o disp_msg.o get_conf.o get_ack.o errbeep.o start_ct1.o \
	rel_ct1.o start_ct2.o read_ct2.o lcg128.o fixseed128.o inklav_rnd128.o \
	conv_time.o init_bctbl.o count_bits.o init_brtbl.o reflect_byte.o \
	init_bcbrtbl.o draw_label.o dethw1.o conv_int.o start_usart.o \
	test_stop.o trheadin.o trscan.o trbyte.o trload.o trwaimgi.o val_digit.o \
	parse_int.o ucmphlde.o ucmpdehl.o mul8.o signexbc.o umul8.o get_any.o \
	jpvect.o trhead.o trprgout.o trleader.o usartout.o tapeout.o detallram.o \
	allramon.o allramoff.o rmmtrans.o
AS = as-z80
AR = ar-z80
ASFLAGS = -z80
ARFLAGS =

build: lib$(PLATFORM).a

lib$(PLATFORM).a: $(OBJ)
	$(AR) $(ARFLAGS) -r $@ $(OBJ)

%.o: %.s pmd85.inc Makefile
	$(AS) $(ASFLAGS) -c $< -o $@ -aglms=$*.lst

clean:
	rm -f *.o lib$(PLATFORM).a *.map *.lst

.SUFFIXES:

.PHONY: build clean
