package irvine.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Integer utilities.
 * @author Sean A. Irvine
 */
public final class IntegerUtils {

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

  /**
   * Approximate binary logarithm of an integer. If the given integer is 0 then
   * 0 is the result, otherwise the result is the binary logarithm of the
   * absolute value of the given number.
   *
   * @param n integer to get logarithm of
   * @return binary logarithm
   */
  public static int lg(final int n) {
    int a = n < 0 ? -n : n;
    int i = 0;
    while (a >= 256) {
      i += 8;
      a >>= 8;
    }
    if (a >= 16) {
      i += 4;
      a >>= 4;
    }
    if (a >= 4) {
      i += 2;
      a >>= 2;
    }
    if (a >= 2) {
      i += 2;
    } else if (a >= 1) {
      ++i;
    }
    return i;
  }

  /**
   * Return the smallest power of 2 which is larger than <code>x</code>.
   * If <code>x</code> &gt;= 2<sup>30</sup>, then the result is
   * <code>Integer.MIN_VALUE</code> (which is the correct result if
   * the result is considered to be unsigned). If <code>x &lt; 0</code>,
   * then the result is 0.
   *
   * @param x value to round up
   * @return a power of 2
   */
  public static int nextPowerOf2(int x) {
    x |= x >> 1;
    x |= x >> 2;
    x |= x >> 4;
    x |= x >> 8;
    x |= x >> 16;
    return x + 1;
  }

  /**
   * Read numbers from a stream into an array.  Empty lines or lines starting
   * with <code>#</code> are ignored. Behaviour on out of range numbers is
   * undefined.
   *
   * @param reader source
   * @return array of numbers
   * @throws IOException if an I/O error occurs.
   */
  public static int[] suckInNumbers(final BufferedReader reader) throws IOException {
    final ArrayList<Integer> res = new ArrayList<>();
    String line;
    while ((line = reader.readLine()) != null) {
      if (!line.isEmpty() && line.charAt(0) != '#') {
        res.add(Integer.valueOf(line));
      }
    }
    final int[] r = new int[res.size()];
    for (int k = 0; k < r.length; ++k) {
      r[k] = res.get(k);
    }
    return r;
  }

  /**
   * Read numbers from a stream into an array.  Empty lines or lines starting
   * with <code>#</code> are ignored. Behaviour on out of range numbers is
   * undefined.
   *
   * @param resource reader source
   * @return array of numbers
   */
  public static int[] suckInNumbers(final String resource) {
    try (final BufferedReader r = IOUtils.reader(resource)) {
      return suckInNumbers(r);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

}
