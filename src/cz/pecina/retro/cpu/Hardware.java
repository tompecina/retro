/* Hardware.java
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

import java.util.ArrayList;

import org.jdom2.Element;

/**
 * Collection of statful hardware elements (represented by
 * <code>Device</code> objects).
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Hardware extends ArrayList<Device> {

  // static logger
  private static final Logger log =
    Logger.getLogger(Hardware.class.getName());

  // name of the hardware set
  private String name;

  // suspension level
  private int suspension;

  /**
   * Creates a new set of <code>Device</code> objects.
   *
   * @param name name of the hardware set
   */
  public Hardware(final String name) {
    this.name = name;
    log.fine("New Hardware '" + name + "' created");
  }

  /**
   * Gets the name of the hardware set.
   *
   * @return name of the hardware set
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the <code>Device</code> object identified by name.
   *
   * @param  name name of the Device object
   * @return      the <code>Device</code> object or <code>null</code>
   *              if not found
   */
  public Device getDeviceByName(final String name) {
    log.finer("Requesting Device '" + name + "' from Hardware '" +
	      this.name + "'");
    for (Device device: this) {
      if (device.getName().equals(name)) {
	return device;
      }
    }
    log.finer("Device '" + name + "' not found");
    return null;
  }

  /**
   * Gets a representation of the <code>Hardware</code> object in 
   * a JDOM <code>Element</code>.
   *
   * @param hardware <code>Element</code> representing the
   * <code>Hardware</code> object
   */
  public void marshal(final Element hardware) {
    log.fine("Marshalling hardware");
    hardware.setAttribute("name", name);
    for (Device device: this) {
      hardware.addContent(device.marshal());
    }
  }

  /**
   * Loads a representation of the <code>Hardware</code> object from
   * a JDOM <code>Element</code>.
   *
   * @param hardware <code>Element</code> to be loaded
   */
  public void unmarshal(final Element hardware) {
    log.fine("Unmarshalling hardware");
    for (Device device: this) {
      device.unmarshal(hardware);
    }
  }

  /**
   * Suspends operation of all <code>Device</code>s.
   */
  public void suspend() {
    if (suspension++ == 0) {
      for (Device device: this) {
	device.suspend();
      }
    }
  }

  /**
   * Resumes operation of all <code>Device</code>s.
   */
  public void resume() {
    if (suspension > 0) {
      suspension--;
    }
    if (suspension == 0) {
      for (Device device: this) {
	device.resume();
      }
    }
  }

  /**
   * Resets all <code>Device</code>s.
   */
  public void reset() {
    for (Device device: this) {
      device.reset();
    }
  }
}
