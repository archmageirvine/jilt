package irvine.filter;

import junit.framework.TestCase;

/**
 * Test the corresponding class.
 * @author Sean A. Irvine
 */
public class IncreasingFilterTest extends TestCase {

  public void testFilter() {
    final Filter filter = new IncreasingFilter();
    assertTrue(filter.is(null));
    assertTrue(filter.is(""));
    assertTrue(filter.is("a"));
    assertTrue(filter.is("abz"));
    assertFalse(filter.is("baz"));
    assertFalse(filter.is("zab"));
    assertFalse(filter.is("ahhh"));
  }
}
