APPL = reversi
MCU = z80
OBJ = game.o util.o glyphs.o labels.o board.o book.o minimax.o \
	../adras10.o ../wrchar10.o ../beep.o ../waits.o ../inkey.o \
	../inklav.o ../erase.o ../prtout.o \
	heap.o
AS = as-z80
LD = ld-z80
OBJCOPY = objcopy-z80
ASFLAGS = -z80 -I ..
LDFLAGS = --section-start=.text=0x0000

build: $(APPL)-en.bin $(APPL)-en.hex $(APPL)-cs.bin $(APPL)-cs.hex \
	$(APPL)-sk.bin $(APPL)-sk.hex

%.hex: %.bin
	$(OBJCOPY) -I binary -O ihex $< $@

$(APPL)-en.bin: main-en.o $(OBJ)
	$(LD) $(LDFLAGS) main-en.o $(OBJ) -o $@ -Map=$(APPL)-en.map

$(APPL)-cs.bin: main-cs.o $(OBJ)
	$(LD) $(LDFLAGS) main-cs.o $(OBJ) -o $@ -Map=$(APPL)-cs.map

$(APPL)-sk.bin: main-sk.o $(OBJ)
	$(LD) $(LDFLAGS) main-sk.o $(OBJ) -o $@ -Map=$(APPL)-sk.map

main-en.o: main.s reversi.inc ../pmd85.inc lang-en.inc \
	../glyphs10.inc Makefile
	$(AS) $(ASFLAGS) --defsym en=1 -c $< -o $@ -aglms=main-en.lst

main-cs.o: main.s reversi.inc ../pmd85.inc lang-cs.inc \
	../glyphs10.inc Makefile
	$(AS) $(ASFLAGS) --defsym cs=1 -c $< -o $@ -aglms=main-cs.lst

main-sk.o: main.s reversi.inc ../pmd85.inc lang-sk.inc \
	../glyphs10.inc Makefile
	$(AS) $(ASFLAGS) --defsym sk=1 -c $< -o $@ -aglms=main-sk.lst

%.o: %.s reversi.inc ../pmd85.inc ../glyphs10.inc Makefile
	$(AS) $(ASFLAGS) -c $< -o $@ -aglms=$*.lst

clean:
	rm -f *.o *.lst $(APPL)*.bin $(APPL)*.hex $(APPL)*.map

.SUFFIXES:

.PHONY: build clean
