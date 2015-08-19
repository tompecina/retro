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

  // file length
  private int fileLength;

  // file name
  private String fileName;

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

  // check sum
  private byte checkSum(final List<Byte> list,
			final int start,
			final int length) {
    byte s = 0;
    for (int i = 0; i < length; i++) {
      s += list.get(start + i) & 0xff;
    }
    return s;
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
		   final int length)
    throws TapeException {
    log.fine("Creating new PMD tape header");
    
    if (length < 63) {
      throw new TapeException(Application
        .getString(this, "PMDTAPERead.notEnoughData"));
    }
    if (!(checkLeader(list, offset + 0, 0x10, (byte)0xff) &&
	  checkLeader(list, offset + 0x10, 0x10, (byte)0x00) &&
	  checkLeader(list, offset + 0x20, 0x10, (byte)0x55))) {
      throw new TapeException(Application
        .getString(this, "PMDTAPERead.notEnoughData"));
    }
    
    fileNumber = list.get(offset + 0x30) & 0xff;
    if (fileNumber > 99) {
      throw new TapeException(Application
        .getString(this, "PMDTAPERead.notEnoughData"));
    }

    fileType = list.get(offset + 0x31) & 0xff;
    
    startAddress = ((list.get(offset + 0x33) & 0xff) << 8) +
      (list.get(offset + 0x32) & 0xff);
    
    fileLength = ((list.get(offset + 0x35) & 0xff) << 8) +
      (list.get(offset + 0x34) & 0xff) + 1;
    
    final StringBuilder s = new StringBuilder();;
    for (int i = 0x36; i < 0x3e; i++) {
      s.append((char)list.get(offset + 0x34));
    }
    fileName = s.toString().trim();
    
    if (checkSum(list, offset + 0x30, 0x0e) != list.get(offset + 0x3f)) {
      throw new TapeException(Application
        .getString(this, "PMDTAPERead.notEnoughData"));
    }
    
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
   * Gets the file length
   *
   * @return the file length
   */
  public int getFileLength() {
    return fileLength;
  }
  /**
   * Gets the file name
   *
   * @return the file name
   */
  public String getFileName() {
    return fileName;
  }
}
