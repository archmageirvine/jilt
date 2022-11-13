package irvine.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

  /**
   * Read strings from given source.
   * @param reader source for reading
   * @param casing what casing should be applied
   * @return the strings
   * @throws IOException if an I/O error occurs.
   */
  public static List<String> suckInWords(final BufferedReader reader, final Casing casing) throws IOException {
    final List<String> res = new ArrayList<>();
    String line;
    while ((line = reader.readLine()) != null) {
      res.add(casing.apply(line));
    }
    return res;
  }

  /**
   * Read strings from given input stream.
   * @param file file containing strings
   * @param casing what casing should be applied
   * @return the strings
   * @throws IOException if an I/O error occurs.
   */
  public static List<String> suckInWords(final String file, final Casing casing) throws IOException {
    return suckInWords(IOUtils.getReader(file), casing);
  }

  /**
   * Compute the syndrome of a word where the syndrome is the count of each letter A through Z.
   * @param word word
   * @return syndrome
   */
  public static int[] syndrome(final String word) {
    final int[] counts = new int[26];
    for (int k = 0; k < word.length(); ++k) {
      final char c = word.charAt(k);
      if (c >= 'A' && c <= 'Z') {
        ++counts[c - 'A'];
      } else if (c >= 'a' && c <= 'z') {
        ++counts[c - 'a'];
      }
    }
    return counts;
  }
}
