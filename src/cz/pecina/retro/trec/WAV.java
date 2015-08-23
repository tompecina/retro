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
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFileFormat;
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
    
    final long tapeLength = tape.lastKey() + tape.get(tape.lastKey()) + 1;
    final int silenceLength =
      Math.round(OUTPUT_SILENCE_LENGTH * OUTPUT_SAMPLE_RATE);
    final int soundLength = Math.round(
      ((tapeLength / tapeRecorderInterface.tapeSampleRate) + 2) *
      OUTPUT_SAMPLE_RATE);

    final short[] buffer = new short[soundLength + silenceLength];
    for (int l = 0; l < soundLength; l++) {
      buffer[l] = (l < silenceLength) ? 0 : Short.MIN_VALUE;
    }
    for (long start: tape.keySet()) {
      final long end = ((start + tape.get(start)) * soundLength) / tapeLength;
      for (int l = (int)((start * soundLength) / tapeLength); l < end; l++) {
	buffer[l + silenceLength] = Short.MAX_VALUE;
      }
    }

    final AudioFormat format =
      new AudioFormat(OUTPUT_SAMPLE_RATE, 16, 2, true, false);
    try (AudioInputStream stream =
	 new AudioInputStream(new WavStream(buffer), format, 2 * tapeLength)) {
      AudioSystem.write(stream, AudioFileFormat.Type.WAVE, file);
    } catch (final IOException exception) {
      log.fine("Error, writing failed, exception: " + exception);
      throw Application.createError(this, "WAVWrite");
    }
    
    log.fine("Writing completed");
  }

  // WAV stream
  private class WavStream extends InputStream {
    private short[] buffer;
    private int index = -1;
    
    public WavStream(final short[] buffer) {
      this.buffer = buffer;
    }

    // for description see InputStream
    @Override
    public int read() {
      if ((++index >> 2) >= buffer.length) {
	return -1;
      }
      return ((index & 1) == 0) ?
	     (buffer[index >> 2] & 0xff) :
	     (buffer[index >> 2] >> 8);
    }
  }

  /**
   * Reads the tape from a WAV file.
   *
   * @param file input file
   */
  public void read(final File file) {
    log.fine("Reading tape data from an WAV file, file: " + file);

    // read data and format information
    AudioFileFormat fileFormat = null;
    AudioFormat format = null;
    int bytesPerFrame = 0;
    final List<byte[]> buffer = new ArrayList<>();
    try (AudioInputStream stream = AudioSystem.getAudioInputStream(file)) {
      fileFormat = AudioSystem.getAudioFileFormat(file);
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
	buffer.add(b);
      }
    } catch (final UnsupportedAudioFileException | IOException exception) {
      log.fine("Error, reading failed, exception: " + exception);
      throw Application.createError(this, "WAVRead");
    }

    // check encoding
    final AudioFormat.Encoding encoding = format.getEncoding();
    if ((encoding != AudioFormat.Encoding.PCM_SIGNED) &&
	(encoding != AudioFormat.Encoding.PCM_UNSIGNED)) {
      log.fine("Error, unsupported encoding, exception: " + encoding);
      throw Application.createError(this, "WAVRead");
    }

    log.fine("Reading completed");
  }    
}
