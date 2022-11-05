package irvine.util;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class StringUtilsTest extends TestCase {

  public void testRep() {
    assertEquals("", StringUtils.rep('#', 0));
    assertEquals("#", StringUtils.rep('#', 1));
    assertEquals("#####", StringUtils.rep('#', 5));
  }
}
