/* JoystickHardware.java
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

package cz.pecina.retro.jstick;

import java.util.logging.Logger;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import cz.pecina.retro.cpu.IOPin;

import cz.pecina.retro.gui.LockableButton;

/**
 * Hardware of the universal joystick.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class JoystickHardware {

  // static logger
  private static final Logger log =
    Logger.getLogger(JoystickHardware.class.getName());

  /**
   * The North button.
   */
  public final LockableButton northButton =
    new LockableButton("jstick/JoystickButton/N-%d-%s.png", null, null);

  /**
   * The East button.
   */
  public final LockableButton eastButton =
    new LockableButton("jstick/JoystickButton/E-%d-%s.png", null, null);

  /**
   * The South button.
   */
  public final LockableButton southButton =
    new LockableButton("jstick/JoystickButton/S-%d-%s.png", null, null);

  /**
   * The West button.
   */
  public final LockableButton westButton =
    new LockableButton("jstick/JoystickButton/W-%d-%s.png", null, null);

  /**
   * The Fire button.
   */
  public final LockableButton fireButton =
    new LockableButton("jstick/JoystickButton/F-%d-%s.png", null, null);
 
  // button pins
  private final NorthPin northPin = new NorthPin();
  private final EastPin eastPin = new EastPin();
  private final SouthPin southPin = new SouthPin();
  private final WestPin westPin = new WestPin();
  private final FirePin firePin = new FirePin();

  // button presses
  private boolean northBuffer, northCurrent, northNext;
  private boolean eastBuffer, eastCurrent, eastNext;
  private boolean southBuffer, southCurrent, southNext;
  private boolean westBuffer, westCurrent, westNext;
  private boolean fireBuffer, fireCurrent, fireNext;
  
  /**
   * Creates the joystick hardware object.
   */
  public JoystickHardware() {
    log.fine("New joystick hardware creation started");

    northButton.addChangeListener(new NorthListener(northButton));
    eastButton.addChangeListener(new EastListener(eastButton));
    southButton.addChangeListener(new SouthListener(southButton));
    westButton.addChangeListener(new WestListener(westButton));
    fireButton.addChangeListener(new FireListener(fireButton));

    log.fine("New joystick hardware created");
  }

  // North listener
  private class NorthListener implements ChangeListener {

    private LockableButton key;
    
    public NorthListener(final LockableButton key) {
      super();
      assert key != null;
      this.key = key;
    }

    // for description see ChangeListener
    @Override
    public void stateChanged(final ChangeEvent event) {
      log.finer("North listener called");
      final boolean pressed = key.isPressed();
      log.finest("Pressed: " + pressed);
      northCurrent = pressed;
      northNext = northNext || pressed;
    }
  }

  // East listener
  private class EastListener implements ChangeListener {

    private LockableButton key;
    
    public EastListener(final LockableButton key) {
      super();
      assert key != null;
      this.key = key;
    }

    // for description see ChangeListener
    @Override
    public void stateChanged(final ChangeEvent event) {
      log.finer("East listener called");
      final boolean pressed = key.isPressed();
      log.finest("Pressed: " + pressed);
      eastCurrent = pressed;
      eastNext = eastNext || pressed;
    }
  }

  // South listener
  private class SouthListener implements ChangeListener {

    private LockableButton key;
    
    public SouthListener(final LockableButton key) {
      super();
      assert key != null;
      this.key = key;
    }

    // for description see ChangeListener
    @Override
    public void stateChanged(final ChangeEvent event) {
      log.finer("South listener called");
      final boolean pressed = key.isPressed();
      log.finest("Pressed: " + pressed);
      southCurrent = pressed;
      southNext = southNext || pressed;
    }
  }

  // West listener
  private class WestListener implements ChangeListener {

    private LockableButton key;
    
    public WestListener(final LockableButton key) {
      super();
      assert key != null;
      this.key = key;
    }

    // for description see ChangeListener
    @Override
    public void stateChanged(final ChangeEvent event) {
      log.finer("West listener called");
      final boolean pressed = key.isPressed();
      log.finest("Pressed: " + pressed);
      westCurrent = pressed;
      westNext = westNext || pressed;
    }
  }

  // Fire listener
  private class FireListener implements ChangeListener {

    private LockableButton key;
    
    public FireListener(final LockableButton key) {
      super();
      assert key != null;
      this.key = key;
    }

    // for description see ChangeListener
    @Override
    public void stateChanged(final ChangeEvent event) {
      log.finer("Fire listener called");
      final boolean pressed = key.isPressed();
      log.finest("Pressed: " + pressed);
      fireCurrent = pressed;
      fireNext = fireNext || pressed;
    }
  }
  
  /**
   * Performs periodic buttons update.
   */
  public void update() {
    updateBuffer();
  }

  /**
   * Updates the matrix of button presses.
   */
  public void updateBuffer() {
    northBuffer = northNext;
    northNext = northCurrent;
    eastBuffer = eastNext;
    eastNext = eastCurrent;
    southBuffer = southNext;
    southNext = southCurrent;
    westBuffer = westNext;
    westNext = westCurrent;
    fireBuffer = fireNext;
    fireNext = fireCurrent;
  }
	
  // North pin
  private class NorthPin extends IOPin {

    // for description see IOPin
    @Override
    public int query() {
      return northBuffer ? 0 : 1;
    }
  }

  // East pin
  private class EastPin extends IOPin {

    // for description see IOPin
    @Override
    public int query() {
      return eastBuffer ? 0 : 1;
    }
  }

  // South pin
  private class SouthPin extends IOPin {

    // for description see IOPin
    @Override
    public int query() {
      return southBuffer ? 0 : 1;
    }
  }

  // West pin
  private class WestPin extends IOPin {

    // for description see IOPin
    @Override
    public int query() {
      return westBuffer ? 0 : 1;
    }
  }

  // Fire pin
  private class FirePin extends IOPin {

    // for description see IOPin
    @Override
    public int query() {
      return fireBuffer ? 0 : 1;
    }
  }

  /**
   * Gets the North pin.
   *
   * @return the North pin.
   */
  public IOPin getNorthPin() {
    return northPin;
  }

  /**
   * Gets the East pin.
   *
   * @return the East pin.
   */
  public IOPin getEastPin() {
    return eastPin;
  }

  /**
   * Gets the South pin.
   *
   * @return the South pin.
   */
  public IOPin getSouthPin() {
    return southPin;
  }

  /**
   * Gets the West pin.
   *
   * @return the West pin.
   */
  public IOPin getWestPin() {
    return westPin;
  }

  /**
   * Gets the Fire pin.
   *
   * @return the Fire pin.
   */
  public IOPin getFirePin() {
    return firePin;
  }
}
