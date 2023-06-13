package irvine.wordsmith;

import java.util.Arrays;

/**
 * Check if there are various ways of slicing that give an explanation.
 * @author Sean A. Irvine
 */
public class SliceInspector implements Inspector {

  private Inspector mA = null;

  @Override
  public String inspect(final String... words) {
    int minLength = Integer.MAX_VALUE;
    for (final String w : words) {
      minLength = Math.min(minLength, w.length());
    }
    final StringBuilder sb = new StringBuilder();
    // Try k letters from beginning and end
    for (int k = 1; 2 * k < minLength; ++k) {
      if (mA == null) {
        mA = new DirListInspector(false);
      }
      final String[] sublist = new String[words.length];
      for (int j = 0; j < words.length; ++j) {
        sublist[j] = words[j].substring(0, k) + words[j].substring(words[j].length() - k);
      }
      final String si = mA.inspect(sublist);
      if (si != null) {
        sb.append("Taking ").append(k).append(" letters from beginning and end gives \n").append(Arrays.toString(sublist)).append("\n --> ").append(si);
      }
    }
    // Try middle letters
    for (int k = 1; 2 * k < minLength; ++k) {
      if (mA == null) {
        mA = new DirListInspector(false);
      }
      final String[] sublist = new String[words.length];
      for (int j = 0; j < words.length; ++j) {
        sublist[j] = words[j].substring(k, words[j].length() - k);
      }
      final String si = mA.inspect(sublist);
      if (si != null) {
        sb.append("Taking ").append(k).append(" letters from the middle gives \n").append(Arrays.toString(sublist)).append("\n --> ").append(si);
      }
    }
    // Try k letters slide across
    for (int k = 2; k < minLength; ++k) {
      if (mA == null) {
        mA = new DirListInspector(false);
      }
      for (int offset = 0; offset + k <= minLength; ++offset) {
        final String[] sublist = new String[words.length];
        for (int j = 0; j < words.length; ++j) {
          sublist[j] = words[j].substring(offset, offset + k);
        }
        final String si = mA.inspect(sublist);
        if (si != null) {
          sb.append("Taking ").append(k).append(" letters starting from ").append(offset).append(" gives \n").append(Arrays.toString(sublist)).append("\n --> ").append(si);
        }
      }
    }
    return sb.length() == 0 ? null : sb.toString();
  }
}
