package irvine.filter;

/**
 * Test if a specified string matches a regular expression.
 * @author Sean A. Irvine
 */
public class RegexFilter implements Filter {

  private final String mRegex;

  /**
   * Construct a new filter for words matching a regular expression.
   * @param regex regular expression to be searched for
   */
  public RegexFilter(final String regex) {
    mRegex = regex;
  }

  @Override
  public boolean is(final String word) {
    return word.matches(mRegex);
  }
}
