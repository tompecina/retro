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

package cz.pecina.retro.trec;

import java.util.logging.Logger;
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
import cz.pecina.retro.common.Application;

/**
 * XML reader/writer.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class XML extends TapeProcessor {

  // static logger
  private static final Logger log =
    Logger.getLogger(XML.class.getName());

  // XML file version
  private static final String TAPE_XML_FILE_VERSION = "2.0";

  // the tape recorder interface
  private TapeRecorderInterface tapeRecorderInterface;
  
  /**
   * Creates an instance of XML format reader/writer.
   *
   * @param tape                  the tape to operate on
   * @param tapeRecorderInterface the tape recorder interface object
   */
  public XML(final Tape tape,
	     final TapeRecorderInterface tapeRecorderInterface) {
    super(tape);
    this.tapeRecorderInterface = tapeRecorderInterface;
    log.fine("New XML reader/writer created");
  }

  /**
   * Writes the tape to an XML file.
   *
   * @param file output file
   */
  public void write(final File file) {
    log.fine("Writing tape data to an XML file, file: " + file);
    final Element tag = new Element("tape");
    final Namespace namespace =
      Namespace.getNamespace("xsi",
			     XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
    tag.addNamespaceDeclaration(namespace);
    tag.setAttribute("noNamespaceSchemaLocation", Application.XSD_PREFIX +
		     "tape-" + TAPE_XML_FILE_VERSION + ".xsd", namespace);
    tag.setAttribute("version", TAPE_XML_FILE_VERSION);
    tag.setAttribute("rate", String.valueOf(tapeRecorderInterface.tapeSampleRate));
    tag.setAttribute("unit", "per sec");
    try {
      long currPos = -1;
      for (long start: tape.navigableKeySet()) {
	final long duration = tape.get(start);
	log.finest(String.format("Fetched: (%d, %d)", start, duration));
	if ((start > currPos) &&
	    (duration > 0) &&
	    ((start + duration) <= tapeRecorderInterface.getMaxTapeLength())) {
	  final Element pulse = new Element("pulse");
	  pulse.setAttribute("start", String.valueOf(start));
	  pulse.setAttribute("duration", String.valueOf(duration));
	  tag.addContent(pulse);
	  log.finest(String.format("Write: (%d, %d)", start, duration));
	  currPos = start + duration;
	}
      }
    } catch (Exception exception) {
      log.fine("Error, writing failed, exception: " + exception);
      throw Application.createError(this, "XMLWrite");
    }
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
   * Reads the tape from an XML file.
   *
   * @param file input file
   */
  public void read(final File file) {
    log.fine("Reading tape data from an XML file, file: " + file);
    try {
      SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
	.newSchema(new StreamSource(getClass()
	.getResourceAsStream("tape-" + TAPE_XML_FILE_VERSION + ".xsd")))
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
    if (!tag.getName().equals("tape")) {
      log.fine("Error, parsing failed, no <tape> tag");
      throw Application.createError(this, "noTape");
    }
    if (!TAPE_XML_FILE_VERSION.equals(tag.getAttributeValue("version"))) {
      log.fine("Version mismatch");
      throw Application.createError(this, "version");
    }
    if (!"per sec".equals(tag.getAttributeValue("unit"))) {
      log.fine("Unsupported sample rate");
      throw Application.createError(this, "XMLSampleRate");
    }
    tape.clear();
    try {
      long currPos = -1;
      for (Element pulse: tag.getChildren("pulse")) {
	final long start = Long.parseLong(pulse.getAttributeValue("start"));
	final long duration =
	  Long.parseLong(pulse.getAttributeValue("duration"));
	if ((start <= currPos) ||
	    (duration <= 0) ||
	    ((start + duration) > tapeRecorderInterface.getMaxTapeLength())) {
	  log.fine("Error in XML file");
	  throw Application.createError(this, "XML");
	}
	tape.put(start, duration);
	log.finest(String.format("Read: (%d, %d)", start, duration));
	currPos = start;
      }
    } catch (Exception exception) {
      log.fine("Error, parsing failed, exception: " + exception);
      throw Application.createError(this, "parsing");
    }
    log.fine("Reading completed");
  }    
}
