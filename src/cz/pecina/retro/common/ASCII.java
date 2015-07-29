/* ASCII.java
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

package cz.pecina.retro.common;

/**
 * ASCII control characters
 */
public class ASCII {
  public static final int NUL = 0x00;
  public static final int ENQ = 0x05;
  public static final int BEL = 0x07;
  public static final int BS = 0x08;
  public static final int HT = 0x09;
  public static final int LF = 0x0a;
  public static final int VT = 0x0b;
  public static final int FF = 0x0c;
  public static final int CR = 0x0d;
  public static final int SO = 0x0e;
  public static final int LS1 = SO;
  public static final int SI = 0x0f;
  public static final int LS0 = SI;
  public static final int DC1 = 0x11;
  public static final int XON = DC1;
  public static final int DC3 = 0x13;
  public static final int XOFF = DC3;
  public static final int CAN = 0x18;
  public static final int SUB = 0x1a;
  public static final int ESC = 0x1b;
  public static final int DEL = 0x7f;
  public static final int IND = 0x84;
  public static final int NEL = 0x85;
  public static final int HTS = 0x88;
  public static final int RI = 0x8d;
  public static final int SS2 = 0x8e;
  public static final int SS3 = 0x8f;
  public static final int DCS = 0x90;
  public static final int CSI = 0x9b;
  public static final int ST = 0x9c;
}
