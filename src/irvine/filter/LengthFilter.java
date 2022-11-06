package irvine.filter;

/**
 * Test for an exact length.
 * @author Sean A. Irvine
 */
public class LengthFilter implements Filter {

  private final int mLen;

  public LengthFilter(final int len) {
    mLen = len;
  }

  @Override
  public boolean is(final String word) {
    return word.length() == mLen;
  }
}
