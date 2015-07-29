/* HexField.java
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

package cz.pecina.retro.gui;

import java.util.regex.Pattern;
import java.awt.Dimension;
import javax.swing.JTextField;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

/**
 * Variable-length hexadecimal input field.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class HexField extends JTextField {

  // maximum number of characters in the field
  private int size;

  /**
   * Creates an instance of a variable-length hexadecimal input field.
   *
   * @param size maximum number of hexadecimal characters the field can hold
   */
  public HexField(final int size) {
    super(size);
    this.size = size;
    ((AbstractDocument)getDocument()).setDocumentFilter(new HexFilter());
    setMinimumSize(new Dimension((size == 2) ? 25 : 40, 17));
  }

  // filter for hexadecimal characters
  private class HexFilter extends DocumentFilter {

    private static final String hex = "[\\da-fA-F]*";

    @Override
    public void insertString(final FilterBypass fb,
			     final int offset,
			     final String string,
			     final AttributeSet attr
			     ) throws BadLocationException {
      if (Pattern.matches(hex, string) &&
	  ((fb.getDocument().getLength() + string.length()) <= size)) {
	fb.insertString(offset, string, attr);
      }
    }

    @Override
    public void replace(final FilterBypass fb,
			final int offset,
			final int length,
			final String string,
			final AttributeSet attr
			) throws BadLocationException {
      if (Pattern.matches(hex, string) &&
	  ((fb.getDocument().getLength() + string.length() - length) <= size)) {
	fb.replace(offset, length, string, attr);
      }
    }
  }

  /**
   * Gets the value of the field.
   *
   * @return    numeric value of the field
   * @exception NumberFormatException if the field is empty or invalid
   */
  public int getValue() throws NumberFormatException {
    return Integer.parseInt(getText(), 16);
  }

  /**
   * Determines if the field is empty.
   *
   * @return true of the field is empty, false otherwise
   */
  public boolean isEmpty() {
    return getText().isEmpty();
  }
}
