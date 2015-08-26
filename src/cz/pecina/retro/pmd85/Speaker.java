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

package cz.pecina.retro.pmd85;

import java.util.logging.Logger;

import cz.pecina.retro.common.Parameters;
import cz.pecina.retro.common.Sound;

import cz.pecina.retro.cpu.IOPin;

/**
 * The PMD 85 built-in speaker.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Speaker {

  // static logger
  private static final Logger log =
    Logger.getLogger(Speaker.class.getName());

  // name of the device
  private String name;

  // input pin
  private final InPin inPin = new InPin();

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
   * @return the input pin
   */
  public IOPin getInPin() {
    return inPin;
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

    log.fine("New speaker creation completed: " + name);
  }

  // input pin class
  private class InPin extends IOPin {
	
    // main constructor
    private InPin() {
      super();
      log.finer("New speaker input pin created");
    }

    // for description see IOPin
    @Override
    public void notifyChange() {
      Parameters.sound.write(Sound.SPEAKER_CHANNEL, queryNode() != 0);
    }
  }
}
