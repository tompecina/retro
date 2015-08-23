/* Block.java
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
 * Block-based <code>Device</code> descriptor.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public abstract class Block extends Descriptor {

  // static logger
  private static final Logger log =
    Logger.getLogger(Block.class.getName());

  /**
   * Creates an instance of the block.
   *
   * @param name name of the block
   */
  public Block(final String name) {
    super(name, "block");
    log.fine("Block '" + name + "' created");
  }

  // for description see Descriptor
  @Override
  public Element marshal() {
    final Element block = new Element(tagName);
    block.setAttribute("name", name);
    getContent(block);
    log.fine("Block '" + name + "' retrieved");
    return block;
  }

  // for description see Descriptor 
  @Override
  public void unmarshal(final Element block) {
    assert block.getName().equals(tagName);
    processContent(block);
    log.fine("Block '" + name + "' processed");
  }
	
  /**
   * Gets block data as a byte array.
   *
   * @return the block data as a byte array
   */
  public abstract byte[] getMemory();
    
  /**
   * Gets block data in XML.
   *
   * @param block the <code>Element</code> tag
   */
  public abstract void getContent(Element block);
    
  /**
   * Processes block data in XML.
   *
   * @param block the <code>Element</code> tag
   */
  public abstract void processContent(Element block);
}
