/* Sound.java
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

package cz.pecina.retro.common;

import java.util.logging.Logger;

import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.LineUnavailableException;

/**
 * The common sound interface of the emulator.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class Sound {

  // static logger
  private static final Logger log =
    Logger.getLogger(Sound.class.getName());

  /**
   * The channel assigned to the tape recorder.
   */
  public static final int TAPE_RECORDER_CHANNEL = 0;

  /**
   * The channel assigned to the internal speaker.
   */
  public static final int SPEAKER_CHANNEL = 1;

  // overlap factor
  private static final float OVERLAP = 1.0f;
  
  // size of frame in number of bytes
  private static final int FRAME_SIZE = 2;

  // sample rate
  private float sampleRate;

  // average number of samples per emulator period
  private int samplesPerPeriod;

  // overlap in number of samples
  private int overlap;

  // buffer length is number of samples
  private int bufferLength;

  // number of sound channels
  private int numberChannels;

  // sound lines
  private SourceDataLine[] lines;

  // gain controls and limits
  private FloatControl[] gainControls;
  private float[] gainMinima, gainMaxima;
  
  // mute controls
  private BooleanControl[] muteControls;
    
  // last CPU clock
  private long lastCPUClock;
  
  // last frame positions
  private long[] framePositions;

  // last levels, per channel
  private boolean[] lastLevels;

  // queues of values, per channel
  private List<TreeMap<Long,Boolean>> queues = new ArrayList<>();
  
  // audio buffer
  private byte[] buffer;

  // silence buffer
  private byte[] silence;
  
  // get system clock
  private long getTime() {
    return Parameters.systemClockSource.getSystemClock();
  }

  /**
   * Creates an instance of an audio interface.
   * <p>
   * The audio is always 16-bit signed PCR, monoaural type; only
   * the sample rate and the number of mixer channels are user-selectable.
   *
   * @param sampleRate     the sample rate in sampler per second
   * @param numberChannels number of channels to be mixed
   */
  public Sound(final float sampleRate, final int numberChannels) {
    log.fine("New Sound creation started");
    assert sampleRate > 1000;
    assert numberChannels > 0;
    assert Parameters.timerPeriod > 0;
    assert Parameters.timerCycles > 0;
    
    // check for existence of Sound object
    if (Parameters.sound != null) {
      log.fine("Error, Sound already exists");
      throw Application.createError(this, "sound.exists");
     }

    // update fields
    this.sampleRate = sampleRate;
    this.numberChannels = numberChannels;
    
    // calculate sizes
    samplesPerPeriod = Math.round((sampleRate * Parameters.timerPeriod) / 1000);
    log.fine("Samples per period: " + samplesPerPeriod);
    overlap = Math.round(samplesPerPeriod * OVERLAP);
    log.fine("Overlap: " + overlap);
    bufferLength = samplesPerPeriod + overlap;
    log.fine("Buffer length: " + bufferLength);
    
    // set up audio format
    final AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
    final DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
    if (!AudioSystem.isLineSupported(info)) {
      log.fine("Sound hardware is not available");
      return;
    }

    // prepare arrays
    lines = new SourceDataLine[numberChannels];
    gainControls = new FloatControl[numberChannels];
    gainMinima = new float[numberChannels];
    gainMaxima = new float[numberChannels];
    muteControls = new BooleanControl[numberChannels];
    framePositions = new long[numberChannels];
    lastLevels = new boolean[numberChannels];
    buffer = new byte[bufferLength * FRAME_SIZE];
    silence = new byte[bufferLength * FRAME_SIZE];

    // get audio lines and controls
    try {
      for (int channel = 0; channel < numberChannels; channel++) {
	final SourceDataLine line = (SourceDataLine)AudioSystem.getLine(info);
	line.open(format);
	lines[channel] = line;
	if (!line.isControlSupported(FloatControl.Type.MASTER_GAIN) ||
	    !line.isControlSupported(BooleanControl.Type.MUTE)) {
	  log.fine("Required control not supported on one of the lines");
	  throw new LineUnavailableException("Required control not supported");
	}
	gainControls[channel] =
	  (FloatControl)line.getControl(FloatControl.Type.MASTER_GAIN);
	gainMinima[channel] = gainControls[channel].getMinimum();
	gainMaxima[channel] = gainControls[channel].getMaximum();
	muteControls[channel] =
	  (BooleanControl)line.getControl(BooleanControl.Type.MUTE);
	queues.add(new TreeMap<>());
      }
    } catch (final LineUnavailableException exception) {
      log.fine("Failed to open audio lines, no sound available");
      return;
    }
    log.fine("Audio lines set up");

    // write silence
    for (int channel = 0; channel < numberChannels; channel++) {
      lines[channel].write(silence, 0, bufferLength * FRAME_SIZE);
    }
    log.fine("Silence fed into audio lines");

    Parameters.sound = this;

    log.fine("New Sound creation completed");
  }

  /**
   * Starts the audio system.
   */ 
  public void start() {

    // check if audio supported by hardware
    if (Parameters.sound == null) {
      log.fine("Audio not supported by hardware");
      return;
    }

    // start all lines
    for (int channel = 0; channel < numberChannels; channel++) {
      lines[channel].start();
    }
    log.fine("Audio lines started");
  }
  
  /**
   * Updates the audio system.
   */ 
  public void update() {
    log.finer("Timer event started");

    // check if audio supported by hardware
    if (Parameters.sound == null) {
      log.finer("Audio not supported by hardware");
      return;
    }

    // check if emulated CPU running
    final long clock = getTime();
    final int ticks = (int)(clock - lastCPUClock);
    log.finer("Elapsed time in CPU clock ticks: " + ticks);
    final boolean running = (ticks >= Parameters.timerCycles);
    log.finer("Emulated CPU is " + (running ? "running" : "stopped"));
    
    // process channels
    for (int channel = 0; channel < numberChannels; channel++) {
      log.finer("Processing channel: " + channel);

      // get number of frames needed
      final long framePosition = lines[channel].getLongFramePosition();
      long framesNeeded = samplesPerPeriod;
      framePositions[channel] = framePosition;

      // if more than buffer size, feed silence
      while (framesNeeded > bufferLength) {
	final int excess = (int)Math.max(framesNeeded - bufferLength, bufferLength);
	lines[channel].write(silence, 0, excess * FRAME_SIZE);
	framesNeeded -= excess;
      }

      if (running) {   // if running, supply data
	
	// get per-channel parameters
	final TreeMap<Long,Boolean> queue = queues.get(channel);
	boolean level = lastLevels[channel];
	log.finer("Last level: " + level);
	final float samplesPerCPUClock = (float)framesNeeded / (float)ticks;
	log.finer("Samples per CPU clock tick: " + samplesPerCPUClock);

	// create samples
	int i = 0;
	for (long position: queue.keySet()) {
	  int address =
	    Math.round((position - lastCPUClock) * samplesPerCPUClock);
	  log.finest("Address: " + address);
	  while (i < (address - 1)) {
	    buffer[i * FRAME_SIZE] = (byte)(level ? 0xff : 0x00);
	    buffer[(i++ * FRAME_SIZE) + 1] = (byte)(level ? 0x7f : 0x80);
	  }
	  level = queue.get(position);
	}
	while (i < framesNeeded) {
	  buffer[i * FRAME_SIZE] = (byte)(level ? 0xff : 0x00);
	  buffer[(i++ * FRAME_SIZE) + 1] = (byte)(level ? 0x7f : 0x80);
	}

	// remove queued data
	queue.clear();

	// write buffer
	lines[channel].write(buffer, 0, (int)framesNeeded * FRAME_SIZE);

	// set last level
	lastLevels[channel] = level;
	
      } else {  // if not running, feed silence
	lines[channel].write(silence, 0, (int)framesNeeded * FRAME_SIZE);
	log.finest("Silence fed to line");
      }
    }

    // update last clock
    lastCPUClock = clock;
  }

  /**
   * Writes a level to the sound interface.
   *
   * @param channel the channel number
   * @param level   the level valid for the current CPU time
   */
  public void write(final int channel, final boolean level) {
    log.finest("Writing to channel: " + channel + ", level: " + level);
    assert (channel >= 0) && (channel < numberChannels);
    queues.get(channel).put(getTime(), level);
  }
}
