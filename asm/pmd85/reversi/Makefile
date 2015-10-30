APPL = reversi
MCU = z80
OBJ = main.o game.o util.o glyphs.o labels.o board.o
AS = as-z80
LD = ld-z80
OBJCOPY = objcopy-z80
ASFLAGS = -g -z80
LDFLAGS = -g -r --section-start=.text=0x0000

build: $(APPL).out $(APPL).hex

%.hex: %.out
	$(OBJCOPY) -O ihex $< $@

$(APPL).out: $(OBJ)
	$(LD) $(LDFLAGS) $(OBJ) -o $@

%.o: %.S reversi.inc ../glyphs.inc
	$(AS) $(ASFLAGS) -c $< -o $@ -a=$*.lst

%.S: %.s
	mv $< $@

clean:
	rm -f *.o *.lst $(APPL).out $(APPL).hex

.SUFFIXES:

.PHONY: build clean
