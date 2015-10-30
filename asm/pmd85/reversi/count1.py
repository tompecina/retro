#! /usr/bin/python3

for i in range(16):
    l = []
    for j in range(16):
        a = i * 16 + j
        c = 0
        for k in range(8):
            if a & 1:
                c += 1
            a >>= 1
        l.append(c)
    print(l)
