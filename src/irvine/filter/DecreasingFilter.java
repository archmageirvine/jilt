package irvine.filter;

/**
 * Test if in strictly decreasing alphabetical order.
 * @author Sean A. Irvine
 */
public class DecreasingFilter implements Filter {

  @Override
  public boolean is(final String word) {
    if (word != null) {
      for (int k = 1; k < word.length(); ++k) {
        if (word.charAt(k) >= word.charAt(k - 1)) {
          return false;
        }
      }
    }
    return true;
  }
}
