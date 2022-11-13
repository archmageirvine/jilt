package irvine.util;

import junit.framework.TestCase;

/**
 * Tests the corresponding class.
 * @author Sean A. Irvine
 */
public class LongUtilsTest extends TestCase {

  public void testPow() {
    assertEquals(1024, LongUtils.pow(2, 10));
    assertEquals(14348907, LongUtils.pow(3, 15));
    assertEquals(-14348907, LongUtils.pow(-3, 15));
    assertEquals(9, LongUtils.pow(-3, 2));
    assertEquals(-3, LongUtils.pow(-3, 1));
    assertEquals(1, LongUtils.pow(-3, 0));
  }
}
