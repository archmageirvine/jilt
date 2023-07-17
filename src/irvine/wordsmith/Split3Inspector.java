package irvine.wordsmith;

import java.util.Set;

/**
 * Check if each word can be split into three parts.
 * @author Sean A. Irvine
 */
public class Split3Inspector extends Split2Inspector {

  @Override
  protected String is(final Set<String> dict, final String w) {
    for (int k = 1; k < w.length(); ++k) {
      for (int j = k + 1; j < w.length(); ++j) {
        if (dict.contains(w.substring(0, k)) && dict.contains(w.substring(k, j)) && dict.contains(w.substring(j))) {
          return w.substring(0, k) + "-" + w.substring(k, j) + "-" + w.substring(j);
        }
      }
    }
    return null;
  }

  @Override
  protected String words() {
    return "three";
  }
}
