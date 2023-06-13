package irvine.wordsmith;

import irvine.util.IntegerUtils;

/**
 * Check for length patterns.
 * @author Sean A. Irvine
 */
public class LengthInspector implements Inspector {

  @Override
  public String inspect(final String... words) {
    if (words.length < 3) {
      return null;
    }
    final int[] lengths = new int[words.length];
    for (int k = 0; k < words.length; ++k) {
      lengths[k] = words[k].length();
    }
    if (IntegerUtils.isConstant(lengths)) {
      return "All words have the same length: " + lengths[0];
    }
    final int d = IntegerUtils.arithmeticProgression(lengths);
    if (d > 0) {
      return "Words increase in length by " + d + " letters at each step";
    } else if (d < 0) {
      return "Words decrease in length by " + -d + " letters at each step";
    }
    return null;
  }
}
