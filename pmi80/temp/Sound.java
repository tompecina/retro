/* Sound.java
 *
 * Copyright (C) 2014-2015, Tomáš Pecina <tomas@pecina.cz>
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

package cz.pecina.retro.pmi80;

import java.util.TreeMap;
import java.util.ArrayList;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.AudioSystem;

public class Sound {
  private TreeMap<Long, Short> queue = new TreeMap<>();
  private byte[] buffer =
    new byte[((int)Constants.SOUND_SAMPLES_PER_TIMER_PERIOD + 2) * 2];
  private byte[] silence =
    new byte[(int)Constants.SOUND_SAMPLES_PER_TIMER_PERIOD * 4];
  private boolean running;
  private double overlap;
  private short state;
  private long startTime;
  private SourceDataLine line;
  private int volume = 50;

  public void add(long cycle, short state) {
    queue.put(cycle, state);
  }

  public boolean start() {
    AudioFormat format =
      new AudioFormat((float)Constants.SOUND_SAMPLE_RATE, 16, 1, true, true);
    DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
    System.out.println(info);
    if (!AudioSystem.isLineSupported(info)) {
      return false;
    }
    try {
      line = (SourceDataLine)AudioSystem.getLine(info);
      line.open(format, (int)Constants.SOUND_SAMPLES_PER_TIMER_PERIOD * 40);
    } catch (Exception exception) {
      return false;
    }
    line.start();
    line.write(silence, 0, (int)Constants.SOUND_SAMPLES_PER_TIMER_PERIOD * 4);
    running = true;
    // for (int i = 0; i < 1000; i+=2) {
    //     test[i] = (byte)((((i % 50) * 500) >> 8) & 0xff);
    //     test[i + 1] = (byte)(((i % 50) * 500) & 0xff);
    // }
    return true;
  }

  public void stop() {
    queue.clear();
    overlap = 0;
    state = 0;
    line.close();
    running = false;
  }

  public void reset() {
    startTime = Computer.cpu.getCycleCounter();
  }

  public void process() {
    if (running) {
      int value;
      int position = 0;
      double time = overlap;
      while (time < Constants.TIMER_CYCLES) {
	while (!queue.isEmpty() && ((queue.firstKey() - startTime) < time)) {
	  state = queue.pollFirstEntry().getValue();
	}
	value = (short)((state * volume) / 100);
	buffer[position++] = (byte)((value >> 8) & 0xff);
	buffer[position++] = (byte)(value & 0xff);
	// position += 2;
	time += Constants.SOUND_CYCLES_PER_SAMPLE;
      }
      overlap = time - Constants.TIMER_CYCLES;
      // for (int i = 0; i < position; i++)
      // 	System.out.printf("%02x ", test[i]);
      // System.out.println();
      // while (line.available() < 3000)
      // 	line.write(test, 0, position);
      System.out.print(System.nanoTime()/1000 + " " + position +
		       " " + line.available() + " ");
      line.write(buffer, 0, position);
      System.out.println(line.available());
      // line.write(test, 0, position);
      queue.clear();
    }
  }

  public void setVolume(int volume) {
    this.volume = volume;
  }
}
