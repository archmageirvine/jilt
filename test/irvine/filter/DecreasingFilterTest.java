package irvine.filter;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class DecreasingFilterTest extends TestCase {

  public void testFilter() {
    final Filter filter = new DecreasingFilter();
    assertTrue(filter.is(null));
    assertTrue(filter.is(""));
    assertTrue(filter.is("a"));
    assertFalse(filter.is("abz"));
    assertFalse(filter.is("baz"));
    assertTrue(filter.is("zba"));
    assertFalse(filter.is("hhhd"));
  }
}
