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

package cz.pecina.retro.pmi80;

import java.util.logging.Logger;
import java.util.Arrays;
import java.io.File;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
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
    {"pmi80.xml", ".pmi80.xml"};

  // options
  private final Options options = new Options();

  // hardware object to operate on
  private Hardware hardware;

  // prints usage information
  private void usage() {
    final HelpFormatter usage = new HelpFormatter();
    usage.setSyntaxPrefix(Application.getString(this, "help.usage"));
    usage.printHelp("java -jar pmi80.jar [OPTION] ...", options);
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
    options.addOption(
      OptionBuilder
      .withLongOpt("usage")
      .withDescription(Application.getString(this, "option.usage"))
      .create("?"));
    options.addOption(
      OptionBuilder
      .withLongOpt("version")
      .withDescription(Application.getString(this, "option.version"))
      .create("V"));
    options.addOption(
      OptionBuilder
      .withLongOpt("language")
      .hasArg()
      .withArgName("code")
      .withDescription(Application.getString(this, "option.language"))
      .create("l"));
    options.addOption(
      OptionBuilder
      .withLongOpt("pixel-size")
      .hasArg()
      .withArgName("size")
      .withDescription(Application.getString(this, "option.pixelSize"))
      .create("p"));
    options.addOption(
      OptionBuilder
      .withLongOpt("address")
      .hasArg()
      .withArgName("addr")
      .withDescription(Application.getString(this, "option.address"))
      .create("a"));
    options.addOption(
      OptionBuilder
      .withLongOpt("start-rom")
      .hasArg()
      .withArgName("addr")
      .withDescription(Application.getString(this, "option.startRom"))
      .create("O"));
    options.addOption(
      OptionBuilder
      .withLongOpt("start-ram")
      .hasArg()
      .withArgName("addr")
      .withDescription(Application.getString(this, "option.startRam"))
      .create("A"));
    options.addOption(
      OptionBuilder
      .withLongOpt("binary")
      .hasArgs(2)
      .withArgName("file>,<addr")
      .withValueSeparator(',')
      .withDescription(Application.getString(this, "option.binary"))
      .create("b"));
    options.addOption(
      OptionBuilder
      .withLongOpt("intel-hex")
      .hasArg()
      .withArgName("file")
      .withDescription(Application.getString(this, "option.intelHex"))
      .create("h"));
    options.addOption(
      OptionBuilder
      .withLongOpt("xml")
      .hasArg()
      .withArgName("file")
      .withDescription(Application.getString(this, "option.xml"))
      .create("x"));
    options.addOption(
      OptionBuilder
      .withLongOpt("snapshot")
      .hasArg()
      .withArgName("file")
      .withDescription(Application.getString(this, "option.snapshot"))
      .create("s"));
    options.addOption(
      OptionBuilder
      .withLongOpt("write-snapshot")
      .hasArg()
      .withArgName("file")
      .withDescription(Application.getString(this, "option.writeSnapshot"))
      .create("w"));
    log.finer("Options set up");
	
    // parse the command line
    final CommandLineParser parser = new BasicParser();
    CommandLine line = null;
    try {
      line = parser.parse(options, Parameters.arguments);
    } catch (Exception exception) {
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
	} catch (Exception exception) {
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
    if (!success)
      for (String name: DEFAULT_SNAPSHOT_NAMES) {
	final File defaultSnapshot =
	  new File(new File(System.getProperty("user.home")), name);
	if (defaultSnapshot.canRead()) {
	  try {
	    new Snapshot(hardware).read(defaultSnapshot);
	  } catch (Exception exception) {
	    log.fine("Error reading default snapshot");
	    System.out.println(
	      Application.getString(this, "error.errorDefaultShapshot"));
	    error();
	  }
	  log.fine("Default snapshot read from the user's home directory");
	  break;
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
	    if (!Arrays.asList(Constants.SUPPORTED_LOCALES)
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
	    if (!Arrays.asList(Constants.PIXEL_SIZES)
		.contains(pixelSize)) {
	      System.out.println(Application.getString(
	        this, "error.unsupportedPixelSize"));
	      error();
	    }
	    UserPreferences.setPixelSize(pixelSize);
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
	  case "O":
	    log.finer("Processing -O");
	    final int startROM = Integer.parseInt(option.getValue());
	    if ((startROM < 0) || (startROM > 64)) {
	      System.out.println(Application.getString(
	        this, "error.unsupportedMemoryStart"));
	      error();
	    }
	    UserPreferences.setStartROM(startROM);
	    break;
	  case "A":
	    log.finer("Processing -A");
	    final int startRAM = Integer.parseInt(option.getValue());
	    if ((startRAM < 0) || (startRAM > 64)) {
	      System.out.println(Application.getString(
	        this, "error.unsupportedMemoryStart"));
	      error();
	    }
	    UserPreferences.setStartRAM(startRAM);
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
	}
      }
    } catch (Exception exception) {
      usage();
      error();
    }

    log.fine("New CommandLineProcessor creation completed");
  }
}
