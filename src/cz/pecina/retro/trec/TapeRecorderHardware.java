/* TapeRecorderHardware.java
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

package cz.pecina.retro.trec;

import java.util.logging.Logger;
import java.util.Iterator;
import cz.pecina.retro.common.Parameters;
import cz.pecina.retro.cpu.IOPin;
import cz.pecina.retro.gui.Counter;
import cz.pecina.retro.gui.BlinkLED;

/**
 * Tape recorder hardware.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class TapeRecorderHardware {

  // static logger
  private static final Logger log =
    Logger.getLogger(TapeRecorderHardware.class.getName());

  /**
   * Number of counter digits.
   */
  public static final int NUMBER_COUNTER_DIGITS = 4;

  // blinking parameters for the recording LED
  private static final int BLINK_ON = 750;
  private static final int BLINK_OFF = 750;

  // divisor determining the speed of tape recorder counter
  private static final double COUNTER_DIVISOR = 1e7 / 9.0;
  
  // multiplier for FF/REWIND operation
  private static final int FAST_MULTIPLIER = 8;

  // the tape recorder interface
  private TapeRecorderInterface tapeRecorderInterface;
  
  // states of the tape recorder
  private enum TapeRecorderState {STOPPED, PLAY, RECORD, REWIND, FF};

  // the tape in the tape recorder
  private final Tape tape = new Tape();

  // current tape recorder state
  private TapeRecorderState tapeRecorderState = TapeRecorderState.STOPPED;

  // true if the tape recorder is paused
  private boolean paused;

  // input pin
  private final InPin inPin = new InPin();

  // output pin
  private final OutPin outPin = new OutPin();

  // buttons
  private final TapeRecorderButtonsLayout tapeRecorderButtonsLayout =
    new TapeRecorderButtonsLayout();

  // counter
  private final Counter counter =
    new Counter(NUMBER_COUNTER_DIGITS, "basic", "white");

  // counter reset button
  private final TapeRecorderCounterResetButton tapeRecorderCounterResetButton =
    new TapeRecorderCounterResetButton("toolTip.reset");

  // VU-meter
  private final VUMeter vumeter = new VUMeter();
 
  // recording LED
  private final BlinkLED recordingLED = new BlinkLED("small", "red");

  // temporary recording data
  private final Tape recording = new Tape();

  // state variables
  private long tapePosition, lastCycleCounter, counterOffset, pulseStart,
    pulseLast, startCycleCounter, startPosition;
  private int pulseCount;
  private boolean replay;
    
  /**
   * Gets the tape recorder interface object.
   *
   * @return <code>TapeRecorderInterface</code> object
   */
  public TapeRecorderInterface getTapeRecorderInterface() {
    return tapeRecorderInterface;
  }

  /**
   * Gets the output pin.
   *
   * @return the output pin
   */
  public IOPin getOutPin() {
    return outPin;
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
   * Creates a new tape recorder hardware object.
   *
   * @param tapeRecorderInterface the tape recorder interface
   */
  public TapeRecorderHardware(final TapeRecorderInterface tapeRecorderInterface) {
    log.fine("New TapeRecorderHardware creation started");
    this.tapeRecorderInterface = tapeRecorderInterface;
    log.fine("New TapeRecorderHardware created");
  }

  /**
   * Gets the tape in the tape recorder.
   *
   * @return the tape in the tape recorder
   */
  public Tape getTape() {
    return tape;
  }

  /**
   * Gets the tape recorder keyboard layout.
   *
   * @return the tape recorder keyboard layout
   */
  public TapeRecorderButtonsLayout getTapeRecorderButtonsLayout() {
    return tapeRecorderButtonsLayout;
  }

  /**
   * Sets the position of the tape.
   *
   * @param position the position of the tape, in CPU cycles
   */
  public void setTapePosition(final long position) {
    assert position >= 0;
    tapePosition = position;
  }

  /**
   * Gets the tape recorder counter.
   *
   * @return the tape recorder counter
   */
  public Counter getCounter() {
    return counter;
  }

  /**
   * Sets the counter offset.
   *
   * @param n the counter offset, in CPU cycles
   */
  public void setCounterOffset(final long n) {
    counterOffset = n;
  }

  /**
   * Gets the tape recorder counter reset button.
   *
   * @return the tape recorder counter reset button
   */
  public TapeRecorderCounterResetButton getTapeRecorderCounterResetButton() {
    return tapeRecorderCounterResetButton;
  }

  /**
   * Resets the tape recorder counter.
   */
  public void resetCounter() {
    counterOffset = tapePosition;
  }

  /**
   * Resets the tape position and counter.
   */
  public void resetTape() {
    setTapePosition(0);
    resetCounter();
  }

  /**
   * Gets the VU-meter.
   *
   * @return the VU-meter
   */
  public VUMeter getVUMeter() {
    return vumeter;
  }

  /**
   * Gets the recording LED.
   *
   * @return the recording LED
   */
  public BlinkLED getRecordingLED() {
    return recordingLED;
  }

  // in pin
  private class InPin extends IOPin {
    private boolean outState;

    // for description see IOPin
    @Override
    public void notifyChange() {
      final boolean message = (queryNode() == 0);
      if (message != outState) {
	log.finest("Changed level detected: " + message);
	outState = message;
	if (tapeRecorderState == TapeRecorderState.RECORD) {
	  final long currCycleCounter =
	    Parameters.systemClockSource.getSystemClock() -
	    startCycleCounter + startPosition;
	  pulseCount++;
	  if (tapeRecorderInterface.holdOffPeriod > 0) {
	    if (currCycleCounter > pulseLast) {
	      if (!paused && (pulseStart != -1)) {
		recording.put(pulseStart, pulseLast - pulseStart);
	      }
	      pulseStart = currCycleCounter;
	    }
	    pulseLast = currCycleCounter + tapeRecorderInterface.holdOffPeriod;
	  } else if (!paused) {
	    if (message) {
	      pulseStart = currCycleCounter;
	    } else {
	      if ((currCycleCounter > pulseStart) && (pulseStart != -1)) {
		recording.put(pulseStart, currCycleCounter - pulseStart);
	      }
	      pulseStart = -1;
	    }
	  }		
	}
      }
    }
  }

  // output pin
  private class OutPin extends IOPin {

    // for description see IOPin
    @Override
    public int query() {
      if (replay) {
	final long preciseTapePosition =
	  startPosition + Parameters.systemClockSource.getSystemClock() -
	  startCycleCounter;
	final Long key = tape.lowerKey(preciseTapePosition + 1);
	if ((key != null) && ((key + tape.get(key)) > preciseTapePosition)) {
	  return 0;
	}
      }
      return 1;
    }
  }
    
  // start recording
  private void startRecording() {
    recording.clear();
    pulseStart = pulseLast = -1;
    startCycleCounter = Parameters.systemClockSource.getSystemClock();
    startPosition = tapePosition;
    log.finer("Recording started");
  }

  // stop recording
  private void stopRecording() {
    if (tapeRecorderInterface.holdOffPeriod > 0) {
      if (pulseStart != -1) {
	recording.put(pulseStart, pulseLast - pulseStart);
      }
    } else {
      if (pulseStart != -1) {
	recording.put(pulseStart, Parameters.systemClockSource.getSystemClock() -
	  startCycleCounter + startPosition - pulseStart);
      }
    }
    for (Iterator<Long> iter = tape.navigableKeySet().iterator();
	 iter.hasNext();
	 ) {
      final long key = iter.next();
      if (((key + tape.get(key)) >= startPosition) && (key <= tapePosition)) {
	iter.remove();
      }
    }
    for (long start: recording.navigableKeySet()) {
      final long duration = recording.get(start);
      tape.put(start, duration);
    }
    log.finer("Recording stopped");
  }

  // start replay
  private void startReplay() {
    startCycleCounter = Parameters.systemClockSource.getSystemClock();
    startPosition = tapePosition;
    replay = true;
    log.finer("Replay started");
  }

  // stop replay
  private void stopReplay() {
    replay = false;
    log.finer("Replay stopped");
  }

  // execute periodic tape recorder processing
  public void process() {
    log.finest("Tape recorder processing started");

    switch (tapeRecorderState) {
      case STOPPED:
	log.finest("Stopped");
	if (tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_RECORD).isPressed()) {
	  log.fine("RECORD pressed");
	  tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_PLAY).setPressed(true);
	  if (paused) {
	    recordingLED.setState(BLINK_ON, BLINK_OFF);
	  } else {
	    recordingLED.setState(true);
	    startRecording();
	  }
	  tapeRecorderState = TapeRecorderState.RECORD;
	} else if (tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
		   .BUTTON_POSITION_PLAY).isPressed()) {
	  log.fine("PLAY pressed");
	  if (!paused) {
	    startReplay();
	  }
	  tapeRecorderState = TapeRecorderState.PLAY;
	} else if (tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
		   .BUTTON_POSITION_REWIND).isPressed()) {
	  log.fine("REWIND pressed");
	  tapeRecorderState = TapeRecorderState.REWIND;
	} else if (tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
		   .BUTTON_POSITION_FF).isPressed()) {
	  log.fine("FF pressed");
	  tapeRecorderState = TapeRecorderState.FF;
	} else if (tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
		   .BUTTON_POSITION_PAUSE).isPressed() != paused) {
	  log.fine("PAUSE pressed");
	  paused = !paused;
	} else if (tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
		   .BUTTON_POSITION_EJECT).isPressed()) {
	  log.fine("EJECT pressed");
	}
	break;
      case RECORD:
	log.finest("Recording");
	if (tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_REWIND).isPressed()) {
	  log.fine("REWIND pressed");
	  tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_RECORD).setPressed(false);
	  tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_PLAY).setPressed(false);
	  recordingLED.setState(false);
	  if (!paused) {
	    stopRecording();
	  }
	  tapeRecorderState = TapeRecorderState.REWIND;
	} else if (tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
		   .BUTTON_POSITION_FF).isPressed()) {
	  log.fine("FF pressed");
	  tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_RECORD).setPressed(false);
	  tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_PLAY).setPressed(false);
	  recordingLED.setState(false);
	  if (!paused) {
	    stopRecording();
	  }
	  tapeRecorderState = TapeRecorderState.FF;
	} else if (tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
		   .BUTTON_POSITION_STOP).isPressed()) {
	  log.fine("STOP pressed");
	  tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_RECORD).setPressed(false);
	  tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_PLAY).setPressed(false);
	  recordingLED.setState(false);
	  if (!paused) {
	    stopRecording();
	  }
	  tapeRecorderState = TapeRecorderState.STOPPED;
	} else if (tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	           .BUTTON_POSITION_PAUSE).isPressed() != paused) {
	  log.fine("PAUSE pressed");
	  if (paused) {
	    recordingLED.setState(true);
	    startRecording();
	  } else {
	    recordingLED.setState(BLINK_ON, BLINK_OFF);
	    stopRecording();
	  }
	  paused = !paused;
	} else if (tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
		   .BUTTON_POSITION_EJECT).isPressed()) {
	  log.fine("EJECT pressed");
	  tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_RECORD).setPressed(false);
	  tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_PLAY).setPressed(false);
	  recordingLED.setState(false);
	  if (!paused) {
	    stopRecording();
	  }
	  tapeRecorderState = TapeRecorderState.STOPPED;
	} else {
	  if (!paused) {
	    tapePosition +=
	      Parameters.systemClockSource.getSystemClock() - lastCycleCounter;
	  }
	  if (tapePosition >= tapeRecorderInterface.getMaxTapeLength()) {
	    log.fine("End of tape");
	    tapePosition = tapeRecorderInterface.getMaxTapeLength();
	    tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	      .BUTTON_POSITION_RECORD).setPressed(false);
	    tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	      .BUTTON_POSITION_PLAY).setPressed(false);
	    recordingLED.setState(false);
	    if (!paused) {
	      stopRecording();
	    }
	    tapeRecorderState = TapeRecorderState.STOPPED;
	  }
	}
	break;
      case PLAY:
	log.finest("Replaying");
	if (tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_RECORD).isPressed()) {
	  log.fine("RECORD pressed");
	  if (paused) {
	    recordingLED.setState(BLINK_ON, BLINK_OFF);
	  } else {
	    stopReplay();
	    recordingLED.setState(true);
	    startRecording();
	  }
	  tapeRecorderState = TapeRecorderState.RECORD;
	} else if (tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
		   .BUTTON_POSITION_REWIND).isPressed()) {
	  log.fine("REWIND pressed");
	  tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_PLAY).setPressed(false);
	  if (!paused) {
	    stopReplay();
	  }
	  tapeRecorderState = TapeRecorderState.REWIND;
	} else if (tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	           .BUTTON_POSITION_FF).isPressed()) {
	  log.fine("FF pressed");
	  tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_PLAY).setPressed(false);
	  if (!paused) {
	    stopReplay();
	  }
	  tapeRecorderState = TapeRecorderState.FF;
	} else if (tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
		  .BUTTON_POSITION_STOP).isPressed()) {
	  log.fine("STOP pressed");
	  tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_PLAY).setPressed(false);
	  if (!paused) {
	    stopReplay();
	  }
	  tapeRecorderState = TapeRecorderState.STOPPED;
	} else if (tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
		   .BUTTON_POSITION_PAUSE).isPressed() != paused) {
	  log.fine("PAUSE pressed");
	  if (paused) {
	    startReplay();
	  } else {
	    stopReplay();
	  }
	  paused = !paused;
	} else if (tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	           .BUTTON_POSITION_EJECT).isPressed()) {
	  log.fine("EJECT pressed");
	  tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_PLAY).setPressed(false);
	  if (!paused) {
	    stopReplay();
	  }
	  tapeRecorderState = TapeRecorderState.STOPPED;
	} else {
	  if (!paused) {
	    tapePosition += Parameters.systemClockSource.getSystemClock() -
	      lastCycleCounter;
	  }
	  if (tapePosition >= tapeRecorderInterface.getMaxTapeLength()) {
	    log.fine("End of tape");
	    tapePosition = tapeRecorderInterface.getMaxTapeLength();
	    tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	      .BUTTON_POSITION_RECORD).setPressed(false);
	    tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	      .BUTTON_POSITION_PLAY).setPressed(false);
	    if (!paused) {
	      stopReplay();
	    }
	    tapeRecorderState = TapeRecorderState.STOPPED;
	  }
	}
	break;
      case REWIND:
	log.finest("Rewinding");
	if (tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_RECORD).isPressed()) {
	  log.fine("RECORD pressed");
	  tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_REWIND).setPressed(false);
	  tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_RECORD).setPressed(true);
	  tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_PLAY).setPressed(true);
	  if (paused) {
	    recordingLED.setState(BLINK_ON, BLINK_OFF);
	  } else {
	    recordingLED.setState(true);
	    startRecording();
	  }
	  tapeRecorderState = TapeRecorderState.RECORD;
	} else if (tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
		   .BUTTON_POSITION_PLAY).isPressed()) {
	  log.fine("PLAY pressed");
	  tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_REWIND).setPressed(false);
	  tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_PLAY).setPressed(true);
	  if (!paused) {
	    startReplay();
	  }
	  tapeRecorderState = TapeRecorderState.PLAY;
	} else if (tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
		   .BUTTON_POSITION_FF).isPressed()) {
	  log.fine("FF pressed");
	  tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_REWIND).setPressed(false);
	  tapeRecorderState = TapeRecorderState.FF;
	} else if (tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	           .BUTTON_POSITION_STOP).isPressed()) {
	  log.fine("STOP pressed");
	  tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_REWIND).setPressed(false);
	  tapeRecorderState = TapeRecorderState.STOPPED;
	} else if (tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	           .BUTTON_POSITION_PAUSE).isPressed() != paused) {
	  log.fine("PAUSE pressed");
	  paused = !paused;
	} else if (tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
		   .BUTTON_POSITION_EJECT).isPressed()) {
	  log.fine("EJECT pressed");
	  tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_REWIND).setPressed(false);
	  tapeRecorderState = TapeRecorderState.STOPPED;
	} else {
	  tapePosition -= (Parameters.systemClockSource.getSystemClock() -
			   lastCycleCounter) * FAST_MULTIPLIER;
	  if (tapePosition < 0) {
	    log.fine("Beginning of tape");
	    tapePosition = 0;
	    tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	      .BUTTON_POSITION_REWIND).setPressed(false);
	    tapeRecorderState = TapeRecorderState.STOPPED;
	  }
	}
	break;
      case FF:
      default:
	log.finest("Fast-forwarding");
	if (tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_RECORD).isPressed()) {
	  log.fine("RECORD pressed");
	  tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_FF).setPressed(false);
	  tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_RECORD).setPressed(true);
	  tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_PLAY).setPressed(true);
	  if (paused) {
	    recordingLED.setState(BLINK_ON, BLINK_OFF);
	  } else {
	    recordingLED.setState(true);
	    startRecording();
	  }
	  tapeRecorderState = TapeRecorderState.RECORD;
	} else if (tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	           .BUTTON_POSITION_PLAY).isPressed()) {
	  log.fine("PLAY pressed");
	  tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_FF).setPressed(false);
	  tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_PLAY).setPressed(true);
	  if (!paused) {
	    startReplay();
	  }
	  tapeRecorderState = TapeRecorderState.PLAY;
	} else if (tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
		   .BUTTON_POSITION_REWIND).isPressed()) {
	  log.fine("REWIND pressed");
	  tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_FF).setPressed(false);
	  tapeRecorderState = TapeRecorderState.REWIND;
	} else if (tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
		   .BUTTON_POSITION_STOP).isPressed()) {
	  log.fine("STOP pressed");
	  tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_FF).setPressed(false);
	  tapeRecorderState = TapeRecorderState.STOPPED;
	} else if (tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	           .BUTTON_POSITION_PAUSE).isPressed() != paused) {
	  log.fine("PAUSE pressed");
	  paused = !paused;
	} else if (tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
		   .BUTTON_POSITION_EJECT).isPressed()) {
	  log.fine("EJECT pressed");
	  tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	    .BUTTON_POSITION_FF).setPressed(false);
	  tapeRecorderState = TapeRecorderState.STOPPED;
	} else {
	  tapePosition += (Parameters.systemClockSource.getSystemClock() -
			   lastCycleCounter) * FAST_MULTIPLIER;
	  if (tapePosition >= tapeRecorderInterface.getMaxTapeLength()) {
	    log.fine("End of tape");
	    tapePosition = tapeRecorderInterface.getMaxTapeLength();
	    tapeRecorderButtonsLayout.getButton(TapeRecorderButtonsLayout
	      .BUTTON_POSITION_FF).setPressed(false);
	    tapeRecorderState = TapeRecorderState.STOPPED;
	  }
	}
	break;
    }
    counter.setState(((double)(tapePosition - counterOffset))
		     / COUNTER_DIVISOR);
    lastCycleCounter = Parameters.systemClockSource.getSystemClock();
    if (tapeRecorderCounterResetButton.isPressed()) {
      log.finest("Resetting counter");
      resetCounter();
    }

    if (tapeRecorderState == TapeRecorderState.RECORD) {
      int i;
      for (i = 0; i < VUMeter.VUMETER_MAX; i++) {
	if ((9 * pulseCount) < (75 * tapeRecorderInterface.timerPeriod * i)) {
	  break;
	}
      }
      vumeter.setState(i);
    } else if ((tapeRecorderState == TapeRecorderState.PLAY) && !paused) {
      for (long val: tape.subMap(tapePosition - Parameters.timerCycles,
				 tapePosition).values()) {
	pulseCount += (int)val;
      }
      int i;
      for (i = 0; i < VUMeter.VUMETER_MAX; i++) {
	if ((9 * pulseCount) < (8000 * tapeRecorderInterface.timerPeriod * i)) {
	  break;
	}
      }
      vumeter.setState(i);
    } else {
      vumeter.setState(0);
    }
    pulseCount -= pulseCount >> 3;
  }
}
