package irvine.util;

import java.util.Arrays;

/**
 * Various string utility functions.
 * @author Sean A. Irvine
 */
public final class StringUtils {

  private StringUtils() { }

  /**
   * A string comprising the same character repeated a number of times.
   * @param c character to repeat
   * @param count number of instances
   * @return string with repeated character
   */
  public static String rep(final char c, final int count) {
    if (count < 0) {
      throw new IllegalArgumentException();
    }
    final char[] s = new char[count];
    Arrays.fill(s, c);
    return new String(s);
  }
}
