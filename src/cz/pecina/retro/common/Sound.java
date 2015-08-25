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

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.Timer;

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

  // timer period in milliseconds
  private static final int TIMER_PERIOD = 50;

  // buffer length in bytes
  private static final int BUFFER_LENGTH = 0x10000;  // 64KiB
  
  // sample rate
  private float sampleRate;

  // number of samples per one CPU clock
  private double samplesPerCPUClock;

  // average number of samples per tick
  private int samplesPerTick;

  // number of sound channels
  private int numberChannels;

  // sound lines
  private SourceDataLine[] lines;

  // gain controls and limits
  private FloatControl[] gainControls;
  private float[] gainMinima, gainMaxima;
  
  // mute controls
  private BooleanControl[] muteControls;
    
  // number of bytes written to a channel
  private long[] bytesWritten;

  // initial time in nanoseconds
  private long initialNanoTime;

  // last CPU clock
  private long lastCPUClock;
  
  // last levels, per channel
  private boolean[] levels;

  // queues of values, per channel
  private List<TreeMap<Long,Boolean>> queues = new ArrayList<>();
  
  // audio buffer
  private byte[] buffer = new byte[BUFFER_LENGTH];
  
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
    
    if (Parameters.sound != null) {
      log.fine("Error, Sound already exists");
      throw Application.createError(this, "sound.exists");
     }
    Parameters.sound = this;

    this.sampleRate = sampleRate;
    this.numberChannels = numberChannels;
    
    samplesPerTick = Math.round((sampleRate * TIMER_PERIOD) / 1000);
    log.fine("Samples per tick: " + samplesPerTick);
    samplesPerCPUClock = Parameters.CPUFrequency / sampleRate;
    log.fine("Samples per CPU clock: " + samplesPerCPUClock);
    
    AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
    final DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
    if (!AudioSystem.isLineSupported(info)) {
      log.fine("Sound hardware is not available");
      return;
    }
    lines = new SourceDataLine[numberChannels];
    gainControls = new FloatControl[numberChannels];
    gainMinima = new float[numberChannels];
    gainMaxima = new float[numberChannels];
    muteControls = new BooleanControl[numberChannels];
    bytesWritten = new long[numberChannels];
    levels = new boolean[numberChannels];
    try {
      for (int channel = 0; channel < numberChannels; channel++) {
	final SourceDataLine line = (SourceDataLine)AudioSystem.getLine(info);
	line.open(format, 80 * samplesPerTick);
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
      lines = null;
      return;
    }
    log.fine("Audio lines set up");
    
    for (int channel = 0; channel < numberChannels; channel++) {
      bytesWritten[channel] +=
	lines[channel].write(buffer, 0, 4 * samplesPerTick);
    }
    lastCPUClock = getTime();
    initialNanoTime = System.nanoTime();
    for (int channel = 0; channel < numberChannels; channel++) {
      lines[channel].start();
    }
    log.fine("Audio line fed with data (silence) and started");
    
    new Timer(TIMER_PERIOD, new TimerListener()).start();
    log.fine("Timer running, period: " + TIMER_PERIOD);
    
    log.fine("New Sound creation completed");
  }

  // timer listener
  private class TimerListener implements ActionListener {

    // for description see ActionListener
    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Timer event started");
           
      final long totalSamplesNeeded =
	Math.round((sampleRate / 1e9) * (System.nanoTime() - initialNanoTime));
      for (int channel = 0; channel < numberChannels; channel++) {
	log.finer("Processing channel: " + channel);
	int newSamplesNeeded =
	  (int)(samplesPerTick + totalSamplesNeeded - (bytesWritten[channel] / 2));
	final int available = lines[channel].available();
	if (newSamplesNeeded > (available / 2)) {
	  newSamplesNeeded = available / 2;
	}
	log.finer("New samples needed: " + newSamplesNeeded);
	final TreeMap<Long,Boolean> queue = queues.get(channel);
	boolean level = levels[channel];
	log.finer("Last level: " + level);
	final long time = getTime();
	int i = 0;
	for (long pos: queue.keySet()) {
	  int addr = (int)Math.round((pos - lastCPUClock) * samplesPerCPUClock);
	  if (addr >= (BUFFER_LENGTH / 2)) {
	    addr = (BUFFER_LENGTH / 2) - 1;
	  }
	  while (i < (addr - 1)) {
	    buffer[i * 2] = (byte)(level ? 0xff : 0x00);
	    buffer[(i++ * 2) + 1] = (byte)(level ? 0x7f : 0x80);
	  }
	  level = queue.get(pos);
	}
	while (i < newSamplesNeeded) {
	    buffer[i * 2] = (byte)(level ? 0xff : 0x00);
	    buffer[(i++ * 2) + 1] = (byte)(level ? 0x7f : 0x80);
	}
	queue.clear();
	lastCPUClock = time;
	levels[channel] = level;
	bytesWritten[channel] +=
	  lines[channel].write(buffer, 0, 2 * newSamplesNeeded);
      }
    }
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
