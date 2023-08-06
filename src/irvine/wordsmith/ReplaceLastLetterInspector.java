package irvine.wordsmith;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import irvine.jilt.Dictionary;

/**
 * Check if the initial letter can be replaced.
 * @author Sean A. Irvine
 */
public class ReplaceLastLetterInspector implements Inspector {

  protected String is(final Set<String> dict, final String w) {
    if (w.length() == 0) {
      return null;
    }
    final char c = w.charAt(w.length() - 1);
    final String w1 = w.substring(0, w.length() - 1);
    for (char r = 'A'; r <= 'Z'; ++r) {
      if (r != c) {
        final String t = w1 + r;
        if (dict.contains(t)) {
          return t;
        }
      }
    }
    return null;
  }

  @Override
  public String inspect(final String... words) {
    if (words.length < 4) {
      return null;
    }
    final Set<String> dict = Dictionary.getDefaultDictionary();
    final List<String> res = new ArrayList<>();
    for (final String w : words) {
      final String replacement = is(dict, w);
      if (replacement == null) {
        return null;
      }
      res.add(replacement);
    }
    return "Every word remains a word when the last letter is replaced:\n" + res;
  }
}
