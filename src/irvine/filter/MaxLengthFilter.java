package irvine.filter;

/**
 * Test for a maximum length.
 * @author Sean A. Irvine
 */
public class MaxLengthFilter implements Filter {

  private final int mLen;

  /**
   * Filter to a maximum length.
   * @param len maximum length
   */
  public MaxLengthFilter(final int len) {
    mLen = len;
  }

  @Override
  public boolean is(final String word) {
    return word.length() <= mLen;
  }
}
