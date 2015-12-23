APPL = fig-forth
OBJ = boot.o fig-forth.o
AS = as-z80
LD = ld-z80
BTP = bin2ptp
OBJCOPY = objcopy-z80
ASFLAGS = -z80 -I ../lib
LDFLAGS = -L ../lib -T $(APPL).ld

build: $(APPL).bin $(APPL).hex $(APPL).ptp

$(APPL).ptp: $(APPL).bin
	$(BTP) -n 0 -m FIGFORTH -o $@ $(APPL).bin

%.hex: %.bin
	$(OBJCOPY) -I binary -O ihex $< $@

$(APPL).bin: $(OBJ) ../lib/libpmd85.a $(APPL).ld
	$(LD) $(LDFLAGS) $(OBJ) -l pmd85 -o $@ -Map=$(APPL).map

%.o: %.s ../lib/pmd85.inc Makefile
	$(AS) $(ASFLAGS) -c $< -o $@ -aglms=$*.lst

clean:
	rm -f *.o *.lst $(APPL)*.bin $(APPL)*.hex $(APPL)*.map

.SUFFIXES:

.PHONY: build clean
