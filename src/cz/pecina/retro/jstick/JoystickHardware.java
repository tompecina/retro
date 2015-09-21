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
   * The Up button.
   */
  public final LockableButton upButton =
    new LockableButton("jstick/JoystickButton/up-%d-%s.png", null, null);

  /**
   * The Right button.
   */
  public final LockableButton rightButton =
    new LockableButton("jstick/JoystickButton/right-%d-%s.png", null, null);

  /**
   * The Down button.
   */
  public final LockableButton downButton =
    new LockableButton("jstick/JoystickButton/down-%d-%s.png", null, null);

  /**
   * The Left button.
   */
  public final LockableButton leftButton =
    new LockableButton("jstick/JoystickButton/left-%d-%s.png", null, null);

  /**
   * The Fire button.
   */
  public final LockableButton fireButton =
    new LockableButton("jstick/JoystickButton/fire-%d-%s.png", null, null);
 
  // button pins
  private final UpPin upPin = new UpPin();
  private final RightPin rightPin = new RightPin();
  private final DownPin downPin = new DownPin();
  private final LeftPin leftPin = new LeftPin();
  private final FirePin firePin = new FirePin();

  // button presses
  private boolean upBuffer, upCurrent, upNext;
  private boolean rightBuffer, rightCurrent, rightNext;
  private boolean downBuffer, downCurrent, downNext;
  private boolean leftBuffer, leftCurrent, leftNext;
  private boolean fireBuffer, fireCurrent, fireNext;
  
  /**
   * Creates the joystick hardware object.
   */
  public JoystickHardware() {
    log.fine("New joystick hardware creation started");

    upButton.addChangeListener(new UpListener(upButton));
    rightButton.addChangeListener(new RightListener(rightButton));
    downButton.addChangeListener(new DownListener(downButton));
    leftButton.addChangeListener(new LeftListener(leftButton));
    fireButton.addChangeListener(new FireListener(fireButton));

    log.fine("New joystick hardware created");
  }

  // Up listener
  private class UpListener implements ChangeListener {

    private LockableButton key;
    
    public UpListener(final LockableButton key) {
      super();
      assert key != null;
      this.key = key;
    }

    // for description see ChangeListener
    @Override
    public void stateChanged(final ChangeEvent event) {
      log.finer("Up listener called");
      final boolean pressed = key.isPressed();
      log.finest("Pressed: " + pressed);
      upCurrent = pressed;
      upNext = upNext || pressed;
    }
  }

  // Right listener
  private class RightListener implements ChangeListener {

    private LockableButton key;
    
    public RightListener(final LockableButton key) {
      super();
      assert key != null;
      this.key = key;
    }

    // for description see ChangeListener
    @Override
    public void stateChanged(final ChangeEvent event) {
      log.finer("Right listener called");
      final boolean pressed = key.isPressed();
      log.finest("Pressed: " + pressed);
      rightCurrent = pressed;
      rightNext = rightNext || pressed;
    }
  }

  // Down listener
  private class DownListener implements ChangeListener {

    private LockableButton key;
    
    public DownListener(final LockableButton key) {
      super();
      assert key != null;
      this.key = key;
    }

    // for description see ChangeListener
    @Override
    public void stateChanged(final ChangeEvent event) {
      log.finer("Down listener called");
      final boolean pressed = key.isPressed();
      log.finest("Pressed: " + pressed);
      downCurrent = pressed;
      downNext = downNext || pressed;
    }
  }

  // Left listener
  private class LeftListener implements ChangeListener {

    private LockableButton key;
    
    public LeftListener(final LockableButton key) {
      super();
      assert key != null;
      this.key = key;
    }

    // for description see ChangeListener
    @Override
    public void stateChanged(final ChangeEvent event) {
      log.finer("Left listener called");
      final boolean pressed = key.isPressed();
      log.finest("Pressed: " + pressed);
      leftCurrent = pressed;
      leftNext = leftNext || pressed;
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
    upBuffer = upNext;
    upNext = upCurrent;
    rightBuffer = rightNext;
    rightNext = rightCurrent;
    downBuffer = downNext;
    downNext = downCurrent;
    leftBuffer = leftNext;
    leftNext = leftCurrent;
    fireBuffer = fireNext;
    fireNext = fireCurrent;
  }
	
  // Up pin
  private class UpPin extends IOPin {

    // for description see IOPin
    @Override
    public int query() {
      return upBuffer ? 0 : 1;
    }
  }

  // Right pin
  private class RightPin extends IOPin {

    // for description see IOPin
    @Override
    public int query() {
      return rightBuffer ? 0 : 1;
    }
  }

  // Down pin
  private class DownPin extends IOPin {

    // for description see IOPin
    @Override
    public int query() {
      return downBuffer ? 0 : 1;
    }
  }

  // Left pin
  private class LeftPin extends IOPin {

    // for description see IOPin
    @Override
    public int query() {
      return leftBuffer ? 0 : 1;
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
   * Gets the Up pin.
   *
   * @return the Up pin.
   */
  public IOPin getUpPin() {
    return upPin;
  }

  /**
   * Gets the Right pin.
   *
   * @return the Right pin.
   */
  public IOPin getRightPin() {
    return rightPin;
  }

  /**
   * Gets the Down pin.
   *
   * @return the Down pin.
   */
  public IOPin getDownPin() {
    return downPin;
  }

  /**
   * Gets the Left pin.
   *
   * @return the Left pin.
   */
  public IOPin getLeftPin() {
    return leftPin;
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
