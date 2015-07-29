/* XML.java
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

package cz.pecina.retro.memory;

import java.util.logging.Logger;
import java.util.List;
import java.io.File;
import java.io.PrintWriter;
import javax.xml.XMLConstants;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.jdom2.output.Format;
import javax.xml.validation.SchemaFactory;
import javax.xml.transform.stream.StreamSource;
import cz.pecina.retro.cpu.Hardware;
import cz.pecina.retro.common.Application;

/**
 * XML reader/writer.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class XML extends MemoryProcessor {

  // static logger
  private static final Logger log = Logger.getLogger(XML.class.getName());

  // XML file version
  private static final String MEMORY_XML_FILE_VERSION = "2.0";

  /**
   * Creates an instance of XML format reader/writer.
   *
   * @param hardware              hardware set
   * @param sourceMemoryBank      source memory bank
   * @param destinationMemoryBank destination memory bank
   */
  public XML(final Hardware hardware,
	     final String sourceMemoryBank,
	     final String destinationMemoryBank) {
    super(hardware, sourceMemoryBank, destinationMemoryBank);
    log.fine("New XML created");
  }

  /**
   * Writes a memory range to a file, with wrap-around.
   *
   * @param file         output file
   * @param startAddress starting address
   * @param number       number of bytes
   */
  public void write(final File file,
		    final int startAddress,
		    final int number) {
    log.finer("Writing XML data to a file, with redirection");
    write(file, startAddress, number, startAddress);
  }

  /**
   * Writes a memory range to a file, with wrap-around.
   *
   * @param file               output file
   * @param startAddress       starting address
   * @param number             number of bytes
   * @param destinationAddress destination address
   */
  public void write(final File file,
		    final int startAddress,
		    final int number,
		    final int destinationAddress) {
    log.fine(String.format(
      "Writing XML data to a file, file: %s, start address: %04x," +
      " number of bytes: %d",
      file.getName(),
      startAddress,
      number));
    final Element tag = new Element("memory");
    Snapshot.buildBlockElement(sourceMemory, tag, startAddress, number);
    final Namespace namespace =
      Namespace.getNamespace("xsi",
			     XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
    tag.addNamespaceDeclaration(namespace);
    tag.setAttribute(
      "noNamespaceSchemaLocation",
      Application.XSD_PREFIX + "memory-" + MEMORY_XML_FILE_VERSION + ".xsd",
      namespace);
    tag.setAttribute("version", MEMORY_XML_FILE_VERSION);
    tag.setAttribute("start", String.format("%04X", destinationAddress));
    final Document doc = new Document(tag);
    try (final PrintWriter writer = new PrintWriter(file)) {
      new XMLOutputter(Format.getPrettyFormat()).output(doc, writer);
    } catch (Exception exception) {
      log.fine("Error, writing failed, exception: " + exception);
      throw Application.createError(this, "XMLWrite");
    }
    log.fine("Writing completed");
  }

  /**
   * Reads XML data from a file and stores it in memory.
   *
   * @param  file input file
   * @return info info record
   */
  public Info read(final File file) {
    log.finer("Reading XML data from a file, with redirection");
    return read(file, -1);
  }

  /**
   * Reads XML data from a file and stores it in memory.
   *
   * @param  file               input file
   * @param  destinationAddress destination address (<code>-1</code> = none)
   * @return info record
   */
  public Info read(final File file, final int destinationAddress) {
    log.fine(String.format(
      "Reading XML data from a file, file: %s, destination address: %04x",
      file.getName(),
      destinationAddress));
    try {
      SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
	.newSchema(new StreamSource(getClass()
	.getResourceAsStream("memory-" + MEMORY_XML_FILE_VERSION + ".xsd")))
	.newValidator().validate(new StreamSource(file));
    } catch (Exception exception) {
      log.fine("Error, validation failed, exception: " + exception);
      throw Application.createError(this, "validation");
    }
    Document doc;
    try {
      doc = new SAXBuilder().build(file);
    } catch (JDOMException exception) {
      log.fine("Error, parsing failed, exception: " + exception);
      throw Application.createError(this, "parsing");
    } catch (Exception exception) {
      log.fine("Error, reading failed, exception: " + exception);
      throw Application.createError(this, "XMLRead");
    }
    Element tag;
    try {
      tag = doc.getRootElement();
    } catch (Exception exception) {
      log.fine("Error, parsing failed, exception: " + exception);
      throw Application.createError(this, "parsing");
    }
    if (!tag.getName().equals("memory")) {
      log.fine("Error, parsing failed, no <memory> tag");
      throw Application.createError(this, "parsing");
    }
    if (!MEMORY_XML_FILE_VERSION.equals(tag.getAttributeValue("version"))) {
      log.fine("Version mismatch");
      throw Application.createError(this, "version");
    }
    final Info info = Snapshot.processBlockElement(destinationMemory,
						   tag,
						   destinationAddress);
    if (info.number == 0) {
      log.fine("Reading completed, with info: number: 0");
    } else {
      log.fine(String.format(
        "Reading completed, with info: number: %d, min: %04x, max: %04x",
	info.number,
	info.minAddress,
	info.maxAddress));
    }
    return info;
  }	    
}
