/* Register.java
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
 * String-based <code>Device</code> descriptor.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public abstract class Register extends Descriptor {

  // static logger
  private static final Logger log =
    Logger.getLogger(Register.class.getName());

  /**
   * Creates an instance of the register.
   *
   * @param name name of the register
   */
  public Register(final String name) {
    super(name, "register");
    log.fine("Register '" + name + "' created");
  }

  // for description see Descriptor 
  @Override
  public Element marshal() {
    final Element register = new Element(tagName);
    register.setAttribute("name", name);
    final String value = getValue();
    register.addContent(value);
    log.fine("Register '" + name + "' holding value '" + value +
	     "' marshalled");
    return register;
  }

  // for description see Descriptor 
  @Override
  public void unmarshal(final Element descriptor) {
    assert descriptor.getName().equals(tagName);
    final String value = descriptor.getTextTrim();
    processValue(value);
    log.fine("Register '" + name + "' unmarshalled to value '" +
	     value + "'");
  }
	
  /**
   * Gets the text representation of the register value.
   *
   * @return the text representation of the register value
   */
  public abstract String getValue();
    
  /**
   * Processes the text representation of the register value.
   *
   * @param value the text representation of the register value
   */
  public abstract void processValue(String value);
}
