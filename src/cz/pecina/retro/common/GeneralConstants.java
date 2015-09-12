/* GeneralConstants.java
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

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

/**
 * General constants common to all emulated devices.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public final class GeneralConstants {
    
  /**
   * Array of pixel sizes for which bitmaps are available.
   */
  public static final Integer[] PIXEL_SIZES = {1, 2, 3, 4};

  /**
   * Array of supported language strings.
   */
  public static final String[] SUPPORTED_LOCALES = {"en-US", "cs-CZ", "sk-SK"};

  public static final int TOOL_TIP_INITIAL_DELAY = 1000;
  public static final int TOOL_TIP_DISMISS_DELAY = 5000;
  public static final int TOOL_TIP_RESHOW_DELAY = 0;

  /**
   * Set of safe characters likely to be displayed correctly on all platforms.
   */
  public static final Set<Character> SAFE_CHARACTERS =
    new HashSet<>(Arrays.asList(new Character[] {
      ' ', '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-',
      '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';',
      '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
      'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
      'X', 'Y', 'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c', 'd', 'e',
      'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
      't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '¡', '¢', '£',
      '¤', '¥', '¦', '§', '¨', '©', 'ª', '«', '¬', '®', '¯', '°', '±', '²',
      '³', '´', 'µ', '¶', '·', '¸', '¹', 'º', '»', '¼', '½', '¾', '¿', 'À',
      'À', 'Á', 'Á', 'Â', 'Ã', 'Ä', 'Ä', 'Å', 'Æ', 'Ç', 'È', 'É', 'É', 'Ê',
      'Ë', 'Ë', 'Ì', 'Í', 'Í', 'Î', 'Ï', 'Ï', 'Ð', 'Ñ', 'Ò', 'Ó', 'Ó', 'Ô',
      'Ô', 'Õ', 'Ö', 'Ö', '×', 'Ø', 'Ù', 'Ú', 'Ú', 'Û', 'Ü', 'Ü', 'Ý', 'Ý',
      'Þ', 'ß', 'à', 'à', 'á', 'á', 'â', 'ã', 'ä', 'ä', 'å', 'æ', 'ç', 'è',
      'é', 'é', 'ê', 'ë', 'ë', 'ì', 'í', 'í', 'î', 'ï', 'ï', 'ð', 'ñ', 'ò',
      'ó', 'ó', 'ô', 'ô', 'õ', 'ö', 'ö', '÷', 'ø', 'ù', 'ú', 'ú', 'û', 'ü',
      'ü', 'ý', 'ý', 'þ', 'ÿ', 'ÿ', 'Ą', 'ą', 'Ć', 'ć', 'Č', 'č', 'Ď', 'ď',
      'Ě', 'ě', 'Ĺ', 'ĺ', 'Ľ', 'ľ', 'Ł', 'ł', 'Ň', 'ň', 'Œ', 'œ', 'Ŕ', 'ŕ',
      'Ř', 'ř', 'Ś', 'ś', 'Ş', 'ş', 'Š', 'š', 'Ť', 'ť', 'Ů', 'ů', 'Ÿ', 'Ź',
      'ź', 'Ż', 'ż', 'Ž', 'ž',
      '\u03b1',  // alpha
      '\u03b2',  // beta
      '\u03b3',  // gamma
      '\u03b4',  // delta
      '\u03c0'   // pi
    }));
  
  // default constructor disabled
  private GeneralConstants() {}
}
