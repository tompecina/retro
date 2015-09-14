#! /usr/bin/python3

ss = ["B", "C", "D", "E", "H", "L", "(HL)", "A"]

for i in range(0x08):

    ii = i & 0x07
    s = ss[ii]
    
    print("    // cb %02x" % i)

    if ii == 6:
        print("    new Opcode(\"RLC\", \"(HL)\", 1, Processor.INS_MR | Processor.INS_MW, new Executable() {")
    else:
        print("    new Opcode(\"RLC\", \"" + s + "\", 1, Processor.INS_NONE, new Executable() {")
    print("	@Override")
    print("	public int exec() {")
    if ii == 6:
        s = "tb"
        print("	  int tb = memory.getByte(HL());")
    print("	  if ((" + s + " & 0x80) != 0) {")
    print("	    SETCF();")
    print("	  } else {")
    print("	    RESETCF();")
    print("	  }")
    print("	  " + s + " = ((" + s + " << 1) | (F & 1)) & 0xff;")
    if ii == 6:
        print("	  memory.setByte(HL(), tb);")
    print("	  F5(" + s + ");")
    print("	  RESETHF();")
    print("	  RESETNF();")
    print("	  incPC();")
    if ii == 6:
        print("	  return 15;")
    else:
        print("	  return 8;")
    print("	}")
    print("      }")		    
    print("      ),")
    print();
  
for i in range(0x08, 0x10):

    ii = i & 0x07
    s = ss[ii]
    
    print("    // cb %02x" % i)

    if ii == 6:
        print("    new Opcode(\"RRC\", \"(HL)\", 1, Processor.INS_MR | Processor.INS_MW, new Executable() {")
    else:
        print("    new Opcode(\"RRC\", \"" + s + "\", 1, Processor.INS_NONE, new Executable() {")
    print("	@Override")
    print("	public int exec() {")
    if ii == 6:
        s = "tb"
        print("	  int tb = memory.getByte(HL());")
    print("	  if ((" + s + " & 1) != 0) {")
    print("	    SETCF();")
    print("	  } else {")
    print("	    RESETCF();")
    print("	  }")
    print("	  " + s + " = ((" + s + " >> 1) | (F << 1)) & 0xff;")
    if ii == 6:
        print("	  memory.setByte(HL(), tb);")
    print("	  F5(" + s + ");")
    print("	  RESETHF();")
    print("	  RESETNF();")
    print("	  incPC();")
    if ii == 6:
        print("	  return 15;")
    else:
        print("	  return 8;")
    print("	}")
    print("      }")		    
    print("      ),")
    print();
    
for i in range(0x10, 0x18):

    ii = i & 0x07
    s = ss[ii]
    
    print("    // cb %02x" % i)

    if ii == 6:
        print("    new Opcode(\"RL\", \"(HL)\", 1, Processor.INS_MR | Processor.INS_MW, new Executable() {")
    else:
        print("    new Opcode(\"RL\", \"" + s + "\", 1, Processor.INS_NONE, new Executable() {")
    print("	@Override")
    print("	public int exec() {")
    if ii == 6:
        s = "tb"
        print("	  int tb = memory.getByte(HL());")
    print("	  " + s + " = (" + s + " << 1) | (F & 1);")
    print("	  if ((" + s + " & 0x100) != 0) {")
    print("	    SETCF();")
    print("	  } else {")
    print("	    RESETCF();")
    print("	  }")
    print("	  " + s + " &= 0xff;")
    if ii == 6:
        print("	  memory.setByte(HL(), tb);")
    print("	  F5(" + s + ");")
    print("	  RESETHF();")
    print("	  RESETNF();")
    print("	  incPC();")
    if ii == 6:
        print("	  return 15;")
    else:
        print("	  return 8;")
    print("	}")
    print("      }")		    
    print("      ),")
    print();

for i in range(0x18, 0x20):

    ii = i & 0x07
    s = ss[ii]
    
    print("    // cb %02x" % i)

    if ii == 6:
        print("    new Opcode(\"RR\", \"(HL)\", 1, Processor.INS_MR | Processor.INS_MW, new Executable() {")
    else:
        print("    new Opcode(\"RR\", \"" + s + "\", 1, Processor.INS_NONE, new Executable() {")
    print("	@Override")
    print("	public int exec() {")
    if ii == 6:
        s = "tb"
        print("	  int tb = memory.getByte(HL());")
    print("	  final int ti = " + s + ";")
    print("	  " + s + " = (" + s + " >> 1) | ((F & 1) << 7);")
    if ii == 6:
        print("	  memory.setByte(HL(), tb);")
    print("	  if ((ti & 1) != 0) {")
    print("	    SETCF();")
    print("	  } else {")
    print("	    RESETCF();")
    print("	  }")
    print("	  F5(" + s + ");")
    print("	  RESETHF();")
    print("	  RESETNF();")
    print("	  incPC();")
    if ii == 6:
        print("	  return 15;")
    else:
        print("	  return 8;")
    print("	}")
    print("      }")		    
    print("      ),")
    print();
    
