/* ServoDisc.java
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

import javax.swing.JLabel;
import javax.swing.ImageIcon;

public class ServoDisc extends JLabel {
  private static final ImageIcon[] icons =
    new ImageIcon[Constants.NUMBER_SERVO_STATES];
  private static boolean iconsLoaded = false;
  private int state = 0;

  public ServoDisc() {
    if (!iconsLoaded) {
      for (int i = 0; i < Constants.NUMBER_SERVO_STATES; i++) {
	icons[i] = IconCache.get("servo/servo-" + Constants.servoDiscWidth +
	  "x" + Constants.servoDiscHeight + "-" + i + ".png");
      }
      iconsLoaded = true;
    }
    setIcon(icons[0]);
    repaint();
  }
    
  public void setState(int state) {
    this.state = state;
    setIcon(icons[state]);
    repaint();
  }

  public int getState() {
    return state;
  }
}
