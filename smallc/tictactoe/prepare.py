#! /usr/bin/python3

# prepare.py
#
# Copyright (C) 2015, Tomáš Pecina <tomas@pecina.cz>
#
# This file is part of cz.pecina.retro, retro 8-bit computer emulators.
#
# This application is free software: you can redistribute it and/or
# modify it under the terms of the GNU General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
#
# This application is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.         
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.


# This is the code preparing the best moves table for Tic-Tac-Toe.

# sign of x
def sign(x):
    if x > 0:
        return 1
    elif x < 0:
        return -1
    else:
        return 0

# returns true if board is won by player 1
def win(x):
    return (x[0] == 1 and x[1] == 1 and x[2] == 1) or \
           (x[3] == 1 and x[4] == 1 and x[5] == 1) or \
           (x[6] == 1 and x[7] == 1 and x[8] == 1) or \
           (x[0] == 1 and x[3] == 1 and x[6] == 1) or \
           (x[1] == 1 and x[4] == 1 and x[7] == 1) or \
           (x[2] == 1 and x[5] == 1 and x[8] == 1) or \
           (x[0] == 1 and x[4] == 1 and x[8] == 1) or \
           (x[2] == 1 and x[4] == 1 and x[6] == 1)

# calculates numeric hash of a board           
def b2h(b):
    r = 0
    for i in range(9):
        r = (r << 2) | (b[8 - i] & 0b11)
    return r

# repository of boards
repo = {}

# calculate score of a completed board
def score(b):
    m = max(b)
    if m == -9:
        return 0
    else:
        return m

# calculate score of an incomplete board
def assess(x):
    q = b2h(x)
    if q in repo:
        return score(repo[q])
    r = []
    for i in range(9):
        if x[i] != 0:
            r.append(-9)
        else:
            y = x[:]
            y[i] = 1
            if win(y):
                r.append(9)
            else:
                a = -assess(list(map(lambda n : -n, y)))
                a -= sign(a)
                r.append(a)
    repo[q] = r
    return score(r)

# build repo of all necessary boards
assess([0] * 9)
for i in range(9):
    b = [0] * 9
    b[i] = -1
    assess(b)

# eliminate immediately winning and/or almost full boards
keys = list(repo.keys())
for k in keys:
    r = repo[k]
    if max(r) == 9:
        del repo[k]
    else:
        n = 0
        for p in r:
            if p > -9:
                n += 1
        if n < 2:
            del repo[k]
l = len(repo)
print("; this is a computer generated file, do not edit it")
print()
print("\t.section .text")
print()
print("\t.global _bmt")
print("_bmt:")
n = 0
for k in repo:
    r = repo[k]
    m = max(r)
    s = 0
    for i in range(8, -1, -1):
        s <<= 1
        if r[i] == m:
            s |= 1
    n += 1
    print("\t.byte\t0x%02x, 0x%02x, 0x%02x, 0x%02x, 0x%02x" %
          (k % 0x100, (k >> 8) % 0x100, (k >> 16) % 0x100, s % 0x100, (s >> 8) % 0x100))
print()
print("\t.end")
