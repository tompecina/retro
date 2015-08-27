/* Device.java
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

import java.util.logging.Logger;

import java.util.List;
import java.util.ArrayList;

import org.jdom2.Element;

/**
 * Abstract stateful hardware element.  It is identified by
 * a unique name and contains a set of <code>Descriptor</code>s
 * to be preserved in snapshots.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public abstract class Device extends ArrayList<Descriptor> {

  // static logger
  private static final Logger log =
    Logger.getLogger(Device.class.getName());

  /**
   *  The suspension flag.
   */
  protected boolean suspended;

  /**
   * name of the element
   */
  protected String name;

  /**
   * Constructor storing the name of the element.
   *
   * @param name name of the element
   */
  public Device(final String name) {
    this.name = name;
    log.fine("New Device '" + name + "' created");
  }

  /**
   * Gets the name of the element.
   *
   * @return the name of the element
   */
  public String getName() {
    log.finer("Retrieving Device name: " + name);
    return name;
  }

  /**
   * Sets the name of the element.
   *
   * @param name the name of the element
   */
  public void setName(final String name) {
    this.name = name;
    log.finer("Device name set: " + name);
  }

  /**
   * Gets the <code>Block</code> object identified by name.
   *
   * @param  name name of the <code>Block</code> object
   * @return      the <code>Block</code> object or null if not found
   */
  public Block getBlockByName(final String name) {
    log.finer("Requesting Block '" + name + "' from Device '" +
	      this.name + "'");
    for (Descriptor descriptor: this) {
      if ((descriptor instanceof Block) &&
	  descriptor.getName().equals(name)) {
	return (Block)descriptor;
      }
    }
    log.finer("Block '" + name + "' not found");
    return null;
  }

  /**
   * Gets a list of all <code>Block</code> objects.
   *
   * @return the list of all <code>Block</code> objects
   */
  public List<Block> getBlocks() {
    log.finer("Requesting list of all blocks from Device '" + this.name + "'");
    final List<Block> list = new ArrayList<>();
    for (Descriptor descriptor: this) {
      if (descriptor instanceof Block) {
	list.add((Block)descriptor);
      }
    }
    return list;
  }

  /**
   * Gets a representation of the <code>Device</code> in 
   * a JDOM <code>Element</code>.
   *
   * @return <code>Element</code> representing the <code>Device</code>
   */
  public Element marshal() {
    log.fine("Marshalling device: " + name);
    preMarshal();
    final Element device = new Element("device");
    device.setAttribute("name", name);
    for (Descriptor descriptor: this) {
      device.addContent(descriptor.marshal());
    }
    postMarshal();
    return device;
  }

  /**
   * Method called before marshalling.
   */
  public void preMarshal() {
  }

  /**
   * Method called after marshalling.
   */
  public void postMarshal() {
  }

  /**
   * Loads a representation of the <code>Device</code> from 
   * a JDOM <code>Element</code>.
   * <p>
   * Note: The current implementation ignores devices the computer
   * does not have.  This is a controversial design decision, which
   * may be revised in the future.
   *
   * @param hardware <code>Element</code> to be loaded
   */
  public void unmarshal(final Element hardware) {
    log.fine("Unmarshalling device: " + name);
    for (Element device: hardware.getChildren()) {
      if (device.getAttributeValue("name").equals(name)) {
	preUnmarshal();
	for (Element descriptorTag: device.getChildren()) {
	  for (Descriptor descriptor: this) {
	    if (descriptor.getName().equals(descriptorTag
					    .getAttributeValue("name"))) {
	      descriptor.unmarshal(descriptorTag);
	      break;
	    }
	  }
	}
	postUnmarshal();
	break;
      }
    }
  }

  /**
   * Method called before unmarshalling.
   */
  public void preUnmarshal() {
  }

  /**
   * Method called after unmarshalling.
   */
  public void postUnmarshal() {
  }

  /**
   * Stops the element for maintenance tasks such as snapshot loading.
   */
  public void suspend() {
    suspended = true;
  }

  /**
   * Resumes the operation of the element.
   */
  public void resume() {
    suspended = false;
  }

  /**
   * Returns <code>true</code> if the element is suspended.
   *
   * @return <code>true</code> if the element is suspended
   */
  public boolean isSuspended() {
    return suspended;
  }

  /**
   * Resets the device.
   */
  public void reset() {
  }

  // // for description see Object
  // @Override
  // public boolean equals(final Object obj) {
  //   return (obj != null) && (obj instanceof Device) && (this == obj);
  // }
}
