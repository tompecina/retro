/* PCKeyboardHardware.java
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

package cz.pecina.retro.pckbd;

import java.util.logging.Logger;

import java.util.concurrent.ConcurrentLinkedQueue;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import cz.pecina.retro.peripherals.Peripheral;

import cz.pecina.retro.common.Parameters;

import cz.pecina.retro.cpu.IOElement;
import cz.pecina.retro.cpu.CPUEventOwner;
import cz.pecina.retro.cpu.CPUScheduler;

import cz.pecina.retro.gui.GUI;
import cz.pecina.retro.gui.Shortcut;

/**
 * PC keyboard hardware.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class PCKeyboardHardware implements IOElement, CPUEventOwner {

  // static logger
  private static final Logger log =
    Logger.getLogger(PCKeyboardHardware.class.getName());

  // input register bitmasks
  private static final int BITMASK_DATA = 1 << 0;
  private static final int BITMASK_CLOCK = 1 << 1;
  private static final int BITMASK_INTERRUPT_FLAG = 1 << 2;

  // output register bitmasks
  private static final int BITMASK_RESET = 1 << 0;
  private static final int BITMASK_INTERRUPT_MASK = 1 << 1;

  // timing constants
  private final long timingPreStart = timing(5.0);
  private final long timingStart = timing(120.0);
  private final long timingClockHigh = timing(70.0);
  private final long timingDataChange = timing(93.0);
  private final long timingClockSlot = timing(100.0);
  private final long timingTimeout = timing(1000.0);

  // base port
  private int basePort;

  // interrupt vector
  private int vector;

  // keyboard layout
  private final PCKeyboardLayout layout = new PCKeyboardLayout(this);

  // CPU scheduler
  private final CPUScheduler scheduler = Parameters.cpu.getCPUScheduler();

  // reset flag (if true device is in reset mode)
  private boolean resetFlag;

  // interrupt mask (if true and vector is set, interrupt is enabled)
  private boolean interruptMask;

  // interrupt pending flag
  private boolean interruptFlag;

  // current data on the output port
  private int portData = BITMASK_CLOCK;

  // queue of scan codes waiting for transmission
  private final ConcurrentLinkedQueue<Integer> queue =
    new ConcurrentLinkedQueue<>();

  // time when the next transmission may begin
  private long timeout;

  /**
   * Creates the PC keyboard hardware object.
   *
   * @param basePort the base port
   * @param vector   the interrupt vector
   */
  public PCKeyboardHardware(final int basePort, final int vector) {
    assert (basePort >= 0) && (basePort < 0x100);
    assert (vector >= -1) && (vector < 8);
    this.basePort = basePort;
    this.vector = vector;
    connect();
    log.fine("New PC keyboard hardware created");
  }

  /**
   * Activates shortcuts on the frame.
   *
   * @param frame the frame
   */
  public void activateShortcuts(final PCKeyboardFrame frame) {
    assert frame != null;
    frame.addKeyListener(new ShortcutKeyListener());
    log.fine("Shortcuts added");
  }

  /**
   * Calculates a timing constant.
   *
   * @param  t the time in usec
   * @return the timing constant in clock cycles
   */
  public long timing(final double t) {
    assert t >= 0;
    final long r = Math.round((t / 1e6) * Parameters.CPUFrequency);
    log.finer(String.format("Calculation of timing: %f -> %d", t, r));
    return r;
  }

  /**
   * Deactivates hardware.
   */
  public void deactivate() {
    disconnect();
    for (PCKeyboardKey key: layout.getKeys()) {
      GUI.removeResizeable(key);
    }
    scheduler.removeAllScheduledEvents(this);
  }

  /**
   * Gets the keyboard layout.
   *
   * @return the keyboard element
   */
  public PCKeyboardLayout getLayout() {
    return layout;
  }    

  // connect to ports
  private void connect() {
    Parameters.cpu.addIOInput(basePort, this);
    Parameters.cpu.addIOOutput(basePort, this);
    log.fine("Ports connected");
  }

  // disconnect from ports
  private void disconnect() {
    Parameters.cpu.removeIOInput(basePort, this);
    Parameters.cpu.removeIOOutput(basePort, this);
    log.fine("Ports disconnected");
  }

  /**
   * Reconnects PC keyboard to a new base port and sets the new
   * interrupt vector.
   *
   * @param basePort the new base port
   * @param vector   the new interrupt vector
   */
  public void reconnect(final int basePort, final int vector) {
    assert (basePort >= 0) && (basePort < 0x100);
    assert (vector >= -1) && (vector < 8);
    if ((this.basePort != basePort) || (this.vector != vector)) {
      disconnect();
      this.basePort = basePort;
      this.vector = vector;
      connect();
      log.fine(String.format("PC keyboard hardware reconnected to new base" +
        " port %02x and interrupt vector set to %d", basePort, vector));
    } else {
      log.finer("PC keyboard hardware reconnection not required");
    }
  }

  // for description see IOElement
  @Override
  public void portOutput(final int port, final int data) {
    log.finest(String.format("Port output: %02x -> (%02x)", data, port));
    assert port == basePort;
    if ((data & BITMASK_RESET) != 0) {
      resetFlag = true;
      interruptMask = interruptFlag = false;
      scheduler.removeAllScheduledEvents(this);
      queue.clear();
      timeout = Parameters.systemClockSource.getSystemClock();
      log.fine("PC keyboard reset");
    } else {
      resetFlag = false;
      interruptMask = (data & BITMASK_INTERRUPT_MASK) != 0;
      interruptFlag &= interruptMask;		    
      log.finer("PC keyboard interrput mask set to:" + interruptMask);
    }
  }

  // for description see IOElement
  @Override
  public int portInput(final int port) {
    assert port == basePort;
    int r = portData;
    if (interruptFlag) {
      r |= BITMASK_INTERRUPT_FLAG;
    }
    interruptFlag = false;
    log.finest(String.format("Port input: (%02x) -> %02x", port, r));
    return r;
  }

  // key listener
  private class ShortcutKeyListener extends KeyAdapter {

    private void listener(final KeyEvent event, final boolean released) {
      for (PCKeyboardKey key: layout.getKeys()) {
	for (Shortcut shortcut: key.getShortcuts()) {
	  if ((event.getExtendedKeyCode() == shortcut.getKeyCode()) &&
	      ((shortcut.getKeyLocation() == -1) ||
	       (event.getKeyLocation() == shortcut.getKeyLocation()))) {
	    key.setPressed(!released);
	    sendScanCode(key.getScanCode(), released);
	    log.finer("Sending scan code: " + key.getScanCode() +
		      ", released: " + released);
	    return;
	  }
	}
      }
    }

    @Override
    public void keyPressed(final KeyEvent event) {
      System.err.println(event.getKeyCode() + ":" +
			 event.getKeyChar() + ":" +
			 (int)event.getKeyChar() + ":" +
			 event.getKeyLocation() + ":" +
			 event.isActionKey() + ":" +
			 event.paramString() + ":" +
			 event.getExtendedKeyCode() + ":");
      listener(event, false);
    }

    @Override
    public void keyReleased(final KeyEvent event) {
      listener(event, true);
    }
  }

  /**
   * Schedules the key's press or release scan code for transmission.
   *
   * @param scanCode the scan code of the key
   * @param released {@code false} if pressed,
   *                 {@code true} if released
   */
  public void sendScanCode(int scanCode, final boolean released) {
    assert scanCode > 0;
    log.finer("Scheduling scan code: " + scanCode + ", released: " + released);
    if (!resetFlag) {
      long startTime = Math.max(Parameters.systemClockSource.getSystemClock(),
				timeout) + timingTimeout;
      scheduler.addScheduledEvent(this, startTime, 0);
      scheduler.addScheduledEvent(this,
				  startTime + timingPreStart,
				  BITMASK_DATA);
      startTime += timingStart;
      if (released) {
	scanCode |= 0x80;
      }
      scanCode = (scanCode << 1) | 0x01;
      for (int i = 0; i < 9; i++) {
	scheduler.addScheduledEvent(this,
				    startTime,
				    (scanCode & BITMASK_DATA) | BITMASK_CLOCK);
	scheduler.addScheduledEvent(this,
				    startTime + timingClockHigh,
				    scanCode & BITMASK_DATA);
	scanCode >>= 1;
	scheduler.addScheduledEvent(this,
				    startTime + timingDataChange,
				    scanCode & BITMASK_DATA);
	startTime += timingClockSlot;
      }
      scheduler.addScheduledEvent(this, startTime, BITMASK_CLOCK);
      timeout = startTime + timingTimeout;
      log.finer("Scan code scheduled for transmission");
    } else {
      log.finer("Scan code ignored, keyboard in reset condition");
    }
  }

  // for description see CPUEventOwner
  @Override
  public void performScheduledEvent(final int parameter) {
    portData = parameter;
    log.finer(String.format("Output data set to %02x", parameter));
    if (((parameter & BITMASK_CLOCK) == 0) && interruptMask) {
      if ((vector != -1) && Parameters.cpu.isIE()) {
	Parameters.cpu.requestInterrupt(vector);
	log.finer("Interrupt requested");
      } else {
	interruptFlag = true;
	log.finer("Interrupt flag set");
      }
    }
  }
}
