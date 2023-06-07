package irvine.filter;

import java.util.HashSet;

/**
 * Test if every letter in the word is different.
 * @author Sean A. Irvine
 */
public class DistinctFilter implements Filter {

  @Override
  public boolean is(final String word) {
    final HashSet<Character> seen = new HashSet<>();
    for (int k = 0; k < word.length(); ++k) {
      if (!seen.add(word.charAt(k))) {
        return false;
      }
    }
    return true;
  }
}
