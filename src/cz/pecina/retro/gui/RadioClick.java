/* RadioClick.java
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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JRadioButton;

/**
 * Selects a radio button on a mouse click.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class RadioClick extends MouseAdapter {

  // the radio button to be selected
  private JRadioButton radioButton;

  /**
   * Creates a MouseListener instance that selects a radio button
   * on a mouse click.
   *
   * @param radioButton the radio button to be selected
   */
  public RadioClick(final JRadioButton radioButton) {
    super();
    this.radioButton = radioButton;
  }

  // for description see MouseAdapter
  @Override
  public void mouseClicked(final MouseEvent event) {
    radioButton.setSelected(true);
  }
}
