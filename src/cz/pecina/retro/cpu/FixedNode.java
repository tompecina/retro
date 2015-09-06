/* FixedNode.java
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

/**
 * I/O node with settable fixed signal level.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class FixedNode extends IONode {
  
  // the signal level of the pin
  private int level;

  /**
   * The main constructor.
   *
   * @param level the level level
   */
  public FixedNode(final int level) {
    this.level = level;
  }
  
  // for description see IONode
  @Override
  public int query() {
    return level;
  }

  /**
   * Sets the level on the node.
   *
   * @param level the new level
   */
  public void setLevel(final int level) {
    this.level = level;
    notifyChange();
  }

  /**
   * Gets the level on the node.
   *
   * @return the level on the node
   */
  public int getLevel() {
    return level;
  }
}
