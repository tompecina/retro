/* TerminalCell.java
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

package cz.pecina.retro.pmi80;

import javax.swing.ImageIcon;

public class TerminalCell {
  private int code;
  private boolean bold, underscored, blinking, reverse;

  public TerminalCell() {
    this(0, false, false, false, false);
  }

  public TerminalCell(int code,
		      boolean bold,
		      boolean underscored,
		      boolean blinking,
		      boolean reverse) {
    this.code = code;
    this.bold = bold;
    this.underscored = underscored;
    this.blinking = blinking;
    this.reverse = reverse;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public boolean isBold() {
    return bold;
  }

  public void setBold(boolean bold) {
    this.bold = bold;
  }

  public boolean isUnderscored() {
    return underscored;
  }

  public void setUnderscored(boolean underscored) {
    this.underscored = underscored;
  }

  public boolean isBlinking() {
    return blinking;
  }

  public void setBlinking(boolean blinking) {
    this.blinking = blinking;
  }

  public boolean isReverse() {
    return reverse;
  }

  public void setReverse(boolean reverse) {
    this.reverse = reverse;
  }
}
