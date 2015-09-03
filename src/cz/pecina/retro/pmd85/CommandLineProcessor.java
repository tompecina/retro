/* CommandLineProcessor.java
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

package cz.pecina.retro.pmd85;

import java.util.logging.Logger;

import java.util.Arrays;

import java.io.File;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;

import cz.pecina.retro.common.GeneralConstants;
import cz.pecina.retro.common.Parameters;
import cz.pecina.retro.common.Application;

import cz.pecina.retro.cpu.Hardware;

import cz.pecina.retro.memory.Raw;
import cz.pecina.retro.memory.IntelHEX;
import cz.pecina.retro.memory.XML;
import cz.pecina.retro.memory.Snapshot;

/**
 * Command line arguments processor.  Before processing the command
 * line options, it loads the default snapshot (if exists).
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class CommandLineProcessor {

  // static logger
  private static final Logger log =
    Logger.getLogger(CommandLineProcessor.class.getName());

  // default memory bank
  private static final String DEFAULT_BANK = "COMBINED";

  // default snapshot file name
  private static final String[] DEFAULT_SNAPSHOT_NAMES =
    {"pmd85.xml", ".pmd85.xml"};

  // options
  private final Options options = new Options();

  // hardware object to operate on
  private Hardware hardware;

  /**
   * The name of the file containing the initial ROM contents.
   */
  public static String fileNameROM;
  
  /**
   * The name of the file containing the initial ROM module contents.
   */
  public static String fileNameRMM;
  
  // prints usage information
  private void usage() {
    final HelpFormatter usage = new HelpFormatter();
    usage.setSyntaxPrefix(Application.getString(this, "help.usage"));
    usage.printHelp("java -jar pmd85.jar [OPTION] ...", options);
  }

  // exit with an error exit code
  private void error() {
    System.exit(1);
  }

  /**
   * Creates an instance of the command line procssor.
   *
   * @param hardware the hardware object to operate on
   */
  public CommandLineProcessor(final Hardware hardware) {
    log.fine("New CommandLineProcessor creation started");
    this.hardware = hardware;

    // build options
    log.fine("Building options");
    options.addOption(
      Option.builder("?")
      .longOpt("help")
      .desc(Application.getString(this, "option.help"))
      .build());
    options.addOption(
      Option.builder("V")
      .longOpt("version")
      .desc(Application.getString(this, "option.version"))
      .build());
    options.addOption(
      Option.builder("l")
      .longOpt("language")
      .hasArg()
      .argName("CODE")
      .desc(Application.getString(this, "option.language"))
      .build());
    options.addOption(
      Option.builder("p")
      .longOpt("pixel-size")
      .hasArg()
      .argName("SIZE")
      .desc(Application.getString(this, "option.pixelSize"))
      .build());
    options.addOption(
      Option.builder("a")
      .longOpt("address")
      .hasArg()
      .argName("ADDR")
      .desc(Application.getString(this, "option.address"))
      .build());
    options.addOption(
      Option.builder("o")
      .longOpt("ROM-file")
      .hasArg()
      .argName("FILE")
      .desc(Application.getString(this, "option.ROM"))
      .build());
    options.addOption(
      Option.builder("m")
      .longOpt("RMM-file")
      .hasArg()
      .argName("FILE")
      .desc(Application.getString(this, "option.RMM"))
      .build());
    options.addOption(
      Option.builder("b")
      .longOpt("binary")
      .hasArgs()
      .numberOfArgs(2)
      .argName("FILE>,<ADDR")
      .valueSeparator(',')
      .desc(Application.getString(this, "option.binary"))
      .build());
    options.addOption(
      Option.builder("h")
      .longOpt("intel-hex")
      .hasArg()
      .argName("FILE")
      .desc(Application.getString(this, "option.intelHex"))
      .build());
    options.addOption(
      Option.builder("x")
      .longOpt("xml")
      .hasArg()
      .argName("FILE")
      .desc(Application.getString(this, "option.xml"))
      .build());
    options.addOption(
      Option.builder("s")
      .longOpt("snapshot")
      .hasArg()
      .argName("FILE")
      .desc(Application.getString(this, "option.snapshot"))
      .build());
    options.addOption(
      Option.builder("w")
      .longOpt("write-snapshot")
      .hasArg()
      .argName("FILE")
      .desc(Application.getString(this, "option.writeSnapshot"))
      .build());
    options.addOption(
      Option.builder("S")
      .longOpt("speed-up")
      .hasArg()
      .argName("FACTOR")
      .desc(Application.getString(this, "option.speedUp"))
      .build());
    options.addOption(
      Option.builder("g")
      .longOpt("opengl")
      .desc(Application.getString(this, "option.openGL"))
      .build());
    options.addOption(
      Option.builder("G")
      .longOpt("no-opengl")
      .desc(Application.getString(this, "option.noOpenGL"))
      .build());
    log.finer("Options set up");
	
    // parse the command line
    final CommandLineParser parser = new DefaultParser();
    CommandLine line = null;
    try {
      line = parser.parse(options, Parameters.arguments);
    } catch (final Exception exception) {
      usage();
      error();
    }
    log.finer("Command line parsed");

    // load default snapshot if exists
    boolean success = false;
    for (String name: DEFAULT_SNAPSHOT_NAMES) {
      final File defaultSnapshot = new File(name);
      if (defaultSnapshot.canRead()) {
	try {
	  new Snapshot(hardware).read(defaultSnapshot);
	} catch (final RuntimeException exception) {
	  log.fine("Error reading default snapshot");
	  System.out.println(
	    Application.getString(this, "error.errorDefaultShapshot"));
	  error();
	}
	log.fine("Default snapshot read from the current directory");
	success = true;
	break;
      }
    }
    if (!success) {
      for (String name: DEFAULT_SNAPSHOT_NAMES) {
	final File defaultSnapshot =
	  new File(new File(System.getProperty("user.home")), name);
	if (defaultSnapshot.canRead()) {
	  try {
	    new Snapshot(hardware).read(defaultSnapshot);
	  } catch (final Exception exception) {
	    log.fine("Error reading default snapshot");
	    System.out.println(
	      Application.getString(this, "error.errorDefaultShapshot"));
	    error();
	  }
	  log.fine("Default snapshot read from the user's home directory");
	  break;
	}
      }
    }
    log.finer("Default files processed");

    // process the options
    if (!line.getArgList().isEmpty()) {
      usage();
      error();
    }
    if (line.hasOption("?")) {
      log.finer("Processing -?");
      usage();
      System.exit(0);
    }
    if (line.hasOption("V")) {
      log.finer("Processing -V");
      System.out.println(Application.getString(this, "longAppName") +
        " " + Application.getString(this, "version") + " @VERSION@");
      System.exit(0);
    }
    try {
      for (Option option: line.getOptions()) {
	switch (option.getOpt()) {
	  case "l":
	    log.finer("Processing -l");
	    final String language = option.getValue();
	    if (!Arrays.asList(GeneralConstants.SUPPORTED_LOCALES)
		.contains(language)) {
	      System.out.println(Application.getString(
	        this, "error.unsupportedLanguage"));
	      error();
	    }
	    UserPreferences.setLocale(language);
	    break;
	  case "p":
	    log.finer("Processing -p");
	    final int pixelSize = Integer.parseInt(option.getValue());
	    if (!Arrays.asList(GeneralConstants.PIXEL_SIZES)
		.contains(pixelSize)) {
	      System.out.println(Application.getString(
	        this, "error.unsupportedPixelSize"));
	      error();
	    }
	    UserPreferences.setPixelSize(pixelSize);
	    break;
	  case "o":
	    log.finer("Processing -o");
	    fileNameROM = option.getValue();
	    break;
	  case "m":
	    log.finer("Processing -m");
	    fileNameRMM = option.getValue();
	    break;
	  case "a":
	    log.finer("Processing -a");
	    int address = Integer.parseInt(option.getValue(), 16);
	    if ((address < 0) || (address >= 0x10000)) {
	      System.out.println(Application.getString(
	        this, "error.invalidAddress"));
	      error();
	    }
	    Parameters.cpu.setPC(address);
	    break;
	  case "b":
	    log.finer("Processing -b");
	    File file = new File(option.getValue(0));
	    address = Integer.parseInt(option.getValue(1), 16);
	    if ((address < 0) || (address >= 0x10000)) {
	      System.out.println(Application.getString(
	        this, "error.invalidAddress"));
	      error();
	    }
	    new Raw(hardware, DEFAULT_BANK, DEFAULT_BANK).read(file, address);
	    break;
	  case "h":
	    log.finer("Processing -h");
	    file = new File(option.getValue());
	    new IntelHEX(hardware, DEFAULT_BANK, DEFAULT_BANK).read(file);
	    break;
	  case "x":
	    log.finer("Processing -x");
	    file = new File(option.getValue());
	    new XML(hardware, DEFAULT_BANK, DEFAULT_BANK).read(file);
	    break;
	  case "s":
	    log.finer("Processing -s");
	    file = new File(option.getValue());
	    new Snapshot(hardware).read(file);
	    break;
	  case "w":
	    log.finer("Processing -w");
	    file = new File(option.getValue());
	    new Snapshot(hardware).write(file);
	    System.exit(0);
	    break;
	  case "S":
	    log.finer("Processing -S");
	    Parameters.speedUp = Integer.parseInt(option.getValue());
	    if (Parameters.speedUp < 1) {
	      System.out.println(Application.getString(
	        this, "error.nonPositiveSpeedUp"));
	      error();
	    }
	    break;
	  case "g":
	    log.finer("Processing -g");
	    Parameters.openGL = true;
	    break;
	  case "G":
	    log.finer("Processing -G");
	    Parameters.openGL = false;
	    break;
	}
      }
    } catch (final Exception exception) {
      usage();
      error();
    }

    log.fine("New CommandLineProcessor creation completed");
  }
}