for i in range(0x20, 0x28):

    ii = i & 0x07
    s = ss[ii]
    
    print("    // cb %02x" % i)

    if ii == 6:
        print("    new Opcode(\"SLA\", \"(HL)\", 1, Processor.INS_MR | Processor.INS_MW, new Executable() {")
    else:
        print("    new Opcode(\"SLA\", \"" + s + "\", 1, Processor.INS_NONE, new Executable() {")
    print("	@Override")
    print("	public int exec() {")
    if ii == 6:
        s = "tb"
        print("	  int tb = memory.getByte(HL());")
    print("	  if ((" + s + " & 0x80) != 0) {")
    print("	    SETCF();")
    print("	  } else {")
    print("	    RESETCF();")
    print("	  }")
    print("	  " + s + " = (" + s + " << 1) & 0xff;")
    if ii == 6:
        print("	  memory.setByte(HL(), tb);")
    print("	  F5(" + s + ");")
    print("	  RESETHF();")
    print("	  RESETNF();")
    print("	  incPC();")
    if ii == 6:
        print("	  return 15;")
    else:
        print("	  return 8;")
    print("	}")
    print("      }")		    
    print("      ),")
    print();

for i in range(0x28, 0x30):

    ii = i & 0x07
    s = ss[ii]
    
    print("    // cb %02x" % i)

    if ii == 6:
        print("    new Opcode(\"SRA\", \"(HL)\", 1, Processor.INS_MR | Processor.INS_MW, new Executable() {")
    else:
        print("    new Opcode(\"SRA\", \"" + s + "\", 1, Processor.INS_NONE, new Executable() {")
    print("	@Override")
    print("	public int exec() {")
    if ii == 6:
        s = "tb"
        print("	  int tb = memory.getByte(HL());")
    print("	  if ((" + s + " & 1) != 0) {")
    print("	    SETCF();")
    print("	  } else {")
    print("	    RESETCF();")
    print("	  }")
    print("	  " + s + " = (" + s + " & 0x80) | (" + s + " >> 1);")
    if ii == 6:
        print("	  memory.setByte(HL(), tb);")
    print("	  F5(" + s + ");")
    print("	  RESETHF();")
    print("	  RESETNF();")
    print("	  incPC();")
    if ii == 6:
        print("	  return 15;")
    else:
        print("	  return 8;")
    print("	}")
    print("      }")		    
    print("      ),")
    print();

for i in range(0x30, 0x38):

    ii = i & 0x07
    s = ss[ii]
    
    print("    // cb %02x" % i)

    if ii == 6:
        print("    new Opcode(\"SLL\", \"(HL)\", 1, Processor.INS_MR | Processor.INS_MW, new Executable() {")
    else:
        print("    new Opcode(\"SLL\", \"" + s + "\", 1, Processor.INS_NONE, new Executable() {")
    print("	@Override")
    print("	public int exec() {")
    if ii == 6:
        s = "tb"
        print("	  int tb = memory.getByte(HL());")
    print("	  if ((" + s + " & 0x80) != 0) {")
    print("	    SETCF();")
    print("	  } else {")
    print("	    RESETCF();")
    print("	  }")
    print("	  " + s + " = ((" + s + " << 1) & 0xff) | 1;")
    if ii == 6:
        print("	  memory.setByte(HL(), tb);")
    print("	  F5(" + s + ");")
    print("	  RESETHF();")
    print("	  RESETNF();")
    print("	  incPC();")
    if ii == 6:
        print("	  return 15;")
    else:
        print("	  return 8;")
    print("	}")
    print("      }")		    
    print("      ),")
    print();

for i in range(0x38, 0x40):

    ii = i & 0x07
    s = ss[ii]
    
    print("    // cb %02x" % i)

    if ii == 6:
        print("    new Opcode(\"SRL\", \"(HL)\", 1, Processor.INS_MR | Processor.INS_MW, new Executable() {")
    else:
        print("    new Opcode(\"SRL\", \"" + s + "\", 1, Processor.INS_NONE, new Executable() {")
    print("	@Override")
    print("	public int exec() {")
    if ii == 6:
        s = "tb"
        print("	  int tb = memory.getByte(HL());")
    print("	  if ((" + s + " & 1) != 0) {")
    print("	    SETCF();")
    print("	  } else {")
    print("	    RESETCF();")
    print("	  }")
    print("	  " + s + " >>= 1;")
    if ii == 6:
        print("	  memory.setByte(HL(), tb);")
    print("	  F5(" + s + ");")
    print("	  RESETHF();")
    print("	  RESETNF();")
    print("	  incPC();")
    if ii == 6:
        print("	  return 15;")
    else:
        print("	  return 8;")
    print("	}")
    print("      }")		    
    print("      ),")
    print();
