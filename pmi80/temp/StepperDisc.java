/* StepperDisc.java
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
 **/

package cz.pecina.retro.pmi80;

import javax.swing.JLabel;
import javax.swing.ImageIcon;

public class StepperDisc extends JLabel {
  private static final ImageIcon[] icons =
    new ImageIcon[Constants.NUMBER_STEPPER_STEPS];
  private static boolean iconsLoaded = false;
  private int state = 0;

  public StepperDisc() {
    if (!iconsLoaded) {
      for (int i = 0; i < Constants.NUMBER_STEPPER_STEPS; i++) {
	icons[i] = IconCache.get("stepper/stepper-" +
	  Constants.stepperDiscWidth + "x" + Constants.stepperDiscHeight +
	  "-" + i + ".png");
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
