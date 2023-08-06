package irvine.wordsmith;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import irvine.jilt.Dictionary;

/**
 * Check if each word can be rotated into another word.
 * @author Sean A. Irvine
 */
public class RotationInspector implements Inspector {

  protected String is(final Set<String> dict, final String w) {
    for (int k = 1; k < w.length(); ++k) {
      final String t = w.substring(k) + w.substring(0, k);
      if (!t.equals(w) && dict.contains(t)) {
        return t;
      }
    }
    return null;
  }

  @Override
  public String inspect(final String... words) {
    final Set<String> dict = Dictionary.getDefaultDictionary();
    final List<String> res = new ArrayList<>();
    for (final String w : words) {
      final String rotation = is(dict, w);
      if (rotation == null) {
        return null;
      }
      res.add(rotation);
    }
    return "Every word can be rotated and remain a word:\n" + res;
  }
}
