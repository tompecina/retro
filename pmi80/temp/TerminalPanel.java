/* TerminalPanel.java
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

import java.awt.Dimension;
import java.awt.Color;
import javax.swing.JPanel;

public class TerminalPanel extends PeripheralFrame {
  private TerminalDisplay terminal;

  public TerminalPanel(Peripheral peripheral) {
    super(Emulator.textResources.getString("terminal.frameTitle"), peripheral);

    JPanel pane = new JPanel();
    pane.setLayout(null);
    pane.setPreferredSize(new Dimension(Constants.terminalWidth,
					Constants.terminalHeight));
    pane.setBackground(Color.BLACK);

    terminal = new TerminalDisplay();
    terminal.setBounds(Constants.terminalOffsetX,
		       Constants.terminalOffsetY,
		       Constants.terminalWidth,
		       Constants.terminalHeight);
    pane.add(terminal);

    add(pane);
    setResizable(false);
    pack();
    postamble();
  }

  public void output(int ch) {
    terminal.output(ch);
  }
}
