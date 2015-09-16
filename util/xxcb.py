#! /usr/bin/python3

ss = ["B", "C", "D", "E", "H", "L", "(HL)", "A"]
pp = ["dd", "fd"]
xx = ["IX", "IY"]

for j in range(2):

    p = pp[j]
    x = xx[j]

    print("  /**")
    print("   * The array of opcodes with the prefix %s CB." % p.upper())
    print("   */")
    print("  protected final Opcode[] opcodes%sCB = new Opcode[] {" % p.upper())
    print();
    
    for i in range(0x08):

        ii = i & 0x07
        s = ss[ii]
    
        if ii == 6:
            print(("    // " + p + " cb %02x") % i)
        else:
            print(("    // " + p + " cb %02x (undocumented)") % i)

        if ii == 6:
            print("    new Opcode(\"RLC\", \"(" + x + "<d>)\",")
            print("	       2,")
            print("	       Processor.INS_MR | Processor.INS_MW,")
            print("	       new Executable() {")
        else:
            print("    new Opcode(\"RLC\", \"(" + x + "<d>)," + s + "\",")
            print("	       2,")
            print("	       Processor.INS_UND | Processor.INS_MR | Processor.INS_MW,")
            print("	       new Executable() {")
        print("	@Override")
        print("	public int exec() {")
        print("	  incPC(-1);")
        print("	  WZ = " + x + " + ((byte)memory.getByte(PC));")
        print("	  int tb = memory.getByte(WZ);")
        print("	  if ((tb & 0x80) != 0) {")
        print("	    SETCF();")
        print("	  } else {")
        print("	    CLEARCF();")
        print("	  }")
        print("	  tb = ((tb << 1) | (F & 1)) & 0xff;")
        print("	  memory.setByte(WZ, tb);")
        if ii != 6:
            print("	  " + s + " = tb;")
        print("	  F5(tb);")
        print("	  CLEARHF();")
        print("	  CLEARNF();")
        print("	  incPC(2);")
        print("	  return 19;")
        print("	}")
        print("      }")		    
        print("      ),")
        print();

    for i in range(0x08, 0x10):

        ii = i & 0x07
        s = ss[ii]

        if ii == 6:
            print(("    // " + p + " cb %02x") % i)
        else:
            print(("    // " + p + " cb %02x (undocumented)") % i)

        if ii == 6:
            print("    new Opcode(\"RRC\", \"(" + x + "<d>)\",")
            print("	       2,")
            print("	       Processor.INS_MR | Processor.INS_MW,")
            print("	       new Executable() {")
        else:
            print("    new Opcode(\"RRC\", \"(" + x + "<d>)," + s + "\",")
            print("	       2,")
            print("	       Processor.INS_UND | Processor.INS_MR | Processor.INS_MW,")
            print("	       new Executable() {")
        print("	@Override")
        print("	public int exec() {")
        print("	  incPC(-1);")
        print("	  WZ = " + x + " + ((byte)memory.getByte(PC));")
        print("	  int tb = memory.getByte(WZ);")
        print("	  if ((tb & 1) != 0) {")
        print("	    SETCF();")
        print("	  } else {")
        print("	    CLEARCF();")
        print("	  }")
        print("	  tb = ((tb >> 1) | (F << 7)) & 0xff;")
        print("	  memory.setByte(WZ, tb);")
        if ii != 6:
            print("	  " + s + " = tb;")
        print("	  F5(tb);")
        print("	  CLEARHF();")
        print("	  CLEARNF();")
        print("	  incPC(2);")
        print("	  return 19;")
        print("	}")
        print("      }")		    
        print("      ),")
        print();

    for i in range(0x10, 0x18):

        ii = i & 0x07
        s = ss[ii]

        if ii == 6:
            print(("    // " + p + " cb %02x") % i)
        else:
            print(("    // " + p + " cb %02x (undocumented)") % i)

        if ii == 6:
            print("    new Opcode(\"RL\", \"(" + x + "<d>)\",")
            print("	       2,")
            print("	       Processor.INS_MR | Processor.INS_MW,")
            print("	       new Executable() {")
        else:
            print("    new Opcode(\"RL\", \"(" + x + "<d>)," + s + "\",")
            print("	       2,")
            print("	       Processor.INS_UND | Processor.INS_MR | Processor.INS_MW,")
            print("	       new Executable() {")
        print("	@Override")
        print("	public int exec() {")
        print("	  incPC(-1);")
        print("	  WZ = " + x + " + ((byte)memory.getByte(PC));")
        print("	  int tb = memory.getByte(WZ);")
        print("	  tb = (tb << 1) | (F & 1);")
        print("	  if ((tb & 0x100) != 0) {")
        print("	    SETCF();")
        print("	  } else {")
        print("	    CLEARCF();")
        print("	  }")
        print("	  tb &= 0xff;")
        print("	  memory.setByte(WZ, tb);")
        if ii != 6:
            print("	  " + s + " = tb;")
        print("	  F5(tb);")
        print("	  CLEARHF();")
        print("	  CLEARNF();")
        print("	  incPC(2);")
        print("	  return 19;")
        print("	}")
        print("      }")		    
        print("      ),")
        print();

    for i in range(0x18, 0x20):

        ii = i & 0x07
        s = ss[ii]

        if ii == 6:
            print(("    // " + p + " cb %02x") % i)
        else:
            print(("    // " + p + " cb %02x (undocumented)") % i)

        if ii == 6:
            print("    new Opcode(\"RR\", \"(" + x + "<d>)\",")
            print("	       2,")
            print("	       Processor.INS_MR | Processor.INS_MW,")
            print("	       new Executable() {")
        else:
            print("    new Opcode(\"RR\", \"(" + x + "<d>)," + s + "\",")
            print("	       2,")
            print("	       Processor.INS_UND | Processor.INS_MR | Processor.INS_MW,")
            print("	       new Executable() {")
        print("	@Override")
        print("	public int exec() {")
        print("	  incPC(-1);")
        print("	  WZ = " + x + " + ((byte)memory.getByte(PC));")
        print("	  int tb = memory.getByte(WZ);")
        print("	  final int ti = tb;")
        print("	  tb = (tb >> 1) | ((F & 1) << 7);")
        print("	  memory.setByte(WZ, tb);")
        if ii != 6:
            print("	  " + s + " = tb;")
        print("	  if ((ti & 1) != 0) {")
        print("	    SETCF();")
        print("	  } else {")
        print("	    CLEARCF();")
        print("	  }")
        print("	  F5(tb);")
        print("	  CLEARHF();")
        print("	  CLEARNF();")
        print("	  incPC(2);")
        print("	  return 19;")
        print("	}")
        print("      }")		    
        print("      ),")
        print();

    for i in range(0x20, 0x28):

        ii = i & 0x07
        s = ss[ii]

        if ii == 6:
            print(("    // " + p + " cb %02x") % i)
        else:
            print(("    // " + p + " cb %02x (undocumented)") % i)

        if ii == 6:
            print("    new Opcode(\"SLA\", \"(" + x + "<d>)\",")
            print("	       2,")
            print("	       Processor.INS_MR | Processor.INS_MW,")
            print("	       new Executable() {")
        else:
            print("    new Opcode(\"SLA\", \"(" + x + "<d>)," + s + "\",")
            print("	       2,")
            print("	       Processor.INS_UND | Processor.INS_MR | Processor.INS_MW,")
            print("	       new Executable() {")
        print("	@Override")
        print("	public int exec() {")
        print("	  incPC(-1);")
        print("	  WZ = " + x + " + ((byte)memory.getByte(PC));")
        print("	  int tb = memory.getByte(WZ);")
        print("	  if ((tb & 0x80) != 0) {")
        print("	    SETCF();")
        print("	  } else {")
        print("	    CLEARCF();")
        print("	  }")
        print("	  tb = (tb << 1) & 0xff;")
        print("	  memory.setByte(WZ, tb);")
        if ii != 6:
            print("	  " + s + " = tb;")
        print("	  F5(tb);")
        print("	  CLEARHF();")
        print("	  CLEARNF();")
        print("	  incPC(2);")
        print("	  return 19;")
        print("	}")
        print("      }")		    
        print("      ),")
        print();

    for i in range(0x28, 0x30):

        ii = i & 0x07
        s = ss[ii]

        if ii == 6:
            print(("    // " + p + " cb %02x") % i)
        else:
            print(("    // " + p + " cb %02x (undocumented)") % i)

        if ii == 6:
            print("    new Opcode(\"SRA\", \"(" + x + "<d>)\",")
            print("	       2,")
            print("	       Processor.INS_MR | Processor.INS_MW,")
            print("	       new Executable() {")
        else:
            print("    new Opcode(\"SRA\", \"(" + x + "<d>)," + s + "\",")
            print("	       2,")
            print("	       Processor.INS_UND | Processor.INS_MR | Processor.INS_MW,")
            print("	       new Executable() {")
        print("	@Override")
        print("	public int exec() {")
        print("	  incPC(-1);")
        print("	  WZ = " + x + " + ((byte)memory.getByte(PC));")
        print("	  int tb = memory.getByte(WZ);")
        print("	  if ((tb & 1) != 0) {")
        print("	    SETCF();")
        print("	  } else {")
        print("	    CLEARCF();")
        print("	  }")
        print("	  tb = (tb & 0x80) | (tb >> 1);")
        print("	  memory.setByte(WZ, tb);")
        if ii != 6:
            print("	  " + s + " = tb;")
        print("	  F5(tb);")
        print("	  CLEARHF();")
        print("	  CLEARNF();")
        print("	  incPC(2);")
        print("	  return 19;")
        print("	}")
        print("      }")		    
        print("      ),")
        print();

    for i in range(0x30, 0x38):

        ii = i & 0x07
        s = ss[ii]

        if ii == 6:
            print(("    // " + p + " cb %02x") % i)
        else:
            print(("    // " + p + " cb %02x (undocumented)") % i)

        if ii == 6:
            print("    new Opcode(\"SLL\", \"(" + x + "<d>)\",")
            print("	       2,")
            print("	       Processor.INS_MR | Processor.INS_MW,")
            print("	       new Executable() {")
        else:
            print("    new Opcode(\"SLL\", \"(" + x + "<d>)," + s + "\",")
            print("	       2,")
            print("	       Processor.INS_UND | Processor.INS_MR | Processor.INS_MW,")
            print("	       new Executable() {")
        print("	@Override")
        print("	public int exec() {")
        print("	  incPC(-1);")
        print("	  WZ = " + x + " + ((byte)memory.getByte(PC));")
        print("	  int tb = memory.getByte(WZ);")
        print("	  if ((tb & 0x80) != 0) {")
        print("	    SETCF();")
        print("	  } else {")
        print("	    CLEARCF();")
        print("	  }")
        print("	  tb = ((tb << 1) & 0xff) | 1;")
        print("	  memory.setByte(WZ, tb);")
        if ii != 6:
            print("	  " + s + " = tb;")
        print("	  F5(tb);")
        print("	  CLEARHF();")
        print("	  CLEARNF();")
        print("	  incPC(2);")
        print("	  return 19;")
        print("	}")
        print("      }")		    
        print("      ),")
        print();

    for i in range(0x38, 0x40):

        ii = i & 0x07
        s = ss[ii]

        if ii == 6:
            print(("    // " + p + " cb %02x") % i)
        else:
            print(("    // " + p + " cb %02x (undocumented)") % i)

        if ii == 6:
            print("    new Opcode(\"SRL\", \"(" + x + "<d>)\",")
            print("	       2,")
            print("	       Processor.INS_MR | Processor.INS_MW,")
            print("	       new Executable() {")
        else:
            print("    new Opcode(\"SRL\", \"(" + x + "<d>)," + s + "\",")
            print("	       2,")
            print("	       Processor.INS_UND | Processor.INS_MR | Processor.INS_MW,")
            print("	       new Executable() {")
        print("	@Override")
        print("	public int exec() {")
        print("	  incPC(-1);")
        print("	  WZ = " + x + " + ((byte)memory.getByte(PC));")
        print("	  int tb = memory.getByte(WZ);")
        print("	  if ((tb & 1) != 0) {")
        print("	    SETCF();")
        print("	  } else {")
        print("	    CLEARCF();")
        print("	  }")
        print("	  tb >>= 1;")
        print("	  memory.setByte(WZ, tb);")
        if ii != 6:
            print("	  " + s + " = tb;")
        print("	  F5(tb);")
        print("	  CLEARHF();")
        print("	  CLEARNF();")
        print("	  incPC(2);")
        print("	  return 19;")
        print("	}")
        print("      }")		    
        print("      ),")
        print();

    for i in range(0x40, 0x80):

        ii = i & 0x07
        s = ss[ii]
        b = (i & 0b00111000) >> 3

        if ii == 6:
            print(("    // " + p + " cb %02x") % i)
        else:
            print(("    // " + p + " cb %02x (undocumented)") % i)

        if ii == 6:
            print(("    new Opcode(\"BIT\", \"%d,(" + x + "<d>)\", 2, Processor.INS_MR, new Executable() {") % b)
        else:
            print(("new Opcode(\"BIT\", \"%d,(" + x + "<d>)," + s + "\",") % b)
            print("	       2,")
            print("	       Processor.INS_UND | Processor.INS_MR,")
            print("	       new Executable() {")
        print("	@Override")
        print("	public int exec() {")
        print("	  incPC(-1);")
        print("	  WZ = " + x + " + ((byte)memory.getByte(PC));")
        print("	  final int tb = memory.getByte(WZ);")
        if ii == 6:
            print("	  F22(tb, WZ >> 8);")
        else:
            print("	  F4(tb);")
        print(("	  if ((tb & 0x%02x) == 0) {") % (1 << b))
        print("	    SETZF();")
        print("	    SETPF();")
        print("	  } else {")
        print("	    CLEARZF();")
        print("	    CLEARPF();")
        print("	  }")
        if b == 7:
            print("	  if (ZFSET()) {")
            print("	    CLEARSF();")
            print("	  } else {")
            print("	    SETSF();")
            print("	  }")
        else:
            print("	  CLEARSF();")
        print("	  SETHF();")
        print("	  CLEARNF();")
        print("	  incPC(2);")
        print("	  return 16;")
        print("	}")
        print("      }")		    
        print("      ),")
        print();

    for i in range(0x80, 0xc0):

        ii = i & 0x07
        s = ss[ii]
        b = (i & 0b00111000) >> 3

        if ii == 6:
            print(("    // " + p + " cb %02x") % i)
        else:
            print(("    // " + p + " cb %02x (undocumented)") % i)

        if ii == 6:
            print(("    new Opcode(\"RES\", \"%d,(" + x + "<d>)\",") % b)
            print("	       2,")
            print("	       Processor.INS_MW | Processor.INS_MW,")
            print("	       new Executable() {")
        else:
            print(("    new Opcode(\"RES\", \"%d,(" + x + "<d>)," + s + "\",") % b)
            print("	       2,")
            print("	       Processor.INS_UND | Processor.INS_MR | Processor.INS_MW,")
            print("	       new Executable() {")
        print("	@Override")
        print("	public int exec() {")
        print("	  incPC(-1);")
        print("	  WZ = " + x + " + ((byte)memory.getByte(PC));")
        if ii != 6:
            print(("	  " + s + " = memory.getByte(WZ) & 0x%02x;") % (0xff - (1 << b)))
            print("	  memory.setByte(WZ, " + s + ");")
        else:
            print("	  memory.setByte(WZ, memory.getByte(WZ) & 0x%02x);" % (0xff - (1 << b)))
        print("	  incPC(2);")
        print("	  return 19;")
        print("	}")
        print("      }")		    
        print("      ),")
        print();

    for i in range(0xc0, 0x100):

        ii = i & 0x07
        s = ss[ii]
        b = (i & 0b00111000) >> 3

        if ii == 6:
            print(("    // " + p + " cb %02x") % i)
        else:
            print(("    // " + p + " cb %02x (undocumented)") % i)

        if ii == 6:
            print(("    new Opcode(\"SET\", \"%d,(" + x + "<d>)\",") % b)
            print("	       2,")
            print("	       Processor.INS_MW | Processor.INS_MW,")
            print("	       new Executable() {")
        else:
            print(("    new Opcode(\"SET\", \"%d,(" + x + "<d>)," + s + "\",") % b)
            print("	       2,")
            print("	       Processor.INS_UND | Processor.INS_MR | Processor.INS_MW,")
            print("	       new Executable() {")
        print("	@Override")
        print("	public int exec() {")
        print("	  incPC(-1);")
        print("	  WZ = " + x + " + ((byte)memory.getByte(PC));")
        if ii != 6:
            print(("	  " + s + " = memory.getByte(WZ) | 0x%02x;") % (1 << b))
            print("	  memory.setByte(WZ, " + s + ");")
        else:
            print("	  memory.setByte(WZ, memory.getByte(WZ) | 0x%02x);" % (1 << b))
        print("	  incPC(2);")
        print("	  return 19;")
        print("	}")
        print("      }")		    
        print("      ),")
        print();

    print("  };")
    print();
