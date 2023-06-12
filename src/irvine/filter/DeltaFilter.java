package irvine.filter;

import java.util.Locale;

/**
 * Test if the word is one character different from the specified word.
 * @author Sean A. Irvine
 */
public class DeltaFilter implements Filter {

  private final String mWord;

  /**
   * Construct a new filter for words with a single letter difference from a given word.
   * @param word search term
   */
  public DeltaFilter(final String word) {
    mWord = word.toUpperCase(Locale.getDefault());
  }

  @Override
  public boolean is(final String word) {
    if (word.length() != mWord.length()) {
      return false;
    }
    int diffs = 0;
    for (int k = 0; k < word.length(); ++k) {
      if (Character.toUpperCase(word.charAt(k)) != mWord.charAt(k) && ++diffs > 1) {
        return false;
      }
    }
    return diffs == 1;
  }
}
