package irvine.wordsmith;

import irvine.util.StringUtils;

/**
 * Check for letters in common to all words.
 * @author Sean A. Irvine
 */
public class CommonLettersInspector implements Inspector {

  @Override
  public String inspect(final String... words) {
    if (words.length < 3) {
      return null;
    }
    int syn = ~0;
    for (final String w : words) {
      syn &= StringUtils.syn(w);
    }
    if (syn == 0) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    for (int k = 0, j = 1; k < 26; ++k, j <<= 1) {
      if ((syn & j) != 0) {
        sb.append((char) ('A' + k));
      }
    }
    return "Every words contains: " + sb;
  }
}
