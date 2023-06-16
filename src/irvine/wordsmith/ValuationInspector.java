package irvine.wordsmith;

import irvine.util.IntegerUtils;

/**
 * Check for constants and progressions in various ways of looking at letters.
 * @author Sean A. Irvine
 */
public class ValuationInspector implements Inspector {

  private final int mMinWords;
  private final String mName;
  private final int[] mValues;

  ValuationInspector(final int minWords, final String name, final int... values) {
    mMinWords = minWords;
    mName = name;
    mValues = values;
  }

  @Override
  public String inspect(final String... words) {
    if (words.length < mMinWords) {
      return null;
    }
    final int[] cnts = new int[words.length];
    for (int k = 0; k < words.length; ++k) {
      final String w = words[k];
      for (int j = 0; j < w.length(); ++j) {
        final char c = w.charAt(j);
        if (c >= 'A' && c <= 'Z') {
          cnts[k] += mValues[c - 'A'];
        }
      }
    }
    if (IntegerUtils.isConstant(cnts)) {
      return "All words have the same count in " + mName + ": " + cnts[0];
    }
    final int d = IntegerUtils.arithmeticProgression(cnts);
    if (d > 0) {
      return "Words increase in " + mName + " by " + d + " at each word";
    } else if (d < 0) {
      return "Words decrease in " + mName + " by " + -d + " at each word";
    }
    return null;
  }
}
