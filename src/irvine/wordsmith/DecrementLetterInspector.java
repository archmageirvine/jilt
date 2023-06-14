package irvine.wordsmith;

import java.util.Set;

/**
 * Check if we can decrement a letter and still get a word.
 * @author Sean A. Irvine
 */
public class DecrementLetterInspector extends IncrementLetterInspector {

  @Override
  protected String search(final Set<String> dict, final String word) {
    for (int k = 0; k < word.length(); ++k) {
      final char c = word.charAt(k);
      final char d = c == 'A' ? 'Z' : (char) (c - 1);
      final String t = word.substring(0, k) + d + word.substring(k + 1);
      if (dict.contains(t)) {
        return t;
      }
    }
    return null;
  }

  @Override
  protected String desc() {
    return "Decrement";
  }
}
