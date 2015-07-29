/* Servo.java
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

import cz.pecina.retro.cpu.IOElement;
import cz.pecina.retro.common.Util;

public class Servo extends Peripheral implements IOElement {
  private ServoPanel panel;
  private double absolutePosition, relativePosition, velocity;
  private long start;
  private int command;
  private int latch;

  public Servo() {
    super("servo");
    start = Computer.cpu.getCycleCounter();
  }

  public void connect() {
    Computer.cpu.addIOOutput(0xa0, this);
    Computer.cpu.addIOInput(0xa0, this);
    panel = new ServoPanel(this);
    setFrame(panel);
  }

  public void disconnect() {
    super.disconnect();
    Computer.cpu.clearIOOutput(0xa0);
    Computer.cpu.clearIOInput(0xa0);
  }

  private void updatePositionAndVelocity() {
    if (isActive()) {
      long period = Computer.cpu.getCycleCounter() - start;
      start += period;
      double seconds = period / Constants.CPU_FREQUENCY;
      double sign = Math.signum(velocity);
      double acceleration = -sign * 2;
      if ((command & 0x04) != 0) {
	acceleration -= velocity * 0.1;
      } else if ((command & 0x01) != 0) {
	acceleration += (((command & 0x02) == 0) ? 1 : -1) * 50;
      }
      velocity += acceleration * seconds;
      if (Math.abs(velocity) > 200) {
	velocity = Math.signum(velocity) * 200;
      }
      absolutePosition += velocity * seconds;
      relativePosition = Util.modulo(absolutePosition, 360);
    }
  }

  public void portOutput(int port, int data) {
    updatePositionAndVelocity();
    command = data;
    latch |= data;
  }

  public int portInput(int port) {
    int tmp = (int)(relativePosition / 15);
    return (((relativePosition < 15) || (relativePosition > 345)) ?
      0x04 : 0) | ((tmp + 1) >> 2) | ((tmp + 3) >> 1);
  }

  public void update() {
    boolean repaint = false;
    int state;
    if (isActive()) {
      updatePositionAndVelocity();
      state = (int)(relativePosition + 0.5) % 360;
      if (state != panel.getDisc().getState()) {
	panel.getDisc().setState(state);
	repaint = true;
      }
      state = (int)Math.round((absolutePosition / 360) *
        Constants.NUMBER_STATES_PER_DIGIT);
      if (state != panel.getCounter().getState()) {
	panel.getCounter().setState(state);
	repaint = true;
      }
      if (Math.abs(velocity) > (60 * Constants.SERVO_LED_LIMIT))
	state = 1;
      else
	state = (((int)(relativePosition / 15) + 1) >> 2) & 1;
      if (state != panel.getALED().getState()) {
	panel.getALED().setState(state);
	repaint = true;
      }
      if (Math.abs(velocity) > (60 * Constants.SERVO_LED_LIMIT)) {
	state = 1;
      } else {
	state = (((int)(relativePosition / 15) + 3) >> 2) & 1;
      }
      if (state != panel.getBLED().getState()) {
	panel.getBLED().setState(state);
	repaint = true;
      }
      if (Math.abs(velocity) > (60 * Constants.SERVO_LED_LIMIT)) {
	state = 0;
      } else {
	state = ((relativePosition < 15) || (relativePosition > 345)) ? 1 : 0;
      }
      if (state != panel.getZLED().getState()) {
	panel.getZLED().setState(state);
	repaint = true;
      }
      state = latch & 0x01;
      if (state != panel.getPWMLED().getState()) {
	panel.getPWMLED().setState(state);
	repaint = true;
      }
      state = (latch >> 1) & 0x01;
      if (state != panel.getDirectionLED().getState()) {
	panel.getDirectionLED().setState(state);
	repaint = true;
      }
      state = (latch >> 2) & 0x01;
      if (state != panel.getBrakeLED().getState()) {
	panel.getBrakeLED().setState(state);
	repaint = true;
      }
      latch = command;
      if (repaint) {
	panel.repaint();
      }
    }
  }
}
