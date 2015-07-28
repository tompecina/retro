/* ConfirmationBox.java
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

package cz.pecina.retro.gui;

import java.awt.Component;
import java.awt.HeadlessException;
import javax.swing.JOptionPane;
import cz.pecina.retro.common.Application;

/**
 * Information dialog box.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public final class ConfirmationBox {
  
  /**
   * Displays a modal Yes/No confirmation dialog box.
   *
   * @param     parentComponent   determines the <code>Frame</code> in which
   *                              the dialog is displayed; if
   *                              <code>null</code>, or if the
   *                              <code>parentComponent</code> has no
   *                              <code>Frame</code>, a default
   *                              <code>Frame</code> is used
   * @param     message           the <code>Object</code> to display
   * @return                      an <code>int</code> indicating the option
   *                              selected by the user
   * @exception HeadlessException if
   *                              <code>GraphicsEnvironment.isHeadless</code>
   *                              returns <code>true</code>
   */
  public static int display(final Component parentComponent,
			    final Object message
			    ) throws HeadlessException {
    return JOptionPane.showConfirmDialog(
      parentComponent,
      message,
      Application.getString(ConfirmationBox.class, "confirm.title"),
      JOptionPane.YES_NO_OPTION);
  }

  // default constructor disabled
  private ConfirmationBox() {};
}
