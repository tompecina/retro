APPL = logik
OBJ = main.o board.o labels.o game.o tree.o
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

$(APPL)-en.bin: $(OBJ) lang-en.o ../lib/libpmd85.a ../lib/pmd85.ld
	$(LD) $(LDFLAGS) $(OBJ) lang-en.o -l pmd85 -o $@ -Map=$(APPL)-en.map

$(APPL)-cs.bin: $(OBJ) lang-cs.o ../lib/libpmd85.a ../lib/pmd85.ld
	$(LD) $(LDFLAGS) $(OBJ) lang-cs.o -l pmd85 -o $@ -Map=$(APPL)-cs.map

$(APPL)-sk.bin: $(OBJ) lang-sk.o ../lib/libpmd85.a ../lib/pmd85.ld
	$(LD) $(LDFLAGS) $(OBJ) lang-sk.o -l pmd85 -o $@ -Map=$(APPL)-sk.map

%.o: %.s $(APPL).inc ../lib/pmd85.inc Makefile
	$(AS) $(ASFLAGS) -c $< -o $@ -aglms=$*.lst

clean:
	rm -f *.o *.lst $(APPL)*.bin $(APPL)*.hex $(APPL)*.ptp $(APPL)*.map

.SUFFIXES:

.PHONY: build clean
