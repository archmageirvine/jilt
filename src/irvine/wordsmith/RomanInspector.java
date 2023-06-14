package irvine.wordsmith;

import java.util.Arrays;

import irvine.util.IntegerUtils;

/**
 * Check for Roman numerals.
 * @author Sean A. Irvine
 */
public class RomanInspector implements Inspector {

  private static final String RANK = "IVXLCDM";
  private static final int[] VALUE = {1, 5, 10, 50, 100, 500, 1000};

  private static int parse(final CharSequence roman) {
    int v = 0;
    final int rlen = roman.length();
    for (int k = 0; k < rlen; ++k) {
      final int r = RANK.indexOf(roman.charAt(k));
      if (r == -1) {
        return 0; // not Roman
      }
      final int t = VALUE[r];
      if (k != rlen - 1 && RANK.indexOf(roman.charAt(k + 1)) > r) {
        // Subtractive principle
        v -= t;
      } else {
        v += t;
      }
    }
    return v;
  }

  @Override
  public String inspect(final String... words) {
    if (words.length < 3) {
      return null;
    }
    final String[] roman = new String[words.length];
    final int[] values = new int[words.length];
    for (int k = 0; k < words.length; ++k) {
      final String w = words[k];
      final StringBuilder sb = new StringBuilder();
      for (int j = 0; j < w.length(); ++j) {
        if (RANK.indexOf(w.charAt(j)) >= 0) {
          sb.append(w.charAt(j));
        }
      }
      if (sb.length() == 0) {
        return null;
      }
      values[k] = parse(sb);
      if (values[k] < 1) {
        return null;
      }
      roman[k] = sb.toString();
    }
    if (IntegerUtils.isConstant(values)) {
      return "All the words contain Roman numeral " + roman[0];
    }
    final int d = IntegerUtils.arithmeticProgression(values);
    if (d > 0) {
      return "Increasing Roman numerals:\n" + Arrays.toString(roman);
    } else if (d < 0) {
      return "Decreasing Roman numerals:\n" + Arrays.toString(roman);
    }
    return null;
  }
}
