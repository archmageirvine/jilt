package irvine.wordsmith;

import java.util.ArrayList;
import java.util.Set;

import irvine.jilt.Dictionary;

/**
 * Check if we can introduce a given letter into each word.
 * @author Sean A. Irvine
 */
public class ReplaceLetterInspector implements Inspector {

  protected String search(final Set<String> dict, final String word, final char replacement) {
    for (int k = 0; k < word.length(); ++k) {
      if (word.charAt(k) != replacement) {
        final String t = word.substring(0, k) + replacement + word.substring(k + 1);
        if (dict.contains(t)) {
          return t;
        }
      }
    }
    return null;
  }

  @Override
  public String inspect(final String... words) {
    final Set<String> dict = Dictionary.getDefaultDictionary();
    for (char replacement = 'A'; replacement <= 'Z'; ++replacement) {
      final ArrayList<String> res = new ArrayList<>();
      for (final String w : words) {
        final String r = search(dict, w, replacement);
        if (r == null) {
          break;
        }
        res.add(r);
      }
      if (res.size() == words.length) {
        return "Replace a letter with " + replacement + " and still have a word\n " + res;
      }
    }
    return null;
  }
}
