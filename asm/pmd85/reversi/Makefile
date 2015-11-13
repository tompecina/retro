APPL = reversi
MCU = z80
OBJ = main.o game.o util.o glyphs.o labels.o board.o book.o minimax.o\
      ../adras10.o ../wrchar10.o ../beep.o ../waits.o ../inkey.o \
      ../erase.o ../prtout.o
AS = as-z80
LD = ld-z80
OBJCOPY = objcopy-z80
ASFLAGS = -z80 -I ..
LDFLAGS = --section-start=.text=0x0000

build: $(APPL).bin $(APPL).hex

%.hex: %.bin
	$(OBJCOPY) -I binary -O ihex $< $@

$(APPL).bin: $(OBJ)
	$(LD) $(LDFLAGS) $(OBJ) -o $@ -Map=$(APPL).map

%.o: %.s reversi.inc ../pmd85.inc ../glyphs10.inc Makefile
	$(AS) $(ASFLAGS) -c $< -o $@ -aglms=$*.lst

clean:
	rm -f *.o *.lst $(APPL).bin $(APPL).hex

.SUFFIXES:

.PHONY: build clean
