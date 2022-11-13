package irvine.util;

import java.util.Arrays;

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
}
