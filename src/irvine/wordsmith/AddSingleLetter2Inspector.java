package irvine.wordsmith;

import irvine.jilt.Dictionary;

/**
 * Check if they remain words when adding a single letter in two places.
 * @author Sean A. Irvine
 */
public class AddSingleLetter2Inspector extends AddSingleLetterInspector {

  protected String is(final char c, final String word) {
    for (int k = 0; k <= word.length(); ++k) {
      for (int j = k; j <= word.length(); ++j) {
        final String w = word.substring(0, k) + c + word.substring(k, j) + c + word.substring(j);
        if (Dictionary.getDefaultDictionary().contains(w)) {
          return w;
        }
      }
    }
    return null;
  }
}
