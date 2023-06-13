package irvine.wordsmith;

import irvine.util.StringUtils;

/**
 * Check for consecutive letters.
 * @author Sean A. Irvine
 */
public class ConsecutiveLettersInspector implements Inspector {

  private int syndrome(final String w) {
    int syn = 0;
    for (int k = 0; k < w.length(); ++k) {
      final char c = w.charAt(k);
      if (c >= 'A' && c <= 'Z') {
        syn |= 1 << (c - 'A');
      }
    }
    return syn;
  }

  private int maxConsecutiveLetters(final String w) {
    final String syn = Integer.toBinaryString(syndrome(w));
    final String ones = StringUtils.rep('1', syn.length());
    for (int k = 0; k < ones.length(); ++k) {
      if (syn.contains(ones.substring(k))) {
        return ones.length() - k;
      }
    }
    return 0;
  }

  @Override
  public String inspect(final String... words) {
    if (words.length < 4) {
      return null;
    }
    final int consec = maxConsecutiveLetters(words[0]);
    for (int k = 1; k < words.length; ++k) {
      if (maxConsecutiveLetters(words[k]) != consec) {
        return null;
      }
    }
    return "Every word contains exactly " + consec + " consecutive letters";
  }
}
