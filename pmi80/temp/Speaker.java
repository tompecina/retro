/* Speaker.java
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

import javax.swing.JOptionPane;
import cz.pecina.retro.cpu.IOElement;

public class Speaker extends Peripheral implements IOElement {
  private boolean state;
  private SpeakerPanel panel;

  public Speaker() {
    super("speaker");
  }

  public void connect() {
    if (Computer.sound.start()) {	    
      Computer.cpu.addIOOutput(0xdf, this);
      Computer.cpu.addIOOutput(0xef, this);
      panel = new SpeakerPanel(this);
      setFrame(panel);
      Computer.sound.add(Computer.cpu.getCycleCounter(), Short.MIN_VALUE);
    } else
      JOptionPane.showMessageDialog(
        null,
	Emulator.textResources.getString("speaker.error.noSound"),
	Emulator.textResources.getString("speaker.error.title"),
	JOptionPane.ERROR_MESSAGE);
  }

  public void disconnect() {
    super.disconnect();
    Computer.sound.stop();
  }

  public void portOutput(int port, int data) {
    state = (port == 0xdf) ? false : !state;
    Computer.sound.add(Computer.cpu.getCycleCounter(),
		       state ? Short.MAX_VALUE : Short.MIN_VALUE);
  }

  public int portInput(int port) {
    return 0xff;
  }
}
