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

import java.util.Map;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import cz.pecina.retro.common.Parameters;

import cz.pecina.retro.cpu.IONode;
import cz.pecina.retro.cpu.IOPin;
import cz.pecina.retro.cpu.CPUEventOwner;
import cz.pecina.retro.cpu.CPUScheduler;

import cz.pecina.retro.gui.GenericButton;
import cz.pecina.retro.gui.Counter;
import cz.pecina.retro.gui.BlinkLED;

/**
 * Tape recorder hardware.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class TapeRecorderHardware implements CPUEventOwner {

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

  // multiplier for FF/REWIND operation
  private static final int FAST_MULTIPLIER = 8;

  // the tape recorder interface
  private TapeRecorderInterface tapeRecorderInterface;
  
  // states of the tape recorder
  private enum TapeRecorderState {STOPPED, PLAY, RECORD, REWIND, FF};

  // current tape recorder state
  private TapeRecorderState tapeRecorderState = TapeRecorderState.STOPPED;

  // tape in the tape recorder
  private Tape tape = new Tape();
  
  // output level
  private int output;

  // pulse counters for VU-meter
  private int outPulseCount, inPulseCount;

  // CPU scheduler
  private final CPUScheduler scheduler = Parameters.cpu.getCPUScheduler();

  // input pin
  private final InPin inPin = new InPin();

  // output pin
  private final OutPin outPin = new OutPin();

  // button layout
  private final TapeRecorderButtonsLayout tapeRecorderButtonsLayout =
    new TapeRecorderButtonsLayout();

  // buttons
  private GenericButton recordButton = tapeRecorderButtonsLayout
    .getButton(TapeRecorderButtonsLayout.BUTTON_POSITION_RECORD);
  private GenericButton playButton = tapeRecorderButtonsLayout
    .getButton(TapeRecorderButtonsLayout.BUTTON_POSITION_PLAY);
  private GenericButton rewindButton = tapeRecorderButtonsLayout
    .getButton(TapeRecorderButtonsLayout.BUTTON_POSITION_REWIND);
  private GenericButton ffButton = tapeRecorderButtonsLayout
    .getButton(TapeRecorderButtonsLayout.BUTTON_POSITION_FF);
  private GenericButton stopButton = tapeRecorderButtonsLayout
    .getButton(TapeRecorderButtonsLayout.BUTTON_POSITION_STOP);
  private GenericButton pauseButton = tapeRecorderButtonsLayout
    .getButton(TapeRecorderButtonsLayout.BUTTON_POSITION_PAUSE);
  private GenericButton ejectButton = tapeRecorderButtonsLayout
    .getButton(TapeRecorderButtonsLayout.BUTTON_POSITION_EJECT);

  // counter
  private final Counter counter =
    new Counter(NUMBER_COUNTER_DIGITS, "basic", "white");

  // counter reset button
  private final TapeRecorderCounterResetButton counterResetButton =
    new TapeRecorderCounterResetButton("toolTip.reset");

  // VU-meter
  private final VUMeter vumeter = new VUMeter();
 
  // recording LED
  private final BlinkLED recordingLED = new BlinkLED("small", "red");

  // latest position of the head, in clock cycles
  private long position;

  // latest time, in clock cycles
  private long time;

  // speed of the tape, relative
  private long speed;

  // counter offset at start of tape, in clock cycles
  private long offset;

  // start of the curent pulse
  private long pulseStart;

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
    recordButton.addMouseListener(new RecordListener());
    playButton.addMouseListener(new PlayListener());
    rewindButton.addMouseListener(new RewindListener());
    ffButton.addMouseListener(new FFListener());
    stopButton.addMouseListener(new StopListener());
    pauseButton.addMouseListener(new PauseListener());
    counterResetButton.addMouseListener(new CounterResetListener());
      
    log.fine("New TapeRecorderHardware created");
  }

  /**
   * Resets the tape position and counter.
   */
  public void resetTape() {
    offset -= position;
    position = 0;
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
   * Gets the tape recorder buttons layout.
   *
   * @return the tape recorder buttons layout
   */
  public TapeRecorderButtonsLayout getTapeRecorderButtonsLayout() {
    return tapeRecorderButtonsLayout;
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
   * Gets the tape recorder counter reset button.
   *
   * @return the tape recorder counter reset button
   */
  public TapeRecorderCounterResetButton getTapeRecorderCounterResetButton() {
    return counterResetButton;
  }

  // get system clock
  private long getTime() {
    return Parameters.systemClockSource.getSystemClock();
  }

  // resets the tape recorder counter
  private void resetCounter() {
    offset = getTime();
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

  // input pin
  private class InPin extends IOPin {
    private int level;
    
    // for description see IOPin
    @Override
    public void notifyChange() {
      final int newLevel = IONode.normalize(queryNode());
      if ((tapeRecorderState == TapeRecorderState.RECORD) &&
	  (newLevel != level)) {
	if (!pauseButton.isPressed()) {
	  update();
	  if (pulseStart == -1) {
	    pulseStart = position;
	  } else {
	    if (position > pulseStart) {
	      tape.subMap(pulseStart, true, position, true).clear();
	      for (;;) {
		final Map.Entry<Long,Long> entry = tape.floorEntry(position);
		if ((entry == null) ||
		    ((entry.getKey() + entry.getValue()) < position)) {
		  break;
		}
		tape.remove(entry.getKey());
	      }
	      tape.put(pulseStart, position - pulseStart);
	    }
	    pulseStart = -1;
	  }
	}
	outPulseCount++;
      }
      level = newLevel;
    }
  }

  // output pin
  private class OutPin extends IOPin {

    // for description see IOPin
    @Override
    public int query() {
      return output;
    }
  }

  // schedule next pulse
  private void schedule() {
    update();
    final Map.Entry<Long,Long> entry = tape.ceilingEntry(position + 1);
    if (entry != null) {
      scheduler.addScheduledEvent(
        this,
	time + entry.getKey() - position,
	1);
      scheduler.addScheduledEvent(
        this,
	time + entry.getKey() + entry.getValue() - position,
	0);
    }
    inPulseCount++;
    log.finest("Pulse scheduled");
  }

  // for description see CPUEventOwner
  @Override
  public void performScheduledEvent(final int parameter) {
    output = parameter;
    outPin.notifyChangeNode();
    if ((tapeRecorderState == TapeRecorderState.PLAY) &&
	!pauseButton.isPressed() &&
	(parameter == 0)) {
      schedule();
    }
    log.finest("Event performed, output is now: " + output);
  }

  // update position
  private void update() {
    final long newTime = getTime();
    if (!pauseButton.isPressed() ||
	(tapeRecorderState == TapeRecorderState.REWIND) ||
	(tapeRecorderState == TapeRecorderState.FF)) {
      position += (newTime - time) * speed;
      if (position < 0) {
	position = 0;
	speed = 0;
	rewindButton.setPressed(false);
	tapeRecorderState = TapeRecorderState.STOPPED;
      }
    }
    time = newTime;
  }

  /**
   * Stops the tape recorder;
   */
  public void stop() {
    if (tapeRecorderState != TapeRecorderState.STOPPED) {
      update();
      speed = 0;
      pulseStart = -1;
      inPulseCount = outPulseCount = 0;
      recordButton.setPressed(false);
      playButton.setPressed(false);
      rewindButton.setPressed(false);
      ffButton.setPressed(false);
      recordingLED.setState(false);
      tapeRecorderState = TapeRecorderState.STOPPED;
      log.finer("Tape recorder stopped");
    }
  }

  // record button listener
  private class RecordListener extends MouseAdapter {

    // for description see MouseListener
    @Override
    public void mousePressed(final MouseEvent event) {
      if (tapeRecorderState != TapeRecorderState.RECORD) {
	stop();
	speed = 1;
	recordButton.setPressed(true);
	playButton.setPressed(true);
	if (pauseButton.isPressed()) {
	  recordingLED.setState(BLINK_ON, BLINK_OFF);
	} else {
	  recordingLED.setState(true);
	}
	tapeRecorderState = TapeRecorderState.RECORD;
	log.fine("Record button pressed");
      }
    }
  }
  
  // play button listener
  private class PlayListener extends MouseAdapter {

    // for description see MouseListener
    @Override
    public void mousePressed(final MouseEvent event) {
      if ((tapeRecorderState != TapeRecorderState.PLAY) &&
	  (tapeRecorderState != TapeRecorderState.RECORD)) {
	stop();
	speed = 1;
	schedule();
	playButton.setPressed(true);
	tapeRecorderState = TapeRecorderState.PLAY;
	log.fine("Play button pressed");
      }
    }
  }
  
  // rewind button listener
  private class RewindListener extends MouseAdapter {

    // for description see MouseListener
    @Override
    public void mousePressed(final MouseEvent event) {
      if (tapeRecorderState != TapeRecorderState.REWIND) {
	stop();
	speed = -FAST_MULTIPLIER;
	rewindButton.setPressed(true);
	tapeRecorderState = TapeRecorderState.REWIND;
	log.fine("Rewind button pressed");
      }
    }
  }
  
  // FF button listener
  private class FFListener extends MouseAdapter {

    // for description see MouseListener
    @Override
    public void mousePressed(final MouseEvent event) {
      if (tapeRecorderState != TapeRecorderState.FF) {
	stop();
	speed = FAST_MULTIPLIER;
	ffButton.setPressed(true);
	tapeRecorderState = TapeRecorderState.FF;
	log.fine("FF button pressed");
      }
    }
  }
  
  // stop button listener
  private class StopListener extends MouseAdapter {

    // for description see MouseListener
    @Override
    public void mousePressed(final MouseEvent event) {
      if (tapeRecorderState != TapeRecorderState.STOPPED) {
	stop();
	log.fine("Stop button pressed");
      }
    }
  }
  
  // pause button listener
  private class PauseListener extends MouseAdapter {

    // for description see MouseListener
    @Override
    public void mousePressed(final MouseEvent event) {
      if (tapeRecorderState == TapeRecorderState.RECORD) {
	if (pauseButton.isPressed()) {
	  recordingLED.setState(BLINK_ON, BLINK_OFF);
	} else {
	  recordingLED.setState(true);
	}
      }
      log.fine("Pause button pressed");
    }
  }
  
  // counter reset button listener
  private class CounterResetListener extends MouseAdapter {

    // for description see MouseListener
    @Override
    public void mousePressed(final MouseEvent event) {
      update();
      offset = position;
      log.fine("Counter reset");
    }
  }
  
  // execute periodic tape recorder processing
  public void process() {
    log.finest("Tape recorder processing started");

    update();

    // update counter
    counter.setState(((double)(position - offset))
  		     / tapeRecorderInterface.tapeSampleRate);

    // update VU-meter
    if (tapeRecorderState == TapeRecorderState.RECORD) {
      int i;
      for (i = 0; i < VUMeter.VUMETER_MAX; i++) {
  	if (outPulseCount < (tapeRecorderInterface.vuRecConstant * i)) {
  	  break;
  	}
      }
      vumeter.setState(i);
    } else
    if ((tapeRecorderState == TapeRecorderState.PLAY) && !pauseButton.isPressed()) {
      int i;
      for (i = 0; i < VUMeter.VUMETER_MAX; i++) {
  	if (inPulseCount < (tapeRecorderInterface.vuPlayConstant * i)) {
  	  break;
  	}
      }
      vumeter.setState(i);
    } else {
      vumeter.setState(0);
    }
    outPulseCount -= outPulseCount >> 3;
    inPulseCount -= inPulseCount >> 3;
  }
}
