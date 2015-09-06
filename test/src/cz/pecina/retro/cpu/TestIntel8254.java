/* TestIntel8254.java
 *
 * Copyright (C) 2015, Tomáš Pecina <tomas@pecina.cz>
 *
 * This file is part of cz.pecina.retro, retro 8-bit computer emulators.
 *
 * This application is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This application is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.         
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cz.pecina.retro.cpu;

import junit.framework.TestCase;

public class TestIntel8254 extends TestCase {

  Intel8254 pit;
  FixedNode c0clk;
  FixedNode[] gates = new FixedNode[2];
  IONode outs[] = new IONode[2];
  
  @Override
  protected void setUp() {
    pit = new Intel8254("PIT", new boolean[] {false, true, false});
    c0clk = new FixedNode(0);
    c0clk.add(pit.getClockPin(0));
    for (int i = 0; i < 2; i++) {
      gates[i] = new FixedNode(1);
      gates[i].add(pit.getGatePin(i));
      outs[i] = new IONode();
      outs[i].add(pit.getOutPin(i));
    }
  }

  // counter status
  private class Status {
    int status, value, out;
  }
  
  // set clock pin on Counter 0
  private void clock0(final int level) {
    c0clk.setLevel(level);
  }

  // clock pulse on Counter 0
  private void pulse0() {
    clock0(1);
    clock0(0);
  }
  
  // set gate pin on Counter 0
  private void gate0(final int level) {
    gates[0].setLevel(level);
  }

  // set gate pin on Counter 1
  private void gate1(final int level) {
    gates[1].setLevel(level);
  }

  // get status and counter value on counter
  private Status poll(final int n) {
    Status s = new Status();
    pit.portOutput(3, 0b11100000 | (0b10 << n));
    s.status = pit.portInput(n);
    int rw = (s.status >> 4) & 3;
    if (rw == 0) {
      fail("Zero RW in status");
    }
    if (rw == 3) {
      int lsb = pit.portInput(n);
      int msb = pit.portInput(n);
      s.value = lsb + (msb * (((s.status & 1) == 1) ? 100 : 0x100));
    } else {
      s.value = pit.portInput(n);
    }
    s.out = outs[n].query();
    return s;
  }

  // output value to counter
  private void out(final int n, final int data) {
    pit.portOutput(n, data);
  }

  // output value to Counter 0
  private void out0(final int data) {
    out(0, data);
  }

  // output Control Word
  private void outcw(final int data) {
    out(3, data);
  }

  // output value to Counter 1
  private void out1(final int data) {
    out(1, data);
  }

  public void testMode0_1() {
    Status s;

    pit.reset();

    outcw(0x10);

    s = poll(0);
    assertEquals("Stage 0 status", 0x50, s.status, 0x50);
    assertEquals("Stage 0 out", 0, s.out);

    out0(4);
    pulse0();

    s = poll(0);
    assertEquals("Stage 1 status", 0x10, s.status);
    assertEquals("Stage 1 value", 4, s.value);
    assertEquals("Stage 1 out", 0, s.out);

    pulse0();

    s = poll(0);
    assertEquals("Stage 2 status", 0x10, s.status);
    assertEquals("Stage 2 value", 3, s.value);
    assertEquals("Stage 2 out", 0, s.out);

    pulse0();

    s = poll(0);
    assertEquals("Stage 3 status", 0x10, s.status);
    assertEquals("Stage 3 value", 2, s.value);
    assertEquals("Stage 3 out", 0, s.out);

    pulse0();

    s = poll(0);
    assertEquals("Stage 4 status", 0x10, s.status);
    assertEquals("Stage 4 value", 1, s.value);
    assertEquals("Stage 4 out", 0, s.out);

    pulse0();

    s = poll(0);
    assertEquals("Stage 5 status", 0x90, s.status);
    assertEquals("Stage 5 value", 0, s.value);
    assertEquals("Stage 5 out", 1, s.out);

    pulse0();

    s = poll(0);
    assertEquals("Stage 6 status", 0x90, s.status);
    assertEquals("Stage 6 value", 0xff, s.value);
    assertEquals("Stage 6 out", 1, s.out);

    pulse0();

    s = poll(0);
    assertEquals("Stage 7 status", 0x90, s.status);
    assertEquals("Stage 7 value", 0xfe, s.value);
    assertEquals("Stage 7 out", 1, s.out);
  }

  public void testMode0_2() {
    Status s;

    pit.reset();

    outcw(0x10);

    s = poll(0);
    assertEquals("Stage 0 status", 0x50, s.status, 0x50);
    assertEquals("Stage 0 out", 0, s.out);

    out0(3);
    pulse0();

    s = poll(0);
    assertEquals("Stage 1 status", 0x10, s.status);
    assertEquals("Stage 1 value", 3, s.value);
    assertEquals("Stage 1 out", 0, s.out);

    clock0(1);
    gate0(0);
    clock0(0);
    
    s = poll(0);
    assertEquals("Stage 2 status", 0x10, s.status);
    assertEquals("Stage 2 value", 2, s.value);
    assertEquals("Stage 2 out", 0, s.out);

    pulse0();

    s = poll(0);
    assertEquals("Stage 3 status", 0x10, s.status);
    assertEquals("Stage 3 value", 2, s.value);
    assertEquals("Stage 3 out", 0, s.out);

    pulse0();

    s = poll(0);
    assertEquals("Stage 4 status", 0x10, s.status);
    assertEquals("Stage 4 value", 2, s.value);
    assertEquals("Stage 4 out", 0, s.out);

    gate0(1);
    pulse0();

    s = poll(0);
    assertEquals("Stage 5 status", 0x10, s.status);
    assertEquals("Stage 5 value", 1, s.value);
    assertEquals("Stage 5 out", 0, s.out);

    pulse0();

    s = poll(0);
    assertEquals("Stage 6 status", 0x90, s.status);
    assertEquals("Stage 6 value", 0, s.value);
    assertEquals("Stage 6 out", 1, s.out);

    pulse0();

    s = poll(0);
    assertEquals("Stage 7 status", 0x90, s.status);
    assertEquals("Stage 7 value", 0xff, s.value);
    assertEquals("Stage 7 out", 1, s.out);
  }

  public void testMode0_3() {
    Status s;

    pit.reset();

    outcw(0x10);

    s = poll(0);
    assertEquals("Stage 0 status", 0x50, s.status, 0x50);
    assertEquals("Stage 0 out", 0, s.out);

    out0(3);
    pulse0();

    s = poll(0);
    assertEquals("Stage 1 status", 0x10, s.status);
    assertEquals("Stage 1 value", 3, s.value);
    assertEquals("Stage 1 out", 0, s.out);

    pulse0();
    
    s = poll(0);
    assertEquals("Stage 2 status", 0x10, s.status);
    assertEquals("Stage 2 value", 2, s.value);
    assertEquals("Stage 2 out", 0, s.out);

    pulse0();

    s = poll(0);
    assertEquals("Stage 3 status", 0x10, s.status);
    assertEquals("Stage 3 value", 1, s.value);
    assertEquals("Stage 3 out", 0, s.out);

    out0(2);
    pulse0();

    s = poll(0);
    assertEquals("Stage 4 status", 0x10, s.status);
    assertEquals("Stage 4 value", 2, s.value);
    assertEquals("Stage 4 out", 0, s.out);

    pulse0();

    s = poll(0);
    assertEquals("Stage 5 status", 0x10, s.status);
    assertEquals("Stage 5 value", 1, s.value);
    assertEquals("Stage 5 out", 0, s.out);

    pulse0();

    s = poll(0);
    assertEquals("Stage 6 status", 0x90, s.status);
    assertEquals("Stage 6 value", 0, s.value);
    assertEquals("Stage 6 out", 1, s.out);

    pulse0();

    s = poll(0);
    assertEquals("Stage 7 status", 0x90, s.status);
    assertEquals("Stage 7 value", 0xff, s.value);
    assertEquals("Stage 7 out", 1, s.out);
  }

  public void testMode0_4() {
    Status s;

    pit.reset();

    outcw(0b00110000);

    s = poll(0);
    assertEquals("Stage 0 status", 0x50, s.status, 0x50);
    assertEquals("Stage 0 out", 0, s.out);

    out0(1);
    out0(0);
    pulse0();

    s = poll(0);
    assertEquals("Stage 1 status", 0x30, s.status);
    assertEquals("Stage 1 value", 1, s.value);
    assertEquals("Stage 1 out", 0, s.out);

    pulse0();

    s = poll(0);
    assertEquals("Stage 2 status", 0xb0, s.status);
    assertEquals("Stage 2 value", 0, s.value);
    assertEquals("Stage 2 out", 1, s.out);

    pulse0();

    s = poll(0);
    assertEquals("Stage 3 status", 0xb0, s.status);
    assertEquals("Stage 3 value", 0xffff, s.value);
    assertEquals("Stage 3 out", 1, s.out);

    out0(3);

    s = poll(0);
    assertEquals("Stage 4 status", 0xb0, s.status);
    assertEquals("Stage 4 value", 0xffff, s.value);
    assertEquals("Stage 4 out", 0, s.out);

    pulse0();

    s = poll(0);
    assertEquals("Stage 5 status", 0xb0, s.status);
    assertEquals("Stage 5 value", 0, s.value);
    assertEquals("Stage 5 out", 1, s.out);

    pulse0();

    s = poll(0);
    assertEquals("Stage 6 status", 0xb0, s.status);
    assertEquals("Stage 6 value", 0xff, s.value);
    assertEquals("Stage 6 out", 1, s.out);

    pulse0();

    s = poll(0);
    assertEquals("Stage 7 status", 0xb0, s.status);
    assertEquals("Stage 7 value", 0xfe, s.value);
    assertEquals("Stage 7 out", 1, s.out);
  }
}
