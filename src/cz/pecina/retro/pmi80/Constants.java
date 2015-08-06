/* Constants.java
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

package cz.pecina.retro.pmi80;

/**
 * Constants specific to Tesla PMI-80.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public final class Constants {
    
  public static final int DEFAULT_START_ROM = 0;
  public static final int DEFAULT_START_RAM = 1;

  public static final double CPU_FREQUENCY = 1e7/9;
  public static final int TIMER_PERIOD = 20;  // in msec
  public static final long TIMER_CYCLES = (TIMER_PERIOD * 10000) / 9;

  public static final String RES_PREFIX = "cz/pecina/retro/pmi80/";

  public static final int TAPE_SAMPLE_RATE = 1111111;
 
  public static final int NUMBER_TERMINAL_LINES = 24;
  public static final int NUMBER_TERMINAL_COLUMNS = 80;
  public static final int TERMINAL_CELL_BASE_WIDTH = 10;
  public static final int TERMINAL_CELL_BASE_HEIGHT = 20;
  public static final int TERMINAL_BASE_OFFSET_X = 10;
  public static final int TERMINAL_BASE_OFFSET_Y = 10;
  public static final int TERMINAL_BASE_WIDTH =
    (TERMINAL_CELL_BASE_WIDTH * NUMBER_TERMINAL_COLUMNS) +
    (TERMINAL_BASE_OFFSET_X * 2);
  public static final int TERMINAL_BASE_HEIGHT =
    (TERMINAL_CELL_BASE_HEIGHT * NUMBER_TERMINAL_LINES) +
    (TERMINAL_BASE_OFFSET_Y * 2);

  public static final double SOUND_SAMPLE_RATE = 44100;
  public static final double SOUND_CYCLES_PER_SAMPLE =
    CPU_FREQUENCY / SOUND_SAMPLE_RATE;
  public static final double SOUND_SAMPLES_PER_TIMER_PERIOD =
    TIMER_CYCLES / SOUND_CYCLES_PER_SAMPLE;

  public static final int NUMBER_STEPPER_STEPS = 80;
  public static final int NUMBER_STEPPER_INPUT_LEDS = 4;
  public static final int STEPPER_MASK_BASE_WIDTH = 200;
  public static final int STEPPER_MASK_BASE_HEIGHT = 200;
  public static final int STEPPER_DISC_BASE_WIDTH = 50;
  public static final int STEPPER_DISC_BASE_HEIGHT = 50;
  public static final int STEPPER_DISC_BASE_OFFSET_X = 75;
  public static final int STEPPER_DISC_BASE_OFFSET_Y = 75;
  public static final int NUMBER_STEPPER_COUNTER_DIGITS = 3;
  public static final int STEPPER_DIGIT_BASE_GRID_X = -13;
  public static final int STEPPER_DIGIT_BASE_OFFSET_X = 108;
  public static final int STEPPER_DIGIT_BASE_OFFSET_Y = 9;
  public static final int STEPPER_INPUT_LED_BASE_GRID_X = 22;
  public static final int STEPPER_INPUT_LED_BASE_OFFSET_X = 45;
  public static final int STEPPER_INPUT_LED_BASE_OFFSET_Y = 182;
  public static final int STEPPER_INDEX_LED_BASE_OFFSET_X = 156;
  public static final int STEPPER_INDEX_LED_BASE_OFFSET_Y =
    STEPPER_INPUT_LED_BASE_OFFSET_Y;

  public static final int NUMBER_SERVO_STATES = 360;
  public static final int SERVO_LED_LIMIT = 10;
  public static final int SERVO_MASK_BASE_WIDTH = 200;
  public static final int SERVO_MASK_BASE_HEIGHT = 200;
  public static final int SERVO_DISC_BASE_WIDTH = 50;
  public static final int SERVO_DISC_BASE_HEIGHT = 50;
  public static final int SERVO_DISC_BASE_OFFSET_X = 75;
  public static final int SERVO_DISC_BASE_OFFSET_Y = 75;
  public static final int NUMBER_SERVO_COUNTER_DIGITS = 3;
  public static final int SERVO_DIGIT_BASE_GRID_X = -13;
  public static final int SERVO_DIGIT_BASE_OFFSET_X = 108;
  public static final int SERVO_DIGIT_BASE_OFFSET_Y = 9;
  public static final int SERVO_PWM_LED_BASE_OFFSET_X = 43;
  public static final int SERVO_PWM_LED_BASE_OFFSET_Y = 182;
  public static final int SERVO_DIRECTION_LED_BASE_OFFSET_X = 63;
  public static final int SERVO_DIRECTION_LED_BASE_OFFSET_Y =
    SERVO_PWM_LED_BASE_OFFSET_Y;
  public static final int SERVO_BRAKE_LED_BASE_OFFSET_X = 83;
  public static final int SERVO_BRAKE_LED_BASE_OFFSET_Y =
    SERVO_PWM_LED_BASE_OFFSET_Y;
  public static final int SERVO_A_LED_BASE_OFFSET_X = 120;
  public static final int SERVO_A_LED_BASE_OFFSET_Y =
    SERVO_PWM_LED_BASE_OFFSET_Y;
  public static final int SERVO_B_LED_BASE_OFFSET_X = 140;
  public static final int SERVO_B_LED_BASE_OFFSET_Y =
    SERVO_PWM_LED_BASE_OFFSET_Y;
  public static final int SERVO_Z_LED_BASE_OFFSET_X = 159;
  public static final int SERVO_Z_LED_BASE_OFFSET_Y =
    SERVO_PWM_LED_BASE_OFFSET_Y;

  // default constructor disabled
  private Constants() {};
}
