package irvine.filter;

import java.util.Locale;

/**
 * Test if the word is a substring of the specified word.
 * @author Sean A. Irvine
 */
public class SubstringFilter implements Filter {

  private final String mWord;

  /**
   * Construct a new filter for words that are a substring of a given word.
   * @param word search term
   */
  public SubstringFilter(final String word) {
    mWord = word.toUpperCase(Locale.getDefault());
  }

  @Override
  public boolean is(final String word) {
    return mWord.contains(word.toUpperCase(Locale.getDefault()));
  }
}
