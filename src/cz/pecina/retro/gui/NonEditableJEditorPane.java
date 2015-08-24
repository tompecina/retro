/* NonEditableJEditorPane.java
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

import java.util.logging.Logger;

import java.net.URL;

import javax.swing.JEditorPane;

import java.io.IOException;

/**
 * {@code JEditorPane} with editing disabled.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class NonEditableJEditorPane extends JEditorPane {

  // static logger
  private static final Logger log =
    Logger.getLogger(NonEditableJEditorPane.class.getName());

  /**
   * Creates an instance of {@code JEditorPane} with editing disabled.
   */
  public NonEditableJEditorPane() {
    super();
    setEditable(false);
    log.fine("New NonEditableJEditorPane created");
  }

  /**
   * Creates an instance of {@code JEditorPane} with editing disabled.
   *
   * @param  initialPage the URL
   * @throws IOException if the URL is {@code null} or cannot
   *                     be accessed
   */
  public NonEditableJEditorPane(final URL initialPage) throws IOException {
    super(initialPage);
    setEditable(false);
    log.fine("New NonEditableJEditorPane created");
  }

  /**
   * Creates an instance of {@code JEditorPane} with editing disabled.
   *
   * @param  url         the URL
   * @throws IOException if the URL is {@code null} or cannot
   *                     be accessed
   */
  public NonEditableJEditorPane(final String url) throws IOException {
    super(url);
    setEditable(false);
    log.fine("New NonEditableJEditorPane created");
  }

  /**
   * Creates an instance of {@code JEditorPane} with
   * editing disabled.
   *
   * @param  type                 mime type of the given text
   * @param  text                 the text to initialize with;
   *                              may be {@code null}
   * @throws NullPointerException if the type parameter is
   *                              {@code null}
   */
  public NonEditableJEditorPane(final String type, final String text) {
    super(type, text);
    setEditable(false);
    log.fine("New NonEditableJEditorPane created");
  }
}
