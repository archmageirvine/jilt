package irvine.wordsmith;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import irvine.jilt.Dictionary;

/**
 * Check if each word can be split into two parts.
 * @author Sean A. Irvine
 */
public class Split2Inspector implements Inspector {

  protected String is(final Set<String> dict, final String w) {
    for (int k = 1; k < w.length(); ++k) {
      if (dict.contains(w.substring(0, k)) && dict.contains(w.substring(k))) {
        return w.substring(0, k) + "-" + w.substring(k);
      }
    }
    return null;
  }

  protected String words() {
    return "two";
  }

  @Override
  public String inspect(final String... words) {
    final Set<String> dict = Dictionary.getDefaultDictionary();
    final List<String> res = new ArrayList<>();
    for (final String w : words) {
      final String split = is(dict, w);
      if (split == null) {
        return null;
      }
      res.add(split);
    }
    return "Every word can be split into " + words() + " dictionary words:\n" + res;
  }
}
