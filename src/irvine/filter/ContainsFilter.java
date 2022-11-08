package irvine.filter;

/**
 * Test if a specified string is present.
 * @author Sean A. Irvine
 */
public class ContainsFilter implements Filter {

  private final String mS;

  /**
   * Construct a new filter for words containing a particular string.
   * @param s string to be searched for
   */
  public ContainsFilter(final String s) {
    mS = s;
  }

  @Override
  public boolean is(final String word) {
    return word.contains(mS);
  }
}
