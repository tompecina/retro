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
import java.util.logging.Level;

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
  private static final float OVERLAP = 1;
  
  // sample rate
  private float samplingRate;

  // number of samples per emulator period
  private int samplesPerPeriod;

  // overlap in number of samples
  private int overlap;

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
  
  // last levels, per channel
  private boolean[] lastLevels;

  // queues of values, per channel
  private List<TreeMap<Long,Boolean>> queues = new ArrayList<>();
  
  // audio buffer
  private byte[] buffer;

  // silence buffer
  private byte[] silence;

  // stabilization flag
  private boolean stable;
  
  // stabilization counter
  private long counter;
  
  // get system clock
  private long getTime() {
    return Parameters.systemClockSource.getSystemClock();
  }

  /**
   * Creates an instance of an audio interface.
   * <p>
   * The audio is always 8-bit signed PCR, monoaural type; only
   * the sample rate and the number of mixer channels are user-selectable.
   *
   * @param samplingRate   the sample rate in sampler per second
   * @param numberChannels number of channels to be mixed
   */
  public Sound(final int samplingRate, final int numberChannels) {
    log.fine("New Sound creation started");
    assert samplingRate > 1000;
    assert numberChannels > 0;
    assert Parameters.timerPeriod > 0;
    assert Parameters.timerCycles > 0;
    assert ((samplingRate * Parameters.timerPeriod) % 1000) == 0;
    
    // check for existence of Sound object
    if (Parameters.sound != null) {
      log.fine("Error, Sound already exists");
      throw Application.createError(this, "sound.exists");
     }
    Parameters.sound = this;

    // update fields
    this.samplingRate = samplingRate;
    this.numberChannels = numberChannels;
    
    // calculate sizes
    samplesPerPeriod = (samplingRate * Parameters.timerPeriod) / 1000;
    log.fine("Samples per period: " + samplesPerPeriod);
    overlap = Math.round(samplesPerPeriod * OVERLAP);
    log.fine("Overlap: " + overlap);
    
    // set up audio format
    final AudioFormat format = new AudioFormat(samplingRate, 8, 1, true, false);
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
    lastLevels = new boolean[numberChannels];
    buffer = new byte[samplesPerPeriod];
    silence = new byte[samplesPerPeriod + overlap];

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
      lines = null;
      log.fine("Failed to open audio lines, no sound available");
      return;
    }
    log.fine("Audio lines set up");

    // write silence
    for (int channel = 0; channel < numberChannels; channel++) {
      lines[channel].write(silence, 0, samplesPerPeriod + overlap);
    }
    log.fine("Silence fed into audio lines");

    log.fine("New Sound creation completed");
  }

  /**
   * Starts the audio system.
   */ 
  public void start() {

    // check if audio supported by hardware
    if (lines == null) {
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
    if (lines == null) {
      log.finer("Audio not supported by hardware");
      return;
    }

    // make 10 dry runs to let JVM stabilize
    log.finer("Counter: " + counter);
    if (++counter == 10) {
      log.finer("Audio stable");
      start();
      stable = true;
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

      if (running) {   // if running, supply data
	
	// get per-channel parameters
	final TreeMap<Long,Boolean> queue = queues.get(channel);
	boolean level = lastLevels[channel];
	log.finer("Last level: " + level);
	final float samplesPerCPUClock = (float)samplesPerPeriod / (float)ticks;
	log.finer("Samples per CPU clock tick: " + samplesPerCPUClock);

	// create samples
	int i = 0;
	for (long position: queue.keySet()) {
	  int address = Math.min(
	    Math.round((position - lastCPUClock) * samplesPerCPUClock),
	    samplesPerPeriod - 1);
	  log.finest("Address: " + address + ", level: " + level);
	  while (i < (address - 1)) {
	    buffer[i++] = (byte)(level ? 0x7f : 0x00);
	  }
	  level = queue.get(position);
	}
	while (i < samplesPerPeriod) {
	  buffer[i++] = (byte)(level ? 0x7f : 0x00);
	}

	// remove queued data
	queue.clear();

	// log buffer
	if (log.isLoggable(Level.FINEST)) {
	  final byte buffer0 = buffer[0];
	  for (i = 1; i < samplesPerPeriod; i++) {
	    if (buffer[i] != buffer0) {
	      break;
	    }
	  }
	  if (i == samplesPerPeriod) {
	    log.finest(String.format("Buffer: (repeated) %02x", buffer0));
	  } else {
	      StringBuilder s = new StringBuilder();
	      for (i = 0; i < samplesPerPeriod; i++) {
		s.append(String.format("%02x ", buffer[i]));
	      }
	      log.finest("Buffer: " + s.toString());
	  }
	}
	
	// write buffer
	if (stable) {

	  // log line state
	  log.finest("Line is " +
	    (lines[channel].isRunning() ? "running" : "stopped"));
	  log.finest("Line available: " + lines[channel].available() +
	    ", frame position: " + lines[channel].getLongFramePosition());

	  // write buffer
	  lines[channel].write(buffer, 0, samplesPerPeriod);
	  log.finest("Buffer fed to line");
	}

	// set last level
	lastLevels[channel] = level;
	
      } else {  // if not running, feed silence
	if (stable) {
	  lines[channel].write(silence, 0, samplesPerPeriod);
	  log.finest("Silence fed to line");
	}
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
    final long time = getTime();
    log.finest("Writing to channel: " + channel + ", level: " + level + ", at: " + time);
    assert (channel >= 0) && (channel < numberChannels);
    queues.get(channel).put(time, level);
  }

  /**
   * Sets the mute control.
   *
   * @param channel the channel number
   * @param mute    the new mute setting for the channel
   */
  public void setMute(final int channel, final boolean mute) {
    muteControls[channel].setValue(mute);
    log.fine("Mute on channel " + channel + " set to: " + mute);
  }

  /**
   * Gets the mute control.
   *
   * @param  channel the channel number
   * @return         the mute setting for the channel
   */
  public boolean getMute(final int channel) {
    final boolean mute = muteControls[channel].getValue();
    log.finer("Mute on channel " + channel + " is: " + mute);
    return mute;
  }

  /**
   * Sets the volume (gain) control.
   *
   * @param channel the channel number
   * @param mute    the new volume (gain) setting for the channel, 0.0-1.0
   */
  public void setVolume(final int channel, final float volume) {
    gainControls[channel].setValue(gainMinima[channel] +
      (volume * (gainMaxima[channel] - gainMinima[channel])));
    log.fine("Volume on channel " + channel + " set to: " + volume);
  }

  /**
   * Gets the volume (gain) control.
   *
   * @param  channel the channel number
   * @return         the volume (gain) setting for the channel
   */
  public float getVolume(final int channel) {
    final float volume = gainMinima[channel] + (gainControls[channel].getValue() *
      (gainMaxima[channel] - gainMinima[channel]));
    log.finer("Volume on channel " + channel + " is: " + volume);
    return volume;
  }

}
