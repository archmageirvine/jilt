package irvine.util;

/**
 * Integer utilities.
 * @author Sean A. Irvine
 */
public class IntegerUtils {

  private IntegerUtils() { }

  /**
   * Make the array an identity map up to entry <code>n</code>.
   * @param a array
   * @param n maximum entry
   * @return the array
   */
  public static int[] identity(final int[] a, final int n) {
    for (int k = 0; k < n; ++k) {
      a[k] = k;
    }
    return a;
  }

  /**
   * Make the array an identity map up to entry <code>n</code>.
   * @param a array
   * @return the array
   */
  public static int[] identity(final int[] a) {
    return identity(a, a.length);
  }
}
