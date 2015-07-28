/* Stepper.java
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

package cz.pecina.retro.pmi80;

import cz.pecina.retro.cpu.IOElement;
import cz.pecina.retro.common.Util;

public class Stepper extends Peripheral implements IOElement {
  private StepperPanel panel;
  private int position, currCommand;

  public Stepper() {
    super("stepper");
  }

  public void connect() {
    Computer.cpu.addIOOutput(0x60, this);
    Computer.cpu.addIOInput(0x60, this);
    panel = new StepperPanel(this);
    setFrame(panel);
    panel.getIndexLED().setState(true);
  }

  public void disconnect() {
    super.disconnect();
    Computer.cpu.clearIOOutput(0x60);
    Computer.cpu.clearIOInput(0x60);
  }

  public void portOutput(int port, int data) {
    data &= 0x0f;
    if (data != currCommand) {
      for (int i = 0; i < Constants.NUMBER_STEPPER_INPUT_LEDS; i++)
	panel.getInputLED(i).setState((data >> i) & 1);
      if ((data & 0x03) == 0x03)
	data &= ~0x03;
      if ((data & 0x0c) == 0x0c)
	data &= ~0x0c;
      currCommand = data;
      int t = (int)(0x8408657623128408L >> (4 * data)) & 0x0f;
      if ((t & 0x08) == 0) {
	t = (t - position) & 0x07;
	if (t < 4)
	  position += t;
	else if (t > 4)
	  position += t - 8;
	panel.getDisc().setState(Util.modulo(position,
          Constants.NUMBER_STEPPER_STEPS));
	panel.getCounter().setState((int)Math.round(((double)position /
	  Constants.NUMBER_STEPPER_STEPS) * Constants.NUMBER_STATES_PER_DIGIT));
	panel.getIndexLED().setState((position %
	  Constants.NUMBER_STEPPER_STEPS) == 0);
      }
      panel.repaint();
    }
  }

  public int portInput(int port) {
    return (position == 0) ? 0x01 : 0x00;
  }
}
