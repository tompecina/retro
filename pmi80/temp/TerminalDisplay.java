/* TerminalDisplay.java
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
 **/

package cz.pecina.retro.pmi80;

import java.util.ArrayList;
import java.util.Scanner;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import javax.swing.JComponent;
import javax.swing.JScrollBar;

public class TerminalDisplay extends JComponent {
  private static final Color GREEN = new Color(0x80ff80);
  private static final int REVERSE_QM = -0x20;
  private TerminalCell[][] cells =
    new TerminalCell[Constants.NUMBER_TERMINAL_LINES]
    [Constants.NUMBER_TERMINAL_COLUMNS];
  private TerminalLine[] lines =
    new TerminalLine[Constants.NUMBER_TERMINAL_LINES];
  private TerminalColumn[] columns =
    new TerminalColumn[Constants.NUMBER_TERMINAL_COLUMNS];
  private int cursorLine, cursorColumn;
  private int savedCursorLine = -1, savedCursorColumn;
  private int topMargin, bottomMargin = Constants.NUMBER_TERMINAL_LINES - 1;
  private ArrayList<Integer> esc, cs, dcs;
  private int[] charsetG = {TerminalFont.CHARSET_ASCII,
			    TerminalFont.CHARSET_ASCII,
			    TerminalFont.CHARSET_SUPPLEMENTAL,
			    TerminalFont.CHARSET_SUPPLEMENTAL};
  private int lockGL = 0, lockGR = 2, singleGL = -1;
  private boolean modeKAM, modeIRM, modeSRM, modeLNM, modeS7C1T;
  private boolean modeDECCKM, modeDECANM, modeDECCOLM, modeDECSCLM,
    modeDECSCNM, modeDECOM, modeDECAWM = true, modeDECARM, modeDECPFF,
    modeDECPEX, modeDECTCEM = true, modeDECNRCM, modeDECKPAM;
  private boolean bold, underscored, blinking, reverse;

  public TerminalDisplay() {
    int line, column;
    for (line = 0; line < Constants.NUMBER_TERMINAL_LINES; line++) {
      lines[line] = new TerminalLine();
      for (column = 0; column < Constants.NUMBER_TERMINAL_COLUMNS; column++) {
	cells[line][column] = new TerminalCell();
      }
    }
    for (column = 0; column < Constants.NUMBER_TERMINAL_COLUMNS; column++) {
      columns[column] = new TerminalColumn();
    }
    resetTabStops();
  }

  private void resetTabStops() {
    for (int column = 0; column < Constants.NUMBER_TERMINAL_COLUMNS; column++) {
      columns[column].setTabStop(((column % 8) == 7) ||
        (column > (Constants.NUMBER_TERMINAL_COLUMNS - 9)));
    }
  }

  private int b2i(boolean b) {
    return b ? 1 : 0;
  }

  private boolean i2b(int n) {
    return n != 0;
  }

  public void paint(Graphics canvas) {
    TerminalCell cell;
    TerminalLine cellLine;
    for (int line = 0; line < Constants.NUMBER_TERMINAL_LINES; line++) {
      for (int column = 0;
	   column < Constants.NUMBER_TERMINAL_COLUMNS;
	   column++) {
	cell = cells[line][column];
	cellLine = lines[line];
	TerminalFont.paintGlyph(
	  canvas,
	  cell.getCode(),
	  false,
	  GREEN,
	  Color.BLACK,
	  column * 10,
	  line * 20,
	  1,
	  1,
	  i2b(b2i(modeDECSCNM) ^ b2i(cell.isReverse()) ^ b2i(modeDECTCEM &&
	    (line == cursorLine) && (column == cursorColumn))),
	  cell.isBold(),
	  cell.isUnderscored(),
	  cellLine.isDoubleWidth(),
	  cellLine.isDoubleHeight(),
	  ((column & 1) == 0),
	  cellLine.isBottomHalf());
      }
    }
  }

  private void scrollUp() {
    int line, column;
    for (line = topMargin; line < bottomMargin; line++) {
      for (column = 0; column < Constants.NUMBER_TERMINAL_COLUMNS; column++) {
	cells[line][column] = cells[line + 1][column];
      }
      lines[line] = lines[line + 1];
    }
    for (column = 0; column < Constants.NUMBER_TERMINAL_COLUMNS; column++) {
      cells[line][column] = new TerminalCell();
    }
    lines[line] = new TerminalLine();
  }

  private void scrollDown() {
    int line, column;
    for (line = bottomMargin; line > topMargin; line--) {
      for (column = 0; column < Constants.NUMBER_TERMINAL_COLUMNS; column++) {
	cells[line][column] = cells[line - 1][column];
      }
      lines[line] = lines[line - 1];
    }
    for (column = 0; column < Constants.NUMBER_TERMINAL_COLUMNS; column++) {
      cells[line][column] = new TerminalCell();
    }
    lines[line] = new TerminalLine();
  }

