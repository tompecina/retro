APPL = logik
OBJ = board.o random.o labels.o game.o tree.o
AS = as-z80
LD = ld-z80
BTP = bin2ptp
OBJCOPY = objcopy-z80
ASFLAGS = -z80 -I ../lib
LDFLAGS = -L ../lib -T pmd85.ld

build: $(APPL)-en.bin $(APPL)-en.hex $(APPL)-cs.bin $(APPL)-cs.hex \
	$(APPL)-sk.bin $(APPL)-sk.hex $(APPL).ptp

$(APPL).ptp: $(APPL)-en.bin $(APPL)-cs.bin $(APPL)-sk.bin
	$(BTP) -n 0 -m LOGIK-EN -o $@ $(APPL)-en.bin
	$(BTP) -append -n 1 -m LOGIK-CS -o $@ $(APPL)-cs.bin
	$(BTP) -append -n 2 -m LOGIK-SK -o $@ $(APPL)-sk.bin

%.hex: %.bin
	$(OBJCOPY) -I binary -O ihex $< $@

$(APPL)-en.bin: main-en.o $(OBJ) ../lib/libpmd85.a ../lib/pmd85.ld
	$(LD) $(LDFLAGS) main-en.o $(OBJ) -l pmd85 -o $@ -Map=$(APPL)-en.map

$(APPL)-cs.bin: main-cs.o $(OBJ) ../lib/libpmd85.a ../lib/pmd85.ld
	$(LD) $(LDFLAGS) main-cs.o $(OBJ) -l pmd85 -o $@ -Map=$(APPL)-cs.map

$(APPL)-sk.bin: main-sk.o $(OBJ) ../lib/libpmd85.a ../lib/pmd85.ld
	$(LD) $(LDFLAGS) main-sk.o $(OBJ) -l pmd85 -o $@ -Map=$(APPL)-sk.map

main-en.o: main.s $(APPL).inc tree.inc ../lib/pmd85.inc lang-en.inc Makefile
	$(AS) $(ASFLAGS) --defsym en=1 -c $< -o $@ -aglms=main-en.lst

main-cs.o: main.s $(APPL).inc tree.inc ../lib/pmd85.inc lang-cs.inc Makefile
	$(AS) $(ASFLAGS) --defsym cs=1 -c $< -o $@ -aglms=main-cs.lst

main-sk.o: main.s $(APPL).inc tree.inc ../lib/pmd85.inc lang-sk.inc Makefile
	$(AS) $(ASFLAGS) --defsym sk=1 -c $< -o $@ -aglms=main-sk.lst

%.o: %.s $(APPL).inc ../lib/pmd85.inc Makefile
	$(AS) $(ASFLAGS) -c $< -o $@ -aglms=$*.lst

clean:
	rm -f *.o *.lst $(APPL)*.bin $(APPL)*.hex $(APPL)*.map

.SUFFIXES:

.PHONY: build clean
