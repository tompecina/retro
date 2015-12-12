APPL = logik
OBJ = board.o glyphs.o util.o random.o labels.o game.o tree.o \
	group.o \
	../adras10.o ../wrchar10.o ../beep.o ../waits.o ../inkey.o \
	../inklav.o ../erase.o ../prtout.o ../sedit.o ../indcall.o \
	../debug.o \
	heap.o
AS = as-z80
LD = ld-z80
BTP = bin2ptp
OBJCOPY = objcopy-z80
ASFLAGS = -z80 -I ..
LDFLAGS = --section-start=.text=0x0000

build: $(APPL)-en.bin $(APPL)-en.hex $(APPL)-cs.bin $(APPL)-cs.hex \
	$(APPL)-sk.bin $(APPL)-sk.hex $(APPL).ptp

$(APPL).ptp: $(APPL)-en.bin $(APPL)-cs.bin $(APPL)-sk.bin
	$(BTP) -n 0 -m LOGIK-EN -o $@ $(APPL)-en.bin
	$(BTP) -append -n 1 -m LOGIK-CS -o $@ $(APPL)-cs.bin
	$(BTP) -append -n 2 -m LOGIK-SK -o $@ $(APPL)-sk.bin

%.hex: %.bin
	$(OBJCOPY) -I binary -O ihex $< $@

$(APPL)-en.bin: main-en.o $(OBJ)
	$(LD) $(LDFLAGS) main-en.o $(OBJ) -o $@ -Map=$(APPL)-en.map

$(APPL)-cs.bin: main-cs.o $(OBJ)
	$(LD) $(LDFLAGS) main-cs.o $(OBJ) -o $@ -Map=$(APPL)-cs.map

$(APPL)-sk.bin: main-sk.o $(OBJ)
	$(LD) $(LDFLAGS) main-sk.o $(OBJ) -o $@ -Map=$(APPL)-sk.map

main-en.o: main.s $(APPL).inc tree.inc ../pmd85.inc ../glyphs10.inc lang-en.inc Makefile
	$(AS) $(ASFLAGS) --defsym en=1 -c $< -o $@ -aglms=main-en.lst

main-cs.o: main.s $(APPL).inc tree.inc ../pmd85.inc ../glyphs10.inc lang-cs.inc Makefile
	$(AS) $(ASFLAGS) --defsym cs=1 -c $< -o $@ -aglms=main-cs.lst

main-sk.o: main.s $(APPL).inc tree.inc ../pmd85.inc ../glyphs10.inc lang-sk.inc Makefile
	$(AS) $(ASFLAGS) --defsym sk=1 -c $< -o $@ -aglms=main-sk.lst

%.o: %.s $(APPL).inc ../pmd85.inc ../glyphs10.inc Makefile
	$(AS) $(ASFLAGS) -c $< -o $@ -aglms=$*.lst

clean:
	rm -f *.o *.lst $(APPL)*.bin $(APPL)*.hex $(APPL)*.map \
	../*.o ../*.lst ../*.map

.SUFFIXES:

.PHONY: build clean
