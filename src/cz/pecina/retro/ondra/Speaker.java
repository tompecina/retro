/* Speaker.java
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

package cz.pecina.retro.ondra;

import java.util.logging.Logger;

import cz.pecina.retro.common.Parameters;
import cz.pecina.retro.common.Sound;

import cz.pecina.retro.cpu.IOPin;
import cz.pecina.retro.cpu.IONode;
import cz.pecina.retro.cpu.CPUEventOwner;
import cz.pecina.retro.cpu.CPUScheduler;

/**
 * The Ondra built-in speaker.  The speaker is driven from an astable
 * multivibrator circuit, which is emulated in a somewhat simplified manner.
 * However, both free-running and triggered operation should be
 * simulated with virtualy no difference from the real circuit.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Speaker implements CPUEventOwner {

  // static logger
  private static final Logger log =
    Logger.getLogger(Speaker.class.getName());

  // pulse duration
  private static final int PULSE = 425;

  // pause duration
  private static final int[] PAUSE = new int[]
    {4779, 2880, 1995, 1040, 901, 814, 716};

  // name of the device
  private String name;

  // input value
  private int value;

  // input pins
  private final InPin[] inPins = new InPin[3];

  /**
   * Gets the device name.
   *
   * @return the name of the device
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the input pin.
   *
   * @param  number the pin number
   * @return        the input pin
   */
  public IOPin getInPin(final int number) {
    return inPins[number];
  }

  /**
   * Main constructor.
   *
   * @param name         the name of the device
   */
  public Speaker(final String name) {
    assert name != null;
    log.fine("New speaker creation started: " + name);

    this.name = name;

    for (int i = 0; i < 3; i++) {
      inPins[i] = new InPin(i);
    }
    
    log.fine("New speaker creation completed: " + name);
  }

  // input pin class
  private class InPin extends IOPin {

    private int number;
    public int level;
    
    // main constructor
    private InPin(final int number) {
      super();
      this.number = number;
      log.finer("New speaker input pin created, number: " + number);
    }

    // for description see IOPin
    @Override
    public void notifyChange() {
      final int newLevel = IONode.normalize(queryNode());
      if (newLevel != level) {
	level = newLevel;
	value = (inPins[2].level << 2) | (inPins[1].level << 1) | level;
	CPUScheduler.removeAllEvents(Speaker.this);
	if (value > 0) {
	  Parameters.sound.write(Sound.SPEAKER_CHANNEL, true);
	  CPUScheduler.addEventRelative(Speaker.this, PULSE);
	} else {
	  Parameters.sound.write(Sound.SPEAKER_CHANNEL, false);
	}
      }
    }
  }

  // for description see CPUEventOwner
  @Override
  public void performEvent(final int parameter, final long delay) {
    if ((parameter != 0) && (value != 0)) {
      Parameters.sound.write(Sound.SPEAKER_CHANNEL, true);
      CPUScheduler.addEventRelative(this, PULSE);
    } else {
      Parameters.sound.write(Sound.SPEAKER_CHANNEL, false);
      if (value != 0) {
	CPUScheduler.addEventRelative(this, PAUSE[value - 1], 1);
      }
    }
  }
}
