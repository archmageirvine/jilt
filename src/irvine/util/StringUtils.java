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

  static final char[] APPEND_CHAR = new char[65536];
  static {
    for (char c = '0'; c <= '9'; ++c) {
      APPEND_CHAR[c] = c;
    }
    for (char c = 'a'; c <= 'z'; ++c) {
      APPEND_CHAR[c] = c;
    }
    for (char c = 'A'; c <= 'Z'; ++c) {
      APPEND_CHAR[c] = (char) (c - 'A' + 'a');
    }
    APPEND_CHAR['.'] = '\uFFFF';
    APPEND_CHAR[','] = '\uFFFF';
    APPEND_CHAR[':'] = '\uFFFF';
    APPEND_CHAR[';'] = '\uFFFF';
    APPEND_CHAR['?'] = '\uFFFF';
    APPEND_CHAR['!'] = '\uFFFF';
    APPEND_CHAR['$'] = '\uFFFF';
    APPEND_CHAR['%'] = '\uFFFF';
    APPEND_CHAR['"'] = '\uFFFF';
    APPEND_CHAR['`'] = '\uFFFF';
    APPEND_CHAR['\''] = '\uFFFF';
    APPEND_CHAR['\u0092'] = '\uFFFF'; // an apostrophe in some codes
    APPEND_CHAR[' '] = (char) 1;
    APPEND_CHAR['\n'] = (char) 1;
    APPEND_CHAR['\t'] = (char) 1;
    APPEND_CHAR['\r'] = (char) 1;
    APPEND_CHAR['\f'] = (char) 1;
    APPEND_CHAR['\u000B'] = (char) 1;
    APPEND_CHAR['/'] = (char) 1;
    APPEND_CHAR['~'] = (char) 1;
    APPEND_CHAR['='] = (char) 1;
    APPEND_CHAR['('] = (char) 1;
    APPEND_CHAR[')'] = (char) 1;
    APPEND_CHAR['{'] = (char) 1;
    APPEND_CHAR['}'] = (char) 1;
    APPEND_CHAR['['] = (char) 1;
    APPEND_CHAR[']'] = (char) 1;
  }

  /**
   * Clean up a word by making it lower case and discarding characters which
   * are not letters or digits.  If the word is sufficiently dirty then the
   * empty string is returned.
   * @param s word to clean
   * @return cleaned word
   */
  public static String clean(final String s) {
    // For efficiency test if word already clean and just return string
    boolean clean = true;
    for (int k = 0; k < s.length(); ++k) {
      final char d = s.charAt(k);
      if (d >= 'A' && d <= 'Z') {
        clean = false;
      } else {
        final char c = APPEND_CHAR[s.charAt(k)];
        if (c == 0) {
          return "";
        } else if (c == '\uFFFF') {
          clean = false;
        }
      }
    }
    if (clean) {
      return s;
    }
    // There is at least one character that needs to change
    final StringBuilder sb = new StringBuilder();
    for (int k = 0; k < s.length(); ++k) {
      final char c = APPEND_CHAR[s.charAt(k)];
      if (c != '\uFFFF') {
        sb.append(c);
      }
    }
    return sb.toString();
  }

  /**
   * Compute the syndome of the capital letters in the word.
   * @param w word
   * @return syndrome
   */
  public static int syn(final String w) {
    int syn = 0;
    for (int k = 0; k < w.length(); ++k) {
      final char c = w.charAt(k);
      if (c >= 'A' && c <= 'Z') {
        syn |= 1 << (c - 'A');
      }
    }
    return syn;
  }
}
