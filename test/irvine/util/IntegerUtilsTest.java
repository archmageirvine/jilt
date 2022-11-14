package irvine.util;

import java.util.Arrays;
import java.util.Random;

import junit.framework.TestCase;

/**
 * Tests the corresponding class.
 * @author Sean A. Irvine
 */
public class IntegerUtilsTest extends TestCase {

  public void testIdentity() {
    final int[] a = new int[3];
    IntegerUtils.identity(a);
    assertEquals("[0, 1, 2]", Arrays.toString(a));
  }

  public void testLg() {
    assertEquals(0, IntegerUtils.lg(0));
    for (int i = 1, j = 1; j >= 0; ++i, j <<= 1) {
      assertEquals(i, IntegerUtils.lg(j));
      assertEquals(i, IntegerUtils.lg(-j));
    }
    for (int i = 2, j = 2; j >= 0; ++i, j <<= 1) {
      assertEquals(i, IntegerUtils.lg(j + 1));
      assertEquals(i, IntegerUtils.lg(-j - 1));
    }
    assertEquals(31, IntegerUtils.lg(0x7FFFFFFF));
  }

  public void testNextPowerOf2() {
    for (int k = 0, x = 0; k < 32; ++k) {
      x = IntegerUtils.nextPowerOf2(x);
      assertEquals(String.valueOf(k), 1 << k, x);
    }
    assertEquals(0, IntegerUtils.nextPowerOf2(Integer.MIN_VALUE));
    final Random r = new Random();
    for (int j = 0; j < 1000; ++j) {
      final int k = r.nextInt();
      final int z = IntegerUtils.nextPowerOf2(k);
      if (k < 0) {
        assertEquals(0, z);
      } else {
        assertEquals(0, z & (z - 1));
        if (z != Integer.MIN_VALUE) {
          assertTrue(z > k);
          if (k > 0) {
            assertTrue(z >>> 1 <= k);
          } else {
            assertTrue(z >> 1 <= k);
          }
        }
      }
    }
  }
}
