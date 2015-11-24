#! /usr/bin/python3

fi = open("gen1.txt")

for d in range(4):
    for i in range(128):
        ln = fi.readline()
        l=[]
        for j in range(81):
            ch = ln[j]
            if (ch == "."):
                l.append(0)
            else:
                l.append(ord(ch) - ord("0"))
        r = []
        for n in range(9):
            found = False
            for j in range(81):
                if l[j] == n + 1:
                    found = True
                    r.append(j)
            if found:
                r[-1] |= 0x80
            else:
                r.append(0xff)
        print("\t; #%d @%d" % (i, d))
        p = "\t.byte\t"
        s = list(map(lambda x: "0x%02x" % x, r))
        while len(s) > 8:
            print("%s%s" % (p, ", ".join(s[:8])))
            s = s[8:]
        print("%s%s" % (p, ", ".join(s)))
