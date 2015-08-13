/* IOPin.java
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
 * An I/O pin that can be connected to a node.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class IOPin {

  /**
   * The node to which the pin is connected.
   */
  protected IONode node;

  /**
   * Main constructor.
   */
  public IOPin() {
  }

  /**
   * Set the node.
   *
   * @param node node to be set
   */
  public void setNode(final IONode node) {
    this.node = node;
  }

  /**
   * Query the state (signal level) of the node the pin is connected to.
   * If the pin is connected to no node, high impedance is assumed.
   *
   * @return state (signal level) of the node
   */
  public int queryNode() {
    return (node == null) ? IONode.HIGH_IMPEDANCE : node.query();
  }

  /**
   * Notify the node of the possibly changed state (signal level).
   */
  public void notifyChangeNode() {
    if (node != null)
      node.notifyChange();
  }

  /**
   * State (signal level) of the pin.  For output or tri-state pins,
   * this method is to be overridden by the subclass.
   *
   * @return state (signal level) of the pin
   */
  public int query() {
    return IONode.HIGH_IMPEDANCE;
  }
    
  /**
   * Notification event on possible change of state (signal level) of
   * the node.  For input or tri-state pins, this method is to be overridden
   * by the subclass.
   */
  public void notifyChange() {
  }
}
