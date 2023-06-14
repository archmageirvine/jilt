package irvine.wordsmith;

import java.util.Arrays;

import irvine.util.IntegerUtils;
import irvine.util.StringUtils;

/**
 * Check for length patterns.
 * @author Sean A. Irvine
 */
public class UniqueInspector implements Inspector {

  @Override
  public String inspect(final String... words) {
    if (words.length < 3) {
      return null;
    }
    final int[] lengths = new int[words.length];
    final int[] syn = new int[words.length];
    final int[] unique = new int[words.length];
    for (int k = 0; k < words.length; ++k) {
      lengths[k] = words[k].length();
      syn[k] = StringUtils.syn(words[k]);
      unique[k] = Integer.bitCount(syn[k]);
    }
    if (IntegerUtils.isConstant(syn) && IntegerUtils.isConstant(lengths)) {
      return "All the words are anagrams of each other";
    }
    if (Arrays.equals(lengths, unique)) {
      return "No word contains a repeated letter";
    }
    if (IntegerUtils.isConstant(unique)) {
      return "All words have the same number of distinct letters: " + unique[0];
    }
    final int d = IntegerUtils.arithmeticProgression(unique);
    if (d > 0) {
      return "Words increase in unique letters by " + d + " letters at each step";
    } else if (d < 0) {
      return "Words decrease in unique letters by " + -d + " letters at each step";
    }
    return null;
  }
}
