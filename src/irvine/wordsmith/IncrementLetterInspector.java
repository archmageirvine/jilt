package irvine.wordsmith;

import java.util.ArrayList;
import java.util.Set;

import irvine.jilt.Dictionary;

/**
 * Check if we can increment a letter and still get a word.
 * @author Sean A. Irvine
 */
public class IncrementLetterInspector implements Inspector {

  protected String search(final Set<String> dict, final String word) {
    for (int k = 0; k < word.length(); ++k) {
      final char c = word.charAt(k);
      final char d = c == 'Z' ? 'A' : (char) (c + 1);
      final String t = word.substring(0, k) + d + word.substring(k + 1);
      if (dict.contains(t)) {
        return t;
      }
    }
    return null;
  }

  protected String desc() {
    return "Increment";
  }

  @Override
  public String inspect(final String... words) {
    final Set<String> dict = Dictionary.getDefaultDictionary();
    final ArrayList<String> res = new ArrayList<>();
    for (final String w : words) {
      final String r = search(dict, w);
      if (r == null) {
        return null;
      }
      res.add(r);
    }
    return desc() + " a letter in the word to get another word\n" + res;
  }
}
