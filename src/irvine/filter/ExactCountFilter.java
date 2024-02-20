package irvine.filter;

/**
 * Test if given letters occur a specified number of times.
 * @author Sean A. Irvine
 */
public class ExactCountFilter implements Filter {

  private final String mAllowed;
  private final int mCount;

  /**
   * Construct a new filter for words containing specified count of letters
   * @param allowed string to be searched for
   */
  public ExactCountFilter(final String allowed, final int count) {
    mAllowed = allowed;
    mCount = count;
  }

  @Override
  public boolean is(final String word) {
    int cnt = 0;
    for (int k = 0; k < word.length(); ++k) {
      if (mAllowed.indexOf(word.charAt(k)) >= 0 && ++cnt > mCount) {
        return false;
      }
    }
    return cnt == mCount;
  }
}