  private String a2s(ArrayList<Integer> l) {
    String string = new String();
    for (int ch: cs) {
      string += (char)ch;
    }
    return string;
  }

  private int a2i(ArrayList<Integer> l) {
    if ((l == null) || (l.isEmpty())) {
      return 1;
    }
    try {
      int r = Integer.parseInt(a2s(l));
      return (r > 0) ? r : 1;
    } catch (Exception exception) {
      return 1;
    }
  }

  public void output(int ch) {
    int line, column, charset;
    Scanner scanner;
    switch (ch) {
      case ASCII.NUL:
      case ASCII.ENQ:
      case ASCII.BEL:
      case ASCII.DC1:
      case ASCII.DC3:		
      case ASCII.DEL:
	break;
      case ASCII.SO:  // LS1
	lockGL = 1;
	break;
      case ASCII.SI:  // LS0
	lockGL = 0;
	break;
      case ASCII.SS2:  // SS2
	singleGL = 2;
	break;
      case ASCII.SS3:  // SS3
	singleGL = 3;
	break;
      case ASCII.BS:
	if (cursorColumn > 0) {
	  cursorColumn--;
	  repaint();
	}
	break;
      case ASCII.HT:
	while ((cursorColumn < Constants.NUMBER_TERMINAL_COLUMNS) &&
	       !columns[++cursorColumn].isTabStop());
	repaint();
	break;
      case ASCII.LF:
      case ASCII.FF:
      case ASCII.VT:
	if (modeLNM) {
	  output(ASCII.NEL);
	} else {
	  output(ASCII.IND);
	}
	break;
      case ASCII.CR:
	cursorColumn = 0;
	repaint();
	break;
      case ASCII.CAN:
	esc = cs = dcs = null;
	break;
      case ASCII.SUB:
	if ((esc != null) || (cs != null) || (dcs != null)) {
	  esc = cs = dcs = null;
	  output(REVERSE_QM);
	}
	break;
      case ASCII.ESC:
	esc = new ArrayList<>();
	break;
      case ASCII.IND:
	if (++cursorLine > bottomMargin) {
	  cursorLine--;
	  scrollUp();
	}
	repaint();
	break;
      case ASCII.NEL:
	cursorColumn = 0;
	output(ASCII.IND);
	break;
      case ASCII.HTS:
	columns[cursorColumn].setTabStop(true);
	break;
      case ASCII.RI:
	if (--cursorLine < topMargin) {
	  cursorLine++;
	  scrollDown();
	}
	repaint();
	break;
      case ASCII.DCS:
	dcs = new ArrayList<>();
	break;
      case ASCII.CSI:
	cs = new ArrayList<>();
	break;
      case ASCII.ST:
	dcs = null;
	break;
      default:
	if (esc != null) {
	  if (esc.isEmpty()) {
	    if ((ch >= 0x40) && (ch <= 0x5f)) {
	      esc = null;
	      output(ch + 0x40);
	      break;
	    }
	    switch ((char)ch) {
	      case '~':  // LS1R
		lockGR = 1;
		esc = null;
		break;
	      case 'n':  // LS2
		lockGL = 2;
		esc = null;
		break;
	      case '}':  // LS2R
		lockGR = 2;
		esc = null;
		break;
	      case 'o':  // LS3
		lockGL = 3;
		esc = null;
		break;
	      case '|':  // LS3R
		lockGR = 3;
		esc = null;
		break;
	      case ' ':
	      case '(':
	      case ')':
	      case '*':
	      case '+':
	      case '#':
		esc.add(ch);
		break;
	      case '=':
	      case '>':
		modeDECKPAM = ((char)ch == '=');
		esc = null;
		break;
	      case '7':
		savedCursorLine = cursorLine;
		savedCursorColumn = cursorColumn;
		esc = null;
		break;
	      case '8':
		if (savedCursorLine == -1)
		  cursorLine = cursorColumn = 0;
		else {
		  cursorLine = savedCursorLine;
		  cursorColumn = savedCursorColumn;
		}
		esc = null;
		repaint();
		break;
	      case 'c':
		for (line = topMargin;
		     line < Constants.NUMBER_TERMINAL_LINES;
		     line++) {
		  for (column = 0;
		       column < Constants.NUMBER_TERMINAL_COLUMNS;
		       column++) {
		    cells[line][column] = new TerminalCell();
		  }
		}
		cursorLine = cursorColumn = 0;
		savedCursorLine = -1;
		topMargin = 0;
		bottomMargin = Constants.NUMBER_TERMINAL_LINES - 1;
		esc = cs = dcs = null;
		repaint();
		break;
	      default:
		esc = null;
		break;
	    }
	    break;
	  } else if (esc.size() == 1) {
	    switch ((char)(int)esc.get(0)) {
	      case '(':  // G0
	      case ')':  // G1
	      case '*':  // G2
	      case '+':  // G3
		switch ((char)ch) {
		  default:
		  case 'B':  // ASCII
		    charset = TerminalFont.CHARSET_ASCII;
		    break;
		  case '<':  // DEC supplemental
		    charset = TerminalFont.CHARSET_SUPPLEMENTAL;
		    break;
		  case '0':  // DEC special graphics
		    charset = TerminalFont.CHARSET_GRAPHICS;
		    break;
		  case 'A':  // NRC British
		    charset = TerminalFont.CHARSET_BRITISH;
		    break;
		  case '4':  // NRC Dutch
		    charset = TerminalFont.CHARSET_DUTCH;
		    break;
		  case 'C':
		  case '5':  // NRC Finnish
		    charset = TerminalFont.CHARSET_FINNISH;
		    break;
		  case 'R':  // NRC French
		    charset = TerminalFont.CHARSET_FRENCH;
		    break;
		  case 'Q':  // NRC French Canadian
		    charset = TerminalFont.CHARSET_FRENCH_CANADIAN;
		    break;
		  case 'K':  // NRC German
		    charset = TerminalFont.CHARSET_GERMAN;
		    break;
		  case 'Y':  // NRC Italian
		    charset = TerminalFont.CHARSET_ITALIAN;
		    break;
		  case 'E':
		  case '6':  // NRC Norwegian/Danish
		    charset = TerminalFont.CHARSET_NORWEGIAN_DANISH;
		    break;
		  case 'Z':  // NRC Spanish
		    charset = TerminalFont.CHARSET_SPANISH;
		    break;
		  case 'H':
		  case '7':  // NRC Swedish
		    charset = TerminalFont.CHARSET_SWEDISH;
		    break;
		  case '=':  // NRC Swiss
		    charset = TerminalFont.CHARSET_SWISS;
		    break;
		}
		charsetG[esc.get(0) & 3] = charset;
		esc = null;
		break;
	      case ' ':
		if ((char)ch == 'F') { // S7C1T
		  modeS7C1T = true;
		} else if ((char)ch == 'G') {  // S8C1T
		  modeS7C1T = false;
		}
		esc = null;
		break;
	      case '#':
		switch ((char)ch) {
		  case '3':
		  case '4':
		  case '5':
		  case '6':
		    esc = null;
		    break;
		  case '8':
		    for (line = topMargin;
			 line < Constants.NUMBER_TERMINAL_LINES;
			 line++) {
		      for (column = 0;
			   column < Constants.NUMBER_TERMINAL_COLUMNS;
			   column++) {
			cells[line][column] =
			  new TerminalCell((int)'E',
					   false,
					   false,
					   false,
					   false);
		      }
		    }
		    cursorLine = cursorColumn = 0;
		    esc = null;
		    repaint();
		    break;
		}
				
	      default:
		esc = null;
		break;
	    }
	  }
	} else if (cs != null) {
	  if ((ch >= 0x20) && (ch <= 0x3f)) {
	    cs.add(ch);
	  } else {
	    switch ((char)ch) {
	      case 'p':  // DECSCL
		cs = null;
		break;
	      case 'h':  // SM
	      case 'l':  // RM
		if (!cs.isEmpty()) {
		  boolean set = ((char)ch == 'h');
		  boolean privateMode = ((char)(int)cs.get(0) == '?');
		  if (privateMode) {
		    cs.remove(0);
		  }
		  scanner = new Scanner(a2s(cs)).useDelimiter("\\s*;\\s*");
		  while (scanner.hasNextInt()) {
		    if (privateMode) {
		      switch (scanner.nextInt()) {
			case 1:
			  modeDECCKM = set;
			  break;
			case 2:	
			  if (!set)
			    modeDECANM = false;
			  break;
			case 3:
			  modeDECCOLM = set;
			  break;
			case 4:
			  modeDECSCLM = set;
			  break;
			case 5:
			  modeDECSCNM = set;
			  repaint();
			  break;
			case 6:
			  modeDECOM = set;
			  break;
			case 7:
			  modeDECAWM = set;
			  break;
			case 8:
			  modeDECARM = set;
			  break;
			case 18:
			  modeDECPFF = set;
			  break;
			case 19:
			  modeDECPEX = set;
			  break;
			case 25:
			  modeDECTCEM = set;
			  break;
			case 42:
			  modeDECNRCM = set;
			  break;
			default:
			  break;
		      }
		    } else {
		      switch (scanner.nextInt()) {
			case 2:
			  modeKAM = set;
			  break;
			case 4:
			  modeIRM = set;
			  break;
			case 12:
			  modeSRM = set;
			  break;
			case 20:
			  modeLNM = set;
			  break;
			default:
			  break;
		      }
		    }
		  }
		}
		break;
	      case 'A':  // CUU
		cursorLine -= a2i(cs);
		if (cursorLine < topMargin) {
		  cursorLine = topMargin;
		}
		repaint();
		break;
	      case 'B':  // CUD
		cursorLine += a2i(cs);
		if (cursorLine > bottomMargin) {
		  cursorLine = bottomMargin;
		}
		repaint();
		break;
	      case 'C':  // CUF
		cursorColumn += a2i(cs);
		if (cursorColumn >= Constants.NUMBER_TERMINAL_COLUMNS) {
		  cursorColumn = Constants.NUMBER_TERMINAL_COLUMNS - 1;
		}
		repaint();
		break;
	      case 'D':  // CUB
		cursorColumn -= a2i(cs);
		if (cursorColumn < 0) {
		  cursorColumn = 0;
		}
		repaint();
		break;
	      case 'H':  // CUP
	      case 'f':  // HVP
		scanner = new Scanner(a2s(cs)).useDelimiter("\\s*;\\s*");
		try {
		  cursorLine = scanner.nextInt();
		  if (cursorLine < topMargin) {
		    cursorLine = topMargin;
		  }
		  if (cursorLine > bottomMargin) {
		    cursorLine = bottomMargin;
		  }
		  cursorColumn = scanner.nextInt();
		  if (cursorColumn < 0) {
		    cursorColumn = 0;
		  }
		  if (cursorColumn >= Constants.NUMBER_TERMINAL_COLUMNS) {
		    cursorColumn = Constants.NUMBER_TERMINAL_COLUMNS - 1;
		  }
		} catch (Exception exception) {
		}
		repaint();
		break;
	      case 'g':  // TBC
		if (cs.isEmpty() || ((char)(int)cs.get(0) == '0')) {
		  columns[cursorColumn].setTabStop(false);
		} else if ((char)(int)cs.get(0) == '3') {
		  for (column = 0;
		       column < Constants.NUMBER_TERMINAL_COLUMNS;
		       column++) {
		    columns[cursorColumn].setTabStop(false);
		  }
		}
		break;
	      case 'm':  // SGR
		if (!cs.isEmpty()) {
		  scanner = new Scanner(a2s(cs)).useDelimiter("\\s*;\\s*");
		  while (scanner.hasNextInt()) {
		    switch (scanner.nextInt()) {
		      case 0:
			bold = underscored = blinking = reverse = false;
			break;
		      case 1:
			bold = true;
			break;
		      case 4:
			underscored = true;
			break;
		      case 5:
			blinking = true;
			break;
		      case 7:
			reverse = true;
			break;
		      case 22:
			bold = false;
			break;
		      case 24:
			underscored = false;
			break;
		      case 25:
			blinking = false;
			break;
		      case 27:
			reverse = false;
			break;
		    }
		  }
		}
		break;
	    }
	    cs = null;
	  }
	  break;
	} else {
	  if ((ch < 0) && (-ch < TerminalFont.NUMBER_GLYPHS)) {
	    ch = -ch;
	  } else if (ch == 0x20) {
	    ch = 0;
	  } else if ((ch > 0x20) && (ch < 0x7f)) {
	    charset = (singleGL == -1) ? lockGL : singleGL;
	    ch = TerminalFont.charsetMapping[charsetG[charset]]
	      [ch - TerminalFont.CHARSET_OFFSET];
	  } else if ((ch > 0xa0) && (ch < 0xff)) {
	    ch = TerminalFont.charsetMapping[charsetG[lockGR]]
	      [ch - TerminalFont.CHARSET_OFFSET];
	  } else {
	    ch = -1;
	  }
	  singleGL = -1;
	  if ((ch >= 0) && (ch < TerminalFont.NUMBER_GLYPHS)) {
	    if (modeIRM) {
	      for (column = Constants.NUMBER_TERMINAL_COLUMNS;
		   column > cursorColumn;
		   column--) {
		cells[cursorLine][column] = cells[cursorLine][column - 1];
	      }
	    }
	    cells[cursorLine][cursorColumn] =
	      new TerminalCell(ch, bold, underscored, blinking, reverse);
	    if ((++cursorColumn == Constants.NUMBER_TERMINAL_COLUMNS) &&
		modeDECAWM) {
	      cursorColumn = 0;
	      if (++cursorLine > bottomMargin) {
		bottomMargin--;
		scrollUp();
	      }
	    }
	    repaint();
	    break;
	  }
	  break;
	}
    }
  }
}
