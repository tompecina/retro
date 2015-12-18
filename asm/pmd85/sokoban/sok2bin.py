#! /usr/bin/python3
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
#
#
# Utility converting text Sokoban files to compressed binary format.
#

from sys import argv, stdin, stdout, stderr
from getopt import getopt, GetoptError
from re import search

def main(argv):

    def usage():
        print("Usage:")
        print("  sok2bin [options] [infile...]")
        print("\nOptions:")
        print("  -?,--help                     show usage information")
        print("  -V, --version                 show version")
        print("  -o OUTFILE, --output=OUTFILE  output file (required)")
        print("  -v, --verbose                 verbose output")

    def report(*msg):
        if verbose:
            print(*msg, file=stderr)

    def error(*msg):
        print(*msg, file=stderr)
        exit(1)

    def putbyte(b):
        r = []
        for i in range(8):
            r.append((b >> (7 - i)) & 1)
        return r

    def out(ctr, c):
        if ctr == 1:
            r = [0]
        else:
            ctr -= 2
            r = [1, (ctr >> 2) & 1, (ctr >> 1) & 1, ctr & 1]
        if c == '#':
            r.extend([0, 1])
        elif c == '$':
            r.extend([1, 0])
        elif c == '*':
            r.extend([1, 1, 1])
        elif c == '.':
            r.extend([1, 1, 0])
        elif c == ' ':
            r.extend([0, 0])
        else:
            error("Internal error")
        return r
            
    def proc(fi):
        lines = fi.readlines()
        lines.append("")
        run = False
        for line in lines:
            line = line.strip()
            isdata = (line and line[0] in "#B*123456789")
            if isdata and not run:
                run = True
                l = []
            if run:
                if isdata:
                    l.append(line)
                else:
                    run = False
                    l = '|'.join(l)
                    l = l.replace("||", "|")
                    if l[-1] == '|':
                        l = l[:-1]
                    l = l.split('|')
                    for i in range(len(l)):
                        ll = l[i]
                        while True:
                            m = search(r'(\d+)\(([^\d)]+)\)', ll)
                            if not m:
                                break
                            st = m.start()
                            en = m.end()
                            nl = ll[:st]
                            for j in range(int(m.group(1))):
                                nl += m.group(2)
                            nl += ll[en:]
                            l[i] = ll = nl
                        while True:
                            m = search(r'(\d+)(.)', ll)
                            if not m:
                                break
                            st = m.start()
                            en = m.end()
                            nl = ll[:st]
                            for j in range(int(m.group(1))):
                                nl += m.group(2)
                            nl += ll[en:]
                            l[i] = ll = nl
                    rows = len(l)
                    if rows < 3:
                        error("Too few rows")
                    cols = max(map(len, l))
                    for i in range(rows):
                        if len(l[i]) < cols:
                            l[i] += " " * (cols - len(l[i]))
                    report("Rows: %d, columns: %d" % (rows, cols))
                    b = []
                    b.extend(putbyte(cols))
                    b.extend(putbyte(rows))
                    l = "".join(l)
                    ctr = pos = 0
                    prow = -1
                    for ch in l:
                        if ch == '#':
                            c = '#'
                        elif ch in "p@":
                            c = '@'
                        elif ch in "P+":
                            c = '+'
                        elif ch in "b$":
                            c = '$'
                        elif ch in "B*":
                            c = '*'
                        elif ch == '.':
                            c = '.'
                        elif ch in " -_":
                            c = ' '
                        else:
                            error("Syntax error (1)")
                        if c in '@+':
                            prow = pos // rows
                            pcol = pos % rows
                            if c == '@':
                                c = ' '
                            else:
                                c = '.'
                        if ctr and (c == prev):
                            ctr += 1
                            if ctr == 9:
                                b.extend(out(ctr, c))
                                prev = ctr = 0
                        else:
                            if ctr:
                                b.extend(out(ctr, c))
                            ctr = 1
                            prev = c
                        pos += 1
                    if pos != (rows * cols):
                        error("Syntax error (2)")
                    while len(b) % 8:
                        b.append(0)
                    if prow < 0:
                        error("Syntax error (3)")
                    b.extend(putbyte(pcol))
                    b.extend(putbyte(prow))
                    r = []
                    for i in range(len(b) // 8):
                        p = 0
                        for j in range(8):
                            p |= b[(i * 8) + j] << (7 - j)
                        r.append(p)
                    lr = len(r)
                    r.insert(0, lr % 0x100)
                    r.insert(1, lr // 0x100)
                    fo.write(bytes(r))
                    
    try:
        opts, args = getopt(argv, '?Vo:v', ['help', 'version', 'output=', 'verbose'])
    except GetoptError:
        usage()
        exit(1)

    outfile = ''
    verbose = False
    
    for opt, arg in opts:
        if opt in ['-?', '--help']:
            usage()
            return 0
        elif opt in ['-V', '--version']:
            print("sok2bin V1.0")
            return 0
        elif opt in ['-o', '--output']:
            outfile = arg
        elif opt in ['-v', '--verbose']:
            verbose = True
    infiles = args[:]
    if not outfile:
        error("No output file")
    try:
        fo = open(outfile, "wb")
    except:
        error("Error opening file:", outfile)
    if not infiles:
        report("Processing standard input")
        proc(stdin)
    else:
        for infile in infiles:
            report("Processing file:", infile)
            try:
                fi = open(infile)
            except:
                print("Error opening file:", infile)
                return 1
            proc(fi)
            fi.close()
    fo.close()
        
    return 0

main(argv[1:])
