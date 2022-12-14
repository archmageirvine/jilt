package irvine.filter;

/**
 * Test for a minimum length.
 * @author Sean A. Irvine
 */
public class MinLengthFilter implements Filter {

  private final int mLen;

  /**
   * Filter for minimum length.
   * @param len minimum length
   */
  public MinLengthFilter(final int len) {
    mLen = len;
  }

  @Override
  public boolean is(final String word) {
    return word.length() >= mLen;
  }
}
