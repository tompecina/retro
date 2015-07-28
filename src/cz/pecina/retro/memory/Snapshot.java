/* Snapshot.java
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

package cz.pecina.retro.memory;

import java.util.logging.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
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
 * Hardware snapshot reader/writer.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Snapshot extends MemoryProcessor {

  // static logger
  private static final Logger log =
    Logger.getLogger(Snapshot.class.getName());

  // XML file version
  private static final String SNAPSHOT_XML_FILE_VERSION = "2.0";

  // name of the <bytes> tag
  private static final String subtagName = "bytes";

  // cut-off number of equal bytes
  private static final int COUNT_LIMIT = 7;

  /**
   * Creates an instance of <code>Snapshot</code> reader/writer.
   *
   * @param hardware hardware set
   */
  public Snapshot(final Hardware hardware) {
    super(hardware);
    log.fine("New Snapshot created");
  }

  /**
   * Writes a hardware shapshot to a file.
   *
   * @param file output file
   */
  public void write(final File file) {
    log.fine("Writing snapshot to a file, file: " + file.getName());
    final Element snapshot = new Element("snapshot");
    final Namespace namespace =
      Namespace.getNamespace("xsi",
			     XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
    snapshot.addNamespaceDeclaration(namespace);
    snapshot.setAttribute(
      "noNamespaceSchemaLocation",
      Application.XSD_PREFIX + "snapshot-" + SNAPSHOT_XML_FILE_VERSION + ".xsd",
      namespace);
    snapshot.setAttribute("version", SNAPSHOT_XML_FILE_VERSION);
    hardware.marshal(snapshot);
    final Document doc = new Document(snapshot);
    try (final PrintWriter writer = new PrintWriter(file)) {
      new XMLOutputter(Format.getPrettyFormat()).output(doc, writer);
    } catch (Exception exception) {
      log.fine("Error, writing failed, exception: " + exception);
      throw Application.createError(this, "XMLWrite");
    }
    log.fine("Writing completed");
  }
	
  /**
   * Reads snapshot from a file and sets hardware accordingly.
   *
   * @param file input file
   */
  public void read(final File file) {
    log.fine("Reading snapshot from a file, file: " + file.getName());
    try {
      SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
	.newSchema(new StreamSource(getClass()
	.getResourceAsStream("snapshot-" + SNAPSHOT_XML_FILE_VERSION + ".xsd")))
	.newValidator().validate(new StreamSource(file));
    } catch (Exception exception) {
      log.fine("Error, validation failed, exception: " + exception);
      throw Application.createError(this, "validation");
    }
    Document doc;
    Element snapshot;
    try {
      doc = new SAXBuilder().build(file);
    } catch (JDOMException exception) {
      log.fine("Error, parsing failed, exception: " + exception);
      throw Application.createError(this, "parsing");
    } catch (Exception exception) {
      log.fine("Error, reading failed, exception: " + exception);
      throw Application.createError(this, "XMLRead");
    }
    try {
      snapshot = doc.getRootElement();
    } catch (Exception exception) {
      log.fine("Error, parsing failed, exception: " + exception);
      throw Application.createError(this, "parsing");
    }
    if (!snapshot.getName().equals("snapshot")) {
      log.fine("Error, parsing failed, no <snapshot> tag");
      throw Application.createError(this, "parsing");
    }
    if (!SNAPSHOT_XML_FILE_VERSION
	.equals(snapshot.getAttributeValue("version"))) {
      log.fine("Version mismatch");
      throw Application.createError(this, "version");
    }
    hardware.unmarshal(snapshot);
    log.fine("Reading completed");
  }	    

  /**
   * Builds a block tag.
   *
   * @param memory       memory array
   * @param tag          tag to build
   * @param startAddress starting address
   * @param number       number of bytes
   */
  public static void buildBlockElement(final byte[] memory,
				       final Element tag,
				       final int startAddress,
				       final int number) {
    log.finer(String.format(
      "Method buildMemoryElement called: start address: %04x," +
      " number of bytes: %d",
      startAddress,
      number));
    boolean inSequence = false;
    StringBuilder data = new StringBuilder();
    Element bytes;
    for (int i = 0, j = 0; i < number;) {
      final int memoryI = memory[(startAddress + i) & 0xffff] & 0xff;
      final int remain = number - i;	
      boolean compress = false;
      if (remain >= COUNT_LIMIT) {
	compress = true;
	for (j = 0; j < COUNT_LIMIT; j++) {
	  if (memoryI != (memory[(startAddress + i + j) & 0xffff] & 0xff)) {
	    compress = false;
	    break;
	  }
	}
      }
      if (compress) {
	for (; j < remain; j++) {
	  if (memoryI != (memory[(startAddress + i + j) & 0xffff] & 0xff)) {
	    break;
	  }
	}
	if (inSequence) {
	  bytes = new Element(subtagName);
	  bytes.addContent(data.toString());
	  tag.addContent(bytes);
	  data = new StringBuilder();
	  log.finest("Data sequence closed");
	}
	inSequence = false;
	bytes = new Element(subtagName);
	bytes.setAttribute("count", String.valueOf(j));
	bytes.addContent(String.format("%02x", memoryI));
	tag.addContent(bytes);
	log.finest(String.format(
	  "Repeated data sequence written, count: %d, data: %02x",
	  j,
	  memoryI));
	i += j;
      } else {
	if (!inSequence) {
	  data = new StringBuilder();
	  log.finest("Unique data sequence started");
	}
	data.append(String.format("%02x", memoryI));
	log.finest(String.format("One byte written: %02x", memoryI));
	inSequence = true;
	i++;
      }
    }
    if (inSequence) {
      bytes = new Element(subtagName);
      bytes.addContent(data.toString());
      tag.addContent(bytes);
      log.finest("Final data sequence closed");
    }
    log.finer("Method buildMemoryElement finished");
  }

  /**
   * Processes a block tag.
   *
   * @param  memory             memory array
   * @param  tag                tag to process
   * @param  destinationAddress destination address (<code>-1</code> = none)
   * @return info               info record
   */
  public static Info processBlockElement(final byte[] memory,
					 final Element tag,
					 final int destinationAddress) {
    log.finer(String.format(
      "Method processMemoryElement called: destination address: %04x",
      destinationAddress));
    assert (destinationAddress >= -1) && (destinationAddress <= 0xffff);
    final Info info = new Info();
    int startAddress = 0;
    if (destinationAddress == -1) {
      try {
	startAddress = Integer.parseInt(tag.getAttributeValue("start"), 16);
      } catch (Exception exception) {
	log.fine("Error in starting address, exception: " + exception);
	throw Application.createError(Snapshot.class, "parsing");
      }
      log.finer(String.format("Starting address: %04x", startAddress));
    } else {
      startAddress = destinationAddress;
      log.finer(String.format("Destination address used instead: %04x",
			      startAddress));
    }
    for (Element bytes: tag.getChildren(subtagName)) {
      int count;
      String string;
      if ((string = bytes.getAttributeValue("count")) != null) {
	try {
	  count = Integer.parseInt(string);
	} catch (Exception exception) {
	  log.fine("Error in count, exception: " + exception);
	  throw Application.createError(Snapshot.class, "parsing");
	}
      } else {
	count = 1;
      }
      string = bytes.getTextTrim();
      try {
	for (; count > 0; count--) {
	  for (int i = 0; i < (string.length() / 2); i++) {
	    startAddress &= 0xffff;
	    if (startAddress < info.minAddress) {
	      info.minAddress = startAddress;
	    }
	    if (startAddress > info.maxAddress) {
	      info.maxAddress = startAddress;
	    }
	    final int dataByte = Integer.parseInt(
	      string.substring(i * 2, (i + 1) * 2), 16);
	    memory[startAddress] = (byte)dataByte;
	    log.finest(String.format("Read: %02x -> (%04x)",
				     dataByte,
				     startAddress));
	    startAddress++;
	    info.number++;
	  }
	}
      } catch (Exception exception) {
	log.fine("Error, parsing failed, exception: " + exception);
	throw Application.createError(Snapshot.class, "parsing");
      }
    }
    log.finer("Method processMemoryElement finished");
    return info;
  }
}
