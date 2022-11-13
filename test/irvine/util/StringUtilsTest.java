package irvine.util;

import java.util.Arrays;

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

  public void testSyndrome() {
    assertEquals("[0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 1, 0, 0, 2, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0]", Arrays.toString(StringUtils.syndrome("Mississippi")));
  }
}
