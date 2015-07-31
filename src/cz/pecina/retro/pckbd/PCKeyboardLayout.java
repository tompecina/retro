/* PCKeyboardLayout.java
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
import java.awt.event.KeyEvent;
import cz.pecina.retro.gui.Shortcut;

/**
 * PC keyboard leyout.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class PCKeyboardLayout {

  // static logger
  private static final Logger log =
    Logger.getLogger(PCKeyboardLayout.class.getName());

  // array of the keys
  private PCKeyboardKey[] keys;

  /**
   * Creates the keyboard layout.
   *
   * @param hardware hardware object to operate on
   */
  public PCKeyboardLayout(final PCKeyboardHardware hardware) {
    assert hardware != null;
    keys = new PCKeyboardKey[] {
      new PCKeyboardKey(hardware,
			"f1",
			2,
			2,
			0x3b,
			KeyEvent.VK_F1),
      new PCKeyboardKey(hardware,
			"f2",
			8,
			2,
			0x3c,
			KeyEvent.VK_F2),
      new PCKeyboardKey(hardware,
			"f3",
			2,
			8,
			0x3d,
			KeyEvent.VK_F3),
      new PCKeyboardKey(hardware,
			"f4",
			8,
			8,
			0x3e,
			KeyEvent.VK_F4),
      new PCKeyboardKey(hardware,
			"f5",
			2,
			14,
			0x3f,
			KeyEvent.VK_F5),
      new PCKeyboardKey(hardware,
			"f6",
			8,
			14,
			0x40,
			KeyEvent.VK_F6),
      new PCKeyboardKey(hardware,
			"f7",
			2,
			20,
			0x41,
			KeyEvent.VK_F7),
      new PCKeyboardKey(hardware,
			"f8",
			8,
			20,
			0x42,
			KeyEvent.VK_F8),
      new PCKeyboardKey(hardware,
			"f9",
			2,
			26,
			0x43,
			KeyEvent.VK_F9),
      new PCKeyboardKey(hardware,
			"f10",
			8,
			26,
			0x44,
			KeyEvent.VK_F10),
      new PCKeyboardKey(hardware,
			"esc",
			16,
			2,
			0x01,
			KeyEvent.VK_ESCAPE),
      new PCKeyboardKey(hardware,
			"1",
			22,
			2,
			0x02,
			KeyEvent.VK_1),
      new PCKeyboardKey(hardware,
			"2",
			28,
			2,
			0x03,
			KeyEvent.VK_2),
      new PCKeyboardKey(hardware,
			"3",
			34,
			2,
			0x04,
			KeyEvent.VK_3),
      new PCKeyboardKey(hardware,
			"4",
			40,
			2,
			0x05,
			KeyEvent.VK_4),
      new PCKeyboardKey(hardware,
			"5",
			46,
			2,
			0x06,
			KeyEvent.VK_5),
      new PCKeyboardKey(hardware,
			"6",
			52,
			2,
			0x07,
			KeyEvent.VK_6),
      new PCKeyboardKey(hardware,
			"7",
			58,
			2,
			0x08,
			KeyEvent.VK_7),
      new PCKeyboardKey(hardware,
			"8",
			64,
			2,
			0x09,
			KeyEvent.VK_8),
      new PCKeyboardKey(hardware,
			"9",
			70,
			2,
			0x0a,
			KeyEvent.VK_9),
      new PCKeyboardKey(hardware,
			"0",
			76,
			2,
			0x0b,
			KeyEvent.VK_0),
      new PCKeyboardKey(hardware,
			"minus",
			82,
			2,
			0x0c,
			new Shortcut[] {
	new Shortcut(KeyEvent.VK_MINUS, KeyEvent.KEY_LOCATION_STANDARD),
	new Shortcut(KeyEvent.VK_SUBTRACT, KeyEvent.KEY_LOCATION_STANDARD)
			}),
      new PCKeyboardKey(hardware,
			"equals",
			88,
			2,
			0x0d,
			KeyEvent.VK_EQUALS),
      new PCKeyboardKey(hardware,
			"backspace",
			94,
			2,
			0x0e,
			KeyEvent.VK_BACK_SPACE),
      new PCKeyboardKey(hardware,
			"numlock",
			106,
			2,
			0x45,
			KeyEvent.VK_NUM_LOCK),
      new PCKeyboardKey(hardware,
			"scrlock",
			118,
			2,
			0x46,
			KeyEvent.VK_SCROLL_LOCK),
      new PCKeyboardKey(hardware,
			"tab",
			16,
			8,
			0x0f,
			KeyEvent.VK_TAB),
      new PCKeyboardKey(hardware,
			"q",
			25,
			8,
			0x10,
			KeyEvent.VK_Q),
      new PCKeyboardKey(hardware,
			"w",
			31,
			8,
			0x11,
			KeyEvent.VK_W),
      new PCKeyboardKey(hardware,
			"e",
			37,
			8,
			0x12,
			KeyEvent.VK_E),
      new PCKeyboardKey(hardware,
			"r",
			43,
			8,
			0x13,
			KeyEvent.VK_R),
      new PCKeyboardKey(hardware,
			"t",
			49,
			8,
			0x14,
			KeyEvent.VK_T),
      new PCKeyboardKey(hardware,
			"y",
			55,
			8,
			0x15,
			KeyEvent.VK_Y),
      new PCKeyboardKey(hardware,
			"u",
			61,
			8,
			0x16,
			KeyEvent.VK_U),
      new PCKeyboardKey(hardware,
			"i",
			67,
			8,
			0x17,
			KeyEvent.VK_I),
      new PCKeyboardKey(hardware,
			"o",
			73,
			8,
			0x18,
			KeyEvent.VK_O),
      new PCKeyboardKey(hardware,
			"p",
			79,
			8,
			0x19,
			KeyEvent.VK_P),
      new PCKeyboardKey(hardware,
			"openbracket",
			85,
			8,
			0x1a,
			KeyEvent.VK_OPEN_BRACKET),
      new PCKeyboardKey(hardware,
			"closebracket",
			91,
			8,
			0x1b,
			KeyEvent.VK_CLOSE_BRACKET),
      new PCKeyboardKey(hardware,
			"enter",
			100,
			8,
			0x1c,
			KeyEvent.VK_ENTER,
			KeyEvent.KEY_LOCATION_STANDARD),
      new PCKeyboardKey(hardware,
			"numpad7",
			106,
			8,
			0x47,
			new Shortcut[] {
        new Shortcut(KeyEvent.VK_HOME), new Shortcut(KeyEvent.VK_NUMPAD7)
			}),
      new PCKeyboardKey(hardware,
			"numpad8",
			112,
			8,
			0x48,
			new Shortcut[] {
       new Shortcut(KeyEvent.VK_UP),
       new Shortcut(KeyEvent.VK_KP_UP),
       new Shortcut(KeyEvent.VK_NUMPAD8)
			}),
      new PCKeyboardKey(hardware,
			"numpad9",
			118,
			8,
			0x49,
			new Shortcut[] {
        new Shortcut(KeyEvent.VK_PAGE_UP),
	new Shortcut(KeyEvent.VK_NUMPAD9)
			}),
      new PCKeyboardKey(hardware,
			"numpadminus",
			124,
			8,
			0x4a,
			new Shortcut[] {
        new Shortcut(KeyEvent.VK_MINUS, KeyEvent.KEY_LOCATION_NUMPAD),
	new Shortcut(KeyEvent.VK_SUBTRACT, KeyEvent.KEY_LOCATION_NUMPAD)
			}),
      new PCKeyboardKey(hardware,
			"ctrl",
			16,
			14,
			0x1d,
			KeyEvent.VK_CONTROL,
			KeyEvent.KEY_LOCATION_LEFT),
      new PCKeyboardKey(hardware,
			"a",
			28,
			14,
			0x1e,
			KeyEvent.VK_A),
      new PCKeyboardKey(hardware,
			"s",
			34,
			14,
			0x1f,
			KeyEvent.VK_S),
      new PCKeyboardKey(hardware,
			"d",
			40,
			14,
			0x20,
			KeyEvent.VK_D),
      new PCKeyboardKey(hardware,
			"f",
			46,
			14,
			0x21,
			KeyEvent.VK_F),
      new PCKeyboardKey(hardware,
			"g",
			52,
			14,
			0x22,
			KeyEvent.VK_G),
      new PCKeyboardKey(hardware,
			"h",
			58,
			14,
			0x23,
			KeyEvent.VK_H),
      new PCKeyboardKey(hardware,
			"j",
			64,
			14,
			0x24,
			KeyEvent.VK_J),
      new PCKeyboardKey(hardware,
			"k",
			70,
			14,
			0x25,
			KeyEvent.VK_K),
      new PCKeyboardKey(hardware,
			"l",
			76,
			14,
			0x26,
			KeyEvent.VK_L),
      new PCKeyboardKey(hardware,
			"semicolon",
			82,
			14,
			0x27,
			KeyEvent.VK_SEMICOLON),
      new PCKeyboardKey(hardware,
			"quote",
			88,
			14,
			0x28,
			KeyEvent.VK_QUOTE),
      new PCKeyboardKey(hardware,
			"backquote",
			94,
			14,
			0x29,
			KeyEvent.VK_BACK_QUOTE),
      new PCKeyboardKey(hardware,
			"numpad4",
			106,
			14,
			0x4b,
			new Shortcut[] {
        new Shortcut(KeyEvent.VK_LEFT),
	new Shortcut(KeyEvent.VK_KP_LEFT),
	new Shortcut(KeyEvent.VK_NUMPAD4)
			}),
      new PCKeyboardKey(hardware,
			"numpad5",
			112,
			14,
			0x4c,
			KeyEvent.VK_NUMPAD5),
      new PCKeyboardKey(hardware,
			"numpad6",
			118,
			14,
			0x4d,
			new Shortcut[] {
        new Shortcut(KeyEvent.VK_RIGHT),
	new Shortcut(KeyEvent.VK_KP_RIGHT),
	new Shortcut(KeyEvent.VK_NUMPAD6)
			}),
      new PCKeyboardKey(hardware,
			"numpadplus",
			124,
			14,
			0x4e,
			new Shortcut[] {
        new Shortcut(KeyEvent.VK_PLUS, KeyEvent.KEY_LOCATION_NUMPAD),
	new Shortcut(KeyEvent.VK_ADD, KeyEvent.KEY_LOCATION_NUMPAD)
			}),
      new PCKeyboardKey(hardware,
			"shift",
			16,
			20,
			0x2a,
			new Shortcut[] {
        new Shortcut(KeyEvent.VK_SHIFT, KeyEvent.KEY_LOCATION_LEFT)
			}),
      new PCKeyboardKey(hardware,
			"backslash",
			25,
			20,
			0x2b,
			KeyEvent.VK_BACK_SLASH),
      new PCKeyboardKey(hardware,
			"z",
			31,
			20,
			0x2c,
			KeyEvent.VK_Z),
      new PCKeyboardKey(hardware,
			"x",
			37,
			20,
			0x2d,
			KeyEvent.VK_X),
      new PCKeyboardKey(hardware,
			"c",
			43,
			20,
			0x2e,
			KeyEvent.VK_C),
      new PCKeyboardKey(hardware,
			"v",
			49,
			20,
			0x2f,
			KeyEvent.VK_V),
      new PCKeyboardKey(hardware,
			"b",
			55,
			20,
			0x30,
			KeyEvent.VK_B),
      new PCKeyboardKey(hardware,
			"n",
			61,
			20,
			0x31,
			KeyEvent.VK_N),
      new PCKeyboardKey(hardware,
			"m",
			67,
			20,
			0x32,
			KeyEvent.VK_M),
      new PCKeyboardKey(hardware,
			"comma",
			73,
			20,
			0x33,
			KeyEvent.VK_COMMA),
      new PCKeyboardKey(hardware,
			"period",
			79,
			20,
			0x34,
			KeyEvent.VK_PERIOD,
			KeyEvent.KEY_LOCATION_STANDARD),
      new PCKeyboardKey(hardware,
			"slash",
			85,
			20,
			0x35,
			KeyEvent.VK_SLASH,
			KeyEvent.KEY_LOCATION_STANDARD),
      new PCKeyboardKey(hardware,
			"shift",
			91,
			20,
			0x36,
			new Shortcut[] {
        new Shortcut(KeyEvent.VK_SHIFT, KeyEvent.KEY_LOCATION_RIGHT)
			}),
      new PCKeyboardKey(hardware,
			"numpadasterisk",
			100,
			20,
			0x37,
			new Shortcut[] {
        new Shortcut(KeyEvent.VK_ASTERISK, KeyEvent.KEY_LOCATION_NUMPAD),
	new Shortcut(KeyEvent.VK_MULTIPLY, KeyEvent.KEY_LOCATION_NUMPAD),
	new Shortcut(KeyEvent.VK_PRINTSCREEN)
			}),
      new PCKeyboardKey(hardware,
			"numpad1",
			106,
			20,
			0x4f,
			new Shortcut[] {
        new Shortcut(KeyEvent.VK_END),
	new Shortcut(KeyEvent.VK_NUMPAD1)
			}),
      new PCKeyboardKey(hardware,
			"numpad2",
			112,
			20,
			0x50,
			new Shortcut[] {
        new Shortcut(KeyEvent.VK_DOWN),
	new Shortcut(KeyEvent.VK_KP_DOWN),
	new Shortcut(KeyEvent.VK_NUMPAD2)
			}),
      new PCKeyboardKey(hardware,
			"numpad3",
			118,
			20,
			0x51,
			new Shortcut[] {
        new Shortcut(KeyEvent.VK_PAGE_DOWN),
	new Shortcut(KeyEvent.VK_NUMPAD3)
			}),
      new PCKeyboardKey(hardware,
			"alt",
			16,
			26,
			0x38,
			KeyEvent.VK_ALT),
      new PCKeyboardKey(hardware,
			"space",
			28,
			26,
			0x39,
			KeyEvent.VK_SPACE),
      new PCKeyboardKey(hardware,
			"capslock",
			88,
			26,
			0x3a,
			KeyEvent.VK_CAPS_LOCK),
      new PCKeyboardKey(hardware,
			"numpad0",
			100,
			26,
			0x52,
			new Shortcut[] {
        new Shortcut(KeyEvent.VK_INSERT),
	new Shortcut(KeyEvent.VK_NUMPAD0)
			}),
      new PCKeyboardKey(hardware,
			"numpadperiod",
			112,
			26,
			0x53,
			new Shortcut[] {
        new Shortcut(KeyEvent.VK_DECIMAL, KeyEvent.KEY_LOCATION_NUMPAD),
	new Shortcut(KeyEvent.VK_SEPARATOR),
	new Shortcut(KeyEvent.VK_DELETE)}
			)};
    log.fine("New PC keyboard leyout set up");
  }
    
  /**
   * Gets the array of keys.
   *
   * @return the array of keys
   */
  public PCKeyboardKey[] getKeys() {
    return keys;
  }
}