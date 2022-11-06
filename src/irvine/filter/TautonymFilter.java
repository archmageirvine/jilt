package irvine.filter;

/**
 * Test if the word is a tautonym. That is, looks for words with repeated letters
 * such as "murmur" and "beriberi".
 * @author Sean A. Irvine
 */
public class TautonymFilter implements Filter {

  private final int mRepeats;

  /**
   * Filter for tautonyms with a given number of repeats.
   * @param repeats number of repeats.
   */
  public TautonymFilter(final int repeats) {
    if (repeats < 1) {
      throw new IllegalArgumentException("Repeats must be at least 1");
    }
    mRepeats = repeats;
  }

  @Override
  public boolean is(final String word) {
    if (word != null) {
      final int len = word.length();
      if (len % mRepeats != 0) {
        // word length incompatible with repeat size
        return false;
      }
      final int r = len / mRepeats;
      for (int k = 0; k < r; ++k) {
        final char x = word.charAt(k);
        for (int j = k + r; j < len; j += r) {
          if (x != word.charAt(j)) {
            return false;
          }
        }
      }
    }
    return true;
  }
}
