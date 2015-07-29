/* StepperPanel.java
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
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import cz.pecina.retro.gui.Counter;
import cz.pecina.retro.gui.Digit;
import cz.pecina.retro.gui.LED;

public class StepperPanel extends PeripheralFrame {
  private StepperDisc disc;
  private Counter counter;
  private LED[] inputLEDs = new LED[Constants.NUMBER_STEPPER_INPUT_LEDS];
  private LED indexLED;

  public StepperPanel(Peripheral peripheral) {
    super(Emulator.textResources.getString("stepper.frameTitle"), peripheral);
    int i;
    Digit digit;
    LED led;

    JLayeredPane pane = new JLayeredPane();
    pane.setLayout(null);
    pane.setPreferredSize(new Dimension(Constants.stepperMaskWidth,
					Constants.stepperMaskHeight));
    JLabel background =
      new JLabel(IconCache.get("stepper/steppermask-" +
        Constants.stepperMaskWidth + "x" + Constants.stepperMaskHeight +
        ".png"));
    background.setBounds(0,
			 0,
			 Constants.stepperMaskWidth,
			 Constants.stepperMaskHeight);
    pane.add(background, new Integer(Constants.BACKGROUND_LAYER));

    disc = new StepperDisc();
    disc.setBounds(Constants.stepperDiscOffsetX,
		   Constants.stepperDiscOffsetY,
		   Constants.stepperDiscWidth,
		   Constants.stepperDiscHeight);
    pane.add(disc);

    counter =
      new Counter(Constants.NUMBER_STEPPER_COUNTER_DIGITS, "basic", "white");
    for (i = 0; i < Constants.NUMBER_STEPPER_COUNTER_DIGITS; i++) {
      digit = counter.getDigit(i);
      digit.setBounds(
        (i * Constants.stepperDigitGridX) + Constants.stepperDigitOffsetX,
	Constants.stepperDigitOffsetY,
	Constants.digitWidth,
	Constants.digitHeight);
      add(digit);
    }

    for (i = 0; i < Constants.NUMBER_STEPPER_INPUT_LEDS; i++) {
      led = inputLEDs[i] = new LED("small", "yellow");
      led.setBounds(
        Constants.stepperInputLEDOffsetX + (Constants.stepperInputLEDGridX * i),
	Constants.stepperInputLEDOffsetY,
	Constants.LEDWidth,
	Constants.LEDHeight);
      add(led);
    }
    indexLED = new LED("small", "green");
    indexLED.setBounds(Constants.stepperIndexLEDOffsetX,
		       Constants.stepperIndexLEDOffsetY,
		       Constants.LEDWidth,
		       Constants.LEDHeight);
    add(indexLED);

    add(pane);
    setResizable(false);
    pack();
    postamble();
  }

  public StepperDisc getDisc() {
    return disc;
  }

  public Counter getCounter() {
    return counter;
  }

  public LED getInputLED(int n) {
    return inputLEDs[n];
  }

  public LED getIndexLED() {
    return indexLED;
  }
}
