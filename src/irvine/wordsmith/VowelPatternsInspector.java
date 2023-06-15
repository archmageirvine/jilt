package irvine.wordsmith;

import irvine.util.IntegerUtils;
import irvine.util.LongUtils;

/**
 * Check for length patterns.
 * @author Sean A. Irvine
 */
public class VowelPatternsInspector implements Inspector {

  @Override
  public String inspect(final String... words) {
    if (words.length < 3) {
      return null;
    }
    final int[] lengths = new int[words.length];
    final long[] patterns = new long[words.length];
    final int[] vowels = new int[words.length];
    int maxLength = 0;
    for (int k = 0; k < words.length; ++k) {
      lengths[k] = words[k].length();
      maxLength = Math.max(maxLength, lengths[k]);
      final String w = words[k];
      long p = 0;
      int vowelCount = 0;
      for (int j = 0; j < w.length(); ++j) {
        final char c = w.charAt(j);
        if (c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U') {
          ++vowelCount;
          p += 1L << j;
        }
      }
      patterns[k] = p;
      vowels[k] = vowelCount;
    }
    if (maxLength < Long.SIZE && IntegerUtils.isConstant(lengths) && LongUtils.isConstant(patterns)) {
      return "All words have the same vowel/consonant pattern " + Long.toBinaryString(patterns[0]);
    }
    if (IntegerUtils.isConstant(vowels)) {
      return "Every word contains " + vowels[0] + " vowels";
    }
    final int d = IntegerUtils.arithmeticProgression(vowels);
    if (d > 0) {
      return "Words increase in vowels by " + d + " letters at each step";
    } else if (d < 0) {
      return "Words decrease in vowels by " + -d + " letters at each step";
    }
    return null;
  }
}
