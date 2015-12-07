#! /usr/bin/python3

# simple utility producing block of code comparison test data

from random import randint

P = 5
C = 8
CP = 0x8000
COLORS = "01234567"

def match(a, b):
    x = 0
    for p in range(P):
        if a[p] == b[p]:
            x += 1
    y = 0
    for c in range(C):
        na = nb = 0
        for p in range(P):
            if a[p] == COLORS[c]:
                na += 1
            if b[p] == COLORS[c]:
                nb += 1
        y += min(na, nb)
    y -= x
    return x, y

for i in range(4000):
    c1 = randint(0, CP - 1)
    c2 = randint(0, CP - 1)
    r = match("%005o" % c1, "%005o" % c2)
    print("\t.byte\t0x%02x\t; %005o %005o (%d,%d)" % ((r[0] << 3) | r[1], c1, c2, r[0], r[1]))
    print("\t.word\t0x%04x, 0x%04x" % (c1, c2))
