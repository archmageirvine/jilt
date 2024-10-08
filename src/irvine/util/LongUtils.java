package irvine.util;

/**
 * Long utilities.
 * @author Sean A. Irvine
 */
public final class LongUtils {

  private LongUtils() { }

  /**
   * Compute <code>a^e</code>. Does not check for overflow.
   * @param a base
   * @param e exponent
   * @return <code>a^e</code>
   */
  public static long pow(long a, long e) {
    if (a == 1 || e == 0) {
      return 1;
    } else if (a == 0) {
      return 0;
    } else if (e == 1) {
      return a;
    } else if (e == 2) {
      return a * a;
    }
    long r = 1;
    while (e != 0) {
      if ((e & 1) != 0) {
        r *= a;
      }
      a *= a;
      e >>>= 1;
    }
    return r;
  }

  /**
   * Test if the specified array is a constant value.
   * @param a array to test
   * @return true if the array is a constant value
   */
  public static boolean isConstant(final long... a) {
    final long v0 = a[0];
    for (final long v : a) {
      if (v != v0) {
        return false;
      }
    }
    return true;
  }

}
