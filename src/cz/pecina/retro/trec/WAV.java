/* WAV.java
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

import java.util.List;
import java.util.ArrayList;

import java.io.File;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import cz.pecina.retro.common.Application;

/**
 * WAV reader/writer.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class WAV extends TapeProcessor {

  // static logger
  private static final Logger log =
    Logger.getLogger(WAV.class.getName());

  // output sample rate
  private static final float OUTPUT_SAMPLE_RATE = 44100;
  
  // length of initial silence, in seconds
  private static final float OUTPUT_SILENCE_LENGTH = 0.5f;
  
  // the tape recorder interface
  private TapeRecorderInterface tapeRecorderInterface;
  
  /**
   * Creates an instance of WAV format reader/writer.
   *
   * @param tape                  the tape to operate on
   * @param tapeRecorderInterface the tape recorder interface object
   */
  public WAV(final Tape tape,
	     final TapeRecorderInterface tapeRecorderInterface) {
    super(tape);
    assert tape != null;
    this.tapeRecorderInterface = tapeRecorderInterface;
    log.fine("New WAV reader/writer created");
  }

  /**
   * Writes the tape to a WAV file.
   *
   * @param file output file
   */
  public void write(final File file) {
    log.fine("Writing tape data to an WAV file, file: " + file);
    assert !tape.isEmpty();

    // calculate array size
    final long tapeLength = tape.lastKey() + tape.get(tape.lastKey()) + 1;
    final int silenceLength =
      Math.round(OUTPUT_SILENCE_LENGTH * OUTPUT_SAMPLE_RATE);
    final int tapeSampleRate = tapeRecorderInterface.tapeSampleRate;
    final int soundLength = Math.round(
      ((tapeLength / tapeSampleRate) + 2) *
      OUTPUT_SAMPLE_RATE);
    final int totalLength = soundLength + silenceLength;
    final long dataChunkLength = totalLength * 4;
    final long riffChunkLength = dataChunkLength + 36;
    if (riffChunkLength > 0xffffffffL) {
      log.fine("Error, tape too long");
      throw Application.createError(this, "tooLong");
    }
  
    // fill in data
    final short[] buffer = new short[(int)totalLength];
    for (int i = 0; i < soundLength; i++) {
      buffer[i] = (i < silenceLength) ? 0 : Short.MIN_VALUE;
    }
    int last = -1;
    for (long start: tape.keySet()) {
      final int end =
	Math.round(((start + tape.get(start)) * OUTPUT_SAMPLE_RATE)
		   / tapeSampleRate);
      for (int i = Math.round((start * OUTPUT_SAMPLE_RATE) / tapeSampleRate);
	   i < end;
	   i++) {
	last = i + silenceLength;
	buffer[last] = Short.MAX_VALUE;
      }
    }
    for (int i = last + 1; i < totalLength; i++) {
      buffer[i] = 0;
    }
   
    // write the byte array to file (the Java implementation is broken)
    try (FileOutputStream outputStream = new FileOutputStream(file)) {
      outputStream.write(new byte[]
	{(byte)0x52, (byte)0x49, (byte)0x46, (byte)0x46});
      outputStream.write((byte)(riffChunkLength & 0xff));
      outputStream.write((byte)((riffChunkLength >> 8) & 0xff));
      outputStream.write((byte)((riffChunkLength >> 16) & 0xff));
      outputStream.write((byte)((riffChunkLength >> 24) & 0xff));
      outputStream.write(new byte[]
	{(byte)0x57, (byte)0x41, (byte)0x56, (byte)0x45,
	 (byte)0x66, (byte)0x6d, (byte)0x74, (byte)0x20,
	 (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00,
	 (byte)0x01, (byte)0x00, (byte)0x02, (byte)0x00,
	 (byte)0x44, (byte)0xac, (byte)0x00, (byte)0x00,
	 (byte)0x10, (byte)0xb1, (byte)0x02, (byte)0x00,
	 (byte)0x04, (byte)0x00, (byte)0x10, (byte)0x00,
	 (byte)0x64, (byte)0x61, (byte)0x74, (byte)0x61});
      outputStream.write((byte)(dataChunkLength & 0xff));
      outputStream.write((byte)((dataChunkLength >> 8) & 0xff));
      outputStream.write((byte)((dataChunkLength >> 16) & 0xff));
      outputStream.write((byte)((dataChunkLength >> 24) & 0xff));
      for (int i = 0; i < totalLength; i++) {
	outputStream.write((byte)(buffer[i] & 0xff));
	outputStream.write((byte)(buffer[i] >> 8));
	outputStream.write((byte)(buffer[i] & 0xff));
	outputStream.write((byte)(buffer[i] >> 8));
      }
    } catch (final IOException exception) {
      log.fine("Error, writing to file failed, exception: " + exception);
      throw Application.createError(this, "WAVWrite");
    }
    
    log.fine("Writing completed");
  }

  /**
   * Reads the tape from a WAV file.
   *
   * @param file input file
   */
  public void read(final File file) {
    log.fine("Reading tape data from an WAV file, file: " + file);

    // read audio data
    AudioFormat format = null;
    int bytesPerFrame = 0;
    final List<byte[]> byteBuffer = new ArrayList<>();
    try (AudioInputStream stream = AudioSystem.getAudioInputStream(file)) {
      format = stream.getFormat();
      bytesPerFrame = format.getFrameSize();
      if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
	bytesPerFrame = 1;
      }
      byte[] b;
      for (;;) {
	b = new byte[bytesPerFrame];
	if (stream.read(b) != bytesPerFrame) {
	  break;
	}
	byteBuffer.add(b);
      }
    } catch (final UnsupportedAudioFileException | IOException exception) {
      log.fine("Error, reading failed, exception: " + exception);
      throw Application.createError(this, "WAVRead");
    }
    log.fine("Audio data loaded, number of samples: " + byteBuffer.size());

    // check encoding
    final AudioFormat.Encoding encoding = format.getEncoding();
    if ((encoding != AudioFormat.Encoding.PCM_SIGNED) &&
	(encoding != AudioFormat.Encoding.PCM_UNSIGNED)) {
      log.fine("Error, unsupported encoding");
      throw Application.createError(this, "WAVEncoding");
    }

    // check sample size
    final int sampleSize = format.getSampleSizeInBits();
    if ((sampleSize != 8) && (sampleSize != 16)) {
      log.fine("Error, unsupported sample size");
      throw Application.createError(this, "WAVSampleSize");
    }

    // get audio parameters
    final int channels = format.getChannels();
    final int frameSize = channels * (sampleSize / 8);
    final float sampleRate = format.getSampleRate();
    final boolean bigEndian = format.isBigEndian();
    log.finer(String.format(
      "Encoding: %s, sample size: %d, channels: %d," +
      " sample rate: %g, big-endian: %s",
      encoding, sampleSize, channels, sampleRate, bigEndian));

    // convert data to a float sum over all channels
    final List<Float> floatBuffer = new ArrayList<>();
    for (byte[] sample: byteBuffer) {
      if (sample.length != frameSize) {
	log.fine("Error in format");
	throw Application.createError(this, "WAV");
      }
      float sum = 0;
      for (int channel = 0; channel < channels; channel++) {
	int value;
	if (sampleSize == 8) {
	  value = sample[channel];
	} else if (bigEndian) {
	  value = ((sample[channel * 2] & 0xff) << 8) |
	    ((sample[(channel * 2) + 1]) & 0xff);
	} else {
	  value = ((sample[(channel * 2) + 1] & 0xff) << 8) |
	    ((sample[channel * 2]) & 0xff);
	}
	if (encoding == AudioFormat.Encoding.PCM_SIGNED) {
	  if (sampleSize == 8) {
	    value = (byte)value;
	  } else {
	    value = (short)value;
	  }
	} else {
	  value -= (sampleSize == 8) ? 0x80 : 0x8000;
	}
	sum += value;
      }
      log.finest("Adding: " + sum);
      floatBuffer.add(sum);
    }
    log.finer("Data converted to floats");

    // calculate peak value
    float peak = 0;
    for (float sample: floatBuffer) {
      final float abs = Math.abs(sample);
      if (abs > peak) {
	peak = abs;
      }
    }
    log.finer("Peak value calculated: " + peak);

    // apply hysteresis with a 1/3 peak threshhold
    final float threshhold = peak / 3;
    final List<Boolean> booleanBuffer = new ArrayList<>();
    boolean level = false;
    for (float sample: floatBuffer) {
      if (sample > threshhold) {
	level = true;
      } else if (sample < -threshhold) {
	level = false;
      }
      booleanBuffer.add(level);
      log.finest("Adding: " + level);
    }
    log.finer("Hysteresis applied");

    // clear tape
    tape.clear();
    
    // write data to tape
    boolean inPulse = false;
    double pulseStart = 0;
    double position = 0;
    final double delta = tapeRecorderInterface.tapeSampleRate / sampleRate;
    for (boolean sample: booleanBuffer) {
      if (sample && !inPulse) {
	pulseStart = position;
	inPulse = true;
      } else if (!sample && inPulse) {
	tape.put(Math.round(pulseStart), Math.round(position - pulseStart));
	log.finest("Pulse at (" + pulseStart + "," +
		   (position - pulseStart) + ")");
	inPulse = false;
      }
      position += delta;
    }
    if (inPulse) {
      tape.put(Math.round(pulseStart), Math.round(position - pulseStart));
      log.finest("Final pulse at (" + pulseStart + "," +
		 (position - pulseStart) + ")");
    }
    
    log.fine("Reading completed");
  }
}
