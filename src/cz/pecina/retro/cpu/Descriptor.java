/* Descriptor.java
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

import org.jdom2.Element;

/**
 * {@code Device} descriptor, may be {@code Register}
 * or {@code Block}.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public abstract class Descriptor {

  // static logger
  private static final Logger log =
    Logger.getLogger(Descriptor.class.getName());

  /**
   *  Name of the {@code Descriptor}.
   */
  protected String name;

  /**
   * Name of the XML tag representing the {@code Descriptor}.
   */
  protected String tagName;

  /**
   * Creates an instance of {@code Descriptor}.
   *
   * @param name    name of the {@code Descriptor}
   * @param tagName name of the XML tag representing
   *                the {@code Descriptor}
   */
  public Descriptor(final String name, final String tagName) {
    this.name = name;
    this.tagName = tagName;
    log.fine("New Descriptor created: " + name + ", " + tagName);
  }

  /**
   * Gets the name of the {@code Descriptor}.
   *
   * @return name of the {@code Descriptor}
   */
  public String getName() {
    return name;
  }

  /**
   * Gets a representation of the {@code Descriptor} in
   * a JDOM {@code Element}.
   *
   * @return {@code Element} representing the {@code Descriptor}
   */
  public abstract Element marshal();

  /**
   * Loads a representation of the {@code Descriptor} from
   * a JDOM {@code Element}.
   *
   * @param descriptor {@code Element} to be loaded
   */
  public abstract void unmarshal(Element descriptor);
}
