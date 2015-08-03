APPL = t
MCU = z80
OBJ = boot.o $(APPL).o pmi80lib.o pmi80clib.o crun8080lib.o
DEP = Makefile
CC = scc8080
AS = as-z80
LD = ld-z80
OBJCOPY = objcopy-z80
CCFLAGS = -a
ASFLAGS = -g -z80
LDFLAGS = -g -r --section-start=.text=0x2000

build: $(APPL).out $(APPL).hex

%.hex: %.out
	$(OBJCOPY) -O ihex $< $@

$(APPL).out: $(OBJ)
	$(LD) $(LDFLAGS) $(OBJ) -o $@

%.o: %.S
	$(AS) $(ASFLAGS) -c $< -o $@ -a=$*.lst

%.s: %.c
	$(CC) $(CCFLAGS) $<

%.S: %.s
	mv $< $@

clean:
	rm -f *.o *.lst $(APPL).out $(APPL).hex

.SUFFIXES:

.PHONY: build clean
