/* BlockModel.java
 *
 * Copyright (C) 2014-2015, Tomáš Pecina <tomas@pecina.cz>
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

package cz.pecina.retro.gui;

/**
 * Container clas holding graphical parameters for blocks.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class BlockModel {

  /**
   * Type of the block elements.
   */
  public String elementType;

  /**
   * Color of the block elements.
   */
  public String elementColor;

  /**
   * Type of the nudge buttons.
   */
  public String buttonType;

  /**
   * Color of the nudge buttons.
   */
  public String buttonColor;

  /**
   * Symbol for the increment button.
   */
  public String incrementButtonSymbol;

  /**
   * Symbol for the decrement button.
   */
  public String decrementButtonSymbol;

  /**
   * Horizontal block grid dimension.  It is the horizontal distance
   * between the n-th and (n+1)-th element of the grid.  For numeric
   * blocks, this is normally negative.
   */
  public int gridX;
    
  /**
   * Horizontal offset of the 0-th element measured from the reference
   * point of the block, i.e., the value used in the block's
   * <code>place</code> method.
   */
  public int elementOffsetX;

  /**
   * Vertical offset of the 0-th element measured from the reference
   * point of the block, i.e., the value used in the block's
   * <code>place</code> method.
   */
  public int elementOffsetY;

  /**
   * Horizontal offset of the 0-th element's increment button measured
   * from the reference point of the block, i.e., the value used in
   * the block's <code>place</code> method.
   */
  public int incrementButtonOffsetX;

  /**
   * Vertical offset of the 0-th element's increment button measured
   * from the reference point of the block, i.e., the value used in
   * the block's <code>place</code> method.
   */
  public int incrementButtonOffsetY;

  /**
   * Horizontal offset of the 0-th element's decrement button measured
   * from the reference point of the block, i.e., the value used in
   * the block's <code>place</code> method.
   */
  public int decrementButtonOffsetX;

  /**
   * Vertical offset of the 0-th element's decrement button measured
   * from the reference point of the block, i.e., the value used in
   * the block's <code>place</code> method.
   */
  public int decrementButtonOffsetY;
}
