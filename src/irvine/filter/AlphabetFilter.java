package irvine.filter;

/**
 * Test if the word is consists entirely of the specified characters.
 * @author Sean A. Irvine
 */
public class AlphabetFilter implements Filter {

  private final String mAlphabet;

  /**
   * Construct a new filter for words using a particular alphabet.
   * @param alphabet allowed characters
   */
  public AlphabetFilter(final String alphabet) {
    mAlphabet = alphabet;
  }

  @Override
  public boolean is(final String word) {
    for (int k = 0; k < word.length(); ++k) {
      if (mAlphabet.indexOf(word.charAt(k)) < 0) {
        return false;
      }
    }
    return true;
  }
}
