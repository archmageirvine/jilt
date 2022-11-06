package irvine.filter;

/**
 * Test if the word contains the specified number of repeats of each letter.
 * such as "murmur" and "beriberi".
 * @author Sean A. Irvine
 */
public class DiplogramFilter implements Filter {

  private final int mRepeats;

  /**
   * Filter for diplograms with a given number of repeats.
   * @param repeats number of repeats.
   */
  public DiplogramFilter(final int repeats) {
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
      final int[] cnt = new int[26];
      for (int k = 0; k < word.length(); ++k) {
        final char c = Character.toUpperCase(word.charAt(k));
        if (c < 'A' || c > 'Z') {
          return false;
        }
        cnt[c - 'A']++;
      }
      for (int c : cnt) {
        if (c != 0 && c != mRepeats) {
          return false;
        }
      }
    }
    return true;
  }
}
