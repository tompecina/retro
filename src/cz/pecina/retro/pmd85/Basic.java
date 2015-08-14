/* Basic.java
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

package cz.pecina.retro.pmd85;

import java.util.logging.Logger;
import cz.pecina.retro.cpu.Intel8255;
import cz.pecina.retro.cpu.IOPin;
import cz.pecina.retro.cpu.IONode;

/**
 * Hardware of the Tesla PMD 85 pluggable ROM module.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Basic {

  // static logger
  private static final Logger log =
    Logger.getLogger(Basic.class.getName());

  private static final String[] TOKENS = {
    /* 0x80 */ "END", 
    /* 0x81 */ "FOR",
    /* 0x82 */ "NEXT",
    /* 0x83 */ "DATA",
    /* 0x84 */ "INPUT",
    /* 0x85 */ "DIM",
    /* 0x86 */ "READ",
    /* 0x87 */ "LET",
    /* 0x88 */ "GOTO",
    /* 0x89 */ "RUN",
    /* 0x8a */ "IF",
    /* 0x8b */ "RESTORE",
    /* 0x8c */ "GOSUB",
    /* 0x8d */ "RETURN",
    /* 0x8e */ "REM",
    /* 0x8f */ "STOP",
    /* 0x90 */ "BIT",
    /* 0x91 */ "ON",
    /* 0x92 */ "NULL",
    /* 0x93 */ "WAIT",
    /* 0x94 */ "DEF",
    /* 0x95 */ "POKE",
    /* 0x96 */ "PRINT",
    /* 0x97 */ "ERR",
    /* 0x98 */ "LIST",
    /* 0x99 */ "CLEAR",
    /* 0x9a */ "LLIST",
    /* 0x9b */ "RAD",   // used for MONIT in model PMD 85-1
    /* 0x9c */ "NEW",
    /* 0x9d */ "TAB(",
    /* 0x9e */ "TO",
    /* 0x9f */ "FNC",
    /* 0xa0 */"SPC(",
    /* 0xa1 */ "THEN",
    /* 0xa2 */ "NOT",
    /* 0xa3 */ "STEP",
    /* 0xa4 */ "+",
    /* 0xa5 */ "-",
    /* 0xa6 */ "*",
    /* 0xa7 */ "/",
    /* 0xa8 */ "^",
    /* 0xa9 */ "AND",
    /* 0xaa */ "OR",
    /* 0xab */ ">",
    /* 0xac */ "=",
    /* 0xad */ "<",
    /* 0xae */ "SGN",
    /* 0xaf */ "INT",
    /* 0xb0 */ "ABS",
    /* 0xb1 */ "USR",
    /* 0xb2 */ "FRE",
    /* 0xb3 */ "INP",
    /* 0xb4 */ "POS",
    /* 0xb5 */ "SQR",
    /* 0xb6 */ "RND",
    /* 0xb7 */ "LOG",
    /* 0xb8 */ "EXP",
    /* 0xb9 */ "COS",
    /* 0xba */ "SIN",
    /* 0xbb */ "TAN",
    /* 0xbc */ "ATN",
    /* 0xbd */ "PEEK",
    /* 0xbe */ "LEN",
    /* 0xbf */ "STR$",
    /* 0xc0 */ "VAL",
    /* 0xc1 */ "ASC",
    /* 0xc2 */ "CHR$",
    /* 0xc3 */ "LEFT$",
    /* 0xc4 */ "RIGHT$",
    /* 0xc5 */ "MID$",
    /* 0xc6 */ "SCALE",
    /* 0xc7 */ "PLOT",
    /* 0xc8 */ "MOVE",
    /* 0xc9 */ "BEEP",
    /* 0xca */ "AXES",
    /* 0xcb */ "GCLEAR",
    /* 0xcc */ "PAUSE",
    /* 0xcd */ "DISP",
    /* 0xce */ "_",     // equivalent to ?
    /* 0xcf */ "BMOVE",
    /* 0xd0 */ "BPLOT",
    /* 0xd1 */ "LOAD",
    /* 0xd2 */ "SAVE",
    /* 0xd3 */ "DLOAD",
    /* 0xd4 */ "DSAVE",
    /* 0xd5 */ "LABEL",
    /* 0xd6 */ "FILL",
    /* 0xd7 */ "AUTO",
    /* 0xd8 */ "OUTPUT",
    /* 0xd9 */ "STATUS",
    /* 0xda */ "ENTER",
    /* 0xdb */ "CONTROL",
    /* 0xdc */ "CHECK",
    /* 0xdd */ "CONT",
    /* 0xde */ "OUT",
    /* 0xdf */ "INKEY",
    /* 0xe0 */ "CODE",
    /* 0xe1 */ "ROM",
    /* 0xe2 */ "APOKE",
    /* 0xe3 */ "PEN",
    /* 0xe4 */ "INK(",
    /* 0xe5 */ "APEEK",
    /* 0xe6 */ "ADR",
    /* 0xe7 */ "AT",
    /* 0xe8 */ "HEX$",
    /* 0xe9 */ "DEG",
    /* 0xea */ "WINDOW",
    /* 0xeb */ "BEN",
    /* 0xec */ null,
    /* 0xed */ null,
    /* 0xee */ null,
    /* 0xef */ null,
    /* 0xf0 */ null,
    /* 0xf1 */ null,
    /* 0xf2 */ null,
    /* 0xf3 */ null,
    /* 0xf4 */ null,
    /* 0xf5 */ null,
    /* 0xf6 */ null,
    /* 0xf7 */ null,
    /* 0xf8 */ null,
    /* 0xf9 */ null,
    /* 0xfa */ null,
    /* 0xfb */ null,
    /* 0xfc */ null,
    /* 0xfd */ null,
    /* 0xfe */ null,
    /* 0xff */ null
  };

  // the memory object
  private PMDMemory memory;

  // the PIO
  private Intel8255 pio = new Intel8255("ROM_MODULE_PIO");

  // address pins
  private IOPin[] addressPins = new IOPin[16];

  // data pins
  private DataPin[] dataPins = new DataPin[8];

  /**
   * Creates a new ROM module hardware object.
   *
   * @param computerHardware the computer hardware object
   */
  public Basic(final ComputerHardware computerHardware) {
    log.fine("ROM module creation started");
    assert computerHardware != null;

    memory = computerHardware.getMemory();

    // set up address pins
    for (int i = 0; i < 16; i++) {
      addressPins[i] = new IOPin();
      new IONode().add(pio.getPin(8 + i)).add(addressPins[i]);
    }
    
    // set up data pins
    for (int i = 0; i < 8; i++) {
      dataPins[i] = new DataPin(i);
      new IONode().add(pio.getPin(i)).add(dataPins[i]);
    }
    
    log.finer("ROM module set up");
  }

  /**
   * Gets the PIO.
   */
  public Intel8255 getPIO() {
    return pio;
  }

  // data pins
  private class DataPin extends IOPin {
    private int mask;

    private DataPin(final int n) {
      super();
      assert (n >= 0) && (n < 8);
      mask = 1 << n;
    }

    @Override
    public int query() {
      int a = 0;
      for (int i = 15; i >= 0; i--) {
	a = (a << 1) | (addressPins[i].queryNode() & 1);
      }
      return (a < (memory.getSizeRMM() * 0x400)) ?
	     (memory.getRMM()[a] & mask) :
	     0xff;
    }
  }
}
