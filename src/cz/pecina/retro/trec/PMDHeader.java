/* PMDHeader.java
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
import java.util.List;
import java.util.ArrayList;
import cz.pecina.retro.common.Application;

/**
 * PMD 85 header file.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class PMDHeader {

  // static logger
  private static final Logger log =
    Logger.getLogger(PMDHeader.class.getName());

  // file number
  private int fileNumber;

  // file type
  private int fileType;

  // start address
  private int startAddress;

  // body length
  private int bodyLength;

  // file name
  private String fileName;

  // list of bytes in the header
  private final List<Byte> bytes = new ArrayList<>();
  
  // check leader
  private boolean checkLeader(final List<Byte> list,
			      final int start,
			      final int length,
			      final byte value) {
    for (int i = 0; i < length; i++) {
      if (list.get(start + i) != value) {
	return false;
      }
    }
    return true;
  }

  /**
   * Constructs a new PMD 85 tape header.
   *
   * @param list   list of input data
   * @param offset offset in the list
   * @param offset length of bytes available in the list, counting
   *               from <code>offset</code>
   */
  public PMDHeader(final List<Byte> list,
		   final int offset,
		   final int length
		   ) throws TapeException {
    log.fine("Creating new PMD tape header");
    
    if (length < 63) {
      throw new TapeException(Application
        .getString(this, "error.PMDTAPERead.notEnoughData"));
    }
    log.finest("Length ok");

    for (int i = 0; i < 63; i++) {
      bytes.add(list.get(offset + i));
    }
    log.finest("Copy of header made and saved");
    
    if (!(checkLeader(list, offset + 0, 0x10, (byte)0xff) &&
	  checkLeader(list, offset + 0x10, 0x10, (byte)0x00) &&
	  checkLeader(list, offset + 0x20, 0x10, (byte)0x55))) {
      throw new TapeException(Application
        .getString(this, "error.PMDTAPERead.notEnoughData"));
    }
    log.finest("Leader ok");
    
    fileNumber = list.get(offset + 0x30) & 0xff;
    if (fileNumber > 99) {
      throw new TapeException(Application
        .getString(this, "error.PMDTAPERead.notEnoughData"));
    }
    log.finest("File number: " + fileNumber);

    fileType = list.get(offset + 0x31) & 0xff;
    log.finest("File type: " + fileType);
    
    startAddress = ((list.get(offset + 0x33) & 0xff) << 8) +
      (list.get(offset + 0x32) & 0xff);
    log.finest(String.format("Start address: 0x%04x", startAddress));

    bodyLength = ((list.get(offset + 0x35) & 0xff) << 8) +
      (list.get(offset + 0x34) & 0xff) + 1;
    log.finest(String.format("File length: 0x%04x", bodyLength));
    
    final StringBuilder s = new StringBuilder();;
    for (int i = 0x36; i < 0x3e; i++) {
      s.append((char)(list.get(offset + i) & 0xff));
    }
    fileName = s.toString().trim();
    log.finest("File name: " + fileName);
    
    if (PMD.checkSum(list, offset + 0x30, 0x0e) != list.get(offset + 0x3e)) {
      throw new TapeException(Application
        .getString(this, "error.PMDTAPERead.notEnoughData"));
    }
    log.finest("Checksum ok");
    
    log.finer("New PMD tape header set up");
  }

  /**
   * Gets the file number.
   *
   * @return the file number, 0-99
   */
  public int getFileNumber() {
    return fileNumber;
  }

  /**
   * Gets the file type.
   *
   * @return the file type
   */
  public int getFileType() {
    return fileType;
  }

  /**
   * Gets the starting address
   *
   * @return the starting address
   */
  public int getStartAddress() {
    return startAddress;
  }

  /**
   * Gets the body length
   *
   * @return the body length
   */
  public int getBodyLength() {
    return bodyLength;
  }

  /**
   * Gets the file name
   *
   * @return the file name
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * Gets the list of bytes in the header
   *
   * @return the list of bytes
   */
  public List<Byte> getBytes() {
    return bytes;
  }
}
