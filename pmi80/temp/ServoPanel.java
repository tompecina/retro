/* ServoPanel.java
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

import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import cz.pecina.retro.gui.Counter;
import cz.pecina.retro.gui.Digit;
import cz.pecina.retro.gui.LED;

public class ServoPanel extends PeripheralFrame {
  private ServoDisc disc;
  private Counter counter;
  private LED PWMLED, directionLED, brakeLED, ALED, BLED, ZLED;

  public ServoPanel(Peripheral peripheral) {
    super(Emulator.textResources.getString("servo.frameTitle"), peripheral);
    Digit digit;

    JLayeredPane pane = new JLayeredPane();
    pane.setLayout(null);
    pane.setPreferredSize(new Dimension(Constants.servoMaskWidth,
					Constants.servoMaskHeight));
    JLabel background = new JLabel(IconCache.get("servo/servomask-" +
      Constants.servoMaskWidth + "x" + Constants.servoMaskHeight + ".png"));
    background.setBounds(0,
			 0,
			 Constants.servoMaskWidth,
			 Constants.servoMaskHeight);
    pane.add(background, new Integer(Constants.BACKGROUND_LAYER));

    disc = new ServoDisc();
    disc.setBounds(Constants.servoDiscOffsetX,
		   Constants.servoDiscOffsetY,
		   Constants.servoDiscWidth,
		   Constants.servoDiscHeight);
    pane.add(disc);

    counter =
      new Counter(Constants.NUMBER_SERVO_COUNTER_DIGITS, "basic", "white");
    for (int i = 0; i < Constants.NUMBER_SERVO_COUNTER_DIGITS; i++) {
      digit = counter.getDigit(i);
      digit.setBounds(
        (i * Constants.servoDigitGridX) + Constants.servoDigitOffsetX,
	Constants.servoDigitOffsetY,
	Constants.digitWidth,
	Constants.digitHeight);
      add(digit);
    }

    PWMLED = new LED("small", "yellow");
    PWMLED.setBounds(Constants.servoPWMLEDOffsetX,
		     Constants.servoPWMLEDOffsetY,
		     Constants.LEDWidth,
		     Constants.LEDHeight);
    add(PWMLED);

    directionLED = new LED("small", "yellow");
    directionLED.setBounds(Constants.servoDirectionLEDOffsetX,
			   Constants.servoDirectionLEDOffsetY,
			   Constants.LEDWidth,
			   Constants.LEDHeight);
    add(directionLED);

    brakeLED = new LED("small", "red");
    brakeLED.setBounds(Constants.servoBrakeLEDOffsetX,
		       Constants.servoBrakeLEDOffsetY,
		       Constants.LEDWidth,
		       Constants.LEDHeight);
    add(brakeLED);

    ALED = new LED("small", "green");
    ALED.setBounds(Constants.servoALEDOffsetX,
		   Constants.servoALEDOffsetY,
		   Constants.LEDWidth,
		   Constants.LEDHeight);
    add(ALED);

    BLED = new LED("small", "green");
    BLED.setBounds(Constants.servoBLEDOffsetX,
		   Constants.servoBLEDOffsetY,
		   Constants.LEDWidth,
		   Constants.LEDHeight);
    add(BLED);

    ZLED = new LED("small", "green");
    ZLED.setBounds(Constants.servoZLEDOffsetX,
		   Constants.servoZLEDOffsetY,
		   Constants.LEDWidth,
		   Constants.LEDHeight);
    add(ZLED);

    add(pane);
    setResizable(false);
    pack();
    postamble();
  }

  public ServoDisc getDisc() {
    return disc;
  }

  public Counter getCounter() {
    return counter;
  }

  public LED getPWMLED() {
    return PWMLED;
  }

  public LED getDirectionLED() {
    return directionLED;
  }

  public LED getBrakeLED() {
    return brakeLED;
  }

  public LED getALED() {
    return ALED;
  }

  public LED getBLED() {
    return BLED;
  }

  public LED getZLED() {
    return ZLED;
  }
}
