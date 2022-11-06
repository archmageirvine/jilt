package irvine.filter;

/**
 * Test if alphabetical.
 * @author Sean A. Irvine
 */
public class PalindromeFilter implements Filter {

  @Override
  public boolean is(final String word) {
    if (word != null) {
      final int len = word.length();
      for (int i = 0, j = len - 1; i < len >>> 1; ++i, --j) {
        if (word.charAt(i) != word.charAt(j)) {
          return false;
        }
      }
    }
    return true;
  }
}
