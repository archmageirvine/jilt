package irvine.filter;

import java.util.HashMap;

/**
 * Test if a specified string matches a particular letter pattern.
 * @author Sean A. Irvine
 */
public class PatternFilter implements Filter {

  private final String mPattern;

  /**
   * Construct a new filter for words matching a letter pattern.
   * @param pattern pattern to be searched for
   */
  public PatternFilter(final String pattern) {
    mPattern = pattern;
  }

  @Override
  public boolean is(final String word) {
    if (word.length() != mPattern.length()) {
      return false;
    }
    final HashMap<Character, Character> map = new HashMap<>();
    for (int k = 0; k < word.length(); ++k) {
      final char c = word.charAt(k);
      final char p = mPattern.charAt(k);
      final Character m = map.get(p);
      if (m == null) {
        if (map.values().contains(c)) {
          return false;
        }
        map.put(p, c);
      } else if (m != c) {
        return false;
      }
    }
    return true;
  }
}
