APPL = forth
OBJ = forth.o
AS = as-z80
LD = ld-z80
CIM = cim
ASFLAGS = -z80 -I ../lib
LDFLAGS = -L ../lib -T $(APPL).ld

build: $(APPL).com $(APPL).p32

$(APPL).p32: $(APPL).com
	$(CIM) $(APPL).p32 -f
	$(CIM) $(APPL).p32 -a $(APPL).com

$(APPL).com: $(OBJ) ../lib/libpmd85.a $(APPL).ld
	$(LD) $(LDFLAGS) $(OBJ) -l pmd85 -o $@ -Map=$(APPL).map

%.o: %.s $(APPL).inc ../lib/pmd85.inc Makefile
	$(AS) $(ASFLAGS) -c $< -o $@ -aglms=$*.lst

clean:
	rm -f *.o *.lst $(APPL)*.com $(APPL).p32 $(APPL)*.map

.SUFFIXES:

.PHONY: build clean
